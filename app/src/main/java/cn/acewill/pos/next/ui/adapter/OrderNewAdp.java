package cn.acewill.pos.next.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.TakeOut;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishDiscount;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ScrolGridView;
import cn.acewill.pos.next.widget.ScrolListView;

import static cn.acewill.pos.R.id.tv_count;

/**
 * Created by DHH on 2016/6/17.
 */
public class OrderNewAdp extends BaseAdapter {
    public final int INITID = -1;
    public List<DishCount> checkDishCount;
    private Context context;
    private Resources res;
    public int selectItemPosition = INITID;//选择的item下标

    public OrderNewAdp(Context context) {
        super(context);
        this.context = context;
        res = context.getResources();
    }


//    @Override
//    public int getCount() {
//        return dataList != null ? dataList.size() : 0;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return dataList != null ? dataList.get(position) : null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//    public void setData(List<Dish> dataList) {
//        this.dataList = dataList;
//        notifyDataSetChanged();
//    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final int selectIndex = position;
        final Dish dish = (Dish)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_order_info_new, null);
            holder.lin_conver = (RelativeLayout) convertView.findViewById(R.id.lin_conver);
            holder.lin_count = (LinearLayout) convertView.findViewById(R.id.lin_count);
            holder.lin_money = (LinearLayout) convertView.findViewById(R.id.lin_money);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(tv_count);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_disCount = (TextView) convertView.findViewById(R.id.tv_disCount);
            holder.tv_option = (TextView) convertView.findViewById(R.id.tv_option);
            holder.lv_dish_package_item = (ScrolListView) convertView.findViewById(R.id.lv_dish_package_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BigDecimal price = (dish.getDishRealCost()).multiply(new BigDecimal(dish.quantity)).setScale(3, BigDecimal.ROUND_DOWN);
        String money = String.format("%.2f ", price);
        holder.tv_money.setText("¥ " + money);

        holder.tv_count.setText(dish.quantity + "");
        //        if (dish.quantity > 1) {
        //            //            holder.lin_count.setVisibility(View.VISIBLE);
        //        } else {
        ////            holder.lin_count.setVisibility(View.INVISIBLE);
        //        }
        //        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
        //        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //        holder.tv_money.setPadding(10,15,10,15);
        //        holder.tv_money.setLayoutParams(params);

        if (checkDishCount != null && checkDishCount.size() > 0) {
            DishCount dishCount = checkDishCount.get(position);
            if (dishCount != null) {
                if (dishCount.getDishid() == dish.getDishId()) {
                    convertView.setBackgroundColor(res.getColor(R.color.bbutton_danger));
                }
            }
        }
        //有套餐项
        if (dish.subItemList != null && dish.subItemList.size() > 0) {
            holder.lv_dish_package_item.setVisibility(View.VISIBLE);
            DishPackageItemAdp dishPackageItemAdp = new DishPackageItemAdp(context);
            dishPackageItemAdp.setDish(dish);
            dishPackageItemAdp.setData(dish.subItemList);
            holder.lv_dish_package_item.setAdapter(dishPackageItemAdp);
        } else {
            holder.lv_dish_package_item.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams mLayoutParams;
        if (dish.optionList != null && dish.optionList.size() > 0 || dish.getMarketList() != null && dish.getMarketList().size() > 0 || dish.getTempMarketList() != null && dish.getTempMarketList().size() > 0) {
            mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            mLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        holder.tv_dishName.setLayoutParams(mLayoutParams);

        holder.tv_dishName.setText(dish.getDishName());
        if (dish.optionList != null && dish.optionList.size() > 0) {
            holder.tv_option.setVisibility(View.VISIBLE);
            holder.tv_option.setText(optionTextShow(dish));
        } else {
            holder.tv_option.setVisibility(View.GONE);
        }
        if (dish.getMarketList() != null && dish.getMarketList().size() > 0 || dish.getTempMarketList() != null && dish.getTempMarketList().size() > 0) {
            holder.tv_disCount.setVisibility(View.VISIBLE);
            holder.tv_disCount.setText(ToolsUtils.getDisCountStr(dish.getMarketList(), dish.getTempMarketList()));
        } else {
            holder.tv_disCount.setVisibility(View.GONE);
        }

        if (position != selectItemPosition) {
            holder.lin_conver.setBackgroundColor(res.getColor(R.color.white));
            holder.lv_dish_package_item.setBackgroundColor(res.getColor(R.color.white));
            holder.tv_count.setBackgroundResource(R.drawable.border_black);
            holder.tv_count.setTextColor(res.getColor(R.color.black));
            holder.tv_dishName.setTextColor(res.getColor(R.color.black));
            holder.tv_disCount.setTextColor(res.getColor(R.color.login_gray));
            holder.tv_option.setTextColor(res.getColor(R.color.login_gray));
            holder.lin_money.setBackgroundColor(res.getColor(R.color.bbutton_info_dish_item));
            holder.tv_money.setTextColor(res.getColor(R.color.black));
        } else {
            holder.lin_conver.setBackgroundColor(res.getColor(R.color.main_order_item_bg));
            holder.lv_dish_package_item.setBackgroundColor(res.getColor(R.color.main_order_item_bg));
            holder.tv_count.setBackgroundResource(R.drawable.border_white);
            holder.tv_count.setTextColor(res.getColor(R.color.white));
            holder.tv_dishName.setTextColor(res.getColor(R.color.white));
            holder.tv_disCount.setTextColor(res.getColor(R.color.white));
            holder.tv_option.setTextColor(res.getColor(R.color.white));
            holder.lin_money.setBackgroundColor(res.getColor(R.color.main_order_item_money_bg));
            holder.tv_money.setTextColor(res.getColor(R.color.white));
        }

        return convertView;
    }


    public void setCheckDishCount(List<DishCount> checkDishCount) {
        if (checkDishCount != null) {
            if (this.checkDishCount != null && this.checkDishCount.size() > 0) {
                this.checkDishCount.clear();
            }
            this.checkDishCount = checkDishCount;
            this.notifyDataSetChanged();
        }
    }

    private String optionTextShow(Dish mDishModel) {
        StringBuffer sb = new StringBuffer();
        String space = "";
        int size = mDishModel.optionList.size();
        for (int i = 0; i < size; i++) {
            if (i != size - 1) {
                space = "\n";
            } else {
                space = "";
            }
            Option option = mDishModel.optionList.get(i);
            if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                sb.append("+ " + option.name + space);
            } else {
                sb.append("+ " + option.name + " (" + option.getPrice() + ")" + space);
            }
        }
        return sb.toString();
    }

    public void dishInfoDialog(final Dish dish, final int position) {
        final Dialog dialog = DialogUtil.createDialog(context, R.layout.dialog_dish_info, 8, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView dialog_ok = (TextView) dialog.findViewById(R.id.dialog_ok);
        TextView dialog_cancle = (TextView) dialog.findViewById(R.id.dialog_cancle);
        TextView retreat_title = (TextView) dialog.findViewById(R.id.retreat_title);
        final ScrolListView lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);
        final ScrolGridView gv_discount = (ScrolGridView) dialog.findViewById(R.id.gv_discount);
        final ScrolGridView gv_takeOut = (ScrolGridView) dialog.findViewById(R.id.gv_takeOut);
        final CommonEditText dish_count = (CommonEditText) dialog.findViewById(R.id.dish_count);
        final RelativeLayout rel_sku = (RelativeLayout) dialog.findViewById(R.id.rel_sku);
        final RelativeLayout rel_takeOut = (RelativeLayout) dialog.findViewById(R.id.rel_takeOut);
        final RelativeLayout rel_discount = (RelativeLayout) dialog.findViewById(R.id.rel_discount);
        ImageView dish_reduce = (ImageView) dialog.findViewById(R.id.dish_reduce);
        ImageView dish_plus = (ImageView) dialog.findViewById(R.id.dish_plus);

        retreat_title.setText(dish.getDishName());
        dish_count.setText(dish.quantity + "");
        dish_count.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dish_count.setText("");
                dish_count.setSelectAllOnFocus(true);
                return false;
            }
        });

        final int[] num = {dish.quantity};
        final int[] current_selectDish = {dish.current_select};
        final int[] current_selectTakeOut = {dish.current_selectTakeOut};
        if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
            rel_discount.setVisibility(View.VISIBLE);
            final DiscountAdp discountAdp = new DiscountAdp(context);
            ArrayList<DishDiscount> newDishDiscountList = new ArrayList<>();
            if (newDishDiscountList != null && newDishDiscountList.size() > 0) {
                newDishDiscountList.clear();
            }
            ArrayList<DishDiscount> oldDishDiscount = dish.getDishDiscount();
            DishDiscount tempDiscount = oldDishDiscount.get(0);

            if (dish.isSelectDisCount == false) {
                String price = (dish.getPrice().setScale(2, BigDecimal.ROUND_DOWN)).toString();
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
            dish.isSelectDisCount = true;
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

        if (dish.optionCategoryList != null && dish.optionCategoryList.size() > 0) {
            rel_sku.setVisibility(View.VISIBLE);
            OptionCategoryAdp optionCategoryAdp = new OptionCategoryAdp(context, dish);
            optionCategoryAdp.setOptionList(dish.optionList);
            optionCategoryAdp.setData(dish.optionCategoryList);
            lv_option.setAdapter(optionCategoryAdp);
        }

        dish_reduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num[0] > 1) {
                    num[0]--;
                    dish_count.setText(num[0] + "");
                }

            }
        });
        dish_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num[0]++;
                dish_count.setText(num[0] + "");
            }
        });

        dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String acount = dish_count.getText().toString().trim();
                if (TextUtils.isEmpty(acount)) {
                    MyApplication.getInstance().ShowToast("选择份数不能为空!");
                } else {
                    int count = Integer.valueOf(acount);
                    if (count <= 0) {
                        MyApplication.getInstance().ShowToast("选择份数不能为0");
                    } else {
                        //                //遍历选中的定制项
                        if (!DishOptionController.checkSelectOption(dish, null)) {
                            return;
                        }
                        dish.optionList = DishOptionController.getDishOption(dish, null);
                        Cart.getInstance().selectCount(position, count, current_selectTakeOut[0], current_selectDish[0], -10);
                        dialog.dismiss();
                    }
                }
            }
        });

    }


    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_money;
        TextView tv_package;
        TextView tv_option;
        TextView tv_disCount;
        RelativeLayout lin_conver;
        LinearLayout lin_count;
        LinearLayout lin_money;
        ScrolListView lv_dish_package_item;
    }

    public int getSelectItemPosition() {
        return selectItemPosition;
    }

    public void setSelectItemPosition(int selectItemPosition) {
        this.selectItemPosition = selectItemPosition;
        this.notifyDataSetChanged();
    }

}
