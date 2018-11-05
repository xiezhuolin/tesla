package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.StandByCash;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.adapter.StandByCashPayTypeAdp;
import cn.acewill.pos.next.utils.Keys;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.ViewUtil;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * 新建备用金
 * Created by DHH on 2016/6/12.
 */
public class CreateStandByCashAty extends BaseActivity {

    @BindView( R.id.lin_spending )
    LinearLayout linSpending;
    @BindView( R.id.tv_payment )
    ComTextView tvSpending;
    @BindView( R.id.tv_collection )
    ComTextView tvEarning;
    @BindView( R.id.save_next_btn )
    ComTextView saveNextBtn;
    @BindView( R.id.tv_standByCashType )
    TextView tvStandByCashType;
    @BindView( R.id.paytype_gv )
    RecyclerView paytypeGv;
    @BindView( R.id.key_one )
    ComTextView keyOne;
    @BindView( R.id.key_two )
    ComTextView keyTwo;
    @BindView( R.id.key_three )
    ComTextView keyThree;
    @BindView( R.id.key_other_100 )
    ComTextView keyOther100;
    @BindView( R.id.key_four )
    ComTextView keyFour;
    @BindView( R.id.key_five )
    ComTextView keyFive;
    @BindView( R.id.key_six )
    ComTextView keySix;
    @BindView( R.id.key_other_50 )
    ComTextView keyOther50;
    @BindView( R.id.key_seven )
    ComTextView keySeven;
    @BindView( R.id.key_eight )
    ComTextView keyEight;
    @BindView( R.id.key_nine )
    ComTextView keyNine;
    @BindView( R.id.key_other_20 )
    ComTextView keyOther20;
    @BindView( R.id.key_delete )
    ComTextView keyDelete;
    @BindView( R.id.key_zero )
    ComTextView keyZero;
    @BindView( R.id.key_point )
    ComTextView keyPoint;
    @BindView( R.id.key_other_10 )
    ComTextView keyOther10;
    @BindView( R.id.tv_moneyShow )
    TextView tvMoneyShow;
    @BindView( R.id.tv_login )
    TextView tvLogin;
    @BindView( R.id.rel_back )
    RelativeLayout relBack;

