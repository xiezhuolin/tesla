package cn.acewill.pos.next.ui.activity.newPos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.PowerController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.WorkShift;
import cn.acewill.pos.next.model.WorkShiftNewReport;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.adapter.WorkShiftHistoryAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.qqtheme.framework.picker.DatePicker;

/**
 * 交接班记录
 * Created by DHH on 2016/6/12.
 */
public class WorkShiftHistoryAty extends BaseActivity{
    @BindView( R.id.tv_start_time )
    TextView tvStartTime;
    @BindView( R.id.tv_end_time )
    TextView tvEndTime;
    @BindView( R.id.tv_query )
    TextView tvQuery;
    @BindView( R.id.lv_workShift )
    ListView lvWorkShift;
    @BindView( R.id.rel_back )
    RelativeLayout relBack;

    private MyApplication application;

    private WorkShiftHistoryAdp workShiftHistoryAdp;
    private Calendar calendar;
    private String startInitTime = " 00:00:00";
    private String endInitTime = " 23:59:59";

    private DatePicker startPicker;
    private DatePicker endPicker;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_work_shift_history);
        ButterKnife.bind(this);
        initView();
    }

    private void loadData() {
        String startTime = tvStartTime.getText().toString().trim();
        String endTime = tvEndTime.getText().toString().trim();
        queryWorkShift(startTime,endTime);
    }

    private void initView() {
        myApplication.addPage(WorkShiftHistoryAty.this);
        setTitle(ToolsUtils.returnXMLStr("hostory_shift"));
        setShowBtnBack(true);
        application = MyApplication.getInstance();
        startPicker = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
        endPicker = new DatePicker((Activity) context, DatePicker.YEAR_MONTH_DAY);
        startPicker = setDatePicker(startPicker);
        endPicker = setDatePicker(endPicker);

        calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvStartTime.setText(year+"-"+checkDayOrMonthStr(month)+"-"+checkDayOrMonthStr(day)+startInitTime);
        tvEndTime.setText(year+"-"+checkDayOrMonthStr(month)+"-"+checkDayOrMonthStr(day)+endInitTime);

        workShiftHistoryAdp = new WorkShiftHistoryAdp(context);
        lvWorkShift.setAdapter(workShiftHistoryAdp);

        lvWorkShift.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkShift workShift = (WorkShift) workShiftHistoryAdp.getItem(position);
                if(workShift != null)
                {
                    int workShiftId = Integer.valueOf(workShift.getId()+"");
                    workShiftReport(workShiftId);
                }
            }
        });
    }

    /**
     * 获取班次交接报表数据
     *
     * @param workShiftId 班次Id
     */
    private void workShiftReport(Integer workShiftId) {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.workShiftReport(workShiftId, "",new ResultCallback<WorkShiftNewReport>() {
                @Override
                public void onResult(final WorkShiftNewReport result) {
                    Log.i("获取交接班报表数据==>>", ToolsUtils.getPrinterSth(result));
                    if(result != null)
                    {
                        Intent intent = new Intent();
                        intent.setClass(WorkShiftHistoryAty.this, WorkShiftReportAty.class);
                        intent.putExtra("WorkShiftReport", (Serializable) result);
                        intent.putExtra("printType", PowerController.SHIFT_WORK);
                        startActivity(intent);
                    }
                    else{
                        showToast("获取交接班报表数据为空.");
                        Log.i("获取交接班报表数据失败", "获取交接班报表数据为空");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("获取交接班报表数据失败," + e.getMessage());
                    Log.i("获取交接班报表数据失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("获取交接班报表数据失败", e.getMessage());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @OnClick( {R.id.rel_back,R.id.tv_query,R.id.tv_start_time,R.id.tv_end_time} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_back:
                finish();
                break;
            case R.id.tv_query:
                String startTime = tvStartTime.getText().toString().trim();
                String endTime = tvEndTime.getText().toString().trim();
                queryWorkShift(startTime,endTime);
                break;
            case R.id.tv_start_time:
                startPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tvStartTime.setText(year + "-" + checkDayOrMonthStr(Integer.valueOf(month)) + "-" + checkDayOrMonthStr(Integer.valueOf(day))+startInitTime);
                    }
                });
                startPicker.show();
                break;
            case R.id.tv_end_time:
                endPicker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        tvEndTime.setText(year + "-" + checkDayOrMonthStr(Integer.valueOf(month)) + "-" + checkDayOrMonthStr(Integer.valueOf(day))+endInitTime);
                    }
                });
                endPicker.show();
                break;
        }
    }

    /**
     * 查询历史交接班记录
     * @param startTime
     * @param endTime
     */
    private void queryWorkShift(final String startTime,final String endTime)
    {
        String checkStartTime = ToolsUtils.cloneTo(startTime);
        String checkEndTime = ToolsUtils.cloneTo(endTime);
        checkStartTime = checkStartTime.replace(" ","").replace("-","").replace(":","");
        checkEndTime = checkEndTime.replace(" ","").replace("-","").replace(":","");
        long startCheckTime = Long.valueOf(checkStartTime);
        long endCheckTime = Long.valueOf(checkEndTime);
        if(endCheckTime>startCheckTime)
        {
            try {
                StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
                storeBusinessService.getWorkShiftHistory(startTime, endTime, new ResultCallback<List<WorkShift>>() {
                    @Override
                    public void onResult(List<WorkShift> result) {
                        if(!ToolsUtils.isList(result))
                        {
                            workShiftHistoryAdp.setData(result);
                        }
                        else{
                            showToast("无交接班记录!");
                            Log.i("无交接班记录!","");
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        showToast("获取交接班记录失败!" + e.getMessage());
                        Log.i("获取交接班记录失败", e.getMessage());
                    }
                });
            } catch (PosServiceException e) {
                e.printStackTrace();
                showToast("交班失败,请重试!" + e.getMessage());
                Log.i("获取交接班记录失败", e.getMessage());
            }
        }
        else{
            showToast("查询的结束时间必须大于起始时间");
        }

    }

    /**
     * 检测月份日期格式 小于10要在前面补个0
     * @param month
     * @return
     */
    private String checkDayOrMonthStr(int month)
    {
        if(month < 10)
        {
            return "0"+month;
        }
        return month+"";
    }

    private DatePicker setDatePicker(DatePicker picker)
    {
        if(picker != null)
        {
            picker.setHeight(application.getScreenHeight() / 3);
            picker.setCancelTextColor(application.getResources().getColor(R.color.red));
            picker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
            picker.setCancelTextSize(28);
            picker.setSubmitTextSize(28);
            picker.setRangeStart(2017, 1, 1);//开始范围
            picker.setRangeEnd(2022, 1, 1);//结束范围
        }
        return picker;
    }
}
