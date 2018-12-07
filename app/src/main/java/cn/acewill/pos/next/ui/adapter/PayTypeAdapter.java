package cn.acewill.pos.next.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acewill.paylibrary.EPayTask;
import com.acewill.paylibrary.MicropayTask;
import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.epay.EPayResult;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.PrinterDataController;
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
import cn.acewill.pos.next.model.PaymentCategory;
import cn.acewill.pos.next.model.WftRespOnse;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.model.wsh.WshDealPreview;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.retrofit.response.LKLResponse;
import cn.acewill.pos.next.service.retrofit.response.ValidationResponse;
import cn.acewill.pos.next.service.retrofit.response.WeiFuTongResponse;
import cn.acewill.pos.next.service.retrofit.response.pay.BaseWechatPayResult;
import cn.acewill.pos.next.service.retrofit.response.pay.GsonUtils;
import cn.acewill.pos.next.service.retrofit.response.pay.WechatPayResult;
import cn.acewill.pos.next.service.retrofit.response.pay.WechatPayResult2;
import cn.acewill.pos.next.ui.activity.CheckOutNewAty;
import cn.acewill.pos.next.utils.CheckOutUtil;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.CreateImage;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.FileLog;
import cn.acewill.pos.next.utils.PayDialogUtil;
import cn.acewill.pos.next.utils.ScanGunKeyEventHelper;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.ComTextView;
import cn.acewill.pos.next.widget.ProgressDialogF;

import static cn.acewill.pos.next.model.dish.Cart.getDishItemList;

/**
 * 支付方式列表
 * Created by aqw on 2016/11/25.
 */
public class PayTypeAdapter extends BaseAdapter implements ScanGunKeyEventHelper.OnScanSuccessListener {

    private ProgressDialogF progressDialog;

    private static final int FAIL_PAY = 0;//支付失败
    private static final int FAIL_ORDER = 1;//下单失败
    private static final int FAIL_LKL_PAY = 2;//拉卡拉失败
    private static final int FAIL_WFT_PAY = 3;//威富通失败
    private CheckOutNewAty aty;
    private InTask task;
    private OutTask outTask;
    private BigDecimal printMoney;
    private boolean isDebug = false;
    private int payTypeID;
    private Payment pt;
    private int pay_channel = PayReqModel.PTID_SSS_WEIXIN;// 支付宝：PayReqModel.PTID_SSS_ALI，微信:PTID_SSS_WEIXIN
    private Dialog paydialog;
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;
    private boolean scaning = false;//防止扫码枪扫多次
    private String code_scan;//反扫返回的二维码号，用于重试使用
    private Dialog failDialog;
    private Cart cart;
    private Order order;//用于副屏显示数据

    private final Timer timer = new Timer();//轮询拉卡拉支付交易情况
    private TimerTask lklTask;
    private Handler handler;

    private Timer timerwft = new Timer();//轮询威富通支付交易情况
    private TimerTask wftTask;
    private Handler wfthandler;
    private CardRecord cardRecord = null;
    private int totalFee;
    private String orderTradeNo;
    private String autoCode;
    private String paymentNo;//流水号
    private String transactionNo = ""; //微信，支付宝，刷卡等方式的支付流水号，特别长的一段字符串

    private int delayedTime = 2 * 1000;//延迟2秒
    private int cycleTime = 3 * 1000;//周期循环时间

    private PosInfo posInfo;

    boolean isDisCount = false;//支付时检测菜品沽清状态 如果成功 其它的支付方式就不去检测沽清状态正常支付下单

    private int wftPayType = 3;//选择威富通 是微信支付还是支付宝支付  0是微信 1是支付宝
    private Store store;
    private boolean isClickPay = true;

    private OrderService mOrderService;

    public boolean isClickPay() {
        return isClickPay;
    }

    public void setClickPay(boolean clickPay) {
        isClickPay = clickPay;
    }

