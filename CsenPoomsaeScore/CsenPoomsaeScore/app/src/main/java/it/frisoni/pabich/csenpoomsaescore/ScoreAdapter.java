package it.frisoni.pabich.csenpoomsaescore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;


/**
 * Created by giacomofrisoni on 05/04/2017.
 */

public class ScoreAdapter extends ArrayAdapter<AthleteScore> {

    public ScoreAdapter(final Context context, final List<AthleteScore> scores) {
        super(context, 0, scores);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //Ottiene l'elemento per la posizione corrente
        AthleteScore score = getItem(position);

        //Controlla se una vista esistente viene riutilizzata, altrimenti la definisce
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_row_layout, parent, false);
        }

        // Lookup view for data population
        /*
        TextView tvNumber = (TextView) convertView.findViewById(R.id.number);
        TextView tvDate = (TextView) convertView.findViewById(R.id.date);
        TextView tvTime = (TextView) convertView.findViewById(R.id.time);
        TextView tvAccuracy = (TextView) convertView.findViewById(R.id.accuracy);
        TextView tvPresentation = (TextView) convertView.findViewById(R.id.presentation);
        TextView tvTotal = (TextView) convertView.findViewById(R.id.total);

        // Populate the data into the template view using the data object
        tvNumber.setText(String.valueOf(position + 1));
        tvDate.setText(score.date);
        tvTime.setText(score.time);
        tvAccuracy.setText(score.accuracy);
        tvPresentation.setText(score.presentation);
        tvTotal.setText(score.total);*/

        // Return the completed view to render on screen
        return convertView;
    }
}