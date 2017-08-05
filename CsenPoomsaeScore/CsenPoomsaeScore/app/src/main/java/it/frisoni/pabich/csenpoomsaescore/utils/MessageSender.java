package it.frisoni.pabich.csenpoomsaescore.utils;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.Socket;

/**
 * Created by marti on 05/08/2017.
 */

public class MessageSender extends AsyncTask<MessageToSend, Void, Boolean> {

    private Exception exception;

    protected Boolean doInBackground(MessageToSend... props) {
        try {
            try {
                String messageStr = props[0].getText();
                int msg_length = messageStr.length();
                byte[] message = messageStr.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length, props[0].getAdress(), props[0].getPort());
                props[0].getSocket().send(p);
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    protected void onPostExecute() {

    }
}
