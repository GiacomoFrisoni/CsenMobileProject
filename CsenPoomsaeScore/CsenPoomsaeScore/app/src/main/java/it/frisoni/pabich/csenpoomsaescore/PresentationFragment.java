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
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.math.BigDecimal;

import static android.content.ContentValues.TAG;

public class PresentationFragment extends Fragment {

    /**
     * Interfaccia per gestire il flusso dell'applicazione dal fragment all'activity.
     */
    public interface OnPresentationInteraction {
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

    //Variabili
    private TextView txvCounter;
    private TextView txvSpeedPower, txvStrengthPace, txvEnergy;
    private DiscreteSeekBar skSpeedPower, skStrengthPace, skEnergy;
    private BigDecimal curPoints, curPointsOld;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presentation, container, false);

        //Creazione dei riferimenti con gli elementi della view tramite l'id univoco loro assegnato
        //txvCounter = (TextView) view.findViewById(R.id.txv_counter);
        //txvSpeedPower = (TextView) view.findViewById(R.id.txv_speed_power);
        //txvEnergy = (TextView) view.findViewById(R.id.txv_energy);
        //skSpeedPower = (DiscreteSeekBar) view.findViewById(R.id.seek_bar_speed_power);
        //skStrengthPace = (DiscreteSeekBar) view.findViewById(R.id.seek_bar_strength_pace);
        //skEnergy = (DiscreteSeekBar) view.findViewById(R.id.seek_bar_energy);

        //Ottenimento dei dati dalla precedente schermata
        //... Codice...

        //Listener seekbar e inizializzazioni

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
        txvCounter.setText(curPoints.toString());
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
