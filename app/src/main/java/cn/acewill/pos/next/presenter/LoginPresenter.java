package cn.acewill.pos.next.presenter;

import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.user.User;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.DialogView;

/**
 * 登陆管理
 * Created by DHH on 2016/6/12.
 */
public class LoginPresenter {
    private DialogView dialogView;
    private MyApplication myApplication;
    private SystemService systemService = null;

    public LoginPresenter(DialogView loginView) {
        this.dialogView = loginView;
        myApplication = MyApplication.getInstance();
    }

    public <T> void getLoginWork(final String userName,final String pwd) {
        dialogView.showDialog();
        systemService = null;
        try {
            systemService = SystemService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        systemService.terminalLogin(new ResultCallback() {
            @Override
            public void onResult(Object result) {
                int code = (Integer)result;
                if(code ==0)
                {
                    systemService.login(userName, pwd, new ResultCallback<User>() {
                        @Override
                        public void onResult(User user) {
                            PowerController.powerIds = user.getUserRet().authorityIDs;//权限id列表
                            dialogView.dissDialog();
                            dialogView.callBackData(user);
                        }

                        @Override
                        public void onError(PosServiceException e) {
                            dialogView.dissDialog();
                            dialogView.showError(e);
                        }
                    });
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
