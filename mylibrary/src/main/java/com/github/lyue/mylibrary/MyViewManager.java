package com.github.lyue.mylibrary;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by yue_liang on 2017/4/1.
 */

public class MyViewManager {
    private MyView mMyView;
    private MyViewManager (View view, int id) {
        mMyView = (MyView) view.findViewById(id);
    }
    private MyViewManager (View view) {
        mMyView = (MyView) view;
    }
    public static MyViewManager loadMyView(LayoutInflater inflater,
                                  ViewGroup rootView,
                                  boolean attachToRoot) {
        return new MyViewManager(
                inflater.inflate(R.layout.sample_my_view, rootView, attachToRoot),
                R.id.my_view);
    }

    public static MyViewManager findMyView(View myView) {
        return new MyViewManager(myView);
    }

    public MyViewManager setMoveBounds(ViewGroup viewGroup) {
        getMyView().setMoveBounds(viewGroup);
        return this;
    }

    public MyView getMyView() {
        return mMyView;
    }
}
