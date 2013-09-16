package com.pipi.studio.dev.net;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.pipi.studio.dev.base.ThreadCallBack;
import com.pipi.studio.dev.net.WebServiceRequest.BjlyAndroidHttpTransport;
import com.pipi.studio.dev.util.LogUtil;

import android.content.Context;
import android.os.Message;


public class WebService extends BaseRequest {
	private ThreadCallBack mCallBack;
	private String mNameSpace;
	private String mMethodName;
	private String mUrl;
	private String mRequestXml;
	private String mParamName;
	
	
	public WebService(ThreadCallBack callBack, String nameSpace, String methodName, String url,String paramName, String paramValue, int requestCode) {
		mCallBack = callBack;
		mNameSpace = nameSpace;
		mMethodName = methodName;
		mUrl = url;
		mParamName = paramName;
		mRequestXml = paramValue;
		
		this.requestCode = requestCode;
	}
	
	@Override
	public void run() {
		String responseObj = "";
		
		SoapObject Request = new SoapObject(mNameSpace, mMethodName);
		PropertyInfo RequestProp = new PropertyInfo();
		RequestProp.setName(mParamName);
		RequestProp.setValue(mRequestXml);
		Request.addProperty(RequestProp);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setAddAdornments(false);
		envelope.setOutputSoapObject(Request);

		BjlyAndroidHttpTransport androidHttpTransport = new BjlyAndroidHttpTransport(mUrl);

		androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		try {
			androidHttpTransport.call(mNameSpace + mMethodName, envelope);
			if(null != envelope.getResponse()){
				responseObj = String.valueOf(envelope.getResponse());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Message msg = new Message();
			msg.obj = responseObj;
			LogUtil.d("result", responseObj);
			msg.getData().putSerializable("callback", mCallBack);
			resultHandler.sendMessage(msg);
		}
	}
}