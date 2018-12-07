package cn.acewill.pos.next.service;

import android.content.Context;
import android.text.TextUtils;

import com.acewill.paylibrary.PayReqModel;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.acewill.pos.next.common.NetOrderController;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.dao.cache.CachedDao;
import cn.acewill.pos.next.exception.ErrorCode;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.factory.RetrofitFactory;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.WftRespOnse;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.service.retrofit.MemberResponse;
import cn.acewill.pos.next.service.retrofit.RetrofitOrderService;
import cn.acewill.pos.next.service.retrofit.response.KDSResponse;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.service.retrofit.response.pay.BaseWechatPayResult;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Acewill on 2016/6/2.
 */
public class OrderService {

	RetrofitOrderService internalService;
	private static RetrofitOrderService internalKdsService;
	private static RetrofitOrderService internalJYJService;
	private static OrderService         orderService;
	private static OrderService         kdsOrderService;
	private static OrderService         jyjOrderService;
	private        CachedDao            cachedDishDao;

	public static OrderService getInstance() throws PosServiceException {
		if (orderService == null) {
			RetrofitOrderService internalService = RetrofitFactory
					.buildService(RetrofitOrderService.class);
			orderService = new OrderService(internalService);
		}
		return orderService;
	}

	public static OrderService getKdsInstance(Context context) throws PosServiceException {
		if (kdsOrderService == null) {
			Store  store   = Store.getInstance(context);
			String baseUrl = "http://" + store.getKdsServer() + ":" + store.getKdsPort() + "/";
			internalKdsService = RetrofitFactory
					.buildKdsService(baseUrl, RetrofitOrderService.class);
			kdsOrderService = new OrderService();
		}
		return kdsOrderService;
	}

	public static OrderService getKdsInstance(Context context, String ip) throws PosServiceException {
		if (kdsOrderService == null) {
			//            Store store = Store.getInstance(context);
			String baseUrl = "http://" + ip + ":" + "8080" + "/";
			internalKdsService = RetrofitFactory
					.buildKdsService(baseUrl, RetrofitOrderService.class);
			kdsOrderService = new OrderService();
		}
		return kdsOrderService;
	}

	public static OrderService getKdsInstance(KDS kds) throws PosServiceException {
		//        if (kdsOrderService == null) {
		String port = kds.getPort();
		port = TextUtils.isEmpty(port) ? "8080" : port;
		String baseUrl = "http://" + kds.getIp() + ":" + port + "/";
		internalKdsService = RetrofitFactory.buildKdsService(baseUrl, RetrofitOrderService.class);
		kdsOrderService = new OrderService();
		//        }
		return kdsOrderService;
	}


	public static OrderService getJyjOrderService() throws PosServiceException {
		Store store = Store.getInstance(MyApplication.getInstance().getContext());
		String baseUrl = "http://" + store.getServiceAddressJyj() + ":" + store
				.getStorePortJyj() + "/";
		internalJYJService = RetrofitFactory.buildKdsService(baseUrl, RetrofitOrderService.class);
		jyjOrderService = new OrderService();
		return jyjOrderService;
	}

	private OrderService() {
	}

	private OrderService(RetrofitOrderService internalService) {
		this.internalService = internalService;
		cachedDishDao = new CachedDao();
	}

	public RetrofitOrderService getInternalService() {
		return internalService;
	}


	//获取指定桌台上的所有预定信息
	public List<Order> getOrderByTable(long tableId) {
		List<Order> orderList = new ArrayList<>();

		return orderList;
	}

