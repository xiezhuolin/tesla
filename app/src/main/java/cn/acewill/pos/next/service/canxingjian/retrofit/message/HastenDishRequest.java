package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * Created by Acewill on 2016/6/7.
 */
public class HastenDishRequest {
    private String oid;
    private String accessid;
    private boolean border;
    private String dkids;
    private List<String> amount; //格式为 ooid_数目
    private List<String> oiid;
    private String username;
    private String pwd;
    private long operationId;


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

    public String getAccessid() {
        return accessid;
    }

    public void setAccessid(String accessid) {
        this.accessid = accessid;
    }

    public boolean isBorder() {
        return border;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public String getDkids() {
        return dkids;
    }

    public void setDkids(String dkids) {
        this.dkids = dkids;
    }

    public List<String> getAmount() {
        return amount;
    }

    public void setAmount(List<String> amount) {
        this.amount = amount;
    }

    public List<String> getOiid() {
        return oiid;
    }

    public void setOiid(List<String> oiid) {
        this.oiid = oiid;
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

    public long getOperationId() {
        return operationId;
    }

    public void setOperationId(long operationId) {
        this.operationId = operationId;
    }

   // {"oid":"31","accessid":"","border":false,"dkids":"","amount":["192_2","186_1"],"oiid":["192","186"],"username":"1","pwd":"acewill","operationId":1465260820}
}
