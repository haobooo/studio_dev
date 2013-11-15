package com.pipi.studio.dev.widget;



import com.pipi.studio.dev.R;
import com.pipi.studio.dev.util.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SlideLeftableView extends LinearLayout {
	private static final String TAG = "SlideLeftableView";
	
	private static final int mThreshold = 30;
	
	private GestureDetector mDetector;
	
	private View baseInfoView;
	private View deleteView;
	
	private float xDistance, yDistance, xLast, yLast, originX, originY;
	private int mWidth;
	
	private OnTapUpListener mOnTapUpListener;
	private OnPreAndPostListener mOnPreAndPostListener;
	private ExtraData mExtraData;
	
	public SlideLeftableView(Context context) {
		super(context);
		
		init(context);
	}
	
	public SlideLeftableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context);
	}
	
	public SlideLeftableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init(context);
	}
	
	public void setOnTapUpListener(OnTapUpListener listener) {
		mOnTapUpListener = listener;
	}
	
	private void init (Context context) {
		mDetector = new GestureDetector(context, new GestureListener());
	}
	
	public void setOnPreAndPostListener(OnPreAndPostListener listener) {
		mOnPreAndPostListener = listener;
	}
	
	public void setExtraData(ExtraData data) {
		mExtraData = data;
	}
	
	public void restoreScroll() {
		scrollTo(0, getScrollY());
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onLayout] left=" + left + ", top=" + top + ", right=" + right + ", bottom=" + bottom);
		baseInfoView.layout(left, top, right, bottom);
		
		// Get deleteView's width
		deleteView.measure(0, MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
		mWidth = deleteView.getMeasuredWidth();
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onLayout] mWidht=" + mWidth);
		deleteView.layout(right, top, right + mWidth, bottom);
		
		super.onLayout(changed, left, top, right, bottom);
		
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		baseInfoView = findViewById(R.id.item_content);
		deleteView = findViewById(R.id.delete_content);
		
		deleteView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnTapUpListener != null && mExtraData != null) {
					mOnTapUpListener.OnRightTailClick(mExtraData.category, mExtraData.id, mExtraData.partitionIndex);
				}
			}
			
		});
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
			
			if (mOnPreAndPostListener != null) {
				if (scrollX == 0)
					mOnPreAndPostListener.OnPreScroll();
			}
			
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
				
				if (mOnPreAndPostListener != null) {
					if (scrollX >= 0)
						mOnPreAndPostListener.OnAfterScroll(SlideLeftableView.this);
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
	
	public interface OnPreAndPostListener {
		public void OnPreScroll();
		public void OnAfterScroll(SlideLeftableView view);
	}
	
	public static class ExtraData {
		public String category;
		public int id;
		public int partitionIndex;
	}
}