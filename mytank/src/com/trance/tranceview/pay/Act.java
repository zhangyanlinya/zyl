package com.trance.tranceview.pay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import c.b.BP;
import c.b.PListener;
import c.b.QListener;

import com.trance.tranceview.R;

public class Act extends Activity implements OnCheckedChangeListener {

	// 此为测试Appid,请将Appid改成你自己的Bmob AppId
//	String APPID = "11e93b5e5fad3225bebec3fde45839e7";
	String APPID = "4e729b153e9d6ac8ebdfef72f5faff7b";//我的
	// 此为支付插件的官方最新版本号,请在更新时留意更新说明
	int PLUGINVERSION = 7;

	EditText name, price, body, order;
	Button go;
	RadioGroup type;
	TextView tv;

	ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);

		// 必须先初始化
		BP.init(this, APPID);

		// 初始化BmobPay对象,可以在支付时再初始化
		name = (EditText) findViewById(R.id.name);
		price = (EditText) findViewById(R.id.price);
		body = (EditText) findViewById(R.id.body);
		order = (EditText) findViewById(R.id.order);
		go = (Button) findViewById(R.id.go);
		type = (RadioGroup) findViewById(R.id.type);
		tv = (TextView) findViewById(R.id.tv);

		type.setOnCheckedChangeListener(this);
		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (type.getCheckedRadioButtonId() == R.id.alipay) // 当选择的是支付宝支付时
					pay(true);
				else if (type.getCheckedRadioButtonId() == R.id.wxpay) // 调用插件用微信支付
					pay(false);
				else if (type.getCheckedRadioButtonId() == R.id.query) // 选择查询时
					query();
			}
		});

		View exit = findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BP.ForceFree();
			}
		});
		exit.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				BP.ForceExit();
				return true;
			}
		});

		int pluginVersion = BP.getPluginVersion();
		if (pluginVersion < PLUGINVERSION) {// 为0说明未安装支付插件, 否则就是支付插件的版本低于官方最新版
			Toast.makeText(
					Act.this,
					pluginVersion == 0 ? "监测到本机尚未安装支付插件,无法进行支付,请先安装插件(无流量消耗)"
							: "监测到本机的支付插件不是最新版,最好进行更新,请先更新插件(无流量消耗)", Toast.LENGTH_LONG).show();
			installBmobPayPlugin("bp.db");
		}
	}

	/**
	 * 调用支付
	 * 
	 * @param alipayOrWechatPay
	 *            支付类型，true为支付宝支付,false为微信支付
	 */
	void pay(final boolean alipayOrWechatPay) {
		showDialog("正在获取订单...");
		final String name = getName();

		BP.pay(name, getBody(), getPrice(), alipayOrWechatPay, new PListener() {

			// 因为网络等原因,支付结果未知(小概率事件),出于保险起见稍后手动查询
			@Override
			public void unknow() {
				Toast.makeText(Act.this, "支付结果未知,请稍后手动查询", Toast.LENGTH_SHORT)
						.show();
				tv.append(name + "'s pay status is unknow\n\n");
				hideDialog();
			}

			// 支付成功,如果金额较大请手动查询确认
			@Override
			public void succeed() {
				Toast.makeText(Act.this, "支付成功!", Toast.LENGTH_SHORT).show();
				tv.append(name + "'s pay status is success\n\n");
				hideDialog();
			}

			// 无论成功与否,返回订单号
			@Override
			public void orderId(String orderId) {
				// 此处应该保存订单号,比如保存进数据库等,以便以后查询
				order.setText(orderId);
				tv.append(name + "'s orderid is " + orderId + "\n\n");
				showDialog("获取订单成功!请等待跳转到支付页面~");
			}

			// 支付失败,原因可能是用户中断支付操作,也可能是网络原因
			@Override
			public void fail(int code, String reason) {

				// 当code为-2,意味着用户中断了操作
				// code为-3意味着没有安装BmobPlugin插件
				if (code == -3) {
					Toast.makeText(
							Act.this,
							"监测到你尚未安装支付插件,无法进行支付,请先安装插件(已打包在本地,无流量消耗),安装结束后重新支付",
							Toast.LENGTH_LONG).show();
					installBmobPayPlugin("bp.db");
				} else {
					Toast.makeText(Act.this, "支付中断!", Toast.LENGTH_SHORT)
							.show();
				}
				tv.append(name + "'s pay status is fail, error code is \n"
						+ code + " ,reason is " + reason + "\n\n");
				hideDialog();
			}
		});
	}

	// 执行订单查询
	void query() {
		showDialog("正在查询订单...");
		final String orderId = getOrder();

		BP.query(orderId, new QListener() {

			@Override
			public void succeed(String status) {
				Toast.makeText(Act.this, "查询成功!该订单状态为 : " + status,
						Toast.LENGTH_SHORT).show();
				tv.append("pay status of" + orderId + " is " + status + "\n\n");
				hideDialog();
			}

			@Override
			public void fail(int code, String reason) {
				Toast.makeText(Act.this, "查询失败", Toast.LENGTH_SHORT).show();
				tv.append("query order fail, error code is " + code
						+ " ,reason is \n" + reason + "\n\n");
				hideDialog();
			}
		});
	}

	// 以下仅为控件操作，可以略过
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.alipay:
			// 以下仅为控件操作，可以略过
			name.setVisibility(View.VISIBLE);
			price.setVisibility(View.VISIBLE);
			body.setVisibility(View.VISIBLE);
			order.setVisibility(View.GONE);
			go.setText("支付宝支付");
			break;
		case R.id.wxpay:
			// 以下仅为控件操作，可以略过
			name.setVisibility(View.VISIBLE);
			price.setVisibility(View.VISIBLE);
			body.setVisibility(View.VISIBLE);
			order.setVisibility(View.GONE);
			go.setText("微信支付");
			break;
		case R.id.query:
			// 以下仅为控件操作，可以略过
			name.setVisibility(View.GONE);
			price.setVisibility(View.GONE);
			body.setVisibility(View.GONE);
			order.setVisibility(View.VISIBLE);
			go.setText("订单查询");
			break;

		default:
			break;
		}
	}

	// 默认为0.02
	double getPrice() {
		double price = 0.02;
		try {
			price = Double.parseDouble(this.price.getText().toString());
		} catch (NumberFormatException e) {
		}
		return price;
	}

	// 商品详情(可不填)
	String getName() {
		return this.name.getText().toString();
	}

	// 商品详情(可不填)
	String getBody() {
		return this.body.getText().toString();
	}

	// 支付订单号(查询时必填)
	String getOrder() {
		return this.order.getText().toString();
	}

	void showDialog(String message) {
		try {
			if (dialog == null) {
				dialog = new ProgressDialog(this);
				dialog.setCancelable(true);
			}
			dialog.setMessage(message);
			dialog.show();
		} catch (Exception e) {
			// 在其他线程调用dialog会报错
		}
	}

	void hideDialog() {
		if (dialog != null && dialog.isShowing())
			try {
				dialog.dismiss();
			} catch (Exception e) {
			}
	}

	void installBmobPayPlugin(String fileName) {
		try {
			InputStream is = getAssets().open(fileName);
			File file = new File(Environment.getExternalStorageDirectory()
					+ File.separator + fileName + ".apk");
			if (file.exists())
				file.delete();
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + file),
					"application/vnd.android.package-archive");
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
