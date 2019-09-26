package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.annotations.Expose;

public class AckMessage extends WebSocketMessageData {

    @Expose
    private MessageTypes ackType;
    @Expose
    private String value;

    public AckMessage(final MessageTypes ackType, final String value) {
        this.ackType = ackType;
        this.value = value;
    }

    public MessageTypes getAckType() {
        return ackType;
    }

    public void setAckType(MessageTypes ackType) {
        this.ackType = ackType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getAckType() + " - " + getValue();
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }
}
