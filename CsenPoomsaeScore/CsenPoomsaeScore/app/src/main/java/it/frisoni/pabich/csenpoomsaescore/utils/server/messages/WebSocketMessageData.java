package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public abstract class WebSocketMessageData {

    protected Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public abstract String toString();
    public abstract String toJson();
}

/*
* abstract class WebSocketMessageData
    {
        public abstract override string ToString();
        public abstract string ToJson();
    }

    class EmptyMessageData : WebSocketMessageData
    {

        public EmptyMessageData() { }

        public override string ToJson()
        {
            return JsonConvert.SerializeObject(this);
        }

        public override string ToString()
        {
            return "";
        }
    }
* */
