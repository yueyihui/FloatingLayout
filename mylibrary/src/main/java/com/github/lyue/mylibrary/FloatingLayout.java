package com.github.lyue.mylibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * TODO: document your custom view class.
 */
public class FloatingLayout extends FrameLayout {
    private static final String TAG = FloatingLayout.class.getSimpleName();
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private float mActionDownX;
    private float mActionDownY;

    private float mMoveX;
    private float mMoveY;

    private VelocityTracker mVelocityTracker;

    private int MOVE_STATE_MOVE_FROM_LEFT = 0x000F;
    private int MOVE_STATE_MOVE_FROM_RIGHT = 0x00F0;
    private int MOVE_STATE_MOVE_FROM_TOP = 0x0F00;
    private int MOVE_STATE_MOVE_FROM_BOTTOM = 0xF000;
    private int MOVE_STATE_STOP = 0x0000;

    private int mMoveState = MOVE_STATE_STOP;

    private int NONE = 0x0000;
    private int TOP = 0x000F;
    private int LEFT = 0x00F0;
    private int RIGHT = 0x0F00;
    private int BOTTOM = 0xF000;
    private int mBeyondCategory = NONE;

    private static final int DELAY_TIME = 250;

    private int mTouchSlop = 6;
    private float mOtherSideX;
    private float mOtherSideY;
    private Rect mBounds;
    private Rect mHitRect;
    private Rect mCurrentHitRect; //Current position from parent
    private ViewGroup mBoundsView;

