package cn.acewill.pos.next.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.common.MainEvenBusController;
import cn.acewill.pos.next.common.MarketDataController;
import cn.acewill.pos.next.common.NetOrderController;
import cn.acewill.pos.next.common.PosSinUsbScreenController;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.PermissionCallback;
import cn.acewill.pos.next.model.Definition;
import cn.acewill.pos.next.model.Discount;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.KitchenStall;
import cn.acewill.pos.next.model.Market;
import cn.acewill.pos.next.model.TerminalInfo;
import cn.acewill.pos.next.model.TerminalVersion;
import cn.acewill.pos.next.model.WorkShift;
import cn.acewill.pos.next.model.WorkShiftNewReport;
import cn.acewill.pos.next.model.WorkShiftReport;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.MarketingActivity;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.printer.PrinterTemplates;
import cn.acewill.pos.next.printer.usb.GpUsbPrinter;
import cn.acewill.pos.next.printer.usb.UsbPrinter;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.LogService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.PrintManager;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.service.UpLoadOrderService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.service.retrofit.response.ScreenResponse;
import cn.acewill.pos.next.ui.activity.newPos.CallGoodsAty;
import cn.acewill.pos.next.ui.activity.newPos.CardRecordsAty;
import cn.acewill.pos.next.ui.activity.newPos.DishCountSettAty;
import cn.acewill.pos.next.ui.activity.newPos.DishMenuAty;
import cn.acewill.pos.next.ui.activity.newPos.ErrPrinteAty;
import cn.acewill.pos.next.ui.activity.newPos.ErrTipsAty;
import cn.acewill.pos.next.ui.activity.newPos.ManageSetAty;
import cn.acewill.pos.next.ui.activity.newPos.MemberAty;
import cn.acewill.pos.next.ui.activity.newPos.MessAgeAty;
import cn.acewill.pos.next.ui.activity.newPos.ModifiPwAty;
import cn.acewill.pos.next.ui.activity.newPos.OrderAmountAty;
import cn.acewill.pos.next.ui.activity.newPos.OrderInfoHistoryAty;
import cn.acewill.pos.next.ui.activity.newPos.ReportAty;
import cn.acewill.pos.next.ui.activity.newPos.ShowReportAty;
import cn.acewill.pos.next.ui.activity.newPos.StaffAty;
import cn.acewill.pos.next.ui.activity.newPos.StandByCashAty;
import cn.acewill.pos.next.ui.activity.newPos.UpLoadActivity;
import cn.acewill.pos.next.ui.activity.newPos.WorkShiftHistoryAty;
import cn.acewill.pos.next.ui.adapter.DefinitionAdp;
import cn.acewill.pos.next.ui.fragment.FastFoodFragment;
import cn.acewill.pos.next.ui.fragment.TableFragment2;
import cn.acewill.pos.next.ui.presentation.SecondaryScreenShow;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DownUtlis;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.MainPopWindow;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ScrolGridView;
import cn.weightservice.Weight;

/**
 * Created by DHH on 2016/12/23.
 */

public class OrderDishMainAty extends BaseActivity {
	@BindView(R.id.tv_order)
	TextView       tvOrder;
	@BindView(R.id.tv_menu)
	TextView       tvMenu;
	@BindView(R.id.tv_showView)
	TextView       tvShowView;
	@BindView(R.id.tv_search)
	TextView       tvSearch;
	@BindView(R.id.tv_checkBox)
	TextView       tvCheckBox;
	@BindView(R.id.tv_main_title)
	TextView       tvMainTitle;
	@BindView(R.id.tv_mainSelect)
	TextView       tvMainSelect;
	@BindView(R.id.rel_tab)
	RelativeLayout relTab;
	@BindView(R.id.rel_top)
	RelativeLayout relTop;

	private String TAG = "OrderDishMainAty";
	private PosInfo                   posInfo;
	private Store                     store;
	private GpUsbPrinter              usbPrinter;
	private PosSinUsbScreenController posSinUsbScreenController;
	private UserData                  mUserData;
	private Integer                   workshiftId;
	private LogService                logService;
	//    private IWeight iWeight;
	private Weight                    weight;
	private SystemService             systemService;
	private Intent                    intent;
	//    private BadgeView badeView;
	private ProgressDialogF           progressDialog;

	private SecondaryScreenShow secondaryScreendShow = null;//副屏
	private ScreenResponse screenResponse;
	private boolean isSunMiScreen = true;//是否是商米副屏

	private Fragment tempFragment = null;
	private TableFragment2   tableFragment;
	private FastFoodFragment fastFoodFragment;

	private MainPopWindow mainPopWindow;

	private int checkOutPrintCounts = 1;//结账单打印张数
	private int guestReceiptCounts  = 1;//客用小票打印张数

	private boolean isShowDishImg = false;
	private TimerTaskController timerTaskController;

	private static int NETORDERITEM = 110;//网络订单回调
	private static int FETCHORDER   = 120;//取单回调

