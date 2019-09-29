package it.frisoni.pabich.csenpoomsaescore.utils.server;

import java.util.ArrayList;
import java.util.List;

import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;
import it.frisoni.pabich.csenpoomsaescore.widgets.WidgetWithConnectionStatus;

public class ConnectionStatus {

    // Internal variables
    private final static long PING_INTERVAL = 30000;

    // Static variable for singleton
    private static ConnectionStatus connectionStatusRef;

    // Class variables
    private ConnectionStatuses currentConnectionStatus;
    private String serverIPAddress, serverPort, deviceIDRef;
    private List<ConnectionStatusListener> connectionStatusListeners;
    private Thread keepAliveThread;

    private ConnectionStatus() {
        currentConnectionStatus = ConnectionStatuses.NOT_CONNECTED;
        connectionStatusListeners = new ArrayList<>();
        serverIPAddress = "";
        serverPort = "";
        deviceIDRef = "";
    }

    public static ConnectionStatus getInstance() {
        if (connectionStatusRef == null)
            connectionStatusRef = new ConnectionStatus();

        return connectionStatusRef;
    }

    public void addConnectionStatusListener(final ConnectionStatusListener listener) {
        this.connectionStatusListeners.add(listener);
    }

    public void removeConnectionStatusListener(final ConnectionStatusListener listener) {
        this.connectionStatusListeners.remove(listener);
    }

    public ConnectionStatusListener bindWidgetToConnectionStatus(final WidgetWithConnectionStatus widget) {

        // First set the widget at current status
        switch (currentConnectionStatus) {
            case NOT_CONNECTED:
                widget.setOnNotConnected();
                break;
            case CONNECTING:
                widget.setOnConnecting();
                break;
            case CONNECTED:
                widget.setOnConnected(getServerIPAddress(), getServerPort(), getDeviceID());
                break;
            default:
                break;
        }

        // Create then the listener for the widget
        final ConnectionStatusListener listener = new ConnectionStatusListener() {
            @Override
            public void onConnectionStatusChanged(final ConnectionStatuses connectionStatus) {
                switch (connectionStatus) {
                    case NOT_CONNECTED:
                        widget.setOnNotConnected();
                        break;
                    case CONNECTING:
                        widget.setOnConnecting();
                        break;
                    case CONNECTED:
                        widget.setOnConnected(getServerIPAddress(), getServerPort(), getDeviceID());
                        break;
                    default:
                        break;
                }
            }
        };

        // Add it to listeners
        addConnectionStatusListener(listener);

        // And return it for removing in from the view, when no more need
        return listener;
    }

    public ConnectionStatuses getCurrentConnectionStatus() {
        return currentConnectionStatus;
    }

    public void configureConnection(final String ip, final String port) {
        serverIPAddress = ip;
        serverPort = port;
    }

    public void forceRefreshConnectionStatus() {
        // Check if server is still alive
        if (WebSocketHelper.getInstance().isServerAvailable()) {

            // If it's still alive, send an "are you still alive" message
            WebSocketHelper.getInstance().sendConnectionCheckRequest(new ResponseListener() {
                @Override
                public void onResponse() {
                    // Ack received in time, inform all my subscribers is all ok
                    for (ConnectionStatusListener listener : connectionStatusListeners) {
                        listener.onConnectionStatusChanged(ConnectionStatuses.CONNECTED);
                    }
                }

                @Override
                public void onTimeout() {
                    // Timeout! Server is not responding, so connection is no more
                    setOnNotConnected();

                    for (ConnectionStatusListener listener : connectionStatusListeners) {
                        listener.onConnectionStatusChanged(ConnectionStatuses.NOT_CONNECTED);
                    }
                }
            });
        } else {
            // Server is not alive, so there is no connection!
            for (ConnectionStatusListener listener : connectionStatusListeners) {
                listener.onConnectionStatusChanged(ConnectionStatuses.NOT_CONNECTED);
            }
        }
    }

    public void setOnConnecting() {
        currentConnectionStatus = ConnectionStatuses.CONNECTING;
        deviceIDRef = "";

        for (ConnectionStatusListener listener : connectionStatusListeners) {
            listener.onConnectionStatusChanged(ConnectionStatuses.CONNECTING);
        }
    }

    public void setOnNotConnected() {
        currentConnectionStatus = ConnectionStatuses.NOT_CONNECTED;
        deviceIDRef = "";
        serverIPAddress = "";
        serverPort = "";
        keepAliveThread.interrupt();

        for (ConnectionStatusListener listener : connectionStatusListeners) {
            listener.onConnectionStatusChanged(ConnectionStatuses.NOT_CONNECTED);
        }
    }

    public void setOnConnected(final String deviceID) {
        // Save all variables
        currentConnectionStatus = ConnectionStatuses.CONNECTED;
        deviceIDRef = deviceID;

        // Inform all that device is connected!
        for (ConnectionStatusListener listener : connectionStatusListeners) {
            listener.onConnectionStatusChanged(ConnectionStatuses.CONNECTED);
        }

        // Good, keep-alive can start now to ping-pong
        keepAliveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        // Sleep for X seconds
                        Thread.sleep(PING_INTERVAL);

                        // Ask if server is still alive
                        forceRefreshConnectionStatus();
                    } catch (Exception e) {}
                }
            }
        });

        keepAliveThread.start();
    }

    public String getDeviceID() {
        return this.deviceIDRef;
    }

    public String getServerIPAddress() {
        return serverIPAddress;
    }

    public String getServerPort() {
        return serverPort;
    }
}