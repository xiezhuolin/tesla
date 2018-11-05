package cn.acewill.pos.next.service.canxingjian.retrofit.message;

/**
 * Created by Acewill on 2016/6/7.
 */
public class MergeOrderRequest {
    private String fromoid;
    private String tooid;
    private String bswitchtable;// 1 并台

    public String getFromoid() {
        return fromoid;
    }

    public void setFromoid(String fromoid) {
        this.fromoid = fromoid;
    }

    public String getTooid() {
        return tooid;
    }

    public void setTooid(String tooid) {
        this.tooid = tooid;
    }

    public String getBswitchtable() {
        return bswitchtable;
    }

    public void setBswitchtable(String bswitchtable) {
        this.bswitchtable = bswitchtable;
    }
}
