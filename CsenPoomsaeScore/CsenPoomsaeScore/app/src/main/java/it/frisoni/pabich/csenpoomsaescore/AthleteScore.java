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
 */

public class AthleteScore implements BaseColumns {

    public static final String TABLE_NAME = "athlete_scores";

    public static final String COLUMN_ACCURACY = "accuracy";
    public static final String COLUMN_PRESENTATION = "presentation";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_DATETIME = "datetime";

    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private int id;
    private double accuracy;
    private double presentation;
    private double total;
    private Calendar dateTime;

    public AthleteScore(double accuracy, double presentation, double total, Calendar dateTime) {
        this.accuracy = accuracy;
        this.presentation = presentation;
        this.total = total;
        this.dateTime = dateTime;
    }

    public AthleteScore(Cursor cursor) {
        this.id = cursor.getInt(cursor.getColumnIndex(_ID));
        this.accuracy = cursor.getDouble(cursor.getColumnIndex(COLUMN_ACCURACY));
        this.presentation = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRESENTATION));
        this.total = cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL));
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

    public int getId() {
        return id;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public double getPresentation() {
        return presentation;
    }

    public double getTotal() {
        return total;
    }

    public Calendar getDateTime() {
        return dateTime;
    }
}
