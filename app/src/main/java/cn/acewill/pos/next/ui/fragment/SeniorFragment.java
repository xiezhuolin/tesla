package cn.acewill.pos.next.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.WeightCallBack;
import cn.acewill.pos.next.model.TakeOut;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class SeniorFragment extends BaseFragment implements WeightCallBack{
    @BindView( R.id.lin_automatic )
    LinearLayout linAutoMatic;
    @BindView( R.id.ck_select_netorder )
    CheckBox ckSelectNetorder;
    @BindView( R.id.ck_select_printPackageName )
    CheckBox ckSelectPackageName;
    @BindView( R.id.ck_select_printQrCode )
    CheckBox ckSelectQrCode;
    @BindView( R.id.ck_select_kitchMoney )
    CheckBox ckSelectKitchMoney;
    @BindView( R.id.ck_select_automatic_netorder )
    CheckBox ckSelectAutoMaticNetOrder;
    @BindView( R.id.ck_select_quickOrder )
    CheckBox ckSelectQuickOrder;
    @BindView( R.id.ck_select_netorder_first )
    CheckBox ckSelectNetOrderFirst;
    @BindView( R.id.ck_select_summary_code )
    CheckBox ckSelectSummaryCode;
    @BindView( R.id.ck_select_waiMai_order_receive )
    CheckBox ckSelectWaiMaiOrderReveive;
    @BindView( R.id.ck_select_waiMai_guest_info )
    CheckBox ckSelectWaiMaiGuestInfo;
    @BindView( R.id.ck_is_create_order_jyj )
    CheckBox ckIsCreateOrderJyj;
    @BindView( R.id.ck_is_pad )
    CheckBox ckIsPad;
    @BindView( R.id.lin_setTareWeight )
    LinearLayout linSetTareWeight;
    @BindView( R.id.tv_tareWeight )
    TextView tvTareWeight;

    private Store store;

    private TimerTaskController timerTaskController;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_senior_setting, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        store = Store.getInstance(aty);
        tvTareWeight.setText(store.getTareWeight()+" kg");
        timerTaskController = TimerTaskController.getInstance();
        //说明开启了称重服务
        if(timerTaskController.getLastWeight() != null)
        {
            timerTaskController.setWeightCallBack(this);
        }
        ckSelectNetorder.setChecked(store.getReceiveNetOrder());
        ckSelectPackageName.setChecked(store.isPrintPackageName());
        ckSelectQrCode.setChecked(store.isPrintQRCode());
        ckSelectKitchMoney.setChecked(store.getKitMoneyStyle());
        ckSelectAutoMaticNetOrder.setChecked(store.getAutoMaticNetOrder());
        ckSelectQuickOrder.setChecked(store.isOpenQuickOrder());
        ckSelectNetOrderFirst.setChecked(store.isNetOrderFirstShow());
        ckSelectSummaryCode.setChecked(store.isSummaryShowKDSCode());
        ckSelectWaiMaiOrderReveive.setChecked(store.isWaiMaiOrderReceive());
        ckSelectWaiMaiGuestInfo.setChecked(store.isWaiMaiGuestInfo());
        ckIsCreateOrderJyj.setChecked(store.isCreateOrderJyj());
        ckIsPad.setChecked(store.isPadRunnIng());
        if(store.getReceiveNetOrder())
        {
            linAutoMatic.setVisibility(View.VISIBLE);
        }
        else{
            linAutoMatic.setVisibility(View.GONE);
        }

        //是否在厨房单上显示菜品金额
        ckSelectKitchMoney.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setKitMoneyStyle(true);
                }
                else{
                    store.setKitMoneyStyle(false);
                }
            }
        });
        //网络接单
        ckSelectNetorder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setReceiveNetOrder(true);
                    linAutoMatic.setVisibility(View.VISIBLE);
                }
                else{
                    store.setReceiveNetOrder(false);
                    linAutoMatic.setVisibility(View.GONE);
                }
            }
        });
        //是否自动接收网络接单
        ckSelectAutoMaticNetOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setAutoMaticNetOrder(true);
                }
                else{
                    store.setAutoMaticNetOrder(false);
                }
            }
        });
        //是否打印套餐名
        ckSelectPackageName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setPrintPackageName(true);
                }
                else{
                    store.setPrintPackageName(false);
                }
            }
        });
        //是否打印发票二维码
        ckSelectQrCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setPrintQRCode(true);
                }
                else{
                    store.setPrintQRCode(false);
                }
            }
        });
        //是否开启快捷扫码下单
        ckSelectQuickOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setOpenQuickOrder(true);
                }
                else{
                    store.setOpenQuickOrder(false);
                }
            }
        });
        //订单管理-网上订单是否优先显示
        ckSelectNetOrderFirst.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setNetOrderFirstShow(true);
                }
                else{
                    store.setNetOrderFirstShow(false);
                }
            }
        });
        //是否在总单小票上打印KDS叫号、消号条码
        ckSelectSummaryCode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setSummaryShowKDSCode(true);
                }
                else{
                    store.setSummaryShowKDSCode(false);
                }
            }
        });
        //外卖单是否自动接收
        ckSelectWaiMaiOrderReveive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setWaiMaiOrderReceive(true);
                }
                else{
                    store.setWaiMaiOrderReceive(false);
                }
            }
        });
        //外卖单是否要输入顾客信息
        ckSelectWaiMaiGuestInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setWaiMaiGuestInfo(true);
                }
                else{
                    store.setWaiMaiGuestInfo(false);
                }
            }
        });
        //是否在Pad上运行POS
        ckIsPad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setPadRunnIng(true);
                }
                else{
                    store.setPadRunnIng(false);
                }
            }
        });
        //是否下单到吉野家服务器
        ckIsCreateOrderJyj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    store.setCreateOrderJyj(true);
                }
                else{
                    store.setCreateOrderJyj(false);
                }
                ToolsUtils.writeUserOperationRecords("跳转到login界面");
                UserData mUserData = UserData.getInstance(aty);
                mUserData.setUserName("");
                mUserData.setPwd("");
                mUserData.setSaveStated(false);
                myApplication.unbindCashBoxServer();
                myApplication.soundPool.stop(1);
                myApplication.clean();
                Intent orderIntent = new Intent(aty, LoginAty.class);
                orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(orderIntent);
            }
        });
        //设置秤的皮重
        linSetTareWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.createSingleButtonDialog(aty, ToolsUtils.returnXMLStr("tare"), new DialogTCallback() {
                    @Override
                    public void onConfirm(Object o) {
                        TakeOut takeOut = (TakeOut) o;
                        if(takeOut != null)
                        {
                            tvTareWeight.setText(takeOut.getTakeOutStr()+" kg");
                        }
                    }

                    @Override
                    public void onCancle() {

                    }
                });
            }
        });
    }

    private float tareWeight = 0;
    @Override
    public void changeTareWeight(float tareWeight) {

    }

    @Override
    public void changeNetWeight(float netWeight) {
//        tareWeight = netWeight;
    }

    @Override
    public void changeGrossWeight(float netWeight) {

    }

    @Override
    public void setPointnumber(int pointnumber) {

    }

    @Override
    public void signalLampState(boolean isStable) {

    }

    @Override
    public void setTareNumSuccess(boolean state) {

    }

    @Override
    public void setZeroNumSuccess(boolean state) {

    }
}
