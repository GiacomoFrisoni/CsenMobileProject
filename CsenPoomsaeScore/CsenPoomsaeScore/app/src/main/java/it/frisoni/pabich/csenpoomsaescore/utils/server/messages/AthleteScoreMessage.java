package it.frisoni.pabich.csenpoomsaescore.utils.server.messages;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AthleteScoreMessage extends WebSocketMessageData {

    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static final String TIME_FORMAT = "HH:mm:ss";

    @Expose
    private float accuracy;
    @Expose
    private float presentation;
    @Expose
    private float total;
    @Expose
    private String date;
    @Expose
    private String time;

    public AthleteScoreMessage(final float accuracy, final float presentation, final float total, final Calendar dateTime) {
        this.accuracy = accuracy;
        this.presentation = presentation;
        this.total = total;
        this.date = this.getDateString(dateTime);
        this.time = this.getTimeString(dateTime);
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getPresentation() {
        return presentation;
    }

    public void setPresentation(float presentation) {
        this.presentation = presentation;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Accuracy: " + getAccuracy() + ", Presentation: " + getPresentation() + ", Total: " + getTotal() + ", Date: " + getDate() + ", Time: " + getTime();
    }

    @Override
    public String toJson() {
        return gson.toJson(this);
    }

    private String getTimeString(final Calendar dateTime) {
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(dateTime.getTime());
    }

    private String getDateString(final Calendar dateTime) {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(dateTime.getTime());
    }
}
