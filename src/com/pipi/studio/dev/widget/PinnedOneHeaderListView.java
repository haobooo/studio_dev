package com.pipi.studio.dev.widget;


/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.pipi.studio.dev.util.LogUtil;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * A ListView that maintains a header pinned at the top of the list. The pinned
 * header can be pushed up and dissolved as needed.
 */
public class PinnedOneHeaderListView extends ListView implements OnScrollListener{
	private static final String TAG = "PinnedOneHeaderListView";
	
	/**
	 * Adapter interface. The list adapter must implement this interface.
	 */
	public interface PinnedHeaderAdapter {

		/**
		 * Pinned header state: don't show the header.
		 */
		public static final int PINNED_HEADER_GONE = 0;

		/**
		 * Pinned header state: show the header at the top of the list.
		 */
		public static final int PINNED_HEADER_VISIBLE = 1;

		/**
		 * Pinned header state: show the header. If the header extends beyond
		 * the bottom of the first shown element, push it up and clip.
		 */
		public static final int PINNED_HEADER_PUSHED_UP = 2;

		/**
		 * Computes the desired state of the pinned header for the
		 * given position of the first visible list item. Allowed return values
		 * are {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
		 * {@link #PINNED_HEADER_PUSHED_UP}.
		 */
		int getPinnedHeaderState(int position);

		/**
		 * Configures the pinned header view to match the first
		 * visible list item.
		 * 
		 * @param header
		 *            pinned header view.
		 * @param position
		 *            position of the first visible list item.
		 * @param alpha
		 *            fading of the header view, between 0 and 255.
		 */
		void configurePinnedHeader(View header, int position);
		
		public int getNextPartationPosition(int position);
	}

	private static final int MAX_ALPHA = 255;

	private PinnedHeaderAdapter mAdapter;
	private View mHeaderView;
	private boolean mHeaderViewVisible;

	private int mHeaderViewWidth;
	private int mHeaderViewHeight;
	private boolean mHeadViewLayouted = false;

	public PinnedOneHeaderListView(Context context) {
		this(context, null);
	}

	public PinnedOneHeaderListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PinnedOneHeaderListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		super.setOnScrollListener(this);
	}

	public void setPinnedHeaderView(View view) {
		mHeaderView = view;
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[setPinnedHeaderView] mHeaderView="+mHeaderView);
		// Disable vertical fading when the pinned header is present
		// TODO change ListView to allow separate measures for top and bottom
		// fading edge;
		// in this particular case we would like to disable the top, but not the
		// bottom edge.
		if (mHeaderView != null) {
			setFadingEdgeLength(0);
		}
		// requestLayout();
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (PinnedHeaderAdapter) adapter;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[onLayout] mHeaderView="+mHeaderView + "; mHeadViewLayouted=" + mHeadViewLayouted + "; mHeaderViewWidth=" + mHeaderViewWidth + "; mHeaderViewHeight=" + mHeaderViewHeight);
		if (mHeaderView != null && !mHeadViewLayouted) {
			mHeaderView.layout(0 + getPaddingLeft(), 0+getPaddingTop(), mHeaderViewWidth + getPaddingLeft(), mHeaderViewHeight);
			//configureHeaderView(getFirstVisiblePosition());
			
			mHeadViewLayouted = true;
		}
	}

	public void configureHeaderView(int firstVisibleItem, int visibleItemCount) {
		if (mHeaderView == null) {
			return;
		}
		
		int state = mAdapter.getPinnedHeaderState(firstVisibleItem);
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "[configureHeaderView] state=" + state);
		
		switch(state) {
		case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE:
			mAdapter.configurePinnedHeader(mHeaderView, firstVisibleItem);
			mHeaderView.layout(0 + getPaddingLeft(), 0+getPaddingTop(), mHeaderViewWidth+getPaddingLeft(), mHeaderViewHeight);
			mHeaderViewVisible = true;
			break;
		case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP:
			//计算可见的下一个Partation的顶部和mHeaderView底部的关系，并据此来决定是否调整mHeaderView.
			int nextPartationIndex = mAdapter.getNextPartationPosition(firstVisibleItem);
			int lastVisibleItem = visibleItemCount + firstVisibleItem - 1;
			
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "[configureHeaderView] 	firstVisibleItem=" + firstVisibleItem);
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "[configureHeaderView] 	nextPartationIndex=" + nextPartationIndex);
			
			if (nextPartationIndex >= firstVisibleItem && nextPartationIndex <= lastVisibleItem ) {
				int viewIndex = nextPartationIndex - firstVisibleItem;
				View headerView = getChildAt(viewIndex);
				
				int bottom = mHeaderView.getBottom();
				int nextHeaderTop = headerView.getTop();
				int nextHeaderBottom = headerView.getBottom();
				
				if (nextHeaderTop <= mHeaderViewHeight) {
					int y;
					y = nextHeaderTop - mHeaderViewHeight;
					
					if (nextHeaderBottom <= mHeaderViewHeight) {
						y = 0;
						
						mAdapter.configurePinnedHeader(mHeaderView, nextPartationIndex);
					} else {
						mAdapter.configurePinnedHeader(mHeaderView, firstVisibleItem);
					}
					mHeaderView.layout(0 + getPaddingLeft(), y, mHeaderViewWidth + getPaddingLeft(), mHeaderViewHeight
							+ y);
					mHeaderViewVisible = true;
				} else {
					mAdapter.configurePinnedHeader(mHeaderView, firstVisibleItem);
					mHeaderView.layout(0 + getPaddingLeft(), 0+getPaddingTop(), mHeaderViewWidth + getPaddingLeft(), mHeaderViewHeight);
				}
			}
//			View firstView = getChildAt(0);
//			int bottom = firstView.getBottom();
//			int headerHeight = mHeaderView.getHeight();
//			int y;
//			if (bottom < headerHeight) {
//				y = (bottom - headerHeight);
//			} else {
//				y = 0;
//			}
//			mAdapter.configurePinnedHeader(mHeaderView, firstVisibleItem);
//			if (mHeaderView.getTop() != y) {
//				paddingLeft = this.getPaddingLeft();
//				mHeaderView.layout(0, y, mHeaderViewWidth + paddingLeft+getPaddingRight(), mHeaderViewHeight
//						+ y);
//			}
//			mHeaderViewVisible = true;
			break;
		}
		
		
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		configureHeaderView(firstVisibleItem, visibleItemCount);
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
