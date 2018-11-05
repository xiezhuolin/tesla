package cn.acewill.pos.next.ui.fragment;

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
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * 正反扫设置
 * Created by aqw on 2016/12/8.
 */
public class ScanFragment extends BaseFragment {

    @BindView(R.id.z_scan_tv)
    ComTextView zScanTv;
    @BindView(R.id.z_scan_tip)
    ComTextView zScanTip;
    @BindView(R.id.z_scan_ll)
    LinearLayout zScanLl;
    @BindView(R.id.f_scan_tv)
    ComTextView fScanTv;
    @BindView(R.id.f_scan_tip)
    ComTextView fScanTip;
    @BindView(R.id.f_scan_ll)
    LinearLayout fScanLl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_set, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        boolean isFront = Store.getInstance(mContext).isFront();
        setSelect(isFront);
    }


    @OnClick({R.id.z_scan_ll, R.id.f_scan_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.z_scan_ll://正扫
                setSelect(true);
                Store.getInstance(mContext).setFront(true);
                break;
            case R.id.f_scan_ll://反扫
                setSelect(false);
                Store.getInstance(mContext).setFront(false);
                break;
        }
    }

    //更新选择UI
    private void setSelect(boolean isFront){
        if(isFront){//选择正扫
            zScanLl.setSelected(true);
            zScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            zScanTip.setTextColor(ContextCompat.getColor(mContext,R.color.white));

            fScanLl.setSelected(false);
            fScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.order_day_font_darkgray));
            fScanTip.setTextColor(ContextCompat.getColor(mContext,R.color.login_gray));
        }else {//选择反扫
            fScanLl.setSelected(true);
            fScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            fScanTip.setTextColor(ContextCompat.getColor(mContext,R.color.white));

            zScanLl.setSelected(false);
            zScanTv.setTextColor(ContextCompat.getColor(mContext,R.color.order_day_font_darkgray));
            zScanTip.setTextColor(ContextCompat.getColor(mContext,R.color.login_gray));
        }
    }
}
