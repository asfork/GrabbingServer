package com.steve;

import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Run server to listen to broadcast and receive images from devices in new thread
 */
public class GrabbingServer {
    private static int LOCAL_LISTENING_PORT;
    private static String IMG_FOLDER;

    public static void main(String[] args) throws Exception {
        // Load properties file to get settings
        try {
            String listeningPort = FileUtils.readProperties("LOCAL_LISTENING_PORT");
            IMG_FOLDER = FileUtils.readProperties("IMG_FOLDER");

            if (listeningPort != null) {
                LOCAL_LISTENING_PORT = Integer.parseInt(listeningPort);
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
        final DatagramSocket datagramSocket = new DatagramSocket(LOCAL_LISTENING_PORT);
        // Receive UDP packet thread.
        new Thread(new BroadcastListenerThread(datagramSocket)).start();

        // listen to LOCAL_PORT
        ServerSocket serverSocket = new ServerSocket(LOCAL_LISTENING_PORT);
        if(serverSocket.isBound())
            System.out.println("Images handler thread started");
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
