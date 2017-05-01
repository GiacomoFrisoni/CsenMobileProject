package it.frisoni.pabich.csenpoomsaescore.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by giacomofrisoni on 11/04/2017.
 *
 * Questa classe utilizza il pattern Singleton per gestire la vibrazione.
 */

public class VibrationHandler {

    private final static int VIBR_DURATION = 50;

    private static volatile VibrationHandler singleton;

    private Vibrator vibrator;

    private VibrationHandler() { }

    /**
     * Questo metodo restituisce un VibrationHandler.
     * Se il VibrationHandler è nullo, viene creato alla prima chiamata.
     * In questo modo le risorse vengono allocate solo se necessario.
     *
     * @return il VibrationHandler.
     */
    public static VibrationHandler getHandler() {
        if (singleton == null) {
            synchronized (VibrationHandler.class) {
                if (singleton == null) {
                    singleton = new VibrationHandler();
                }
            }
        }
        return singleton;
    }

    /**
     * Questo metodo inizializza la variabile per la gestione della vibrazione
     * nell'ambito dell'activity specificata.
     *
     * @param activity
     *      l'activity in cui gestire la vibrazione
     */
    public void initialize(final Activity activity) {
        if (activity != null) {
            vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    /**
     * Avvia una vibrazione di durata prefissata.
     */
    public void vibrate() {
        if (vibrator != null) {
            vibrator.vibrate(VIBR_DURATION);
        } else {
            Log.e(TAG, "Vibrator not initialized");
        }
    }
}
