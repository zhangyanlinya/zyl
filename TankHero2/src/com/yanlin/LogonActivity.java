package com.yanlin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LogonActivity extends Activity {

	private EditText ip;
	private TextView textIp;
	private EditText port;
	private TextView textPort;
	private CheckBox cb;
	private Button btn_single;
	private Button btn_network;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.login);

		ip = (EditText) findViewById(R.id.EditText03);
		port = (EditText) findViewById(R.id.EditText04);
		cb = (CheckBox) findViewById(R.id.ipPortCheck);
		textIp = (TextView) findViewById(R.id.TextIp);
		textPort = (TextView) findViewById(R.id.TextPort);
		btn_single = (Button) findViewById(R.id.Btn_single);
		btn_network = (Button) findViewById(R.id.Btn_network);

		// 单人模式游戏
		btn_single.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
					GameView.single=true;			
					Intent intent = new Intent();
					intent.setClass(LogonActivity.this, AndroidTankActivity.class);
					startActivity(intent);
				

			}
		});
		// 多人模式游戏
		btn_network.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (checkIpPort()) {
				
					GameView.single=false;
				//	NetClient.updPort=Integer.parseInt(port.getText().toString());
					Intent intent = new Intent();
					intent.setClass(LogonActivity.this, AndroidTankActivity.class);
					startActivity(intent);
				}
				
			}
		});
	

		cb.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cb.isChecked()) {
					setIpPortVisible(0);
				} else {
					setIpPortVisible(8);
				}
			}
		});
		
	}

	private void setIpPortVisible(Integer visibility) {
		ip.setVisibility(visibility);
		port.setVisibility(visibility);
		textIp.setVisibility(visibility);
		textPort.setVisibility(visibility);
		
	}

	public boolean checkIpPort() {

		if (ip.getText()
				.toString()
				.matches(
						"^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|[1-9])\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|\\d)\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]\\d|[1-9])$") == false) {
			Toast.makeText(LogonActivity.this, "请输入正确的IP地址", Toast.LENGTH_SHORT)
					.show();
			ip.setFocusable(true);
			
			return false;
			
		}
		if (port.getText().toString()
				.matches("^([1-9]|[1-9]\\d{1,3}|[1-6][0-5][0-5][0-3][0-5])$") == false) {
			Toast.makeText(LogonActivity.this, "请输入正确的端口", Toast.LENGTH_SHORT)
					.show();
			port.setFocusable(true);
			return false;
		}

		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == 4){			//按下返回键
				System.exit(0);
			}
		return true;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

}
