package it.frisoni.pabich.csenpoomsaescore.utils;

/**
 * Created by giacomofrisoni on 25/04/2017.
 *
 * Questa classe di utilità mette a disposizione utili metodi di mapping di valori tra due range.
 */

public final class RangeMappingUtilities {
    /**
     * Questo metodo mappa un valore appartenente a un range di partenza, in un proporzionale
     * valore relativo un secondo range.
     *
     * @param x
     *      valore di input
     * @param in_min
     *      valore minimo del range di input
     * @param in_max
     *      valore massimo del range di input
     * @param out_min
     *      valore minimo del range di output
     * @param out_max
     *      valore massimo del range di output
     * @return il proporzionale valore associato al range di output
     */
    public static int map (int x, int in_min, int in_max, int out_min, int out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
