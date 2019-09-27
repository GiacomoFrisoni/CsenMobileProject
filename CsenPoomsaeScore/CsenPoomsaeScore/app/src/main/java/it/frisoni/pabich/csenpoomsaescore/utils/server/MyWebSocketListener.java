package it.frisoni.pabich.csenpoomsaescore.utils.server;

import okio.ByteString;

public abstract class MyWebSocketListener {

    public void onOpen(String senderIPAddress) {}
    public void onMessage(String senderIPAddress, String message) {}
    public void onMessage(String senderIPAddress, ByteString message) {}
    public void onFailure(String senderIPAddress,String reason) {}
    public void onClosing(String senderIPAddress,String reason) {}
    public void onClosed(String senderIPAddress,String reason) {}

    public void onPong(String senderIPAddress, String deviceID) { }

    public void onSetDeviceIDReceived(String senderIPAddress) { }
    public void onSetDeviceIDAckReceived(String senderIPAddress, String deviceID) { }
    public void onSetDeviceIDFailed() { }

    public void onAthleteScoreAckReceived() { }
}
