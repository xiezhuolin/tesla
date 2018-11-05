package cn.acewill.pos.next.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.presenter.OrderPresenter;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.adapter.OrderDayNewAdapter;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.WindowUtil;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.OnRecyclerItemClickListener;

/**
 * 订单信息列表
 * Created by DHH on 2016/6/12.
 */
public class OrderInfoAty extends BaseActivity implements DialogView, SwipeRefreshLayout.OnRefreshListener, OrderDayNewAdapter.RefrushLisener {
    @BindView( R.id.rel_back )
    RelativeLayout relBack;
    @BindView( R.id.img_refresh )
    ImageView imgRefresh;
    @BindView( R.id.btn_refresh )
    TextView btnRefresh;
    @BindView( R.id.rel_refresh )
    RelativeLayout relRefresh;
    @BindView( R.id.rel_top )
    RelativeLayout relTop;
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.all_ord )
    TextView allOrd;
    @BindView( R.id.tack_out_ord )
    TextView tackOutOrd;
    @BindView( R.id.online_ord )
    TextView onlineOrd;
    @BindView( R.id.unpayde_ord )
    TextView unpaydeOrd;
    @BindView( R.id.payde_ord )
    TextView paydeOrd;
    @BindView( R.id.tv_number )
    TextView tvNumber;
    @BindView( R.id.search_cotent )
    CommonEditText edMemberNumber;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;

    private OrderPresenter orderPresenter;
    private OrderDayNewAdapter adapter;
    private List<Order> orderList = new ArrayList<Order>();
    private int lastVisibleItem = 0;


    private int page = 0;
    private int limit = 200;
    private int orderType = -1;//-1:全部;0:堂食;1:外带;2:外卖
    private int payStatus = -1;//-1:全部;0:未支付;1:已经支付;2:已经退款

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_order_info);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void loadData() {
        //edtext的控件内容监听
        edMemberNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                    List<Order> orders = null;
                    if (ToolsUtils.isNumeric(s.toString())) {
                        orders = getOrderInfoByOrderId(orderList, s.toString());
                        adapter.setData(orders);
                        adapter.changeMoreStatus(adapter.NO_MORE);
                        orderSrl.setRefreshing(false);
                    }
                } else {
                    searchClear.setVisibility(View.GONE);
                    resetOrderInfoView(orderList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void resetOrderInfoView(List<Order> orderList)
    {
        adapter.setData(orderList);
        if (orderList != null && orderList.size() > 0) {
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

    private List<Order> getOrderInfoByOrderId(List<Order> orderList, String orderId) {
        List<Order> orderS = new CopyOnWriteArrayList<>();
        if (orderList != null && orderList.size() > 0) {
            for (Order order : orderList) {
                if ((order.getId() + "").contains(orderId)) {
                    orderS.add(order);
                }
            }
        }
        return orderS;
    }

    private void initView() {
        myApplication.addPage(OrderInfoAty.this);
        allOrd.setSelected(true);

        orderPresenter = new OrderPresenter(this);
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new OrderDayNewAdapter(context, orderList, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);
        if (ToolsUtils.logicIsTable()) {
            tvNumber.setText(ToolsUtils.returnXMLStr("table_number"));
        } else {
            if (StoreInfor.cardNumberMode) {
                tvNumber.setText(ToolsUtils.returnXMLStr("card_number"));
            } else {
                tvNumber.setText("取餐号");
            }
        }

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
                    orderPresenter.getAllOrders(++page, limit, orderType, payStatus);
                }
            }
        });

        orderLv.addOnItemTouchListener(new OnRecyclerItemClickListener(orderLv) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                Order order = (Order) adapter.getItem(position);
                if (order != null) {
                    Intent intent = new Intent();
                    ToolsUtils.writeUserOperationRecords("跳转订单详情界面");
                    intent.setClass(OrderInfoAty.this, OrderDetailAty.class);
                    intent.putExtra("orderId", order.getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {

            }
        });
    }

    private void initData() {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        page = 0;
        orderPresenter.getAllOrders(page, limit, orderType, payStatus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void showDialog() {
        showProgress("正在获取订单详情...");
    }

    @Override
    public void dissDialog() {
        dissmiss();
    }

    @Override
    public void showError(PosServiceException e) {
        orderSrl.setRefreshing(false);
    }

    @Override
    public <T> void callBackData(T t) {
        List<Order> orders = (List<Order>) t;
        adapter.setData(orders);
        orderList = ToolsUtils.cloneTo(orders);
        if (orders != null && orders.size() > 0) {
            if (orders.size() < limit) {
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
    public void refrush() {
        initData();
    }

    @OnClick( {R.id.search_clear, R.id.all_ord, R.id.tack_out_ord, R.id.online_ord, R.id.unpayde_ord, R.id.payde_ord, R.id.rel_back, R.id.rel_refresh} )
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.search_clear:
                WindowUtil.hiddenKey();
                edMemberNumber.setText("");
                resetOrderInfoView(orderList);
                break;
            case R.id.rel_refresh:
                ToolsUtils.writeUserOperationRecords("刷新订单列表");
                initData();
                break;
            case R.id.rel_back:
                ToolsUtils.writeUserOperationRecords("退出订单列表页面");
                EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_TABLE));
                OrderInfoAty.this.finish();
                break;
            case R.id.all_ord://
                ToolsUtils.writeUserOperationRecords("切换至全部订单列表");
                setTabColor(allOrd);
                orderType = -1;
                payStatus = -1;
                break;
            case R.id.tack_out_ord://外带
                ToolsUtils.writeUserOperationRecords("切换至外带订单列表");
                setTabColor(tackOutOrd);
                orderType = 1;
                payStatus = -1;
                break;
            case R.id.online_ord://外卖
                ToolsUtils.writeUserOperationRecords("切换至外卖订单列表");
                setTabColor(onlineOrd);
                payStatus = -1;
                orderType = 2;
                break;
            case R.id.unpayde_ord://未支付
                ToolsUtils.writeUserOperationRecords("切换至未支付订单列表");
                setTabColor(unpaydeOrd);
                orderType = -1;
                payStatus = 0;
                break;
            case R.id.payde_ord://已支付
                ToolsUtils.writeUserOperationRecords("切换至已支付订单列表");
                setTabColor(paydeOrd);
                orderType = -1;
                payStatus = 1;
                break;
        }

        initData();
    }

    private void setTabColor(TextView view) {
        allOrd.setSelected(false);
        tackOutOrd.setSelected(false);
        onlineOrd.setSelected(false);
        unpaydeOrd.setSelected(false);
        paydeOrd.setSelected(false);
        view.setSelected(true);
    }
}
