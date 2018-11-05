package cn.acewill.pos.next.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliGoodsItem;
import com.acewill.paylibrary.tencent.WXPay;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.model.Customer;
import cn.acewill.pos.next.model.OrderStatus;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishItem;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.payment.PayType;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.swipemenulistview.SwipeMenu;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuCreator;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuItem;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuListView;
import cn.acewill.pos.next.ui.adapter.DishKindsNewAdp;
import cn.acewill.pos.next.ui.adapter.DishPagerNewAdp;
import cn.acewill.pos.next.ui.adapter.OrderNewAdp;
import cn.acewill.pos.next.ui.adapter.PayTypeMoneyAdp;
import cn.acewill.pos.next.ui.adapter.RetreatOrderAdp;
import cn.acewill.pos.next.ui.adapter.SelectPayTypeNewAdp;
import cn.acewill.pos.next.utils.CheckOutUtil;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DishMenuUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.sunmi.SunmiSecondScreen;
import cn.acewill.pos.next.widget.NoPreloadViewPager;
import cn.acewill.pos.next.widget.ProgressDialogF;

import static cn.acewill.pos.R.id.lv_retreat;
import static cn.acewill.pos.next.common.DishDataController.dishKindList;


/**
 * 点菜下单结账界面
 * Created by DHH on 2016/6/12.
 */
public class OrderDishAty extends BaseActivity {

    @BindView( R.id.rel_bottom )
    RelativeLayout relBottom;
    @BindView( R.id.tv_main_title )
    TextView tvMainTitle;
    @BindView( R.id.rel_top )
    RelativeLayout relTop;
    @BindView( R.id.vp_dish )
    NoPreloadViewPager vpDish;
    @BindView( R.id.gv_dish_kind )
    GridView gvDishKind;
    @BindView( R.id.tv_price )
    TextView tvPrice;
    @BindView( R.id.tv_price_prefer )
    TextView tvPricePrefer;
    @BindView( R.id.tv_cost_prefer )
    TextView tvCostPrefer;
    @BindView( R.id.rel_right_bottom )
    RelativeLayout relRightBottom;
    @BindView( R.id.tv_table_info )
    TextView tvTableInfo;
    @BindView( R.id.lv_order )
    SwipeMenuListView lvOrder;
    @BindView( R.id.paytype_gv )
    RecyclerView paytypeGv;
    @BindView( R.id.tv_main )
    TextView tvMain;
    @BindView( R.id.tv_scan )
    TextView tvScan;
    @BindView( R.id.tv_search )
    TextView tvSearch;
    @BindView( R.id.tv_checkout )
    TextView tvCheckout;
    @BindView( R.id.tv_checkOrder )
    TextView tvCheckOrder;
    @BindView( R.id.tv_printe )
    TextView tvPrinte;
    @BindView( R.id.tv_up )
    TextView tvUp;
    @BindView( R.id.tv_down )
    TextView tvDown;
    @BindView( R.id.lin_do )
    LinearLayout linDo;
    @BindView( lv_retreat )
    ListView lvRetreat;

    private static int CHECKOUT = 110;
    private Intent intent;

    public RetreatOrderAdp retreatOrderAdp;
    private SelectPayTypeNewAdp selectPayTypeAdp;
    private PayTypeMoneyAdp payTypeMoneyAdp;
    private DishKindsNewAdp dishKindsAdp;
    private DishPagerNewAdp dishPagerAdp;
    public OrderNewAdp orderAdp;

    private ProgressDialogF progressDialogF;
    private List<DishType> dishKind;//菜品分类
    private Cart cart;
    private PosInfo posInfo;
    private CheckOutUtil checkOutUtil;
    private String tableName;//送餐桌台号

    /**
     * 滑动page的下标位置
     */
    private int currentPosition = 0;
    private final int PAY_AFTER = 1;//下单前支付标识
    private final int PAY_BEFORE = 2;//下单后支付标识
    private int tableStyle;  //桌台状态的类型
    public Map<String, Integer> dishSelectMap = new HashMap<String, Integer>();//已点分类菜品项目份数

    private int pay_channel = PayReqModel.PTID_SSS_WEIXIN;// 支付宝：PayReqModel.PTID_SSS_ALI，微信:PTID_SSS_WEIXIN
    private List<AliGoodsItem> aliGoodsItem = new ArrayList<AliGoodsItem>();//支付宝参数
    private List<PayType> payTypeList = new ArrayList<>();//用户已选的支付信息

