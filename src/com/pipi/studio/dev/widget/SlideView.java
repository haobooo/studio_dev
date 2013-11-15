package com.pipi.studio.dev.widget;


import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.util.AsyncImageLoader;
import com.pipi.studio.dev.util.AsyncImageLoader.ImageCallback;
import com.pipi.studio.dev.util.LogUtil;


public class SlideView extends FrameLayout {
	private static final String TAG = "SlideView";
	
	private Context mContext;
	
	private ViewPager mViewPager;
	private ViewPageIndicator mIndicator;
	
	// the size of the pictures to be shown.
	private int mSlidePicWidth = 0;
	private int mSlidePicHeight = 0;
	
	// the count ot the pictures to be shown.
	private int mSlidePicCount = 0;
	private String[] mPicUrls;
	
	private ArrayList<ImageView> mPicViews = new ArrayList<ImageView>();
	
	private ViewPagerAdapter mAdapter;
	
	private ScheduledExecutorService scheduledExecutorService;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "[handler] handleMessage");
			int position = mViewPager.getCurrentItem() + 1;
			if (position >= mSlidePicCount) {
				position = 0;
			}
			mViewPager.setCurrentItem(position);// change to current image
		};
	};
	
	public SlideView(Context context) {
		this(context, null);
	}
	
	public SlideView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SlideView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		
		init(context);
	}
	
	/**
	 * Add ViewPager and ViewPagerIndicator to the SlideView.
	 */
	private void init(Context context) {
		mViewPager = new ViewPager(context); 
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mViewPager, lp);
		
		mIndicator = new ViewPageIndicator(context);
		lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		addView(mIndicator, lp);
		
		getViewTreeObserver().addOnGlobalLayoutListener(
			new ViewTreeObserver.OnGlobalLayoutListener() {
	
				@Override
				public void onGlobalLayout() {
					SlideView.this.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					
					mSlidePicWidth = mViewPager.getWidth();
					mSlidePicHeight = mViewPager.getHeight();
					if (LogUtil.IS_LOG) LogUtil.d(TAG, "mSlidePicWidth=" + mSlidePicWidth + "; mSlidePicHeight=" + mSlidePicHeight);
					
					startShow();
				}
				
			});
		
		mAdapter = new ViewPagerAdapter();
		mViewPager.setAdapter(mAdapter);
	}
	
	/**
	 * Set the resource to show in the slide.
	 */
	public void setResources(int count, String[] urls) {
		mSlidePicCount = count;
		mPicUrls = urls;
		
		if (mSlidePicWidth > 0 && mSlidePicHeight > 0) {
			startShow();
		}
	}
	
	// add pictures to ViewPager and start to show.
	private void startShow() {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "startShow");
		
		if (mSlidePicCount == 0 || mPicUrls == null || mPicUrls.length < mSlidePicCount) {
			if (LogUtil.IS_LOG) LogUtil.d(TAG, "[startShow] params error!");
			return;
		}
		
		//attach the urls to imageviews.
		for (int i = 0; i < mSlidePicCount; i++) {
			final ImageView imageView = new ImageView(mContext);
			
			String url = mPicUrls[i];
			if(!TextUtils.isEmpty(url)){
				Bitmap bitmap = AsyncImageLoader.loadDrawable(url, mSlidePicWidth, mSlidePicHeight, new ImageCallback() {

					@Override
					public void imageLoaded(Bitmap imageDrawable,
							String imageUrl) {
						imageView.setImageBitmap(imageDrawable);
					}        			
	    		});	    		
	    		if (bitmap != null) {
	    			imageView.setImageBitmap(bitmap);
	    		} else {
	    			imageView.setImageResource(R.drawable.ic_default);
	    		}
			}
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	                LinearLayout.LayoutParams.WRAP_CONTENT));
			mPicViews.add(imageView);
		}
		mAdapter.notifyDataSetChanged();
		
		//init indicator.
		mIndicator.setPointCount(mSlidePicCount);
		
		//start auto slide.
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3, TimeUnit.SECONDS);
		
		// set page
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			@Override
			public void onPageSelected(int position) {
				mIndicator.setPoint(position);
			}
			
		});
	}
	
	@Override
	protected void onDetachedFromWindow() {
		//stop auto slide here.
		scheduledExecutorService.shutdown();
	}
	
	private class ScrollTask implements Runnable {

		public void run() {
			synchronized (mViewPager) {
				handler.obtainMessage().sendToTarget();
			}
		}
	}
	
	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPicViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == (View)obj;
		}
		
		@Override
        public Object instantiateItem(View container, int position) {
			View page = mPicViews.get(position);
			((ViewGroup)container).addView(page);
			
			return page;
		}
		
		@Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewGroup) container).removeView((View) object);
        }
	}
}