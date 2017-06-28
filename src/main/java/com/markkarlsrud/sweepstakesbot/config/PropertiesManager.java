package com.markkarlsrud.sweepstakesbot.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by mkarlsru on 6/25/17.
 */
public class PropertiesManager {
    private static PropertiesManager ourInstance = new PropertiesManager();

    public static PropertiesManager getInstance() {
        return ourInstance;
    }

    private PropertiesManager() {
    }

    public static Properties getProperties(String fileName) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = getFile(fileName, ".properties");
        properties.load(inputStream);
        return properties;
    }

    public static List<String> getCSVStrings(String fileName) throws IOException {
        InputStream inputStream = getFile(fileName, ".csv");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        List<String> inputList = br.lines().collect(Collectors.toList());
        br.close();
        return inputList;
    }

    private static InputStream getFile(String fileName, String fileSuffix) {
        return PropertiesManager.class.getClassLoader().getResourceAsStream(
                fileName.endsWith(fileSuffix) ? fileName : fileName + fileSuffix);
    }
}