    public PayTypeAdapter(Context context) {
        super(context);
        cart = Cart.getInstance();
        progressDialog = new ProgressDialogF(context);
        aty = (CheckOutNewAty) context;
        store = Store.getInstance(context);
        mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
        isDisCount = false;
        posInfo = PosInfo.getInstance();
        try {
            mOrderService = OrderService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Payment payment = (Payment) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_paytype, null);
            holder.type_name = (ComTextView) convertView.findViewById(R.id.type_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.type_name.setText(payment.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClickPay())
                {
                    payTypeID = payment.getId();
                    pt = payment;
                    payTypeLogic(payment);
                }
            }
        });

        return convertView;
    }


    /**
     * 当支付dialog被关闭后与之相匹配的倒计时查询服务也应当被关闭
     */
    private void payDialogDismissLis()
    {
        if(paydialog != null)
        {
            paydialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    wftTimerCancel();
                    if(failDialog != null && failDialog.isShowing())
                    {
                        failDialog.dismiss();
                    }
                    isShowFailDialog = false;
                    if (task != null)
                        task.cancel(true);
                    if (outTask != null)
                        outTask.cancel(true);
                }
            });
        }
    }

    private Long orderNewId = 0L;

    public void getOrderId(final String barcode, final boolean isAgain) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.getNextOrderId(new ResultCallback<Long>() {
                @Override
                public void onResult(Long result) {
                    if (result > 0) {
                        orderNewId = result;
                        outPay(barcode, result, isAgain);
//                        outPay2(barcode, result, isAgain);
                    }
                    else{
                        setClickPay(true);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    setClickPay(true);
                    Log.i("获取订单Id失败", e.getMessage());
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
                    //                    aty.showToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    class ViewHolder {
        ComTextView type_name;
    }

    public void refreshDish(List<DishCount> result, List<Dish> dishs) {
        //刷新菜品数据,显示沽清
        String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough") + "\n\n" + names + "。\n\n" + ToolsUtils.returnXMLStr("please_re_order"));
        if (progressDialog != null) {
            progressDialog.disLoading();
        }
        Log.i("以下菜品份数不足:", names + "====<<");
    }


    //选择支付
    private void payTypeLogic(final Payment payment) {
        scaning = false;
        if (aty.nopay_money.compareTo(BigDecimal.ZERO) == 0 || aty.nopay_money.compareTo(BigDecimal.ZERO) == -1) {
            aty.showToast(ToolsUtils.returnXMLStr("is_pay_please_check_checkout"));
            return;
        }
        if (!isDisCount) {
            final CheckOutUtil checUtil = new CheckOutUtil(aty, payment);
            checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
                @Override
                public void haveStock() {
                    isDisCount = true;
                    payOrder(payment);
                }

                @Override
                public void noStock(List dataList) {
                    refreshDish(dataList, getDishItemList());
                }
            });
        }
        else if (isDisCount) {
            payOrder(payment);
        }

    }

    private void payOrder(final Payment payment) {
        //现金类型
        if (payment.getCategory() == PaymentCategory.CASH) {
            //7寸屏
            if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
                SunmiSecondScreen.getInstance(context).sendData4DSD(payment.getName() + ToolsUtils.returnXMLStr("pays"), aty.nopay_money.setScale(2, BigDecimal.ROUND_DOWN).toString());
            } else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                SunmiSecondScreen.getInstance(context).showDishImgExcel(order);
            }

            PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                @Override
                public void onOk(Object o) {
                    printMoney = (BigDecimal) o;
                    if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                        if (payment.getId() == 0) {
                            PrinterDataController.getInstance().openCashBox();
                        }
                        aty.changeMoney(printMoney, printMoney.min(aty.nopay_money), payment, "", transactionNo, null, null);
                    } else {
                        aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                    }
                }
            }, aty.nopay_money, true);
            return;
        }
        switch (payment.getId()) {
            case 1://支付宝
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        printMoney = (BigDecimal) o;
                        if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                            pay_channel = PayReqModel.PTID_SSS_ALI;
                            scanLogic(payment.getId());
//                            scanLogic2(payment.getId());
                        } else {
                            aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                        }
                    }
                }, aty.nopay_money, true);
                break;
            case 2://微信
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        printMoney = (BigDecimal) o;
                        if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                            pay_channel = PayReqModel.PTID_SSS_WEIXIN;
                            scanLogic(payment.getId());
