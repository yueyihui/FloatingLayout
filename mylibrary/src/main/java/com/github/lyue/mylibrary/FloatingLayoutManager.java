package com.github.lyue.mylibrary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yue_liang on 2017/4/1.
 */

public class FloatingLayoutManager {
    private FloatingLayout mFloatingLayout;
    private FloatingLayoutManager(View view, int id) {
        mFloatingLayout = (FloatingLayout) view.findViewById(id);
    }
    private FloatingLayoutManager(View view) {
        mFloatingLayout = (FloatingLayout) view;
    }
    public static FloatingLayoutManager loadMyView(LayoutInflater inflater,
                                                   ViewGroup rootView,
                                                   boolean attachToRoot) {
        return new FloatingLayoutManager(
                inflater.inflate(R.layout.sample_floating_layout, rootView, attachToRoot),
                R.id.my_view);
    }

    public static FloatingLayoutManager findMyView(View myView) {
        return new FloatingLayoutManager(myView);
    }

    public FloatingLayoutManager setMoveBounds(ViewGroup viewGroup) {
        getMyView().setMoveBounds(viewGroup);
        return this;
    }

    public FloatingLayout getMyView() {
        return mFloatingLayout;
    }
}
