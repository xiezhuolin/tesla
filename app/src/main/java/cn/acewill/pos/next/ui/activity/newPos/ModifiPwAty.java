package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 修改密码
 * Created by DHH on 2016/6/12.
 */
public class ModifiPwAty extends BaseActivity {
    @BindView( R.id.login_old_pw_et )
    EditText loginOldPwEt;
    @BindView( R.id.login_new_1_pw_et )
    EditText loginNew1pwEt;
    @BindView( R.id.login_new_2_pw_et )
    EditText loginNew2pwEt;
    @BindView( R.id.rel_back )
    RelativeLayout relBack;

    private UserData mUserData;
    private String userName;
    private String userPw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_modify_pw);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        myApplication.addPage(ModifiPwAty.this);
        setTitle(ToolsUtils.returnXMLStr("modify_pw"));
        setShowBtnBack(true);

        mUserData = UserData.getInstance(context);
        userName = mUserData.getRealName();
        userPw = mUserData.getPwd();
    }

    @OnClick( {R.id.tv_modify,R.id.rel_back} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_modify:
                ToolsUtils.writeUserOperationRecords("重置密码");
                checkModifyPw();
                break;
            case R.id.rel_back:
                finish();
                break;
        }
    }

    private void checkModifyPw()
    {
        String oldPw = loginOldPwEt.getText().toString().trim();
        String new1Pw = loginNew1pwEt.getText().toString().trim();
        String new2Pw = loginNew2pwEt.getText().toString().trim();
        if (TextUtils.isEmpty(oldPw)) {
            showToast(ToolsUtils.returnXMLStr("old_password_is_not_null"));
            return;
        }
        if (!oldPw.equals(userPw)) {
            showToast(ToolsUtils.returnXMLStr("input_old_password_is_error"));
            return;
        }
        if (TextUtils.isEmpty(new1Pw)) {
            showToast(ToolsUtils.returnXMLStr("new_password_is_not_null"));
            return;
        }
        if (!new1Pw.equals(new2Pw)) {
            showToast(ToolsUtils.returnXMLStr("two_passwords_are_not_consistent"));
            return;
        }
        modifyPw(oldPw,new1Pw);
    }

    private void modifyPw(String oldPw ,String newPw)
    {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.changePwd(userName, oldPw,newPw,new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if((int)result == 0)
                    {
                        showToast(ToolsUtils.returnXMLStr("modify_success"));
                        ToolsUtils.writeUserOperationRecords("跳转到login界面");
                        UserData mUserData = UserData.getInstance(context);
                        mUserData.setPwd("");
                        mUserData.setSaveStated(false);
                        myApplication.clean();
                        Intent orderIntent = new Intent(ModifiPwAty.this, LoginAty.class);
                        startActivity(orderIntent);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(ToolsUtils.returnXMLStr("modify_pw_error")+"," + e.getMessage());
                    Log.i("修改密码失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(ToolsUtils.returnXMLStr("modify_pw_error")+","+ e.getMessage());
            Log.i("修改密码失败", e.getMessage());
        }
    }
}
