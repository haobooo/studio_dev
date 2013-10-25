package com.pipi.studio.dev.widget;


import com.pipi.studio.dev.util.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


public class SlideLeftableView extends LinearLayout {
	private static final String TAG = "SlideLeftableView";
	
	private GestureDetector mDetector;
	
	private View mainView;
	private View additionalView;
	
	private int mWidth;
	
	private OnTapUpListener mOnTapUpListener;
	private ExtraData mExtraData;
	
	public SlideLeftableView(Context context) {
		this(context, null);
	}
	
	public SlideLeftableView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SlideLeftableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setOrientation(HORIZONTAL);
		
		mDetector = new GestureDetector(context, new GestureListener());
	}
	
	public void setOnTapUpListener(OnTapUpListener listener) {
		mOnTapUpListener = listener;
	}
	
	public void setExtraData(ExtraData data) {
		mExtraData = data;
	}
	
	public void restoreScroll() {
		scrollTo(0, getScrollY());
	}
	
	public void setViews(View main, View additional) {
		if (mainView != null) {
			this.removeView(mainView);
		}
		mainView = main;
		addView(mainView);
		
		if (additionalView != null) {
			this.removeView(additionalView);
		}
		additionalView = additional;
		addView(additionalView);
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onLayout] left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
		
		final int width = right - left;
		final int height = bottom - top;
		
		mainView.layout(0, 0, width, height);
		
		// Get deleteView's width
		additionalView.measure(0, MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
		mWidth = additionalView.getMeasuredWidth();
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onLayout] mWidht=" + mWidth + ", mHeight=" + additionalView.getMeasuredHeight());
		additionalView.layout(width, 0, width + mWidth, bottom);
		additionalView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnTapUpListener != null && mExtraData != null) {
					mOnTapUpListener.OnRightTailClick(mExtraData.category, mExtraData.id, mExtraData.partitionIndex);
				}
			}
			
		});
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
//		baseInfoView = findViewById(R.id.item_content);
//		deleteView = findViewById(R.id.delete_content);
//		
//		deleteView.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mOnTapUpListener != null && mExtraData != null) {
//					mOnTapUpListener.OnRightTailClick(mExtraData.category, mExtraData.id, mExtraData.partitionIndex);
//				}
//			}
//			
//		});
	}
	
	/**
	 * Should NOT override the onInterceptTouchEvent(), otherwise the children
	 * will NOT receiver OnClick message.
	 */
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		switch (ev.getAction() & MotionEvent.ACTION_MASK) {
//		case MotionEvent.ACTION_DOWN:
//		case MotionEvent.ACTION_MOVE:
//			return true;
//		
//		}
//		return false;
//	}
	
	@Override  
    public boolean onTouchEvent(MotionEvent ev) {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onTouchEvent] ev=" + ev);
		
		if(mDetector.onTouchEvent(ev)) return true;
		
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			xDistance = yDistance = 0f;  
//            xLast = ev.getX();  
//            yLast = ev.getY();
//            originX = ev.getX();
//            originY = ev.getY();
//            
//			return true;
//		case MotionEvent.ACTION_MOVE:
//			final float curX = ev.getX();  
//            final float curY = ev.getY();  
//            
//            float xOffset = curX - originX;
//            float yOffset = curY - originY;
//            
//            if (Math.abs(xOffset) < mThreshold || Math.abs(xOffset) < Math.abs(yOffset)) {
//            	break;
//            }
//            
//            xDistance = curX - xLast;  
//            yDistance = curY - yLast;
//            xLast = curX;  
//            yLast = curY;
//
//            if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onTouchEvent] xDistance=" + xDistance);
//            if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onTouchEvent] getScrollX()=" + getScrollX());
//            
//            if (xDistance < 0 ) {
//            	// slide from right to left.
//            	if (getScrollX() >= mWidth) {
//            		break;
//            	}
//            	
//            	if (getScrollX() + (-(int)xDistance) < mWidth) {
//            		scrollBy(-(int)xDistance, 0);
//            	} else {
//            		int offset = mWidth - getScrollX();
//            		scrollBy(offset, 0);
//            	}
//
//            } else {
//            	// slide from left to right.
//            	int scrollX = getScrollX();
//            	if (scrollX <= 0) {
//            		break;
//            	}
//            	
//            	if (scrollX - xDistance >= 0) {
//            		scrollBy(-(int)xDistance, 0);
//            	} else {
//            		scrollBy(-scrollX, 0);
//            	}
//            	
//            }
//            return true;
//		}
		
		return super.onTouchEvent(ev);
		
	}
	
	public class GestureListener implements GestureDetector.OnGestureListener{
		private static final int SWIPE_MIN_DISTANCE = 120;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
 
		public boolean onDown(MotionEvent e) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "onDown");
			return true;
		}

		public void onShowPress(MotionEvent e) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "onShowPress");
		}

		public boolean onSingleTapUp(MotionEvent e) {
			int scrollX = getScrollX();
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "onSingleTapUp getScrollX=" + scrollX);
			if ((scrollX == 0) && (mOnTapUpListener != null) && (mExtraData != null)) {
				mOnTapUpListener.OnContentClick(mExtraData.category, mExtraData.id);
			}
			
			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "onScroll");
			float xOffset = e2.getX() - e1.getX();
			float yOffset = e2.getY() - e1.getY();
			float xDistance = Math.abs(xOffset);
			float yDistance = Math.abs(yOffset);
			int scrollX = getScrollX();
			int scrollY = getScrollY();
			int nextScrollX;
			
			if (xDistance > yDistance) {
				if (xOffset < 0) {
					// slide from right to left.
					nextScrollX = (int) (scrollX + xDistance);
					if (nextScrollX > mWidth) {
						nextScrollX = mWidth;
					}
					
					scrollTo(nextScrollX, scrollY);
				} else {
					// slide from left to right.
					nextScrollX = (int) (scrollX - xDistance);
					if (nextScrollX < 0) {
						nextScrollX = 0;
					}
					
					scrollTo(nextScrollX, scrollY);
				}
				
				return true;
			}
			return false;
		}

		public void onLongPress(MotionEvent e) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "onLongPress");
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "onSingleTapUp");
			return false;
		}
	}
	
	public interface OnTapUpListener {
		public void OnContentClick(String category, int id);
		public void OnRightTailClick(String category, int id, int partitionIndex);
	}
	
	public static class ExtraData {
		public String category;
		public int id;
		public int partitionIndex;
	}
}