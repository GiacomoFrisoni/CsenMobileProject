package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by giacomofrisoni on 29/03/2017.
 */

public class MenuFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnMenuInteraction {
        void onStartClick();
        void onScoresClick();
        void onSettingsClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private OnMenuInteraction listener;

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe MenuFragment
     */
    public static MenuFragment newInstance() {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    private Button btnStart;
    private Button btnList;
    private Button btnSettings;

    //Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
    public MenuFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        btnStart = (Button) view.findViewById(R.id.btn_start);
        btnList = (Button) view.findViewById(R.id.btn_list);
        btnSettings = (Button) view.findViewById(R.id.btn_settings);

        //Creazione dei listener per intercettare i click sui bottoni da parte dell'utente
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStartClick();
                }
            }
        });
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onSettingsClick();
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
        if (context instanceof OnMenuInteraction) {
            listener = (OnMenuInteraction) context;
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
}
