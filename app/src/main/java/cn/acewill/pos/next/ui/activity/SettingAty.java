package cn.acewill.pos.next.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.utils.SettingsUtils;
import cn.acewill.pos.next.utils.ToolsUtils;


public class SettingAty extends BaseActivity {
	@SuppressWarnings("unused")
	private String TAG = "SettingAty";
	private Context mContext;
	private EditText mIPEdit;
//	private EditText mPortEdit;
	private EditText setting_title;
	private Button okButton;
	private ImageView mCleanIp, mCleanPort;
	private MyApplication myApp = MyApplication.getInstance();
	private CheckBox checkBox;
	private CheckBox one_cb, two_cb;
	private CheckBox isPrint;
	private CheckBox scan_z, scan_f;
	private CheckBox isDebug;
	private EditText wx_ptid, ali_ptid, wx_coupon_ptid, ali_coupon_ptid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentXml(R.layout.aty_setting);
		myApplication.addPage(SettingAty.this);
		setTitle(ToolsUtils.returnXMLStr("setting_title_system"));
		initView();
		regListener();
		loadData();
	}

	private void initView() {
		mContext = SettingAty.this;
		mCleanIp = (ImageView) findViewById(R.id.img_clean_ip);
		mCleanPort = (ImageView) findViewById(R.id.img_clean_port);
		mIPEdit = (EditText) findViewById(R.id.setting_ip);
//		mPortEdit = (EditText) findViewById(R.id.setting_port);
		setting_title = (EditText) findViewById(R.id.setting_title);
		okButton = (Button) findViewById(R.id.setting_ok);
		checkBox = (CheckBox) findViewById(R.id.checkBox);
		one_cb = (CheckBox) findViewById(R.id.one_cb);
		two_cb = (CheckBox) findViewById(R.id.two_cb);
		isPrint = (CheckBox) findViewById(R.id.isPrint);
		scan_z = (CheckBox) findViewById(R.id.scan_z);
		scan_f = (CheckBox) findViewById(R.id.scan_f);
		isDebug = (CheckBox) findViewById(R.id.isDebug);
		wx_ptid = (EditText) findViewById(R.id.wx_ptid);
		ali_ptid = (EditText) findViewById(R.id.ali_ptid);
		wx_coupon_ptid = (EditText) findViewById(R.id.wx_coupon_ptid);
		ali_coupon_ptid = (EditText) findViewById(R.id.ali_coupon_ptid);
	}

	private void regListener() {
		okButton.setOnClickListener(new OnClickListenerImp());
		mCleanIp.setOnClickListener(new OnClickListenerImp());
		mCleanPort.setOnClickListener(new OnClickListenerImp());
		one_cb.setOnClickListener(new OnClickListenerImp());
		two_cb.setOnClickListener(new OnClickListenerImp());
		scan_z.setOnClickListener(new OnClickListenerImp());
		scan_f.setOnClickListener(new OnClickListenerImp());
		isDebug.setOnClickListener(new OnClickListenerImp());

		if (1 == UserData.getInstance(context).getPrintPage())
			one_cb.setSelected(true);
		else
			two_cb.setSelected(true);

		if (UserData.getInstance(context).isPrint())
			isPrint.setChecked(true);
		else
			isPrint.setChecked(false);

		if (UserData.getInstance(context).isDebug())
			isDebug.setChecked(true);
		else
			isDebug.setChecked(false);

		if (UserData.getInstance(context).isScanz())
			scan_z.setSelected(true);
		else
			scan_f.setSelected(true);

		wx_ptid.setText(UserData.getInstance(context).getWxPtid() + "");
		ali_ptid.setText(UserData.getInstance(context).getAliPtid() + "");
		wx_coupon_ptid.setText(UserData.getInstance(context).getWxCouPtid()
				+ "");
		ali_coupon_ptid.setText(UserData.getInstance(context).getAliCouPtid()
				+ "");

		if (!TextUtils.isEmpty(UserData.getInstance(context).getPrintTitle())) {
			setting_title
					.setText(UserData.getInstance(context).getPrintTitle());
		}

		// edIP的清除按钮显示监听
		mIPEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (s.length() > 0) {
					mCleanIp.setVisibility(View.VISIBLE);
				} else {
					mCleanIp.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

//		// edPort的清除按钮显示监听
//		mPortEdit.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int arg1, int arg2,
//					int arg3) {
//				// TODO Auto-generated method stub
//				if (s.length() > 0) {
//					mCleanPort.setVisibility(View.VISIBLE);
//				} else {
//					mCleanPort.setVisibility(View.GONE);
//				}
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		});
	}
	private Store store;
	private void loadData() {
		store = Store.getInstance(myApp);

		String ip = store.getServiceAddress();
		if (!TextUtils.isEmpty(ip)) {
			mIPEdit.setText(ip);
//			mPortEdit.setText(port);
		}
		if (myApp.getIsTuicai().equals("是")) {
			checkBox.setChecked(true);
		} else {
			checkBox.setChecked(false);
		}
	}

	/**
	 * 创建日期：2015年7月24日<br>
	 * 版权所有 XXX公司。 保留所有权利。<br>
	 * 项目名：XXX项目 - Android客户端<br>
	 * 描述：点击监听
	 * 
	 * @author HJK
	 */
	class OnClickListenerImp implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.setting_ok:
				String ip = mIPEdit.getText().toString().trim();
