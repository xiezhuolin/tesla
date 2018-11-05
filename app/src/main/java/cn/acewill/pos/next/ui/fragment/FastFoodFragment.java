package cn.acewill.pos.next.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.tencent.WXPay;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.common.MarketDataController;
import cn.acewill.pos.next.common.PosSinUsbScreenController;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogActiveCall;
import cn.acewill.pos.next.interfices.DialogEtsCallback;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.interfices.KeyCallBack;
import cn.acewill.pos.next.interfices.WeightCallBack;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishItem;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.payment.WaimaiType;
import cn.acewill.pos.next.model.table.Sections;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.swipemenulistview.SwipeMenu;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuCreator;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuItem;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuListView;
import cn.acewill.pos.next.ui.activity.CheckOutNewAty;
import cn.acewill.pos.next.ui.activity.newPos.FetchOrderAty;
import cn.acewill.pos.next.ui.adapter.DishKidsAdp;
import cn.acewill.pos.next.ui.adapter.DishPagerNewAdp;
import cn.acewill.pos.next.ui.adapter.EatTypeAdapter;
import cn.acewill.pos.next.ui.adapter.OrderNewAdp;
import cn.acewill.pos.next.ui.adapter.SectionAdapter;
import cn.acewill.pos.next.ui.adapter.SelectPayTypeNewAdp;
import cn.acewill.pos.next.utils.CheckOutUtil;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DensityUtils;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DishMenuUtil;
import cn.acewill.pos.next.utils.FormatUtils;
import cn.acewill.pos.next.utils.PayDialogUtil;
import cn.acewill.pos.next.utils.ScanGunKeyEventHelper;
import cn.acewill.pos.next.utils.ScreenUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.AutoFitTextView;
import cn.acewill.pos.next.widget.NoPreloadViewPager;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ZoneGridView;

import static cn.acewill.pos.R.id.tv_eat_type;
import static cn.acewill.pos.next.common.DishDataController.dishKindList;
import static cn.acewill.pos.next.model.dish.Cart.getDishItemList;


