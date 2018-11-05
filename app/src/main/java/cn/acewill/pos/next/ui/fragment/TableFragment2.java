package cn.acewill.pos.next.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtsCallback;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.table.Section;
import cn.acewill.pos.next.model.table.SectionUIProperties;
import cn.acewill.pos.next.model.table.Table;
import cn.acewill.pos.next.model.table.TableOrder;
import cn.acewill.pos.next.model.table.TableStatus;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.ui.activity.newPos.TableOrderNewAty;
import cn.acewill.pos.next.ui.adapter.TableOrderAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolGridView;

import static cn.acewill.pos.next.common.RetreatDishController.tableOrder;

/**
 * Created by hzc on 2017/1/4.
 */

public class TableFragment2 extends BaseFragment {
    @BindView(R.id.rl_root)
    RelativeLayout rl_root;
    @BindView(R.id.rl_zoon)
    LinearLayout rl_zoon;
    @BindView(R.id.rl_table)
    RelativeLayout rl_table;
    @BindView(R.id.iv_bg)
    ImageView iv_bg;
    List<Section> sections;
    private List<TableOrder> orders;
    private int currentZoon;
    int mScreenHeight;
    private String tableName = "";
     float scale = 1;

    private int delayedTime = 5*1000;
    private Timer timer = null;
    private TimerTask task;
    private Handler handler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.frag_table_manager2, container, false);
        ButterKnife.bind(this, inflate);
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        scale = getResources().getDisplayMetrics().density;
        mScreenHeight = wm.getDefaultDisplay().getHeight();
        return inflate;
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.i("停止刷新","");
        timerCancel();
    }
    @Override
    public void onResume() {
        super.onResume();
        cycleData();
        if(tableId >0)
        {
            exitTable(tableId);
        }
    }

    private void initData() {
        try {
            TableService.getInstance().getTableSections(new ResultCallback<List<Section>>() {
                @Override
                public void onResult(List<Section> result) {

                    sections = result;
                    try {
                        TableService.getInstance().tablesOrderData(-1, new ResultCallback<List<TableOrder>>() {
                            @Override
                            public void onResult(List<TableOrder> result) {
                                mScreenHeight = rl_table.getHeight();
                                orders = result;
                                int i = 0;
                                initTables(currentZoon);
                                rl_zoon.removeAllViews();
                                for (Section section : sections) {
                                    addZoon(section, i);
                                    i++;
                                }
                            }

                            @Override
                            public void onError(PosServiceException e) {
                            }
                        });
                    } catch (PosServiceException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(PosServiceException e) {
                    e.printStackTrace();
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void addZoon(Section section, final int i) {

        View inflate = View.inflate(getContext(), R.layout.item_zoon, null);


        TextView textView = (TextView) inflate.findViewById(R.id.tv_zoon);
        textView.setText(section.getName());
        ToolsUtils.writeUserOperationRecords("切换区域到:==>>"+section.getName());
//        textView.setTextSize(20);
//        textView.setGravity(Gravity.CENTER);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 35);
//        layoutParams.setMargins(0,0,20,0);
//        textView.setLayoutParams(layoutParams);
//        textView.setPadding(20,0,20,0);
//        textView.setBackgroundResource(R.drawable.selector_blue_white);
        if (i == currentZoon) {
            textView.setTextColor(0xffffffff);
            textView.setSelected(true);
        } else {
            textView.setTextColor(0xff000000);
            textView.setSelected(false);
        }
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCurrentZoon(i);
            }
        });
        rl_zoon.addView(inflate);
    }

    private void setCurrentZoon(int i) {
        if (currentZoon == i) {
            return;
        }
        currentZoon = i;
        int childCount = rl_zoon.getChildCount();
        for (int j = 0; j < childCount; j++) {
            TextView child = (TextView) ((LinearLayout) rl_zoon.getChildAt(j)).getChildAt(0);
            if (i == j) {
                child.setTextColor(0xffffffff);
                child.setSelected(true);
            } else {
                child.setTextColor(0xff000000);
                child.setSelected(false);
            }
        }
        initTables(i);
    }

    private void initTables(int i) {
        initTables(sections.get(i));
    }

    public SectionUIProperties sectionUIProperties;
    private void initTables(Section section) {
        if (!TextUtils.isEmpty(section.imageName)) {
            Picasso.with(getContext()).load(section.imageName).placeholder(R.mipmap.bg_table).error(R.mipmap.bg_table).into(iv_bg);
        }
        rl_table.removeAllViews();
        List<Table> tableList = section.getTableList();
        for (final Table table : tableList) {
            View inflate = View.inflate(getContext(), R.layout.item_table_hzc, null);
            if(sectionUIProperties == null)
            {
                sectionUIProperties = section.sectionUIProperties;
                if(sectionUIProperties == null)
                {
                    return;
                }
            }
            int widthPx = getPx(table.uiProperties.width, sectionUIProperties.screenHeight);
            int heightPx = getPx(table.uiProperties.height, sectionUIProperties.screenHeight);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthPx,
                    heightPx);
            layoutParams.setMargins(getPx(table.uiProperties.x, sectionUIProperties.screenHeight),
                    getPx(table.uiProperties.y, sectionUIProperties.screenHeight), 0, 0);
            inflate.setLayoutParams(layoutParams);
            inflate.setPadding(0, 0, 0, 0);
            if (table.status == TableStatus.EMPTY) {

                View ll_empty_table = inflate.findViewById(R.id.ll_empty_table);
                ll_empty_table.setVisibility(View.VISIBLE);

                if (table.uiProperties.isCircle()) {
                    inflate.setBackgroundResource(R.drawable.circle_white);
                } else {
                    inflate.setBackgroundResource(R.drawable.rect_white);
                }
                inflate.findViewById(R.id.ll_inuse_table).setVisibility(View.GONE);
                tableName = table.getName();
                ((TextView) inflate.findViewById(R.id.tv_table_name_empty)).setText(tableName);
                ((TextView) inflate.findViewById(R.id.tv_table_person_empty)).setText(table.getCapacity() + "人桌");
            } else {

                inflate.findViewById(R.id.ll_empty_table).setVisibility(View.GONE);
                View ll_inuse_table = inflate.findViewById(R.id.ll_inuse_table);
                ll_inuse_table.setVisibility(View.VISIBLE);
                if (table.uiProperties.isCircle()) {
                    inflate.setBackgroundResource(R.drawable.circle_blue);
                } else {
                    inflate.setBackgroundResource(R.drawable.rect_blue);
                }
                tableName = table.getName();
                if (table.status == TableStatus.IN_USE && !TextUtils.isEmpty(table.temStu)) {
                    tableName = table.getTemStu() + tableName;
                }
                TextView tv_table_name_inuse = (TextView) inflate.findViewById(R.id.tv_table_name_inuse);
                tv_table_name_inuse.setText(tableName);
                tv_table_name_inuse.setTextSize(heightPx/(5.5f*scale));
                float textSize = heightPx/(7.5f*scale);
                for (TableOrder order : orders) {
                    if (order.id == table.getId()) {
                        TextView tv_table_person_inuse = (TextView) inflate.findViewById(R.id.tv_table_person_inuse);
//                        tv_table_person_inuse.setLayoutParams(layoutParams1);
                        tv_table_person_inuse.setText(order.number + "/" + table.getCapacity());
                        TextView tv_price_inuse = (TextView) inflate.findViewById(R.id.tv_price_inuse);
                        tv_price_inuse.setText("￥" + order.total);
                        tv_price_inuse.setTextSize(textSize);
                        tv_table_person_inuse.setTextSize(textSize);
                        if (order.time != null) {
                            try {
                                String[] split = order.time.split(" ")[1].split(":");
                                TextView tv_time_inuse = (TextView) inflate.findViewById(R.id.tv_time_inuse);
                                tv_time_inuse.setText(split[0] + ":" + split[1]);
                                tv_time_inuse.setTextSize(textSize);
                            } catch (Exception e) {
                            }
                        }
                    }
                }
            }

            inflate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (table.status == TableStatus.EMPTY) {
                        openTable(table.getId(), table.getName(),table.getCapacity()+"");
                    } else {
                        tableName = table.getName();
                        if (table.status == TableStatus.IN_USE && !TextUtils.isEmpty(table.temStu)) {
                            tableName = table.getTemStu() + tableName;
                        }
                        ordersTable(table.getId(), tableName);
                    }
                }
            });
            rl_table.addView(inflate);
        }
    }

    private int getPx(int px, int height) {
        return px * mScreenHeight / height;
    }

    /**
     * 开台操作
     */
    private void openTable(final long tableId, final String tableName,final String tableCapacity) {
        this.tableId = tableId;
        DialogUtil.openTableDialog(aty, "开台", "开台人数", "请输入开台人数", 0, true, false, new DialogEtsCallback() {
            @Override
            public void onConfirm(String sth) {
                Integer numberOfCustomer;
                if(TextUtils.isEmpty(sth))
                {
                    numberOfCustomer = Integer.valueOf(tableCapacity);//人数  直接点击确定按钮,使用桌台的默认最大人数
                }
                else
                {
                    numberOfCustomer = Integer.valueOf(sth);//人数
                }
                openTable(tableId, numberOfCustomer, tableName,true);//开台操作
            }

            @Override
            public void onCancle(String sth) {
                Integer numberOfCustomer;
                if(TextUtils.isEmpty(sth))
                {
                    numberOfCustomer = Integer.valueOf(tableCapacity);//人数  直接点击确定按钮,使用桌台的默认最大人数
                }
                else
                {
                    numberOfCustomer = Integer.valueOf(sth);//人数
                }
                openTable(tableId, numberOfCustomer, tableName,false);//开台操作
            }
        });
    }

    /**
     * 开台
     *
     * @param tableId
     * @param numberOfCustomer
     */
    private void openTable(final long tableId, final int numberOfCustomer, final String tableName,final boolean isComeIn) {
        try {
            TableService tableService = TableService.getInstance();
            tableService.openTable(tableId, numberOfCustomer, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0 && isComeIn) {
                        Integer peopleNums = Integer.valueOf(String.valueOf(numberOfCustomer));
                        Order order = new Order();
                        order.addTableIds(tableId);
                        order.setCustomerAmount(peopleNums);
                        setTableInfo(tableId, order, tableName);
                        DishDataController.tablePeopleNumMap.put(tableId,peopleNums);
                        //                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, tableId));//跳转到下单界面
                    }
                    else
                    {
                        initData();
                        Integer peopleNums = Integer.valueOf(String.valueOf(numberOfCustomer));
                        DishDataController.tablePeopleNumMap.put(tableId,peopleNums);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("开台失败," + e.getMessage() + "!");
                    Log.i("开台失败,",e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            Log.i("开台失败,",e.getMessage());
        }
    }

    /**
     * 获取某个桌台上的订单
     *
     * @param tableId
     */
    private long tableId;
    private void ordersTable(final long tableId, final String tableName) {
        try {
            this.tableId = tableId;
            showProgress();
            TableService tableService = TableService.getInstance();
            tableService.ordersTable(tableId, new ResultCallback<List<Order>>() {
                @Override
                public void onResult(List<Order> result) {
                    dissmiss();
                    if (result != null) {
                        if (result.size() == 0) {
                            Order order = new Order();
                            order.setTableId(tableId);
                            order.setTableNames(tableName);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, order));//跳转到下单界面
                        } else {
                            if (result.size() == 1) {
                                tableOrder = result.get(0);
                                if (tableOrder != null) {
                                    tableOrder.setTableNames(tableName);
                                    tableOrder.setTableId(tableId);
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, tableOrder));//跳转到下单界面
                                } else {
                                    Order order = new Order();
                                    order.setTableId(tableId);
                                    order.setTableStyle(Constant.JsToAndroid.JS_A_SPLITTABLE);
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, order));//跳转到下单界面
                                }
                            } else {
                                crateOrderDialog();
                                tableOrderAdp.setData(result);
                                selectTableOrderIndex = -1;
                                tableOrderAdp.setPosition(selectTableOrderIndex);
                                dialog_title.setText("选择桌台号( " + tableName + " )订单");
                                dialog.show();
                            }
                        }
                    } else {
                        showToast("获取桌台订单信息失败!");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("获取桌台订单信息失败," + e.getMessage() + "!");
                    Log.i("获取桌台订单信息失败,",e.getMessage());
                    dissmiss();
                }
            });
        } catch (PosServiceException e) {
            dissmiss();
            Log.i("获取桌台订单信息失败,",e.getMessage());
            e.printStackTrace();
        }
    }

    private void setTableInfo(final long tableId, final Order order, String tableName) {
        enterTable(tableId,order,tableName);
    }

    private void enterTable(final long tableId,final Order order,final String tableName)
    {
        try {
            TableService tableService = TableService.getInstance();
            tableService.enterTable(tableId, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if((int)result == 0)
                    {
                        Intent tableIntent = new Intent(aty, TableOrderNewAty.class);
                        tableIntent.putExtra("tableId", tableId);
                        Bundle tableBun = new Bundle();
                        order.setTableNames(tableName);
                        tableBun.putSerializable("tableOrder", order);
                        tableIntent.putExtras(tableBun);
                        startActivity(tableIntent);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("进入桌台失败," + e.getMessage());
                    Log.i("进入桌台失败,",e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("进入桌台失败,",e.getMessage());
        }
    }

    private void exitTable(final long tableId)
    {
        try {
            TableService tableService = TableService.getInstance();
            tableService.exitTable(tableId, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if((int)result == 0)
                    {
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("退出桌台失败,",e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("退出桌台失败,",e.getMessage());
        }
    }



    private Dialog dialog;
    private Order selectOrder;
    private TextView dialog_title;
    private ScrolGridView gv_definition;
    private LinearLayout lin_select_work;
    private TextView negativeButton, positiveButton;
    private TableOrderAdp tableOrderAdp;
    private int selectTableOrderIndex = -1;//
    private Dialog crateOrderDialog() {
        selectOrder = null;
        dialog = DialogUtil.getDialogShow(aty, R.layout.dialog_work_shift, 0.5f, 0.6f, false, false);
        dialog_title = (TextView) dialog.findViewById(R.id.print_title);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        lin_select_work = (LinearLayout) dialog.findViewById(R.id.lin_select_work);
        gv_definition = (ScrolGridView) dialog.findViewById(R.id.gv_definition);
        LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
        LinearLayout lin_money = (LinearLayout) dialog.findViewById(R.id.lin_money);
        negativeButton = (TextView) dialog.findViewById(R.id.print_ok);
        positiveButton = (TextView) dialog.findViewById(R.id.print_cancle);
        message.setText("桌台订单号");
        dialog_title.setText("选择(" + tableName + ")桌台号订单");
        lin_money.setVisibility(View.GONE);
        tableOrderAdp = new TableOrderAdp(aty);
        gv_definition.setAdapter(tableOrderAdp);
        print_close_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /**
         * 取消
         */
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /**
         * 确定
         */
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTableOrderIndex != -1) {
                    if (selectOrder != null) {
                        selectOrder.setTableNames(tableName);
                        selectOrder.setTableId(tableId);
                        dialog.dismiss();
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, selectOrder));//跳转到下单界面
                    }
                } else {
                    showToast("请选择一个桌台订单!");
                }

            }
        });
        gv_definition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTableOrderIndex = position;
                Order order = (Order) tableOrderAdp.getItem(position);
                tableOrderAdp.setPosition(selectTableOrderIndex);
                if (order != null) {
                    selectOrder = order;
                }
            }
        });
        return dialog;
    }

    private void cycleData()
    {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                // 要做的事情
                initData();
            }
        };
        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, delayedTime);//不延时并且5秒循环一次刷新桌台
    }

    private void timerCancel()
    {
        if(timer != null)
        {
            timer.cancel();
        }
    }

}
