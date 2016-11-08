package br.edu.ufcg.analytics.meliorbusao.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;

import br.edu.ufcg.analytics.meliorbusao.R;

public class ProgressUtils {

    protected ProgressUtils() {}

    /**
     * Seta as configurações das abrras de progresso do card
     * @param context
     * @return
     */
    public static ProgressBar buildProgressBar(Context context) {

        ProgressBar pBar = new ProgressBar(context);
        pBar.setIndeterminate(true);
        Drawable spinnerDrawable = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            spinnerDrawable = context.getResources().getDrawable(R.drawable.progress, context.getTheme());
        } else {
            spinnerDrawable = context.getResources().getDrawable(R.drawable.progress);
        }

        pBar.setIndeterminateDrawable(spinnerDrawable);
        pBar.setVisibility(View.VISIBLE);
        pBar.setScaleX(0.25f);
        pBar.setScaleY(0.25f);

        return pBar;
    }
}
