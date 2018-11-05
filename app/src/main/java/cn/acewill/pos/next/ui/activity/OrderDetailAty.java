package cn.acewill.pos.next.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.ReprintController;
import cn.acewill.pos.next.common.RetreatDishController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.PermissionCallback;
import cn.acewill.pos.next.model.OrderStatus;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.OrderSingleReason;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.service.TradeService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.ui.adapter.OrderInforAdapter;
import cn.acewill.pos.next.ui.adapter.OrderSingleReasonAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

import static cn.acewill.pos.R.id.refund_btn;

/**
 * 订单详细信息
 * Created by DHH on 2016/6/12.
 */
public class OrderDetailAty extends BaseActivity {
    @BindView( R.id.order_id )
    TextView order_Id;
    @BindView( R.id.creatTime )
    TextView creatTime;
    @BindView( R.id.order_creatat )
    TextView orderCreatat;
    @BindView( R.id.order_type )
    TextView orderType;
    @BindView( R.id.order_paystate )
    TextView orderPaystate;
    @BindView( R.id.order_total )
    TextView orderTotal;
    @BindView( R.id.order_cost )
    TextView orderCost;
    @BindView( R.id.order_refund )
    TextView orderRefund;
    @BindView( R.id.order_source )
    TextView orderSource;
    @BindView( R.id.order_paytype )
    TextView orderPaytype;
    @BindView( R.id.people )
    TextView people;
    @BindView( R.id.order_tradno )
    TextView orderTradno;
    @BindView( R.id.tradeno_ll )
    LinearLayout tradenoLl;
    @BindView( R.id.order_callnum )
    TextView orderCallnum;
    @BindView( R.id.order_sl )
    ScrollView orderSl;
    @BindView( refund_btn )
    TextView refundBtn;
    @BindView( R.id.handle_btn )
    TextView handleBtn;
    @BindView( R.id.since_btn )
    TextView sinceBtn;
    @BindView( R.id.dish_list )
    ScrolListView dishList;
    @BindView( R.id.removeDish_btn )
    TextView removeDishBtn;
    @BindView( R.id.tv_callNumber_hine )
    TextView tvCallNumberHine;
    @BindView( R.id.reverseCheckOut_btn )
    TextView reverseCheckOutBtn;
    @BindView( R.id.receive_man )
    TextView receiveMan;
    @BindView( R.id.receive_phone )
    TextView receivePhone;
    @BindView( R.id.receive_address )
    TextView receiveAddress;
    @BindView( R.id.lin_waimai )
    LinearLayout linWaimai;
    @BindView( R.id.waimai_money )
    TextView waimaiMoney;
    @BindView( R.id.lin_waimai_money )
    LinearLayout linWaimaiMoney;

    private PosInfo posInfo;
    private OrderService orderService;
    private StoreBusinessService storeBusinessService;
    private OrderInforAdapter adapter;
    private Order orderPrint;//补打小票使用
    private String paymentNo;//电子流水号-补打小票使用
    //    private int payTypeID;//支付方式id-补打小票使用
    private String mTradeNo;//第三方交易号
    private String cbNo;//盒子流水号
    private double money;//盒子退款金额
//    private boolean isRefundDish = true;//是否能退菜 true可以

    private String orderId;//从上一个页面传人
    private String paymentstate;

    private Store store;

    private int RECHECKOUT = 100;//反结账跳转

    public static List<OrderItem> orderItemList = new CopyOnWriteArrayList<OrderItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_order_detail_info);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        setTitle(ToolsUtils.returnXMLStr("order_details"));
        setShowBtnBack(true);
