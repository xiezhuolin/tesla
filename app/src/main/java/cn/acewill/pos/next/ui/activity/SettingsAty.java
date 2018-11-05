package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/6/12.
 */
public class SettingsAty extends BaseActivity {
    @BindView( R.id.setting_storeName )
    EditText settingStoreName;
    @BindView( R.id.setting_phone )
    EditText settingPhone;
    @BindView( R.id.setting_address )
    EditText settingAddress;
    @BindView( R.id.setting_app )
    EditText settingApp;
    @BindView( R.id.setting_brand )
    EditText settingBrand;
    @BindView( R.id.setting_store )
    EditText settingStore;
    @BindView( R.id.setting_device )
    EditText settingDevice;
    @BindView( R.id.setting_service_address )
    EditText settingServiceAddress;
    @BindView( R.id.setting_service_port )
    EditText settingServicePort;
    @BindView( R.id.setting_pos_id )
    EditText settingPosId;
    @BindView( R.id.btn_save )
    Button btnSave;

    private Store store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_settings);
        myApplication.addPage(SettingsAty.this);
        setTitle(ToolsUtils.returnXMLStr("setting_title_system"));
        //初始化 BufferKnife
        ButterKnife.bind(this);
        store = Store.getInstance(context);
        regListener();
        loadData();
    }

    @OnClick( { R.id.btn_save} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveInfo();
                break;
        }
    }

    private void regListener() {

    }

    private void loadData() {
        boolean saveState = store.getSaveState();
        if (saveState) {
            settingStoreName.setText(store.storeName);
            settingPhone.setText(store.storePhone);
            settingAddress.setText(store.storeAddress);

            settingApp.setText(store.getStoreAppId());
            settingBrand.setText(store.getBrandId());
            settingStore.setText(store.getStoreId());
            settingDevice.setText(store.getDeviceName());
            settingServiceAddress.setText(store.getServiceAddress());
            settingServicePort.setText(store.getStorePort());
            settingPosId.setText(store.getStorePosId());
        }
    }


    /**
     * 保存设置的配置信息
     */
    private void saveInfo() {
        String storeName = settingStoreName.getText().toString().trim();
        String storePhone = settingPhone.getText().toString().trim();
        String storeAddress = settingAddress.getText().toString().trim();
        String storeMerchantsId = settingApp.getText().toString().trim();
        String storeBrandId = settingBrand.getText().toString().trim();
        String storeStoreId = settingStore.getText().toString().trim();
        String storeDeviceName = settingDevice.getText().toString().trim();

        String serviceAddress = settingServiceAddress.getText().toString().trim();
        String servicePort = settingServicePort.getText().toString().trim();
        String servicePosId = settingPosId.getText().toString().trim();

        if (TextUtils.isEmpty(storeMerchantsId)) {
            showToast("商户ID不能为空");
            return;
        }
        if (TextUtils.isEmpty(storeBrandId)) {
            showToast("品牌ID不能为空");
            return;
        }
        if (TextUtils.isEmpty(storeStoreId)) {
            showToast("门店ID不能为空");
            return;
        }
        if (TextUtils.isEmpty(storeDeviceName)) {
            showToast("设备名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(serviceAddress)) {
            showToast(ToolsUtils.returnXMLStr("server_address_is_not_null"));
            return;
        }
        if (TextUtils.isEmpty(servicePosId)) {
            showToast("本机ID不能为空");
            return;
        }

        if (!TextUtils.isEmpty(storeName)) {
            store.storeName = storeName;
        }
        if (!TextUtils.isEmpty(storePhone)) {
            store.storePhone = storePhone;
        }
        if (!TextUtils.isEmpty(storeAddress)) {
            store.storeAddress = storeAddress;
        }
        if (!TextUtils.isEmpty(servicePort)) {
            store.setStorePort(servicePort);
        }
        store.setStoreAppId(storeMerchantsId);
        store.setBrandId(storeBrandId);
        store.setStoreId(storeStoreId);
        store.setDeviceName(storeDeviceName);

        store.setServiceAddress(serviceAddress);
        store.setStorePosId(servicePosId);

        showToast("保存成功!");
        store.setSaveState(true);
        finish();
    }

}
