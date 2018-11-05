package cn.acewill.pos.next.ui.fragment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.OrderItemReportData;
import cn.acewill.pos.next.model.report.DishReport;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.adapter.ReportDishAdapter;
import cn.acewill.pos.next.ui.adapter.ReportPayAdapter;
import cn.acewill.pos.next.widget.ComTextView;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * xtt报表
 * Created by aqw on 2016/8/20.
 */
public class XttReportFragment extends BaseFragment {

    @BindView(R.id.bc_order_num)
    ComTextView bcOrderNum;
    @BindView(R.id.bc_order_money)
    ComTextView bcOrderMoney;
    @BindView(R.id.bc_refund_order_num)
    ComTextView bcRefundOrderNum;
    @BindView(R.id.bc_refund_order_money)
    ComTextView bcRefundOrderMoney;
    @BindView(R.id.bc_refund_dish_num)
    ComTextView bcRefundDishNum;
    @BindView(R.id.bc_refund_dish_money)
    ComTextView bcRefundDishMoney;
    @BindView(R.id.day_order_num)
    ComTextView dayOrderNum;
    @BindView(R.id.day_order_money)
    ComTextView dayOrderMoney;
    @BindView(R.id.day_refund_order_num)
    ComTextView dayRefundOrderNum;
    @BindView(R.id.day_refund_order_money)
    ComTextView dayRefundOrderMoney;
    @BindView(R.id.day_refund_dish_num)
    ComTextView dayRefundDishNum;
    @BindView(R.id.day_refund_dish_money)
    ComTextView dayRefundDishMoney;
    @BindView(R.id.bc_pay_list)
    ListView bcPayList;
    @BindView(R.id.day_pay_list)
    ListView dayPayList;
    @BindView(R.id.bc_sv)
    ScrollView bcSv;
    @BindView(R.id.day_sv)
    ScrollView daySv;
    @BindView(R.id.bc_tip_bg)
    LinearLayout orderTipBg;
    @BindView(R.id.bc_order_tip_bg)
    LinearLayout bcOrderTipBg;
    @BindView(R.id.bc_dish_tip_bg)
    LinearLayout bcDishTipBg;
    @BindView(R.id.day_tip_bg)
    LinearLayout dayTipBg;
    @BindView(R.id.day_order_tip_bg)
    LinearLayout dayOrderTipBg;
    @BindView(R.id.day_dish_tip_bg)
    LinearLayout dayDishTipBg;
    @BindView(R.id.bc_pay_ll)
    LinearLayout bcPayLl;
    @BindView(R.id.day_pay_ll)
    LinearLayout dayPayLl;
    @BindView(R.id.day_dish_list)
    ScrolListView dayDishList;
    @BindView(R.id.day_dish_ll)
    LinearLayout dayDishLl;

    private ReportPayAdapter bcAdapter;//班次
    private ReportPayAdapter dayAdapter;//当天
    private ReportDishAdapter dishAdapter;//菜品销售

    private StoreBusinessService storeBusinessService;
    private PosInfo posInfo ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report_home, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        posInfo = PosInfo.getInstance();
        setBg();
        try {
            storeBusinessService = StoreBusinessService.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        bcAdapter = new ReportPayAdapter(mContext);
        dayAdapter = new ReportPayAdapter(mContext);
        dishAdapter = new ReportDishAdapter(mContext);

        bcPayList.setAdapter(bcAdapter);
        dayPayList.setAdapter(dayAdapter);
        dayDishList.setAdapter(dishAdapter);
    }

    private void setBg() {
        GradientDrawable myGrad = (GradientDrawable) orderTipBg.getBackground();
        myGrad.setColor(ContextCompat.getColor(mContext, R.color.green_nomal));

        GradientDrawable myGrad1 = (GradientDrawable) bcOrderTipBg.getBackground();
        myGrad1.setColor(ContextCompat.getColor(mContext, R.color.gray_check_sth));

        GradientDrawable myGrad2 = (GradientDrawable) bcDishTipBg.getBackground();
        myGrad2.setColor(ContextCompat.getColor(mContext, R.color.font_gray));

        GradientDrawable myGrad3 = (GradientDrawable) dayTipBg.getBackground();
        myGrad3.setColor(ContextCompat.getColor(mContext, R.color.green_nomal));

        GradientDrawable myGrad4 = (GradientDrawable) dayOrderTipBg.getBackground();
        myGrad4.setColor(ContextCompat.getColor(mContext, R.color.gray_check_sth));

        GradientDrawable myGrad5 = (GradientDrawable) dayDishTipBg.getBackground();
        myGrad5.setColor(ContextCompat.getColor(mContext, R.color.font_gray));
    }

