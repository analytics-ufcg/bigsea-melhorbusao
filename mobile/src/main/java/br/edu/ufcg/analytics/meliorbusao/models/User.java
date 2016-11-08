package br.edu.ufcg.analytics.meliorbusao.models;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class User {

    private static User instance;
    private String userID;


    public static User getInstance() {
        if (instance == null) {
            User user = new User();
            instance = user;
        }
        return instance;
    }

    private User() {
        userID = null;
    }

    /**
     *
     * @param context
     * @return O id do usuário
     */
    public String getUserID(Context context) {
        if (userID == null){
            userID = generateID(context);
        }
        return userID;
    }

    // generate a unique ID for each device
    // use available schemes if possible / generate a random signature instead

    /**
     * Geração do id do celular
     * @param context
     * @return O id do celular (que se usa como id do usuário)
     */
    private static String generateID(Context context) {

        // use the ANDROID_ID constant, generated at the first device boot
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        // in case known problems are occured
        if ("9774d56d682e549c".equals(deviceId) || deviceId == null) {

            // get a unique deviceID like IMEI for GSM or ESN for CDMA phones
            // don't forget:
            //    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
            deviceId = ((TelephonyManager) context
                    .getSystemService( Context.TELEPHONY_SERVICE ))
                    .getDeviceId();

            // if nothing else works, generate a random number
            if (deviceId == null) {

                Random tmpRand = new Random();
                deviceId = String.valueOf(tmpRand.nextLong());
            }

        }

        // any value is hashed to have consistent format
        return getHash(deviceId);
    }

    // generates a SHA-1 hash for any string

    /**
     *
     * @param stringToHash
     * @return
     */
    public static String getHash(String stringToHash) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] result = null;

        try {
            result = digest.digest(stringToHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();

        for (byte b : result)
        {
            sb.append(String.format("%02X", b));
        }

        String messageDigest = sb.toString();
        return messageDigest;
    }



}
