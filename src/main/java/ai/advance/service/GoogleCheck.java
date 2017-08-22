package ai.advance.service;

import ai.advance.entity.GeometryLocation;
import ai.advance.entity.Location;
import ai.advance.entity.PlaceCheckInput;
import ai.advance.entity.PlaceDetail;
import ai.advance.utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * Created by ekadenyaz on 8/10/17.
 */
public class GoogleCheck {

    String googleSearchTextUri = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    String googleSearchDetailUri = "https://maps.googleapis.com/maps/api/place/details/json";

    private static final int PROCESSOR_SIZE = 5;

    List<String> apiKey = Arrays.asList(
        "AIzaSyBl8tDNv9bOgj5PteLe177WTsWCB6oLeLk",
        "AIzaSyD_nkcEFMlfk6QehiK-jFKem1We2v2YoO0",
        "AIzaSyAvxVdxhDqyVuMMiiKEcoqBAva5BOguGKw",
        "AIzaSyCXGjg3Hjbb1Jt9dZo8VSp8c3co1i5zZuM",
        "AIzaSyA8G9ykgRsH0j4qydHmljXj2j9GJ-NMfWQ",
        "AIzaSyCXGjg3Hjbb1Jt9dZo8VSp8c3co1i5zZuM",
        "AIzaSyB-SSsUvO3yxTilXyDz-ZFJoLhv0qLeUVc",
        "AIzaSyCsedv_6PC9VNPuiNKZALooxdcxiBgf890",
        "AIzaSyCvyJk8GZJaJkKtOMutktGtNyo27NBGYMA",
        "AIzaSyDIvDv3XrWddmzyMIrj8jWzGvxgfQkFvrM",
        "AIzaSyCsedv_6PC9VNPuiNKZALooxdcxiBgf890",
        "AIzaSyDqcwoYBESLz_dVgcHFIygL_3RLlpw9srg",
        "AIzaSyA3NJeob7czKysXlW7b2ADVPaSIj5eBmf4",
        "AIzaSyCjDbHNPi1x0UvZZc_FiZMra9Q8wQWjsZ4",
        "AIzaSyC49dQLf5m5pTJ0wBuCJQoae9uxqYWjQhw",
        "AIzaSyBJoL69cmn3X1Uf23j47Dzh-yeCnMACXf8",
        "AIzaSyC7VTy1fw8NrWq6Nfs_UHj6OuTnZqmjeP4",
        "AIzaSyDhFaCXSFtdGTny99emd2oFHIXFMsa9PVw",
        "AIzaSyDiuRwKIU044ppK_GHVCZxcDHpw3aVpYh4",
        "AIzaSyA4nYjRo6Pye_jaYqZufE6xx473MzK-HqY",
        "AIzaSyBS3J-nP-2FSPEuPZk4DQ4Y3NreXTxRI7w",
        "AIzaSyDvnJv_5YfTmfkZj6gN1Ya3vc6BqfEzOjo",
        "AIzaSyAA92VBXjbRxiNFwKNRt29jDj95qQcJKtA",
        "AIzaSyC2nm9oOVtuV2ajAO6zBs95lgCrSDXJ0VI",
        "AIzaSyAvxVdxhDqyVuMMiiKEcoqBAva5BOguGKw",
        "AIzaSyCpolzbNq_XcAF78ofElBSU7zR-b0sYmfc",
        "AIzaSyC2nm9oOVtuV2ajAO6zBs95lgCrSDXJ0VI",
        "AIzaSyDjpYbcGOvN7GxL7OnovQQz1dBxhFUrBzE",
        "AIzaSyBRfY8R8uy2FXnKV1u9usSc_REl1WUFjBI",
        "AIzaSyDxgRA2vLaeap_nOCPHDsI1Nm2v1zHKr10",
        "AIzaSyC2nm9oOVtuV2ajAO6zBs95lgCrSDXJ0VI",
        "AIzaSyDIlYSvlPVDvD_PpxJZsSscDfHVP_Ns7Wc",
        "AIzaSyDQ5LSi13ighFRcp_JU7kXiKXnrjbPDiuk",
        "AIzaSyAO95UzCT8ES3ElLoXkeYHAzGHy1rVV5ys",
        "AIzaSyAgl3JVvIhFLDE6XQE34poFa5PlsFZf56c",
        "AIzaSyCA2a9Okh-ptTUUz2asKCv4S7GeEQ3L6V4",
        "AIzaSyBOlFc3DEDPIiK1DQXIEEtdgmWUbHLEizc",
        "AIzaSyCBN95GAgbRr650S2eL_B8KqlaQP7FlXpM",
        "AIzaSyC_GQB6tDz1nyFI0K8ukxQCGVhzdqExNws"
        );


