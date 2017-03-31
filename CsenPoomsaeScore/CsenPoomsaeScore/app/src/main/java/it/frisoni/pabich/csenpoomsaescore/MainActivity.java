package it.frisoni.pabich.csenpoomsaescore;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnMenuInteraction, AccuracyFragment.OnAccuracyInteraction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Popolazione del layout con l'istanza di MenuFragment.
         */
        addFragment(MenuFragment.newInstance(), false);
    }

    @Override
    public void onStartClick() {
        //Replace del fragment nel layout
        replaceFragment(AccuracyFragment.newInstance(), true);
    }

    @Override
    public void onScoresClick() {
        //Verrà inserito il codice per il passaggio al fragment
    }

    @Override
    public void onSettingsClick() {
        //Verrà inserito il codice per il passaggio al fragment
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
