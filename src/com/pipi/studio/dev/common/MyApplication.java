package com.pipi.studio.dev.common;

import java.io.File;

import com.pipi.studio.dev.net.DefaultThreadPool;
import com.pipi.studio.dev.util.LogUtil;


import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;


public class MyApplication extends Application {
	private static final String TAG = "MyApplication";
	
	public static int CACHE_INTERNAL = 0;
	public static int CACHE_SDCARD = 1;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
//		 获取硬件id
//		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		Constants.HWID=tm.getDeviceId();
		Constants.appInstance = this;
		
	}
	
	@Override
	public void onLowMemory() {
		/**
		 * 低内存的时候主动释放所有线程和资源 
		 * 
		 * PS:这里不一定每被都调用
		 */
		DefaultThreadPool.shutdown();
		if (LogUtil.IS_LOG) LogUtil.i(this.getClass().getName(), "MyApplication  onError  onLowMemory");
		super.onLowMemory();
	}
	
	@Override
	public void onTerminate() {
		/**
		 * 系统退出的时候主动释放所有线程和资源
		 * PS:这里不一定被都调用
		 */
		DefaultThreadPool.shutdown();
		if (LogUtil.IS_LOG) LogUtil.i(this.getClass().getName(), "MyApplication  onError  onTerminate");
		super.onTerminate();
	}
	
	public String getCacheDirPath(int cacheType) {
		if (cacheType == CACHE_SDCARD) {
			try {
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {
					String path = getExternalCacheDir().getAbsolutePath() + File.separator;
					if (LogUtil.IS_LOG) LogUtil.d(this.getClass().getName(), "path=" + path);
					return path;
				} else {
					if (LogUtil.IS_LOG) LogUtil.d(this.getClass().getName(), "status=" + status);
					return null;
				}
			} catch (Exception e) {
				if (LogUtil.IS_LOG) LogUtil.d(this.getClass().getName(), "get external cache error: "  + e);
				return null;
			}
		} else if (cacheType == CACHE_INTERNAL) {
			String path = getCacheDir().getAbsolutePath() + File.separator;
			if (LogUtil.IS_LOG) LogUtil.d(this.getClass().getName(), "path=" + path);
			return path;
		}
		
		return null;
	}

}