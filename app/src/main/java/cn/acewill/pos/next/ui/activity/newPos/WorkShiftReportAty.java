package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.model.TerminalInfo;
import cn.acewill.pos.next.model.WorkShiftNewReport;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.adapter.WorkShiftReportAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

import static cn.acewill.pos.next.utils.TimeUtil.getTimeStr;

/**
 * Created by DHH on 2017/3/21.
 */

public class WorkShiftReportAty extends BaseActivity {
    @BindView( R.id.tv_titckTitle )
    TextView tvTitckTitle;
    @BindView( R.id.tv_printerName )
    TextView tvPrinterName;
    @BindView( R.id.tv_timeStart )
    TextView tvTimeStart;
    @BindView( R.id.tv_timeEnd )
    TextView tvTimeEnd;
    @BindView( R.id.tv_printTime )
    TextView tvPrintTime;
    @BindView( R.id.lv_report )
    ScrolListView lvReport;
    @BindView( R.id.print_cancle )
    TextView printCancle;
    @BindView( R.id.print_ok )
    TextView printOk;
    @BindView( R.id.sc_scroll )
    ScrollView scScroll;
    @BindView( R.id.tv_main_title )
    TextView tvMainTitle;
    @BindView( R.id.tv_submitCash )
    TextView tvSubmitCash;
    @BindView( R.id.tv_startWorkShiftCash )
    TextView tvStartWorkShiftCash;
    @BindView( R.id.tv_endWrkShiftCash )
    TextView tvEndWorkShiftCash;
    @BindView( R.id.tv_workShiftName )
    TextView tvWorkShiftName;
    @BindView( R.id.tv_differenceCash )
    TextView tvDifferenceCash;
    @BindView( R.id.rel_back )
    RelativeLayout relBack;

    private PosInfo posInfo;
    private WorkShiftNewReport workShiftReport;
    private int printType;

    private WorkShiftReportAdp showReportAdp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_work_shift_report);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        posInfo = PosInfo.getInstance();
        workShiftReport = (WorkShiftNewReport) getIntent().getSerializableExtra("WorkShiftReport");
        printType = getIntent().getIntExtra("printType", PowerController.DAILY);

        if (StoreInfor.terminalInfo != null) {
            TerminalInfo terminalInfo = StoreInfor.terminalInfo;
            String brandName = TextUtils.isEmpty(terminalInfo.brandName) ? ToolsUtils.returnXMLStr("wisdom_cash_register") : terminalInfo.brandName;
            String storeName = TextUtils.isEmpty(terminalInfo.sname) ? ToolsUtils.returnXMLStr("acewill_cloud_pos") : terminalInfo.sname;
            if (TextUtils.isEmpty(terminalInfo.sname)) {
                tvMainTitle.setText(brandName);
            }
            tvMainTitle.setText(brandName + "-" + storeName);
        }

        String printStr = "";
        //日结
        if (printType == PowerController.DAILY) {
            printStr = "日结";
        } else if (printType == PowerController.SHIFT_WORK) {
            printStr = "交接班";
        }
        tvTitckTitle.setText(printStr + "报表");
        tvPrinterName.setText(printStr + "人 : " + posInfo.getRealname());
        tvWorkShiftName.setText("班次名称 : " + workShiftReport.getWorkShiftName());
        tvTimeStart.setText(printStr + "开始时间 : " + workShiftReport.getStartTime());
        tvTimeEnd.setText(printStr + "结束时间 : " + workShiftReport.getEndTime());
        tvPrintTime.setText("小票打印时间 : " + getTimeStr(System.currentTimeMillis()));
        tvStartWorkShiftCash.setText("开班钱箱余额 : " + workShiftReport.getStartWorkShiftCash());
        tvEndWorkShiftCash.setText("交班钱箱余额 : " + workShiftReport.getEndWorkShiftCash());
        showReportAdp = new WorkShiftReportAdp(context);
        showReportAdp.setData(workShiftReport.getWorkShiftCategoryDataList());
        lvReport.setAdapter(showReportAdp);

        loadCustomerData(workShiftReport);
    }

    private void loadCustomerData(WorkShiftNewReport pctData) {
        if (pctData != null) {
            //应交金额
            tvSubmitCash.setText(pctData.getSubmitCash() + " /元");
            //差额
            tvDifferenceCash.setText(pctData.getDifferenceCash() + " /元");
        }
    }

    @OnClick( {R.id.rel_back,R.id.print_cancle, R.id.print_ok} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_back:
                finish();
                break;
            case R.id.print_cancle:
                WorkShiftReportAty.this.finish();
                break;
            case R.id.print_ok:
                break;
        }
    }
}
