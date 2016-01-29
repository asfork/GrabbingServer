package com.steve;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Run server to listen to broadcast and receive images from devices in new thread
 */
public class GrabbingServer {
    private static int LOCAL_PORT;
    private static String IMG_FOLDER;

    public static void main(String[] args) throws Exception {
        // Load properties file to get settings
        try {
            String local_port = FileUtils.readProperties("LOCAL_PORT");
            IMG_FOLDER = FileUtils.readProperties("IMG_FOLDER");

            if (local_port != null) {
                LOCAL_PORT = Integer.parseInt(local_port);
            } else {
                System.out.println("HOST_PORT or LOCAL_PORT error");
                System.exit(1);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Properties file missing");
            System.exit(1);
        }

        // Use this port to receive broadcast packet
        final DatagramSocket datagramSocket = new DatagramSocket(LOCAL_PORT);
        // Receive UDP packet thread.
        new Thread(new BroadcastListenerThread(datagramSocket)).start();

        // listen to LOCAL_PORT
        ServerSocket serverSocket = new ServerSocket(LOCAL_PORT);
        if(serverSocket.isBound())
            System.out.println("The Server is listenning the port " + LOCAL_PORT);
        while(true) {
            //检查是否有连接，该语句是阻塞语句，如果没有则会停在这
            Socket socket = serverSocket.accept();
            //如果有连接则返回true，执行下面语句
            if(socket.isConnected()) {
                //打印已连接的客户端的IP
                System.out.println(socket.getInetAddress().getHostAddress() + " is connected!");
                //开启新线程接收图片，主线程继续回去while最开始检查有无连接
                new Thread(new ImgHandlerThread(socket, IMG_FOLDER)).start();
            }
        }
    }
}