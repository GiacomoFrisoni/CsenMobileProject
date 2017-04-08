package it.frisoni.pabich.csenpoomsaescore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnMenuInteraction,
        AccuracyFragment.OnAccuracyInteraction, PresentationFragment.OnPresentationInteraction,
        ResultsFragment.OnResultsInteraction, SettingsFragment.OnSettingsInteraction,
        ScoresFragment.OnScoresInteraction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Test db (codice temporaneo)*/
        DbManager dbManager = new DbManager(this);
        dbManager.addAthleteScore(new AthleteScore(4.0, 2.0, 6.0, Calendar.getInstance()));

        String encrypted = AESHelper.encryption("1972");
        String decrypted = AESHelper.decryption(encrypted);

        /**
         * Popolazione del layout con l'istanza di MenuFragment.
         */
        addFragment(MenuFragment.newInstance(), false);
    }

    @Override
    public void onStartClick() {
        /**
         * Replace del fragment nel layout con l'istanza di AccuracyFragment.
         */
        replaceFragment(AccuracyFragment.newInstance(), true);
    }

    @Override
    public void onScoresClick() {
        /**
         * Replace del fragment nel layout con l'istanza di ScoresFragment.
         */
        replaceFragment(ScoresFragment.newInstance(), true);
    }

    @Override
    public void onSettingsClick() {
        /**
         * Replace del fragment nel layout con l'istanza di SettingsFragment.
         */
        replaceFragment(SettingsFragment.newInstance(), true);
    }

    @Override
    public void onBackClick() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        }
    }

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
