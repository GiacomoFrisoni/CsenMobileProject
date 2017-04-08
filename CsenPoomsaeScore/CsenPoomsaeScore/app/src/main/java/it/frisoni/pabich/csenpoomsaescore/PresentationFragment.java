package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
        PresentationFragment fragment = new PresentationFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_presentation, container, false);
        return view;
    }

    /**
     * Metodo del ciclo di vita del fragment che viene richiamato quando lo stesso viene "collegato" ad un'activity.
     *
     * @param context
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
