package cn.acewill.pos.next.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acewill.paylibrary.EPayTask;
import com.acewill.paylibrary.MicropayTask;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliGoodsItem;
import com.acewill.paylibrary.epay.EPayResult;
import com.acewill.paylibrary.tencent.WXPay;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.BtnOnClickListener;
import cn.acewill.pos.next.model.KdsDishItem;
import cn.acewill.pos.next.model.KdsOrder;
import cn.acewill.pos.next.model.KdsOrderData;
import cn.acewill.pos.next.model.PaymentList;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.model.ScanType;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.CookingMethod;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.PaymentStatus;
import cn.acewill.pos.next.model.payment.PayType;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.presenter.OrderPresenter;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.PrintManager;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.TradeService;
import cn.acewill.pos.next.service.retrofit.response.KDSResponse;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.adapter.OrderDishAdapter;
import cn.acewill.pos.next.ui.adapter.PayTypeMoneyAdp;
import cn.acewill.pos.next.ui.adapter.PrinterAdp;
import cn.acewill.pos.next.ui.adapter.SelectPayTypeAdp;
import cn.acewill.pos.next.ui.presentation.SecondaryCheckout;
import cn.acewill.pos.next.ui.presentation.SecondaryPresentation;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.CreateImage;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.FormatUtils;
import cn.acewill.pos.next.utils.Keys;
import cn.acewill.pos.next.utils.ScanGunKeyEventHelper;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;

/****************废弃**********
 * 支付结账
 */
public class CheckOutAty extends BaseActivity implements DialogView, ScanGunKeyEventHelper.OnScanSuccessListener {


    @BindView(R.id.close_btn)
    LinearLayout closeBtn;
    @BindView(R.id.order_dishs)
    ListView orderDishs;
    @BindView(R.id.post_type_ll)
    LinearLayout postTypeLl;
    @BindView( R.id.post_type_lv )
    ListView postTypeLv;
    @BindView( R.id.total_money )
    TextView totalMoney;
    @BindView(R.id.unpaid_tv)
    TextView unpaidTv;
    @BindView(R.id.unpaid_money)
    TextView unpaidMoney;
    @BindView( R.id.paytype_gv )
    RecyclerView paytypeGv;
    @BindView( R.id.key_one )
    LinearLayout keyOne;
    @BindView( R.id.key_two )
    LinearLayout keyTwo;
    @BindView( R.id.key_three )
    LinearLayout keyThree;
    @BindView( R.id.key_delete )
    LinearLayout keyDelete;
    @BindView( R.id.key_four )
    LinearLayout keyFour;
    @BindView( R.id.key_five )
    LinearLayout keyFive;
    @BindView( R.id.key_six )
    LinearLayout keySix;
    @BindView( R.id.key_clear )
    LinearLayout keyClear;
    @BindView( R.id.key_seven )
    LinearLayout keySeven;
    @BindView( R.id.key_eight )
    LinearLayout keyEight;
    @BindView( R.id.key_nine )
    LinearLayout keyNine;
    @BindView( R.id.key_zero )
    LinearLayout keyZero;
    @BindView( R.id.key_point )
    LinearLayout keyPoint;
    @BindView( R.id.key_pay )
    LinearLayout keyPay;
    @BindView( R.id.key_cash_box )
    LinearLayout keyCashBox;
    @BindView( R.id.tv_select_printer )
    TextView tvSelectPrinter;


    private OrderPresenter orderPresenter;
    private OrderDishAdapter orderDishAdapter;
    private DishService dishService;
    private TradeService tradeService;
    private SelectPayTypeAdp selectPayTypeAdp;
    private PayTypeMoneyAdp payTypeMoneyAdp;
    private SecondaryCheckout secondaryCheckout;

    private String orderId;//从上一个页面传人

    private double total_money;//总金额
    private double print_money;//输入金额
    private double nopay_money;//未付金额
    private String print_str = "";//输入的字符
    private int cur_position = 0;
    private int payTypeID = 0;

