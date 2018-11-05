package cn.acewill.pos.next.model;

/**
 * Created by aqw on 2016/12/15.
 */
public enum  PaymentCategory {

    CASH(0), //现金类型
    NON_CASH(1); //非现金类型

    private int category;
    PaymentCategory(int category) {
        this.category = category;
    }
}
