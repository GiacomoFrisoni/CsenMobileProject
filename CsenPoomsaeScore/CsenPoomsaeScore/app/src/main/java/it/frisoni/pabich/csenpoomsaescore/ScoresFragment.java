package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;
import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;
import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;


/**
 * Created by giacomofrisoni on 30/03/2017.
 *
 * Questa classe è dedidata alla visualizzazione dei punteggi di gara memorizzati.
 */

public class ScoresFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnScoresInteraction {
        void onMenuClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private ScoresFragment.OnScoresInteraction listener;

    /**
     * Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
     */
    public ScoresFragment() {
    }

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe ScoresFragment
     */
    public static ScoresFragment newInstance() {
        return new ScoresFragment();
    }

    //Database
    private DbManager dbManager;

    //Adapter
    private ScoreAdapter adapter;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private ListView lsvScores;
    private TextView txvPlaceholder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scores, container, false);

        //Inizializzazione della variabile per la gestione del database
        dbManager = new DbManager(getActivity());

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        lsvScores = (ListView) view.findViewById(R.id.lsv_scores);
        txvPlaceholder = (TextView) view.findViewById(R.id.txv_placeholder);

        //Caricamento dei dati nella lista dei punteggi
        populateListView();

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

        return view;
    }

    /**
     * Metodo per la popolazione inziale della lista dei punteggi.
     */
    private void populateListView() {
        List<AthleteScore> scores = dbManager.getAthleteScores();
        adapter = new ScoreAdapter(getActivity(), scores);
        lsvScores.setAdapter(adapter);

        if (scores.size() > 0) {
            txvPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            txvPlaceholder.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Metodo per l'aggiornamento dei dati mostrati dalla lista.
     */
    public void updateListView() {
        List<AthleteScore> scores = dbManager.getAthleteScores();
        adapter.clear();
        adapter.addAll(scores);
        adapter.notifyDataSetChanged();

        if (scores.size() > 0) {
            txvPlaceholder.setVisibility(View.INVISIBLE);
        } else {
            txvPlaceholder.setVisibility(View.VISIBLE);
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
        if (context instanceof ScoresFragment.OnScoresInteraction) {
            listener = (ScoresFragment.OnScoresInteraction) context;
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
}
