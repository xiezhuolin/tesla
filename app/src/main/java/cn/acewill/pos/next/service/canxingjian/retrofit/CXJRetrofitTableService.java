package cn.acewill.pos.next.service.canxingjian.retrofit;

import java.util.List;

import cn.acewill.pos.next.service.canxingjian.retrofit.message.AllTableOrders;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.OperationResponse;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.TableOrderResponse;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.TableRespone;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;


/**
 * Created by Acewill on 2016/6/3.
 *
 */
public interface CXJRetrofitTableService {

    /**
     * 返回[["1","3","-1","1","0",""],["2","3","-1","1","0",""]]
     */
//    @GET("order/api/api.php?do=getTablesStatus&app=ClientAndroidMobile")
//    Call<List<List<String>>> getTablesStatus();
    @FormUrlEncoded
    @POST("selfpos/management/tables")
    Observable<Response<TableRespone>> setTableDirty(@Field("appId") String appId, @Field("brandId") String brandId, @Field("storeId") String storeId);

    /**
     * 返回消息 {"success":true} 或者 {"success":false,"msg":"服务器内部错误，请与管理员联系,谢谢"}
     * @param tableId
     * @return
     */
    @FormUrlEncoded
    @POST("order/apps/order/current.php?do=setTableDirty")
    Call<OperationResponse> setTableDirty(@Field("tid") long tableId);

    /**
     * 返回消息 {"success":true} 或者 {"success":false,"msg":"服务器内部错误，请与管理员联系,谢谢"}
     * @param tableId
     * @return
     */
    @FormUrlEncoded
    @POST("/order/api/api.php?do=setOrderInfo&app=ClientNewAndroidMobile")
    Call<OperationResponse> setTableInfo(@Field("tid") long tableId);

    /**
     * 返回消息 {"success":true} 或者 {"success":false,"msg":"服务器内部错误，请与管理员联系,谢谢"}
     * @param tableId
     * @return
     */
    @FormUrlEncoded
    @POST("order/apps/order/current.php?do=setTableEmpty")
    Call<OperationResponse> setTableEmpty(@Field("tid") long tableId);

    public static class RestaurantResponse {
        public boolean success;
        public String restaurant;
    }

    //TODO
    @GET("order/api/api.php?do=getRrestantName&app=ClientTouch")
    Call<RestaurantResponse> getRestaurantName();

    /**
     * 所有桌台订单信息
     */
    @GET("order/api/api.php?do=getAllTableOrderDetail&app=ClientNewAndroidMobile")
    Call<AllTableOrders> getAllTableOrders();

    /**
     * 某一桌台订单信息(只返回概要信息), 一个桌台上可能有多个订单
     * 返回{"success":1,"orders":[{"orderinfo":{"oid":"31","people":"1"},"orderitems":[]}]}
     */
    @FormUrlEncoded
    @POST("/order/api/api.php?do=getTableOrderDetail&app=ClientNewAndroidMobile")
    Call<AllTableOrders> getTableOrders(@Field("tid") long tableId);

    /**
     * 获取订单信息
     * 返回消息 {"success":1,"order":{"orderinfo":{"oid":"31","people":"1","tids":"2","ctid":null,"newuid":"1","newtime":"2016-05-09 12:58:08","memo":null,"cost":28,"orderstatus":"2","total":28,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":28,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["186","2","1","0","1","2016-05-09 12:58:08","49",28,"2",1,null,null,[],"",null,"",28,28,0,null,"\u7ba1\u7406\u5458"]]}}
     */
    @FormUrlEncoded
    @POST("/order/api/api.php?do=getOrderDetail&app=ClientNewAndroidMobile")
    Call<TableOrderResponse> getOrderDetail(@Field("oid") long orderId);

    public static final String GETSERVERTIMESTAMP = ""; // 时间戳接口

    /**
     * Unix时间戳，1970-1-1到当前时间的秒数
     * @return
     */
    @GET("/order/api/api.php?do=getServerTimestamp&app=ClientAndroidMobile")
    Call<Long> getServiceTimestamp();

