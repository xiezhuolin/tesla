package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;

/**
 * 帮助界面
 * Created by aqw on 2016/12/12.
 */
public class HelpFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
    }

//    @OnClick(R.id.save_btn)
//    public void onClick() {
//        String money = saleMoney.getText().toString().trim();
//        if(TextUtils.isEmpty(money)){
//            showToast("请输入有效金额");
//            return;
//        }
//        Store.getInstance(getActivity()).setSaleMoney(Float.parseFloat(money));
//        showToast("保存成功");
//    }
}
