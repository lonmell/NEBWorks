package com.krafte.nebworks.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

public class CustomEditText extends TextInputEditText {
    public CustomEditText(@NonNull Context context) {
        super(context);
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        getParent().requestDisallowInterceptTouchEvent(true);
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            getParent().requestDisallowInterceptTouchEvent(false);
            performClick();
        }

        return true;
    }
}
