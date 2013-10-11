package com.pipi.studio.dev.widget;


import java.util.Random;

import com.pipi.studio.dev.R;

import android.app.Activity;
import android.content.Context;
import android.opengl.Visibility;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;


public class JumpFrameLayout extends RelativeLayout {
	private static final String TAG = "XXXFrameLayout";
	private static final boolean DEBUG = true;
	
	private static final Interpolator sInterpolator = new Interpolator() {
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t * t * t + 1.0f;
		}
	};
	
	private static final int MAX_SETTLE_DURATION = 600;
	
	
	private Scroller mScroller;
	private boolean mIsBeingDragged = true;
	private boolean mScrolling;
	
	protected int mActivePointerId = INVALID_POINTER;
	private static final int INVALID_POINTER = -1;
	
	protected VelocityTracker mVelocityTracker;
	
	private float mLastMotionX;
	private float mLastMotionY;
	private int mTouchSlop;
	
	private int mScreenHeight;
	
	private ImageView mContent;
	private TextView mBehind;
	
	private int[] imgs = {
		R.drawable.zaker,
		R.drawable.zaker1,
		R.drawable.zaker2,
	};
	
	public JumpFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public JumpFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		if (DEBUG) Log.d(TAG, "XXXFrameLayout constructor!");
		
		mScroller = new Scroller(context, sInterpolator);
		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
		
		// 获取屏幕的高度
		mScreenHeight = ((Activity)context).getWindowManager().getDefaultDisplay().getHeight();
		
		// 添加底层的View。
		LayoutParams behindParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mBehind = new TextView(context);
		mBehind.setText(R.string.hello_world);
		mBehind.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
		addView(mBehind, behindParams);
		
		// 添加前台View
		LayoutParams aboveParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mContent = new ImageView(context);
		mContent.setImageResource(imgs[new Random().nextInt(3)]);
		mContent.setScaleType(ScaleType.FIT_XY);
		addView(mContent, aboveParams);
	}
	
	private int getPointerIndex(MotionEvent ev, int id) {
		int activePointerIndex = MotionEventCompat.findPointerIndex(ev, id);
		if (activePointerIndex == -1)
			mActivePointerId = INVALID_POINTER;
		return activePointerIndex;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

		if (DEBUG) Log.d(TAG, "onInterceptTouchEvent action=" + action);

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			determineDrag(ev);
			break;
		case MotionEvent.ACTION_DOWN:
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			if (mActivePointerId == INVALID_POINTER)
				break;
			mLastMotionX = MotionEventCompat.getX(ev, index);
			mLastMotionY = MotionEventCompat.getY(ev, index);
			
			break;
		case MotionEventCompat.ACTION_POINTER_UP:
			break;
		}

		return true;
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

