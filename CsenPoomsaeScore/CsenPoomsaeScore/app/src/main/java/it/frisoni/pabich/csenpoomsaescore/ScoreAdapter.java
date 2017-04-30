package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;


/**
 * Created by giacomofrisoni on 05/04/2017.
 *
 * Questa classe modella un adapter per la realizzazione di una lista costituita da elementi personalizzati
 * finalizzati alla visualizzazione dei punteggi di gara memorizzati.
 */

public class ScoreAdapter extends ArrayAdapter<AthleteScore> {

    public ScoreAdapter(final Context context, final List<AthleteScore> scores) {
        super(context, 0, scores);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Ottiene il punteggio associato alla posizione corrente
        AthleteScore score = getItem(position);

        //Controlla se una vista esistente viene riutilizzata, altrimenti la definisce
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_row_layout, parent, false);
        }

        //Imposta il testo delle descrizioni
        ((TextView) convertView.findViewById(R.id.txv_accuracy_title)).setText(
                this.getContext().getString(R.string.score_description, this.getContext().getString(R.string.accuracy)));
        ((TextView) convertView.findViewById(R.id.txv_presentation_title)).setText(
                this.getContext().getString(R.string.score_description, this.getContext().getString(R.string.presentation)));

        //Ottiene i riferimenti al row layout
        TextView txvNumber = (TextView) convertView.findViewById(R.id.number);
        TextView txvDate = (TextView) convertView.findViewById(R.id.txv_date);
        TextView txvTime = (TextView) convertView.findViewById(R.id.txv_time);
        TextView txvAccuracy = (TextView) convertView.findViewById(R.id.txv_accuracy);
        TextView txvPresentation = (TextView) convertView.findViewById(R.id.txv_presentation);
        TextView txvTotal = (TextView) convertView.findViewById(R.id.txv_total);

        //Popola il template coi dati da visualizzare
        txvNumber.setText(String.valueOf(position + 1));
        txvDate.setText(score.getDateString());
        txvTime.setText(score.getTimeString());
        txvAccuracy.setText(String.valueOf(score.getAccuracy()));
        txvPresentation.setText(String.valueOf(score.getPresentation()));
        txvTotal.setText(String.valueOf(score.getTotal()));

        // Return the completed view to render on screen
        return convertView;
    }
}