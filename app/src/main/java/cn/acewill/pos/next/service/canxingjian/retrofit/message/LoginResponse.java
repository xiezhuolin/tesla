package cn.acewill.pos.next.service.canxingjian.retrofit.message;

/**
 * Created by Acewill on 2016/6/6.
 */
public class LoginResponse {
    private int success;
    private String username;
    private String rname;
    private String uid;
    private int[] funcodes;
    private String msg;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int[] getFuncodes() {
        return funcodes;
    }

    public void setFuncodes(int[] funcodes) {
        this.funcodes = funcodes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
