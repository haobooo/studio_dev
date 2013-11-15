package com.pipi.studio.dev.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.base.BaseActivity;
import com.pipi.studio.dev.widget.SlideLeftableView;


public class MainActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SlideLeftableView view = (SlideLeftableView) LayoutInflater.from(this).inflate(R.layout.slide_left_layout, null);
		
		//TextView mainView = new TextView(this);
		//mainView.setText(this.getString(R.string.app_name));
		
		//View additionalView = LayoutInflater.from(this).inflate(R.layout.additional_layout, null);
		
		//view.setViews(mainView, additionalView);
		setContentView(view);
	}
	
}