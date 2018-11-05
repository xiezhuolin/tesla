package cn.acewill.pos.next.ui.activity.newPos;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acewill.paylibrary.PayReqModel;
import com.acewill.paylibrary.alipay.config.AlipayConfig;
import com.acewill.paylibrary.epay.AliGoodsItem;
import com.acewill.paylibrary.tencent.WXPay;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TableDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.interfices.PermissionCallback;
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
import cn.acewill.pos.next.model.order.OrderSingleReason;
import cn.acewill.pos.next.model.payment.PayType;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.table.Sections;
import cn.acewill.pos.next.model.table.TableData;
import cn.acewill.pos.next.model.table.TableInfor;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.service.TradeService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.swipemenulistview.SwipeMenu;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuCreator;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuItem;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuListView;
import cn.acewill.pos.next.ui.activity.CheckOutNewAty;
import cn.acewill.pos.next.ui.activity.SearchAty;
import cn.acewill.pos.next.ui.adapter.DishKindsNewAdp;
import cn.acewill.pos.next.ui.adapter.DishPagerNewAdp;
import cn.acewill.pos.next.ui.adapter.OrderNewAdp;
import cn.acewill.pos.next.ui.adapter.OrderSingleReasonAdp;
import cn.acewill.pos.next.ui.adapter.PayTypeMoneyAdp;
import cn.acewill.pos.next.ui.adapter.RetreatOrderAdp;
import cn.acewill.pos.next.ui.adapter.SectionAdapter;
import cn.acewill.pos.next.ui.adapter.SelectPayTypeNewAdp;
import cn.acewill.pos.next.utils.CheckOutUtil;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DishMenuUtil;
import cn.acewill.pos.next.utils.ScreenUtil;
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
public class TableOrderNewAty extends BaseActivity {

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
    @BindView( R.id.tv_up )
    TextView tvUp;
    @BindView( R.id.tv_down )
    TextView tvDown;
    @BindView( R.id.lin_do )
    LinearLayout linDo;
    @BindView( lv_retreat )
    ListView lvRetreat;
    @BindView( R.id.tv_select )
    TextView tvSelect;
    @BindView( R.id.rel_right )
    RelativeLayout relRight;

    private static int CHECKOUT = 110;


    private Intent intent;

    public RetreatOrderAdp retreatOrderAdp;
    private SelectPayTypeNewAdp selectPayTypeAdp;
    private PayTypeMoneyAdp payTypeMoneyAdp;
    private DishKindsNewAdp dishKindsAdp;
    private DishPagerNewAdp dishPagerAdp;
    public OrderNewAdp orderAdp;

    private List<DishType> dishKind;//菜品分类
    private Cart cart;
    private PosInfo posInfo;
    private CheckOutUtil checkOutUtil;

    private final int PAY_AFTER = 1;//下单前支付标识
    private final int PAY_BEFORE = 2;//下单后支付标识
    private int tableStyle;  //桌台状态的类型
    public Map<String, Integer> dishSelectMap = new HashMap<String, Integer>();//已点分类菜品项目份数

    private int pay_channel = PayReqModel.PTID_SSS_WEIXIN;// 支付宝：PayReqModel.PTID_SSS_ALI，微信:PTID_SSS_WEIXIN
    private List<AliGoodsItem> aliGoodsItem = new ArrayList<AliGoodsItem>();//支付宝参数
    private List<PayType> payTypeList = new ArrayList<>();//用户已选的支付信息

    private ProgressDialogF progressDialog;

    /**
     * 滑动page的下标位置
     */
    private int currentPosition = 0;
    /**
     * 选择的position的值
     */
    int selected = 0;
    /**
     * 是否有向上的动画
     */
    boolean isUp = false;
    /**
     * 是否有向下的动画
     */
    boolean isDown = false;
    private int columns = 4;
    private int rows = 2;
    private int itemNums = columns * rows;

    private Long sectionId = -1L;//-1表示所有区域
    private PopupWindow window;
    private PopupWindow orderItemWindow;
    private ListView popLv;
    private ListView popOrderItemLv;
    private SectionAdapter sectionAdapter;
    private SectionAdapter orderItemSectionAdp;
    private List<TableInfor> allTableInfor = new ArrayList<>();

