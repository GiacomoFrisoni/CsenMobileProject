package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import it.frisoni.pabich.csenpoomsaescore.utils.server.ConnectionStatus;
import it.frisoni.pabich.csenpoomsaescore.utils.server.ConnectionStatusListener;
import it.frisoni.pabich.csenpoomsaescore.utils.server.ConnectionStatuses;

import static android.content.ContentValues.TAG;

/**
 * Created by giacomofrisoni on 29/03/2017.
 *
 * Questa classe è dedidata alla gestione del menù da cui è possibile accedere alle varie
 * funzionalità messe a disposizione dall'applicazione.
 */

public class MenuFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnMenuInteraction {
        void onStartNewEvaluation();

        void onScoresClick();

        void onSettingsClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private OnMenuInteraction listener;

    /**
     * Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
     */
    public MenuFragment() {
    }

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe MenuFragment
     */
    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    //Titolo
    private TextView txvTitle, txvTabletConnectionStatus, txvAppVersion;

    //Bottoni di interazione
    private Button btnStart, btnList, btnSettings;

    private ConnectionStatusListener connectionStatusListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        txvTitle = (TextView) view.findViewById(R.id.txv_title);
        btnStart = (Button) view.findViewById(R.id.btn_start);
        btnList = (Button) view.findViewById(R.id.btn_list);
        btnSettings = (Button) view.findViewById(R.id.btn_settings);
        txvTabletConnectionStatus = (TextView) view.findViewById(R.id.txv_tablet_connection_status);
        txvAppVersion = (TextView) view.findViewById(R.id.txv_app_version);

        // Set version name
        txvAppVersion.setText(BuildConfig.VERSION_NAME);

        String title = txvTitle.getText().toString();
        SpannableString spanString = new SpannableString(title);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, 4, 0);
        spanString.setSpan(new StyleSpan(Typeface.ITALIC), 4, spanString.length(), 0);
        txvTitle.setText(spanString);

        //Imposta il font personalizzato per il titolo
        //Typeface custom_font = Typeface.createFromAsset(getActivity().getAssets(),  "fonts/go3v2.ttf");
        //txvTitle.setTypeface(custom_font);

        //Creazione dei listener per l'intercettazione dei click sui bottoni da parte dell'utente
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStartNewEvaluation();
                }
            }
        });
        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScoresClick();
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

    /**
     * Metodo del ciclo di vita del fragment che viene richiamato quando lo stesso viene "collegato" ad un'activity.
     *
     * @param context activity context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (listener == null) {
            if (context instanceof OnMenuInteraction) {
                listener = (OnMenuInteraction) context;
            } else {
                Log.e(TAG, "Not valid context for MenuFragment");
            }
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

    private void ChangeConnectionStatus(final ConnectionStatuses connectionStatus) {
        if (this.getActivity() != null) {
            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (connectionStatus) {
                        case NOT_CONNECTED:
                            txvTabletConnectionStatus.setText(getText(R.string.tablet_not_connected_info));
                            txvTabletConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_wifi_off), null, null, null);
                            break;
                        case CONNECTING:
                            txvTabletConnectionStatus.setText(getText(R.string.tablet_check_connection));
                            txvTabletConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_refresh), null, null, null);
                            break;
                        case CONNECTED:
                            txvTabletConnectionStatus.setText(getString(R.string.tablet_connected_device_id, ConnectionStatus.getInstance().getServerIPAddress(), ConnectionStatus.getInstance().getDeviceID()));
                            txvTabletConnectionStatus.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.ic_wifi_on), null, null, null);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }
}