    /**
     * 请求数据： jsondata:{"accessid":"","orderidentity":1465189403,"mid":-1,"username":"1","pwd":"1","people":"7","tid":"2","tids":"2","ctid":"",
     * "saleuid":"","bid":"","omids":"","freememo":"","normalitems":[[0,"64","3",1,"26.00","","","","",[],false,1]],"setmeals":[],"freeitems":[]}
     * 响应数据: {"success":1,"order":{"orderinfo":{"oid":34,"people":"7","tids":"2","ctid":null,"newuid":"1","newtime":"2016-06-06 13:03:24","memo":null,"cost":0,"orderstatus":"1","total":26,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":0,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["189","2","1","0","1","2016-06-06 13:03:24","64",26,"3",1,null,null,[],"",null,"",26,0,0,null,"\u7ba1\u7406\u5458"]]}}
     * /
     */
    @FormUrlEncoded
    @POST("order/api/api.php?app=clientTouch&do=newOrder")
    Call<TableOrderResponse>   createOrder(@Field("jsondata") String jsonData);

    /**
     * 是否更新数据库
     * @return
     */
    @GET("order/api/api.php?do=isSqliteFileUpdated&app=ClientNewAndroidMobile")
    Call<String> getIsUpdataDb();

    /**
     *  请求form， tids 和 tid
     * 返回 {"success":1}
     */
    @POST("order/api/api.php?app=clientTouch&do=updateOperationTable")
    Call<OperationResponse>   updateTable();

    /**
     * 请求和返回跟updateTable一样
     */
    @POST("order/api/api.php?app=clientTouch&do=checkAndLockTable")
    Call<OperationResponse>   lockTable();

    /**
     * ["订单id","桌台id","桌台名称"]
     * 返回[["32","1","1"],["33","1","1"],["34","2","\u6d4b\u8bd5\u684c\u53f0"],["35","2","\u6d4b\u8bd5\u684c\u53f0"],["36","1","1"],["31","2","\u6d4b\u8bd5\u684c\u53f0"]]
     * 返回所有的订单概要信息
     */
    @GET("order/api/api.php?do=getAllTableOid&app=ClientAndroidMobile")
    Call<List<List<String>>> getAllOrderSummary();

     /**
      * 输入： jsondata:{"oid":"31","accessid":"","border":false,"dkids":"","amount":["192_2","186_1"],"oiid":["192","186"],"username":"1","pwd":"acewill","operationId":1465260820}
      * 返回： {"success":true,"order":{"orderinfo":{"oid":"31","people":"1","tids":"2","ctid":null,"newuid":"1","newtime":"2016-05-09 12:58:08","memo":null,"cost":28,"orderstatus":"1","total":80,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":28,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["186","2","1","0","1","2016-05-09 12:58:08","49",28,"2",1,null,null,[],"",null,"",28,28,0,null,"\u7ba1\u7406\u5458"],["192","2","1","0","1","2016-06-07 08:51:13","66",26,"3",2,null,null,[],"",null,"",52,0,0,null,"\u7ba1\u7406\u5458"]]}}
     * 催菜
     */
     @FormUrlEncoded
    @POST("order/api/api.php?do=kitchenNoticeHasten&app=ClientTouch")
    Call<TableOrderResponse> hastenDish(@Field("jsondata") String orderItems);

    /**
     * 输入： amount:2, rsid:8 ,oiid:194 ,oid:31 ,accessid:
     * 返回：{"success":true,"order":{"orderinfo":{"oid":"31","people":"1","tids":"2","ctid":null,"newuid":"1","newtime":"2016-05-09 12:58:08","memo":null,"cost":28,"orderstatus":"1","total":132,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":28,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["186","2","1","0","1","2016-05-09 12:58:08","49",28,"2",1,null,null,[],"",null,"",28,28,0,null,"\u7ba1\u7406\u5458"],["192","2","1","0","1","2016-06-07 08:51:13","66",26,"3",2,null,null,[],"",null,"",52,0,0,null,"\u7ba1\u7406\u5458"],["193","2","1","0","1","2016-06-07 09:01:19","65",26,"3",2,null,null,[],"",null,"",52,0,0,null,"\u7ba1\u7406\u5458"],["194","3","1","0","1","2016-06-07 09:02:04","68",26,"3",2,null,null,[],"",null,"",0,0,0,null,"\u7ba1\u7406\u5458"]]}}
     *      或者： {"success":0,"msg":"订单项[阳光鲜橙]状态为[退菜]，不能执行此操作"}
     * 退菜
     */
    @FormUrlEncoded
    @POST("order/api/api.php?do=rejectDish&app=ClientTouch")
    Call<TableOrderResponse> rejectDish(@Field("amount") int amount,
                                        @Field("rsid") int rsid, //退菜原因
                                        @Field("oiid") long oiid,
                                        @Field("oid") long oid,
                                        @Field("accessid") String accessid);

