package it.frisoni.pabich.csenpoomsaescore;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import it.frisoni.pabich.csenpoomsaescore.utils.AppPreferences;
import it.frisoni.pabich.csenpoomsaescore.utils.ConnectionHelper;
import it.frisoni.pabich.csenpoomsaescore.utils.VibrationHandler;
import it.frisoni.pabich.csenpoomsaescore.widgets.ResultMenuFragment;

import static android.content.ContentValues.TAG;


/**
 * Questa classe è dedidata alla gestione dell'activity principale in cui vengono mostrati i vari fragment
 * dell'applicazione.
 */

public class MainActivity extends AppCompatActivity implements MenuFragment.OnMenuInteraction,
        AccuracyFragment.OnAccuracyInteraction, PresentationFragment.OnPresentationInteraction,
        ResultsFragment.OnResultsInteraction, SettingsFragment.OnSettingsInteraction,
        ScoresFragment.OnScoresInteraction, ResultMenuFragment.OnResultMenuInteraction {

    //Variabile per la gestione delle SharedPreferences
    private AppPreferences appPrefs;

    //Variabili per la memorizzazione dei punteggi
    private float accuracyPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Inizializza l'oggetto per la gestione delle SharedPreferences.
         */
        appPrefs = new AppPreferences(MainActivity.this);

        /*
         * Inizializza l'oggetto per la gestione della vibrazione.
         */
        VibrationHandler.getHandler().initialize(MainActivity.this);

        /*
         * Salva nelle SharedPreferences la password criptata al primo avvio dell'applicazione.
         */
        if (appPrefs.getFirstTimeKey()) {
            try {
                byte[] pw_bytes = new byte[]{-115, -46, -60, 14, 56, 115, -88, 112, -84, -118, 116, 103, 100, 33, -128, 74};
                appPrefs.setPwSettingsKey(Base64.encodeToString(pw_bytes, Base64.NO_WRAP));
                appPrefs.setFirstTimeKey(false);
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
         * Tornano al menù dalla schermata di results, lo stack viene ripulito per impedire
         * il back.
         */
        FragmentManager manager = getSupportFragmentManager();
        for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
            manager.popBackStack();
        }
        replaceFragment(MenuFragment.newInstance(), false);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onAccuracyClick() {
        /*
         * Replace del fragment nel layout con l'istanza di AccuracyFragment.
         */
        if (ConnectionHelper.isConnectionAvaiable()) {
            if (!ConnectionHelper.isConnectionEstabished()) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.connection_avaiable_message))
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(getBaseContext(), getString(R.string.connection_config_server), Toast.LENGTH_LONG);
                            }
                        })
                        .setNegativeButton(getString(R.string.connection_continue_offline), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                replaceFragment(AccuracyFragment.newInstance(), true);
                                VibrationHandler.getHandler().vibrate();
                            }
                        })
                        .show();
            } else {
                replaceFragment(AccuracyFragment.newInstance(), true);
                VibrationHandler.getHandler().vibrate();
            }

        } else {
            replaceFragment(AccuracyFragment.newInstance(), true);
            VibrationHandler.getHandler().vibrate();
        }


    }

    @Override
    public void onPresentationClick(float accuracyPoints) {
        /*
         * Aggiunta di PresentationFragment.
         */
        this.accuracyPoints = accuracyPoints;
        addFragment(PresentationFragment.newInstance(), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onResultMenuClick(float presentationPoints){
        addFragment(ResultMenuFragment.newInstance(this.accuracyPoints, presentationPoints), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onResultsClick(float presentationPoints) {
        /*
         * Aggiunta di ResultsFragment.
         */
        addFragment(ResultsFragment.newInstance(this.accuracyPoints, presentationPoints), true);
        VibrationHandler.getHandler().vibrate();
    }

    @Override
    public void onBackPressed() {
        final FragmentManager manager = getSupportFragmentManager();
        final Fragment f = manager.findFragmentById(R.id.fragment_container);
        if (appPrefs.getBackButtonKey() || (!(f instanceof PresentationFragment) && !(f instanceof ResultsFragment))) {
            if (f instanceof AccuracyFragment) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.back_accuracy_message))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                manager.popBackStackImmediate();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            } else {
                if (manager.getBackStackEntryCount() > 0) {
                    manager.popBackStackImmediate();
                } else {
                    super.onBackPressed();
                }
            }
        }
        VibrationHandler.getHandler().vibrate();
    }

    protected void addFragment(Fragment fragment, boolean back) {
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        final Fragment f = manager.findFragmentById(R.id.fragment_container);
        if (f != null && !f.isHidden()) {
            transaction.hide(manager.findFragmentById(R.id.fragment_container));
        }
        transaction.add(R.id.fragment_container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    protected void replaceFragment(Fragment fragment, boolean back) {
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.replace(R.id.fragment_container, fragment);
        if (back) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
