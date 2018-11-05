package cn.acewill.pos.next.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * 正反扫设置
 * Created by aqw on 2016/12/8.
 */
public class LanguageFragment extends BaseFragment {

    @BindView(R.id.z_zh_tv)
    ComTextView zScanTv;
    @BindView(R.id.z_scan_ll)
    LinearLayout zScanLl;
    @BindView(R.id.f_en_tv)
    ComTextView fScanTv;
    @BindView(R.id.f_scan_ll)
    LinearLayout fScanLl;

    Store store;
    MyApplication myApplication;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_set, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        store = Store.getInstance(mContext);
        String language = store.getLanguageSett();
        myApplication = MyApplication.getInstance();
        if(language.equals(Constant.LANGUAGE_CHINESE))//中文
        {
            setSelect(true);
        }
        else if(language.equals(Constant.LANGUAGE_ENGLISH))//英文
        {
            setSelect(false);
        }
    }


    @OnClick({R.id.z_scan_ll, R.id.f_scan_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.z_scan_ll://中文
                setSelect(true);
                store.setLanguageSett(Constant.LANGUAGE_CHINESE);
                exitApp();
                break;
            case R.id.f_scan_ll://英语
                setSelect(false);
                store.setLanguageSett(Constant.LANGUAGE_ENGLISH);
                exitApp();
                break;
        }
    }

    private void exitApp()
    {
        myApplication.unbindCashBoxServer();
        myApplication.soundPool.stop(1);
        myApplication.clean();
        Intent orderIntent = new Intent(getActivity(), LoginAty.class);
        orderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(orderIntent);

        // 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    //更新选择语言
    private void setSelect(boolean isFront){
        if(isFront){//选择中文
            zScanLl.setSelected(true);
            zScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.white));

            fScanLl.setSelected(false);
            fScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.order_day_font_darkgray));
        }else {//选择英语
            fScanLl.setSelected(true);
            fScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.white));

            zScanLl.setSelected(false);
            zScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.order_day_font_darkgray));
        }
    }
}
