package cn.acewill.pos.next.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.adapter.FragmentNetOrderAdp;
import cn.acewill.pos.next.ui.adapter.NetOrderInfoAdp;
import cn.acewill.pos.next.ui.adapter.PayMentListAdp;
import cn.acewill.pos.next.widget.ScrolListView;


/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class NetOrderFragment extends BaseFragment  {
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.lin_left )
    LinearLayout linLeft;
    @BindView( R.id.lin_center )
    RelativeLayout linCenter;
    @BindView( R.id.lin_right )
    LinearLayout linRight;
    @BindView( R.id.rel_center_count )
    RelativeLayout relCenterCount;
    @BindView( R.id.text_tips )
    TextView textTips;
    @BindView( R.id.btn_receive )
    TextView btnReceive;
    @BindView( R.id.btn_refuse )
    TextView btnRefuse;
    @BindView( R.id.order_dish_count )
    TextView orderDishCount;
    @BindView( R.id.order_customerAmount )
    TextView orderCustomerAmount;
    @BindView( R.id.order_creat_time )
    TextView orderCreateTime;
    @BindView( R.id.order_price )
    TextView orderPrice;
    @BindView( R.id.order_activeMoney )
    TextView orderActiveMoney;
    @BindView( R.id.order_cost )
    TextView orderCost;
    @BindView( R.id.member_number )
    TextView memberNumber;
    @BindView( R.id.member_name )
    TextView memberName;
    @BindView( R.id.member_level )
    TextView memberLevel;
    @BindView( R.id.member_MemberConsumeCost )
    TextView memberMemberConSumeCost;
    @BindView( R.id.sale_name )
    TextView saleName;
    @BindView( R.id.sale_phone )
    TextView salePhone;
    @BindView( R.id.sale_address )
    TextView saleAddress;
    @BindView( R.id.sale_orderNumber )
    TextView saleOrderNumber;
    @BindView( R.id.lin_payType )
    LinearLayout linPayType;
    @BindView( R.id.lin_member )
    LinearLayout linMember;
    @BindView( R.id.lin_saleOut )
    LinearLayout linSaleOut;
    @BindView( R.id.lv_payType )
    ScrolListView lvPayType;
    @BindView( R.id.dish_list )
    ScrolListView dishList;
    @BindView( R.id.sc_scroll )
    ScrollView scScroll;


    private Store store;
    private PosInfo posInfo;
    private Cart cart;
    private UserData mUserData;

    private String TAG = "NetOrderFragment";
    private FragmentNetOrderAdp adapter;
    private NetOrderInfoAdp netOrderInfoAdp;
    private PayMentListAdp payMentListAdp;
    private List<Order> netOrderList = new CopyOnWriteArrayList<>();
    private int lastVisibleItem = 0;
    private Intent intent;
    private int limit = 20;
    private boolean isPrinting = false;//是否正在打印中、
    private boolean isShowToast = true;//是否显示Toast
    private TimerTaskController timerTaskController;
    private Order selectOrder;

    public boolean isShowToast() {
        return isShowToast;
    }

    public void setShowToast(boolean showToast) {
        isShowToast = showToast;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_netorder, container, false);
//        ButterKnife.bind(this, view);
//        store = Store.getInstance(aty);
//        mUserData = UserData.getInstance(aty);
//        posInfo = PosInfo.getInstance();
//        cart = Cart.getInstance();
//        if (store.getReceiveNetOrder()) {
//            textTips.setVisibility(View.GONE);
//            linLeft.setVisibility(View.VISIBLE);
//            linCenter.setVisibility(View.VISIBLE);
//            linRight.setVisibility(View.VISIBLE);
//            initView();
//            showCenterRightInfo(false);
//        } else {
//            textTips.setVisibility(View.VISIBLE);
//            linLeft.setVisibility(View.GONE);
//            linCenter.setVisibility(View.GONE);
//            linRight.setVisibility(View.GONE);
//        }
        return view;
    }

