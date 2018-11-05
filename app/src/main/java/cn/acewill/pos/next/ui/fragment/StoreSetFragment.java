package cn.acewill.pos.next.ui.fragment;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.utils.KeyBoardUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 门店配置
 * Created by aqw on 2016/8/20.
 */
public class StoreSetFragment extends BaseFragment {


    @BindView(R.id.store_save)
    TextView storeSave;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.websit)
    EditText websit;
    @BindView(R.id.preorderTime)
    EditText preorderTime;
    @BindView(R.id.orderTrading)
    EditText orderTrading;
    @BindView(R.id.kds_address)
    EditText kdsAddress;
    @BindView(R.id.kds_port)
    EditText kdsPort;

    private PosInfo posInfo;
    private Store store;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_set, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        store = store.getInstance(aty);
        posInfo = PosInfo.getInstance();
        address.setText(store.getStoreAddress());
        phone.setText(store.getStorePhone());
        websit.setText(store.getWebsit());
        preorderTime.setText(store.getPreordertime() + "");
        orderTrading.setText(store.getOrderTradingLiMint() + "");
        kdsAddress.setText(store.getKdsServer());
        kdsPort.setText(store.getKdsPort());

    }

    @OnClick(R.id.store_save)
    public void onClick() {
        saveData();
    }

    private void saveData() {
        String addressSth = address.getText().toString().trim();
        String phoneSth = phone.getText().toString().trim();
        String websitSth = websit.getText().toString().trim();
        String preorderTimeSth = preorderTime.getText().toString().trim();
        String orderTradingSth = orderTrading.getText().toString().trim();
        String kds_address = kdsAddress.getText().toString().trim();
        String kds_port = kdsPort.getText().toString().trim();

        if (!ToolsUtils.isNull(addressSth)) {
            store.setStoreAddress(addressSth);
            posInfo.setAddress(addressSth);
        }
        if (!ToolsUtils.isNull(phoneSth)) {
            store.setStorePhone(phoneSth);
            posInfo.setPhone(phoneSth);
        }
        if (!ToolsUtils.isNull(websitSth)) {
            store.setWebsit(websitSth);
            posInfo.setWebsite(addressSth);
        }
        if (!ToolsUtils.isNull(preorderTimeSth)) {
            store.setPreordertime(Integer.valueOf(preorderTimeSth));
            posInfo.setPreordertime(Integer.valueOf(preorderTimeSth));
        }
        if (!ToolsUtils.isNull(orderTradingSth)) {
            store.setOrderTradingLiMint(Integer.valueOf(orderTradingSth));
            posInfo.setOrderTradingLiMint(Integer.valueOf(orderTradingSth));
        }
        if (!ToolsUtils.isNull(kds_address)) {
            store.setKdsServer(kds_address);
        }

        if (!ToolsUtils.isNull(kds_port)) {
            store.setKdsPort(kds_port);
        }

        showToast("保存成功");


    }
}
