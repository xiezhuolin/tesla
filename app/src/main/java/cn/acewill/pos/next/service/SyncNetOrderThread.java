package cn.acewill.pos.next.service;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.next.common.NetOrderController;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.ErrorCode;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.utils.CheckOutUtil;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/9/12.
 */
public class SyncNetOrderThread extends Thread {
    @Override
    public void run() {
        OrderService orderService = null;
        try {
            orderService = OrderService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
        final Store store = Store.getInstance(MyApplication.getInstance());
        while (MyApplication.getInstance().isConFirmNetOrder()) {
            try {
                final OrderService finalOrderService = orderService;
                orderService.syncNetOrders(store.getPreordertime(), new ResultCallback<List<Order>>() {
                    @Override
                    public void onResult(final List<Order> result) {
                        if (!ToolsUtils.isList(result)) {
                            if (store.getAutoMaticNetOrder()) {
                                autoMaticNetOrder(result);
                            } else {
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.PUT_NET_ORDER, result));
                            }
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        if (e.getErrorCode() == ErrorCode.TOKEN_ERROR) {
                            //                            EventBus.getDefault().post(new PosEvent(Constant.EventState.TOKEN_TIME_OUT));
                        }
                        //                        MyApplication.getInstance().ShowToast("获取网络订单失败 "+e.getMessage());
                    }
                });
            } catch (Throwable t) {
                t.printStackTrace();
            }
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void autoMaticNetOrder(List<Order> orderList) {
        if (!ToolsUtils.isList(orderList)) {
            for (Order orderInfo : orderList) {
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
                //orderInfo.setComment(source + ":" + orderInfo.getId() + "");
                if (ToolsUtils.logicIsTable()) {
                    checkDishCount(orderInfo);
                } else {
                    checkDishCount(orderInfo);
                }
                sleep();
            }
        }
    }

    /**
     * 检测菜品库存并下单
     *
     * @param order
     */
    public void checkDishCount(final Order order) {
        //转台单
        if (order.getOperation_type() == 2) {
            conFirmNetOrder(order);
        } else {
            CheckOutUtil checkOutUtil = new CheckOutUtil(MyApplication.getInstance().getContext());
            final List<Dish> dishs = new CopyOnWriteArrayList<>();
            for (OrderItem orderItem : order.getItemList()) {
                Dish dish = new Dish(orderItem);
                dishs.add(dish);
            }
            checkOutUtil.getDishStock(dishs, new DishCheckCallback() {
                @Override
                public void haveStock() {
                    //                createOrder(null, order);
                    getOrderId(null, order);
                }

                @Override
                public void noStock(List dataList) {
                    refreshDish(dataList, dishs);
                }
            });
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
            MyApplication.getInstance().ShowToast("正在获取订单详情,请稍等...");
            OrderService orderService = OrderService.getInstance();
            orderService.getOrderInfoById(order.getRefOrderId() + "", new ResultCallback<Order>() {
                @Override
                public void onResult(final Order result) {
                    if (result != null && result.getItemList() != null && result.getItemList().size() > 0) {
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                order.setItemList(result.getItemList());
                                order.setOrderType(result.getOrderType());
                                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINTER_KITCHEN_ORDER, order));
                            }
                        }, 3000);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("订单详情获取失败!", e.getMessage());
                    MyApplication.getInstance().ShowToast(e.getMessage());
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
                                MyApplication.getInstance().ShowToast("通知KDS换台成功!");
                            } else {
                                MyApplication.getInstance().ShowToast("通知KDS换台失败!");
                                KDSOrderFailure(netOrderId, newTableName);
                            }
                        }

                        @Override
                        public void onError(PosServiceException e) {
                            MyApplication.getInstance().ShowToast("通知KDS换台失败," + e.getMessage());
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
        DialogUtil.ordinaryDialog(MyApplication.getInstance().getContext(), ToolsUtils.returnXMLStr("kds_connect_error"), ToolsUtils.returnXMLStr("please_re_switch_KDS"), new DialogCallback() {
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
            OrderService orderService = OrderService.getInstance();
            Order order = null;
            if (dishs != null) {
                order = Cart.getInstance().getNetOrderItem(dishs, netOrder);
            } else if (netOrder != null) {
                order = netOrder;
            }
            if (order != null) {
                final long orderId = order.getId();
                order.setRefNetOrderId(orderId);
                orderService.createOrder(order, new ResultCallback<Order>() {
                    @Override
                    public void onResult(final Order result) {

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
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("orders_failed") +"!" + e.getMessage());
                        //refrushUi(position,netOrder.getId());
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("网上开台下单失败", "===" + e.getMessage());
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
        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("the_following_items_are_not_enough")+"\n\n" + names + "。\n\n"+ToolsUtils.returnXMLStr("please_re_order"));
        Log.i("以下菜品份数不足:",names+"====<<");
    }

    private void refrushUi(long netOrderId) {
        Order copyList = NetOrderController.getNetOrderMap().get(netOrderId);//根据订单ID去map列表里面查询出order对象
        if (copyList != null && copyList.getId() == (int) netOrderId) {
            NetOrderController.getNetOrderList().remove(copyList);
        }
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
