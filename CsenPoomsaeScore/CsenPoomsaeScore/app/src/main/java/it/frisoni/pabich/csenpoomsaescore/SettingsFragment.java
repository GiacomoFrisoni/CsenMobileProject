package it.frisoni.pabich.csenpoomsaescore;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;

import static android.content.ContentValues.TAG;

public class SettingsFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnSettingsInteraction {
        void onBackClick();
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
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    //Costanti
    private final static String PWD = "1972";

    //Variabili
    private Button btnBack;

    //Database e Shared preferences
    private DbManager dbManager;
    private AppPreferences appPrefs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        dbManager = new DbManager(getActivity());
        appPrefs = new AppPreferences(getActivity());

        //Gestione dell'accessibilità dei controlli
        showCustomDialog();

        //Altri controlli...

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        btnBack = (Button) view.findViewById(R.id.btn_back);

        //Creazione di un listener per intercettare il click sul bottone da parte dell'utente
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBackClick();
                }
            }
        });

        return view;
    }

    /**
     * Metodo del ciclo di vita del fragment che viene richiamato quando lo stesso viene "collegato" ad un'activity.
     *
     * @param context
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
        //Visibilità controlli
    }

    /**
     * Visualizza un alert dialog personalizzato per la gestione della visibilità dei componenti
     * costituenti l'interfaccia.
     */
    private void showCustomDialog() {
        AlertDialog.Builder myDialog
                = new AlertDialog.Builder(getActivity());
        myDialog.setTitle(R.string.check_credentials);

        TextView textView = new TextView(getActivity());
        textView.setText(R.string.enter_password);
        LayoutParams textViewLayoutParams
                = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textViewLayoutParams);

        final EditText editText = new EditText(getActivity());
        LayoutParams editTextLayoutParams
                = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        editText.setLayoutParams(editTextLayoutParams);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(textView);
        layout.addView(editText);

        myDialog.setView(layout);
        myDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                if (editText.getText().toString().equals(PWD)) {
                    showComponents();
                } else {
                    Toast.makeText(getActivity(),R.string.wrong_password, Toast.LENGTH_LONG).show();
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
