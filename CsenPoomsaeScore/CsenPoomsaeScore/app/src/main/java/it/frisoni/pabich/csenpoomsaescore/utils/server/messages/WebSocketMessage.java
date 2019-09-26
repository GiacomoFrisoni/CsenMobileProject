package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class WebSocketMessage<D extends WebSocketMessageData> {

    private Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose
    private MessageTypes messageType;
    @Expose
    private String deviceID;
    @Expose
    private D data;

    public WebSocketMessage(MessageTypes messageType, String deviceID, D data) {
        this.messageType = messageType;
        this.deviceID = deviceID;
        this.data = data;
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public MessageTypes getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageTypes messageType) {
        this.messageType = messageType;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }
}
