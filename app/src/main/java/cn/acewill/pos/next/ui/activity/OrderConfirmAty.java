package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.PaymentStatus;
import cn.acewill.pos.next.presenter.OrderPresenter;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.adapter.OrderConfirmAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;

/**
 * 确认订单
 * Created by DHH on 2016/7/22.
 */
public class OrderConfirmAty extends BaseActivity implements DialogView{

    @BindView( R.id.table_num )
    TextView tableNum;
    @BindView( R.id.order_lv )
    ListView orderLv;
    @BindView( R.id.order_allmoney )
    TextView orderAllmoney;
    @BindView( R.id.order_sub )
    TextView orderSub;

    private Gson gson;
    private OrderPresenter orderPresenter;
    private ProgressDialogF progressDialog;
    private OrderConfirmAdp orderConfirmAdp;
    private List<Dish> mDishs;

    private List<Map<String, String>> dishList = new ArrayList<Map<String, String>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_orde_confirm);
        myApplication.addPage(OrderConfirmAty.this);
        setTitle(ToolsUtils.returnXMLStr("order_confirm"));
        setShowBtnBack(true);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        progressDialog = new ProgressDialogF(this);
        orderPresenter = new OrderPresenter(this);
        gson = new Gson();
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        String priceSum = String.format("%.2f ", Cart.getPriceSum());
        mDishs = Cart.getAllOrderDish();
        orderAllmoney.setText(priceSum);
        orderConfirmAdp = new OrderConfirmAdp(context);
        orderConfirmAdp.setData(mDishs);
        orderLv.setAdapter(orderConfirmAdp);
    }

    /**
     * 生成订单
     * @return
     */
    public Order createOrderItem()
    {
        Order order = new Order();
        if(mDishs != null)
        {
//            order.setTotal(Cart.getPriceSum());
//            order.setCost(Cart.getPriceSum());
            order.setCreatedAt(System.currentTimeMillis());
            order.setSource("智能POS—NO1");
            order.setDiscount(1);
            order.setCreatedBy("机器人1号");
            order.setCustomerAmount(2);
            order.setSubtraction(0);
            order.setOrderType("EAT_IN");
            order.setPaymentStatus( PaymentStatus.NOT_PAYED);
            int dishSize =  mDishs.size();
            List<OrderItem> itemList = new ArrayList<>();
            for (int i = 0; i < dishSize; i++) {
                OrderItem item = new OrderItem();
                Dish dish = mDishs.get(i);
                item.setDishId(dish.getDishId());
                item.setDishName(dish.getDishName());
                item.setPrice(dish.getPrice());
//                item.setCost(dish.getPrice()*order.getDiscount());
//                item.setQuantity( dish.selectDishCount);
                item.setComment("不好吃,不给钱");
                item.setGift(true);
                itemList.add(item);
            }
        }
        return order;
    }

    @OnClick( {R.id.order_allmoney, R.id.order_sub} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.order_allmoney:

                break;
            case R.id.order_sub:
                orderPresenter.createOrder(createOrderItem());
                break;
        }
    }

    @Override
    public void showDialog() {
        progressDialog.show();
    }

    @Override
    public void dissDialog() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(PosServiceException e) {
        progressDialog.dismiss();
    }

    @Override
    public <T> void callBackData(T t) {
        progressDialog.dismiss();
        int resultCode = (Integer)t;
        System.out.println((Integer)t+"<<=====");
        if(resultCode == 0)
        {
            Cart.cleanDishList();
        }

    }
}
