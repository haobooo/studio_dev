package com.pipi.studio.dev.activity;

import com.pipi.studio.dev.base.BaseActivity;

import android.os.Bundle;
import android.widget.TextView;


public class MoreActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView view = new TextView(this);
		view.setText("MoreActivity");
		setContentView(view);
	}
}