//		if (mVelocityTracker == null) {
//			mVelocityTracker = VelocityTracker.obtain();
//		}
//		mVelocityTracker.addMovement(ev);
		
		switch(ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			completeScroll();
			
			// Remember where the motion event started
			int index = MotionEventCompat.getActionIndex(ev);
			mActivePointerId = MotionEventCompat.getPointerId(ev, index);
			mLastMotionY = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			this.clearAnimation();
			if (!mIsBeingDragged) {	
				determineDrag(ev);
			}
			
			// 如果处于拖拽的过程中，根据Y轴的移动差值来滚动前面View（即mContent）到相应位置。
			if (mIsBeingDragged) {
				final int activePointerIndex = getPointerIndex(ev, mActivePointerId);
				if (mActivePointerId == INVALID_POINTER)
					break;
				
				final float y = MotionEventCompat.getY(ev, activePointerIndex);
				final float deltaY = mLastMotionY - y;
				
				mLastMotionY = y;
				
				float oldScrollY = mContent.getScrollY();
				float scrollY = oldScrollY + deltaY;
				
				if (DEBUG) Log.d(TAG, "onTouchEvent scrollY=" + oldScrollY + "; deltaY=" + deltaY);
				// 限制往屏幕下方滚动
				if (scrollY <= 0) {
					return true;
				} 
				
				mLastMotionY += scrollY - (int) scrollY;
				
				if (mContent.getVisibility() == View.VISIBLE) {
					mContent.scrollBy(0, (int) deltaY);
				}
				
			}
			
			
			break;
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				mActivePointerId = INVALID_POINTER;
				endDrag();
				
				// Start animation.
				startAnimation();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			if (mIsBeingDragged) {
				mActivePointerId = INVALID_POINTER;
				endDrag();
			}
			break;
		case MotionEventCompat.ACTION_POINTER_DOWN: {
			final int indexx = MotionEventCompat.getActionIndex(ev);
			mLastMotionY = MotionEventCompat.getY(ev, indexx);
			mActivePointerId = MotionEventCompat.getPointerId(ev, indexx);
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			int pointerIndex = getPointerIndex(ev, mActivePointerId);
			if (mActivePointerId == INVALID_POINTER)
				break;
			mLastMotionY = MotionEventCompat.getY(ev, pointerIndex);
			break;
		}
		//return mGestureDetector.onTouchEvent(event);
		
		return true;
	}
	
	private void startAnimation() {
		if (mContent.getScrollY() > mScreenHeight/2) {
			// 如果向上滑动超过屏幕一半，则向上滑动，滚出屏幕。
			moveToTop();
		} else {
			// 如果向上滑动未超过屏幕一半，则向下滑动，执行反弹动画。
			moveToBottom();
		}
	}
	
	private void moveToTop() {
		// 计算动画需要向上平移的距离。
		int distance = mContent.getHeight() - mContent.getScrollY();
		
		Animation a = new TranslateAnimation(0.0f, 0.0f, (float) 0.0, -distance);
		a.setDuration(1000);
		a.setRepeatCount(0);
		a.setInterpolator(AnimationUtils.loadInterpolator(this.getContext(),
                android.R.anim.accelerate_interpolator));
		a.setInterpolator(sInterpolator);
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {
			}

			@Override
			public void onAnimationEnd(Animation paramAnimation) {
				mContent.scrollTo(0, mScreenHeight);
				mContent.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {
			}
			
		});
		
		// 前台View启动动画
		mContent.startAnimation(a);
	}
	
	private void moveToBottom() {
		// 计算动画需要向下移动的距离
		int scrollY = mContent.getScrollY();
		if (DEBUG) Log.d(TAG, "scrollY=" + scrollY);
		
		// 先把前台View移动到动画结束时的位置，然后再执行一个从上往下的动画。
		// 这样能避免使用下面①处的动画所产生的闪屏和半屏现象。
		mContent.scrollTo(0, 0);
		
		//Animation a = new TranslateAnimation(0.0f, 0.0f, (float) 0.0, Math.abs(scrollY)); // ①
		Animation a = new TranslateAnimation(0.0f, 0.0f, -Math.abs(scrollY), 0);
		a.setInterpolator(AnimationUtils.loadInterpolator(this.getContext(),
                android.R.anim.bounce_interpolator));
		a.setDuration(1000);
		a.setRepeatCount(0);
		
		a.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation paramAnimation) {
			}

			@Override
			public void onAnimationEnd(Animation paramAnimation) {
				// 清除动画，否则会影响到后面的滑动。
				mContent.clearAnimation();			
			}

			@Override
			public void onAnimationRepeat(Animation paramAnimation) {
			}
			
		});
		
		// 启动动画。
		mContent.startAnimation(a);

	}
	
	private void endDrag() {
		mIsBeingDragged = false;
		mActivePointerId = INVALID_POINTER;
		
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}
	
	private void startDrag() {
		mIsBeingDragged = true;
	}
	
	private void determineDrag(MotionEvent ev) {
		final int activePointerId = mActivePointerId;
		final int pointerIndex = getPointerIndex(ev, activePointerId);
		if (activePointerId == INVALID_POINTER)
			return;
		final float x = MotionEventCompat.getX(ev, pointerIndex);
		final float dx = x - mLastMotionX;
		final float xDiff = Math.abs(dx);
		final float y = MotionEventCompat.getY(ev, pointerIndex);
		final float dy = y - mLastMotionY;
		final float yDiff = Math.abs(dy);
		
		if ((yDiff > mTouchSlop/6) && (yDiff > xDiff)) {		
			startDrag();
			mLastMotionX = x;
			mLastMotionY = y;
		} 
	}
	
//	@Override
//	public void computeScroll() {
//		if (!mScroller.isFinished()) {
//			if (mScroller.computeScrollOffset()) {
//				int oldX = getScrollX();
//				int oldY = getScrollY();
//				int x = mScroller.getCurrX();
//				int y = mScroller.getCurrY();
//
//				if (oldX != x || oldY != y) {
//					scrollTo(x, y);
//				}
//				
//				return;
//			}
//		}
//		
//		completeScroll();
//	}
	
	private void completeScroll() {
		boolean needPopulate = mScrolling;
		if (needPopulate) {
			// Done with scroll, no longer want to cache view drawing.
			mScroller.abortAnimation();
			int oldX = getScrollX();
			int oldY = getScrollY();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			if (oldX != x || oldY != y) {
				scrollTo(x, y);
			}
		}
		mScrolling = false;
	}
	
}