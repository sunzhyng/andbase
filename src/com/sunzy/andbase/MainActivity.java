package com.sunzy.andbase;


import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	protected void onResume(){
		super.onResume();
		
		AppContext ac = (AppContext) getApplication();
		int nt = ac.getNetworkType();
		TextView txt = (TextView) findViewById(R.id.text_nettype);
		txt.setText(nt+"");
	}
}
