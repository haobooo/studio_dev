package com.pipi.studio.dev.activity;

import java.io.IOException;

import com.pipi.studio.dev.R;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class SlashActivity extends Activity {
	private ImageView mSlashView;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Intent portal = new Intent(SlashActivity.this, PortalActivity.class);
			
			startActivity(portal);
			SlashActivity.this.finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slash_layout);
		
		mSlashView = (ImageView) findViewById(R.id.slash);
		
		buildSlash();
	}

	protected void onResume() {
		super.onResume();
		
		mHandler.sendEmptyMessageDelayed(0, 500);
	}
	
	private void buildSlash() {
		Bitmap map = null;
		try {
			map = BitmapFactory.decodeStream(getAssets().open("slash.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (map != null) {
			mSlashView.setImageBitmap(map);
		}
	}

}
