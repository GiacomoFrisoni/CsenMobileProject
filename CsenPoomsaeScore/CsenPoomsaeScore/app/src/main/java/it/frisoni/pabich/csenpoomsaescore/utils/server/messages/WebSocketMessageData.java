package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class WebSocketMessageData {

    protected Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public abstract String toString();
    public abstract String toJson();
    public abstract MessageTypes getMessageType();
}
