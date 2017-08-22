package ai.advance.utils;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ekadenyaz on 8/15/17.
 */
public class ZipCodeUtil {
    private void replaceZipCode(String original, String replacement){
        String path = "/Users/ekadenyaz/Desktop/";
        String originalFile = path + original + ".csv";
        String replacementFile = path + replacement + ".csv";
        try {
            PrintWriter pw = new PrintWriter(new File(original));
            String header = "Place Name,Latitude,Longitude,Zip Code";
            StringBuilder sb = new StringBuilder();
            pw.write( header + '\n' + sb.toString());
            pw.close();
        } catch (FileNotFoundException fileException) {
            System.out.println("File Can't be found");
        }
    }

    private Map<String, String> getZipCodeRange(String fileName){
        BufferedReader br;
        String [] strings={};
        Map<String, String> zipCodes = new HashMap<>();
        try{
            br = new BufferedReader(new FileReader(fileName));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                strings = line.split(",");
                zipCodes.put(strings[2], strings[3]);
                line = br.readLine();
            }
            br.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO EXception");
        }
        return zipCodes;
    }

    private void writeCSV(String fileName, Map<String, String> zipCodes) {
        BufferedReader br;
        String[] strings = {};
        List<String[]> textLines = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();
            while (line != null) {
                strings = line.split(",");
                strings[2] = zipCodes.get(strings[3]);
                textLines.add(strings);
                line = br.readLine();
            }
            br.close();
            PrintWriter pw = new PrintWriter(new File("population-new"));
            String header = "Province_Code,Province_Name,Zip_Code_Range,City_name,Age-Range,Male,Female";
            StringBuilder sb = new StringBuilder();
            for (String[] textLine : textLines) {
                for (String text : textLine) {
                    sb.append(text);
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\n");
            }
            pw.write(header + '\n' + sb.toString());
            pw.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("IO EXception");
        }
    }

    @Test
    public void test(){
        System.out.println(JSON.toJSONString(getZipCodeRange("city-information.csv")));
        writeCSV("population.csv", getZipCodeRange("city-information.csv"));
    }
}
