package it.frisoni.pabich.csenpoomsaescore.utils.server;

public class MessageCounter {

    private static MessageCounter messageCounter;
    private static int messageCount;

    private MessageCounter() {
        messageCount = 0;
    }

    public static MessageCounter getInstance() {
        if (messageCounter == null)
            messageCounter = new MessageCounter();

        return messageCounter;
    }

    public synchronized int getNextMessageID() {
        messageCount++;
        return messageCount;
    }

    public synchronized void resetCounter() {
        messageCount = 0;
    }
}
