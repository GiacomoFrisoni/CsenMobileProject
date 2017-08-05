package it.frisoni.pabich.csenpoomsaescore.utils;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by marti on 05/08/2017.
 */

public class MessageToSend {

    private String Text;
    private static int Port;
    private static InetAddress Adress;
    private static DatagramSocket Socket;

    public MessageToSend(String text, int port, InetAddress adress, DatagramSocket socket) {
        Text = text;
        Port = port;
        Adress = adress;
        Socket = socket;
    }

    public String getText() {
        return Text;
    }

    public static int getPort() {
        return Port;
    }

    public static InetAddress getAdress() {
        return Adress;
    }

    public static DatagramSocket getSocket() {
        return Socket;
    }
}