    private int reverseCheckOutFlag = 0;//反结账标识

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_table_order_new);
        ButterKnife.bind(this);
        myApplication.addPage(TableOrderNewAty.this);
        initData();
        setLvRetreatGone();
        initTableInfo();
        initCheckOut();
        //        initLindoWidth();
        //        initKindsGv();
        cleanCart();
        //TODO 初始化PopUpWindow
        initPopupWindow();
        initPopOrderItemWindow();
    }


    //设置上一页 下一页的宽度 高度
    private void initLindoWidth() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(myApplication.getScreenWidth() / 9, 120);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        linDo.setLayoutParams(params);
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

                Payment payment = selectPayTypeAdp.getItem(position);

                if (payment.getId() == -1 || payment.getId() == -2 || payment.getId() == -3) {
                    selectPayTypeAdp.setCurrent_selectEatStyle(payment.getId());

                    if (payment.getId() == -1) {
                        posInfo.setOrderType("EAT_IN");
                        return;
                    } else if (payment.getId() == -2) {
                        posInfo.setOrderType("SALE_OUT");
                        return;
                    } else if (payment.getId() == -3) {
                        posInfo.setOrderType("TAKE_OUT");
                        return;
                    }
                }
                if (cart.getDishItemList().size() > 0) {
                    final CheckOutUtil checUtil = new CheckOutUtil(context, payment);
                    checUtil.getDishStock(Cart.getDishItemList(), new DishCheckCallback() {
                        @Override
                        public void haveStock() {
                            checUtil.swichPay(null);
                        }

                        @Override
                        public void noStock(List dataList) {
                            refreshDish(dataList, Cart.getDishItemList());
                        }
                    });
                } else {
                    showToast(ToolsUtils.returnXMLStr("please_click_dish"));
                }


            }
        });

    }

    private void initData() {
        myApplication.addPage(TableOrderNewAty.this);
        intent = new Intent();
        progressDialog = new ProgressDialogF(this);
        posInfo = PosInfo.getInstance();
        posInfo.setOrderType("EAT_IN");
        checkOutUtil = new CheckOutUtil(this);
        dishPagerAdp = new DishPagerNewAdp(context);
        dishKindsAdp = new DishKindsNewAdp(context);

        cart = Cart.getInstance();
        if (cart.getDishItemList() != null && cart.getDishItemList().size() > 0) {
            cart.clear();
        }

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
                    Cart.removeItem(dish);
                }
            }
        });
        //购物车中的菜
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Dish dish = (Dish) orderAdp.getItem(position);
                if (dish != null && dish.quantity > 0) {
                    dishMenuLogic(dish, null, position);
//                    if (dish.subItemList != null && dish.subItemList.size() > 0) {
//                        //设置要展开套餐项的下标
//                        orderAdp.setIsShowDishItemPosition(position);
//                    } else {
//                        dishMenuLogic(dish, null, position);
//                    }
                }
            }
        });
        //已点的菜
        //                retreatOrderAdp = new RetreatOrderAdp(this);
        //           lvRetreat.setAdapter(retreatOrderAdp);
        lvRetreat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final OrderItem orderItem = (OrderItem) retreatOrderAdp.getItem(position);
                if (orderItem != null && orderItem.quantity > 0) {
                    orderItem.setDishDiscount(DishDataController.getDishDiscount(Integer.valueOf(String.valueOf(orderItem.getDishId()))));
                    dishMenuLogic(null, orderItem, position);
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
                System.out.println(currentPosition + "==*****====" + selected);
                currentPosition = position;
                vpDish.setCurrentItem(currentPosition);
                dishPagerAdp.setDishCount(cart.dishCountsList);
                dishKindsAdp.setSelect(currentPosition);
            }
        });
    }

    /**
     * 遍历已经下单的菜品
     */
    private void TraverseAlreadyCheckOutDish(Order tableOrder) {
        if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() > 0) {
            if (DishDataController.orderItemList != null && DishDataController.orderItemList.size() > 0) {
                DishDataController.orderItemList.clear();
            } else {
                DishDataController.orderItemList = new CopyOnWriteArrayList<>();
            }
            for (OrderItem item : tableOrder.getItemList()) {
                item.isSelectItem = false;
                DishDataController.orderItemList.add(item);
            }

        }
    }

    //初始化操作pop
    private void initPopOrderItemWindow() {
        List<Sections> sectionList = new ArrayList<>();
        Sections section = new Sections("催菜", Constant.TABLE_RUSH);
        Sections section2 = new Sections("退菜", Constant.TABLE_REFUN);
        sectionList.add(section);
        sectionList.add(section2);
        View popupItemView = this.getLayoutInflater().inflate(R.layout.popup_view, null);
        popOrderItemLv = (ListView) popupItemView.findViewById(R.id.section_list);
        orderItemSectionAdp = new SectionAdapter(context);
        orderItemSectionAdp.setData(sectionList);
        popOrderItemLv.setAdapter(orderItemSectionAdp);
        popOrderItemLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                orderItemWindow.dismiss();
                Sections sections = (Sections) orderItemSectionAdp.getItem(position);

                if (sections != null) {
                    switch (sections.getSectionsStyle()) {
                        case Constant.TABLE_REFUN:
                            ToolsUtils.writeUserOperationRecords("菜品退菜");
                            TraverseAlreadyCheckOutDish(ToolsUtils.cloneTo(tableOrder));
                            DialogUtil.rushDishDialog(context, "退菜", DishDataController.orderItemList, Constant.TABLE_REFUN, new DialogCallback() {
                                @Override
                                public void onConfirm() {
                                    for (OrderItem orderItem : DishDataController.orderItemList) {
                                        if (!orderItem.isSelectItem) {
                                            DishDataController.orderItemList.remove(orderItem);
                                        }
                                    }
                                    if (DishDataController.orderItemList.size() >= 1) {
                                        //判断是否有退菜权限
                                        PowerController.isLogicPower(context, PowerController.REFUND_DISH,new PermissionCallback() {
                                            @Override
                                            public void havePermission() {
                                                refundDishAndRefundOrder(null, DishDataController.orderItemList, Constant.MULTIPLE);
                                            }

                                            @Override
                                            public void withOutPermission() {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                            break;
                        case Constant.TABLE_RUSH:
                            ToolsUtils.writeUserOperationRecords("菜品催菜");
                            if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() > 0) {
                                TraverseAlreadyCheckOutDish(ToolsUtils.cloneTo(tableOrder));
                                DialogUtil.rushDishDialog(context, "催菜", DishDataController.orderItemList, Constant.TABLE_RUSH, new DialogCallback() {
                                    @Override
                                    public void onConfirm() {
                                        for (OrderItem orderItem : DishDataController.orderItemList) {
                                            if (!orderItem.isSelectItem) {
                                                DishDataController.orderItemList.remove(orderItem);
                                            }
                                        }
                                        if (DishDataController.orderItemList.size() >= 1) {
                                            Order expediteOrder = ToolsUtils.cloneTo(tableOrder);
                                            expediteOrder.setItemList(DishDataController.orderItemList);
                                            expediteOrder.setTableStyle(Constant.EventState.PRINTER_RUSH_DISH);
                                            EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, expediteOrder));
                                        }
                                    }

                                    @Override
                                    public void onCancle() {

                                    }
                                });
                            }
                            break;
                    }
                }

            }
        });
        int width_p = ScreenUtil.getScreenSize(context)[0] / 6;
        orderItemWindow = new PopupWindow(popupItemView);
        orderItemWindow.setWidth(width_p);
        orderItemWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        orderItemWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.empty_icon));
        orderItemWindow.setFocusable(true);
        orderItemWindow.setOutsideTouchable(true);
        orderItemWindow.update();
    }

    //初始化顶部弹框
    private void initPopupWindow() {
        List<Sections> sectionList = new ArrayList<>();
        if (orderId > 0 && orderId != 0) {
            Sections section = new Sections("转台", Constant.TABLE_TURN);
            Sections section2 = new Sections("加台", Constant.TABLE_ADD);
            sectionList.add(section);
            sectionList.add(section2);
        }
        Sections section3 = new Sections("清台", Constant.TABLE_CLEAR);
        sectionList.add(section3);

        View popupView = this.getLayoutInflater().inflate(R.layout.popup_view, null);
        popLv = (ListView) popupView.findViewById(R.id.section_list);
        sectionAdapter = new SectionAdapter(context);
        sectionAdapter.setData(sectionList);
        popLv.setAdapter(sectionAdapter);
        popLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                window.dismiss();
                Sections sections = (Sections) sectionAdapter.getItem(position);
                if (sections != null) {
                    switch (sections.getSectionsStyle()) {
                        case Constant.TABLE_TURN:
                            ToolsUtils.writeUserOperationRecords("转台");
                            getTables(Constant.TABLE_TURN);
                            break;
                        case Constant.TABLE_ADD:
                            ToolsUtils.writeUserOperationRecords("加台");
                            getTables(Constant.TABLE_ADD);
                            break;
                        case Constant.TABLE_CLEAR:
                            ToolsUtils.writeUserOperationRecords("清台");
                            if (orderId != 0 && orderId > 0 && tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() <= 0) {
                                closeTableOrder();
                            } else if (orderId <= 0 || "PAYED".equals(tableOrder.getPaymentStatus().toString())) {
                                closeTableOrder();
                            } else if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() >= 1) {
                                showToast("清台失败,桌台正在使用中!");
                            }
                            break;
                    }
                }

            }
        });

        int width_p = ScreenUtil.getScreenSize(context)[0] / 6;
        window = new PopupWindow(popupView);
        window.setWidth(width_p);
        window.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.empty_icon));
        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.update();
    }


    private void closeTableOrder() {
        //调用清台
        DialogUtil.ordinaryDialog(context, "清台", "是否要清台?", new DialogCallback() {
            @Override
            public void onConfirm() {
                refundDishAndRefundOrder(null, DishDataController.orderItemList, Constant.CLOSEORDER);
            }

            @Override
            public void onCancle() {
            }
        });
    }


    private void getTables(final int type) {
        progressDialog.showLoading("");
        try {
            TableService tableService = TableService.getInstance();
            tableService.getTableInfor(sectionId, new ResultCallback<List<TableData>>() {
                @Override
                public void onResult(List<TableData> result) {
                    progressDialog.disLoading();
                    String dialogTitle = "";
                    if (type == Constant.TABLE_ADD) {
                        dialogTitle = "加台";
                    } else if (type == Constant.TABLE_TURN) {
                        dialogTitle = "转台";
                    } else if (type == Constant.TABLE_JOIN) {
                        dialogTitle = "并台";
                    }
                    switch (type) {
                        case Constant.TABLE_TURN:
                        case Constant.TABLE_ADD://获取空桌列表
                            allTableInfor = TableDataController.getEmptyTables(sectionId, result);
                            DialogUtil.turnTableDialog(context, dialogTitle, Constant.TABLE_TURN, allTableInfor, new DialogTCallback() {
                                @Override
                                public void onConfirm(Object o) {
                                    TableInfor tableInfo = (TableInfor) o;
                                    if (tableInfo != null && tableInfo.getId() > 0) {
                                        if (type == Constant.TABLE_TURN) {
                                            turnTable(tableInfo.getId());
                                        } else if (type == Constant.TABLE_ADD) {
                                            addTableDo(tableInfo.getId());
                                        }
                                    }
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                            break;
                        case Constant.TABLE_JOIN://获取除操作桌台外的其他正在使用的桌台
                            allTableInfor = TableDataController.getUseTables(sectionId, tableId, result);
                            DialogUtil.turnTableDialog(context, dialogTitle, Constant.TABLE_JOIN, allTableInfor, new DialogTCallback() {
                                @Override
                                public void onConfirm(Object o) {
                                    TableInfor tableInfo = (TableInfor) o;
                                    if (tableInfo != null && tableInfo.getId() > 0) {
                                        joinTable(tableInfo.getId());
                                    }
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                            break;
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    Log.e("getTableInfor", e.getMessage());
                    showToast(e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    //转台
    private void turnTable(long newTableid) {
        progressDialog.showLoading("正在转台");
        try {
            TableService tableService = TableService.getInstance();
            tableService.turnTable(tableId, newTableid, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    progressDialog.disLoading();
                    showToast("转台成功");
                    finish();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast("转台失败" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    //并台
    private void joinTable(long newTableid) {
        progressDialog.showLoading("正在并台");
        TableService tableService = null;
        try {
            tableService = TableService.getInstance();
            tableService.joinTable(tableId, newTableid, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    progressDialog.disLoading();
                    showToast("并台成功");
                    finish();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast("并台失败" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    //清台
    private void cleanTable(long newTableid) {
        progressDialog.showLoading("正在清台");
        TableService tableService = null;
        try {
            tableService = TableService.getInstance();
            tableService.clearTable(tableId, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    progressDialog.disLoading();
                    showToast("清台成功");
                    finish();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast("清台失败" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭订单
     *
     * @param orderId
     */
    private void closOrder(final int reasonId, final long orderId) {
        try {
            TradeService tradeService = TradeService.getInstance();
            Order order = new Order();
            order.setId(orderId);
            tradeService.closeOrder(reasonId, order, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("清台成功!");
                        TableOrderNewAty.this.finish();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("关闭订单失败," + e.getMessage());
                    Log.i("关闭订单失败,", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("关闭订单失败,", e.getMessage());
        }
    }

    //加台
    private void addTable(long newTableid, final long people) {
        progressDialog.showLoading("正在加台");
        try {
            TableService tableService = TableService.getInstance();
            tableService.addTable(tableId, newTableid, people, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    progressDialog.disLoading();
                    showToast("加台成功");
                    finish();
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast("加台失败" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    /**
     * 加台操作
     */
    private void addTableDo(final long newTableid) {
        DialogUtil.inputDialog(context, "加台", "加台人数", "请输入加台人数", 0, true, false, new DialogEtCallback() {
            @Override
            public void onConfirm(String sth) {
                if (TextUtils.isEmpty(sth)) {
                    sth = "0";
                }
                Long numberOfCustomer = Long.valueOf(sth);//人数
                addTable(newTableid, numberOfCustomer);//开台操作
            }

            @Override
            public void onCancle() {

            }
        });
    }


    private void dishMenuLogic(final Dish dish, final OrderItem orderItem, final int position) {
        List<DishItem> dishItemList = new ArrayList<DishItem>();
        DishItem dishItem1 = new DishItem("菜品数量", Constant.DishMenu.DISH_COUNT);
        DishItem dishItem2 = new DishItem("菜品定制项", Constant.DishMenu.DISH_SKU);
        DishItem dishItem3 = new DishItem("打折方案", Constant.DishMenu.DISH_DISCOUNT);
        DishItem dishItem4 = new DishItem("退菜", Constant.DishMenu.REFUND_DISH);
        //                    DishItem dishItem5 = new DishItem("转菜", Constant.DishMenu.TURN_DISH);
        DishItem dishItem6 = new DishItem("催菜", Constant.DishMenu.EXPEDITE_DISH);
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
        } else {
            //            if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
            //                dishItemList.add(dishItem3);
            //            }
            dishName = orderItem.getDishName();
            if (tableOrder != null) {
                if (orderItem.optionList != null && orderItem.optionList.size() > 0) {
                    dishItemList.add(dishItem2);
                }

                if (orderItem.dishDiscount != null && orderItem.dishDiscount.size() > 0) {
                    dishItemList.add(dishItem3);
                }

                String paymentstate = tableOrder.getPaymentStatus().toString();
                if (paymentstate.equals("NOT_PAYED") && orderItem.quantity > 0) {
                    dishItemList.add(dishItem4);
                }
                //                dishItemList.add(dishItem5);
                dishItemList.add(dishItem6);
            }
        }

        DialogUtil.creatDishMenu(context, dishName, dishItemList, 146,new DialogTCallback() {
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
                            } else if (orderItem != null) {
                                DishMenuUtil.setOrderItemDisCountDialog(context, orderItem, new DialogTCallback() {
                                    @Override
                                    public void onConfirm(Object o) {
                                        OrderItem item = (OrderItem) o;
                                        upDataOrderCost(item.getId(), item.getTempPrice());
                                    }

                                    @Override
                                    public void onCancle() {

                                    }
                                });
                            }
                            break;
                        //退菜
                        case Constant.DishMenu.REFUND_DISH:
                            ToolsUtils.writeUserOperationRecords("退菜选项");
                            //判断是否有退菜权限
                            PowerController.isLogicPower(context, PowerController.REFUND_DISH,new PermissionCallback() {
                                @Override
                                public void havePermission() {
                                    refundDishAndRefundOrder(orderItem, null, Constant.SINGLE);
                                }

                                @Override
                                public void withOutPermission() {

                                }
                            });
                            break;
                        //转菜
                        case Constant.DishMenu.TURN_DISH:
                            ToolsUtils.writeUserOperationRecords("转菜选项");
                            break;
                        //催菜
                        case Constant.DishMenu.EXPEDITE_DISH:
                            ToolsUtils.writeUserOperationRecords("催菜选项");
                            Order expediteOrder = new Order();
                            expediteOrder = ToolsUtils.cloneTo(tableOrder);
                            List<OrderItem> orderItemList = new ArrayList<OrderItem>();
                            orderItem.isPrint = false;
                            orderItemList.add(ToolsUtils.cloneTo(orderItem));
                            expediteOrder.setItemList(orderItemList);
                            expediteOrder.setComment("");//备注
                            expediteOrder.setTableStyle(Constant.EventState.PRINTER_RUSH_DISH);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_RUSH_DISH, expediteOrder));
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

    /**
     * 获取退菜、退菜原因
     */
    private void getSingleReason(final OrderItem orderItem, final List<OrderItem> orderItemList, final int refundType) {
        try {
            progressDialog.showLoading("");
            TableService tableService = TableService.getInstance();
            tableService.getSingleReason(new ResultCallback<List<OrderSingleReason>>() {
                @Override
                public void onResult(List<OrderSingleReason> result) {
                    progressDialog.disLoading();
                    if (result != null && result.size() > 0) {
                        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_rund, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ListView reasonList = (ListView) dialog.findViewById(R.id.reason_list);
                        TextView cancle = (TextView) dialog.findViewById(R.id.cancle);
                        TextView ok = (TextView) dialog.findViewById(R.id.ok);
                        TextView rund_title = (TextView) dialog.findViewById(R.id.rund_title);

                        if (refundType == Constant.CLOSEORDER) {
                            rund_title.setText("请选择清台原因");
                            ok.setText("清台");
                        } else {
                            rund_title.setText("请选择退菜原因");
                            ok.setText("退菜");
                        }

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
                                if (refundType == Constant.CLOSEORDER) {
                                    if (orderId > 0) {
                                        closOrder(adp.getSelectId(), orderId);
                                    } else if (tableId > 0) {
                                        cleanTable(tableId);
                                    }
                                } else if (refundType == Constant.MULTIPLE || refundType == Constant.SINGLE) {
                                    setRefundMode(adp.getSelectId(), orderItem, orderItemList, refundType);
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
                    progressDialog.disLoading();
                    showToast("获取退菜原因失败," + e.getMessage());
                    Log.i("获取退菜原因失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }

    }

    /**
     * 退菜
     *
     * @param orderItem
     */
    private void refundDishAndRefundOrder(final OrderItem orderItem, List<OrderItem> orderItemList, final int refundType) {
        getSingleReason(orderItem, orderItemList, refundType);
    }

    private void setRefundMode(final int reasonId, final OrderItem orderItem, final List<OrderItem> orderItemList, final int refundType) {
        //单个退菜模式
        if (refundType == Constant.SINGLE) {
            DishMenuUtil.setRefundDishDialog(context, orderItem, new DialogEtCallback() {
                @Override
                public void onConfirm(final String sth) {
                    List<OrderItem> orderItemList = new ArrayList<OrderItem>();
                    orderItem.setRejectedQuantity(Integer.valueOf(sth));
                    orderItemList.add(orderItem);
                    retreatDish(reasonId, orderItemList);
                }

                @Override
                public void onCancle() {

                }
            });
        }
        //退多个菜模式
        else if (refundType == Constant.MULTIPLE) {
            if (orderItemList != null && orderItemList.size() > 0) {
                for (OrderItem item : orderItemList) {
                    item.setRejectedQuantity(item.current_refund_select);
                }
                retreatDish(reasonId, orderItemList);
            }
        }
    }


    //先下单后支付的退菜（类似于桌台点单退菜）
    private void retreatDish(final int reasonId, final List<OrderItem> itemList) {
        try {
            TableService tableService = TableService.getInstance();
            tableService.removeDish(reasonId, tableOrder, itemList, new ResultCallback<Order>() {
                @Override
                public void onResult(Order result) {
                    showToast("退菜成功!");
                    finish();
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("退菜失败," + e.getMessage());
                    Log.i("退菜失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        } finally {
        }
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
            dishKindsAdp.setSelect(currentPosition);
            dishKindsAdp.setData(DishDataController.dishKindList);
            gvDishKind.setAdapter(dishKindsAdp);
            vpDish.setAdapter(dishPagerAdp);
            vpDish.setCurrentItem(currentPosition);
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
                    getDishInfo();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                    Log.i("获取菜品分类为空", "");
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_error")+"," + e.getMessage());
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
                if(!TextUtils.isEmpty(payment.getSubMchID()))
                {
                    WXPay.SUB_MCH_ID = payment.getSubMchID();
                }
            }
        }
    }

    private String total;

    private void initTableInfo() {
        Order tableOrder = (Order) getIntent().getSerializableExtra("tableOrder");
        total = tableOrder.getTotal();
        if (tableOrder != null && tableOrder.getTableStyle() > 0) {
            tableStyle = tableOrder.getTableStyle();
        }
        tableId = getIntent().getLongExtra("tableId", 0);
        reverseCheckOutFlag = getIntent().getIntExtra("reverseCheckOutFlag", 0);
        tableOrderInfo(tableOrder);
        //        if (tableOrder != null && tableOrder.getTableId() > 0 && tableOrder.getId() >0) {
        //        } else if (tableId != 0 && tableId > 0) {
        //            tableOrderInfo(null);
        //        }
        if (tableStyle > 0 && tableStyle == Constant.JsToAndroid.JS_A_SPLITTABLE) {
            if (lvRetreat != null) {
                lvRetreat.setVisibility(View.GONE);
            }
        }
        if (!TextUtils.isEmpty(tableOrder.getTableNames())) {
            tvMainTitle.setText("桌号" + tableOrder.getTableNames());
            tvTableInfo.setText("桌号" + tableOrder.getTableNames() + "正在点菜");
        } else {
            tvTableInfo.setText("正在点菜");
        }
        ToolsUtils.writeUserOperationRecords("进入桌台点菜");
    }

    private long orderId;
    private long tableId;
    private Order tableOrder;

    public void tableOrderInfo(Order tableOrderTemp) {
        cleanCart();
        tableOrder = tableOrderTemp;
        if (tableOrder != null && tableOrder.getCustomerAmount() == 0) {
            Integer peopleNum = DishDataController.tablePeopleNumMap.get(tableId);
            if (peopleNum != null && peopleNum > 0) {
                tableOrder.setCustomerAmount(DishDataController.tablePeopleNumMap.get(tableId));
            }
        }
        if (tableOrderTemp != null && tableOrderTemp.getId() > 0) {
            orderId = tableOrderTemp.getId();
            List<OrderItem> orderItemList = tableOrderTemp.getItemList();
            setRetreatOrderInfo(orderItemList);
        }
        if (ToolsUtils.isReverseCheckOut(reverseCheckOutFlag)) {
            tvCheckOrder.setVisibility(View.GONE);
            tvSearch.setVisibility(View.GONE);
            tvSelect.setVisibility(View.GONE);
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
        //        tvPrice.setText("¥" + String.format("%.2f ", cart.getPriceSum()));
        cleanDishSelectMap();
        DishDataController.cleanDishMarkMap();
        if (dishKindsAdp != null) {
            dishKindsAdp.setMap(dishSelectMap);
        }
        getDishCounts();
        tvPrice.setText("¥0.00");
        tvPricePrefer.setText("¥0.00");
        tvCostPrefer.setText("¥0.00");
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
////            Map<String, Dish> dishMap = DishDataController.getDishMarkMap().get(key);
////            Iterator dishIter = dishMap.keySet().iterator();
////            while (dishIter.hasNext()) {
////                Object dishKey = dishIter.next();
////                Dish dish = dishMap.get(dishKey);
////                num += dish.quantity;
////                dishSelectMap.put((String) key, num);
////            }
////            if (dishMap.size() <= 0) {
////                dishSelectMap.put((String) key, num);
////            }
//        }
//        dishKindsAdp.setMap(dishSelectMap);
//    }

    public void cleanDishSelectMap() {
        if (dishSelectMap != null && dishSelectMap.size() > 0) {
            dishSelectMap.clear();
        }
    }

    private void appTableOrderDish(List<Dish> dishs, final boolean isCheckout) {
        try {
            TableService tableService = TableService.getInstance();
            final List<OrderItem> orderItem = cart.getTableOrderItem(dishs);
            if (orderItem != null && orderItem.size() > 0) {
                Order newOrder = new Order();
                newOrder.setId(orderId);
                tableService.appendDish(newOrder, orderItem, new ResultCallback<Order>() {
                    @Override
                    public void onResult(Order result) {
                        Log.i("<<===加菜下单", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
                        dishPagerAdp.refresh();
                        myApplication.ShowToast("加菜成功!");
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result.getTableNames()));//kds下单
                        progressDialog.disLoading();
                        if (isCheckout) {
                            Intent intent = new Intent(TableOrderNewAty.this, CheckOutNewAty.class);
                            intent.putExtra("source", Constant.EventState.SOURCE_CREAT_ORDER);
                            intent.putExtra("tableOrder", (Serializable) result);
                            startActivityForResult(intent, CHECKOUT);
                        } else {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));
                            finish();
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        Log.i("加菜失败", "orderId ==" + orderId + "===" + e.getMessage());
                        dishPagerAdp.notifyDataSetChanged();
                        myApplication.ShowToast("加菜失败!" + e.getMessage());
                        progressDialog.disLoading();
                        DialogUtil.ordinaryDialog(context, "加菜出错", "加菜出错,该订单已经结账,不能加菜 点击确定直接返回到桌台页面", new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                TableOrderNewAty.this.finish();
                            }

                            @Override
                            public void onCancle() {
                                TableOrderNewAty.this.finish();
                            }
                        });
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("加菜失败", "orderId ==" + orderId + "===" + e.getMessage());
        }
    }

    private void createOrder(List<Dish> dishs, OrderStatus orderStatus) {
        try {
            OrderService orderService = OrderService.getInstance();
            final Order order = cart.getOrderItem(dishs, tableOrder, true, orderStatus);
            if (order != null) {
                orderService.createOrder(order, new ResultCallback<Order>() {
                    @Override
                    public void onResult(Order result) {
                        Log.i("开台下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
                        if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                        }
                        System.out.println("==checkDishCount==" + orderId);
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result.getTableNames()));//kds下单
                        dishPagerAdp.refresh();
                        myApplication.ShowToast("下单成功!");
                        cleanCart();
                        progressDialog.disLoading();
                        TableOrderNewAty.this.finish();
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        Log.i("开台下单失败", "orderId ==" + orderId + "===" + e.getMessage());
                        dishPagerAdp.notifyDataSetChanged();
                        myApplication.ShowToast(ToolsUtils.returnXMLStr("orders_failed")+"!" + e.getMessage());
                        progressDialog.disLoading();
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("开台下单失败", "orderId ==" + orderId + "===" + e.getMessage());
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
        progressDialog.disLoading();
        Log.i("以下菜品份数不足:",names+"====<<");
    }

    public void createOrder(final OrderStatus orderStatus) {
        progressDialog.showLoading("");
        if (cart.getDishItemList().size() > 0) {
            checkOutUtil.getDishStock(cart.getDishItemList(), new DishCheckCallback() {
                @Override
                public void haveStock() {
                    if (orderId > 0 && tableStyle != Constant.JsToAndroid.JS_A_SPLITTABLE) {
                        appTableOrderDish(cart.getDishItemList(), false);
                    } else {
                        getUnpaiedOrderUnderTable(tableId, cart.getDishItemList(), orderStatus);
                    }
                }

                @Override
                public void noStock(List dataList) {
                    refreshDish(dataList, cart.getDishItemList());
                }
            });
        } else {
            myApplication.ShowToast("先点菜吧!");
            progressDialog.disLoading();
        }
    }


    /**
     * 如果是桌台模式,在开台下单前判断这个坐台当前有没有订单
     *
     * @param tableId
     */
    private void getUnpaiedOrderUnderTable(long tableId, final List<Dish> dishs, final OrderStatus orderStatus) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.getUnpaiedOrderUnderTable(tableId, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    //桌台没有被占用
                    if (result == 0) {
                        createOrder(cart.getDishItemList(), orderStatus);
                    } else {
                        progressDialog.disLoading();
                        showToast("桌台被占用,请回到主页重新选择桌台!");
                        ToolsUtils.writeUserOperationRecords("桌台被占用!");
                    }
                    //                    else
                    //                    {
                    //                        DialogUtil.ordinaryDialog(context, "桌台被占用", "桌台被占用,是否要加菜?", new DialogCallback() {
                    //                            @Override
                    //                            public void onConfirm() {
                    //                                myApplication.exit();
                    //                            }
                    //
                    //                            @Override
                    //                            public void onCancle() {
                    //
                    //                            }
                    //                        });
                    //                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialog.disLoading();
                    showToast("检测桌台状态失败" + e.getMessage());
                    Log.i("检测桌台状态失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("检测桌台状态失败", e.getMessage());
        }
    }

    /**
     * 挂单
     */
    private void hangOrder() {
        //说明是要加菜
        if (orderId > 0 && tableStyle != Constant.JsToAndroid.JS_A_SPLITTABLE) {
            TableOrderNewAty.this.finish();
        }
        //开新单
        else {
            if (Cart.getDishItemList() != null && Cart.getDishItemList().size() > 0) {
                DialogUtil.ordinaryDialog(context, "提示", "当前有未下单的菜品,是否要挂单?", new DialogCallback() {
                    @Override
                    public void onConfirm() {
                        createOrder(OrderStatus.PENDING);
                    }

                    @Override
                    public void onCancle() {
                        TableOrderNewAty.this.finish();
                    }
                });
            } else {
                TableOrderNewAty.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
        SunmiSecondScreen.getInstance(context).cleanDSD();

    }

    @OnClick( {R.id.tv_main, R.id.tv_scan, R.id.tv_search, R.id.tv_checkout, R.id.tv_checkOrder, R.id.tv_up, R.id.tv_down, R.id.tv_table_info, R.id.tv_select} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_select:
                if (tableOrder != null && tableOrder.getId() > 0 && !ToolsUtils.isReverseCheckOut(reverseCheckOutFlag)) {
                    ToolsUtils.writeUserOperationRecords("桌台操作状态按钮");
                    int w = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    int h = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    view.measure(w, h);
                    int height = view.getMeasuredHeight();
                    int[] location = new int[2];
                    tvSelect.getLocationOnScreen(location);
                    orderItemWindow.showAsDropDown(relTop, location[0] - 20, location[1] - (height / 2));
                }
                break;
            case R.id.tv_main:
                ToolsUtils.writeUserOperationRecords("主页返回按钮");
                //                hangOrder();
                TableOrderNewAty.this.finish();
                break;
            case R.id.tv_scan:
                break;
            case R.id.tv_search:
                ToolsUtils.writeUserOperationRecords("搜索按钮");
                intent.setClass(TableOrderNewAty.this, SearchAty.class);
                startActivity(intent);
                break;
            //结账
            case R.id.tv_checkout:
                ToolsUtils.writeUserOperationRecords("结账按钮");
                if (!ToolsUtils.isReverseCheckOut(reverseCheckOutFlag)) {
                    if (cart.getDishItemList().size() > 0) {
                        showToast("请先下单!");
                    } else {
                        if (tableOrder != null && tableOrder.getId() > 0) {
                            setTableOrderTotal();
                            Intent intent = new Intent(TableOrderNewAty.this, CheckOutNewAty.class);
                            intent.putExtra("source", Constant.EventState.SOURCE_TABLE_ORDER);
                            intent.putExtra("tableOrder", (Serializable) tableOrder);
                            startActivityForResult(intent, CHECKOUT);
                        } else {
                            showToast(ToolsUtils.returnXMLStr("please_click_dish"));
                        }
                    }
                } else {
                    if (tableOrder != null && tableOrder.getId() > 0) {
                        setTableOrderTotal();
                        Intent intent = new Intent(TableOrderNewAty.this, CheckOutNewAty.class);
                        intent.putExtra("source", Constant.EventState.SOURCE_TABLE_ORDER);
                        intent.putExtra("tableOrder", (Serializable) tableOrder);
                        intent.putExtra("reverseCheckOutFlag", Constant.REVERSE_CHECKOUT);
                        startActivityForResult(intent, CHECKOUT);
                    }
                }
                break;
            //下单
            case R.id.tv_checkOrder:
                ToolsUtils.writeUserOperationRecords("下单按钮");
                createOrder(OrderStatus.NORMAL);
                break;
            case R.id.tv_up:
                //                gvDishKind.onKeyDown(KeyEvent.KEYCODE_DPAD_UP, null);
                KeyEvent event2 = new KeyEvent(KeyEvent.ACTION_DOWN, 1);
                gvDishKind.onKeyDown(KeyEvent.KEYCODE_DPAD_UP, event2);
                break;
            case R.id.tv_down:
                KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, 1);
                gvDishKind.onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, event);
                break;
            //操作按钮
            case R.id.tv_table_info:
                if (!ToolsUtils.isReverseCheckOut(reverseCheckOutFlag)) {
                    ToolsUtils.writeUserOperationRecords("桌台操作状态按钮");
                    int w = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    int h = View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED);
                    view.measure(w, h);
                    int height = view.getMeasuredHeight();
                    int width = view.getMeasuredWidth();
                    int[] location = new int[2];
                    tvTableInfo.getLocationOnScreen(location);
                    window.showAsDropDown(relTop, location[0] + (width / 2), location[1] - height);
                }
                break;
        }
    }

    /**
     * 重新设置菜品打折方案
     */
    private void setTableOrderTotal() {
        if (tableOrder != null && tableOrder.getItemList() != null && tableOrder.getItemList().size() > 0) {
            BigDecimal count = new BigDecimal("0.00");
            for (OrderItem orderItem : tableOrder.getItemList()) {
                count = count.add(orderItem.getCost().multiply(new BigDecimal(orderItem.getQuantity())));
            }
            tableOrder.setTotal(count.toString());
            tableOrder.setCost(count.toString());
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
            //            if (ToolsUtils.logicIsTable()) {
            ////                hangOrder();//是否要挂单
            //                TableOrderNewAty.this.finish();
            //            } else {
            noTableBack();//判断购物车有菜是否要清空购物车
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
                    progressDialog.disLoading();
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
                    progressDialog.disLoading();
                    loadData();
                    Log.i("获取菜品沽清状态失败", e.getMessage());
                    MyApplication.getInstance().ShowToast("获取菜品沽清状态失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            Log.i("获取菜品沽清状态失败", e.getMessage());
            e.printStackTrace();
            MyApplication.getInstance().ShowToast("获取菜品沽清状态失败!");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDishCounts();
        getSections();
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
                tvSelect.setVisibility(View.VISIBLE);
                retreatOrderAdp.setData(orderItemList);
            }
        } else {
            if (lvRetreat != null) {
                lvRetreat.setVisibility(View.GONE);
                tvSelect.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHECKOUT) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
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
                    dishKindsAdp.setMap(dishSelectMap);
                    TableOrderNewAty.this.finish();
                }

                @Override
                public void onCancle() {

                }
            });
        } else {
            TableOrderNewAty.this.finish();
        }
    }

    /**
     * 更新下单菜品的价格
     *
     * @param orderItemId
     * @param newCost
     */
    private void upDataOrderCost(final long orderItemId, final BigDecimal newCost) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.updataOrderCost(orderItemId, newCost, new ResultCallback<PosResponse>() {
                @Override
                public void onResult(PosResponse result) {
                    if (result != null && result.isSuccessful()) {
                        showToast("修改成功!");
                        getOrderById(tableOrder);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("修改菜品价格失败", orderItemId + "修改失败," + e.getMessage());
                    showToast("修改菜品价格失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("修改菜品价格失败", orderItemId + "修改失败," + e.getMessage());
            showToast("修改菜品价格失败," + e.getMessage());
        }
    }

    private void getOrderById(final Order order) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.getOrderInfoById(String.valueOf(order.getId()), new ResultCallback<Order>() {
                @Override
                public void onResult(Order result) {
                    if (result != null && result.getId() > 0) {
                        tableOrder = result;
                        setRetreatOrderInfo(tableOrder.getItemList());
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("获取订单信息失败!" + e.getMessage());
                    Log.i("获取订单信息失败!", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast("获取订单信息失败!" + e.getMessage());
            Log.i("获取订单信息失败!", e.getMessage());
        }
    }

    /**
     * 获取桌台区域列表
     */
    private void getSections() {
        try {
            TableService tableService = TableService.getInstance();
            tableService.getSections(new ResultCallback<List<Sections>>() {
                @Override
                public void onResult(List<Sections> result) {
                    if (result != null && result.size() > 0) {
                        TableDataController.tableSections = result;
                    } else {
                        showToast("区域列表为空");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.e("区域列表获取失败", e.getMessage());
                    showToast(e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.e("区域列表获取失败", e.getMessage());
        }
    }

}
