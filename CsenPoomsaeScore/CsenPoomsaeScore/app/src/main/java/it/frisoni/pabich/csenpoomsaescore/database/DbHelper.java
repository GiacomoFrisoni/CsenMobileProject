package it.frisoni.pabich.csenpoomsaescore.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import it.frisoni.pabich.csenpoomsaescore.model.AthleteScore;

/**
 * Created by giacomofrisoni on 27/03/2017.
 *
 * Questa classe costituisce un helper per la gestione del database e la creazione / cancellazione
 * delle relative tabelle.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "myResults.db";
    private static final int DATABASE_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String CREATE_TABLE_ATHLETES = "CREATE TABLE IF NOT EXISTS " +
            AthleteScore.TABLE_NAME + " (" +
            AthleteScore._ID + " INTEGER PRIMARY KEY, " +
            AthleteScore.COLUMN_ACCURACY + " DOUBLE NOT NULL, " +
            AthleteScore.COLUMN_PRESENTATION + " DOUBLE NOT NULL, " +
            AthleteScore.COLUMN_TOTAL + " DOUBLE NOT NULL, " +
            AthleteScore.COLUMN_DATETIME + " VARCHAR NOT NULL)";

    public static final String DROP_TABLE_ATHLETES = "DROP TABLE IF EXISTS "
            + AthleteScore.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_ATHLETES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(DROP_TABLE_ATHLETES);
        onCreate(database);
    }
}
