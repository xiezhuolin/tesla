package cn.acewill.pos.next.service.canxingjian;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.factory.RetrofitFactory;
import cn.acewill.pos.next.model.Restaurant;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.service.canxingjian.retrofit.CXJRetrofitTableService;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.AllTableOrders;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.AppendToOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.CreateOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.HastenDishRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.MergeOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.OperationResponse;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.RejectDishRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.ServeOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.SplitOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.SwitchTableRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.TableOrderResponse;

/**
 * 这个类所做的工作是把餐行健的数据，转换成我们的格式
 * Created by Acewill on 2016/6/3.
 */
public class CXJTableService {
    private CXJServerUrl cxjServerUrl;
    CXJRetrofitTableService internalService;

    public CXJTableService() {
        cxjServerUrl = new CXJServerUrl();
        internalService = RetrofitFactory.buildService(cxjServerUrl.getBaseUrl(), CXJRetrofitTableService.class);

    }

    public Restaurant getRestaurantInfo() throws IOException {
        CXJRetrofitTableService.RestaurantResponse r = internalService.getRestaurantName().execute().body();

        Restaurant restaurant = new Restaurant();
        restaurant.setName(r.restaurant);

        return restaurant;
    }

    public List<Order> getOrderList() throws IOException {
        AllTableOrders r = internalService.getAllTableOrders().execute().body();

        List<Order> orderList = new ArrayList<>();
        for (TableOrderResponse.OrderResponse or : r.orders) {
            Order o = new Order();

            o.setId(Long.parseLong(or.orderinfo.oid));
            o.setComment(or.orderinfo.memo);

            orderList.add(o);
        }

        return orderList;
    }

    public List<Order> getOrdersByTableId(long tableId) throws IOException {
        AllTableOrders r = internalService.getTableOrders(tableId).execute().body();

        List<Order> orderList = new ArrayList<>();
        for (TableOrderResponse.OrderResponse or : r.orders) {
            Order o = new Order();

            o.setId(Long.parseLong(or.orderinfo.oid));
            o.setComment(or.orderinfo.memo);

            orderList.add(o);
        }

        return orderList;
    }

    public Order getOrderById(long orderId) throws IOException {
        TableOrderResponse r = internalService.getOrderDetail(orderId).execute().body();

        Order o = new Order();

        o.setId(Long.parseLong(r.order.orderinfo.oid));
        o.setComment(r.order.orderinfo.memo);
        o.setCustomerAmount(Integer.parseInt(r.order.orderinfo.people));


        return o;
    }

    //设置
    public boolean setTableDirty(long tableId) throws IOException {
        OperationResponse r = internalService.setTableDirty(tableId).execute().body();

        return r.isSuccess();
    }

    /**
     * 设置开台信息
     * @param tableId
     * @return
     * @throws IOException
     */
    public boolean setTableInfo(long tableId) throws IOException {
        OperationResponse r = internalService.setTableInfo(tableId).execute().body();
        return r.isSuccess();
    }

    public boolean setTableEmpty(long tableId) throws IOException {
        OperationResponse r = internalService.setTableEmpty(tableId).execute().body();

        return r.isSuccess();
    }

    public Date getServerTimestamp() throws IOException {
        Long time = internalService.getServiceTimestamp().execute().body();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(sdf.format(new Date(time))); //1970-01-01-00:00:00


        return new Date();
    }

    public boolean getIsUpdataDb() throws IOException {
        String isUpdataDbSth = internalService.getIsUpdataDb().execute().body();
        boolean isUpdata = Boolean.parseBoolean(isUpdataDbSth);
        return isUpdata;
    }

    public Order createOrder(CreateOrderRequest request) throws IOException, PosServiceException {
        TableOrderResponse response = internalService.createOrder(request.toJson()).execute().body();

        if (response.isSuccessful()) {
            Order order = new Order();
            order.setId(Long.parseLong(response.order.orderinfo.oid));
            order.setComment(response.order.orderinfo.memo);

            return order;
        }

        throw new PosServiceException(response.msg);
    }

    public Order appendToOrder(AppendToOrderRequest request) throws IOException , PosServiceException{
        TableOrderResponse response = internalService.appendToOrder(request.toJson()).execute().body();

        if (response.isSuccessful()) {
            Order order = new Order();
            order.setId(Long.parseLong(response.order.orderinfo.oid));
            order.setComment(response.order.orderinfo.memo);

            return order;
        }

        throw new PosServiceException(response.msg);
    }

    public Order hastenDish(HastenDishRequest request) throws IOException, PosServiceException {
        TableOrderResponse response = internalService.hastenDish(request.toJson()).execute().body();

        if (response.isSuccessful()) {
            Order order = new Order();
            order.setId(Long.parseLong(response.order.orderinfo.oid));
            order.setComment(response.order.orderinfo.memo);

            return order;
        }

        throw new PosServiceException(response.msg);
    }

    public Order rejectDish(RejectDishRequest request) throws IOException,PosServiceException {
        TableOrderResponse response = internalService.rejectDish(request.getAmount(),request.getRsid(), request.getOiid(), request.getOid(), request.getAccessid()).execute().body();

        if (response.isSuccessful()) {
            Order order = new Order();
            order.setId(Long.parseLong(response.order.orderinfo.oid));
            order.setComment(response.order.orderinfo.memo);

            return order;
        }

        throw new PosServiceException(response.msg);
    }

    public Order serveOrder(ServeOrderRequest request) throws IOException, PosServiceException {
        TableOrderResponse response = internalService.serveOrder(request.toJson()).execute().body();

        if (response.isSuccessful()) {
            Order order = new Order();
            order.setId(Long.parseLong(response.order.orderinfo.oid));
            order.setComment(response.order.orderinfo.memo);

            return order;
        }

        throw new PosServiceException(response.msg);
    }

    public Order switchTable(SwitchTableRequest request) throws IOException, PosServiceException {
        TableOrderResponse response = internalService.switchTable(request.getTidFrom(),request.getTidTo(),request.getOid(),request.getAccessid()).execute().body();

        if (response.isSuccessful()) {
            Order order = new Order();
            order.setId(Long.parseLong(response.order.orderinfo.oid));
            order.setComment(response.order.orderinfo.memo);

            return order;
        }

        throw new PosServiceException(response.msg);
    }


    public boolean mergeOrder(MergeOrderRequest request) throws IOException {
        OperationResponse response = internalService.mergeOrder(request.getFromoid(), request.getTooid(), request.getBswitchtable()).execute().body();

        return response.isSuccess();
    }

    public boolean splitOrder(SplitOrderRequest request) throws IOException {
        OperationResponse response = internalService.splitOrder(request.toJson()).execute().body();

        return response.isSuccess();
    }
}
