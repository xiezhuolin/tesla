package cn.acewill.pos.next.ui.activity.newPos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.ReprintController;
import cn.acewill.pos.next.common.RetreatDishController;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.ui.adapter.FragmentOrderNewAdp;
import cn.acewill.pos.next.ui.adapter.OrderInforNewAdp;
import cn.acewill.pos.next.ui.adapter.PayMentListAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.OnRecyclerItemClickListener;
import cn.acewill.pos.next.widget.ScrolListView;
import cn.qqtheme.framework.picker.DatePicker;

/**
 * 交接班记录
 * Created by DHH on 2016/6/12.
 */
public class OrderInfoHistoryAty extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, FragmentOrderNewAdp.RefrushLisener {
    @BindView( R.id.tv_start_time )
    TextView tvStartTime;
    @BindView( R.id.tv_end_time )
    TextView tvEndTime;
    @BindView( R.id.tv_query )
    TextView tvQuery;
    @BindView( R.id.rel_back )
    RelativeLayout relBack;

    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.lin_left )
    LinearLayout linLeft;
    @BindView( R.id.lin_center )
    RelativeLayout linCenter;
    @BindView( R.id.lin_right )
    RelativeLayout linRight;
    @BindView( R.id.rel_center_count )
    RelativeLayout relCenterCount;
    @BindView( R.id.lin_right_bottom )
    LinearLayout linRightBottom;
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
    @BindView( R.id.order_refundPrice )
    TextView orderFefundPrice;
    @BindView( R.id.order_refundTime )
    TextView orderRefundTime;
    @BindView( R.id.btn_rePrint )
    TextView btnRePrint;
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

    private MyApplication application;

    private Calendar calendar;
    private String startInitTime = " 00:00:00";
    private String endInitTime = " 23:59:59";

    private DatePicker startPicker;
    private DatePicker endPicker;

    private Store store;
    private PosInfo posInfo;
    private Cart cart;
    private UserData mUserData;

    private String TAG = "OrderInfoHistoryAty";
    private FragmentOrderNewAdp adapter;
    private OrderInforNewAdp orderInforNewAdp;
    private PayMentListAdp payMentListAdp;
    private List<Order> historyOrderList = new CopyOnWriteArrayList<>();
    private int lastVisibleItem = 0;
    private Intent intent;
    private Order selectOrder;
    private int page = 0;
    private int limit = 800;
    private int orderType = -1;//-1:全部;0:堂食;1:外带;2:外卖
    private int payStatus = -1;//-1:全部;0:未支付;1:已经支付;2:已经退款

    private int RECHECKOUT = 100;//反结账跳转

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_order_info_history);
        ButterKnife.bind(this);
        store = Store.getInstance(this);
        mUserData = UserData.getInstance(this);
        posInfo = PosInfo.getInstance();
        cart = Cart.getInstance();
        initView();
    }

    private void loadData() {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        String startTime = tvStartTime.getText().toString().trim();
        String endTime = tvEndTime.getText().toString().trim();
        queryWorkShift(startTime, endTime);
    }

    private void initView() {
        myApplication.addPage(OrderInfoHistoryAty.this);
        setTitle(ToolsUtils.returnXMLStr("hostory_shift"));
        setShowBtnBack(true);
        application = MyApplication.getInstance();
        startPicker = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
        endPicker = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
        startPicker = setDatePicker(startPicker);
        endPicker = setDatePicker(endPicker);

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvStartTime.setText(year + "-" + checkDayOrMonthStr(month) + "-" + checkDayOrMonthStr(day) + startInitTime);
        tvEndTime.setText(year + "-" + checkDayOrMonthStr(month) + "-" + checkDayOrMonthStr(day) + endInitTime);


        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new FragmentOrderNewAdp(this, this);
        payMentListAdp = new PayMentListAdp(this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);

                    loadData();
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

        orderInforNewAdp = new OrderInforNewAdp(this);
        dishList.setAdapter(orderInforNewAdp);
        RetreatDishController.addListener(new RetreatDishController.ChangeListener() {
            @Override
            public void contentChanged() {
                adapter.notifyDataSetChanged();
            }
        });
        relCenterCount.setVisibility(View.GONE);
        linRightBottom.setVisibility(View.GONE);
        scScroll.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @OnClick( {R.id.rel_back, R.id.tv_query, R.id.tv_start_time, R.id.tv_end_time,R.id.btn_rePrint} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_back:
                finish();
                break;
            case R.id.tv_query:
                String startTime = tvStartTime.getText().toString().trim();
                String endTime = tvEndTime.getText().toString().trim();
                queryWorkShift(startTime, endTime);
                break;
            case R.id.tv_start_time:
                startPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tvStartTime.setText(year + "-" + checkDayOrMonthStr(Integer.valueOf(month)) + "-" + checkDayOrMonthStr(Integer.valueOf(day)) + startInitTime);
                    }
                });
                startPicker.show();
                break;
            case R.id.tv_end_time:
                endPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tvEndTime.setText(year + "-" + checkDayOrMonthStr(Integer.valueOf(month)) + "-" + checkDayOrMonthStr(Integer.valueOf(day)) + endInitTime);
                    }
                });
                endPicker.show();
                break;
            //补打
            case R.id.btn_rePrint:
                ToolsUtils.writeUserOperationRecords("补打小票按钮");
                rePrinter();
                break;
        }
    }

    /**
     * 查询历史交接班记录
     *
     * @param startTime
     * @param endTime
     */
    private void queryWorkShift(final String startTime, final String endTime) {
        String checkStartTime = ToolsUtils.cloneTo(startTime);
        String checkEndTime = ToolsUtils.cloneTo(endTime);
        checkStartTime = checkStartTime.replace(" ", "").replace("-", "").replace(":", "");
        checkEndTime = checkEndTime.replace(" ", "").replace("-", "").replace(":", "");
        long startCheckTime = Long.valueOf(checkStartTime);
        long endCheckTime = Long.valueOf(checkEndTime);
        if (endCheckTime > startCheckTime) {
            getHistoryOrderInfo(startTime, endTime);
        } else {
            showToast(ToolsUtils.returnXMLStr("the_end_time_of_the_query_must_be_greater_than_the_start_time"));
        }
    }

    /**
     * 检测月份日期格式 小于10要在前面补个0
     *
     * @param month
     * @return
     */
    private String checkDayOrMonthStr(int month) {
        if (month < 10) {
            return "0" + month;
        }
        return month + "";
    }

    private DatePicker setDatePicker(DatePicker picker) {
        if (picker != null) {
            picker.setHeight(application.getScreenHeight() / 3);
            picker.setCancelTextColor(application.getResources().getColor(R.color.red));
            picker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
            picker.setCancelTextSize(28);
            picker.setSubmitTextSize(28);
            picker.setRangeStart(2017, 1, 1);//开始范围
            picker.setRangeEnd(2022, 1, 1);//结束范围
        }
        return picker;
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
     * 显示右边订单详情
     *
     * @param result
     */
    private String paymentstate = "";//订单状态

    private void showRightOrderInfo(Order result) {
        float price = Float.parseFloat(result.getTotal());
        float cost = Float.parseFloat(result.getCost());
        orderPrice.setText(ToolsUtils.returnXMLStr("price2") + price);
        orderCost.setText(ToolsUtils.returnXMLStr("cost2")+ cost);
        orderActiveMoney.setText(ToolsUtils.returnXMLStr("active_money2") + new BigDecimal(price - cost).setScale(2, BigDecimal.ROUND_HALF_UP));
        paymentstate = result.getPaymentStatus().toString();
        if ("REFUND".equals(paymentstate))//已退单
        {
            orderFefundPrice.setVisibility(View.VISIBLE);
            orderRefundTime.setVisibility(View.VISIBLE);
            orderCreateTime.setVisibility(View.GONE);
            btnRePrint.setVisibility(View.GONE);
            orderFefundPrice.setText(ToolsUtils.returnXMLStr("refind_prince2") + price);
            orderRefundTime.setText(ToolsUtils.returnXMLStr("retreat_order_time") + TimeUtil.getStringTimeLong(result.getCreatedAt()));
        } else {
            orderFefundPrice.setVisibility(View.GONE);
            orderRefundTime.setVisibility(View.GONE);
            orderCreateTime.setVisibility(View.VISIBLE);
            btnRePrint.setVisibility(View.VISIBLE);
            orderCreateTime.setText(ToolsUtils.returnXMLStr("place_order_time2") + TimeUtil.getStringTimeLong(result.getCreatedAt()));
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
            memberNumber.setText(ToolsUtils.returnXMLStr("member_card_number_xx2") + account.getUno() + "(" + ToolsUtils.replacePhone(account.getPhone()) + ")");
            memberName.setText(ToolsUtils.returnXMLStr("member_name_xx2") + ToolsUtils.getStarString2(account.getName(), 1, 0));
            memberLevel.setText(ToolsUtils.returnXMLStr("member_card_level2") + account.getGradeName());
            memberMemberConSumeCost.setText(ToolsUtils.returnXMLStr("consume_money2") + account.getMemberConsumeCost());
        }
        String type = result.getOrderType();
        if (type.equals("SALE_OUT")) {
            linSaleOut.setVisibility(View.VISIBLE);
            saleName.setText(ToolsUtils.returnXMLStr("name_xx2") + result.getCustomerName());
            salePhone.setText(ToolsUtils.returnXMLStr("phone_xx2") + result.getCustomerPhoneNumber());
            saleAddress.setText(ToolsUtils.returnXMLStr("meals_address2") + result.getCustomerAddress());
            saleOrderNumber.setText(ToolsUtils.returnXMLStr("platform_order_number2") + result.getThirdPlatfromOrderIdDaySeq());
        }
    }

    /**
     * 显示订单信息
     *
     * @param order
     */
    private void showOrderInfo(Order order) {
        if (order != null) {
            //            getOrderById(order.getId() + "");
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
                orderDishCount.setText(ToolsUtils.returnXMLStr("dish_menu_counts2") + dishCount);
                int customerAmount = order.getCustomerAmount() == 0 ? 1 : order.getCustomerAmount();
                orderCustomerAmount.setText(ToolsUtils.returnXMLStr("repast_counts2") + customerAmount);

                showRightOrderInfo(order);
            }
        }
    }

    /**
     * 获取历史订单
     *
     * @param startTime
     * @param endTime
     */
    private void getHistoryOrderInfo(String startTime, String endTime) {
        try {
            showProgress();
            OrderService orderService = OrderService.getInstance();
            orderService.getHistoryOrderInfo(startTime, endTime, new ResultCallback<List<Order>>() {
                @Override
                public void onResult(List<Order> result) {
                    dissmiss();
                    if (result != null && result.size() > 0) {
                        setOrderData(result);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    dissmiss();
                    showToast(ToolsUtils.returnXMLStr("get_history_order_failed_please_try_again") + e.getMessage());
                    Log.i("获取历史订单失败,请重试", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(ToolsUtils.returnXMLStr("get_history_order_failed_please_try_again") + e.getMessage());
            Log.i("获取历史订单失败,请重试", e.getMessage());
        }
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
    public void onRefresh() {
        loadData();
    }

    @Override
    public void refrush() {
        loadData();
    }

    /**
     * 遍历已经下单的菜品
     */
    private List<OrderItem> TraverseAlreadyCheckOutDish(Order tableOrder) {
        List<OrderItem> orderItemList = new CopyOnWriteArrayList<>();
        if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() > 0) {
            orderItemList = ToolsUtils.cloneTo( tableOrder.getItemList());
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
     * 补打小票
     */
    private void rePrinter() {
        Printer printer1 = new Printer(ToolsUtils.returnXMLStr("make_up_checkout_ticket_add_qrcode"));
        //        Printer printer2 = new Printer("补打客用单");
        Printer printer2 = new Printer(ToolsUtils.returnXMLStr("make_up_checkout_ticket_no_add_qrcode"));
        Printer printer3 = new Printer(ToolsUtils.returnXMLStr("make_up_kitchen_ticket_dishReceipt"));
        Printer printer4 = new Printer(ToolsUtils.returnXMLStr("make_up_kitchen_ticket_summaryReceipt"));
        if (ReprintController.getRePrinterList() != null && ReprintController.getRePrinterList().size() > 0) {
            ReprintController.getRePrinterList().clear();
        }
        ReprintController.getRePrinterList().add(printer1);
        ReprintController.getRePrinterList().add(printer2);
        ReprintController.getRePrinterList().add(printer3);
        ReprintController.getRePrinterList().add(printer4);

        DialogUtil.ReprintDialog(this, ToolsUtils.returnXMLStr("make_up_ticket2"), ReprintController.getRePrinterList(), new DialogTCallback() {
            @Override
            public void onConfirm(Object o) {

                if (ReprintController.getRePrinterList().get(0).isSelect) {
                    selectOrder.setReprintState(true);
                    selectOrder.setPrintQrcode(true);
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, selectOrder));
                }
                if (ReprintController.getRePrinterList().get(1).isSelect) {
                    selectOrder.setReprintState(true);
                    selectOrder.setPrintQrcode(false);
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, selectOrder));
                }
                if (ReprintController.getRePrinterList().get(2).isSelect) {
                    if (selectOrder != null && selectOrder.getItemList() != null && selectOrder.getItemList().size() > 0) {
                        final List<OrderItem> orderItemList =TraverseAlreadyCheckOutDish(ToolsUtils.cloneTo(selectOrder));
                        DialogUtil.rushDishDialog(OrderInfoHistoryAty.this, ToolsUtils.returnXMLStr("make_up_kitchen_ticket_dishReceipt"), orderItemList, Constant.TABLE_RUSH, new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                //                                for (OrderItem orderItem : orderItemList) {
                                ToolsUtils.removeItemForSelectDish(orderItemList,false);
                                //                                }
                                if (orderItemList.size() >= 1) {
                                    Order expediteOrder = ToolsUtils.cloneTo(selectOrder);
                                    expediteOrder.setItemList(orderItemList);
                                    expediteOrder.setTableStyle(Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
                                    expediteOrder.setRushDishType(1);
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, expediteOrder));
                                }
                            }

                            @Override
                            public void onCancle() {

                            }
                        });
                    }
                }
                if (ReprintController.getRePrinterList().get(3).isSelect) {
                    if (selectOrder != null && selectOrder.getItemList() != null && selectOrder.getItemList().size() > 0) {
                        final List<OrderItem> orderItemList = TraverseAlreadyCheckOutDish(ToolsUtils.cloneTo(selectOrder));
                        DialogUtil.rushDishDialog(OrderInfoHistoryAty.this, ToolsUtils.returnXMLStr("make_up_kitchen_ticket_summaryReceipt"), orderItemList, Constant.TABLE_RUSH, new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                //                                for (OrderItem orderItem : orderItemList) {
                                ToolsUtils.removeItemForSelectDish(orderItemList,false);
                                //                                }
                                if (orderItemList.size() >= 1) {
                                    Order expediteOrder = ToolsUtils.cloneTo(selectOrder);
                                    expediteOrder.setItemList(orderItemList);
                                    expediteOrder.setTableStyle(Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
                                    expediteOrder.setRushDishType(2);
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, expediteOrder));
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

}
