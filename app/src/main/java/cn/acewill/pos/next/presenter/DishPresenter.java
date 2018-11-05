package cn.acewill.pos.next.presenter;

import java.util.List;

import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.ui.DialogView;

/**
 * 菜品管理
 * Created by DHH on 2016/6/12.
 */
public class DishPresenter {
    private DialogView dialogView;
    private MyApplication myApplication;

    public DishPresenter(DialogView loginView) {
        this.dialogView = loginView;
        myApplication = MyApplication.getInstance();
    }

    /**
     * 获取菜品分类数据
     * @param <T>
     */
    public <T> void getKindData() {
        dialogView.showDialog();
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }

        dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
            @Override
            public void onResult(List<DishType> result) {
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