    /**
     * 选择的position的值
     */
    int selected;
    /**
     * 是否有向上的动画
     */
    boolean isUp = false;
    /**
     * 是否有向下的动画
     */
    boolean isDown = false;

    private int reverseCheckOutFlag = 0;//反结账标识

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_order_dish);
        ButterKnife.bind(this);
        initData();
        setLvRetreatGone();
        initTableInfo();
        initCheckOut();
        //        initLindoWidth();
        cleanCart();
        logicCheck();
    }

    /**
     * 判断是先下单还是先结账
     */
    private void logicCheck() {
        if (ToolsUtils.logicIsTable()) {
            tvCheckOrder.setVisibility(View.VISIBLE);
            tvCheckout.setVisibility(View.GONE);
            relBottom.setVisibility(View.GONE);
        } else {
            tvCheckOrder.setVisibility(View.GONE);
            tvCheckout.setVisibility(View.VISIBLE);
            relBottom.setVisibility(View.VISIBLE);
        }
    }

    //设置上一页 下一页的宽度 高度
    private void initLindoWidth() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myApplication.getScreenWidth() / 9, 120);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        linDo.setLayoutParams(params);
    }

    private void dishStock(Payment payment, final String tableName) {
        final CheckOutUtil checUtil = new CheckOutUtil(context, payment);
        checUtil.getDishStock(Cart.getDishItemList(), new DishCheckCallback() {
            @Override
            public void haveStock() {
                checUtil.swichPay(tableName);
            }

            @Override
            public void noStock(List dataList) {
                refreshDish(dataList, Cart.getDishItemList());
            }
        });
    }

    /**
     * 获取支付方式
     */
    private void initCheckOut() {
        selectPayTypeAdp = new SelectPayTypeNewAdp(context);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        paytypeGv.setLayoutManager(linearLayoutManager);
        paytypeGv.setAdapter(selectPayTypeAdp);

        getPayType();

        selectPayTypeAdp.setOnItemClickListener(new SelectPayTypeNewAdp.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                selectPayTypeAdp.setCurrent_select(position);
                final Payment payment = selectPayTypeAdp.getItem(position);
                ToolsUtils.writeUserOperationRecords("选择了" + payment.getName() + "支付方式");

                if (payment.getId() == -1 || payment.getId() == -2 || payment.getId() == -3) {
                    selectPayTypeAdp.setCurrent_selectEatStyle(payment.getId());

                    if (payment.getId() == -1) {
                        posInfo.setOrderType("EAT_IN");
                        return;
                    } else if (payment.getId() == -2) {
                        posInfo.setOrderType("SALE_OUT");
                        if (ToolsUtils.getCustomerInfoForWaimai()) {
                            DialogUtil.takeOutDialog(context, new DialogTCallback() {
                                @Override
                                public void onConfirm(Object o) {
                                    ToolsUtils.hideInputManager(context, tvCheckout);
                                    if (o != null) {
                                        Customer customer = (Customer) o;
                                        posInfo.setCustomer(customer);
                                    }
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                        }
                        return;
                    } else if (payment.getId() == -3) {
                        posInfo.setOrderType("TAKE_OUT");
                        return;
                    }
                }
                if (cart.getDishItemList().size() > 0) {
                    if (StoreInfor.cardNumberMode) {
                        DialogUtil.initTableInfo(context, new DialogTCallback() {
                            @Override
                            public void onConfirm(Object o) {
                                Order order = (Order) o;
                                if (order != null) {
                                    if (!TextUtils.isEmpty(order.getTableNames())) {
                                        tableName = order.getTableNames();
                                        dishStock(payment, tableName);
                                    }
                                }
                            }

                            @Override
                            public void onCancle() {
                            }
                        });
                    } else {
                        dishStock(payment, null);
                    }
                } else {
                    showToast(ToolsUtils.returnXMLStr("please_click_dish"));
                }


            }
        });

    }

    private void initData() {
        myApplication.addPage(OrderDishAty.this);
        intent = new Intent();
        posInfo = PosInfo.getInstance();
        posInfo.setOrderType("EAT_IN");
        checkOutUtil = new CheckOutUtil(this);
        tvMainTitle.setText("智慧餐饮");
        tvTableInfo.setText("正在点菜");

        cart = Cart.getInstance();
        if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
            cart.clear();
        }
        progressDialogF = new ProgressDialogF(context);
        dishKindsAdp = new DishKindsNewAdp(context);
        dishPagerAdp = new DishPagerNewAdp(context);
        vpDish.setOnPageChangeListener(new NoPreloadViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                dishPagerAdp.setDishCount(cart.dishCountsList);
                dishKindsAdp.setSelect(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        orderAdp = new OrderNewAdp(context);
        lvOrder.setAdapter(orderAdp);

        retreatOrderAdp = new RetreatOrderAdp(this);
        lvRetreat.setAdapter(retreatOrderAdp);

        //左滑删除
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(context);
                item1.setBackground(new ColorDrawable(Color.rgb(0xfe, 0x00, 0x00)));
                item1.setWidth(dp2px(90));
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
                    //                    orderAdp.dishInfoDialog(dish, position);
                    //                    if (dish.subItemList != null && dish.subItemList.size() > 0) {
                    //                        //设置要展开套餐项的下标
                    //                        orderAdp.setIsShowDishItemPosition(position);
                    //                    } else {
                    dishMenuLogic(dish, null, position);
                    //                    }
                }
            }
        });

        //购物车发现变化时的监听
        cart.addListener(new Cart.ChangeListener() {

            @Override
            public void contentChanged() {
                if (dishPagerAdp != null) {
                    dishPagerAdp.refresh();
                    orderAdp.setData(cart.getDishItemList());
                    //                    System.out.println(ToolsUtils.getPrinterSth(cart.dishItemList));
                    tvPrice.setText("¥" + String.format("%.2f ", cart.getPriceSum()));
                    tvPricePrefer.setText("¥" + String.format("%.2f ", cart.getCost() - cart.getPriceSum()));
                    tvCostPrefer.setText("¥" + String.format("%.2f ", cart.getCost()));

//                    setDishSelectMap();

                    //显示客显
                    if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_14) {
                        SunmiSecondScreen.getInstance(context).showExcelData(cart.getOrderItem(null, cart.getDishItemList()));
                    }
                }
            }
        });

        //适配菜品分类数据
        gvDishKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                vpDish.setCurrentItem(currentPosition);
                dishPagerAdp.setDishCount(cart.dishCountsList);
                dishKindsAdp.setSelect(currentPosition);
            }
        });
    }

    private void dishMenuLogic(final Dish dish, final OrderItem orderItem, final int position) {
        List<DishItem> dishItemList = new ArrayList<DishItem>();
        DishItem dishItem1 = new DishItem("菜品数量", Constant.DishMenu.DISH_COUNT);
        DishItem dishItem2 = new DishItem("菜品定制项", Constant.DishMenu.DISH_SKU);
        DishItem dishItem3 = new DishItem("打折方案", Constant.DishMenu.DISH_DISCOUNT);
        DishItem dishItem7 = new DishItem("打包费用", Constant.DishMenu.TAKEOUT_DISH);
        DishItem dishItem8 = new DishItem("菜品规格", Constant.DishMenu.SPECIFICATIONS_DISH);
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

        DialogUtil.creatDishMenu(context, dishName, dishItemList, 146, new DialogTCallback() {
            @Override
            public void onConfirm(Object o) {
                DishItem dishitem = (DishItem) o;
                if (dishitem != null) {
                    switch (dishitem.itemId) {
                        //菜品数量
                        case Constant.DishMenu.DISH_COUNT:
                            ToolsUtils.writeUserOperationRecords("菜品数量选项");
                            DishMenuUtil.setDishCountDialog(context, dish, position);
                            break;

                        //菜品定制项
                        case Constant.DishMenu.DISH_SKU:
                            ToolsUtils.writeUserOperationRecords("菜品定制项选项");
                            if (dish != null) {
                                DishMenuUtil.setDishSkuDialog(context, dish, position);
                            }
                            break;

                        //打折方案
                        case Constant.DishMenu.DISH_DISCOUNT:
                            ToolsUtils.writeUserOperationRecords("打折方案选项");
                            if (dish != null) {
                                DishMenuUtil.setDishDisCountDialog(context, dish, position);
                            }
                            break;
                        //打包费用
                        case Constant.DishMenu.TAKEOUT_DISH:
                            ToolsUtils.writeUserOperationRecords("打包费用选项");
                            DishMenuUtil.setDishTakeOutDialog(context, dish, position);
                            break;
                        //菜品规格
                        case Constant.DishMenu.SPECIFICATIONS_DISH:
                            ToolsUtils.writeUserOperationRecords("菜品规格选项");
                            if (dish != null) {
                                DishMenuUtil.setDishSpecificationsDialog(context, dish, position);
                            }
                            break;
                    }
                }
            }

            @Override
            public void onCancle() {

            }
        });
    }

    private void loadData() {
        if (DishDataController.dishKindList != null && DishDataController.dishKindList.size() > 0) {
            if (DishDataController.menuData != null && DishDataController.menuData.size() > 0) {
                initDishAdapter();
            } else {
                getDishInfo();
            }

        } else {
            getKindInfo();
        }
    }

    private void initDishAdapter() {
        if (dishKindsAdp != null && gvDishKind != null && vpDish != null) {
            dishKindsAdp.setData(dishKindList);
            gvDishKind.setAdapter(dishKindsAdp);
            vpDish.setAdapter(dishPagerAdp);
            vpDish.setCurrentItem(currentPosition);
            dishKindsAdp.setSelect(currentPosition);
        }
    }

    /**
     * 获取菜品分类数据
     */
    private void getKindInfo() {
        progressDialogF.showLoading("");
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
                progressDialogF.disLoading();
                if (result != null && result.size() > 0) {
                    dishKind = result;
                    dishKindList = dishKind;
                    getDishInfo();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialogF.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_error")+"," + e.getMessage());
                Log.i("获取菜品分类为空", "");
            }
        });
    }

    /**
     * 得到菜品数据  dishList
     */
    private void getDishInfo() {
        progressDialogF.showLoading("");
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
                if (result != null && result.size() > 0) {
                    DishDataController.setDishData(result);
                    initDishAdapter();
                    progressDialogF.disLoading();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialogF.disLoading();
                showToast(e.getMessage());
                Log.i("获取菜品为空", e.getMessage());
            }
        });
    }

    //获取支付方式列表
    private void getPayType() {
        try {
            if (StoreInfor.getPaymentList() != null && StoreInfor.getPaymentList().size() > 0) {
                List<Payment> payments = new ArrayList<>();
                for (Payment payment : StoreInfor.getPaymentList()) {
                    payments.add(payment);
                }
                initAliAndWx(payments);
                Payment pay1 = new Payment(-1, "堂食");
                Payment pay2 = new Payment(-2, "外卖");
                Payment pay3 = new Payment(-3, "外带");
                payments.add(pay1);
                payments.add(pay2);
                payments.add(pay3);
                selectPayTypeAdp.setDatas(payments);
                return;
            }
            DishService dishService = DishService.getInstance();
            dishService.getPaytypeList(new ResultCallback<List<Payment>>() {

                @Override
                public void onResult(List<Payment> result) {
                    if (result != null && result.size() > 0) {
                        initAliAndWx(result);
                        Payment pay1 = new Payment(-1, "堂食");
                        Payment pay2 = new Payment(-2, "外卖");
                        Payment pay3 = new Payment(-3, "外带");
                        result.add(pay1);
                        result.add(pay2);
                        result.add(pay3);
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


    private void initTableInfo() {
        Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
        if (tableOrder != null && tableOrder.getTableStyle() > 0) {
            tableStyle = tableOrder.getTableStyle();
        }
        long tableId = getIntent().getLongExtra("TableId", 0);
        if (tableOrder != null && tableOrder.getTableId() > 0) {
            tableOrderInfo(tableOrder);
        } else if (tableId != 0 && tableId > 0) {
            tableOrderInfo(null);
        }
        if (tableStyle > 0 && tableStyle == Constant.JsToAndroid.JS_A_SPLITTABLE) {
            if (lvRetreat != null) {
                lvRetreat.setVisibility(View.GONE);
            }
        }
    }

    private long orderId;
    private Order tableOrder;

    public void tableOrderInfo(Order tableOrderTemp) {
        cleanCart();
        if (tableOrderTemp != null) {
            orderId = tableOrderTemp.getId();
            tableOrder = tableOrderTemp;
            List<OrderItem> orderItemList = tableOrderTemp.getItemList();
            setRetreatOrderInfo(orderItemList);
        }
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
            orderAdp.setData(cart.getDishItemList());
        }
        if (lvOrder != null) {
            lvOrder.setAdapter(orderAdp);
        }
        posInfo.setOrderType("EAT_IN");
        if (selectPayTypeAdp != null) {
            selectPayTypeAdp.setCurrent_selectEatStyle(-1);
            selectPayTypeAdp.notifyDataSetChanged();
        }
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setCustomer(null);
        //        tvPrice.setText("¥" + String.format("%.2f ", cart.getPriceSum()));
        cleanDishSelectMap();
        DishDataController.cleanDishMarkMap();
        if (dishKindsAdp != null) {
            dishKindsAdp.setMap(dishSelectMap);
        }
        getDishCounts();
        orderId = 0;
        tvPrice.setText("¥0.00");
        tvPricePrefer.setText("¥0.00");
        tvCostPrefer.setText("¥0.00");
        DishOptionController.cleanCartDishMap();//下单后清除菜品的定制项缓存
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

//    public void setDishSelectMap() {
//        Iterator iter = DishDataController.getDishMarkMap().keySet().iterator();
//        while (iter.hasNext()) {
//            Object key = iter.next();
//            int num = 0;
//            int kindDishQuantity = DishDataController.getDishMarkMap().get(key);
//            dishSelectMap.put((String) key, kindDishQuantity);
//            //            Map<String, Dish> dishMap = DishDataController.getDishMarkMap().get(key);
//            //            Iterator dishIter = dishMap.keySet().iterator();
//            //            while (dishIter.hasNext()) {
//            //                Object dishKey = dishIter.next();
//            //                Dish dish = dishMap.get(dishKey);
//            //                num += dish.quantity;
//            //                dishSelectMap.put((String) key, num);
//            //            }
//            //            if (dishMap.size() <= 0) {
//            //                dishSelectMap.put((String) key, num);
//            //            }
//        }
//        dishKindsAdp.setMap(dishSelectMap);
//    }

    public void cleanDishSelectMap() {
        if (dishSelectMap != null && dishSelectMap.size() > 0) {
            dishSelectMap.clear();
        }
    }

    private void appTableOrderDish(Order order, List<Dish> dishs) {
        try {
            TableService tableService = TableService.getInstance();
            final List<OrderItem> orderItem = cart.getTableOrderItem(dishs);
            if (orderItem != null && orderItem.size() > 0) {
                Order newOrder = new Order();
                newOrder.setId(orderId);
                tableService.appendDish(newOrder, orderItem, new ResultCallback<Order>() {
                    @Override
                    public void onResult(Order result) {
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                        dishPagerAdp.refresh();
                        myApplication.ShowToast("下单成功!");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, tableName));//kds下单
                        cleanCart();
                        progressDialogF.disLoading();
                        OrderDishAty.this.finish();
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        dishPagerAdp.notifyDataSetChanged();
                        myApplication.ShowToast(ToolsUtils.returnXMLStr("orders_failed")+"!" + e.getMessage());
                        progressDialogF.disLoading();
                        DialogUtil.ordinaryDialog(context, "加菜出错", "加菜出错," + e.getMessage() + " 是否确定退出?", new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                OrderDishAty.this.finish();
                            }

                            @Override
                            public void onCancle() {
                                OrderDishAty.this.finish();
                            }
                        });
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void createOrder(List<Dish> dishs, OrderStatus orderStatus) {
        try {
            OrderService orderService = OrderService.getInstance();
            final Order dishOrder = cart.getOrderItem(dishs, null, true, orderStatus);
            if (dishOrder != null) {
                orderService.createOrder(dishOrder, new ResultCallback<Order>() {
                    @Override
                    public void onResult(Order result) {
                        Log.i("下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
                        if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                        }
                        System.out.println("==checkDishCount==" + orderId);
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result.getTableNames()));//kds下单
                        dishPagerAdp.refresh();
                        myApplication.ShowToast("下单成功!");
                        cleanCart();
                        progressDialogF.disLoading();
                        OrderDishAty.this.finish();
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        Log.i("下单失败", "orderId ==" + orderId + "===" + e.getMessage());
                        dishPagerAdp.notifyDataSetChanged();
                        myApplication.ShowToast(ToolsUtils.returnXMLStr("orders_failed")+"!" + e.getMessage());
                        progressDialogF.disLoading();
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("下单失败", "orderId ==" + orderId + "===" + e.getMessage());
        }
    }

    public void refreshDish(List<DishCount> result, List<Dish> dishs) {
        //刷新菜品数据,显示沽清
        getDishInfo();
        if (orderAdp != null) {
            orderAdp.setCheckDishCount(result);
        }
        String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
        showToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough")+"\n\n" + names + "。\n\n"+ToolsUtils.returnXMLStr("please_re_order"));
        orderAdp.checkDishCount.clear();
        orderAdp.notifyDataSetChanged();
        progressDialogF.disLoading();
        Log.i("以下菜品份数不足:", names + "====<<");
    }


    public void createOrder(final OrderStatus orderStatus) {
        progressDialogF.showLoading("");
        if (cart.getDishItemList().size() > 0) {
            checkOutUtil.getDishStock(cart.getDishItemList(), new DishCheckCallback() {
                @Override
                public void haveStock() {
                    if (orderId > 0 && tableStyle != Constant.JsToAndroid.JS_A_SPLITTABLE) {
                        appTableOrderDish(tableOrder, cart.getDishItemList());
                    } else {
                        createOrder(cart.getDishItemList(), orderStatus);
                    }
                }

                @Override
                public void noStock(List dataList) {
                    refreshDish(dataList, cart.getDishItemList());
                }

            });
        } else {
            myApplication.ShowToast("先点菜吧!");
            progressDialogF.disLoading();
        }
    }


    /**
     * 挂单
     */
    private void hangOrder() {
        ToolsUtils.writeUserOperationRecords("主页返回");
        //说明是要加菜
        if (orderId > 0 && tableStyle != Constant.JsToAndroid.JS_A_SPLITTABLE) {
            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
            OrderDishAty.this.finish();
        }
        //开新单
        else {
            if (Cart.getDishItemList() != null && Cart.getDishItemList().size() > 0) {
                ToolsUtils.writeUserOperationRecords("主页返回挂单");
                DialogUtil.ordinaryDialog(context, "提示", "当前有未下单的菜品,是否要挂单?", new DialogCallback() {
                    @Override
                    public void onConfirm() {
                        createOrder(OrderStatus.PENDING);
                    }

                    @Override
                    public void onCancle() {
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                        OrderDishAty.this.finish();
                    }
                });
            } else {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                OrderDishAty.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SunmiSecondScreen.getInstance(context).showImageListScreen();
    }

    /**
     * 跳转到结账界面
     */
    private void jumoCheckout() {
        checkOutUtil.getDishStock(cart.getDishItemList(), new DishCheckCallback() {
            @Override
            public void haveStock() {
                Intent intent = new Intent(OrderDishAty.this, CheckOutNewAty.class);
                intent.putExtra("source", Constant.EventState.SOURCE_CREAT_ORDER);
                intent.putExtra("tableOrder", (Serializable) tableOrder);
                intent.putExtra("tableName", tableName);
                startActivityForResult(intent, CHECKOUT);
            }

            @Override
            public void noStock(List dataList) {
                refreshDish(dataList, cart.getDishItemList());
            }
        });
    }

    @OnClick( {R.id.tv_main, R.id.tv_scan, R.id.tv_search, R.id.tv_checkout, R.id.tv_checkOrder, R.id.tv_printe, R.id.tv_up, R.id.tv_down} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_main:
                ToolsUtils.writeUserOperationRecords("主页");
                hangOrder();
                //                OrderDishAty.this.finish();
                break;
            case R.id.tv_scan:
                break;
            case R.id.tv_search:
                ToolsUtils.writeUserOperationRecords("搜索按钮");
                intent.setClass(OrderDishAty.this, SearchAty.class);
                startActivity(intent);
                break;
            //结账
            case R.id.tv_checkout:
                ToolsUtils.writeUserOperationRecords("结账按钮");
                if (cart.getDishItemList().size() > 0) {
                    if (StoreInfor.cardNumberMode)//送餐模式
                    {
                        DialogUtil.initTableInfo(context, new DialogTCallback() {
                            @Override
                            public void onConfirm(Object o) {
                                Order order = (Order) o;
                                if (order != null) {
                                    if (!TextUtils.isEmpty(order.getTableNames())) {
                                        tableName = order.getTableNames();
                                        jumoCheckout();
                                    }
                                }
                            }

                            @Override
                            public void onCancle() {
                            }
                        });
                    } else {
                        jumoCheckout();
                    }
                } else {
                    myApplication.ShowToast("先点菜吧!");
                }
                break;
            //下单
            case R.id.tv_checkOrder:
                ToolsUtils.writeUserOperationRecords("下单按钮");
                if (cart.getDishItemList().size() > 0) {
                    createOrder(OrderStatus.NORMAL);
                } else {
                    myApplication.ShowToast("先点菜吧!");
                }
                break;
            case R.id.tv_printe:
                break;
            case R.id.tv_up:
                KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_DOWN, 1);
                gvDishKind.onKeyDown(KeyEvent.KEYCODE_DPAD_UP, event2);
                break;
            case R.id.tv_down:
                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, 1);
                gvDishKind.onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, event);
                break;
        }
    }

    private int itemNum = 12;
    private int itemRowNum = itemNum / 2;

    // 是否在某一页的完整最后一行
    private boolean isAllPageUp() {
        // 求出一共有多少页，这里不需要-1，因为向上滑动需要最后的那一行
        int dishKindsNum = DishDataController.dishKindList.size();
        int rawNum = dishKindsNum / itemNum + 1;
        for (int i = 1; i <= rawNum; i++) {
            if (selected == itemNum * i) {
                return true;
            }
            if (selected == itemNum * i + 1) {
                return true;
            }
            if (selected == itemNum * i + 2) {
                return true;
            }

        }
        return false;
    }

    // 如果在某一页的完整最后一行，并且点击向下按钮
    private boolean isAllPageDown() {
        // 求出一共有多少页-1，因为向下滑动不需要最后的那一行
        int dishKindsNum = DishDataController.dishKindList.size();
        int rawNum = dishKindsNum / itemNum;
        for (int i = 1; i <= rawNum; i++) {
            if (selected == itemRowNum + itemNum * (i - 1)) {
                return true;
            }
            if (selected == itemRowNum + itemNum * (i - 1) + 1) {
                return true;
            }
            if (selected == itemRowNum + itemNum * (i - 1) + 2) {
                return true;
            }
        }
        return false;
    }

    //监听back键,弹出退出提示dialog
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            hangOrder();//是否要挂单
            //            if (ToolsUtils.logicIsTable()) {
            //            } else {
            //                noTableBack();//判断购物车有菜是否要清空购物车
            //            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void getDishCounts() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
                @Override
                public void onResult(List<DishCount> result) {
                    progressDialogF.disLoading();
                    if (result != null && result.size() > 0) {
                        cart.dishCountsList = result;
                        dishPagerAdp.setDishCount(result);
                    } else {
                        MyApplication.getInstance().ShowToast("获取菜品沽清状态失败!");
                    }
                    loadData();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialogF.disLoading();
                    loadData();
                    Log.i("获取菜品沽清状态失败", e.getMessage());
                    MyApplication.getInstance().ShowToast("获取菜品沽清状态失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("获取菜品沽清状态失败", e.getMessage());
            MyApplication.getInstance().ShowToast("获取菜品沽清状态失败!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDishCounts();
    }

    public void setLvRetreatGone() {
        if (lvRetreat != null) {
            lvRetreat.setVisibility(View.GONE);
        }
    }

    public void setRetreatOrderInfo(List<OrderItem> orderItemList) {
        if (orderItemList != null && orderItemList.size() > 0) {
            if (lvRetreat != null) {
                lvRetreat.setVisibility(View.VISIBLE);
                retreatOrderAdp.setData(orderItemList);
            }
        } else {
            if (lvRetreat != null) {
                lvRetreat.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECKOUT) {
            if (resultCode == RESULT_OK) {
                cleanData();
            }
        }
    }

    public void cleanData() {
        cleanCart();
        initData();
    }

    /**
     * 当没有桌台时  购物车有菜判断是否要清空购物车逻辑
     */
    private void noTableBack() {
        if (Cart.getDishItemList() != null && Cart.getDishItemList().size() > 0) {
            DialogUtil.ordinaryDialog(context, "提示", "当前有未下单的菜品,是否清空菜品退出?", new DialogCallback() {
                @Override
                public void onConfirm() {
                    cleanCart();
                    cleanDishSelectMap();
                    DishDataController.cleanDishMarkMap();
                    dishKindsAdp.setMap(dishSelectMap);
                    OrderDishAty.this.finish();
                }

                @Override
                public void onCancle() {

                }
            });
        } else {
            OrderDishAty.this.finish();
        }
    }

}
