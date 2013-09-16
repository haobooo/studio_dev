package com.pipi.studio.dev.base;

import java.io.Serializable;
import java.util.ArrayList;

public interface ThreadCallBack extends Serializable {

	public void onCallbackFromThread(String resultJson);
	
	public void onCallbackFromThread(String resultJson, int requestCode);
}