    private StandByCashPayTypeAdp standByCashPayTypeAdp;
    private StandByCash selectStandbyCast;
    private int reasonType = 1;//0：收入；1：支出

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_create_standby_cash);
        ButterKnife.bind(this);
        initView();
        ViewUtil.setActivityWindow(context, 8, 9);
    }

    private void loadData() {

    }

    private void initView() {
        myApplication.addPage(CreateStandByCashAty.this);
//        setTitle("新建备用金");
//        setShowBtnBack(true);
//        setRightText("确认");
        tvMoneyShow.setText((TextUtils.isEmpty(print_str)?"0":print_str)+"￥");
        standByCashPayTypeAdp = new StandByCashPayTypeAdp(context);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        paytypeGv.setLayoutManager(linearLayoutManager);
        paytypeGv.setAdapter(standByCashPayTypeAdp);


        getStandByCashTypeList();
        standByCashPayTypeAdp.setOnItemClickListener(new StandByCashPayTypeAdp.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                //支出
                if (reasonType == 1) {
                    selectStandbyCast = standbyCashOut.get(position);
                }
                //收入
                else if (reasonType == 0) {
                    selectStandbyCast = standbyCashIn.get(position);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


    @OnClick( {R.id.rel_back,R.id.save_next_btn,R.id.tv_login, R.id.tv_payment, R.id.tv_collection,R.id.key_one, R.id.key_two, R.id.key_three, R.id.key_other_100, R.id.key_four, R.id.key_five, R.id.key_six, R.id.key_other_50, R.id.key_seven, R.id.key_eight, R.id.key_nine, R.id.key_other_20, R.id.key_delete, R.id.key_point, R.id.key_other_10} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_back:
                finish();
                break;
            case R.id.tv_payment:
                tvStandByCashType.setText("支出类型:");
                reasonType = 1;
                if (standbyCashOut != null && standbyCashOut.size() > 0) {
                    standByCashPayTypeAdp.setData(standbyCashOut);
                } else {
                    standByCashPayTypeAdp.setData(null);
                }
                break;
            case R.id.tv_collection:
                tvStandByCashType.setText("收款类型:");
                reasonType = 0;
                if (standbyCashIn != null && standbyCashIn.size() > 0) {
                    standByCashPayTypeAdp.setData(standbyCashIn);
                } else {
                    standByCashPayTypeAdp.setData(null);
                }
                break;
            case R.id.tv_login:
                saveReceiveHistoryRecode(true);
                break;
            case R.id.key_one:
                printValue(Keys.KEY1);
                break;
            case R.id.key_two:
                printValue(Keys.KEY2);
                break;
            case R.id.key_three:
                printValue(Keys.KEY3);
                break;
            case R.id.key_other_100:
                printValue(Keys.KEY100);
                break;
            case R.id.key_four:
                printValue(Keys.KEY4);
                break;
            case R.id.key_five:
                printValue(Keys.KEY5);
                break;
            case R.id.key_six:
                printValue(Keys.KEY6);
                break;
            case R.id.key_other_50:
                printValue(Keys.KEY50);
                break;
            case R.id.key_seven:
                printValue(Keys.KEY7);
                break;
            case R.id.key_eight:
                printValue(Keys.KEY8);
                break;
            case R.id.key_nine:
                printValue(Keys.KEY9);
                break;
            case R.id.key_other_20:
                printValue(Keys.KEY20);
                break;
            case R.id.key_delete:
                printValue(Keys.KEYDE);
                break;
            case R.id.key_zero:
                printValue(Keys.KEY0);
                break;
            case R.id.key_point:
                printValue(Keys.KEYPOINT);
                break;
            case R.id.key_other_10:
                printValue(Keys.KEYTen);
                break;
            case R.id.save_next_btn:
                saveReceiveHistoryRecode(false);
                break;
        }
    }

    private List<StandByCash> standbyCashOut = new ArrayList<>();
    private List<StandByCash> standbyCashIn = new ArrayList<>();

    private void getStandByCashTypeList() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.getStandByCashType(new ResultCallback<List<StandByCash>>() {
                @Override
                public void onResult(List<StandByCash> result) {
                    if (result != null) {
                        for (StandByCash standByCash : result) {
                            //支出
                            if (standByCash.getReasonType() == 1) {
                                standbyCashOut.add(standByCash);
                            }
                            //收款
                            else if (standByCash.getReasonType() == 0) {
                                standbyCashIn.add(standByCash);
                            }
                        }
                        standByCashPayTypeAdp.setData(standbyCashOut);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取备用金支付类型失败,", e.getMessage());
                    showToast("获取备用金支付类型失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void createReceiveHistoryRecord(final boolean isFinish,int reasonid, String reasonName, int outputType, String amount, String createTime) {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.createReceiveHistoryRecord(reasonid, reasonName, outputType, amount, createTime, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("添加备用金记录成功!");
                        if(isFinish)
                        {
                            CreateStandByCashAty.this.finish();
                        }
                        else{
                            print_str = "";
                            tvMoneyShow.setText((TextUtils.isEmpty(print_str)?"0":print_str)+"￥");
                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("添加备用金记录失败,", e.getMessage());
                    showToast("添加备用金记录失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void saveReceiveHistoryRecode(boolean isFinish)
    {
        if(TextUtils.isEmpty(print_str))
        {
            showToast(ToolsUtils.returnXMLStr("input_amount"));
            return;
        }
        if(!TextUtils.isEmpty(print_str)&&print_str.substring(0,1).equals("."))
        {
            showToast("请输入正确金额!");
            return;
        }
        if(selectStandbyCast == null)
        {
            showToast("请选择备用金支付方式正确金额!");
            return;
        }
        createReceiveHistoryRecord(isFinish,selectStandbyCast.getReasonid(),selectStandbyCast.getReasonName(),selectStandbyCast.getReasonType(),print_str,String.valueOf(System.currentTimeMillis()));
    }

    private String print_str = "";//输入的字符
    private BigDecimal print_money = new BigDecimal(0);//输入金额

    private void printValue(Keys keys) {

        switch (keys.getValue()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                if(print_str.length()<7){
                    print_str += keys.getValue() + "";
                }
                break;
            case 10://点
                if (!print_str.contains(".")) {
                    if(print_str.length()<7){
                        print_str += ".";
                    }
                }
                break;
            case 11://删除
                if (print_str.length() > 0) {
                    print_str = print_str.substring(0, print_str.length() - 1);
                }
                break;
            case 100://100
                print_str = "100";
                break;
            case 50://
                print_str = "50";
                break;
            case 20://20
                print_str = "20";
                break;
            case 110://10
                print_str = "10";
                break;
        }
        tvMoneyShow.setText((TextUtils.isEmpty(print_str)?"0":print_str)+"￥");
    }
}
