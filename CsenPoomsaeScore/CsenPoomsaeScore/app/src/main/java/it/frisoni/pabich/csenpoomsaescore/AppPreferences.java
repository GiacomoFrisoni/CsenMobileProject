package it.frisoni.pabich.csenpoomsaescore;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by giacomofrisoni on 03/04/2017.
 */

public class AppPreferences {

    public static final String BACK_BUTTON_KEY = "backButtonEnabling";
    public static final String BRIGHTNESS_CONTROL_KEY = "brightnessControlValue";

    private static final String APP_SHARED_PREFS = "MyPrefs";

    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public Boolean getKeyPrefsBackButton() {
        return _sharedPrefs.getBoolean(BACK_BUTTON_KEY, false);
    }

    public int getKeyPrefsBrightness() {
        return _sharedPrefs.getInt(BRIGHTNESS_CONTROL_KEY, 100);
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
