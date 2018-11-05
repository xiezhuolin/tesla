package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.NetOrderController;
import cn.acewill.pos.next.common.TimerTaskController;
import cn.acewill.pos.next.interfices.NetOrderinfoCallBack;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.ui.adapter.NetOrderAdapter;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2017/6/12.
 */

public class NetOrderNewAty extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, NetOrderAdapter.RefrushLisener,NetOrderinfoCallBack {
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.print_close_ll )
    LinearLayout printCloseLl;

    private String TAG = "NetOrderNewAty";
    private NetOrderAdapter adapter;
    private List<Order> orderList = new ArrayList<Order>();
    private int lastVisibleItem = 0;
    private Intent intent;
    private int limit = 20;

    private boolean isPrinting = false;//是否正在打印中、
    private boolean isShowToast = true;//是否显示Toast
    private TimerTaskController timerTaskController;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_net_order);
        ButterKnife.bind(this);
        myApplication.addPage(NetOrderNewAty.this);
        initView();
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

    @Override
    protected void onResume() {
        super.onResume();
        isShowToast = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isShowToast = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShowToast = false;
    }

    private void initView() {
        isPrinting = false;
        intent = new Intent();
        timerTaskController = TimerTaskController.getInstance();
        timerTaskController.setNetOrderinfoCallBack(this);
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


    @Override
    public void onRefresh() {
        loadData(false);
    }

    @Override
    public void refrush() {
        loadData(false);
    }

    private void loadData(boolean isCome) {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
    }

    @Override
    public void getNetOrderInfoList(List<Order> orderList) {
        if (!ToolsUtils.isList(orderList)) {
            adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
            setNetOrderData(orderList);
        }
        else{
            adapter.setData(null);
            adapter.changeMoreStatus(adapter.NO_MORE);
            orderSrl.setRefreshing(false);
            Log.i("没有网络订单信息", ToolsUtils.getPrinterSth(orderList));
            Log.i(TAG, "后台轮训网上订单信息条数=====0");
            if(isShowToast)
            {
                showToast("没有新的网络订单信息!");
            }
        }
    }

    @Override
    public void printState(boolean isPrint) {
        isPrinting = isPrint;
    }
}
