package cn.acewill.pos.next.ui.activity.newPos;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.RetreatDishController;
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
import cn.acewill.pos.next.model.WorkShiftReport;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.MarketingActivity;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.printer.PrinterTemplates;
import cn.acewill.pos.next.printer.usb.GpUsbPrinter;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.LogService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.PrintManager;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.ui.activity.OrderInfoAty;
import cn.acewill.pos.next.ui.activity.SearchAty;
import cn.acewill.pos.next.ui.adapter.DefinitionAdp;
import cn.acewill.pos.next.ui.fragment.FastFoodFragment;
import cn.acewill.pos.next.ui.fragment.TableFragment2;
import cn.acewill.pos.next.ui.presentation.SecondaryPresentation;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DownUtlis;
import cn.acewill.pos.next.utils.GpEnternetPrintTwo;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.MainPopWindow;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ScrolGridView;

import static cn.acewill.pos.next.common.MarketDataController.cleanStoreMarket;

/**
 * 新Table主界面
 * Created by DHH on 2016/11/25.
 */

public class TableMainAty extends BaseActivity {
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

	private String TAG = "TableMainAty";
	private PosInfo         posInfo;
	private Store           store;
	private UserData        mUserData;
	private Integer         workshiftId;
	private LogService      logService;
	private SystemService   systemService;
	private Intent          intent;
	//    private BadgeView badeView;
	private ProgressDialogF progressDialog;

	private SecondaryPresentation secondaryPresentation = null;//副屏上切换的轮播图

	private Fragment tempFragment = null;
	private TableFragment2   tableFragment;
	private FastFoodFragment fastFoodFragment;

	private MainPopWindow mainPopWindow;

	private int checkOutPrintCounts = 1;//结账单打印张数
	private int guestReceiptCounts  = 1;//客用小票打印张数

	private boolean isShowDishImg = false;
	private TimerTaskController timerTaskController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//        if (Build.VERSION.SDK_INT > 9) {
		//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		//                    .permitAll().build();
		//            StrictMode.setThreadPolicy(policy);
		//        }
		setContentView(R.layout.aty_new_table_main);
		myApplication.addPage(TableMainAty.this);
		ButterKnife.bind(this);
		PrintManager.getInstance().setContext(context);
		MyApplication.getInstance().setContext(context);
		timerTaskController = TimerTaskController.getInstance();
		EventBus.getDefault().register(this);
		loadData();
		bindLogService();
		bindUsbPrint();
		getLogoPath();

