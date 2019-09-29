package it.frisoni.pabich.csenpoomsaescore.utils.server;

import android.support.v4.util.Pair;

public class WebSocketLogger {

    private static final String PREFIX = "[SERVER] ";
    private static boolean isLogEnabled = true;

    @SafeVarargs
    public static void Log(final String message, final Pair<String, String>... params) {
        if (isLogEnabled) {
            String msg = PREFIX;
            msg = msg.concat(message).concat("\n");

            if (params != null) {
                if (params.length > 0) {
                    for (Pair<String, String> s : params) {
                        msg = msg.concat(s.first.concat(": ").concat(s.second.concat(", ")));
                    }

                    msg = msg.substring(0, msg.length() - 2);
                }
            }

            System.out.println(msg);
        }
    }

    public static void enableLogging() {
        isLogEnabled = true;
    }

    public static void disableLoggin() {
        isLogEnabled = false;
    }
}
