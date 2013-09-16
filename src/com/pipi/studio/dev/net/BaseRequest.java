/*
 * Copyright 2011 爱知世元
 * Website:http://www.azsy.cn/
 * Email:info＠azsy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.pipi.studio.dev.net;

import java.io.Serializable;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import com.pipi.studio.dev.base.ThreadCallBack;


import android.os.Handler;
import android.os.Message;
/**
 * 目标：
 * 1、安全有序
 * 2、高效
 * 3、易用、易控制
 * 4、activity停止后停止该activity所用的线程。
 * 5、监测内存，当内存溢出的时候自动垃圾回收，清理资源 ，当程序退出之后终止线程池
 * @author zxy
 *
 */
public class BaseRequest  implements   Runnable, Serializable {
	//static HttpClient httpClient = null;
	HttpUriRequest request = null;
	//Use to distinguish the request sequence in the situation One activity start http request more than one times.
	int requestCode = -1; 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	protected ParseHandler handler = null;
	protected String url = null;
	/**
	 * default is 5 ,to set .
	 */
	protected int connectTimeout = 5000;
	/**
	 * default is 5 ,to set .
	 */
	protected int readTimeout = 10000;
//	protected RequestResultCallback requestCallback = null;
	
	Handler resultHandler = new Handler() {
		public void handleMessage(Message msg) {
				String resultData = (String) msg.obj;
				if(!resultData.contains("ERROR.HTTP.008")){
				ThreadCallBack callBack = (ThreadCallBack) msg.getData()
						.getSerializable("callback");
			
				if (requestCode != -1) {
					callBack.onCallbackFromThread(resultData, requestCode);
				} else {
					callBack.onCallbackFromThread(resultData);
				}
			}

		}
	};
	
	@Override
	public void run() {
		
	}
	
	protected void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	protected void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public HttpUriRequest getRequest() {
		return request;
	}
	
}
