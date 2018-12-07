package cn.acewill.pos.next.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acewill.paylibrary.MicropayTask;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.epay.AliGoodsItem;
import com.acewill.paylibrary.epay.EPayResult;
import com.acewill.paylibrary.tencent.WXPay;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.CreatDealBack;
import cn.acewill.pos.next.interfices.DialogCall;
import cn.acewill.pos.next.interfices.DialogCallBack;
import cn.acewill.pos.next.interfices.DialogMTCallback;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.interfices.InterfaceDialog;
import cn.acewill.pos.next.interfices.KeyCallBack;
import cn.acewill.pos.next.interfices.PermissionCallback;
import cn.acewill.pos.next.model.Customer;
import cn.acewill.pos.next.model.PaymentCategory;
import cn.acewill.pos.next.model.PaymentList;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.PaymentStatus;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.model.wsh.WshCreateDeal;
import cn.acewill.pos.next.model.wsh.WshDealPreview;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.WshService;
import cn.acewill.pos.next.service.retrofit.response.LKLResponse;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.service.retrofit.response.ValidationResponse;
import cn.acewill.pos.next.service.retrofit.response.WeiFuTongResponse;
import cn.acewill.pos.next.service.retrofit.response.pay.BaseWechatPayResult;
import cn.acewill.pos.next.service.retrofit.response.pay.GsonUtils;
import cn.acewill.pos.next.service.retrofit.response.pay.PayResult;
import cn.acewill.pos.next.service.retrofit.response.pay.PaymentInfo;
import cn.acewill.pos.next.service.retrofit.response.pay.WechatPayResult;
import cn.acewill.pos.next.service.retrofit.response.pay.WechatPayResult2;
import cn.acewill.pos.next.service.retrofit.response.pay.WechatPayResult3;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ProgressDialogF;

import static cn.acewill.pos.next.model.dish.Cart.getDishItemList;

/**
 * 快捷结账
 * Created by aqw on 2016/12/5.
 */
public class CheckOutUtil implements ScanGunKeyEventHelper.OnScanSuccessListener {

	private Context         context;
	private ProgressDialogF progressDialog;
	private OrderService    orderService;
	private WshService      wshService;
	private Cart            cart;
	private Store           store;
	private Order           printOrder;
	private Payment         payment;
	private String          paymentNo;//支付宝或微信流水号
	private String          transactionNo; //微信，支付宝，刷卡等方式的支付流水号，特别长的一段字符串
	private BigDecimal      total_money;//总金额
	private int             payTypeID;
	private static final int     FAIL_PAY     = 0;//支付失败
	private static final int     FAIL_ORDER   = 1;//下单失败
	private static final int     FAIL_LKL_PAY = 2;//拉卡拉失败
	private static final int     FAIL_WFT_PAY = 3;//威富通失败
	private              boolean scaning      = false;//防止扫码枪扫多次
	private String code_scan;//反扫返回的二维码号，用于重试使用

	private Dialog  failDialog;
	private OutTask outTask;
	private boolean isDebug     = false;
	private int     pay_channel = PayReqModel.PTID_SSS_WEIXIN;// 支付宝：PayReqModel.PTID_SSS_ALI，微信:PTID_SSS_WEIXIN
	private Dialog                paydialog;
	private ScanGunKeyEventHelper mScanGunKeyEventHelper;
	private String                orderId;
	public  List<AliGoodsItem> aliGoodsItem       = new ArrayList<AliGoodsItem>();//支付宝参数
	private BigDecimal         printMoney         = new BigDecimal(0);
	private boolean            isAnyCheckOut      = false;//允许输入任意金额结账,但必须有一种支付方式即使为零
	private Payment            anyCheckoutPayMent = null;//任意支付金额的支付方式

	private final Timer timer = new Timer();//轮询拉卡拉支付交易情况
	private TimerTask lklTask;
	private Handler   handler;

	private Timer timerwft = new Timer();//轮询威富通支付交易情况
	private TimerTask wftTask;
	private Handler   wfthandler;
	private int       totalFee;
	private String    orderTradeNo;
	private String    autoCode;

	private int        delayedTime   = 2 * 1000;//延迟2秒
	private int        cycleTime     = 3 * 1000;//周期循环时间
	private String     bizId         = "";
	private Account    accountMember = null;//结账微生活会员信息
	private CardRecord cardRecord    = null;
	private List<Payment>                            memberPayMent;//结账方式中含有微生活支付信息List
	private CopyOnWriteArrayList<ValidationResponse> addValidationList;//结账方式中使用了那些美团券的一些信息
	private String                                   wft_transaction_id;
	private int        wftPayType  = 3;//选择威富通 是微信支付还是支付宝支付  0是微信 1是支付宝
	/**
	 * 被抹零的金额
	 */
	private BigDecimal wipingValue = new BigDecimal("0.00");

	private PosInfo posInfo;
	private Dialog  memberDialog;

	boolean isDisCount = false;//支付时检测菜品沽清状态 如果成功 其它的支付方式就不去检测沽清状态正常支付下单
	private OrderService mOrderService;


	public CheckOutUtil(Context context) {
		this.context = context;
		anyCheckoutPayMent = null;
		store = Store.getInstance(context);
		progressDialog = new ProgressDialogF(context);
		cart = Cart.getInstance();
		posInfo = PosInfo.getInstance();
		orderId = posInfo.getOrderId() + "";
		isDisCount = false;
		printOrder = cart.getOrderItem(null, cart.getDishItemList());
		isAnyCheckOut = PowerController.isAllow(PowerController.TAKE_OUT_SALE);

	}

