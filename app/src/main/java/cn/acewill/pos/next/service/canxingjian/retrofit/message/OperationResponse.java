package cn.acewill.pos.next.service.canxingjian.retrofit.message;

/**
 * Created by Acewill on 2016/6/6.
 */
public class OperationResponse {
    private Object success; //有些消息里是boolean，有些消息里是int
    private String msg;

    public boolean isSuccess() {
        if (success instanceof Boolean) {
            return (Boolean)success;
        } else if (success instanceof Integer) {
            return (Integer)success > 0;
        } else if (success instanceof Double) {
            return (Double)success > 0;
        }

        return false;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
