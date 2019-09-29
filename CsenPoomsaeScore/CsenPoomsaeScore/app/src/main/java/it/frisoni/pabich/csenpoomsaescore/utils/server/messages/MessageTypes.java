package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

/**
 * All messages have one type
 */

public enum MessageTypes {
    DEFAULT("DEFAULT"),
    DEVICE_ACCEPTED("DEVICE_ACCEPTED"),
    DEVICE_REJECTED("DEVICE_REJECTED"),
    ACK("ACK"),
    CONNECTION_CHECK("CONNECTION_CHECK"),
    ATHLETE_SCORE("ATHLETE_SCORE");

    private String description;

    MessageTypes(final String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}