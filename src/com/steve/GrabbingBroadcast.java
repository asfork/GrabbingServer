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
    @Option(name = "--give", aliases = "-g", metaVar = "<command>", usage = "Give broadcast to all devices in LAN")
    private String message = "grab";

    @Option(name = "--address", aliases = "-a", metaVar = "<192.0.0.*>", usage = "The IP address of the target device")
    private String address;

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
        if (message.equals(ACTION_GRAB)) {
            //get images from all devices in LAN
            giveBroadcast(BROADCAST_ADDRESS, ACTION_GRAB);
        } else if (message.equals(ACTION_CALL)) {
            // get available devices list in LAN
            FileUtils.removeDevicesList();
            giveBroadcast(BROADCAST_ADDRESS, ACTION_CALL);
        } else if (address != null) {
            // get images by device IP
            giveBroadcast(address, ACTION_GRAB);
        } else {
            System.out.println("Unknown arguments");
        }
    }

    private void giveBroadcast(String address, String msg) {
        try {
            InetAddress inetAddress = InetAddress.getByName(address);
            SocketAddress socketAddress = new InetSocketAddress(inetAddress, HOST_PORT);
            try {
                // Use local port to send broadcast packet
                DatagramSocket datagramSocket = new DatagramSocket(LOCAL_PORT);
                try {
                    byte[] buf = new byte[1024];
                    buf = msg.getBytes();
                    DatagramPacket outPacket = new DatagramPacket(buf, buf.length,
                            socketAddress);
                    datagramSocket.send(outPacket);
                    System.out.println("Send " + msg + " to " + address);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
