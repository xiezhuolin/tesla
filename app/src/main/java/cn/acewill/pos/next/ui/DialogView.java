package cn.acewill.pos.next.ui;


import cn.acewill.pos.next.exception.PosServiceException;

/**
 * Created by DHH on 2016/6/12.
 */
public interface DialogView {
    void showDialog();
    void dissDialog();
    void showError(PosServiceException e);
    <T> void callBackData(T t);
}
