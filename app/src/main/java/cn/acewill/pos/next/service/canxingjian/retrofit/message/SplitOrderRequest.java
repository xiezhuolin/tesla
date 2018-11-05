package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by Acewill on 2016/6/7.
 */
public class SplitOrderRequest {
    //{"oid":"34","tid":"1","newoid":"34","items":[{"oiid":"189","amount":"1"}]}
    private String oid; //订单号
    private String tid;// 桌台号
    private String newoid; //新的订单号
    private List<Item> items;

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getNewoid() {
        return newoid;
    }

    public void setNewoid(String newoid) {
        this.newoid = newoid;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String oiid;
        private String amount;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getOiid() {
            return oiid;
        }

        public void setOiid(String oiid) {
            this.oiid = oiid;
        }
    }
}
