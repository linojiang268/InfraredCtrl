package com.infraredctrl.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.infraredctrl.activity.R;

/**
 * 
 * @ClassName WiFiSetDialog
 * @Description Wifi中继设置Dialog
 * @author ouArea
 * @date 2014-7-7 下午2:56:29
 * 
 */
public class WiFiSetDialog extends Dialog {
	private EditText metWifiStrenAccount, metWifiStrenPass;
	private Button mbtOpen, mbtCancel;
	private String mStrenAccount, mStrenPass;
	private Listener mListener;
	private boolean isSetWifiStren;
	private Context mContext;
	private CheckBox mcbShowPassword;

	public WiFiSetDialog(Context context) {
		super(context, R.style.DialogTheme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_set_wifi_stren_dialog);
		this.findView();
	}

	@Override
	public void onDetachedFromWindow() {
		// mListener.dismiss();
		super.onDetachedFromWindow();
	}

	private void findView() {
		this.metWifiStrenAccount = (EditText) findViewById(R.id.etWifiStrenAccount);
		this.metWifiStrenPass = (EditText) findViewById(R.id.etWifiStrenPass);
		this.mbtOpen = (Button) findViewById(R.id.btOpen);
		this.mbtCancel = (Button) findViewById(R.id.btCancel);
		this.mcbShowPassword = (CheckBox) findViewById(R.id.cbShowPassword);
		mcbShowPassword.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mcbShowPassword.isChecked()) {
					// 设置密码可见
					metWifiStrenPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					mcbShowPassword.setBackgroundResource(R.drawable.show_password_but_pressed);
				} else {
					mcbShowPassword.setBackgroundResource(R.drawable.show_password_but);
					metWifiStrenPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
				}
			}
		});
		mbtOpen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (null == metWifiStrenAccount || metWifiStrenAccount.getText().toString().trim().length() <= 0) {
					Toast.makeText(mContext, "账号不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if (null != metWifiStrenPass && metWifiStrenPass.getText().toString().trim().length() > 0 && metWifiStrenPass.getText().toString().trim().length() < 8) {
					Toast.makeText(mContext, "密码不能少于8位", Toast.LENGTH_SHORT).show();
					return;
				}
				isSetWifiStren = true;
				mStrenAccount = metWifiStrenAccount.getText().toString().trim();
				mStrenPass = metWifiStrenPass.getText().toString().trim();
				// wifiStrenDialog.setText("已开启中继");
				mListener.open();
				WiFiSetDialog.this.dismiss();
			}
		});
		mbtCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isSetWifiStren = false;
				mStrenAccount = null;
				mStrenPass = null;
				// wifiStrenDialog.setText("未开启中继");
				mListener.cancel();
				WiFiSetDialog.this.dismiss();
			}
		});
	}

	public void setListener(Listener listener) {
		this.mListener = listener;
	}

	public interface Listener {
		// public void dismiss();

		public void open();

		public void cancel();
	}

	public boolean isSetWifiStren() {
		return isSetWifiStren;
	}

	public String getStrenAccount() {
		return mStrenAccount;
	}

	public String getStrenPass() {
		return mStrenPass;
	}

	public void refresh(String account, String pass) {
		if (!isSetWifiStren) {
			// this.mStrenAccount = account + "_ZJ";
			// this.mStrenPass = pass;
			metWifiStrenAccount.setText(account + "_ZJ");
			metWifiStrenPass.setText(pass);
		}

	}
}
