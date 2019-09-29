package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.annotations.Expose;

public class DeviceRejectedMessage extends WebSocketMessageData {

    @Expose
    private String reason;

    public DeviceRejectedMessage(final String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Reason: " + reason;
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public MessageTypes getMessageType() {
        return MessageTypes.DEVICE_REJECTED;
    }
}