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
import it.frisoni.pabich.csenpoomsaescore.utils.server.MyWebSocketListener;
import it.frisoni.pabich.csenpoomsaescore.utils.server.WebSocketHelper;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.AthleteScoreMessage;
import it.frisoni.pabich.csenpoomsaescore.utils.server.messages.MessageTypes;

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

    private MyWebSocketListener webSocketListener;
    private boolean isPendingRequestPresent = false;

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

                // Try to send the message to server
                if (WebSocketHelper.getInstance().sendRequest(MessageTypes.ATHLETE_SCORE, new AthleteScoreMessage(
                        accuracyPoints,
                        presentationPoints,
                        round(accuracyPoints + presentationPoints, N_DECIMAL_PLACES),
                        Calendar.getInstance()))) {

                    // Message sent; waiting for ack
                    progressBar.setVisibility(View.VISIBLE);
                    txvError.setText(getContext().getText(R.string.sending_data_message));
                    txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                    isPendingRequestPresent = true;

                    // Check if ack arrive in time
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);

                                if (isPendingRequestPresent) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    txvError.setText(getContext().getText(R.string.sending_data_error));
                                    txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                                }
                            } catch (Exception e) {}
                        }
                    }).start();

                } else {
                    // Message not sent
                    progressBar.setVisibility(View.INVISIBLE);
                    txvError.setText(getContext().getText(R.string.sending_data_error));
                    txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    isPendingRequestPresent = false;
                }

                // If there is a pending request, disable button, but if failed to send, enable it again!
                btnSendScores.setEnabled(!isPendingRequestPresent);
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

        // WebSocket server listener
        this.webSocketListener = new MyWebSocketListener() {
            @Override
            public void onFailure(String senderIPAddress, String reason) {
                super.onFailure(senderIPAddress, reason);
                ChangeConnectionStatus(WebSocketHelper.ConnectionStatus.NOT_CONNECTED, null, null);
            }

            @Override
            public void onPong(String senderIPAddress, String deviceID) {
                super.onPong(senderIPAddress, deviceID);
                ChangeConnectionStatus(WebSocketHelper.ConnectionStatus.CONNECTED, senderIPAddress, deviceID);
            }

            @Override
            public void onAthleteScoreAckReceived() {
                super.onAthleteScoreAckReceived();
                if (isPendingRequestPresent) {
                    isPendingRequestPresent = false;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            txvError.setText(getContext().getText(R.string.scores_sent));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            btnSendScores.setEnabled(!isPendingRequestPresent);
                        }
                    });

                }
            }
        };

        WebSocketHelper.getInstance().addListener(this.webSocketListener);

        // Set default values to all controls
        txvError.setText("");
        progressBar.setVisibility(View.INVISIBLE);
        btnSendScores.setEnabled(false);
        navBar.setTabletNotConnected();

        if (WebSocketHelper.getInstance().sendPingRequest()) {
            ChangeConnectionStatus(WebSocketHelper.ConnectionStatus.CONNECTING, null, null);
        } else {
            ChangeConnectionStatus(WebSocketHelper.ConnectionStatus.NOT_CONNECTED, null, null);
        }

        return view;
    }

    private void ChangeConnectionStatus(final WebSocketHelper.ConnectionStatus connectionStatus, final String serverName, final String deviceID) {
        if (this.getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (connectionStatus) {
                        case WRONG_INPUT:
                            navBar.setTabletNotConnected();
                            break;
                        case NOT_CONNECTED:
                            txvError.setText(getContext().getText(R.string.tablet_not_connected));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                            progressBar.setVisibility(View.INVISIBLE);
                            btnSendScores.setEnabled(false);
                            navBar.setTabletNotConnected();
                            break;
                        case CONNECTING:
                            txvError.setText(getContext().getText(R.string.tablet_connecting));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                            progressBar.setVisibility(View.VISIBLE);
                            btnSendScores.setEnabled(false);
                            navBar.setTabletConnecting();
                            break;
                        case WAITING_FOP_ACK:
                            txvError.setText(getContext().getText(R.string.tablet_waiting_for_ack));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                            progressBar.setVisibility(View.VISIBLE);
                            btnSendScores.setEnabled(false);
                            navBar.setTabletConnecting();
                            break;
                        case CONNECTED:
                            txvError.setText(getContext().getString(R.string.tablet_connected, serverName));
                            txvError.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                            progressBar.setVisibility(View.INVISIBLE);
                            btnSendScores.setEnabled(true);
                            navBar.setTabletConnected(serverName, deviceID);
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
        WebSocketHelper.getInstance().removeListener(this.webSocketListener);
    }

}

