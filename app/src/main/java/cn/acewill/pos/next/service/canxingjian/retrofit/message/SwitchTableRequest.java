package cn.acewill.pos.next.service.canxingjian.retrofit.message;

/**
 * Created by Acewill on 2016/6/7.
 */
public class SwitchTableRequest {

    private String tidFrom;
    private String tidTo;
    private String oid;
    private String accessid;

    public String getTidFrom() {
        return tidFrom;
    }

    public void setTidFrom(String tidFrom) {
        this.tidFrom = tidFrom;
    }

    public String getTidTo() {
        return tidTo;
    }

    public void setTidTo(String tidTo) {
        this.tidTo = tidTo;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }
}
