package com.steve;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 文件操作帮助类
 * 读取配置文件及输出 Json 文件
 */
public class FileUtils {
    private static String proFilePath = System.getProperty("user.dir") + File.separator + "config.properties";
    private static String jsonFilePath = System.getProperty("user.dir") + File.separator + "devices.json";

    public static String readProperties(String key) {
        Properties props = new Properties();
        InputStream in = null;
        try {
//            System.out.println(proFilePath);
            in = new FileInputStream(proFilePath);
            props.load(in);
            return props.getProperty(key).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void removeDevicesList() {
        File jsonFile = new File(jsonFilePath);
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    public static void writeDevicesList(String json) {
        try {
            File jsonFile = new File(jsonFilePath);
            if (jsonFile.exists()) {
                jsonFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type listType = new TypeToken<ArrayList<String>>(){}.getType();
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Gson gson = new Gson();
        List<String> device = gson.fromJson(json, listType);

        try {
            JsonReader reader = new JsonReader(new FileReader(jsonFilePath));
            Map<String, String> devices = gson.fromJson(reader, mapType);
            devices.put(device.get(0), device.get(1));

            try {
                JsonWriter writer = new JsonWriter(new FileWriter(jsonFilePath));
                gson.toJson(devices, mapType, writer);
                System.out.println("Device " + device.get(0) + " has been added to 'devices.json'");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
