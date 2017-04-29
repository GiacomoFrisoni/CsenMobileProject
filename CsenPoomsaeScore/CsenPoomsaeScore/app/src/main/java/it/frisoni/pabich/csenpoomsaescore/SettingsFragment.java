package it.frisoni.pabich.csenpoomsaescore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;
import android.Manifest;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;
import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;
import static it.frisoni.pabich.csenpoomsaescore.RangeMappingUtilities.map;


/**
 * Created by giacomofrisoni on 30/03/2017.
 *
 * Questa classe è dedidata alla gestione della schermata di impostazioni.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnSettingsInteraction {
        void onMenuClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private SettingsFragment.OnSettingsInteraction listener;

    /**
     * Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
     */
    public SettingsFragment() {
    }

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe SettingsFragment
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }


    //Costanti
    private static final int MAX_BRIGHTNESS = 255;
    private static final int MIN_BRIGHTNESS = 10;
    private static final int WRITE_SETTINGS_PERMISSION = 100;
    private static final int WRITE_SETTINGS_REQUEST = 200;

    //Database e Shared preferences
    private DbManager dbManager;
    private AppPreferences appPrefs;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private RelativeLayout rlBack, rlClearScores;
    private SeekBar skbBrightness;
    private ToggleButton tgbBack;
    private Button btnClearList;

    //Variabile per la gestione dei permessi
    private boolean writeSettingsPermission = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //Inizializzazione delle variabili per la gestione del database e delle SharedPreferences
        dbManager = new DbManager(getActivity());
        appPrefs = new AppPreferences(getActivity());

        //Gestione dell'accessibilità dei controlli
        showCustomDialog();

        //Creazione del listner per la seek bar
        final SeekBar.OnSeekBarChangeListener skbListener = new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                progress = map(value, 0, MAX_BRIGHTNESS, MIN_BRIGHTNESS, MAX_BRIGHTNESS);
                if (writeSettingsPermission) {
                    Log.d("PROGRESS", String.valueOf(progress));
                    android.provider.Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                appPrefs.setBrightnessKey(progress);
            }
        };

        //Creazione dei riferimenti
        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        rlBack = (RelativeLayout) view.findViewById(R.id.rl_back);
        rlClearScores = (RelativeLayout) view.findViewById(R.id.rl_clear_scores);
        skbBrightness = (SeekBar) view.findViewById(R.id.skb_brightness);
        tgbBack = (ToggleButton) view.findViewById(R.id.tgb_back);
        btnClearList = (Button) view.findViewById(R.id.btn_clear_list);

        //Gestione della navbar
        //region NavBarListeners
        navBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMenuClick();
                }
            }
        });
        navBar.setForwardButtonEnabled(false);
        //endregion

        //Gestione della seek bar
        skbBrightness.setMax(MAX_BRIGHTNESS);
        skbBrightness.setOnSeekBarChangeListener(skbListener);
        skbBrightness.setProgress(appPrefs.getBrightnessKey());

        //Gestione del toggle button per l'abilitazione del back
        tgbBack.setChecked(appPrefs.getBackButtonKey());
        tgbBack.setOnClickListener(this);

        //Gestione del pulsante per la cancellazione del db
        btnClearList.setOnClickListener(this);

        /*
         * Interrogazione del sistema circa il permesso di scrittura delle impostazioni.
         */
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
            writeSettingsPermission = true;
        } else {
            /*
             * Nel caso il permesso non risulti garantito, viene richiesto all'utente.
             */
            writeSettingsPermission = false;
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_SETTINGS }, WRITE_SETTINGS_PERMISSION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(getActivity().getApplicationContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getActivity().getPackageName()));
                    startActivityForResult(intent, WRITE_SETTINGS_REQUEST);
                }
            }
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tgb_back:
                appPrefs.setBackButtonKey(tgbBack.isChecked());
                break;
            case R.id.btn_clear_list:
                dbManager.clearAthleteScores();
                Toast.makeText(getActivity(), R.string.cleared_list, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    /**
     * Metodo del ciclo di vita del fragment che viene richiamato quando lo stesso viene "collegato" ad un'activity.
     *
     * @param context activity context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SettingsFragment.OnSettingsInteraction) {
            listener = (SettingsFragment.OnSettingsInteraction) context;
        } else {
            Log.e(TAG, "Not valid context for ScoresFragment");
        }
    }

    /**
     * Quando il fragment viene distrutto, viene eliminato il collegamento con l'activity.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
    }

    /**
     * Rende visibili i componenti per la modifica delle impostazioni.
     */
    private void showComponents() {
        rlBack.setVisibility(View.VISIBLE);
        rlClearScores.setVisibility(View.VISIBLE);
    }

    /**
     * Funzione per poter gestire le risposte dell'utente circa i permessi richiesti.
     *
     * @param requestCode codice di richiesta con il quale è stata effettuata la richiesta
     * @param permissions lista dei permessi richiesti
     * @param grantResults lista delle risposte per i permessi richiesti
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == WRITE_SETTINGS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeSettingsPermission = true;
                Toast.makeText(getActivity(), "Permesso concesso!", Toast.LENGTH_SHORT).show();
            } else {
                writeSettingsPermission = false;
                Toast.makeText(getActivity(), "È necessario concedere il permesso per la regolazione della luminosità!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Visualizza un alert dialog personalizzato per la gestione della visibilità dei componenti
     * costituenti l'interfaccia.
     */
    private void showCustomDialog() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(getActivity());
        myDialog.setTitle(R.string.check_credentials);

        final EditText editText = new EditText(getActivity());
        LayoutParams editTextLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(editTextLayoutParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setHint(R.string.enter_password);

        myDialog.setView(editText);

        myDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                boolean visible = false;
                try {
                    byte[] pw = Base64.decode(appPrefs.getPwSettingsKey(), Base64.NO_WRAP);
                    byte[] dataUser = CipherHandler.getHandler().encryptBytes(editText.getText().toString().getBytes());
                    if (Arrays.equals(pw, dataUser))
                        visible = true;
                } catch (Exception e) {
                    Log.e(TAG, "Error during encryption: " + e.getMessage());
                }
                if (visible) {
                    showComponents();
                } else {
                    Toast.makeText(getActivity(), R.string.wrong_password, Toast.LENGTH_LONG).show();
                }
            }
        });
        myDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //Not used
            }
        });
        myDialog.show();
    }
}
