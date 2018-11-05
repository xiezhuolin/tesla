package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.service.PosInfo;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class MyStoreFragment extends BaseFragment {
    @BindView( R.id.store_Id )
    TextView storeId;

    private PosInfo posInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_store, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        posInfo = PosInfo.getInstance();
        storeId.setText(posInfo.getBrandId());
    }
}
