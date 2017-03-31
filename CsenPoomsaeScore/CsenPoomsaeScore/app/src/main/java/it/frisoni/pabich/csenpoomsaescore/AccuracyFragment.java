package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by giacomofrisoni on 30/03/2017.
 */

public class AccuracyFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnAccuracyInteraction {
        void onBackClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private AccuracyFragment.OnAccuracyInteraction listener;

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe AccuracyFragment
     */
    public static AccuracyFragment newInstance() {
        AccuracyFragment fragment = new AccuracyFragment();
        return fragment;
    }

    private Button btnBack;

    //Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
    public AccuracyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accuracy, container, false);

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
        if (context instanceof AccuracyFragment.OnAccuracyInteraction) {
            listener = (AccuracyFragment.OnAccuracyInteraction) context;
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