		getWorkShiftDefinition();
		//        getPayType();
		getMarketList();
	}

	private GpUsbPrinter usbPrinter;

	private void bindUsbPrint() {
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

	//获取班次定义的接口
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
						checkWorkShift(mUserData.getRealName());
						System.out.println(mUserData.getRealName() + " ===username");
					} else {
						errorReturnLogin();
						showToast("无法找到门店对应的班次定义,无法使用收银系统!");
						Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					errorReturnLogin();
					showToast("无法找到门店对应的班次定义,无法使用收银系统!");
					Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			errorReturnLogin();
			showToast("无法找到门店对应的班次定义,无法使用收银系统!");
			Log.i("无法找到门店对应的班次定义,无法使用收银系统!", "");
		}
	}

	//获取营销活动
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

	@Override
	protected void onResume() {
		super.onResume();
		//        if (store.getReceiveNetOrder()) {
		//            timerTaskController.cancleTimter(false);
		//            MyApplication.getInstance().setConFirmNetOrder(true);
		//            timerTaskController.SyncNetOrder();
		//        }
		MyApplication.setContext(context);
		initSecondaryScreen();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭轮训获取网上订单的开关
		MyApplication.getInstance().setConFirmNetOrder(false);
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
		if (secondaryPresentation != null && secondaryPresentation.isShowing()) {
			secondaryPresentation.dismiss();
		}
		if (timer != null) {
			timer.cancel();
		}
		DishDataController.cleanDishDate();
		DishDataController.cleanDishMarkMap();
		NetOrderController.cleanNetOrderData();
		PrinterDataController.cleanPrinterData();
	}

	private void loadData() {
		intent = new Intent();
		posInfo = PosInfo.getInstance();
		mUserData = UserData.getInstance(context);
		store = Store.getInstance(context);
		progressDialog = new ProgressDialogF(this);
		if (StoreInfor.terminalInfo != null) {
			TerminalInfo terminalInfo = StoreInfor.terminalInfo;
			String brandName = TextUtils
					.isEmpty(terminalInfo.brandName) ? "智慧收银" : terminalInfo.brandName;
			String storeName = TextUtils
					.isEmpty(terminalInfo.sname) ? "奥琦玮云POS" : terminalInfo.sname;
			if (TextUtils.isEmpty(terminalInfo.sname)) {
				tvMainTitle.setText(brandName);
			}
			tvMainTitle.setText(brandName + "-" + storeName);
		}
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

		SyncPrinterAndKds();
	}


	//绑定Service
	private void bindLogService() {
		Intent intent = new Intent(TableMainAty.this, LogService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
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
					showToast("获取商家LOGO失败!" + e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
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

	//初始化副屏幕
	private void initSecondaryScreen() {
		DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
		if (dm != null) {
			Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

			//            myApplication.ShowToast("secondary screen found: " + displays.length);

			for (Display display : displays) {
				if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
					secondaryPresentation = new SecondaryPresentation(this, display, null);
					secondaryPresentation.show();
				}
			}
		}
	}

	private Dialog        dialog;
	private TextView      title;
	private ScrolGridView gv_definition;
	private LinearLayout  lin_select_work;
	private EditText      ed_standby_money;
	private TextView      negativeButton, positiveButton;
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
		ed_standby_money = (EditText) dialog.findViewById(R.id.ed_standby_money);
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
				showToast("未设置开班选项,无法使用收银系统!");
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
				showToast("未设置开班选项,无法使用收银系统!");
			}
		});
		return dialog;
	}

	private Dialog       dialogEnd;
	private TextView     titleEnd;
	private TextView     tv_standby_money;
	private EditText     ed_standby_moneyEnd;
	private LinearLayout ed_lin_select_work;
	private TextView     negativeButtonEnd, positiveButtonEnd;

	/**
	 * 初始化交班dialog
	 *
	 * @return
	 */
	private Dialog createEndWorkShift() {
		dialogEnd = DialogUtil.getDialog(context, R.layout.dialog_work_shift, 0.5f, 0.3f);
		titleEnd = (TextView) dialogEnd.findViewById(R.id.print_title);
		ed_standby_moneyEnd = (EditText) dialogEnd.findViewById(R.id.ed_standby_money);
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
	 * 开班设置
	 */
	private void startWorkShift() {
		lin_select_work.setVisibility(View.VISIBLE);
		title.setText("开班");

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
					workShift.setUserName(mUserData.getRealName());
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
							myApplication.ShowToast("开班失败,请重试!" + e.getMessage());
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
	 * 显示点菜界面
	 */
	private void showOrderDishMain() {
		UserData mUserData = UserData.getInstance(context);
		mUserData.setWorkShifts(true);
	}

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
							showToast("获取门店对应的班次信息失败,请重新登录收银系统!");
							Log.i("获取门店对应的班次信息失败", "");
						}
					}
				}

				@Override
				public void onError(PosServiceException e) {
					e.printStackTrace();
					errorReturnLogin();
					showToast("获取门店对应的班次信息失败,请重新登录收银系统!");
					Log.i("获取门店对应的班次信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			errorReturnLogin();
			showToast("获取门店对应的班次信息失败,请重新登录收银系统!");
			Log.i("获取门店对应的班次信息失败", e.getMessage());
		}
	}

	//获取支付方式列表
	private void getPayType() {
		try {
			if (StoreInfor.getPaymentList() != null && StoreInfor.getPaymentList().size() > 0) {
				StoreInfor.getPaymentList().clear();
			}
			DishService dishService = DishService.getInstance();
			dishService.getPaytypeList(new ResultCallback<List<Payment>>() {

				@Override
				public void onResult(List<Payment> result) {
					if (result != null && result.size() > 0) {
						//                        StoreInfor.paymentList = result;
					}
				}

				@Override
				public void onError(PosServiceException e) {
					if (!TextUtils.isEmpty(e.getMessage())) {
						showToast(e.getMessage());
						Log.i("获取支付方式列表", e.getMessage());
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("获取支付方式列表", e.getMessage());
		}

	}


	/**
	 * 当出错时,退回到LoginAty重新登录
	 */
	private void errorReturnLogin() {
		myApplication.clean();
		Intent orderIntent = new Intent(TableMainAty.this, LoginAty.class);
		startActivity(orderIntent);
		myApplication.unbindCashBoxServer();
		myApplication.soundPool.stop(1);
	}

	private AlertDialog alertDialog;
	private long        tableId;
	private Order       tableOrder;

	@Subscribe
	public void PosEventCallBack(PosEvent event) {
		switch (event.getAction()) {
			case Constant.JUMP_LOGIN:
				jumpLogin();
				break;
			//测试打印机打印情况
			case Constant.TestPrinterState.TEST_PRINT:
				//                MainEvenBusController.testPrint(event.getPrinter());
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							GpEnternetPrintTwo.gpPrint("192.168.1.122", 30);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				break;
			//测试KDS打印情况
			case Constant.TestPrinterState.TEST_PRINT_KDS:
				MainEvenBusController.testPrintKds(event.getKds());
				break;
			//当前时段没有菜品档案
			case Constant.EventState.CURRENT_TIME_DISH_NULL:
				showToast("当前时段没有可供选择的菜品!");
				break;
			//通过交接班历史打印交接班报表
			case Constant.EventState.PRINT_WORKE_SHIFT_HISTORY:
				//                workShiftReport(event.getPosition(), false);
				break;
			//下单打印标识
			case Constant.EventState.PRINTER_ORDER:
				DishOptionController.cleanCartDishMap();//下单后清除菜品的定制项缓存
				if (fastFoodFragment != null) {
					fastFoodFragment.getOrderId();
				}
				//                MainEvenBusController.printGuestOrder(store,event.getOrder(), guestReceiptCounts);
				posInfo.setAddWaiMaiMoney(false);//订单添加过外卖配送费逻辑字段
				posInfo.setCustomerAmount(1);//下单成功后将默认的顾客人数设置为1
				posInfo.setComment("");
				break;
			//打印结账单
			case Constant.EventState.PRINT_CHECKOUT:
				//                MainEvenBusController.printCheckOutOrder(event.getOrder(), event.getPosition(), checkOutPrintCounts);
				break;
			//打印厨房小票
			case Constant.EventState.PRINTER_KITCHEN_ORDER:
				MainEvenBusController.printKitChenOrder(event.getOrder());
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
				//                MainEvenBusController.printGuestRetreatDish(event.getOrder(), guestReceiptCounts);
				break;
			//客用退单打印
			case Constant.EventState.PRINTER_RETREAT_ORDER:
				//                MainEvenBusController.printGuestRetreatOrder(event.getOrder(), guestReceiptCounts);
				break;
			//补打下单标识
			case Constant.EventState.PRINTER_EXTRA_RECEIPT:
				//                MainEvenBusController.printExtraReceipt(event.getOrder(), guestReceiptCounts);
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
				Order tableOrder = event.getOrder();
				enterTable(tableOrder.getTableId(), tableOrder);
				break;
			//Token过期的标识
			case Constant.EventState.TOKEN_TIME_OUT:
				Looper.prepare();
				if (alertDialog == null) {
					alertDialog = DialogUtil
							.LoginErrorDialog(context, "出错啦!", "服务器Token异常,是否要重新登录?", new DialogCallback() {
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
					alertDialog = DialogUtil
							.LoginErrorDialog(context, errMessage, "解决错误后按 ( 确定 ) 重新进行打印.", new DialogCallback() {
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
										//                                MainEvenBusController.printGuestOrder(order, guestReceiptCounts);
									}
									//预结小票或者是结账小票
									else if (cashActionId == Constant.EventState.ORDER_TYPE_ADVANCE || cashActionId == Constant.EventState.PRINT_CHECKOUT) {
										//                                MainEvenBusController.printCheckOutOrder(order, position, checkOutPrintCounts);
									}
									//客票退菜打印
									else if (cashActionId == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
										//                                MainEvenBusController.printGuestRetreatDish(order, guestReceiptCounts);
									}
									//客票退单打印
									else if (cashActionId == Constant.EventState.PRINTER_RETREAT_ORDER) {
										//                                MainEvenBusController.printGuestRetreatOrder(order, guestReceiptCounts);
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
				Bundle errKitBundle = new Bundle();
				errKitBundle.putSerializable("order", event.getOrder());
				errKitBundle.putSerializable("oi", event.getOi());
				errKitBundle.putSerializable("dishPackage", event.getDishPackageItem());
				errKitBundle.putSerializable("printer", event.getPrinter());
				intentKit.putExtras(errKitBundle);
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
					intentUp.setClass(TableMainAty.this, DishCountNewAty.class);
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
				}
				//会员
				else if (event.getPosition() == PowerController.MEMBER) {
					ToolsUtils.writeUserOperationRecords("会员");
					startActivity(new Intent(this, MemberAty.class));
				}
				//历史订单
				else if (event.getPosition() == PowerController.HISTORY_ORDER) {
					ToolsUtils.writeUserOperationRecords("历史订单");
					intent.setClass(TableMainAty.this, OrderInfoAty.class);
					startActivity(intent);
					//                    //daySettlement();
					//                    startActivity(new Intent(this, MemberAty.class));
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
					intentUp.setClass(TableMainAty.this, UpLoadActivity.class);
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
					ToolsUtils.writeUserOperationRecords("网上订单");
					//                    MyApplication.getInstance().setConFirmNetOrder(false);//跳转过去后这边停止轮训获取网上订单
					//                    timerTaskController.cancleTimter(false);
					Intent netOrderIntent = new Intent(context, NetOrderNewAty.class);
					startActivityForResult(netOrderIntent, NETORDERITEM);
				}
				//                //版本更新
				//                else if (event.getPosition() == Constant.MainSelctAction.APP_UPDATA) {
				//                    ToolsUtils.writeUserOperationRecords("版本更新");
				//                    checkUpdate();
				//                }
				//                //交班
				//                else if (event.getPosition() == Constant.MainSelctAction.WORK_SHIFT) {
				//                    ToolsUtils.writeUserOperationRecords("交班");
				//                    if (dialogEnd == null) {
				//                        createEndWorkShift();
				//                    }
				//                    if (PrintManager.getInstance().getReceiptPrinter() == null) {
				//                        PrintManager.getInstance().openCashBox();
				//                    } else {
				//                        new Thread(new Runnable() {
				//                            @Override
				//                            public void run() {
				//                                PrintManager.getInstance().openCashBox();
				//                            }
				//                        }).start();//启动打印线程
				//                    }
				//                    endWorkShift();
				//                    dialogEnd.show();
				//                }
				//                //关于POS
				//                if (event.getPosition() == Constant.MainSelctAction.ABOUT_APP) {//关于POS
				//                    ToolsUtils.writeUserOperationRecords("关于POS");
				//                    DialogUtil.aboutDialog(context);
				//                }
				//                //关于本店
				//                if (event.getPosition() == Constant.MainSelctAction.ABOUT_STORE) {//关于本店
				//                    ToolsUtils.writeUserOperationRecords("关于本店");
				//                    DialogUtil.aboutStoreDialog(context, StoreInfor.terminalInfo, new DialogCallback() {
				//                        @Override
				//                        public void onConfirm() {
				//                            DialogUtil.ordinaryDialog(context, "解绑", "是否确定要解绑?", new DialogCallback() {
				//                                @Override
				//                                public void onConfirm() {
				//                                    unBindStore();
				//                                }
				//
				//                                @Override
				//                                public void onCancle() {
				//
				//                                }
				//                            });
				//                        }
				//
				//                        @Override
				//                        public void onCancle() {
				//
				//                        }
				//                    });
				//                }
				//                //更多
				//                if (event.getPosition() == Constant.MainSelctAction.MORE) {//更多
				//                    ToolsUtils.writeUserOperationRecords("更多");
				//                    Intent intentAbout = new Intent();
				//                    intentAbout.setClass(TableMainAty.this, AboutAty.class);
				//                    startActivity(intentAbout);
				//                }
				//                //日结
				//                if (event.getPosition() == Constant.MainSelctAction.TABLE_STATE) {//日结 设置桌台
				//                    ToolsUtils.writeUserOperationRecords("设置桌台");
				//                    //                    daySettlement();
				//                    startActivity(new Intent(this, SetTableActivity.class));
				//                }
				//                //添加菜品
				//                if (event.getPosition() == Constant.MainSelctAction.ADD_DISH) {
				//                    ToolsUtils.writeUserOperationRecords("添加菜品");
				//                    Intent intentAddDish = new Intent();
				//                    intentAddDish.setClass(TableMainAty.this, AddDishAty.class);
				//                    startActivity(intentAddDish);
				//                }
				//                //会员
				//                if (event.getPosition() == 9) {//会员
				//                    ToolsUtils.writeUserOperationRecords("会员");
				//                    //                    daySettlement();
				//                    startActivity(new Intent(this, MemberAty.class));
				//                }
				//                if (event.getPosition() == 10)//员工列表
				//                {
				//                    ToolsUtils.writeUserOperationRecords("员工列表");
				//                    startActivity(new Intent(this, StaffAty.class));
				//                }
				//                if (event.getPosition() == 11)//备用金
				//                {
				//                    ToolsUtils.writeUserOperationRecords("备用金");
				//                    startActivity(new Intent(this, StandByCashAty.class));
				//                }
				//                if (event.getPosition() == 12)//设置
				//                {
				//                    ToolsUtils.writeUserOperationRecords("设置");
				//                    startActivity(new Intent(this, ManageSetAty.class));
				//                }
				//                //添加菜品
				//                if (event.getPosition() == 13) {
				//                    ToolsUtils.writeUserOperationRecords("添加菜品");
				//                    Intent intentAddDish = new Intent();
				//                    intentAddDish.setClass(TableMainAty.this, AddDishAty.class);
				//                    startActivity(intentAddDish);
				//                }
				break;
			//清空购物车
			case Constant.EventState.CLEAN_CART:
				PosInfo posInfo = PosInfo.getInstance();
				posInfo.setCustomer(null);
				Cart.cleanDishList();
				DishDataController.cleanDishMarkMap();
				SunmiSecondScreen.getInstance(context).cleanDSD();
				if (fastFoodFragment != null) {
					fastFoodFragment.cleanData();
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
			//切换用户
			case Constant.EventState.LOGOUT:
				logOut();
				break;
		}
	}

	/**
	 * 解绑门店
	 */
	private void unBindStore() {
		DialogUtil.ordinaryDialog(context, "解绑", "是否确定要解绑?", new DialogCallback() {
			@Override
			public void onConfirm() {
				String userName    = mUserData.getRealName();
				String userPwd     = mUserData.getPwd();
				String terminalMac = posInfo.getTerminalMac();
				try {
					StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
					storeBusinessService
							.unbindStore(userName, userPwd, terminalMac, new ResultCallback<Integer>() {
								@Override
								public void onResult(Integer result) {
									if (result == 0) {
										showToast("解绑门店信息成功!");
										store.setDeviceName("");
										StoreInfor.terminalInfo = null;
										jumpLogin();
									}
								}

								@Override
								public void onError(PosServiceException e) {
									showToast("解绑门店信息失败," + e.getMessage());
									Log.i("解绑门店信息失败", e.getMessage());
								}
							});
				} catch (PosServiceException e) {
					e.printStackTrace();
					showToast("解绑门店信息失败," + e.getMessage());
					Log.i("解绑门店信息失败", e.getMessage());
				}
			}

			@Override
			public void onCancle() {

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
	 * 交班
	 */
	private void endWorkShift() {
		titleEnd.setText("交班");
		lin_select_work.setVisibility(View.GONE);
		ed_standby_moneyEnd.setHint("请输入钱箱内所剩金额");
		tv_standby_money.setText("钱箱金额");

		/**
		 * 确定
		 */
		negativeButtonEnd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ToolsUtils.writeUserOperationRecords("确定交班按钮");
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
					PosInfo posInfo = PosInfo.getInstance();
					storeBusinessService.endWorkShift(new Long(posInfo
							.getWorkShiftId()), workShift, new ResultCallback<Integer>() {
						@Override
						public void onResult(Integer result) {
							showToast("交班成功!");
							PosInfo posInfo = PosInfo.getInstance();
							//                            workShiftReport(posInfo.getWorkShiftId(), true);
						}

						@Override
						public void onError(PosServiceException e) {
							myApplication.ShowToast("交班失败,请重试!" + e.getMessage());
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

	//    /**
	//     * 获取班次交接报表数据
	//     *
	//     * @param workShiftId 班次Id
	//     */
	//    private void workShiftReport(Integer workShiftId, final boolean isJumpLogin) {
	//        try {
	//            SystemService systemService = SystemService.getInstance();
	//            systemService.workShiftReport(workShiftId, new ResultCallback<WorkShiftReport>() {
	//                @Override
	//                public void onResult(final WorkShiftReport result) {
	//                    Log.i("获取交接班报表数据==>>", ToolsUtils.getPrinterSth(result));
	//                    printWorkShift(result, isJumpLogin);
	//                }
	//
	//                @Override
	//                public void onError(PosServiceException e) {
	//                    if (isJumpLogin) {
	//                        posInfo.getInstance().setWorkShiftId(null);
	//                    }
	//                    showToast("获取交接班报表数据失败," + e.getMessage());
	//                    Log.i("获取交接班报表数据失败", e.getMessage());
	//                }
	//            });
	//        } catch (PosServiceException e) {
	//            e.printStackTrace();
	//            if (isJumpLogin) {
	//                posInfo.getInstance().setWorkShiftId(null);
	//            }
	//            Log.i("获取交接班报表数据失败", e.getMessage());
	//        }
	//    }

	/**
	 * 交接班打印
	 *
	 * @param result
	 */
	private WorkShiftReport workShiftReport;

	private void printWorkShift(final WorkShiftReport result, final boolean isJumpLogin) {
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

	private void endDailyBusiness(final WorkShiftReport workShiftReport) {
		try {
			OrderService orderService = OrderService.getInstance();
			orderService.endDailyBusiness(new ResultCallback<PosResponse>() {
				@Override
				public void onResult(PosResponse result) {
					if (result != null) {
						showToast("日结成功!");
						printDailyReport(workShiftReport);
						//                        dailyReport();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("日结失败," + e.getMessage());
					Log.i("日结失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			showToast("日结失败," + e.getMessage());
			Log.i("日结失败", e.getMessage());
		}
	}

	private void dailyReport() {
		try {
			SystemService systemService = SystemService.getInstance();
			systemService.dailyReport(new ResultCallback<WorkShiftReport>() {
				@Override
				public void onResult(final WorkShiftReport result) {
					Log.i("获取门店日结信息", ToolsUtils.getPrinterSth(result));
					if (result != null) {
						Intent intent = new Intent();
						intent.setClass(TableMainAty.this, ShowReportAty.class);
						intent.putExtra("WorkShiftReport", (Serializable) result);
						intent.putExtra("printType", PowerController.DAILY);
						startActivity(intent);
						//printDailyReport(result);
					} else {
						showToast("获取门店日结信息为空!");
						Log.i("获取门店日结信息为空", "");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("获取门店日结信息失败!" + e.getMessage());
					Log.i("获取门店日结信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
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
						MyApplication.getInstance().ShowToast("已是最新版本");
					}
				} else {
					MyApplication.getInstance().ShowToast("获取版本信息失败!");
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialogF.disLoading();
				showToast("获取版本信息失败!" + e.getMessage());
				Log.i("获取版本信息失败", e.getMessage());
			}
		});
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
				//                ToolsUtils.writeUserOperationRecords("跳转订单列表界面");
				//                intent.setClass(TableMainAty.this, OrderInfoAty.class);
				//                startActivity(intent);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							GpEnternetPrintTwo.gpPrint("192.168.1.122", 30);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				break;
			case R.id.tv_checkBox:
				ToolsUtils.writeUserOperationRecords("钱箱");
				openMoneyBox();
				break;
			case R.id.tv_menu:
				ToolsUtils.writeUserOperationRecords("菜单");
				startActivity(new Intent(TableMainAty.this, DishMenuAty.class));
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
			//            case tv_hang_order:
			//                ToolsUtils.writeUserOperationRecords("挂单");
			//                if (!Cart.isCartDishNull()) {
			//                    Cart.handDishList(Cart.getDishItemList());
			//                } else {
			//                    showToast("请先点菜!");
			//                }
			//                break;
			//            case R.id.tv_fetch_order:
			//                ToolsUtils.writeUserOperationRecords("取单");
			//                intent.setClass(TableMainAty.this, FetchOrderAty.class);
			//                startActivityForResult(intent, FETCHORDER);
			//                break;
			//            case R.id.tv_netorder:
			//                ToolsUtils.writeUserOperationRecords("跳转网络订单界面");
			//                MyApplication.getInstance().setConFirmNetOrder(false);//跳转过去后这边停止轮训获取网上订单
			//                Intent netOrderIntent = new Intent(context, NetOrderAty.class);
			//                startActivityForResult(netOrderIntent, NETORDERITEM);
			//                break;
			//            case R.id.tv_orderdish:
			//                ToolsUtils.writeUserOperationRecords("跳转快餐点菜下单界面");
			//                Intent tableIntent = new Intent(context, OrderDishAty.class);
			//                startActivity(tableIntent);
			//                break;
			//            case R.id.tv_order:
			//                ToolsUtils.writeUserOperationRecords("跳转订单列表界面");
			//                intent.setClass(TableMainAty.this, OrderInfoAty.class);
			//                startActivity(intent);
			//                break;
			//            case R.id.tv_main_title:
			//                break;
			//            case R.id.tv_reserve:
			//                List<Reserve> reserveList = new ArrayList<>();
			//                Reserve reserve1 = new Reserve("张先生", 5, "013", "2016-05-12", "05:00");
			//                Reserve reserve2 = new Reserve("李先生", 9, "009", "2016-07-14", "06:00");
			//                Reserve reserve3 = new Reserve("王先生", 13, "034", "2016-09-03", "07:00");
			//                Reserve reserve4 = new Reserve("赵先生", 2, "018", "2016-12-15", "08:00");
			//                Reserve reserve5 = new Reserve("朝先生", 4, "033", "2016-03-24", "09:00");
			//                reserveList.add(reserve1);
			//                reserveList.add(reserve2);
			//                reserveList.add(reserve3);
			//                reserveList.add(reserve4);
			//                reserveList.add(reserve5);
			//                DialogUtil.reservePlaceDialog(context, reserveList, new DialogTCallback() {
			//                    @Override
			//                    public void onConfirm(Object o) {
			//                        DialogUtil.reserveDialog(context, new DialogTCallback() {
			//                            @Override
			//                            public void onConfirm(Object o) {
			//
			//                            }
			//
			//                            @Override
			//                            public void onCancle() {
			//
			//                            }
			//                        });
			//                    }
			//
			//                    @Override
			//                    public void onCancle() {
			//
			//                    }
			//                });
			//                break;
		}
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

	private void setTableInfo(final long tableId, final Order order) {
		Intent tableIntent = new Intent(context, TableOrderNewAty.class);
		tableIntent.putExtra("tableId", tableId);
		if (order != null) {
			Bundle tableBun = new Bundle();
			tableBun.putSerializable("tableOrder", order);
			tableIntent.putExtras(tableBun);
		}
		startActivity(tableIntent);
	}

	private static int NETORDERITEM = 110;//网络订单回调
	private static int FETCHORDER   = 120;//取单回调

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

	//    private void setBadeView(int size) {
	//        if (size > 0) {
	//            badeView.setText(size + "");
	//            badeView.show();
	//        } else {
	//            badeView.hide();
	//        }
	//    }

	private void enterTable(final long tableId, final Order tableOrder) {
		try {
			TableService tableService = TableService.getInstance();
			tableService.enterTable(tableId, new ResultCallback() {
				@Override
				public void onResult(Object result) {
					if ((int) result == 0) {
						RetreatDishController.setTableOrder(tableOrder);
						Intent intent = new Intent(TableMainAty.this, TableOrderNewAty.class);
						intent.putExtra("tableId", tableOrder.getTableId());
						Bundle bundle = new Bundle();
						bundle.putSerializable("tableOrder", tableOrder);
						if (tableOrder != null && tableOrder.getId() > 0 && tableOrder.getItemList()
								.size() > 0) {
							RetreatDishController.setItemList(tableOrder.getItemList());
						}
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("进入桌台失败," + e.getMessage() + "!");
					Log.i("进入桌台失败,", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			Log.i("进入桌台失败,", e.getMessage());
		}
	}

	/**
	 * 获取门店营销活动列表
	 */
	private void getStoreMarket() {
		try {
			cleanStoreMarket();
			SystemService systemService = SystemService.getInstance();
			systemService.getStoreMarket(new ResultCallback<List<Market>>() {
				@Override
				public void onResult(List<Market> result) {
					if (result != null) {
						MarketDataController.marketList = result;
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
		timer.schedule(task, delayedTime, cycleTime);//延时3秒并且3秒循环一次获取拉卡拉交易情况
	}

	private void logOut() {
		ToolsUtils.writeUserOperationRecords("注销用户");
		DialogUtil.ordinaryDialog(context, "注销用户", "是否要注销该用户?", new DialogCallback() {
			@Override
			public void onConfirm() {
				jumpLogin();
			}

			@Override
			public void onCancle() {

			}
		});
	}


	//监听back键,弹出退出提示dialog
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			DialogUtil.ordinaryDialog(context, "退出", "是否确定退出?", new DialogCallback() {
				@Override
				public void onConfirm() {
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

}
