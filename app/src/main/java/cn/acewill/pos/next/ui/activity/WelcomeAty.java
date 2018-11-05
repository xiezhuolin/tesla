package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;

/**
 * Created by DHH on 2016/6/12.
 */
public class WelcomeAty extends BaseActivity {
    @BindView( R.id.btn_cc )
    Button btnCc;

    private Thread thread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_welcome);
        ButterKnife.bind(this);
        myApplication.addPage(WelcomeAty.this);
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                print();
//            }
//        });

        //        setImgIconState(true);
        //        btnLogin.setVisibility(View.VISIBLE);
        //        setTitle("请登录来使用本系统");

        //初始化 BufferKnife
        //        ButterKnife.bind(this);
        //        Configure.init(WelcomeAty.this);
        //        myApplication.setScreenHeight(Configure.screenHeight);
        //        myApplication.setScreenWidth(Configure.screenWidth);

        //如果临时用户中保存有用户信息直接弹出登录界面
        //        UserData mUserData = UserData.getInstance(context);
        //        if(!TextUtils.isEmpty(mUserData.getUserName()) && !TextUtils.isEmpty(mUserData.getPwd()))
        //        {
        //            startActivity(LoginAty.class);
        //        }

    }



    @OnClick( {R.id.btn_cc} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cc:
//                System.out.println("111111");
//                thread.start();
                break;

        }
    }

}
