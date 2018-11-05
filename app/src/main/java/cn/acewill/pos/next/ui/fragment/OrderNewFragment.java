package cn.acewill.pos.next.ui.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.ReprintController;
import cn.acewill.pos.next.common.RetreatDishController;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.PermissionCallback;
import cn.acewill.pos.next.model.Customer;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.OrderSingleReason;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.presenter.OrderPresenter;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.service.TradeService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.activity.CheckOutNewAty;
import cn.acewill.pos.next.ui.adapter.FragmentOrderNewAdp;
import cn.acewill.pos.next.ui.adapter.OrderInforNewAdp;
import cn.acewill.pos.next.ui.adapter.OrderSingleReasonAdp;
import cn.acewill.pos.next.ui.adapter.PayMentListAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.WindowUtil;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.OnRecyclerItemClickListener;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class OrderNewFragment extends BaseFragment implements DialogView,
		SwipeRefreshLayout.OnRefreshListener, FragmentOrderNewAdp.RefrushLisener {
	@BindView(R.id.order_lv)
	RecyclerView       orderLv;
	@BindView(R.id.order_srl)
	SwipeRefreshLayout orderSrl;
	@BindView(R.id.lin_left)
	LinearLayout       linLeft;
	@BindView(R.id.lin_center)
	RelativeLayout     linCenter;
	@BindView(R.id.lin_right)
	RelativeLayout     linRight;
	@BindView(R.id.rel_center_count)
	RelativeLayout     relCenterCount;
	@BindView(R.id.lin_right_bottom)
	LinearLayout       linRightBottom;
	@BindView(R.id.btn_receive)
	TextView           btnReceive;
	@BindView(R.id.btn_refuse)
	TextView           btnRefuse;
	@BindView(R.id.order_dish_count)
	TextView           orderDishCount;
	@BindView(R.id.order_customerAmount)
	TextView           orderCustomerAmount;
	@BindView(R.id.order_creat_time)
	TextView           orderCreateTime;
	@BindView(R.id.order_price)
	TextView           orderPrice;
	@BindView(R.id.order_activeMoney)
	TextView           orderActiveMoney;
	@BindView(R.id.order_cost)
	TextView           orderCost;
	@BindView(R.id.member_number)
	TextView           memberNumber;
	@BindView(R.id.member_name)
	TextView           memberName;
	@BindView(R.id.member_level)
	TextView           memberLevel;
	@BindView(R.id.member_MemberConsumeCost)
	TextView           memberMemberConSumeCost;
	@BindView(R.id.sale_name)
	TextView           saleName;
	@BindView(R.id.sale_phone)
	TextView           salePhone;
	@BindView(R.id.sale_address)
	TextView           saleAddress;
	@BindView(R.id.sale_orderNumber)
	TextView           saleOrderNumber;
	@BindView(R.id.order_refundPrice)
	TextView           orderFefundPrice;
	@BindView(R.id.order_refundTime)
	TextView           orderRefundTime;
	@BindView(R.id.btn_refundDish)
	TextView           btnRefunDish;
	@BindView(R.id.btn_refundOrder)
	TextView           btnRefunOrder;
	@BindView(R.id.btn_rePrint)
	TextView           btnRePrint;
	@BindView(R.id.btn_reCheckOut)
	TextView           btnReCheckOutr;
	@BindView(R.id.btn_revokeInvoice)
	TextView           btnRevokeInvoice;
	@BindView(R.id.lin_payType)
	LinearLayout       linPayType;
	@BindView(R.id.lin_member)
	LinearLayout       linMember;
	@BindView(R.id.lin_saleOut)
	LinearLayout       linSaleOut;
	@BindView(R.id.lv_payType)
	ScrolListView      lvPayType;
	@BindView(R.id.dish_list)
	ScrolListView      dishList;
	@BindView(R.id.sc_scroll)
	ScrollView         scScroll;
	@BindView(R.id.search_cotent)
	CommonEditText     edMemberNumber;
	@BindView(R.id.search_clear)
	LinearLayout       searchClear;

	private Store    store;
	private PosInfo  posInfo;
	private Cart     cart;
	private UserData mUserData;

	private String TAG = "OrderNewFragment";
	private FragmentOrderNewAdp adapter;
	private OrderInforNewAdp    orderInforNewAdp;
	private PayMentListAdp      payMentListAdp;
	private List<Order> historyOrderList = new CopyOnWriteArrayList<>();
	private int         lastVisibleItem  = 0;
	private Intent intent;
	private Order  selectOrder;
	private int page      = 0;
	private int limit     = 800;
	private int orderType = -1;//-1:全部;0:堂食;1:外带;2:外卖
	private int payStatus = -1;//-1:全部;0:未支付;1:已经支付;2:已经退款

	private OrderPresenter orderPresenter;
	private int RECHECKOUT = 100;//反结账跳转

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_order_new, container, false);
		ButterKnife.bind(this, view);
		store = Store.getInstance(aty);
		mUserData = UserData.getInstance(aty);
		posInfo = PosInfo.getInstance();
		cart = Cart.getInstance();
		Log.i(TAG, "onCreateView");
		//        linLeft.setVisibility(View.VISIBLE);
		//        linCenter.setVisibility(View.VISIBLE);
		//        linRight.setVisibility(View.VISIBLE);
		initView();
		loadData();
		return view;
	}

	private void initView() {
		intent = new Intent();
		orderSrl.setOnRefreshListener(this);
		orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
		adapter = new FragmentOrderNewAdp(aty, this);
		payMentListAdp = new PayMentListAdp(aty);
		orderPresenter = new OrderPresenter(this);
		final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aty);
		linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
		orderLv.setLayoutManager(linearLayoutManager);
		orderLv.setAdapter(adapter);
		adapter.changeMoreStatus(adapter.NO_MORE);
		lvPayType.setAdapter(payMentListAdp);

		orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
				if (lastVisibleItem + 1 == adapter
						.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
					adapter.setLoadType(adapter.UP_LOAD_TYPE);
					adapter.changeMoreStatus(adapter.LOADING);
					orderPresenter.getAllOrders(++page, limit, orderType, payStatus);
				}
			}
		});

		orderLv.addOnItemTouchListener(new OnRecyclerItemClickListener(orderLv) {
			@Override
			public void onItemClick(RecyclerView.ViewHolder viewHolder) {
				int position = viewHolder.getAdapterPosition();
				selectOrder = ToolsUtils.cloneTo((Order) adapter.getItem(position));
				if (selectOrder != null) {
					showCenterRightInfo(true);
					adapter.setSelectPosition(position);
					showOrderInfo(selectOrder);
				}
			}

			@Override
			public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {

			}
		});

		orderInforNewAdp = new OrderInforNewAdp(aty);
		dishList.setAdapter(orderInforNewAdp);
		RetreatDishController.addListener(new RetreatDishController.ChangeListener() {
			@Override
			public void contentChanged() {
				adapter.notifyDataSetChanged();
			}
		});


		//edtext的控件内容监听
		edMemberNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() > 0) {
					searchClear.setVisibility(View.VISIBLE);
					List<Order> orders = null;
					if (ToolsUtils.isNumeric(s.toString())) {
						orders = getOrderInfoByOrderId(historyOrderList, s.toString());
						adapter.setData(orders);
						if (orders != null && orders.size() > 0 && orders.get(0) != null) {
							showCenterRightInfo(true);
							adapter.setSelectPosition(0);
							selectOrder = ToolsUtils.cloneTo(orders.get(0));
							showOrderInfo(selectOrder);
						}
						adapter.changeMoreStatus(adapter.NO_MORE);
						orderSrl.setRefreshing(false);
					}
				} else {
					searchClear.setVisibility(View.GONE);
					setOrderData(historyOrderList);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	private List<Order> getOrderInfoByOrderId(List<Order> orderList, String orderId) {
		List<Order> orderS = new CopyOnWriteArrayList<>();
		if (orderList != null && orderList.size() > 0) {
			for (Order order : orderList) {
				if ((order.getId() + "").contains(orderId) || (order.getTableNames() + "")
						.contains(orderId) || (order.getCallNumber() + "").contains(orderId)) {
					orderS.add(order);
				}
			}
		}
		return orderS;
	}

	@Override
	public void onResume() {
		super.onResume();
	}


	private void loadData() {
		showCenterRightInfo(false);
		adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
		page = 0;
		orderPresenter.getAllOrders(page, limit, orderType, payStatus);
	}

	@Override
	public void onRefresh() {
		loadData();
	}

	private void setOrderData(List<Order> orderList) {
		historyOrderList = ToolsUtils.cloneTo(orderList);
		showCenterRightInfo(false);
		if (orderList != null && orderList.size() > 0) {
			adapter.setData(orderList);

			if (orderList.get(0) != null) {
				showCenterRightInfo(true);
				adapter.setSelectPosition(0);
				selectOrder = ToolsUtils.cloneTo(orderList.get(0));
				showOrderInfo(selectOrder);
			}

			if (orderList.size() < limit) {
				adapter.changeMoreStatus(adapter.NO_MORE);
			} else {
				adapter.changeMoreStatus(adapter.LOAD_MORE);
			}
		} else {
			adapter.changeMoreStatus(adapter.NO_MORE);
			showCenterRightInfo(false);
		}
		orderSrl.setRefreshing(false);
	}


	@Override
	public void refrush() {
		loadData();
	}


	private void showCenterRightInfo(boolean isShow) {
		if (isShow) {
			relCenterCount.setVisibility(View.VISIBLE);
			linRightBottom.setVisibility(View.VISIBLE);
			scScroll.setVisibility(View.VISIBLE);
		} else {
			selectOrder = null;
			relCenterCount.setVisibility(View.GONE);
			linRightBottom.setVisibility(View.GONE);
			scScroll.setVisibility(View.GONE);
			adapter.setSelectPosition(-1);
		}
	}

	/**
	 * 显示订单信息
	 *
	 * @param order
	 */
	private void showOrderInfo(Order order) {
		if (order != null) {
			getOrderById(order.getId() + "");
		}
	}

	//根据订单号获取订单详情
	private void getOrderById(String orderId) {
		OrderService orderService = null;
		try {
			orderService = OrderService.getInstance();
			orderService.getOrderInfoById(orderId, new ResultCallback<Order>() {
				@Override
				public void onResult(Order order) {
					System.out.println(ToolsUtils.getPrinterSth(order));
					if (order != null) {
						selectOrder = ToolsUtils.cloneTo(order);
						if (order.getItemList() != null && order.getItemList().size() > 0) {
							RetreatDishController.setItemList(order.getItemList());
							RetreatDishController.setTempItemList(order.getItemList());
						}
						List<OrderItem> orderItems = order.getItemList();
						orderInforNewAdp.setData(orderItems);

						int dishCount = 0;
						if (!ToolsUtils.isList(orderItems)) {
							for (OrderItem oi : orderItems) {
								dishCount += oi.getQuantity();
							}
						}
						orderDishCount.setText("点菜数量:" + dishCount);
						int customerAmount = order.getCustomerAmount() == 0 ? 1 : order
								.getCustomerAmount();
						orderCustomerAmount.setText("就餐人数:" + customerAmount);

						showRightOrderInfo(order);
					}
				}

				@Override
				public void onError(PosServiceException e) {
					if (!TextUtils.isEmpty(e.getMessage())) {
						Log.i("订单详情获取失败!", e.getMessage());
						showToast(e.getMessage());
					}

				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 显示右边订单详情
	 *
	 * @param result
	 */
	private String paymentstate = "";//订单状态

	private void showRightOrderInfo(Order result) {
		float price = Float.parseFloat(result.getTotal());
		float cost  = Float.parseFloat(result.getCost());
		orderPrice.setText("应收金额:￥ " + price);
		orderCost.setText("实收金额:￥ " + cost);
		orderActiveMoney.setText("优惠金额:￥ " + new BigDecimal(price - cost)
				.setScale(2, BigDecimal.ROUND_HALF_UP));
		paymentstate = result.getPaymentStatus().toString();
		if ("REFUND".equals(paymentstate))//已退单
		{
			orderFefundPrice.setVisibility(View.VISIBLE);
			orderRefundTime.setVisibility(View.VISIBLE);
			orderCreateTime.setVisibility(View.GONE);
			btnRefunDish.setVisibility(View.GONE);
			btnRefunOrder.setVisibility(View.GONE);
			btnRePrint.setVisibility(View.GONE);
			btnReCheckOutr.setVisibility(View.GONE);
			orderFefundPrice.setText("退款金额:￥ " + price);
			orderRefundTime.setText("退单时间:" + TimeUtil.getStringTimeLong(result.getCreatedAt()));
		} else {
			orderFefundPrice.setVisibility(View.GONE);
			orderRefundTime.setVisibility(View.GONE);
			orderCreateTime.setVisibility(View.VISIBLE);
			btnRefunDish.setVisibility(View.VISIBLE);
			btnRefunOrder.setVisibility(View.VISIBLE);
			btnRePrint.setVisibility(View.VISIBLE);
			btnReCheckOutr.setVisibility(View.VISIBLE);
			orderCreateTime.setText("下单时间:" + TimeUtil.getStringTimeLong(result.getCreatedAt()));
		}

		linPayType.setVisibility(View.GONE);
		linMember.setVisibility(View.GONE);
		linSaleOut.setVisibility(View.GONE);
		if (!ToolsUtils.isList(result.getPaymentList())) {
			linPayType.setVisibility(View.VISIBLE);
			payMentListAdp.setData(result.getPaymentList());
		}
		Account account = result.getAccountMember();
		if (result.getAccountMember() != null) {
			linMember.setVisibility(View.VISIBLE);
			memberNumber.setText("会员卡号:" + account.getUno() + "(" + ToolsUtils
					.replacePhone(account.getPhone()) + ")");
			memberName.setText("会员姓名:" + ToolsUtils.getStarString2(account.getName(), 1, 0));
			memberLevel.setText("卡  等  级:" + account.getGradeName());
			memberMemberConSumeCost.setText("消费金额:￥ " + account.getMemberConsumeCost());
		}
		String type = result.getOrderType();
		if (type.equals("SALE_OUT")) {
			linSaleOut.setVisibility(View.VISIBLE);
			saleName.setText("姓        名:" + result.getCustomerName());
			salePhone.setText("电        话:" + result.getCustomerPhoneNumber());
			saleAddress.setText("送餐地址:" + result.getCustomerAddress());
			saleOrderNumber.setText("平台单号:" + result.getThirdPlatfromOrderIdDaySeq());
		}

		if (!result.getInvoicedState()) {
			btnRefunDish.setVisibility(View.GONE);
			btnRefunOrder.setVisibility(View.GONE);
			btnReCheckOutr.setVisibility(View.GONE);
			btnRevokeInvoice.setVisibility(View.VISIBLE);
		} else {
			btnRefunDish.setVisibility(View.VISIBLE);
			btnRefunOrder.setVisibility(View.VISIBLE);
			btnReCheckOutr.setVisibility(View.VISIBLE);
			btnRevokeInvoice.setVisibility(View.GONE);
		}
	}


	@Override
	public void showDialog() {
		showProgress("正在获取订单详情...");
	}

	@Override
	public void dissDialog() {
		dissmiss();
	}

	@Override
	public void showError(PosServiceException e) {
		orderSrl.setRefreshing(false);
	}

	@Override
	public <T> void callBackData(T t) {
		List<Order> orders = (List<Order>) t;
		setOrderData(orders);
	}


	private boolean logicSelectDishCount(final List<OrderItem> orderItemList) {
		int selectCount = 0;//已经选择退的份数
		if (orderItemList != null && orderItemList.size() > 0) {
			int size = orderItemList.size();
			for (int i = 0; i < size; i++) {
				OrderItem orderItem = orderItemList.get(i);
				selectCount += orderItem.getRejectedQuantity();
			}
		}
		if (selectCount != 0 && selectCount > 0) {
			return true;
		}
		return false;
	}


	/**
	 * 获取退菜、退单原因
	 */
	private void getSingleReason(final int refundType) {
		try {
			TableService tableService = TableService.getInstance();
			tableService.getSingleReason(new ResultCallback<List<OrderSingleReason>>() {
				@Override
				public void onResult(List<OrderSingleReason> result) {
					if (result != null && result.size() > 0) {
						final Dialog dialog     = DialogUtil
								.createDialog(aty, R.layout.dialog_rund, 7, LinearLayout.LayoutParams.WRAP_CONTENT);
						ListView     reasonList = (ListView) dialog.findViewById(R.id.reason_list);
						TextView     cancle     = (TextView) dialog.findViewById(R.id.cancle);
						TextView     ok         = (TextView) dialog.findViewById(R.id.ok);
						TextView     rund_title = (TextView) dialog.findViewById(R.id.rund_title);

						rund_title.setText("请选择退菜原因");
						ok.setText("退菜");

						final OrderSingleReasonAdp adp = new OrderSingleReasonAdp(aty);
						reasonList.setAdapter(adp);
						adp.setData(result);
						adp.setCurrent_select(0);

						cancle.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

						ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								//退菜
								if (refundType == Constant.TABLE_REFUN) {
									refundDishOrOrder(adp.getSelectId());
								}
								//退单
								else if (refundType == Constant.TABLE_REFUN_ORDER) {
									userRefundOrder(adp.getSelectId());
								}
							}
						});

						reasonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								adp.setCurrent_select(position);
							}
						});

						dialog.show();

					} else {
						showToast("获取退菜原因列表为空");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("获取退菜原因失败," + e.getMessage());
					Log.i("获取退菜原因失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}

	}

	private void refundDishOrOrder(final int reasonId) {
		removeDish(reasonId, RetreatDishController.getTempItemList());
	}

	private void removeDish(final int reasonId, final List<OrderItem> orderItemList) {
		StringBuffer sb = new StringBuffer();
		if (orderItemList != null && orderItemList.size() > 0) {
			int size         = orderItemList.size();
			int selectCount  = 0;//已经选择退的份数
			int dishCanCount = 0;//可退的份数
			for (int i = 0; i < size; i++) {
				OrderItem orderItem = orderItemList.get(i);
				int       rejected  = orderItem.getRejectedQuantity();
				if (rejected > 0) {
					sb.append(orderItem.getDishName() + rejected + "/份、");
				}
				selectCount += rejected;
				if (orderItem.quantity > 0 && orderItem.quantity != 0) {
					dishCanCount += orderItem.quantity;
				}
			}
			if (dishCanCount <= 0) {

			} else if (selectCount <= 0) {
				showToast("请先选择要退的菜品!");
				return;
			}
			//退单
			if (dishCanCount == selectCount) {

				DialogUtil.ordinaryDialog(aty, "退单", "菜品退菜份数已满,是否需要退单?", new DialogCallback() {

					@Override
					public void onConfirm() {
						if ("NOT_PAYED".equals(paymentstate)) {
							closOrder(reasonId, selectOrder.getId() + "");
						} else {
							refundOrder(reasonId);
						}
					}

					@Override
					public void onCancle() {

					}
				});

			}
			//退菜
			else {
				DialogUtil.ordinaryDialog(aty, "退菜", "是否确认退" + sb.toString(), new DialogCallback() {
					@Override
					public void onConfirm() {
						List<OrderItem> orderItemLists = new ArrayList<OrderItem>();
						for (OrderItem item : orderItemList) {
							int reject = item.rejectedQuantity;
							if (reject != 0 && reject > 0) {
								orderItemLists.add(item);
							}
						}
						if ("NOT_PAYED".equals(paymentstate)) {
							retreatDish(reasonId, orderItemLists);
						} else {
							refundDish(reasonId, orderItemLists);
						}
					}

					@Override
					public void onCancle() {

					}
				});
			}
		} else {
			showToast("请先选择要退的菜品!");
		}
	}


	//退菜(先支付后下单使用)
	private void refundDish(final int reasonId, final List<OrderItem> orderItemList) {
		try {
			showProgress();
			TradeService tradeService = TradeService.getInstance();
			UserData     userData     = UserData.getInstance(aty);
			final Order  newOrder     = ToolsUtils.cloneTo(selectOrder);
			newOrder.setItemList(ToolsUtils.cloneTo(orderItemList));
			tradeService.refundDish(selectOrder.getId() + "", reasonId, userData
					.getRealName(), newOrder, new ResultCallback<PosResponse>() {
				@Override
				public void onResult(PosResponse result) {
					dissmiss();
					if (result.isSuccessful()) {//退款\菜成功
						showToast("退菜成功");
						Log.i("退菜成功", "success");
						isJyjReDish(reasonId, orderItemList);
						EventBus.getDefault()
								.post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_DISH, orderItemList, selectOrder
										.getId() + ""));
						showCenterRightInfo(false);
						loadData();
						//                        kdsDeleteDish(orderItemList);
					} else {//退菜失败
						showToast(result.getErrmsg());
						Log.i("退菜失败", result.getErrmsg());
					}
				}

				@Override
				public void onError(PosServiceException e) {
					dissmiss();
					if (!TextUtils.isEmpty(e.getMessage())) {
						showToast(e.getMessage());
					}
					Log.i("退菜onError", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 吉野家退菜
	 */
	private void isJyjReDish(int reasonId, final List<OrderItem> orderItemList) {
		if (store.isCreateOrderJyj()) {
			try {
				TradeService tradeService = TradeService.getJyjTradeService();
				UserData     userData     = UserData.getInstance(aty);
				final Order  newOrder     = ToolsUtils.cloneTo(selectOrder);
				newOrder.setItemList(ToolsUtils.cloneTo(orderItemList));
				tradeService.refundJyjDish(selectOrder.getId() + "", reasonId, userData
						.getRealName(), newOrder, new ResultCallback<PosResponse>() {
					@Override
					public void onResult(PosResponse result) {
						if (result.isSuccessful()) {//退款\菜成功
							showToast("JYJ退菜成功");
							Log.i("退菜成功", "success");
						} else {//退菜失败
							showToast(result.getErrmsg());
							Log.i("JYJ退菜失败", result.getErrmsg());
						}
					}

					@Override
					public void onError(PosServiceException e) {
						Log.i("退菜JYJonError", e.getMessage());
						showToast("JYJ退菜失败," + e.getMessage());
					}
				});
			} catch (PosServiceException e) {
				e.printStackTrace();
			}
		}
	}


	//退单(先支付后下单使用)
	private void refundOrder(final int reasonId) {
		try {
			showProgress();
			TradeService tradeService = TradeService.getInstance();
			UserData     userData     = UserData.getInstance(aty);
			final Order  newOrder     = ToolsUtils.cloneTo(selectOrder);
			newOrder.setId(Long.valueOf(selectOrder.getId()));
			tradeService.refund(newOrder, reasonId, userData
					.getRealName(), new ResultCallback<PosResponse>() {
				@Override
				public void onResult(PosResponse result) {
					dissmiss();
					if (result.isSuccessful()) {//退款\菜成功
						showToast("退单成功");
						Log.i("退单成功", "success");
						isJyjReOrder(reasonId);
						EventBus.getDefault()
								.post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_ORDER, selectOrder
										.getId() + ""));
						showCenterRightInfo(false);
						loadData();
					} else {//退款失败
						showToast(result.getErrmsg());
						Log.i("退单失败", result.getErrmsg());
					}
				}

				@Override
				public void onError(PosServiceException e) {
					dissmiss();
					if (!TextUtils.isEmpty(e.getMessage())) {
						showToast(e.getMessage());
					}
					Log.i("退单onError", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * JYJ退单
	 *
	 * @param reasonId
	 */
	private void isJyjReOrder(int reasonId) {
		if (store.isCreateOrderJyj()) {
			try {
				TradeService tradeService = TradeService.getJyjTradeService();
				UserData     userData     = UserData.getInstance(aty);
				final Order  newOrder     = ToolsUtils.cloneTo(selectOrder);
				newOrder.setId(Long.valueOf(selectOrder.getId()));
				tradeService.refundJyj(newOrder, reasonId, userData
						.getRealName(), new ResultCallback<PosResponse>() {
					@Override
					public void onResult(PosResponse result) {
						dissmiss();
						if (result.isSuccessful()) {//退款\菜成功
							Log.i("JYJ退单成功", "success");
						} else {//退款失败
							showToast("JYJ=" + result.getErrmsg());
							Log.i("JYJ退单失败", result.getErrmsg());
						}
					}

					@Override
					public void onError(PosServiceException e) {
						if (!TextUtils.isEmpty(e.getMessage())) {
							showToast("JYJ=" + e.getMessage());
						}
						Log.i("退单JYJonError", e.getMessage());
					}
				});
			} catch (PosServiceException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 先下单后支付的关闭订单（类似于桌台退单）
	 *
	 * @param orderId
	 */
	private void closOrder(final int reasonId, final String orderId) {
		try {
			TradeService tradeService = TradeService.getInstance();
			final Order  newOrder     = ToolsUtils.cloneTo(selectOrder);
			newOrder.setId(Long.valueOf(orderId));
			tradeService.closeOrder(reasonId, newOrder, new ResultCallback() {
				@Override
				public void onResult(Object result) {
					if ((int) result == 0) {
						showToast("退单成功!");
						EventBus.getDefault()
								.post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_ORDER, orderId));
						showCenterRightInfo(false);
						loadData();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("关闭订单失败," + e.getMessage());
					Log.i("退单失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			Log.i("退单失败!", e.getMessage());
		}
	}

	//先下单后支付的退菜（类似于桌台点单退菜）
	private void retreatDish(final int reasonId, final List<OrderItem> itemList) {
		try {
			showProgress();
			TableService tableService = TableService.getInstance();
			tableService.removeDish(reasonId, selectOrder, itemList, new ResultCallback<Order>() {
				@Override
				public void onResult(Order result) {
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
					dissmiss();
					Order newOrder = selectOrder;
					newOrder.setItemList(itemList);
					showToast("退菜成功!");
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_DISH, itemList, selectOrder
									.getId() + ""));
					showCenterRightInfo(false);
					loadData();
					//                    kdsDeleteDish(itemList);
				}

				@Override
				public void onError(PosServiceException e) {
					dissmiss();
					showToast("退菜失败," + e.getMessage());
					Log.i("退菜失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			dissmiss();
		} finally {
			dissmiss();
		}
	}

	/**
	 * 补打小票
	 */
	private void rePrinter() {
		Printer printer1 = new Printer("补打结账单(带电子发票二维码)");
		//        Printer printer2 = new Printer("补打客用单");
		Printer printer2 = new Printer("补打结账单(不带电子发票二维码)");
		Printer printer3 = new Printer("补打厨房小票(分单)");
		Printer printer4 = new Printer("补打厨房小票(总单)");
		if (ReprintController.getRePrinterList() != null && ReprintController.getRePrinterList()
				.size() > 0) {
			ReprintController.getRePrinterList().clear();
		}
		ReprintController.getRePrinterList().add(printer1);
		ReprintController.getRePrinterList().add(printer2);
		ReprintController.getRePrinterList().add(printer3);
		ReprintController.getRePrinterList().add(printer4);

		DialogUtil.ReprintDialog(aty, "补打小票", ReprintController
				.getRePrinterList(), new DialogTCallback() {
			@Override
			public void onConfirm(Object o) {
				boolean isSelectAll = false;
				if (ReprintController.getRePrinterList().get(0).isSelect) {
					selectOrder.setReprintState(true);
					selectOrder.setPrintQrcode(true);
					TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
					isSelectAll = true;
					EventBus.getDefault()
							.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, selectOrder));
				}
				if (ReprintController.getRePrinterList().get(1).isSelect) {
					if (isSelectAll) {
						new Handler().postDelayed(new Runnable() {
							public void run() {
								selectOrder.setReprintState(true);
								selectOrder.setPrintQrcode(false);
								TimerTaskController.getInstance()
										.setStopSyncNetOrder(false);//停止轮训网上订单
								EventBus.getDefault()
										.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, selectOrder));
							}
						}, 3000);
					} else {
						selectOrder.setReprintState(true);
						selectOrder.setPrintQrcode(false);
						TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
						EventBus.getDefault()
								.post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, selectOrder));
					}
				}
				if (ReprintController.getRePrinterList().get(2).isSelect) {
					if (selectOrder != null && selectOrder.getItemList() != null && selectOrder
							.getItemList().size() > 0) {
						final List<OrderItem> orderItemList = TraverseAlreadyCheckOutDish(ToolsUtils
								.cloneTo(selectOrder));
						DialogUtil
								.rushDishDialog(aty, "补打厨房小票(分单)", orderItemList, Constant.TABLE_RUSH, new DialogCallback() {
									@Override
									public void onConfirm() {
										//                                for (OrderItem orderItem : orderItemList) {
										ToolsUtils.removeItemForSelectDish(orderItemList, false);
										//                                }
										if (orderItemList.size() >= 1) {
											Order expediteOrder = ToolsUtils.cloneTo(selectOrder);
											expediteOrder.setItemList(orderItemList);
											expediteOrder
													.setTableStyle(Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
											expediteOrder.setRushDishType(1);
											EventBus.getDefault()
													.post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, expediteOrder));
										}
									}

									@Override
									public void onCancle() {

									}
								});
					}
				}
				if (ReprintController.getRePrinterList().get(3).isSelect) {
					if (selectOrder != null && selectOrder.getItemList() != null && selectOrder
							.getItemList().size() > 0) {
						final List<OrderItem> orderItemList = TraverseAlreadyCheckOutDish(ToolsUtils
								.cloneTo(selectOrder));
						DialogUtil
								.rushDishDialog(aty, "补打厨房小票(总单)", orderItemList, Constant.TABLE_RUSH, new DialogCallback() {
									@Override
									public void onConfirm() {
										//                                for (OrderItem orderItem : orderItemList) {
										ToolsUtils.removeItemForSelectDish(orderItemList, false);
										//                                }
										if (orderItemList.size() >= 1) {
											Order expediteOrder = ToolsUtils.cloneTo(selectOrder);
											expediteOrder.setItemList(orderItemList);
											expediteOrder
													.setTableStyle(Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
											expediteOrder.setRushDishType(2);
											EventBus.getDefault()
													.post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, expediteOrder));
										}
									}

									@Override
									public void onCancle() {

									}
								});
					}
				}

			}

			@Override
			public void onCancle() {

			}
		});
	}

	/**
	 * 遍历已经下单的菜品
	 */
	private List<OrderItem> TraverseAlreadyCheckOutDish(Order tableOrder) {
		List<OrderItem> orderItemList = new CopyOnWriteArrayList<>();
		if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList()
				.size() > 0) {
			orderItemList = ToolsUtils.cloneTo(tableOrder.getItemList());
			if (orderItemList != null && orderItemList.size() > 0) {
				orderItemList.clear();
			} else {
				orderItemList = new CopyOnWriteArrayList<>();
			}
			for (OrderItem item : tableOrder.getItemList()) {
				item.isSelectItem = false;
				orderItemList.add(item);
			}
		}
		return orderItemList;
	}

	/**
	 * 反结账
	 */
	private void reCheckoutOrder() {
		//判断是否有打结账权限
		PowerController.isLogicPower(aty, PowerController.CHECK_OUT_BACK, new PermissionCallback() {
			@Override
			public void havePermission() {
				getreCheckOutReason();
			}

			@Override
			public void withOutPermission() {

			}
		});
	}

	/**
	 * 获取退菜、退菜原因
	 */
	private void getreCheckOutReason() {
		try {
			TableService tableService = TableService.getInstance();
			tableService.getReCheckoutReason(new ResultCallback<List<OrderSingleReason>>() {
				@Override
				public void onResult(List<OrderSingleReason> result) {
					if (result != null && result.size() > 0) {
						final boolean[]    isReCheckOut = {false};
						final Dialog       dialog       = DialogUtil
								.createDialog(aty, R.layout.dialog_recheckout, 7, LinearLayout.LayoutParams.WRAP_CONTENT);
						ListView           reasonList   = (ScrolListView) dialog
								.findViewById(R.id.reason_list);
						TextView           cancle       = (TextView) dialog
								.findViewById(R.id.cancle);
						TextView           ok           = (TextView) dialog.findViewById(R.id.ok);
						TextView           rund_title   = (TextView) dialog
								.findViewById(R.id.rund_title);
						final LinearLayout type_no      = (LinearLayout) dialog
								.findViewById(R.id.type_no);
						final LinearLayout type_yes     = (LinearLayout) dialog
								.findViewById(R.id.type_yes);

						type_no.setSelected(true);
						type_yes.setSelected(false);
						type_no.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								type_no.setSelected(true);
								type_yes.setSelected(false);
								isReCheckOut[0] = false;
							}
						});
						type_yes.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								type_no.setSelected(false);
								type_yes.setSelected(true);
								isReCheckOut[0] = true;
							}
						});


						rund_title.setText("请选择反结账原因");
						ok.setText("反结账");

						final OrderSingleReasonAdp adp = new OrderSingleReasonAdp(aty);
						reasonList.setAdapter(adp);
						adp.setData(result);
						adp.setCurrent_select(0);

						cancle.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}
						});

						ok.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								reverseCheckOut(isReCheckOut[0], adp.getSelectId());
							}
						});

						reasonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								adp.setCurrent_select(position);
							}
						});

						dialog.show();

					} else {
						showToast("获取反结原因列表为空");
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("获取反结原因失败," + e.getMessage());
					Log.i("获取反结原因失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
		}

	}


	/**
	 * 反结账操作
	 */
	private void reverseCheckOut(final boolean isReCheckOut, final int reasonId) {
		if (selectOrder != null && selectOrder.getItemList() != null && selectOrder.getItemList()
				.size() > 0) {
			int        size  = selectOrder.getItemList().size();
			BigDecimal count = new BigDecimal("0.00");
			for (int i = 0; i < size; i++) {
				if (selectOrder.getItemList().size() == i) {
					break;
				}
				OrderItem orderitem    = selectOrder.getItemList().get(i);
				boolean   isDeleteItem = false;
				if (0 >= orderitem.getQuantity()) {
					selectOrder.getItemList().remove(i);
					isDeleteItem = true;
				} else {
					count = count.add(orderitem.getCost()
							.multiply(new BigDecimal(orderitem.getQuantity())));
				}
				if (size == 1) {
					break;
				}
				if (isDeleteItem) {
					i -= 1;
				}
			}
			selectOrder.setTotal(count.toString());
			selectOrder.setCost(count.toString());
			if (selectOrder.getItemList().size() > 0) {
				Intent tableIntent = new Intent(aty, CheckOutNewAty.class);
				tableIntent.putExtra("source", Constant.EventState.SOURCE_TABLE_ORDER);
				tableIntent.putExtra("tableId", selectOrder.getTableId());
				tableIntent.putExtra("reasonId", reasonId);
				tableIntent.putExtra("isReCheckOut", isReCheckOut);
				Bundle tableBun = new Bundle();
				tableBun.putSerializable("tableOrder", selectOrder);
				tableIntent.putExtras(tableBun);
				tableIntent.putExtra("reverseCheckOutFlag", Constant.REVERSE_CHECKOUT);
				startActivityForResult(tableIntent, RECHECKOUT);
			}
		}
	}

	/**
	 * 退菜和退单的操作
	 */
	private void refundDishAndRefundOrder() {
		//判断是否有退菜权限
		PowerController.isLogicPower(aty, PowerController.REFUND_DISH, new PermissionCallback() {
			@Override
			public void havePermission() {
				getSingleReason(Constant.TABLE_REFUN);
			}

			@Override
			public void withOutPermission() {

			}
		});
	}


	/**
	 * 退单的操作
	 */
	private void refundOrder() {
		//判断是否有退菜权限
		PowerController.isLogicPower(aty, PowerController.REFUND_DISH, new PermissionCallback() {
			@Override
			public void havePermission() {
				getSingleReason(Constant.TABLE_REFUN_ORDER);
			}

			@Override
			public void withOutPermission() {

			}
		});
	}

	/**
	 * 收银员退单操作
	 */
	private void userRefundOrder(final int reasonId) {
		if (selectOrder != null && selectOrder.getItemList() != null && selectOrder.getItemList()
				.size() > 0) {
			List<OrderItem> orderItems = ToolsUtils.cloneTo(selectOrder.getItemList());
			int             size       = orderItems.size();
			for (int i = 0; i < size; i++) {
				if (orderItems.size() == i) {
					break;
				}
				OrderItem orderitem    = orderItems.get(i);
				boolean   isDeleteItem = false;
				if (0 >= orderitem.getQuantity()) {
					orderItems.remove(i);
					isDeleteItem = true;
				} else {
					orderitem.setRejectedQuantity(orderitem.getQuantity());
				}
				if (size == 1) {
					break;
				}
				if (isDeleteItem) {
					i -= 1;
				}
			}
			if (orderItems != null && orderItems.size() > 0) {
				removeDish(reasonId, orderItems);
			} else {
				showToast("没有可退菜品!");
			}
		}
	}


	@OnClick({R.id.btn_refundDish, R.id.btn_refundOrder, R.id.btn_rePrint, R.id.btn_reCheckOut,
			R.id.search_clear, R.id.btn_revokeInvoice})
	public void onClick(View view) {
		switch (view.getId()) {
			//退菜
			case R.id.btn_refundDish:
				ToolsUtils.writeUserOperationRecords("退菜按钮");
				if (logicSelectDishCount(RetreatDishController.getTempItemList())) {
					refundDishAndRefundOrder();
				} else {
					showToast("请先选择要退的菜品!");
				}
				break;
			case R.id.btn_refundOrder:
				ToolsUtils.writeUserOperationRecords("退单按钮");
				refundOrder();
				break;
			//补打
			case R.id.btn_rePrint:
				ToolsUtils.writeUserOperationRecords("补打小票按钮");
				rePrinter();
				break;
			case R.id.btn_reCheckOut:
				ToolsUtils.writeUserOperationRecords("反结账按钮");
				reCheckoutOrder();
				break;
			case R.id.btn_revokeInvoice:
				ToolsUtils.writeUserOperationRecords("发票冲红");
				DialogUtil.revokeInvoiceDialog(aty, new DialogTCallback() {
					@Override
					public void onConfirm(Object o) {
						Customer customer    = (Customer) o;
						String   revokeReson = customer.getCustomerOuterOrderId();
						revokeInvoice(revokeReson);
					}

					@Override
					public void onCancle() {

					}
				});
				break;
			case R.id.search_clear:
				WindowUtil.hiddenKey();
				edMemberNumber.setText("");
				setOrderData(historyOrderList);
				break;
		}
	}

	private void revokeInvoice(String revokeReson) {
		try {
			TradeService tradeService = TradeService.getInstance();
			tradeService.revokeInvoice(selectOrder
					.getId() + "", revokeReson, new ResultCallback<Integer>() {
				@Override
				public void onResult(Integer result) {
					if (result == 0) {
						showToast("发票冲红成功!");
						Log.i("发票冲红成功", "订单ID:" + selectOrder.getId());
						loadData();
					}
				}

				@Override
				public void onError(PosServiceException e) {
					showToast("发票冲红失败," + e.getMessage());
					Log.i("发票冲红失败", e.getMessage());
				}
			});
		} catch (PosServiceException e) {
			e.printStackTrace();
			Log.i("发票冲红失败", e.getMessage());
		}

	}
}
