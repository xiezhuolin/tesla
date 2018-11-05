package cn.acewill.pos.next.service.retrofit.response;

import java.util.List;

import cn.acewill.pos.next.model.payment.Payment;

/**
 * Created by Acewill on 2016/8/3.
 */
public class PaymentTypesResponse extends PosResponse {
    private List<Payment> paymentTypes;

    public List<Payment> getPaymentTypes() {
        return paymentTypes;
    }

    public void setPaymentTypes(List<Payment> paymentTypes) {
        this.paymentTypes = paymentTypes;
    }
}