//        rel_back.setBackgroundColor(resources.getColor(R.color.black));
//        rel_title.setBackgroundColor(resources.getColor(R.color.black));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderById();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolsUtils.writeUserOperationRecords("关闭了订单详情界面");
    }

    private void initView() {
        myApplication.addPage(OrderDetailAty.this);
        try {
            orderId = String.valueOf(getIntent().getLongExtra("orderId", 0));
            orderService = OrderService.getInstance();
            storeBusinessService = StoreBusinessService.getInstance();
            posInfo = PosInfo.getInstance();
            store = Store.getInstance(context);
        } catch (PosServiceException e) {
            e.printStackTrace();
        }

        adapter = new OrderInforAdapter(context);
        dishList.setAdapter(adapter);
        RetreatDishController.addListener(new RetreatDishController.ChangeListener() {
            @Override
            public void contentChanged() {
                adapter.notifyDataSetChanged();
            }
        });
        String cardNumberType = "";
        if (ToolsUtils.logicIsTable()) {
            cardNumberType = ToolsUtils.returnXMLStr("table_number");
        } else {
            if (StoreInfor.cardNumberMode) {
                cardNumberType = ToolsUtils.returnXMLStr("card_number");
            } else {
                cardNumberType = "取餐号";
            }
        }
        tvCallNumberHine.setText(cardNumberType);
    }


    //根据订单号获取订单详情
    private void getOrderById() {
        showProgress();
        orderService.getOrderInfoById(orderId, new ResultCallback<Order>() {
            @Override
            public void onResult(Order result) {
                dissmiss();
                System.out.println(ToolsUtils.getPrinterSth(result));
                if (result != null) {
                    if (result.getItemList() != null && result.getItemList().size() > 0) {
                        RetreatDishController.setItemList(result.getItemList());
                        RetreatDishController.setTempItemList(result.getItemList());
                    }
                    orderItemList = result.getOrderItemList();
                    orderPrint = result;
                    paymentstate = result.getPaymentStatus().toString();

                    String payStatus = "";
                    String refund = "0.0￥";
                    reverseCheckOutBtn.setVisibility(View.GONE);

                    //说明是挂单状态
                    if (result.getStatus() == OrderStatus.PENDING) {
                        sinceBtn.setVisibility(View.VISIBLE);
                    } else {
                        sinceBtn.setVisibility(View.GONE);
                    }
                    if ("NOT_PAYED".equals(paymentstate)) {//未支付：显示修改与支付
                        payStatus = ToolsUtils.returnXMLStr("unpaid2");
                        orderPaystate.setTextColor(getResources().getColor(R.color.bbutton_danger_pressed));
                    } else if ("PAYED".equals(paymentstate)) {//已支付
                        payStatus = "已支付";
                        orderPaystate.setTextColor(getResources().getColor(R.color.green));
                        reverseCheckOutBtn.setVisibility(View.VISIBLE);
                    } else if ("REFUND".equals(paymentstate)) {//已退单
                        refundBtn.setClickable(false);
                        refundBtn.setBackgroundColor(resources.getColor(R.color.gray));
                        payStatus = "已退单";
                        refund = result.getTotal() + "￥";
                        orderPaystate.setTextColor(getResources().getColor(R.color.btn_blue_pressed));
                        if (Double.parseDouble(result.getCost()) < 0) {
                            creatTime.setText("退单时间:");
                        }
                    } else if ("FAILED_TO_QUERY_STATUS".equals(paymentstate)) {//支付超时
                        payStatus = "支付超时";
                        orderPaystate.setTextColor(getResources().getColor(R.color.font_blue));
                    } else if ("CANCELED".equals(paymentstate)) {//订单取消
                        payStatus = "订单取消";
                        orderPaystate.setTextColor(getResources().getColor(R.color.dishitem_count_font_gray));
                    } else if ("DUPLICATED".equals(paymentstate)) {//重复订单
                        payStatus = "重复订单";
                        orderPaystate.setTextColor(getResources().getColor(R.color.dishitem_count_font_gray));
                        removeDishBtn.setBackgroundColor(getResources().getColor(R.color.dishitem_count_font_gray));
                        refundBtn.setBackgroundColor(getResources().getColor(R.color.dishitem_count_font_gray));
                        handleBtn.setBackgroundColor(getResources().getColor(R.color.dishitem_count_font_gray));
                        reverseCheckOutBtn.setBackgroundColor(getResources().getColor(R.color.dishitem_count_font_gray));
                        sinceBtn.setBackgroundColor(getResources().getColor(R.color.dishitem_count_font_gray));

                        removeDishBtn.setEnabled(false);
                        refundBtn.setEnabled(false);
                        handleBtn.setEnabled(false);
                        reverseCheckOutBtn.setEnabled(false);
                        sinceBtn.setEnabled(false);
                    }

                    order_Id.setText(result.getId() + "");
                    orderCreatat.setText(TimeUtil.getStringTime(result.getCreatedAt()));
                    String typeStrs = "";
                    if ("EAT_IN".equals(result.getOrderType())) {
                        typeStrs = "堂食";
                    } else if ("TAKE_OUT".equals(result.getOrderType())) {
                        typeStrs = "外带";
                    } else if ("SALE_OUT".equals(result.getOrderType())) {
                        typeStrs = "外卖";
                        linWaimai.setVisibility(View.VISIBLE);
                        linWaimaiMoney.setVisibility(View.VISIBLE);
                        receiveMan.setText(result.getCustomerName());
                        receivePhone.setText(result.getCustomerPhoneNumber());
                        receiveAddress.setText(result.getCustomerAddress());
                        waimaiMoney.setText(result.getShippingFee() + "￥");
                    }

                    orderType.setText(typeStrs);
                    orderPaystate.setText(payStatus);
                    orderTotal.setText(result.getTotal() + "￥");
                    if ("PAYED".equals(paymentstate)) {
                        orderCost.setText(result.getCost() + "￥");
                        float total = Float.valueOf(result.getTotal());
                        float cost = Float.valueOf(result.getCost());
                        if (total != cost) {
                            //                            isRefundDish = false;
                            removeDishBtn.setText("退单");
                        }
                    } else {
                        orderCost.setText("0.00￥");
                    }

                    List<OrderItem> orderItems = result.getItemList();
                    //                    adapter.isRetreatDish(isRefundDish);
                    adapter.setData(orderItems);

                    orderRefund.setText(refund);
                    orderSource.setText(result.getSource());

                    people.setText(result.getCustomerAmount() + "");
                    money = Double.parseDouble(result.getCost());

                    String callNumber = "";
                    if (ToolsUtils.logicIsTable()) {
                        callNumber = (TextUtils.isEmpty(result.getTableNames()) ? "0" : result.getTableNames());
                    } else {
                        if(StoreInfor.cardNumberMode)
                        {
                            callNumber = (TextUtils.isEmpty(result.getTableNames()) ? "0" : result.getTableNames());
                        }
                        else
                        {
                            callNumber = (TextUtils.isEmpty(result.getCallNumber()) ? "0" : result.getCallNumber());
                        }
                    }
                    orderCallnum.setText(callNumber);

                    if (result.getPaymentList() != null && result.getPaymentList().size() > 0) {
                        String payNo = result.getPaymentList().get(0).getPaymentNo();
                        cbNo = result.getPaymentList().get(0).getTransactionNo();
                        mTradeNo = payNo;
                        orderTradno.setText(payNo);
                        paymentNo = cbNo;
                        orderPrint.setPaymentNo(orderId);
                        if (result.getPaymentList() != null && result.getPaymentList().size() >= 1) {
                            int size = result.getPaymentList().size();
                            StringBuffer sb = new StringBuffer();
                            for (int i = 0; i < size; i++) {
                                int paymentTypeId = result.getPaymentList().get(i).getPaymentTypeId();
                                Payment payment = StoreInfor.getPaymentById(paymentTypeId);
                                if (i == (size - 1)) {
                                    sb.append(payment.getName());
                                } else {
                                    sb.append(payment.getName() + "、");
                                }
                                orderPaytype.setText(sb.toString());
                            }
                        } else {
                            orderPaytype.setText("无");
                        }

                    }
                    handleBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                if (!TextUtils.isEmpty(e.getMessage())) {
                    Log.i("订单详情获取失败!", e.getMessage());
                    showToast(e.getMessage());
                    dissmiss();
                }

            }
        });
    }

    /**
     * 补打小票
     */
    private void rePrinter() {
        Printer printer1 = new Printer("补打结账单(带电子发票二维码)");
//        Printer printer2 = new Printer("补打客用单");
        Printer printer2 = new Printer("补打结账单(不带电子发票二维码)");
        Printer printer3 = new Printer("补打厨房小票");
        if (ReprintController.getRePrinterList() != null && ReprintController.getRePrinterList().size() > 0) {
            ReprintController.getRePrinterList().clear();
        }
        ReprintController.getRePrinterList().add(printer1);
        ReprintController.getRePrinterList().add(printer2);
        ReprintController.getRePrinterList().add(printer3);

        DialogUtil.ReprintDialog(context, "补打小票", ReprintController.getRePrinterList(), new DialogTCallback() {
            @Override
            public void onConfirm(Object o) {

                if (ReprintController.getRePrinterList().get(0).isSelect) {
                    orderPrint.setReprintState(true);
                    orderPrint.setPrintQrcode(true);
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, orderPrint));
                }
                if (ReprintController.getRePrinterList().get(1).isSelect) {
                    orderPrint.setReprintState(true);
                    orderPrint.setPrintQrcode(false);
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, orderPrint));
                }
                if(ReprintController.getRePrinterList().get(2).isSelect)
                {
                    if (orderPrint != null && orderPrint.getItemList() != null && orderPrint.getItemList().size() > 0) {
                        TraverseAlreadyCheckOutDish(ToolsUtils.cloneTo(orderPrint));
                        DialogUtil.rushDishDialog(context, "补打厨房小票", orderItemList, Constant.TABLE_RUSH, new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                for (OrderItem orderItem : orderItemList) {
                                    if (!orderItem.isSelectItem) {
                                        orderItemList.remove(orderItem);
                                    }
                                }
                                if (orderItemList.size() >= 1) {
                                    Order expediteOrder = ToolsUtils.cloneTo(orderPrint);
                                    expediteOrder.setItemList(orderItemList);
                                    expediteOrder.setTableStyle(Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT);
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

    /**
     * 遍历已经下单的菜品
     */
    private void TraverseAlreadyCheckOutDish(Order tableOrder) {
        if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() > 0) {
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
    }

    /**
     * 起单
     */
    private void resumeOrder(long orderId, Order order) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.resumeOrder(orderId, order, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    if (result == 0) {
                        showToast("起单成功!");
                        sinceBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("起单失败!", e.getMessage());
                    showToast("起单失败!" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("起单失败!", e.getMessage());
        }
    }

    @OnClick( {R.id.refund_btn, R.id.handle_btn, R.id.since_btn, R.id.removeDish_btn, R.id.reverseCheckOut_btn} )
    public void onClick(View view) {
        switch (view.getId()) {
            //反结账
            case R.id.reverseCheckOut_btn:
                ToolsUtils.writeUserOperationRecords("反结账按钮");
                reCheckoutOrder();
//                DialogUtil.ordinaryDialog(context, "反结账", "是否确定要反结账?", new DialogCallback() {
//                    @Override
//                    public void onConfirm() {
//                        reverseCheckOut();
//                    }
//
//                    @Override
//                    public void onCancle() {
//
//                    }
//                });
                break;
            //催单
            case R.id.refund_btn:
                ToolsUtils.writeUserOperationRecords("催单按钮");
                DialogUtil.inputDialog(context, "催菜", "备注", "请输入催菜备注", 0, true, false, new DialogEtCallback() {
                    @Override
                    public void onConfirm(String sth) {
                        if (orderPrint != null) {
                            orderPrint.setComment(sth);//备注
                            orderPrint.setTableStyle(Constant.EventState.PRINTER_RUSH_DISH);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, orderPrint));
                        }
                    }

                    @Override
                    public void onCancle() {

                    }
                });
                break;
            //补打小票
            case R.id.handle_btn:
                ToolsUtils.writeUserOperationRecords("补打小票按钮");
                rePrinter();
                break;
            //起单
            case R.id.since_btn:
                ToolsUtils.writeUserOperationRecords("起单按钮");
                resumeOrder(Long.valueOf(orderId), orderPrint);
                break;
            //退菜
            case R.id.removeDish_btn:
                ToolsUtils.writeUserOperationRecords("退菜按钮");
                if (logicSelectDishCount(RetreatDishController.getTempItemList())) {
                    refundDishAndRefundOrder();
                } else {
                    showToast("请先选择要退的菜品!");
                }
                break;
        }
    }

    /**
     * 获取退菜、退菜原因
     */
    private void getSingleReason() {
        try {
            TableService tableService = TableService.getInstance();
            tableService.getSingleReason(new ResultCallback<List<OrderSingleReason>>() {
                @Override
                public void onResult(List<OrderSingleReason> result) {
                    if (result != null && result.size() > 0) {
                        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_rund, 7,LinearLayout.LayoutParams.WRAP_CONTENT);
                        ListView reasonList = (ListView) dialog.findViewById(R.id.reason_list);
                        TextView cancle = (TextView) dialog.findViewById(R.id.cancle);
                        TextView ok = (TextView) dialog.findViewById(R.id.ok);
                        TextView rund_title = (TextView) dialog.findViewById(R.id.rund_title);

                        rund_title.setText("请选择退菜原因");
                        ok.setText("退菜");

                        final OrderSingleReasonAdp adp = new OrderSingleReasonAdp(context);
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
                                refundDishOrOrder(adp.getSelectId());
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

    /**
     * 退菜和退单的操作
     */
    private void refundDishAndRefundOrder() {
        //判断是否有退菜权限
        PowerController.isLogicPower(context,  PowerController.REFUND_DISH,new PermissionCallback() {
            @Override
            public void havePermission() {
                getSingleReason();
            }

            @Override
            public void withOutPermission() {

            }
        });
    }

    /**
     * 反结账
     */
    private void reCheckoutOrder() {
        //判断是否有打结账权限
        PowerController.isLogicPower(context,  PowerController.CHECK_OUT_BACK,new PermissionCallback() {
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
                        final boolean[] isReCheckOut = {false};
                        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_recheckout, 7, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ListView reasonList = (ScrolListView) dialog.findViewById(R.id.reason_list);
                        TextView cancle = (TextView) dialog.findViewById(R.id.cancle);
                        TextView ok = (TextView) dialog.findViewById(R.id.ok);
                        TextView rund_title = (TextView) dialog.findViewById(R.id.rund_title);
                        final LinearLayout type_no = (LinearLayout) dialog.findViewById(R.id.type_no);
                        final LinearLayout type_yes = (LinearLayout) dialog.findViewById(R.id.type_yes);

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

                        final OrderSingleReasonAdp adp = new OrderSingleReasonAdp(context);
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
                                reverseCheckOut(isReCheckOut[0],adp.getSelectId());
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
    private void reverseCheckOut(final boolean isReCheckOut,final int reasonId) {
        if (orderPrint != null && orderPrint.getItemList() != null && orderPrint.getItemList().size() > 0) {
            int size = orderPrint.getItemList().size();
            BigDecimal count = new BigDecimal("0.00");
            for (int i = 0; i < size; i++) {
                if(orderPrint.getItemList().size() == i)
                {
                    break;
                }
                OrderItem orderitem = orderPrint.getItemList().get(i);
                boolean isDeleteItem = false;
                if (0 >= orderitem.getQuantity()) {
                    orderPrint.getItemList().remove(i);
                    isDeleteItem = true;
                }
                else{
                    count = count.add(orderitem.getCost().multiply(new BigDecimal(orderitem.getQuantity())));
                }
                if (size == 1) {
                    break;
                }
                if(isDeleteItem)
                {
                    i -= 1;
                }
            }
            orderPrint.setTotal(count.toString());
            orderPrint.setCost(count.toString());

            if (orderPrint.getItemList().size() > 0) {
                Intent tableIntent = new Intent(context, CheckOutNewAty.class);
                tableIntent.putExtra("source", Constant.EventState.SOURCE_TABLE_ORDER);
                tableIntent.putExtra("tableId", orderPrint.getTableId());
                tableIntent.putExtra("reasonId", reasonId);
                tableIntent.putExtra("isReCheckOut", isReCheckOut);
                Bundle tableBun = new Bundle();
                tableBun.putSerializable("tableOrder", orderPrint);
                tableIntent.putExtras(tableBun);
                tableIntent.putExtra("reverseCheckOutFlag", Constant.REVERSE_CHECKOUT);
                startActivityForResult(tableIntent,RECHECKOUT);
            }
        }
    }



    private void refundDishOrOrder(final int reasonId) {
//        if (isRefundDish) {
            removeDish(reasonId, RetreatDishController.getTempItemList());
//        }
//        else {
//            DialogUtil.ordinaryDialog(context, "退单", "确定要退单吗?", new DialogCallback() {
//                @Override
//                public void onConfirm() {
//                    if ("NOT_PAYED".equals(paymentstate)) {
//                        closOrder(reasonId, orderId);
//                    } else {
//                        refundOrder(reasonId);
//                    }
//                }
//
//                @Override
//                public void onCancle() {
//
//                }
//            });
//        }
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

    private void removeDish(final int reasonId, final List<OrderItem> orderItemList) {
        StringBuffer sb = new StringBuffer();
        if (orderItemList != null && orderItemList.size() > 0) {
            int size = orderItemList.size();
            int selectCount = 0;//已经选择退的份数
            int dishCanCount = 0;//可退的份数
            for (int i = 0; i < size; i++) {
                OrderItem orderItem = orderItemList.get(i);
                int rejected = orderItem.getRejectedQuantity();
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
                DialogUtil.ordinaryDialog(context, "退单", "菜品退菜份数已满,是否需要退单?", new DialogCallback() {

                    @Override
                    public void onConfirm() {
                        if ("NOT_PAYED".equals(paymentstate)) {
                            closOrder(reasonId, orderId);
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
                DialogUtil.ordinaryDialog(context, "退菜", "是否确认退" + sb.toString(), new DialogCallback() {
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

    //退单(先支付后下单使用)
    private void refundOrder(int reasonId) {
        try {
            showProgress();
            TradeService tradeService = TradeService.getInstance();
            UserData userData = UserData.getInstance(context);
            final Order newOrder = orderPrint;
            newOrder.setId(Long.valueOf(orderId));
            tradeService.refund(newOrder, reasonId, userData.getRealName(), new ResultCallback<PosResponse>() {
                @Override
                public void onResult(PosResponse result) {
                    dissmiss();
                    if (result.isSuccessful()) {//退款\菜成功
                        showToast("退单成功");
                        Log.i("退单成功", "success");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_ORDER,orderId));
                        finish();
//                        kdsDeleteOrder(orderId);
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

    //退菜(先支付后下单使用)
    private void refundDish(int reasonId, final List<OrderItem> orderItemList) {
        try {
            showProgress();
            TradeService tradeService = TradeService.getInstance();
            UserData userData = UserData.getInstance(context);
            final Order newOrder = orderPrint;
            newOrder.setItemList(orderItemList);
            tradeService.refundDish(orderId, reasonId, userData.getRealName(), newOrder, new ResultCallback<PosResponse>() {
                @Override
                public void onResult(PosResponse result) {
                    dissmiss();
                    if (result.isSuccessful()) {//退款\菜成功
                        showToast("退菜成功");
                        Log.i("退菜成功", "success");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_DISH,orderItemList,orderId));
                        finish();
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

    //先下单后支付的退菜（类似于桌台点单退菜）
    private void retreatDish(final int reasonId, final List<OrderItem> itemList) {
        try {
            showProgress();
            TableService tableService = TableService.getInstance();
            tableService.removeDish(reasonId, orderPrint, itemList, new ResultCallback<Order>() {
                @Override
                public void onResult(Order result) {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                    dissmiss();
                    Order newOrder = orderPrint;
                    newOrder.setItemList(itemList);
                    showToast("退菜成功!");
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_DISH,orderItemList,orderId));
                    finish();
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
     * 先下单后支付的关闭订单（类似于桌台退单）
     *
     * @param orderId
     */
    private void closOrder(final int reasonId, final String orderId) {
        try {
            TradeService tradeService = TradeService.getInstance();
            final Order newOrder = orderPrint;
            newOrder.setId(Long.valueOf(orderId));
            tradeService.closeOrder(reasonId, newOrder, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("退单成功!");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS_REFUND_ORDER,orderId));
                        finish();
//                        kdsDeleteOrder(orderId);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECHECKOUT) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

}
