package it.frisoni.pabich.csenpoomsaescore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import java.util.Calendar;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnMenuInteraction,
        AccuracyFragment.OnAccuracyInteraction, PresentationFragment.OnPresentationInteraction,
        ResultsFragment.OnResultsInteraction, SettingsFragment.OnSettingsInteraction,
        ScoresFragment.OnScoresInteraction {

    //Variabile per la gestione delle SharedPreferences
    private AppPreferences appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appPrefs = new AppPreferences(MainActivity.this);

        /*
         * Inizializza l'oggetto per la gestione della vibrazione.
         */
        VibrationHandler.getHandler().initialize(MainActivity.this);

        /*
         * Test db (codice temporaneo)
         */
        DbManager dbManager = new DbManager(this);
        dbManager.addAthleteScore(new AthleteScore(4.0, 2.0, 6.0, Calendar.getInstance()));

        /*
         * Salva nelle SharedPreferences la password criptata al primo avvio dell'applicazione.
         */
        if (appPrefs.getFirstTimeKey()) {
            try {
                byte[] pw_bytes = new byte[] { -115, -46, -60, 14, 56, 115, -88, 112, -84, -118, 116, 103, 100, 33, -128, 74 };
                appPrefs.setKeyPrefsPwSettings(Base64.encodeToString(pw_bytes, Base64.NO_WRAP));
                appPrefs.setKeyPrefsFirstTime(false);
            } catch (Exception e) {
                Log.e(TAG, "Error during encryption: " + e.getMessage());
            }
        }

        /*
         * Popolazione del layout con l'istanza di MenuFragment.
         */
        addFragment(MenuFragment.newInstance(), false);
    }

    @Override
    public void onStartClick() {
        /*
         * Replace del fragment nel layout con l'istanza di AccuracyFragment.
         */
        replaceFragment(AccuracyFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onScoresClick() {
        /*
         * Replace del fragment nel layout con l'istanza di ScoresFragment.
         */
        replaceFragment(ScoresFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onSettingsClick() {
        /*
         * Replace del fragment nel layout con l'istanza di SettingsFragment.
         */
        replaceFragment(SettingsFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onMenuClick() {
        /*
         * Replace del fragment nel layout con l'istanza di MenuFragment.
         */
        replaceFragment(MenuFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onAccuracyClick() {
        /*
         * Replace del fragment nel layout con l'istanza di AccuracyFragment.
         */
        replaceFragment(AccuracyFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onPresentationClick() {
        /*
         * Replace del fragment nel layout con l'istanza di PresentationFragment.
         */
        replaceFragment(PresentationFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onResultsClick() {
        /*
         * Replace del fragment nel layout con l'istanza di ResultsFragment.
         */
        replaceFragment(ResultsFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    /*
    @Override
    public void onBackClick() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        }
    }*/

    protected void addFragment(Fragment fragment, boolean back) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.fragment_container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    protected void replaceFragment(Fragment fragment, boolean back) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
