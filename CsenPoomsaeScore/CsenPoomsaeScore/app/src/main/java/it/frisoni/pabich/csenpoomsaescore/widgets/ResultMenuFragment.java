package it.frisoni.pabich.csenpoomsaescore.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.Calendar;

import it.frisoni.pabich.csenpoomsaescore.PresentationFragment;
import it.frisoni.pabich.csenpoomsaescore.R;
import it.frisoni.pabich.csenpoomsaescore.ResultsFragment;
import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;
import it.frisoni.pabich.csenpoomsaescore.utils.AppPreferences;
import it.frisoni.pabich.csenpoomsaescore.utils.ConnectionHelper;

import static android.content.ContentValues.TAG;

/**
 * Created by marti on 29/08/2017.
 */

public class ResultMenuFragment extends Fragment {

    public interface OnResultMenuInteraction {
        void onResultsClick(float presentationPoints, boolean isReadOnly);
        void onMenuClick();
        void onBackPressed();
    }

    private ResultMenuFragment.OnResultMenuInteraction listener;

    public ResultMenuFragment(){

    }

    public static ResultMenuFragment newInstance(AthleteScore a) {
        ResultMenuFragment fragment = new ResultMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putFloat("accuracy", a.getAccuracy());
        bundle.putFloat("presentation", a.getPresentation());
        fragment.setArguments(bundle);
        return fragment;
    }

    //Saved scores
    private final static int N_DECIMAL_PLACES = 1;
    private float accuracyPoints, presentationPoints;

    //Controls
    private Button btnSendScores, btnShowResults;
    private TextView txvError;
    private CustomNavBar navBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_menu, container, false);

        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        btnSendScores = (Button) view.findViewById(R.id.btn_send_scores);
        btnShowResults = (Button) view.findViewById(R.id.btn_show_results);
        txvError = (TextView) view.findViewById(R.id.txv_error_sending);

        navBar.setBackButtonEnabled(false);
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
                    new AlertDialog.Builder(ResultMenuFragment.this.getActivity())
                            .setTitle(getString(R.string.confirm))
                            .setMessage(getString(R.string.end_connection_message))
                            .setIconAttribute(android.R.attr.alertDialogIcon)
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
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


        btnSendScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendScores(new AthleteScore(accuracyPoints, presentationPoints, round(accuracyPoints + presentationPoints, N_DECIMAL_PLACES), Calendar.getInstance()))) {
                    txvError.setText(getString(R.string.scores_sent));
                    txvError.setTextColor(Color.GREEN);
                }
                else {
                    txvError.setText(getString(R.string.scores_not_sent));
                    txvError.setTextColor(Color.RED);
                }
            }
        });

        btnShowResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onResultsClick(presentationPoints, true);
                }
            }
        });


        return view;
    }


    private boolean sendScores(final AthleteScore score) {
        //Ho configurato il server
        if (ConnectionHelper.isConnectionEstabished()) {

            //Controllo se ho ancora la connessione e provo ad inviare il dato
            if (ConnectionHelper.sendMessage(score.getPacketToSend()) && ConnectionHelper.isConnectionAvaiable()) {

                //Se ho la connessione e sono riuscito ad inviarlo
                Toast.makeText(getContext(), R.string.package_sent, Toast.LENGTH_SHORT).show();
                return true;

                //Se NON ho la connessione e/o NON sono riuscito a spedire
            } else {
                Toast.makeText(getContext(), R.string.package_not_sent, Toast.LENGTH_SHORT).show();
                return false;
            }

            //Non ho configurato il server, dunque esco senza dire nulla
        } else {
            Toast.makeText(getContext(), "Server non configurato", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



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
        if (context instanceof ResultMenuFragment.OnResultMenuInteraction) {
            listener = (ResultMenuFragment.OnResultMenuInteraction) context;
        } else {
            Log.e(TAG, "Not valid context for PresentationFragment");
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

