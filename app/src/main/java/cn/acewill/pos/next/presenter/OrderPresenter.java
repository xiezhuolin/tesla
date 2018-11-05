package cn.acewill.pos.next.presenter;

import java.util.List;

import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.ui.DialogView;

/**
 * 订单管理
 * Created by aqw on 2016/6/17.
 */
public class OrderPresenter {
    private DialogView dialogView;
    private OrderService orderService;
    private MyApplication myApplication;

    private Store store;
    private PosInfo posInfo;

    private String appId;//商户ID
    private String brandId;//品牌ID
    private String storeId;//门店ID

    public OrderPresenter(DialogView dialogView){
        this.dialogView = dialogView;
        try {
            orderService = OrderService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }

        myApplication = MyApplication.getInstance();
        store = Store.getInstance(myApplication);
        posInfo = PosInfo.getInstance();
        appId = store.getStoreAppId();
        brandId = store.getBrandId();
        storeId = store.getStoreId();
    }

    //根据订单号获取订单详情
    public void getOrderById(String orderId){
        dialogView.showDialog();
        orderService.getOrderInfoById(orderId, new ResultCallback<Order>() {
            @Override
            public void onResult(Order result) {
                dialogView.dissDialog();
                if(result != null)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

    //获取某个门店当日的所有订单
    public void getAllOrders(int page,int limit,int orderType,int payStatus){
        dialogView.showDialog();
        orderService.getAllOrders(posInfo.getToken(),appId, brandId, storeId,page,limit,orderType,payStatus, new ResultCallback<List<Order>>() {
            @Override
            public void onResult(List<Order> result) {
                dialogView.dissDialog();
                if(result != null && result.size()>0)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

    /**
     * 获得挂账列表
     */
    public void cardRecords()
    {
        dialogView.showDialog();
        orderService.getCardRecords(new ResultCallback<List<CardRecord>>() {
            @Override
            public void onResult(List<CardRecord> result) {
                dialogView.dissDialog();
                if(result != null && result.size()>0)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });

    }


    //获取某个桌台上的所有订单
    public void getOrdersByTableId(long tid){
        dialogView.showDialog();
        orderService.getOrdersByTableId(tid, new ResultCallback<List<Order>>() {
            @Override
            public void onResult(List<Order> result) {
                dialogView.dissDialog();
                if(result != null && result.size()>0)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

    //根据获取桌台订单号查询订单信息(接口嵌套)
    public void getOrderByTableId(long tid){
        dialogView.showDialog();
        orderService.getOrderByTableId(tid, new ResultCallback<Order>() {
            @Override
            public void onResult(Order result) {
                dialogView.dissDialog();
                if(result != null)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

    //创建订单
    public void createOrder(Order order){
        dialogView.showDialog();
        orderService.createOrder(order, new ResultCallback<Order>() {
            @Override
            public void onResult(Order result) {
                dialogView.dissDialog();
                dialogView.callBackData(result);
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }

    //加菜
    public void orderAppend(long id,List<OrderItem> items){
        dialogView.showDialog();
        //TODO
        /*orderService.appendOrder(id, items, new ResultCallback<Integer>() {
            @Override
            public void onResult(Integer result) {
                dialogView.dissDialog();
                dialogView.callBackData(result);
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });*/
    }



}
