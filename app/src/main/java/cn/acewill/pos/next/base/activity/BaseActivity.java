package cn.acewill.pos.next.base.activity;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.utils.PermissionsUtil;
import cn.acewill.pos.next.utils.WindowUtil;
import cn.acewill.pos.next.utils.sunmi.SystemUIUtils;
import cn.acewill.pos.next.widget.CatLoadingView;

/**
 * Created by DHH on 2016/6/12.
 */
public class BaseActivity extends FragmentActivity {
    public MyApplication myApplication;
    public Resources resources;
    public Context context;

    private LinearLayout contentRl;
    public TextView textTitle;
//    public ImageView imgIcon;
    private ImageView imgRightIcon;
    public TextView tvLogin;
    public RelativeLayout rel_back;
    public RelativeLayout rel_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_base);
        //初始化 上下文对象
        context = BaseActivity.this;

//        //友盟
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        //初始化 MyApplication
        myApplication = MyApplication.getInstance();
        //初始化 Resources
        resources = getResources();

        contentRl = (LinearLayout) findViewById(R.id.contentRl);
        rel_back = (RelativeLayout) findViewById(R.id.rel_back);
        rel_title = (RelativeLayout) findViewById(R.id.rel_title);
        textTitle = (TextView) findViewById(R.id.textTitle);
        imgRightIcon = (ImageView) findViewById(R.id.img_rightIcon);
        tvLogin = (TextView) findViewById(R.id.tv_login);

        rel_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                WindowUtil.hiddenKey();
            }

//            else {
//                WindowUtil.showKey(context,(EditText) v);
//            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }


    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
//            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),0);
            int[] leftTop = { 0, 0 };
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件

                return false;
            } else {
                return true;
            }
            }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        WindowUtil.hiddenKey();
    }

    @Override
    public void onBackPressed() {
        WindowUtil.hiddenKey();
        super.onBackPressed();
    }

    public void setContentXml(int layoutID) {
        addViewXML(contentRl, layoutID, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    public void addViewXML(ViewGroup group, int id, int width, int height) {
        View contentView = View.inflate(this, id, null);
        group.addView(contentView, width, height);
    }

    public void setShowBtnBack(boolean isShow)
    {
        rel_back.setVisibility(isShow == true ? View.VISIBLE:View.INVISIBLE);
    }

    public void setTitle(String str) {
        if (str == null || TextUtils.isEmpty(str)) {
            textTitle.setText(R.string.app_name);
        } else {
            textTitle.setText(str);
        }
    }

    public void setRightText(String str)
    {
        if (str == null || TextUtils.isEmpty(str)) {
            tvLogin.setVisibility(View.GONE);
        } else {
            tvLogin.setVisibility(View.VISIBLE);
            tvLogin.setText(str);
        }
    }

    public void setRightImage(int imageResId)
    {
        if(imageResId < 0)
        {
            imgRightIcon.setVisibility(View.GONE);
        }
        else
        {
            imgRightIcon.setVisibility(View.VISIBLE);
            imgRightIcon.setBackgroundResource(imageResId);
        }
    }

    //申请SD卡与电话权限
    public void requestPermisions(){
        PermissionsUtil.checkPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode== PermissionsUtil.PERMISSION_REQUEST_CODE){
            if(grantResults!=null&&grantResults.length>0){
                for (int i = 0; i < grantResults.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        //未开启相关权限
                        //MyApplication.getInstance().exit();
                        showToast("请打开设置-应用-开启SD卡与电话权限");
                    }
                }
            }
        }
    }

    /**
     * activity跳转
     *
     * @param clas activity类
     */
    protected void startActivity(Class<? extends BaseActivity> clas) {
        if (clas == null)
            return;
        Intent intent = new Intent(this, clas);
        startActivity(intent);
    }

    /**
     * 跳转Activity
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    public void showToast(String str) {
        myApplication.ShowToast(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myApplication.popActivity(this);
    }

    CatLoadingView mView;
    public void showProgress()
    {
        try {
            mView = new CatLoadingView();
            mView.show(getSupportFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgress(String str)
    {
        try {
            mView = new CatLoadingView();
            if(!TextUtils.isEmpty(str))
            {
                mView.setText(str);
            }
            mView.show(getSupportFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void dissmiss() {
        try {
            if(mView != null)
            {
                mView.setText(resources.getString(R.string.sth_loading));
                mView.dismiss();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SystemUIUtils.setStickFullScreen(getWindow().getDecorView());
    }

}
