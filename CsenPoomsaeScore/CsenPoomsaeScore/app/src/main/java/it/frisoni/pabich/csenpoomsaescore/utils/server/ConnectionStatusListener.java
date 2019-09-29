package it.frisoni.pabich.csenpoomsaescore.utils.server;

public abstract class ConnectionStatusListener {

    /**
     * Event rised when the connection status changes.
     */
    public void onConnectionStatusChanged(final ConnectionStatuses connectionStatus) { }
}