    /**
     * jsondata:{"oid":"37","accessid":"","orderidentity":1465266385,"username":"1","pwd":"1","payinfo":[],"recieve":"26.00","change":"0.00","advance":0,"ptid":"-1","accountpid":"","cardmbid":"","cardcode":"","password":"","bcostcredit":0,"paytypemoney":"","number":"","bsnack":0,"binvoice":0,"bprint":1,"alipaycode":"","dpids":[]}
     * 返回： {"success":1}
     */
     @FormUrlEncoded
     @POST("order/api/api.php?app=clientTouch&do=checkOut")
     Call<OperationResponse>   checkOut(@Field("jsondata") String jsonData);

    /**
     * 输入： tidFrom:2
     * tidTo:1
     * oid:35
     * accessid:
     * 返回：{"success":true,"order":{"orderinfo":{"oid":"35","people":"62","tids":"1","ctid":null,"newuid":"1","newtime":"2016-06-06 17:27:42","memo":null,"cost":0,"orderstatus":"1","total":52,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":0,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["190","2","1","0","1","2016-06-06 17:27:42","64",26,"3",2,null,null,[],"",null,"",52,0,0,null,"\u7ba1\u7406\u5458"]]}}
     * 或者 {"success":0,"msg":"原桌台[测试桌台]不属于订单[31]，请核查"}
     */
    @FormUrlEncoded
    @POST("order/api/api.php?do=switchTable&app=clientTouch")
    Call<TableOrderResponse>   switchTable(@Field("tidFrom") String from, @Field("tidTo") String to,@Field("oid") String oid, @Field("accessid") String accessid);


    /**
     *
     * jsondata:{"oid":"31","accessid":""}
     * 返回： {"success":true,"order":{"orderinfo":{"oid":"31","people":"1","tids":"2","ctid":null,"newuid":"1","newtime":"2016-05-09 12:58:08","memo":null,"cost":28,"orderstatus":"1","total":132,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":28,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["186","2","1","0","1","2016-05-09 12:58:08","49",28,"2",1,null,null,[],"",null,"",28,28,0,null,"\u7ba1\u7406\u5458"],["192","2","1","0","1","2016-06-07 08:51:13","66",26,"3",2,null,null,[],"",null,"",52,0,0,null,"\u7ba1\u7406\u5458"],["193","2","1","0","1","2016-06-07 09:01:19","65",26,"3",2,null,null,[],"",null,"",52,0,0,null,"\u7ba1\u7406\u5458"],["194","3","1","0","1","2016-06-07 09:02:04","68",26,"3",2,null,null,[],"",null,"",0,0,0,null,"\u7ba1\u7406\u5458"]]}}
     */
     @FormUrlEncoded
     @POST("order/api/api.php?app=clientTouch&do=kitchenNoticeServeAll")
     Call<TableOrderResponse>   serveOrder(@Field("jsondata") String jsonData);

    /**
     * 输入： fromoid:38
     * tooid:34
     * bswitchtable:1 是否并台
     * 返回：{"success":1}
     * 并台
     */
    @FormUrlEncoded
    @POST("order/api/api.php?do=mergeOrder&app=clientTouch")
    Call<OperationResponse>   mergeOrder(@Field("fromoid") String from, @Field("tooid") String to, @Field("bswitchtable") String jsonData);

    /**
     * 输入：jsondata:{"oid":"34","tid":"1","newoid":"34","items":[{"oiid":"189","amount":"1"}]}
     * 返回：{"success":true} 或者 {"success":0,"msg":"[转单]操作的两个订单相同，不能执行此操作"}
     *
     /**
     * 分单, 把一个单里的菜品分到其他单中
     */
    @FormUrlEncoded
    @POST("order/api/api.php?app=clientTouch&do=transferlist")
    Call<OperationResponse>   splitOrder(@Field("jsondata") String jsondata);

    /**
     * jsondata:{"accessid":"","orderidentity":1465280349,"mid":-1,"username":"1","pwd":"1","people":"3","tid":"2","tids":"2","ctid":"","saleuid":"","bid":"","omids":"","freememo":"","normalitems":[[0,"66","3",1,"26.00","","","","",[],false,1]],"setmeals":[],"freeitems":[],"oid":"40"}
     */
    @FormUrlEncoded
    @POST("order/api/api.php?app=clientTouch&do=appendOrder")
    Call<TableOrderResponse>   appendToOrder(@Field("jsondata") String jsonData);
}
