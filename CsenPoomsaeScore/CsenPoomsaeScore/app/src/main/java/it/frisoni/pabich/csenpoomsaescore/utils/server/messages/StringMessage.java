package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.annotations.Expose;

public class StringMessage extends  WebSocketMessageData {

    @Expose
    private String value;

    public StringMessage(final String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}