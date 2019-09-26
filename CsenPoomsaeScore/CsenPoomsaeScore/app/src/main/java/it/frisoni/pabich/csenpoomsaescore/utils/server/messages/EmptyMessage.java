package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

public class EmptyMessage extends WebSocketMessageData {

    public EmptyMessage() { }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public String toJson() {
        return "";
    }
}