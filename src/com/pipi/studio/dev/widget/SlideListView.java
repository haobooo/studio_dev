package com.pipi.studio.dev.widget;


import com.pipi.studio.dev.util.LogUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;


public class SlideListView extends ListView {
	private static final String TAG = "SlideListView";
	
	private float xDistance, yDistance, xLast, yLast;
	
	public SlideListView(Context context) {
		super(context);
	}
	
	public SlideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public SlideListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        switch (ev.getAction()) {  
            case MotionEvent.ACTION_DOWN:  
                xDistance = yDistance = 0f;  
                xLast = ev.getX();  
                yLast = ev.getY();  
                break;  
            case MotionEvent.ACTION_MOVE:  
                final float curX = ev.getX();  
                final float curY = ev.getY();  
                  
                xDistance += Math.abs(curX - xLast);  
                yDistance += Math.abs(curY - yLast);  
                xLast = curX;  
                yLast = curY;  
                
                if (LogUtil.IS_LOG) LogUtil.d(TAG, "xDistance=" + xDistance + "; yDistance=" + yDistance);
                if(xDistance > yDistance){  
                    return false;  
                }    
        }  
  
        return super.onInterceptTouchEvent(ev);  
    }
}