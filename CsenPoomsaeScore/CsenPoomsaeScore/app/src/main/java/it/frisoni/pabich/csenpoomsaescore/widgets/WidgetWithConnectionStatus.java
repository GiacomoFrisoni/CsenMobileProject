package it.frisoni.pabich.csenpoomsaescore.widgets;

public interface WidgetWithConnectionStatus {
    void setOnNotConnected();
    void setOnConnecting();
    void setOnConnected(final String ip, final String port, final String deviceID);
}
