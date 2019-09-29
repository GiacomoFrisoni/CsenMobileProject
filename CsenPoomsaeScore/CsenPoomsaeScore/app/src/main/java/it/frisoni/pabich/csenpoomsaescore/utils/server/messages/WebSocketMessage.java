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
    private int ackMessageID;


    public WebSocketMessage(final int messageID, final D data, final int ackMessageID) {
        this.messageID = messageID;
        this.data = data;
        this.ackMessageID = ackMessageID;
        this.messageType = data.getMessageType();
    }

    public WebSocketMessage(final int messageID, final D data) {
        this(messageID, data, -1);
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

    public int getAckMessageID() {
        return ackMessageID;
    }

    public void setAckMessageID(int ackMessageID) {
        this.ackMessageID = ackMessageID;
    }
}
