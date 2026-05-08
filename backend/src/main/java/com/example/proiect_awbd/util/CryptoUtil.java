package com.example.proiect_awbd.util;

import com.example.proiect_awbd.entity.User;
import org.jetbrains.annotations.NotNull;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class CryptoUtil {

    final static private String ALGORITHM = "AES/GCM/noPadding";
    final static private Integer KEY_LENGTH_BITS = 256;
    final static private Integer IV_LENGTH = 12;
    final static private Integer SECRET_LENGTH = KEY_LENGTH_BITS / 8 + IV_LENGTH;

    /**
     * Functie pentru generare de chei private pentru utilizatori. Folosita doar o data la adaugarea unui nou
     * utilizator in baza de date
     * @return cheia generata
     */
    private static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_LENGTH_BITS);
        return keyGenerator.generateKey();
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Functie pentru a obtine cheia utilizatorului in format SecretKey din secretul codificat base64 din baza de date
     * @param userSecretBase64 secretul utilizatorul codificat base64
     * @return cheia in format SecretKey
     */
    private static SecretKey getKeyFromSecret(String userSecretBase64) {
        byte[] raw_secret = Base64.getDecoder().decode(userSecretBase64);
        byte[] raw_key = new byte[KEY_LENGTH_BITS / 8];
        System.arraycopy(raw_secret, 0, raw_key, 0, KEY_LENGTH_BITS / 8);
        return new SecretKeySpec(raw_key, "AES");
    }

    private static GCMParameterSpec getIvFromSecret(String userSecretBase64) {
        byte[] raw_secret = Base64.getDecoder().decode(userSecretBase64);
        byte[] raw_iv = new byte[IV_LENGTH];
        System.arraycopy(raw_secret, KEY_LENGTH_BITS / 8, raw_iv, 0, IV_LENGTH);
        return new GCMParameterSpec(IV_LENGTH * 8, raw_iv);
    }

    /**
     * Functie pentru generarea secretului utilizatorului
     * @return Intoarce cheia utilizatorului (KEY_LENGTH_BITS bits) concatenata cu iv-ul
     */
    public static String generateUserSecretKey() throws NoSuchAlgorithmException {
        try {
            byte[] rawSecret = new byte[SECRET_LENGTH];

            SecretKey key = generateKey();
            IvParameterSpec iv = generateIv();

            System.arraycopy(key.getEncoded(), 0, rawSecret, 0, KEY_LENGTH_BITS / 8);
            System.arraycopy(iv.getIV(), 0, rawSecret, KEY_LENGTH_BITS / 8, IV_LENGTH);

            return Base64.getEncoder().encodeToString(rawSecret);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Eroare la generarea secretului pentru utilizator: " + e.getMessage());
        }
    }

    /**
     * Functia pentru criptarea fisierului
     *
     * @param inputFile  fisierul de intrare ce se vrea criptat
     * @param encFile fisierul criptat
     * @param user       utilizatorul care detine fisierul
     */
    public static void encryptFile(InputStream inputFile, FileOutputStream encFile, User user)
            throws NoSuchPaddingException, NoSuchAlgorithmException, FileNotFoundException {
        try (inputFile; FileOutputStream outputFile = encFile) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey key = getKeyFromSecret(user.getUserSecret());
            GCMParameterSpec iv = getIvFromSecret(user.getUserSecret());

            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            execute(inputFile, outputFile, cipher);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Error initializing encryption. Invalid key or IV: " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException("Error encrypting file: " + e.getMessage(), e);
        }
    }


    /**
     * Functia pentru decriptarea fisierului
     *
     * @param inputFile fisierul criptat
     * @param decFile fisierul rezultat dupa decriptare
     * @param user          utilizatorul care detine fisierul
     * @throws NoSuchAlgorithmException
     */
    public static void decryptFile(InputStream inputFile, FileOutputStream decFile, User user)
            throws NoSuchPaddingException, NoSuchAlgorithmException{
        try (inputFile; FileOutputStream outputFile = decFile) {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKey key = getKeyFromSecret(user.getUserSecret());
            GCMParameterSpec iv = getIvFromSecret(user.getUserSecret());

            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            execute(inputFile, outputFile, cipher);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException("Error initializing decryption. Invalid key or IV: " + e.getMessage(), e);
        } catch (IllegalBlockSizeException | BadPaddingException | IOException e) {
            throw new RuntimeException("Error decrypting file: " + e.getMessage(), e);
        }
    }

    /**
     * Functie privata care executa fie operatia de criptare sau de decripare in functie de parametrul cipher
     */
    private static void execute(@NotNull InputStream inputStream, FileOutputStream outputStream, Cipher cipher)
            throws IOException, IllegalBlockSizeException, BadPaddingException {
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }

        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
    }

}
