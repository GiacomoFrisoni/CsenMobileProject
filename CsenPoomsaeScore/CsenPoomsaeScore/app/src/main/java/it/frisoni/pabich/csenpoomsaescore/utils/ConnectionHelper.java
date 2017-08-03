package it.frisoni.pabich.csenpoomsaescore.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by marti on 03/08/2017.
 */

public class ConnectionHelper {

    private static ConnectionHelper helper;

    private static int Port;
    private static String Ip;
    private static DatagramSocket Socket;
    private static InetAddress Adress;


    private ConnectionHelper(String ip, int port) throws SocketException, UnknownHostException {
        Port = port;
        Socket = new DatagramSocket();
        Ip = ip;
        Adress = InetAddress.getByName(Ip);
    }

    public static boolean establishConnection(String ip, int port) {
        try {
            helper = new ConnectionHelper(ip, port);
            return sendMessage("Hello!");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean refreshConnection () {
        try {
            return sendMessage("Hello!");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean sendMessage(String text) {
        if (helper != null) {
            try {
                String messageStr = text;
                int msg_length = messageStr.length();
                byte[] message = messageStr.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length, Adress, Port);
                Socket.send(p);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public static boolean isConnectionEstabished() {
        return helper == null ? false : true;
    }

    public static String getIp() {
        return Ip;
    }
}
