package cn.acewill.pos.next.service.canxingjian.retrofit.message;

/**
 * Created by Acewill on 2016/6/7.
 */
public class RejectDishRequest {
    //amount:2, rsid:8 ,oiid:194 ,oid:31 ,accessid:
    private int amount;
    private int rsid;
    private long oiid;
    private long oid;
    private String accessid;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRsid() {
        return rsid;
    }

    public void setRsid(int rsid) {
        this.rsid = rsid;
    }

    public long getOiid() {
        return oiid;
    }

    public void setOiid(long oiid) {
        this.oiid = oiid;
    }

    public long getOid() {
        return oid;
    }

    public void setOid(long oid) {
        this.oid = oid;
    }

    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }
}
