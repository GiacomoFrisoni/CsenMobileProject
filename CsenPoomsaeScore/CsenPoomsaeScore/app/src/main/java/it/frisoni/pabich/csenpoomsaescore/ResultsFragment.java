package it.frisoni.pabich.csenpoomsaescore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lb.auto_fit_textview.AutoResizeTextView;

import java.math.BigDecimal;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;

import it.frisoni.pabich.csenpoomsaescore.database.DbManager;
import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;
import it.frisoni.pabich.csenpoomsaescore.utils.AppPreferences;
import it.frisoni.pabich.csenpoomsaescore.utils.ConnectionHelper;
import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;


/**
 * Created by giacomofrisoni on 30/03/2017.
 * <p>
 * Questa classe è dedidata alla visualizzazione dei punteggi ottenuti dall'atleta e del relativo risultato finale.
 */

public class ResultsFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnResultsInteraction {
        void onBackPressed();

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
     * Per il passaggio di parametri si utilizza la classe Bundle.
     * Una volta terminati i dati da inserire, il bundle stesso viene passato al fragment tramite il metodo "setArguments".
     * Questi dati andranno poi recuperati nel metodo "onCreate".
     *
     * @return oggetto di classe ResultsFragment
     */
    public static ResultsFragment newInstance(float accuracyPoints, float presentationPoints) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putFloat("accuracy", accuracyPoints);
        bundle.putFloat("presentation", presentationPoints);
        fragment.setArguments(bundle);
        return fragment;
    }


    //Costanti
    private final static int N_DECIMAL_PLACES = 1;

    //Database and Shared preferences
    private DbManager dbManager;
    private AppPreferences appPrefs;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private float accuracyPoints, presentationPoints;
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
        final float total = round(accuracyPoints + presentationPoints, N_DECIMAL_PLACES);
        txvAccuracy.setText(String.valueOf(accuracyPoints));
        txvPresentation.setText(String.valueOf(presentationPoints));
        txvTotal.setText(String.valueOf(total));

        //Gestione della navbar
        //region NavBarListeners
        //THIS SCREEN SHOULD HAVE ALWAYS A BACK BUTTON
        //if (!appPrefs.getBackButtonKey()) {
        //    navBar.setBackButtonEnabled(false);
        //}
        navBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onBackPressed();
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
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Salvataggio del punteggio nel database
                                    AthleteScore a = new AthleteScore(accuracyPoints, presentationPoints, total, Calendar.getInstance());
                                    dbManager.addAthleteScore(a);
                                    dialog.dismiss();
                                    listener.onMenuClick();
                                    //sendData(a);
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
    /*
    private void sendData(final AthleteScore score) {

        //Ho configurato il server
        if (ConnectionHelper.isConnectionEstabished()) {

            //Controllo se ho ancora la connessione e provo ad inviare il dato
            if (ConnectionHelper.sendMessage(score.getPacketToSend()) && ConnectionHelper.isConnectionAvaiable()) {

                //Se ho la connessione e sono riuscito ad inviarlo
                Toast.makeText(getContext(), R.string.package_sent, Toast.LENGTH_SHORT).show();
                listener.onMenuClick();

            //Se NON ho la connessione e/o NON sono riuscito a spedire
            } else {
                //Mostro un messaggio di errore
                new android.app.AlertDialog.Builder(ResultsFragment.this.getActivity())
                        .setTitle(getString(R.string.attention))
                        .setMessage(getString(R.string.sending_data_error))
                        .setIconAttribute(android.R.attr.alertDialogIcon)

                        //RIPROVO ad inviare i dati, richiamo me stesso
                        .setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                sendData(score);
                            }
                        })

                        //ANNULLO l'invio e vado nella home
                        .setNegativeButton(getString(R.string.continue_to_homepage), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(getContext(), R.string.package_not_sent, Toast.LENGTH_SHORT).show();
                                listener.onMenuClick();
                            }
                        })

                        //TORNO nella schermata dei punteggi
                        .setNeutralButton(getString(R.string.return_to_results), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        //Non ho configurato il server, dunque esco senza dire nulla
        } else {
            listener.onMenuClick();
            Toast.makeText(getContext(), "Server non configurato", Toast.LENGTH_SHORT).show();
        }
    }

    private void simulateSendingData() {
        Thread closeActivity = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ProgressDialog dialog = ProgressDialog.show(ResultsFragment.this.getContext(), getString(R.string.sending_data_title), getString(R.string.sending_data_message), false);
                    Thread.sleep(2000);
                    dialog.dismiss();
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }
            }
        });

        closeActivity.start();
    }*/



    /**
     * Round to certain number of decimals.
     *
     * @param value         number to round
     * @param decimalPlaces number of decimal places
     * @return rounded value
     */
    public static float round(float value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /**
     * Nel metodo "onCreate", vengono recuperati i valori passati nel metodo statico.
     *
     * @param savedInstanceState bundle contenente i dati utili
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            accuracyPoints = round(bundle.getFloat("accuracy"), N_DECIMAL_PLACES);
            presentationPoints = round(bundle.getFloat("presentation"), N_DECIMAL_PLACES);
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
