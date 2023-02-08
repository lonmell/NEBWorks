package com.krafte.nebworks.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SwipeFrameLayout extends FrameLayout {
    /** fling behavior threshold */
    private static final int FLING_THRESHOLD = 180;
    /** callback return value content : Left to Right */
    public static final int LEFT_TO_RIGHT = -1;
    /** callback return value content : Right to Left */
    public static final int RIGHT_TO_LEFT = 1;

    private GestureDetector mGestureDetector;
    private OnSwipeListener mOnSwipeListener;

    /**
     * OnSwipeListener interface
     */
    public interface OnSwipeListener {
        public abstract void onSwipe(View view, int direction);
    }

    /**
     * Set swipe listener
     *
     * @param listener
     */
    public void setOnSwipeListener(final OnSwipeListener listener) {
        mOnSwipeListener = listener;
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public SwipeFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mGestureDetector = new GestureDetector(context,new MySimpleOnGestureListener());
    }

    /**
     * @param context
     * @param attrs
     */
    public SwipeFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context,new MySimpleOnGestureListener());
    }

    /**
     * @param context
     */
    public SwipeFrameLayout(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context,new MySimpleOnGestureListener());
    }

    /**
     * Implement this method to intercept all touch screen motion events. This
     * allows you to watch events as they are dispatched to your children, and
     * take ownership of the current gesture at any point.
     *
     * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    /**
     * GestureDetector.SimpleOnGestureListener
     */
    private class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            // onFling() is not called does not return a true?
            // trueを返さないとonFling()が呼ばれない?
            // return true;
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float sX = e1.getX();
            float sY = e1.getY();
            float eX = e2.getX();
            float eY = e2.getY();
            float deltaX = eX - sX;
            float deltaY = eY - sY;

            if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > FLING_THRESHOLD) {
                // Left to Right
                if (e1.getX() < e2.getX()) {
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipe(SwipeFrameLayout.this, LEFT_TO_RIGHT);
                        return true;
                    }
                }
                // Right to Left
                if (e1.getX() > e2.getX()) {
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipe(SwipeFrameLayout.this, RIGHT_TO_LEFT);
                        return true;
                    }
                }
            }
            return false;
        }
    }

}