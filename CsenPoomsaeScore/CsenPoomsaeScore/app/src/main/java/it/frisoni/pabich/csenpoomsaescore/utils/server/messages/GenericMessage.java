package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.annotations.Expose;

public class GenericMessage extends  WebSocketMessageData {

    @Expose
    private String data;

    public GenericMessage(final String data) {
        this.data = data;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return getData();
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public MessageTypes getMessageType() {
        return MessageTypes.DEFAULT;
    }
}