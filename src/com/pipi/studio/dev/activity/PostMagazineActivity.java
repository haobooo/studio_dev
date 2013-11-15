package com.pipi.studio.dev.activity;

import java.util.ArrayList;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.adapter.BaseListAdapter;
import com.pipi.studio.dev.base.BaseActivity;
import com.pipi.studio.dev.widget.SlideLeftableView;
import com.pipi.studio.dev.widget.SlideLeftableView.ExtraData;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class PostMagazineActivity extends BaseActivity {
	private static final String TAG = "PostMagazineActivity";
	
	private ListView mListView;
	private MyAdapter mAdapter;
	private ArrayList<String> mData = new ArrayList<String>();
	
	private final SlideLeftableView.OnTapUpListener mOnTapUpListener = new SlideLeftableView.OnTapUpListener() {

		@Override
		public void OnContentClick(String category, int id) {
			// ListView item's click.
			//getDetailFromServer(category, id);
			showToast(R.string.app_name);
		}

		@Override
		public void OnRightTailClick(final String category, final int id, final int partitionIndex) {
			// The right tail's click.
			showToast(R.string.quotation);
		}
		
	};
	
	private final SlideLeftableView.OnPreAndPostListener mPreAndPostListener = new SlideLeftableView.OnPreAndPostListener() {
		private SlideLeftableView mLastScrolledView;
		
		@Override
		public void OnPreScroll() {
			if (mLastScrolledView != null)
				mLastScrolledView.restoreScroll();
		}
		
		@Override
		public void OnAfterScroll(SlideLeftableView view) {
			mLastScrolledView = view;
		}
	};
	
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
	
	private class ViewHolder {
		public TextView mTextView;
	}
	
	private class MyAdapter extends BaseListAdapter {
		private Context mContext;
		
		public MyAdapter(Context context) {
			super(context);
			
			mContext = context;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
//				convertView = new SlideLeftableView(mContext);
//				
//				View mainView = LayoutInflater.from(mContext).inflate(R.layout.list_item_1, null);
//				((TextView)mainView.findViewById(android.R.id.text1)).setText(R.string.app_name);
//				
//				View additionalView = LayoutInflater.from(mContext).inflate(R.layout.additional_layout, null);
				
				//((SlideLeftableView)convertView).setViews(mainView, additionalView);
				
				holder = new ViewHolder();
				convertView = LayoutInflater.from(mContext).inflate(R.layout.slide_left_layout, null);
				holder.mTextView = (TextView) convertView.findViewById(android.R.id.text1);
				convertView.setTag(holder);
				
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mTextView.setText(String.valueOf(position));
			
			((SlideLeftableView)convertView).restoreScroll();
			
			ExtraData data = new ExtraData();
			data.category = "";
			data.id = position;
			data.partitionIndex = -1;
			((SlideLeftableView)convertView).setExtraData(data);
			
			((SlideLeftableView)convertView).setOnPreAndPostListener(mPreAndPostListener);
			((SlideLeftableView)convertView).setOnTapUpListener(mOnTapUpListener);
			
			return convertView;
		}
		
		
	}
}