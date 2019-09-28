package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class WebSocketMessage<D extends WebSocketMessageData> {

    private Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose
    private int messageID;
    @Expose
    private MessageTypes messageType;
    @Expose
    private D data;
    @Expose
    private int responseForMessageID;


    public WebSocketMessage(final int messageID, final MessageTypes messageType, final D data, final int responseForMessageID) {
        this.messageID = messageID;
        this.messageType = messageType;
        this.data = data;
        this.responseForMessageID = responseForMessageID;
    }

    public WebSocketMessage(final int messageID, final MessageTypes messageType, final D data) {
        this(messageID, messageType, data, -1);
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

    public D getData() {
        return this.data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public int getMessageID() {
        return this.messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public int getResponseForMessageID() {
        return responseForMessageID;
    }

    public void setResponseForMessageID(int responseForMessageID) {
        this.responseForMessageID = responseForMessageID;
    }
}
