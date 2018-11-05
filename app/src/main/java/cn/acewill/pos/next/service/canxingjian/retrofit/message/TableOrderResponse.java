package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import java.util.List;

/**
 * 单个订单的返回
 * Created by Acewill on 2016/6/7.
 */
public class TableOrderResponse {
    public Object success; //可能是boolean，也可能是int
    public OrderResponse order;
    public String msg;

    public boolean isSuccessful() {
        if (success instanceof Boolean) {
            return (Boolean)success;
        } else {
            return (Double)success > 0;
        }
    }

    public static class OrderResponse {
        public OrderInfoResponse orderinfo;
        public List<List<Object>> orderitems;
    }

    public static class OrderInfoResponse {
        public String oid;
        public String people;
        public String tids;
        public String ctid;
        public String newuid;
        public String newtime;
        public String memo;
        public int cost;
        public String orderstatus;
    }
}
