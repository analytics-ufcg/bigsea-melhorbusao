package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.res.Resources;

public class DisplayMetricUtils {

    protected DisplayMetricUtils() {}

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return  dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp){
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

}
