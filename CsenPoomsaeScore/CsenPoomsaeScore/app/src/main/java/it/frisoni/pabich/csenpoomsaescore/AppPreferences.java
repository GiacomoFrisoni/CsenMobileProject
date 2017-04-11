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

    private static final String APP_SHARED_PREFS = "MyPrefs";

    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    /**
     * All'interno del costruttore vengono recuperate le SharedPreferences mediante il metodo di sistema.
     * La modalità di accesso usata è PRIVATE: solo l'applicazione corrente potrà quindi accedere a tali
     * informazioni.
     *
     * @param context
     *      il contesto dell'applicazione
     */
    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    /**
     * @return true se è la prima volta che si avvia l'applicazione, false altrimenti.
     */
    public Boolean getFirstTimeKey() {
        return _sharedPrefs.getBoolean(FIRST_TIME_KEY, true);
    }

    /**
     * @return la password criptata per l'accesso alle impostazioni avanzate.
     */
    public String getPwSettingsKey() {
        return _sharedPrefs.getString(PW_SETTINGS_KEY, EMPTY_STRING);
    }

    /**
     * @return true se è abilitata la possibilità di poter compiere modifiche su punteggi inseriti,
     * false altrimenti.
     */
    public Boolean getKeyPrefsBackButton() {
        return _sharedPrefs.getBoolean(BACK_BUTTON_KEY, false);
    }

    /**
     * @return il valore percentuale di luminosità desiderata.
     */
    public int getKeyPrefsBrightness() {
        return _sharedPrefs.getInt(BRIGHTNESS_CONTROL_KEY, 100);
    }

    /**
     * Imposta una variabile booleana indicante se è la prima volta o meno che si avvia l'applicazione.
     * @param firstTime
     *      valore indicante il primo avvio
     */
    public void setKeyPrefsFirstTime(Boolean firstTime) {
        _prefsEditor.putBoolean(FIRST_TIME_KEY, firstTime);
        _prefsEditor.commit();
    }

    /**
     * Imposta la password criptata per l'accesso alle impostazioni avanzate.
     * @param pw
     *      password
     */
    public void setKeyPrefsPwSettings(String pw) {
        _prefsEditor.putString(PW_SETTINGS_KEY, pw);
        _prefsEditor.commit();
    }

    /**
     * Imposta la possibilità o meno di poter applicare correzioni a punteggi inseriti.
     * @param mode
     *      abilitazione modalità back
     */
    public void setKeyPrefsBackButton(Boolean mode) {
        _prefsEditor.putBoolean(BACK_BUTTON_KEY, mode);
        _prefsEditor.commit();
    }

    /**
     * Imposta il valore di regolazione desiderato per la luminosità dello schermo.
     * @param value
     *      percentuale di luminosità
     */
    public void setKeyPrefsBrightness(int value) {
        _prefsEditor.putInt(BRIGHTNESS_CONTROL_KEY, value);
        _prefsEditor.commit();
    }
}
