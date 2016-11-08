package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;

import java.util.Random;

public class StringUtils {

    protected StringUtils() {}

    public static String getRandomString(Context context, int arrayResourceId) {
        String[] strings = context.getResources().getStringArray(arrayResourceId);

        return strings[new Random().nextInt(strings.length)];
    }
}