	//监听back键,弹出退出提示dialog
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("exit"), ToolsUtils
					.returnXMLStr("whether_to_exit"), new DialogCallback() {
				@Override
				public void onConfirm() {
					MyApplication.isSyncNetOrderInit = false;
					myApplication.exit();
				}

				@Override
				public void onCancle() {

				}
			});
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NETORDERITEM) {
			if (resultCode == RESULT_OK) {
				//                int netOrderItemSize = data.getIntExtra("netOrderItemSize", 0);
				//                setBadeView(netOrderItemSize);
			}
		} else if (requestCode == FETCHORDER) {
			if (resultCode == RESULT_OK) {
				Cart.notifyContentChange();
			}
		}
	}

	/**
	 * 跳转到Login界面
	 */
	private void jumpLogin() {
		ToolsUtils.writeUserOperationRecords("跳转到login界面");
		UserData mUserData = UserData.getInstance(context);
		mUserData.setUserName("");
		mUserData.setPwd("");
		mUserData.setSaveStated(false);
		errorReturnLogin();
	}

	/**
	 * 当出错时,退回到LoginAty重新登录
	 */
	private void errorReturnLogin() {
		myApplication.unbindCashBoxServer();
		myApplication.soundPool.stop(1);
		myApplication.clean();
		Intent orderIntent = new Intent(OrderDishMainAty.this, LoginAty.class);
		orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(orderIntent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		SunmiSecondScreen.getInstance(context).showImageListScreen();
		//关闭轮训获取网上订单的开关
		MyApplication.getInstance().setConFirmNetOrder(false);
		timerTaskController.cleanNetOrderMap();//清除网上订单map
		timerTaskController.cancleTimter(true);//关闭轮训timer
		if (usbPrinter != null) {
			usbPrinter.unbindService();
		}
		//反注册EventBus
		EventBus.getDefault().unregister(this);
		if (conn != null) {
			//解除LogService绑定
			unbindService(conn);
		}
		if (myWeightConn != null) {
			unbindService(myWeightConn);
		}
		if (secondaryScreendShow != null && secondaryScreendShow.isShowing()) {
			secondaryScreendShow.dismiss();
		}
		if (timer != null) {
			timer.cancel();
		}
		secondScreenDishList = null;
		DishDataController.cleanDishDate();
		DishDataController.cleanDishMarkMap();
		NetOrderController.cleanNetOrderData();
		PrinterDataController.cleanPrinterData();
		timerTaskController.closeWeight();
		stopService();
	}

	private UpLoadOrderService service;
	private ServiceConnection conn2 = new ServiceConnection() {


		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			isBound = true;
			UpLoadOrderService.MyBinder myBinder = (UpLoadOrderService.MyBinder) binder;
			service = myBinder.getService();
			Log.i("DemoLog", "ActivityA onServiceConnected");
			if (!MyApplication.isSyncNetOrderInit) {
				service.startTimer(0);
				MyApplication.isSyncNetOrderInit = true;
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			isBound = false;
			Log.i("DemoLog", "ActivityA onServiceDisconnected");
		}
	};
	public boolean isBound;

	private void initService() {
		Intent intent = new Intent(this, UpLoadOrderService.class);
		intent.putExtra("from", "ScreenProtectedActivity_new");
		Log.i("DemoLog", "----------------------------------------------------------------------");
		Log.i("DemoLog", "ScreenProtectedActivity_new 执行 bindService");
		bindService(intent, conn2, BIND_AUTO_CREATE);
	}

	private void stopService() {
		unbindService(conn2);
		//		stopService(new Intent(this, UpLoadOrderService.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		//        if (store.getReceiveNetOrder()) {
		//            timerTaskController.cancleTimter(false);
		//            MyApplication.getInstance().setConFirmNetOrder(true);
		//            timerTaskController.SyncNetOrder();
		//        }


	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//        if (Build.VERSION.SDK_INT > 9) {
		//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		//                    .permitAll().build();
		//            StrictMode.setThreadPolicy(policy);
		//        }
		setContentView(R.layout.aty_order_dish_main);
		myApplication.addPage(OrderDishMainAty.this);
		ButterKnife.bind(this);
		PrintManager.getInstance().setContext(context);
		MyApplication.getInstance().setContext(context);
		timerTaskController = TimerTaskController.getInstance();
		posSinUsbScreenController = PosSinUsbScreenController.getInstance();
		EventBus.getDefault().register(this);
		loadData();
		bindLogService();
		bindWeightService();
		getLogoPath();
		getMarketList();
		getWorkShiftDefinition();
		initService();
		fixedThreadPool = Executors.newCachedThreadPool();
		initResume();
	}

	private void initResume() {
		MyApplication.setContext(context);
		SunmiSecondScreen.getInstance(context);
		if (screenResponse == null) {
			getScreenConfiguration();
		} else {
			initSecondaryScreen(screenResponse);
		}
	}

	private void loadData() {
		intent = new Intent();
		weight = new Weight();//称重对象
		posInfo = PosInfo.getInstance();
		mUserData = UserData.getInstance(context);
		store = Store.getInstance(context);
		progressDialog = new ProgressDialogF(this);
		if (StoreInfor.terminalInfo != null) {
			TerminalInfo terminalInfo = StoreInfor.terminalInfo;
			String brandName = TextUtils.isEmpty(terminalInfo.brandName) ? ToolsUtils
					.returnXMLStr("wisdom_cash_register") : terminalInfo.brandName;
			String storeName = TextUtils.isEmpty(terminalInfo.sname) ? ToolsUtils
					.returnXMLStr("acewill_cloud_pos") : terminalInfo.sname;
			if (TextUtils.isEmpty(terminalInfo.sname)) {
				tvMainTitle.setText(brandName);
			}
			tvMainTitle.setText(brandName + "-" + storeName);
		}
		posInfo.setBrandName(tvMainTitle.getText().toString().trim());
		try {
			systemService = SystemService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
		}

		//        //初始化网络订单角标提示
		//        badeView = new BadgeView(this, tvNetorder);
		//        badeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		//        badeView.setTextColor(Color.WHITE);
		//        badeView.setBadgeBackgroundColor(Color.RED);
		//        badeView.setTextSize(12);
		//        badeView.hide();

		mainPopWindow = new MainPopWindow(context, PowerController.getSelectPopList());
		mainPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				backgroundAlpha(1f);
			}
		});

		if (StoreInfor.printConfiguration != null && StoreInfor.printConfiguration
				.getCheckoutReceiptCounts() >= 0) {
			checkOutPrintCounts = StoreInfor.printConfiguration.getCheckoutReceiptCounts();
			posInfo.setCheckoutReceiptCounts(checkOutPrintCounts);
		}
		if (StoreInfor.printConfiguration != null && StoreInfor.printConfiguration
				.getGuestReceiptCounts() >= 0) {
			guestReceiptCounts = StoreInfor.printConfiguration.getGuestReceiptCounts();
			posInfo.setGuestReceiptCounts(guestReceiptCounts);
		}
		if (StoreInfor.marketingActivities != null && StoreInfor.marketingActivities.size() > 0) {
			StoreInfor.marketingActivities.clear();
		}

		//		SyncPrinterAndKds();
	}

	//绑定日志Service
	private void bindLogService() {
		Intent intent = new Intent(OrderDishMainAty.this, LogService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 绑定称重service
	 */
	private void bindWeightService() {
		Intent intent = new Intent();
		intent.setAction("com.thyb.weight");
		intent.setPackage("com.thyb");
		bindService(intent, myWeightConn, BIND_AUTO_CREATE);
	}

	private void bindUsbPrint(boolean cashPrinterType) {
		if (cashPrinterType) {
			UsbPrinter.requestUsbPrinter(this);
			posSinUsbScreenController.init();
		} else {
			List<String> printerNameList = GpUsbPrinter.listGpUsbPrinterList(context);
			if (printerNameList.size() > 0) {
				System.out.println("===gp label printer found " + printerNameList.get(0));
				GpUsbPrinter usbPrinter = new GpUsbPrinter(context, printerNameList.get(0));
				usbPrinter.init();
				PrintManager.getInstance().setUsbPrinter(usbPrinter);
				PrinterDataController.setUsbPrinter(usbPrinter);
			} else {
				System.out.println("===gp label printer not found ");
			}
		}
	}


	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			logService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			//返回一个LogService对象
			logService = ((LogService.LogBinder) service).getService();
			if (logService != null && !TextUtils.isEmpty(PosInfo.getInstance().getServerUrl())) {
				myApplication.setLogService(logService);
			}
		}
	};

	private ServiceConnection myWeightConn = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			//            iWeight = IWeight.Stub.asInterface(service);
			//            //如果称重服务启动成功则可以开始检测称重重量
			//            if (iWeight != null) {
			//                timerTaskController.setWeightInfo(iWeight, weight);
			//            }
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub

		}

	};


	private final Timer timer = new Timer();//轮询打印机和KDS的连接情况
	private TimerTask task;
	private int delayedTime = 500;//延迟3秒
	private int cycleTime   = 3 * 1000;//周期循环时间

	private void SyncPrinterAndKds() {
		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PrinterDataController.getPrinterListState();
				PrinterDataController.getKdsListState();
			}
		};
		timer.schedule(task, delayedTime, cycleTime);
	}

	/**
	 * 设置添加屏幕的背景透明度
	 *
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha; //0.0-1.0
		getWindow().setAttributes(lp);
	}

	/**
	 * 获取商家LOGO图片
	 */
	private void getLogoPath() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getLogoPath(new ResultCallback() {
				@Override
				public void onResult(Object result) {
					String path = (String) result;
					if (!TextUtils.isEmpty(path)) {
						PosInfo posInfo = PosInfo.getInstance();
						posInfo.setLogoPath(path);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("get_logo_failure") + "!" + e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取营销活动
	 */
	private void getMarketList() {
		try {
			DishService dishService = DishService.getInstance();
			dishService.getMarketingActivityList(new ResultCallback<List<MarketingActivity>>() {
				@Override
				public void onResult(List<MarketingActivity> result) {
					if (result != null && result.size() > 0) {
						StoreInfor.marketingActivities = result;
					} else {
						Log.i("获取营销活动为空", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					e.printStackTrace();
					Log.i("获取营销活动失败", e.getMessage());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("获取营销活动失败", e.getMessage());
		}
	}

	/**
	 * 获取副屏配置
	 */
	private void getScreenConfiguration() {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.getScreenConfiguration(new ResultCallback<ScreenResponse>() {
				@Override
				public void onResult(ScreenResponse result) {
					if (result != null) {
						screenResponse = null;
						screenResponse = ToolsUtils.cloneTo(result);
						initSecondaryScreen(screenResponse);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("secondary_screen_shows_failure"));
					Log.i("获取门店配置副屏信息失败!", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			showToast(ToolsUtils.returnXMLStr("secondary_screen_shows_failure"));
			Log.i("获取门店配置副屏信息失败!", e.getMessage());
		}
	}

	/**
	 * 获取班次的接口
	 */
	private void getWorkShiftDefinition() {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.getWorkShiftDefinition(new ResultCallback<List<Definition>>() {
				@Override
				public void onResult(List<Definition> result) {
					if (result != null && result.size() > 0) {
						PosInfo posInfo = PosInfo.getInstance();
						posInfo.setDefinitionList(result);
						crateExchangeWork(result);
						checkWorkShift(mUserData.getUserName());
						System.out.println(mUserData.getUserName() + " ===username");
					} else {
						errorReturnLogin();
						showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
						Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					errorReturnLogin();
					showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
					Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			errorReturnLogin();
			showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
			Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
		}
	}

	private Dialog         dialog;
	private TextView       title;
	private ScrolGridView  gv_definition;
	private LinearLayout   lin_select_work;
	private CommonEditText ed_standby_money;
	private TextView       negativeButton, positiveButton;
	private        int selectDefinitionIndex = 0;//选中的班次 默认为0 默认选中第一个
	private static int definitionId          = -1;//班次定义的id，
	private String        definitionName;
	private DefinitionAdp definitionAdp;

	/**
	 * 初始化开班dialog
	 *
	 * @return
	 */
	private Dialog crateExchangeWork(List<Definition> result) {
		dialog = DialogUtil
				.getDialogShow(context, R.layout.dialog_ordinary, 0.5f, 0.6f, false, false);
		dialog.setContentView(R.layout.dialog_work_shift);
		title = (TextView) dialog.findViewById(R.id.print_title);
		lin_select_work = (LinearLayout) dialog.findViewById(R.id.lin_select_work);
		gv_definition = (ScrolGridView) dialog.findViewById(R.id.gv_definition);
		LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
		ed_standby_money = (CommonEditText) dialog.findViewById(R.id.ed_standby_money);
		negativeButton = (TextView) dialog.findViewById(R.id.print_ok);
		positiveButton = (TextView) dialog.findViewById(R.id.print_cancle);
		dialog.setCancelable(false);
		definitionId = result.get(selectDefinitionIndex).getId();//设置默认的班次Id
		definitionName = result.get(selectDefinitionIndex).getName();//设置默认的班次名称
		definitionAdp = new DefinitionAdp(context);
		definitionAdp.setData(result);
		definitionAdp.setPosition(selectDefinitionIndex);
		gv_definition.setAdapter(definitionAdp);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭开班窗口");
				dialog.dismiss();
				errorReturnLogin();
				showToast(ToolsUtils.returnXMLStr("not_setting_not_use_pos"));
			}
		});

		/**
		 * 取消
		 */
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消开班");
				dialog.dismiss();
				errorReturnLogin();
				showToast(ToolsUtils.returnXMLStr("not_setting_not_use_pos"));
			}
		});
		return dialog;
	}

	/**
	 * 判断是否要交班
	 *
	 * @param userName
	 */
	private void checkWorkShift(String userName) {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			final PosInfo        posInfo              = PosInfo.getInstance();
			storeBusinessService.getOpenWorkShift(userName, posInfo
					.getTerminalId(), new ResultCallback<WorkShift>() {
				@Override
				public void onResult(WorkShift result) {
					if (result != null) {
						workshiftId = result.getDefinitionId();
						Integer workShiftId = Integer.valueOf(String.valueOf(result.getId()));
						Integer definitionId = Integer
								.valueOf(String.valueOf(result.getDefinitionId()));
						posInfo.setDefinition(getWorkShiftInfo(definitionId));
						posInfo.setWorkShiftId(workShiftId);
						posInfo.setWorkShiftName(result.getDefinitionName());
						showOrderDishMain();

						showMainActivity();
						Log.e("workshiftId", workShiftId + "");
					} else {
						if (dialog != null) {
							startWorkShift();
							dialog.show();
						} else {
							errorReturnLogin();
							showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
							Log.i("获取门店对应的班次信息失败", "");
						}
					}
				}

				@Override
				public void onError(PosServiceException e) {
					e.printStackTrace();
					errorReturnLogin();
					showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
					Log.i("获取门店对应的班次信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			errorReturnLogin();
			showToast(ToolsUtils.returnXMLStr("not_workshift_not_use_pos"));
			Log.i("获取门店对应的班次信息失败", e.getMessage());
		}
	}

	/**
	 * 是否开班的按钮事件监听
	 */
	private void startWorkShift() {
		lin_select_work.setVisibility(View.VISIBLE);
		title.setText(ToolsUtils.returnXMLStr("open_classes"));

		gv_definition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectDefinitionIndex = position;
				Definition definition = (Definition) definitionAdp.getItem(position);
				definitionAdp.setPosition(position);
				if (definition != null) {
					definitionId = definition.getId();
					definitionName = definition.getName();
				}
			}
		});
		/**
		 * 确定
		 */
		negativeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ToolsUtils.writeUserOperationRecords("确定开班按钮");
				dialog.dismiss();
				String standbyMoney = ed_standby_money.getText().toString().trim();
				if (TextUtils.isEmpty(standbyMoney)) {
					standbyMoney = "0.0";
				}
				try {
					UserData             mUserData            = UserData.getInstance(context);
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					final PosInfo        posinfo              = PosInfo.getInstance();
					WorkShift            workShift            = new WorkShift();
					workShift.setDefinitionId(definitionId);
					workShift.setDefinitionName(definitionName);
					workShift.setUserId(1);
					workShift.setTerminalName(posinfo.getTerminalName());
					workShift.setUserName(mUserData.getUserName());
					workShift.setSpareCash(new BigDecimal(standbyMoney));
					workShift.setStartTime(System.currentTimeMillis());
					workShift.setTerminalId(Integer.valueOf(posinfo.getTerminalId()));

					storeBusinessService.startWorkShift(workShift, new ResultCallback<WorkShift>() {
						@Override
						public void onResult(WorkShift result) {
							Integer workShiftId = Integer.valueOf(String.valueOf(result.getId()));
							Integer definitionId = Integer
									.valueOf(String.valueOf(result.getDefinitionId()));
							posinfo.setDefinition(getWorkShiftInfo(definitionId));
							posinfo.setWorkShiftId(workShiftId);
							posInfo.setWorkShiftName(result.getDefinitionName());

							showMainActivity();
						}

						@Override
						public void onError(PosServiceException e) {
							getWorkShiftDefinition();
							myApplication.ShowToast(ToolsUtils
									.returnXMLStr("open_classes_error_try_again") + e.getMessage());
							Log.i("开班失败", e.getMessage());
						}
					});
				} catch (PosServiceException e) {
					e.printStackTrace();
					Log.i("开班失败", e.getMessage());
				}
			}
		});
	}

	/**
	 * 得到当前班次信息对象
	 *
	 * @param definitionId
	 * @return
	 */
	private Definition getWorkShiftInfo(Integer definitionId) {
		PosInfo          posInfo        = PosInfo.getInstance();
		List<Definition> definitionList = posInfo.getDefinitionList();
		for (Definition definition : definitionList) {
			if (definition.getId() == definitionId) {
				return definition;
			}
		}
		return null;
	}

	/**
	 * 显示点菜界面
	 */
	private void showOrderDishMain() {
		UserData mUserData = UserData.getInstance(context);
		mUserData.setWorkShifts(true);
	}

	/**
	 * 切换fragment
	 *
	 * @param index
	 */
	private void switchFragment(int index) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (tempFragment != null) {
			ft.hide(tempFragment);
		}
		switch (index) {
			case 0:
				if (fastFoodFragment == null) {
					fastFoodFragment = new FastFoodFragment();
					ft.add(R.id.rel_tab, fastFoodFragment);
				} else {
					if (!fastFoodFragment.isAdded()) {
						ft.add(R.id.rel_tab, fastFoodFragment);
					} else {
						ft.show(fastFoodFragment);
					}
				}
				tempFragment = fastFoodFragment;
				ft.commit();
				break;
		}
	}

	/**
	 * 获取菜品分类数据
	 */
	private void getKindInfo() {
		progressDialog.showLoading("");
		DishService dishService = null;
		try {
			dishService = DishService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
			return;
		}
		dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
			@Override
			public void onResult(List<DishType> result) {
				progressDialog.disLoading();
				if (result != null && result.size() > 0) {
					DishDataController.dishKindList = result;
					getDishInfo();
				} else {
					showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
					Log.i("获取菜品分类为空", "");
				}
			}

			@Override
			public void onError(PosServiceException e) {
				showToast(ToolsUtils.returnXMLStr("get_dish_kind_error") + "," + e.getMessage());
				Log.i("获取菜品分类为空", e.getMessage());
			}
		});
	}

	/**
	 * 得到菜品数据  dishList
	 */
	private void getDishInfo() {
		DishService dishService = null;
		try {
			dishService = DishService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
			return;
		}
		dishService.getDishList(new ResultCallback<List<Menu>>() {
			@Override
			public void onResult(List<Menu> result) {
				progressDialog.disLoading();
				if (result != null && result.size() > 0) {
					DishDataController.setDishData(result);
				}
			}

			@Override
			public void onError(PosServiceException e) {
				showToast(e.getMessage());
				Log.i("获取菜品为空", e.getMessage());
				progressDialog.disLoading();
			}
		});
	}

	/**
	 * 获取门店营销活动列表
	 */
	private void getStoreMarket() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getStoreMarket(new ResultCallback<List<Market>>() {
				@Override
				public void onResult(List<Market> result) {
					if (result != null) {
						MarketDataController.setMarketList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("营销活动获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取门店打印机列表
	 */
	private void getPrinterList() {
		try {
			PrinterDataController.cleanPrinterData();
			SystemService systemService = SystemService.getInstance();
			systemService.getPrinterList(new ResultCallback<List<Printer>>() {
				@Override
				public void onResult(List<Printer> result) {
					if (result != null) {
						Integer cashPrinterId      = store.getCashPrinterId();
						Integer secondartPrinterId = store.getSecondaryPrinterId();
						Integer takeoutPrinterId   = store.getTakeoutPrinterId();
						PrinterDataController
								.setPrinterList(result, cashPrinterId, secondartPrinterId, takeoutPrinterId);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("打印机列表获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取KDS列表
	 */
	private void getKDSList() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getKDSList(new ResultCallback<List<KDS>>() {
				@Override
				public void onResult(List<KDS> result) {
					if (result != null) {
						PrinterDataController.setKdsList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("打印机列表获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取门店档口列表信息
	 */
	private void getKitchenStalls() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getKitchenStalls(new ResultCallback<List<KitchenStall>>() {
				@Override
				public void onResult(List<KitchenStall> result) {
					if (result != null) {
						Log.i("打印机档口列表", ToolsUtils.getPrinterSth(result));
						PrinterDataController.setKitchenStallList(result);
						//映射菜品与菜品档口数据
						PrinterDataController.handleKitchenStall();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("打印机档口列表获取失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取全部打印模板
	 */
	private void getAllTemplates() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getAllTemplates(new ResultCallback<List<PrinterTemplates>>() {
				@Override
				public void onResult(List<PrinterTemplates> result) {
					if (result != null && result.size() > 0) {
						PrinterDataController.setPrinterTemplatesList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("获取打印方案失败失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取全单的折扣信息列表
	 */
	private void getOrderDiscountTypes() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.getOrderDiscountTypes(new ResultCallback<List<Discount>>() {
				@Override
				public void onResult(List<Discount> result) {
					if (result != null && result.size() > 0) {
						MarketDataController.setDisCountList(result);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("获取全单的折扣信息列表失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	private void showMainActivity() {
		//获取菜品并显示第一页
		getKindInfo();
		// 默认显示第一页
		switchFragment(0);
		getStoreMarket();
		getPrinterList();
		getKDSList();
		getKitchenStalls();
		getAllTemplates();
		getOrderDiscountTypes();
	}

	/**
	 * 获取班次交接报表数据
	 *
	 * @param workShiftId 班次Id
	 */
	private void workShiftReport(Integer workShiftId, String endWorkAmount, final boolean isJumpLogin) {
		try {
			SystemService systemService = SystemService.getInstance();
			if (TextUtils.isEmpty(endWorkAmount)) {
				endWorkAmount = "0";
			}
			systemService
					.workShiftReport(workShiftId, endWorkAmount, new ResultCallback<WorkShiftNewReport>() {
						@Override
						public void onResult(final WorkShiftNewReport result) {
							Log.i("获取交接班报表数据==>>", ToolsUtils.getPrinterSth(result));
							printWorkShift(result, isJumpLogin);
						}

						@Override
						public void onError(PosServiceException e) {
							if (isJumpLogin) {
								posInfo.getInstance().setWorkShiftId(null);
							}
							showToast(ToolsUtils
									.returnXMLStr("get_workshift_report_data_error") + "," + e
									.getMessage());
							Log.i("获取交接班报表数据失败", e.getMessage());
						}
					});
		} catch (PosServiceException e) {
			e.printStackTrace();
			if (isJumpLogin) {
				posInfo.getInstance().setWorkShiftId(null);
			}
			Log.i("获取交接班报表数据失败", e.getMessage());
		}
	}

	/**
	 * 交接班打印
	 *
	 * @param result
	 */
	private WorkShiftNewReport workShiftReport;

	/**
	 * 打印交接班(日结)记录
	 *
	 * @param result
	 * @param isJumpLogin
	 */
	private void printWorkShift(final WorkShiftNewReport result, final boolean isJumpLogin) {
		if (result != null) {
			if (isJumpLogin) {
				workShiftReport = result;
				posInfo.getInstance().setWorkShiftId(null);
			}
			if (PrinterDataController.getInstance().getReceiptPrinter() == null) {
				PrinterDataController.getInstance()
						.printWorkShift(result, PowerController.SHIFT_WORK);
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						PrinterDataController.getInstance()
								.printWorkShift(result, PowerController.SHIFT_WORK);
					}
				}).start();//启动打印线程
			}
			if (isJumpLogin) {
				jumpLogin();
			}
		}
	}

	/**
	 * 打印日结小票
	 *
	 * @param result
	 */
	WorkShiftReport dailyReport;

	private void printDailyReport(final WorkShiftReport result) {
		if (result != null) {
			dailyReport = result;
			showToast("正在打印日结信息,请稍等...");
			if (PrinterDataController.getInstance().getReceiptPrinter() == null) {
				PrinterDataController.getInstance().printWorkShift(result, PowerController.DAILY);
			} else {
				new Thread(new Runnable() {
					@Override
					public void run() {
						PrinterDataController.getInstance()
								.printWorkShift(result, PowerController.DAILY);
					}
				}).start();//启动打印线程
			}
			jumpLogin();
		}
	}

	/**
	 * 确认打印日结报表
	 */
	private void confirmDailyPrintReport(WorkShiftReport dailyReport) {
		endDailyBusiness(dailyReport);
	}

	/**
	 * 日结
	 */
	private void daySettlement() {
		dailyReport();
	}

	/**
	 * 获取门店日结信息
	 */
	private void dailyReport() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.dailyReport(new ResultCallback<WorkShiftReport>() {
				@Override
				public void onResult(final WorkShiftReport result) {
					Log.i("获取门店日结信息", ToolsUtils.getPrinterSth(result));
					if (result != null) {
						Intent intent = new Intent();
						intent.setClass(OrderDishMainAty.this, ShowReportAty.class);
						intent.putExtra("WorkShiftReport", (Serializable) result);
						intent.putExtra("printType", PowerController.DAILY);
						startActivity(intent);
						//printDailyReport(result);
					} else {
						showToast(ToolsUtils.returnXMLStr("get_daily_data_error"));
						Log.i("获取门店日结信息为空", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("get_daily_data_error") + "!" + e
							.getMessage());
					Log.i("获取门店日结信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 日结打印
	 *
	 * @param workShiftReport
	 */
	private void endDailyBusiness(final WorkShiftReport workShiftReport) {
		try {
			OrderService orderService = OrderService.getInstance();
			orderService.endDailyBusiness(new ResultCallback<PosResponse>() {
				@Override
				public void onResult(PosResponse result) {
					if (result != null) {
						showToast(ToolsUtils.returnXMLStr("daily_success"));
						printDailyReport(workShiftReport);
						//                        dailyReport();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast(ToolsUtils.returnXMLStr("daily_failure") + "," + e.getMessage());
					Log.i("日结失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			showToast(ToolsUtils.returnXMLStr("daily_failure") + "," + e.getMessage());
			Log.i("日结失败", e.getMessage());
		}
	}

	/**
	 * 检查更新
	 */
	private void checkUpdate() {
		final ProgressDialogF progressDialogF = new ProgressDialogF(context);
		progressDialogF.showLoading("");
		systemService.getTerminalVersions(new ResultCallback<TerminalVersion>() {
			@Override
			public void onResult(TerminalVersion result) {
				progressDialogF.disLoading();
				if (result != null) {
					if (Integer.valueOf(result.getVersion()) > MyApplication.getInstance()
							.getVersionCode()) {
						//下载文件
						DownUtlis.getInstance(context)
								.upDataDialog(result.getDescription(), result.getFilename());
					} else {
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("is_the_latest_version"));
					}
				} else {
					MyApplication.getInstance()
							.ShowToast(ToolsUtils.returnXMLStr("get_version_info_failure"));
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialogF.disLoading();
				showToast(ToolsUtils.returnXMLStr("get_version_info_failure") + "!" + e
						.getMessage());
				Log.i("获取版本信息失败", e.getMessage());
			}
		});
	}

	/**
	 * 解绑门店
	 */
	private void unBindStore() {
		DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("unbind"), ToolsUtils
				.returnXMLStr("are_you_sure_unbind"), new DialogCallback() {
			@Override
			public void onConfirm() {
				String userName    = mUserData.getUserName();
				String userPwd     = mUserData.getPwd();
				String terminalMac = posInfo.getTerminalMac();
				try {
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					storeBusinessService
							.unbindStore(userName, userPwd, terminalMac, new ResultCallback<Integer>() {
								@Override
								public void onResult(Integer result) {
									if (result == 0) {
										showToast(ToolsUtils.returnXMLStr("unbind_success"));
										store.setDeviceName("");
										StoreInfor.terminalInfo = null;
										jumpLogin();
									}
								}

								@Override
								public void onError(PosServiceException e) {
									showToast(ToolsUtils.returnXMLStr("unbind_failure") + e
											.getMessage());
									Log.i("解绑门店信息失败", e.getMessage());
								}
							});
				} catch (PosServiceException e) {
					e.printStackTrace();
					showToast(ToolsUtils.returnXMLStr("unbind_failure") + e.getMessage());
					Log.i("解绑门店信息失败", e.getMessage());
				}
			}

			@Override
			public void onCancle() {

			}
		});

	}

	private Dialog         dialogEnd;
	private TextView       titleEnd;
	private TextView       tv_standby_money;
	private CommonEditText ed_standby_moneyEnd;
	private LinearLayout   ed_lin_select_work;
	private TextView       negativeButtonEnd, positiveButtonEnd;

	/**
	 * 初始化交班dialog
	 *
	 * @return
	 */
	private Dialog createEndWorkShift() {
		dialogEnd = DialogUtil.getDialog(context, R.layout.dialog_work_shift, 0.5f, 0.3f);
		titleEnd = (TextView) dialogEnd.findViewById(R.id.print_title);
		ed_standby_moneyEnd = (CommonEditText) dialogEnd.findViewById(R.id.ed_standby_money);
		tv_standby_money = (TextView) dialogEnd.findViewById(R.id.tv_standby_money);
		negativeButtonEnd = (TextView) dialogEnd.findViewById(R.id.print_ok);
		positiveButtonEnd = (TextView) dialogEnd.findViewById(R.id.print_cancle);
		LinearLayout print_close_ll = (LinearLayout) dialogEnd
				.findViewById(R.id.print_close_ll);
		LinearLayout ed_lin_select_work = (LinearLayout) dialogEnd
				.findViewById(R.id.lin_select_work);
		ed_lin_select_work.setVisibility(View.GONE);
		print_close_ll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("关闭交班窗口");
				dialogEnd.dismiss();
			}
		});

		/**
		 * 取消
		 */
		positiveButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("取消交班窗口");
				dialogEnd.dismiss();
			}
		});
		return dialogEnd;
	}

	/**
	 * 交班
	 */
	private void endWorkShift() {
		titleEnd.setText(ToolsUtils.returnXMLStr("shift"));
		lin_select_work.setVisibility(View.GONE);
		ed_standby_moneyEnd.setHint(ToolsUtils.returnXMLStr("please_input_cash_box_money"));
		tv_standby_money.setText(ToolsUtils.returnXMLStr("cash_box_money"));

		/**
		 * 确定
		 */
		negativeButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords(ToolsUtils.returnXMLStr("confirm_shift_menu"));
				dialogEnd.dismiss();
				String standbyMoney = ed_standby_moneyEnd.getText().toString().trim();
				if (TextUtils.isEmpty(standbyMoney)) {
					standbyMoney = "0.0";
				}
				try {
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					WorkShift            workShift            = new WorkShift();
					workShift.setCashRevenue(new BigDecimal(standbyMoney));
					workShift.setEndTime(System.currentTimeMillis());
					PosInfo      posInfo           = PosInfo.getInstance();
					final String finalStandbyMoney = standbyMoney;
					storeBusinessService.endWorkShift(new Long(posInfo
							.getWorkShiftId()), workShift, new ResultCallback<Integer>() {
						@Override
						public void onResult(Integer result) {
							showToast(ToolsUtils.returnXMLStr("shift_success"));
							PosInfo posInfo = PosInfo.getInstance();
							workShiftReport(posInfo.getWorkShiftId(), finalStandbyMoney, true);
						}

						@Override
						public void onError(PosServiceException e) {
							myApplication.ShowToast(ToolsUtils
									.returnXMLStr("shift_failure_try_again") + e.getMessage());
							Log.i("交班失败", e.getMessage());
						}
					});
				} catch (PosServiceException e) {
					e.printStackTrace();
					Log.i("交班失败", e.getMessage());
				}
			}
		});
	}

	/**
	 * 交班
	 */
	private void workShift() {
		if (dialogEnd == null) {
			createEndWorkShift();
		}
		PrinterDataController.getInstance().openCashBox();
		endWorkShift();
		dialogEnd.show();
	}

	/**
	 * 判断是否具有打开钱箱的权限
	 */
	private void openMoneyBox() {
		//判断是否有退菜权限
		PowerController
				.isLogicPower(context, PowerController.OPEN_POS_BOX, new PermissionCallback() {
					@Override
					public void havePermission() {
						recordOpenCashBoxOperation();
						PrinterDataController.getInstance().openCashBox();
					}

					@Override
					public void withOutPermission() {

					}
				});
	}

	/**
	 * 记录打开钱箱操作
	 */
	private void recordOpenCashBoxOperation() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.openCashboxHistory(new ResultCallback() {
				@Override
				public void onResult(Object result) {
					if ((int) result == 0) {
						Log.i(TAG, "记录开启钱箱操作成功");
					} else {
						Log.i(TAG, "记录开启钱箱操作失败");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i(TAG, "记录开启钱箱操作失败," + e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			Log.i(TAG, "记录开启钱箱操作失败," + e.getMessage());
		}
	}


	/**
	 * 注销
	 */
	private void logOut() {
		ToolsUtils.writeUserOperationRecords("注销");
		DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("sth_home_logout"), posInfo
				.getRealname() + "," + ToolsUtils
				.returnXMLStr("whether_you_want_to_exit"), new DialogCallback() {
			@Override
			public void onConfirm() {
				jumpLogin();
				finish();
			}

			@Override
			public void onCancle() {

			}
		});
	}

	/**
	 * evenbus回调处理
	 *
	 * @param event
	 */
	private AlertDialog alertDialog;//Token错误的时候弹出的dialog


	//初始化副屏幕
	private void initSecondaryScreen(ScreenResponse screenResponse) {
		DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
		if (dm != null) {
			Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
			//            myApplication.ShowToast("secondary screen found: " + displays.length);
			for (Display display : displays) {
				isSunMiScreen = false;
				if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
					secondaryScreendShow = new SecondaryScreenShow(getApplicationContext(), display, screenResponse);
					secondaryScreendShow.getWindow()
							.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					secondaryScreendShow.show();

					//                    if (secondaryScreendShow != null) {
					//                        if(secondScreenDishList != null && secondScreenDishList.size() >0)
					//                        {
					//                            secondaryScreendShow.setDishItemInfo(secondScreenDishList, price, dishItemCount);
					//                        }
					//                    }
				}
			}
			if (isSunMiScreen) {
				if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
					SunmiSecondScreen.getInstance(context).setSunMiScreenResponses(screenResponse);
					SunmiSecondScreen.showImageListScreen();
				}
			}

		}
	}

	private ExecutorService fixedThreadPool;
	List<Dish> secondScreenDishList = new ArrayList<>();
	BigDecimal price                = new BigDecimal("0.00");
	int        dishItemCount        = 0;

	@Subscribe
	public void PosEventCallBack(final PosEvent event) {
		switch (event.getAction()) {
			//确认收银打印机是否是possin品牌 true是  false不是
			case Constant.EventState.CASH_PRINTER_USB:
				bindUsbPrint(event.isCashPrinterType());
				break;
			//刷新订单号
			case Constant.EventState.REFRESH_ORDERID:
				if (fastFoodFragment != null) {
					fastFoodFragment.getOrderId();
				}
				break;
			//菜品列表发生变化要在副屏上展示的时候
			case Constant.EventState.DISH_ITEM_CHANGE:
				if (secondaryScreendShow != null) {
					secondScreenDishList = event.getMarketActList();
					price = event.getPrice();
					dishItemCount = event.getDishItemCount();
					secondaryScreendShow
							.setDishItemInfo(secondScreenDishList, event.getPrice(), event
									.getDishItemCount());
				}
				break;
			//会员信息发现变化要在副屏上展示的时候
			case Constant.EventState.MEMBER_INFO_CHANGE:
				setMemberInfo();
				if (secondaryScreendShow != null && secondaryScreendShow.isShowing()) {
					secondaryScreendShow.setMemberInfo();
				}
				break;
			case Constant.JUMP_LOGIN:
				jumpLogin();
				break;
			//发送数据到KDS 换台
			case Constant.EventState.SEND_INFO_KDS_CHANGE_TABLE:
				MainEvenBusController
						.kdsChangeOrderTable(event.getRefOrderId(), event.getTableName());
				break;
			//kds下单打印
			case Constant.EventState.SEND_INFO_KDS:
				MainEvenBusController.kdsCreatOrder(event.getOrder(), event.getTableName());
				break;
			//kds退单打印
			case Constant.EventState.SEND_INFO_KDS_REFUND_ORDER:
				MainEvenBusController.kdsDeleteOrder(event.getOrderId());
				break;
			//kds退菜打印
			case Constant.EventState.SEND_INFO_KDS_REFUND_DISH:
				MainEvenBusController.kdsDeleteDish(event.getOiList(), event.getOrderId());
				break;
			//测试打印机打印情况
			case Constant.TestPrinterState.TEST_PRINT:
				MainEvenBusController.testPrint(event.getPrinter());
				break;
			//测试KDS打印情况
			case Constant.TestPrinterState.TEST_PRINT_KDS:
				MainEvenBusController.testPrintKds(event.getKds());
				break;
			//当前时段没有菜品档案
			case Constant.EventState.CURRENT_TIME_DISH_NULL:
				showToast(ToolsUtils.returnXMLStr("current_dish_get_is_null"));
				break;
			//通过交接班历史打印交接班报表
			case Constant.EventState.PRINT_WORKE_SHIFT_HISTORY:
				workShiftReport(event.getPosition(), "", false);
				break;
			//下单打印标识
			case Constant.EventState.PRINTER_ORDER:
				ToolsUtils.writeUserOperationRecords(Thread.currentThread()
						.getName() + ">TIME===>下单打印客用单==" + TimeUtil
						.getStringTimeLong(System.currentTimeMillis()));
				DishOptionController.cleanCartDishMap();//下单后清除菜品的定制项缓存
				posInfo.setTableNumber("");//下单成功后将保存的餐牌信息清空
				posInfo.setCustomerAmount(1);//下单成功后将默认的顾客人数设置为1
				if (fastFoodFragment != null) {
					fastFoodFragment.getOrderId();
					fastFoodFragment.setCardNumber();
					fastFoodFragment.setPeopleNumber();
				}
				MainEvenBusController.printGuestOrder(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				posInfo.setAddWaiMaiMoney(false);//订单添加过外卖配送费逻辑字段
				posInfo.setCustomer(null);//下单成功后将设置的外卖顾客信息设置为空
				posInfo.setAccountMember(null);//下单成功后将保存的微生活会员对象清空,以免影响下一次客户点餐
				posInfo.setComment("");//下单成功后将保存的订单备注信息清空
				posInfo.setBizId("");
				posInfo.setWshDealPreview(null);
				break;
			//打印结账单
			case Constant.EventState.PRINT_CHECKOUT:
				ToolsUtils.writeUserOperationRecords(Thread.currentThread()
						.getName() + ">TIME===>下单打印结账单==" + TimeUtil
						.getStringTimeLong(System.currentTimeMillis()));
				fixedThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						MainEvenBusController.printCheckOutOrder(store, event.getOrder(), event
								.getPosition(), checkOutPrintCounts);
					}
				});
				break;
			//打印厨房小票
			case Constant.EventState.PRINTER_KITCHEN_ORDER:
				ToolsUtils.writeUserOperationRecords(Thread.currentThread()
						.getName() + ">TIME===>下单打印厨房小票==" + TimeUtil
						.getStringTimeLong(System.currentTimeMillis()));
				fixedThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						MainEvenBusController.printKitChenOrder(event.getOrder());
					}
				});
				break;
			//催菜(补打)厨房
			case Constant.EventState.PRINTER_RUSH_DISH:
				MainEvenBusController.printKitChenRushDishOrder(event.getOrder());
				break;
			//厨房退菜打印
			case Constant.EventState.PRINTER_RETREAT_DISH:
				MainEvenBusController.printKitChenRetreatDishOrder(event.getOrder());
				break;
			//客用退单厨房打印
			case Constant.EventState.PRINTER_RETREAT_KITCHEN_ORDER:
				MainEvenBusController.printGuestKitchenRetreatOrder(event.getOrder());
				break;
			//客票退菜打印
			case Constant.EventState.PRINTER_RETREAT_DISH_GUEST:
				MainEvenBusController.printGuestRetreatDish(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				break;
			//客用退单打印
			case Constant.EventState.PRINTER_RETREAT_ORDER:
				MainEvenBusController.printGuestRetreatOrder(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				break;
			//补打下单标识
			case Constant.EventState.PRINTER_EXTRA_RECEIPT:
				MainEvenBusController.printExtraReceipt(store, event
						.getOrder(), guestReceiptCounts, checkOutPrintCounts);
				break;
			//请求超时
			case Constant.EventState.SERVER_REQUEST_TIMEOUT:
				showToast("请求超时!");
				break;
			//跳转到桌台界面的标识
			case Constant.EventState.SELECT_FRAGMTNT_TABLE:
				if (tableFragment != null) {
				}
				break;
			//跳转到点菜界面的标识
			case Constant.EventState.SELECT_FRAGMTNT_ORDER:
				//                Order tableOrder = event.getOrder();
				//                enterTable(tableOrder.getTableId(), tableOrder);
				break;
			//Token过期的标识
			case Constant.EventState.TOKEN_TIME_OUT:
				Looper.prepare();
				if (alertDialog == null) {
					alertDialog = DialogUtil
							.LoginErrorDialog(context, ToolsUtils.returnXMLStr("wrong"), ToolsUtils
									.returnXMLStr("token_abnormal_is_re_register"), new DialogCallback() {
								@Override
								public void onConfirm() {
									errorReturnLogin();
									alertDialog = null;
								}

								@Override
								public void onCancle() {
									errorReturnLogin();
									alertDialog = null;
								}
							});
				}
				Looper.loop();
				break;
			case Constant.EventState.ERR_PRINT_CASH:
				String errMessage = event.getErrMessage();
				final int cashActionId = event.getCashActionId();
				final Order order = event.getOrder();
				final int position = event.getPosition();
				Looper.prepare();
				if (alertDialog == null) {
					alertDialog = DialogUtil.LoginErrorDialog(context, errMessage, ToolsUtils
							.returnXMLStr("after_pressing_the_error"), new DialogCallback() {
						@Override
						public void onConfirm() {
							alertDialog = null;
							//交班打印出错
							if (cashActionId == PowerController.SHIFT_WORK && workShiftReport != null) {
								printWorkShift(workShiftReport, true);
							}
							//日结打印出错
							else if (cashActionId == PowerController.DAILY && dailyReport != null) {
								printDailyReport(dailyReport);
							}
							//客用打印机打印出错
							else if (cashActionId == Constant.EventState.PRINTER_ORDER) {
								MainEvenBusController
										.printGuestOrder(store, order, guestReceiptCounts, checkOutPrintCounts);
							}
							//预结小票或者是结账小票
							else if (cashActionId == Constant.EventState.ORDER_TYPE_ADVANCE || cashActionId == Constant.EventState.PRINT_CHECKOUT) {
								MainEvenBusController
										.printCheckOutOrder(store, order, position, checkOutPrintCounts);
							}
							//客票退菜打印
							else if (cashActionId == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
								MainEvenBusController
										.printGuestRetreatDish(store, order, guestReceiptCounts, checkOutPrintCounts);
							}
							//客票退单打印
							else if (cashActionId == Constant.EventState.PRINTER_RETREAT_ORDER) {
								MainEvenBusController
										.printGuestRetreatOrder(store, order, guestReceiptCounts, checkOutPrintCounts);
							}
						}

						@Override
						public void onCancle() {
							alertDialog = null;
						}
					});
				}
				Looper.loop();
				break;
			//打印订单小票出错
			case Constant.EventState.ERR_PRINT_ORDER:
				Intent intentwel = new Intent(this, ErrPrinteAty.class);
				intentwel.putExtra("source", Constant.EventState.ERR_PRINT_ORDER);
				Bundle errBundle = new Bundle();
				errBundle.putSerializable("order", event.getOrder());
				errBundle.putSerializable("printer", event.getPrinter());
				intentwel.putExtras(errBundle);
				startActivity(intentwel);
				break;
			//打印厨房小票出错
			case Constant.EventState.ERR_PRINT_KITCHEN_ORDER:
				Intent intentKit = new Intent(this, ErrPrinteAty.class);
				intentKit.putExtra("source", Constant.EventState.ERR_PRINT_KITCHEN_ORDER);
				intentKit.putExtra("errStr", event.getTableName());
				//                Bundle errKitBundle = new Bundle();
				//                errKitBundle.putSerializable("order", event.getOrder());
				//                errKitBundle.putSerializable("oi", event.getOi());
				//                errKitBundle.putSerializable("dishPackage", event.getDishPackageItem());
				//                errKitBundle.putSerializable("printer", event.getPrinter());
				//                intentKit.putExtras(errKitBundle);
				startActivity(intentKit);
				break;
			//打印厨房总单小票出错
			case Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER:
				Intent intentKitAll = new Intent(this, ErrPrinteAty.class);
				intentKitAll
						.putExtra("source", Constant.EventState.ERR_PRINT_KITCHEN_SUMMARY_ORDER);
				intentKitAll.putParcelableArrayListExtra("oiList", (ArrayList) event.getOiList());
				Bundle errKitAllBundle = new Bundle();
				errKitAllBundle.putSerializable("order", event.getOrder());
				errKitAllBundle.putSerializable("printer", event.getPrinter());
				intentKitAll.putExtras(errKitAllBundle);
				startActivity(intentKitAll);
				break;
			//获取到网络订单
			case Constant.EventState.PUT_NET_ORDER:
				//                int size = NetOrderController.getNetOrderList().size();
				//                setBadeView(size);
				break;
			//确认打印日结报表
			case Constant.EventState.PRINT_CONFIRM_DALIY:
				WorkShiftReport workShiftReport = event.getWorkShiftReport();
				if (workShiftReport != null) {
					confirmDailyPrintReport(workShiftReport);
				}
				break;
			case Constant.EventState.SELECT_MAIN_DROP_LIST:
				//消息
				if (event.getPosition() == PowerController.MESSAGE) {
					ToolsUtils.writeUserOperationRecords("消息");
					startActivity(new Intent(this, MessAgeAty.class));
				}
				//菜品沽清
				else if (event.getPosition() == PowerController.SELL_OUT) {
					ToolsUtils.writeUserOperationRecords("菜品沽清");
					Intent intentUp = new Intent();
					intentUp.setClass(OrderDishMainAty.this, DishCountSettAty.class);
					//                    intentUp.setClass(OrderDishMainAty.this, DishCountNewAty.class);
					startActivity(intentUp);
				}
				//版本更新
				else if (event.getPosition() == PowerController.UPDATE) {
					ToolsUtils.writeUserOperationRecords("版本更新");
					checkUpdate();
				}
				//修改密码
				else if (event.getPosition() == PowerController.MODIFY_PW) {
					ToolsUtils.writeUserOperationRecords("修改密码");
					startActivity(new Intent(this, ModifiPwAty.class));
				} else if (event.getPosition() == PowerController.SUPPORT_CALL_GOODS) {
					ToolsUtils.writeUserOperationRecords("门店订货");
					Intent intent = new Intent();
					intent.setClass(this, CallGoodsAty.class);
					startActivity(intent);
				}
				//挂账列表
				else if (event.getPosition() == PowerController.CARD_RECORDS) {
					ToolsUtils.writeUserOperationRecords("挂账列表");
					startActivity(new Intent(this, CardRecordsAty.class));
				}
				//会员
				else if (event.getPosition() == PowerController.MEMBER) {
					ToolsUtils.writeUserOperationRecords("会员");
					startActivity(new Intent(this, MemberAty.class));
				}
				//历史订单
				else if (event.getPosition() == PowerController.HISTORY_ORDER) {
					ToolsUtils.writeUserOperationRecords("历史订单");
					intent.setClass(OrderDishMainAty.this, OrderInfoHistoryAty.class);
					startActivity(intent);
				}
				//报表
				else if (event.getPosition() == PowerController.REPORT) {
					ToolsUtils.writeUserOperationRecords("报表");
					startActivity(new Intent(this, ReportAty.class));
				}
				//员工列表
				else if (event.getPosition() == PowerController.STAFF)//
				{
					ToolsUtils.writeUserOperationRecords("员工列表");
					startActivity(new Intent(this, StaffAty.class));
				}
				//日结
				else if (event.getPosition() == PowerController.DAILY)//
				{
					ToolsUtils.writeUserOperationRecords("日结");
					daySettlement();
				}
				//上传日志
				else if (event.getPosition() == PowerController.UPLOAD_LOG) {
					ToolsUtils.writeUserOperationRecords("上传日志");
					Intent intentUp = new Intent();
					intentUp.setClass(OrderDishMainAty.this, UpLoadActivity.class);
					startActivity(intentUp);
				}
				//备用金
				else if (event.getPosition() == PowerController.STANDBY_CASH) {
					ToolsUtils.writeUserOperationRecords("备用金");
					startActivity(new Intent(this, StandByCashAty.class));
				}
				//设置
				else if (event.getPosition() == PowerController.ADVANCED_SETUP) {
					ToolsUtils.writeUserOperationRecords("设置");
					startActivity(new Intent(this, ManageSetAty.class));
				} else if (event.getPosition() == PowerController.UNBIND_DEVICE) {
					ToolsUtils.writeUserOperationRecords("解绑");
					unBindStore();
				} else if (event.getPosition() == PowerController.SHIFT_WORK) {
					ToolsUtils.writeUserOperationRecords("交班");
					workShift();
				} else if (event.getPosition() == PowerController.SHIFT_WORK_HISTORY) {
					ToolsUtils.writeUserOperationRecords(ToolsUtils.returnXMLStr("hostory_shift"));
					startActivity(new Intent(this, WorkShiftHistoryAty.class));
				} else if (event.getPosition() == PowerController.ABOUT_CLOUDPOS) {
					ToolsUtils.writeUserOperationRecords("关于云POS");
					DialogUtil.aboutCloudPos(context);
				} else if (event.getPosition() == PowerController.NETORDER) {
					//                    ToolsUtils.writeUserOperationRecords("网上订单");
					//                    Intent netOrderIntent = new Intent(context, NetOrderNewAty.class);
					//                    startActivityForResult(netOrderIntent, NETORDERITEM);
				}
				break;
			//清空购物车
			case Constant.EventState.CLEAN_CART:
				PosInfo posInfo = PosInfo.getInstance();
				posInfo.setCustomer(null);
				Cart.cleanDishList();
				DishDataController.cleanDishMarkMap();
				if (fastFoodFragment != null) {
					fastFoodFragment.cleanData();
				}
				if (secondaryScreendShow != null && secondaryScreendShow.isShowing()) {
					secondaryScreendShow.setDishItemInfo(null, null, 0);
				}
				if (isSunMiScreen) {
					SunmiSecondScreen.showImageListScreen();
				}
				break;
			//服务器有连接
			case Constant.EventState.SERVER_STATUS_UP:
				if (fastFoodFragment != null) {
					fastFoodFragment.setWifiState(true);
				}
				break;
			//服务器无连接
			case Constant.EventState.SERVER_STATUS_DOWN:
				if (fastFoodFragment != null) {
					fastFoodFragment.setWifiState(false);
				}
				break;
			//生成订单ID失败的错误提示标签
			case Constant.EventState.ERR_CREATE_ORDERID_FILURE:
				Intent tipsOrdIdFailure = new Intent(this, ErrTipsAty.class);
				tipsOrdIdFailure.putExtra("source", Constant.EventState.ERR_CREATE_ORDERID_FILURE);
				startActivity(tipsOrdIdFailure);
				break;
			//获取线上支付状态失败提示标签
			case Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE:
				Intent tipsOnlinePay = new Intent(this, ErrTipsAty.class);
				tipsOnlinePay
						.putExtra("source", Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE);
				startActivity(tipsOnlinePay);
				break;
			//获取威富通支付状态失败提示标签
			case Constant.EventState.ERR_GET_WFT_PAY_STATE:
				Intent tipsWFTPay = new Intent(this, ErrTipsAty.class);
				tipsWFTPay.putExtra("source", Constant.EventState.ERR_GET_WFT_PAY_STATE);
				startActivity(tipsWFTPay);
				break;
			//获取支付状态失败提示标签
			case Constant.EventState.ERR_GET_PAY_STATE_FAILURE:
				Intent tipsPay = new Intent(this, ErrTipsAty.class);
				tipsPay.putExtra("source", Constant.EventState.ERR_GET_PAY_STATE_FAILURE);
				startActivity(tipsPay);
				break;
			//切换用户
			case Constant.EventState.LOGOUT:
				logOut();
				break;
		}
	}

	@OnClick({R.id.tv_order, R.id.tv_mainSelect, R.id.tv_menu, R.id.tv_checkBox, R.id.tv_showView,
			R.id.tv_search})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_mainSelect:
				ToolsUtils.writeUserOperationRecords("设置按钮");
				mainPopWindow.Show(relTop);
				backgroundAlpha(0.7f);
				break;
			case R.id.tv_order:
				ToolsUtils.writeUserOperationRecords("跳转订单列表界面");
				intent.setClass(OrderDishMainAty.this, OrderAmountAty.class);
				startActivityForResult(intent, NETORDERITEM);
				break;
			case R.id.tv_checkBox:
				ToolsUtils.writeUserOperationRecords("钱箱");
				openMoneyBox();
				break;
			case R.id.tv_menu:
				ToolsUtils.writeUserOperationRecords("菜单");
				startActivity(new Intent(OrderDishMainAty.this, DishMenuAty.class));
				break;
			case R.id.tv_showView:
				ToolsUtils.writeUserOperationRecords("切换菜品视图");
				if (fastFoodFragment != null) {
					if (isShowDishImg) {
						isShowDishImg = false;
					} else {
						isShowDishImg = true;
					}
					fastFoodFragment.switchSHowView(isShowDishImg);
				}
				break;
			case R.id.tv_search:
				ToolsUtils.writeUserOperationRecords("搜索");
				intent.setClass(context, SearchAty.class);
				startActivity(intent);
				break;
		}
	}

	public void setMemberInfo() {
		if (fastFoodFragment != null) {
			fastFoodFragment.setMemberInfo();
		}
	}

}
