package cn.acewill.pos.next.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class AboutFragment extends BaseFragment {
    @BindView( R.id.tv_appVersion )
    TextView tvAppVersion;
    @BindView( R.id.device_Name )
    TextView devieName;
    @BindView( R.id.user_Name )
    TextView userName;
    @BindView( R.id.server_address )
    TextView serverAddress;
    @BindView( R.id.unbind_btn )
    TextView unbindBtn;

    private Store store;
    private PosInfo posInfo;
    private UserData mUserData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        store = Store.getInstance(aty);
        mUserData = UserData.getInstance(aty);
        posInfo = PosInfo.getInstance();
        tvAppVersion.setText("V " + ToolsUtils.getVersionName(aty));
        devieName.setText(store.getTerminalMac());
        userName.setText(mUserData.getRealName());
        serverAddress.setText(posInfo.getServerUrl());
    }

    @OnClick( R.id.unbind_btn )
    public void onClick() {
        unBindStore();
    }

    /**
     * 解绑门店
     */
    private void unBindStore() {
        DialogUtil.ordinaryDialog(aty, ToolsUtils.returnXMLStr("unbind"), ToolsUtils.returnXMLStr("are_you_sure_unbind"), new DialogCallback() {
            @Override
            public void onConfirm() {
                String userName = mUserData.getUserName();
                String userPwd = mUserData.getPwd();
                String terminalMac = posInfo.getTerminalMac();
                try {
                    StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
                    storeBusinessService.unbindStore(userName, userPwd, terminalMac, new ResultCallback<Integer>() {
                        @Override
                        public void onResult(Integer result) {
                            if (result == 0) {
                                showToast(ToolsUtils.returnXMLStr("unbind_success"));
                                store.setDeviceName("");
                                StoreInfor.terminalInfo = null;
                                store.setTerminalMac("");
                                jumpLogin();
                            }
                        }

                        @Override
                        public void onError(PosServiceException e) {
                            showToast(ToolsUtils.returnXMLStr("unbind_failure")+"," + e.getMessage());
                            Log.i("解绑门店信息失败", e.getMessage());
                        }
                    });
                } catch (PosServiceException e) {
                    e.printStackTrace();
                    showToast(ToolsUtils.returnXMLStr("unbind_failure")+"," + e.getMessage());
                    Log.i("解绑门店信息失败", e.getMessage());
                }
            }

            @Override
            public void onCancle() {

            }
        });
    }

    /**
     * 跳转到Login界面
     */
    private void jumpLogin() {
        ToolsUtils.writeUserOperationRecords("跳转到login界面");
        UserData mUserData = UserData.getInstance(aty);
        mUserData.setUserName("");
        mUserData.setPwd("");
        mUserData.setSaveStated(false);
        errorReturnLogin();
    }

    /**
     * 当出错时,退回到LoginAty重新登录
     */
    private void errorReturnLogin() {
        //关闭轮训获取网上订单的开关
        MyApplication.getInstance().setConFirmNetOrder(false);
        TimerTaskController.getInstance().cancleTimter(true);//关闭轮训timer
        myApplication.unbindCashBoxServer();
        myApplication.soundPool.stop(1);
        myApplication.clean();
        Intent orderIntent = new Intent(aty, LoginAty.class);
        orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(orderIntent);
    }

}
