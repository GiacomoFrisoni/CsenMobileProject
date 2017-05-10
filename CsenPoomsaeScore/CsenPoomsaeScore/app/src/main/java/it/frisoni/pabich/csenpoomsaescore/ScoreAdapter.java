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
 * Per ragioni di performance viene adottato sia il VIEW RECYCLING che il VIEW HOLDER PATTERN.
 * Il View Holder pattern, nello specifico, garantisce l'impiego dei findViewById() soltanto al momento
 * della creazione del primo layout (evitando di dover trovare una vista interna a un layout per ogni
 * operazione di riciclo).
 */

public class ScoreAdapter extends ArrayAdapter<AthleteScore> {

    public ScoreAdapter(final Context context, final List<AthleteScore> scores) {
        super(context, 0, scores);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        //Ottiene il punteggio associato alla posizione corrente
        AthleteScore score = getItem(position);

        //Controlla se una vista esistente viene riutilizzata, altrimenti la definisce
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_row_layout, parent, false);

            holder = new ViewHolder();
            holder.number = (TextView) convertView.findViewById(R.id.number);
            holder.date = (TextView) convertView.findViewById(R.id.txv_date);
            holder.time = (TextView) convertView.findViewById(R.id.txv_time);
            holder.accuracy = (TextView) convertView.findViewById(R.id.txv_accuracy);
            holder.presentation = (TextView) convertView.findViewById(R.id.txv_presentation);
            holder.total = (TextView) convertView.findViewById(R.id.txv_total);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Imposta il testo delle descrizioni
        ((TextView) convertView.findViewById(R.id.txv_accuracy_title)).setText(
                this.getContext().getString(R.string.score_description, this.getContext().getString(R.string.accuracy)));
        ((TextView) convertView.findViewById(R.id.txv_presentation_title)).setText(
                this.getContext().getString(R.string.score_description, this.getContext().getString(R.string.presentation)));

        //Popola il template coi dati da visualizzare
        if (score != null) {
            holder.number.setText(String.valueOf(position + 1));
            holder.date.setText(score.getDateString());
            holder.time.setText(score.getTimeString());
            holder.accuracy.setText(String.valueOf(score.getAccuracy()));
            holder.presentation.setText(String.valueOf(score.getPresentation()));
            holder.total.setText(String.valueOf(score.getTotal()));
        }

        //Ritorna la view da renderizzare
        return convertView;
    }

    private static class ViewHolder {
        public TextView number;
        public TextView date;
        public TextView time;
        public TextView accuracy;
        public TextView presentation;
        public TextView total;
    }
}