//				String port = mPortEdit.getText().toString().trim();
				String title = setting_title.getText().toString().trim();
				String wxPtid = wx_ptid.getText().toString().trim();
				String aliPtid = ali_ptid.getText().toString().trim();
				String wxCouPtid = wx_coupon_ptid.getText().toString().trim();
				String aliCouPtid = ali_coupon_ptid.getText().toString().trim();

				if (TextUtils.isEmpty(ip)) {
					myApp.ShowToast("服务器IP地址不能为空");
					return;
				}
//				else if (TextUtils.isEmpty(port)) {
//					myApp.ShowToast("服务器IP端口不能为空");
//					return;
//				}
			   else if (TextUtils.isEmpty(wxPtid)) {
					myApp.ShowToast("微信支付PTID不能为空");
					return;
				} else if (TextUtils.isEmpty(aliPtid)) {
					myApp.ShowToast("支付宝支付PTID不能为空");
					return;
				} else if (TextUtils.isEmpty(wxCouPtid)) {
					myApp.ShowToast("微信卡券PTID不能为空");
					return;
				} else if (TextUtils.isEmpty(aliCouPtid)) {
					myApp.ShowToast("支付宝卡券PTID不能为空");
					return;
				} else {
					if (!TextUtils.isEmpty(title)) {
						UserData.getInstance(context).setPrintTitle(title);
					}

					UserData.getInstance(context).setWxPtid(
							Integer.parseInt(wxPtid));
					UserData.getInstance(context).setAliPtid(
							Integer.parseInt(aliPtid));
					UserData.getInstance(context).setWxCouPtid(
							Integer.parseInt(wxCouPtid));
					UserData.getInstance(context).setAliCouPtid(
							Integer.parseInt(aliCouPtid));

					SettingsUtils.setDBTimeStamp(mContext, (long) 0);


					if (checkBox.isChecked()) {
						myApp.setIsTuicai("是");
					} else {
						myApp.setIsTuicai("否");
					}
					if (isPrint.isChecked())
						UserData.getInstance(context).setPrint(true);
					else
						UserData.getInstance(context).setPrint(false);
					if (isDebug.isChecked())
						UserData.getInstance(context).setDebug(true);
					else
						UserData.getInstance(context).setDebug(false);

					SettingAty.this.finish();
				}
				break;
			case R.id.img_clean_ip:
				mCleanIp.setVisibility(View.GONE);
				mIPEdit.setText("");
				break;
			case R.id.img_clean_port:
				mCleanPort.setVisibility(View.GONE);
//				mPortEdit.setText("");
				break;
			case R.id.one_cb:
				one_cb.setSelected(true);
				two_cb.setSelected(false);
				UserData.getInstance(context).setPrintPage(1);
				break;
			case R.id.two_cb:
				one_cb.setSelected(false);
				two_cb.setSelected(true);
				UserData.getInstance(context).setPrintPage(2);
				break;
			case R.id.scan_z:
				scan_z.setSelected(true);
				scan_f.setSelected(false);
				UserData.getInstance(context).setScanz(true);
				break;
			case R.id.scan_f:
				scan_z.setSelected(false);
				scan_f.setSelected(true);
				UserData.getInstance(context).setScanz(false);
				break;
			case R.id.isDebug:
				if (isDebug.isChecked()) {
					isDebug.setChecked(false);
					final Dialog dialog = new Dialog(SettingAty.this,R.style.loading_dialog);
					View dialog_view = LayoutInflater.from(SettingAty.this)
							.inflate(R.layout.dialog_isdebug, null);
					dialog.setContentView(dialog_view);
					dialog.setCanceledOnTouchOutside(true);

					final EditText pw = (EditText) dialog_view
							.findViewById(R.id.debug_pw);
					TextView debug_cancle = (TextView) dialog_view
							.findViewById(R.id.debug_cancle);
					TextView debug_ok = (TextView) dialog_view
							.findViewById(R.id.debug_ok);

					debug_cancle.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							dialog.dismiss();
						}
					});
					debug_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							String pw_str = pw.getText().toString();
							if (TextUtils.isEmpty(pw_str)) {
								myApp.ShowToast("请输入密码");
							} else {
								if ("111111".equals(pw_str)) {
									isDebug.setChecked(true);
									dialog.dismiss();
								} else {
									myApp.ShowToast("密码错误");
								}
							}
						}
					});

					dialog.show();
					WindowManager.LayoutParams params = dialog.getWindow()
							.getAttributes();
					params.width = 400;
					params.height=WindowManager.LayoutParams.WRAP_CONTENT;
					dialog.getWindow().setAttributes(params);

				}
				break;
			}
		}

	}
}
