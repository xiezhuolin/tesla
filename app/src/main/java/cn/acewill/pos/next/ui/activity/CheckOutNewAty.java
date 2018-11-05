package cn.acewill.pos.next.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliGoodsItem;
import com.acewill.paylibrary.epay.DateUtils;
import com.acewill.paylibrary.tencent.WXPay;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogActiveCall;
import cn.acewill.pos.next.interfices.KeyCallBack;
import cn.acewill.pos.next.model.Customer;
import cn.acewill.pos.next.model.MarketObject;
import cn.acewill.pos.next.model.MarketType;
import cn.acewill.pos.next.model.PaymentCategory;
import cn.acewill.pos.next.model.PaymentList;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.PaymentStatus;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.model.wsh.WshCreateDeal;
import cn.acewill.pos.next.model.wsh.WshDealPreview;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.TradeService;
import cn.acewill.pos.next.service.WshService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.service.retrofit.response.ValidationResponse;
import cn.acewill.pos.next.ui.adapter.PayOrderItemAdp;
import cn.acewill.pos.next.ui.adapter.PayTypeAdapter;
import cn.acewill.pos.next.ui.adapter.PayTypeSelectedAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.FormatUtils;
import cn.acewill.pos.next.utils.PayDialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.WindowUtil;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.ComTextView;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * 结账新UI
 * Created by aqw on 2016/11/14.
 */
public class CheckOutNewAty extends BaseActivity {
    @BindView( R.id.back_ll )
    LinearLayout backLl;
    @BindView( R.id.moneybox_ll )
    LinearLayout moneyboxLl;
    @BindView( R.id.service_btn )
    ComTextView serviceBtn;
    @BindView( R.id.discount_btn )
    ComTextView discountBtn;
    @BindView( R.id.remark_btn )
    ComTextView remarkBtn;
    @BindView( R.id.paytype_lv )
    ScrolListView paytypeLv;
    @BindView( R.id.print_btn )
    ComTextView printBtn;
    @BindView( R.id.checkout_btn )
    ComTextView checkoutBtn;
    @BindView( R.id.ticket_title )
    ComTextView ticketTitle;
    @BindView( R.id.ticket_table )
    ComTextView ticketTable;
    @BindView( R.id.ticket_time )
    ComTextView ticketTime;
    @BindView( R.id.ticket_ordernum )
    ComTextView ticketOrdernum;
    @BindView( R.id.ticket_cashier )
    ComTextView ticketCashier;
    @BindView( R.id.ticket_subtotal )
    ComTextView ticketSubtotal;
    @BindView( R.id.ticket_service )
    ComTextView ticketService;
    @BindView( R.id.ticket_discount )
    ComTextView ticketDiscount;
    @BindView( R.id.ticket_change )
    ComTextView ticketChange;
    @BindView( R.id.ticket_total )
    ComTextView ticketTotal;
    @BindView( R.id.ticket_paylist )
    ScrolListView ticketPaylist;
    @BindView( R.id.ticket_nopay_tv )
    ComTextView ticketNopayTv;
    @BindView( R.id.ticket_nopay_title )
    ComTextView ticketNopayTitle;
    @BindView( R.id.order_item_lv )
    ScrolListView orderItemLv;
    @BindView( R.id.advance_checkout_btn )
    ComTextView advanceCheckoutBtn;

    private static final int FAIL_ORDER = 0;//下单失败
    private static final int FAIL_ORDER_KIT = 1;//kds下单失败

    private DishService dishService;
    private TradeService tradeService;
    private OrderService orderService;
    private WshService wshService;
    private ProgressDialogF progressDialog;
    private PayTypeAdapter payTypeAdapter;
    private PayTypeSelectedAdp paySelectAdp;
    private PayOrderItemAdp payOrderItemAdp;
    private List<Printer> printerList;//打印机列表

    public BigDecimal total_money = new BigDecimal(0);//总金额
    public BigDecimal nopay_money = new BigDecimal(0);//未支付金额
    public BigDecimal temp_money = new BigDecimal(0);//总支付金额，用于营销活动计算使用
    private BigDecimal service_money = new BigDecimal(0);//服务费
    private BigDecimal avtive_money = new BigDecimal(0);//营销活动减免金额
    private BigDecimal wipingValue = new BigDecimal("0.00");//被抹零的金额
    private List<Printer> selectPrint;//选择的打印机
    private List<PaymentList> paymentLists = new ArrayList<PaymentList>();//结账时选择的支付方式信息
    public List<AliGoodsItem> aliGoodsItem = new ArrayList<AliGoodsItem>();//支付宝参数
    public CopyOnWriteArrayList<ValidationResponse> addValidation = new CopyOnWriteArrayList<>();
    public String orderId;//订单id
    private int source = 0;//0:从点菜页面过来；1:从当日订单过来，2：桌台过来
    private Cart cart;
    private Store store;
    private String biz_id;//微生活会员业务流水号，用于提交交易时使用，创建交易预览时给此值赋值
    private Account accountMember = null;//结账微生活会员信息  用于记录当前结账微生活会员信息
    private CardRecord cardRecord = null;//挂账信息
    private WshDealPreview wshDealPreview;//创建交易预览时给此值赋值
    public List<OrderItem> orderItems;
    private Order printOrder;
    private boolean isAppendDish = false;
    private boolean isAnyCheckOut = false;//允许输入任意金额结账,但必须有一种支付方式即使为零

    private String wft_transaction_id;

