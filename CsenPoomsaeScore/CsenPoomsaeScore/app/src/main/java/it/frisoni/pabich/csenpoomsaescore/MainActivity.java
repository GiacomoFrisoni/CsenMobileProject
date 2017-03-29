package it.frisoni.pabich.csenpoomsaescore;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnMenuInteraction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Popolazione del layout con l'istanza di MenuFragment.
         */
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        MenuFragment fragment = MenuFragment.newInstance();
        transaction.add(R.id.activity_main, fragment);
        transaction.commit();
    }

    @Override
    public void onStartClick() {
        //Verrà inserito il codice per il passaggio all'altro fragment
    }
}
