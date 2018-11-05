package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.presenter.OrderPresenter;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.adapter.OrderDayAdapter;

/**
 * 当日订单
 */
public class OrderFragment extends BaseFragment implements DialogView, SwipeRefreshLayout.OnRefreshListener,OrderDayAdapter.RefrushLisener {

    @BindView(R.id.order_lv)
    RecyclerView orderLv;
    @BindView(R.id.order_srl)
    SwipeRefreshLayout orderSrl;
    @BindView(R.id.all_ord)
    TextView allOrd;
    @BindView(R.id.tack_out_ord)
    TextView tackOutOrd;
    @BindView(R.id.online_ord)
    TextView onlineOrd;
    @BindView(R.id.unpayde_ord)
    TextView unpaydeOrd;
    @BindView(R.id.payde_ord)
    TextView paydeOrd;

    private OrderPresenter orderPresenter;
    private OrderDayAdapter adapter;
    private List<Order> orderList = new ArrayList<Order>();
    private int lastVisibleItem = 0;


    private int page = 0;
    private int limit = 20;
    private int orderType = -1;//-1:全部;0:堂食;1:外带;2:外卖
    private int payStatus = -1;//-1:全部;0:未支付;1:已经支付;2:已经退款


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_order, container, false);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {

        allOrd.setSelected(true);

        orderPresenter = new OrderPresenter(this);
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new OrderDayAdapter(getActivity(), orderList, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);

        orderLv.setAdapter(adapter);

        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//                if(lastVisibleItem +1 == adapter.getItemCount() && adapter.load_more_status==adapter.LOAD_MORE){
//                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
//                    adapter.changeMoreStatus(adapter.LOADING);
//                    orderPresenter.getAllOrders(++page,limit);
//                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);
                    orderPresenter.getAllOrders(++page, limit,orderType,payStatus);
                }
            }
        });
    }

    private void initData(){
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        page = 0;
        orderPresenter.getAllOrders(page, limit,orderType,payStatus);
    }

    @Override
    public void onResume() {
        super.onResume();
//        orderSrl.post(new Runnable() {
//            @Override
//            public void run() {
//                orderSrl.setRefreshing(true);
//            }
//        });
        initData();
    }

    @Override
    public void showDialog() {

    }

    @Override
    public void dissDialog() {

    }

    @Override
    public void showError(PosServiceException e) {
        orderSrl.setRefreshing(false);
    }

    @Override
    public <T> void callBackData(T t) {
        //TODO
        List<Order> orders = (List<Order>) t;
        adapter.setData(orders);
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRefresh() {
        initData();
    }


    @Override
    public void refrush() {
        initData();
    }

    @OnClick({R.id.all_ord, R.id.tack_out_ord, R.id.online_ord, R.id.unpayde_ord, R.id.payde_ord})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.all_ord://
                setTabColor(allOrd);
                orderType = -1;
                payStatus = -1;
                break;
            case R.id.tack_out_ord://外带
                setTabColor(tackOutOrd);
                orderType = 1;
                payStatus = -1;
                break;
            case R.id.online_ord://外卖
                setTabColor(onlineOrd);
                payStatus = -1;
                orderType = 2;
                break;
            case R.id.unpayde_ord://未支付
                setTabColor(unpaydeOrd);
                orderType = -1;
                payStatus = 0;
                break;
            case R.id.payde_ord://已支付
                setTabColor(paydeOrd);
                orderType = -1;
                payStatus = 1;
                break;
        }

        initData();
    }

    private void setTabColor(TextView view){
        allOrd.setSelected(false);
        tackOutOrd.setSelected(false);
        onlineOrd.setSelected(false);
        unpaydeOrd.setSelected(false);
        paydeOrd.setSelected(false);
        view.setSelected(true);
    }
}
