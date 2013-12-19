package com.pipi.studio.dev.net;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.util.SystemInfoUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class URLDrawable extends BitmapDrawable {
	protected Drawable drawable;
	
	public URLDrawable(Context context) {
		this.setBounds(SystemInfoUtils.getDefaultImageBounds(context));
		
		drawable = context.getResources().getDrawable(R.drawable.ic_default);
		drawable.setBounds(SystemInfoUtils.getDefaultImageBounds(context));
	}
	
	@Override
	public void draw(Canvas canvas) {
		Log.d("test", "this=" + this.getBounds());
		if (drawable != null) {
			Log.d("test", "draw=" + drawable.getBounds());
			drawable.draw(canvas);
		}
	}
	
}