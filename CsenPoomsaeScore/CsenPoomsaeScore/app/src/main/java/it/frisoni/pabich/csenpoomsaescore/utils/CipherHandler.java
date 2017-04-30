package it.frisoni.pabich.csenpoomsaescore.utils;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static android.content.ContentValues.TAG;

/**
 * Created by giacomofrisoni on 08/04/2017.
 *
 * Questa classe mette a disposizione metodi per la gestione di crittografia AES a 128 bit.
 */

public final class CipherHandler {

    private static volatile CipherHandler singleton;
    private byte[] key = null;

    private CipherHandler() {
        try {
            key = getKey();
        } catch (Exception e) {
            Log.e(TAG, "Error during secret key generation");
        }
    }

    /**
     * Questo metodo ritorna un CipherHandler.
     * Se il CipherHandler non esiste, viene creato alla prima chiamata.
     *
     * @return l'unica istanza di CipherHandler.
     */
    public static CipherHandler getHandler() {
        if (singleton == null) {
            synchronized (CipherHandler.class) {
                if (singleton == null) {
                    singleton = new CipherHandler();
                }
            }
        }
        return singleton;
    }

    /**
     * Cifra una array di byte mediante l'algoritmo di crittografia AES con chiave a 128 bit.
     *
     * @param plainText l'array di byte da cifrare
     * @return l'array di byte cifrato
     * @throws Exception errore durante la cifratura
     */
    public byte[] encryptBytes(byte[] plainText) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(plainText);
    }

    /**
     * Decifra un array di byte criptato mediante AES con chiave a 128 bit.
     *
     * @param cipherText l'array di byte cifrato
     * @return l'array di byte decifrato
     * @throws Exception errore durante la decifratura
     */
    public  byte[] decryptBytes(byte[] cipherText) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(cipherText);
    }

    /*
     * Converte la stringa specificata in un array di byte.
     */
    private static byte[] hexStringToByteArray(final String s) {
        if (s == null || (s.length() % 2) == 1)
            throw new IllegalArgumentException();
        final char[] chars = s.toCharArray();
        final int len = chars.length;
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(chars[i], 16) << 4) + Character.digit(chars[i + 1], 16));
        }
        return data;
    }

    private byte[] getKey() throws Exception {
        return hexStringToByteArray("E072EDF9534053A0B6C581C58FBF25CC");
    }
}
