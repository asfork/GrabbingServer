package com.steve;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

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

    public static void writeDevicesList(String imei, String ip) {
        Map<String, String> devices;
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Gson gson = new Gson();
        try {
            File jsonFile = new File(jsonFilePath);
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
                devices = new HashMap<String, String>();
                devices.put(imei, ip);
            } else {
                JsonReader reader = new JsonReader(new FileReader(jsonFilePath));
                devices = gson.fromJson(reader, mapType);
                devices.put(imei, ip);
            }
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonFile), "UTF-8"))) {
                gson.toJson(devices, mapType, out);
            }
            System.out.println("Device " + imei + " has been added to 'devices.json'");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
