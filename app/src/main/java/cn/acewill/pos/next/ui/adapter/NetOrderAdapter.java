package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.NetOrderController;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.NetOrderRea;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.ui.activity.newPos.NetOrderNewAty;
import cn.acewill.pos.next.utils.CheckOutUtil;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class NetOrderAdapter extends RecyclerView.Adapter {
    public Context context;
    public List<Order> dataList = new ArrayList<>();
    public Cart cart;
    public MyApplication myApplication;
    public LayoutInflater inflater;
    private RefrushLisener refrushLisener;


    public static final int UP_LOAD_TYPE = 0;//上拉加载
    public static final int DOWN_LOAD_TYPE = 1;//下拉刷新
    public int load_type = 0;//加载类型

    public static final int LOAD_MORE = 0;//加载更多
    public static final int LOADING = 1;//正在加载
    public static final int NO_MORE = 2;//没有数据了
    public int load_more_status = 0;
    private int mLastPosition = -1;

    private static final int TYPE_ITEM = 0;//普通Item
    private static final int TYPE_FOOTER = 1;//底部footview


    public NetOrderAdapter(Context context, List<Order> dataList, RefrushLisener refrushLisener) {
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
        this.cart = Cart.getInstance();
        myApplication = MyApplication.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.lv_item_net_order, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = inflater.inflate(R.layout.foot_view, parent, false);
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Order orderInfo = dataList.get(position);
            itemViewHolder.order_num.setText(orderInfo.getId() + "");
            itemViewHolder.take_num.setText(orderInfo.getOrderTypeStr());
            String source = orderInfo.getSource();
            if (!TextUtils.isEmpty(source)) {
                if (source.equals("2")) {
                    source = "微信堂食";
                }
            } else {
                source = "未知来源";
            }
            itemViewHolder.order_source.setText(source);
            itemViewHolder.order_total.setText(orderInfo.getTotal());
            if (orderInfo.getPaymentStatus().toString().equals("NOT_PAYED")) {
                itemViewHolder.order_cost.setText("0.00");
            } else {
                itemViewHolder.order_cost.setText(orderInfo.getCost());
            }
            itemViewHolder.order_cost.setText(orderInfo.getCost());
            itemViewHolder.creat_time.setText(TimeUtil.getStringTimeLong(orderInfo.getCreatedAt()));
            itemViewHolder.edit_btn.setText("拒绝");

            if (orderInfo.getOperation_type() != 2) {
                itemViewHolder.pay_btn.setText("接受");
            } else {
                itemViewHolder.pay_btn.setText("同意换台");
            }
            itemViewHolder.edit_btn.setBackgroundResource(R.drawable.btn_selector_red);
            itemViewHolder.pay_btn.setBackgroundResource(R.drawable.btn_selector_blue);

            //如果是桌台模式
            if (ToolsUtils.logicIsTable()) {
                //判断该订单是否含有tableid,没有的话隐藏接受按钮
                if (orderInfo.preorderTime > 0) {
                    itemViewHolder.pay_btn.setVisibility(View.GONE);
                } else {
                    itemViewHolder.pay_btn.setVisibility(View.VISIBLE);
                }
            }

            itemViewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToolsUtils.writeUserOperationRecords("拒绝网上订单==>订单Id==" + orderInfo.getId());
                    if (itemViewHolder.pay_btn.getVisibility() == View.GONE) {
                        rejuctNetOrder(position, orderInfo.getId(), "订单信息填写错误,无法下单!");
                    } else {
                        List<NetOrderRea> reasonItem = new ArrayList<NetOrderRea>();
                        reasonItem.add(new NetOrderRea("天气原因,无法正常送单!"));
                        reasonItem.add(new NetOrderRea("订单信息填写错误,无法下单!"));
                        reasonItem.add(new NetOrderRea("菜品已售罄"));
                        reasonItem.add(new NetOrderRea("即将打烊"));
                        reasonItem.add(new NetOrderRea("无"));
                        DialogUtil.listDialog(context, "拒绝原因", reasonItem, new DialogEtCallback() {
                            @Override
                            public void onConfirm(String sth) {
                                rejuctNetOrder(position, orderInfo.getId(), sth);
                            }

                            @Override
                            public void onCancle() {

                            }
                        });
                    }
                }
            });

            itemViewHolder.pay_btn.setOnClickListener(new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View v) {
                                                              ToolsUtils.writeUserOperationRecords("接受网上订单==>订单Id==" + orderInfo.getId());
                                                              if (TextUtils.isEmpty(orderInfo.tableid)) {
                                                                  //                        myApplication.ShowToast("未获取到桌台信息,接单失败!");
                                                                  //                        return;
                                                              }
                                                              String source = orderInfo.getSource();
                                                              if (!TextUtils.isEmpty(source)) {
                                                                  if (source.equals("2")) {
                                                                      source = "微信堂食";
                                                                  }
                                                              } else {
                                                                  source = "未知来源";
                                                              }
                                                              orderInfo.setSource(source);
                                                              //                                                              orderInfo.setComment(source + ":" + orderInfo.getId() + "");
                                                              if (ToolsUtils.logicIsTable()) {
                                                                  //                        Long tableId = Long.valueOf(orderInfo.tableid);
                                                                  //                        orderInfo.setTableId(tableId);
                                                                  //0 新订单
                                                                  //                        if(orderInfo.operation_type == 0)
                                                                  //                        {
                                                                  //                            openTable(position,orderInfo.getTableId(),orderInfo.getCustomerAmount(),orderInfo);
                                                                  //                        }
                                                                  //                        //1 加菜单
                                                                  //                        else if(orderInfo.operation_type == 1)
                                                                  //                        {
                                                                  //                            checkDishCount(position,orderInfo);
                                                                  //                        }
                                                                  checkDishCount(orderInfo);
                                                              } else {
                                                                  checkDishCount(orderInfo);
                                                              }
                                                          }
                                                      }
            );
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLastPosition = position;
                    NetOrderAdapter.this.notifyDataSetChanged();
                }
            });
            if (mLastPosition != position) {
                itemViewHolder.scrolListView.setVisibility(View.GONE);
            } else {
                itemViewHolder.scrolListView.setVisibility(View.VISIBLE);
            }
            if (mLastPosition == position) {
                List<OrderItem> orderItemList = dataList.get(position).getItemList();
                if (orderItemList != null) {
                    NetOrderItemAdp netOrderItemAdp = new NetOrderItemAdp(context);
                    netOrderItemAdp.setData(orderItemList);
                    itemViewHolder.scrolListView.setAdapter(netOrderItemAdp);
                }
            }
            holder.itemView.setTag(position);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            switch (load_more_status) {
                case LOAD_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText(ToolsUtils.returnXMLStr("pull_up_to_load_more"));
                    break;
                case LOADING:
                    footViewHolder.load_icon.setVisibility(View.VISIBLE);
                    footViewHolder.load_more_tv.setText(ToolsUtils.returnXMLStr("loading"));
                    break;
                case NO_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText("");
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView order_num;
        private TextView take_num;
        private TextView order_source;
        private TextView order_total;
        private TextView order_cost;
        private TextView creat_time;
        private TextView edit_btn;
        private TextView pay_btn;
        private ScrolListView scrolListView;

        public ItemViewHolder(View view) {
            super(view);
            order_num = (TextView) view.findViewById(R.id.order_num);
            take_num = (TextView) view.findViewById(R.id.take_num);
            order_source = (TextView) view.findViewById(R.id.order_source);
            order_total = (TextView) view.findViewById(R.id.order_total);
            order_cost = (TextView) view.findViewById(R.id.order_cost);
            creat_time = (TextView) view.findViewById(R.id.creat_time);
            edit_btn = (TextView) view.findViewById(R.id.edit_btn);
            pay_btn = (TextView) view.findViewById(R.id.pay_btn);
            scrolListView = (ScrolListView) view.findViewById(R.id.order_lv);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView load_more_tv;
        private ProgressBar load_icon;

        public FootViewHolder(View itemView) {
            super(itemView);
            load_more_tv = (TextView) itemView.findViewById(R.id.load_more_tv);
            load_icon = (ProgressBar) itemView.findViewById(R.id.load_icon);
        }
    }

    /**
     * 更新数据
     *
     * @param orders
     */
    public void setData(List<Order> orders) {
        if (orders != null && orders.size() > 0) {
            switch (load_type) {
                case UP_LOAD_TYPE://上拉加载
                    this.dataList.addAll(orders);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = orders;
                    break;
            }
        } else {
            this.dataList = new ArrayList<>();
        }
        this.notifyDataSetChanged();
    }

    /**
     * status
     * 0:加载更多；1:加载中；2:没有数据了；3:上拉刷新
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        this.notifyDataSetChanged();
    }

    public void setLoadType(int type) {
        load_type = type;
    }

    public interface RefrushLisener {
        public void refrush();
    }

    /**
     * 拒绝网上订单
     */
    private void rejuctNetOrder(final int position, final long orderId, final String rejuctStr) {
        try {
            final OrderService orderService = OrderService.getInstance();
            orderService.rejectNetOrder(orderId, rejuctStr, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    if (result == 0)//表示成功拒绝
                    {
                        if (orderId == dataList.get(position).getId()) {
                            dataList.remove(position);
                        }
                        NetOrderAdapter.this.notifyDataSetChanged();
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PUT_NET_ORDER, dataList));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    MyApplication.getInstance().ShowToast(e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 确认接收网上的订单
     *
     * @param resultOrder
     */
    private void conFirmNetOrder(final Order resultOrder) {
        //        final String netOrderId = String.valueOf(dataList.get(position).getPaymentList().get(0).getOrderId());
        final String netOrderId = String.valueOf(resultOrder.getId());
        final String orderId = String.valueOf(resultOrder.getRefOrderId());
        final long orderIdL = resultOrder.getId();
        OrderService orderService = null;
        try {
            orderService = OrderService.getInstance();

        } catch (PosServiceException e) {
            e.printStackTrace();
        }

        orderService.confirmNetOrder(netOrderId, orderId, resultOrder.getCallNumber(), new ResultCallback<Integer>() {
            @Override
            public void onResult(Integer result) {
                if (result == 0)//表示成功接单
                {
                    refrushUi(resultOrder.getId());
                    MyApplication.getInstance().ShowToast("网络订单编号 : " + orderId + "同步成功!");
                    if (resultOrder.isInformKds())//是否通知KDS打印
                    {
                        kdsChangeOrderTable(resultOrder.getRefOrderId(), resultOrder.getChangeTableName());
                    }
                    if (resultOrder.isInformKitchen())//是否通知厨房打印
                    {
                        getOrderInfo(resultOrder);
                    }
                    sleep();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                MyApplication.getInstance().ShowToast("网上订单确认同步失败," + e.getMessage());
                Log.i("网上订单确认同步失败", "orderId ==" + orderId + "===" + e.getMessage());
            }
        });
    }


    /**
     * 根据订单Id获得订单详情
     */
    private void getOrderInfo(final Order order) {
        try {
            myApplication.ShowToast("正在获取订单详情,请稍等...");
            OrderService orderService = OrderService.getInstance();
            orderService.getOrderInfoById(order.getRefOrderId() + "", new ResultCallback<Order>() {
                @Override
                public void onResult(final Order result) {
                    if (result != null && result.getItemList() != null && result.getItemList().size() > 0) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                order.setItemList(result.getItemList());
                                order.setOrderType(result.getOrderType());
                                order.setId(result.getId());
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, order));
                            }
                        }, 3000);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("订单详情获取失败!", e.getMessage());
                    myApplication.ShowToast(e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("订单详情获取失败!", e.getMessage());
        }
    }

    private void kdsChangeOrderTable(final long netOrderId, final String newTableName) {
        List<KDS> kdsList = PrinterDataController.getKdsList();
        if (kdsList != null && kdsList.size() > 0) {
            int size = kdsList.size();
            for (int i = 0; i < size; i++) {
                KDS kds = kdsList.get(i);
                try {
                    OrderService orderService = OrderService.getKdsInstance(kds);
                    orderService.kdsChangeOrderTable(netOrderId, newTableName, new ResultCallback<Boolean>() {
                        @Override
                        public void onResult(Boolean result) {
                            if (result) {
                                myApplication.ShowToast("通知KDS换台成功!");
                            } else {
                                myApplication.ShowToast("通知KDS换台失败!");
                                KDSOrderFailure(netOrderId, newTableName);
                            }
                        }

                        @Override
                        public void onError(PosServiceException e) {
                            myApplication.ShowToast("通知KDS换台失败," + e.getMessage());
                            Log.i("通知KDS换台失败", e.getMessage());
                            KDSOrderFailure(netOrderId, newTableName);
                        }
                    });
                } catch (PosServiceException e) {
                    e.printStackTrace();
                    Log.i("KDS退单失败", e.getMessage());
                    KDSOrderFailure(netOrderId, newTableName);
                }
            }
        }
    }


    /**
     * KDS连接失败的操作
     */
    private void KDSOrderFailure(final long netOrderId, final String newTableName) {
        DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("kds_connect_error"), ToolsUtils.returnXMLStr("please_re_switch_KDS"), new DialogCallback() {
            @Override
            public void onConfirm() {
                kdsChangeOrderTable(netOrderId, newTableName);
            }

            @Override
            public void onCancle() {
            }
        });
    }

    private void createOrder(List<Dish> dishs, final Order netOrder) {
        try {
            show();
            OrderService orderService = OrderService.getInstance();
            Order order = null;
            if (dishs != null) {
                order = cart.getNetOrderItem(dishs, netOrder);
            } else if (netOrder != null) {
                order = ToolsUtils.cloneTo(netOrder);;
            }
            if (order != null) {
                final long orderId = order.getId();
                order.setRefNetOrderId(orderId);
                order.setThirdPlatformOrderId(netOrder.getOuterOrderid());
                order.setThirdPlatformOrderIdView(netOrder.getOuterOrderIdView());
                order.setThirdPlatfromOrderIdDaySeq(netOrder.getOuterOrderIdDaySeq());
                orderService.createOrder(order, new ResultCallback<Order>() {
                    @Override
                    public void onResult(final Order result) {
                        dissmiss();
                        Log.i("网上开台下单成功", "orderId ==" + orderId + "===" + ToolsUtils.getPrinterSth(result));
                        if (!TextUtils.isEmpty(StoreInfor.storeMode) && StoreInfor.storeMode.equals("TABLE")) {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                        }
                        System.out.println("==checkDishCount==" + orderId);

                        EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_ORDER, result));

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, result));
                            }
                        }, 3000);

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_CHECKOUT, result));
                            }
                        }, 3000);

                        //conFirmNetOrder(position ,result);
                        MyApplication.getInstance().ShowToast("网络订单编号 : " + orderId + "接单成功!");

                        refrushUi(netOrder.getId());
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result.getTableNames()));//kds下单
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        Log.i("网上开台下单失败", "orderId ==" + orderId + "===" + e.getMessage());
                        myApplication.ShowToast("下单失败!" + e.getMessage());
                        //                        refrushUi(position,netOrder.getId());
                        dissmiss();
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("网上开台下单失败", "===" + e.getMessage());
        }
    }

    private void refrushUi(long netOrderId) {
        Order copyList = NetOrderController.getNetOrderMap().get(netOrderId);//根据订单ID去map列表里面查询出order对象
        if (copyList != null && copyList.getId() == (int) netOrderId) {
            NetOrderController.getNetOrderList().remove(copyList);
        }
        NetOrderAdapter.this.notifyDataSetChanged();
        EventBus.getDefault().post(new PosEvent(Constant.EventState.PUT_NET_ORDER, NetOrderController.getNetOrderList()));
    }

    /**
     * 加菜
     *
     * @param dishs
     * @param order
     */
    private void appTableOrderDish(List<Dish> dishs, final Order order) {
        try {
            show();
            TableService tableService = TableService.getInstance();
            final List<OrderItem> orderItem = cart.getTableOrderItem(dishs);
            if (orderItem != null && orderItem.size() > 0) {
                Order newOrder = new Order();
                newOrder.setId(order.getId());
                newOrder.setRefNetOrderId(order.getId());
                tableService.appendDish(newOrder, orderItem, new ResultCallback<Order>() {
                    @Override
                    public void onResult(Order result) {
                        dissmiss();
                        Log.i("<<===网上加菜下单", "orderId ==" + order.getId() + "===" + ToolsUtils.getPrinterSth(result));
                        myApplication.ShowToast("加菜成功!");
                        //conFirmNetOrder(position ,result);

                        MyApplication.getInstance().ShowToast("网络订单编号 : " + order.getId() + "接单成功!");

                        refrushUi(order.getId());
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SEND_INFO_KDS, result, result.getTableNames()));//kds下单
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        dissmiss();
                        Log.i("网上加菜失败", "orderId ==" + order.getId() + "===" + e.getMessage());
                        //                        refrushUi(position,order.getId());
                        myApplication.ShowToast("加菜失败!" + e.getMessage());
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            dissmiss();
            Log.i("网上加菜失败", "orderId ==" + order.getId() + "===" + e.getMessage());
        }
    }

    /**
     * 检测菜品库存并下单
     *
     * @param order
     */
    public void checkDishCount(final Order order) {
        //换台单
        if (order.getOperation_type() == 2) {
            conFirmNetOrder(order);
        } else {
            CheckOutUtil checkOutUtil = new CheckOutUtil(context);
            final List<Dish> dishs = new CopyOnWriteArrayList<>();
            for (OrderItem orderItem : order.getItemList()) {
                Dish dish = new Dish(orderItem);
                dishs.add(dish);
            }
            checkOutUtil.getDishStock(dishs, new DishCheckCallback() {
                @Override
                public void haveStock() {
                    //                if(ToolsUtils.logicIsTable())
                    //                {
                    //                    //先检测桌台状态然后再下新订单
                    //                    if(order.operation_type == 0)
                    //                    {
                    //                        getUnpaiedOrderUnderTable(order.getTableId(),order,position,dishs);
                    //                    }
                    //                    //加菜
                    //                    else if(order.operation_type == 1)
                    //                    {
                    //                        appTableOrderDish(position,dishs,order);
                    //                    }
                    //                }
                    //                else
                    //                {
                    //                createOrder(null, order);
                    getOrderId(null, order);
                    //                }
                }

                @Override
                public void noStock(List dataList) {
                    refreshDish(dataList, dishs);
                }
            });
        }
    }

    public void getOrderId(final List<Dish> dishs, final Order order) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.getNextOrderId(new ResultCallback<Long>() {
                @Override
                public void onResult(Long result) {
                    if (result > 0) {
                        PosInfo posInfo = PosInfo.getInstance();
                        posInfo.setOrderId(result);
                        createOrder(dishs, order);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取订单Id失败", e.getMessage());
                    EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_CREATE_ORDERID_FILURE));
//                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("get_order_id_failure"));
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示沽清提示
     *
     * @param result
     */
    public void refreshDish(List<DishCount> result, List<Dish> dishs) {
        //刷新菜品数据,显示沽清
        String names = Cart.getInstance().getItemNameByDids((ArrayList) result, dishs);
        myApplication.ShowToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough")+"\n\n" + names + "。\n\n"+ToolsUtils.returnXMLStr("please_re_order"));
        Log.i("以下菜品份数不足:",names+"====<<");
    }

    /**
     * 如果是桌台模式,在开台下单前判断这个坐台当前有没有订单
     *
     * @param tableId
     */
    private void getUnpaiedOrderUnderTable(long tableId, final Order order, final int position, final List<Dish> dishs) {
        try {
            OrderService orderService = OrderService.getInstance();
            orderService.getUnpaiedOrderUnderTable(tableId, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    dissmiss();
                    //桌台没有被占用
                    if (result == 0) {
                        createOrder(dishs, order);
                    } else {
                        myApplication.ShowToast("桌台被占用,请回到主页重新选择桌台!");
                        ToolsUtils.writeUserOperationRecords("桌台被占用!");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    dissmiss();
                    myApplication.ShowToast("检测桌台状态失败" + e.getMessage());
                    Log.i("检测桌台状态失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("检测桌台状态失败", e.getMessage());
        }
    }

    /**
     * 开台并下单
     *
     * @param tableId
     * @param numberOfCustomer
     */
    private void openTable(final int position, final long tableId, final int numberOfCustomer, final Order order) {
        try {
            TableService tableService = TableService.getInstance();
            tableService.openTable(tableId, numberOfCustomer, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        if (order != null && order.getId() > 0 && order.getItemList() != null && order.getItemList().size() > 0) {
                            checkDishCount(order);
                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    myApplication.ShowToast("开台失败," + e.getMessage() + "!");
                    Log.i("开台失败,", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            Log.i("开台失败,", e.getMessage());
        }
    }

    private void show() {
        ((NetOrderNewAty) context).showProgress();
    }

    private void dissmiss() {
        ((NetOrderNewAty) context).dissmiss();
    }

    /**
     * 休息两秒
     */
    private void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
