package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.interfices.NetOrderinfoCallBack;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.ui.fragment.NetOrderFragment;
import cn.acewill.pos.next.ui.fragment.OrderNewFragment;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.SegmentedControlItem;
import cn.acewill.pos.next.widget.SegmentedControlView;

/**
 * 关于POS
 * Created by aqw on 2016/12/8.
 */
public class OrderAmountAty extends BaseActivity implements NetOrderinfoCallBack {
    @BindView( R.id.rel_back )
    RelativeLayout relBack;
    @BindView( R.id.scv_top )
    SegmentedControlView scvTop;

    private NetOrderFragment netOrderFragment;
    private OrderNewFragment orderNewFragment;

    private ArrayList<TextView> comTextViewList = new ArrayList<>();
    private List<SegmentedControlItem> items = new ArrayList<>();
    private SegmentedControlItem netOrderItem;
    private SegmentedControlItem offLineItem;
    private int RECHECKOUT = 100;//反结账跳转
    private Store store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_order_mount);
        myApplication.addPage(OrderAmountAty.this);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        netOrderItem = new SegmentedControlItem(ToolsUtils.returnXMLStr("net_order"));
        offLineItem = new SegmentedControlItem(ToolsUtils.returnXMLStr("ol_order"));
        store = store.getInstance(context);
        if (store.isNetOrderFirstShow()) {
            items.add(netOrderItem);
            items.add(offLineItem);
        } else {
            items.add(offLineItem);
            items.add(netOrderItem);
        }
        scvTop.addItems(items);

        changeTab(1);
        scvTop.setOnSegItemClickListener(new SegmentedControlView.OnSegItemClickListener() {
            @Override
            public void onItemClick(SegmentedControlItem item, int position) {
                changeTab(position);
            }
        });
    }

    private void changeTab(int tab) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (tab) {
            case 0:
                logicShowNetorder(ft, tab);
                break;
            case 1:
                logicShowNetorder(ft, tab);
                break;
        }
        ft.commit();
    }

    @OnClick( {R.id.rel_back} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_back:
                ToolsUtils.writeUserOperationRecords("退出更多界面");
                finish();
                break;
        }
    }

    private void logicShowNetorder(FragmentTransaction ft, int tab) {
        if (store.isNetOrderFirstShow()) {
            if (tab == 0) {
                showNetOrderFragment(ft);
            } else {
                showOrderFragment(ft);
            }
        } else {
            if (tab == 0) {
                showOrderFragment(ft);
            } else {
                showNetOrderFragment(ft);
            }
        }
    }

    private void showNetOrderFragment(FragmentTransaction ft) {
        if (netOrderFragment == null) {
            netOrderFragment = new NetOrderFragment();
        }
        netOrderFragment.setShowToast(true);
        ft.replace(R.id.set_content, netOrderFragment);
    }

    private void showOrderFragment(FragmentTransaction ft) {
        if (orderNewFragment == null) {
            orderNewFragment = new OrderNewFragment();
        }
        if (netOrderFragment != null) {
            netOrderFragment.setShowToast(false);
        }
        ft.replace(R.id.set_content, orderNewFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netOrderFragment != null) {
            netOrderFragment.setShowToast(false);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    public void getNetOrderInfoList(List<Order> orderList) {
        if (ToolsUtils.isList(orderList)) {
            netOrderItem.setName("网上订单");
        } else {
            netOrderItem.setName("网上订单(" + orderList.size() + ")");
        }
    }

    @Override
    public void printState(boolean isPrint) {

    }
}
