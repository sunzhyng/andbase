package com.sunzy.andbase;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "ak", Toast.LENGTH_SHORT).show();
			}
		});
	}

	protected void onResume() {
		super.onResume();

		AppEx ac = (AppEx) getApplication();
		int nt = ac.getNetworkType();
		TextView txt = (TextView) findViewById(R.id.text_nettype);
		txt.setText(nt + "");
	}
}
