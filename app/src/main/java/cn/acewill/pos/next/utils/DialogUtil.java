package cn.acewill.pos.next.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.paylibrary.EPayTask;
import com.acewill.paylibrary.MicropayTask;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.ReceiptsDataController;
import cn.acewill.pos.next.common.ReprintController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.CreatDealBack;
import cn.acewill.pos.next.interfices.DialogCall;
import cn.acewill.pos.next.interfices.DialogCallBack;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.interfices.DialogEtsCallback;
import cn.acewill.pos.next.interfices.DialogMTCallback;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.interfices.InterfaceDialog;
import cn.acewill.pos.next.interfices.KeyCallBack;
import cn.acewill.pos.next.interfices.VoucherRefrushLisener;
import cn.acewill.pos.next.interfices.WSHListener;
import cn.acewill.pos.next.interfices.WeightCallBack;
import cn.acewill.pos.next.model.Customer;
import cn.acewill.pos.next.model.DishWeight;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.NetOrderRea;
import cn.acewill.pos.next.model.TakeOut;
import cn.acewill.pos.next.model.TerminalInfo;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishItem;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.newPos.Reserve;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.table.TableInfor;
import cn.acewill.pos.next.model.table.TableStatus;
import cn.acewill.pos.next.model.user.Staff;
import cn.acewill.pos.next.model.user.User;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.model.wsh.WshAccountCoupon;
import cn.acewill.pos.next.model.wsh.WshCreateDeal;
import cn.acewill.pos.next.model.wsh.WshDealPreview;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.WshService;
import cn.acewill.pos.next.service.retrofit.response.ValidationResponse;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.ui.activity.OrderDishMainAty;
import cn.acewill.pos.next.ui.activity.ReceiptsTypeAty;
import cn.acewill.pos.next.ui.adapter.CouponAdapter;
import cn.acewill.pos.next.ui.adapter.DialogListAdp;
import cn.acewill.pos.next.ui.adapter.DishItemAdp;
import cn.acewill.pos.next.ui.adapter.KdsStateAdp;
import cn.acewill.pos.next.ui.adapter.MemberAdapter;
import cn.acewill.pos.next.ui.adapter.PrinterStateAdp;
import cn.acewill.pos.next.ui.adapter.ReprintAdp;
import cn.acewill.pos.next.ui.adapter.ReservePlaceAdp;
import cn.acewill.pos.next.ui.adapter.RushItemAdp;
import cn.acewill.pos.next.ui.adapter.StaffQuartersAdp;
import cn.acewill.pos.next.ui.adapter.TableAdapter;
import cn.acewill.pos.next.widget.ComTextView;
import cn.acewill.pos.next.widget.CommonDialog;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ScrolListView;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.TimePicker;

import static cn.acewill.pos.R.id.btn_add;

/**
 * Created by aqw on 2016/8/15.
 */
