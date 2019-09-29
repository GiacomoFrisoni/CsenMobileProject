package it.frisoni.pabich.csenpoomsaescore.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Calendar;

import it.frisoni.pabich.csenpoomsaescore.R;
import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;
import it.frisoni.pabich.csenpoomsaescore.utils.server.ConnectionStatus;
import it.frisoni.pabich.csenpoomsaescore.utils.server.ConnectionStatusListener;
import it.frisoni.pabich.csenpoomsaescore.utils.server.ConnectionStatuses;
import it.frisoni.pabich.csenpoomsaescore.utils.server.ResponseListener;
import it.frisoni.pabich.csenpoomsaescore.utils.server.WebSocketHelper;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.AthleteScoreMessage;

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

    public ResultMenuFragment() {

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
    private ProgressBar progressBar;

    private ConnectionStatusListener connectionStatusListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result_menu, container, false);

        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        btnSendScores = (Button) view.findViewById(R.id.btn_send_scores);
        btnShowResults = (Button) view.findViewById(R.id.btn_show_results);
        txvError = (TextView) view.findViewById(R.id.txv_error_sending);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);

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
                            .setPositiveButton(getString(R.string.scores_confirm_received), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.onMenuClick();
                                }
                            })
                            .setNegativeButton(getString(R.string.scores_not_confirm_received), new DialogInterface.OnClickListener() {
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
                // Disable button; cannot press it anymore!
                btnSendScores.setEnabled(false);

                if (WebSocketHelper.getInstance().sendRequest(new AthleteScoreMessage(
                                accuracyPoints,
                                presentationPoints,
                                round(accuracyPoints + presentationPoints, N_DECIMAL_PLACES),
                                Calendar.getInstance()), new ResponseListener() {

                            @Override
                            public void onResponse() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        txvError.setText(getContext().getText(R.string.scores_sent));
                                        txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                                        btnSendScores.setEnabled(true);
                                    }
                                });
                            }

                            @Override
                            public void onTimeout() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        txvError.setText(getContext().getText(R.string.sending_data_error));
                                        txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                                        btnSendScores.setEnabled(true);
                                    }
                                });
                            }
                        }
                )) {
                    // Message sent; waiting for ack
                    progressBar.setVisibility(View.VISIBLE);
                    txvError.setText(getContext().getText(R.string.sending_data_message));
                    txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                } else {
                    // Message not sent
                    progressBar.setVisibility(View.INVISIBLE);
                    txvError.setText(getContext().getText(R.string.sending_data_error));
                    txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    btnSendScores.setEnabled(true);
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

        ChangeConnectionStatus(ConnectionStatus.getInstance().getCurrentConnectionStatus());

        this.connectionStatusListener = new ConnectionStatusListener() {
            @Override
            public void onConnectionStatusChanged(final ConnectionStatuses connectionStatus) {
                ChangeConnectionStatus(connectionStatus);
            }
        };

        ConnectionStatus.getInstance().addConnectionStatusListener(connectionStatusListener);


        return view;
    }

    private void ChangeConnectionStatus(final ConnectionStatuses connectionStatus) {
        if (this.getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (connectionStatus) {
                        case NOT_CONNECTED:
                            txvError.setText(getContext().getText(R.string.tablet_not_connected));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            progressBar.setVisibility(View.INVISIBLE);
                            btnSendScores.setEnabled(false);
                            navBar.setOnConnecting();
                            break;
                        case CONNECTING:
                            txvError.setText(getContext().getText(R.string.tablet_connecting));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                            progressBar.setVisibility(View.VISIBLE);
                            btnSendScores.setEnabled(false);
                            navBar.setOnConnecting();
                            break;
                        case CONNECTED:
                            txvError.setText(getContext().getString(R.string.tablet_connected, ConnectionStatus.getInstance().getServerIPAddress()));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            progressBar.setVisibility(View.INVISIBLE);
                            btnSendScores.setEnabled(true);
                            navBar.setOnConnected(ConnectionStatus.getInstance().getServerIPAddress(), ConnectionStatus.getInstance().getServerPort(), ConnectionStatus.getInstance().getDeviceID());
                            break;
                    }
                }
            });
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

        final Bundle bundle = getArguments();
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
        ConnectionStatus.getInstance().removeConnectionStatusListener(this.connectionStatusListener);
    }

}

