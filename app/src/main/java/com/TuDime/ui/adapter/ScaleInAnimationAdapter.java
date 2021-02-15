package com.TuDime.ui.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.TuDime.ui.adapter.listeners.AnimationAdapter;

public class ScaleInAnimationAdapter extends AnimationAdapter {

    private static final float DEFAULT_SCALE_FROM = .5f;
    private final float mFrom;

    public ScaleInAnimationAdapter(RecyclerView.Adapter adapter) {
        this(adapter, DEFAULT_SCALE_FROM);
    }

    public ScaleInAnimationAdapter(RecyclerView.Adapter adapter, float from) {
        super(adapter);
        mFrom = from;
    }

    @Override protected Animator[] getAnimators(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", mFrom, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", mFrom, 1f);
        return new ObjectAnimator[] { scaleX, scaleY };
    }
}
