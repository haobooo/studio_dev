package com.pipi.studio.dev.activity;

import java.util.ArrayList;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.adapter.BaseListAdapter;
import com.pipi.studio.dev.base.BaseActivity;
import com.pipi.studio.dev.widget.SlideLeftableView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class PostMagazineActivity extends BaseActivity {
	
	private ListView mListView;
	private MyAdapter mAdapter;
	private ArrayList<String> mData = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.widget_layout);
		
		initViews();
	}
	
	private void initViews() {
		setTitle(this.getString(R.string.tab_post));
		
		mListView = (ListView) findViewById(android.R.id.list);
		mAdapter = new MyAdapter(this);
		mListView.setAdapter(mAdapter);
		
		for(int i = 0; i < 10; i++) {
			mData.add("Everything will be ok!");
		}
		mAdapter.setData(mData);
	}
	
	private class MyAdapter extends BaseListAdapter {
		private Context mContext;
		
		public MyAdapter(Context context) {
			super(context);
			
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new SlideLeftableView(mContext);
				
				View mainView = LayoutInflater.from(mContext).inflate(R.layout.list_item_1, null);
				((TextView)mainView.findViewById(android.R.id.text1)).setText(R.string.app_name);
				
				View additionalView = LayoutInflater.from(mContext).inflate(R.layout.additional_layout, null);
				
				((SlideLeftableView)convertView).setViews(mainView, additionalView);
			}
			
			((SlideLeftableView)convertView).restoreScroll();
			
			return convertView;
		}
	}
}