package com.pipi.studio.dev.widget.dialog;



import java.util.concurrent.TimeUnit;

import com.pipi.studio.dev.common.Constants;
import com.pipi.studio.dev.net.DefaultThreadPool;
import com.pipi.studio.dev.util.LogUtil;
import com.pipi.studio.dev.util.StringUtil;

import cn.azsy.android.bjly_en.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * 
* @ClassName: CustomLoadingDialog
* @Description: TODO(自定义loading对话框)
* @author liuwei
* @date 2012-7-20 下午03:41:39
*
 */
public class CustomLoadingDialog extends Dialog{
	TextView loading_text;
	String content = "";
	boolean isHideCloseBtn = false;
	public CustomLoadingDialog(Context context,String content,boolean isHideCloseBtn) {
	    super(context,0);
	    this.content = content;
	    this.isHideCloseBtn = isHideCloseBtn;
		LogUtil.d("TAG","****************************");
	}

	 protected void onCreate(Bundle savedInstanceState){
		 super.onCreate(savedInstanceState);
		 //去掉标题
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		 //设置view样式
		 setContentView(R.layout.custom_loading_dialog);	
		 loading_text= (TextView) findViewById(R.id.loading_text);
		 if(StringUtil.isNotEmpty(content)){
			 	loading_text.setText(content);
			}else{
				loading_text.setText(Constants.LOADING_CONTENTS);
			}
	 }
	 //called when this dialog is dismissed
	 protected void onStop() {
		 LogUtil.d("onStop()","this dialog is dismissed");
		 super.onStop();
	 }
	 @Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		 LogUtil.d("show()","this dialog is shown");
		 Constants.IS_STOP_REQUEST = false;
		 
	}
	 @Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
		 LogUtil.d("dismiss()","this dialog is dismissed");
		 try{
			 DefaultThreadPool.pool.awaitTermination(1, TimeUnit.MICROSECONDS);
		 }catch (Exception e) {
			 LogUtil.d("awaitTermination","awaitTermination");
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		 LogUtil.d("onStart()","this dialog is shown");
	}
	 
	 

	 
}
