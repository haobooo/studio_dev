package com.pipi.studio.dev.base;

import java.util.ArrayList;
import java.util.List;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.common.Constants;
import com.pipi.studio.dev.net.AsyncHttpGet;
import com.pipi.studio.dev.net.AsyncHttpPost;
import com.pipi.studio.dev.net.BaseRequest;
import com.pipi.studio.dev.net.DefaultThreadPool;
import com.pipi.studio.dev.net.WebService;
import com.pipi.studio.dev.net.utils.CheckNetWorkUtil;
import com.pipi.studio.dev.net.utils.RequestParameter;
import com.pipi.studio.dev.net.utils.StringUtil;
import com.pipi.studio.dev.util.LogUtil;
import com.pipi.studio.dev.util.SystemInfoUtils;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity implements ThreadCallBack{
	private static final String TAG = BaseActivity.class.getSimpleName();
	
	public static final int TITLE_LEFT_BUTTON = 1;
	public static final int TITLE_RIGHT_BUTTON = 2;
	
	/**
	 * 当前activity所持有的所有请求
	 */
	List<BaseRequest> requestList = null;

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
		super.onResume();
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

	public  void cancelRequest() {
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

	/**
	 *  The title's left button click listener.
	 */
	public void onTitleLeftButton(View view) {
		finish();
	}
	
	/**
	 *  The title's right button click listener.
	 */
	public void onTitleRightButton(View view) {
		showToast("You should override the onTitleRightButton() function in your activity.");
	}
	
	/**
	 * Set the title's left or right button's background.
	 * @param type should be {@link #TITLE_LEFT_BUTTON} or {@link #TITLE_RIGHT_BUTTON}
	 * @param resId
	 */
	public void setTitleButtonBackground(int type, int resId) {
		ImageView view = null;
		// more safe
		if (type == TITLE_LEFT_BUTTON) {
			view = (ImageView) findViewById(R.id.title_left_button);
		} else if (type == TITLE_RIGHT_BUTTON) {
			view = (ImageView) findViewById(R.id.title_right_button);
		}
		
		if (view != null) {
			view.setImageResource(resId);
			view.setVisibility(View.VISIBLE);
		}
	}
	
	public void setTitle(CharSequence title) {
		TextView titleView = (TextView) findViewById(R.id.title_content);
		if (titleView != null) {
			titleView.setText(title);
		}
	}
	
	public void hideTitleButton(int type) {
		if ((type & TITLE_LEFT_BUTTON) != 0) {
			findViewById(R.id.title_left_button).setVisibility(View.GONE);
		}
		
		if ((type & TITLE_RIGHT_BUTTON) != 0) {
			findViewById(R.id.title_right_button).setVisibility(View.GONE);
		}
	}
	
	/**
	 *  The Back button's click listener.
	 */
	public void onBack(View view) {
		finish();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	@Override
	public void onCallbackFromThread(String resultJson) {
		
	}
	
	protected void showToast(String message)
	{
		Toast toast=Toast.makeText(this, (!StringUtil.isEmpty(message))?message:Constants.ERROR_MESSAGE, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	protected void showToast(String message, int length){
		Toast toast=Toast.makeText(this, (!StringUtil.isEmpty(message))?message:Constants.ERROR_MESSAGE, length);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
	
	protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog, String serviceCode){
		if(isShowLoadingDialog){
			if(!CheckNetWorkUtil.checkNetWork(this)){
				return ;
			}
		}
		if(null != parameter)
		{
			parameter.add(new RequestParameter("isource", Constants.SOURCE));
			parameter.add(new RequestParameter("source", Constants.SOURCE));
			/*parameter.add(new RequestParameter("isLogined","0"));*/
			parameter.add(new RequestParameter("hwId", Constants.HWID));
			parameter.add(new RequestParameter("serviceCode", Constants.SERVICE_CODE));
			/*parameter.add(new RequestParameter("deviceSystem", Build.VERSION.RELEASE));*/
			/*parameter.add(new RequestParameter("deviceName", Build.MODEL));
			parameter.add(new RequestParameter("deviceNet",SystemInfoUtils.getNetWorkType(this)));
			parameter.add(new RequestParameter("screenSize",SystemInfoUtils.getScreenSize(this)));*/
			
			
		}
		if(null != parameter){
			for (int i = 0; i < parameter.size(); i++) {
				RequestParameter requestParameter = parameter.get(i);
				LogUtil.d("requestParameter", requestParameter.getName()+"="+requestParameter.getValue());
			}
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

	protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog){
		if(isShowLoadingDialog){
			if(!CheckNetWorkUtil.checkNetWork(this)){
				return ;
			}
		}
		if(null != parameter)
		{
			parameter.add(new RequestParameter("isource", Constants.SOURCE));
			parameter.add(new RequestParameter("source", Constants.SOURCE));
			/*parameter.add(new RequestParameter("isLogined","0"));*/
			parameter.add(new RequestParameter("hwId", Constants.HWID));
			parameter.add(new RequestParameter("serviceCode", Constants.SERVICE_CODE));
			/*parameter.add(new RequestParameter("deviceSystem", Build.VERSION.RELEASE));*/
			/*parameter.add(new RequestParameter("deviceName", Build.MODEL));
			parameter.add(new RequestParameter("deviceNet",SystemInfoUtils.getNetWorkType(this)));
			parameter.add(new RequestParameter("screenSize",SystemInfoUtils.getScreenSize(this)));*/
			
			
		}
		if(null != parameter){
			for (int i = 0; i < parameter.size(); i++) {
				RequestParameter requestParameter = parameter.get(i);
				LogUtil.d("requestParameter", requestParameter.getName()+"="+requestParameter.getValue());
			}
		}
		
		BaseRequest httpRequest = null;
		if("POST".equalsIgnoreCase(requestType)){
			httpRequest = new AsyncHttpPost(this, url, parameter, isShowLoadingDialog, "", false);
		}else{
			httpRequest = new AsyncHttpGet(this, url, parameter, isShowLoadingDialog, "", false);
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
	
	protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog,int connectTimeout,int readTimeout){
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
			parameter.add(new RequestParameter("deviceSystem", Build.VERSION.RELEASE));
			parameter.add(new RequestParameter("deviceName", Build.MODEL));
			parameter.add(new RequestParameter("deviceNet",SystemInfoUtils.getNetWorkType(this)));
			parameter.add(new RequestParameter("screenSize",SystemInfoUtils.getScreenSize(this)));
		}
		for (int i = 0; i < parameter.size(); i++) {
			RequestParameter requestParameter = parameter.get(i);
			LogUtil.d("requestParameter", requestParameter.getName()+"="+requestParameter.getValue());
		}
		BaseRequest httpRequest = null;
		if("POST".equalsIgnoreCase(requestType)){
			httpRequest = new AsyncHttpPost(this, url, parameter,isShowLoadingDialog,connectTimeout,readTimeout);
		}else{
			httpRequest = new AsyncHttpGet(this, url, parameter,isShowLoadingDialog,connectTimeout,readTimeout);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}
	
	protected void startHttpRequst(String requestType,String url,List<RequestParameter> parameter,boolean isShowLoadingDialog,String loadingDialogContent,boolean isHideCloseBtn,int connectTimeout,int readTimeout){
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
			parameter.add(new RequestParameter("deviceSystem", Build.VERSION.RELEASE));
			parameter.add(new RequestParameter("deviceName", Build.MODEL));
			parameter.add(new RequestParameter("deviceNet",SystemInfoUtils.getNetWorkType(this)));
			parameter.add(new RequestParameter("screenSize",SystemInfoUtils.getScreenSize(this)));
		}
		for (int i = 0; i < parameter.size(); i++) {
			RequestParameter requestParameter = parameter.get(i);
			LogUtil.d("requestParameter", requestParameter.getName()+"="+requestParameter.getValue());
		}
		BaseRequest httpRequest = null;
		if("POST".equalsIgnoreCase(requestType)){
			httpRequest = new AsyncHttpPost(this, url, parameter,isShowLoadingDialog,loadingDialogContent,isHideCloseBtn,connectTimeout,readTimeout);
		}else{
			httpRequest = new AsyncHttpGet(this, url, parameter,isShowLoadingDialog,loadingDialogContent,isHideCloseBtn,connectTimeout,readTimeout);
		}
		DefaultThreadPool.getInstance().execute(httpRequest);
		this.requestList.add(httpRequest);
	}

	protected void startWebServiceRequest(String nameSpace, String methodName, String url,String paramName, String paramValue, int requestCode) {
		WebService webServiceRequest = new WebService(this, nameSpace, methodName, url, paramName, paramValue, requestCode);
		DefaultThreadPool.getInstance().execute(webServiceRequest);
		this.requestList.add(webServiceRequest);
	}

	@Override
	public void onCallbackFromThread(String resultJson, int requestCode) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
