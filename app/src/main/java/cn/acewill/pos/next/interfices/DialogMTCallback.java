package cn.acewill.pos.next.interfices;

import java.math.BigDecimal;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.next.service.retrofit.response.ValidationResponse;

/**
 * Created by DHH on 2016/8/23.
 */
public interface DialogMTCallback  {
    public void onCheckout(BigDecimal money, boolean isCheckOut,CopyOnWriteArrayList<ValidationResponse> addValidationList);//微生活创建交易预览成功后调用
}
