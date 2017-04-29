package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lb.auto_fit_textview.AutoResizeTextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;
import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;


/**
 * Created by giacomofrisoni on 30/03/2017.
 *
 * Questa classe è dedidata alla visualizzazione dei punteggi ottenuti dall'atleta e del relativo risultato finale.
 */

public class ResultsFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnResultsInteraction {
        void onPresentationClick();
        void onMenuClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private ResultsFragment.OnResultsInteraction listener;

    /**
     * Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
     */
    public ResultsFragment() {
    }

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe ResultsFragment
     */
    public static ResultsFragment newInstance() {
        return new ResultsFragment();
    }


    //Costanti
    private final static int N_DECIMAL_PLACES = 1;

    //Database and Shared preferences
    private DbManager dbManager;
    private AppPreferences appPrefs;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private AutoResizeTextView txvAccuracy, txvPresentation, txvTotal;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        //Inizializzazione della variabile per la gestione del database
        dbManager = new DbManager(ResultsFragment.this.getActivity());

        //Inizializzazione della variabile per la gestione delle shared preferences
        appPrefs = new AppPreferences(getActivity());

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        txvAccuracy = (AutoResizeTextView) view.findViewById(R.id.txv_accuracy);
        txvPresentation = (AutoResizeTextView) view.findViewById(R.id.txv_presentation);
        txvTotal = (AutoResizeTextView) view.findViewById(R.id.txv_total);

        //Inizializzazione dei campi testuali per la visualizzazione dei risultati
        final float accuracy_points = round(appPrefs.getAccuracyKey(), N_DECIMAL_PLACES);
        final float presentation_points = round(appPrefs.getPresentationKey(),N_DECIMAL_PLACES);
        final float total = round(accuracy_points + presentation_points, N_DECIMAL_PLACES);
        txvAccuracy.setText(String.valueOf(accuracy_points));
        txvPresentation.setText(String.valueOf(presentation_points));
        txvTotal.setText(String.valueOf(total));

        //Gestione della navbar
        //region NavBarListeners
        navBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && appPrefs.getBackButtonKey()) {
                    listener.onPresentationClick();
                }
            }
        });
        navBar.getForwardButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    new AlertDialog.Builder(ResultsFragment.this.getActivity())
                            .setTitle(getString(R.string.confirm))
                            .setMessage(getString(R.string.end_valutation_message))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Salvataggio del punteggio nel database
                                    dbManager.addAthleteScore(new AthleteScore(accuracy_points, presentation_points, total, Calendar.getInstance()));
                                    listener.onMenuClick();
                                }
                            })
                            .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
            }
        });
        //endregion

        return view;
    }

    /**
     * Round to certain number of decimals.
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * Metodo del ciclo di vita del fragment che viene richiamato quando lo stesso viene "collegato" ad un'activity.
     *
     * @param context activity context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResultsFragment.OnResultsInteraction) {
            listener = (ResultsFragment.OnResultsInteraction) context;
        } else {
            Log.e(TAG, "Not valid context for ResultsFragment");
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