    public int reverseCheckOutFlag = 0;//反结账标识
    private int reasonId = 0;//原因Id
    private boolean isReCheckOut = false;
    private BigDecimal anyCheckoutPrintMoney = null;//任意输入的支付金额
    PosInfo posInfo;
    private int wftPayType = 3;//选择威富通 是微信支付还是支付宝支付  0是微信 1是支付宝

    public int getWftPayType() {
        return wftPayType;
    }

    public void setWftPayType(int wftPayType) {
        this.wftPayType = wftPayType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acy_check_out_new);
        myApplication.addPage(CheckOutNewAty.this);

        //初始化 BufferKnife
        ButterKnife.bind(this);
        initView();
        switchLogic();
        getPayType();
        if (!ToolsUtils.logicIsTable()) {
            advanceCheckoutBtn.setVisibility(View.GONE);
        }
        isAnyCheckOut = PowerController.isAllow(PowerController.TAKE_OUT_SALE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        SunmiSecondScreen.getInstance(context).showImageListScreen();
    }

    private void initView() {
        progressDialog = new ProgressDialogF(context);
        try {
            anyCheckoutPrintMoney = null;
            source = getIntent().getIntExtra("source", 0);
            posInfo = PosInfo.getInstance();
            cart = Cart.getInstance();
            store = Store.getInstance(context);
            dishService = DishService.getInstance();
            tradeService = TradeService.getInstance();
            orderService = OrderService.getInstance();
            wshService = WshService.getInstance();

            payTypeAdapter = new PayTypeAdapter(context);
            payOrderItemAdp = new PayOrderItemAdp(context);
            paytypeLv.setAdapter(payTypeAdapter);
            paySelectAdp = new PayTypeSelectedAdp(context);
            ticketPaylist.setAdapter(paySelectAdp);
            orderItemLv.setAdapter(payOrderItemAdp);

            String storeName = Store.getInstance(context).getStoreName();
            ticketTitle.setText("欢迎光临" + storeName);
            ticketTime.setText("时   间: " + DateUtils.getCurrentDate());
            ticketCashier.setText("收银员: " + UserData.getInstance(context).getRealName());


        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    //根据不同页面来源处理
    private String tableName;

    private void switchLogic() {
        switch (source) {
            case 0://下单页面过来
                Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
                avtive_money = (BigDecimal) getIntent().getSerializableExtra("active_money");
                tableName = getIntent().getStringExtra("tableName");
                String activeName = getIntent().getStringExtra("activityName");
                orderId = posInfo.getOrderId() + "";
                ticketOrdernum.setText(ToolsUtils.returnXMLStr("order_id_xx") + orderId);
                BigDecimal cartCost = new BigDecimal(cart.getCost()).setScale(2, BigDecimal.ROUND_HALF_UP);
                temp_money = total_money = nopay_money = ToolsUtils.wipeZeroMoney(cartCost);
                wipingValue = cartCost.subtract(total_money);
                ticketSubtotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");//小计
                ticketTotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");//合计

                printOrder = cart.getOrderItem(tableOrder, cart.getDishItemList());
                orderItems = cart.getOrderItem(tableOrder, cart.getDishItemList()).getItemList();
                updateTicket();

                payTypeAdapter.setOrder(printOrder);
                if (avtive_money != null && avtive_money.compareTo(BigDecimal.ZERO) > 0) {
                    ticketDiscount.setText("-" + avtive_money + "￥");
                    //客显屏显示优惠金额
                    printOrder.setAvtive_money(avtive_money);
                }
                if (!TextUtils.isEmpty(activeName)) {
                    printOrder.setAvtiveName(activeName);
                }
                showScreen();

                payOrderItemAdp.setData(orderItems);

                if (orderItems != null) {
                    for (OrderItem orderItem : orderItems) {
                        AliGoodsItem item = new AliGoodsItem();
                        item.setGoods_id(orderItem.getId() + "");
                        item.setGoods_category(orderItem.getDishId() + "");
                        item.setPrice(FormatUtils.getDoubleW(new BigDecimal(orderItem.getPrice() + "").doubleValue()));
                        item.setQuantity(orderItem.getQuantity() + "");
                        item.setGoods_name(orderItem.getDishName());
                        aliGoodsItem.add(item);
                    }
                }
                break;
            case 1://当日订单页面过来
                orderId = getIntent().getStringExtra("orderId");
                ticketOrdernum.setText(ToolsUtils.returnXMLStr("order_id_xx") + orderId);
                getOrderById(orderId);
                break;
            case 2:
                Order table = (Order) getIntent().getSerializableExtra("tableOrder");
                reverseCheckOutFlag = getIntent().getIntExtra("reverseCheckOutFlag", 0);
                reasonId = getIntent().getIntExtra("reasonId", 0);
                isReCheckOut = getIntent().getBooleanExtra("isReCheckOut", false);

                printOrder = table;
                showScreen();
                payTypeAdapter.setOrder(printOrder);
                DishDataController.handleOrder(printOrder);
                if (table != null) {
                    orderId = String.valueOf(table.getId());
                    ticketOrdernum.setText(ToolsUtils.returnXMLStr("order_id_xx") + orderId);
                    BigDecimal cartCost2 = new BigDecimal(table.getCost()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    temp_money = total_money = nopay_money = ToolsUtils.wipeZeroMoney(cartCost2);
                    wipingValue = cartCost2.subtract(total_money);
                    ticketSubtotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                    ticketTotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                    updateTicket();

                    orderItems = table.getItemList();

                    payOrderItemAdp.setData(orderItems);
                    if (orderItems != null) {
                        for (OrderItem orderItem : orderItems) {
                            AliGoodsItem item = new AliGoodsItem();
                            item.setGoods_id(orderItem.getId() + "");
                            item.setGoods_category(orderItem.getDishId() + "");
                            item.setPrice(FormatUtils.getDoubleW(new BigDecimal(orderItem.getPrice() + "").doubleValue()));
                            item.setQuantity(orderItem.getQuantity() + "");
                            item.setGoods_name(orderItem.getDishName());
                            aliGoodsItem.add(item);
                        }
                    }
                }
                break;
        }
    }


    //获取支付方式列表
    private void getPayType() {
        if (StoreInfor.getPaymentList() != null && StoreInfor.getPaymentList().size() > 0) {
            initAliAndWx(StoreInfor.getPaymentList());
            payTypeAdapter.setData(StoreInfor.getPaymentList());
            return;
        }
        dishService.getPaytypeList(new ResultCallback<List<Payment>>() {
            @Override
            public void onResult(List<Payment> result) {
                if (result != null && result.size() > 0) {
                    initAliAndWx(result);
                    payTypeAdapter.setData(result);
                }
            }

            @Override
            public void onError(PosServiceException e) {
            }
        });
    }


    //为支付宝和微信支付参数赋值
    private void initAliAndWx(List<Payment> result) {
        for (Payment payment : result) {
            if (payment.getId() == 1) {//支付宝
                AlipayConfig.APPID = payment.getAppIDs();
                AlipayConfig.key = payment.getKeyStr();
            }
            if (payment.getId() == 2) {//微信
                WXPay.APPID = payment.getAppIDs();
                WXPay.KEY = payment.getKeyStr();
                WXPay.MCH_ID = payment.getMchID();
                WXPay.APPSECRET = payment.getAppsecret();
                if (!TextUtils.isEmpty(payment.getSubMchID())) {
                    WXPay.SUB_MCH_ID = payment.getSubMchID();
                }
            }
        }
    }

    //根据订单号获取订单详情
    public void getOrderById(String orderId) {
        orderService.getOrderInfoById(orderId, new ResultCallback<Order>() {
            @Override
            public void onResult(Order result) {
                if (result != null) {
                    printOrder = result;
                    showScreen();
                    payTypeAdapter.setOrder(printOrder);
                    temp_money = total_money = nopay_money = new BigDecimal(result.getTotal());
                    ticketSubtotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                    ticketTotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                    updateTicket();

                    orderItems = result.getItemList();

                    payOrderItemAdp.setData(orderItems);

                    if (orderItems != null) {
                        for (OrderItem orderItem : orderItems) {
                            AliGoodsItem item = new AliGoodsItem();
                            item.setGoods_id(orderItem.getId() + "");
                            item.setGoods_category(orderItem.getDishId() + "");
                            item.setPrice(FormatUtils.getDoubleW(new BigDecimal(orderItem.getPrice() + "").doubleValue()));
                            item.setQuantity(orderItem.getQuantity() + "");
                            item.setGoods_name(orderItem.getDishName());
                            aliGoodsItem.add(item);
                        }
                    }

                }

            }

            @Override
            public void onError(PosServiceException e) {

            }
        });
    }

    //显示14寸客显
    private void showScreen() {
        if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
            SunmiSecondScreen.getInstance(context).showDishImgExcel(printOrder);
        }
    }

    /**
     * 提交会员交易
     */
    private void commitDeal() {
        try {
            boolean isNotVerity = true;//不需要任何验证

            final Dialog smsDialog = DialogUtil.createDialog(context, R.layout.dialog_vertify, 3, LinearLayout.LayoutParams.WRAP_CONTENT, false);
            TextView title = (TextView) smsDialog.findViewById(R.id.title);
            final CommonEditText code = (CommonEditText) smsDialog.findViewById(R.id.code);
            TextView dialog_cancle = (TextView) smsDialog.findViewById(R.id.dialog_cancle);
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
                    commitPay(smsDialog, code);
                    //                    final CheckOutUtil checUtil = new CheckOutUtil(context);
                    //                    checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
                    //                        @Override
                    //                        public void haveStock() {
                    //                            commitPay(smsDialog,code);
                    //                        }
                    //
                    //                        @Override
                    //                        public void noStock(List dataList) {
                    //                            refreshDish(dataList, getDishItemList());
                    //                        }
                    //                    });
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
                code.setHint(ToolsUtils.returnXMLStr("please_input_transaction_password_verification"));
                smsDialog.show();
            }

            code.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (KeyEvent.KEYCODE_ENTER == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
                        if (!TextUtils.isEmpty(code.getText().toString().trim())) {
                            final String num = code.getText().toString().trim();
                            if (TextUtils.isEmpty(num)) {
                                if (wshDealPreview.isVerify_sms()) {
                                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("please_input_SMS_verification"));
                                }
                                if (wshDealPreview.isVerify_password()) {
                                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("please_input_transaction_password_verification"));
                                }
                            } else {
                                WindowUtil.hiddenKey();
                                commitPay(smsDialog, code);
                                //                                final CheckOutUtil checUtil = new CheckOutUtil(context);
                                //                                checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
                                //                                    @Override
                                //                                    public void haveStock() {
                                //                                        commitPay(smsDialog,code);
                                //                                    }
                                //
                                //                                    @Override
                                //                                    public void noStock(List dataList) {
                                //                                        refreshDish(dataList, getDishItemList());
                                //                                    }
                                //                                });

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
                            if (nopay_money.compareTo(BigDecimal.ZERO) == -1 || nopay_money.compareTo(BigDecimal.ZERO) == 0) {
                                tradeLogic();
                            } else {
                                showToast(ToolsUtils.returnXMLStr("please_pay_lave_money"));
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

    private void commitPay(final Dialog smsDialog, CommonEditText code) {
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
                    if (nopay_money.compareTo(BigDecimal.ZERO) == -1 || nopay_money.compareTo(BigDecimal.ZERO) == 0) {
                        tradeLogic();
                    } else {
                        showToast(ToolsUtils.returnXMLStr("please_pay_lave_money"));
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

    public void executeCode(CopyOnWriteArrayList<ValidationResponse> addValidation) {
        progressDialog.showLoading("");
        StoreBusinessService storeBusinessService = null;
        try {
            storeBusinessService = StoreBusinessService.getInstance();
            StringBuffer sbb = new StringBuffer();
            for (ValidationResponse validation : addValidation) {
                if (validation.isSuccess() && validation.vouchersIsEff()) {
                    String sb = sbb.toString();
                    String code = validation.getCouponCode();
                    String h = ",";
                    if (TextUtils.isEmpty(sb)) {
                        sbb.append(code);
                    } else {
                        sbb.append(h + code);
                    }
                }
            }
            //            System.out.println("券码===》2》" + sbb.toString());
            Log.e("组合结账美团交易券列表:====" + orderId, sbb.toString());
            storeBusinessService.executeCode(sbb.toString(), posInfo.getOrderId(), new ResultCallback<ValidationResponse>() {
                @Override
                public void onResult(ValidationResponse result) {
                    progressDialog.disLoading();
                    if (result.isSuccess()) {
                        creatOrdAndCheckOut();
                    } else {
                        MyApplication.getInstance().ShowToast(result.getMessage());
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    Log.e("提交美团交易有误:====" + orderId, e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断是先支付还是后支付:先支付走下单方法，后支付走结账方法
     */
    private void tradeLogic() {
        switch (source) {
            case 0://先支付
                if (addValidation != null && addValidation.size() > 0) {
                    executeCode(addValidation);
                } else {
                    creatOrdAndCheckOut();
                }
                break;
            case 1:
            case 2://后支付
                if (ToolsUtils.isReverseCheckOut(reverseCheckOutFlag)) {
                    reCheckOut();
                } else {
                    checkOut();
                }
                break;
        }
    }

    private void creatDeal(Account accountMember) {
        progressDialog.showLoading("");
        final WshCreateDeal.Request request = new WshCreateDeal.Request();
        final String bis_id = System.currentTimeMillis() + "";
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
                    commitDeal();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("submission_of_member_transaction_failed") + "," + e.getMessage() + "!");
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 先支付后下单：下单接口同时处理结账
     */
    public static final int MIN_CLICK_DELAY_TIME = 1000;//防止重复点击造成的重复下单
    private long lastClickTime = 0;

    private void creatOrdAndCheckOut() {
        if (posInfo.isMemberCheckOut()) {
            creatDeal(posInfo.getAccountMember());
        }
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime <= Constant.MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            return;
        }
        progressDialog.showLoading(ToolsUtils.returnXMLStr("is_placing_an_order"));
        //是挂单
        if (cardRecord != null) {
            printOrder.setCardRecord(cardRecord);
        }
        printOrder.setPaymentStatus(PaymentStatus.PAYED);
        printOrder.setPaymentList(paymentLists);
        printOrder.setPaidAt(System.currentTimeMillis());
        //免单
        if (posInfo.isFreeOrder()) {
            BigDecimal freeMoney = new BigDecimal("0.00");
            printOrder.setCost("0");
            printOrder.setTake_money(freeMoney);
            printOrder.setServiceMoney(freeMoney);
            printOrder.setAvtive_money(new BigDecimal(Cart.getPriceSum()).setScale(2, BigDecimal.ROUND_DOWN));//优惠金额为订单总价
        } else {
            printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN).toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
            printOrder.setTake_money(cart.getTakeMoney());
            if (anyCheckoutPrintMoney != null) {
                printOrder.setCost(anyCheckoutPrintMoney.setScale(2, BigDecimal.ROUND_DOWN).toString());//注意,因为是允许输入任意金额支付所以这里需要把cost改为任意输入的金额
            }

            BigDecimal dish_active = getOrderDishPreferential(printOrder);//菜品优惠金额
            //            BigDecimal dish_active = new BigDecimal(cart.getPriceSum() - cart.getCost());
            printOrder.setServiceMoney(service_money.setScale(2, BigDecimal.ROUND_DOWN));
            printOrder.setAvtive_money(avtive_money.add(dish_active).setScale(2, BigDecimal.ROUND_DOWN));
        }
        printOrder.setTableNames(tableName);
        if (store.isWaiMaiGuestInfo() && posInfo.getOrderType().equals("SALE_OUT") && posInfo.getCustomer() != null) {
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
            printOrder.setMemberGrade(accountMember.getGradeName());
            printOrder.setMemberBizId(getBiz_id());
            printOrder.setAccountMember(accountMember);
            printOrder.setMemberid(accountMember.getUno());
            printOrder.setMemberName(accountMember.getName());
            printOrder.setMemberPhoneNumber(accountMember.getPhone());
        } else if (posInfo.getAccountMember() != null) {
            accountMember = ToolsUtils.cloneTo(posInfo.getAccountMember());
            printOrder.setMemberGrade(accountMember.getGradeName());
            accountMember.setMemberConsumeCost(new BigDecimal(printOrder.getCost()));
            printOrder.setMemberBizId(getBiz_id());
            printOrder.setAccountMember(accountMember);
            printOrder.setMemberid(accountMember.getUno());
            printOrder.setMemberName(accountMember.getName());
            printOrder.setMemberPhoneNumber(accountMember.getPhone());
        }
        printOrder.setCustomerAmount(posInfo.getCustomerAmount());
        printOrder.setComment(posInfo.getComment());
        printOrder.setWipingValue(wipingValue);
        final Order newOrder = ToolsUtils.cloneTo(printOrder);
        setOrderItemMarketInfo();
        if (printOrder != null) {
            orderService.createOrder(printOrder, new ResultCallback<Order>() {
                @Override
                public void onResult(Order result) {
                    anyCheckoutPrintMoney = null;
                    if (result != null) {
                        if (store.isCreateOrderJyj()) {
                            result.setJyjOrder(true);//将下单状态改为向JYJ下单
                            createOrderJyj(result);
                        } else {
                            orderPrint(newOrder, result);
                        }
                        //                        kdsCreatOrder(result, tableName);
                    } else {
                        Log.i("订单下单失败", ToolsUtils.getPrinterSth(result));
                        progressDialog.disLoading();
                        showToast(ToolsUtils.returnXMLStr("orders_failed"));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("订单下单失败", e.getMessage());
                    progressDialog.disLoading();
                    showToast(ToolsUtils.returnXMLStr("orders_failed") + e.getMessage());
                }
            });
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
                            order.setJyjPrintErrMessage(ToolsUtils.returnXMLStr("jyj_order_error_message"));
                            orderPrint(order, order);
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        progressDialog.disLoading();
                        Log.e("JYJ下单", "JYJ下单失败:" + e.getMessage());
                        showToast(ToolsUtils.returnXMLStr("orders_failed") + "," + e.getMessage());
                        order.setJyjPrintErrMessage(ToolsUtils.returnXMLStr("jyj_order_error_message"));
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
        if (!TextUtils.isEmpty(wft_transaction_id)) {
            Log.i("创建威富通交易成功", "订单ID:" + result.getId() + "==威富通交易流水号:" + wft_transaction_id);
        }
        TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
        Log.i("下单成功==》打印结账单跟厨房单", "");

        Log.i("结账页面下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
        EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));
        //System.out.println(ToolsUtils.getPrinterSth(result));
        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));

        newOrder.setId(result.getId());
        newOrder.setCallNumber(result.getCallNumber());
        newOrder.setCustomerAmount(newOrder.getCustomerAmount());
        if (!TextUtils.isEmpty(newOrder.getTableNames())) {
            newOrder.setTableNames(newOrder.getTableNames());
        }

        //下单成功取消免单
        posInfo.setFreeOrder(false);
        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, tableName));

        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, newOrder));

        new Handler().postDelayed(new Runnable() {
            public void run() {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, newOrder));
            }
        }, 3000);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, newOrder));
            }
        }, 3000);
        progressDialog.disLoading();
        finish();
    }

