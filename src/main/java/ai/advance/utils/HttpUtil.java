package ai.advance.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Level;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Slf4j
public class HttpUtil {

    private static final int DEFAULT_TIMEOUT = 60 * 1000; // 超时时间
    private static final String DEFAULT_USER_AGENT = "Advance AI HTTP Client/1.0";

    private static CloseableHttpClient defaultClient = null;

    //FIXME: 这里忽略了 https 安全检查
    private static TrustManager manager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    private static RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setCookieSpec(CookieSpecs.STANDARD_STRICT)
            .setSocketTimeout(DEFAULT_TIMEOUT)
            .setConnectTimeout(DEFAULT_TIMEOUT)
            .setConnectionRequestTimeout(DEFAULT_TIMEOUT)
            .build();

    static {
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{manager}, null);

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.INSTANCE)
                            .register("https", socketFactory).build();

            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            CookieStore cookieStore = new BasicCookieStore();
            defaultClient = HttpClients.custom().setUserAgent(DEFAULT_USER_AGENT)
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                    .setConnectionManager(connectionManager)
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultRequestConfig(defaultRequestConfig).setRedirectStrategy(new DefaultRedirectStrategy() {
                        @Override
                        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
                                throws ProtocolException {
                            boolean isRedirected = super.isRedirected(request, response, context);
                            int statusCode = response.getStatusLine().getStatusCode();
                            if (statusCode == 301 || statusCode == 302) {
                                isRedirected = true;
                            }
                            return isRedirected;
                        }
                    }).build();
        } catch (KeyManagementException|NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String executeRequest(HttpRequestBase r) {
        try {
            CloseableHttpResponse response = defaultClient.execute(r);
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("http request failed: {} {}, {}", r.getMethod(), r.getURI().toString(), e.getMessage(), e);
        } finally {
            r.releaseConnection();
        }
        return null;
    }

    public static String postForm(String url, Map<String, String> params) {
        HttpPost request = new HttpPost(url);
        request.setConfig(defaultRequestConfig);
        request.setHeader("Content-type", "application/x-www-form-urlencoded");
        if (params != null && params.size() > 0) {
            List<NameValuePair> list = new ArrayList<>(params.size());
            for(Map.Entry<String, String> e : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(e.getKey(), e.getValue());
                list.add(pair);
            }
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, StandardCharsets.UTF_8);
            request.setEntity(entity);
        }
        return executeRequest(request);
    }

    public static String get(String url, Map<String, String> params) {
        HttpGet request = new HttpGet(buildUrl(url, params));
        return executeRequest(request);
    }

    public static String getWithProxy(String url, Map<String, String> params) {
        HttpHost proxy = new HttpHost("192.168.1.5", 24000);
        RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
        HttpGet request = new HttpGet(buildUrl(url, params));
        request.setConfig(config);
        return executeRequest(request);
    }

    public static String postJson(String url, String json) {
        HttpPost request = new HttpPost(url);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        request.setEntity(entity);
        return executeRequest(request);
    }

    static public String buildQueryString(Map<String, String> params) {
        if(params == null || params.isEmpty())
            return "";

        StringBuilder queryString = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                try {
                    queryString
                            .append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(entry.getValue(), "UTF-8"))
                            .append('&');
                } catch (UnsupportedEncodingException e) {
                    log.error("can not encode url parameter {}", entry.getKey(), e);
                }
            }
        }
        queryString.deleteCharAt(queryString.length() - 1);
        return queryString.toString();
    }

    static public String buildUrl(String url, Map<String, String> params) {
        if(url == null)
            return null;

        String qs = buildQueryString(params);
        if(qs.equals(""))
            return url;

        return url + ((url.indexOf('?') == -1) ? "?" : "&") + qs;
    }
}
