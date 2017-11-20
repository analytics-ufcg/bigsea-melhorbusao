package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;

import java.util.List;
import java.util.Random;

public class StringUtils {

    protected StringUtils() {}

    public static String getRandomString(Context context, int arrayResourceId) {
        String[] strings = context.getResources().getStringArray(arrayResourceId);

        return strings[new Random().nextInt(strings.length)];
    }

    public static String getStringListConcat(List<String> list) {
        StringBuilder listSB = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) listSB.append(", ");
            listSB.append(list.get(i));
        }

        return listSB.toString();
    }
}
