package it.frisoni.pabich.csenpoomsaescore.utils.server;

import android.annotation.SuppressLint;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.AckMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.ConnectionCheckMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.DeviceAcceptedMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.DeviceRejectedMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.GenericMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.WebSocketMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.WebSocketMessageData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketHelper {

    // Internal variables
    private final static long ACK_TIMEOUT = 6000;
    public final static String DEFAULT_WEBSOCKET_IP = "192.168.1.3";
    public final static String DEFAULT_WEBSOCKET_PORT = "25565";

    // Reference to Gson converter
    private Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    // Static variable to access the Singleton class
    private static WebSocketHelper webSocketHelper;


    // List of listeners attached to WebSocketHelper that will be informed when some message arrives
    private Map<Integer, ResponseListener> pendingMessages;
    private ConnectionStatusListener connectionStatusListener;

    // Reference to WebSocket, when opened (can be null)
    private WebSocket webSocketRef;


    @SuppressLint("UseSparseArrays")
    private WebSocketHelper() {
        this.pendingMessages = new HashMap<>();
        this.connectionStatusListener = new ConnectionStatusListener() {
            @Override
            public void onConnectionStatusChanged(final ConnectionStatuses connectionStatus) {
                if (connectionStatus == ConnectionStatuses.NOT_CONNECTED) {
                    webSocketRef = null;
                }
            }
        };

        ConnectionStatus.getInstance().addConnectionStatusListener(this.connectionStatusListener);
    }


    public boolean isServerAvailable() {
        return webSocketRef != null;
    }

    /**
     * Get the instance of WebScoket. Remember, for using it you need to call configureWebSocket first!
     * @return the reference for the incapsulated WebSocket logic
     */
    public static WebSocketHelper getInstance(){
        if (webSocketHelper == null)
            webSocketHelper = new WebSocketHelper();

        return webSocketHelper;
    }

    /**
     * Get the configuration done for the WebSocket, trying to connect on the specified IP-PORT combination.
     * Creates the request and tries to connect on the WebSocket. Register a listener before calling this method,
     * since if WebScoket will be opened, an onOpen or onFailure method will be called.
     * @param ipAddress address when to connect to WebSocket
     * @param port port when to connect to WebSocket
     */
    public void configureWebSocket(final String ipAddress, final String port){
        // Save the reference for IP and PORT
        ConnectionStatus.getInstance().configureConnection(ipAddress, port);

        // Create the client and the request call for the (insecure) WebSocket (secure = wss)
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("ws://" + ConnectionStatus.getInstance().getServerIPAddress() + ":" + ConnectionStatus.getInstance().getServerPort()).build();

        // After creating the listener, make the request for the WebSocket, passing the listener on.
        this.webSocketRef = client.newWebSocket(request, this.createWebSocketListener());
        client.dispatcher().executorService().shutdown();

        WebSocketLogger.Log("WebSocket configuration started");
    }


    /**
     * Send an ACK to the server, informing that I received the message with specified ID
     * @param ackMessageID MessageID of message that I ack
     * @return TRUE if sending is possible, FALSE if I cannot send (server not configured / connected)
     */
    public boolean sendAckFor(final int ackMessageID) {
        return this.sendMessage(new AckMessage(), ackMessageID, null);
    }

    /**
     * Send a PING to the server, to ask if it's still alive
     * @param responseListener the listener triggered when response from server will arrive
     * @return TRUE if sending is possible, FALSE if I cannot send (server not configured / connected)
     */
    public boolean sendConnectionCheckRequest(final ResponseListener responseListener) {
        return this.sendMessage(new ConnectionCheckMessage(), -1, responseListener);
    }

    /**
     * Send a request to the server
     * @param data type of WebSocketMessageData I want to send
     * @param responseListener the listener triggered when response from server will arrive
     * @param <D> any type that extends from WebSocketMessageData
     * @return TRUE if sending is possible, FALSE if I cannot send (server not configured / connected)
     */
    public <D extends WebSocketMessageData> boolean sendRequest(final D data, final ResponseListener responseListener) {
        return this.sendMessage(data, -1, responseListener);
    }


    /**
     * Send a message to the server
     * @param data type of WebSocketMessageData I want to send
     * @param ackMessageID MessageID of the server message that I respond for
     * @param responseListener the listener triggered when response from server will arrive
     * @param <D> any type that extends from WebSocketMessageData
     * @return TRUE if sending is possible, FALSE if I cannot send (server not configured / connected)
     */
    public <D extends WebSocketMessageData> boolean sendMessage(final D data, final int ackMessageID, final ResponseListener responseListener) {
        synchronized (this) {
            if (isServerAvailable()) {
                // Prepare the message
                final WebSocketMessage<D> message = new WebSocketMessage<>(
                        MessageCounter.getInstance().getNextMessageID(),
                        data,
                        ackMessageID
                );

                // If I need a response for this particular message
                if (responseListener != null) {
                    // Put it in the pending messages
                    this.pendingMessages.put(message.getMessageID(), responseListener);

                    // Creates new thread that will check if message will arrive
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Sleep X time
                                Thread.sleep(ACK_TIMEOUT);

                                // If after X time the message is still on pendingMessages, timeout and delete
                                final ResponseListener tmpResponseListener = pendingMessages.get(message.getMessageID());
                                if (tmpResponseListener != null) {
                                    tmpResponseListener.onTimeout();
                                    pendingMessages.remove(message.getMessageID());
                                }

                                ConnectionStatus.getInstance().forceRefreshConnectionStatus();

                            } catch (Exception e) { }
                        }
                    }).start();
                }

                // Send the message
                webSocketRef.send(message.toJson());

                // LOG
                WebSocketLogger.Log(
                        "Message sent",
                        new Pair<>("MessageType", message.getMessageType().getDescription()),
                        new Pair<>("Data", message.getData().toString()),
                        new Pair<>("AckMessageID", String.valueOf(message.getAckMessageID())));

                return true;
            } else {
                // LOG
                WebSocketLogger.Log("Message was not sent: isServerAvailable() = false");
                return false;
            }
        }
    }

    /**
     *  Prepare the listener of the WebSocket, that will respond on EVERY message.
     *  On every "true" WebSocket listener there is the call for every "listener" that wants
     *  to know what's happening. To each message many listeners can be attached.
     * @return WebSocket listener, ready to use.
     */
    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);

                // Force the Connecting status (I'm opening...)
                ConnectionStatus.getInstance().setOnConnecting();

                // LOG
                WebSocketLogger.Log("Connection is opening...");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                // Convert the message for future uses
                final WebSocketMessage<GenericMessage> message = gson.fromJson(text, new TypeToken<WebSocketMessage<GenericMessage>>(){}.getType());

                // Logger: I received a message!
                WebSocketLogger.Log("Received a new message", new Pair<>(message.getMessageType().toString(), String.valueOf(message.getMessageID())));

                // Check the type of the message
                switch (message.getMessageType()) {
                    //------------------------------------------------------------------------------------------------------------------------------------------------------------- Device Accepted
                    case DEVICE_ACCEPTED:
                        // Get the right message
                        final WebSocketMessage<DeviceAcceptedMessage> wsmDeviceAccepted = gson.fromJson(text, new  TypeToken<WebSocketMessage<DeviceAcceptedMessage>>(){}.getType());

                        // Ack the server that I received the DEVICE_ACCEPTED
                        sendAckFor(wsmDeviceAccepted.getMessageID());

                        // Now I'm ready to be connected
                        ConnectionStatus.getInstance().setOnConnected(wsmDeviceAccepted.getData().getDeviceID());

                        WebSocketLogger.Log("Ack sent for DEVICE_ACCEPTED");
                        break;

                    //------------------------------------------------------------------------------------------------------------------------------------------------------------- Device Rejected
                    case DEVICE_REJECTED:
                        // Got it, I cannot be accepted, send ACK that I know I'm rejected
                        final WebSocketMessage<DeviceRejectedMessage> wsmDeviceRejected = gson.fromJson(text, new  TypeToken<WebSocketMessage<DeviceRejectedMessage>>(){}.getType());
                        sendAckFor(wsmDeviceRejected.getMessageID());

                        // I cannot be connected
                        ConnectionStatus.getInstance().setOnNotConnected();
                        break;

                    //------------------------------------------------------------------------------------------------------------------------------------------------------------- Ack
                    case ACK:
                        synchronized (this) {
                            final WebSocketMessage<AckMessage> wsmAck = gson.fromJson(text, new TypeToken<WebSocketMessage<AckMessage>>() {}.getType());
                            final ResponseListener responseListener = pendingMessages.get(wsmAck.getAckMessageID());

                            if (responseListener != null) {
                                responseListener.onResponse();
                                pendingMessages.remove(wsmAck.getAckMessageID());
                            }
                        }
                        break;

                    //------------------------------------------------------------------------------------------------------------------------------------------------------------- Connection Check
                    case CONNECTION_CHECK:
                        final WebSocketMessage<ConnectionCheckMessage> wsmConnectionCheck = gson.fromJson(text, new  TypeToken<WebSocketMessage<ConnectionCheckMessage>>(){}.getType());
                        sendAckFor(wsmConnectionCheck.getMessageID());
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);

                ConnectionStatus.getInstance().setOnNotConnected();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);

                webSocketRef = null;
                ConnectionStatus.getInstance().setOnNotConnected();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }
        };
    }
}
