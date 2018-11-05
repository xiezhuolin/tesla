package cn.acewill.pos.next.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import CommDevice.USBPort;
import aclasdriver.OpScale;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.NetOrderinfoCallBack;
import cn.acewill.pos.next.interfices.WeightCallBack;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;

/**
 * Created by DHH on 2017/6/7.
 */

public class TimerTaskController {
	private static TimerTaskController  instance;
	private        NetOrderinfoCallBack netOrderinfoCallBack;
	private static final String TAG = "TimerTaskController";
	private static Store           store;
	private        MyTimer         timerSyncNetOrder;//轮询网上订单接口
	private        MyTimerTask     taskSyncNetOrder;
	private        Handler         handler;
	private        MyTimer         timerStart;//下单轮训订单数据ordertimer
	private        MyTimerTask     taskStart;
	private        Handler         handlerStart;
	private static ProgressDialogF progressDialog;
	private int cycle       = 10 * 1000;//周期循环时间
	private int delayedTime = 3 * 1000;//延迟2秒
	private static ArrayMap<Long, Order> receiveOrderMap;//已经轮训到的网上订单


	public class MyTimer extends Timer {
		private int id;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}

	public static TimerTaskController getInstance() {
		if (instance == null) {
			instance = new TimerTaskController();
			store = Store.getInstance(MyApplication.getInstance().getContext());
			progressDialog = new ProgressDialogF(MyApplication.getInstance().getContext());
			receiveOrderMap = new ArrayMap<>();
			initOpScale();
		}
		return instance;
	}


	public ArrayMap<Long, Order> getReceiveOrderMap() {
		return receiveOrderMap;
	}

	public void setReceiveOrderList(List<Order> orderList) {
		if (orderList != null && orderList.size() > 0) {
			if (receiveOrderMap != null) {
				int size = orderList.size();
				for (int i = 0; i < size; i++) {
					Order order = orderList.get(i);
					if (order != null && order.getNetOrderState() == 0) {
						receiveOrderMap.put(order.getId(), order);
					}
				}
			}
		}
	}


	/**
	 * 通过网上订单ID修改订单接收状态
	 * 0:为未接收
	 * 1:为已接收并已下单
	 * 2:为已接收但下单出错
	 *
	 * @param netOrderId
	 * @param netOrderState
	 */
	public static void modifyOrderType(Long netOrderId, int netOrderState) {
		if (receiveOrderMap != null && receiveOrderMap.size() > 0) {
			Order order = receiveOrderMap.get(netOrderId);
			if (order != null) {
				if (order.getId() == netOrderId) {
					Log.i(TAG, "网上订单id==》》" + netOrderId + "==修改后网上订单状态==》》" + netOrderState);
					order.setNetOrderState(netOrderState);
				}
			}
		}
	}

	/**
	 * 网上订单是否已经被接收
	 *
	 * @param netOrderId
	 * @return
	 */
	public static boolean isReceiveNetOrder(Long netOrderId) {
		boolean isReceive = false;
		if (receiveOrderMap != null && receiveOrderMap.size() > 0) {
			Order order = receiveOrderMap.get(netOrderId);
			if (order != null && order.getNetOrderState() == 1) {
				isReceive = true;
			}
		}
		return isReceive;
	}

	/**
	 * 判断所要查询的订单状态是否与stateType一致
	 *
	 * @param netOrderId
	 * @param stateType  网上订单的接收状态  0为未接收  1为已接收并已下单  2为已接收但下单出错  3正面处理当前订单(正在下单处理的过程中)
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static boolean netOrderState(Long netOrderId, int stateType) {
		boolean isStateSame = false;
		if (receiveOrderMap != null && receiveOrderMap.size() > 0) {
			Order order = receiveOrderMap.get(netOrderId);
			if (order != null && order.getNetOrderState() == stateType) {
				isStateSame = true;
			}
		}
		return isStateSame;
	}


	private boolean isStopSyncNetOrder = true;//是否要停止网上订单轮训接口

	public boolean isStopSyncNetOrder() {
		return isStopSyncNetOrder;
	}


	public void setStopSyncNetOrder(boolean stopSyncNetOrder) {
		isStopSyncNetOrder = stopSyncNetOrder;
		//打印结束
		if (stopSyncNetOrder) {
			if (netOrderinfoCallBack != null) {
				netOrderinfoCallBack.printState(false);
			}
			Log.i(TAG, "网上订单打印结束");
		}
		//正在下单 并进行打印中
		else {
			if (netOrderinfoCallBack != null) {
				netOrderinfoCallBack.printState(true);
			}
			Log.i(TAG, "网上订单打印开始");
		}
	}


	public void setNetOrderinfoCallBack(NetOrderinfoCallBack callback) {
		netOrderinfoCallBack = callback;
	}

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void SyncNetOrder() {
		timerSyncNetOrder = new MyTimer();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				// 要做的事情
				Log.i(TAG, "开始后台轮训网上订单" + format.format(new Date()));
				if (isStopSyncNetOrder()) {
					getNetOrderInfo();
				}
			}
		};

		taskSyncNetOrder = new MyTimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, format.format(new Date()) + "taskSyncNetOrderID>>" + this.getMyid());
				Message message = new Message();
				message.what = 1;
				handler.sendMessage(message);
			}
		};
		taskSyncNetOrder.setMyid(System.currentTimeMillis());
		timerSyncNetOrder.schedule(taskSyncNetOrder, delayedTime, cycle);//5秒循环一次获取网上订单详情
	}

	/**
	 * 判断外卖单是否接收
	 *
	 * @return
	 */
	private boolean logicWaiMaiOrderReceive(Order order) {
		boolean isReceive = true;
		if (order.getOrderType().equals("SALE_OUT") && !store.isWaiMaiOrderReceive()) {
			isReceive = false;
		}
		return isReceive;
	}

