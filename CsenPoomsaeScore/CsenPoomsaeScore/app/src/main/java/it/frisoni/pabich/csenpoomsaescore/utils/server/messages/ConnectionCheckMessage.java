package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

public class ConnectionCheckMessage extends WebSocketMessageData {

    public ConnectionCheckMessage() { }

    @Override
    public String toString() {
        return "Connection Check";
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public MessageTypes getMessageType() {
        return MessageTypes.CONNECTION_CHECK;
    }
}