    /**
     * 算出订单优惠的金额
     *
     * @param order
     */
    private BigDecimal getOrderDishPreferential(Order order) {
        BigDecimal orderMoney = new BigDecimal("0.00");
        if (order != null && order.getItemList() != null && order.getItemList().size() > 0) {
            for (OrderItem orderItem : order.getItemList()) {
                if (orderItem.getMarketList() != null && orderItem.getMarketList().size() > 0) {
                    for (MarketObject marketObject : orderItem.getMarketList()) {
                        orderMoney = orderMoney.add(marketObject.getReduceCash());
                    }
                }
            }
        }
        return orderMoney;
    }

    /**
     * 结账
     */
    private void checkOut() {
        progressDialog.showLoading(ToolsUtils.returnXMLStr("is_checking_out"));
        tradeService.checkOut(paymentLists, orderId, new ResultCallback<PosResponse>() {
            @Override
            public void onResult(PosResponse result) {
                progressDialog.disLoading();
                if (result.isSuccessful()) {//结账成功
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    Log.i("结账页面结账成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                    //                    Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
                    printOrder.setServiceMoney(service_money.setScale(2, BigDecimal.ROUND_DOWN));
                    printOrder.setAvtive_money(avtive_money.setScale(2, BigDecimal.ROUND_DOWN));
                    printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN).toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
                    if (!TextUtils.isEmpty(tableName)) {
                        printOrder.setTableNames(tableName);
                    }
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, printOrder));
                    showToast(ToolsUtils.returnXMLStr("check_out_success"));
                    setResult(RESULT_OK);
                    finish();
                } else {//结账失败
                    showToast(result.getErrmsg());
                    Log.i("订单结账失败", ToolsUtils.getPrinterSth(result));
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                Log.i("订单结账失败", e.getMessage());
            }
        });
    }

    private void reCheckOut() {
        progressDialog.showLoading("");
        tradeService.reCheckOut(paymentLists, orderId, reasonId, isReCheckOut, new ResultCallback<PosResponse>() {
            @Override
            public void onResult(PosResponse result) {
                progressDialog.disLoading();
                if (result.isSuccessful()) {//结账成功
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    Log.i("结账页面反结账成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                    //                    Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
                    printOrder.setServiceMoney(service_money.setScale(2, BigDecimal.ROUND_DOWN));
                    printOrder.setAvtive_money(avtive_money.setScale(2, BigDecimal.ROUND_DOWN));
                    printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN).toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
                    if (!TextUtils.isEmpty(tableName)) {
                        printOrder.setTableNames(tableName);
                    }
                    //打双份结账单，发了两次
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, printOrder));
                    showToast(ToolsUtils.returnXMLStr("recheck_out_success"));
                    setResult(RESULT_OK);
                    finish();
                } else {//结账失败
                    showToast(result.getErrmsg());
                    Log.i("订单反结账失败", ToolsUtils.getPrinterSth(result));
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                Log.i("订单反结账失败", e.getMessage());
            }
        });
    }

    /**
     * 会员选择添加成功后,又重新选择了一次,而第一次的会员支付又没有删除所以会导致错误添加
     * 故要将其删除
     */
    public void removeMemberPay() {
        if (paymentLists != null && paymentLists.size() > 0) {
            int size = paymentLists.size();
            for (int i = 0; i < size; i++) {
                if (paymentLists.size() == i) {
                    break;
                }
                boolean isDeleteItem = false;
                PaymentList payMent = paymentLists.get(i);
                if (payMent != null) {
                    int id = payMent.getPaymentTypeId();
                    if (id == 3 || id == 4 || id == 5) {
                        nopay_money = nopay_money.add(payMent.getValue());
                        BigDecimal noPayMoTex = new BigDecimal(ticketNopayTv.getText().toString()).setScale(2, BigDecimal.ROUND_DOWN);
                        ticketNopayTv.setText(noPayMoTex.add(payMent.getValue()).setScale(2, BigDecimal.ROUND_DOWN) + "");

                        paymentLists.remove(payMent);
                        isDeleteItem = true;
                    }
                    if (size == 1) {
                        break;
                    }
                    if (isDeleteItem) {
                        i -= 1;
                    }
                }
            }
        }
    }


    public boolean isHaveMemberPay(int payId) {
        boolean isHave = false;
        if (paymentLists != null && paymentLists.size() > 0) {
            int size = paymentLists.size();
            for (int i = 0; i < size; i++) {
                PaymentList payment = paymentLists.get(i);
                if (payId == payment.getPaymentTypeId()) {
                    isHave = true;
                }
            }
        }
        return isHave;
    }

    /**
     * 更新未支付金额及UI
     *
     * @param pay_money  输入的金额
     * @param cash_money 现金支付金额
     * @param payment    支付方式ID
     * @param paymentNo  微信支付宝流水号
     */
    public void changeMoney(BigDecimal pay_money, BigDecimal cash_money, Payment payment, String paymentNo, String transactionNo, List<Payment> memberPayMent, CopyOnWriteArrayList<ValidationResponse> addValidationLists) {
        nopay_money = nopay_money.subtract(pay_money).setScale(2, BigDecimal.ROUND_DOWN);
        boolean isHaveMemberPay = false;
        int memberListSize = 0;
        if (memberPayMent != null && memberPayMent.size() > 0) {
            isHaveMemberPay = true;
            memberListSize = memberPayMent.size();
        }
        if (payment.getId() == 3 || payment.getId() == 4 || payment.getId() == 5) {
            if (isHaveMemberPay && nopay_money.compareTo(BigDecimal.ZERO) == -1) {
                nopay_money = new BigDecimal(0);
            }
        }
        PosInfo posInfo = PosInfo.getInstance();

        updateTicket();

        boolean isAdded = false;
        for (PaymentList paymentList : paymentLists) {
            for (int i = 0; i < memberListSize; i++) {
                Payment memberPay = memberPayMent.get(i);
                if (memberPay.getId() == paymentList.getPaymentTypeId()) {
                    isAdded = true;
                    //如果使用的是会员，则重置会员消费金额，因为只有在点击结账时，才提交会员交易
                    if (memberPay.getId() == 3 || memberPay.getId() == 4 || memberPay.getId() == 5) {
                        if (memberPay.getDeductibleAmount() > 0) {
                            paymentList.setValue(new BigDecimal(memberPay.getDeductibleAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                            paymentList.setPaymentNo(paymentNo);
                        }
                    }
                }
                break;
            }

            if (payment.getId() == paymentList.getPaymentTypeId() && (payment.getId() != 3 && payment.getId() != 4 && payment.getId() != 5 && payment.getId() != -32 && payment.getId() != -8)) {
                isAdded = true;
                //其他支付方式只相加金额
                //如果是现金类型则不能按输入的金额算，取输入金额与未支付金额最小值
                if (payment.getCategory() == PaymentCategory.CASH) {
                    if (cash_money.compareTo(BigDecimal.ZERO) == 1) {
                        paymentList.setValue(paymentList.getValue().add(cash_money));
                    }
                } else {
                    if (pay_money.compareTo(BigDecimal.ZERO) == 1) {
                        paymentList.setValue(paymentList.getValue().add(pay_money));
                    }
                }
                break;
            }
        }
        //支付成功后添加到支付方式列表paymentLists，用于下单或结账接口使用
        if (!isAdded) {
            boolean isHaveMber = false;
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
                        if (memberPay.getId() == 3 || memberPay.getId() == 4 || memberPay.getId() == 5 || memberPay.getId() == -8) {
                            checkModle.setPaymentNo(paymentNo);
                            if (memberPay.getId() == -8) {
                                wft_transaction_id = transactionNo;
                                if (wftPayType == 0) {
                                    checkModle.setSource(ToolsUtils.returnXMLStr("wxPay"));
                                } else if (wftPayType == 1) {
                                    checkModle.setSource(ToolsUtils.returnXMLStr("sth_zfb"));
                                }
                                checkModle.setPaymentNo(transactionNo);
                            }
                        } else {
                            checkModle.setPaymentNo(orderId);
                        }
                        checkModle.setValue(new BigDecimal(memberPay.getDeductibleAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
                        checkModle.setOperation("PAY");
                        checkModle.setPayName(memberPay.getName());
                        paymentLists.add(checkModle);
                    }
                }
            }

            boolean isHaveMt = false;
            if (addValidationLists != null && addValidationLists.size() > 0) {
                int validationSize = addValidationLists.size();
                for (int i = 0; i < validationSize; i++) {
                    isHaveMt = true;
                    ValidationResponse validation = addValidationLists.get(i);
                    if (validation != null) {
                        addValidation.add(validation);
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
                            checkModle.setValue(new BigDecimal(validation.getDealValue()).setScale(2, BigDecimal.ROUND_HALF_UP));
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
                if (payment.getId() == 3 || payment.getId() == 4 || payment.getId() == 5 || payment.getId() == -8) {
                    checkModle.setPaymentNo(paymentNo);
                    if (payment.getId() == -8) {
                        wft_transaction_id = transactionNo;
                        if (wftPayType == 0) {
                            checkModle.setSource(ToolsUtils.returnXMLStr("wxPay"));
                        } else if (wftPayType == 1) {
                            checkModle.setSource(ToolsUtils.returnXMLStr("sth_zfb"));
                        }
                        checkModle.setPaymentNo(transactionNo);
                    }
                } else {
                    checkModle.setPaymentNo(orderId);
                }
                if (payment.getCategory() == PaymentCategory.CASH) {//如果是现金类型则不能按输入的金额算，取输入金额与未支付金额最小值
                    checkModle.setValue(cash_money);
                } else {
                    checkModle.setValue(pay_money);
                }
                if (!TextUtils.isEmpty(transactionNo)) {
                    checkModle.setTransactionNo(transactionNo);
                }
                checkModle.setOperation("PAY");
                checkModle.setPayName(payment.getName());

                //如果是现金类型则不能按输入的金额算，取输入金额与未支付金额最小值
                if (payment.getCategory() == PaymentCategory.CASH) {
                    if (cash_money.compareTo(BigDecimal.ZERO) == 1) {
                        paymentLists.add(checkModle);
                    }
                } else {
                    if (pay_money.compareTo(BigDecimal.ZERO) == 1) {
                        paymentLists.add(checkModle);
                    }
                }
            }

        }
        printOrder.setPaymentList(paymentLists);
        printOrder.setPay_money(total_money.subtract(nopay_money));

        if (!TextUtils.isEmpty(orderId)) {
            printOrder.setPaymentNo(orderId);
        }

        //有对应的权限，并且选择的支付方式是不需要检查余额的, 这时候“结账”按钮该成可用状态
        if (isAnyCheckOut && !payment.isCheckBalance() && paymentLists != null && paymentLists.size() >= 1) {
            checkoutBtn.setBackgroundResource(R.drawable.pay_btn_press);

            checkoutBtn.setEnabled(true);
            ticketNopayTitle.setText(ToolsUtils.returnXMLStr("sth_returnMoney"));
            checkoutBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
            ticketNopayTv.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
            printOrder.setGive_money(nopay_money);

            anyCheckoutPrintMoney = new BigDecimal(pay_money.setScale(2, BigDecimal.ROUND_DOWN).toString());
        }
        //更新已选择的支付方式UI
        paySelectAdp.setData(paymentLists);
        payTypeAdapter.setClickPay(true);
    }

    //更新小票UI数据
    private void updateTicket() {
        //更新结账按钮状态
        if (nopay_money.compareTo(BigDecimal.ZERO) == 1) {
            checkoutBtn.setBackgroundResource(R.drawable.layout_gray);
            checkoutBtn.setEnabled(false);
            checkoutBtn.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
            ticketNopayTitle.setText(ToolsUtils.returnXMLStr("unpaid2"));
            ticketNopayTv.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
        } else {
            checkoutBtn.setBackgroundResource(R.drawable.pay_btn_press);
            checkoutBtn.setEnabled(true);
            ticketNopayTitle.setText(ToolsUtils.returnXMLStr("sth_returnMoney"));
            checkoutBtn.setTextColor(ContextCompat.getColor(context, R.color.white));
            ticketNopayTv.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
            printOrder.setGive_money(nopay_money);
        }
    }

    public String getBiz_id() {
        return biz_id;
    }

    public void setBiz_id(String biz_id) {
        this.biz_id = biz_id;
    }

    public WshDealPreview getWshDealPreview() {
        return wshDealPreview;
    }

    public void setWshDealPreview(WshDealPreview wshDealPreview) {
        this.wshDealPreview = wshDealPreview;
    }

    public Account getAccountMember() {
        return accountMember;
    }

    public void setAccountMember(Account accountMember) {
        this.accountMember = accountMember;
    }

    public CardRecord getCardRecord() {
        return cardRecord;
    }

    public void setCardRecord(CardRecord cardRecord) {
        this.cardRecord = cardRecord;
    }

    public CopyOnWriteArrayList<ValidationResponse> getAddValidation() {
        return addValidation;
    }

    public void setAddValidation(CopyOnWriteArrayList<ValidationResponse> addValidations) {
        this.addValidation = ToolsUtils.cloneTo(addValidations);
    }

    @OnClick( {R.id.back_ll, R.id.moneybox_ll, R.id.service_btn, R.id.discount_btn, R.id.remark_btn, R.id.print_btn, R.id.checkout_btn, R.id.advance_checkout_btn} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.advance_checkout_btn:
                if (ToolsUtils.getCustomerInfoForWaimai() && posInfo.getOrderType().equals("SALE_OUT")) {
                    Customer customer = posInfo.getCustomer();
                    if (customer != null) {
                        printOrder.setCustomerAddress(customer.getCustomerAddress());
                        printOrder.setCustomerPhoneNumber(customer.getCustomerPhoneNumber());
                        printOrder.setCustomerName(customer.getCustomerName());
                        printOrder.setOuterOrderid(customer.getCustomerOuterOrderId());
                        printOrder.setSource(ToolsUtils.returnXMLStr("manual_input"));//手动录入标识
                    }
                }
                TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                printOrder.setCost(total_money.setScale(2, BigDecimal.ROUND_DOWN).toString());//注意，此处total_money已经是计算了服务费与营销活动后的金额
                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, printOrder, Constant.EventState.ORDER_TYPE_ADVANCE));
                break;
            case R.id.back_ll://返回
                if (paymentLists.size() > 0) {
                    showToast(ToolsUtils.returnXMLStr("checkout_exit_error"));
                } else {
                    finish();
                }
                break;
            case R.id.service_btn://服务费
                Payment payment = new Payment(99, ToolsUtils.returnXMLStr("service_charge"));
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        nopay_money = nopay_money.subtract(service_money);//先减去上一次输入的服务费，避免出现多次输入服务费相加的结果
                        total_money = total_money.subtract(service_money);//先减去上一次输入的服务费，避免出现多次输入服务费相加的结果
                        service_money = (BigDecimal) o;
                        ticketService.setText(service_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                        nopay_money = nopay_money.add(service_money);
                        total_money = total_money.add(service_money);
                        ticketTotal.setText(total_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                        updateTicket();
                    }
                }, new BigDecimal(5), false);
                break;
            case R.id.discount_btn://打折
                PayDialogUtil.getActiveDialog(context, new BigDecimal[]{temp_money}, printOrder, null, new DialogActiveCall() {
                    @Override
                    public void onOk(BigDecimal allMoney, BigDecimal activeMoney, String activeName) {
                        avtive_money = activeMoney;
                        //优惠的金额保留小数点
                        avtive_money = avtive_money.setScale(2, BigDecimal.ROUND_DOWN);
                        ticketDiscount.setText("-" + avtive_money + "￥");

                        allMoney = ToolsUtils.wipeZeroMoney(allMoney);
                        nopay_money = total_money = allMoney;
                        //最后的总额去掉小数点
                        ticketTotal.setText(total_money + "￥");

                        //setOrderItemMarket(new MarketObject(activeName, avtive_money, MarketType.DISCOUNT));

                        updateTicket();
                        orderItems = ToolsUtils.cloneTo(printOrder.getItemList());
                        payOrderItemAdp.setData(orderItems);

                        //客显屏显示优惠金额
                        printOrder.setAvtive_money(avtive_money);
                        printOrder.setAvtiveName(activeName);
                        showScreen();
                    }
                });
                break;
            case R.id.remark_btn://备注
                PayDialogUtil.setMarkDialog(context, posInfo.getComment(), new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        String remark = o.toString();
                        posInfo.setComment(remark);
                    }
                });
                break;
            case R.id.print_btn://收据打印机
                PayDialogUtil.selectPrint(context, selectPrint, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        selectPrint = (List<Printer>) o;
                        if (selectPrint != null && selectPrint.size() > 0) {
                            String printName = "";
                            for (Printer printer : selectPrint) {
                                printName += "[" + printer.getDeviceName() + "]";
                            }
                            printBtn.setText(printName + "上打印");
                        } else {
                            printBtn.setText("选择打印机");
                        }
                    }
                });
                break;
            case R.id.checkout_btn://结账
                pay();
                break;
        }
    }

    private void setOrderItemMarketInfo() {
        List<OrderItem> orderItemList = printOrder.getItemList();
        int size = orderItemList.size();
        for (int i = 0; i < size; i++) {
            OrderItem oi = orderItemList.get(i);
            oi.marketList = null;//要先将菜品里已经存在的优惠方案给清除掉然后下面会重新全部添加
            if (oi.getMarketList() == null) {
                oi.marketList = new ArrayList<>();
            }
            if (!ToolsUtils.isList(orderItems.get(i).getMarketList())) {
                oi.getMarketList().addAll(orderItems.get(i).getMarketList());//添加优惠方案
            }
            if (!ToolsUtils.isList(orderItems.get(i).getTempMarketList())) {
                oi.getMarketList().addAll(orderItems.get(i).getTempMarketList());//添加临时列表里的优惠方案
                oi.setCost(orderItems.get(i).getTempPrice());//把存储的价格赋值给cost
            }
            if (posInfo.isFreeOrder())//如果是免单
            {
                MarketObject marketObject = new MarketObject(ToolsUtils.returnXMLStr("free_order"), oi.getCost(), MarketType.MANUAL);
                oi.getMarketList().add(marketObject);
            }
        }
    }


    //结账
    private void pay() {
        if (TextUtils.isEmpty(biz_id)) {
            if (paymentLists != null && paymentLists.size() >= 1 && isAnyCheckOut) {
                tradeLogic();
            } else {
                if (nopay_money.compareTo(BigDecimal.ZERO) == -1 || nopay_money.compareTo(BigDecimal.ZERO) == 0) {
                    tradeLogic();
                } else {
                    showToast(ToolsUtils.returnXMLStr("please_pay_lave_money"));
                }
            }
        } else {//提交会员交易
            commitDeal();
        }
    }

}
