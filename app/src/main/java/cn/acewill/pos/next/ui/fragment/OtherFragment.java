package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class OtherFragment extends BaseFragment {

    @BindView(R.id.sale_money)
    CommonEditText saleMoney;
    @BindView(R.id.save_btn)
    TextView saveBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        float money = Store.getInstance(getActivity()).getSaleMoney();
        saleMoney.setText(money+"");
    }

    @OnClick(R.id.save_btn)
    public void onClick() {
        String money = saleMoney.getText().toString().trim();
        if(TextUtils.isEmpty(money)){
            showToast(ToolsUtils.returnXMLStr("please_enter_a_valid_amount"));
            return;
        }
        Store.getInstance(getActivity()).setSaleMoney(Float.parseFloat(money));
        showToast("保存成功");
    }
}
