package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Acewill on 2016/6/7.
 */
public class ServeOrderRequest {
    private String oid;
    private String accessid;

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
}