    public FloatingLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public FloatingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public FloatingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch(ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mMoveState == MOVE_STATE_STOP) {
                    setClickable(true);
                }
                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(ev);
                mActionDownX = ev.getX();
                mActionDownY = ev.getY();
                getHitRect(mCurrentHitRect);
                break;
            case MotionEvent.ACTION_MOVE:
                return Math.abs(ev.getX()- mActionDownX) > mTouchSlop
                        || Math.abs(ev.getY()- mActionDownY) > mTouchSlop;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(1000);
                mMoveX = event.getX()- mActionDownX;
                mMoveY = event.getY()- mActionDownY;
                checkBeyondBounds();
                if (mTouchSlop < mMoveX || -mTouchSlop > mMoveX) {
                    this.animate()
                            .xBy(mMoveX)
                            .setDuration(0)
                            .start();
                    if (mMoveX > 0) {
                        mMoveState |= MOVE_STATE_MOVE_FROM_LEFT;
                    } else {
                        mMoveState |= MOVE_STATE_MOVE_FROM_RIGHT;
                    }
                }
                if (mTouchSlop < mMoveY || -mTouchSlop > mMoveY) {
                    this.animate()
                            .yBy(mMoveY)
                            .setDuration(0)
                            .start();
                    if (mMoveY > 0) {
                        mMoveState |= MOVE_STATE_MOVE_FROM_TOP;
                    } else {
                        mMoveState |= MOVE_STATE_MOVE_FROM_BOTTOM;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isBeyond()) {
                    if (isTop()) {
                        this.animate()
                                .y(mBounds.top)
                                .setDuration(DELAY_TIME)
                                .start();
                    }
                    if (isLeft()) {
                        this.animate()
                                .x(mBounds.left)
                                .setDuration(DELAY_TIME)
                                .start();
                    }
                    if (isRight()) {
                        this.animate()
                                .x(mBounds.right - (mHitRect.right - mHitRect.left))
                                .setDuration(DELAY_TIME)
                                .start();
                    }
                    if (isBottom()) {
                        this.animate()
                                .y(mBounds.bottom - (mHitRect.bottom - mHitRect.top))
                                .setDuration(DELAY_TIME)
                                .start();
                    }
                } else if (isBeyondMiddleLine()) {
                    moveToHorizontalOtherSide();
                    /*if (compareHorizontalToVertical()) {
                        moveToHorizontalOtherSide();
                    } else {
                        moveToVerticalOtherSide();
                    }*/
                } else {
                    resetPosition();
                }
                if (mMoveState != MOVE_STATE_STOP) {
                    setClickable(false);
                    mMoveState = MOVE_STATE_STOP;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void resetPosition() {
        animate().
                x(mCurrentHitRect.left - mBounds.left).
                y(mCurrentHitRect.top - mBounds.top).
                setDuration(DELAY_TIME).start();
    }

    private void moveToVerticalOtherSide() {
        animate().
                y(mOtherSideY).
                setDuration(DELAY_TIME).start();
    }

    private void moveToHorizontalOtherSide() {
        animate().
                x(mOtherSideX).
                setDuration(DELAY_TIME).start();
    }

    /**
     * if return true, that mean is horizontal is trend
     * else vertical is.
     * @return
     */
    private boolean compareHorizontalToVertical() {
       return Math.abs(mVelocityTracker.getXVelocity()) > Math.abs(mVelocityTracker.getYVelocity());
    }

    private boolean isBeyondMiddleLine() {
        /*boolean h = isBeyondHorizontal();
        boolean v = isBeyondVertical();
        return  h || v;*/
        return isBeyondHorizontal();
    }

    private boolean isBeyondHorizontal() {
        Rect hit = new Rect();
        getHitRect(hit);
        if(isFromLeft() && hit.centerX() >= mBounds.centerX()) {
            mOtherSideX = mBounds.right - (hit.right - hit.left);
            return true;
        } else if (isFromRight() && hit.centerX() <= mBounds.centerX()) {
            mOtherSideX = mBounds.left;
            return true;
        } else {
            return false;
        }
    }

    private boolean isFromRight() {
        return (mMoveState & MOVE_STATE_MOVE_FROM_RIGHT) == MOVE_STATE_MOVE_FROM_RIGHT;
    }

    private boolean isFromLeft() {
        return (mMoveState & MOVE_STATE_MOVE_FROM_LEFT) == MOVE_STATE_MOVE_FROM_LEFT;
    }

    private boolean isBeyondVertical() {
        Rect hit = new Rect();
        getHitRect(hit);
        if(isFromTop() && hit.centerY() >= mBounds.centerY()) {
            mOtherSideY = mBounds.bottom - (hit.bottom - hit.top);
            return true;
        } else if (isFromBottom() && hit.centerY() <= mBounds.centerY()) {
            mOtherSideY = mBounds.top;
            return true;
        } else {
            return false;
        }
    }
    private boolean isFromTop() {
        return (mMoveState & MOVE_STATE_MOVE_FROM_TOP) == MOVE_STATE_MOVE_FROM_TOP;
    }
    private boolean isFromBottom() {
        return (mMoveState & MOVE_STATE_MOVE_FROM_BOTTOM) == MOVE_STATE_MOVE_FROM_BOTTOM;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mBoundsView.getLocalVisibleRect(mBounds);
        }
    }

    private boolean checkBeyondBounds() {
        getHitRect(mHitRect);
        if (Debug.isDebuggerConnected()) {
            Log.w(TAG, "mHitRect = " + mHitRect.toString());
        }
        int temporaryLeft = mHitRect.left + (int)mMoveX;
        int temporaryTop = mHitRect.top + (int)mMoveY;
        int temporaryRight = mHitRect.right + (int)mMoveX;
        int temporaryBottom = mHitRect.bottom + (int)mMoveY;

        resetBeyondCategoryCheck();

        if(mBounds.left >= temporaryLeft) {
            mBeyondCategory |= LEFT;
        }
        if (mBounds.top >= temporaryTop) {
            mBeyondCategory |= TOP;
        }
        if (mBounds.right <= temporaryRight) {
            mBeyondCategory |= RIGHT;
        }
        if (mBounds.bottom <= temporaryBottom) {
            mBeyondCategory |= BOTTOM;
        }
        if (isBeyond()) {
            if (Debug.isDebuggerConnected()) {
                Log.d(TAG, "beyond category is " + String.format("%x", mBeyondCategory));
            }
            return true;
        } else {
            if (Debug.isDebuggerConnected()) {
                Log.d(TAG, "Move inside ViewGroup");
            }
            return false;
        }
    }

    private boolean isTop() {
        return (mBeyondCategory & TOP) == TOP;
    }

    private boolean isLeft() {
        return (mBeyondCategory & LEFT) == LEFT;
    }

    private boolean isRight() {
        return (mBeyondCategory & RIGHT) == RIGHT;
    }

    private boolean isBottom() {
        return (mBeyondCategory & BOTTOM) == BOTTOM;
    }

    private boolean isBeyond() {
        return mBeyondCategory != NONE;
    }

    private void resetBeyondCategoryCheck() {
        mBeyondCategory = NONE;
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.FloatingLayout, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.FloatingLayout_exampleString);
        mExampleColor = a.getColor(
                R.styleable.FloatingLayout_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.FloatingLayout_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.FloatingLayout_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.FloatingLayout_exampleDrawable);
            if (mExampleDrawable != null) {
                mExampleDrawable.setCallback(this);
            }
        }

        a.recycle();
        mHitRect = new Rect();
        mBounds = new Rect();
        mCurrentHitRect = new Rect();
    }

    public void setMoveBounds(ViewGroup boundsView) {
        mBoundsView = boundsView;
    }
}
