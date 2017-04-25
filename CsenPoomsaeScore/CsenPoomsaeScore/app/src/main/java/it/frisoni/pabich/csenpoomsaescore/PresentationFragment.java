package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.graphics.PorterDuff;
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

import java.math.BigDecimal;
import java.util.Locale;

import it.frisoni.pabich.csenpoomsaescore.widgets.CustomNavBar;

import static android.content.ContentValues.TAG;
import static it.frisoni.pabich.csenpoomsaescore.RangeMappingUtilities.map;

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
    private final static double SCALE_FACTOR = 10d;
    private final static int MIN_PROGRESS = 5;
    private final static int MAX_PROGRESS = 20;

    //Barra di navigazione
    private CustomNavBar navBar;

    //Variabili
    private TextView txvCounter;
    private TextView txvSpeedPower, txvStrengthPace, txvEnergy;
    private SeekBar skbSpeedPower, skbStrengthPace, skbEnergy;
    private BigDecimal curPoints;

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
        skbStrengthPace = (SeekBar) view.findViewById(R.id.skb_strength_pace);
        skbEnergy = (SeekBar) view.findViewById(R.id.skb_energy);

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
        final SeekBar.OnSeekBarChangeListener listener_speed_power = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                txvSpeedPower.setText(getDescriptionFromValue(value));
                txvSpeedPower.setTextColor(getColorFromValue(value));
                changeSeekBarColor(seekBar, value);
                refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        final SeekBar.OnSeekBarChangeListener listener_strength_pace = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                txvStrengthPace.setText(getDescriptionFromValue(value));
                txvStrengthPace.setTextColor(getColorFromValue(value));
                changeSeekBarColor(seekBar, value);
                refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        final SeekBar.OnSeekBarChangeListener listener_energy = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                txvEnergy.setText(getDescriptionFromValue(value));
                txvEnergy.setTextColor(getColorFromValue(value));
                changeSeekBarColor(seekBar, value);
                refreshCounter(skbSpeedPower.getProgress(), skbStrengthPace.getProgress(), skbEnergy.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        //endregion

        //Inizializzazione delle seekbar
        skbSpeedPower.setOnSeekBarChangeListener(listener_speed_power);
        skbStrengthPace.setOnSeekBarChangeListener(listener_strength_pace);
        skbEnergy.setOnSeekBarChangeListener(listener_energy);
        skbSpeedPower.setMax(MAX_PROGRESS - MIN_PROGRESS);
        skbStrengthPace.setMax(MAX_PROGRESS - MIN_PROGRESS);
        skbEnergy.setMax(MAX_PROGRESS - MIN_PROGRESS);
        skbSpeedPower.setProgress(MAX_PROGRESS - MIN_PROGRESS);
        skbStrengthPace.setProgress(MAX_PROGRESS - MIN_PROGRESS);
        skbEnergy.setProgress(MAX_PROGRESS - MIN_PROGRESS);
        changeSeekBarColor(skbSpeedPower, MAX_PROGRESS - MIN_PROGRESS);
        changeSeekBarColor(skbStrengthPace, MAX_PROGRESS - MIN_PROGRESS);
        changeSeekBarColor(skbEnergy, MAX_PROGRESS - MIN_PROGRESS);

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
     *      valore
     * @param lower
     *      valore minimo dell'intervallo
     * @param upper
     *      valore massimo dell'intervallo
     * @return true se il numero specificato è incluso nel range, false altrimenti.
     */
    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    /*
     * Aggiorna il contatore effettuando la somma dei valori passati come parametro,
     * operando le opportune mappature e scale.
     */
    private void refreshCounter(int... values) {
        curPoints = BigDecimal.ZERO;
        for (int value : values) {
            curPoints = curPoints.add((BigDecimal.valueOf(map(value, 0, MAX_PROGRESS - MIN_PROGRESS, MIN_PROGRESS, MAX_PROGRESS)/SCALE_FACTOR)));
        }
        txvCounter.setText(String.format(Locale.getDefault(), "%.1f", curPoints));
    }

    /*
     * Aggiorna il colore della seekbar sulla base del valore indicato come parametro.
     */
    private void changeSeekBarColor(final SeekBar seekBar, final int value) {
        seekBar.getProgressDrawable().setColorFilter(getColorFromValue(value), PorterDuff.Mode.SRC_IN);
    }

    /*
     * Restituisce la descrizione associata al valore di avanzamento indicato.
     */
    private String getDescriptionFromValue(int value) {
        String description = getString(R.string.very_poor);
        if (isBetween(value, 13, 15)) {
            description = getString(R.string.perfect);
        } else if (isBetween(value, 10, 12)) {
            description = getString(R.string.excellent);
        } else if (isBetween(value, 7, 9)) {
            description = getString(R.string.very_good);
        } else if (isBetween(value, 4, 6)) {
            description = getString(R.string.good);
        } else if (isBetween(value, 1, 3)) {
            description = getString(R.string.poor);
        }
        return map(value, 0, MAX_PROGRESS - MIN_PROGRESS, MIN_PROGRESS, MAX_PROGRESS)/SCALE_FACTOR + " - " + description;
    }

    /*
     * Restituisce il colore associato al valore di avanzamento indicato.
     */
    private int getColorFromValue(int value) {
        if (isBetween(value, 10, 15)) {
            return ContextCompat.getColor(getActivity(), R.color.green);
        } else if (isBetween(value, 4, 9)) {
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
