package it.frisoni.pabich.csenpoomsaescore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Arrays;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;

import static android.content.ContentValues.TAG;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnSettingsInteraction {
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

    //Database e Shared preferences
    private DbManager dbManager;
    private AppPreferences appPrefs;

    //Variabili
    private DiscreteSeekBar discreteSeekBar;
    private TextView txvInfoEnabling;
    private ToggleButton tgbBack;
    private Button btnClearList;

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
        final DiscreteSeekBar.OnProgressChangeListener listener = new DiscreteSeekBar.OnProgressChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                progress = value;
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                android.provider.Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
                appPrefs.setKeyPrefsBrightness(progress);
            }
        };

        //Creazione dei riferimenti
        discreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.seek_bar_brightness);
        txvInfoEnabling = (TextView) view.findViewById(R.id.info_text_2);
        tgbBack = (ToggleButton) view.findViewById(R.id.tgb_back);
        btnClearList = (Button) view.findViewById(R.id.btn_clear_list);

        //Gestione della seek bar
        discreteSeekBar.setOnProgressChangeListener(listener);
        discreteSeekBar.setProgress(appPrefs.getKeyPrefsBrightness());

        //Gestione del toggle button per l'abilitazione del back
        tgbBack.setChecked(appPrefs.getKeyPrefsBackButton());

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tgb_back:
                appPrefs.setKeyPrefsBackButton(tgbBack.isChecked());
                break;
            case R.id.btn_clear_list:
                dbManager.clearAthleteScores();
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
        txvInfoEnabling.setVisibility(View.VISIBLE);
        tgbBack.setVisibility(View.VISIBLE);
        btnClearList.setVisibility(View.VISIBLE);
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