	public CheckOutUtil(Context context, Payment payment) {
		this.context = context;
		this.payment = ToolsUtils.cloneTo(payment);
		store = Store.getInstance(context);
		BigDecimal cartCost = new BigDecimal(cart.getCost() + "");
		this.total_money = ToolsUtils.wipeZeroMoney(cartCost);
		wipingValue = cartCost.subtract(total_money);
		anyCheckoutPayMent = null;
		progressDialog = new ProgressDialogF(context);
		cart = Cart.getInstance();
		printOrder = cart.getOrderItem(null, cart.getDishItemList());
		payTypeID = payment.getId();
		isDisCount = false;
		posInfo = PosInfo.getInstance();
		mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
		isAnyCheckOut = PowerController.isAllow(PowerController.TAKE_OUT_SALE);

		orderId = posInfo.getOrderId() + "";

		List<OrderItem> orderItems = printOrder.getItemList();

		if (orderItems != null) {
			for (OrderItem orderItem : orderItems) {
				if (!ToolsUtils.isList(orderItem.getTempMarketList())) {
					orderItem.setMarketList(orderItem.getMarketList(), orderItem
							.getTempMarketList());
				}
				AliGoodsItem item = new AliGoodsItem();
				item.setGoods_id(orderItem.getId() + "");
				item.setGoods_category(orderItem.getDishId() + "");
				item.setPrice(FormatUtils
						.getDoubleW(new BigDecimal(orderItem.getPrice() + "").doubleValue()));
				item.setQuantity(orderItem.getQuantity() + "");
				item.setGoods_name(orderItem.getDishName());
				aliGoodsItem.add(item);
			}
		}

		try {
			orderService = OrderService.getInstance();
			wshService = WshService.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void payDialogDismissLis() {
		if (paydialog != null) {
			paydialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					wftTimerCancel();
					if (failDialog != null && failDialog.isShowing()) {
						failDialog.dismiss();
					}
					isShowFailDialog = false;
					if (outTask != null)
						outTask.cancel(true);
				}
			});
		}
	}

	private void showToast(String msg) {
		MyApplication.getInstance().ShowToast(msg);
	}

	/**
	 * 查询支付结果的runnable，重复查询结果
	 */
	private Runnable queryPayResultRunnable = new Runnable() {
		@Override
		public void run() {
			mOrderService
					.queryWechatPayResult(mPayReqModel, new ResultCallback<BaseWechatPayResult>() {
						@Override
						public void onResult(BaseWechatPayResult result) {
							if (result.getResult() == 0) {
								try {
									WechatPayResult3 bean = GsonUtils
											.getSingleBean(result
													.getContent(), WechatPayResult3.class);
									Log.e("查询微信支付结果>", new Gson().toJson(bean));
									if ("SUCCESS".equals(bean.getResult_code())) {
										if ("NOTPAY".equals(bean.getTrade_state())) {
											wechatPayResult(PayResult.UNPAY);
											//									mView.returnQueryWechatPayResult(new SelfPosPayResult(PayResultType.PAY_INPROGRESS, PayResultMsg.PAY_INPROGRESS));
										} else if ("SUCCESS".equals(bean.getTrade_state())) {
											wechatPayResult(PayResult.SUCCESS);
											showToast(ToolsUtils.returnXMLStr("pay_success"));
											paymentNo = bean.getOut_trade_no();
											transactionNo = bean.getOut_trade_no();
											hideDialog();
											creatOrdAndCheckOut();
											handler2.removeCallbacks(queryPayResultRunnable);
											hideDialog();
											ToastUitl
													.show(MyApplication.getContext(), "支付成功", 3000);
											//									mView.returnQueryWechatPayResult(new SelfPosPayResult(PayResultType.PAY_SUCCESS, PayResultMsg.PAY_SUCCESS));
										} else if ("USERPAYING".equals(bean.getTrade_state())) {
											wechatPayResult(PayResult.USERPAYING);
											//									mView.returnQueryWechatPayResult(new SelfPosPayResult(PayResultType.USER_PAYING, PayResultMsg.USER_PAYING));
										} else {
											wechatPayResult(PayResult.UNKNOW);
											//									mView.returnQueryWechatPayResult(new SelfPosPayResult(PayResultType.PAY_UNKNOW, PayResultMsg.PAY_UNKNOW));
										}
									} else {
										wechatPayResult(PayResult.FAIL);

										//								mView.returnQueryWechatPayResult(new SelfPosPayResult(PayResultType.PAY_FAIL, PayResultMsg.PAY_FAIL, bean
										//										.getReturn_msg()));
									}

								} catch (Exception e) {
									//							mView.returnQueryWechatPayResult(new SelfPosPayResult(PayResultType.PAY_ERROR, PayResultMsg.PAY_ERROR));
									e.printStackTrace();
								}
							}
						}

						@Override
						public void onError(PosServiceException e) {

						}
					});
		}
	};

	/**
	 * 微信支付结果
	 *
	 * @param statu
	 */
	private void wechatPayResult(int statu) {
		switch (statu) {
			case PayResult.UNPAY:
				handler2.postDelayed(queryPayResultRunnable, 2000);
				break;
			case PayResult.SUCCESS:

				break;
			case PayResult.USERPAYING:
				break;
			case PayResult.FAIL:
				break;
			case PayResult.UNKNOW:
				break;
		}
	}

	/**
	 * 用来查询结果的handler
	 */
	Handler handler2 = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			return false;
		}
	});
	private PayReqModel mPayReqModel;

	//扫码判断
	private void scanLogic2() {
		// 正扫
		if (Store.getInstance(context).isFront()) {

			progressDialog.showLoading("");
			String      storeName = Store.getInstance(context).getStoreName();
			PayReqModel model     = new PayReqModel();
			model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
			model.orderNo = orderId;
			model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils
					.returnXMLStr("product_details") : storeName;
			model.isDebug = isDebug;
			model.payType = pay_channel;
			model.authCode = "";
			model.aliGoodsItem = aliGoodsItem;
			model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils
					.returnXMLStr("food_consumption") : storeName;
			model.storeId = Store.getInstance(context).getStoreId();
			model.terminalId = Store.getInstance(context).getDeviceName();
			try {
				mOrderService = OrderService.getInstance();
			} catch (PosServiceException e) {
				e.printStackTrace();
			}
			PaymentInfo info = new PaymentInfo();
			info.appIDs = WXPay.APPID;
			info.keyStr = WXPay.KEY;
			info.appsecret = WXPay.APPSECRET;
			info.mchID = WXPay.MCH_ID;
			info.subMchID = WXPay.SUB_MCH_ID;
			model.paymentStr = new Gson().toJson(info);
			mPayReqModel = model;
			mOrderService.wechatSaoMa(model, new ResultCallback<BaseWechatPayResult>() {
				@Override
				public void onResult(BaseWechatPayResult result) {
					progressDialog.disLoading();
					if (result.getResult() == 0) {
						try {
							WechatPayResult2 bean = GsonUtils
									.getSingleBean(result
											.getContent(), WechatPayResult2.class);
							FileLog.log("微信扫码支付结果>" + new Gson().toJson(bean));
							Log.e("微信扫码支付结果>", new Gson().toJson(bean));
							if ("SUCCESS".equals(bean.getResult_code())) {
								String qr_code = bean.getCode_url();
								handler2.postDelayed(queryPayResultRunnable, 2000);
								//如果在PAD上运行POS
								if (store.isPadRunnIng()) {
									paydialog = DialogUtil
											.scanPadDialog(context, payTypeID, qr_code, null, new InterfaceDialog() {
												@Override
												public void onCancle() {
													Log.i("正扫手动下单：", "");
													handler2.removeCallbacks(queryPayResultRunnable);
												}

												@Override
												public void onOk(Object o) {
													Log.i("正扫重试：", "");
												}
											});
									payDialogDismissLis();
								} else {
									Bitmap bitmap = null;
									Bitmap qrcode = CreateImage
											.creatQRImage(qr_code, bitmap, 100, 100);
									//7寸屏
									if (SunmiSecondScreen
											.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
										SunmiSecondScreen
												.saveWxOrAliQcode(qrcode, payTypeID == 1 ? ToolsUtils
														.returnXMLStr("alipay") : ToolsUtils
														.returnXMLStr("wechat"), "￥" + total_money
														.setScale(2, BigDecimal.ROUND_DOWN)
														.toString());
									} else if (SunmiSecondScreen
											.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
										SunmiSecondScreen.showImgExcel(qrcode, printOrder);
									}

									paydialog = DialogUtil
											.scanDialog(context, payTypeID, new DialogCallBack() {

												@Override
												public void onOk() {//等待超时重试
													Log.i("正扫超时：", "等待超时重试");
												}

												@Override
												public void onCancle() {//等待超时并且客人已支付成功，手动下单
													Log.i("正扫超时：", "等待超时手动下单");
												}
											});
									payDialogDismissLis();
								}
							} else {
								EventBus.getDefault()
										.post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
								hideDialog();
								showToast(ToolsUtils
										.returnXMLStr("creat_qrcode_failure") + "," + bean
										.getReturn_msg() + "!");
								Log.e("正扫", "生成二维码失败");
								//                                mView.returnWechatSaoMaResult(new SelfPosPayResult(PayResultType.PAY_FAIL, PayResultMsg.PAY_FAIL, bean
								//                                        .getReturn_msg()));
							}
						} catch (Exception e) {
							//                            mView.returnWechatSaoMaResult(new SelfPosPayResult(PayResultType.PAY_ERROR, PayResultMsg.PAY_ERROR));
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onError(PosServiceException e) {

				}
			});


			//            if(wftPayType == 0 || wftPayType == 1)
			//            {
			//                model.pay_type = wftPayType;
			//                createWtfZsQrCode();
			//            }
			//            else{
			//                task.execute(model);
			//            }
		} else {
			//调用扫码枪
			scanGunDialog();
		}
	}

	// 反扫支付(扫码枪扫完后调用这个方法)
	private void outPay(String code, boolean isQuery) {
		//        progressDialog.showLoading("正在支付");
		String      storeName = Store.getInstance(context).getStoreName();
		PayReqModel model     = new PayReqModel();
		outTask = new OutTask();
		model.totalAmount = total_money.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		model.orderNo = orderId;
		model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("product_details") : storeName;
		model.isDebug = isDebug;
		model.payType = pay_channel;
		model.authCode = code;
		model.aliGoodsItem = aliGoodsItem;
		model.isQuery = isQuery;
		model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("food_consumption") : storeName;
		model.storeId = Store.getInstance(context).getStoreId();
		model.terminalId = Store.getInstance(context).getDeviceName();
		//        outTask.execute(model);
	}

	// 反扫支付(扫码枪扫完后调用这个方法)
	private void outPay2(String code, Long orderNewId, boolean isQuery) {
		progressDialog.showLoading(ToolsUtils.returnXMLStr("being_paid"));
		String      storeName = Store.getInstance(context).getStoreName();
		PayReqModel model     = new PayReqModel();
		model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		model.orderNo = orderId;
		model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("product_details") : storeName;
		model.isDebug = isDebug;
		model.payType = pay_channel;
		model.authCode = code;
		model.aliGoodsItem = aliGoodsItem;
		model.isQuery = isQuery;
		model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("food_consumption") : storeName;
		model.storeId = Store.getInstance(context).getStoreId();
		model.terminalId = Store.getInstance(context).getDeviceName();

		mOrderService.wechatPay(model, new ResultCallback<BaseWechatPayResult>() {
			@Override
			public void onResult(BaseWechatPayResult result) {
				if (result.getResult() == 0) {
					String content = result.getContent();
					FileLog.log(content);
					try {
						WechatPayResult bean = GsonUtils
								.getSingleBean(content, WechatPayResult.class);
						if ("SUCCESS".equals(bean.getResult_code())) {
							FileLog.log(content);
							//									mView.returnWechatShuaKaResult(new SelfPosPayResult(PayResultType.PAY_SUCCESS, PayResultMsg.PAY_SUCCESS));
						} else if ("FAIL".equals(bean.getResult_code()) && "USERPAYING"
								.equals(bean.getErr_code())) {
							//									mView.returnWechatShuaKaResult(new SelfPosPayResult(PayResultType.USER_PAYING, PayResultMsg.USER_PAYING, bean
							//											.getErr_code_des()));
						} else if ("FAIL".equals(bean.getResult_code())) {
							//									mView.returnWechatShuaKaResult(new SelfPosPayResult(PayResultType.PAY_FAIL, PayResultMsg.PAY_FAIL, bean
							//											.getErr_code_des()));
						} else {
							//									mView.returnWechatShuaKaResult(new SelfPosPayResult(PayResultType.PAY_UNKNOW, PayResultMsg.PAY_UNKNOW));
						}
					} catch (Exception e) {
						//								mView.returnWechatShuaKaResult(new SelfPosPayResult(PayResultType.PAY_ERROR, PayResultMsg.PAY_ERROR));
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onError(PosServiceException e) {

			}
		});
	}

	/**
	 * 正扫(生成二维码)
	 *
	 * @author aqw
	 */
	//	class InTask extends EPayTask {
	//		@Override
	//		protected void onPostExecute(EPayResult result) {
	//			progressDialog.disLoading();
	//			if (result != null && !result.success) {
	//				EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
	//				hideDialog();
	//				showToast(ToolsUtils.returnXMLStr("creat_qrcode_failure") + "," + result
	//						.getErrMessage() + "!");
	//				Log.e("正扫", "生成二维码失败");
	//			} else if (EPayResult.PAY_STATUS_COMPLETE
	//					.equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
	//				showToast(ToolsUtils.returnXMLStr("pay_success"));
	//				paymentNo = orderId;
	//				transactionNo = result.trade_no;
	//				//                paymentNo = result.trade_no;
	//				hideDialog();
	//				creatOrdAndCheckOut();
	//			} else if (result != null && result.success) {// 微信支付成功
	//				showToast(ToolsUtils.returnXMLStr("pay_success"));
	//				paymentNo = result.transaction_id;
	//				transactionNo = result.transaction_id;
	//				hideDialog();
	//				creatOrdAndCheckOut();
	//			} else {
	//				if (pay_channel != PayReqModel.PTID_SSS_WEIXIN) {
	//					showToast(ToolsUtils.returnXMLStr("pay_failure"));
	//					Log.e("正扫", "支付失败");
	//					exceptMethod(FAIL_PAY);
	//				}
	//			}
	//
	//		}

	//	}

	/**
	 * 反扫
	 *
	 * @author aqw
	 */
	class OutTask extends MicropayTask {
		@Override
		protected void onPostExecute(EPayResult result) {
			progressDialog.disLoading();
			//            showToast(result.code);
			Log.i("weifutongEPayResult", ToolsUtils.getPrinterSth(result));
			if (result != null && !result.success) {
				EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
				hideDialog();
				//                showToast(ToolsUtils.returnXMLStr("pay_failure")+"," + result.getErrMessage() + "!");
				scaning = false;
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE));
				Log.e("反扫", "扫描二维码失败");
			} else if (EPayResult.PAY_STATUS_COMPLETE
					.equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
				showToast(ToolsUtils.returnXMLStr("pay_success"));
				paymentNo = orderId;
				transactionNo = result.trade_no;
				//                paymentNo = result.trade_no;
				hideDialog();
				creatOrdAndCheckOut();
			} else if (result != null && result.success && !result.WFT) {// 微信支付成功
				showToast(ToolsUtils.returnXMLStr("pay_success"));
				paymentNo = result.transaction_id;
				transactionNo = result.transaction_id;
				hideDialog();
				creatOrdAndCheckOut();
			} else if (result != null && result.weifutongPayStart)// 威富通成功
			{
				showToast(ToolsUtils.returnXMLStr("auth_code_success"));
				paymentNo = wft_transaction_id;
				transactionNo = wft_transaction_id;
				hideDialog();
				totalFee = total_money.multiply(new BigDecimal("100")).intValue();
				orderTradeNo = ToolsUtils.completionOrderId(orderId) + "_" + TimeUtil
						.getTimeToken();
				autoCode = result.code;
				getWayWeiFuTong(autoCode, ToolsUtils
						.handleCarDish(cart.getDishItemList()), totalFee, orderTradeNo);
			} else {
				if (pay_channel != PayReqModel.PTID_SSS_WEIXIN) {
					showToast(ToolsUtils.returnXMLStr("pay_failure"));
					Log.e("反扫", "支付失败");
					exceptMethod(FAIL_PAY);
				}
			}
			sb.setLength(0);
		}
	}

	/**
	 * 反扫等待框
	 */
	private void scanGunDialog() {
		//展示总金额到副屏
		if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
			SunmiSecondScreen.getInstance(context)
					.sendData4DSD(payTypeID == 1 ? ToolsUtils.returnXMLStr("alipay") : ToolsUtils
							.returnXMLStr("wechat"), total_money.setScale(2, BigDecimal.ROUND_DOWN)
							.toString());
		} else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
			SunmiSecondScreen.getInstance(context).showDishImgExcel(printOrder);
		}

		paydialog = DialogUtil
				.scanGunDialog(context, payTypeID, outTask, mScanGunKeyEventHelper, new DialogCallBack() {

					@Override
					public void onOk() {//等待超时重试
						Log.i("反扫超时：", "等待超时重试");
						//                if (!TextUtils.isEmpty(code_scan)) {
						//                    scanRetry();
						//                }
					}

					@Override
					public void onCancle() {//等待超时并且客人已支付成功，手动下单
						Log.i("反扫超时：", "等待超时手动下单");
						//                creatOrdAndCheckOut();
					}
				});
		payDialogDismissLis();

	}

	/**
	 * 反扫重试
	 */
	private void scanRetry() {
		outPay(code_scan, true);
		scanGunDialog();
	}

	//隐藏dialog
	private void hideDialog() {
		if (paydialog != null && paydialog.isShowing()) {
			paydialog.dismiss();
		}
	}

	/**
	 * 支付或下单失败弹框
	 *
	 * @param t
	 */
	private boolean isShowFailDialog = false;

	private void exceptMethod(final int t) {
		hideDialog();

		if (failDialog == null || !failDialog.isShowing() && !isShowFailDialog) {
			failDialog = DialogUtil
					.createDialog(context, R.layout.dialog_payfail, 4, LinearLayout.LayoutParams.WRAP_CONTENT, true);
			isShowFailDialog = true;
		}

		TextView msg   = (TextView) failDialog.findViewById(R.id.msg);
		TextView retry = (TextView) failDialog.findViewById(R.id.retry);
		TextView creat = (TextView) failDialog.findViewById(R.id.creat);

		retry.setText(ToolsUtils.returnXMLStr("common_cancel"));
		creat.setVisibility(View.GONE);

		failDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				isShowFailDialog = false;
				if (outTask != null)
					outTask.cancel(true);
			}
		});

		switch (t) {
			case FAIL_PAY://支付失败
				//                creat.setVisibility(View.VISIBLE);
				msg.setText(ToolsUtils.returnXMLStr("pay_failure_or_timeout_please_try_again"));
				if (outTask != null)
					outTask.cancel(true);
				Log.i("支付失败", "点击重试");
				break;
			case FAIL_ORDER://下单失败
				msg.setText(ToolsUtils.returnXMLStr("checkout_faliure_please_try_again"));
				break;
			case FAIL_LKL_PAY://拉卡拉支付失败
				creat.setVisibility(View.VISIBLE);
				msg.setText("拉卡拉支付失败或超时，请取消重试");
				break;
			case FAIL_WFT_PAY://威富通支付失败
				creat.setVisibility(View.VISIBLE);
				msg.setText(ToolsUtils.returnXMLStr("wft_pay_failure"));
				break;
		}

		retry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				failDialog.dismiss();
				switch (t) {
					case FAIL_PAY://支付失败
						//                        if (task != null)
						//                            task.cancel(true);
						//                        if (outTask != null)
						//                            outTask.cancel(true);
						//                        Log.i("支付失败", "点击重试");

						//                        if (Store.getInstance(context).isFront()) {
						//                            scanLogic();
						//                        } else {
						//                            scanRetry();
						//                        }

						break;
					case FAIL_ORDER://下单失败
						//                        Log.i("下单失败", "点击重试");
						//                        creatOrdAndCheckOut();
						break;
					case FAIL_LKL_PAY://拉卡拉支付失败
						//                        Log.i("拉卡拉支付失败", "点击重试");
						//                        creatPaymaxCharge();//交易失败后重新获取支付信息
						break;
					case FAIL_WFT_PAY:
						Log.i("威富通支付失败", "点击取消");
						//                        if(!TextUtils.isEmpty(autoCode))
						//                        {
						//                            getWayWeiFuTong(autoCode, ToolsUtils.handleCarDish(cart.getDishItemList()), totalFee, orderTradeNo);//交易失败后重新获取支付信息
						//                        }
						break;
				}
			}
		});

		//        //出现查询支付状态超时时，手动点击下单
		//        creat.setOnClickListener(new View.OnClickListener() {
		//            @Override
		//            public void onClick(View v) {
		//                failDialog.dismiss();
		//                if (task != null)
		//                    task.cancel(true);
		//                if (outTask != null)
		//                    outTask.cancel(true);
		//                timerCancel();
		//                wftTimerCancel();
		//                creatOrdAndCheckOut();
		//                Log.i("支付失败", "手动下单");
		//            }
		//        });

	}

	private void cancelFailDialog() {
		if (failDialog != null && failDialog.isShowing()) {
			failDialog.dismiss();
		}
	}

	/**
	 * 检测菜品沽清状况
	 *
	 * @param dishs
	 * @param callback callback.haveStock(); 有库存   callback.noStock();无库存
	 */
	public void getDishStock(List<Dish> dishs, final DishCheckCallback callback) {
		progressDialog.showLoading("");
		DishService dishService = null;
		try {
			dishService = DishService.getInstance();
		} catch (PosServiceException e) {
			e.printStackTrace();
			return;
		}
		List<DishCount> dishCountList = new ArrayList<>();
		int             size          = dishs.size();
		for (int i = 0; i < size; i++) {
			Dish dish = dishs.get(i);

			DishCount count = new DishCount();
			count.setDishid(dish.getDishId());
			count.setCount(dish.quantity);
			dishCountList.add(count);

			if (dish.isPackage()) {
				for (Dish.Package aPackage : dish.subItemList) {
					DishCount dc = new DishCount();
					dc.setDishid(aPackage.getDishId());
					dc.setCount(aPackage.quantity * dish.quantity);
					dishCountList.add(dc);
				}
			}

		}
		dishService.checkDishCount(dishCountList, new ResultCallback<List<DishCount>>() {
			@Override
			public void onResult(List<DishCount> result) {
				progressDialog.disLoading();
				if (result == null || result.size() <= 0) {
					callback.haveStock();
				} else {
					callback.noStock(result);
				}
			}

			@Override
			public void onError(PosServiceException e) {
				showToast(e.getMessage());
				progressDialog.disLoading();
			}
		});
	}

	public void directlyCreateOrder(String tableName, BigDecimal money) {
		this.tableName = tableName;
		this.payment.setName(ToolsUtils.returnXMLStr("sth_money"));
		this.payment.setId(0);//将前面快捷支付的支付方式的Id设为现金
		//7寸屏
		if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
			SunmiSecondScreen.getInstance(context)
					.sendData4DSD(payment.getName() + ToolsUtils.returnXMLStr("pays"), total_money
							.setScale(2, BigDecimal.ROUND_DOWN).toString());
		} else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
			SunmiSecondScreen.getInstance(context).showDishImgExcel(printOrder);
		}
		//        printMoney = money;
		printMoney = ToolsUtils.wipeZeroMoney(money);
		//付够现金或者是免单则可下单
		if (isCanCreate() || posInfo.isFreeOrder()) {
			//调用结账
			creatOrdAndCheckOut();
			PrinterDataController.getInstance().openCashBox();
		}
	}

	public void refreshDish(List<DishCount> result, List<Dish> dishs) {
		//刷新菜品数据,显示沽清
		String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
		MyApplication.getInstance().ShowToast(ToolsUtils
				.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names
				+ "。\n\n" + ToolsUtils.returnXMLStr("please_re_order"));
		if (progressDialog != null) {
			progressDialog.disLoading();
		}
		Log.i("以下菜品份数不足:", names + "====<<");
	}


	public void swichPay(final String tableName) {
		if (ToolsUtils.isList(Cart.getDishItemList())) {
			isDisCount = true;
		} else {
			if (!isDisCount) {
				final CheckOutUtil checUtil = new CheckOutUtil(context, payment);
				checUtil.getDishStock(Cart.getDishItemList(), new DishCheckCallback() {
					@Override
					public void haveStock() {
						payOrder(tableName);
					}

					@Override
					public void noStock(List dataList) {
						refreshDish(dataList, Cart.getDishItemList());
					}
				});
			}
		}
		if (isDisCount) {
			payOrder(tableName);
		}
	}

	private void payOrder(String tableName) {
		//现金类型支付方式
		this.tableName = tableName;
		if (payment.getCategory() == PaymentCategory.CASH) {
			if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
				//7寸屏
				if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
					SunmiSecondScreen.getInstance(context)
							.sendData4DSD(payment.getName() + ToolsUtils
									.returnXMLStr("pays"), total_money
									.setScale(2, BigDecimal.ROUND_DOWN).toString());
				} else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
					SunmiSecondScreen.getInstance(context).showDishImgExcel(printOrder);
				}
				PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
					@Override
					public void onOk(Object o) {
						printMoney = (BigDecimal) o;

						if (isCanCreate()) {
							if (payment.getId() == 0) {
								PrinterDataController.getInstance().openCashBox();
							}
							//调用结账
							creatOrdAndCheckOut();
						} else {
							if (logicIsAnyCheckout(payment)) {
								anyCheckoutPayMent = payment;
								//调用结账
								creatOrdAndCheckOut();
							} else {
								showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
							}
						}
					}
				}, total_money, true);
			} else {
				showToast(ToolsUtils.returnXMLStr("please_click_dish"));
			}
			return;
		}
		switch (payTypeID) {
			case 1://支付宝
				if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
					printMoney = total_money;
					isCanCreate();
					pay_channel = PayReqModel.PTID_SSS_ALI;
					scanLogic2();
				} else {
					showToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}
				break;
			case 2://微信
				if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
					printMoney = total_money;
					isCanCreate();
					pay_channel = PayReqModel.PTID_SSS_WEIXIN;
					//					scanLogic();
					scanLogic2();
				} else {
					showToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}
				break;
			case -8://威富通支付
				if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
					printMoney = total_money;
					isCanCreate();
					pay_channel = PayReqModel.PTID_SSS_WEIFUTONG;
					DialogUtil.switchWftPay(context, new KeyCallBack() {
						@Override
						public void onOk(Object o) {
							wftPayType = (int) o;
							scanLogic2();
						}
					});
				} else {
					showToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}
				break;
			//挂账
			case -34:
				logicChargeOffs();
				break;
			case 3:
			case 4:
			case 5://储值、积分、优惠券
				//                if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
				memberDialog = DialogUtil.memberDialog(context, payTypeID, total_money, printOrder
						.getItemList(), true, new CreatDealBack() {
					@Override
					public void onDeal(String bizid, WshDealPreview result, BigDecimal money, boolean isCheckOut, Account account, List<Payment> memberPayMents) {//money为消费会员金额,bizid是业务流水号，提交交易时使用
						printMoney = money;
						memberPayMent = ToolsUtils.cloneTo(memberPayMents);
						posInfo.setMemberCheckOut(false);
						bizId = bizid;
						posInfo.setBizId(bizid);
						if (account != null) {
							accountMember = ToolsUtils.cloneTo(account);
							if (isCheckOut) {
								accountMember.setMemberConsumeCost(money);
							}
						}
						if (!isCheckOut) {
							posInfo.setMemberCheckOut(true);
							posInfo.setBizId(bizid);
							posInfo.setWshDealPreview(result);
						} else {
							//                            posInfo.setBizId("");
							posInfo.setWshDealPreview(null);
							if (isCanCreate()) {
								commitDeal(bizid, result, true);
							} else {
								showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
							}
						}
					}
				});
				break;
			case -32://美团
				if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
					DialogUtil.meiTuanDialog(context, posInfo
							.getOrderId(), total_money, true, null, new DialogMTCallback() {
						@Override
						public void onCheckout(BigDecimal money, boolean isCheckOut, CopyOnWriteArrayList<ValidationResponse> addValidationLists) {
							printMoney = money;
							addValidationList = ToolsUtils.cloneTo(addValidationLists);
							creatOrdAndCheckOut();
						}
					});
				} else {
					showToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}
				break;
			case 7://银行卡
				PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
					@Override
					public void onOk(Object o) {
						printMoney = (BigDecimal) o;
						if (isCanCreate()) {
							creatOrdAndCheckOut();
						} else {
							showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
						}
					}
				}, total_money, true);
				break;

			case -10://储值卡
				if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
					printMoney = total_money;
					DialogUtil.showMemberDialog(context, printMoney, new DialogCall() {
						@Override
						public void onOk(Object obj) {
							if (((String) obj).equals("Success")) {
								isCanCreate();
								showToast(ToolsUtils.returnXMLStr("pay_success"));
								paymentNo = orderId;
								transactionNo = orderId;
								creatOrdAndCheckOut();
							}
						}

						@Override
						public void onCancle(Object obj) {

						}
					});
				} else {
					showToast(ToolsUtils.returnXMLStr("please_click_dish"));
				}
				break;
			default:
				showToast(ToolsUtils.returnXMLStr("this_method_is_not_supported"));
				break;
		}
	}

	/**
	 * 挂账
	 */
	private void logicChargeOffs() {
		//判断是否有退菜权限
		PowerController
				.isLogicPower(context, PowerController.REFUND_DISH, new PermissionCallback() {
					@Override
					public void havePermission() {
						DialogUtil.cardRecordDialog(context, new DialogTCallback() {
							@Override
							public void onConfirm(Object o) {
								cardRecord = (CardRecord) o;
								if (cardRecord != null) {
									printMoney = total_money;
									creatOrdAndCheckOut();
								}
							}

							@Override
							public void onCancle() {

							}
						});
					}

					@Override
					public void withOutPermission() {

					}
				});
	}

	//    /**
	//     * 挂单
	//     */
	//    private void chargeOffs()
	//    {
	//        try {
	//            OrderService orderService = OrderService.getInstance();
	//            orderService.chargeOffs(posInfo.getOrderId(), true, new ResultCallback<Integer>() {
	//                @Override
	//                public void onResult(Integer result) {
	//                    if(result == 0)
	//                    {
	//                        printMoney = total_money;
	//                        creatOrdAndCheckOut();
	//                    }
	//                    else{
	//                        showToast(ToolsUtils.returnXMLStr("cardrecord_error") +"!");
	//                        Log.e("挂单失败", "");
	//                    }
	//                }
	//
	//                @Override
	//                public void onError(PosServiceException e) {
	//                    showToast(ToolsUtils.returnXMLStr("cardrecord_error") + e.getMessage()+ "!");
	//                    Log.e("挂单失败", e.getMessage());
	//                }
	//            });
	//        } catch (PosServiceException e) {
	//            e.printStackTrace();
	//        }
	//    }


	private boolean isCanCreate() {
		if (printMoney.compareTo(total_money) == 0 || printMoney.compareTo(total_money) == 1) {
			printOrder.setPay_money(printMoney);
			printOrder.setGive_money(total_money.subtract(printMoney));
			return true;
		}
		return false;
	}



	/**
	 * 提交会员交易
	 */
	private void commitDeal(final String biz_id, final WshDealPreview wshDealPreview, final boolean isCheckOut) {
		try {
			boolean isNotVerity = true;//不需要任何验证

			final Dialog smsDialog = DialogUtil
					.createDialog(context, R.layout.dialog_vertify, 3, LinearLayout.LayoutParams.WRAP_CONTENT, false);
			TextView             title = (TextView) smsDialog.findViewById(R.id.title);
			final CommonEditText code  = (CommonEditText) smsDialog.findViewById(R.id.code);
			TextView dialog_cancle = (TextView) smsDialog
					.findViewById(R.id.dialog_cancle);
			TextView dialog_ok = (TextView) smsDialog.findViewById(R.id.dialog_ok);

			dialog_cancle.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					smsDialog.dismiss();
				}
			});

			dialog_ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final CheckOutUtil checUtil = new CheckOutUtil(context, payment);
					checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
						@Override
						public void haveStock() {
							commitPay(smsDialog, code, biz_id, isCheckOut);
						}

						@Override
						public void noStock(List dataList) {
							refreshDish(dataList, getDishItemList());
						}
					});
				}
			});

			if (wshDealPreview.isVerify_sms()) {//是否需要短信验证
				isNotVerity = false;
				title.setText(ToolsUtils.returnXMLStr("SMS_verification"));
				code.setHint(ToolsUtils.returnXMLStr("please_input_SMS_verification"));
				smsDialog.show();
			}
			if (wshDealPreview.isVerify_password()) {//是否需要交易密码
				isNotVerity = false;
				title.setText(ToolsUtils.returnXMLStr("transaction_password_verification"));
				code.setHint(ToolsUtils
						.returnXMLStr("please_input_transaction_password_verification"));
				smsDialog.show();
			}

			code.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event
							.getAction()) {
						if (!TextUtils.isEmpty(code.getText().toString().trim())) {
							final String num = code.getText().toString().trim();
							if (TextUtils.isEmpty(num)) {
								if (wshDealPreview.isVerify_sms()) {
									MyApplication.getInstance().ShowToast(ToolsUtils
											.returnXMLStr("please_input_SMS_verification"));
								}
								if (wshDealPreview.isVerify_password()) {
									MyApplication.getInstance().ShowToast(ToolsUtils
											.returnXMLStr("please_input_transaction_password_verification"));
								}
							} else {
								WindowUtil.hiddenKey();
								final CheckOutUtil checUtil = new CheckOutUtil(context, payment);
								checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
									@Override
									public void haveStock() {
										commitPay(smsDialog, code, biz_id, isCheckOut);
									}

									@Override
									public void noStock(List dataList) {
										refreshDish(dataList, getDishItemList());
									}
								});
							}
						}
						return true;
					}
					return false;
				}
			});

			if (isNotVerity) {//不需要任何验证
				progressDialog.showLoading("");
				wshService.commitDeal(biz_id, "", new ResultCallback<PosResponse>() {
					@Override
					public void onResult(PosResponse result) {
						progressDialog.disLoading();
						if (result.getResult() == 0) {//提交成功
							posInfo.setMemberCheckOut(false);
							//                            posInfo.setBizId("");
							//                            posInfo.setWshDealPreview(null);
							if (isCheckOut) {
								creatOrdAndCheckOut();
							}
						} else {
							showToast(result.getErrmsg());
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						showToast(e.getMessage());
					}
				});
			}

		} catch (Exception e) {
			progressDialog.disLoading();
			e.printStackTrace();
		}
	}

	private void commitPay(final Dialog smsDialog, CommonEditText code, String biz_id, final boolean isCheckOut) {
		String sms = code.getText().toString();
		if (TextUtils.isEmpty(sms)) {
			showToast(code.getHint() + "");
			return;
		}
		progressDialog.showLoading("");
		wshService.commitDeal(biz_id, sms, new ResultCallback<PosResponse>() {
			@Override
			public void onResult(PosResponse result) {
				progressDialog.disLoading();

				if (result.getResult() == 0) {//提交成功
					smsDialog.dismiss();
					posInfo.setMemberCheckOut(false);
					//                    posInfo.setBizId("");
					//                    posInfo.setWshDealPreview(null);
					if (isCheckOut) {
						creatOrdAndCheckOut();
					}
				} else {
					showToast(result.getErrmsg());
				}
			}

			@Override
			public void onError(PosServiceException e) {
				progressDialog.disLoading();
				showToast(e.getMessage());
			}
		});
	}

	private void creatDeal(Account accountMember) {
		progressDialog.showLoading("");
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
		request.setCno(accountMember.getUno()); //卡号
		request.setUid(accountMember.getUid());
		request.setPayment_amount(total_money.multiply(new BigDecimal("100")).intValue());
		request.setConsume_amount(total_money.multiply(new BigDecimal("100")).intValue());
		try {
			WshService wshService = WshService.getInstance();
			wshService.createDeal(request, new ResultCallback<WshDealPreview>() {
				@Override
				public void onResult(WshDealPreview result) {
					progressDialog.disLoading();
					commitDeal(posInfo.getBizId(), posInfo.getWshDealPreview(), true);
				}

				@Override
				public void onError(PosServiceException e) {
					progressDialog.disLoading();
					MyApplication.getInstance().ShowToast(ToolsUtils
							.returnXMLStr("submission_of_member_transaction_failed") + "," + e
							.getMessage() + "!");
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 先支付后下单：下单接口同时处理结账
	 */
	private long lastClickTime = 0;

	public void creatOrdAndCheckOut() {
		ToolsUtils.writeUserOperationRecords("TIME===>调用下单接口==" + TimeUtil
				.getStringTimeLong(System.currentTimeMillis()));
		if (posInfo.isMemberCheckOut()) {
			creatDeal(posInfo.getAccountMember());
		} else {
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime - lastClickTime <= Constant.MIN_CLICK_DELAY_TIME) {
				lastClickTime = currentTime;
				return;
			}
			progressDialog.showLoading(ToolsUtils.returnXMLStr("is_placing_an_order"));

			//是挂单
			if (payTypeID == -34) {
				if (cardRecord != null) {
					printOrder.setCardRecord(cardRecord);
				}
			}
			printOrder.setPaymentStatus(PaymentStatus.PAYED);
			printOrder.setPaymentList(getPaymentList());
			printOrder.setPaidAt(System.currentTimeMillis());
			//免单
			if (posInfo.isFreeOrder()) {
				printOrder.setCost("0");
				printOrder.setAvtive_money(new BigDecimal(cart.getPriceSum())
						.setScale(2, BigDecimal.ROUND_DOWN));
				printOrder.setTake_money(new BigDecimal("0.00"));
			} else {
				printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN).toString());
				printOrder.setAvtive_money(new BigDecimal(cart.getPriceSum() - cart.getCost())
						.setScale(2, BigDecimal.ROUND_DOWN));
				printOrder.setTake_money(cart.getTakeMoney());
				if (anyCheckoutPayMent != null) {
					printOrder.setCost(printMoney.setScale(2, BigDecimal.ROUND_DOWN)
							.toString());//注意,因为是允许输入任意金额支付所以这里需要把cost改为任意输入的金额
				}
			}
			printOrder.setPaymentNo(paymentNo);
			printOrder.setTableNames(tableName);
			printOrder.setWipingValue(wipingValue);
			if (store.isWaiMaiGuestInfo() && posInfo.getOrderType().equals("SALE_OUT") && posInfo
					.getCustomer() != null) {
				Customer customer = posInfo.getCustomer();
				if (customer != null) {
					printOrder.setCustomerAddress(customer.getCustomerAddress());
					printOrder.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
					printOrder.setCustomerName(customer.getCustomerName());
					printOrder.setOuterOrderid(customer.getCustomerOuterOrderId());
					printOrder.setSource(ToolsUtils.returnXMLStr("manual_input"));//手动录入标识
				}
			}

			if (accountMember != null) {
				accountMember.setMemberConsumeCost(new BigDecimal(printOrder.getCost()));
				printOrder.setAccountMember(accountMember);
				printOrder.setMemberBizId(posInfo.getBizId());
				printOrder.setMemberGrade(accountMember.getGradeName());
				printOrder.setMemberid(accountMember.getUno());
				printOrder.setMemberName(accountMember.getName());
				printOrder.setMemberPhoneNumber(accountMember.getPhone());
			} else if (posInfo.getAccountMember() != null) {
				accountMember = ToolsUtils.cloneTo(posInfo.getAccountMember());
				accountMember.setMemberConsumeCost(new BigDecimal(printOrder.getCost()));
				printOrder.setMemberBizId(posInfo.getBizId());
				printOrder.setMemberGrade(accountMember.getGradeName());
				printOrder.setAccountMember(accountMember);
				printOrder.setMemberid(accountMember.getUno());
				printOrder.setMemberName(accountMember.getName());
				printOrder.setMemberPhoneNumber(accountMember.getPhone());
			}
			printOrder.setCustomerAmount(posInfo.getCustomerAmount());
			printOrder.setComment(posInfo.getComment());
			final Order newOrder = ToolsUtils.cloneTo(printOrder);
			if (printOrder != null) {
				orderService.createOrder(printOrder, new ResultCallback<Order>() {
					@Override
					public void onResult(Order result) {
						progressDialog.disLoading();
						anyCheckoutPayMent = null;
						if (result != null) {
							if (store.isCreateOrderJyj()) {
								result.setJyjOrder(true);//将下单状态改为向JYJ下单
								createOrderJyj(result);
							} else {
								orderPrint(newOrder, result);
							}
						} else {
							showToast(ToolsUtils.returnXMLStr("orders_failed"));
							Log.e("下单", "下单失败");
							exceptMethod(FAIL_ORDER);
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.e("下单", "下单失败:" + e.getMessage());
						showToast(ToolsUtils.returnXMLStr("orders_failed") + "," + e.getMessage());
						exceptMethod(FAIL_ORDER);
					}
				});
			}
		}
	}

	private void createOrderJyj(final Order order) {
		progressDialog.showLoading(ToolsUtils.returnXMLStr("is_placing_an_order"));
		final Order newOrder = ToolsUtils.cloneTo(order);
		if (newOrder != null) {
			try {
				OrderService orderService = OrderService.getJyjOrderService();
				orderService.createOrder(newOrder, new ResultCallback<Order>() {
					@Override
					public void onResult(Order result) {
						progressDialog.disLoading();
						if (result != null) {
							orderPrint(result, result);
						} else {
							Log.e("JYJ下单", "JYJ下单失败:" + "null");
							showToast(ToolsUtils.returnXMLStr("orders_failed"));
							order.setJyjPrintErrMessage(ToolsUtils
									.returnXMLStr("jyj_order_error_message"));
							orderPrint(order, order);
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.e("JYJ下单", "JYJ下单失败:" + e.getMessage());
						showToast(ToolsUtils.returnXMLStr("orders_failed") + "," + e.getMessage());
						order.setJyjPrintErrMessage(ToolsUtils
								.returnXMLStr("jyj_order_error_message"));
						orderPrint(order, order);
					}
				});


			} catch (PosServiceException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 打印
	 *
	 * @param newOrder 原本向后台提交下单的order
	 * @param result   下单后服务器返回的order
	 */
	private void orderPrint(final Order newOrder, Order result) {
		ToolsUtils.writeUserOperationRecords("TIME===>调用下单接口==" + TimeUtil
				.getStringTimeLong(System.currentTimeMillis()));

		if (!TextUtils.isEmpty(wft_transaction_id)) {
			Log.i("创建威富通交易成功", "订单ID:" + result.getId() + "==威富通交易流水号:" + wft_transaction_id);
		}
		TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
		Log.i("下单成功", "打印结账单跟厨房单===" + TimeUtil.getStringTimeLong(System.currentTimeMillis()));

		Log.i("快捷结账页面下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
		EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));


		newOrder.setId(result.getId());
		newOrder.setCallNumber(result.getCallNumber());
		newOrder.setCustomerAmount(newOrder.getCustomerAmount());
		if (!TextUtils.isEmpty(newOrder.getTableNames())) {
			newOrder.setTableNames(newOrder.getTableNames());
		}

		//下单成功取消免单
		posInfo.setFreeOrder(false);
		EventBus.getDefault()
				.post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, tableName));

		EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, newOrder));

		new Handler().postDelayed(new Runnable() {
			public void run() {
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, newOrder));
			}
		}, 3000);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, newOrder));
			}
		}, 3000);
	}

	/**
	 * KDS创建订单
	 */
	private String tableName;

	private List<PaymentList> getPaymentList() {
		PosInfo           posInfo      = PosInfo.getInstance();
		List<PaymentList> paymentLists = new ArrayList<>();
		boolean           isHaveMber   = false;
		boolean           isHaveMt     = false;
		if (memberPayMent != null && memberPayMent.size() > 0) {
			int memberListSize = memberPayMent.size();
			for (int i = 0; i < memberListSize; i++) {
				isHaveMber = true;
				Payment memberPay = memberPayMent.get(i);
				if (memberPay != null) {
					if (memberPay.getDeductibleAmount() > 0) {
						PaymentList checkModle = new PaymentList();
						checkModle.setAppId(posInfo.getAppId());
						checkModle.setBrandId(posInfo.getBrandId());
						checkModle.setStoreId(posInfo.getStoreId());
						checkModle.setOrderId(orderId);
						checkModle.setPaymentTypeId(memberPay.getId());
						checkModle.setCreatedAt(System.currentTimeMillis());
						checkModle.setPaidAt(System.currentTimeMillis());
						if (memberPay.getId() == 3 || memberPay.getId() == 4 || memberPay
								.getId() == 5) {
							checkModle.setPaymentNo(bizId);
						}
						checkModle.setValue(new BigDecimal(memberPay.getDeductibleAmount())
								.setScale(2, BigDecimal.ROUND_HALF_UP));
						checkModle.setOperation("PAY");
						checkModle.setPayName(memberPay.getName());
						paymentLists.add(checkModle);
					}
				}
			}
		}
		if (addValidationList != null && addValidationList.size() > 0) {
			int validationSize = addValidationList.size();
			for (int i = 0; i < validationSize; i++) {
				isHaveMt = true;
				ValidationResponse validation = addValidationList.get(i);
				if (validation != null) {
					if (validation.isSuccess() && validation.getDealValue() > 0) {
						PaymentList checkModle = new PaymentList();
						checkModle.setAppId(posInfo.getAppId());
						checkModle.setBrandId(posInfo.getBrandId());
						checkModle.setStoreId(posInfo.getStoreId());
						checkModle.setOrderId(orderId);
						checkModle.setPaymentTypeId(payment.getId());
						checkModle.setCreatedAt(System.currentTimeMillis());
						checkModle.setPaidAt(System.currentTimeMillis());
						checkModle.setPaymentNo(validation.getCouponCode());
						checkModle.setValue(new BigDecimal(validation.getDealValue())
								.setScale(2, BigDecimal.ROUND_HALF_UP));
						checkModle.setOperation("PAY");
						checkModle.setPayName(payment.getName());
						paymentLists.add(checkModle);
					}
				}
			}
		}
		if (!isHaveMber && !isHaveMt) {
			PaymentList checkModle = new PaymentList();
			checkModle.setAppId(posInfo.getAppId());
			checkModle.setBrandId(posInfo.getBrandId());
			checkModle.setStoreId(posInfo.getStoreId());
			checkModle.setOrderId(orderId);
			checkModle.setPaymentTypeId(payment.getId());
			checkModle.setCreatedAt(System.currentTimeMillis());
			checkModle.setPaidAt(System.currentTimeMillis());
			//        //支付宝 //微信
			//        if(payment.getId() == 1 || payment.getId() == 2)
			//        {
			//            checkModle.setPaymentNo(paymentNo);
			//        }
			if (payment.getId() == 3 || payment.getId() == 4 || payment.getId() == 5) {
				checkModle.setPaymentNo(bizId);
			} else if (payment.getId() == -8) {
				if (wftPayType == 0) {
					checkModle.setSource(ToolsUtils.returnXMLStr("wxPay"));
				} else if (wftPayType == 1) {
					checkModle.setSource(ToolsUtils.returnXMLStr("sth_zfb"));
				}
				checkModle.setPaymentNo(orderTradeNo);
			} else {
				checkModle.setPaymentNo(orderId);
			}
			if (!TextUtils.isEmpty(transactionNo)) {
				checkModle.setTransactionNo(transactionNo);
			}
			checkModle.setValue(total_money);
			checkModle.setOperation("PAY");
			checkModle.setPayName(payment.getName());
			paymentLists.add(checkModle);
		}
		return paymentLists;
	}

	private StringBuffer sb = new StringBuffer();

	@Override
	public void onScanSuccess(String barcode) {
		Log.e("反扫code返回:", barcode);
		//        System.out.println("===>>"+barcode);
		sb.append(barcode);
		String code = sb.toString().trim();
		if (code.length() == 18) {
			if (!scaning) {
				System.out.println("===>>" + code);
				scaning = true;
				code_scan = code;
				outPay(code_scan, false);
			}
		}
		//        if (!TextUtils.isEmpty(barcode)) {
		//            if (!scaning) {
		//                scaning = true;
		//                code_scan = barcode;
		//                outPay(barcode, false);
		//            }
		//            else {
		//                Log.i("扫码枪返回", "重复扫码");
		//                showToast(ToolsUtils.returnXMLStr("do_not_repeat_the_sweep_code"));
		//            }
		//        }
	}

	/**
	 * 判断是否允许任意金额结账
	 *
	 * @return
	 */
	private boolean logicIsAnyCheckout(Payment payment) {
		//有对应的权限，并且选择的支付方式是不需要检查余额的，且至少有一种支付方式
		if (isAnyCheckOut) {
			if (printMoney != null) {
				if (!payment.isCheckBalance()) {
					if (printMoney.compareTo(BigDecimal.ZERO) >= 0) {
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * 提交拉卡拉支付请求
	 */
	private void creatPaymaxCharge() {
		String storeName = Store.getInstance(context).getStoreName() + "-" + ToolsUtils
				.returnXMLStr("food_consumption");
		String subject = TextUtils.isEmpty(storeName) ? ToolsUtils
				.returnXMLStr("product_details") : storeName;
		try {
			progressDialog.showLoading("");
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.creatPaymaxCharge(printMoney.toString(), subject, ToolsUtils
					.handleCarDish(cart
							.getDishItemList()), orderId, "wechat_csb", "192.168.1.66", "789456", new ResultCallback<LKLResponse>() {
				@Override
				public void onResult(LKLResponse result) {
					progressDialog.disLoading();
					if (result != null && !TextUtils.isEmpty(result.getId()) && result
							.getCredential() != null && result.getCredential()
							.getWechat_csb() != null && !TextUtils
							.isEmpty(result.getCredential().getWechat_csb().getQr_code())) {
						String wxQr_Code = result.getCredential().getWechat_csb()
								.getQr_code();//微信扫码的二维码链接
						String chargeid = result.getId();
						createQrCode(chargeid, wxQr_Code);
					} else {
						showToast("获取拉卡拉支付信息失败!");
						Log.v("获取拉卡拉支付信息失败", ToolsUtils.getPrinterSth(result));
					}
				}

				@Override
				public void onError(PosServiceException e) {
					progressDialog.disLoading();
					showToast("获取拉卡拉支付信息失败," + e.getMessage());
					Log.v("获取拉卡拉支付信息失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	private void createQrCode(String chargeid, String qrCode) {
		if (!TextUtils.isEmpty(qrCode)) {
			Bitmap bitmap = null;
			Bitmap qrcode = CreateImage.creatQRImage(qrCode, bitmap, 100, 100);
			//7寸屏
			if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
				SunmiSecondScreen.saveWxOrAliQcode(qrcode, ToolsUtils
						.returnXMLStr("lkl_pay"), "￥" + total_money
						.setScale(2, BigDecimal.ROUND_DOWN).toString());
			} else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
				SunmiSecondScreen.showImgExcel(qrcode, printOrder);
			}
			cycleLklPay(chargeid);

			paydialog = DialogUtil.scanDialog(context, payTypeID,new DialogCallBack() {

				@Override
				public void onOk() {//等待超时重试
					Log.i("正扫超时：", "等待超时重试");
					creatPaymaxCharge();//等待超时重试后重新扫码支付
				}

				@Override
				public void onCancle() {//等待超时并且客人已支付成功，手动下单
					Log.i("正扫超时：", "等待超时手动下单");
					creatOrdAndCheckOut();
				}
			});
			payDialogDismissLis();
		}
	}

	/**
	 * 轮询拉卡拉交易情况  成功或者失败
	 *
	 * @param chargeid
	 */
	private void cycleLklPay(final String chargeid) {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				// 要做的事情
				retrievePaymax(chargeid);
			}
		};
		lklTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		timer.schedule(lklTask, delayedTime, cycleTime);//延时3秒并且3秒循环一次获取拉卡拉交易情况
	}

	private void retrievePaymax(String chargeid) {
		try {
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService.retrievePaymax(chargeid, new ResultCallback<LKLResponse>() {
				@Override
				public void onResult(LKLResponse result) {
					progressDialog.disLoading();
					if (result != null && result.isReqSuccessFlag()) {
						if (result.getStatus().equals("SUCCEED"))//交易成功
						{
							showToast(ToolsUtils.returnXMLStr("pay_success"));
							paymentNo = orderId;
							timerCancel();
							creatOrdAndCheckOut();
						} else if (result.getStatus().equals("FAILED"))//交易失败
						{
							timerCancel();
							exceptMethod(FAIL_LKL_PAY);
						}
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.v("创建拉卡拉交易失败", e.getMessage());
					creatPaymaxCharge();//交易失败后重新获取支付信息
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	private void timerCancel() {
		hideDialog();
		if (timer != null) {
			timer.cancel();
		}
	}

	private void wftTimerCancel() {
		hideDialog();
		if (timerwft != null) {
			queryWeifuTongCount = 0;
			timerwft.cancel();
			timerwft = null;
		}
	}

	private int queryWeifuTongCount = 0;

	private void cycleWFTPay(final String out_trade_no, final String chargeid) {
		wfthandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				// 要做的事情
				if (queryWeifuTongCount >= 30) {
					wftTimerCancel();
					exceptMethod(FAIL_WFT_PAY);
				} else {
					queryWeifuTongCount += 1;
					queryWeiFuTong(out_trade_no, chargeid);
				}
			}
		};
		wftTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				wfthandler.sendMessage(message);
			}
		};
		timerwft.schedule(wftTask, delayedTime, cycleTime);//延时3秒并且3秒循环一次获取威富通交易情况
	}

	private void getWayWeiFuTong(String auth_code, String body, Integer total_fee, final String out_trade_no) {
		try {
			progressDialog.showLoading(ToolsUtils.returnXMLStr("query_pay_state"));
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService
					.getWayWeiFuTong(auth_code, body, total_fee, out_trade_no, new ResultCallback<WeiFuTongResponse>() {
						@Override
						public void onResult(final WeiFuTongResponse result) {
							new Handler().postDelayed(new Runnable() {
								public void run() {
									progressDialog.disLoading();
									if (result != null) {
										timerwft = new Timer();
										cycleWFTPay(out_trade_no, result.getTransaction_id());
										//                        String transaction_id = result.getTransaction_id();
										//                        if(!TextUtils.isEmpty(result.getTransaction_id()))
										//                        {
										//                            cycleWFTPay(out_trade_no,transaction_id);
										//                        }
										//                        else{
										//                            showToast("获取威富通支付信息失败,"+result.getMessage());
										//                            Log.v("获取威富通支付信息失败", ToolsUtils.getPrinterSth(result));
										//                        }
									}
								}
							}, 2000);
						}

						@Override
						public void onError(PosServiceException e) {
							progressDialog.disLoading();
							EventBus.getDefault()
									.post(new PosEvent(Constant.EventState.ERR_GET_WFT_PAY_STATE));
							//                    showToast(ToolsUtils.returnXMLStr("get_wft_pay_info_failure")+"," + e.getMessage());
							Log.i("获取威富通支付信息失败", e.getMessage());
						}
					});
		} catch (PosServiceException e) {
			e.printStackTrace();
			//            showToast(ToolsUtils.returnXMLStr("get_wft_pay_info_failure"));
			EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_GET_WFT_PAY_STATE));
			Log.i("获取威富通支付信息失败", e.getMessage());
		}
	}

	private void queryWeiFuTong(final String out_trade_no, final String transaction_id) {
		try {
			if (!Store.getInstance(context).isFront()) {
				progressDialog.showLoading("");
			}
			StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
			storeBusinessService
					.queryWeiFuTong(out_trade_no, transaction_id, new ResultCallback<WeiFuTongResponse>() {
						@Override
						public void onResult(WeiFuTongResponse result) {
							if (result != null) {
								String query   = result.getNeed_query();
								String errcode = result.getErrcode();
								if (result.isSuccess()) {
									wftTimerCancel();
									progressDialog.disLoading();
									hideDialog();
									showToast(ToolsUtils.returnXMLStr("pay_success"));
									paymentNo = orderId;
									wft_transaction_id = out_trade_no;
									Log.i("创建威富通交易成功", out_trade_no);
									cancelFailDialog();
									creatOrdAndCheckOut();
								} else if (!result.isSuccess() && "false".equals(query)) {
									wftTimerCancel();
									EventBus.getDefault()
											.post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
									progressDialog.disLoading();
									exceptMethod(FAIL_WFT_PAY);
									showToast(ToolsUtils.returnXMLStr("wft_pay_failure"));
									Log.i("威富通支付失败===>>", out_trade_no);
								}
							}
						}

						@Override
						public void onError(PosServiceException e) {
							progressDialog.disLoading();
							Log.i("创建威富通交易失败", e.getMessage());
							getWayWeiFuTong(autoCode, ToolsUtils.handleCarDish(cart
									.getDishItemList()), totalFee, orderTradeNo);//交易失败后重新获取支付信息
						}
					});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

}