	//获取一个门店的所有订单
	public void getAllOrders(String token, String appId, String brandId, String storeId, int page, int limit, int orderType, int payStatus, final ResultCallback<List<Order>> resultCallback) {
		if (!ServerStatus.getInstance().isRunning()) {
			resultCallback.onResult(cachedDishDao.getAllOrders());
			//TODO -- 需要检查估清状态
			return;
		}

		internalService
				.getAllOrders(token, appId, brandId, storeId, page, limit, orderType, payStatus, "created_at", "DESC")
				.map(new Func1<PosResponse<List<Order>>, List<Order>>() {
					@Override
					public List<Order> call(PosResponse<List<Order>> listPosResponse) {
						if (listPosResponse.isSuccessful()) {
							return listPosResponse.getContent();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);

					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Order>>(resultCallback));
	}

	//根据订单号获取订单详情
	public void getOrderInfoById(String orderId, final ResultCallback<Order> resultCallback) {
		if (!ServerStatus.getInstance().isRunning()) {
			resultCallback.onResult(cachedDishDao.findOrderById(Long.valueOf(orderId).longValue()));
			return;
		}

		PosInfo posInfo = PosInfo.getInstance();
		internalService.getOrderInfoById(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), orderId).map(new Func1<PosResponse<Order>, Order>() {
			@Override
			public Order call(PosResponse<Order> orderPosResponse) {
				if (orderPosResponse.isSuccessful()) {
					return orderPosResponse.getContent();
				}
				PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
						.getErrmsg());
				throw Exceptions.propagate(exception);

			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Order>(resultCallback));
	}


	//获取指定订单的详细信息
	public void getOrderById(long oid, final ResultCallback<Order> resultCallback) {
		internalService.getOrderById(oid).map(new Func1<PosResponse<Order>, Order>() {
			@Override
			public Order call(PosResponse<Order> orderPosResponse) {
				if (orderPosResponse.isSuccessful()) {
					return orderPosResponse.getContent();
				}
				PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
						.getErrmsg());
				throw Exceptions.propagate(exception);

			}
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Order>(resultCallback));
	}

	//获取某个桌台上的所有订单
	public void getOrdersByTableId(long tid, ResultCallback<List<Order>> resultCallback) {
		internalService.getOrdersByTableId(tid)
				.map(new Func1<PosResponse<List<Order>>, List<Order>>() {
					@Override
					public List<Order> call(PosResponse<List<Order>> listPosResponse) {
						if (listPosResponse.isSuccessful()) {
							return listPosResponse.getContent();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);

					}
				}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Order>>(resultCallback));
	}

	//根据获取桌台订单号查询订单信息(接口嵌套) --TODO
	public void getOrderByTableId(long tid, ResultCallback<Order> resultCallback) {
		internalService.getOrdersByTableId(tid)
				.flatMap(new Func1<PosResponse<List<Order>>, Observable<PosResponse<Order>>>() {
					@Override
					public Observable<PosResponse<Order>> call(PosResponse<List<Order>> listPosResponse) {
						if (listPosResponse.isSuccessful()) {
							return internalService
									.getOrderById(((Order) listPosResponse.getContent().get(0))
											.getId());
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);

					}
				}).map(new Func1<PosResponse<Order>, Order>() {

			@Override
			public Order call(PosResponse<Order> orderPosResponse) {
				if (orderPosResponse.isSuccessful()) {
					return orderPosResponse.getContent();
				}
				PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
						.getErrmsg());
				throw Exceptions.propagate(exception);

			}
		})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Order>(resultCallback));
	}

	SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 微信支付
	 *
	 * @param payModel
	 */
	public void wechatPay(PayReqModel payModel, ResultCallback<BaseWechatPayResult> resultCallback) {
		final PosInfo posInfo = PosInfo.getInstance();
		Calendar      nowTime = Calendar.getInstance();
		String        time1   = format1.format(nowTime.getTime());
		nowTime.add(Calendar.MINUTE, 2);
		String     time2 = format1.format(nowTime.getTime());
		BigDecimal temp  = new BigDecimal(payModel.totalAmount);
		int total = temp.multiply(new BigDecimal("100"))
				.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		internalService
				.micropayToWepay("", posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),
						payModel.orderNo, total, payModel.wxGoodsDetail
						, payModel.authCode, payModel.wxGoodsDetail, time1,
						time2, posInfo.getTerminalName(), "192.168.1.101")
				.map(new Func1<BaseWechatPayResult, BaseWechatPayResult>() {
					@Override
					public BaseWechatPayResult call(BaseWechatPayResult result) {
						return result;
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<BaseWechatPayResult>(resultCallback));
	}
	public void queryWechatPayResult(PayReqModel payModel, ResultCallback<BaseWechatPayResult> resultCallback) {
		final PosInfo posInfo = PosInfo.getInstance();
		internalService
				.queryWechatPayResult("", posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId(),
						payModel.orderNo, payModel.paymentStr)
				.map(new Func1<BaseWechatPayResult, BaseWechatPayResult>() {
					@Override
					public BaseWechatPayResult call(BaseWechatPayResult result) {
						return result;
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<BaseWechatPayResult>(resultCallback));
	}

	public void wechatSaoMa(PayReqModel payModel, ResultCallback<BaseWechatPayResult> resultCallback) {
		final PosInfo posInfo = PosInfo.getInstance();
		BigDecimal    temp    = new BigDecimal(payModel.totalAmount);
		int total = temp.multiply(new BigDecimal("100"))
				.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		Calendar nowTime = Calendar.getInstance();
		String   time1   = format1.format(nowTime.getTime());
		nowTime.add(Calendar.MINUTE, 2);
		String time2 = format1.format(nowTime.getTime());

		internalService
				.unifiedOrderToWepay("", posInfo.getAppId(), posInfo.getBrandId(), posInfo
								.getStoreId(),
						payModel.orderNo, total, "192.168.1.101",
						payModel.wxGoodsDetail, payModel.paymentStr)
				.map(new Func1<BaseWechatPayResult, BaseWechatPayResult>() {
					@Override
					public BaseWechatPayResult call(BaseWechatPayResult result) {
						return result;
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<BaseWechatPayResult>(resultCallback));
	}


	//获取一个订单号
	public void getNextOrderId(final ResultCallback<Long> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		if (!ToolsUtils.isNetworkConnected(MyApplication.getInstance().getContext())) {
			//无法连接服务器，可以直接进入系统
			long orderId = cachedDishDao.getNextOrderId();
			posInfo.setOrderId(orderId);
			resultCallback.onResult(orderId);
			return;
		}
		internalService
				.getNextOrderId(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<PosResponse<Long>, Long>() {
					@Override
					public Long call(PosResponse<Long> orderPosResponse) {
						if (orderPosResponse.isSuccessful()) {
							return orderPosResponse.getContent();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);

					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Long>(resultCallback));
	}

	/**
	 * 下单
	 * 1. 向服务器发送下单请求
	 * 2.1 如果成功，则打印厨房单， 同时打印正常的订单小票
	 * 2.2 如果失败，不打印厨房单， 只打印一张下单失败的小票给顾客（上面有比如微信的支付流水号这些信息方便顾客退款）
	 */
	public void createOrder(final Order order, ResultCallback<Order> resultCallback) {
		Store store = Store
				.getInstance(MyApplication.getInstance().getContext());
		RetrofitOrderService tempInternalService = null;
		boolean              isCreateOrderJyj    = store.isCreateOrderJyj();
		if (isCreateOrderJyj) {
			//第一次先下单到我们后台
			if (!order.isJyjOrder()) {
				tempInternalService = this.internalService;
			}
			//第二次要下到吉野家后台
			else {
				tempInternalService = this.internalJYJService;
			}
		} else {
			tempInternalService = this.internalService;
		}
		try {
			final PosInfo posInfo = PosInfo.getInstance();

			order.setWorkShiftId(posInfo.getWorkShiftId());

			if (!ServerStatus.getInstance().isRunning()) {
				//直接保存到本地
				//本地生成的取餐号有自己的前缀，以免和服务器生成的取餐号重复
				order.setCallNumber("" + cachedDishDao.getNextCallNumber());
				order.setTableNames(order.getTableNames());
				order.setCustomerAmount(order.getCustomerAmount());
				order.setWorkShiftId(posInfo.getWorkShiftId());
				//表示是本地创建的
				order.setCreatedOffline(true);

				//无法在保存后获取id
				//order.setId(newOrder.getId());
				if (order.getRefNetOrderId() <= 0) {
					cachedDishDao.saveOrder(order);
					resultCallback.onResult(order);
				}
				return;
			}
			if (order.getRefNetOrderId() > 0) {
				order.setId(posInfo.getNetOrderId());//将orderId设置为从后台拿到的OrderId
			} else {
				order.setId(posInfo.getOrderId());//将orderId设置为从后台拿到的OrderId
			}

			if (order.getId() <= 0) {
				MyApplication.getInstance().ShowToast("订单号获取失败,请在订单下拉列表中刷新订单号后重新下单!");
				return;
			}
			tempInternalService.createOrder(posInfo.getAppId(), posInfo.getBrandId(), posInfo
					.getStoreId(), order).map(new Func1<PosResponse<Order>, Order>() {
				@Override
				public Order call(PosResponse<Order> orderPosResponse) {
					if (orderPosResponse.isSuccessful()) {
						//下单成功 -- 打印
						Order newOrder = orderPosResponse.getContent();
						order.setId(newOrder.getId());
						order.setCallNumber(newOrder.getCallNumber());
						order.setCustomerAmount(order.getCustomerAmount());

						if (!TextUtils.isEmpty(order.getTableNames())) {
							order.setTableNames(order.getTableNames());
						}
						return order;
					}

					PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
							.getErrmsg());
					throw Exceptions.propagate(exception);
				}
			}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
					.subscribe(new ResultSubscriber<Order>(resultCallback));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 如果是桌台模式,在开台下单前判断这个坐台当前有没有订单
	 *
	 * @param tableId
	 * @param resultCallback
	 */
	public void getUnpaiedOrderUnderTable(long tableId, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.getUnpaiedOrderUnderTable(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), tableId).map(new Func1<PosResponse, Integer>() {
			@Override
			public Integer call(PosResponse response) {
				if (response.isSuccessful()) {
					return 0;
				}
				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}


	//更新订单
	public Order udpateOrderSummary(long orderId, int customerAmount, String comment) {
		Order order = new Order();

		return order;
	}

	//给已有订单添加菜品
	/*public void appendOrder(long orderId, List<CreateOrder.MyOrderItme> items, ResultCallback<Integer> resultCallback) {
	    internalService.appendOrder(orderId, items).map(new Func1<PosResponse<Integer>, Integer>() {
            @Override
            public Integer call(PosResponse<Integer> orderPosResponse) {
                if (orderPosResponse.isSuccessful()) {
                    return orderPosResponse.getContent();
                }
                    PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, Constant.SERVICE_TETURN_ERROR);
                    throw Exceptions.propagate(exception);

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));

    }*/

	/**
	 * 把订单fromOrder合并到订单toOrder中
	 *
	 * @return 合并后的订单
	 */

	public void mergeOrder(String appId, String brandId, String storeId, long fromOrder, long toOrder, ResultCallback<Integer> resultCallback) {
		internalService.margeOrder(appId, brandId, storeId, fromOrder, toOrder)
				.map(new Func1<PosResponse<Integer>, Integer>() {
					@Override
					public Integer call(PosResponse<Integer> orderPosResponse) {
						if (orderPosResponse.isSuccessful()) {
							return orderPosResponse.getContent();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);

					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}

	/**
	 * 全单退菜
	 *
	 * @param orderId        单号
	 * @param rejectReasonId 原因
	 * @return
	 */
	public boolean rejectOrder(long orderId, int rejectReasonId) {
		return false;
	}


	/**
	 * 查询网上的订单
	 *
	 * @param preordertime   单位分钟；是指查询离就餐还有多少时间的订单
	 * @param resultCallback
	 */
	public void syncNetOrders(int preordertime, ResultCallback<List<Order>> resultCallback) {
		PosInfo                   posInfo             = PosInfo.getInstance();
		final TimerTaskController timerTaskController = new TimerTaskController();
		internalService.syncNetOrders(posInfo.getAppId(), posInfo.getStoreId(), preordertime)
				.map(new Func1<PosResponse<List<Order>>, List<Order>>() {
					@Override
					public List<Order> call(PosResponse<List<Order>> orderPosResponse) {
						PosServiceException exception = null;
						if (orderPosResponse.isSuccessful()) {
							//将轮训到的订单放入到map中 方便后面查询订单下单状态 防止重复下单
							timerTaskController.setReceiveOrderList(orderPosResponse.getContent());
							return NetOrderController
									.setNetOrderList(orderPosResponse.getContent());
						} else {
							if (orderPosResponse.getResult() == 1035)//token错误
							{
								exception = new PosServiceException(ErrorCode.TOKEN_ERROR, orderPosResponse
										.getErrmsg());
							} else {
								exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
										.getErrmsg());
							}
						}
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Order>>(resultCallback));
	}


	/**
	 * 确认接收网上的订单
	 *
	 * @param netorderid     网上订单的订单号
	 * @param callnumber     取餐号
	 * @param resultCallback 终端id
	 */
	public void confirmNetOrder(String netorderid, String orderid, String callnumber, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.confirmNetOrder(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), netorderid, orderid, callnumber, posInfo.getTerminalId())
				.map(new Func1<PosResponse, Integer>() {
					@Override
					public Integer call(PosResponse response) {
						if (response.getResult() == 0) {
							return 0;
						}
						PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}

	/**
	 * 拒绝网上的订单
	 *
	 * @param netorderid     网上订单的订单号
	 * @param resultCallback 拒绝的理由
	 */
	public void rejectNetOrder(long netorderid, String rejectreason, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.rejectNetOrder(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), String.valueOf(netorderid), rejectreason)
				.map(new Func1<PosResponse, Integer>() {
					@Override
					public Integer call(PosResponse response) {
						if (response.isSuccessful()) {
							return 0;
						}

						PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}


	/**
	 * 刷新网络订单界面数据
	 */
	public void refrushOrderData() {
		try {
			final Store  store        = Store.getInstance(MyApplication.getInstance());
			OrderService orderService = OrderService.getInstance();
			orderService.syncNetOrders(store.getPreordertime(), new ResultCallback<List<Order>>() {
				@Override
				public void onResult(List<Order> result) {
					if (!ToolsUtils.isList(result)) {
						EventBus.getDefault()
								.post(new PosEvent(Constant.EventState.PUT_NET_ORDER, result));
					}
				}

				@Override
				public void onError(PosServiceException e) {
					MyApplication.getInstance().ShowToast(e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * KDS下单
	 *
	 * @param orderdata
	 * @param resultCallback
	 */
	public void kdsCreatOrder(String orderdata, ResultCallback<KDSResponse> resultCallback) {
		//        internalKdsService.newOrderKds(orderdata).subscribeOn(Schedulers.io())
		//                .observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<KDSResponse>(resultCallback));
		try {
			internalKdsService.newOrderKds(orderdata).map(new Func1<KDSResponse, KDSResponse>() {
				@Override
				public KDSResponse call(KDSResponse response) {
					if (response.isSuccess()) {
						return response;
					}

					PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
							.getMsg());
					throw Exceptions.propagate(exception);
				}
			}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
					.subscribe(new ResultSubscriber<KDSResponse>(resultCallback));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * KDS退单
	 *
	 * @param orderId
	 * @param resultCallback
	 */
	public void kdsDeleteOrder(String orderId, ResultCallback<Boolean> resultCallback) {
		internalKdsService.deleteOrderKds(orderId).map(new Func1<KDSResponse, Boolean>() {
			@Override
			public Boolean call(KDSResponse response) {
				if (response.isSuccess()) {
					return response.isSuccess();
				}

				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getMsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Boolean>(resultCallback));
	}

	/**
	 * 检测Kds连接连接情况
	 *
	 * @param resultCallback
	 */
	public void kdsConnectCheck(ResultCallback<Boolean> resultCallback) {
		internalKdsService.connectionCheck().map(new Func1<KDSResponse, Boolean>() {
			@Override
			public Boolean call(KDSResponse response) {
				if (response.isSuccess()) {
					return response.isSuccess();
				}

				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getMsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Boolean>(resultCallback));
	}

	/**
	 * 修改订单桌台
	 *
	 * @param netOrderId
	 * @param newTableName
	 * @param resultCallback
	 */
	public void kdsChangeOrderTable(final long netOrderId, final String newTableName, ResultCallback<Boolean> resultCallback) {
		internalKdsService.changOrderTable(netOrderId + "", newTableName)
				.map(new Func1<KDSResponse, Boolean>() {
					@Override
					public Boolean call(KDSResponse response) {
						if (response.isSuccess()) {
							return response.isSuccess();
						}

						PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
								.getMsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Boolean>(resultCallback));
	}


	/**
	 * KDS删菜
	 *
	 * @param Deletedishes
	 * @param resultCallback
	 */
	public void kdsDeleteDish(String Deletedishes, ResultCallback<Boolean> resultCallback) {
		internalKdsService.deleteDishKds(Deletedishes).map(new Func1<KDSResponse, Boolean>() {
			@Override
			public Boolean call(KDSResponse response) {
				if (response.isSuccess()) {
					return response.isSuccess();
				}

				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getMsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Boolean>(resultCallback));
	}

	/**
	 * 挂单==》起单
	 *
	 * @param orderId
	 * @param resultCallback
	 */
	public void resumeOrder(long orderId, final Order order, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.resumeOrder(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), orderId).map(new Func1<PosResponse<Integer>, Integer>() {
			@Override
			public Integer call(PosResponse<Integer> orderPosResponse) {
				if (orderPosResponse.isSuccessful()) {
					TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.PRINTER_ORDER, order));
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, order));
					return 0;
				}
				PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
						.getErrmsg());
				throw Exceptions.propagate(exception);

			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}

	/**
	 * 修改下单菜品的价格
	 *
	 * @param orderItemId
	 * @param newCost
	 * @param resultCallback
	 */
	public void updataOrderCost(long orderItemId, BigDecimal newCost, ResultCallback<PosResponse> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.updataOrderCost(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), orderItemId, newCost).map(new Func1<PosResponse, PosResponse>() {
			@Override
			public PosResponse call(PosResponse response) {
				if (response.isSuccessful()) {
					return response;
				}
				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<PosResponse>(resultCallback));
	}

	/**
	 * 日结
	 *
	 * @param resultCallback
	 */
	public void endDailyBusiness(ResultCallback<PosResponse> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService
				.endDailyBusiness(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<PosResponse, PosResponse>() {
					@Override
					public PosResponse call(PosResponse response) {
						if (response.isSuccessful()) {
							return response;
						}
						PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<PosResponse>(resultCallback));
	}

	/**
	 * 验证会员信息
	 *
	 * @param account        会员卡号或者手机号
	 * @param amount         人数
	 * @param resultCallback
	 */
	public void validateUserCommitDeal(String account, int amount, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.validateUserCommitDeal(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), account, amount).map(new Func1<PosResponse, Integer>() {
			@Override
			public Integer call(PosResponse response) {
				if (response.getResult() == 0) {
					return 0;
				}

				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}

	//获取一个门店的所有订单
	public void getMemberOrderList(String appId, String brandId, String storeId, int page, int limit, final ResultCallback<MemberResponse> resultCallback) {
		internalService
				.getMemberOrderList(appId, brandId, storeId, page, limit, "created_at", "DESC")
				.map(new Func1<MemberResponse, MemberResponse>() {
					@Override
					public MemberResponse call(MemberResponse listPosResponse) {
						if (listPosResponse.isSuccessful()) {
							return listPosResponse;
						}
						PosServiceException exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, listPosResponse
								.getErrmsg());
						throw Exceptions.propagate(exception);

					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<MemberResponse>(resultCallback));
	}


	/**
	 * 检测是否取餐
	 *
	 * @param orderId
	 * @param resultCallback
	 */
	public void checkServerStatus(long orderId, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService.checkServerStatus(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), orderId).map(new Func1<PosResponse, Integer>() {
			@Override
			public Integer call(PosResponse response) {
				if (response.getResult() == 0) {
					return 0;
				}

				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}

	//根据订单号获取订单详情
	public void getHistoryOrderInfo(String startTime, String endTime, final ResultCallback<List<Order>> resultCallback) {

		PosInfo posInfo = PosInfo.getInstance();
		internalService.getOrderHistoryInfo(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), startTime, endTime)
				.map(new Func1<PosResponse<List<Order>>, List<Order>>() {
					@Override
					public List<Order> call(PosResponse<List<Order>> orderPosResponse) {
						PosServiceException exception = null;
						if (orderPosResponse.isSuccessful()) {
							return orderPosResponse.getContent();
						} else {
							if (orderPosResponse.getResult() == 1035)//token错误
							{
								exception = new PosServiceException(ErrorCode.TOKEN_ERROR, orderPosResponse
										.getErrmsg());
							} else {
								exception = new PosServiceException(ErrorCode.UNKNOWN_ERROR, orderPosResponse
										.getErrmsg());
							}
						}
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<Order>>(resultCallback));
	}


	/**
	 * 正扫威富通生成二维码
	 *
	 * @param wftPayType
	 * @param totalFee
	 * @param orderTradeNo
	 * @param resultCallback
	 */
	public void createCodeUrl(int wftPayType, int totalFee, String orderTradeNo, ResultCallback<WftRespOnse> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		Store   store   = Store.getInstance(MyApplication.getInstance().getContext());
		internalService.createCodeUrl(posInfo.getAppId(),
				posInfo.getStoreId(), posInfo.getStoreId(),
				ToolsUtils.getIPAddress(MyApplication.getInstance().getContext()),
				ToolsUtils.handleCarDish(Cart.getDishItemList()), totalFee, orderTradeNo, store
						.getTerminalMac(),
				posInfo.getRealname(), store.getTerminalMac(), wftPayType)
				.map(new Func1<PosResponse<WftRespOnse>, WftRespOnse>() {
					@Override
					public WftRespOnse call(PosResponse<WftRespOnse> response) {
						if (response.getResult() == 0) {
							return response.getData();
						}
						PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response
								.getErrmsg());
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<WftRespOnse>(resultCallback));
	}


	/**
	 * 挂单
	 *
	 * @param orderId
	 * @param resultCallback isStatus 挂单 yes 销账 no
	 */
	public void chargeOffs(long orderId, boolean isStatus, ResultCallback<Integer> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		Integer status  = null;
		if (!isStatus) {
			status = 0;
		}
		internalService.chargeOffs(posInfo.getAppId(), posInfo.getBrandId(), posInfo
				.getStoreId(), orderId, status).map(new Func1<PosResponse, Integer>() {
			@Override
			public Integer call(PosResponse response) {
				if (response.getResult() == 0) {
					return 0;
				}
				PosServiceException exception = new PosServiceException(ErrorCode.SERVER_NOT_CONFIGURED, response
						.getErrmsg());
				throw Exceptions.propagate(exception);
			}
		}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<Integer>(resultCallback));
	}


	/**
	 * 获取已挂账列表
	 *
	 * @param resultCallback
	 */
	public void getCardRecords(final ResultCallback<List<CardRecord>> resultCallback) {
		PosInfo posInfo = PosInfo.getInstance();
		internalService
				.getCardRecords(posInfo.getAppId(), posInfo.getBrandId(), posInfo.getStoreId())
				.map(new Func1<PosResponse<List<CardRecord>>, List<CardRecord>>() {
					@Override
					public List<CardRecord> call(PosResponse<List<CardRecord>> orderPosResponse) {
						PosServiceException exception = null;
						if (orderPosResponse.isSuccessful()) {
							return orderPosResponse.getData();
						}
						throw Exceptions.propagate(exception);
					}
				}).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.subscribe(new ResultSubscriber<List<CardRecord>>(resultCallback));
	}


}
