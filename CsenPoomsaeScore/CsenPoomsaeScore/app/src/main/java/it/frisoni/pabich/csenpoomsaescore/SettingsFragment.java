package it.frisoni.pabich.csenpoomsaescore;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;

import android.Manifest;


import it.frisoni.pabich.csenpoomsaescore.database.DbManager;
import it.frisoni.pabich.csenpoomsaescore.utils.AppPreferences;
import it.frisoni.pabich.csenpoomsaescore.utils.CipherHandler;
import it.frisoni.pabich.csenpoomsaescore.utils.ConnectionHelper;
import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;
import static it.frisoni.pabich.csenpoomsaescore.utils.RangeMappingUtilities.map;


/**
 * Created by giacomofrisoni on 30/03/2017.
 * <p>
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
    private static final int MIN_BRIGHTNESS = 20;
    private static final int WRITE_SETTINGS_PERMISSION = 100;
    //private static final int WRITE_SETTINGS_REQUEST = 200;
    private static final int PORT = 9050;

    //Database e Shared preferences
    private DbManager dbManager;
    private AppPreferences appPrefs;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private RelativeLayout rlHiddenSettings;
    private SeekBar skbBrightness;
    private ToggleButton tgbBack;
    private Button btnClearList, btnTest;
    private EditText edtIp;
    private TextView textError;


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
                if (hasPermissions(getActivity())) {
                    //Method 1
                    Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                    android.provider.Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);

                    WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                    lp.screenBrightness = (float) progress / 255; //...and put it here
                    getActivity().getWindow().setAttributes(lp);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                appPrefs.setBrightnessKey(progress);

                if (!hasPermissions(getActivity())) {
                    new AlertDialog.Builder(SettingsFragment.this.getActivity())
                            .setTitle(getString(R.string.attention))
                            .setMessage(getString(R.string.ask_write_permission_message))
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    askForPermissions(getActivity());
                                }
                            })
                            .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        };

        //Creazione dei riferimenti
        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        rlHiddenSettings = (RelativeLayout) view.findViewById(R.id.rlt_hidden_settings);
        skbBrightness = (SeekBar) view.findViewById(R.id.skb_brightness);
        tgbBack = (ToggleButton) view.findViewById(R.id.tgb_back);
        btnClearList = (Button) view.findViewById(R.id.btn_clear_list);
        btnTest = (Button) view.findViewById(R.id.btn_test);
        edtIp = (EditText) view.findViewById(R.id.edt_ip);
        textError = (TextView) view.findViewById(R.id.text_error_connection);

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

        //Testing della connessione
        btnTest.setOnClickListener(this);

        if (ConnectionHelper.isConnectionEstabished()) {
            edtIp.setText(ConnectionHelper.getIp());

            if (ConnectionHelper.refreshConnection()) {
                textError.setText("Connected");
                textError.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
            } else {
                textError.setText("Unable to connect");
                textError.setTextColor(Color.RED);
            }
        }

        return view;
    }


    private boolean hasPermissions(Activity context) {
        boolean permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        return permission;
    }

    public void askForPermissions(Activity context) {
        if (!hasPermissions(context)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                startActivityForResult(intent, SettingsFragment.WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, SettingsFragment.WRITE_SETTINGS_PERMISSION);
            }
        }
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
            case R.id.btn_test:
                //Toglie la tastiera
                hideKeyboard(getActivity());

                //Reset della connessione
                ConnectionHelper.resetConnection();

                //Guarda se c'è wifi
                if (ConnectionHelper.isConnectionAvaiable(getActivity())) {
                    //Guarda se IP è corretto
                    if (Patterns.IP_ADDRESS.matcher(edtIp.getText().toString()).matches()) {
                        //Stabilisce la connessione
                        if (ConnectionHelper.establishConnection(edtIp.getText().toString(), PORT)) {
                            //OK pacchetto inviato
                            textError.setText(R.string.package_sent);
                            textError.setTextColor(Color.GREEN);
                        } else {
                            //Qualcosa è andato storto
                            textError.setText(R.string.package_not_sent);
                            textError.setTextColor(Color.RED);
                        }
                    }
                    else {
                        //Indirizzo IP non valido
                        textError.setText(R.string.invalid_ip);
                        textError.setTextColor(Color.RED);
                    }
                }
                else  {
                    //Nessuna connessione
                    textError.setText(R.string.no_connection);
                    textError.setTextColor(Color.RED);
                }
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
        rlHiddenSettings.setVisibility(View.VISIBLE);
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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}