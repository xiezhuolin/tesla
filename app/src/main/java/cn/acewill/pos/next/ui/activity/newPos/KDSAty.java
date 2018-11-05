package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.KDS;
import cn.acewill.pos.next.model.KdsDishItem;
import cn.acewill.pos.next.model.KdsOrderData;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.service.retrofit.response.KDSResponse;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 添加删除KDS
 * Created by DHH on 2016/6/12.
 */
public class KDSAty extends BaseActivity {
    @BindView( R.id.printer_name )
    EditText printerName;
    @BindView( R.id.lin_1 )
    LinearLayout lin1;
    @BindView( R.id.tv_2 )
    TextView tv2;
    @BindView( R.id.printer_ip )
    EditText printerIp;
    @BindView( R.id.lin_3 )
    LinearLayout lin3;
    @BindView( R.id.tv_4 )
    TextView tv4;
    @BindView( R.id.test_kds )
    TextView testKds;
    @BindView( R.id.delete_kds )
    TextView deleteKds;

    private KDS kds;
    private String KdsName;
    private String KdsIp;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_kds);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        myApplication.addPage(KDSAty.this);
        setTitle("KDS");
        setShowBtnBack(true);
        setRightText(ToolsUtils.returnXMLStr("setting_save"));
        kds = (KDS) getIntent().getSerializableExtra("kds");
        if (kds == null) {
            deleteKds.setVisibility(View.GONE);
        } else {
            deleteKds.setVisibility(View.VISIBLE);
            printerName.setText(kds.getKdsName());
            printerIp.setText(kds.getIp());
        }
    }



    @OnClick( {R.id.tv_login, R.id.delete_kds, R.id.test_kds} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                saveKdsInfo();
                break;
            case R.id.delete_kds:
                    if (kds != null) {
                        DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("delete_kds"), ToolsUtils.returnXMLStr("is_delete_kds")+"(" + kds.getKdsName() + ") ？", new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                deleteKds(kds);
                            }

                            @Override
                            public void onCancle() {

                            }
                        });
                    }
                break;
            case R.id.test_kds:
                testPrintKds();
                break;
        }
    }

    private void testPrintKds()
    {
        KdsIp = printerIp.getText().toString().trim();
        if (TextUtils.isEmpty(KdsIp)) {
            showToast("请输入IP地址");
            return;
        }
        if (!ToolsUtils.isIP(KdsIp)) {
            showToast("请输入正确的打印机IP地址");
            return;
        }
        try {
            OrderService kdsOrderService = OrderService.getKdsInstance(this,KdsIp);
            KdsOrderData kdsOrderData = new KdsOrderData();
            List<KdsDishItem> kdsDishItemList = new ArrayList<>();
            kdsOrderData.oid = "666";
            kdsOrderData.createTime = System.currentTimeMillis();
            kdsOrderData.fetchID = "999";
            kdsOrderData.type = 0;
            kdsOrderData.total = "100";
            kdsOrderData.paystatus = 1;
            KdsDishItem kdsItem = new KdsDishItem();
            kdsItem.did = "11" + "";
            kdsItem.name = "测试菜品";
            kdsItem.count = 10;
            kdsItem.dishKind =  "77";
            kdsItem.seq = "99-66-33";
            kdsItem.price = "99";
            kdsDishItemList.add(kdsItem);
            kdsOrderData.dishitems = kdsDishItemList;

            kdsOrderService.kdsCreatOrder(new Gson().toJson(kdsOrderData), new ResultCallback<KDSResponse>() {
                @Override
                public void onResult(KDSResponse results) {
                    Log.i("KDS下单返回数据", ToolsUtils.getPrinterSth(results) + "");
                    //                        progressDialog.disLoading();
                    if (results.isSuccess()) {
                        showToast("测试KDS连接成功");
                    } else {
                        Log.i("KDS下单失败", results.getMsg());
                        showToast(results.getMsg());
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("KDS下单失败", e.getMessage());
                    //                        progressDialog.disLoading();
                    showToast(e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    private void saveKdsInfo() {
        KdsName = printerName.getText().toString().trim();
        KdsIp = printerIp.getText().toString().trim();
        if (TextUtils.isEmpty(KdsName)) {
            showToast("请输入KDS名称");
            return;
        }
        if (TextUtils.isEmpty(KdsIp)) {
            showToast("请输入IP地址");
            return;
        }
        if (!ToolsUtils.isIP(KdsIp)) {
            showToast("请输入正确的打印机IP地址");
            return;
        }
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.addKds(KdsIp,KdsName, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("添加成功!");
                        KDSAty.this.finish();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("添加打印机失败,", e.getMessage());
                    showToast("添加打印机失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void deleteKds(final KDS kds) {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.deleteKds(kds, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("删除成功!");
                        PrinterDataController.removeKds(kds);
                        KDSAty.this.finish();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("删除KDS失败,", e.getMessage());
                    showToast("删除KDS失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }
}