	public class MyTimerTask extends TimerTask {
		public long myid;

		public long getMyid() {
			return myid;
		}

		public void setMyid(long myid) {
			this.myid = myid;
		}

		@Override
		public void run() {

		}
	}

	public void restoreSyncNetOrder(long time, final List<Order> orderList) {
		timerStart = new MyTimer();
		final int[] i    = {0};
		final int   size = orderList.size();  //10
		handlerStart = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				try {
					if (isCleanOrderList) {
						if (orderList != null && orderList.size() > 0) {
							orderList.clear();
						}
					}
					// 要做的事情
					if (orderList != null && orderList.size() > 0) {
						Order orderInfo = ToolsUtils.cloneTo(orderList.get(i[0]));
						//订单对象不能为空 并且订单没有被门店接收过
						if (orderInfo != null && !isReceiveNetOrder(orderInfo.getId())) {
							setStopSyncNetOrder(false);//停止轮训网上订单
							ToolsUtils.writeUserOperationRecords("接受网上订单==>订单Id==" + orderInfo
									.getId());
							String source = orderInfo.getSource();
							if (!TextUtils.isEmpty(source)) {
								if (source.equals("2")) {
									source = "微信堂食";
								}
							} else {
								source = "未知来源";
							}
							orderInfo.setSource(source);
							if (ToolsUtils.logicIsTable()) {
								checkDishCount(orderInfo);
							} else {
								checkDishCount(orderInfo);
							}
							Log.i(TAG, "i[0]==" + i[0] + ",size==" + size);
							if (i[0] == size - 1) {
								setStopSyncNetOrder(true);//开始轮训网上订单
								cancleStartTimer();
							}
							i[0] = i[0] + 1;
						} else {
							modifyOrderType(orderInfo.getId(), 3);
							Log.i(TAG, format.format(new Date()) + "已经被接受过的订单>>" + orderInfo
									.getId());
							MyApplication.getInstance().ShowToast(ToolsUtils
									.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
							orderList.remove(i[0]);
							setStopSyncNetOrder(false);//开始轮训网上订单
							setStopSyncNetOrder(true);//开始轮训网上订单
							cancleStartTimer();
						}
					} else {
						setStopSyncNetOrder(false);//停止轮训网上订单
						setStopSyncNetOrder(true);//开始轮训网上订单
						cancleStartTimer();
					}
				} catch (Exception e) {
					e.printStackTrace();
					i[0] = 0;
					setStopSyncNetOrder(false);//停止轮训网上订单
					setStopSyncNetOrder(true);//开始轮训网上订单
					cancleStartTimer();
				}
			}
		};
		taskStart = new MyTimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.i(TAG, format.format(new Date()) + "taskStartID>>" + this.getMyid());
				Message message = new Message();
				message.what = 1;
				handlerStart.sendMessage(message);
			}
		};
		taskStart.setMyid(System.currentTimeMillis());
		timerStart.schedule(taskStart, 0, time * 1000);//5秒循环一次获取网上订单详情
	}

	public void cancleTimter(boolean isColsePrintOrder) {
		if (timerSyncNetOrder != null) {
			Log.i(TAG, "关闭后台轮训网上订单");
			timerSyncNetOrder.cancel();
			timerSyncNetOrder = null;
		}
		if (isColsePrintOrder) {
			cancleStartTimer();
		}

	}


	public void cancleStartTimer() {
		if (timerStart != null) {
			timerStart.cancel();
			timerStart = null;
		}
	}

	public void cleanNetOrderMap() {
		if (receiveOrderMap != null) {
			receiveOrderMap.clear();
		}
	}

	public void closeWeight() {
		if (mOpScale != null) {
			mOpScale.Close();
			mOpScale = null;
		}
	}


	private boolean isCleanOrderList = false;//是否要重置netOrderList

	private synchronized void getNetOrderInfo() {
		Log.i(TAG, "后台轮训网上订单开关状态==" + MyApplication.getInstance().isConFirmNetOrder());
		if (MyApplication.getInstance().isConFirmNetOrder()) {
			try {
				OrderService orderService = OrderService.getInstance();
				final Store  store        = Store.getInstance(MyApplication.getInstance());
				orderService
						.syncNetOrders(store.getPreordertime(), new ResultCallback<List<Order>>() {
							@Override
							public void onResult(List<Order> result) {
								List<Order> orderList            = new CopyOnWriteArrayList<Order>();
								List<Order> orderWaiMaiList      = new CopyOnWriteArrayList<Order>();
								boolean     isWaiMaiOrderReceive = true;
								if (!ToolsUtils.isList(result)) {
									isCleanOrderList = false;
									Log.i(TAG, "后台轮训网上订单信息条数=====" + result.size());
									if (!store.isWaiMaiOrderReceive()) {
										isWaiMaiOrderReceive = false;
										for (Order order : result) {
											if (order.getOrderType().equals("SALE_OUT")) {
												orderWaiMaiList.add(order);
											} else {
												orderList.add(order);
											}
										}
									} else {
										orderList = ToolsUtils.cloneTo(result);
									}
									if (store.getAutoMaticNetOrder()) {
										autoMaticNetOrder(orderList);
									} else {
										if (isWaiMaiOrderReceive) {
											EventBus.getDefault()
													.post(new PosEvent(Constant.EventState.PUT_NET_ORDER, orderList));
										} else {
											EventBus.getDefault()
													.post(new PosEvent(Constant.EventState.PUT_NET_ORDER, orderWaiMaiList));
										}
									}
								} else {
									isCleanOrderList = true;
									Log.i(TAG, "后台轮训网上订单信息条数=====0");
								}
								if (netOrderinfoCallBack != null) {
									if (isWaiMaiOrderReceive) {
										netOrderinfoCallBack.getNetOrderInfoList(result);
									} else {
										netOrderinfoCallBack.getNetOrderInfoList(orderWaiMaiList);
									}

								}
							}

							@Override
							public void onError(PosServiceException e) {
								Log.i(TAG, "同步订单时后台或者网络异常造成失败1");
								if (netOrderinfoCallBack != null) {
									netOrderinfoCallBack
											.getNetOrderInfoList(new ArrayList<Order>());
								}
							}
						});
			} catch (PosServiceException e) {
				e.printStackTrace();
				Log.i(TAG, "同步订单时后台或者网络异常造成失败2");
				if (netOrderinfoCallBack != null) {
					netOrderinfoCallBack.getNetOrderInfoList(new ArrayList<Order>());
				}
			}
		}
	}

	private void autoMaticNetOrder(List<Order> orderList) {
		if (!ToolsUtils.isList(orderList)) {
			restoreSyncNetOrder(1, orderList);
		}
	}

	/**
	 * 检测菜品库存并下单
	 *
	 * @param order
	 */
	public synchronized void checkDishCount(final Order order) {
		Log.i("TimerTaskController", "checkDishCount--网络订单 id===>>" + order.getId());
		if (netOrderState(order.getId(), 3))//该订单是否是正在处理中的订单
		{
			Log.i("checkDishCount检测菜品库存并下单", "该订单是否是正在处理中的订单 id===>>" + order.getId());
			setStopSyncNetOrder(true);//开始轮训网上订单
			cancleStartTimer();
			return;
		}
		//转台单
		if (order.getOperation_type() == 2) {
			conFirmNetOrder(order);
		} else {
			getOrderId(null, order);
			//            CheckOutUtil checkOutUtil = new CheckOutUtil(MyApplication.getInstance().getContext());
			//            final List<Dish> dishs = new CopyOnWriteArrayList<>();
			//            for (OrderItem orderItem : order.getItemList()) {
			//                Dish dish = new Dish(orderItem);
			//                dishs.add(dish);
			//            }
			//            checkOutUtil.getDishStock(dishs, new DishCheckCallback() {
			//                @Override
			//                public void haveStock() {
			//                    //                createOrder(null, order);
			//                    getOrderId(null, order);
			//                }
			//
			//                @Override
			//                public void noStock(List dataList) {
			//                    refreshDish(dataList, dishs);
			//                }
			//            });
		}
	}

	/**
	 * 确认接收网上的订单
	 *
	 * @param resultOrder
	 */
	private synchronized void conFirmNetOrder(final Order resultOrder) {
		modifyOrderType(resultOrder.getId(), 3);//正在处理该订单
		//        final String netOrderId = String.valueOf(dataList.get(position).getPaymentList().get(0).getOrderId());
		final String netOrderId   = String.valueOf(resultOrder.getId());
		final String orderId      = String.valueOf(resultOrder.getRefOrderId());
		final long   orderIdL     = resultOrder.getId();
		OrderService orderService = null;
		try {
			orderService = OrderService.getInstance();

		} catch (PosServiceException e) {
			e.printStackTrace();
		}

		orderService.confirmNetOrder(netOrderId, orderId, resultOrder
				.getCallNumber(), new ResultCallback<Integer>() {
			@Override
			public void onResult(Integer result) {
				if (result == 0)//表示成功接单
				{
					modifyOrderType(Long.parseLong(netOrderId), 1);//修改为已接收
					refrushUi(resultOrder.getId());
					MyApplication.getInstance().ShowToast("网络订单编号 : " + orderId + "同步成功!");
					if (resultOrder.isInformKds())//是否通知KDS打印
					{
						kdsChangeOrderTable(resultOrder.getRefOrderId(), resultOrder
								.getChangeTableName());
					}
					if (resultOrder.isInformKitchen())//是否通知厨房打印
					{
						getOrderInfo(resultOrder);
					}
					sleep(3);
				} else {
					modifyOrderType(Long.parseLong(netOrderId), 2);//修改为下单出错
				}

			}

			@Override
			public void onError(PosServiceException e) {
				modifyOrderType(Long.parseLong(netOrderId), 2);//修改为下单出错
				MyApplication.getInstance().ShowToast("网上订单确认同步失败," + e.getMessage());
				Log.i("网上订单确认同步失败", "orderId ==" + orderId + "===" + e.getMessage());
			}
		});
	}

	/**
	 * 根据订单Id获得订单详情
	 */
	private void getOrderInfo(final Order order) {
		try {
			MyApplication.getInstance().ShowToast("正在获取订单详情,请稍等...");
			OrderService orderService = OrderService.getInstance();
			orderService.getOrderInfoById(order.getRefOrderId() + "", new ResultCallback<Order>() {
				@Override
				public void onResult(final Order result) {
					if (result != null && result.getItemList() != null && result.getItemList()
							.size() > 0) {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								order.setItemList(result.getItemList());
								order.setOrderType(result.getOrderType());
								order.setId(result.getId());
								EventBus.getDefault()
										.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, order));
							}
						}, 3000);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					Log.i("订单详情获取失败!", e.getMessage());
					MyApplication.getInstance().ShowToast(e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			Log.i("订单详情获取失败!", e.getMessage());
		}
	}

	private void kdsChangeOrderTable(final long netOrderId, final String newTableName) {
		List<KDS> kdsList = PrinterDataController.getKdsList();
		if (kdsList != null && kdsList.size() > 0) {
			int size = kdsList.size();
			for (int i = 0; i < size; i++) {
				KDS kds = kdsList.get(i);
				try {
					OrderService orderService = OrderService.getKdsInstance(kds);
					orderService
							.kdsChangeOrderTable(netOrderId, newTableName, new ResultCallback<Boolean>() {
								@Override
								public void onResult(Boolean result) {
									if (result) {
										MyApplication.getInstance().ShowToast("通知KDS换台成功!");
									} else {
										MyApplication.getInstance().ShowToast("通知KDS换台失败!");
										KDSOrderFailure(netOrderId, newTableName);
									}
								}

								@Override
								public void onError(PosServiceException e) {
									MyApplication.getInstance()
											.ShowToast("通知KDS换台失败," + e.getMessage());
									Log.i("通知KDS换台失败", e.getMessage());
									KDSOrderFailure(netOrderId, newTableName);
								}
							});
				} catch (PosServiceException e) {
					e.printStackTrace();
					Log.i("KDS退单失败", e.getMessage());
					KDSOrderFailure(netOrderId, newTableName);
				}
			}
		}
	}

	/**
	 * KDS连接失败的操作
	 */
	private void KDSOrderFailure(final long netOrderId, final String newTableName) {
		DialogUtil.ordinaryDialog(MyApplication.getInstance().getContext(), ToolsUtils
				.returnXMLStr("kds_connect_error"), ToolsUtils
				.returnXMLStr("please_re_switch_KDS"), new DialogCallback() {
			@Override
			public void onConfirm() {
				kdsChangeOrderTable(netOrderId, newTableName);
			}

			@Override
			public void onCancle() {
			}
		});
	}


	private synchronized void createOrder(List<Dish> dishs, final Order netOrder) {
		try {
			OrderService orderService = OrderService.getInstance();
			Order        order        = null;
			if (dishs != null) {
				order = Cart.getInstance().getNetOrderItem(dishs, netOrder);
			} else if (netOrder != null) {
				order = ToolsUtils.cloneTo(netOrder);
			}
			if (order != null) {
				final long orderId = order.getId();
				order.setRefNetOrderId(orderId);
				order.setThirdPlatformOrderId(netOrder.getOuterOrderid());
				order.setThirdPlatformOrderIdView(netOrder.getOuterOrderIdView());
				order.setThirdPlatfromOrderIdDaySeq(netOrder.getOuterOrderIdDaySeq());
				final Order finalOrder = ToolsUtils.cloneTo(order);
				orderService.createOrder(order, new ResultCallback<Order>() {
					@Override
					public void onResult(final Order result) {
						if (result != null) {
							modifyOrderType(orderId, 1);//修改为已接收
							setStopSyncNetOrder(false);//停止轮训网上订单
							if (store.isCreateOrderJyj()) {
								result.setJyjOrder(true);//将下单状态改为向JYJ下单
								createOrderJyj(result, orderId);
							} else {
								orderPrint(result, orderId);
							}


						} else {
							modifyOrderType(orderId, 2);//修改为下单出错
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.orderErrTips(finalOrder, orderId));
							Log.i("网上开台下单失败", "orderId ==" + orderId + "===" + "订单状态获取失败,或者门店已接收该订单!");

							setStopSyncNetOrder(false);//停止轮训网上订单
							setStopSyncNetOrder(true);//开始轮训网上订单
							cancleStartTimer();
						}
					}

					@Override
					public void onError(PosServiceException e) {
						modifyOrderType(orderId, 2);//修改为下单出错
						Log.i("网上开台下单失败", "orderId ==" + orderId + "===" + e.getMessage());
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.orderErrTips(finalOrder, orderId));

						setStopSyncNetOrder(false);//停止轮训网上订单
						setStopSyncNetOrder(true);//开始轮训网上订单
						cancleStartTimer();
						//refrushUi(position,netOrder.getId());
					}
				});
			}
		} catch (PosServiceException e) {
			e.printStackTrace();
			Log.i("网上开台下单失败", "===" + e.getMessage());
		}
	}

	private void orderPrint(final Order result, long orderId) {
		Log.i("网上开台下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
		if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
			EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
		}
		System.out.println("==checkDishCount==" + orderId);

		EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, result));

		new Handler().postDelayed(new Runnable() {
			public void run() {
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, result));
			}
		}, 3000);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				EventBus.getDefault()
						.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, result));
			}
		}, 3000);
		//conFirmNetOrder(position ,result);
		MyApplication.getInstance().ShowToast("网络订单编号 : " + orderId + "接单成功!");

		refrushUi(orderId);
		EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result
				.getTableNames()));//kds下单
	}

	private void createOrderJyj(final Order order, final long orderId) {
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
							orderPrint(result, orderId);
						} else {
							Log.e("JYJ下单", "JYJ下单失败:" + "null");
							MyApplication.getInstance()
									.ShowToast(ToolsUtils.returnXMLStr("orders_failed"));
							order.setJyjPrintErrMessage(ToolsUtils
									.returnXMLStr("jyj_order_error_message"));
							orderPrint(order, orderId);
						}
					}

					@Override
					public void onError(PosServiceException e) {
						progressDialog.disLoading();
						Log.e("JYJ下单", "JYJ下单失败:" + e.getMessage());
						MyApplication.getInstance()
								.ShowToast(ToolsUtils.returnXMLStr("orders_failed") + "," + e
										.getMessage());
						order.setJyjPrintErrMessage(ToolsUtils
								.returnXMLStr("jyj_order_error_message"));
						orderPrint(order, orderId);
					}
				});
			} catch (PosServiceException e) {
				e.printStackTrace();
			}

		}
	}

	public synchronized void getOrderId(final List<Dish> dishs, final Order order) {
		try {
			modifyOrderType(order.getId(), 3);//正在处理该订单
			OrderService orderService = OrderService.getInstance();
			orderService.getNextOrderId(new ResultCallback<Long>() {
				@Override
				public void onResult(Long result) {
					Log.i("网络订单和平台订单号分别是", order.getId() + "-------" + result);
					if (result > 0) {
						PosInfo posInfo = PosInfo.getInstance();
						posInfo.setNetOrderId(result);
						createOrder(dishs, order);
					} else {
						modifyOrderType(order.getId(), 2);//下单的过程中出错
					}
				}

				@Override
				public void onError(PosServiceException e) {
					modifyOrderType(order.getId(), 2);//下单的过程中出错
					Log.i("获取订单Id失败", e.getMessage());
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
					//                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示沽清提示
	 *
	 * @param result
	 */
	public void refreshDish(List<DishCount> result, List<Dish> dishs) {
		//刷新菜品数据,显示沽清
		String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
		MyApplication.getInstance().ShowToast(ToolsUtils
				.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names + "。\n\n" + ToolsUtils
				.returnXMLStr("please_re_order"));
		Log.i("以下菜品份数不足:", names + "====<<");
	}

	private void refrushUi(long netOrderId) {
		Order copyList = NetOrderController.getNetOrderMap()
				.get(netOrderId);//根据订单ID去map列表里面查询出order对象
		if (copyList != null && copyList.getId() == (int) netOrderId) {
			NetOrderController.getNetOrderList().remove(copyList);
		}
	}

	/**
	 * 休息三秒
	 */
	private void sleep(long time) {
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static OpScale mOpScale = null;
	private static OpScale.WeightInfo mLastWeight;
	private static String strPrinterSerial = "";
	private static WeightCallBack weightCallBack;
	private static final int MESSAGE_TARE     = 0;
	private static final int MESSAGE_ZERO     = 1;
	private static final int MESSAGE_WEIGHT   = 2;
	private static final int MESSAGE_VERSION  = 3;
	private static final int MESSAGE_UPGRADE  = 4;
	private static final int MESSAGE_PROGRESS = 5;

	//
	public void setWeightCallBack(WeightCallBack weightCallBack) {
		this.weightCallBack = weightCallBack;
	}

	//设置净重
	public static void setNetWeight(String num) {
		if (weightCallBack != null) {
			System.out.println("净重==>" + Float.valueOf(num));
			weightCallBack.changeNetWeight(Float.valueOf(num));
		}
	}

	//设置皮重
	public static void setTareWeight(String num) {
		if (weightCallBack != null) {
			System.out.println("皮重==>" + Float.valueOf(num));
			weightCallBack.changeTareWeight(Float.valueOf(num));
		}
	}

	//设置总重
	public static void setGrossWeight(String num) {
		if (weightCallBack != null) {
			System.out.println("总重==>" + Float.valueOf(num));
			weightCallBack.changeGrossWeight(Float.valueOf(num));
		}
	}

	//当前秤重量是否已经稳定显示重量
	public static void getOverWeight() {
		if (weightCallBack != null && mLastWeight != null) {
			weightCallBack.signalLampState(mLastWeight.isStable);
		}
	}

	@SuppressLint("HandlerLeak")
	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			progressDialog.disLoading();
			switch (msg.what) {
				case MESSAGE_TARE:
					MyApplication.getInstance().ShowToast("皮重设置" + (msg.arg1 == 0 ? "成功" : "失败"));
					if (msg.arg1 == 0) {
						if (weightCallBack != null) {
							weightCallBack.setTareNumSuccess(true);
						}
					}
					break;
				case MESSAGE_ZERO:
					MyApplication.getInstance().ShowToast("归零设置" + (msg.arg1 == 0 ? "成功" : "失败"));
					if (weightCallBack != null) {
						weightCallBack.setZeroNumSuccess(true);
					}
					break;
				case MESSAGE_WEIGHT:
					//判断称重服务是否稳定（不跳字）
					if (!mLastWeight.isOverWeight) {
						setNetWeight(String.format("%.3f", mLastWeight.netWeight));
						setTareWeight(String.format("%.3f", mLastWeight.tareWeight));
						setGrossWeight(String.format("%.3f", mLastWeight.grossWeight));
					}
					getOverWeight();
					break;
				case MESSAGE_VERSION:
					break;
				case MESSAGE_UPGRADE:
					break;
				case MESSAGE_PROGRESS:
					break;
			}
		}
	};

	public static void initOpScale() {
		if (mOpScale != null) {
			mOpScale.Close();
			mOpScale = null;
		}
		mOpScale = new OpScale();
		strPrinterSerial = USBPort.getDeviceName(0);
		if (mOpScale.Open(null) == 0) {
			mOpScale.SetWeightChangedListener(new OpScale.WeightChangedListener() {
				@Override
				public void onWeightChanged(OpScale.WeightInfo info) {
					if (info != null) {
						mLastWeight = info;
						mHandler.sendEmptyMessage(MESSAGE_WEIGHT);
					}

				}
			});
		} else {
			mOpScale = null;
			Log.e("Timertask", "尚未配置称重设备");
		}
	}

	public OpScale.WeightInfo getLastWeight() {
		if (mLastWeight != null) {
			return mLastWeight;
		}
		return null;
	}

	//设置秤的皮重
	public void setTareWeightNum() {
		progressDialog.show();
		new Thread() {
			public void run() {
				boolean result = false;
				Message msg    = Message.obtain();
				msg.what = MESSAGE_TARE;
				if (mOpScale != null) {
					synchronized (mOpScale) {
						result = mOpScale.Tare() == 0;
					}
				}
				msg.arg1 = result ? 0 : -1;
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	//设置秤归零
	public void setZeroNum() {
		progressDialog.show();
		new Thread() {
			public void run() {
				boolean result = false;
				Message msg    = Message.obtain();
				msg.what = MESSAGE_ZERO;
				if (mOpScale != null) {
					synchronized (mOpScale) {
						result = mOpScale.Zero() == 0;
					}
				}
				msg.arg1 = result ? 0 : -1;
				mHandler.sendMessage(msg);
			}
		}.start();
	}


	//    private IWeight iWeight;
	//    private Weight weight;

	//
	//    private WeightCallBack weightCallBack;
	//    private Timer weightTimer;
	//    private Handler weightHandler;
	//    private TimerTask weightTask;
	//
	//    public Weight getWeight() {
	//        return weight;
	//    }
	//
	//    public void setWeightCallBack(WeightCallBack weightCallBack)
	//    {
	//        this.weightCallBack = weightCallBack;
	//    }
	//
	//
	//    //设置皮重
	//    public void setTareWeight(int tareWeight)
	//    {
	//        if(weight != null)
	//        {
	//            weight.setTareWeight(tareWeight);
	//        }
	//
	//    }
	//
	//    //设置净重
	//    public void setNetWeight(int netWeight)
	//    {
	//        if(weight != null)
	//        {
	//            weight.setNetWeight(netWeight);
	//        }
	//    }
	//
	//
	////    public void setWeightInfo(IWeight iWeight,Weight weight)
	////    {
	////        this.iWeight = iWeight;
	////        this.weight = weight;
	////        //连接上秤就把秤上的皮重重置
	////        resetclick();
	////    }
	//
	//    public void cleanWeightTimer()
	//    {
	//        if (weightTimer != null) {
	//            weightTimer.cancel();
	//        }
	//    }
	//
	////    //去皮操作
	////    private void callsetTare() {
	////        try {
	////            if(iWeight != null)
	////            {
	////                iWeight.setTare();
	////            }
	////        } catch (RemoteException e) {
	////            e.printStackTrace();
	////        }
	////    }
	//
	////    //置零操作
	////    private void callsetZero() {
	////        try {
	////            if(iWeight != null)
	////            {
	////                iWeight.setZero();
	////            }
	////        } catch (RemoteException e) {
	////            e.printStackTrace();
	////        }
	////    }
	//
	////    //重置操作
	////    private void resetclick() {
	////        try {
	////            if(iWeight != null)
	////            {
	////                iWeight.reset();
	////            }
	////        } catch (RemoteException e) {
	////            // TODO Auto-generated catch block
	////            e.printStackTrace();
	////        }
	////    }
	//
	//    public void setCallBackNetWeight(int netWeight,int pointnumber)
	//    {
	//        if(weightCallBack != null)
	//        {
	//            String netWeightTemp = "";
	//            switch (pointnumber)
	//            {
	//                case 0:
	//                    netWeightTemp = netWeight+"";
	//                    break;
	//                case 1:
	//                    netWeightTemp = (netWeight/10f)+"";
	//                    break;
	//                case 2:
	//                    netWeightTemp = (netWeight/100f)+"";
	//                    break;
	//                case 3:
	//                    netWeightTemp = (netWeight/1000f)+"";
	//                    break;
	//            }
	//            weightCallBack.changeNetWeight(Float.valueOf(netWeightTemp));
	//        }
	//    }
	//
	//    public void setCallBackTareWeight(int tareWeight,int pointnumber)
	//    {
	//        if(weightCallBack != null)
	//        {
	//            String tareWeightTemp = "";
	//            switch (pointnumber)
	//            {
	//                case 0:
	//                    tareWeightTemp = (tareWeight)+"";
	//                    break;
	//                case 1:
	//                    tareWeightTemp = (tareWeight/10f)+"";
	//                    break;
	//                case 2:
	//                    tareWeightTemp = (tareWeight/100f)+"";
	//                    break;
	//                case 3:
	//                    tareWeightTemp = (tareWeight/1000f)+"";
	//                    break;
	//            }
	//            weightCallBack.changeTareWeight(Float.valueOf(tareWeightTemp));
	//        }
	//    }
	//
	//    public void setCallBackPointNumber(int pointNumber)
	//    {
	//        if(weightCallBack != null)
	//        {
	//            weightCallBack.setPointnumber(pointNumber);
	//        }
	//    }
	//
	//
	//
	//    //检测称重秤回调
	////    public void syncWeightTimer() {
	////        weightTimer = new Timer();
	////        weightHandler = new Handler() {
	////            public void handleMessage(Message msg) {
	////                if (msg.what == 1) {
	////                    float price=12.45f;
	////                    float amount=0f;
	////                    int pnumber = 0;
	////                    if ((null!=weight) && (System.currentTimeMillis()-weight.getCurrentTM()<2000) ){
	////                        pnumber=weight.getPointnumber();
	////                        //如果秤的重量稳定下来了
	////                        if(weight.getSteadyMark()==1)
	////                        {
	////                            setCallBackPointNumber(pnumber);
	////                            switch (pnumber) {
	////                                case 0:
	//////                                    System.out.println(df0.format(weight.getNetWeight())+"<<========0");
	////                                    setCallBackNetWeight(weight.getNetWeight(),pnumber);
	////                                    setCallBackTareWeight(weight.getTareWeight(),pnumber);
	////                                    //                                ettare1.setText( df0.format(weight.getTareWeight()));
	////                                    //                                etweight1.setText(df0.format(weight.getNetWeight()));
	////                                    break;
	////                                case 1:
	//////                                    System.out.println(df1.format(weight.getNetWeight()/10f)+"<<========1");
	////                                    setCallBackNetWeight(weight.getNetWeight(),pnumber);
	////                                    setCallBackTareWeight(weight.getTareWeight(),pnumber);
	////                                    //                                ettare1.setText( df1.format(weight.getTareWeight()/10f));
	////                                    //                                etweight1.setText(df1.format(weight.getNetWeight()/10f));
	////                                    break;
	////                                case 2:
	//////                                    System.out.println(df2.format(weight.getNetWeight()/100f)+"<<========2");
	////                                    setCallBackNetWeight(weight.getNetWeight(),pnumber);
	////                                    setCallBackTareWeight(weight.getTareWeight(),pnumber);
	////                                    //                                ettare1.setText( df2.format(weight.getTareWeight()/100f));
	////                                    //                                etweight1.setText(df2.format(weight.getNetWeight()/100f));
	////                                    break;
	////                                case 3:
	//////                                    System.out.println(df3.format(weight.getNetWeight()/1000f)+"<<========3");
	////                                    setCallBackNetWeight(weight.getNetWeight(),pnumber);
	////                                    setCallBackTareWeight(weight.getTareWeight(),pnumber);
	////                                    //                                ettare1.setText( df3.format(weight.getTareWeight()/1000f));
	////                                    //                                etweight1.setText(df3.format(weight.getNetWeight()/1000f));
	////                                    break;
	////
	////                                default:
	//////                                    System.out.println( df3.format(weight.getNetWeight()/1000f)+"<<========default");
	////                                    pnumber = 3;
	////                                    setCallBackPointNumber(pnumber);
	////                                    setCallBackNetWeight(weight.getNetWeight(),pnumber);
	////                                    setCallBackTareWeight(weight.getTareWeight(),pnumber);
	////                                    //                                ettare1.setText( df3.format(weight.getTareWeight()/1000f));
	////                                    //                                etweight1.setText(df3.format(weight.getNetWeight()/1000f));
	////                                    break;
	////                            }
	////                        }
	////
	////                        //                        if (weight.getZeroMark()==1 ){
	////                        //                            ivlwlamp.setImageResource(R.drawable.pilot_lamp_true);
	////                        //                        }
	////                        //                        else {
	////                        //                            ivlwlamp.setImageResource(R.drawable.pilot_lamp_false);
	////                        //                        }
	////                        //
	////                        //                        if (weight.getSteadyMark()==1){
	////                        //                            ivwdlamp.setImageResource(R.drawable.pilot_lamp_true);
	////                        //                        }
	////                        //                        else {
	////                        //                            ivwdlamp.setImageResource(R.drawable.pilot_lamp_false);
	////                        //                        }
	////                        //                        if (weight.getTareMark()==1){
	////                        //                            ivqplamp.setImageResource(R.drawable.pilot_lamp_false);
	////                        //                        }
	////                        //                        else {
	////                        //                            ivqplamp.setImageResource(R.drawable.pilot_lamp_true);
	////                        //                        }
	////                        //                        etweight1.setTextSize(50f);
	////                        //
	////                        //                        if (weight.getOverLoadMark()==1){
	////                        //                            etweight1.setTextSize(40f);
	////                        //
	////                        //                            etweight1.setText("超载"); //ERR02
	////                        //                        }
	////                        //                        if (weight.getOpenZeroHighMark()==1){   //ERR04  零点高
	////                        //                            etweight1.setTextSize(40f);
	////                        //                            etweight1.setText("零点高");
	////                        //
	////                        //                        }
	////                        //                        if (weight.getOpenZeroLowMark()==1){
	////                        //                            etweight1.setTextSize(40f);
	////                        //                            etweight1.setText("零点低");    //ERR03 零点低
	////                        //                        }
	////                    }
	////                    else {
	//////                                                ettare1.setText("-");
	//////                                                etweight1.setTextSize(40f);
	//////                                                etweight1.setText("与A/D不通讯"); //与A/D不通讯ERR01
	////                    }
	////                    super.handleMessage(msg);
	////                };
	////            };
	////        };
	////
	////
	////        weightTask = new TimerTask() {  //用于处理重量信息
	////
	////            @Override
	////            public void run() {
	//////                // 需要做的事:发送消息
	//////                if (null == iWeight) {
	//////                    return;
	//////                }
	//////                try {
	//////                    weight = iWeight.getWeight();
	//////                } catch (RemoteException e) {
	//////                    // TODO Auto-generated catch block
	//////                    e.printStackTrace();
	//////                }
	//////                if (null != weight) {
	//////                    Message message = new Message();
	//////                    message.what = 1;
	//////                    weightHandler.sendMessage(message);
	//////                }
	////            }
	////        };
	////        if (weightTimer != null && weightTask != null) {
	////            weightTimer.schedule(weightTask, 1000, 1000);//延时1秒轮训周期1秒一次
	////        } else {
	////            Log.i(TAG, "称重Task没有启动");
	////        }
	////    }
}
