package it.frisoni.pabich.csenpoomsaescore.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import it.frisoni.pabich.csenpoomsaescore.AthleteScore;

import static android.content.ContentValues.TAG;

/**
 * Created by giacomofrisoni on 27/03/2017.
 */

public class DbManager {

    private DbHelper dbHelper;

    public DbManager(Context context) {
        dbHelper = new DbHelper(context);
    }

    public boolean addAthleteScore(AthleteScore score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long row = db.insert(AthleteScore.TABLE_NAME, null, score.getContentValues());
        return row > 0;
    }

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
}