    @Override
    public void onResume() {
        super.onResume();
        getDishReport();
        getReport(posInfo.getWorkShiftId()+"");
    }

    /**
     * 获取当日班次和当天的报表数据
     */
    public void getReport(String workShiftId) {
        try {
            storeBusinessService.getReportData(workShiftId, new ResultCallback<List<OrderItemReportData>>() {
                @Override
                public void onResult(List<OrderItemReportData> result) {
                    if (result != null && result.size() > 0) {

                        for (OrderItemReportData item : result) {
                            if ("当前班次".equals(item.getNote())) {
                                bcOrderNum.setText(item.getOrderTotal() + "");
                                bcOrderMoney.setText("￥" + item.getSalesTotal());
                                bcRefundOrderNum.setText(item.getExitOrderTotal() + "");
                                bcRefundOrderMoney.setText("￥" + item.getExitOrderSalesTotal());
                                bcRefundDishNum.setText(item.getExitItemTotal() + "");
                                bcRefundDishMoney.setText("￥" + item.getExitItemSalesTotal());

                                List<OrderItemReportData.ItemSalesData> itemSalesDatas = item.getItemSalesDatas();
                                if (itemSalesDatas != null && itemSalesDatas.size() > 0) {
                                    bcPayLl.setVisibility(View.VISIBLE);
                                    int count = 0;
                                    BigDecimal money = new BigDecimal(0);
                                    for (OrderItemReportData.ItemSalesData itemSalesData : itemSalesDatas) {
                                        count += itemSalesData.itemCounts;
                                        money = money.add(itemSalesData.total);
                                    }
                                    OrderItemReportData.ItemSalesData itemData = new OrderItemReportData.ItemSalesData();
                                    itemData.name = "合计";
                                    itemData.itemCounts = count;
                                    itemData.total = money;
                                    itemSalesDatas.add(itemData);

                                    bcAdapter.setData(itemSalesDatas);
                                } else {
                                    bcPayLl.setVisibility(View.GONE);
                                }

                            }
                            if ("当天".equals(item.getNote())) {
                                dayOrderNum.setText(item.getOrderTotal() + "");
                                dayOrderMoney.setText("￥" + item.getSalesTotal());
                                dayRefundOrderNum.setText(item.getExitOrderTotal() + "");
                                dayRefundOrderMoney.setText("￥" + item.getExitOrderSalesTotal());
                                dayRefundDishNum.setText(item.getExitItemTotal() + "");
                                dayRefundDishMoney.setText("￥" + item.getExitItemSalesTotal());

                                List<OrderItemReportData.ItemSalesData> itemSalesDatas = item.getItemSalesDatas();
                                if (itemSalesDatas != null && itemSalesDatas.size() > 0) {
                                    dayPayLl.setVisibility(View.VISIBLE);
                                    int count = 0;
                                    BigDecimal money = new BigDecimal(0);
                                    for (OrderItemReportData.ItemSalesData itemSalesData : itemSalesDatas) {
                                        count += itemSalesData.itemCounts;
                                        money = money.add(itemSalesData.total);
                                    }
                                    OrderItemReportData.ItemSalesData itemData = new OrderItemReportData.ItemSalesData();
                                    itemData.name = "合计";
                                    itemData.itemCounts = count;
                                    itemData.total = money;
                                    itemSalesDatas.add(itemData);

                                    dayAdapter.setData(itemSalesDatas);
                                } else {
                                    dayPayLl.setVisibility(View.GONE);
                                }

                            }
                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.e("getReport", e.getMessage());
                    showToast(e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取菜品销售报表
     */
    private void getDishReport() {
        storeBusinessService.getDishReport(new ResultCallback<List<DishReport>>() {
            @Override
            public void onResult(List<DishReport> result) {

                if(result!=null&&result.size()>0){
                    dayDishLl.setVisibility(View.VISIBLE);

                    dishAdapter.setData(result);
                }else {
                    dayDishLl.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(PosServiceException e) {
                e.printStackTrace();
            }
        });
    }
}
