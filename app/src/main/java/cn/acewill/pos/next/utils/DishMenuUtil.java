package cn.acewill.pos.next.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.common.MarketDataController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.interfices.KeyCallBack;
import cn.acewill.pos.next.model.TakeOut;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishDiscount;
import cn.acewill.pos.next.model.dish.Specification;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.adapter.DiscountAdp;
import cn.acewill.pos.next.ui.adapter.OptionCategoryAdp;
import cn.acewill.pos.next.ui.adapter.OptionCategoryPackageAdp;
import cn.acewill.pos.next.ui.adapter.SpecificationsAdp;
import cn.acewill.pos.next.ui.adapter.TakeOutAdp;
import cn.acewill.pos.next.widget.ComTextView;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ScrolGridView;
import cn.acewill.pos.next.widget.ScrolListView;

import static cn.acewill.pos.R.id.money;

/**
 * Created by DHH on 2016/12/20.
 */

public class DishMenuUtil {

    /**
     * 设置菜品份数
     *
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishCountDialog(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_menu_count, 6, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final CommonEditText dish_count = (CommonEditText) dialog.findViewById(R.id.dish_count);
        ImageView dish_reduce = (ImageView) dialog.findViewById(R.id.dish_reduce);
        ImageView dish_plus = (ImageView) dialog.findViewById(R.id.dish_plus);

        retreat_title.setText(dish.getDishName());
        dish_count.setText(dish.quantity + "");
        dish_count.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToolsUtils.writeUserOperationRecords("选择了菜品("+dish.getDishName()+")份数框");
                dish_count.setText("");
                dish_count.setSelectAllOnFocus(true);
                return false;
            }
        });

        final int[] dishQuantity = {dish.quantity};
        dish_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dishQuantity[0] > 1) {
                    dishQuantity[0]--;
                    dish_count.setText(dishQuantity[0] + "");
                    ToolsUtils.writeUserOperationRecords("菜品("+dish.getDishName()+")退菜份数-1");
                }

            }
        });
        dish_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品("+dish.getDishName()+")退菜份数+1");
                dishQuantity[0]++;
                dish_count.setText(dishQuantity[0] + "");
            }
        });
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("按下取消菜品("+dish.getDishName()+")份数按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("按下确定菜品("+dish.getDishName()+")份数按钮");
                String acount = dish_count.getText().toString().trim();
                if (TextUtils.isEmpty(acount)) {
                    MyApplication.getInstance().ShowToast("选择份数不能为空!");
                } else {
                    int count = Integer.valueOf(acount);
                    if (count <= 0) {
                        MyApplication.getInstance().ShowToast("选择份数不能为0");
                    } else {
                        Cart.getInstance().selectCount(position, count, -10, -10,-10);
                        dialog.dismiss();
                    }
                }
            }
        });
        return dialog;
    }

    /**
     * 设置菜品SKU
     *
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishSkuDialog(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_sku, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolListView lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);

        retreat_title.setText(dish.getDishName());
        if (dish.optionCategoryList != null && dish.optionCategoryList.size() > 0) {
            OptionCategoryAdp optionCategoryAdp = new OptionCategoryAdp(context,dish);
            optionCategoryAdp.setOptionList(dish.optionList);
            optionCategoryAdp.setData(dish.optionCategoryList);
            lv_option.setAdapter(optionCategoryAdp);
        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品SKU取消按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品SKU确定按钮");
                //遍历选中的定制项
                if(!DishOptionController.checkSelectOption(dish,null))
                {
                    return;
                }
                dish.optionList = DishOptionController.getDishOption(dish,null);
                Cart.getInstance().selectCount(position, dish.quantity, -10, -10,-10);
                dialog.dismiss();

            }
        });
        return dialog;
    }


    public static Dialog setDishPackageSkuDialog(Context context, final Dish dish,final Dish.Package packagItem,final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_sku, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolListView lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);

        retreat_title.setText(packagItem.getDishName());
        if (packagItem.optionCategoryList != null && packagItem.optionCategoryList.size() > 0) {
            OptionCategoryPackageAdp optionCategoryAdp = new OptionCategoryPackageAdp(context,dish,packagItem);
            optionCategoryAdp.setOptionList(packagItem.optionList);
            optionCategoryAdp.setData(packagItem.optionCategoryList);
            lv_option.setAdapter(optionCategoryAdp);
        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品SKU取消按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品SKU确定按钮");
                //遍历选中的定制项
                if(!DishOptionController.checkSelectOption(dish,packagItem))
                {
                    return;
                }
                packagItem.optionList = DishOptionController.getDishOption(dish,packagItem);
                Cart.getInstance().selectDishPackage(position,packagItem);
                dialog.dismiss();
            }
        });
        return dialog;
    }


    /**
     * 设置菜品折扣
     *
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishDisCountDialog(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_discount, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolGridView gv_discount = (ScrolGridView) dialog.findViewById(R.id.gv_discount);

        retreat_title.setText(dish.getDishName());
        final int[] current_selectDish = {dish.current_select};
        List<DishDiscount> dishDisCountList = MarketDataController.getDishDiscount(dish, StoreInfor.marketingActivities);
        dish.setDishDiscount(dishDisCountList);
        if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
            final DiscountAdp discountAdp = new DiscountAdp(context);
            ArrayList<DishDiscount> newDishDiscountList = new ArrayList<>();
            if (newDishDiscountList != null && newDishDiscountList.size() > 0) {
                newDishDiscountList.clear();
            }
            ArrayList<DishDiscount> oldDishDiscount = dish.getDishDiscount();
            DishDiscount tempDiscount = oldDishDiscount.get(0);

            if (dish.isSelectDisCount == false) {
                String price = (dish.getDishPrice().setScale(2, BigDecimal.ROUND_DOWN)).toString();
                DishDiscount dishDiscount = new DishDiscount(tempDiscount, price);
                newDishDiscountList.add(dishDiscount);
                for (DishDiscount count : oldDishDiscount) {
                    newDishDiscountList.add(count);
                }
            } else {
                newDishDiscountList = oldDishDiscount;
            }
            discountAdp.setCurrent_select(dish.current_select);
            discountAdp.setData(newDishDiscountList);
            gv_discount.setAdapter(discountAdp);
            dish.dishDiscount = newDishDiscountList;
//            dish.isSelectDisCount = true;
            gv_discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DishDiscount discount = (DishDiscount) discountAdp.getItem(position);
                    if (discount != null) {
                        if(discount.isEnable())
                        {
                            current_selectDish[0] = position;
                            discountAdp.setCurrent_select(position);
                            discountAdp.notifyDataSetChanged();
                        }
                        else{
                            MyApplication.getInstance().ShowToast("减免金额大于菜品金额!");
                        }
                    }
                }
            });
        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品折扣取消按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品折扣确定按钮");
                Cart.getInstance().selectCount(position, dish.quantity, -10, current_selectDish[0],-10);
                dialog.dismiss();
            }
        });
        return dialog;
    }

    /**
     * 设置菜品打包费用
     *
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishTakeOutDialog(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_take_out, 5, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        RelativeLayout rel_takeOut = (RelativeLayout) dialog.findViewById(R.id.rel_takeOut);
        final ScrolGridView gv_takeOut = (ScrolGridView) dialog.findViewById(R.id.gv_takeOut);

        retreat_title.setText(dish.getDishName());
        final int[] current_selectTakeOut = {dish.current_selectTakeOut};

        if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType())) {
            rel_takeOut.setVisibility(View.VISIBLE);
            final TakeOutAdp takeOutAdp = new TakeOutAdp(context);
            List<TakeOut> takeOutList = new ArrayList<>();
            BigDecimal collectPrice = dish.waiDai_costTemp.setScale(2, BigDecimal.ROUND_DOWN);
            BigDecimal freePrice = new BigDecimal("0.00");
            TakeOut takeOut1 = new TakeOut(collectPrice, collectPrice + "");
            TakeOut takeOut2 = new TakeOut(freePrice, freePrice + "");
            takeOutList.add(takeOut1);
            takeOutList.add(takeOut2);
            takeOutAdp.setCurrent_select(dish.current_selectTakeOut);
            takeOutAdp.setData(takeOutList);
            gv_takeOut.setAdapter(takeOutAdp);
            gv_takeOut.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TakeOut takeOut = (TakeOut) takeOutAdp.getItem(position);
                    if (takeOut != null) {
                        current_selectTakeOut[0] = position;
                        takeOutAdp.setCurrent_select(position);
                        takeOutAdp.notifyDataSetChanged();
                    }
                }
            });

        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品打包费用取消按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品打包费用确定按钮");
                Cart.getInstance().selectCount(position, dish.quantity, current_selectTakeOut[0], -10,-10);
                dialog.dismiss();
            }
        });
        return dialog;
    }


    /**
     * 选择菜品规格
     * @param context
     * @param dish
     * @param position
     * @return
     */
    public static Dialog setDishSpecificationsDialog(Context context, final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_discount, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        TextView dish_price = (TextView) dialog.findViewById(R.id.dish_price);
        final ScrolGridView gv_discount = (ScrolGridView) dialog.findViewById(R.id.gv_discount);
        dish_price.setText("菜品规格:");
        final int[] current_selectSp = {dish.current_selectSpecifications};
        retreat_title.setText(dish.getDishName());
        final SpecificationsAdp specificationsAdp = new SpecificationsAdp(context);
        specificationsAdp.setCurrent_select(current_selectSp[0]);
        specificationsAdp.setData(dish.getSpecificationList());
        gv_discount.setAdapter(specificationsAdp);
        gv_discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Specification specification = (Specification) specificationsAdp.getItem(position);
                if (specification != null) {
                    current_selectSp[0] = position;
                    specificationsAdp.setCurrent_select(position);
                    specificationsAdp.notifyDataSetChanged();
                }
            }
        });
        //        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品规格取消按钮");
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品规格确定按钮");
                Cart.getInstance().selectCount(position, dish.quantity,-10,-10,current_selectSp[0]);
                dialog.dismiss();
            }
        });
        return dialog;
    }


    /**
     * 设置菜品退菜
     *
     * @param context
     * @param orderItem
     * @return
     */
    public static Dialog setRefundDishDialog(Context context, final OrderItem orderItem, final DialogEtCallback dialogEtCallback) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_menu_count, 6, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        TextView dish_name = (TextView) dialog.findViewById(R.id.dish_name);
        final CommonEditText dish_count = (CommonEditText) dialog.findViewById(R.id.dish_count);
        ImageView dish_reduce = (ImageView) dialog.findViewById(R.id.dish_reduce);
        ImageView dish_plus = (ImageView) dialog.findViewById(R.id.dish_plus);
        final int[] dishMaxRefund = {orderItem.quantity};//最大退菜份数
        final int[] initRefund = {1};//初始退菜份数
        retreat_title.setText(orderItem.getDishName());
        dish_name.setText("退菜份数:");
        dish_count.setText(initRefund[0] + "");
        dish_count.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ToolsUtils.writeUserOperationRecords("菜品("+orderItem.getDishName()+")退菜份数选择框");
                dish_count.setText("");
                dish_count.setSelectAllOnFocus(true);
                return false;
            }
        });

        dish_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initRefund[0] > 1) {
                    initRefund[0]--;
                    dish_count.setText(initRefund[0] + "");
                    ToolsUtils.writeUserOperationRecords("菜品("+orderItem.getDishName()+")退菜份数-1");
                }

            }
        });
        dish_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initRefund[0] + 1 > dishMaxRefund[0]) {
                    MyApplication.getInstance().ShowToast("超出可选份额!");
                } else {
                    initRefund[0]++;
                    dish_count.setText(initRefund[0] + "");
                    ToolsUtils.writeUserOperationRecords("菜品("+orderItem.getDishName()+")退菜份数+1");
                }
            }
        });
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品退菜取消");
                dialogEtCallback.onCancle();
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("菜品退菜确定");
                String acount = dish_count.getText().toString().trim();
                if (TextUtils.isEmpty(acount)) {
                    MyApplication.getInstance().ShowToast("选择份数不能为空!");
                } else {
                    int count = Integer.valueOf(acount);
                    if (count <= 0) {
                        MyApplication.getInstance().ShowToast("选择份数不能为0");
                    } else {
                        dialogEtCallback.onConfirm(acount);
                        dialog.dismiss();
                    }
                }
            }
        });
        return dialog;
    }


    public static Dialog setOrderItemDisCountDialog(Context context, final OrderItem orderItem, final DialogTCallback callback) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_discount, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolGridView gv_discount = (ScrolGridView) dialog.findViewById(R.id.gv_discount);

        retreat_title.setText(orderItem.getDishName());
        final int[] current_selectDish = {orderItem.current_select};
        if (orderItem.dishDiscount != null && orderItem.dishDiscount.size() > 0) {
            final DiscountAdp discountAdp = new DiscountAdp(context);
            ArrayList<DishDiscount> newDishDiscountList = new ArrayList<>();
            if (newDishDiscountList != null && newDishDiscountList.size() > 0) {
                newDishDiscountList.clear();
            }
            ArrayList<DishDiscount> oldDishDiscount = orderItem.getDishDiscount();
            DishDiscount tempDiscount = oldDishDiscount.get(0);

            if (orderItem.isSelectDisCount == false) {
                String price = (orderItem.getPrice().setScale(2, BigDecimal.ROUND_DOWN)).toString();
                DishDiscount dishDiscount = new DishDiscount(tempDiscount, price);
                newDishDiscountList.add(dishDiscount);
                for (DishDiscount count : oldDishDiscount) {
                    newDishDiscountList.add(count);
                }
            } else {
                newDishDiscountList = oldDishDiscount;
            }
            discountAdp.setCurrent_select(orderItem.current_select);
            discountAdp.setData(newDishDiscountList);
            gv_discount.setAdapter(discountAdp);
            orderItem.dishDiscount = newDishDiscountList;
            orderItem.isSelectDisCount = true;
            gv_discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DishDiscount discount = (DishDiscount) discountAdp.getItem(position);
                    if (discount != null) {
                        current_selectDish[0] = position;
                        discountAdp.setCurrent_select(position);
                        discountAdp.notifyDataSetChanged();
                    }
                }
            });
        }
        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("下单后菜品折扣取消");
                dialog.dismiss();
                callback.onCancle();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("下单后菜品折扣确定");
                int current = current_selectDish[0];
                if (current > 0) {
                    if (orderItem.dishDiscount != null && orderItem.dishDiscount.size() > 0) {
                        BigDecimal cost = orderItem.getPrice();
                        if (current == orderItem.dishDiscount.size()) {
                            orderItem.current_select = current;
                            orderItem.cost = cost;
                            orderItem.setTempPrice(cost);
                        } else {
                            orderItem.current_select = current;
                            BigDecimal discountCost = new BigDecimal(orderItem.dishDiscount.get(current).discountPrice);
                            orderItem.cost = discountCost;
                            orderItem.setTempPrice(discountCost);
                        }
                    }
                }
                callback.onConfirm(orderItem);
                dialog.dismiss();
            }
        });
        return dialog;
    }


    public static Dialog setDishCount(final Context context, String title, final KeyCallBack callBack) {

        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_set_dishcount_key_number, 6, LinearLayout.LayoutParams.WRAP_CONTENT);

        ComTextView key_ok = (ComTextView) dialog.findViewById(R.id.key_ok);
        ComTextView key_title = (ComTextView) dialog.findViewById(R.id.key_title);
        ComTextView key_one = (ComTextView) dialog.findViewById(R.id.key_one);
        ComTextView key_two = (ComTextView) dialog.findViewById(R.id.key_two);
        ComTextView key_three = (ComTextView) dialog.findViewById(R.id.key_three);
        ComTextView key_four = (ComTextView) dialog.findViewById(R.id.key_four);
        ComTextView key_five = (ComTextView) dialog.findViewById(R.id.key_five);
        ComTextView key_six = (ComTextView) dialog.findViewById(R.id.key_six);
        ComTextView key_seven = (ComTextView) dialog.findViewById(R.id.key_seven);
        ComTextView key_eight = (ComTextView) dialog.findViewById(R.id.key_eight);
        ComTextView key_nine = (ComTextView) dialog.findViewById(R.id.key_nine);
        ComTextView key_delete = (ComTextView) dialog.findViewById(R.id.key_delete);
        ComTextView key_zero = (ComTextView) dialog.findViewById(R.id.key_zero);
        ComTextView key_point = (ComTextView) dialog.findViewById(R.id.key_point);

        key_title.setText(TextUtils.isEmpty(title) ? "数量" : title);

        class OnClickLisener implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.key_one:
                        printValue(Keys.KEY1);
                        break;
                    case R.id.key_two:
                        printValue(Keys.KEY2);
                        break;
                    case R.id.key_three:
                        printValue(Keys.KEY3);
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
                    case R.id.key_seven:
                        printValue(Keys.KEY7);
                        break;
                    case R.id.key_eight:
                        printValue(Keys.KEY8);
                        break;
                    case R.id.key_nine:
                        printValue(Keys.KEY9);
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
                }

                if (keys.getValue() == 12) {
                    dialog.dismiss();
                    callBack.onOk(print_money);
                }

            }
        }


        OnClickLisener onClickLisener = new OnClickLisener();

        key_one.setOnClickListener(onClickLisener);
        key_two.setOnClickListener(onClickLisener);
        key_three.setOnClickListener(onClickLisener);
        key_four.setOnClickListener(onClickLisener);
        key_five.setOnClickListener(onClickLisener);
        key_six.setOnClickListener(onClickLisener);
        key_seven.setOnClickListener(onClickLisener);
        key_eight.setOnClickListener(onClickLisener);
        key_nine.setOnClickListener(onClickLisener);
        key_delete.setOnClickListener(onClickLisener);
        key_zero.setOnClickListener(onClickLisener);
        key_point.setOnClickListener(onClickLisener);
        key_ok.setOnClickListener(onClickLisener);

        return dialog;
    }

}
