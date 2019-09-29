package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;


public class AckMessage extends WebSocketMessageData {

    public AckMessage() { }

    @Override
    public String toString() {
        return "Ack Message";
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    @Override
    public MessageTypes getMessageType() {
        return MessageTypes.ACK;
    }
}
