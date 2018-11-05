package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acewill on 2016/6/6.
 */
public class CreateOrderRequest {
    private String accessid = "";
    private long orderidentity; //下单的标识，用时间戳
    private int mid = -1; //菜谱id
    private String username;
    private String pwd; //服务员密码
    private String people; // 人数
    private String tid; //桌台id
    private String tids; //桌台id列表
    private String ctid = ""; // 客人类型id
    private String saleuid = "";
    private String bid = ""; //预定id
    private String omids = "";//备注id列表 （每个id对应一种原因）
    private String freememo = "";
    private List<List<Object>> normalitems = new ArrayList<>(); //茶品列表
    private List<List<Object>> setmeals = new ArrayList<>(); //套餐列表
    private List<List<Object>> freeitems = new ArrayList<>(); //免费菜品

    public String toJson() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }


    public static class OrderItemRequest {
        private int bwait; //是否等叫， 0 不等，1 等，
        private String did; //菜品id
        private String duid;// 茶品单位id,
        private int amount; //数量
        private String price; //价格
        private String assistduid;
        private String assistamount;
        private String omids;
        private String freememo;
        private List<Object> cook;
        private boolean bgift;//是否免费
        private int unknown; //未知字段

        public List<Object> toArray() {
            List<Object> array = new ArrayList<>();
            array.add(bwait);
            array.add(did);
            array.add(duid);
            array.add(amount);
            array.add(price);
            array.add(assistduid);
            array.add(assistamount);
            array.add(omids);
            array.add(freememo);
            array.add(cook);
            array.add(bgift);
            array.add(unknown);

            return array;
        }

        public int getBwait() {
            return bwait;
        }

        public void setBwait(int bwait) {
            this.bwait = bwait;
        }

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getDuid() {
            return duid;
        }

        public void setDuid(String duid) {
            this.duid = duid;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getAssistduid() {
            return assistduid;
        }

        public void setAssistduid(String assistduid) {
            this.assistduid = assistduid;
        }

        public String getAssistamount() {
            return assistamount;
        }

        public void setAssistamount(String assistamount) {
            this.assistamount = assistamount;
        }

        public String getOmids() {
            return omids;
        }

        public void setOmids(String omids) {
            this.omids = omids;
        }

        public String getFreememo() {
            return freememo;
        }

        public void setFreememo(String freememo) {
            this.freememo = freememo;
        }

        public List<Object> getCook() {
            return cook;
        }

        public void setCook(List<Object> cook) {
            this.cook = cook;
        }

        public boolean isBgift() {
            return bgift;
        }

        public void setBgift(boolean bgift) {
            this.bgift = bgift;
        }

        public int getUnknown() {
            return unknown;
        }

        public void setUnknown(int unknown) {
            this.unknown = unknown;
        }
    }

    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }

    public long getOrderidentity() {
        return orderidentity;
    }

    public void setOrderidentity(long orderidentity) {
        this.orderidentity = orderidentity;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTids() {
        return tids;
    }

    public void setTids(String tids) {
        this.tids = tids;
    }

    public String getCtid() {
        return ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid;
    }

    public String getSaleuid() {
        return saleuid;
    }

    public void setSaleuid(String saleuid) {
        this.saleuid = saleuid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getOmids() {
        return omids;
    }

    public void setOmids(String omids) {
        this.omids = omids;
    }

    public String getFreememo() {
        return freememo;
    }

    public void setFreememo(String freememo) {
        this.freememo = freememo;
    }

    public List<List<Object>> getNormalitems() {
        return normalitems;
    }

    public void setNormalitems(List<List<Object>> normalitems) {
        this.normalitems = normalitems;
    }

    public List<List<Object>> getSetmeals() {
        return setmeals;
    }

    public void setSetmeals(List<List<Object>> setmeals) {
        this.setmeals = setmeals;
    }

    public List<List<Object>> getFreeitems() {
        return freeitems;
    }

    public void setFreeitems(List<List<Object>> freeitems) {
        this.freeitems = freeitems;
    }
}
