package it.frisoni.pabich.csenpoomsaescore.utils.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.AckMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.EmptyMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.MessageTypes;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.StringMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.WebSocketMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.WebSocketMessageData;
import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketHelper {

    // Static variable to access the Singleton class
    private static WebSocketHelper webSocketHelper;

    // List of listeners attached to WebSocketHelper that will be informed when some message arrives
    private List<MyWebSocketListener> myWebSocketListeners;



    // Reference to WebSocket, when opened (can be null)
    private WebSocket webSocketRef;

    // Reference to IP, PORT and DeviceID, when server is reached
    private String ipAddress, port;

    private MyDevice myDevice;

    // Reference to gson converter
    private Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private WebSocketHelper() {
        this.myDevice = new MyDevice();
        this.myWebSocketListeners = new ArrayList<>();
        this.myWebSocketListeners.add(new MyWebSocketListener() {
            @Override
            public void onOpen(String senderIPAddress) {
                super.onOpen(senderIPAddress);
                System.out.println("OnOpen was called");
            }

            @Override
            public void onMessage(String senderIPAddress, String message) {
                super.onMessage(senderIPAddress, message);
                System.out.println("Message received from " + senderIPAddress + ": " + message);
            }

            @Override
            public void onMessage(String senderIPAddress, ByteString message) {
                super.onMessage(senderIPAddress, message);
            }

            @Override
            public void onFailure(String senderIPAddress, String reason) {
                super.onFailure(senderIPAddress, reason);
                System.out.println("Something failed " + senderIPAddress + ": " + reason);
            }

            @Override
            public void onClosing(String senderIPAddress, String reason) {
                super.onClosing(senderIPAddress, reason);
                System.out.println("Closing... " + senderIPAddress + ": " + reason);
            }

            @Override
            public void onClosed(String senderIPAddress, String reason) {
                super.onClosed(senderIPAddress, reason);
                System.out.println("Closed! " + senderIPAddress + ": " + reason);
            }

            @Override
            public void onPairingRequestSent(String senderIPAddress) {
                System.out.println("Pairing request sent from " + senderIPAddress);
            }

            @Override
            public void onSetDeviceIDReceived(String senderIPAddress) {
                System.out.println("SetDeviceID Received from " + senderIPAddress);
            }

            @Override
            public void onSetDeviceIDAckReceived(String senderIPAddress, String deviceID) {
                System.out.println("SetDeviceID Ack Received from " + senderIPAddress);
            }
        });
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
        this.ipAddress = ipAddress;
        this.port = port;

        // Create the client and the request call for the (insecure) WebSocket (secure = wss)
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url("ws://" + this.ipAddress + ":" + this.port).build();

        // After creating the listener, make the request for the WebSocket, passing the listener on.
        this.webSocketRef = client.newWebSocket(request, this.createWebSocketListener());
        client.dispatcher().executorService().shutdown();
    }

    /**
     * Sends over WebSocket a generic message with data
     * @param messageType type of the message
     * @param data data of the message
     * @return TRUE if server was configured to send messages, FALSE otherwise
     */
    public <D extends WebSocketMessageData> boolean sendRequest(MessageTypes messageType, D data) {
        if (webSocketRef != null && this.myDevice.getStatus() == MyDeviceStatus.CONNECTED) {
            webSocketRef.send(new WebSocketMessage<D>(messageType, this.myDevice.getDeviceID(), data).toJson());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends over WebSocket a PING request
     * @return TRUE if server was configured to send messages, FALSE otherwise
     */
    public boolean sendPingRequest() {
        return sendRequest(MessageTypes.PING, new EmptyMessage());
    }

    /**
     * Add a listener to the WebSocket. When some message will arrive, listener will be informed.
     * Remember to REMOVE THE LISTENER to avoid poor app performance! Best practice: save the listener
     * into a class variable, so you can add and remove it easily.
     * @param listener ad implemented webSocketListener
     */
    public void addListener(MyWebSocketListener listener){
        this.myWebSocketListeners.add(listener);
    }

    /**
     * Remove a listener from the WebSocket.
     * @param listener listener to remove.
     */
    public void removeListener(MyWebSocketListener listener){
        this.myWebSocketListeners.remove(listener);
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

                for (MyWebSocketListener listener : myWebSocketListeners) {
                    listener.onOpen(ipAddress);
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);

                // Inform all listeners that I received something
                for (MyWebSocketListener listener : myWebSocketListeners) {
                    listener.onMessage(ipAddress, text);
                }

                System.out.println("Listeners registered: " + myWebSocketListeners.size());

                WebSocketMessage<StringMessage> message = gson.fromJson(text, new TypeToken<WebSocketMessage<StringMessage>>(){}.getType());

                // Switch on message type
                switch ((message.getMessageType())) {
                    //----------------------------------------------------------------------------------------------------------------------------- SET_DEVICE_ID
                    case SET_DEVICE_ID:
                        // Save it
                        myDevice.setAsConnectionPending();

                        // Send an ack to server: yeah dude, I gotcha this!
                        webSocketRef.send(new WebSocketMessage<>(MessageTypes.ACK, message.getDeviceID(), new AckMessage(MessageTypes.SET_DEVICE_ID, message.getDeviceID())).toJson());

                        // Inform all the listeners I got the DeviceID (but I'm not ready yet)
                        for (MyWebSocketListener listener : myWebSocketListeners) {
                            listener.onSetDeviceIDReceived(ipAddress);
                        }

                        // Set a timeout: if ack will not be received, tablet will not be connected!
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(7000);
                                    if (myDevice.getStatus() != MyDeviceStatus.CONNECTED) {
                                        for (MyWebSocketListener listener : myWebSocketListeners) {
                                            listener.onSetDeviceIDFailed();
                                        }
                                    }
                                } catch (Exception e) { }
                            }
                        }).start();

                        break;

                    //----------------------------------------------------------------------------------------------------------------------------- REJECT_DEVICE
                    case REJECT_DEVICE:
                        myDevice.setAsNotConnected();
                        webSocketRef.send(new WebSocketMessage<>(MessageTypes.ACK, message.getDeviceID(), new AckMessage(MessageTypes.REJECT_DEVICE, message.getDeviceID())).toJson());

                        // Inform all the listeners I got the DeviceID (but I'm not ready yet)
                        for (MyWebSocketListener listener : myWebSocketListeners) {
                            listener.onSetDeviceIDFailed();
                        }

                        break;

                    //----------------------------------------------------------------------------------------------------------------------------- ACK
                    case ACK:

                        final WebSocketMessage<AckMessage> ackMessage = gson.fromJson(text, new TypeToken<WebSocketMessage<AckMessage>>(){}.getType());

                        switch (ackMessage.getData().getAckType()) {
                            case SET_DEVICE_ID:
                                if (myDevice.getStatus() == MyDeviceStatus.PENDING) {
                                    myDevice.setAsConnected(ackMessage.getDeviceID());

                                    for (MyWebSocketListener listener : myWebSocketListeners) {
                                        listener.onSetDeviceIDAckReceived(ipAddress, myDevice.getDeviceID());
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                        break;

                    //----------------------------------------------------------------------------------------------------------------------------- PONG
                    case PONG:
                        // Inform all the listeners I got the DeviceID (but I'm not ready yet)
                        for (MyWebSocketListener listener : myWebSocketListeners) {
                            listener.onPong(ipAddress, myDevice.getDeviceID());
                        }
                        break;

                    //----------------------------------------------------------------------------------------------------------------------------- PING
                    case PING:
                        // Send a PONG to server: yeah dude, I gotcha this!
                        webSocketRef.send(new WebSocketMessage<>(MessageTypes.PONG, message.getDeviceID(), new EmptyMessage()).toJson());
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);

                for (MyWebSocketListener listener : myWebSocketListeners) {
                    listener.onMessage(ipAddress, bytes);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);

                for (MyWebSocketListener listener : myWebSocketListeners) {
                    listener.onClosed(ipAddress, reason);
                }

                // DO NOT SET webSocketRef = null;
                // onClosed must be triggered yet!
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);

                for (MyWebSocketListener listener : myWebSocketListeners) {
                    listener.onClosed(ipAddress, reason);
                }

                webSocketRef = null;
                myDevice.setAsNotConnected();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);

                for (MyWebSocketListener listener : myWebSocketListeners) {
                    listener.onFailure(ipAddress, t.getMessage());
                }
            }
        };
    }

    public static MyWebSocketListener setNavBarListener(final CustomNavBar customNavBar) {
        final MyWebSocketListener webSocketListener = new MyWebSocketListener() {
            @Override
            public void onFailure(String senderIPAddress, String reason) {
                super.onFailure(senderIPAddress, reason);
                customNavBar.setTabletNotConnected();
            }

            @Override
            public void onPong(String senderIPAddress, String deviceID) {
                super.onPong(senderIPAddress, deviceID);
                customNavBar.setTabletConnected(senderIPAddress, deviceID);
            }
        };

        if (WebSocketHelper.getInstance().sendPingRequest()) {
            customNavBar.setTabletConnecting();
        } else {
            customNavBar.setTabletNotConnected();
        }

        WebSocketHelper.getInstance().addListener(webSocketListener);

        return webSocketListener;
    }

    public enum ConnectionStatus {
        WRONG_INPUT, NOT_CONNECTED, CONNECTING, WAITING_FOP_ACK, CONNECTED
    }
}