/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class FastFoodFragment extends BaseFragment implements ScanGunKeyEventHelper.OnScanSuccessListener, WeightCallBack {
    @BindView( R.id.gv_dish_kind )
    ZoneGridView gvDishKind;
    @BindView( R.id.vp_dish )
    NoPreloadViewPager vpDish;
    @BindView( R.id.tv_order_id )
    TextView tvOrderId;
    @BindView( R.id.tv_storeName )
    TextView tvStoreName;
    @BindView( R.id.tv_userName )
    TextView tvUserName;
    @BindView( R.id.tv_price )
    TextView tvPrice;
    @BindView( R.id.btn_pre )
    TextView btnPre;
    @BindView( R.id.btn_next )
    TextView btnNext;
    @BindView( R.id.tv_time )
    TextView tvTime;
    @BindView( R.id.tv_member_info )
    TextView tvMemberInfo;
    @BindView( R.id.eat_in )
    Button eatIn;
    @BindView( R.id.sale_out )
    Button saleOut;
    @BindView( R.id.take_out )
    Button takeOut;
    @BindView( R.id.scan_code_dish )
    TextView scanCodeDish;
    @BindView( R.id.tv_count )
    TextView tvCount;
    @BindView( R.id.tv_card_number )
    TextView tvCardNumber;
    @BindView( R.id.tv_people_number )
    TextView tvPeopleNumber;
    @BindView( R.id.lv_order )
    SwipeMenuListView lvOrder;
    @BindView( R.id.paytype_gv )
    RecyclerView paytypeGv;
    @BindView( R.id.rel_order_info )
    RelativeLayout relOrderInfo;
    @BindView( R.id.rel_bottom )
    RelativeLayout relBottom;
    @BindView( R.id.rel_mask )
    RelativeLayout relMask;
    @BindView( R.id.img_wifi_state )
    ImageView imgWifiState;
    @BindView( R.id.img_print_state )
    ImageView imgPrintState;
    @BindView( tv_eat_type )
    AutoFitTextView tvEatType;

    private Cart cart;
    private Store store;
    private UserData mUserData;
    private Intent intent;
    private PosInfo posInfo;
    private boolean scaning = false;//防止扫码枪扫多次
    private ScanGunKeyEventHelper mScanGunKeyEventHelper;

    private ListView popLv;
    private PopupWindow window;
    private SectionAdapter sectionAdapter;
    private ListView popDishLv;
    private PopupWindow dishWindow;
    private SectionAdapter sectionDishAdapter;
    private ListView popEatTypeLv;
    private PopupWindow eatTypeWindow;
    private EatTypeAdapter eatTypeAdapter;
    private DishKidsAdp dishKidsTopAdp;
    private OrderNewAdp orderAdp;
    private ProgressDialogF progressDialog;
    private DishPagerNewAdp dishPagerAdp;
    private SelectPayTypeNewAdp selectPayTypeAdp;

    private List<Payment> paymentList = new ArrayList<>();

    private List<DishType> dishKind;//菜品分类
    private String tableName;//送餐桌台号
    private Integer numberOfCustomer = 0;
    private static int CHECKOUT = 110;

    // 用于显示每列4个Item项。
    int VIEW_COUNT = 8;

    // 用于显示页号的索引
    int index = 0;
    /**
     * 滑动page的下标位置
     */
    private int currentPosition = 0;
    private Long orderId = -1L;

    /**
     * 快速扫码点餐dialog
     */
    private Dialog quickOrderDialog;

    private TimerTaskController timerTaskController;

    private View fragmentContainerView;

    //    private View currentAnimalView;
    //    private AnimationSet manimationSet;
    //    ScaleAnimation scaleAnimation = new ScaleAnimation(1.1f, 1f, 1.1f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    //    ScaleAnimation scaleAnimation1 = new ScaleAnimation(1, 1.1f, 1, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentContainerView = inflater.inflate(R.layout.fragment_fast_food, container, false);
        ButterKnife.bind(this, fragmentContainerView);
        initData();
        cleanCart();
        setTime();
        initPayType();
        //TODO 初始化PopUpWindow
        initPopupWindow();
        eatTypePopupWindow();
        getOrderId();
        return fragmentContainerView;
    }

    //显示时间
    private void setTime() {
        new TimeThread().start(); //启动新的线程
    }

    @Override
    public void onResume() {
        super.onResume();
        getDishCounts();
    }


    private void initData() {
        myApplication.addPage(aty);
        intent = new Intent();
        store = Store.getInstance(aty);
        posInfo = PosInfo.getInstance();
        mUserData = UserData.getInstance(aty);
        mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);
        tvStoreName.setText(ToolsUtils.returnXMLStr("acewill_cloud_pos_v") + ToolsUtils.getVersionName(aty));
        posInfo.setOrderType("EAT_IN");
        eatSyleList.clear();
        eatSyleList.add(eatIn);
        eatSyleList.add(saleOut);
        eatSyleList.add(takeOut);
        setEatStyle(0);

        setPeopleNumber();

        tvUserName.setText(mUserData.getRealName());
        progressDialog = new ProgressDialogF(aty);
        dishKidsTopAdp = new DishKidsAdp(aty);
        dishPagerAdp = new DishPagerNewAdp(aty);

        orderAdp = new OrderNewAdp(aty);
        lvOrder.setAdapter(orderAdp);

        if (store.isOpenQuickOrder()) {
            scanCodeDish.setVisibility(View.VISIBLE);
        } else {
            scanCodeDish.setVisibility(View.INVISIBLE);
        }

        //左滑删除
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(aty);
                item1.setBackground(new ColorDrawable(Color.rgb(0xfe, 0x00, 0x00)));
                item1.setWidth(DensityUtils.dp2px(getActivity(), 100));
                item1.setTitle(ToolsUtils.returnXMLStr("delete"));
                item1.setTitleSize(18);
                item1.setTitleColor(resources.getColor(R.color.white));
                menu.addMenuItem(item1);
            }
        };

        // set creator
        lvOrder.setMenuCreator(creator);
        // step 2. listener item click event
        lvOrder.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                Dish dish = (Dish) orderAdp.getItem(position);
                if (dish != null) {
                    Cart.removeItem(dish, position);
                }
            }
        });

        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dish dish = (Dish) orderAdp.getItem(position);
                if (dish != null) {
                    relMask.setVisibility(View.VISIBLE);
                    orderAdp.setSelectItemPosition(position);
                    //dishMenuLogic(dish, position);
                    showDishPopupWindow(view, dish, position);
                }
            }
        });

        //购物车发现变化时的监听
        cart.addListener(new Cart.ChangeListener() {
            @Override
            public void contentChanged() {
                if (dishPagerAdp != null) {
                    dishPagerAdp.refresh();
                    List<Dish> marketActList = cart.getDishItemMarketActList();
                    orderAdp.setData(marketActList);
                    int dishItemSum = getOrderDishItemSum();
                    if (dishItemSum >= 1) {
                        tvCount.setVisibility(View.VISIBLE);
                        tvCount.setText(dishItemSum + "");
                    } else {
                        tvCount.setVisibility(View.GONE);
                        PosSinUsbScreenController.getInstance().ledClear();
                    }
                    ToolsUtils.writeUserOperationRecords("TIME===>选中菜品后购物车刷新时间==" + TimeUtil.getStringTimeLong(System.currentTimeMillis()));
                    //System.out.println(ToolsUtils.getPrinterSth(cart.dishItemList));
                    BigDecimal price = new BigDecimal(cart.getCost()).setScale(3, BigDecimal.ROUND_DOWN);
                    tvPrice.setText("¥" + String.format("%.2f ", price));
                    PosSinUsbScreenController.getInstance().ledDisplayTotal(String.format("%.2f ", price));
                    dishKidsTopAdp.notifyDataSetChanged();
                    ToolsUtils.writeUserOperationRecords("TIME===>选中菜品后计算总价钱==" + TimeUtil.getStringTimeLong(System.currentTimeMillis()));
                    //显示客显
                    if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                        SunmiSecondScreen.getInstance(aty).showDishImgExcel(cart.getOrderItem(null, cart.getDishItemList()));
                        ToolsUtils.writeUserOperationRecords("TIME===>商米副屏菜品刷新==" + TimeUtil.getStringTimeLong(System.currentTimeMillis()));
                    }

//                    EventBus.getDefault().post(new PosEvent(Constant.EventState.DISH_ITEM_CHANGE, marketActList, price, dishItemSum));

                    if (cart.getCost() >= 0)//如果选择的菜品金额大于等于0
                    {
                        BigDecimal exactlyMoney = new BigDecimal(String.valueOf(cart.getCost()));//金额刚好
                        final int[] othValues = FormatUtils.getMoney(exactlyMoney.intValue());
                        BigDecimal moneyOne = new BigDecimal(othValues[0]);
                        BigDecimal moneyTwo = new BigDecimal(othValues[1]);
                        BigDecimal moneyThree = new BigDecimal(othValues[2]);

                        Payment pay1 = new Payment(-1, ToolsUtils.returnXMLStr("exactly"), exactlyMoney);
                        Payment pay2 = new Payment(-2, "￥" + moneyOne.toString(), moneyOne);
                        Payment pay3 = new Payment(-3, "￥" + moneyTwo.toString(), moneyTwo);
                        Payment pay4 = new Payment(-4, "￥" + moneyThree.toString(), moneyThree);
                        Payment pay5 = new Payment(-5, ToolsUtils.returnXMLStr("combination_checkout"));

                        List<Payment> paymentCostList = new ArrayList<Payment>();
                        paymentCostList.clear();
                        paymentCostList.add(pay1);
                        paymentCostList.add(pay2);
                        paymentCostList.add(pay3);
                        paymentCostList.add(pay4);
                        paymentCostList.add(pay5);
                        for (Payment payment : paymentList) {
                            paymentCostList.add(payment);
                        }
                        if (paymentCostList != null && paymentCostList.size() > 0 && selectPayTypeAdp != null) {
                            selectPayTypeAdp.setDatas(paymentCostList);
                        }
                    }

                }
            }
        });

        cart = Cart.getInstance();
        if (getDishItemList() != null && getDishItemList().size() > 0) {
            if (cart != null) {
                cart.clear();
            }
        }
        //设置菜品分类ViewPager不可触摸滑动
        vpDish.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        //适配菜品分类数据
        gvDishKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = VIEW_COUNT * index + position;
                vpDish.setCurrentItem(currentPosition);
                dishPagerAdp.setDishCount(cart.dishCountsList);
                dishKidsTopAdp.setSelect(currentPosition % VIEW_COUNT);

                //                currentAnimalView = view;
                //                startAnimation(view);
                //                gvDishKind.setCurrentPosition(position);
                //                dishKidsTopAdp.setNotifyItemSelected(position);
            }
        });
        //        gvDishKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        //            @Override
        //            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //                currentAnimalView = view;
        //                startAnimation(view);
        //                gvDishKind.setCurrentPosition(position);
        //                dishKidsTopAdp.setNotifyDataChange(position);
        //            }
        //
        //            @Override
        //            public void onNothingSelected(AdapterView<?> parent) {
        //
        //            }
        //        });

        quickOrderDialog = DialogUtil.scanQuickOrder(aty, mScanGunKeyEventHelper);


        quickOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                scaning = false;
            }
        });

        timerTaskController = TimerTaskController.getInstance();
        //说明开启了称重服务
        if (timerTaskController.getLastWeight() != null) {
            timerTaskController.setWeightCallBack(this);
//            timerTaskController.syncWeightTimer();
        }
    }

    private int getOrderDishItemSum() {
        int dishItemSum = 0;
        if (!ToolsUtils.isList(getDishItemList())) {
            int size = getDishItemList().size();
            for (int i = 0; i < size; i++) {
                dishItemSum += getDishItemList().get(i).getQuantity();
            }
        }
        return dishItemSum;
    }

    /**
     * 获取支付方式
     */
    private void initPayType() {
        selectPayTypeAdp = new SelectPayTypeNewAdp(aty);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(aty);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        paytypeGv.setLayoutManager(linearLayoutManager);
        paytypeGv.setAdapter(selectPayTypeAdp);

        getPayType();

        selectPayTypeAdp.setOnItemClickListener(new SelectPayTypeNewAdp.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                final Payment payment = selectPayTypeAdp.getItem(position);
                ToolsUtils.writeUserOperationRecords("选择了" + payment.getName() + "支付方式");
                if (getDishItemList().size() > 0 || payment.getId() == 3 || payment.getId() == 4 || payment.getId() == 5) {
                    if (StoreInfor.cardNumberMode) {
                        tableName = posInfo.getTableNumber();
                        if (TextUtils.isEmpty(tableName)) {
                            DialogUtil.initTableInfo(aty, new DialogTCallback() {
                                @Override
                                public void onConfirm(Object o) {
                                    Order order = (Order) o;
                                    if (order != null) {
                                        if (!TextUtils.isEmpty(order.getTableNames())) {
                                            tableName = order.getTableNames();
                                            posInfo.setTableNumber(tableName);
                                            setCardNumber();
                                            dishStock(payment, tableName);
                                        }
                                    }
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                        }
                        else{
                            dishStock(payment, tableName);
                        }
                    } else {
                        dishStock(payment, null);
                    }
                } else {
                    showToast(ToolsUtils.returnXMLStr("please_click_dish"));
                }
            }
        });
    }

    public void setMemberInfo() {
        //处理监听事件
        if (posInfo.getAccountMember() != null) {
            Account account = posInfo.getAccountMember();
            tvMemberInfo.setVisibility(View.VISIBLE);
            tvMemberInfo.setText(ToolsUtils.returnXMLStr("phoneNumber") + ToolsUtils.replacePhone(account.getPhone()) + "\n"+ToolsUtils.returnXMLStr("member_grade") + account.getGradeName());
        } else {
            tvMemberInfo.setVisibility(View.GONE);
        }
    }


    private void swichPay(CheckOutUtil checUtil, final Payment payment, final String tableNames) {
        if (payment.getId() == -1 || payment.getId() == -2 || payment.getId() == -3 || payment.getId() == -4) {
            checUtil.directlyCreateOrder(tableNames, payment.getMoney());
        } else if (payment.getId() == -5) {
            ToolsUtils.writeUserOperationRecords("组合结账");
            if (getDishItemList().size() > 0) {
                if (!TextUtils.isEmpty(tableNames)) {
                    tableName = tableNames;
                }
                jumpCheckout();
            } else {
                myApplication.ShowToast(ToolsUtils.returnXMLStr("please_click_dish"));
            }
        } else {
            checUtil.swichPay(tableName);
        }
    }

    private void dishStock(final Payment payment, final String tableNames) {
        final CheckOutUtil checUtil = new CheckOutUtil(aty, payment);
        checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
            @Override
            public void haveStock() {
                if (posInfo.getOrderType().equals("SALE_OUT") && store.isWaiMaiGuestInfo()) {
                    DialogUtil.takeOutDialog(aty, new DialogTCallback() {
                        @Override
                        public void onConfirm(Object o) {
                            swichPay(checUtil, payment, tableNames);
                        }

                        @Override
                        public void onCancle() {

                        }
                    });
                } else {
                    swichPay(checUtil, payment, tableNames);
                }

            }

            @Override
            public void noStock(List dataList) {
                refreshDish(dataList, getDishItemList());
            }
        });
    }

    /**
     * 跳转到结账界面
     */
    private Order tableOrder;

    private void jumpCheckout() {
        float dishPrice = Cart.getPriceSum();
        float dishCost = Cart.getCost();
        BigDecimal activeMoney = new BigDecimal(String.valueOf(dishPrice - dishCost)).setScale(2, BigDecimal.ROUND_HALF_UP);
        Intent intent = new Intent(aty, CheckOutNewAty.class);
        intent.putExtra("source", Constant.EventState.SOURCE_CREAT_ORDER);
        intent.putExtra("tableOrder", (Serializable) tableOrder);
        intent.putExtra("active_money", (Serializable) activeMoney);
        intent.putExtra("activityName", activityName);
        intent.putExtra("tableName", tableName);
        startActivityForResult(intent, CHECKOUT);
    }

    public void refreshDish(List<DishCount> result, List<Dish> dishs) {
        //刷新菜品数据,显示沽清
        getDishInfo();
        if (orderAdp != null) {
            orderAdp.setCheckDishCount(result);
        }
        String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
        showToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough")+"\n\n" + names
                + "。\n\n"+ToolsUtils.returnXMLStr("please_re_order"));
        orderAdp.checkDishCount.clear();
        orderAdp.notifyDataSetChanged();
        progressDialog.disLoading();
        Log.i("以下菜品份数不足:", names + "====<<");
    }

    private void cleanPayMentList() {
        if (paymentList != null && paymentList.size() > 0) {
            paymentList.clear();
        }
    }


    public void getOrderId() {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.getNextOrderId(new ResultCallback<Long>() {
                @Override
                public void onResult(Long result) {
                    if (result > 0) {
                        orderId = result;
                        posInfo.setOrderId(orderId);
                        Log.i("获取订单Id成功", "====>>:NextOrderId=" + orderId);
                        tvOrderId.setText(ToolsUtils.returnXMLStr("order_id_xx") + orderId);
                        posInfo.setAccountMember(null);
                        tvMemberInfo.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取订单Id失败", e.getMessage());
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
//                    showToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    //获取支付方式列表
    private void getPayType() {
        try {
            cleanPayMentList();
            if (StoreInfor.getPaymentList() != null && StoreInfor.getPaymentList().size() > 0) {
                for (Payment payment : StoreInfor.getPaymentList()) {
                    paymentList.add(payment);
                }
                initAliAndWx(paymentList);
                selectPayTypeAdp.setDatas(paymentList);
                return;
            }
            DishService dishService = DishService.getInstance();
            dishService.getPaytypeList(new ResultCallback<List<Payment>>() {

                @Override
                public void onResult(List<Payment> result) {
                    if (result != null && result.size() > 0) {
                        paymentList = result;
                        initAliAndWx(result);
                        selectPayTypeAdp.setDatas(result);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取支付方式列表", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("获取支付方式列表", e.getMessage());
        }
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

    public void getDishCounts() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
                @Override
                public void onResult(List<DishCount> result) {
                    progressDialog.disLoading();
                    if (result != null && result.size() > 0) {
                        cart.dishCountsList = result;
                        dishPagerAdp.setDishCount(result);
                    } else {
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
                    }
                    loadData();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    loadData();
                    Log.i("获取菜品沽清状态失败", e.getMessage());
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err")+"," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            Log.i("获取菜品沽清状态失败", e.getMessage());
            e.printStackTrace();
            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
        }
    }

    private void loadData() {
        if (DishDataController.dishKindList != null && DishDataController.dishKindList.size() > 0) {
            if (DishDataController.menuData != null && DishDataController.menuData.size() > 0) {
                setKidsMap(dishKindList);
                initDishAdapter();
            } else {
                getDishInfo();
            }

        } else {
            getKindInfo();
        }
    }

    private void initDishAdapter() {
        progressDialog.disLoading();
        if (dishKidsTopAdp != null && gvDishKind != null && vpDish != null) {
            setKidsMap(dishKindList);
            gvDishKind.setAdapter(dishKidsTopAdp);
            vpDish.setAdapter(dishPagerAdp);
            vpDish.setCurrentItem(currentPosition);
            dishKidsTopAdp.setSelect(currentPosition);

            //            View view = dishKidsTopAdp.getView(0,null,null);
            //            currentAnimalView = view;
            //            startAnimation(view);
            //            gvDishKind.setCurrentPosition(0);
            //            dishKidsTopAdp.setNotifyDataChange(0);
        }
    }

    private ArrayList<HashMap<String, DishType>> listItem = new ArrayList<>();

    private void setKidsMap(List<DishType> dishKindList) {
        if (listItem != null && listItem.size() > 0) {
            listItem.clear();
        }
        if (dishKindList != null && dishKindList.size() > 0) {
            int size = dishKindList.size();
            for (int i = 0; i < size; i++) {
                HashMap<String, DishType> map = new HashMap<String, DishType>();
                map.put("kindsItem", dishKindList.get(i));
                listItem.add(map);
            }
            checkButton();
            dishKidsTopAdp.setKidsMapList(listItem);
        }
    }

    /**
     * 获取菜品分类数据
     */
    private void getKindInfo() {
        progressDialog.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
            @Override
            public void onResult(List<DishType> result) {
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    dishKind = result;
                    dishKindList = dishKind;
                    setKidsMap(dishKindList);
                    getDishInfo();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                    Log.i("获取菜品分类为空", "");
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null")+"," + e.getMessage());
                Log.i("获取菜品分类为空", e.getMessage());
            }
        });
    }

    /**
     * 得到菜品数据  dishList
     */
    private void getDishInfo() {
        progressDialog.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getDishList(new ResultCallback<List<Menu>>() {
            @Override
            public void onResult(List<Menu> result) {
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    DishDataController.setDishData(result);
                    initDishAdapter();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(e.getMessage());
                Log.i("获取菜品为空", e.getMessage());
            }
        });
    }

    public void checkButton() {
        // 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
        // 将向前翻页的按钮设为不可用。
        if (index <= 0) {
            btnPre.setEnabled(false);
            btnPre.setTextColor(resources.getColor(R.color.gray_check_sth));
        } else {
            btnPre.setEnabled(true);
            btnPre.setTextColor(resources.getColor(R.color.black));
        }
        // 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        // 将向后翻页的按钮设为不可用。
        if (listItem.size() - index * VIEW_COUNT <= VIEW_COUNT) {
            btnNext.setEnabled(false);
            btnNext.setTextColor(resources.getColor(R.color.gray_check_sth));
        }
        // 否则将2个按钮都设为可用的。
        else {
            btnNext.setEnabled(true);
            btnNext.setTextColor(resources.getColor(R.color.black));
        }
    }

    // 点击左边的Button，表示向前翻页，索引值要减1.
    public void preView(boolean isSwitchData) {
        index--;

        dishKidsTopAdp.setSelectPage(index);
        if (isSwitchData) {
            dishKidsTopAdp.setSelect(VIEW_COUNT - 1);//切换到最后一项
            currentPosition = index * VIEW_COUNT + (VIEW_COUNT - 1);
            vpDish.setCurrentItem(currentPosition);
        }
        // 刷新ListView里面的数值。
        dishKidsTopAdp.notifyDataSetChanged();

        // 检查Button是否可用。
        checkButton();
    }

    // 点击右边的Button，表示向后翻页，索引值要加1.
    public void nextView(boolean isSwitchData) {
        index++;

        dishKidsTopAdp.setSelectPage(index);
        if (isSwitchData) {
            dishKidsTopAdp.setSelect(0);//切换到第一项
            currentPosition = index * VIEW_COUNT;
            vpDish.setCurrentItem(currentPosition);
        }
        // 刷新ListView里面的数值。
        dishKidsTopAdp.notifyDataSetChanged();

        // 检查Button是否可用。
        checkButton();
    }

    /**
     * 清空购物车,以及清空一些数据缓存
     */
    public void cleanCart() {
        Cart.cleanDishList();
        if (dishPagerAdp != null) {
            dishPagerAdp.refresh();
        }
        if (orderAdp != null) {
            orderAdp.setData(getDishItemList());
        }
        if (lvOrder != null) {
            lvOrder.setAdapter(orderAdp);
        }
        if (selectPayTypeAdp != null && paymentList != null) {
            selectPayTypeAdp.setDatas(paymentList);
        }
//        cleanDishSelectMap();
        DishDataController.cleanDishMarkMap();
        if (dishKidsTopAdp != null) {
            dishKidsTopAdp.notifyDataSetChanged();
//            dishKidsTopAdp.setMap(dishSelectMap);
        }
        posInfo.setOrderType("EAT_IN");
        tvEatType.setText(ToolsUtils.returnXMLStr("eat_in"));
        if (eatTypeAdapter != null) {
            eatTypeAdapter.setSelectPosition(0);
        }
        //        setEatStyle(0);
        tvPrice.setText("¥ 0.00");
        tvCount.setVisibility(View.GONE);
        DishOptionController.cleanCartDishMap();//下单后清除菜品的定制项缓存
        posInfo.setAccountMember(null);
        tvMemberInfo.setVisibility(View.GONE);
    }

    //清空菜品分类所选择的提示个数map
//    public void cleanDishSelectMap() {
//        if (dishSelectMap != null && dishSelectMap.size() > 0) {
//            dishSelectMap.clear();
//        }
//    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = 1;  //消息(一个整型值)
                    mHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    //在主线程里面处理消息并更新UI界面
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);
                    tvTime.setText(sysTimeStr); //更新时间
                    break;

            }
        }
    };

    /**
     * 切换试图刷新界面
     *
     * @param currentPosition
     */
    private void switchRefreshUi(int currentPosition, boolean isShowDishImg) {
        this.currentPosition = currentPosition;
        dishPagerAdp.setDishShowStyle(isShowDishImg);
        vpDish.setAdapter(dishPagerAdp);
        vpDish.setCurrentItem(currentPosition);
        dishKidsTopAdp.setSelect(currentPosition % VIEW_COUNT);
    }

    private List<TextView> eatSyleList = new ArrayList<>();

    private void setEatStyle(int selectPosition) {
        for (int i = 0; i < eatSyleList.size(); i++) {
            if (i == selectPosition) {
                eatSyleList.get(i).setTextColor(resources.getColor(R.color.white));
                eatSyleList.get(i).setBackgroundResource(R.drawable.border_blue);
            } else {
                eatSyleList.get(i).setTextColor(resources.getColor(R.color.black));
                eatSyleList.get(i).setBackgroundResource(R.drawable.border);
            }
        }
    }


    public void setWifiState(boolean state) {
        if (state) {
            ToolsUtils.writeUserOperationRecords("服务器已连接");
            imgWifiState.setImageResource(R.mipmap.img_order_wifi_success);
        } else {
            ToolsUtils.writeUserOperationRecords("服务器未连接");
            imgWifiState.setImageResource(R.mipmap.img_order_wifi_failure);
        }
    }

    @OnClick( {R.id.img_print_state, R.id.tv_order_id, R.id.eat_in, R.id.sale_out, R.id.take_out, R.id.btn_pre, R.id.btn_next, R.id.tv_userName, R.id.rel_mask, R.id.scan_code_dish, R.id.tv_eat_type} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_print_state:
                if (PrinterDataController.getPrinterList() != null && PrinterDataController.getPrinterList().size() > 0) {
                    DialogUtil.printerStateDialog(aty);
                } else {
                    showToast(ToolsUtils.returnXMLStr("not_find_printer"));
                }
                break;
            case R.id.eat_in:
                if (!posInfo.getOrderType().equals("EAT_IN")) {
                    posInfo.setOrderType("EAT_IN");
                    Cart.switchEATINPrice();
                }
                setEatStyle(0);
                break;
            case R.id.sale_out:
                if (!posInfo.getOrderType().equals("SALE_OUT")) {
                    posInfo.setOrderType("SALE_OUT");
                    Cart.switchWaiMaiPrice();
                }
                setEatStyle(1);
                posInfo.setWaimaiType(WaimaiType.LOCAL);
                break;
            case R.id.take_out:
                if (!posInfo.getOrderType().equals("TAKE_OUT")) {
                    posInfo.setOrderType("TAKE_OUT");
                    Cart.switchEATINPrice();
                }
                setEatStyle(2);
                break;
            case R.id.btn_pre:
                //                if(currentAnimalView != null)
                //                {
                //                    currentAnimalView.clearAnimation();
                //                }
                preView(true);
                break;
            case R.id.btn_next:
                //                if(currentAnimalView != null)
                //                {
                //                    currentAnimalView.clearAnimation();
                //                }
                nextView(true);
                break;
            case R.id.tv_userName:
                EventBus.getDefault().post(new PosEvent(Constant.EventState.LOGOUT));
                break;
            case R.id.tv_order_id:
                relMask.setVisibility(View.VISIBLE);
                ToolsUtils.writeUserOperationRecords("全单操作");
                int w = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int h = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                view.measure(w, h);
                int height = view.getMeasuredHeight();
                int width = view.getMeasuredWidth();
                int[] location = new int[2];
                tvOrderId.getLocationOnScreen(location);
                window.showAsDropDown(relOrderInfo, location[0], location[1] - (2 * height) + 45);

                freeOrderSett(false);
                break;
            case R.id.rel_mask:
                relMask.setVisibility(View.GONE);
                if (dishWindow != null && dishWindow.isShowing()) {
                    dishWindow.dismiss();
                }
                if (window != null && window.isShowing()) {
                    window.dismiss();
                }
                if (eatTypeWindow != null && eatTypeWindow.isShowing()) {
                    eatTypeWindow.dismiss();
                }
                //                hideBottomUIMenu();
                break;
            case R.id.scan_code_dish:
                ToolsUtils.writeUserOperationRecords("快捷下单");
                quickOrderDialog.show();
                break;
            case R.id.tv_eat_type:
                ToolsUtils.writeUserOperationRecords("选择就餐方式");
                relMask.setVisibility(View.VISIBLE);
                int ww = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                int hw = View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED);
                relBottom.measure(ww, ww);
                int heightt = view.getMeasuredHeight();
                int widtht = view.getMeasuredWidth();

                int[] locationEat = new int[2];
                relBottom.getLocationOnScreen(locationEat);
                eatTypeWindow.showAtLocation(relBottom, Gravity.NO_GRAVITY, locationEat[0], locationEat[1] - (heightt * 3) - 20);
                break;
        }
    }

    //    /**
    //     * 隐藏虚拟按键，并且全屏
    //     */
    //    protected void hideBottomUIMenu() {
    //        //隐藏虚拟按键，并且全屏
    //        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
    //            View v = aty.getWindow().getDecorView();
    //            v.setSystemUiVisibility(View.GONE);
    //        } else if (Build.VERSION.SDK_INT >= 19) {
    //            //for new api versions.
    //            View decorView = aty.getWindow().getDecorView();
    //            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    //                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
    //            decorView.setSystemUiVisibility(uiOptions);
    //        }
    //    }


    StringBuffer sb = new StringBuffer();
    int isPrintState = 1; //1扫描前  2 扫描中  3扫描完毕

    /**
     * 扫码成功后返回的字符串
     *
     * @param barcode
     */
    @Override
    public void onScanSuccess(String barcode) {
        Log.e("反扫code返回:", barcode);
        if (isPrintState == 1) {
            if (barcode.contains("e")) {
                isPrintState = 3;
            }
            sb.append(barcode);
        }
        if (isPrintState == 3) {
            String code = sb.toString();
            sb.setLength(0);
            isPrintState = 1;
            if (!TextUtils.isEmpty(code)) {
                if (!scaning) {
                    scaning = true;
                    List<Dish> scanDishList = MarketDataController.scanQuickOrder(code);
                    if (scanDishList != null && scanDishList.size() > 0) {
                        for (Dish dish : scanDishList) {
                            Cart.getInstance().addItem(dish, false);
                            try {
                                Thread.sleep(200);//加一个菜休眠100毫秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Cart.notifyContentChange();
                    } else {
                        showToast("请扫描正确菜品二维码!");
                    }
                    quickOrderDialog.dismiss();
                } else {
                    Log.i("扫码枪返回", "重复扫码");
                    showToast(ToolsUtils.returnXMLStr("do_not_repeat_the_sweep_code"));
                }
            }
        }
    }

    public void switchSHowView(boolean isShowDishImg) {
        //        if (isShowDishImg)//菜品显示图片
        //        {
        //            imgDishShowStyle.setBackgroundResource(R.mipmap.img_dish_bg_dissmiss);
        //            isShowDishImg = false;
        //        } else//菜品不显示图片
        //        {
        //            imgDishShowStyle.setBackgroundResource(R.mipmap.img_dish_bg_show);
        //            isShowDishImg = true;
        //        }
        switchRefreshUi(0, isShowDishImg);
    }


    public void cleanData() {
        cleanCart();
    }

    private void dishMenuLogic(final Dish dish, final int position) {
        List<DishItem> dishItemList = new ArrayList<DishItem>();
        DishItem dishItem1 = new DishItem(ToolsUtils.returnXMLStr("dish_count"), Constant.DishMenu.DISH_COUNT);
        DishItem dishItem2 = new DishItem(ToolsUtils.returnXMLStr("dish_sku"), Constant.DishMenu.DISH_SKU);
        DishItem dishItem3 = new DishItem(ToolsUtils.returnXMLStr("discount_plan"), Constant.DishMenu.DISH_DISCOUNT);
        DishItem dishItem7 = new DishItem(ToolsUtils.returnXMLStr("package_cost"), Constant.DishMenu.TAKEOUT_DISH);
        DishItem dishItem8 = new DishItem(ToolsUtils.returnXMLStr("dish_specification"), Constant.DishMenu.SPECIFICATIONS_DISH);
        String dishName = "";
        if (dish != null) {
            dishName = dish.getDishName();
            dishItemList.add(dishItem1);
            if (dish.getOptionCategoryList() != null && dish.getOptionCategoryList().size() > 0) {
                dishItemList.add(dishItem2);
            }
            if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
                dishItemList.add(dishItem3);
            }
            if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                dishItemList.add(dishItem7);
            }
            if (dish.getSpecificationList() != null && dish.getSpecificationList().size() > 0) {
                dishItemList.add(dishItem8);
            }
        }
        //        int width = (myApplication.getScreenWidth()/2) - myApplication.getScreenWidth()/10*4;
        //        DialogUtil.creatDishMenu(aty, dishName, dishItemList, width,new DialogTCallback() {
        //            @Override
        //            public void onConfirm(Object o) {
        //                DishItem dishitem = (DishItem) o;
        //                if (dishitem != null) {
        //                    switch (dishitem.itemId) {
        //                        //菜品数量
        //                        case Constant.DishMenu.DISH_COUNT:
        //                            ToolsUtils.writeUserOperationRecords("菜品数量选项");
        //                            DishMenuUtil.setDishCountDialog(aty, dish, position);
        //                            break;
        //                        //菜品定制项
        //                        case Constant.DishMenu.DISH_SKU:
        //                            ToolsUtils.writeUserOperationRecords("菜品定制项选项");
        //                            if (dish != null) {
        //                                DishMenuUtil.setDishSkuDialog(aty, dish, position);
        //                            }
        //                            break;
        //                        //打折方案
        //                        case Constant.DishMenu.DISH_DISCOUNT:
        //                            ToolsUtils.writeUserOperationRecords("打折方案选项");
        //                            if (dish != null) {
        //                                DishMenuUtil.setDishDisCountDialog(aty, dish, position);
        //                            }
        //                            break;
        //                        //打包费用
        //                        case Constant.DishMenu.TAKEOUT_DISH:
        //                            ToolsUtils.writeUserOperationRecords("打包费用选项");
        //                            DishMenuUtil.setDishTakeOutDialog(aty, dish, position);
        //                            break;
        //                        //菜品规格
        //                        case Constant.DishMenu.SPECIFICATIONS_DISH:
        //                            ToolsUtils.writeUserOperationRecords("菜品规格选项");
        //                            if (dish != null) {
        //                                DishMenuUtil.setDishSpecificationsDialog(aty, dish, position);
        //                            }
        //                            break;
        //                    }
        //                }
        //            }
        //
        //            @Override
        //            public void onCancle() {
        //                orderAdp.setSelectItemPosition(orderAdp.INITID);//设置成从未选中的颜色样式
        //            }
        //        });
    }

    public void setCardNumber() {
        if (StoreInfor.cardNumberMode && !TextUtils.isEmpty(posInfo.getTableNumber())) {
            tvCardNumber.setVisibility(View.VISIBLE);
            tvCardNumber.setText(ToolsUtils.returnXMLStr("tableNumber")+ posInfo.getTableNumber());
        } else {
            tvCardNumber.setVisibility(View.GONE);
        }
    }

    public void setPeopleNumber() {
        if (StoreInfor.isRepastPopulation()) {
            tvPeopleNumber.setVisibility(View.VISIBLE);
            tvPeopleNumber.setText(ToolsUtils.returnXMLStr("persons") + posInfo.getCustomerAmount());
        } else {
            tvPeopleNumber.setVisibility(View.GONE);
        }
    }

    private List<Sections> sectionList = new ArrayList<>();
    private List<Sections> sectionDishList = new ArrayList<>();
    private List<Sections> sectionEatTypeList = new ArrayList<>();
    //初始化顶部弹框
    private BigDecimal temp_money = new BigDecimal(0);//总支付金额，用于营销活动计算使用
    private BigDecimal avtive_money = new BigDecimal(0);//营销活动减免金额
    private String activityName = "";//营销活动名称

    private void initPopupWindow() {
        List<Sections> sectionListlocal = new ArrayList<>();
        Sections section1 = new Sections(ToolsUtils.returnXMLStr("order_hang"), Constant.ORDER_HANG);
        Sections section2 = new Sections(ToolsUtils.returnXMLStr("person"), Constant.ORDER_CUSTOMER_NUMBER);
        Sections section3 = new Sections(ToolsUtils.returnXMLStr("note"), Constant.ORDER_REMARK);
        Sections section4 = new Sections(ToolsUtils.returnXMLStr("get_order"), Constant.ORDER_GET);
        Sections section5 = new Sections(ToolsUtils.returnXMLStr("free_order"), Constant.ORDER_FREE);
        Sections section6 = new Sections(ToolsUtils.returnXMLStr("discount_all_dish"), Constant.ORDER_ALL_DISCOUNT);
        Sections section7 = new Sections(ToolsUtils.returnXMLStr("clean_dish"), Constant.ORDER_CLEAN);
        Sections section8 = new Sections(ToolsUtils.returnXMLStr("refresh_orderId"), Constant.ORDER_ID_REFRESH);
        Sections section9 = new Sections(ToolsUtils.returnXMLStr("clean_member_info"), Constant.ORDER_MEMBER_CLEAN);
        Sections section10 = new Sections(ToolsUtils.returnXMLStr("cardnumber"), Constant.ORDER_CARDNUMBER_MODE);
        sectionListlocal.add(section1);
        sectionListlocal.add(section4);
        if(StoreInfor.isRepastPopulation())
        {
            sectionListlocal.add(section2);
        }
        if (StoreInfor.cardNumberMode) {
            sectionListlocal.add(section10);
        }

        sectionListlocal.add(section3);
        sectionListlocal.add(section6);
        sectionListlocal.add(section7);
        //免单是需要后台权限的
        if (PowerController.isAllow(PowerController.FREE_SINGLE)) {
            sectionListlocal.add(section5);
        }
        sectionListlocal.add(section8);
        sectionListlocal.add(section9);
        View popupView = aty.getLayoutInflater().inflate(R.layout.popup_view_dish_menu, null);
        popLv = (ListView) popupView.findViewById(R.id.section_list);
        sectionAdapter = new SectionAdapter(aty);
        sectionList = ToolsUtils.cloneTo(sectionListlocal);
        sectionAdapter.setData(sectionListlocal);
        popLv.setAdapter(sectionAdapter);
        popLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                window.dismiss();
                Sections sections = (Sections) sectionAdapter.getItem(position);
                if (sections != null) {
                    switch (sections.getSectionsStyle()) {
                        case Constant.ORDER_CARDNUMBER_MODE:
                            ToolsUtils.writeUserOperationRecords("餐牌模式");
                            DialogUtil.initTableInfo(aty, new DialogTCallback() {
                                @Override
                                public void onConfirm(Object o) {
                                    Order order = (Order) o;
                                    if (order != null) {
                                        if (!TextUtils.isEmpty(order.getTableNames())) {
                                            tableName = order.getTableNames();
                                            posInfo.setTableNumber(tableName);
                                            setCardNumber();
                                        }
                                    }
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                            break;
                        case Constant.ORDER_MEMBER_CLEAN:
                            ToolsUtils.writeUserOperationRecords("清除微生活会员信息");
                            posInfo.setAccountMember(null);
                            tvMemberInfo.setVisibility(View.GONE);
                            Cart.notifyContentChange();
                            break;
                        case Constant.ORDER_CLEAN:
                            ToolsUtils.writeUserOperationRecords("清空");
                            PosSinUsbScreenController.getInstance().ledClear();
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));
                            cleanData();
                            //                            getDishCounts();
                            break;
                        case Constant.ORDER_HANG:
                            ToolsUtils.writeUserOperationRecords("挂单");
                            if (!Cart.isCartDishNull()) {
                                Cart.handDishList(Cart.getDishItemList());
                            } else {
                                showToast(ToolsUtils.returnXMLStr("please_click_dish"));
                            }
                            break;
                        case Constant.ORDER_GET:
                            ToolsUtils.writeUserOperationRecords("取单");
                            intent.setClass(aty, FetchOrderAty.class);
                            startActivity(intent);
                            break;
                        case Constant.ORDER_REMARK:
                            ToolsUtils.writeUserOperationRecords("备注");
                            PayDialogUtil.setMarkDialog(aty, "", new KeyCallBack() {
                                @Override
                                public void onOk(Object o) {
                                    String remark = o.toString();
                                    posInfo.setComment(remark);
                                }
                            });
                            break;
                        case Constant.ORDER_ALL_DISCOUNT:
                            ToolsUtils.writeUserOperationRecords("全单打折");
                            if (Cart.getDishItemList() != null && Cart.getDishItemList().size() > 0) {
                                temp_money = new BigDecimal(cart.getCost() + "");
                                PayDialogUtil.getActiveDialog(aty, new BigDecimal[]{temp_money}, null, Cart.getDishItemList(), new DialogActiveCall() {
                                    @Override
                                    public void onOk(BigDecimal allMoney, BigDecimal activeMoney, String activeName) {
                                        avtive_money = activeMoney;
                                        //优惠的金额保留小数点
                                        avtive_money = avtive_money.setScale(2, BigDecimal.ROUND_DOWN);
                                        activityName = activeName;
                                        Cart.notifyContentChange();
                                        // allMoney = allMoney.setScale(0, BigDecimal.ROUND_HALF_UP);

                                        //                                        ToolsUtils.setDishItemMarket(new MarketObject(activeName, avtive_money, MarketType.DISCOUNT));
                                        //                                        Cart.setDishItemTempMarketActList();
                                    }
                                });
                            } else {
                                showToast(ToolsUtils.returnXMLStr("please_click_dish"));
                            }
                            break;
                        case Constant.ORDER_CUSTOMER_NUMBER:
                            ToolsUtils.writeUserOperationRecords("人数");
                            DialogUtil.openTableDialog(aty, ToolsUtils.returnXMLStr("person"), ToolsUtils.returnXMLStr("repast_counts3"), ToolsUtils.returnXMLStr("sth_person_hine"), 0, true, false, new DialogEtsCallback() {
                                @Override
                                public void onConfirm(String sth) {
                                    if (TextUtils.isEmpty(sth)) {
                                        numberOfCustomer = Integer.valueOf(1);//人数  直接点击确定按钮,就餐人数设为0
                                    } else {
                                        numberOfCustomer = Integer.valueOf(sth);//人数
                                    }
                                    posInfo.setCustomerAmount(numberOfCustomer);
                                    setPeopleNumber();
                                }

                                @Override
                                public void onCancle(String sth) {
                                    if (TextUtils.isEmpty(sth)) {
                                        numberOfCustomer = Integer.valueOf(1);//人数  直接点击确定按钮,就餐人数设为0
                                    } else {
                                        numberOfCustomer = Integer.valueOf(sth);//人数
                                    }
                                    posInfo.setCustomerAmount(numberOfCustomer);
                                    setPeopleNumber();
                                }
                            });
                            break;
                        case Constant.ORDER_FREE:
                            ToolsUtils.writeUserOperationRecords("免单");
                            freeOrderSett(true);
                            break;
                        case Constant.ORDER_ID_REFRESH:
                            ToolsUtils.writeUserOperationRecords("刷新订单号");
                            getOrderId();
                            break;
                    }
                }

            }
        });

        int width_p = ScreenUtil.getScreenSize(aty)[0] / 5;
        window = new PopupWindow(popupView);
        window.setWidth(width_p);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(ContextCompat.getDrawable(aty, R.drawable.empty_icon));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();

        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                relMask.setVisibility(View.GONE);
            }
        });
    }

    private void freeOrderSett(boolean isClick)
    {
        if (sectionList != null && sectionList.size() > 0) {
            int size = sectionList.size();
            for (int i = 0; i < size; i++) {
                Sections section = sectionList.get(i);
                if(section.getSectionsStyle() == Constant.ORDER_FREE)
                {
                    if (section != null) {
                        String name = section.getName();
                        if(isClick)
                        {
                            if (name.equals(ToolsUtils.returnXMLStr("free_order"))) {
                                section.setName(ToolsUtils.returnXMLStr("cancel_free"));
                                posInfo.setFreeOrder(true);
                            } else {
                                section.setName(ToolsUtils.returnXMLStr("free_order"));
                                posInfo.setFreeOrder(false);
                            }
                        }
                        else{
                            if(posInfo.isFreeOrder())
                            {
                                section.setName(ToolsUtils.returnXMLStr("cancel_free"));
                            }
                            else{
                                section.setName(ToolsUtils.returnXMLStr("free_order"));
                            }
                        }
                        sectionAdapter.setData(sectionList);
                    }
                }
            }
        }
    }

    private void showDishPopupWindow(final View view, final Dish dish, final int dishPosition) {
        List<Sections> sectionDishListlocal = new ArrayList<>();
        sectionDishList = ToolsUtils.cloneTo(sectionDishListlocal);
        Sections dishItem1 = new Sections(ToolsUtils.returnXMLStr("dish_count"), Constant.DishMenu.DISH_COUNT);
        Sections dishItem2 = new Sections(ToolsUtils.returnXMLStr("dish_sku"), Constant.DishMenu.DISH_SKU);
        Sections dishItem3 = new Sections(ToolsUtils.returnXMLStr("discount_plan"), Constant.DishMenu.DISH_DISCOUNT);
        Sections dishItem7 = new Sections(ToolsUtils.returnXMLStr("package_cost"), Constant.DishMenu.TAKEOUT_DISH);
        Sections dishItem8 = new Sections(ToolsUtils.returnXMLStr("dish_specification"), Constant.DishMenu.SPECIFICATIONS_DISH);

        String dishName = "";
        if (dish != null) {
            dishName = dish.getDishName();
            sectionDishListlocal.add(dishItem1);
            if (dish.getOptionCategoryList() != null && dish.getOptionCategoryList().size() > 0) {
                sectionDishListlocal.add(dishItem2);
            }
            if (StoreInfor.marketingActivities != null && StoreInfor.marketingActivities.size() > 0) {
                sectionDishListlocal.add(dishItem3);
            }
            if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                sectionDishListlocal.add(dishItem7);
            }
            if (dish.getSpecificationList() != null && dish.getSpecificationList().size() > 0) {
                sectionDishListlocal.add(dishItem8);
            }
        }

        View popupView = aty.getLayoutInflater().inflate(R.layout.popup_view_dish, null);
        popDishLv = (ListView) popupView.findViewById(R.id.section_list);
        sectionDishAdapter = new SectionAdapter(aty);
        sectionDishAdapter.setData(sectionDishListlocal);
        popDishLv.setAdapter(sectionDishAdapter);


        int width_p = ScreenUtil.getScreenSize(aty)[0] / 6;
        dishWindow = new PopupWindow(popupView);
        dishWindow.setWidth(width_p);
        dishWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        dishWindow.setBackgroundDrawable(ContextCompat.getDrawable(aty, R.drawable.empty_icon));
        dishWindow.setFocusable(true);
        dishWindow.setOutsideTouchable(true);
        dishWindow.update();

        ToolsUtils.writeUserOperationRecords("单项菜品操作");
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int popWidth = (myApplication.getScreenWidth() / 2) - myApplication.getScreenWidth() / 10 * 3;
        dishWindow.showAtLocation(view, Gravity.NO_GRAVITY, popWidth + dishWindow.getWidth(), location[1]);
        popDishLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dishWindow.dismiss();
                Sections sections = (Sections) sectionDishAdapter.getItem(position);
                if (sections != null) {
                    switch (sections.getSectionsStyle()) {
                        //菜品数量
                        case Constant.DishMenu.DISH_COUNT:
                            ToolsUtils.writeUserOperationRecords("菜品数量选项");
                            DishMenuUtil.setDishCountDialog(aty, dish, dishPosition);
                            break;
                        //菜品定制项
                        case Constant.DishMenu.DISH_SKU:
                            ToolsUtils.writeUserOperationRecords("菜品定制项选项");
                            if (dish != null) {
                                DishMenuUtil.setDishSkuDialog(aty, dish, dishPosition);
                            }
                            break;
                        //打折方案
                        case Constant.DishMenu.DISH_DISCOUNT:
                            ToolsUtils.writeUserOperationRecords("打折方案选项");
                            if (dish != null) {
                                DishMenuUtil.setDishDisCountDialog(aty, dish, dishPosition);
                            }
                            break;
                        //打包费用
                        case Constant.DishMenu.TAKEOUT_DISH:
                            ToolsUtils.writeUserOperationRecords("打包费用选项");
                            DishMenuUtil.setDishTakeOutDialog(aty, dish, dishPosition);
                            break;
                        //菜品规格
                        case Constant.DishMenu.SPECIFICATIONS_DISH:
                            ToolsUtils.writeUserOperationRecords("菜品规格选项");
                            if (dish != null) {
                                DishMenuUtil.setDishSpecificationsDialog(aty, dish, dishPosition);
                            }
                            break;

                    }
                }
            }
        });


        dishWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                relMask.setVisibility(View.GONE);
                orderAdp.setSelectItemPosition(orderAdp.INITID);
            }
        });
    }

    private void eatTypePopupWindow() {
        List<Sections> sectionDishListlocal = new ArrayList<>();
        sectionEatTypeList = ToolsUtils.cloneTo(sectionDishListlocal);
        Sections dishItem1 = new Sections(ToolsUtils.returnXMLStr("eat_in"), Constant.DishMenu.EAT_IN);
        Sections dishItem2 = new Sections(ToolsUtils.returnXMLStr("sth_take_out_title"), Constant.DishMenu.SALE_OUT);
        Sections dishItem3 = new Sections(ToolsUtils.returnXMLStr("take_out"), Constant.DishMenu.TAKE_OUT);
        sectionEatTypeList.add(dishItem1);
        sectionEatTypeList.add(dishItem2);
        sectionEatTypeList.add(dishItem3);

        View popupView = aty.getLayoutInflater().inflate(R.layout.popup_eat_type, null);
        popEatTypeLv = (ListView) popupView.findViewById(R.id.section_list);
        eatTypeAdapter = new EatTypeAdapter(aty);
        eatTypeAdapter.setData(sectionEatTypeList);
        popEatTypeLv.setAdapter(eatTypeAdapter);
        popEatTypeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eatTypeWindow.dismiss();
                Sections sections = (Sections) eatTypeAdapter.getItem(position);
                if (sections != null) {
                    switch (sections.getSectionsStyle()) {
                        //堂食
                        case Constant.DishMenu.EAT_IN:
                            if (!posInfo.getOrderType().equals("EAT_IN")) {
                                posInfo.setOrderType("EAT_IN");
                                Cart.switchEATINPrice();
                            }
                            break;
                        //外卖
                        case Constant.DishMenu.SALE_OUT:
                            if (!posInfo.getOrderType().equals("SALE_OUT")) {
                                posInfo.setOrderType("SALE_OUT");
                                Cart.switchWaiMaiPrice();
                            }
                            break;
                        //外带
                        case Constant.DishMenu.TAKE_OUT:
                            if (!posInfo.getOrderType().equals("TAKE_OUT")) {
                                posInfo.setOrderType("TAKE_OUT");
                                Cart.switchEATINPrice();
                            }
                            break;
                    }
                    tvEatType.setText(sections.getName());
                    eatTypeAdapter.setSelectPosition(position);
                }
            }
        });

        int width_p = ScreenUtil.getScreenSize(aty)[0] / 6;
        eatTypeWindow = new PopupWindow(popupView);
        //        eatTypeWindow.setWidth(widtht*2-10);
        eatTypeWindow.setWidth(width_p / 3 + 15);
        eatTypeWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        eatTypeWindow.setBackgroundDrawable(ContextCompat.getDrawable(aty, R.drawable.enity_gray_drawable));
        eatTypeWindow.setFocusable(true);
        eatTypeWindow.setOutsideTouchable(true);
        eatTypeWindow.update();

        eatTypeWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                relMask.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void changeTareWeight(float tareWeight) {

    }

    @Override
    public void changeNetWeight(float netWeight) {

    }

    @Override
    public void changeGrossWeight(float netWeight) {

    }

    @Override
    public void setPointnumber(int pointnumber) {

    }

    @Override
    public void signalLampState(boolean isStable) {

    }

    @Override
    public void setTareNumSuccess(boolean state) {

    }

    @Override
    public void setZeroNumSuccess(boolean state) {

    }

}
