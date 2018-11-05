package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.interfices.FetchOrderCallBack;
import cn.acewill.pos.next.model.FetchOrder;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.ui.adapter.FetchOrderAdp;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 取单
 * Created by DHH on 2017/3/21.
 */

public class FetchOrderAty extends BaseActivity {
    @BindView( R.id.tv_title )
    TextView tvTitle;
    @BindView( R.id.lv_order )
    ListView lv_order;

    private FetchOrderAdp fetchOrderAdp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_fetch_order);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvTitle.setText("取单");
        findViewById(R.id.rel_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolsUtils.writeUserOperationRecords("退出取单界面");
                finish();
            }
        });
        fetchOrderAdp = new FetchOrderAdp(context, new FetchOrderCallBack() {
            @Override
            public void onOk(Object o,int position) {
                FetchOrder fetchOrder = (FetchOrder) o;
                if (Cart.isCartDishNull()) {
                    if (fetchOrder.getFetchDishList() != null && fetchOrder.getFetchDishList().size() > 0) {
                        Cart.setDishItemList( ToolsUtils.cloneTo(fetchOrder.getFetchDishList()));
                        Cart.removeHandDishList(position);
                        setResult(RESULT_OK);
                        FetchOrderAty.this.finish();
                    }
                } else {
                    MyApplication.getInstance().ShowToast("购物车中已有未下单菜品,请先下单或清空购物车!");
                }
            }
        });
        fetchOrderAdp.setData(Cart.getHandDishItemList());
        lv_order.setAdapter(fetchOrderAdp);
        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }
}
