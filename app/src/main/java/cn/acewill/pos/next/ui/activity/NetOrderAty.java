package cn.acewill.pos.next.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.NetOrderController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.service.OrderService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.ui.adapter.NetOrderAdapter;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/6/12.
 */
public class NetOrderAty extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, NetOrderAdapter.RefrushLisener {
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.print_close_ll )
    LinearLayout printCloseLl;

    private String TAG = "NetOrderAty";
    private NetOrderAdapter adapter;
    private List<Order> orderList = new ArrayList<Order>();
    private int lastVisibleItem = 0;

    private int limit = 20;
    private int delayedTime = 5 * 1000;
    private Intent intent;

    private Timer timer = new Timer();
    private TimerTask task;
    private Handler handler;

    private Timer timerStart;//轮询网上订单接口
    private TimerTask taskStart;
    private Handler handlerStart;

    private boolean isStopSyncNetOrder = true;//是否要停止网上订单轮训接口
    private boolean isPrinting = false;//是否正在打印中

    public boolean isStopSyncNetOrder() {
        return isStopSyncNetOrder;
    }

    public void setStopSyncNetOrder(boolean stopSyncNetOrder) {
        isStopSyncNetOrder = stopSyncNetOrder;
        //打印结束
        if(stopSyncNetOrder)
        {
            isPrinting = false;
        }
        //正在下单 并进行打印中
        else{
            isPrinting = true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_net_order);
        ButterKnife.bind(this);
        myApplication.addPage(NetOrderAty.this);
        initView();
//        cycleData();
    }

    public void cancleStartTimer() {
        if (timerStart != null) {
            timerStart.cancel();
            timerStart = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerCancel();
        cancleStartTimer();
    }

    private void initView() {
        isPrinting = false;
        intent = new Intent();
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new NetOrderAdapter(context, orderList, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);

        orderLv.setAdapter(adapter);

        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);
                }
            }
        });
    }

    private void loadData(boolean isCome) {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        getNetOrder();
    }

    @Override
    public void onRefresh() {
        if(!isPrinting)
        {
//            loadData(false);
        }
    }

    @Override
    public void refrush() {
        if(!isPrinting)
        {
//            loadData(false);
        }
    }

    @OnClick( R.id.print_close_ll )
    public void onClick() {
        int size = 0;
        if (NetOrderController.getNetOrderList() != null) {
            size = NetOrderController.getNetOrderList().size();
        }
        intent.putExtra("netOrderItemSize", size);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setNetOrderData(List<Order> orderList) {
        if (orderList != null && orderList.size() > 0) {
            adapter.setData(orderList);
            if (orderList.size() < limit) {
                adapter.changeMoreStatus(adapter.NO_MORE);
            } else {
                adapter.changeMoreStatus(adapter.LOAD_MORE);
            }
        } else {
            adapter.changeMoreStatus(adapter.NO_MORE);
        }
        orderSrl.setRefreshing(false);
    }

    public void getNetOrder() {
        try {
            ToolsUtils.writeUserOperationRecords("下拉刷新获取网络订单列表");
            OrderService orderService = OrderService.getInstance();
            final Store store = Store.getInstance(MyApplication.getInstance());
            orderService.syncNetOrders(store.getPreordertime(), new ResultCallback<List<Order>>() {
                @Override
                public void onResult(List<Order> orderList) {
                    dissmiss();
                    if (!ToolsUtils.isList(orderList)) {
                        Log.i(TAG, "后台轮训网上订单信息条数=====" + orderList.size());
                        if (store.getAutoMaticNetOrder()) {
                            autoMaticNetOrder(orderList);
                        }
                        setNetOrderData(orderList);
                    } else {
                        adapter.setData(null);
                        adapter.changeMoreStatus(adapter.NO_MORE);
                        orderSrl.setRefreshing(false);
                        showToast("没有新的网络订单信息!");
                        Log.i("没有网络订单信息", ToolsUtils.getPrinterSth(orderList));
                        Log.i(TAG, "后台轮训网上订单信息条数=====0");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    dissmiss();
                    adapter.setData(null);
                    adapter.changeMoreStatus(adapter.NO_MORE);
                    orderSrl.setRefreshing(false);
                    showToast("获取网络订单失败 " + e.getMessage());
                    Log.i("获取网络订单失败", e.getMessage());
                    Log.i(TAG, "后台轮训网上订单信息条数=====0");
                }
            });
        } catch (PosServiceException e) {
            dissmiss();
            e.printStackTrace();
            Log.i("获取网络订单失败", e.getMessage());
        }
    }

    private void autoMaticNetOrder(List<Order> orderList) {
        if (!ToolsUtils.isList(orderList)) {
            restoreSyncNetOrder(10, orderList);
        }
    }

    public void restoreSyncNetOrder(long time, final List<Order> orderList) {
        timerStart = new Timer();
        final int[] i = {0};
        final int size = orderList.size();
        handlerStart = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                try {
                    // 要做的事情
                    if (orderList != null && orderList.size() > 0) {
                        Order orderInfo = ToolsUtils.cloneTo(orderList.get(i[0]));
                        if (orderInfo != null) {
                            setStopSyncNetOrder(false);//停止轮训网上订单
                            ToolsUtils.writeUserOperationRecords("接受网上订单==>订单Id==" + orderInfo.getId());
                            String source = orderInfo.getSource();
                            if (!TextUtils.isEmpty(source)) {
                                if (source.equals("2")) {
                                    source = "微信堂食";
                                }
                            } else {
                                source = "未知来源";
                            }
                            orderInfo.setSource(source);
                            if (ToolsUtils.logicIsTable()) {
                                adapter.checkDishCount(orderInfo);
                            } else {
                                adapter.checkDishCount(orderInfo);
                            }
                            if (i[0] == size - 1) {
                                cancleStartTimer();
                                setStopSyncNetOrder(true);//开始轮训网上订单
                            }
                            i[0] = i[0] + 1;
                        } else {
                            cancleStartTimer();
                            setStopSyncNetOrder(true);//开始轮训网上订单
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    cancleStartTimer();
                    setStopSyncNetOrder(true);//开始轮训网上订单
                }
            }
        };
        taskStart = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handlerStart.sendMessage(message);
            }
        };
        timerStart.schedule(taskStart, 0, time * 1000);//5秒循环一次获取网上订单详情
    }


    private void cycleData() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                // 要做的事情
                Log.i(TAG, "开始前台轮训网上订单");
                if (isStopSyncNetOrder()) {
                    loadData(false);
                }
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
        timer.schedule(task, 0, delayedTime);//不延时并且5秒循环一次获取网上订单
    }

    private void timerCancel() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
