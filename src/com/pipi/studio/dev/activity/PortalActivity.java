package com.pipi.studio.dev.activity;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.base.BaseTabActivity;
import com.pipi.studio.dev.util.LogUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;


public class PortalActivity extends BaseTabActivity {
	private static final String TAG = "PortalActivity";
	
	private String currentTab = null;
	private TabHost tabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "onCreate");
		
		setContentView(R.layout.portal_tab_layout);
		
		initView();

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "onNewIntent");
		setIntent(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		currentTab = getIntent().getStringExtra("CURRENT_TAB");
		
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "onResume currentTab=" + currentTab);
		if (currentTab != null && currentTab.length() > 0) {
			tabHost.setCurrentTabByTag(currentTab);
			currentTab = null;
			
			Intent intent = getIntent();
			intent.putExtra("CURRENT_TAB", "");
			setIntent(intent);
		}
	}
	
	private void initView() {
		tabHost = getTabHost();
		
		tabHost.addTab(tabHost.newTabSpec("MainActivity")
				.setIndicator(LayoutInflater.from(this).inflate(R.layout.tab_indicator_home, null))
				.setContent(new Intent(this, MainActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("PostMagazineActivity")
				.setIndicator(LayoutInflater.from(this).inflate(R.layout.tab_indicator_post, null))
				.setContent(new Intent(this, PostMagazineActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("BookmarkActivity")
				.setIndicator(LayoutInflater.from(this).inflate(R.layout.tab_indicator_bookmark, null))
				.setContent(new Intent(this, BookmarkActivity.class)));
		
		Bundle extras = new Bundle();
		extras.putIntArray("counts", new int[]{1,2,3,4,5});
		extras.putStringArray("names", new String[]{"新浪", "搜狐", "网易", "百度", "腾讯"});
		extras.putBooleanArray("showIfEmpty", new boolean[] {true, true, true, true, true});
		extras.putBooleanArray("headers", new boolean[] {false, false, false, false, false});
		extras.putIntArray("delays", new int[]{100, 200, 300, 400, 500});
		
		Intent intent = new Intent(this, PinnedHeaderListDemoActivity.class);
		intent.putExtras(extras);
		tabHost.addTab(tabHost.newTabSpec("MoreActivity")
				.setIndicator(LayoutInflater.from(this).inflate(R.layout.tab_indicator_more, null))
				.setContent(intent));
		
	}
}