//    private void initView() {
//        isPrinting = false;
//        intent = new Intent();
//        timerTaskController = TimerTaskController.getInstance();
//        timerTaskController.setNetOrderinfoCallBack(this);
//        orderSrl.setOnRefreshListener(this);
//        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
//        adapter = new FragmentNetOrderAdp(aty, this);
//        payMentListAdp = new PayMentListAdp(aty);
//        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aty);
//        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
//        orderLv.setLayoutManager(linearLayoutManager);
//        orderLv.setAdapter(adapter);
//        adapter.changeMoreStatus(adapter.NO_MORE);
//        lvPayType.setAdapter(payMentListAdp);
//
//        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
//                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
//                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
//                    adapter.changeMoreStatus(adapter.LOADING);
//                }
//            }
//        });
//
//        orderLv.addOnItemTouchListener(new OnRecyclerItemClickListener(orderLv) {
//            @Override
//            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
//                int position = viewHolder.getAdapterPosition();
//                selectOrder = ToolsUtils.cloneTo((Order) adapter.getItem(position));
//                if (selectOrder != null) {
//                    showCenterRightInfo(true);
//                    adapter.setSelectPosition(position);
//                    showOrderInfo(selectOrder);
//                }
//            }
//
//            @Override
//            public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {
//
//            }
//        });
//
//        netOrderInfoAdp = new NetOrderInfoAdp(aty);
//        dishList.setAdapter(netOrderInfoAdp);
//        RetreatDishController.addListener(new RetreatDishController.ChangeListener() {
//            @Override
//            public void contentChanged() {
//                adapter.notifyDataSetChanged();
//            }
//        });
//
//    }
//
//    private void loadData() {
//        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
//    }
//
//    @Override
//    public void onRefresh() {
//        loadData();
//    }
//
//    private void setNetOrderData(List<Order> orderList) {
//        netOrderList = ToolsUtils.cloneTo(orderList);
//        if (orderList != null && orderList.size() > 0) {
//            adapter.setData(orderList);
//            if (orderList.size() < limit) {
//                adapter.changeMoreStatus(adapter.NO_MORE);
//            } else {
//                adapter.changeMoreStatus(adapter.LOAD_MORE);
//            }
//        } else {
//            adapter.changeMoreStatus(adapter.NO_MORE);
//            showCenterRightInfo(false);
//        }
//        orderSrl.setRefreshing(false);
//    }
//
//    @Override
//    public void getNetOrderInfoList(List<Order> orderList) {
//        if (!ToolsUtils.isList(orderList)) {
//            adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
//            setNetOrderData(orderList);
//        } else {
//            adapter.setData(null);
//            adapter.changeMoreStatus(adapter.NO_MORE);
//            orderSrl.setRefreshing(false);
//            Log.i("没有网络订单信息", ToolsUtils.getPrinterSth(orderList));
//            Log.i(TAG, "后台轮训网上订单信息条数=====0");
//            if (isShowToast) {
//                showToast(ToolsUtils.returnXMLStr("not_more_net_orders"));
//            }
//        }
//    }
//
//    @Override
//    public void printState(boolean isPrint) {
//        isPrinting = isPrint;
//    }
//
//    @Override
//    public void refrush() {
//        loadData();
//    }
//
//
//    private void showCenterRightInfo(boolean isShow)
//    {
//        if(isShow)
//        {
//            relCenterCount.setVisibility(View.VISIBLE);
//            scScroll.setVisibility(View.VISIBLE);
//        }
//        else {
//            selectOrder = null;
//            relCenterCount.setVisibility(View.GONE);
//            scScroll.setVisibility(View.GONE);
//            adapter.setSelectPosition(-1);
//        }
//    }
//
//    /**
//     * 显示订单信息
//     *
//     * @param order
//     */
//    private void showOrderInfo(Order order) {
//        if (order != null) {
//            if (order.getOperation_type() != 2) {
//                btnReceive.setText(ToolsUtils.returnXMLStr("accept_order"));
//            }
//            else{
//                btnReceive.setText(ToolsUtils.returnXMLStr("agree_change_table"));
//            }
//
//            //如果是桌台模式
//            if (ToolsUtils.logicIsTable()) {
//                //判断该订单是否含有tableid,没有的话隐藏接受按钮
//                if (order.preorderTime > 0) {
//                    btnReceive.setVisibility(View.GONE);
//                } else {
//                    btnReceive.setVisibility(View.VISIBLE);
//                }
//            }
//
//            if (order.getItemList() != null && order.getItemList().size() > 0) {
//                RetreatDishController.setItemList(order.getItemList());
//                RetreatDishController.setTempItemList(order.getItemList());
//            }
//            List<OrderItem> orderItems = order.getItemList();
//            netOrderInfoAdp.setData(orderItems);
//
//            int dishCount = 0;
//            if(!ToolsUtils.isList(orderItems))
//            {
//                for (OrderItem oi:orderItems)
//                {
//                    dishCount+=oi.getQuantity();
//                }
//            }
//            orderDishCount.setText(ToolsUtils.returnXMLStr("dish_menu_counts2")+dishCount);
//            int customerAmount = order.getCustomerAmount() == 0 ? 1:order.getCustomerAmount();
//            orderCustomerAmount.setText(ToolsUtils.returnXMLStr("repast_counts2")+customerAmount);
//
//            showRightOrderInfo(order);
//        }
//    }
//
//    /**
//     * 显示右边订单详情
//     * @param result
//     */
//    private void showRightOrderInfo(Order result)
//    {
//        orderCreateTime.setText(ToolsUtils.returnXMLStr("place_order_time2")+TimeUtil.getStringTimeLong(result.getCreatedAt()));
//        float price = Float.parseFloat(result.getTotal());
//        float cost = Float.parseFloat(result.getCost());
//        orderPrice.setText(ToolsUtils.returnXMLStr("price2")+price);
//        orderCost.setText(ToolsUtils.returnXMLStr("cost2")+cost);
//        orderActiveMoney.setText(ToolsUtils.returnXMLStr("active_money2")+ new BigDecimal(price-cost).setScale(2,BigDecimal.ROUND_HALF_UP));
//        linPayType.setVisibility(View.GONE);
//        linMember.setVisibility(View.GONE);
//        linSaleOut.setVisibility(View.GONE);
//        if(!ToolsUtils.isList(result.getPaymentList()))
//        {
//            linPayType.setVisibility(View.VISIBLE);
//            payMentListAdp.setData(result.getPaymentList());
//        }
//        Account account = result.getAccountMember();
//        if(result.getAccountMember() != null)
//        {
//            linMember.setVisibility(View.VISIBLE);
//            memberNumber.setText(ToolsUtils.returnXMLStr("member_card_number_xx2") + account.getUno() + "(" + ToolsUtils.replacePhone(account.getPhone()) + ")");
//            memberName.setText(ToolsUtils.returnXMLStr("member_name_xx2")+ ToolsUtils.getStarString2(account.getName(), 1, 0));
//            memberLevel.setText(ToolsUtils.returnXMLStr("member_card_level2") + account.getGradeName());
//            memberMemberConSumeCost.setText(ToolsUtils.returnXMLStr("consume_money2")  + account.getMemberConsumeCost());
//        }
//        String type = result.getOrderType();
//        if(type.equals("SALE_OUT"))
//        {
//            linSaleOut.setVisibility(View.VISIBLE);
//            saleName.setText(ToolsUtils.returnXMLStr("name_xx2") + result.getCustomerName());
//            salePhone.setText(ToolsUtils.returnXMLStr("phone_xx2") + result.getCustomerPhoneNumber());
//            saleAddress.setText(ToolsUtils.returnXMLStr("meals_address2") + result.getCustomerAddress());
//            saleOrderNumber.setText(ToolsUtils.returnXMLStr("platform_order_number2") + result.getThirdPlatfromOrderIdDaySeq());
//        }
//    }
//
//    private void refuseOrder(final Order orderInfo)
//    {
//        ToolsUtils.writeUserOperationRecords("拒绝网上订单==>订单Id==" + orderInfo.getId());
//        if (btnReceive.getVisibility() == View.GONE) {
//            rejuctNetOrder(orderInfo.getId(), ToolsUtils.returnXMLStr("order_info_error"));
//        } else {
//            List<NetOrderRea> reasonItem = new ArrayList<NetOrderRea>();
//            reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("weather_reason")));
//            reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("order_info_error")));
//            reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("dish_is_sell_out")));
//            reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("will_close")));
//            reasonItem.add(new NetOrderRea(ToolsUtils.returnXMLStr("no")));
//            DialogUtil.listDialog(aty, ToolsUtils.returnXMLStr("reject_reason"), reasonItem, new DialogEtCallback() {
//                @Override
//                public void onConfirm(String sth) {
//                    rejuctNetOrder(orderInfo.getId(), sth);
//                }
//
//                @Override
//                public void onCancle() {
//
//                }
//            });
//        }
//    }
//
//    /**
//     * 拒绝网上订单
//     */
//    private void rejuctNetOrder(final long orderId, final String rejuctStr) {
//        try {
//            final OrderService orderService = OrderService.getInstance();
//            orderService.rejectNetOrder(orderId, rejuctStr, new ResultCallback<Integer>() {
//                @Override
//                public void onResult(Integer result) {
//                    if (result == 0)//表示成功拒绝
//                    {
//                        netOrderInfoAdp.notifyDataSetChanged();
//                        showCenterRightInfo(false);
//                    }
//                }
//
//                @Override
//                public void onError(PosServiceException e) {
//                    MyApplication.getInstance().ShowToast(e.getMessage());
//                }
//            });
//        } catch (PosServiceException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 接收网上订单
//     * @param orderInfo
//     */
//    private void receiveOrder(Order orderInfo)
//    {
//        if(orderInfo != null && !timerTaskController.isReceiveNetOrder(orderInfo.getId()))
//        {
//            ToolsUtils.writeUserOperationRecords("接受网上订单==>订单Id==" + orderInfo.getId());
//            String source = orderInfo.getSource();
//            if (!TextUtils.isEmpty(source)) {
//                if (source.equals("2")) {
//                    source = "微信堂食";
//                }
//            } else {
//                source = "未知来源";
//            }
//            orderInfo.setSource(source);
//            checkDishCount(orderInfo);
//        }
//        else{
//            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
//        }
//    }
//
//    /**
//     * 检测菜品库存并下单
//     *
//     * @param order
//     */
//    public void checkDishCount(final Order order) {
//        //换台单
//        if (order.getOperation_type() == 2) {
//            conFirmNetOrder(order);
//        } else {
//            CheckOutUtil checkOutUtil = new CheckOutUtil(aty);
//            final List<Dish> dishs = new CopyOnWriteArrayList<>();
//            for (OrderItem orderItem : order.getItemList()) {
//                Dish dish = new Dish(orderItem);
//                dishs.add(dish);
//            }
//            checkOutUtil.getDishStock(dishs, new DishCheckCallback() {
//                @Override
//                public void haveStock() {
//                    getOrderId(null, order);
//                }
//
//                @Override
//                public void noStock(List dataList) {
//                    refreshDish(dataList, dishs);
//                }
//            });
//        }
//    }
//
//    /**
//     * 确认接收网上的订单
//     *
//     * @param resultOrder
//     */
//    private void conFirmNetOrder(final Order resultOrder) {
//        showProgress();
//        final String netOrderId = String.valueOf(resultOrder.getId());
//        final String orderId = String.valueOf(resultOrder.getRefOrderId());
//        final long orderIdL = resultOrder.getId();
//        OrderService orderService = null;
//        try {
//            orderService = OrderService.getInstance();
//        } catch (PosServiceException e) {
//            e.printStackTrace();
//        }
//        orderService.confirmNetOrder(netOrderId, orderId, resultOrder.getCallNumber(), new ResultCallback<Integer>() {
//            @Override
//            public void onResult(Integer result) {
//                dissmiss();
//                if (result == 0)//表示成功接单
//                {
//                    timerTaskController.modifyOrderType(Long.parseLong(netOrderId),1);
//                    showCenterRightInfo(false);
//                    refrushUi(resultOrder.getId());
//                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("net_order_number") + orderId + ToolsUtils.returnXMLStr("synchronization_is_success"));
//                    if (resultOrder.isInformKds())//是否通知KDS打印
//                    {
//                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS_CHANGE_TABLE, resultOrder.getRefOrderId(), resultOrder.getChangeTableName()));
//                    }
//                    if (resultOrder.isInformKitchen())//是否通知厨房打印
//                    {
//                        getOrderInfo(resultOrder);
//                    }
//                    sleep();
//                }
//            }
//
//            @Override
//            public void onError(PosServiceException e) {
//                dissmiss();
//                MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("synchronization_is_failure")+"," + e.getMessage());
//                Log.i("网上订单确认同步失败", "orderId ==" + orderId + "===" + e.getMessage());
//            }
//        });
//    }
//
//    /**
//     * 根据订单Id获得订单详情
//     */
//    private void getOrderInfo(final Order order) {
//        try {
//            myApplication.ShowToast(ToolsUtils.returnXMLStr("getting_order_details_please_wait"));
//            OrderService orderService = OrderService.getInstance();
//            orderService.getOrderInfoById(order.getRefOrderId() + "", new ResultCallback<Order>() {
//                @Override
//                public void onResult(final Order result) {
//                    if (result != null && result.getItemList() != null && result.getItemList().size() > 0) {
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                order.setItemList(result.getItemList());
//                                order.setOrderType(result.getOrderType());
//                                order.setId(result.getId());
//                                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, order));
//                            }
//                        }, 3000);
//                    }
//                }
//
//                @Override
//                public void onError(PosServiceException e) {
//                    Log.i("订单详情获取失败!", e.getMessage());
//                    myApplication.ShowToast(e.getMessage());
//                }
//            });
//        } catch (PosServiceException e) {
//            e.printStackTrace();
//            Log.i("订单详情获取失败!", e.getMessage());
//        }
//    }
//
//    /**
//     * 休息两秒
//     */
//    private void sleep() {
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//    public void getOrderId(final List<Dish> dishs, final Order order) {
//        try {
//            OrderService orderService = OrderService.getInstance();
//            orderService.getNextOrderId(new ResultCallback<Long>() {
//                @Override
//                public void onResult(Long result) {
//                    if (result > 0) {
//                        PosInfo posInfo = PosInfo.getInstance();
//                        posInfo.setNetOrderId(result);
//                        createOrder(dishs, order);
//                    }
//                }
//
//                @Override
//                public void onError(PosServiceException e) {
//                    Log.i("获取订单Id失败", e.getMessage());
//                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
////                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
//                }
//            });
//        } catch (PosServiceException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createOrder(List<Dish> dishs, final Order netOrder) {
//        try {
//            showProgress();
//            OrderService orderService = OrderService.getInstance();
//            Order order = null;
//            if (dishs != null) {
//                order = cart.getNetOrderItem(dishs, netOrder);
//            } else if (netOrder != null) {
//                order = ToolsUtils.cloneTo(netOrder);;
//            }
//            if (order != null) {
//                final long orderId = order.getId();
//                order.setRefNetOrderId(orderId);
//                order.setThirdPlatformOrderId(netOrder.getOuterOrderid());
//                order.setThirdPlatformOrderIdView(netOrder.getOuterOrderIdView());
//                order.setThirdPlatfromOrderIdDaySeq(netOrder.getOuterOrderIdDaySeq());
//                final Order finalOrder = ToolsUtils.cloneTo(order);
//                orderService.createOrder(order, new ResultCallback<Order>() {
//                    @Override
//                    public void onResult(final Order result) {
//                        dissmiss();
//                        if(result != null)
//                        {
//                            timerTaskController.modifyOrderType(orderId,1);//修改为已接收
//                            TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
//                            showCenterRightInfo(false);
//
//                            if (store.isCreateOrderJyj()) {
//                                result.setJyjOrder(true);//将下单状态改为向JYJ下单
//                                createOrderJyj(result,orderId);
//                            } else {
//                                orderPrint(result,orderId);
//                            }
//
//                        }
//                        else{
//                            MyApplication.getInstance().ShowToast(ToolsUtils.orderErrTips(finalOrder,orderId));
//                        }
//                    }
//
//                    @Override
//                    public void onError(PosServiceException e) {
//                        Log.i("网上开台下单失败", "orderId ==" + orderId + "===" + e.getMessage());
//                        myApplication.ShowToast(ToolsUtils.orderErrTips(finalOrder,orderId));
//                        //                        refrushUi(position,netOrder.getId());
//                        dissmiss();
//                    }
//                });
//            }
//        } catch (PosServiceException e) {
//            e.printStackTrace();
//            Log.i("网上开台下单失败", "===" + e.getMessage());
//        }
//    }
//
//    private void createOrderJyj(final Order order,final long orderId) {
//        showProgress();
//        final Order newOrder = ToolsUtils.cloneTo(order);
//        if (newOrder != null) {
//            try {
//                OrderService orderService = OrderService.getJyjOrderService();
//                orderService.createOrder(newOrder, new ResultCallback<Order>() {
//                    @Override
//                    public void onResult(Order result) {
//                        dissmiss();
//                        if (result != null) {
//                            orderPrint(result, orderId);
//                        } else {
//                            Log.e("JYJ下单", "JYJ下单失败:" + "null");
//                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("orders_failed"));
//                            order.setJyjPrintErrMessage(ToolsUtils.returnXMLStr("jyj_order_error_message"));
//                            orderPrint(order, orderId);
//                        }
//                    }
//
//                    @Override
//                    public void onError(PosServiceException e) {
//                        dissmiss();
//                        Log.e("JYJ下单", "JYJ下单失败:" + e.getMessage());
//                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("orders_failed") + "," + e.getMessage());
//                        order.setJyjPrintErrMessage(ToolsUtils.returnXMLStr("jyj_order_error_message"));
//                        orderPrint(order, orderId);
//                    }
//                });
//            } catch (PosServiceException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    private void orderPrint(final Order result,long orderId)
//    {
//        Log.i("网上开台下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
//        if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
//            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
//        }
//        System.out.println("==checkDishCount==" + orderId);
//
//        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, result));
//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, result));
//            }
//        }, 3000);
//
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, result));
//            }
//        }, 3000);
//
//        //conFirmNetOrder(position ,result);
//        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("net_order_number") + orderId + ToolsUtils.returnXMLStr("get_order_success"));
//
//        refrushUi(orderId);
//        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result.getTableNames()));//kds下单
//    }
//
//    /**
//     * 显示沽清提示
//     *
//     * @param result
//     */
//    public void refreshDish(List<DishCount> result, List<Dish> dishs) {
//        //刷新菜品数据,显示沽清
//        String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
//        myApplication.ShowToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough")+"\n\n" + names
//                + "。\n\n"+ToolsUtils.returnXMLStr("please_re_order"));
//        Log.i("以下菜品份数不足:",names+"====<<");
//    }
//
//    private void refrushUi(long netOrderId) {
//        Order copyList = NetOrderController.getNetOrderMap().get(netOrderId);//根据订单ID去map列表里面查询出order对象
//        if (copyList != null && copyList.getId() == (int) netOrderId) {
//            NetOrderController.getNetOrderList().remove(copyList);
//        }
//        netOrderInfoAdp.notifyDataSetChanged();
//        EventBus.getDefault().post(new PosEvent(Constant.EventState.PUT_NET_ORDER, NetOrderController.getNetOrderList()));
//    }
//
//
//
//    @OnClick( {R.id.btn_receive, R.id.btn_refuse} )
//    public void onClick(View view) {
//        switch (view.getId()) {
//            //接单
//            case R.id.btn_receive:
//                if(selectOrder != null)
//                {
//                    receiveOrder(selectOrder);
//                }
//                else{
//                    showToast(ToolsUtils.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
//                }
//                break;
//            //拒单
//            case R.id.btn_refuse:
//                if(selectOrder != null)
//                {
//                    refuseOrder(selectOrder);
//                }
//                else{
//                    showToast(ToolsUtils.returnXMLStr("order_has_been_received_by_the_store_or_has_been_rejected"));
//                }
//                break;
//        }
//    }
}
