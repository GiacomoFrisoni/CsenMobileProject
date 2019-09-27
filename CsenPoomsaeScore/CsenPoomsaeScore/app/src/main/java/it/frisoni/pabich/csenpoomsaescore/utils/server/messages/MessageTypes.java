package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

/**
 * All messages have one type
 */

public enum MessageTypes {
    SET_DEVICE_ID("SET_DEVICE_ID"), REJECT_DEVICE("REJECT_DEVICE"), ACK("ACK"), PING("PING"), PONG("PONG"), ATHLETE_SCORE("ATHLETE_SCORE");

    private String description;

    MessageTypes(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}


/*
public abstract class messageTypes {
    
    private final String code;
    
    private messageTypes(final String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.getCode();
    }

    /*
      All messages INCOMING into the device

    public static class Incoming extends messageTypes {

        public static final Incoming SET_DEVICE_ID = new Incoming("set-device-id");
        public static final Incoming PING = new Incoming("ping-request");
        public static final Incoming PONG = new Incoming("pong-response");
        public static final Incoming DATA = new Incoming("data");
        public static final Incoming ACK_DATA = new Incoming("ack-data");

        private Incoming(String code) {
            super(code);
        }
    }

     All messages OUTCOMING from the device

    public static class Outcoming extends messageTypes {

        public static final Outcoming PING = new Outcoming("ping-request");
        public static final Outcoming PONG = new Outcoming("pong-response");
        public static final Outcoming DATA = new Outcoming("data");
        public static final Outcoming ACK_DATA = new Outcoming("ack-data");
        
        private Outcoming(String code) {
            super(code);
        }
    }
}*/