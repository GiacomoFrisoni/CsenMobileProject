package it.frisoni.pabich.csenpoomsaescore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by giacomofrisoni on 03/04/2017.
 *
 * Questa classe si occupa di gestire le SharedPreferences dell'applicazione.
 */

public class AppPreferences {

    private static final String EMPTY_STRING = "";

    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String PW_SETTINGS_KEY = "pwSettings";
    private static final String BACK_BUTTON_KEY = "backButtonEnabling";
    private static final String BRIGHTNESS_CONTROL_KEY = "brightnessControlValue";
    private static final String ACCURACY_KEY = "accuracyPoints";
    private static final String PRESENTATION_KEY = "presentationPoints";

    private static final String APP_SHARED_PREFS = "MyPrefs";

    private SharedPreferences sharedPrefs;

    /**
     * All'interno del costruttore vengono recuperate le SharedPreferences mediante il metodo di sistema.
     * La modalità di accesso usata è PRIVATE: solo l'applicazione corrente potrà quindi accedere a tali
     * informazioni.
     *
     * @param context
     *      il contesto dell'applicazione
     */
    public AppPreferences(Context context) {
        this.sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
    }

    /**
     * @return true se è la prima volta che si avvia l'applicazione, false altrimenti.
     */
    public Boolean getFirstTimeKey() {
        return sharedPrefs.getBoolean(FIRST_TIME_KEY, true);
    }

    /**
     * @return la password criptata per l'accesso alle impostazioni avanzate.
     */
    public String getPwSettingsKey() {
        return sharedPrefs.getString(PW_SETTINGS_KEY, EMPTY_STRING);
    }

    /**
     * @return true se è abilitata la possibilità di poter compiere modifiche su punteggi inseriti,
     * false altrimenti.
     */
    public Boolean getBackButtonKey() {
        return sharedPrefs.getBoolean(BACK_BUTTON_KEY, false);
    }

    /**
     * @return il valore percentuale di luminosità desiderata.
     */
    public int getBrightnessKey() {
        return sharedPrefs.getInt(BRIGHTNESS_CONTROL_KEY, 100);
    }


    /**
     * @return il corrente punteggio di accuratezza salvato.
     */
    public float getAccuracyKey() {
        return sharedPrefs.getFloat(ACCURACY_KEY, 0);
    }

    /**
     * @return il corrente punteggio di presentazione salvato.
     */
    public float getPresentationKey() {
        return sharedPrefs.getFloat(PRESENTATION_KEY, 0);
    }

    /**
     * Imposta una variabile booleana indicante se è la prima volta o meno che si avvia l'applicazione.
     * @param firstTime
     *      valore indicante il primo avvio
     */
    public void setFirstTimeKey(Boolean firstTime) {
        sharedPrefs.edit().putBoolean(FIRST_TIME_KEY, firstTime).apply();
    }

    /**
     * Imposta la password criptata per l'accesso alle impostazioni avanzate.
     * @param pw
     *      password
     */
    public void setPwSettingsKey(String pw) {
        sharedPrefs.edit().putString(PW_SETTINGS_KEY, pw).apply();
    }

    /**
     * Imposta la possibilità o meno di poter applicare correzioni a punteggi inseriti.
     * @param mode
     *      abilitazione modalità back
     */
    public void setBackButtonKey(Boolean mode) {
        sharedPrefs.edit().putBoolean(BACK_BUTTON_KEY, mode).apply();
    }

    /**
     * Imposta il valore di regolazione desiderato per la luminosità dello schermo.
     * @param value
     *      percentuale di luminosità
     */
    public void setBrightnessKey(int value) {
        sharedPrefs.edit().putInt(BRIGHTNESS_CONTROL_KEY, value).apply();
    }

    /**
     * Imposta il punteggio di accuratezza corrente.
     * @param value
     *      punteggio
     */
    public void setAccuracyKey(float value) {
        sharedPrefs.edit().putFloat(ACCURACY_KEY, value).apply();
    }

    /**
     * Imposta il punteggio di presentazione corrente.
     * @param value
     *      punteggio
     */
    public void setPresentationKey(float value) {
        sharedPrefs.edit().putFloat(PRESENTATION_KEY, value).apply();
    }
}
