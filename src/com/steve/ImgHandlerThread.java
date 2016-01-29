package com.steve;

import java.io.*;
import java.net.Socket;

/**
 * 该类实现Runnable接口，用于实现多线程复制图片。
 * 该类的作用就是与主线程传入的Socket连接进行通信，从网络流获取对方的传送的文件数据。
 */
public class ImgHandlerThread implements Runnable {
    Socket socket = null;
    String imgFolder = null;
    BufferedInputStream bufIn = null;
    BufferedOutputStream bufOut = null;
    PrintWriter result = null;

    public ImgHandlerThread(Socket socket, String imgFolder) {
        this.socket = socket;
        this.imgFolder = imgFolder;
    }

    public void run() {
        String imgPath = imgFolder + File.separator + socket.getInetAddress().getHostAddress() + ".jpg";
        File imgFile = new File(imgPath);

        if (!imgFile.getParentFile().exists()) {
                imgFile.getParentFile().mkdirs();
        }

        if (!imgFile.exists()) {
            try {
                imgFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //获取传入Socket的输入流，用于读取图片数据
            bufIn = new BufferedInputStream(socket.getInputStream());
            //在当前文件夹创建名为filename的文件，作为输出流的目的
            bufOut = new BufferedOutputStream(new FileOutputStream(imgFile));
            //该流用来向客户端发送确认信息
            result = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            byte[] bufdata = new byte[1024];
            int len;
            //从输入流读取数据，不为-1，即文件结束则继续读
            while ((len = bufIn.read(bufdata)) != -1) {
                //往文件里写数据
                bufOut.write(bufdata, 0, len);
                bufOut.flush();
            }

            result.println("Image received successfully");
            System.out.println("Grabbing done from " + socket.getInetAddress().getHostAddress());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                bufIn.close();
                bufOut.close();
                result.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
