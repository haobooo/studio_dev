package com.pipi.studio.dev.net;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnectionSE;

import android.content.Context;


public class WebServiceRequest {
	
	private static String responseObj = "";
	
	public static String startWebServiceRequest(Context mContext, String mNameSpace, String mMethodName, String mUrl,String mRequestXml) {
		SoapObject Request = new SoapObject(mNameSpace, mMethodName);
		PropertyInfo RequestProp = new PropertyInfo();
		RequestProp.setName("request");
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
			return responseObj;
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj;
		}
	}
	
	public static String startWSDLRequest(Context mContext, String mNameSpace, String mMethodName, String mUrl,String mRequestXml) {
		SoapObject Request = new SoapObject(mNameSpace, mMethodName);
		PropertyInfo RequestProp = new PropertyInfo();
		RequestProp.setName("in");
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
			return responseObj;
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj;
		}
	}

	public static String startWebServiceRequest(Context mContext, String mNameSpace, String mMethodName, String mUrl,String mRequestXml, int timeOut) {
		SoapObject Request = new SoapObject(mNameSpace, mMethodName);
		PropertyInfo RequestProp = new PropertyInfo();
		RequestProp.setName("RequestXml");
		RequestProp.setValue(mRequestXml);
		Request.addProperty(RequestProp);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setAddAdornments(false);
		envelope.setOutputSoapObject(Request);
		envelope.bodyOut = Request;
		

		HttpTransportSE androidHttpTransport = new HttpTransportSE(mUrl, timeOut);

		androidHttpTransport.debug = true;
		androidHttpTransport.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		try {
			androidHttpTransport.call(mNameSpace + mMethodName, envelope);
			if(null != envelope.getResponse()){
				responseObj = String.valueOf(envelope.getResponse());
			}
			return responseObj;
		} catch (Exception e) {
			e.printStackTrace();
			return responseObj;
		}
	}

	public static class BjlyAndroidHttpTransport extends HttpTransportSE {
		private int timeout = 20000;

		public BjlyAndroidHttpTransport(String url) {
			super(url);
		}

		public BjlyAndroidHttpTransport(String url, int timeout) {
			super(url);
			this.timeout = timeout;
		}

		protected org.ksoap2.transport.ServiceConnection getServiceConnection() throws IOException {
			ServiceConnectionSE serviceConnection = new ServiceConnectionSE(this.url, timeout);
			return serviceConnection;
		}
	}
}
