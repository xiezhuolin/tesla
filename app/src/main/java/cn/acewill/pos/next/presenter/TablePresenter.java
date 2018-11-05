package cn.acewill.pos.next.presenter;

import java.util.List;

import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.table.Table;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.ui.DialogView;

/**
 * 桌台管理
 * Created by DHH on 2016/6/12.
 */
public class TablePresenter {
    private DialogView dialogView;
    private MyApplication myApplication;

    private Store store;

    private String appId;//商户ID
    private String brandId;//品牌ID
    private String storeId;//门店ID

    TableService systemService;
    public TablePresenter(DialogView loginView) {
        this.dialogView = loginView;
        myApplication = MyApplication.getInstance();
        store = Store.getInstance(myApplication);
        appId = store.getStoreAppId();
        brandId = store.getBrandId();
        storeId = store.getStoreId();

        try {
            systemService = TableService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    public <T> void getTableInfoWork(long id) {
        dialogView.showDialog();
        systemService.getTables(appId, brandId, storeId,id, new ResultCallback<List<Table>>() {
            @Override
            public void onResult(List<Table> result) {
                dialogView.dissDialog();
                if(result != null && result.size() >0)
                {
                    dialogView.callBackData(result);
                }
                else
                {
                    dialogView.callBackData(null);
                }
            }

            @Override
            public void onError(PosServiceException e) {
                dialogView.dissDialog();
                dialogView.showError(e);
            }
        });
    }
}
