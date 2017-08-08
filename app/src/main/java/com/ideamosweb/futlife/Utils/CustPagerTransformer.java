package com.ideamosweb.futlife.Utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Creado por Deimer Villa on 3/11/16.
 * Función:
 */
public class CustPagerTransformer implements ViewPager.PageTransformer {

    private int maxTranslateOffsetX;
    private ViewPager view_pager;

    public CustPagerTransformer(Context context) {
        this.maxTranslateOffsetX = reformatPixels(context, 180);
    }

    public void transformPage(View view, float position) {
        if(view_pager == null) {
            view_pager = (ViewPager) view.getParent();
        }
        int leftInScreen = view.getLeft() - view_pager.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - view_pager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * 0.38f / view_pager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
        }
    }

    private int reformatPixels(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

}