    public List<PlaceDetail> checkCompanyOnGoogle(PlaceCheckInput input) {
        /**
         * google map search text 分页数据 (暂时只拿第一页数据)
         *
         String pageToken = placeSearchJSON.getString("next_page_token");
         while (pageToken != null) {
         apiParams.clear();
         apiParams.put("pagetoken", pageToken);
         apiParams.put("key", apiKey);
         placeSearchJSON = getGoogleResponse(apiParams, placeSearchUrl);
         JSONArray newJsonArray = placeSearchJSON.getJSONArray("results");
         resultList.addAll(newJsonArray);
         pageToken = placeSearchJSON.getString("next_page_token");
         }
         */
        try {
            List<PlaceDetail> placeDetails = googleSearchPlaceId(input);
            System.out.println(placeDetails);
            return placeDetails;
        } catch (Exception e) {
            System.out.print("Error here");
        }
        return Collections.emptyList();
    }

    /**
     * 根据用户输入信息查询placeId
     *
     * @param input
     * @return
     */
    @SneakyThrows
    private List<PlaceDetail> googleSearchPlaceId(PlaceCheckInput input) {
        List<PlaceDetail> placeDetailList = new ArrayList<>();
        Iterator<String> apiKeyIterator = apiKey.iterator();

        //Get All the parameter and Set for the request
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(input.getPlaceName());
        if (StringUtils.isNotBlank(input.getZipCode())) {
            stringBuilder.append(" ").append(input.getZipCode());
        }
        stringBuilder.append(" INDONESIA");
        Map<String, String> searchTextParams = new HashMap<>();
        searchTextParams.put("query", stringBuilder.toString());

        changeKey:
        for (; ; ) {
            if (apiKeyIterator.hasNext()) {
                String currentKey = apiKeyIterator.next();
                searchTextParams.put("key", currentKey);
                String searchTextResult;
                searchTextResult = HttpUtil.get(googleSearchTextUri, searchTextParams);
                JSONObject searchTextObject = JSONObject.parseObject(searchTextResult);
                String searchTextStatus = searchTextObject.getString("status");
                switch (searchTextStatus) {
                    case "OK"://成功  Use Google API to get all the input-related Place ID and parse them into a List of String
                        JSONArray array = JSONArray.parseArray(searchTextObject.getString("results"));
                        for (int i = 0; i < array.size(); i++) {
                            PlaceDetail placeDetail;
                            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(array.get(i)));
                            if (jsonObject.getJSONArray("types").contains(input.getPlaceName())) {
                                placeDetail =
                                    googleSearchPlaceDetail(jsonObject.getString("place_id"));
                                placeDetail.setZipCode(input.getZipCode());
                                placeDetailList.add(placeDetail);
                            }
                        }
                        return placeDetailList;
                    case "OVER_QUERY_LIMIT"://账号到达上限，切换账号
                    case "REQUEST_DENIED":// key无效，获取下一个key
                        System.out.println("Google map search place id with invalid api key：" + currentKey);
                        continue changeKey;
                    default: //查询失败，放弃
                        return placeDetailList;
                }
            }
            return placeDetailList;
        }
    }

    /**
     * 根据placeId查询 placeDetail
     *
     * @param placeId
     * @return
     */
    @SneakyThrows
    private PlaceDetail googleSearchPlaceDetail(String placeId) {
        PlaceDetail placeDetail = new PlaceDetail();
        Iterator<String> apiKeyIterator = apiKey.iterator();
        Map<String, String> params = new HashMap<>();
        params.put("placeid", placeId);

        changeKey:
        for (; ; ) {
            if (apiKeyIterator.hasNext()) {
                String currentKey = apiKeyIterator.next();
                params.put("key", currentKey);
                //Use Proxy to access Google
                String result;
                result = HttpUtil.get(googleSearchDetailUri, params);
                JSONObject resultObject = JSONObject.parseObject(result);
                String status = resultObject.getString("status");
                switch (status) {
                    case "OK"://成功  Get The Result and Convert them into the BasicCompanyDetail Entity
                        placeDetail = new PlaceDetail();
                        JSONObject placeObject = resultObject.getJSONObject("result");
                        placeDetail.setPlaceName(placeObject.getString("name"));
                        placeDetail.setGeometryLocation(convertFromJSON(placeObject));
                        return placeDetail;
                    case "OVER_QUERY_LIMIT": //账号到达上限，切换账号
                    case "REQUEST_DENIED":   // key无效，获取下一个key
                        System.out.println("Google map search place id with invalid api key：" + currentKey);
                        System.out.println(status);
                        continue changeKey;
                    default:
                        return placeDetail;
                }
            }
            return placeDetail;
        }
    }


    private GeometryLocation convertFromJSON(JSONObject jsonObject) {
        JSONObject geometry = jsonObject.getJSONObject("geometry");
        JSONObject locationObject = geometry.getJSONObject("location");
        JSONObject northeastViewPortObject = geometry.getJSONObject("viewport").getJSONObject("northeast");
        JSONObject southwestViewPortObject = geometry.getJSONObject("viewport").getJSONObject("southwest");
        Location location = locationObject.toJavaObject(Location.class);
        Location northeastViewPort = northeastViewPortObject.toJavaObject(Location.class);
        Location southwestViewPort = southwestViewPortObject.toJavaObject(Location.class);

        GeometryLocation geometryLocation = new GeometryLocation();
        geometryLocation.setLocation(location);
        geometryLocation.setNortheastViewport(northeastViewPort);
        geometryLocation.setSouthwestViewport(southwestViewPort);
        return geometryLocation;
    }

    private void writeToCSV(List<PlaceDetail> placeDetails, String filename) {
        String path = "/Users/ekadenyaz/Desktop/";
        String fileName = path + filename + ".csv";
        try {
            PrintWriter pw = new PrintWriter(new File(fileName));
            String header = "Place Name,Latitude,Longitude,Northeast Viewport Latitude,Northeast Viewport Longitude," +
                "Southwest Viewport Latitude,Southwest Viewport Longitude,Zip Code";
            StringBuilder sb = new StringBuilder();
            for (PlaceDetail placeDetail : placeDetails) {
                GeometryLocation geometryLocation = placeDetail.getGeometryLocation();
                sb.append(placeDetail.getPlaceName().replaceAll(",", "/"));
                sb.append(",");
                sb.append(geometryLocation.getLocation().getLat());
                sb.append(",");
                sb.append(geometryLocation.getLocation().getLng());
                sb.append(",");
                sb.append(geometryLocation.getNortheastViewport().getLat());
                sb.append(",");
                sb.append(geometryLocation.getNortheastViewport().getLng());
                sb.append(",");
                sb.append(geometryLocation.getSouthwestViewport().getLat());
                sb.append(",");
                sb.append(geometryLocation.getSouthwestViewport().getLng());
                sb.append(",");
                sb.append(placeDetail.getZipCode());
                sb.append('\n');
            }
            pw.write( header + '\n' + sb.toString());
            pw.close();
        } catch (FileNotFoundException fileException) {
            System.out.println("File Can't be found");
        }
    }

    private List<PlaceDetail> mergeList(List<PlaceDetail> finalResult, List<PlaceDetail> newList) {
        for (PlaceDetail placeDetail : newList) {
            if (!finalResult.contains(placeDetail)) {
                finalResult.add(placeDetail);
            }
        }
        return finalResult;
    }

    private List<String> getAllIndonesiaZipCode(String fileName) {
        BufferedReader br;
        String[] strings = {};
        try {
            br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            strings = sb.toString().split(",");
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO EXception");
        }
        return Arrays.asList(strings);
    }

    private Callable<List<PlaceDetail>> createCallableByPlaceId(PlaceCheckInput input) {
        return () -> checkCompanyOnGoogle(input);
    }

    private List<Callable<List<PlaceDetail>>> createCallable(List<PlaceCheckInput> inputs) {
        return inputs.stream().map(this::createCallableByPlaceId).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private List<List<PlaceDetail>> getAllPlaceInfo(List<PlaceCheckInput> placeIdList) throws InterruptedException {
        ExecutorService executor = Executors.newWorkStealingPool(createProcessor(placeIdList.size()));
        return executor.invokeAll(createCallable(placeIdList))
            .stream()
            .map(future -> {
                try {
                    return future.get(2, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            })
            .collect(Collectors.toList());
    }

    private int createProcessor(int dataSize) {
        return dataSize > PROCESSOR_SIZE ? dataSize / PROCESSOR_SIZE : 1;
    }

    @Test
    public void crawlData() {
        List<PlaceDetail> finalResultList = new ArrayList<>();
        List<String> zipCodes = getAllIndonesiaZipCode("testZipCode.txt");
        PlaceCheckInput placeCheckInput = new PlaceCheckInput();
        int zipCodesSize = zipCodes.size();
        placeCheckInput.setPlaceName("restaurant");
        for (String zipCode : zipCodes) {
            placeCheckInput.setZipCode(zipCode);
            List<PlaceDetail> resultList = checkCompanyOnGoogle(placeCheckInput);
            finalResultList = mergeList(finalResultList, resultList);
            System.out.println(finalResultList.size());
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("Interrupted");
            }
            zipCodesSize--;
            System.out.println("Task Left : " + zipCodesSize);
        }
        writeToCSV(finalResultList, placeCheckInput.getPlaceName() + "-raw");
        List<PlaceDetail> filteredResult = finalResultList.stream().
            collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(PlaceDetail::getPlaceName))), ArrayList::new));
        System.out.println("Result Here:" + JSON.toJSONString(filteredResult.size()));
        writeToCSV(filteredResult, placeCheckInput.getPlaceName() + "-filtered");
    }

    @Test
    public void multiThreadCrawler(){
        String query = "hospital";
        List<PlaceDetail> finalResultList = new ArrayList<>();
        List<String> zipCodes = getAllIndonesiaZipCode("testZipCode.txt");
        List<PlaceCheckInput> inputList = new ArrayList<>();
        for(String zipCode : zipCodes){
            PlaceCheckInput input = new PlaceCheckInput();
            input.setPlaceName(query);
            input.setZipCode(zipCode);
            inputList.add(input);
        }
        try{
            List<List<PlaceDetail>> crawledData = getAllPlaceInfo(inputList);
            for(List<PlaceDetail> resultList : crawledData){
                finalResultList = mergeList(finalResultList, resultList);
            }
            System.out.println("Total Results Count : " + finalResultList.size());
            writeToCSV(finalResultList, query + "-raw");
            List<PlaceDetail> filteredResult = finalResultList.stream().
                collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(PlaceDetail::getPlaceName))), ArrayList::new));
            System.out.println("Filtered Results Count : " + filteredResult.size());
            writeToCSV(filteredResult, query + "-filtered");

        } catch (InterruptedException e){
            System.out.println("Interrupted");
        }
    }

    @Test
    public void zipCode(){
        System.out.println(getAllIndonesiaZipCode("ZipCode.txt").size());
    }

    @Test
    public void convertTest(){
        String result = "      {" +
            " \"geometry\" : {\n" +
            "         \"location\" : {\n" +
            "            \"lat\" : -33.866651,\n" +
            "            \"lng\" : 151.195827\n" +
            "         },\n" +
            "         \"viewport\" : {\n" +
            "            \"northeast\" : {\n" +
            "               \"lat\" : -33.8653881697085,\n" +
            "               \"lng\" : 151.1969739802915\n" +
            "            },\n" +
            "            \"southwest\" : {\n" +
            "               \"lat\" : -33.86808613029149,\n" +
            "               \"lng\" : 151.1942760197085\n" +
            "            }\n" +
            "         }\n" +
            "      }" +
            "}";
        JSONObject object = JSON.parseObject(result);
        System.out.println(convertFromJSON(object));
    }

}
