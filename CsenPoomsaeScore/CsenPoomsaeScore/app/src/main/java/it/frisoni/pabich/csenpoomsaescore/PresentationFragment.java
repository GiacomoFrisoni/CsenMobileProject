package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.math.BigDecimal;
import java.util.Locale;

import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;

public class PresentationFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnPresentationInteraction {
        void onAccuracyClick();
        void onResultsClick();
    }

    /**
     * Variabile per il mantenimento del collegamento con l'activity "ascoltatrice".
     * Cosente di segnalare l'intercettazione di eventi.
     */
    private PresentationFragment.OnPresentationInteraction listener;

    /**
     * Costruttore vuoto richiesto dal sistema per poter funzionare correttamente in tutte le situazioni.
     */
    public PresentationFragment() {
    }

    /**
     * "Costruttore" statico del fragment.
     * L'utilizzo di questo metodo, che ritorna un oggetto della classe corrente, rappresenta la modalità standard per istanziare un oggetto
     * di una classe Fragment.
     *
     * @return oggetto di classe PresentationFragment
     */
    public static PresentationFragment newInstance() {
        return new PresentationFragment();
    }


    //Costanti
    private final static double RANGE_SCALE = 10d;
    private final static int START_PROGRESS = 20;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private TextView txvCounter;
    private TextView txvSpeedPower, txvStrengthPace, txvEnergy;
    //private DiscreteSeekBar skbSpeedPower, skbStrengthPace, skbEnergy;
    private DiscreteSeekBar skbStrengthPace, skbEnergy;
    private BigDecimal curPoints;
    private SeekBar skbSpeedPower;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presentation, container, false);

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        navBar = (CustomNavBar) view.findViewById(R.id.nav_bar);
        txvCounter = (TextView) view.findViewById(R.id.txv_counter);
        txvSpeedPower = (TextView) view.findViewById(R.id.txv_speed_power);
        txvStrengthPace = (TextView) view.findViewById(R.id.txv_strength_pace);
        txvEnergy = (TextView) view.findViewById(R.id.txv_energy);
        skbSpeedPower = (SeekBar) view.findViewById(R.id.skb_speed_power);
        skbStrengthPace = (DiscreteSeekBar) view.findViewById(R.id.skb_strength_pace);
        skbEnergy = (DiscreteSeekBar) view.findViewById(R.id.skb_energy);

        //Gestione della navbar
        //region NavBarListeners
        navBar.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAccuracyClick();
                }
            }
        });
        navBar.getForwardButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onResultsClick();
                }
            }
        });
        //endregion

        //Listener per la gestione delle seekbar
        //region seekBarListenersDefinition
        final DiscreteSeekBar.OnProgressChangeListener listener_speed_power = new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                txvSpeedPower.setText(getDescriptionFromValue(value));
                txvSpeedPower.setTextColor(getColorFromValue(value));
                seekBar.setIndicatorFormatter(String.valueOf(value/RANGE_SCALE));
                changeSeekBarColors(seekBar, value);
                refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) { }
        };
        final DiscreteSeekBar.OnProgressChangeListener listener_strength_pace = new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                txvStrengthPace.setText(getDescriptionFromValue(value));
                txvStrengthPace.setTextColor(getColorFromValue(value));
                seekBar.setIndicatorFormatter(String.valueOf(value/RANGE_SCALE));
                changeSeekBarColors(seekBar, value);
                refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) { }
        };
        final DiscreteSeekBar.OnProgressChangeListener listener_energy = new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                txvEnergy.setText(getDescriptionFromValue(value));
                txvEnergy.setTextColor(getColorFromValue(value));
                seekBar.setIndicatorFormatter(String.valueOf(value/RANGE_SCALE));
                changeSeekBarColors(seekBar, value);
                refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) { }
        };
        //endregion

        //Inizializzazione delle seekbar
        skbSpeedPower.setProgress(START_PROGRESS);
        skbStrengthPace.setProgress(START_PROGRESS);
        skbEnergy.setProgress(START_PROGRESS);
        //changeSeekBarColors(skbSpeedPower, START_PROGRESS);
        changeSeekBarColors(skbStrengthPace, START_PROGRESS);
        changeSeekBarColors(skbEnergy, START_PROGRESS);

        //Inizializzazione del contatore
        refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());

        //Inizializzazione delle textview
        txvSpeedPower.setText(getDescriptionFromValue(skbSpeedPower.getProgress()));
        txvStrengthPace.setText(getDescriptionFromValue(skbStrengthPace.getProgress()));
        txvEnergy.setText(getDescriptionFromValue(skbEnergy.getProgress()));

        return view;
    }

    /**
     * Questo metodo verifica se un numero è incluso in un certo range.
     * @param x
     *      il valore
     * @param lower
     *      il valore minimo dell'intervallo
     * @param upper
     *      il valore massimo dell'intervallo
     * @return true se il numero specificato è incluso nel range, false altrimenti.
     */
    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    private void refreshCounter(int... values) {
        curPoints = BigDecimal.ZERO;
        for (int value : values) {
            curPoints = curPoints.add((BigDecimal.valueOf(value/10d)));
        }
        txvCounter.setText(String.format(Locale.getDefault(), "%f", curPoints));
    }

    private void changeSeekBarColors(final DiscreteSeekBar seekBar, final int value) {
        seekBar.setScrubberColor(getColorFromValue(value));
        seekBar.setRippleColor(getColorFromValue(value));
        seekBar.setThumbColor(getColorFromValue(value), getColorFromValue(value));
    }

    private String getDescriptionFromValue(int value) {
        String description = getString(R.string.very_poor);
        if (isBetween(value, 18, 20)) {
            description = getString(R.string.perfect);
        } else if (isBetween(value, 15, 17)) {
            description = getString(R.string.excellent);
        } else if (isBetween(value, 12, 14)) {
            description = getString(R.string.very_good);
        } else if (isBetween(value, 9, 11)) {
            description = getString(R.string.good);
        } else if (isBetween(value, 6, 8)) {
            description = getString(R.string.poor);
        }
        return  value/10d + " - " + description;
    }

    private int getColorFromValue(int value) {
        if (isBetween(value, 16, 20)) {
            return ContextCompat.getColor(getActivity(), R.color.green);
        } else if (isBetween(value, 11, 16)) {
            return ContextCompat.getColor(getActivity(), R.color.orange);
        } else {
            return ContextCompat.getColor(getActivity(), R.color.red_pressed);
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
        if (context instanceof PresentationFragment.OnPresentationInteraction) {
            listener = (PresentationFragment.OnPresentationInteraction) context;
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
