package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

import static android.content.ContentValues.TAG;

/**
 * Created by giacomofrisoni on 30/03/2017.
 */

public class AccuracyFragment extends Fragment implements View.OnClickListener {

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
     * Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
     */
    public AccuracyFragment() {
    }

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

    //Constants
    public final static int VIBR_DURATION = 100;
    public final static double START_POINTS = 4.0;
    public final static double MIN_POINTS = 0.0;
    public final static double MAX_POINTS = START_POINTS;
    public final static double SMALL_PENALTY = 0.3;
    public final static double BIG_PENALTY = 0.1;

    //Counter TextView
    private TextView txvCounter;

    //Buttons
    private Button btnAddBigPenalty;
    private Button btnAddSmallPenalty;
    private Button btnRemoveBigPenalty;
    private Button btnRemoveSmallPenalty;
    private Button btnBack;

    //Counter
    private BigDecimal cur_points;

    //Vibrator
    private Vibrator vibrator;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accuracy, container, false);

        //Settaggio del componente per la vibrazione
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        //txvCounter = (TextView) findViewById(R.id.txv_counter);
        btnBack = (Button) view.findViewById(R.id.btn_back);
        //btnAddBigPenalty = (Button) findViewById(R.id.btn_add_big_penalty);
        //btnAddSmallPenalty = (Button) findViewById(R.id.btn_add_small_penalty);
        //btnRemoveBigPenalty = (Button) findViewById(R.id.btn_remove_big_penalty);
        //btnRemoveSmallPenalty = (Button) findViewById(R.id.btn_remove_small_penalty);

        //Intercettazione dei click sui bottoni da parte dell'utente
        btnAddBigPenalty.setOnClickListener(this);
        btnAddSmallPenalty.setOnClickListener(this);
        btnRemoveBigPenalty.setOnClickListener(this);
        btnRemoveSmallPenalty.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        //Inizializzazione del contatore
        this.cur_points = BigDecimal.valueOf(START_POINTS);
        refreshPoints();

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*
            case R.id.btn_add_big_penalty:
                addPenalty(BIG_PENALTY);
                break;
            case R.id.btn_add_small_penalty:
                addPenalty(SMALL_PENALTY);
                break;
            case R.id.btn_remove_big_penalty:
                removePenalty(BIG_PENALTY);
                break;
            case R.id.btn_remove_small_penalty:
                removePenalty(SMALL_PENALTY);
                break;
                */
            case R.id.btn_back:
                if (listener != null) {
                    listener.onBackClick();
                }
                break;
            default:
                vibrator.vibrate(VIBR_DURATION);
                refreshPoints();
                break;
        }
    }

    private void refreshPoints() {
        txvCounter.setText(cur_points.toString());
    }

    private void addPenalty(double value) {
        cur_points = cur_points.subtract(BigDecimal.valueOf(value));
        cur_points = cur_points.max(BigDecimal.valueOf(MIN_POINTS));
    }

    private void removePenalty(double value) {
        cur_points = cur_points.add(BigDecimal.valueOf(value));
        cur_points = cur_points.min(BigDecimal.valueOf(MAX_POINTS));
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
        } else {
            Log.e(TAG, "Not valid context for AccuracyFragment");
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
