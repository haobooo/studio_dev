package com.pipi.studio.dev.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public class BaseListAdapter<E> extends BaseAdapter {
	private static final String TAG = "BaseListAdapter";
	
	protected Context mContext;
	protected ArrayList<E> mData = new ArrayList<E>();
	
	public BaseListAdapter(Context context) {
		mContext = context;
	}
	
	public BaseListAdapter(Context context, ArrayList<E> list) {
		mContext = context;
		mData = list;
	}
	
	@Override
	public int getCount() {
		if (mData == null) {
			return 0;
		}
		
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		if (mData == null) {
			return null;
		}
		
		return mData.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setData(ArrayList<E> data) {
		mData = data;
		notifyDataSetChanged();
	}
	
	public void addData(ArrayList<E> data) {
		if (data != null) {
			mData.addAll(data);
			notifyDataSetChanged();
		}
	}
	
	public void addItem(E item) {
		if (item != null) {
			mData.add(item);
			notifyDataSetChanged();
		}
	}
}