package cn.acewill.pos.next.service.retrofit;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.pos.next.model.WftRespOnse;
import cn.acewill.pos.next.model.order.CardRecord;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.service.retrofit.response.KDSResponse;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.service.retrofit.response.pay.BaseWechatPayResult;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Acewill on 2016/6/17.
 */
public interface RetrofitOrderService {
    @GET( "api/orders/today" )
        //    @GET("management/orders")
    Observable<PosResponse<List<Order>>> getAllOrders(@Query( "token" ) String token, @Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "page" ) int page, @Query( "limit" ) int limit, @Query( "orderType" ) int orderType, @Query( "payStatus" ) int payStatus
            , @Query( "sort" ) String sort, @Query( "direction" ) String direction);

    @GET( "api/orders" )
    Observable<PosResponse<Order>> getOrderInfoById(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderId" ) String orderId);

    @GET( "api/orders/nextOrderId" )
    Observable<PosResponse<Long>> getNextOrderId(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId);

    @GET( "management/orders/{oid}" )
    Observable<PosResponse<Order>> getOrderById(@Path( "oid" ) long oid);

    @GET( "management/orders/table/{tid}" )
    Observable<PosResponse<List<Order>>> getOrdersByTableId(@Path( "tid" ) long tid);

    @POST( "api/orders/create" )
    Observable<PosResponse<Order>> createOrder(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Body Order order);

    @POST( "/api/orders/getUnpaiedOrderUnderTable" )
    Observable<PosResponse<Order>> getUnpaiedOrderUnderTable(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "tableId" ) long tableId);

    //    @FormUrlEncoded
    //    @POST("management/orders")
    //    Observable<PosResponse<Order>> createOrder(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Field( "order" ) String order);

    @POST( "selfpos/management/orders/append" )
    Observable<PosResponse<Integer>> appendOrder(@Query( "id" ) long oid, @Body List<OrderItem> orderItmes);

    @POST( "selfpos/management/orders/merge" )
    Observable<PosResponse<Integer>> margeOrder(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "fromOrderId" ) long fromOrderId, @Query( "toOrderId" ) long toOrderId);

    @POST( "AcewillKDS/newOrder.do" )
    Observable<KDSResponse> newOrderKds(@Query( "orderdata" ) String orderdata);

    @POST( "AcewillKDS/deleteDishesInOrder.do" )
    Observable<KDSResponse> deleteDishKds(@Query( "deletedishes" ) String deletedishes);

    @POST( "AcewillKDS/deleteOrder.do" )
    Observable<KDSResponse> deleteOrderKds(@Query( "oid" ) String oid);

    @POST( "AcewillKDS/connectionCheck.do" )
    Observable<KDSResponse> connectionCheck();

    /**
     * 换台
     *
     * @return
     */
    @POST( "AcewillKDS/changOrderTable.do" )
    Observable<KDSResponse> changOrderTable(@Query( "oid" ) String oid,
                                            @Query( "newTableName" ) String newTableName);

    /**
     * 查询网上的订单
     *
     * @param appId
     * @param storeId
     * @param preordertime 单位分钟；是指查询离就餐还有多少时间的订单
     * @return
     */
    @POST( "api/terminal/syncNetOrders" )
    Observable<PosResponse<List<Order>>> syncNetOrders(@Query( "appid" ) String appId, @Query( "storeid" ) String storeId, @Query( "preordertime" ) Integer preordertime);

    /**
     *
     * 确认接收网上的订单
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param netorderid 网上订单的订单号
     * @param callnumber 取餐号
     * @param terminalid 终端id
     * @return
     */
    @POST( "api/terminal/confirmNetOrder" )
    Observable<PosResponse<List<Order>>> confirmNetOrder(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId, @Query( "netorderid" ) String netorderid, @Query( "orderid" ) String orderid, @Query( "callnumber" ) String callnumber, @Query( "terminalid" ) String terminalid);

    /**
     * 拒绝网上的订单
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param netorderid   网上订单的订单号
     * @param rejectreason 拒绝的理由
     * @return
     */
    @POST( "api/terminal/rejectNetOrder" )
    Observable<PosResponse<List<Order>>> rejectNetOrder(@Query( "appid" ) String appId, @Query( "brandid" ) String brandId, @Query( "storeid" ) String storeId, @Query( "netorderid" ) String netorderid, @Query( "rejectreason" ) String rejectreason);

    /**
     * 将挂起的订单下单
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @return
     */
    @POST( "api/orders/resume" )
    Observable<PosResponse<Integer>> resumeOrder(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderId" ) long orderId);


    /**
     * 下单后修改菜品价格
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderItemId
     * @param newCost
     * @return
     */
    @POST( "/api/orders/item/cost" )
    Observable<PosResponse> updataOrderCost(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderItemId" ) long orderItemId, @Query( "newCost" ) BigDecimal newCost);


    @POST( "/api/orders/endDailyBusiness" )
    Observable<PosResponse> endDailyBusiness(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId);

    /**
     * 验证会员
     */
    @POST( "api/orders/validateUserAndCommitDeal" )
    Observable<PosResponse> validateUserCommitDeal(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "account" ) String account, @Query( "amount" ) int amount);

    @GET( "api/orders/todaySummary" )
    Observable<MemberResponse> getMemberOrderList(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId,
                                                  @Query( "storeId" ) String storeId, @Query( "page" ) int page, @Query( "limit" ) int limit, @Query( "sort" ) String sort, @Query( "direction" ) String direction);

    /**
     * 检测是否取餐
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @return
     */
    @POST( "api/orders/checkServeStatus" )
    Observable<PosResponse> checkServerStatus(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderId" ) long orderId);

    /**
     * 查询历史订单
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param startDate
     * @param endDate
     * @return
     */
    @POST( "api/terminal/getOrderHistoryByDate" )
    Observable<PosResponse<List<Order>>> getOrderHistoryInfo(@Query( "appid" ) String appId,
                                                             @Query( "brandid" ) String brandId,
                                                             @Query( "storeid" ) String storeId,
                                                             @Query( "startDate" ) String startDate,
                                                             @Query( "endDate" ) String endDate);


    /**
     * 正扫威富通生成二维码
     *
     * @param appId
     * @param storeId
     * @param ipAddress
     * @param body
     * @param total_fee
     * @param orderId
     * @param device_info
     * @param op_user_id
     * @param op_device_id
     * @param pay_type
     * @return
     */
    @POST( "api/swiftpass/createCodeUrl" )
    Observable<PosResponse<WftRespOnse>> createCodeUrl(@Query( "appId" ) String appId,
                                                       @Query( "storeId" ) String storeId,
                                                       @Query( "op_shop_id" ) String op_shop_id,
                                                       @Query( "mch_create_ip" ) String ipAddress,
                                                       @Query( "body" ) String body,
                                                       @Query( "total_fee" ) Integer total_fee,
                                                       @Query( "out_trade_no" ) String orderId,
                                                       @Query( "device_info" ) String device_info,
                                                       @Query( "op_user_id" ) String op_user_id,
                                                       @Query( "op_device_id" ) String op_device_id,
                                                       @Query( "pay_type" ) Integer pay_type);


    /**
     * 挂账
     *
     * @param appId
     * @param brandId
     * @param storeId
     * @param orderId
     * @return
     */
    @POST( "api/orders/chargeOffs" )
    Observable<PosResponse> chargeOffs(@Query( "appId" ) String appId, @Query( "brandId" ) String brandId, @Query( "storeId" ) String storeId, @Query( "orderId" ) long orderId, @Query( "status" ) Integer status);


    /**
     * 获取已挂账列表
     * @param appId
     * @param brandId
     * @param storeId
     * @return
     */
    @POST( "api/orders/cardRecords" )
    Observable<PosResponse<List<CardRecord>>> getCardRecords(@Query( "appId" ) String appId,
                                                             @Query( "brandId" ) String brandId,
                                                             @Query( "storeId" ) String storeId);


    /**
     * 微信刷卡支付
     * @param cacheControl
     * @param appId
     * @param brandId
     * @param storeId
     * @param outTradeNo
     * @param totalFee
     * @param body
     * @param authCode
     * @param paymentStr
     * @param timeStart
     * @param timeExpire
     * @param deviceInfo
     * @param spbillCreateIp
     * @return
     */
    @POST("/api/wxpay/micropayToWepay")
    Observable<BaseWechatPayResult> micropayToWepay(
            @Header("Cache-Control") String cacheControl,
            @Query("appId") String appId,
            @Query("brandId") String brandId,
            @Query("storeId") String storeId,
            @Query("outTradeNo") String outTradeNo,
            @Query("totalFee") int totalFee,
            @Query("body") String body,
            @Query("authCode") String authCode,
            @Query("paymentStr") String paymentStr,
            @Query("timeStart") String timeStart,
            @Query("timeExpire") String timeExpire,
            @Query("deviceInfo") String deviceInfo,
            @Query("spbillCreateIp") String spbillCreateIp);

    /**
     * 查询微信支付结果
     * @param cacheControl
     * @param appId
     * @param brandId
     * @param storeId
     * @param outTradeNo
     * @param paymentStr
     * @return
     */
    @POST("/api/wxpay/orderQuery")
    Observable<BaseWechatPayResult> queryWechatPayResult(
            @Header("Cache-Control") String cacheControl,
            @Query("appId") String appId,
            @Query("brandId") String brandId,
            @Query("storeId") String storeId,
            @Query("outTradeNo") String outTradeNo,
            @Query("paymentStr") String paymentStr);

    /**
     * 微信扫码支付
     * @param cacheControl
     * @param appId
     * @param brandId
     * @param storeId
     * @param outTradeNo
     * @param totalFee
     * @param spbillCreateIp
     * @param body
     * @param paymentStr
     * @return
     */
    @POST("/api/wxpay/unifiedOrderToWepay")
    Observable<BaseWechatPayResult> unifiedOrderToWepay(
            @Header("Cache-Control") String cacheControl,
            @Query("appId") String appId,
            @Query("brandId") String brandId,
            @Query("storeId") String storeId,
            @Query("outTradeNo") String outTradeNo,
            @Query("totalFee") int totalFee,
            @Query("spbillCreateIp") String spbillCreateIp,
            @Query("body") String body,
            @Query("paymentStr") String paymentStr);



}
