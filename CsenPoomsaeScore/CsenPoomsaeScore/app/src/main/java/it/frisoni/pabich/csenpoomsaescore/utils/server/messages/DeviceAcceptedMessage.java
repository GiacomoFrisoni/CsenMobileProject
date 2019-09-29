package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.annotations.Expose;

public class DeviceAcceptedMessage extends WebSocketMessageData {

    @Expose
    private String deviceID;

    public DeviceAcceptedMessage(final String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceID() {
        return this.deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    @Override
    public String toString() {
        return "DeviceID: " + deviceID;
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public MessageTypes getMessageType() {
        return MessageTypes.DEVICE_ACCEPTED;
    }
}