    private boolean isDebug = true;// Debug模式，支付金额为0.01元
    private boolean isFront = false;// true:正扫，false:反扫(使用扫描枪)
    private int pay_channel = PayReqModel.PTID_SSS_WEIXIN;// 支付宝：PayReqModel.PTID_SSS_ALI，微信:PTID_SSS_WEIXIN
    private List<AliGoodsItem> aliGoodsItem = new ArrayList<AliGoodsItem>();//支付宝参数
    private List<PayType> payTypeList = new ArrayList<>();//用户已选的支付信息

    private InTask task;
    private OutTask outTask;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    private Dialog paydialog;
    private ProgressDialogF progressDialog;
    private Cart cart;
    private OrderService orderService;
    private String paymentNo;//微信支付流水号，用于退款
    private String paymentNo_ali;//支付宝的支付流水号

    private int source = 0;//0:从点菜页面过来；1:从当日订单过来

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_check_out);
        myApplication.addPage(CheckOutAty.this);

        //初始化 BufferKnife
        ButterKnife.bind(this);

        initView();
        initData();
        switchLogic();
        getPrinterList(false);
    }

    private void initView() {


    }

    private void initData() {
        source = getIntent().getIntExtra("source", 0);
        cart = Cart.getInstance();
        orderPresenter = new OrderPresenter(this);
        progressDialog = new ProgressDialogF(this);
        //        progressDialog.setCanceledOnTouchOutside(false);
        isFront = (StoreInfor.scanType != ScanType.SCAN_USER);

        if (!isFront) {//反扫初始化扫描枪回调
            mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
        }
        try {
            dishService = DishService.getInstance();
            tradeService = TradeService.getInstance();
            orderService = OrderService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
        orderDishAdapter = new OrderDishAdapter(context);
        orderDishs.setAdapter(orderDishAdapter);
        selectPayTypeAdp = new SelectPayTypeAdp(context);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        paytypeGv.setLayoutManager(linearLayoutManager);
        paytypeGv.setAdapter(selectPayTypeAdp);
        payTypeMoneyAdp = new PayTypeMoneyAdp(context, new BtnOnClickListener() {
            @Override
            public void onClick(int type, int position) {
                if (payTypeList != null && payTypeList.size() > 0) {
                    payTypeList.remove(position);
                    payTypeMoneyAdp.setData(payTypeList);
                }
            }
        });
        postTypeLv.setAdapter(payTypeMoneyAdp);


        selectPayTypeAdp.setOnItemClickListener(new SelectPayTypeAdp.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                int clickType = selectPayTypeAdp.getItem(position).getId();
                if(clickType==6||clickType==7){
                    showToast(ToolsUtils.returnXMLStr("this_method_is_not_supported"));
                    return;
                }
                boolean wxAndAliExit = false;
                for (PayType payType : payTypeList) {
                    if (payType.getId() == 2 && clickType == 1) {
                        wxAndAliExit = true;
                        break;
                    }
                    if (payType.getId() == 1 && clickType == 2) {
                        wxAndAliExit = true;
                        break;
                    }
                }
                if(wxAndAliExit){
                    showToast("微信与支付宝同时只能选则一种");
                    return;
                }
                payTypeID = selectPayTypeAdp.getItem(position).getId();
                selectPayTypeAdp.setCurrent_select(position);
                cur_position = position;
                print_money = 0;
                print_str = "";

                for (PayType payType : payTypeList) {
                    if (payType.getId() == payTypeID) {
                        print_money = payType.getMoney();
                        print_str = payType.getPrint();
                    }
                }

                //选则会员卡时弹出(储值、积分、优惠券)
                switch (payTypeID){
                    case 1://支付宝
                        pay_channel = PayReqModel.PTID_SSS_ALI;
                        break;
                    case 2://微信
                        pay_channel = PayReqModel.PTID_SSS_WEIXIN;
                        break;
                    case 3:case 4:case 5://储值、积分、优惠券

                        break;
                }


//                formatPrint();
//                if (secondaryCheckout != null) {
//                    secondaryCheckout.setPayType(((Payment) selectPayTypeAdp.getItem(position)).getName());
//                }
            }
        });

        setPayBtnEnable(false);

        getPayType();
    }

    //根据不同页面来源处理
    private void switchLogic() {
        switch (source) {
            case 0://下单页面过来
                Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
                orderId = System.currentTimeMillis() + "";

                totalMoney.setText("￥" + cart.getPriceSum());
                unpaidMoney.setText("￥" + cart.getPriceSum());
                total_money = Double.parseDouble(String.valueOf(cart.getPriceSum()));

                List<OrderItem> orderItems = cart.getOrderItem(tableOrder,cart.getDishItemList()).getItemList();
                orderDishAdapter.setData(orderItems);

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
                //客显屏
                initSecondaryScreen(cart.getOrderItem(tableOrder,cart.getDishItemList()));
                break;
            case 1://当日订单页面过来
                orderId = getIntent().getStringExtra("orderId");
                orderPresenter.getOrderById(orderId);
                break;
            case 2:
                Order table = (Order) getIntent().getSerializableExtra("tableOrder");
//                System.out.println(ToolsUtils.getPrinterSth(table));
                if(table != null)
                {
                    orderId = String.valueOf(table.getId());
                    totalMoney.setText("￥" + table.getTotal());
                    unpaidMoney.setText("￥" + table.getTotal());
                    total_money = Double.parseDouble(String.valueOf(table.getTotal()));

                    List<OrderItem> orderTableItems = table.getItemList();
                    orderDishAdapter.setData(orderTableItems);

                    if (orderTableItems != null) {
                        for (OrderItem orderItem : orderTableItems) {
                            AliGoodsItem item = new AliGoodsItem();
                            item.setGoods_id(orderItem.getId() + "");
                            item.setGoods_category(orderItem.getDishId() + "");
                            item.setPrice(FormatUtils.getDoubleW(new BigDecimal(orderItem.getPrice() + "").doubleValue()));
                            item.setQuantity(orderItem.getQuantity() + "");
                            item.setGoods_name(orderItem.getDishName());
                            aliGoodsItem.add(item);
                        }
                    }
                    //客显屏
                    initSecondaryScreen(table);
                }
                break;
        }
    }

    //初始化副屏幕-结账
    private void initSecondaryScreen(Order order) {
        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (dm != null) {
            Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

            for (Display display : displays) {
                if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                    secondaryCheckout = new SecondaryCheckout(this, display, order);
                    secondaryCheckout.show();
                }
            }
        }
    }

    //初始化副屏幕-默认
    private void initSecondaryScreenDf() {
        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (dm != null) {
            Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

            for (Display display : displays) {
                if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                    SecondaryPresentation secondaryPresentation = new SecondaryPresentation(this, display, null);
                    if (secondaryPresentation != null) {
                        secondaryPresentation.show();
                    }
                }
            }
        }
    }


    @Override
    public void showDialog() {

    }

    @Override
    public void dissDialog() {

    }

    @Override
    public void showError(PosServiceException e) {

    }

    //获取支付方式列表
    private void getPayType() {
        dishService.getPaytypeList(new ResultCallback<List<Payment>>() {

            @Override
            public void onResult(List<Payment> result) {
                if (result != null && result.size() > 0) {
                    initAliAndWx(result);
                    selectPayTypeAdp.setDatas(result);
//                    payTypeMoneyAdp.setData(result);
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
                if(!TextUtils.isEmpty(payment.getSubMchID()))
                {
                    WXPay.SUB_MCH_ID = payment.getSubMchID();
                }
            }
        }
    }

    //返回订单菜品数据
    @Override
    public <T> void callBackData(T t) {
        Order mOrder = (Order) t;
        if (mOrder != null) {
            initSecondaryScreen(mOrder);//客显屏

            String tmoney = mOrder.getTotal();
            totalMoney.setText("￥" + tmoney);
            unpaidMoney.setText("￥" + tmoney);

            total_money = TextUtils.isEmpty(tmoney) ? 0 : Double.parseDouble(tmoney);
            List<OrderItem> orderItems = mOrder.getItemList();
            orderDishAdapter.setData(orderItems);

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

    public class openCashBoxThread extends Thread {
        @Override
        public void run() {
            super.run();
            openCashBox();
        }
    }

    /**
     * 打开钱箱
     */
    private void openCashBox() {
        PrintManager.getInstance().openCashBox();
    }

    ArrayList<String> printerList;

    public Dialog printerListDialog(final Context context, final String title, List<Printer> dataList, String printerArr[], final DialogCallback dialogCallback) {
        if (ToolsUtils.isList(dataList)) {
            return null;
        }
        printerList = new ArrayList<String>();
        for (int i = 0; i < printerArr.length; i++) {
            if (!printerArr[i].equals("")) {
                printerList.add(printerArr[i]);
            }
        }
        final Dialog dialog = DialogUtil.getDialog(context, R.layout.aty_receipts);
        TextView print_title = (TextView) dialog.findViewById(R.id.print_title);
        ListView lv_list = (ListView) dialog.findViewById(R.id.lv_receipts);
        TextView print_cancle = (TextView) dialog.findViewById(R.id.print_cancle);
        TextView print_ok = (TextView) dialog.findViewById(R.id.print_ok);
        if (!TextUtils.isEmpty(title)) {
            print_title.setText(title);
        }

        print_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCallback.onCancle();
            }
        });
        print_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCallback.onConfirm();
            }
        });

        final PrinterAdp printerAdp = new PrinterAdp(context);
        printerAdp.setData(dataList);
        printerAdp.setStringArr(printerList);
        lv_list.setAdapter(printerAdp);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Printer printer = (Printer) printerAdp.getItem(position);
                if (printer != null) {
                    int size = printerList.size();
                    String printId = String.valueOf(printer.getId());
                    if (size == 0) {
                        printerList.add(printId);
                    } else {
                        if (isHavePrinter(printId)) {
                            printerList.remove(printId);
                        } else {
                            printerList.add(printId);
                        }
                    }
                    printerAdp.setStringArr(printerList);
                    printerAdp.notifyDataSetChanged();
                }
            }
        });
        return dialog;
    }

    private boolean isHavePrinter(String printerId) {
        int size = printerList.size();
        for (int i = 0; i < size; i++) {
            String printerSth = printerList.get(i);
            if (printerSth.equals(printerId)) {
                return true;
            }
        }
        return false;
    }

    private String savePrinterSelect(ArrayList<String> printerList) {
        StringBuffer sb = new StringBuffer();
        if (printerList != null && printerList.size() > 0) {
            int size = printerList.size();
            for (int i = 0; i < size; i++) {
                String printerSth = printerList.get(i);
                if (!printerSth.equals("")) {
                    sb.append(printerSth + ",");
                }
            }
        }
        String printerSelect = sb.toString();
        Store store = Store.getInstance(context);
        store.setPrinterSelect(printerSelect);
        return printerSelect;
    }

    private String getPringerName(List<Printer> result, ArrayList<String> printerList) {
        StringBuffer sb = new StringBuffer();
        if (!ToolsUtils.isList(result)) {
            if (!ToolsUtils.isList(printerList)) {
                int size = result.size();
                for (int i = 0; i < size; i++) {
                    Printer printer = result.get(i);
                    String printerId = String.valueOf(printer.getId());
                    int printerSize = printerList.size();
                    for (int j = 0; j < printerSize; j++) {
                        String savePrinterId = printerList.get(j);
                        if (printerId.equals(savePrinterId)) {
                            sb.append(printer.getDeviceName() + "打印机,");
                            PrintManager.getInstance().addSelectPrinter(printer);
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取打印机列表
     */
    private void getPrinterList(final boolean isSave) {
        try {
            progressDialog.showLoading("");
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.listPrinters(new ResultCallback<List<Printer>>() {
                @Override
                public void onResult(final List<Printer> result) {
                    progressDialog.disLoading();
                    if (result != null && result.size() > 0) {
                        Store store = Store.getInstance(context);
                        String printerSelect = store.getPrinterSelect();
                        String printerArr[] = printerSelect.split(",");
                        if (isSave) {
                            printerListDialog(context, "打印机列表", result, printerArr, new DialogCallback() {
                                @Override
                                public void onConfirm() {
                                    tvSelectPrinter.setText("选择打印机:" + getPringerName(result, printerList));
                                    savePrinterSelect(printerList);
                                }

                                @Override
                                public void onCancle() {
                                }
                            });
                        } else {
                            printerList = new ArrayList<String>();
                            for (int i = 0; i < printerArr.length; i++) {
                                if (!printerArr[i].equals("")) {
                                    printerList.add(printerArr[i]);
                                }
                            }
                            tvSelectPrinter.setText("选择打印机:" + getPringerName(result, printerList));
                        }

                    } else {
                        showToast("暂未添加任何打印机!");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast("获取打印机列表失败!" + e.getMessage());
                    progressDialog.disLoading();
                }
            });

        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast("获取打印机列表失败!" + e.getMessage());
            progressDialog.disLoading();
        }
    }

    @OnClick({R.id.close_btn, R.id.key_one, R.id.key_two, R.id.key_three, R.id.key_delete,
            R.id.key_four, R.id.key_five, R.id.key_six, R.id.key_clear, R.id.key_seven,
            R.id.key_eight, R.id.key_nine, R.id.key_zero, R.id.key_point, R.id.key_pay, R.id.key_cash_box, R.id.tv_select_printer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select_printer:
                getPrinterList(true);
                break;
            case R.id.key_cash_box:
                //打开钱箱
                new openCashBoxThread().start();
                break;
            case R.id.close_btn:
                finish();
                break;
            case R.id.key_one:
                printValue(Keys.KEY1);
                break;
            case R.id.key_two:
                printValue(Keys.KEY2);
                break;
            case R.id.key_three:
                printValue(Keys.KEY3);
                break;
            case R.id.key_delete:
                printValue(Keys.KEYDE);
                break;
            case R.id.key_four:
                printValue(Keys.KEY4);
                break;
            case R.id.key_five:
                printValue(Keys.KEY5);
                break;
            case R.id.key_six:
                printValue(Keys.KEY6);
                break;
            case R.id.key_clear:
                printValue(Keys.KEYCL);
                break;
            case R.id.key_seven:
                printValue(Keys.KEY7);
                break;
            case R.id.key_eight:
                printValue(Keys.KEY8);
                break;
            case R.id.key_nine:
                printValue(Keys.KEY9);
                break;
            case R.id.key_zero:
                printValue(Keys.KEY0);
                break;
            case R.id.key_point:
                printValue(Keys.KEYPOINT);
                break;
            case R.id.key_pay:
                printValue(Keys.KEYPAY);
                break;
        }
    }

    private void printValue(Keys keys) {

        switch (keys.getValue()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                print_str += keys.getValue() + "";
                break;
            case 10://点
                if (!print_str.contains(".")) {
                    print_str += ".";
                }
                break;
            case 11://删除
                if (print_str.length() > 0) {
                    print_str = print_str.substring(0, print_str.length() - 1);
                }
                break;
            case 12://清空
                print_str = "";
                break;
            case 13://支付
                progressDialog.showLoading("");
                pay();
                break;
        }

        if (keys.getValue() != 13) {
            formatPrint();
        }

    }

    //支付
    private void pay() {

        boolean haveWxorAli = false;
        for (PayType payType : payTypeList) {
            if(payType.getId()==1 || payType.getId()==2){
                haveWxorAli = true;
            }
        }

        if(haveWxorAli){//微信或支付宝
            scanLogic();
        }else{//现金
            tradeLogic();
        }

//        switch (payTypeID) {
//            case 0://现金
//                tradeLogic();
//                break;
//            case 1://支付宝
//                pay_channel = PayReqModel.PTID_SSS_ALI;
//                scanLogic();
//                break;
//            case 2://微信
//                pay_channel = PayReqModel.PTID_SSS_WEIXIN;
//                scanLogic();
//                break;
//            default:
//                progressDialog.dismiss();
//                showToast("此方式暂不支持");
//                break;
//        }
    }

    //扫码判断
    private void scanLogic() {
        // 正扫
        if (isFront) {

            PayReqModel model = new PayReqModel();
            task = new InTask();
            model.totalAmount = total_money;
            model.orderNo = orderId;
            model.wxGoodsDetail = "商品详情";
            model.isDebug = isDebug;
            model.payType = pay_channel;
            model.authCode = "";
            model.aliGoodsItem = aliGoodsItem;
            task.execute(model);

        } else {
            //调用扫码枪
            progressDialog.disLoading();
            //paydialog = DialogUtil.scanGunDialog(context, payTypeID, outTask, mScanGunKeyEventHelper);
        }
    }

    // 反扫支付(扫码枪扫完后调用这个方法)
    private void outPay(String code) {
        PayReqModel model = new PayReqModel();
        outTask = new OutTask();
        model.totalAmount = total_money;
        model.orderNo = orderId;
        model.wxGoodsDetail = "商品详情";
        model.isDebug = isDebug;
        model.payType = pay_channel;
        model.authCode = code;
        model.aliGoodsItem = aliGoodsItem;

        outTask.execute(model);
    }


    /**
     * 正扫(生成二维码)
     *
     * @author aqw
     */
    class InTask extends EPayTask {
        @Override
        protected void onPostExecute(EPayResult result) {
            progressDialog.disLoading();
            if (result != null &&!result.success) {
                showToast(ToolsUtils.returnXMLStr("creat_qrcode_failure") +","+result.getErrMessage()+"!");

            } else if (EPayResult.PAY_STATUS_COMPLETE
                    .equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
                showToast("支付成功！");
//                paymentNo = result.trade_no;
                paymentNo_ali = orderId;
                tradeLogic();
            } else if (result != null && result.success) {// 微信支付成功
                showToast("支付成功！");
                paymentNo = result.transaction_id;
                tradeLogic();

            } else {
                showToast(ToolsUtils.returnXMLStr("pay_failure"));
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.disLoading();
            if (!TextUtils.isEmpty(values[0])) {
                System.out.println("二维码是:" + values[0]);
                Bitmap bitmap = null;
                Bitmap qrcode = CreateImage.creatQRImage(values[0], bitmap,
                        500, 500);
//                paydialog = DialogUtil.scanDialog(context,qrcode, payTypeID, task, values[0]);
//
//                paydialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        if (secondaryCheckout != null) {
//                            secondaryCheckout.setHiddenCode();
//                        }
//                    }
//                });

                if (secondaryCheckout != null) {
                    secondaryCheckout.setScanCode(values[0], payTypeID);
                }
            }
        }
    }

    /**
     * 反扫
     *
     * @author aqw
     */
    class OutTask extends MicropayTask {
        @Override
        protected void onPostExecute(EPayResult result) {
            progressDialog.disLoading();
            if (result != null &&!result.success) {
                showToast(ToolsUtils.returnXMLStr("creat_qrcode_failure") +","+result.getErrMessage()+"!");

            } else if (EPayResult.PAY_STATUS_COMPLETE
                    .equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
                showToast("支付成功！");
//                paymentNo = result.trade_no;
                paymentNo_ali = orderId;

                tradeLogic();
            } else if (result != null && result.success) {// 微信支付成功

                showToast("支付成功！");
                paymentNo = result.transaction_id;
                tradeLogic();
            } else {
                showToast(ToolsUtils.returnXMLStr("pay_failure"));
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        return false;
    }

    /**
     * 扫码枪回调
     *
     * @param barcode
     */
    @Override
    public void onScanSuccess(String barcode) {
        Log.e("返回:", barcode);
        if (!TextUtils.isEmpty(barcode)) {
            //            showToast("扫码结果:"+barcode);
            outPay(barcode);
        }
    }

    /**
     * 判断是先支付还是后支付:先支付走下单方法，后支付走结账方法
     */
    private void tradeLogic() {
        switch (source) {
            case 0://先支付
                creatOrdAndCheckOut();
                break;
            case 1://后支付
                checkOut();
                break;
            case 2://桌台订单过来的支付
//                Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
                checkOut();
                break;
        }
    }

    /**
     * 先支付后下单：下单接口同时处理结账
     */
    private void creatOrdAndCheckOut() {
        Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
        final Order order = cart.getOrderItem(tableOrder,cart.getDishItemList());
        order.setPaymentStatus(PaymentStatus.PAYED);
        order.setPaymentList(getPaymentList());
        if (order != null) {
            orderService.createOrder(order, new ResultCallback<Order>() {
                @Override
                public void onResult(Order result) {
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));
                    progressDialog.disLoading();
                    if (paydialog != null) {
                        paydialog.dismiss();
                    }
                    kdsCreatOrder(order);
                        showToast("下单成功!");
                    finish();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast(ToolsUtils.returnXMLStr("orders_failed")+"!" + e.getMessage());
                }
            });
        }
    }

    /**
     * 结账
     */
    private void checkOut() {
        tradeService.checkOut(getPaymentList(), orderId, new ResultCallback<PosResponse>() {
            @Override
            public void onResult(PosResponse result) {
                progressDialog.disLoading();
                if (result.isSuccessful()) {//结账成功
                    TimerTaskController.getInstance().setStopSyncNetOrder(false);//停止轮训网上订单
                    Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER,tableOrder));
                    showToast(ToolsUtils.returnXMLStr("check_out_success"));
                    if (paydialog != null) {
                        paydialog.dismiss();
                    }
                    finish();
                } else {//结账失败
                    showToast(result.getErrmsg());
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
            }
        });
    }

    /**
     * KDS创建订单
     */
    private void kdsCreatOrder(Order result) {
        String kdsServer = Store.getInstance(this).getKdsServer();
        String kdsPort = Store.getInstance(this).getKdsPort();
        if (!TextUtils.isEmpty(kdsServer) && !TextUtils.isEmpty(kdsPort)) {
            try {


                OrderService kdsOrderService = OrderService.getKdsInstance(this);

                KdsOrder kdsOrder = new KdsOrder();
                KdsOrderData kdsOrderData = new KdsOrderData();
                List<KdsDishItem> kdsDishItemList = new ArrayList<>();

                kdsOrderData.oid = result.getId() + "";
                kdsOrderData.createTime = result.getCreatedAt();
                int type = 0;
                if ("EAT_IN".equals(result.getOrderType())) {//堂食
                    type = 0;
                } else if ("TAKE_OUT".equals(result.getOrderType())) {//打包外带
                    type = 1;
                } else {//外卖
                    type = 2;
                }
                kdsOrderData.type = type;
                kdsOrderData.total = result.getTotal();
                int paystatus = 0;
                if (result.getPaymentStatus() == PaymentStatus.NOT_PAYED) {//未付款
                    paystatus = 0;
                } else if (result.getPaymentStatus() == PaymentStatus.PAYED) {//已付款
                    paystatus = 1;
                }
                kdsOrderData.paystatus = paystatus;

                //菜品项
                for (OrderItem orderItem : result.getItemList()) {
                    KdsDishItem kdsItem = new KdsDishItem();
                    kdsItem.did = orderItem.getDishId() + "";
                    kdsItem.name = orderItem.getDishName();
                    kdsItem.count = orderItem.getQuantity();
                    kdsItem.dishKind = orderItem.getDishKindStr();
                    if (orderItem.getCookList() != null && orderItem.getCookList().size() > 0) {
                        String cooks = "";
                        for (CookingMethod cookingMethod : orderItem.getCookList()) {
                            cooks += cookingMethod.cookName + ",";
                        }
                        kdsItem.cook = cooks;
                    }
                    kdsItem.price = orderItem.getPrice().toString();
                    kdsDishItemList.add(kdsItem);
                }

                kdsOrderData.dishitems = kdsDishItemList;
                //kdsOrder.orderdata = kdsOrderData;


                kdsOrderService.kdsCreatOrder(new Gson().toJson(kdsOrderData), new ResultCallback<KDSResponse>() {
                    @Override
                    public void onResult(KDSResponse result) {
                        progressDialog.disLoading();
                        if (result.isSuccess()) {
                            showToast("KDS下单成功");

                        } else {
                            showToast(result.getMsg());
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        progressDialog.disLoading();
                        showToast(e.getMessage());
                    }
                });

            } catch (PosServiceException e) {
                e.printStackTrace();
            }
        }

    }

    //生成结账的Body参数
    private List<PaymentList> getPaymentList() {
        PosInfo posInfo = PosInfo.getInstance();
        List<PaymentList> paymentLists = new ArrayList<PaymentList>();
//        PaymentList checkModle = new PaymentList();
//        checkModle.setAppId(posInfo.getAppId());
//        checkModle.setBrandId(posInfo.getBrandId());
//        checkModle.setStoreId(posInfo.getStoreId());
//        checkModle.setOrderId(orderId);
//        checkModle.setPaymentTypeId(payTypeID);
//        checkModle.setPaymentNo(paymentNo);
//        BigDecimal b1 = new BigDecimal(total_money+"");
//        checkModle.setValue(b1);
//        checkModle.setOperation("PAY");
//        paymentLists.add(checkModle);

        for (PayType payType : payTypeList) {
            PaymentList checkModle = new PaymentList();
            checkModle.setAppId(posInfo.getAppId());
            checkModle.setBrandId(posInfo.getBrandId());
            checkModle.setStoreId(posInfo.getStoreId());
            checkModle.setOrderId(orderId);
            checkModle.setPaymentTypeId(payTypeID);
            checkModle.setPaymentNo(orderId);
            BigDecimal b1 = new BigDecimal(payType.getMoney()+"");
            checkModle.setValue(b1);
            checkModle.setOperation("PAY");
            paymentLists.add(checkModle);
        }

        return paymentLists;
    }

    private void formatPayType() {
        boolean isExit = false;
        for (PayType payType : payTypeList) {
            if (payType.getId() == payTypeID) {
                if (TextUtils.isEmpty(print_str)) {
                    payTypeList.remove(payType);
                } else {
                    isExit = true;
                    payType.setMoney(print_money);
                    payType.setPrint(print_str);
                }
                break;
            }
        }
        if (!isExit && !TextUtils.isEmpty(print_str)) {
            PayType mType = new PayType();
            mType.setId(payTypeID);
            mType.setMoney(print_money);
            mType.setName(selectPayTypeAdp.getItem(cur_position).getName());
            mType.setPrint(print_str);
            payTypeList.add(mType);
        }

        double payedMoney = 0;
        for (PayType payType : payTypeList) {
            payedMoney += payType.getMoney();
        }
        nopay_money = total_money - payedMoney;
        if (nopay_money > 0) {
            unpaidTv.setText("未付:");
            unpaidMoney.setText("￥" + FormatUtils.getDoubleW(nopay_money));
            setPayBtnEnable(false);
        } else {
            unpaidTv.setText("找零:");
            unpaidMoney.setText("￥" + FormatUtils.getDoubleW(Math.abs(nopay_money)));
            setPayBtnEnable(true);
        }
    }

    private void formatPrint() {
        if (print_str.equals(".") || TextUtils.isEmpty(print_str)) {
            print_money = 0;
        } else if (print_str.length() > 0 && ".".equals(print_str.charAt(0))) {
            print_money = Double.parseDouble("0" + print_str);
        } else if (print_str.length() > 0 && ".".equals(print_str.charAt(print_str.length() - 1))) {
            print_money = Double.parseDouble(print_str.substring(0, print_str.length() - 1));
        } else {
            print_money = Double.parseDouble(print_str);
        }
        formatPayType();

        payTypeMoneyAdp.setData(payTypeList);

        //设置客显屏输入金额
//        if (secondaryCheckout != null) {
//            secondaryCheckout.setCostMoney(print_money);
//        }

    }

    //设置结账按钮是否可以点击
    private void setPayBtnEnable(boolean enable) {
        keyPay.setSelected(!enable);
        keyPay.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        if (task != null)
            task.cancel(true);
        if (outTask != null)
            outTask.cancel(true);
        if (secondaryCheckout != null && secondaryCheckout.isShowing()) {
            secondaryCheckout.dismiss();
        }
        super.onDestroy();
    }
}
