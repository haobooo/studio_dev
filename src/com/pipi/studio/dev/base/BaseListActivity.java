package com.pipi.studio.dev.base;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ComponentCallbacks2;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.pipi.studio.dev.common.Constants;
import com.pipi.studio.dev.net.AsyncHttpGet;
import com.pipi.studio.dev.net.AsyncHttpPost;
import com.pipi.studio.dev.net.BaseRequest;
import com.pipi.studio.dev.net.DefaultThreadPool;
import com.pipi.studio.dev.net.utils.CheckNetWorkUtil;
import com.pipi.studio.dev.net.utils.RequestParameter;
import com.pipi.studio.dev.util.AsyncImageLoader;
import com.pipi.studio.dev.util.LogUtil;
import com.pipi.studio.dev.util.SystemInfoUtils;

public class BaseListActivity extends ListActivity implements ThreadCallBack, OnScrollListener{
	private static final String TAG = "BaseListActivity";
	
	/**
	 * 当前activity所持有的所有请求
	 */
	List<BaseRequest> requestList = null;

	private int mLastItem;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestList = new ArrayList<BaseRequest>();
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		getListView().setOnScrollListener(this);

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		/**
		 * 在activity销毁的时候同时设置停止请求，停止线程请求回调
		 */
		cancelRequest();
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		/**
		 * 在activity销毁的时候同时设置停止请求，停止线程请求回调
		 */
		cancelRequest();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
	}
	
	private void cancelRequest() {
		if (requestList != null && requestList.size() > 0) {
			for (BaseRequest request : requestList) {
				if (request.getRequest() != null) {
					try {
						request.getRequest().abort();
						requestList.remove(request.getRequest());
						
						Log.d("netlib", "netlib ,onDestroy request to  "
								+ request.getRequest().getURI()
								+ "  is removed");
					} catch (UnsupportedOperationException e) {
						//do nothing .
					}
				}
			}
		}
	}

	@Override
	public void onCallbackFromThread(String resultJson) {
		// TODO Auto-generated method stub
		
	}
	
	protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog){
		if(!CheckNetWorkUtil.checkNetWork(this)){
			return ;
		}
		if(null != parameter)
		{
			parameter.add(new RequestParameter("isource", Constants.SOURCE));
			parameter.add(new RequestParameter("source", Constants.SOURCE));
			parameter.add(new RequestParameter("hwId", Constants.HWID));
			parameter.add(new RequestParameter("serviceCode", Constants.SERVICE_CODE));
			parameter.add(new RequestParameter("deviceSystem", Build.VERSION.RELEASE));
			parameter.add(new RequestParameter("deviceName", Build.MODEL));
			parameter.add(new RequestParameter("deviceNet",SystemInfoUtils.getNetWorkType(this)));
			parameter.add(new RequestParameter("screenSize",SystemInfoUtils.getScreenSize(this)));
		}
		BaseRequest httpRequest = null;
		if("POST".equalsIgnoreCase(requestType)){
			httpRequest = new AsyncHttpPost(this, url, parameter,isShowLoadingDialog,"",false);
		}else{
			httpRequest = new AsyncHttpGet(this, url, parameter,isShowLoadingDialog,"",false);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}

	protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog, int requestCode){
		if(isShowLoadingDialog){
			if(!CheckNetWorkUtil.checkNetWork(this)){
				return ;
			}
		}
		if(null != parameter)
		{
			parameter.add(new RequestParameter("isource", Constants.SOURCE));
			parameter.add(new RequestParameter("source", Constants.SOURCE));
			parameter.add(new RequestParameter("hwId", Constants.HWID));
			parameter.add(new RequestParameter("serviceCode", Constants.SERVICE_CODE));
		}
		
		BaseRequest httpRequest = null;
		if("POST".equalsIgnoreCase(requestType)){
			httpRequest = new AsyncHttpPost(this, url, parameter, isShowLoadingDialog, "", false, requestCode);
		}else{
			httpRequest = new AsyncHttpGet(this, url, parameter, isShowLoadingDialog, "", false, requestCode);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}
	
	@Override
	public void onCallbackFromThread(String resultJson, int requestCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView listView, int scrollState) {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "scrollState=" + scrollState);
		if(mLastItem == listView.getAdapter().getCount()  && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
			loadMoreDate();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "firstVisibleItem=" + firstVisibleItem + "; visibleItemCount=" + visibleItemCount + "; totalItemCount=" + totalItemCount);
		mLastItem = firstVisibleItem + visibleItemCount;
	}
	
	protected void loadMoreDate() {
		if (LogUtil.IS_LOG) LogUtil.d(TAG, "you SHOULD dine the loadMoreData customly!");
	}
	
	@Override
    public void onTrimMemory(int level) {
        if (LogUtil.IS_LOG) LogUtil.d(TAG, "onTrimMemory: " + level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_MODERATE) {
        	AsyncImageLoader.clearImageCache();
        }
    }
}
