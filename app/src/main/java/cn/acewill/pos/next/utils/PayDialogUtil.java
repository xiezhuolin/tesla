package cn.acewill.pos.next.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.PosSinUsbScreenController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogActiveCall;
import cn.acewill.pos.next.interfices.DialogCall;
import cn.acewill.pos.next.interfices.DishCheckCallback;
import cn.acewill.pos.next.interfices.KeyCallBack;
import cn.acewill.pos.next.model.MarketObject;
import cn.acewill.pos.next.model.MarketType;
import cn.acewill.pos.next.model.PaymentCategory;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.order.MarketingActivity;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.adapter.ActivityListAdapter;
import cn.acewill.pos.next.ui.adapter.TickPrintAdapter;
import cn.acewill.pos.next.widget.ComTextView;
import cn.acewill.pos.next.widget.CommonEditText;

import static cn.acewill.pos.next.common.MarketDataController.equalOption;
import static cn.acewill.pos.next.model.dish.Cart.getDishItemList;

/**
 * 结账使用
 * Created by aqw on 2016/11/18.
 */
public class PayDialogUtil {

    /**
     * 自定义金额键盘
     *
     * @param context
     * @param money
     * @param isShowNopay 是否显示未支付金额
     * @return
     */
    public static Dialog keyNumDialog(final Context context, final Payment payment, final KeyCallBack callBack, final BigDecimal money, final boolean isShowNopay) {

        final int[] othValues = FormatUtils.getMoney(money.intValue());

        final Dialog dialog = DialogUtil.createDialog(context, R.layout.key_number, 6, 7, true);

        ComTextView key_close = (ComTextView) dialog.findViewById(R.id.key_close);
        final ComTextView key_money = (ComTextView) dialog.findViewById(R.id.key_money);
        final LinearLayout no_pay_ll = (LinearLayout) dialog.findViewById(R.id.no_pay_ll);
        final ComTextView no_pay_tv = (ComTextView) dialog.findViewById(R.id.no_pay_tv);
        final ComTextView no_pay_money = (ComTextView) dialog.findViewById(R.id.no_pay_money);
        ComTextView key_title = (ComTextView) dialog.findViewById(R.id.key_title);
        ComTextView key_clear = (ComTextView) dialog.findViewById(R.id.key_clear);
        ComTextView key_one = (ComTextView) dialog.findViewById(R.id.key_one);
        ComTextView key_two = (ComTextView) dialog.findViewById(R.id.key_two);
        ComTextView key_three = (ComTextView) dialog.findViewById(R.id.key_three);
        ComTextView key_other_one = (ComTextView) dialog.findViewById(R.id.key_other_one);
        ComTextView key_four = (ComTextView) dialog.findViewById(R.id.key_four);
        ComTextView key_five = (ComTextView) dialog.findViewById(R.id.key_five);
        ComTextView key_six = (ComTextView) dialog.findViewById(R.id.key_six);
        ComTextView key_other_two = (ComTextView) dialog.findViewById(R.id.key_other_two);
        ComTextView key_seven = (ComTextView) dialog.findViewById(R.id.key_seven);
        ComTextView key_eight = (ComTextView) dialog.findViewById(R.id.key_eight);
        ComTextView key_nine = (ComTextView) dialog.findViewById(R.id.key_nine);
        ComTextView key_other_three = (ComTextView) dialog.findViewById(R.id.key_other_three);
        ComTextView key_delete = (ComTextView) dialog.findViewById(R.id.key_delete);
        ComTextView key_zero = (ComTextView) dialog.findViewById(R.id.key_zero);
        ComTextView key_point = (ComTextView) dialog.findViewById(R.id.key_point);
        ComTextView key_ok = (ComTextView) dialog.findViewById(R.id.key_ok);

        key_other_one.setText(othValues[0] + "￥");
        key_other_two.setText(othValues[1] + "￥");
        key_other_three.setText(othValues[2] + "￥");

        String title = payment.getName();
        key_title.setText(TextUtils.isEmpty(title) ? ToolsUtils.returnXMLStr("input_amount") : title);

        if (isShowNopay) {
            no_pay_ll.setVisibility(View.VISIBLE);

            if (money.compareTo(BigDecimal.ZERO) == 1) {
                no_pay_tv.setText(ToolsUtils.returnXMLStr("unpaid2"));
                no_pay_money.setText(money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                PosSinUsbScreenController.getInstance().ledDisplayTotal(money.setScale(2, BigDecimal.ROUND_DOWN)+"");
                no_pay_money.setTextColor(context.getResources().getColor(R.color.actionsheet_red));
            } else {
                no_pay_tv.setText(ToolsUtils.returnXMLStr("sth_returnMoney"));
                no_pay_money.setText(money.setScale(2, BigDecimal.ROUND_DOWN).negate() + "￥");
                no_pay_money.setTextColor(context.getResources().getColor(R.color.green));
            }

        }

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //清空副屏，展示默认欢迎页
//                SunmiSecondScreen.getInstance(context).showImageListScreen();
            }
        });

        class OnClickLisener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.key_close:
                        if (money.compareTo(BigDecimal.ZERO) == 1) {
                            PosSinUsbScreenController.getInstance().ledDisplayTotal(money.setScale(2, BigDecimal.ROUND_DOWN)+"");
                        }
                        dialog.dismiss();
                        break;
                    case R.id.key_clear:
                        printValue(Keys.KEYCL);
                        break;
                    case R.id.key_one:
                        printValue(Keys.KEY1);
                        break;
                    case R.id.key_two:
                        printValue(Keys.KEY2);
                        break;
                    case R.id.key_three:
                        printValue(Keys.KEY3);
                        break;
                    case R.id.key_other_one:
                        printValue(Keys.KEYMONEY_ONE);
                        break;
                    case R.id.key_four:
                        printValue(Keys.KEY4);
                        break;
                    case R.id.key_five:
                        printValue(Keys.KEY5);
                        break;
                    case R.id.key_six:
                        printValue(Keys.KEY6);
                        break;
                    case R.id.key_other_two:
                        printValue(Keys.KEYMONEY_TWO);
                        break;
                    case R.id.key_seven:
                        printValue(Keys.KEY7);
                        break;
                    case R.id.key_eight:
                        printValue(Keys.KEY8);
                        break;
                    case R.id.key_nine:
                        printValue(Keys.KEY9);
                        break;
                    case R.id.key_other_three:
                        printValue(Keys.KEYMONEY_THREE);
                        break;
                    case R.id.key_delete:
                        printValue(Keys.KEYDE);
                        break;
                    case R.id.key_zero:
                        printValue(Keys.KEY0);
                        break;
                    case R.id.key_point:
                        printValue(Keys.KEYPOINT);
                        break;
                    case R.id.key_ok:
                        printValue(Keys.KEYOK);
                        break;

                }
            }

            private String print_str = "";//输入的字符
            private BigDecimal print_money = new BigDecimal(0);//输入金额

            private void printValue(Keys keys) {

                switch (keys.getValue()) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        if (print_str.length() < 7) {
                            print_str += keys.getValue() + "";
                        }
                        break;
                    case 10://点
                        if (!print_str.contains(".")) {
                            if (print_str.length() < 7) {
                                print_str += ".";
                            }
                        }
                        break;
                    case 11://删除
                        if (print_str.length() > 0) {
                            print_str = print_str.substring(0, print_str.length() - 1);
                        }
                        break;
                    case 12://刚好
                        print_str = money + "";
                        break;
                    case 14://确认
                        dialog.dismiss();
                        callBack.onOk(print_money);
                        break;
                    case 15://自动生成金额1
                        print_str = othValues[0] + "";
                        break;
                    case 16://自动生成金额2
                        print_str = othValues[1] + "";
                        break;
                    case 17://自动生成金额3
                        print_str = othValues[2] + "";
                        break;
                }

                if (keys.getValue() != 14) {
                    if (print_str.equals(".") || TextUtils.isEmpty(print_str)) {
                        print_money = new BigDecimal(0);
                    } else if (print_str.length() > 0 && ".".equals(print_str.charAt(0))) {
                        print_money = new BigDecimal("0" + print_str);
                    } else if (print_str.length() > 0 && ".".equals(print_str.charAt(print_str.length() - 1))) {
                        print_money = new BigDecimal(print_str.substring(0, print_str.length() - 1));
                    } else {
                        print_money = new BigDecimal(print_str);
                    }
                    key_money.setText((TextUtils.isEmpty(print_str) ? "0" : print_str) + "￥");
                    //是否显示未支付
                    if (isShowNopay) {
                        no_pay_ll.setVisibility(View.VISIBLE);
                        BigDecimal nopay_money = money.subtract(print_money);
                        if (nopay_money.compareTo(BigDecimal.ZERO) == 1) {
                            no_pay_tv.setText(ToolsUtils.returnXMLStr("unpaid2"));
                            no_pay_money.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN) + "￥");
                            PosSinUsbScreenController.getInstance().ledDisplayPayment(print_money.setScale(2, BigDecimal.ROUND_DOWN)+"");
                            no_pay_money.setTextColor(context.getResources().getColor(R.color.actionsheet_red));
                        } else {
                            no_pay_tv.setText(ToolsUtils.returnXMLStr("sth_returnMoney"));
                            no_pay_money.setText(nopay_money.setScale(2, BigDecimal.ROUND_DOWN).negate() + "￥");
                            PosSinUsbScreenController.getInstance().ledDisplayChange(nopay_money.setScale(2, BigDecimal.ROUND_DOWN)+"");
                            no_pay_money.setTextColor(context.getResources().getColor(R.color.green));
                        }
                    }
                }

                if (keys.getValue() == 12) {
                    dialog.dismiss();
                    if(payment.getCategory() == PaymentCategory.CASH)
                    {
                        final CheckOutUtil checUtil = new CheckOutUtil(context);
                        checUtil.getDishStock(getDishItemList(), new DishCheckCallback() {
                            @Override
                            public void haveStock() {
                                callBack.onOk(print_money);
                            }

                            @Override
                            public void noStock(List dataList) {
                                DialogUtil.refreshDish(dataList, getDishItemList());
                            }
                        });
                    }
                    else{
                        callBack.onOk(print_money);
                    }
                }

            }
        }


        OnClickLisener onClickLisener = new OnClickLisener();

        key_close.setOnClickListener(onClickLisener);
        key_clear.setOnClickListener(onClickLisener);
        key_one.setOnClickListener(onClickLisener);
        key_two.setOnClickListener(onClickLisener);
        key_three.setOnClickListener(onClickLisener);
        key_other_one.setOnClickListener(onClickLisener);
        key_four.setOnClickListener(onClickLisener);
        key_five.setOnClickListener(onClickLisener);
        key_six.setOnClickListener(onClickLisener);
        key_other_two.setOnClickListener(onClickLisener);
        key_seven.setOnClickListener(onClickLisener);
        key_eight.setOnClickListener(onClickLisener);
        key_nine.setOnClickListener(onClickLisener);
        key_other_three.setOnClickListener(onClickLisener);
        key_delete.setOnClickListener(onClickLisener);
        key_zero.setOnClickListener(onClickLisener);
        key_point.setOnClickListener(onClickLisener);
        key_ok.setOnClickListener(onClickLisener);


        return dialog;
    }

    /**
     * 营销活动
     *
     * @param context
     * @param allMoney 订单总金额
     * @param callBack
     */
    public static void getActiveDialog(final Context context, final BigDecimal[] allMoney, final Order order, final List<Dish> dishList, final DialogActiveCall callBack) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_active, 4, 5, true);
        final List<Dish> singleItemList = new ArrayList<Dish>();
        final BigDecimal[] takeOutMoney = {new BigDecimal("0.00")};//打包费
        int itemIndex = 1;
        //先将菜品拆分出来 最后再进行菜品组装
        if(dishList != null && dishList.size() >0)
        {
            for (Dish item : dishList) {
                int count = item.getQuantity();
                if (count == 1) {
                    item.setItemIndex(itemIndex);
                    itemIndex++;
                    singleItemList.add(item);
                } else {
                    for (int i = 0; i < count; i++) {
                        Dish singleItem = ToolsUtils.cloneTo(item);
                        singleItem.setItemIndex(itemIndex);
                        singleItem.setQuantity(1);
                        singleItem.setTempQuantity(1);
                        itemIndex++;
                        singleItemList.add(singleItem);
                    }
                }
            }
        }


        ComTextView active_close = (ComTextView) dialog.findViewById(R.id.active_close);
        final ListView active_list = (ListView) dialog.findViewById(R.id.active_list);
        final ComTextView none = (ComTextView) dialog.findViewById(R.id.none);
        final LinearLayout none_ll = (LinearLayout) dialog.findViewById(R.id.none_ll);
        final ProgressBar none_pro = (ProgressBar) dialog.findViewById(R.id.none_pro);

        final BigDecimal[] reduceMoney = {new BigDecimal(0)};//优惠金额

        final BigDecimal[] notJobMoney = {new BigDecimal(0)};//不参与打折金额

        if (order != null) {
            BigDecimal tempMarketPrice = new BigDecimal("0.000");
            List<OrderItem> orderItemList = order.getItemList();
            int size = orderItemList.size();
            if (!ToolsUtils.isList(orderItemList)) {
                for (int i = 0; i < size; i++) {
                    OrderItem oi = orderItemList.get(i);
                    if (!ToolsUtils.isList(oi.getTempMarketList())) {
                        for (MarketObject marketObject1 : oi.getTempMarketList()) {
                            if (marketObject1.getMarketType() == MarketType.DISCOUNT) {
                                tempMarketPrice = marketObject1.getReduceCash();
                            }
                        }
                    }
                }
            }
            allMoney[0] = allMoney[0].add(tempMarketPrice);
        }

        final ActivityListAdapter adapter = new ActivityListAdapter(context);
        adapter.setAllMoney(allMoney[0] + "");
        active_list.setAdapter(adapter);

        if (StoreInfor.marketingActivities != null && StoreInfor.marketingActivities.size() > 0) {
            none_ll.setVisibility(View.GONE);
            active_list.setVisibility(View.VISIBLE);
            adapter.setData(StoreInfor.marketingActivities);
        } else {
            none_pro.setVisibility(View.GONE);
            none.setText(ToolsUtils.returnXMLStr("activity_is_null"));
        }

        active_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final MarketingActivity activitys = (MarketingActivity) adapter.getItem(position);
                if(!TextUtils.isEmpty(allMoney[0]+""))
                {
                    if (activitys.getDiscountType() == 1) {
                        BigDecimal reduceMoney = activitys.getDiscountAmount();
                        if (reduceMoney.compareTo(new BigDecimal(allMoney[0]+"")) == 1)//如果折扣金额大于订单金额
                        {
                            return;
                        }
                    }
                }
                BigDecimal marketCost = new BigDecimal("0");//参与活动的菜品总金额
                BigDecimal dishPriceSum = new BigDecimal("0.000");//参加促销活动菜品的总price  最后要拿这个钱来算比例
                if (activitys != null) {
                    if (singleItemList != null && singleItemList.size() > 0) {
                        for (Dish dish : singleItemList) {
                            if (!dish.isParticipationDisCount())//不参与打折的菜品
                            {
                                notJobMoney[0] = notJobMoney[0].add(dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity())));
                            } else {
                                BigDecimal dishPrice = dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity()));
                                dishPriceSum = dishPriceSum.add(dishPrice);
                                marketCost = marketCost.add(dishPrice);
                            }
                        }

                        if(dishPriceSum.compareTo(BigDecimal.ZERO) == 0)
                        {
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("not_dish_join_activity"));
                            return;
                        }
                        Dish tempDish  = null ;
                        //优惠的总金额
                        BigDecimal dishPreferentialSum = new BigDecimal("0.000");
                        //用来临时计算的满减优惠总金额
                        BigDecimal tempDishPreferentialSum = new BigDecimal("0.00");
                        //满减优惠总金额
                        BigDecimal dishPreferentialCashSum = new BigDecimal("0.00");
                        int size = singleItemList.size();
                        //全单满减
                        if (activitys.getDiscountType() == 1) {
                            dishPreferentialSum = activitys.getDiscountAmount();
                            tempDishPreferentialSum = activitys.getDiscountAmount();
                        }
                        else if(activitys.getDiscountType() == 0)
                        {
                            dishPreferentialSum = marketCost.subtract(marketCost.multiply(activitys.getDiscountRate()));
                        }
                        for (int i = 0; i < size; i++) {
                            Dish dish = singleItemList.get(i);
                            if (dish.isParticipationDisCount())
                            {
                                //计算外带打包费用
                                if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                                    if (dish.getWaiDai_cost() != null) {
                                        BigDecimal bigDecimal3 = new BigDecimal(dish.quantity);
                                        takeOutMoney[0] = takeOutMoney[0].add(bigDecimal3.multiply(dish.getWaiDai_cost()));
                                    }
                                }
                                //全单满减
                                if (activitys.getDiscountType() == 1) {
                                    BigDecimal dishTotalCost = dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 2, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(dish.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);
                                    dish.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    dish.setOnlyCost(dish.getOrderDishCost().subtract(dishPartPreferential));
                                    dishPreferentialCashSum = dishPreferentialCashSum.add(dish.getCost().multiply(new BigDecimal(dish.getQuantity())));
                                    if(size -1 == i)//最后一个菜
                                    {
                                        tempDish = dish;
                                    }
                                    else{
                                        setMarketObjectList(dish, activitys, meanDishPreferential);
                                        tempDishPreferentialSum = tempDishPreferentialSum.subtract(meanDishPreferential);
                                    }
                                }
                                //全单折扣
                                else if (activitys.getDiscountType() == 0) {
//                                    //单项菜品优惠金额
                                    BigDecimal dishTotalCost = dish.getOrderDishCost().multiply(new BigDecimal(dish.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 3, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(dish.getQuantity()), 3, BigDecimal.ROUND_HALF_UP);
                                    dish.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    dish.setOnlyCost(dish.getOrderDishCost().subtract(dishPartPreferential));
                                    setMarketObjectList(dish, activitys, meanDishPreferential);
                                }
                            }
                        }
                        if (activitys.getDiscountType() == 1) {
                            BigDecimal price = marketCost.subtract(dishPreferentialSum);//参加完优惠活动后应该是这个价钱
                            tempDish.setOnlyCost(tempDish.getCost().add(price.subtract(dishPreferentialCashSum)));
                            tempDish.setAllOrderDisCountSubtractPrice(tempDish.getAllOrderDisCountSubtractPrice().add(dishPreferentialCashSum.subtract(price)));
                            setMarketObjectList(tempDish, activitys, tempDishPreferentialSum);
                        }

                        Cart.getDishItemList().clear();
                        Cart.dishItemList = assemblyList(singleItemList);

                        allMoney[0] = marketCost.add(notJobMoney[0]).subtract(dishPreferentialSum);//计算完优惠后，再加上之前减去的不参与打折的金额,然后减去优惠的金额，返回最终要支付的总金额
                        callBack.onOk(allMoney[0].add(takeOutMoney[0]).setScale(2, BigDecimal.ROUND_HALF_UP), dishPreferentialSum, activitys.getDiscountName());
                        dialog.dismiss();
                    } else if (order != null) {
                        List<OrderItem> itemList = breakUpList(order.getItemList());
                        for (OrderItem orderItem : itemList) {
                            if (!orderItem.isParticipationDisCount())//不参与打折的菜品
                            {
                                notJobMoney[0] = notJobMoney[0].add(orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity())));
                            }
                            else{
                                BigDecimal dishPrice = orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity()));
                                dishPriceSum = dishPriceSum.add(dishPrice);
                                marketCost = marketCost.add(dishPrice);
                            }
                        }

                        if(dishPriceSum.compareTo(BigDecimal.ZERO) == 0)
                        {
                            MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("not_dish_join_activity"));
                            return;
                        }

                        OrderItem tempOrderItem = null;
                        //优惠的总金额
                        BigDecimal dishPreferentialSum = new BigDecimal("0.000");
                        //用来临时计算的满减优惠总金额
                        BigDecimal tempDishPreferentialSum = new BigDecimal("0.00");
                        //满减优惠总金额
                        BigDecimal dishPreferentialCashSum = new BigDecimal("0.00");
                        //全单满减
                        if (activitys.getDiscountType() == 1) {
                            dishPreferentialSum = activitys.getDiscountAmount();
                            tempDishPreferentialSum = activitys.getDiscountAmount();
                        }
                        else if(activitys.getDiscountType() == 0)
                        {
                            dishPreferentialSum = marketCost.subtract(marketCost.multiply(activitys.getDiscountRate()));
                        }
                        int size = itemList.size();
                        for (int i = 0; i < size; i++) {
                            OrderItem orderItem  = itemList.get(i);
                            if (orderItem.isParticipationDisCount())
                            {
                                //计算外带打包费用
                                if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                                    if (orderItem.getWaiDai_cost() != null) {
                                        BigDecimal bigDecimal3 = new BigDecimal(orderItem.quantity);
                                        takeOutMoney[0] = takeOutMoney[0].add(bigDecimal3.multiply(orderItem.getWaiDai_cost()));
                                    }
                                }
                                //全单满减
                                if (activitys.getDiscountType() == 1) {
                                    BigDecimal dishTotalCost = orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 2, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(orderItem.getQuantity()), 2, BigDecimal.ROUND_HALF_UP);
                                    orderItem.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    orderItem.setOnlyCost(orderItem.getOrderDishCost().subtract(dishPartPreferential));

                                    dishPreferentialCashSum = dishPreferentialCashSum.add(orderItem.getCost().multiply(new BigDecimal(orderItem.getQuantity())));
                                    if(size -1 == i)//最后一个菜
                                    {
                                        tempOrderItem = orderItem;
                                    }
                                    else{
                                        setMarketObjectList(orderItem, activitys, meanDishPreferential);
                                        tempDishPreferentialSum = tempDishPreferentialSum.subtract(meanDishPreferential);
                                    }
                                }
                                //全单折扣
                                else if (activitys.getDiscountType() == 0) {
                                    BigDecimal dishTotalCost = orderItem.getOrderDishCost().multiply(new BigDecimal(orderItem.getQuantity()));
                                    //单项菜品优惠金额
                                    BigDecimal meanDishPreferential = dishTotalCost.multiply(dishPreferentialSum).divide(marketCost, 3, BigDecimal.ROUND_HALF_UP);
                                    BigDecimal dishPartPreferential = meanDishPreferential.divide(new BigDecimal(orderItem.getQuantity()), 3, BigDecimal.ROUND_HALF_UP);
                                    orderItem.setAllOrderDisCountSubtractPrice(dishPartPreferential);
                                    orderItem.setOnlyCost(orderItem.getOrderDishCost().subtract(dishPartPreferential));
                                    setMarketObjectList(orderItem, activitys, meanDishPreferential);
                                }
                            }
                        }
                        if (activitys.getDiscountType() == 1) {
                            BigDecimal price = marketCost.subtract(dishPreferentialSum);//参加完优惠活动后应该是这个价钱
                            tempOrderItem.setOnlyCost(tempOrderItem.getCost().add(price.subtract(dishPreferentialCashSum)));
                            tempOrderItem.setAllOrderDisCountSubtractPrice(tempOrderItem.getAllOrderDisCountSubtractPrice().add(dishPreferentialCashSum.subtract(price)));
                            setMarketObjectList(tempOrderItem, activitys, tempDishPreferentialSum);
                        }

                        order.getItemList().clear();
                        order.setItemList(assemblyOrderItemList(itemList));

                        allMoney[0] = marketCost.add(notJobMoney[0]).subtract(dishPreferentialSum);//计算完优惠后，再加上之前减去的不参与打折的金额,然后减去优惠的金额，返回最终要支付的总金额
                        callBack.onOk(allMoney[0].add(takeOutMoney[0]).setScale(2, BigDecimal.ROUND_HALF_UP), dishPreferentialSum, activitys.getDiscountName());
                        dialog.dismiss();
                    }
                } else {
                    MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("discount_is_null"));
                }
            }
        });

        active_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 组装list 将拆开的菜品重新组装起来
     * @param singleItemList
     * @return
     */
    private static List<Dish> assemblyList(List<Dish> singleItemList)
    {
        List<Dish> dishItem = new ArrayList<Dish>();
        for (Dish item : singleItemList) {
            Dish singleItem = ToolsUtils.cloneTo(item);
            BigDecimal itemCost = item.getCost();
            boolean isSame = false;
            boolean sameOption = true;//默认是相同的
            List<Option> options = item.getOptionList();
            List<Dish.Package> itemSubList = item.getSubItemList();
            for (Dish bean : dishItem) {
                // 判断是不是套餐，如果是套餐比较套餐项；
                // 普通菜品就比较定制项
                List<Dish.Package> beanSubList = bean.getSubItemList();
                if (beanSubList == null || beanSubList.isEmpty()) {
                    List<Option> list1 = bean.getOptionList();
                    if (options != null && list1 != null)
                        sameOption = equalOption(options, list1);
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost()) && item.getAllOrderDisCountSubtractPrice().equals(bean.getAllOrderDisCountSubtractPrice()) && item.getDishSubAllOrderDisCount().equals(bean.getDishSubAllOrderDisCount())  && sameOption) {
                        //这是相同项的
                        isSame = true;
                        bean.setQuantity(bean.getQuantity() + 1);
                        break;
                    }
                } else {
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost())) {
                        if (itemSubList != null && !itemSubList.isEmpty()) {
                            // 需要比较两个套餐的套餐项是否都一样
                            if (sameItemList(itemSubList, beanSubList)) {
                                //这是相同项的
                                isSame = true;
                                bean.setQuantity(bean.getQuantity() + 1);
                                bean.setTempQuantity(bean.getTempQuantity() + 1);
                                break;
                            }
                        }
                    }
                }
            }
            if (!isSame) {
                dishItem.add(singleItem);
            }
        }
        return dishItem;
    }

    /**
     * 拆分菜品列表
     * @param dishList
     * @return
     */
    private static List<OrderItem> breakUpList(List<OrderItem> dishList)
    {
        int itemIndex = 1;
        List<OrderItem> singleItemList = new ArrayList<OrderItem>();
        for (OrderItem item : dishList) {
            int count = item.getQuantity();
            if (count == 1) {
                item.setItemIndex(itemIndex);
                itemIndex++;
                singleItemList.add(item);
            } else {
                for (int i = 0; i < count; i++) {
                    OrderItem singleItem = ToolsUtils.cloneTo(item);
                    singleItem.setItemIndex(itemIndex);
                    singleItem.setQuantity(1);
                    itemIndex++;
                    singleItemList.add(singleItem);
                }
            }
        }
        return singleItemList;
    }

    /**
     * 组装订单中的菜品列表
     * @param singleItemList
     * @return
     */
    private static List<OrderItem> assemblyOrderItemList(List<OrderItem> singleItemList)
    {
        List<OrderItem> dishItem = new ArrayList<OrderItem>();
        for (OrderItem item : singleItemList) {
            OrderItem singleItem = ToolsUtils.cloneTo(item);
            BigDecimal itemCost = item.getCost();
            boolean isSame = false;
            boolean sameOption = true;//默认是相同的
            List<Option> options = item.getOptionList();
            List<Dish.Package> itemSubList = item.getSubItemList();
            for (OrderItem bean : dishItem) {
                // 判断是不是套餐，如果是套餐比较套餐项；
                // 普通菜品就比较定制项
                List<Dish.Package> beanSubList = bean.getSubItemList();
                if (beanSubList == null || beanSubList.isEmpty()) {
                    List<Option> list1 = bean.getOptionList();
                    if (options != null && list1 != null)
                        sameOption = equalOption(options, list1);
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost()) && sameOption) {
                        //这是相同项的
                        isSame = true;
                        bean.setQuantity(bean.getQuantity() + 1);
                        break;
                    }
                } else {
                    if (item.getDishId() == bean.getDishId() && item.getCost().equals(bean.getCost())) {
                        if (itemSubList != null && !itemSubList.isEmpty()) {
                            // 需要比较两个套餐的套餐项是否都一样
                            if (sameItemList(itemSubList, beanSubList)) {
                                //这是相同项的
                                isSame = true;
                                bean.setQuantity(bean.getQuantity() + 1);
                                break;
                            }
                        }
                    }
                }
            }
            if (!isSame) {
                dishItem.add(singleItem);
            }
        }
        return dishItem;
    }

    private static boolean sameItemList(List<Dish.Package> list1, List<Dish.Package> list2) {
        if (list1.size() != list2.size())
            return false;
        for (Dish.Package item1 : list1) {
            boolean exist = false;
            for (Dish.Package item2 : list2) {
                if (item1.getDishId() == item2.getDishId() && item1.getItemPrice() != null && item2.getItemPrice() != null && item1.getItemPrice().equals(item2.getItemPrice())) {
                    exist = true;
                }
            }
            if (!exist)
                return false;
        }

        return true;
    }

    /**
     * 手动设置营销方案优惠金额
     */
    public static void setActiveMoney(Context context, final BigDecimal allMoney, final DialogCall callBack) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_active_set, 4, LinearLayout.LayoutParams.WRAP_CONTENT);
        final CommonEditText active_money = (CommonEditText) dialog.findViewById(R.id.active_money);
        TextView cancle = (TextView) dialog.findViewById(R.id.cancle);
        TextView ok = (TextView) dialog.findViewById(R.id.ok);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callBack.onCancle("");
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String money = active_money.getText().toString();
                if (TextUtils.isEmpty(money)) {
                    MyApplication.getInstance().ShowToast("请输入优惠金额");
                    return;
                }
                if (money.length() == 1 && ".".equals(money)) {
                    MyApplication.getInstance().ShowToast("请输入正确金额");
                    return;
                }
                if (new BigDecimal(money).compareTo(allMoney) == 1) {
                    MyApplication.getInstance().ShowToast("优惠金额不能大于总金额");
                    return;
                }
                dialog.dismiss();
                callBack.onOk(new BigDecimal(money).setScale(2, BigDecimal.ROUND_DOWN));
            }
        });

    }

    /**
     * 备注
     *
     * @param context
     * @param mark
     * @param keyCallBack
     */
    public static void setMarkDialog(Context context, String mark, final KeyCallBack keyCallBack) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_remark, 4, 5, true);

        ComTextView dialog_close = (ComTextView) dialog.findViewById(R.id.dialog_close);
        ComTextView dialog_ok = (ComTextView) dialog.findViewById(R.id.dialog_ok);
        final EditText content = (EditText) dialog.findViewById(R.id.content);

        content.setTextSize(DensityUtils.px2sp(context, 22));
        content.setText(mark);
        content.setSelection(content.getText().length());

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyCallBack.onOk(content.getText().toString());
                dialog.dismiss();
            }
        });

    }

    /**
     * 选择小票打印机
     *
     * @param context
     * @param keyCallBack
     */
    public static void selectPrint(final Context context, final List<Printer> selectPrints, final KeyCallBack keyCallBack) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_print, 4, 5, true);
        ComTextView dialog_close = (ComTextView) dialog.findViewById(R.id.dialog_close);
        ComTextView dialog_ok = (ComTextView) dialog.findViewById(R.id.dialog_ok);
        final ListView print_list = (ListView) dialog.findViewById(R.id.print_list);
        final ComTextView none = (ComTextView) dialog.findViewById(R.id.none);
        final LinearLayout none_ll = (LinearLayout) dialog.findViewById(R.id.none_ll);
        final ProgressBar none_pro = (ProgressBar) dialog.findViewById(R.id.none_pro);

        final TickPrintAdapter adapter = new TickPrintAdapter(context);
        print_list.setAdapter(adapter);

        dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyCallBack.onOk(adapter.getSelectPrints());
                dialog.dismiss();
            }
        });

        try {

            List<Printer> printerList = StoreInfor.printerList;

            if (printerList == null || printerList.size() == 0) {
                StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
                storeBusinessService.listPrinters(new ResultCallback<List<Printer>>() {
                    @Override
                    public void onResult(List<Printer> result) {
                        if (result != null && result.size() > 0) {
                            none_ll.setVisibility(View.GONE);
                            print_list.setVisibility(View.VISIBLE);
                            adapter.setData(result);
                        } else {
                            none_pro.setVisibility(View.GONE);
                            none.setText(ToolsUtils.returnXMLStr("not_find_printer"));
                        }
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        if (!TextUtils.isEmpty(e.getMessage())) {
                            MyApplication.getInstance().ShowToast(e.getMessage());
                            none_pro.setVisibility(View.GONE);
                            none.setText(e.getMessage());
                        }
                    }
                });
            } else {
                none_ll.setVisibility(View.GONE);
                print_list.setVisibility(View.VISIBLE);
                adapter.setData(printerList);
            }

        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private static void setMarketObjectList(OrderItem dish, MarketingActivity activitys, BigDecimal marketDishCost) {
        if (dish.getMarketList() == null) {
            dish.marketList = new ArrayList<>();
        }
        else{
            ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.DISCOUNT);
//            for(MarketObject marketObject:dish.getMarketList())
//            {
//                if(marketObject.getMarketType() == MarketType.DISCOUNT)
//                {
//                    dish.getMarketList().remove(marketObject);
//                }
//            }
        }
        MarketObject marketObject = new MarketObject(activitys.getDiscountName(), marketDishCost, MarketType.DISCOUNT);
        dish.marketList.add(marketObject);
    }

    private static void setMarketObjectList(Dish dish, MarketingActivity activitys, BigDecimal marketDishCost) {
        if (dish.getMarketList() == null) {
            dish.marketList = new ArrayList<>();
        }
        else{
            ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.DISCOUNT);
//            for(MarketObject marketObject:dish.getMarketList())
//            {
//                if(marketObject.getMarketType() == MarketType.DISCOUNT)
//                {
//                    dish.getMarketList().remove(marketObject);
//                }
//            }
        }
        MarketObject marketObject = new MarketObject(activitys.getDiscountName(), marketDishCost, MarketType.DISCOUNT);
        dish.marketList.add(marketObject);
    }
}
