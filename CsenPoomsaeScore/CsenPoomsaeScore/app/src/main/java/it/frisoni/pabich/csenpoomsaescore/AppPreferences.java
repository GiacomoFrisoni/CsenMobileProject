package it.frisoni.pabich.csenpoomsaescore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by giacomofrisoni on 03/04/2017.
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

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public Boolean getKeyFirstTime() {
        return _sharedPrefs.getBoolean(FIRST_TIME_KEY, true);
    }

    public String getPwSettingsKey() {
        return _sharedPrefs.getString(PW_SETTINGS_KEY, EMPTY_STRING);
    }

    public Boolean getKeyPrefsBackButton() {
        return _sharedPrefs.getBoolean(BACK_BUTTON_KEY, false);
    }

    public int getKeyPrefsBrightness() {
        return _sharedPrefs.getInt(BRIGHTNESS_CONTROL_KEY, 100);
    }

    public void setKeyPrefsFirstTime(Boolean firstTime) {
        _prefsEditor.putBoolean(FIRST_TIME_KEY, firstTime);
        _prefsEditor.commit();
    }

    public void setKeyPrefsPwSettings(String pw) {
        _prefsEditor.putString(PW_SETTINGS_KEY, pw);
        _prefsEditor.commit();
    }

    public void setKeyPrefsBackButton(Boolean mode) {
        _prefsEditor.putBoolean(BACK_BUTTON_KEY, mode);
        _prefsEditor.commit();
    }

    public void setKeyPrefsBrightness(int value) {
        _prefsEditor.putInt(BRIGHTNESS_CONTROL_KEY, value);
        _prefsEditor.commit();
    }
}
