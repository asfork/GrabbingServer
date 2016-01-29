package com.steve;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 文件操作帮助类
 * 读取配置文件及输出 Json 文件
 */
public class FileUtils {
    private static String proFilePath = System.getProperty("user.dir") + "/config.properties";

    public static String readProperties(String key) {
        Properties props = new Properties();
        InputStream in = null;
        try {
            System.out.println(proFilePath);
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


}
