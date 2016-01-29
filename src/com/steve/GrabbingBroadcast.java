package com.steve;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.net.*;

/**
 * Get command line incoming string and send them as broadcast
 */
public class GrabbingBroadcast {
    private static final String ACTION_GRAB = "grab";
    private static final String ACTION_CALL = "devices";

    private static String BROADCAST_ADDRESS;
    private static int HOST_PORT;
    private static int LOCAL_PORT;

    // Command line values
    @Option(name = "-g", aliases = "--grab", usage = "get images from all devices in LAN")
    private boolean getImg = true;

    @Option(name = "-a", aliases = "--address", usage = "get images by device IP")
    private String address;

    @Option(name = "-d", aliases = "--devices", usage = "get available devices list in LAN")
    private boolean getDevices = true;


    public static void main(String[] args) {
        System.exit(new GrabbingBroadcast().run(args));
    }

    private int run(String[] args) {
        int isErr = 1;
        CmdLineParser parser = new CmdLineParser(this);
        try {
            // Get the command line values
            parser.parseArgument(args);
            isErr = getProperties();

            if (isErr == 0) {
                run();
            }
            return isErr;
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            return isErr;
        }
    }

    private int getProperties() {
        try {
            BROADCAST_ADDRESS = FileUtils.readProperties("BROADCAST_ADDRESS");
            String host_port = FileUtils.readProperties("HOST_PORT");
            String local_port = FileUtils.readProperties("LOCAL_PORT");

            if (host_port != null && local_port != null) {
                HOST_PORT = Integer.parseInt(host_port);
                LOCAL_PORT = Integer.parseInt(local_port);
            } else {
                System.out.println("HOST_PORT or LOCAL_PORT error");
                return 1;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Properties file missing");
            return 1;
        }
        return 0;
    }

    private void run() {
        try {
            // get images by device IP
            if (getImg && address != null) {
                InetAddress inetAddress = InetAddress.getByName(address);
                SocketAddress socketAddress = new InetSocketAddress(inetAddress, HOST_PORT);
                giveBroadcast(socketAddress, ACTION_GRAB);
            } else if (getDevices && address == null) {  //get images from all devices in LAN
                InetAddress inetAddress = InetAddress.getByName(BROADCAST_ADDRESS);
                SocketAddress socketAddress = new InetSocketAddress(inetAddress, HOST_PORT);
                giveBroadcast(socketAddress, ACTION_GRAB);
            } else if (getDevices) {   // get available devices list in LAN
                InetAddress inetAddress = InetAddress.getByName(BROADCAST_ADDRESS);
                SocketAddress socketAddress = new InetSocketAddress(inetAddress, HOST_PORT);
                giveBroadcast(socketAddress, ACTION_CALL);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void giveBroadcast(SocketAddress socketAddr, String msg) {
        try {
            // Use local port to send broadcast packet
            final DatagramSocket datagramSocket = new DatagramSocket(LOCAL_PORT);
            try {
                byte[] buf = new byte[1024];
                buf = msg.getBytes();
                DatagramPacket outPacket = new DatagramPacket(buf, buf.length,
                        socketAddr);
                datagramSocket.send(outPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