public class DialogUtil {
	/**
	 * 登录设置弹出框
	 *
	 * @param context
	 */
	public static void loginSetDialog(final Context context) {
		final MyApplication myApplication = MyApplication.getInstance();

		final Dialog  dialog           = createDialog(context, R.layout.dialog_setting, 8, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		final Store   store            = Store.getInstance(context);
		final boolean isCreateOrderJyj = store.isCreateOrderJyj();

		final LinearLayout close_ll = (LinearLayout) dialog.findViewById(R.id.close_ll);
		final LinearLayout lin_jyj  = (LinearLayout) dialog.findViewById(R.id.lin_jyj);
		final RelativeLayout rel_bindInfo = (RelativeLayout) dialog
				.findViewById(R.id.rel_bindInfo);
		final CommonEditText login_server = (CommonEditText) dialog
				.findViewById(R.id.login_server);
		final CommonEditText login_port = (CommonEditText) dialog
				.findViewById(R.id.login_port);
		final CommonEditText login_server_jyj = (CommonEditText) dialog
				.findViewById(R.id.login_server_jyj);
		final CommonEditText login_port_jyj = (CommonEditText) dialog
				.findViewById(R.id.login_port_jyj);
		final CommonEditText kds_address = (CommonEditText) dialog
				.findViewById(R.id.kds_address);
		final CommonEditText kds_port = (CommonEditText) dialog.findViewById(R.id.kds_port);
		//        final CommonEditText login_merchant = (CommonEditText) dialog.findViewById(R.id.login_merchant);
		//        final CommonEditText login_store = (CommonEditText) dialog.findViewById(R.id.login_store);
		//        final CommonEditText login_brand = (CommonEditText) dialog.findViewById(R.id.login_brand);
		final CommonEditText device_name = (CommonEditText) dialog
				.findViewById(R.id.device_name);
		final CheckBox ck_select_dish = (CheckBox) dialog
				.findViewById(R.id.ck_select_dish);
		final CheckBox ck_select_netorder = (CheckBox) dialog
				.findViewById(R.id.ck_select_netorder);
		final CheckBox ck_select_kit_money = (CheckBox) dialog
				.findViewById(R.id.ck_select_kit_money);
		final TextView tv_bindInfo = (TextView) dialog.findViewById(R.id.tv_bindInfo);

		final TextView save_btn = (TextView) dialog.findViewById(R.id.save_btn);

		if (isCreateOrderJyj) {
			lin_jyj.setVisibility(View.VISIBLE);
		} else {
			lin_jyj.setVisibility(View.GONE);
		}

		//        login_server.setText("43.241.226.10");
		//        if (!store.getSaveState()) {
		//            String storeMerchantsId = "a123";
		//            String storeBrandId = "2";
		//            String storeStoreId = "2";
		//            String storeDeviceName = "pos14478";
		//            String serviceAddress = "sz.canxingjian.com";
		//            String kdsAddress = "192.168.1.116";
		//            String kdsPort = "8080";
		//            String servicePort = "18080";
		//
		//            login_server.setText(serviceAddress);
		//            login_port.setText(servicePort);
		//            kds_address.setText(kdsAddress);
		//            kds_port.setText(kdsPort);
		//            login_merchant.setText(storeMerchantsId);
		//
		//            login_store.setText(storeStoreId);
		//            login_brand.setText(storeBrandId);
		//            device_name.setText(storeDeviceName);
		//
		//            store.setDishShowStyle(false);
		//        } else {
		login_server.setText(store.getServiceAddress());
		login_port.setText(store.getStorePort());
		login_server_jyj.setText(store.getServiceAddressJyj());
		login_port_jyj.setText(store.getStorePortJyj());
		kds_address.setText(store.getKdsServer());
		kds_port.setText(store.getKdsPort());
		//            login_merchant.setText(store.getStoreAppId());
		//            login_store.setText(store.getStoreId());
		//            login_brand.setText(store.getBrandId());
		device_name.setText(store.getDeviceName());

		ck_select_dish.setChecked(store.getDishShowStyle());
		ck_select_netorder.setChecked(store.getReceiveNetOrder());
		ck_select_kit_money.setChecked(store.getKitMoneyStyle());
		//        }

		if (StoreInfor.terminalInfo != null) {
			rel_bindInfo.setVisibility(View.VISIBLE);
			TerminalInfo terminalInfo = StoreInfor.terminalInfo;
			String storeInfo = "品牌名称:" + terminalInfo.brandName + "\t门店名称:" + terminalInfo.sname + "\t本机唯一绑定标识:" + store
					.getTenTokenStr();
			tv_bindInfo.setText(storeInfo);
		} else {
			rel_bindInfo.setVisibility(View.GONE);
		}

		close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭登录设置窗口");
				((LoginAty) context).getTerminInfo();
				dialog.dismiss();
			}
		});

		save_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//                String storeMerchantsId = login_merchant.getText().toString().trim();
				//                String storeBrandId = login_brand.getText().toString().trim();
				//                String storeStoreId = login_store.getText().toString().trim();

				String storeDeviceName = device_name.getText().toString().trim();
				String serviceAddress  = login_server.getText().toString().trim();
				String servicePort     = login_port.getText().toString().trim();

				String kdsAddress = kds_address.getText().toString().trim();
				String kdsPort    = kds_port.getText().toString().trim();

				if (ck_select_dish.isChecked()) {
					store.setDishShowStyle(true);
				} else {
					store.setDishShowStyle(false);
				}

				if (ck_select_netorder.isChecked()) {
					store.setReceiveNetOrder(true);
				} else {
					store.setReceiveNetOrder(false);
				}

				if (ck_select_kit_money.isChecked()) {
					store.setKitMoneyStyle(true);
				} else {
					store.setKitMoneyStyle(false);
				}


				if (isCreateOrderJyj) {
					String serviceAddress_jyj = login_server_jyj.getText().toString().trim();
					String servicePort_jyj    = login_port_jyj.getText().toString().trim();
					if (TextUtils.isEmpty(serviceAddress_jyj)) {
						myApplication.ShowToast(ToolsUtils
								.returnXMLStr("server_address_is_not_null_jyj"));
						return;
					}
					if (TextUtils.isEmpty(servicePort_jyj)) {
						myApplication.ShowToast(ToolsUtils.returnXMLStr("port_is_not_null_jyj"));
						return;
					}
					store.setServiceAddressJyj(serviceAddress_jyj);
					store.setStorePortJyj(servicePort_jyj);
				}

				if (TextUtils.isEmpty(serviceAddress)) {
					myApplication.ShowToast(ToolsUtils.returnXMLStr("server_address_is_not_null"));
					return;
				}
				if (TextUtils.isEmpty(servicePort)) {
					myApplication.ShowToast(ToolsUtils.returnXMLStr("port_is_not_null"));
					return;
				}
				//                if (TextUtils.isEmpty(kdsAddress)) {
				//                    myApplication.ShowToast("KDS地址不能为空");
				//                    return;
				//                }
				//                if (TextUtils.isEmpty(kdsPort)) {
				//                    myApplication.ShowToast("KDS端口号不能为空");
				//                    return;
				//                }

				//                if (TextUtils.isEmpty(storeMerchantsId)) {
				//                    myApplication.ShowToast("商户ID不能为空");
				//                    return;
				//                }
				//                if (TextUtils.isEmpty(storeBrandId)) {
				//                    myApplication.ShowToast("品牌ID不能为空");
				//                    return;
				//                }
				//                if (TextUtils.isEmpty(storeStoreId)) {
				//                    myApplication.ShowToast("门店ID不能为空");
				//                    return;
				//                }

				store.setStorePort(servicePort);
				store.setServiceAddress(serviceAddress);
				//                store.setStoreAppId(storeMerchantsId);
				//                store.setBrandId(storeBrandId);
				//                store.setStoreId(storeStoreId);
				store.setDeviceName(storeDeviceName);
				store.setKdsServer(kdsAddress);
				store.setKdsPort(kdsPort);
				store.setSaveState(true);

				((LoginAty) context).getTerminInfo();
				((LoginAty) context).saveSetting();
				ToolsUtils.writeUserOperationRecords("登录设置操作");
				dialog.dismiss();
			}
		});
	}

	//创建一个Dialog
	public static Dialog getDialog(final Context context, int layout, float width, float height) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new CommonDialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(false);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * width); // 高度设置为屏幕的0.5
		//        p.height = (int) (d.getHeight() * height); // 宽度设置为屏幕的0.5
		dialogWindow.setAttributes(p);

		dialog.show();

		return dialog;
	}

	//创建一个Dialog
	public static Dialog getDialog(final Context context, int layout, float width, float height, int wh) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new CommonDialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(false);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * width); // 高度设置为屏幕的0.5
		p.height = (int) (d.getHeight() * height); // 宽度设置为屏幕的0.5
		dialogWindow.setAttributes(p);

		dialog.show();

		return dialog;
	}

	//创建一个Dialog
	public static Dialog getDialog(final Context context, int layout) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new Dialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(true);

		dialog.show();

		return dialog;
	}

	//创建一个Dialog
	public static Dialog getDialogShow(final Context context, int layout, float width, float height, boolean touchOutside, boolean isShow) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new CommonDialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(touchOutside);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * width); // 高度设置为屏幕的0.5
		//        p.height = (int) (d.getHeight() * height); // 宽度设置为屏幕的0.5
		dialogWindow.setAttributes(p);

		if (isShow) {
			dialog.show();
		}
		return dialog;
	}

	/**
	 * 创建一个Dialog
	 *
	 * @param context
	 * @param layout  布局视图
	 * @param width   占屏幕百分比宽
	 * @param height  占屏幕百分比高
	 * @return
	 */
	public static Dialog createDialog(final Context context, int layout, int width, int height, boolean isShow) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new CommonDialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(false);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		if (width == LinearLayout.LayoutParams.MATCH_PARENT || width == LinearLayout.LayoutParams.WRAP_CONTENT) {
			p.width = width;
		} else {
			p.width = (int) (d.getWidth() * width * 0.1);
		}
		if (height == LinearLayout.LayoutParams.MATCH_PARENT || height == LinearLayout.LayoutParams.WRAP_CONTENT) {
			p.height = height;
		} else {
			p.height = (int) (d.getHeight() * height * 0.1);
		}

		dialogWindow.setAttributes(p);

		if (isShow) {
			dialog.show();
		}

		return dialog;
	}

	/**
	 * 创建一个Dialog
	 *
	 * @param context
	 * @param layout  布局视图
	 * @param width   占屏幕百分比宽
	 * @param height  占屏幕百分比高
	 * @return
	 */
	public static Dialog createDialog(final Context context, int layout, int width, int height) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View           view     = inflater.inflate(layout, null);
		final Dialog   dialog   = new CommonDialog(context);
		//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(view);
		dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
		dialog.setCanceledOnTouchOutside(false);

		Window                     dialogWindow = dialog.getWindow();
		WindowManager              m            = ((Activity) context).getWindowManager();
		Display                    d            = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p            = dialogWindow.getAttributes(); // 获取对话框当前的参数值
		if (width == LinearLayout.LayoutParams.MATCH_PARENT || width == LinearLayout.LayoutParams.WRAP_CONTENT) {
			p.width = width;
		} else {
			p.width = (int) (d.getWidth() * width * 0.1);
		}
		if (height == LinearLayout.LayoutParams.MATCH_PARENT || height == LinearLayout.LayoutParams.WRAP_CONTENT) {
			p.height = height;
		} else {
			p.height = (int) (d.getHeight() * height * 0.1);
		}

		dialogWindow.setAttributes(p);

		dialog.show();

		return dialog;
	}
	/**
	 * 正扫弹出框
	 *
	 * @param context
	 * @param type    1:支付宝，2:微信
	 */
	public static Dialog scanDialog(final Context context, final int type, final DialogCallBack callBack) {
		final Dialog       dialog     = createDialog(context, R.layout.dialog_pay_scancode, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		ComTextView        scan_title = (ComTextView) dialog.findViewById(R.id.scan_title);
		ComTextView        scan_close = (ComTextView) dialog.findViewById(R.id.scan_close);
		final ComTextView  msg        = (ComTextView) dialog.findViewById(R.id.scan_msg);
		final LinearLayout retry_ll   = (LinearLayout) dialog.findViewById(R.id.retry_ll);
		TextView           creat      = (TextView) dialog.findViewById(R.id.creat);
		TextView           retry      = (TextView) dialog.findViewById(R.id.retry);

		String str = type == 1 ? ToolsUtils.returnXMLStr("sth_zfb") : ToolsUtils
				.returnXMLStr("sth_wx");
		if (type == 12) {
			str = ToolsUtils.returnXMLStr("lkl_pay");
		} else if (type == -8) {
			str = "";
		}

		scan_title.setText(ToolsUtils.returnXMLStr("guest") + str + ToolsUtils
				.returnXMLStr("scan_pos_qrcode"));

		final int   second = 90;
		final int[] num    = new int[1];
		num[0] = second;
		final Timer[] mTimer = new Timer[1];
		mTimer[0] = new Timer();

		msg.setText(ToolsUtils.returnXMLStr("wait_pay_please_wating") + "(" + num[0] + ToolsUtils
				.returnXMLStr("second") + ")");

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {

				if (num[0] > 0) {
					msg.setText(ToolsUtils
							.returnXMLStr("wait_pay_please_wating") + "(" + num[0] + ToolsUtils
							.returnXMLStr("second") + ")");
				} else {

					num[0] = second;
					msg.setText(ToolsUtils.returnXMLStr("wait_pay_timeOut_please_try_again"));
					mTimer[0].cancel();
					retry_ll.setVisibility(View.VISIBLE);
				}
			}
		};

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				num[0]--;
				handler.sendMessage(message);
			}
		};
		mTimer[0].schedule(timerTask, 1000, 1000);

		//当调用查询支付结果接口超时并确认客户已支付时，手动点击按钮调用下单
		creat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("正扫==>>当调用查询支付结果接口超时并确认客户已支付时,点击按钮调用下单");
				dialog.dismiss();
				callBack.onCancle();
			}
		});

		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onOk();
				ToolsUtils.writeUserOperationRecords("正扫==>>重试调用扫码支付");
				dialog.dismiss();
				//                retry_ll.setVisibility(View.GONE);
				//                mTimer[0] = new Timer();
				//                TimerTask mTimerTask = new TimerTask() {
				//                    @Override
				//                    public void run() {
				//                        Message message = new Message();
				//                        num[0]--;
				//                        handler.sendMessage(message);
				//                    }
				//                };
				//                mTimer[0].schedule(mTimerTask,1000,1000);
				//                callBack.onOk();
			}
		});

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {

				if (mTimer[0] != null) {
					mTimer[0].cancel();
				}

				//清空副屏，展示默认欢迎页
				//                SunmiSecondScreen.getInstance(context).showImageListScreen();
			}
		});

		scan_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onCancle();
				ToolsUtils.writeUserOperationRecords("正扫==>>关闭扫描窗口");
				dialog.dismiss();
			}
		});

		return dialog;
	}
	/**
	 * 正扫弹出框
	 *
	 * @param context
	 * @param type    1:支付宝，2:微信
	 */
	public static Dialog scanDialog(final Context context, final int type, final EPayTask inTask, final DialogCallBack callBack) {
		final Dialog       dialog     = createDialog(context, R.layout.dialog_pay_scancode, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		ComTextView        scan_title = (ComTextView) dialog.findViewById(R.id.scan_title);
		ComTextView        scan_close = (ComTextView) dialog.findViewById(R.id.scan_close);
		final ComTextView  msg        = (ComTextView) dialog.findViewById(R.id.scan_msg);
		final LinearLayout retry_ll   = (LinearLayout) dialog.findViewById(R.id.retry_ll);
		TextView           creat      = (TextView) dialog.findViewById(R.id.creat);
		TextView           retry      = (TextView) dialog.findViewById(R.id.retry);

		String str = type == 1 ? ToolsUtils.returnXMLStr("sth_zfb") : ToolsUtils
				.returnXMLStr("sth_wx");
		if (type == 12) {
			str = ToolsUtils.returnXMLStr("lkl_pay");
		} else if (type == -8) {
			str = "";
		}

		scan_title.setText(ToolsUtils.returnXMLStr("guest") + str + ToolsUtils
				.returnXMLStr("scan_pos_qrcode"));

		final int   second = 90;
		final int[] num    = new int[1];
		num[0] = second;
		final Timer[] mTimer = new Timer[1];
		mTimer[0] = new Timer();

		msg.setText(ToolsUtils.returnXMLStr("wait_pay_please_wating") + "(" + num[0] + ToolsUtils
				.returnXMLStr("second") + ")");

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {

				if (num[0] > 0) {
					msg.setText(ToolsUtils
							.returnXMLStr("wait_pay_please_wating") + "(" + num[0] + ToolsUtils
							.returnXMLStr("second") + ")");
				} else {
					if (inTask != null) {
						inTask.cancel(true);
					}
					num[0] = second;
					msg.setText(ToolsUtils.returnXMLStr("wait_pay_timeOut_please_try_again"));
					mTimer[0].cancel();
					retry_ll.setVisibility(View.VISIBLE);
				}
			}
		};

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				num[0]--;
				handler.sendMessage(message);
			}
		};
		mTimer[0].schedule(timerTask, 1000, 1000);

		//当调用查询支付结果接口超时并确认客户已支付时，手动点击按钮调用下单
		creat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("正扫==>>当调用查询支付结果接口超时并确认客户已支付时,点击按钮调用下单");
				dialog.dismiss();
				callBack.onCancle();
			}
		});

		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onOk();
				ToolsUtils.writeUserOperationRecords("正扫==>>重试调用扫码支付");
				dialog.dismiss();
				//                retry_ll.setVisibility(View.GONE);
				//                mTimer[0] = new Timer();
				//                TimerTask mTimerTask = new TimerTask() {
				//                    @Override
				//                    public void run() {
				//                        Message message = new Message();
				//                        num[0]--;
				//                        handler.sendMessage(message);
				//                    }
				//                };
				//                mTimer[0].schedule(mTimerTask,1000,1000);
				//                callBack.onOk();
			}
		});

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (inTask != null) {
					inTask.cancel(true);
				}
				if (mTimer[0] != null) {
					mTimer[0].cancel();
				}

				//清空副屏，展示默认欢迎页
				//                SunmiSecondScreen.getInstance(context).showImageListScreen();
			}
		});

		scan_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onCancle();
				ToolsUtils.writeUserOperationRecords("正扫==>>关闭扫描窗口");
				dialog.dismiss();
			}
		});

		return dialog;
	}


	/**
	 * 扫码弹出框
	 *
	 * @param context
	 * @param type      1:支付宝，2:微信
	 * @param qrCodeUrl 二维码号
	 */
	public static Dialog scanPadDialog(final Context context, final int type, final String qrCodeUrl, final Bitmap qrCodeBit, final InterfaceDialog interfaceDialog) {
		final Dialog       dialog       = createDialog(context, R.layout.dialog_pay_pad_scancode, 6, LinearLayout.LayoutParams.WRAP_CONTENT);
		final Store        store        = Store.getInstance(context);
		final TextView     scan_code_tv = (TextView) dialog.findViewById(R.id.scan_code_tv);
		final LinearLayout retry_ll     = (LinearLayout) dialog.findViewById(R.id.retry_ll);
		ImageView          img          = (ImageView) dialog.findViewById(R.id.scan_code_iv);
		TextView           creat        = (TextView) dialog.findViewById(R.id.creat);
		TextView           retry        = (TextView) dialog.findViewById(R.id.retry);
		String str = type == 1 ? ToolsUtils.returnXMLStr("sth_zfb") : ToolsUtils
				.returnXMLStr("sth_wx");

		final String title = ToolsUtils.returnXMLStr("guest") + str + ToolsUtils
				.returnXMLStr("scan_pos_qrcode");
		Bitmap bitmap = null;
		Bitmap qrcode = CreateImage.creatQRImage(qrCodeUrl, bitmap, 450, 450);
		img.setImageBitmap(qrcode);

		scan_code_tv.setText(title);

		creat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("已支付==扫码弹出框");
				interfaceDialog.onCancle();
			}
		});
		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("重试==扫码弹出框");
				retry_ll.setVisibility(View.GONE);
				interfaceDialog.onOk("");
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		return dialog;
	}


	/**
	 * 扫码弹出框
	 *
	 * @param context
	 * @param type      1:支付宝，2:微信
	 * @param qrCodeUrl 二维码号
	 */
	public static Dialog scanPadDialog(final Context context, final int type, final EPayTask inTask, final String qrCodeUrl, final Bitmap qrCodeBit, final InterfaceDialog interfaceDialog) {
		final Dialog       dialog       = createDialog(context, R.layout.dialog_pay_pad_scancode, 6, LinearLayout.LayoutParams.WRAP_CONTENT);
		final Store        store        = Store.getInstance(context);
		final TextView     scan_code_tv = (TextView) dialog.findViewById(R.id.scan_code_tv);
		final LinearLayout retry_ll     = (LinearLayout) dialog.findViewById(R.id.retry_ll);
		ImageView          img          = (ImageView) dialog.findViewById(R.id.scan_code_iv);
		TextView           creat        = (TextView) dialog.findViewById(R.id.creat);
		TextView           retry        = (TextView) dialog.findViewById(R.id.retry);

		final int   second = 90;
		final int[] num    = new int[1];
		num[0] = second;
		final Timer mTimer = new Timer();

		String str = type == 1 ? ToolsUtils.returnXMLStr("sth_zfb") : ToolsUtils
				.returnXMLStr("sth_wx");
		if (type == 12) {
			str = ToolsUtils.returnXMLStr("lkl_pay");
		} else if (type == -8) {
			str = "";
		}
		final String title = ToolsUtils.returnXMLStr("guest") + str + ToolsUtils
				.returnXMLStr("scan_pos_qrcode");

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {

				if (num[0] >= 0) {
					scan_code_tv.setText(title + "(" + num[0] + "s)");
				} else {
					mTimer.cancel();
					retry_ll.setVisibility(View.VISIBLE);
				}
			}
		};

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				num[0]--;
				handler.sendMessage(message);
			}
		};
		mTimer.schedule(timerTask, 1000, 1000);

		if (type == -8) {
			if (qrCodeBit == null) {
				img.setImageBitmap(CreateImage.convertStringToIcon(qrCodeUrl));
			} else {
				img.setImageBitmap(qrCodeBit);
			}
		} else {
			Bitmap bitmap = null;
			Bitmap qrcode = CreateImage.creatQRImage(qrCodeUrl, bitmap, 450, 450);
			img.setImageBitmap(qrcode);
		}

		scan_code_tv.setText(title);

		creat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("已支付==扫码弹出框");
				interfaceDialog.onCancle();
			}
		});
		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("重试==扫码弹出框");
				retry_ll.setVisibility(View.GONE);
				if (inTask != null) {
					inTask.cancel(true);
				}
				interfaceDialog.onOk("");
				dialog.dismiss();
			}
		});
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (mTimer != null) {
					mTimer.cancel();
				}
				if (inTask != null) {
					inTask.cancel(true);
				}
			}
		});
		return dialog;
	}


	/**
	 * 扫码枪扫描前提示框(反扫)
	 *
	 * @param context
	 * @param type         1:支付宝，2:微信
	 * @param micropayTask
	 * @return
	 */
	public static Dialog scanGunDialog(final Context context, final int type,
	                                   final MicropayTask micropayTask,
	                                   final ScanGunKeyEventHelper mScanGunKeyEventHelper, final DialogCallBack callBack) {
		final Dialog dialog = createDialog(context, R.layout.dialog_pay_scancode, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);

		ComTextView        scan_title = (ComTextView) dialog.findViewById(R.id.scan_title);
		ComTextView        scan_close = (ComTextView) dialog.findViewById(R.id.scan_close);
		final ComTextView  msg        = (ComTextView) dialog.findViewById(R.id.scan_msg);
		final LinearLayout retry_ll   = (LinearLayout) dialog.findViewById(R.id.retry_ll);
		TextView           creat      = (TextView) dialog.findViewById(R.id.creat);
		TextView           retry      = (TextView) dialog.findViewById(R.id.retry);

		String str = type == 1 ? ToolsUtils.returnXMLStr("sth_zfb") : ToolsUtils
				.returnXMLStr("sth_wx");
		scan_title.setText(ToolsUtils.returnXMLStr("please_scan_guest") + str);


		final int   second = 90;
		final int[] num    = new int[1];
		num[0] = second;
		final Timer[] mTimer = new Timer[1];
		mTimer[0] = new Timer();

		msg.setText(ToolsUtils.returnXMLStr("wait_pay_please_wating") + "(" + num[0] + ToolsUtils
				.returnXMLStr("second") + ")");

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message m) {

				if (num[0] > 0) {
					msg.setText(ToolsUtils
							.returnXMLStr("wait_pay_please_wating") + "(" + num[0] + ToolsUtils
							.returnXMLStr("second") + ")");
				} else {
					if (micropayTask != null) {
						micropayTask.cancel(true);
					}
					num[0] = second;
					msg.setText(ToolsUtils.returnXMLStr("wait_pay_timeOut_please_try_again"));
					mTimer[0].cancel();
					retry_ll.setVisibility(View.VISIBLE);
				}
			}
		};

		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message message = new Message();
				num[0]--;
				handler.sendMessage(message);
			}
		};
		mTimer[0].schedule(timerTask, 1000, 1000);

		//当调用查询支付结果接口超时并确认客户已支付时，手动点击按钮调用下单
		creat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("反扫==>>当调用查询支付结果接口超时并确认客户已支付时,点击按钮调用下单");
				dialog.dismiss();
				callBack.onCancle();
			}
		});

		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("反扫==>>重试调用扫码支付");
				dialog.dismiss();
				//                retry_ll.setVisibility(View.GONE);
				//                mTimer[0] = new Timer();
				//                TimerTask mTimerTask = new TimerTask() {
				//                    @Override
				//                    public void run() {
				//                        Message message = new Message();
				//                        num[0]--;
				//                        handler.sendMessage(message);
				//                    }
				//                };
				//                mTimer[0].schedule(mTimerTask,1000,1000);
				callBack.onOk();
			}
		});

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (micropayTask != null) {
					micropayTask.cancel(true);
				}
				if (mTimer[0] != null) {
					mTimer[0].cancel();
				}

				//清空副屏，展示默认欢迎页
				//                SunmiSecondScreen.getInstance(context).showImageListScreen();
			}
		});

		scan_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callBack.onCancle();
				ToolsUtils.writeUserOperationRecords("反扫==>>关闭扫描窗口");
				dialog.dismiss();
			}
		});

		//截获扫码枪按键事件.发给ScanGunKeyEventHelper
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
					mScanGunKeyEventHelper.analysisKeyEvent(event);
				}
				return false;
			}
		});

		return dialog;
	}


	/**
	 * 扫码快速点餐
	 *
	 * @param context
	 * @param mScanGunKeyEventHelper
	 * @return
	 */
	public static Dialog scanQuickOrder(final Context context, final ScanGunKeyEventHelper mScanGunKeyEventHelper) {
		final Dialog dialog = createDialog(context, R.layout.dialog_scan_quick_order, 5, LinearLayout.LayoutParams.WRAP_CONTENT, false);

		ComTextView       scan_title = (ComTextView) dialog.findViewById(R.id.scan_title);
		ComTextView       scan_close = (ComTextView) dialog.findViewById(R.id.scan_close);
		final ComTextView msg        = (ComTextView) dialog.findViewById(R.id.scan_msg);

		scan_title.setText(ToolsUtils.returnXMLStr("scan_ticket_qrcode_quick_order"));


		msg.setText(ToolsUtils.returnXMLStr("please_scan_guest_ticket_dish_qrcode"));

		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				//清空副屏，展示默认欢迎页
				//                SunmiSecondScreen.getInstance(context).showImageListScreen();
			}
		});

		scan_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("反扫==>>关闭快捷点餐扫描窗口");
				dialog.dismiss();
			}
		});

		//截获扫码枪按键事件.发给ScanGunKeyEventHelper
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
					mScanGunKeyEventHelper.analysisKeyEvent(event);
				}
				return false;
			}
		});

		return dialog;
	}


	/**
	 * 打印机弹出框
	 *
	 * @param context
	 * @param source  0:添加打印机；1:修改打印机
	 * @param printer 修改时打印机参数
	 */
	public static Dialog printerDialog(final Context context, final int source, final Printer printer, final DialogCallback dialogCallback) {
		final Dialog dialog = getDialog(context, R.layout.dialog_printer, 0.4f, 0.5f);

		TextView     print_title    = (TextView) dialog.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		EditText     print_ip       = (EditText) dialog.findViewById(R.id.print_ip);
		EditText     print_brand    = (EditText) dialog.findViewById(R.id.print_brand);
		TextView     print_type     = (TextView) dialog.findViewById(R.id.print_type);
		EditText     print_des      = (EditText) dialog.findViewById(R.id.print_des);
		TextView     print_cancle   = (TextView) dialog.findViewById(R.id.print_cancle);
		TextView     print_ok       = (TextView) dialog.findViewById(R.id.print_ok);

		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				dialogCallback.onConfirm();
			}
		});
		print_type.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!ToolsUtils.isList(ReceiptsDataController.receiptList)) {
					Intent intent = new Intent();
					intent.setClass(context, ReceiptsTypeAty.class);
					context.startActivity(intent);
				}
			}
		});

		if (source == Constant.DialogStyle.ADD_PRINTER) {
			print_title.setText("添加打印机");

		} else if (source == Constant.DialogStyle.MODIFY_PRINTER) {
			print_title.setText("修改打印机");
			print_ip.setText(printer.getIp());
			print_brand.setText(printer.getVendor());
			//            print_type.setText(printer.getReceiptTypeList());
			print_des.setText(printer.getDescription());
		}
		return dialog;
	}

	public static Dialog initTableInfo(final Context context, final DialogTCallback dialogCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_table_info, 0.5f, 0.3f);
		final CommonEditText ed_tableNumber = (CommonEditText) dialog
				.findViewById(R.id.ed_tableNumber);
		final CommonEditText ed_peopleNums = (CommonEditText) dialog
				.findViewById(R.id.ed_peopleNums);
		TextView print_ok     = (TextView) dialog.findViewById(R.id.print_ok);
		TextView print_cancle = (TextView) dialog.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialog
				.findViewById(R.id.print_close_ll);
		LinearLayout lin_money = (LinearLayout) dialog.findViewById(R.id.lin_money);
		lin_money.setVisibility(View.GONE);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭餐牌信息窗口");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消餐牌信息按钮");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定餐牌信息按钮");
				String tableNumber = ed_tableNumber.getText().toString().trim();
				String nums        = ed_peopleNums.getText().toString().trim();
				if (TextUtils.isEmpty(tableNumber)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_tableNumber"));
				} else {
					Order order = new Order();
					order.setTableNames(tableNumber);
					dialogCallback.onConfirm(order);
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}


	public static Dialog inputDialog(final Context context, final String title, final String content, final String contentHint, final int maxNums, final boolean isContentNull, final boolean isPrintZero, final DialogEtCallback dialogEtCallback) {
		Dialog dialog = null;
		if (!TextUtils.isEmpty(title) && title.equals(ToolsUtils.returnXMLStr("ct_kt"))) {
			dialog = DialogUtil.getDialog(context, R.layout.dialog_work_shift2, 0.5f, 0.3f);
		} else {
			dialog = DialogUtil.getDialog(context, R.layout.dialog_work_shift, 0.5f, 0.3f);
		}
		TextView       print_title         = (TextView) dialog.findViewById(R.id.print_title);
		TextView       tv_standby_money    = (TextView) dialog.findViewById(R.id.tv_standby_money);
		final EditText ed_standby_moneyEnd = (EditText) dialog.findViewById(R.id.ed_standby_money);
		TextView       print_ok            = (TextView) dialog.findViewById(R.id.print_ok);
		TextView       print_cancle        = (TextView) dialog.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialog
				.findViewById(R.id.print_close_ll);
		LinearLayout lin_select_work = (LinearLayout) dialog
				.findViewById(R.id.lin_select_work);
		lin_select_work.setVisibility(View.GONE);
		if (isContentNull) {
			//            ed_standby_moneyEnd.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
			if (title.equals(ToolsUtils.returnXMLStr("ct_kt"))) {
				print_close_ll.setVisibility(View.GONE);
				print_cancle.setText(ToolsUtils.returnXMLStr("ct_kt"));
				print_ok.setText(ToolsUtils.returnXMLStr("open_table_cliick_dish"));
			}
		}
		if (!TextUtils.isEmpty(content)) {
			tv_standby_money.setText(content);
		}
		if (!TextUtils.isEmpty(contentHint)) {
			ed_standby_moneyEnd.setHint(contentHint);
		}
		final Dialog finalDialog = dialog;
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				finalDialog.dismiss();
				dialogEtCallback.onCancle();
			}
		});


		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				finalDialog.dismiss();
				dialogEtCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				final String sth = ed_standby_moneyEnd.getText().toString().trim();
				if (isContentNull) {
					dialogEtCallback.onConfirm(sth);
					finalDialog.dismiss();
					return;
				}
				if (TextUtils.isEmpty(sth)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_enter_it_correctly"));
				} else if (!TextUtils.isEmpty(sth)) {
					if (maxNums != 0) {
						int num = Integer.valueOf(sth);
						if (num > maxNums) {
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("select_dish_person_more_is_select_person"));
						} else if (num == maxNums) {
							dialogEtCallback.onConfirm(sth);
							finalDialog.dismiss();
						}
					} else {
						int num = Integer.valueOf(sth);
						if (isPrintZero) {
							dialogEtCallback.onConfirm(sth);
							finalDialog.dismiss();
						} else {
							if (num == 0) {
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("please_enter_it_correctly"));
							} else {
								dialogEtCallback.onConfirm(sth);
								finalDialog.dismiss();
							}
						}
					}
				} else {
					dialogEtCallback.onConfirm(sth);
					finalDialog.dismiss();
				}
			}
		});
		return dialog;
	}

	public static Dialog openTableDialog(final Context context, final String title, final String content, final String contentHint, final int maxNums, final boolean isContentNull, final boolean isPrintZero, final DialogEtsCallback dialogEtCallback) {
		Dialog dialog = null;
		if (!TextUtils.isEmpty(title) && title.equals(ToolsUtils.returnXMLStr("ct_kt"))) {
			dialog = DialogUtil.getDialog(context, R.layout.dialog_work_shift2, 0.5f, 0.3f);
		} else {
			dialog = DialogUtil.getDialog(context, R.layout.dialog_work_shift, 0.5f, 0.3f);
		}
		TextView       print_title         = (TextView) dialog.findViewById(R.id.print_title);
		TextView       tv_standby_money    = (TextView) dialog.findViewById(R.id.tv_standby_money);
		final EditText ed_standby_moneyEnd = (EditText) dialog.findViewById(R.id.ed_standby_money);
		TextView       print_ok            = (TextView) dialog.findViewById(R.id.print_ok);
		TextView       print_cancle        = (TextView) dialog.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialog
				.findViewById(R.id.print_close_ll);
		LinearLayout lin_select_work = (LinearLayout) dialog
				.findViewById(R.id.lin_select_work);
		lin_select_work.setVisibility(View.GONE);
		if (isContentNull) {
			//            ed_standby_moneyEnd.setInputType(InputType.TYPE_CLASS_TEXT);
		}
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
			if (title.equals(ToolsUtils.returnXMLStr("ct_kt"))) {
				print_cancle.setText(ToolsUtils.returnXMLStr("ct_kt"));
				print_ok.setText(ToolsUtils.returnXMLStr("open_table_cliick_dish"));
			}
		}
		if (!TextUtils.isEmpty(content)) {
			tv_standby_money.setText(content);
		}
		if (!TextUtils.isEmpty(contentHint)) {
			ed_standby_moneyEnd.setHint(contentHint);
		}
		final Dialog finalDialog = dialog;
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				finalDialog.dismiss();
				//                dialogEtCallback.onCancle("");
			}
		});


		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				final String sth = ed_standby_moneyEnd.getText().toString().trim();
				if (isContentNull) {
					dialogEtCallback.onCancle(sth);
					finalDialog.dismiss();
					return;
				}
				if (TextUtils.isEmpty(sth)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_enter_it_correctly"));
				} else if (!TextUtils.isEmpty(sth)) {
					if (maxNums != 0) {
						int num = Integer.valueOf(sth);
						if (num > maxNums) {
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("select_dish_person_more_is_select_person"));
						} else if (num == maxNums) {
							dialogEtCallback.onCancle(sth);
							finalDialog.dismiss();
						}
					} else {
						int num = Integer.valueOf(sth);
						if (isPrintZero) {
							dialogEtCallback.onCancle(sth);
							finalDialog.dismiss();
						} else {
							if (num == 0) {
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("please_enter_it_correctly"));
							} else {
								dialogEtCallback.onCancle(sth);
								finalDialog.dismiss();
							}
						}
					}
				} else {
					dialogEtCallback.onCancle(sth);
					finalDialog.dismiss();
				}
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				final String sth = ed_standby_moneyEnd.getText().toString().trim();
				if (isContentNull) {
					dialogEtCallback.onConfirm(sth);
					finalDialog.dismiss();
					return;
				}
				if (TextUtils.isEmpty(sth)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_enter_it_correctly"));
				} else if (!TextUtils.isEmpty(sth)) {
					if (maxNums != 0) {
						int num = Integer.valueOf(sth);
						if (num > maxNums) {
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("select_dish_person_more_is_select_person"));
						} else if (num == maxNums) {
							dialogEtCallback.onConfirm(sth);
							finalDialog.dismiss();
						}
					} else {
						int num = Integer.valueOf(sth);
						if (isPrintZero) {
							dialogEtCallback.onConfirm(sth);
							finalDialog.dismiss();
						} else {
							if (num == 0) {
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("please_enter_it_correctly"));
							} else {
								dialogEtCallback.onConfirm(sth);
								finalDialog.dismiss();
							}
						}
					}
				} else {
					dialogEtCallback.onConfirm(sth);
					finalDialog.dismiss();
				}
			}
		});
		return dialog;
	}


	/**
	 * 两个输入框的dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog inputDoubleDialog(final Context context, final String titleStr, final String oneLineStr, final String twoLineStr, final DialogTCallback dialogTCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_double_simple, 0.5f, 0.5f);
		dialog.setCanceledOnTouchOutside(false);
		TextView             tv_back   = (TextView) dialog.findViewById(R.id.tv_back);
		TextView             tv_ok     = (TextView) dialog.findViewById(R.id.tv_ok);
		TextView             tv_title  = (TextView) dialog.findViewById(R.id.tv_title);
		TextView             tv_oneStr = (TextView) dialog.findViewById(R.id.tv_oneStr);
		TextView             tv_twoStr = (TextView) dialog.findViewById(R.id.tv_twoStr);
		final CommonEditText ed_oneStr = (CommonEditText) dialog.findViewById(R.id.ed_oneStr);
		final CommonEditText ed_twoStr = (CommonEditText) dialog.findViewById(R.id.ed_twoStr);

		tv_title.setText(titleStr);
		tv_oneStr.setText(oneLineStr);
		tv_twoStr.setText(twoLineStr);
		ed_oneStr.setHint(ToolsUtils.returnXMLStr("please_input") + oneLineStr);
		ed_twoStr.setHint(ToolsUtils.returnXMLStr("please_input") + twoLineStr);
		if (twoLineStr.contains(ToolsUtils.returnXMLStr("string_login_pwd")) || twoLineStr
				.contains("password")) {
			ed_twoStr
					.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		} else {
			ed_twoStr.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		}

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + titleStr + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});

		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + titleStr + "按钮");
				String edOneLineStr = ed_oneStr.getText().toString().trim();
				String edTwoLineStr = ed_twoStr.getText().toString().trim();
				if (TextUtils.isEmpty(edOneLineStr)) {
					MyApplication.getInstance()
							.ShowToast(oneLineStr + ToolsUtils.returnXMLStr("not_is_null"));
					return;
				}
				if (TextUtils.isEmpty(edTwoLineStr)) {
					MyApplication.getInstance()
							.ShowToast(twoLineStr + ToolsUtils.returnXMLStr("not_is_null"));
					return;
				}
				User user = new User();
				user.setName(edOneLineStr);
				user.setPassword(edTwoLineStr);
				dialog.cancel();
				dialogTCallback.onConfirm(user);
			}
		});
		return dialog;
	}

	/**
	 * 预订的dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog reserveDialog(final Context context, final DialogTCallback dialogTCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_reserve_info, 0.7f, 0.5f);
		final Calendar calendar = Calendar.getInstance();
		dialog.setCanceledOnTouchOutside(false);
		TextView       tv_back          = (TextView) dialog.findViewById(R.id.tv_back);
		TextView       tv_ok            = (TextView) dialog.findViewById(R.id.tv_ok);
		final EditText ed_customrerName = (EditText) dialog.findViewById(R.id.ed_customrerName);
		final EditText ed_phone         = (EditText) dialog.findViewById(R.id.ed_phone);
		final EditText ed_note          = (EditText) dialog.findViewById(R.id.ed_note);
		final TextView tv_date          = (TextView) dialog.findViewById(R.id.tv_date);
		final TextView tv_time          = (TextView) dialog.findViewById(R.id.tv_time);
		final EditText ed_person        = (EditText) dialog.findViewById(R.id.ed_person);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消预订按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定预订按钮");
				String customrerName = ed_customrerName.getText().toString().trim();
				String phoneNumber   = ed_phone.getText().toString().trim();
				String note          = ed_note.getText().toString().trim();
				String perspnNumber  = ed_person.getText().toString().trim();
				if (TextUtils.isEmpty(customrerName)) {
					MyApplication.getInstance().ShowToast("客户姓名不能为空!");
					return;
				}
				if (TextUtils.isEmpty(phoneNumber)) {
					MyApplication.getInstance().ShowToast("电话号码不能为空!");
					return;
				}
				if (TextUtils.isEmpty(perspnNumber)) {
					MyApplication.getInstance().ShowToast("就餐人数不能为空!");
					return;
				}
			}
		});

		tv_date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("预订==>>选择日期按钮");
				DatePicker    picker      = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
				MyApplication application = MyApplication.getInstance();
				picker.setHeight(application.getScreenHeight() / 3);
				picker.setCancelTextColor(application.getResources().getColor(R.color.red));
				picker.setSubmitTextColor(application.getResources()
						.getColor(R.color.blue_table_nomber_title));
				picker.setCancelTextSize(28);
				picker.setSubmitTextSize(28);
				int year  = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day   = calendar.get(Calendar.DAY_OF_MONTH);
				picker.setRangeStart(year, month, day);//开始范围
				picker.setRangeEnd(2022, 1, 1);//结束范围
				picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
					@Override
					public void onDatePicked(String year, String month, String day) {
						tv_date.setText(year + "-" + month + "-" + day);
					}
				});
				picker.show();
			}
		});

		tv_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("预订==>>选择时间按钮");
				TimePicker    picker      = new TimePicker((Activity) context, TimePicker.HOUR_24);
				MyApplication application = MyApplication.getInstance();
				picker.setHeight(application.getScreenHeight() / 3);
				picker.setCancelTextColor(application.getResources().getColor(R.color.red));
				picker.setSubmitTextColor(application.getResources()
						.getColor(R.color.blue_table_nomber_title));
				picker.setCancelTextSize(28);
				picker.setSubmitTextSize(28);
				int hour   = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				picker.setSelectedItem(hour, minute);
				picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
					@Override
					public void onTimePicked(String hour, String minute) {
						tv_time.setText(hour + ":" + minute);
					}
				});
				picker.show();
			}
		});
		return dialog;
	}


	/**
	 * 检测打印机状态
	 *
	 * @param context
	 * @return
	 */
	public static Dialog printerStateDialog(final Context context) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_printer_state, 0.8f, 0.8f);
		dialog.setCanceledOnTouchOutside(false);

		final LinearLayout lin_left  = (LinearLayout) dialog.findViewById(R.id.lin_left);
		final LinearLayout lin_right = (LinearLayout) dialog.findViewById(R.id.lin_right);

		LinearLayout   lin_print_state = (LinearLayout) dialog.findViewById(R.id.lin_print_state);
		final TextView print_state     = (TextView) dialog.findViewById(R.id.print_state);
		final TextView tv_line_state   = (TextView) dialog.findViewById(R.id.tv_line_state);

		LinearLayout   lin_print_record = (LinearLayout) dialog.findViewById(R.id.lin_print_record);
		final TextView print_record     = (TextView) dialog.findViewById(R.id.print_record);
		final TextView tv_line_record   = (TextView) dialog.findViewById(R.id.tv_line_record);

		TextView tv_ok      = (TextView) dialog.findViewById(R.id.tv_ok);
		ListView lv_printer = (ListView) dialog.findViewById(R.id.lv_printer);
		ListView lv_KDS     = (ListView) dialog.findViewById(R.id.lv_KDS);

		//        ListView lv_record = (ListView) dialog.findViewById(R.id.lv_record);
		tv_ok.setText(ToolsUtils.returnXMLStr("close"));
		final PrinterStateAdp printerStateAdp = new PrinterStateAdp(context);
		final KdsStateAdp     kdsStateAdp     = new KdsStateAdp(context);
		//        final PrintRecordAdp printRecordAdp = new PrintRecordAdp(context);
		lv_printer.setAdapter(printerStateAdp);
		lv_KDS.setAdapter(kdsStateAdp);
		//        lv_record.setAdapter(printRecordAdp);
		//打印机及KDS状态
		lin_print_state.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lin_left.setVisibility(View.VISIBLE);
				lin_right.setVisibility(View.GONE);
				print_state.setTextColor(context.getResources().getColor(R.color.title_text_color));
				tv_line_state.setBackgroundColor(context.getResources()
						.getColor(R.color.title_text_color));

				print_record
						.setTextColor(context.getResources().getColor(R.color.main_order_item_bg));
				tv_line_record
						.setBackgroundColor(context.getResources().getColor(R.color.transform));

			}
		});

		//打印记录
		lin_print_record.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				lin_left.setVisibility(View.GONE);
				lin_right.setVisibility(View.VISIBLE);
				print_state
						.setTextColor(context.getResources().getColor(R.color.main_order_item_bg));
				tv_line_state
						.setBackgroundColor(context.getResources().getColor(R.color.transform));

				print_record
						.setTextColor(context.getResources().getColor(R.color.title_text_color));
				tv_line_record.setBackgroundColor(context.getResources()
						.getColor(R.color.title_text_color));
			}
		});

		long                delayedTime = 800;
		long                cycleTime   = 4000;
		final List<Printer> printerList = PrinterDataController.getPrinterList();
		final List<KDS>     kdsList     = PrinterDataController.getKdsList();
		//        final List<PrintRecord> printRecordList = PrinterDataController.getPrintRecordList();
		final Timer timer = new Timer();//轮询打印机连接状态
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				// 要做的事情
				if (printerList != null && printerList.size() > 0) {
					printerStateAdp.setData(printerList);
				}
				if (kdsList != null && kdsList.size() > 0) {
					kdsStateAdp.setData(kdsList);
				}
				//                if(printRecordList != null && printRecordList.size() >0)
				//                {
				//                    printRecordAdp.setData(printRecordList);
				//                }
			}
		};

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		timer.schedule(task, delayedTime, cycleTime);//延时3秒并且2秒循环一次获取拉卡拉交易情况

		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消检测打印状态按钮");
				dialog.cancel();
				if (timer != null) {
					timer.cancel();
				}
			}
		});
		return dialog;
	}


	/**
	 * 记账的dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog changeAccountDialog(final Context context, final DialogTCallback dialogTCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_change_account, 0.7f, 0.5f);
		final Calendar calendar = Calendar.getInstance();
		dialog.setCanceledOnTouchOutside(false);
		TextView       tv_back          = (TextView) dialog.findViewById(R.id.tv_back);
		TextView       tv_ok            = (TextView) dialog.findViewById(R.id.tv_ok);
		EditText       ed_customrerName = (EditText) dialog.findViewById(R.id.ed_customrerName);
		EditText       ed_member_number = (EditText) dialog.findViewById(R.id.ed_member_number);
		EditText       ed_note          = (EditText) dialog.findViewById(R.id.ed_note);
		final TextView tv_date          = (TextView) dialog.findViewById(R.id.tv_date);
		final TextView tv_time          = (TextView) dialog.findViewById(R.id.tv_time);
		CheckBox       ck_select_remind = (CheckBox) dialog.findViewById(R.id.ck_select_remind);

		final String customrerName = ed_customrerName.getText().toString().trim();
		final String memberNumber  = ed_member_number.getText().toString().trim();
		final String note          = ed_note.getText().toString().trim();

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消记账按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定记账按钮");
				if (TextUtils.isEmpty(customrerName)) {
					MyApplication.getInstance().ShowToast("客户姓名不能为空!");
					return;
				}
				if (TextUtils.isEmpty(memberNumber)) {
					MyApplication.getInstance().ShowToast("会员号不能为空!");
					return;
				}

			}
		});

		tv_date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("记账==>>选择日期按钮");
				DatePicker    picker      = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
				MyApplication application = MyApplication.getInstance();
				picker.setHeight(application.getScreenHeight() / 3);
				picker.setCancelTextColor(application.getResources().getColor(R.color.red));
				picker.setSubmitTextColor(application.getResources()
						.getColor(R.color.blue_table_nomber_title));
				picker.setCancelTextSize(28);
				picker.setSubmitTextSize(28);
				int year  = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day   = calendar.get(Calendar.DAY_OF_MONTH);
				picker.setRangeStart(year, month, day);//开始范围
				picker.setRangeEnd(2022, 1, 1);//结束范围
				picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
					@Override
					public void onDatePicked(String year, String month, String day) {
						tv_date.setText(year + "-" + month + "-" + day);
					}
				});
				picker.show();
			}
		});

		tv_time.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("记账==>>选择时间按钮");
				TimePicker    picker      = new TimePicker((Activity) context, TimePicker.HOUR_24);
				MyApplication application = MyApplication.getInstance();
				picker.setHeight(application.getScreenHeight() / 3);
				picker.setCancelTextColor(application.getResources().getColor(R.color.red));
				picker.setSubmitTextColor(application.getResources()
						.getColor(R.color.blue_table_nomber_title));
				picker.setCancelTextSize(28);
				picker.setSubmitTextSize(28);
				int hour   = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				picker.setSelectedItem(hour, minute);
				picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
					@Override
					public void onTimePicked(String hour, String minute) {
						tv_time.setText(hour + ":" + minute);
					}
				});
				picker.show();
			}
		});
		return dialog;
	}


	/**
	 * 预定留位dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog reservePlaceDialog(final Context context, List<Reserve> reserveList, final DialogTCallback dialogTCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_reserve_place, 0.8f, 0.5f);
		dialog.setCanceledOnTouchOutside(false);
		TextView        tv_back         = (TextView) dialog.findViewById(R.id.tv_back);
		TextView        tv_add          = (TextView) dialog.findViewById(R.id.tv_add);
		ListView        lv_reserve      = (ListView) dialog.findViewById(R.id.lv_reserve);
		ReservePlaceAdp reservePlaceAdp = new ReservePlaceAdp(context);
		lv_reserve.setAdapter(reservePlaceAdp);
		reservePlaceAdp.setData(reserveList);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消预定留位按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});

		tv_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定预定留位按钮");
				dialog.cancel();
				dialogTCallback.onConfirm(null);
			}
		});
		return dialog;
	}

	/**
	 * 催菜dialog
	 *
	 * @param context
	 * @param title
	 * @param orderItems
	 * @param dialogCallback
	 * @return
	 */
	public static Dialog rushDishDialog(final Context context, final String title, final List<OrderItem> orderItems, int sectionsStyle, final DialogCallback dialogCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_rush_item, 0.7f, 0.7f, 1);
		TextView        tv_back  = (TextView) dialog.findViewById(R.id.tv_back);
		TextView        tv_add   = (TextView) dialog.findViewById(R.id.tv_add);
		TextView        tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		final TextView  tv_all   = (TextView) dialog.findViewById(R.id.tv_all);
		ListView        lv_order = (ListView) dialog.findViewById(R.id.lv_order);
		final boolean[] isCheck  = {true};
		tv_title.setText(title);
		final RushItemAdp rushItemAdp = new RushItemAdp(context, sectionsStyle);
		rushItemAdp.setData(orderItems);
		lv_order.setAdapter(rushItemAdp);
		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogCallback.onCancle();
			}
		});
		tv_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.cancel();
				dialogCallback.onConfirm();
			}
		});
		tv_all.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheck[0]) {
					ToolsUtils.writeUserOperationRecords("全部勾选按钮==>>" + title);
					tv_all.setText(ToolsUtils.returnXMLStr("all_order_cancle"));
					isCheck[0] = false;
					for (OrderItem orderItem : orderItems) {
						orderItem.isSelectItem = true;
					}
				} else {
					ToolsUtils.writeUserOperationRecords("全部取消按钮==>>" + title);
					tv_all.setText(ToolsUtils.returnXMLStr("all_order_select"));
					isCheck[0] = true;
					for (OrderItem orderItem : orderItems) {
						orderItem.isSelectItem = false;
					}
				}
				rushItemAdp.notifyDataSetChanged();
			}
		});

		//        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		//            @Override
		//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//                OrderItem orderItem = (OrderItem) DishDataController.orderItemList.get(position);
		//                if (orderItem != null) {
		//                    if (orderItem.isSelectItem) {
		//                        orderItem.isSelectItem = false;
		//                    } else {
		//                        orderItem.isSelectItem = true;
		//                    }
		//                    rushItemAdp.notifyDataSetChanged();
		//                }
		//            }
		//        });

		return dialog;
	}


	/**
	 * 补打结账单选择dialog
	 *
	 * @param context
	 * @param title
	 * @param printerList
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog ReprintDialog(final Context context, final String title, List<Printer> printerList, final DialogTCallback dialogTCallback) {
		//        final Dialog dialog = DialogUtil.getDialog(context, R.layout.dialog_empty_table, 0.5f, 0.4f, 1);
		final Dialog dialog   = createDialog(context, R.layout.dialog_empty_table, 6, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		TextView     tv_back  = (TextView) dialog.findViewById(R.id.tv_back);
		TextView     tv_add   = (TextView) dialog.findViewById(R.id.tv_add);
		TextView     tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		GridView     handleGv = (GridView) dialog.findViewById(R.id.handle_gv);
		tv_title.setText(title);
		handleGv.setNumColumns(2);
		final ReprintAdp reprintAdp = new ReprintAdp(context);
		reprintAdp.setData(printerList);
		handleGv.setAdapter(reprintAdp);
		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onConfirm(1);
			}
		});
		handleGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Printer printer = (Printer) ReprintController.getRePrinterList().get(position);
				if (printer != null) {
					if (printer.isSelect) {
						printer.isSelect = false;
						ToolsUtils
								.writeUserOperationRecords("取消选择" + printer.getDeviceName() + "按钮");
					} else {
						printer.isSelect = true;
						ToolsUtils.writeUserOperationRecords("选择" + printer.getDeviceName() + "按钮");
					}
					reprintAdp.notifyDataSetChanged();
				}
			}
		});
		return dialog;
	}


	/**
	 * 转台
	 *
	 * @param context
	 * @param allTableInfor
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog turnTableDialog(final Context context, final String title, final int table_type, List<TableInfor> allTableInfor, final DialogTCallback dialogTCallback) {
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_empty_table, 0.7f, 0.7f, 1);
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back  = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_add   = (TextView) dialog.findViewById(R.id.tv_add);
		TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		GridView handleGv = (GridView) dialog.findViewById(R.id.handle_gv);
		tv_title.setText(title);
		final TableAdapter tableAdapter = new TableAdapter(context);
		tableAdapter.setSelectIndex(-1);
		tableAdapter.setData(allTableInfor);
		handleGv.setAdapter(tableAdapter);

		final TableInfor[] tableInfor = {null};
		handleGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				tableInfor[0] = (TableInfor) tableAdapter.getItem(position);
				if (Constant.TABLE_TURN == table_type) {
					if (tableInfor[0] != null && tableInfor[0].getStatus() == TableStatus.EMPTY)//空闲
					{
						tableAdapter.setSelectIndex(position);
					} else {
						MyApplication.getInstance().ShowToast("请选择空桌台");
					}
				} else if (Constant.TABLE_JOIN == table_type) {
					if (tableInfor[0] != null && tableInfor[0]
							.getStatus() == TableStatus.IN_USE)//使用
					{
						tableAdapter.setSelectIndex(position);
					} else {
						MyApplication.getInstance().ShowToast("请选择有订单桌台");
					}
				}
			}
		});

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});

		tv_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				if (tableInfor[0] != null) {
					dialog.cancel();
					dialogTCallback.onConfirm(tableInfor[0]);
				} else {
					MyApplication.getInstance().ShowToast("请选择桌台");
				}


			}
		});
		return dialog;
	}

	/**
	 * 外卖dialog
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog takeOutDialog(final Context context, final DialogTCallback dialogTCallback) {
		final String title = ToolsUtils.returnXMLStr("sth_take_out_title");
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_take_out, 0.9f, 0.9f);
		final Calendar calendar = Calendar.getInstance();
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok   = (TextView) dialog.findViewById(R.id.tv_ok);

		final EditText ed_customrerName = (EditText) dialog.findViewById(R.id.ed_customrerName);
		final EditText ed_phone         = (EditText) dialog.findViewById(R.id.ed_phone);
		final EditText ed_address       = (EditText) dialog.findViewById(R.id.ed_address);
		final EditText ed_outerOrderId  = (EditText) dialog.findViewById(R.id.ed_outerOrderId);

		//        EditText ed_note = (EditText) dialog.findViewById(R.id.ed_note);
		//        EditText ed_advance_minutes = (EditText) dialog.findViewById(R.id.ed_advance_minutes);
		//        final TextView tv_date = (TextView) dialog.findViewById(R.id.tv_date);
		//        final TextView tv_time = (TextView) dialog.findViewById(R.id.tv_time);
		//        CheckBox ck_select_remind = (CheckBox) dialog.findViewById(R.id.ck_select_remind);

		//        ed_customrerName.setText("张大猛");
		//        ed_phone.setText("110");
		//        ed_address.setText("左转");
		//        ed_outerOrderId.setText("10086");

		//        final String note = ed_note.getText().toString().trim();

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				String customrerName = ed_customrerName.getText().toString().trim();
				String phone         = ed_phone.getText().toString().trim();
				String address       = ed_address.getText().toString().trim();
				String outerOrderId  = ed_outerOrderId.getText().toString().trim();
				//                if (TextUtils.isEmpty(outerOrderId)) {
				//                    MyApplication.getInstance().ShowToast("请填写外部平台订单号!");
				//                    return;
				//                } else if (TextUtils.isEmpty(customrerName)) {
				//                    MyApplication.getInstance().ShowToast("请填写收货人姓名!");
				//                    return;
				//                } else if (TextUtils.isEmpty(phone)) {
				//                    MyApplication.getInstance().ShowToast("请填写收货人电话!");
				//                    return;
				//                } else if (TextUtils.isEmpty(address)) {
				//                    MyApplication.getInstance().ShowToast("请填写收货人地址!");
				//                    return;
				//                }
				Customer customer = new Customer();
				customer.setCustomerAddress(address);
				customer.setCustomerName(customrerName);
				customer.setCustomerPhoneNumber(phone);
				customer.setCustomerOuterOrderId(outerOrderId);
				PosInfo posInfo = PosInfo.getInstance();
				posInfo.setCustomer(customer);
				dialog.cancel();
				dialogTCallback.onConfirm(customer);
			}
		});

		//        tv_date.setOnClickListener(new View.OnClickListener() {
		//            @Override
		//            public void onClick(View v) {
		//                ToolsUtils.writeUserOperationRecords(title + "==>>选择日期按钮");
		//                DatePicker picker = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
		//                MyApplication application = MyApplication.getInstance();
		//                picker.setHeight(application.getScreenHeight() / 3);
		//                picker.setCancelTextColor(application.getResources().getColor(R.color.red));
		//                picker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
		//                picker.setCancelTextSize(28);
		//                picker.setSubmitTextSize(28);
		//                int year = calendar.get(Calendar.YEAR);
		//                int month = calendar.get(Calendar.MONTH);
		//                int day = calendar.get(Calendar.DAY_OF_MONTH);
		//                picker.setRangeStart(year, month, day);//开始范围
		//                picker.setRangeEnd(2022, 1, 1);//结束范围
		//                picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
		//                    @Override
		//                    public void onDatePicked(String year, String month, String day) {
		//                        tv_date.setText(year + "-" + month + "-" + day);
		//                    }
		//                });
		//                picker.show();
		//            }
		//        });
		//        tv_time.setOnClickListener(new View.OnClickListener() {
		//            @Override
		//            public void onClick(View v) {
		//                ToolsUtils.writeUserOperationRecords(title + "==>>选择时间按钮");
		//                TimePicker picker = new TimePicker((Activity) context, TimePicker.HOUR_24);
		//                MyApplication application = MyApplication.getInstance();
		//                picker.setHeight(application.getScreenHeight() / 3);
		//                picker.setCancelTextColor(application.getResources().getColor(R.color.red));
		//                picker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
		//                picker.setCancelTextSize(28);
		//                picker.setSubmitTextSize(28);
		//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
		//                int minute = calendar.get(Calendar.MINUTE);
		//                picker.setSelectedItem(hour, minute);
		//                picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
		//                    @Override
		//                    public void onTimePicked(String hour, String minute) {
		//                        tv_time.setText(hour + ":" + minute);
		//                    }
		//                });
		//                picker.show();
		//            }
		//        });
		return dialog;
	}


	public static Dialog cardRecordDialog(final Context context, final DialogTCallback dialogTCallback) {
		final String title = ToolsUtils.returnXMLStr("sth_cardRecord_title");
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_cardrecord, 0.7f, 0.5f);
		final Calendar calendar = Calendar.getInstance();
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok   = (TextView) dialog.findViewById(R.id.tv_ok);

		final EditText ed_customrerName = (EditText) dialog.findViewById(R.id.ed_customrerName);
		final EditText ed_contact       = (EditText) dialog.findViewById(R.id.ed_contact);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				String customrerName = ed_customrerName.getText().toString().trim();
				String contact       = ed_contact.getText().toString().trim();
				if (TextUtils.isEmpty(customrerName)) {
					MyApplication.getInstance().ShowToast("请输入联系人姓名");
					return;
				} else if (TextUtils.isEmpty(contact)) {
					MyApplication.getInstance().ShowToast("请填写联系人联系方式!");
					return;
				}
				CardRecord cardRecord = new CardRecord();
				cardRecord.setName(customrerName);
				cardRecord.setContact(contact);
				dialogTCallback.onConfirm(cardRecord);
				dialog.cancel();
			}
		});
		return dialog;
	}


	public static Dialog listDialog(final Context context, final String title, List<NetOrderRea> dataList, final DialogEtCallback dialogEtCallback) {
		if (ToolsUtils.isList(dataList)) {
			return null;
		}
		final Dialog dialog         = DialogUtil.getDialog(context, R.layout.dialog_list_item);
		TextView     print_title    = (TextView) dialog.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		ListView     lv_list        = (ListView) dialog.findViewById(R.id.lv_list);
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
		}
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				dialog.dismiss();
				dialogEtCallback.onCancle();
			}
		});
		final DialogListAdp dialogListAdp = new DialogListAdp(context);
		dialogListAdp.setData(dataList);
		lv_list.setAdapter(dialogListAdp);
		lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NetOrderRea reason = (NetOrderRea) dialogListAdp.getItem(position);
				if (reason != null) {
					dialogEtCallback.onConfirm(reason.refuseReason);
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}

	public static Dialog ordinaryDialog(final Context context, final String title, final String content, final DialogCallback dialogCallback) {
		final Dialog dialog         = getDialogShow(context, R.layout.dialog_ordinary, 0.4f, 0.3f, false, true);
		TextView     print_title    = (TextView) dialog.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		TextView     tv_content     = (TextView) dialog.findViewById(R.id.tv_content);
		TextView     tv_tips        = (TextView) dialog.findViewById(R.id.tv_tips);
		TextView     print_cancle   = (TextView) dialog.findViewById(R.id.print_cancle);
		TextView     print_ok       = (TextView) dialog.findViewById(R.id.print_ok);
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
		}
		tv_content.setText(TextUtils.isEmpty(content) ? ToolsUtils
				.returnXMLStr("text_app_version_hine") : content);
		if (!TextUtils.isEmpty(title) && title.equals(ToolsUtils.returnXMLStr("sth_home_logout"))) {
			tv_tips.setVisibility(View.VISIBLE);
		} else {
			tv_tips.setVisibility(View.GONE);
		}

		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.dismiss();
				dialogCallback.onConfirm();
			}
		});
		return dialog;
	}

	public static AlertDialog LoginErrorDialog(final Context context, final String title, final String content, final DialogCallback dialogCallback) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.dialog_ordinary);
		window.setBackgroundDrawableResource(R.color.transparent);
		alertDialog.setCanceledOnTouchOutside(false);

		WindowManager              m = ((Activity) context).getWindowManager();
		Display                    d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 0.4); // 高度设置为屏幕的0.5
		//        p.height = (int) (d.getHeight() * 0.3); // 宽度设置为屏幕的0.5
		window.setAttributes(p);

		TextView     print_title    = (TextView) window.findViewById(R.id.print_title);
		LinearLayout print_close_ll = (LinearLayout) window.findViewById(R.id.print_close_ll);
		TextView     tv_content     = (TextView) window.findViewById(R.id.tv_content);
		TextView     print_cancle   = (TextView) window.findViewById(R.id.print_cancle);
		TextView     print_ok       = (TextView) window.findViewById(R.id.print_ok);
		if (!TextUtils.isEmpty(title)) {
			print_title.setText(title);
		}
		if (!TextUtils.isEmpty(content)) {
			tv_content.setText(content);
		}

		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭" + title + "窗口");
				alertDialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				alertDialog.dismiss();
				dialogCallback.onCancle();
			}
		});
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				alertDialog.dismiss();
				dialogCallback.onConfirm();
			}
		});
		return alertDialog;
	}

	public static Dialog creatDishMenu(final Context context, final String title, final List<DishItem> dishItemList, final int width, final DialogTCallback dialogTCallback) {
		final Dialog               dialog = createDialog(context, R.layout.dialog_orderdish_item, 5, LinearLayout.LayoutParams.WRAP_CONTENT);
		Window                     window = dialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.TOP;
		params.x = width;
		window.setAttributes(params);

		TextView tv_back  = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok    = (TextView) dialog.findViewById(R.id.tv_ok);
		TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
		ListView lv_item  = (ListView) dialog.findViewById(R.id.lv_item);
		if (!TextUtils.isEmpty(title)) {
			tv_title.setText(title);
		}
		final DishItemAdp dishItemAdp = new DishItemAdp(context);
		dishItemAdp.setData(dishItemList);
		lv_item.setAdapter(dishItemAdp);

		lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DishItem dishItem = (DishItem) dishItemAdp.getItem(position);
				if (dishItem != null) {
					dialog.cancel();
					dialogTCallback.onConfirm(dishItem);
				}
			}
		});

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onConfirm(null);
			}
		});
		return dialog;
	}

	public static Dialog meiTuanDialog(final Context context, final Long orderId, final BigDecimal money, final boolean isCheckOut, CopyOnWriteArrayList<ValidationResponse> addValidationLists, final DialogMTCallback dialogMTCallback) {
		final double totalMoney = Double
				.valueOf(money.toString());
		final int[]                                    maxVouchersNums   = {99};//该张订单最大使用券的张数
		final CopyOnWriteArrayList<String>             addTicketList     = new CopyOnWriteArrayList<>();
		final CopyOnWriteArrayList<ValidationResponse> addValidationList = new CopyOnWriteArrayList<>();
		if (addValidationLists != null && addValidationLists.size() > 0) {
			for (ValidationResponse vv : addValidationLists) {
				addValidationList.add(vv);
				addTicketList.add(vv.getCouponCode());
			}
		}
		final Dialog      dialog   = createDialog(context, R.layout.meituan_layout, 7, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		final ComTextView mt_close = (ComTextView) dialog.findViewById(R.id.mt_close);
		final TextView tv_not_pay_price = (TextView) dialog
				.findViewById(R.id.tv_not_pay_price);
		final TextView tv_pay_price = (TextView) dialog.findViewById(R.id.tv_pay_price);
		final TextView wsh_warn     = (TextView) dialog.findViewById(R.id.wsh_warn);
		final ScrolListView ticket_list = (ScrolListView) dialog
				.findViewById(R.id.ticket_list);
		final TextView query  = (TextView) dialog.findViewById(R.id.query);
		final TextView submit = (TextView) dialog.findViewById(R.id.submit);
		tv_not_pay_price.setText(money.toString());

		//展示券列表的adapter
		class MTVouchersAdp extends BaseAdapter {
			private Context               context;
			private VoucherRefrushLisener callback;
			private String voucherStr = "";
			private EditText mCurrentEdtView;
			private StringBuffer                             sbb      = new StringBuffer();
			private CopyOnWriteArrayList<ValidationResponse> dataList = new CopyOnWriteArrayList<>();

			public ValidationResponse getValidation(int position) {
				return dataList.get(position);
			}

			/**
			 *  获得已经选择可使用券的总金额
			 * @return
			 */
			public double getVouchersMoney() {
				double money = 0;
				sbb.setLength(0);
				if (addValidationList != null && addValidationList.size() > 0) {
					addValidationList.clear();
				}
				if (dataList != null && dataList.size() > 0) {
					for (ValidationResponse validation : dataList) {
						if (validation.isSuccess() && validation.vouchersIsEff()) {
							money += validation.getDealValue();
							addValidationList.add(validation);//将已经成功添加了的券加入到列表中,后面下单需要此参数
							String sb   = sbb.toString();
							String code = validation.getCouponCode();
							String h    = ",";
							if (TextUtils.isEmpty(sb)) {
								sbb.append(code);
							} else {
								sbb.append(h + code);
							}
						}
					}
				}
				voucherStr = sbb.toString();
				return money;
			}

			public String getVoucherStr() {
				return voucherStr;
			}

			public MTVouchersAdp(Context context, VoucherRefrushLisener callback) {
				this.context = context;
				this.callback = callback;
				if (dataList != null && dataList.size() > 0) {
					dataList.clear();
				}
			}

			public void addNullItem() {
				if (dataList == null || dataList.size() == 0) {
					ValidationResponse validation2 = new ValidationResponse();
					validation2.setOperateType(3);//添加一个初始项
					dataList.add(validation2);
					notifyDataSetChanged();
				}
			}

			@Override
			public int getCount() {
				return dataList.size() != 0 ? dataList.size() : 0;
			}

			@Override
			public Object getItem(int position) {
				return null;
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}

			public void addValidation(ValidationResponse validation) {
				if (dataList == null) {
					dataList = new CopyOnWriteArrayList<>();
				}
				int dataSize           = dataList.size();
				int validationSuccSize = 0;//可以正常使用券的张数
				for (int i = 0; i < dataSize; i++) {
					ValidationResponse vv = dataList.get(i);
					if (vv != null) {
						if (vv.isSuccess() && vv.vouchersIsEff()) {
							validationSuccSize += 1;
						}
					}
					if (dataList.get(i) != null) {
						if (dataList.get(i).getOperateType() == 2 || dataList.get(i)
								.getOperateType() == 3) {
							removeValidation(i, true);//删除空项
						}
					}
				}
				//券有效并且在有效时间内
				if (validation.isSuccess() && validation.vouchersIsEff()) {
					if (validationSuccSize == 0) {
						maxVouchersNums[0] = validation.getCount();//将最大可使用的券张数保存起来,方便下次判断
						dataList.add(validation);
						addTicketList.add(validation.getCouponCode());
						callback.refrush(getVouchersMoney());
						notifyDataSetChanged();
					} else {
						if (validationSuccSize >= maxVouchersNums[0]) {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("voucher_is_max"));
							return;
						}
						if (maxVouchersNums[0] > validation.getCount()) {
							maxVouchersNums[0] = validation.getCount();
						}
						dataList.add(validation);
						addTicketList.add(validation.getCouponCode());
						callback.refrush(getVouchersMoney());
						notifyDataSetChanged();
					}
				} else {
					if (validation.getOperateType() == 2) //是一个可删除的项
					{
						dataList.add(validation);
						addTicketList.add(validation.getCouponCode());
						notifyDataSetChanged();
					} else {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("voucher_info_error"));
					}
				}
			}

			public void removeValidation(int position, boolean isRefresh) {
				if (dataList.get(position) != null) {
					ValidationResponse validation = dataList.get(position);
					if (validation.isSuccess()) {
						addTicketList.remove(validation.getCouponCode());
					}
					dataList.remove(position);
					callback.refrush(getVouchersMoney());
					notifyDataSetChanged();
				}
			}

			public EditText getCurrentEditView() {
				return mCurrentEdtView;
			}

			private View.OnFocusChangeListener mFocusChangedListener = new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View view, boolean hasFocus) {
					if (hasFocus) {
						mCurrentEdtView = (EditText) view;
					}
				}
			};

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				ViewHolder         holder     = null;
				ValidationResponse validation = null;
				if (dataList.get(position) != null) {
					validation = dataList.get(position);
				}
				if (convertView == null) {
					holder = new ViewHolder();
					convertView = LayoutInflater.from(context)
							.inflate(R.layout.meituan_add_view, null);
					holder.voucherCodeEd = (CommonEditText) convertView
							.findViewById(R.id.voucherCode);
					holder.lin_voucher_info = (LinearLayout) convertView
							.findViewById(R.id.lin_voucher_info);
					holder.tv_ver_state = (TextView) convertView.findViewById(R.id.tv_ver_state);
					holder.tv_cost = (TextView) convertView.findViewById(R.id.tv_cost);
					holder.tv_min_voucher = (TextView) convertView
							.findViewById(R.id.tv_min_voucher);
					holder.tv_ver_eff_time = (TextView) convertView
							.findViewById(R.id.tv_ver_eff_time);
					holder.tv_max_voucher = (TextView) convertView
							.findViewById(R.id.tv_max_voucher);
					holder.rel_right = (RelativeLayout) convertView.findViewById(R.id.rel_right);
					holder.btn_add = (Button) convertView.findViewById(btn_add);
					holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);

					holder.voucherCodeEd.setOnFocusChangeListener(mFocusChangedListener);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				if (validation != null) {
					if (validation.getOperateType() != 3) {
						holder.lin_voucher_info.setVisibility(View.VISIBLE);
						holder.rel_right.setVisibility(View.INVISIBLE);
						if (validation.getOperateType() == 1) {
							holder.btn_add.setVisibility(View.VISIBLE);
							holder.btn_minus.setVisibility(View.GONE);
						} else if (validation.getOperateType() == 2) {
							holder.btn_add.setVisibility(View.GONE);
							holder.btn_minus.setVisibility(View.VISIBLE);
						}
					} else {
						holder.lin_voucher_info.setVisibility(View.GONE);
						holder.rel_right.setVisibility(View.GONE);
					}
					if (validation.isSuccess()) {
						holder.voucherCodeEd.setText(validation.getCouponCode());//券码
						holder.tv_ver_state.setText(ToolsUtils.returnXMLStr("success"));
					} else {
						holder.tv_ver_state.setText(ToolsUtils.returnXMLStr("failure"));
					}
					holder.tv_cost.setText(validation.getDealValue() + " ￥");
					holder.tv_min_voucher.setText(validation.getMinConsume() + " " + ToolsUtils
							.returnXMLStr("leaf"));
					holder.tv_max_voucher
							.setText(validation.getCount() + " " + ToolsUtils.returnXMLStr("leaf"));
					holder.tv_ver_eff_time.setText(validation.getDealBeginTime());
					if (validation.getMinConsume() > 1 || validation.getCount() > 1) {
						holder.rel_right.setVisibility(View.VISIBLE);
						holder.btn_add.setVisibility(View.VISIBLE);
						holder.btn_minus.setVisibility(View.GONE);

						ValidationResponse validation2 = new ValidationResponse();
						validation2.setOperateType(2);//添加一个可删除的项
						//                        addValidation(validation2);
					} else {
						if (validation.getOperateType() != 3) {
							holder.rel_right.setVisibility(View.VISIBLE);
							holder.lin_voucher_info.setVisibility(View.VISIBLE);
							holder.voucherCodeEd.setText("");
							if (validation.getOperateType() == 0) {
								holder.btn_add.setVisibility(View.GONE);
								holder.btn_minus.setVisibility(View.GONE);
							} else if (validation.getOperateType() == 1) {
								holder.btn_add.setVisibility(View.VISIBLE);
								holder.btn_minus.setVisibility(View.GONE);
							} else if (validation.getOperateType() == 2) {
								holder.btn_add.setVisibility(View.GONE);
								holder.btn_minus.setVisibility(View.VISIBLE);
								holder.lin_voucher_info.setVisibility(View.GONE);
							}
						} else {
							holder.lin_voucher_info.setVisibility(View.GONE);
							holder.rel_right.setVisibility(View.GONE);
						}
					}
				} else {
					holder.lin_voucher_info.setVisibility(View.GONE);
				}

				holder.btn_add.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ValidationResponse validation2 = new ValidationResponse();
						validation2.setOperateType(2);//添加一个可删除的项
						addValidation(validation2);
					}
				});
				holder.btn_minus.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						removeValidation(position, true);
					}
				});

				//                holder.voucherCodeEd.setOnTouchListener(new View.OnTouchListener() {
				//                    @Override
				//                    public boolean onTouch(View v, MotionEvent event) {
				//                        ((ViewGroup) v.getParent())
				//                                .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
				//                        return false;
				//                    }
				//                });

				//                convertView.setOnTouchListener(new View.OnTouchListener() {
				//                    @Override
				//                    public boolean onTouch(View v, MotionEvent event) {
				//                        ((ViewGroup) v)
				//                                .setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
				//                        return false;
				//                    }
				//                });
				return convertView;
			}

			class ViewHolder {
				CommonEditText voucherCodeEd;
				TextView       tv_ver_state;
				TextView       tv_cost;
				TextView       tv_min_voucher;
				TextView       tv_ver_eff_time;
				TextView       tv_max_voucher;
				LinearLayout   lin_voucher_info;
				RelativeLayout rel_right;
				Button         btn_add;
				Button         btn_minus;
			}
		}

		final MTVouchersAdp mtVouchersAdp = new MTVouchersAdp(context, new VoucherRefrushLisener() {
			@Override
			public void refrush(double payMoney) {
				tv_pay_price.setText(String.format("%.2f ", (totalMoney - payMoney)) + "");
				if (isCheckOut) {
					if (payMoney >= totalMoney) {
						query.setVisibility(View.GONE);
						submit.setVisibility(View.VISIBLE);
					}
				} else {
					if (payMoney > 0) {
						query.setVisibility(View.VISIBLE);
						submit.setVisibility(View.VISIBLE);
					}

				}
			}
		});

		mtVouchersAdp.addNullItem();
		ticket_list.setAdapter(mtVouchersAdp);

		class VoucherCode implements ScanGunKeyEventHelper.OnScanSuccessListener {
			ProgressDialogF       progressDialog;
			StoreBusinessService  storeBusinessService;
			ScanGunKeyEventHelper mScanGunKeyEventHelper;

			public VoucherCode() {
				try {
					progressDialog = new ProgressDialogF(context);
					storeBusinessService = StoreBusinessService.getInstance();
					mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void keyEvent(KeyEvent event) {
				mScanGunKeyEventHelper.analysisKeyEvent(event);
			}

			//获取券码信息
			public void getVoucherInfo(final String num) {
				try {
					wsh_warn.setVisibility(View.GONE);
					progressDialog.showLoading("");
					storeBusinessService
							.validationSetout(num, new ResultCallback<ValidationResponse>() {
								@Override
								public void onResult(ValidationResponse result) {
									progressDialog.disLoading();
									if (result.isSuccess()) {
										wsh_warn.setVisibility(View.GONE);
										//如何第0项是初始项的话
										if (mtVouchersAdp.getValidation(0) != null && mtVouchersAdp
												.getValidation(0).getOperateType() == 3) {
											mtVouchersAdp.removeValidation(0, true);//删除空项
										}
										if (addTicketList != null && addTicketList.size() > 0) {
											if (addTicketList.contains(result.getCouponCode())) {
												MyApplication.getInstance().ShowToast(ToolsUtils
														.returnXMLStr("voucher_info_existing"));
												return;
											}
										}
										mtVouchersAdp.addValidation(result);
									} else {
										wsh_warn.setVisibility(View.VISIBLE);
										wsh_warn.setText(result.getMessage());
									}
								}

								@Override
								public void onError(PosServiceException e) {
									progressDialog.disLoading();
									MyApplication.getInstance().ShowToast(e.getMessage());
									wsh_warn.setVisibility(View.VISIBLE);
									wsh_warn.setText(e.getMessage());
									Log.e("查询券有误:====" + num, e.getMessage());
								}
							});
				} catch (Exception e) {
					e.printStackTrace();
					progressDialog.disLoading();
					wsh_warn.setVisibility(View.VISIBLE);
					wsh_warn.setText(e.getMessage());
					Log.e("查询券有误:====" + num, e.getMessage());
				}
			}

			public void executeCode() {
				wsh_warn.setVisibility(View.GONE);
				progressDialog.showLoading("");
				storeBusinessService.executeCode(mtVouchersAdp
						.getVoucherStr(), orderId, new ResultCallback<ValidationResponse>() {
					@Override
					public void onResult(ValidationResponse result) {
						progressDialog.disLoading();
						if (result.isSuccess()) {
							dialog.dismiss();
							wsh_warn.setVisibility(View.GONE);
							dialogMTCallback
									.onCheckout(new BigDecimal(mtVouchersAdp.getVouchersMoney())
											.setScale(2, BigDecimal.ROUND_HALF_UP), isCheckOut, addValidationList);
						} else {
							wsh_warn.setVisibility(View.VISIBLE);
							wsh_warn.setText(ToolsUtils.returnXMLStr("voucher_info_null"));
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.e("提交美团交易有误:====" + orderId, e.getMessage());
					}
				});
			}

			@Override
			public void onScanSuccess(String barcode) {
				Log.e("扫码枪扫会员卡返回:", barcode);
				if (!TextUtils.isEmpty(barcode)) {
					if (mtVouchersAdp.getVouchersMoney() >= totalMoney) {
						MyApplication.getInstance().ShowToast("券金额已支付完毕,请勿再次添加券");
						return;
					}
					getVoucherInfo(barcode);
				}
			}
		}

		final VoucherCode voucherCode = new VoucherCode();
		//截获扫码枪按键事件.发给ScanGunKeyEventHelper
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
					//                    if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
					voucherCode.keyEvent(event);
					//                    }
				}
				return false;
			}
		});

		//验券
		query.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("验证美团验券提交按钮");
				if (mtVouchersAdp.getCurrentEditView() != null) {
					final String num = mtVouchersAdp.getCurrentEditView().getText().toString()
							.trim();
					if (TextUtils.isEmpty(num)) {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("please_input_voucher_code"));
						return;
					}
					if (addTicketList != null && addTicketList.size() > 0) {
						if (addTicketList.contains(num)) {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("voucher_info_existing"));
							return;
						}
					}
					voucherCode.getVoucherInfo(num);
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_voucher_code"));
				}
			}
		});

		//提交付款申请
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isCheckOut) {
					//                    System.out.println("券码===》》" + mtVouchersAdp.getVoucherStr());
					Log.e("美团交易券列表:====" + orderId, mtVouchersAdp.getVoucherStr());
					voucherCode.executeCode();
				} else {
					if (addValidationList != null && addValidationList.size() > 0) {
						dialog.dismiss();
						dialogMTCallback.onCheckout(new BigDecimal(mtVouchersAdp.getVouchersMoney())
								.setScale(2, BigDecimal.ROUND_HALF_UP), isCheckOut, addValidationList);
					} else {
						MyApplication.getInstance().ShowToast(ToolsUtils
								.returnXMLStr("please_select_at_least_one_coupon"));
					}
				}
			}
		});

		mt_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭美团验券窗口");
				dialog.dismiss();
			}
		});

		//        voucherCodeEd.setOnKeyListener(new View.OnKeyListener() {
		//            @Override
		//            public boolean onKey(View v, int keyCode, KeyEvent event) {
		//                //MyApplication.getInstance().ShowToast(keyCode+"============"+event.getAction());
		//                if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
		//                    if (!TextUtils.isEmpty(voucherCodeEd.getText().toString().trim())) {
		//                        final String num = voucherCodeEd.getText().toString().trim();
		//                        if (TextUtils.isEmpty(num)) {
		//                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("please_input_voucher"));
		//                        } else {
		//                            WindowUtil.hiddenKey();
		//                            voucherCode.getVoucherInfo(num);
		//                        }
		//                    }
		//                    return true;
		//                }
		//                return false;
		//            }
		//        });
		return dialog;
	}

	/**
	 * 会员消费
	 *
	 * @param context
	 * @param typeid         支付类型，3:会员卡(储值),4:优惠券,5:积分
	 * @param money          未支付金额，用于优惠券判断是否满多少可以使用
	 * @param orderItems     菜品项
	 * @param isCheckBalance 是否要对订单进行全部支付
	 * @param creatDealBack
	 * @return
	 */
	public static Dialog memberDialog(final Context context, final int typeid, final BigDecimal money, final List<OrderItem> orderItems, final boolean isCheckBalance, final CreatDealBack creatDealBack) {
		final Account[] accountMemberInfo = {null};
		final BigDecimal memberMoney = ToolsUtils
				.cloneTo(money.setScale(2, BigDecimal.ROUND_DOWN));
		final PosInfo posInfo = PosInfo.getInstance();
		final Dialog  dialog  = createDialog(context, R.layout.wsh_layout, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		final ComTextView wsh_title = (ComTextView) dialog
				.findViewById(R.id.wsh_title);
		final ComTextView wsh_close = (ComTextView) dialog
				.findViewById(R.id.wsh_close);
		final CommonEditText cardno = (CommonEditText) dialog
				.findViewById(R.id.cardno);
		final TextView checkmember = (TextView) dialog
				.findViewById(R.id.checkmember);
		final TextView name       = (TextView) dialog.findViewById(R.id.name);
		final TextView cardnum_tv = (TextView) dialog.findViewById(R.id.cardnum_tv);
		final TextView cancel_saveMember = (TextView) dialog
				.findViewById(R.id.cancel_saveMember);
		TextView       cancle   = (TextView) dialog.findViewById(R.id.cancle);
		TextView       use      = (TextView) dialog.findViewById(R.id.use);
		final TextView wsh_warn = (TextView) dialog.findViewById(R.id.wsh_warn);
		final LinearLayout wsh_info_ll = (LinearLayout) dialog
				.findViewById(R.id.wsh_info_ll);
		final ListView couponList = (ListView) dialog.findViewById(R.id.couponList);
		final LinearLayout coupon_ll = (LinearLayout) dialog
				.findViewById(R.id.coupon_ll);
		final RelativeLayout balance_ll = (RelativeLayout) dialog
				.findViewById(R.id.balance_ll);
		final TextView tv_balance_hine = (TextView) dialog
				.findViewById(R.id.tv_balance_hine);
		final TextView tv_credit_hine = (TextView) dialog
				.findViewById(R.id.tv_credit_hine);
		final TextView tv_sure = (TextView) dialog.findViewById(R.id.tv_sure);
		final TextView member_info = (TextView) dialog
				.findViewById(R.id.member_info);
		final TextView tv_coupon_hine = (TextView) dialog
				.findViewById(R.id.tv_coupon_hine);
		final TextView order_money = (TextView) dialog
				.findViewById(R.id.order_money);
		final ScrolListView lv_memberCard = (ScrolListView) dialog
				.findViewById(R.id.lv_memberCard);
		final RelativeLayout credit_ll = (RelativeLayout) dialog
				.findViewById(R.id.credit_ll);
		final RelativeLayout rel_selectMemberCard = (RelativeLayout) dialog
				.findViewById(R.id.rel_selectMemberCard);

		final CheckBox ckSelectBalance = (CheckBox) dialog.findViewById(R.id.ck_select_balance);
		final CheckBox ckSelectCredit  = (CheckBox) dialog.findViewById(R.id.ck_select_credit);
		final TextView tv_pay_balance  = (TextView) dialog.findViewById(R.id.tv_pay_balance);
		final TextView tv_pay_credit   = (TextView) dialog.findViewById(R.id.tv_pay_credit);

		if (!isCheckBalance) {
			cancel_saveMember.setVisibility(View.GONE);
		} else {
			cancel_saveMember.setVisibility(View.VISIBLE);
		}

		switch (typeid) {
			case 3://会员卡
				wsh_title.setText(ToolsUtils.returnXMLStr("stored_value"));
				break;
			case 4://优惠券
				wsh_title.setText(ToolsUtils.returnXMLStr("chit"));
				break;
			case 5://积分
				wsh_title.setText(ToolsUtils.returnXMLStr("integrals"));
				break;
		}

		final boolean[] isCreditOpen = {false};//积分开关开启状态
		final boolean[] isStoredOpen = {false};//处置开关开启状态

		final Account[] memberInfo = new Account[1];
		//优惠券
		final List<WshAccountCoupon>[] coupons = new List[1];

		class MemberPay {
			public void logicPay(List dataList, boolean isCreditOpen, boolean isStoredOpen) {
				if (accountMemberInfo[0] != null) {
					//优惠券
					coupons[0] = ToolsUtils.cloneTo(dataList);
					int moneyCredit = 0;//优惠券金额
					if (coupons[0] != null && coupons[0].size() > 0) {
						for (WshAccountCoupon coupon : coupons[0]) {
							int deductibleAmount = coupon.getDeno() * coupon.getSelectCount();
							moneyCredit += deductibleAmount;
						}
					}
					//用户的积分
					BigDecimal userCredit = new BigDecimal("0.00");
					if (accountMemberInfo[0].getCredit() != null && accountMemberInfo[0]
							.isUsecredit() && isCreditOpen) {
						userCredit = new BigDecimal(accountMemberInfo[0].getCredit());
					}
					//用户的储值金额
					BigDecimal userStored = new BigDecimal("0.00");
					if (isStoredOpen) {
						userStored = new BigDecimal(accountMemberInfo[0].getBalance() / 100.0)
								.setScale(2, BigDecimal.ROUND_DOWN);
					}
					//用户使用券的金额
					BigDecimal userCoupon = new BigDecimal(moneyCredit);
					//用户的储值金额 + 使用了优惠券金额 + 积分
					BigDecimal countMoney = userStored.add(userCoupon).add(userCredit);
					//用户支付了多少
					BigDecimal userPay = new BigDecimal("0.00");
					//用户还需要支付多少
					BigDecimal userNeedPay = new BigDecimal("0.00");
					//是否支付完成
					boolean userIsPayEnd = false;
					userNeedPay = ToolsUtils.cloneTo(memberMoney);
					//如果使用了券
					if (userCoupon.compareTo(BigDecimal.ZERO) > 0) {
						userPay = userPay.add(userCoupon);
						userNeedPay = userNeedPay.subtract(userPay);
						if (userNeedPay.compareTo(BigDecimal.ZERO) != 1) {
							userNeedPay = new BigDecimal("0.00");
							userIsPayEnd = true;
						}

						order_money
								.setText(ToolsUtils.returnXMLStr("wating_pay_money") + userNeedPay);
						ckSelectBalance.setChecked(false);
						tv_pay_balance.setVisibility(View.GONE);
						ckSelectCredit.setChecked(false);
						tv_pay_credit.setVisibility(View.GONE);

					}
					if (!userIsPayEnd) {
						//如果积分大于0
						if (userCredit.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal decimal  = new BigDecimal("0");//小数部分 最后要给加上
							int        userNeed = userNeedPay.intValue();
							decimal = userNeedPay.subtract(new BigDecimal(userNeed));
							userNeedPay = new BigDecimal(userNeed + "");
							//积分等于需要支付的金额
							if (userNeedPay.compareTo(userCredit) == 0) {
								userNeedPay = new BigDecimal("0.00");
								order_money.setText(ToolsUtils
										.returnXMLStr("wating_pay_money") + decimal);
								ckSelectBalance.setChecked(false);
								tv_pay_balance.setVisibility(View.GONE);
								ckSelectCredit.setChecked(true);
								tv_pay_credit.setVisibility(View.VISIBLE);
								tv_pay_credit.setText(ToolsUtils
										.returnXMLStr("pay_intergral") + userCredit);
								//                                userNeedPay = userNeedPay.subtract(userCredit);
							}
							//需要支付的金额小于用户的积分
							else if (userNeedPay.compareTo(userCredit) == -1) {
								//                                userNeedPay = userCredit.subtract(userNeedPay);
								order_money.setText(ToolsUtils
										.returnXMLStr("wating_pay_money") + decimal);
								ckSelectBalance.setChecked(false);
								tv_pay_balance.setVisibility(View.GONE);
								ckSelectCredit.setChecked(true);
								tv_pay_credit.setVisibility(View.VISIBLE);
								tv_pay_credit.setText(ToolsUtils
										.returnXMLStr("pay_intergral") + userNeedPay);
								userNeedPay = new BigDecimal("0.00");
							}
							//需要支付的金额大于用户的积分
							else if (userNeedPay.compareTo(userCredit) == 1) {
								userNeedPay = userNeedPay.subtract(userCredit);
								order_money.setText(ToolsUtils
										.returnXMLStr("wating_pay_money") + decimal
										.add(userNeedPay));
								ckSelectBalance.setChecked(false);
								tv_pay_balance.setVisibility(View.GONE);
								ckSelectCredit.setChecked(true);
								tv_pay_credit.setVisibility(View.VISIBLE);
								tv_pay_credit.setText(ToolsUtils
										.returnXMLStr("pay_intergral") + userCredit);
							}
							userNeedPay = userNeedPay.add(decimal);
							if (userNeedPay.compareTo(BigDecimal.ZERO) == 0) {
								userIsPayEnd = true;
							}
						}
					}
					if (!userIsPayEnd) {
						//如果储值大于0
						if (userStored.compareTo(BigDecimal.ZERO) > 0) {
							BigDecimal storedShow = new BigDecimal("0.00");
							storedShow = ToolsUtils.cloneTo(userNeedPay);
							userNeedPay = userNeedPay.subtract(userStored);
							if (userNeedPay.compareTo(BigDecimal.ZERO) != 1) {
								userNeedPay = new BigDecimal("0.00");
								userIsPayEnd = true;
							}
							order_money.setText(ToolsUtils
									.returnXMLStr("wating_pay_money") + userNeedPay);
							ckSelectBalance.setChecked(true);
							tv_pay_balance.setVisibility(View.VISIBLE);
							tv_pay_balance.setText(ToolsUtils.returnXMLStr("paid2") + storedShow);

							//                            balance.setText(storedShow+"");
							if (!userIsPayEnd) {
								//                                balance.setText(userStored+"");
								tv_pay_balance
										.setText(ToolsUtils.returnXMLStr("paid2") + userStored);
							}
						}
					}
					if (!isCreditOpen) {
						tv_pay_credit.setText("");
						tv_pay_credit.setVisibility(View.GONE);
					}
					if (!isStoredOpen) {
						tv_pay_balance.setText("");
						tv_pay_balance.setVisibility(View.GONE);
					}
					if (!userIsPayEnd) {
						order_money
								.setText(ToolsUtils.returnXMLStr("wating_pay_money") + userNeedPay);
						if (isCheckBalance) {
							if (userNeedPay.compareTo(BigDecimal.ZERO) > 0) {
								MyApplication.getInstance().ShowToast(ToolsUtils
										.returnXMLStr("member_pay_money_error"));
							}
						}
					}
				}
			}
		}

		final MemberPay memberPay = new MemberPay();

		final CouponAdapter adapter = new CouponAdapter(context, memberMoney, new WSHListener() {
			@Override
			public void refrush(List dataList) {
				memberPay.logicPay(dataList, isCreditOpen[0], isStoredOpen[0]);
			}
		});
		couponList.setAdapter(adapter);

		//储值开关是否开启
		ckSelectBalance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isStoredOpen[0] = true;
				} else {
					isStoredOpen[0] = false;
				}
				memberPay.logicPay(coupons[0], isCreditOpen[0], isStoredOpen[0]);
			}
		});

		//积分开关是否开启
		ckSelectCredit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isCreditOpen[0] = true;
				} else {
					isCreditOpen[0] = false;
				}
				memberPay.logicPay(coupons[0], isCreditOpen[0], isStoredOpen[0]);
			}
		});


		class MemberInfor implements ScanGunKeyEventHelper.OnScanSuccessListener {

			ProgressDialogF       progressDialog;
			WshService            wshService;
			ScanGunKeyEventHelper mScanGunKeyEventHelper;

			public MemberInfor() {
				try {
					progressDialog = new ProgressDialogF(context);
					wshService = WshService.getInstance();
					mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public void keyEvent(KeyEvent event) {
				mScanGunKeyEventHelper.analysisKeyEvent(event);
			}

			public void setMemberInfoShow(Account account) {
				if (account.getBalance() != null && account.getBalance() > 0) {
					balance_ll.setVisibility(View.VISIBLE);
				} else {
					balance_ll.setVisibility(View.GONE);
				}
				if (account.isUsecredit()) {
					credit_ll.setVisibility(View.VISIBLE);
				} else {
					credit_ll.setVisibility(View.GONE);
				}
				if (account.getCoupons() != null && account.getCoupons().size() > 0) {
					tv_coupon_hine.setVisibility(View.VISIBLE);
					couponList.setVisibility(View.VISIBLE);
				} else {
					tv_coupon_hine.setVisibility(View.GONE);
					couponList.setVisibility(View.GONE);
				}
				if (account.getCredit() != null && account.getCredit() > 0) {
					credit_ll.setVisibility(View.VISIBLE);
				} else {
					credit_ll.setVisibility(View.GONE);
				}
			}

			//获取会员信息
			public void getMemberInfo(final String num) {
				try {
					progressDialog.showLoading("");
					wsh_info_ll.setVisibility(View.GONE);
					wshService.getMemberInfo(num, new ResultCallback<List<Account>>() {
						@Override
						public void onResult(final List<Account> result) {
							progressDialog.disLoading();
							if (result != null && result.size() > 0) {
								if (result.size() == 1) {
									Account account = result.get(0);
									accountMemberInfo[0] = ToolsUtils.cloneTo(account);
									wsh_warn.setVisibility(View.GONE);
									wsh_info_ll.setVisibility(View.VISIBLE);
									memberInfo[0] = account;
									setMemberInfoShow(account);
									name.setText(account.getName());
									cardnum_tv.setText(num);

									//用户的储值金额
									BigDecimal userStored = new BigDecimal(account
											.getBalance() / 100.0)
											.setScale(2, BigDecimal.ROUND_DOWN);
									tv_balance_hine.setText(ToolsUtils
											.returnXMLStr("amount_of_the_remaining_stored") + userStored + "(￥)");
									order_money.setText(ToolsUtils
											.returnXMLStr("wating_pay_money") + memberMoney
											.toString());
									tv_credit_hine.setText(ToolsUtils
											.returnXMLStr("intergral_of_the_remaining_stored") + account
											.getCredit());
									member_info.setText(ToolsUtils
											.returnXMLStr("member_card_number_xx2") + account
											.getUno() + "  " + ToolsUtils
											.returnXMLStr("phoneNumber") + "  " + account
											.getPhone() + "  " + ToolsUtils
											.returnXMLStr("sth_name2") + "  " + account
											.getName() + ToolsUtils
											.logicUserCardGender(account) + "  " + ToolsUtils
											.returnXMLStr("grade") + "  " + account.getGradeName());

									ckSelectBalance.setChecked(false);
									tv_pay_balance.setVisibility(View.GONE);
									ckSelectCredit.setChecked(false);
									tv_pay_credit.setVisibility(View.GONE);

									if (accountMemberInfo[0] != null) {
										posInfo.setAccountMember(accountMemberInfo[0]);
										EventBus.getDefault()
												.post(new PosEvent(Constant.EventState.MEMBER_INFO_CHANGE));
									}

									adapter.chandDataSize(account.getCoupons().size());
									adapter.setData(account.getCoupons());
								} else {
									rel_selectMemberCard.setVisibility(View.VISIBLE);
									wsh_warn.setVisibility(View.GONE);
									final MemberAdapter memberAdapter = new MemberAdapter(context);
									memberAdapter.setData(result);
									lv_memberCard.setAdapter(memberAdapter);
									final int[] current = {-1};
									lv_memberCard
											.setOnItemClickListener(new AdapterView.OnItemClickListener() {
												@Override
												public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
													current[0] = position;
													memberAdapter.setCurrent_select(position);
												}
											});

									tv_sure.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											if (current[0] != -1) {
												Account account = result.get(current[0]);
												accountMemberInfo[0] = ToolsUtils.cloneTo(account);
												wsh_warn.setVisibility(View.GONE);
												rel_selectMemberCard.setVisibility(View.GONE);
												wsh_info_ll.setVisibility(View.VISIBLE);

												memberInfo[0] = account;

												setMemberInfoShow(account);

												name.setText(account.getName());
												cardnum_tv.setText(num);

												//用户的储值金额
												BigDecimal userStored = new BigDecimal(account
														.getBalance() / 100.0)
														.setScale(2, BigDecimal.ROUND_DOWN);
												tv_balance_hine.setText(ToolsUtils
														.returnXMLStr("amount_of_the_remaining_stored") + userStored + "(￥)");
												order_money.setText(ToolsUtils
														.returnXMLStr("wating_pay_money") + memberMoney
														.toString());
												tv_credit_hine.setText(ToolsUtils
														.returnXMLStr("intergral_of_the_remaining_stored") + account
														.getCredit());
												member_info.setText(ToolsUtils
														.returnXMLStr("member_card_number_xx2") + account
														.getUno() + "  " + ToolsUtils
														.returnXMLStr("phoneNumber") + "  " + account
														.getPhone() + "  " + ToolsUtils
														.returnXMLStr("sth_name2") + "  " + account
														.getName() + ToolsUtils
														.logicUserCardGender(account) + "  " + ToolsUtils
														.returnXMLStr("grade") + "  " + account
														.getGradeName());


												ckSelectBalance.setChecked(false);
												tv_pay_balance.setVisibility(View.GONE);
												ckSelectCredit.setChecked(false);
												tv_pay_credit.setVisibility(View.GONE);

												if (accountMemberInfo[0] != null) {
													posInfo.setAccountMember(accountMemberInfo[0]);
													EventBus.getDefault()
															.post(new PosEvent(Constant.EventState.MEMBER_INFO_CHANGE));
												}

												adapter.chandDataSize(account.getCoupons().size());
												adapter.setData(account.getCoupons());
											} else {
												MyApplication.getInstance().ShowToast(ToolsUtils
														.returnXMLStr("please_select_member_card"));
											}
										}
									});
								}
							} else {
								wsh_warn.setVisibility(View.VISIBLE);
								rel_selectMemberCard.setVisibility(View.GONE);
								wsh_warn.setText(ToolsUtils
										.returnXMLStr("this_number_is_not_member"));
							}
						}

						@Override
						public void onError(PosServiceException e) {
							progressDialog.disLoading();
							wsh_info_ll.setVisibility(View.GONE);
							rel_selectMemberCard.setVisibility(View.GONE);
							wsh_warn.setVisibility(View.VISIBLE);
							wsh_warn.setText(e.getMessage());
							//                            cardno.setText("");
							MyApplication.getInstance().ShowToast(e.getMessage());
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
					progressDialog.disLoading();
					wsh_info_ll.setVisibility(View.GONE);
					wsh_warn.setVisibility(View.VISIBLE);
					wsh_warn.setText(e.getMessage());
				}
			}

			//创建交易
			public void creatDeal() {

				boolean isCanCreat1  = false;
				boolean isCanCreat2  = false;
				boolean isCanCreat3  = false;
				boolean isCanCreat4  = false;
				int     consumAmount = 0;//消费金额/分

				final WshCreateDeal.Request request = new WshCreateDeal.Request();
				final String                bis_id  = System.currentTimeMillis() + "";
				request.setBiz_id(bis_id);
				request.setConsume_amount(0);//分
				request.setCount_num(1);
				request.setPayment_amount(0);
				request.setPayment_mode(1);
				request.setSub_balance(0);
				request.setSub_credit(0);
				request.setRemark(ToolsUtils.returnXMLStr("consumption_preview"));
				request.setCno(memberInfo[0].getUno()); //卡号
				request.setUid(memberInfo[0].getUid());

				//处理菜品参数
				if (orderItems != null && orderItems.size() > 0) {
					List<WshCreateDeal.Pruduct> products = new ArrayList<>();
					for (OrderItem orderItem : orderItems) {
						WshCreateDeal.Pruduct pruduct = new WshCreateDeal.Pruduct();
						pruduct.name = orderItem.getDishName();
						pruduct.no = orderItem.getDishId() + "";
						pruduct.num = orderItem.getQuantity();
						pruduct.price = orderItem.getPrice().subtract(new BigDecimal(100))
								.intValue();//传的是分
						pruduct.is_activity = 2;//不参加活动
						products.add(pruduct);
					}
					request.setProducts(products);
				}
				final List<Payment> memberPayMent = new CopyOnWriteArrayList<>();
				int                 storedId      = 3;//储值
				int                 vouchersId    = 4;//券
				int                 integralId    = 5;//积分
				//储值
				//                if (memberInfo[0].getBalance() > 0) {
				String balanceStr = tv_pay_balance.getText().toString();
				balanceStr = balanceStr.replace(ToolsUtils.returnXMLStr("paid2"), "");
				try {
					if (!TextUtils.isEmpty(balanceStr)) {
						isCanCreat1 = true;
						isCanCreat4 = true;
						float balance = Float.parseFloat(balanceStr);
						balance = balance * 100;
						BigDecimal bd = new BigDecimal((double) balance);
						bd = bd.setScale(0, BigDecimal.ROUND_HALF_UP);
						BigDecimal bdd = bd
								.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						Payment payment = new Payment(storedId, ToolsUtils
								.returnXMLStr("member_stored_value"), bdd.floatValue());
						memberPayMent.add(payment);
						consumAmount += bd.floatValue();
						request.setSub_balance((int) bd.floatValue());
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("please_input_correct_stored_value"));
					return;
				}
				//                }

				//优惠券
				List<WshAccountCoupon> coupons = adapter.getSelectCoupon();
				if (coupons != null && coupons.size() > 0) {
					isCanCreat2 = true;
					isCanCreat4 = true;
					int          money = 0;//优惠券金额
					List<String> dinos = new ArrayList<String>();
					for (WshAccountCoupon coupon : coupons) {
						int deductibleAmount = coupon.getDeno() * coupon.getSelectCount();
						money += deductibleAmount;
						for (int i = 0; i < Math
								.min(coupon.getSelectCount(), coupon.getCoupon_ids().size()); i++) {
							dinos.add(coupon.getCoupon_ids().get(i));
						}
					}
					Payment payment = new Payment(vouchersId, ToolsUtils
							.returnXMLStr("coupon"), money);
					memberPayMent.add(payment);
					consumAmount += money * 100;
					request.setDeno_coupon_ids(dinos);
				}

				//积分
				//if (memberInfo[0].getCredit() > 0) {
				String creditStr = tv_pay_credit.getText().toString();
				creditStr = creditStr.replace(ToolsUtils.returnXMLStr("pay_intergral"), "");
				try {
					if (!TextUtils.isEmpty(creditStr)) {
						isCanCreat3 = true;
						isCanCreat4 = true;
						consumAmount += Integer.parseInt(creditStr) * 100;
						Payment payment = new Payment(integralId, ToolsUtils
								.returnXMLStr("member_intergrals"), Integer.parseInt(creditStr));
						memberPayMent.add(payment);
						request.setSub_credit(Integer.parseInt(creditStr));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("please_input_correct_stored_value"));
					return;
				}
				//                }

				if (isCanCreat4) {
					request.setConsume_amount(memberMoney.multiply(new BigDecimal("100"))
							.intValue());
					BigDecimal paymentAmount = memberMoney.multiply(new BigDecimal("100"))
							.subtract(new BigDecimal(consumAmount));
					if (paymentAmount.compareTo(BigDecimal.ZERO) != 1) {
						paymentAmount = new BigDecimal("0");
					}
					request.setPayment_amount(paymentAmount.intValue());
				} else if (isCanCreat1 == false && isCanCreat2 == false && isCanCreat3 == false) {
					request.setPayment_amount(memberMoney.multiply(new BigDecimal("100"))
							.intValue());
					request.setConsume_amount(memberMoney.multiply(new BigDecimal("100"))
							.intValue());
				}

				if (isCanCreat1 == false && isCanCreat2 == false && isCanCreat3 == false || isCanCreat4) {
					progressDialog.showLoading("");
					try {
						final boolean finalIsCanCreat1  = isCanCreat1;
						final boolean finalIsCanCreat2  = isCanCreat2;
						final boolean finalIsCanCreat3  = isCanCreat3;
						final boolean finalIsCanCreat4  = isCanCreat4;
						final int     finalConsumAmount = consumAmount;
						wshService.createDeal(request, new ResultCallback<WshDealPreview>() {
							@Override
							public void onResult(WshDealPreview result) {
								progressDialog.disLoading();
								BigDecimal print_money = new BigDecimal(request
										.getConsume_amount() / 100.0)
										.setScale(2, BigDecimal.ROUND_HALF_UP);
								if (isCheckBalance && finalIsCanCreat4)//需要结算订单剩下的全部余额
								{
									if (print_money.compareTo(memberMoney) == -1) {
										MyApplication.getInstance().ShowToast(ToolsUtils
												.returnXMLStr("please_enter_a_valid_amount"));
										return;
									}
								}
								dialog.dismiss();
								if (finalIsCanCreat1 == false && finalIsCanCreat2 == false && finalIsCanCreat3 == false) {
									if (!isCheckBalance) {
										creatDealBack
												.onDeal(bis_id, result, new BigDecimal(finalConsumAmount / 100.0)
														.setScale(2, BigDecimal.ROUND_HALF_UP), false, accountMemberInfo[0], memberPayMent);
									} else {
										creatDealBack.onDeal(bis_id, result, print_money
												.setScale(2, BigDecimal.ROUND_HALF_UP), false, accountMemberInfo[0], memberPayMent);
									}
									//                                    creatDealBack.onDeal(bis_id, result, new BigDecimal(finalConsumAmount / 100.0).setScale(2, BigDecimal.ROUND_HALF_UP), false, accountMemberInfo[0], memberPayMent);

								} else {
									creatDealBack
											.onDeal(bis_id, result, new BigDecimal(finalConsumAmount / 100.0)
													.setScale(2, BigDecimal.ROUND_HALF_UP), true, accountMemberInfo[0], memberPayMent);
								}
							}

							@Override
							public void onError(PosServiceException e) {
								progressDialog.disLoading();
								MyApplication.getInstance().ShowToast(e.getMessage());
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
						progressDialog.disLoading();
					}
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_select_valid_member_pay"));
				}
			}

			@Override
			public void onScanSuccess(String barcode) {
				Log.e("扫码枪扫会员卡返回:", barcode);
				if (!TextUtils.isEmpty(barcode)) {
					getMemberInfo(barcode);
				}
			}
		}

		final MemberInfor member = new MemberInfor();
		if (posInfo.getAccountMember() != null) {
			Account account  = posInfo.getAccountMember();
			String  showText = "";
			if (!TextUtils.isEmpty(account.getPhone())) {
				showText = account.getPhone();
			} else {
				showText = account.getUno();
			}
			cardno.setText(showText);
			member.getMemberInfo(account.getUno());
		}

		//截获扫码枪按键事件.发给ScanGunKeyEventHelper
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_HOME && keyCode != KeyEvent.KEYCODE_MENU) {
					if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event
							.getAction()) {
						member.keyEvent(event);
					}
				}
				return false;
			}
		});

		cardno.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//MyApplication.getInstance().ShowToast(keyCode+"============"+event.getAction());
				if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event
						.getAction()) {
					if (!TextUtils.isEmpty(cardno.getText().toString().trim())) {
						final String num = cardno.getText().toString().trim();
						if (TextUtils.isEmpty(num)) {
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("sth_member_number_hine"));
						} else {
							WindowUtil.hiddenKey();
							member.getMemberInfo(num);
						}
					}
					return true;
				}
				return false;
			}
		});


		wsh_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭会员消费窗口");
				dialog.dismiss();
			}
		});

		cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消会员消费按钮");
				dialog.dismiss();
			}
		});
		//验证会员
		checkmember.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("验证会员按钮");
				final String num = cardno.getText().toString().trim();
				if (TextUtils.isEmpty(num)) {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_member_number"));
					return;
				}
				member.getMemberInfo(num);
			}
		});

		//创建微生活交易
		use.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("创建微生活交易按钮");
				if (Cart.getDishItemList() != null && Cart.getDishItemList().size() > 0) {
					if (!isCheckBalance) {
						member.creatDeal();
					} else {
						final CheckOutUtil checUtil = new CheckOutUtil(context);
						checUtil.getDishStock(Cart.getDishItemList(), new DishCheckCallback() {
							@Override
							public void haveStock() {
								member.creatDeal();
							}

							@Override
							public void noStock(List dataList) {
								refreshDish(dataList, Cart.getDishItemList());
							}
						});
					}
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}

			}
		});

		cancel_saveMember.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消但保留会员信息按钮");
				if (accountMemberInfo[0] != null) {
					posInfo.setAccountMember(accountMemberInfo[0]);
					((OrderDishMainAty) context).setMemberInfo();
					dialog.dismiss();
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_correct_member_info"));
				}
			}
		});
		return dialog;
	}

	public static void refreshDish(List<DishCount> result, List<Dish> dishs) {
		//刷新菜品数据,显示沽清
		String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
		MyApplication.getInstance().ShowToast(ToolsUtils
				.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names + "。\n\n" + ToolsUtils
				.returnXMLStr("please_re_order"));
		Log.i("以下菜品份数不足:", names + "====<<");
	}

	public static void aboutDialog(Context context) {
		final Dialog dialog  = createDialog(context, R.layout.dialog_about, 4, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		TextView     version = (TextView) dialog.findViewById(R.id.about_version);

		version.setText(MyApplication.getInstance().getVersionName());

		dialog.setCanceledOnTouchOutside(true);
	}

	public static void aboutStoreDialog(Context context, TerminalInfo terminalInfo, final DialogCallback dialogCallback) {
		final Dialog dialog          = createDialog(context, R.layout.dialog_about_store, 4, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		TextView     version         = (TextView) dialog.findViewById(R.id.about_version);
		TextView     unbind_btn      = (TextView) dialog.findViewById(R.id.unbind_btn);
		TextView     tv_storeName    = (TextView) dialog.findViewById(R.id.tv_storeName);
		TextView     tv_phone        = (TextView) dialog.findViewById(R.id.tv_phone);
		TextView     tv_address      = (TextView) dialog.findViewById(R.id.tv_address);
		TextView     tv_terminalInfo = (TextView) dialog.findViewById(R.id.tv_terminalInfo);
		version.setText(terminalInfo.tname);
		tv_storeName.setText(terminalInfo.sname);
		tv_phone.setText(terminalInfo.phone);
		tv_address.setText(terminalInfo.address);
		tv_terminalInfo
				.setText("appId:" + terminalInfo.appid + "  brandId:" + terminalInfo.brandid + "  storeId:" + terminalInfo.storeid);

		unbind_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("解绑门店按钮");
				dialog.dismiss();
				dialogCallback.onConfirm();
			}
		});

		dialog.setCanceledOnTouchOutside(true);
	}

	/**
	 * 刷卡支付下单
	 *
	 * @param context
	 * @param cost
	 * @param dialogCall
	 * @return
	 */
	public static Dialog showMemberDialog(final Context context, final BigDecimal cost, final DialogCall dialogCall) {
		final Dialog dialog = createDialog(context, R.layout.dialog_member_card_pay, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		dialog.setCanceledOnTouchOutside(true);
		final EditText et_member  = (EditText) dialog.findViewById(R.id.et_member);
		final TextView tv_price   = (TextView) dialog.findViewById(R.id.tv_price);
		final TextView tv_card_no = (TextView) dialog.findViewById(R.id.tv_card_no);
		final TextView tv_balance = (TextView) dialog.findViewById(R.id.tv_balance);
		tv_card_no.setTag(0);
		tv_price.setText(ToolsUtils.returnXMLStr("this_consumption") + "   " + cost);

		et_member.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
			                          int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
			                              int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String  cardNo = s.toString();
				Integer tag    = (Integer) tv_card_no.getTag();// 是否读取过会员卡，反正重复读取
				if (tag == 1) {
					return;
				}
				if (cardNo.contains("\n") || cardNo.contains("\r")) {
					et_member.setText("");
					String                  mCardNo = cardNo.replace("\n", "").replace("\r", "");
					HashMap<String, String> cardMap = ToolsUtils.getCardInfo(context);
					String                  balance = cardMap.get(mCardNo);
					if (balance == null) {
						tv_card_no.setText(ToolsUtils.returnXMLStr("card_numb") + ToolsUtils
								.returnXMLStr("not_find_info"));
						tv_balance.setText(ToolsUtils.returnXMLStr("balance"));
						return;
					}
					BigDecimal subtract = new BigDecimal(balance).subtract(cost);
					tv_card_no.setText(ToolsUtils.returnXMLStr("card_numb") + mCardNo);
					if (subtract.compareTo(new BigDecimal(0)) < 0) {
						tv_balance.setText(ToolsUtils
								.returnXMLStr("balance") + balance + "￥,  " + ToolsUtils
								.returnXMLStr("insufficient_balance"));
						return;
					}
					tv_card_no.setTag(1);
					tv_balance.setText(ToolsUtils.returnXMLStr("balance") + balance + "￥");

					dialogCall.onOk("Success");
					dialog.dismiss();
				}
			}
		});
		return dialog;
	}

	public static Dialog showQuarters(final Context context, final List<Staff> staffList, final DialogTCallback dialogCall) {
		final Dialog dialog = createDialog(context, R.layout.dialog_staff_quarters, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		ScrolListView lv_staffQuarters = (ScrolListView) dialog
				.findViewById(R.id.lv_staffQuarters);
		final ComTextView wsh_close = (ComTextView) dialog.findViewById(R.id.wsh_close);
		final TextView    tv_sure   = (TextView) dialog.findViewById(R.id.tv_sure);
		wsh_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭会员消费窗口");
				dialog.dismiss();
			}
		});
		final StaffQuartersAdp staffQuartersAdp = new StaffQuartersAdp(context);
		lv_staffQuarters.setAdapter(staffQuartersAdp);
		final Staff[] staff = {null};
		if (staffList != null && staffList.size() > 0) {
			staffQuartersAdp.setData(staffList);
			lv_staffQuarters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					staffQuartersAdp.setCurrent_select(position);
					staff[0] = (Staff) staffQuartersAdp.getItem(position);
				}
			});
		}
		tv_sure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (staff[0] != null) {
					dialogCall.onConfirm(staff[0]);
				}
				dialog.dismiss();
			}
		});

		return dialog;
	}

	public static Dialog aboutCloudPos(final Context context) {
		Store        store     = Store.getInstance(context);
		UserData     mUserData = UserData.getInstance(context);
		PosInfo      posInfo   = PosInfo.getInstance();
		WifiManager  wifiMgr   = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo     info      = wifiMgr.getConnectionInfo();
		String       wifiId    = info != null ? info.getSSID() : "";
		final Dialog dialog    = createDialog(context, R.layout.dialog_about_pos, 8, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		dialog.setCanceledOnTouchOutside(true);
		TextView tv_appVersion     = (TextView) dialog.findViewById(R.id.tv_appVersion);
		TextView os_version        = (TextView) dialog.findViewById(R.id.os_version);
		TextView device_Name       = (TextView) dialog.findViewById(R.id.device_Name);
		TextView ip_address        = (TextView) dialog.findViewById(R.id.ip_address);
		TextView wifi_name         = (TextView) dialog.findViewById(R.id.wifi_name);
		TextView user_Name         = (TextView) dialog.findViewById(R.id.user_Name);
		TextView store_endTime     = (TextView) dialog.findViewById(R.id.store_endTime);
		TextView device_isInternet = (TextView) dialog.findViewById(R.id.device_isInternet);
		TextView server_address    = (TextView) dialog.findViewById(R.id.server_address);
		TextView memory_Size       = (TextView) dialog.findViewById(R.id.memory_Size);

		String currentapiVersion = android.os.Build.VERSION.RELEASE;
		tv_appVersion.setText("V " + ToolsUtils.getVersionName(context));
		os_version.setText("V " + currentapiVersion);
		device_Name.setText(store.getTerminalMac());
		ip_address.setText(ToolsUtils.getIPAddress(context));
		wifi_name.setText(wifiId.replace("\"", ""));
		user_Name.setText(mUserData.getRealName());
		store_endTime.setText(store.getStoreEndTime());
		device_isInternet.setText(TextUtils.isEmpty(wifiId) ? ToolsUtils
				.returnXMLStr("not_connect") : ToolsUtils.returnXMLStr("connect"));
		server_address.setText(posInfo.getServerUrl());
		memory_Size.setText(ToolsUtils.getAvailableInternalMemorySize(context));
		return dialog;
	}

	/**
	 * 单个输入框的dialog
	 *
	 * @return
	 */
	public static Dialog createSingleButtonDialog(Context context, String title, final DialogTCallback dialogTCallback) {
		final boolean[]           isConfirm           = {false};//判断是否要确认提交皮重数据
		final String              unit                = " kg";
		final TimerTaskController timerTaskController = TimerTaskController.getInstance();
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_three_button, 0.5f, 0.3f);
		TextView printTitle = (TextView) dialog
				.findViewById(R.id.print_title);
		TextView tv_standby_money = (TextView) dialog
				.findViewById(R.id.tv_standby_money);
		final CommonEditText ed_standby_moneyEnd = (CommonEditText) dialog
				.findViewById(R.id.ed_standby_money);
		TextView negativeButtonEnd = (TextView) dialog
				.findViewById(R.id.print_ok);
		TextView positiveButtonEnd = (TextView) dialog
				.findViewById(R.id.print_cancle);
		TextView zeroButtonEnd = (TextView) dialog
				.findViewById(R.id.print_zero);
		LinearLayout print_close_ll = (LinearLayout) dialog
				.findViewById(R.id.print_close_ll);
		final Store store = Store.getInstance(context);
		printTitle.setText(title);
		tv_standby_money.setText(ToolsUtils.returnXMLStr("sth_home_setting") + title);
		ed_standby_moneyEnd.setHint(ToolsUtils.returnXMLStr("please_input") + title);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭单个输入框的dialog窗口");
				dialog.dismiss();
				dialogTCallback.onCancle();
			}
		});

		/**
		 * 取消
		 */
		positiveButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消关闭单个输入框的dialog");
				dialog.dismiss();
				dialogTCallback.onCancle();
			}
		});

		/**
		 * 归零
		 */
		zeroButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("归零dialog");
				isConfirm[0] = false;
				timerTaskController.setTareWeightNum();
				timerTaskController.setZeroNum();
			}
		});

		/**
		 * 确认
		 */
		negativeButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确认单个输入框的dialog");
				isConfirm[0] = true;
				timerTaskController.setTareWeightNum();
			}
		});

		class WeightInfor implements WeightCallBack {
			public WeightInfor() {

			}

			@Override
			public void changeTareWeight(float tareWeight) {

			}

			@Override
			public void changeNetWeight(float netWeight) {
				ed_standby_moneyEnd.setText(netWeight + unit);
			}

			@Override
			public void changeGrossWeight(float netWeight) {

			}

			@Override
			public void setPointnumber(int pointnumber) {

			}

			@Override
			public void signalLampState(boolean isStable) {
			}

			@Override
			public void setTareNumSuccess(boolean state) {
				if (state && isConfirm[0]) {
					try {
						float weight = Float.valueOf(ed_standby_moneyEnd.getText().toString().trim()
								.replace(unit, ""));
						if (weight < 0) {
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("please_enter_it_correctly"));
							return;
						}
						store.setTareWeight(weight);
						TakeOut takeOut = new TakeOut();
						takeOut.setTakeOutStr(weight + "");
						dialogTCallback.onConfirm(takeOut);
						dialog.dismiss();
					} catch (NumberFormatException e) {
						e.printStackTrace();
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("please_enter_it_correctly"));
					}
				}
			}

			@Override
			public void setZeroNumSuccess(boolean state) {

			}
		}
		final WeightInfor   member               = new WeightInfor();
		TimerTaskController timerTaskController2 = TimerTaskController.getInstance();
		//说明开启了称重服务
		if (timerTaskController2.getLastWeight() != null) {
			timerTaskController2.setWeightCallBack(member);
		}
		return dialog;
	}


	/**
	 * 通过菜品重量得到当前菜品的价格
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog dishWeight(Context context, final Dish dish, final DialogTCallback dialogTCallback) {
		final Store  store      = Store.getInstance(context);
		final Dialog dialog     = createDialog(context, R.layout.dialog_dish_weight, 5, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		TextView     printTitle = (TextView) dialog.findViewById(R.id.print_title);
		final CommonEditText ed_dish_netWeight = (CommonEditText) dialog
				.findViewById(R.id.ed_dish_netWeight);
		TextView print_cancle = (TextView) dialog.findViewById(R.id.print_cancle);
		TextView print_ok     = (TextView) dialog.findViewById(R.id.print_ok);

		final ImageView img_weightState = (ImageView) dialog.findViewById(R.id.img_weightState);
		final TextView  tv_cost         = (TextView) dialog.findViewById(R.id.tv_cost);
		TextView        tv_unitPrice    = (TextView) dialog.findViewById(R.id.tv_unitPrice);
		TextView        tv_weight_hine  = (TextView) dialog.findViewById(R.id.tv_weight_hine);

		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		printTitle.setText(dish.getDishName());

		String dishUnit = dish.getDishUnit();
		dishUnit = TextUtils.isEmpty(dishUnit) ? "份" : dishUnit;
		tv_unitPrice.setText("￥" + dish.getPrice() + "/" + dishUnit);
		tv_weight_hine
				.setText(ToolsUtils.returnXMLStr("isSetTareNums") + store.getTareWeight() + "/kg");

		//右上关闭
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭菜品的价格dialog窗口");
				dialogTCallback.onCancle();
				dialog.dismiss();
			}
		});
		//取消
		print_cancle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭菜品的价格dialog窗口");
				dialogTCallback.onCancle();
				dialog.dismiss();
			}
		});
		//算出菜品总价
		print_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("算出菜品总价菜品的价格dialog窗口");
				try {
					DishWeight dishWeight = new DishWeight();
					float weight = Float
							.valueOf(ed_dish_netWeight.getText().toString().trim());
					if (weight < 0) {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("please_input_correct_money"));
						return;
					}
					dishWeight.setDishWeight(weight);
					dialogTCallback.onConfirm(dishWeight);
					dialog.dismiss();
				} catch (NumberFormatException e) {
					e.printStackTrace();
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("please_input_correct_money"));
				}
			}
		});

		class WeightInfor implements WeightCallBack {
			public WeightInfor() {

			}

			@Override
			public void changeTareWeight(float tareWeight) {

			}

			@Override
			public void changeNetWeight(float netWeight) {
				BigDecimal cost = dish.getPrice()
						.multiply(new BigDecimal(String.valueOf(netWeight)))
						.setScale(2, BigDecimal.ROUND_DOWN);
				tv_cost.setText("￥" + cost.toString());
				ed_dish_netWeight.setText(netWeight + "");
			}

			@Override
			public void changeGrossWeight(float netWeight) {

			}

			@Override
			public void setPointnumber(int pointnumber) {

			}

			@Override
			public void signalLampState(boolean isStable) {
				if (isStable) {
					img_weightState.setBackgroundResource(R.mipmap.point_green);
				} else {
					img_weightState.setBackgroundResource(R.mipmap.point_red);
				}
			}

			@Override
			public void setTareNumSuccess(boolean state) {

			}

			@Override
			public void setZeroNumSuccess(boolean state) {

			}
		}
		final WeightInfor   member              = new WeightInfor();
		TimerTaskController timerTaskController = TimerTaskController.getInstance();
		//说明开启了称重服务
		if (timerTaskController.getLastWeight() != null) {
			timerTaskController.setWeightCallBack(member);
		}
		return dialog;
	}


	/**
	 * 发票冲红
	 *
	 * @param context
	 * @param dialogTCallback
	 * @return
	 */
	public static Dialog revokeInvoiceDialog(final Context context, final DialogTCallback dialogTCallback) {
		final String title = ToolsUtils.returnXMLStr("ticket_revokeinvoice");
		final Dialog dialog = DialogUtil
				.getDialog(context, R.layout.dialog_revoke_invoice, 0.9f, 0.5f);
		dialog.setCanceledOnTouchOutside(false);
		TextView tv_back = (TextView) dialog.findViewById(R.id.tv_back);
		TextView tv_ok   = (TextView) dialog.findViewById(R.id.tv_ok);

		final EditText ed_outerOrderId = (EditText) dialog.findViewById(R.id.ed_outerOrderId);

		tv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消" + title + "按钮");
				dialog.cancel();
				dialogTCallback.onCancle();
			}
		});
		tv_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定" + title + "按钮");
				String outerOrderId = ed_outerOrderId.getText().toString().trim();
				if (TextUtils.isEmpty(outerOrderId)) {
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("please_input_ticket_revokeinvoice_reason"));
					return;
				}
				Customer customer = new Customer();
				customer.setCustomerOuterOrderId(outerOrderId);
				dialog.cancel();
				dialogTCallback.onConfirm(customer);
			}
		});
		return dialog;
	}

	public static Dialog switchWftPay(Context context, final KeyCallBack keyCallBack) {
		final Dialog       dialog     = createDialog(context, R.layout.dialog_sw_wft_pay, 5, 5, true);
		final LinearLayout wx_ll      = (LinearLayout) dialog.findViewById(R.id.wx_ll);
		final LinearLayout ali_ll     = (LinearLayout) dialog.findViewById(R.id.ali_ll);
		ComTextView        wx_tv      = (ComTextView) dialog.findViewById(R.id.wx_tv);
		ComTextView        ali_tv     = (ComTextView) dialog.findViewById(R.id.ali_tv);
		ComTextView        scan_close = (ComTextView) dialog.findViewById(R.id.scan_close);
		//WX
		wx_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("WFT选择支付方式微信按钮");
				wx_ll.setSelected(true);
				ali_ll.setSelected(false);
				keyCallBack.onOk(0);
				dialog.cancel();
			}
		});

		//ALI
		ali_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("WFT选择支付方式支付宝按钮");
				wx_ll.setSelected(false);
				ali_ll.setSelected(true);
				keyCallBack.onOk(1);
				dialog.cancel();
			}
		});
		scan_close.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("WFT选择支付方式取消按钮");
				dialog.cancel();
			}
		});
		return dialog;
	}
}
