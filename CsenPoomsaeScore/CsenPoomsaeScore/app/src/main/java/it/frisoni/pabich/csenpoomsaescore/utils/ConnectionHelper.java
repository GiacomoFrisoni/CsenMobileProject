package it.frisoni.pabich.csenpoomsaescore.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ConnectionHelper  {

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
            return refreshConnection();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean refreshConnection () {
        return sendMessage("Hello!");
    }

    public static  boolean sendMessage(String text) {
        MessageSender msg = new MessageSender();
        msg.execute(new MessageToSend(text, Port, Adress, Socket));
        return true;
    }


    public static boolean isConnectionEstabished() {
        return helper != null;
    }

    public static boolean isConnectionAvaiable() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public static String getIp() {
        return Ip;
    }

    public static  void resetConnection() {
        helper = null;
    }
}
