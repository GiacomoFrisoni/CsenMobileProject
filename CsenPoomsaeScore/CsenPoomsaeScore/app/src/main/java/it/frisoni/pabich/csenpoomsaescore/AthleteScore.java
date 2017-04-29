package it.frisoni.pabich.csenpoomsaescore;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by giacomofrisoni on 27/03/2017.
 *
 * Questa classe modella un punteggio associato a un atleta e tutte le informazioni correlate.
 */

public class AthleteScore implements BaseColumns {

    //Nome della tabella
    public static final String TABLE_NAME = "athlete_scores";

    //Nomi delle colonne
    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_PRESENTATION = "presentation";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_DATETIME = "datetime";

    //Formati per la data e l'ora
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";

    private int id;
    private float accuracy;
    private float presentation;
    private float total;
    private Calendar dateTime;

    /**
     * Creazione di un nuovo punteggio.
     *
     * @param accuracy
     *      precisione
     * @param presentation
     *      presentazione
     * @param total
     *      totale
     * @param dateTime
     *      data e ora di registrazione
     */
    public AthleteScore(float accuracy, float presentation, float total, Calendar dateTime) {
        this.accuracy = accuracy;
        this.presentation = presentation;
        this.total = total;
        this.dateTime = dateTime;
    }

    /**
     * Creazione di un nuovo punteggio.
     *
     * @param cursor
     *      cursore da cui prelevare i dati
     */
    public AthleteScore(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(_ID));
        this.accuracy = cursor.getFloat(cursor.getColumnIndex(COLUMN_ACCURACY));
        this.presentation = cursor.getFloat(cursor.getColumnIndex(COLUMN_PRESENTATION));
        this.total = cursor.getFloat(cursor.getColumnIndex(COLUMN_TOTAL));
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault());
            Date date = sdf.parse(cursor.getString(cursor.getColumnIndex(COLUMN_DATETIME)));
            this.dateTime = sdf.getCalendar();
        } catch (ParseException e) {
            Log.e(TAG, "Failed string to calendar conversion");
        }
    }

    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ACCURACY, accuracy);
        cv.put(COLUMN_PRESENTATION, presentation);
        cv.put(COLUMN_TOTAL, total);
        cv.put(COLUMN_DATETIME, new SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).format(dateTime.getTime()));
        return cv;
    }

    /**
     * @return l'id per il punteggio complessivo.
     */
    public int getId() {
        return id;
    }

    /**
     * @return il punteggio di accuracy.
     */
    public float getAccuracy() {
        return accuracy;
    }

    /**
     * @return il punteggio di presentazione.
     */
    public float getPresentation() {
        return presentation;
    }

    /**
     * @return il punteggio totale.
     */
    public float getTotal() {
        return total;
    }

    /**
     * @return la data di registrazione del punteggio in formato testuale.
     */
    public String getDateString() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dateTime.getTime());
    }

    /**
     * @return l'ora di registrazione del punteggio in formato testuale.
     */
    public String getTimeString() {
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(dateTime.getTime());
    }
}