//                            scanLogic2(payment.getId());
                        } else {
                            aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                        }
                    }
                }, aty.nopay_money, true);
                break;
            case -8://威富通
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        printMoney = (BigDecimal) o;
                        if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                            pay_channel = PayReqModel.PTID_SSS_WEIFUTONG;
                            DialogUtil.switchWftPay(context, new KeyCallBack() {
                                @Override
                                public void onOk(Object o) {
                                    wftPayType = (int)o;
                                    aty.setWftPayType(wftPayType);
                                    scanLogic(payment.getId());
//                                    scanLogic2(payment.getId());
                                }
                            });
                        } else {
                            aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                        }
                    }
                }, aty.nopay_money, true);
                break;
            //挂账
            case -34:
                logicChargeOffs(payment);
                break;
            case 3:
            case 4:
            case 5://储值、积分、优惠券
                aty.removeMemberPay();//每一次重新选择会员支付方式,都要先将原先的会员支付给清空,然后重新操作
                DialogUtil.memberDialog(context, payTypeID, aty.nopay_money, aty.orderItems, false, new CreatDealBack() {
                    @Override
                    public void onDeal(String bizid, WshDealPreview result, BigDecimal money, boolean isCheckOut, Account acount, List<Payment> memberPayMent) {//money为消费会员金额,bizid是业务流水号，提交交易时使用
                        aty.changeMoney(money, money, payment, bizid, transactionNo, memberPayMent, null);
                        posInfo.setMemberCheckOut(false);
                        aty.setBiz_id(bizid);
                        aty.setWshDealPreview(result);
                        posInfo.setBizId(bizid);
                        posInfo.setWshDealPreview(result);
                        if (acount != null) {
                            if (isCheckOut) {
                                acount.setMemberConsumeCost(money);
                            }
                            aty.setAccountMember(acount);
                        }
                        if (!isCheckOut) {
                            posInfo.setMemberCheckOut(true);
                        }
                    }
                });
                break;
            case -32://美团
                DialogUtil.meiTuanDialog(context, posInfo.getOrderId(), aty.nopay_money, false, aty.addValidation, new DialogMTCallback() {
                    @Override
                    public void onCheckout(BigDecimal money, boolean isCheckOut, CopyOnWriteArrayList<ValidationResponse> addValidationLists) {
                        printMoney = money;
                        //                        aty.setAddValidation(addValidationLists);
                        aty.changeMoney(printMoney, printMoney, payment, "", transactionNo, null, addValidationLists);
                        //                        addValidationList = ToolsUtils.cloneTo(addValidationLists);
                        //                        creatOrdAndCheckOut();
                    }
                });
                break;
            case 7://银行卡
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        printMoney = (BigDecimal) o;
                        if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                            aty.changeMoney(printMoney, printMoney, payment, "", transactionNo, null, null);
                        } else {
                            aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                        }
                    }
                }, aty.nopay_money, true);
                break;
            case -9://拉卡拉
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        printMoney = (BigDecimal) o;
                        if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                            creatPaymaxCharge();
                        } else {
                            aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                        }
                    }
                }, aty.nopay_money, true);
                break;
            case -10://储值卡
                PayDialogUtil.keyNumDialog(context, payment, new KeyCallBack() {
                    @Override
                    public void onOk(Object o) {
                        printMoney = (BigDecimal) o;
                        if (printMoney.compareTo(BigDecimal.ZERO) == 1) {
                            DialogUtil.showMemberDialog(context, aty.nopay_money, new DialogCall() {
                                @Override
                                public void onOk(Object obj) {
                                    if (((String) obj).equals("Success")) {
                                        aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                                        transactionNo = ToolsUtils.completionOrderId(aty.orderId);
                                        aty.changeMoney(printMoney, printMoney, payment, "", transactionNo, null, null);
                                    }
                                }

                                @Override
                                public void onCancle(Object obj) {
                                }
                            });
                        } else {
                            aty.showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
                        }
                    }
                }, aty.nopay_money, true);
                break;
            default:
                aty.showToast(ToolsUtils.returnXMLStr("this_method_is_not_supported"));
                break;
        }
    }
    //扫码判断
    private void scanLogic2(int payTypeID) {
        setClickPay(false);
        // 正扫
        if (Store.getInstance(context).isFront()) {

            progressDialog.showLoading("");
            String storeName = Store.getInstance(context).getStoreName();
            PayReqModel model = new PayReqModel();
            task = new InTask();
            model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            model.orderNo = aty.orderId;
            model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("product_details") : storeName;
            model.isDebug = isDebug;
            model.payType = pay_channel;
            model.authCode = "";
            model.aliGoodsItem = aty.aliGoodsItem;
            model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("food_consumption") : storeName;
            model.storeId = Store.getInstance(context).getStoreId();
            model.terminalId = Store.getInstance(context).getDeviceName();

            mOrderService.wechatSaoMa(model, new ResultCallback<BaseWechatPayResult>() {
                @Override
                public void onResult(BaseWechatPayResult result) {
                    if (result.getResult() == 0) {
                        try {
                            WechatPayResult2 bean = GsonUtils
                                    .getSingleBean(result
                                            .getContent(), WechatPayResult2.class);
                            FileLog.log("微信扫码支付结果>"+new Gson().toJson(bean));
                            if ("SUCCESS".equals(bean.getResult_code())) {
                                String qr_code = bean.getCode_url();
//                                mView.returnWechatSaoMaResult(new SelfPosPayResult(PayResultType.PAY_INPROGRESS, PayResultMsg.PAY_INPROGRESS, qr_code));
                            } else {
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
    //扫码判断
    private void scanLogic(int payTypeID) {
        setClickPay(false);
        // 正扫
        if (Store.getInstance(context).isFront()) {

            progressDialog.showLoading("");
            String storeName = Store.getInstance(context).getStoreName();
            PayReqModel model = new PayReqModel();
            task = new InTask();
            model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            model.orderNo = aty.orderId;
            model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("product_details") : storeName;
            model.isDebug = isDebug;
            model.payType = pay_channel;
            model.authCode = "";
            model.aliGoodsItem = aty.aliGoodsItem;
            model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("food_consumption") : storeName;
            model.storeId = Store.getInstance(context).getStoreId();
            model.terminalId = Store.getInstance(context).getDeviceName();
            if(wftPayType == 0 || wftPayType == 1)
            {
                model.pay_type = wftPayType;
                createWtfZsQrCode();
            }
            else{
                task.execute(model);
            }
        } else {
            //调用扫码枪
            scanGunDialog();
        }
    }

    /**
     * 挂账
     */
    private void logicChargeOffs(final Payment payment)
    {
        //判断是否有退菜权限
        PowerController.isLogicPower(context, PowerController.REFUND_DISH, new PermissionCallback() {
            @Override
            public void havePermission() {
                DialogUtil.cardRecordDialog(context, new DialogTCallback() {
                    @Override
                    public void onConfirm(Object o) {
                        cardRecord = (CardRecord)o;
                        if(cardRecord != null)
                        {
                            printMoney = aty.total_money;
                            aty.setCardRecord(cardRecord);
                            aty.changeMoney(printMoney, printMoney.min(aty.nopay_money), payment, "", transactionNo, null, null);
//                            chargeOffs(payment,cardRecord);
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
//    private void chargeOffs(final Payment payment,final CardRecord cardRecord)
//    {
//        try {
//            OrderService orderService = OrderService.getInstance();
//            orderService.chargeOffs(Long.valueOf(aty.orderId), true, new ResultCallback<Integer>() {
//                @Override
//                public void onResult(Integer result) {
//                    if(result == 0)
//                    {
//                        printMoney = aty.total_money;
//                        aty.setCardRecord(cardRecord);
//                        aty.changeMoney(printMoney, printMoney.min(aty.nopay_money), payment, "", transactionNo, null, null);
//                    }
//                    else{
//                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("cardrecord_error") +"!");
//                        Log.e("挂单失败", "");
//                    }
//                }
//
//                @Override
//                public void onError(PosServiceException e) {
//                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("cardrecord_error") + e.getMessage()+ "!");
//                    Log.e("挂单失败", e.getMessage());
//                }
//            });
//        } catch (PosServiceException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 正扫生成威富通二维码
     */
    private void createWtfZsQrCode()
    {
        setClickPay(false);
        try {
            progressDialog.showLoading("");
            OrderService orderService = OrderService.getInstance();
            totalFee = printMoney.multiply(new BigDecimal("100")).intValue();


            String orderi = "";
//            String body = "";
            if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
                orderi = orderNewId + "";
//                body = "1231*321";
            } else {
                orderi = aty.orderId;
//                body = ToolsUtils.handleCarDish(cart.getDishItemList());
            }
            orderTradeNo = ToolsUtils.completionOrderId(orderi) + "_" + TimeUtil.getTimeToken();

            orderService.createCodeUrl(wftPayType,totalFee, orderTradeNo,new ResultCallback<WftRespOnse>() {
                @Override
                public void onResult(WftRespOnse result) {
                    progressDialog.disLoading();
                    if(result != null && !TextUtils.isEmpty(result.getCodeImgUrl()))
                    {
                        createWFTQrCode(result.getCodeImgUrl());
                    }
                    else{
                        setClickPay(true);
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("creat_qrcode_failure")  + "!");
                        Log.e("正扫", "生成WFT二维码失败");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    setClickPay(true);
                    progressDialog.disLoading();
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("creat_qrcode_failure") + "," + e.getMessage() + "!");
                    Log.e("正扫", "生成WFT二维码失败");
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            setClickPay(true);
        }
    }

    private void createWFTQrCode(String qrCode)
    {
        if(!TextUtils.isEmpty(qrCode))
        {
            Bitmap qrCodeBig = CreateImage.convertStringToIcon(qrCode);
            //7寸屏
            if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
                SunmiSecondScreen.saveWxOrAliQcode(qrCodeBig, ToolsUtils.returnXMLStr("wft_pay"), "￥" + printMoney.setScale(2, BigDecimal.ROUND_DOWN).toString());
            } else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                SunmiSecondScreen.showImgExcel(qrCodeBig, order);
            }
            timerwft = new Timer();
            cycleWFTPay(orderTradeNo, orderTradeNo);

            if(store.isPadRunnIng())
            {
                paydialog = DialogUtil.scanPadDialog(context, payTypeID, task, qrCode,qrCodeBig, new InterfaceDialog() {
                    @Override
                    public void onCancle() {
                        setClickPay(true);
                        Log.i("正扫手动下单：", "");
                        if (task != null)
                            task.cancel(true);
                        if (outTask != null)
                            outTask.cancel(true);
                    }

                    @Override
                    public void onOk(Object o) {
                        Log.i("正扫重试：", "");
                        setClickPay(true);
                        //                            scanLogic();
                    }
                });
                payDialogDismissLis();
            }
            else{
                paydialog = DialogUtil.scanDialog(context, payTypeID, task, new DialogCallBack() {

                    @Override
                    public void onOk() {//等待超时重试
                        setClickPay(true);
                        Log.i("正扫超时：", "等待超时重试");
                        //                      scanLogic();
                    }

                    @Override
                    public void onCancle() {//等待超时并且客人已支付成功，手动下单
                        setClickPay(true);
                        Log.i("正扫超时：", "等待超时手动下单");
                        //                      creatOrdAndCheckOut();
                    }
                });
                payDialogDismissLis();
            }

        }
    }

    // 反扫支付(扫码枪扫完后调用这个方法)
    private void outPay2(String code, Long orderNewId, boolean isQuery) {
        progressDialog.showLoading(ToolsUtils.returnXMLStr("being_paid"));
        String storeName = Store.getInstance(context).getStoreName();
        PayReqModel model = new PayReqModel();
        model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
            model.orderNo = orderNewId + "";
        } else {
            model.orderNo = aty.orderId;
        }
        model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("product_details") : storeName;
        model.isDebug = isDebug;
        model.payType = pay_channel;
        model.authCode = code;
        model.aliGoodsItem = aty.aliGoodsItem;
        model.isQuery = isQuery;
        model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("food_consumption") : storeName;
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

    // 反扫支付(扫码枪扫完后调用这个方法)
    private void outPay(String code, Long orderNewId, boolean isQuery) {
        progressDialog.showLoading(ToolsUtils.returnXMLStr("being_paid"));
        String storeName = Store.getInstance(context).getStoreName();
        PayReqModel model = new PayReqModel();
        outTask = new OutTask();
        model.totalAmount = printMoney.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
            model.orderNo = orderNewId + "";
        } else {
            model.orderNo = aty.orderId;
        }
        model.wxGoodsDetail = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("product_details") : storeName;
        model.isDebug = isDebug;
        model.payType = pay_channel;
        model.authCode = code;
        model.aliGoodsItem = aty.aliGoodsItem;
        model.isQuery = isQuery;
        model.storeName = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("food_consumption") : storeName;
        model.storeId = Store.getInstance(context).getStoreId();
        model.terminalId = Store.getInstance(context).getDeviceName();

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
            if (result != null && !result.success) {
                setClickPay(true);
                EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
                hideDialog();
                aty.showToast(ToolsUtils.returnXMLStr("creat_qrcode_failure") + "," + result.getErrMessage() + "!");
                Log.e("正扫", "生成二维码失败");
            } else if (EPayResult.PAY_STATUS_COMPLETE
                    .equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
                aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                //                paymentNo = result.trade_no;
                paymentNo = aty.orderId;
                transactionNo = result.trade_no;
                aty.changeMoney(printMoney, printMoney, pt, paymentNo, transactionNo, null, null);
                hideDialog();
            } else if (result != null && result.success) {// 微信支付成功
                aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                paymentNo = result.transaction_id;
                transactionNo = result.transaction_id;
                aty.changeMoney(printMoney, printMoney, pt, result.transaction_id, transactionNo, null, null);
                hideDialog();
            } else {
                setClickPay(true);
                if (pay_channel != PayReqModel.PTID_SSS_WEIXIN) {
                    aty.showToast(ToolsUtils.returnXMLStr("pay_failure"));
                    Log.e("正扫", "支付失败");
                    exceptMethod(FAIL_PAY);
                }
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.disLoading();
            if (!TextUtils.isEmpty(values[0])) {
                System.out.println("二维码是:" + values[0]);

                if(store.isPadRunnIng())
                {
                    paydialog = DialogUtil.scanPadDialog(context, payTypeID, task, values[0],null, new InterfaceDialog() {
                        @Override
                        public void onCancle() {
                            setClickPay(true);
                            Log.i("正扫手动下单：", "");
                            if (task != null)
                                task.cancel(true);
                            if (outTask != null)
                                outTask.cancel(true);
                        }

                        @Override
                        public void onOk(Object o) {
                            setClickPay(true);
                            Log.i("正扫重试：", "");
                            //                            scanLogic();
                        }
                    });
                    payDialogDismissLis();
                }
                else{
                    Bitmap bitmap = null;
                    Bitmap qrcode = CreateImage.creatQRImage(values[0], bitmap, 100, 100);
                    //7寸屏
                    if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
                        SunmiSecondScreen.saveWxOrAliQcode(qrcode, payTypeID == 1 ? ToolsUtils.returnXMLStr("alipay") : ToolsUtils.returnXMLStr("wechat"), "￥" + printMoney.setScale(2, BigDecimal.ROUND_DOWN).toString());
                    } else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                        SunmiSecondScreen.showImgExcel(qrcode, order);
                    }

                    paydialog = DialogUtil.scanDialog(context, payTypeID, task, new DialogCallBack() {

                        @Override
                        public void onOk() {//等待超时重试
                            setClickPay(true);
                            Log.i("正扫", "等待超时重试");
                            //                        scanLogic(0);
                        }

                        @Override
                        public void onCancle() {//等待超时并且客人已支付成功，手动点击已支付
                            setClickPay(true);
                            Log.i("正扫", "等待超时并且客人已支付成功");
                            //                        aty.changeMoney(printMoney, printMoney, pt, paymentNo,null);
                        }
                    });
                    payDialogDismissLis();
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
            if (result != null && !result.success && result.WFT) {
                setClickPay(true);
                EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
                hideDialog();
                //                aty.showToast(ToolsUtils.returnXMLStr("pay_failure")+"," + result.getErrMessage() + "!");
                scaning = false;
                EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_GET_ONLINE_PAY_STATE_FAILURE));
                Log.e("反扫", "支付失败");
            } else if (EPayResult.PAY_STATUS_COMPLETE
                    .equalsIgnoreCase(result.trade_status)) {// 支付宝支付成功
                aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                //                paymentNo = result.trade_no;
                paymentNo = aty.orderId;
                transactionNo = result.trade_no;
                aty.changeMoney(printMoney, printMoney, pt, aty.orderId, transactionNo, null, null);
                hideDialog();
            } else if (result != null && result.success && !result.WFT) {// 微信支付成功
                aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                paymentNo = result.transaction_id;
                transactionNo = result.transaction_id;
                aty.changeMoney(printMoney, printMoney, pt, result.transaction_id, transactionNo, null, null);
                hideDialog();
            } else if (result != null && result.weifutongPayStart)// 威富通成功
            {
                aty.showToast(ToolsUtils.returnXMLStr("auth_code_success"));
                hideDialog();
                totalFee = printMoney.multiply(new BigDecimal("100")).intValue();

                String orderi = "";
                String body = "";
                if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
                    orderi = orderNewId + "";
                    body = "1231*321";
                } else {
                    orderi = aty.orderId;
                    body = ToolsUtils.handleCarDish(cart.getDishItemList());
                }
                orderTradeNo = ToolsUtils.completionOrderId(orderi) + "_" + TimeUtil.getTimeToken();
                autoCode = result.code;
                paymentNo = orderTradeNo;
                transactionNo = orderTradeNo;
                getWayWeiFuTong(autoCode, body, totalFee, orderTradeNo);
            } else {
                setClickPay(true);
                if (pay_channel != PayReqModel.PTID_SSS_WEIXIN) {
                    aty.showToast(ToolsUtils.returnXMLStr("pay_failure"));
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

        paydialog = DialogUtil.scanGunDialog(context, payTypeID, outTask, mScanGunKeyEventHelper, new DialogCallBack() {

            @Override
            public void onOk() {//等待超时重试
                setClickPay(true);
                Log.i("反扫超时：", "等待超时重试");
                //                if (!TextUtils.isEmpty(code_scan)) {
                //                    scanRetry();
                //                }
            }

            @Override
            public void onCancle() {//等待超时并且客人已支付成功，手动确认
                setClickPay(true);
                Log.i("反扫超时：", "等待超时手动确认");
                //                aty.changeMoney(printMoney, printMoney, pt, paymentNo,null);
            }
        });
        payDialogDismissLis();

        //此处延时是因为在关闭金额输入框时调用了清空副屏
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //展示总金额到副屏
                if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
                    SunmiSecondScreen.getInstance(context).sendData4DSD(payTypeID == 1 ? ToolsUtils.returnXMLStr("alipay") : ToolsUtils.returnXMLStr("wechat"), printMoney.setScale(2, BigDecimal.ROUND_DOWN).toString());
                } else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                    SunmiSecondScreen.getInstance(context).showDishImgExcel(order);
                }

            }
        }, 500);
    }

    private StringBuffer sb = new StringBuffer();
    @Override
    public void onScanSuccess(String barcode) {
        Log.e("反扫code返回:", barcode);
        sb.append(barcode);
        String code = sb.toString().trim();
        if (code.length() == 18) {
            if (!scaning) {
                System.out.println("===>>" + code);
                scaning = true;
                code_scan = code;
                if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
                    getOrderId(code_scan, false);
                } else {
                    outPay(code_scan, 0L, false);
//                    outPay2(code_scan, 0L, false);
                }
            }
        }
        //        if (!TextUtils.isEmpty(barcode)) {
        //            if (!scaning) {
        //                scaning = true;
        //                code_scan = barcode;
        //                if(ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag))
        //                {
        //                    getOrderId(barcode,false);
        //                }
        //                else{
        //                    outPay(barcode,0L ,false);
        //                }
        //            }
        //        }
    }

    /**
     * 反扫重试
     */
    private void scanRetry(boolean isAgain) {
        if (ToolsUtils.isReverseCheckOut(aty.reverseCheckOutFlag)) {
            getOrderId(code_scan, isAgain);
        } else {
            outPay(code_scan, 0L, isAgain);
//            outPay2(code_scan, 0L, isAgain);
        }
        scanGunDialog();
    }

    /**
     * 支付或下单失败弹框
     *
     * @param t
     */
    private boolean isShowFailDialog = false;
    private void exceptMethod(final int t) {
        setClickPay(true);
        hideDialog();

        if (failDialog == null || !failDialog.isShowing() && !isShowFailDialog) {
            failDialog = DialogUtil.createDialog(context, R.layout.dialog_payfail, 4, LinearLayout.LayoutParams.WRAP_CONTENT, true);
            isShowFailDialog = true;
        }

        TextView msg = (TextView) failDialog.findViewById(R.id.msg);
        TextView retry = (TextView) failDialog.findViewById(R.id.retry);
        TextView creat = (TextView) failDialog.findViewById(R.id.creat);

        retry.setText(ToolsUtils.returnXMLStr("common_cancel"));
        creat.setVisibility(View.GONE);

        failDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowFailDialog = false;
                if (task != null)
                    task.cancel(true);
                if (outTask != null)
                    outTask.cancel(true);
            }
        });

        switch (t) {
            case FAIL_PAY://支付失败
                //                creat.setVisibility(View.VISIBLE);
                msg.setText(ToolsUtils.returnXMLStr("pay_failure_or_timeout_please_try_again"));
                if (task != null)
                    task.cancel(true);
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
                        if (task != null)
                            task.cancel(true);
                        if (outTask != null)
                            outTask.cancel(true);
                        Log.i("支付失败", "点击重试");

                        scanRetry(true);
                        break;
                    case FAIL_LKL_PAY://拉卡拉支付失败
                        Log.i("拉卡拉支付失败", "点击重试");
                        creatPaymaxCharge();//交易失败后重新获取支付信息
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

        //        //出现查询支付状态超时时，手动点击确认成功
        //        creat.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                failDialog.dismiss();
        //                if (task != null)
        //                    task.cancel(true);
        //                if (outTask != null)
        //                    outTask.cancel(true);
        //                wftTimerCancel();
        //                aty.changeMoney(printMoney, printMoney, pt, paymentNo,null);
        //                Log.i("支付失败", "手动确认");
        //            }
        //        });

    }

    private void cancelFailDialog()
    {
        if(failDialog != null && failDialog.isShowing())
        {
            failDialog.dismiss();
        }
    }

    //隐藏dialog
    private void hideDialog() {
        if (paydialog != null && paydialog.isShowing()) {
            paydialog.dismiss();
        }
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * 提交拉卡拉支付请求
     */
    private void creatPaymaxCharge() {
        String storeName = Store.getInstance(context).getStoreName() + "-" + ToolsUtils.returnXMLStr("food_consumption");
        String subject = TextUtils.isEmpty(storeName) ? ToolsUtils.returnXMLStr("product_details") : storeName;
        try {
            progressDialog.showLoading("");
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.creatPaymaxCharge(printMoney.toString(), subject, ToolsUtils.handleCarDish(cart.getDishItemList()), aty.orderId, "wechat_csb", "192.168.1.66", "789456", new ResultCallback<LKLResponse>() {
                @Override
                public void onResult(LKLResponse result) {
                    progressDialog.disLoading();
                    if (result != null && !TextUtils.isEmpty(result.getId()) && result.getCredential() != null && result.getCredential().getWechat_csb() != null && !TextUtils.isEmpty(result.getCredential().getWechat_csb().getQr_code())) {
                        String wxQr_Code = result.getCredential().getWechat_csb().getQr_code();//微信扫码的二维码链接
                        String chargeid = result.getId();
                        createQrCode(chargeid, wxQr_Code);
                    } else {
                        aty.showToast("获取拉卡拉支付信息失败!");
                        Log.v("获取拉卡拉支付信息失败", ToolsUtils.getPrinterSth(result));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    aty.showToast("获取拉卡拉支付信息失败," + e.getMessage());
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
                SunmiSecondScreen.saveWxOrAliQcode(qrcode, "拉卡拉支付:", "￥" + aty.nopay_money.setScale(2, BigDecimal.ROUND_DOWN).toString());
            } else if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                SunmiSecondScreen.showImgExcel(qrcode, order);
            }
            cycleLklPay(chargeid);

            paydialog = DialogUtil.scanDialog(context, payTypeID, task, new DialogCallBack() {

                @Override
                public void onOk() {//等待超时重试
                    setClickPay(true);
                    Log.i("正扫超时：", "等待超时重试");
                    creatPaymaxCharge();//等待超时重试后重新扫码支付
                }

                @Override
                public void onCancle() {//等待超时并且客人已支付成功，手动下单
                    setClickPay(true);
                    Log.i("正扫", "等待超时并且客人已支付成功");
                    aty.changeMoney(printMoney, printMoney, pt, aty.orderId, transactionNo, null, null);
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
                            timerCancel();
                            aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                            aty.changeMoney(printMoney, printMoney, pt, aty.orderId, transactionNo, null, null);
                            hideDialog();
                        } else if (result.getStatus().equals("FAILED"))//交易失败
                        {
                            timerCancel();
                            hideDialog();
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

    private void getWayWeiFuTong(String auth_code, String body, Integer total_fee, final String out_trade_no) {
        try {
            progressDialog.showLoading(ToolsUtils.returnXMLStr("query_pay_state"));
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getWayWeiFuTong(auth_code, body, total_fee, out_trade_no, new ResultCallback<WeiFuTongResponse>() {
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
                    //                    aty.showToast(ToolsUtils.returnXMLStr("get_wft_pay_info_failure")+"," + e.getMessage());
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_GET_WFT_PAY_STATE));
                    Log.i("获取威富通支付信息失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            //            aty.showToast(ToolsUtils.returnXMLStr("get_wft_pay_info_failure"));
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_GET_WFT_PAY_STATE));
            Log.i("获取威富通支付信息失败", e.getMessage());
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

    private void queryWeiFuTong(final String out_trade_no, final String transaction_id) {
        try {
            if(!Store.getInstance(context).isFront())
            {
                progressDialog.showLoading("");
            }
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.queryWeiFuTong(out_trade_no, transaction_id, new ResultCallback<WeiFuTongResponse>() {
                @Override
                public void onResult(WeiFuTongResponse result) {
                    if (result != null) {
                        String query = result.getNeed_query();
                        String errcode = result.getErrcode();
                        if (result.isSuccess()) {
                            wftTimerCancel();
                            progressDialog.disLoading();
                            aty.showToast(ToolsUtils.returnXMLStr("pay_success"));
                            Log.i("创建威富通交易成功", out_trade_no);
                            cancelFailDialog();
                            aty.changeMoney(printMoney, printMoney, pt, out_trade_no, out_trade_no, null, null);
                        } else if (!result.isSuccess() && "false".equals(query)) {
                            wftTimerCancel();
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.REFRESH_ORDERID));
                            progressDialog.disLoading();
                            exceptMethod(FAIL_WFT_PAY);
                            aty.showToast(ToolsUtils.returnXMLStr("wft_pay_failure"));
                            Log.i("威富通支付失败===>>", out_trade_no);
                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    setClickPay(true);
                    progressDialog.disLoading();
                    Log.i("创建威富通交易失败", e.getMessage());
                    getWayWeiFuTong(autoCode, ToolsUtils.handleCarDish(cart.getDishItemList()), totalFee, orderTradeNo);//交易失败后重新获取支付信息
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }
}
