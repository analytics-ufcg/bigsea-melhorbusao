package br.edu.ufcg.analytics.meliorbusao.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ffosilva on 08/11/15.
 */
public class UnswipableViewPager extends ViewPager {

    public UnswipableViewPager(Context context) {
        super(context);
    }

    public UnswipableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Never allow swiping to switch between pages
        return false;
    }
}