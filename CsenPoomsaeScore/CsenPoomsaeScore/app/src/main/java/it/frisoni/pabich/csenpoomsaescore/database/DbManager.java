package it.frisoni.pabich.csenpoomsaescore.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;

/**
 * Created by giacomofrisoni on 27/03/2017.
 *
 * Questa classe è dedicata all'interazione col database per il salvataggio di nuovi dati, l'ottenimento di quelli
 * memorizzati e la loro eventuale cancellazione.
 */

public class DbManager {

    private DbHelper dbHelper;

    public DbManager(Context context) {
        dbHelper = new DbHelper(context);
    }

    /**
     * Salva un nuovo punteggio di gara.
     *
     * @param score
     *      AthleteScore contenente le varie informazioni sui punteggi otenuti dall'atleta
     * @return numero di righe interessate
     */
    public boolean addAthleteScore(AthleteScore score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.insert(AthleteScore.TABLE_NAME, null, score.getContentValues());
        return row > 0;
    }

    /**
     * Fornisce l'elenco dei punteggi di gara memorizzati, ottenuti dai vari atleti.
     *
     * @return elenco di punteggi
     */
    public List<AthleteScore> getAthleteScores() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<AthleteScore> athleteScores = new ArrayList<>();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + AthleteScore.TABLE_NAME;
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                AthleteScore score = new AthleteScore(cursor);
                athleteScores.add(score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return athleteScores;
    }

    /**
     * Elimina il contenuto della tabella dedicata alla memorizzazione dei punteggi di gara.
     */
    public void clearAthleteScores() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + AthleteScore.TABLE_NAME);
    }
}
