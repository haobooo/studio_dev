package com.pipi.studio.dev.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pipi.studio.dev.R;
import com.pipi.studio.dev.base.BaseActivity;
import com.pipi.studio.dev.widget.SlideLeftableView;
import com.pipi.studio.dev.widget.SlideView;


public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	
	private SlideView mSlideView;
	private String[] urls = {
		"http://d.pcs.baidu.com/thumbnail/b2644d3ef304a228538d95b09c9ef096?fid=16942054-250528-1190386085&time=1384506863&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-EjoGKSv9bsEbkeDK834raYjArvc%3D&rt=sh&expires=8h&sharesign=unknown&r=254672310&size=c710_u500&quality=100",
		"http://d.pcs.baidu.com/thumbnail/c43108f9707c14ab1db75b11f2441ccf?fid=16942054-250528-397242075&time=1384506781&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-a9AYKllGZUAH7PpFiOt6iJsdEa8%3D&rt=sh&expires=8h&sharesign=unknown&r=151422818&size=c710_u500&quality=100",
		"http://d.pcs.baidu.com/thumbnail/a2230cb94e7815af350dea8e003e8536?fid=16942054-250528-1998619365&time=1384506881&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-DxHSoSrXWPd3%2BblDWTOwa3w%2FKOc%3D&rt=sh&expires=8h&sharesign=unknown&r=124664981&size=c710_u500&quality=100",
		"http://d.pcs.baidu.com/thumbnail/069b9b69223f7df2c3ea77168733ad04?fid=16942054-250528-4090897665&time=1384506895&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-hd0H%2FYyekdLM9Db0FQF08FRVQHE%3D&rt=sh&expires=8h&sharesign=unknown&r=494495260&size=c710_u500&quality=100",
		"http://d.pcs.baidu.com/thumbnail/f0804e5c5369b45c8eae130ade6b4c37?fid=16942054-250528-2226055600&time=1384507072&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-hh5Tz%2F%2FAyHP6Cz9S3%2B5MNCWJ27I%3D&rt=sh&expires=8h&sharesign=unknown&r=123758261&size=c710_u500&quality=100",
		"http://d.pcs.baidu.com/thumbnail/1597b1079ea6a80d0438f80116a22aca?fid=16942054-250528-3147104219&time=1384507086&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-aOQurBftoCRVTfBySoYnWsioDrM%3D&rt=sh&expires=8h&sharesign=unknown&r=521311511&size=c710_u500&quality=100"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SlideLeftableView view = (SlideLeftableView) LayoutInflater.from(this).inflate(R.layout.slide_left_layout, null);
		
		//TextView mainView = new TextView(this);
		//mainView.setText(this.getString(R.string.app_name));
		
		//View additionalView = LayoutInflater.from(this).inflate(R.layout.additional_layout, null);
		
		//view.setViews(mainView, additionalView);
		setContentView(R.layout.main);
		
		initViews();
	}
	
	private void initViews() {
		mSlideView = (SlideView) findViewById(R.id.slide);
		
		mSlideView.setResources(urls.length, urls);
	}
	
}