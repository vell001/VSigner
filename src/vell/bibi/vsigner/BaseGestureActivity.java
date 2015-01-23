package vell.bibi.vsigner;

import vell.bibi.vsigner.config.Constants;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class BaseGestureActivity extends BaseActivity implements
		OnTouchListener, OnGestureListener {

	protected GestureDetector mGestureDetector;
	protected View mFlingView;
	@Override
	public void initViews() {
		mFlingView = initFlingView();
		mGestureDetector = new GestureDetector(mContext, this);
	}

	@Override
	public void initListeners() {
		mFlingView.setOnTouchListener(this);
		mFlingView.setLongClickable(true);
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		System.out.println("onFling");
		if (e1.getX() - e2.getX() > Constants.FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > Constants.FLING_MIN_VELOCITY) {
			// Fling left
			flingLeft();
		} else if (e2.getX() - e1.getX() > Constants.FLING_MIN_DISTANCE
				&& Math.abs(velocityX) > Constants.FLING_MIN_VELOCITY) {
			// Fling right
			flingRight();
		}
		return false;
	}

	/**
	 * 获取手势view
	 * 
	 * @return
	 */
	protected abstract View initFlingView();
	/**
	 * 左滑
	 */
	protected abstract void flingLeft();
	/**
	 * 右滑
	 */
	protected abstract void flingRight();

	@Override
	public void onLongPress(MotionEvent arg0) {
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}
}
