package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.TakeOut;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/6/17.
 */
public class TakeOutAdp<T> extends BaseAdapter {
    private int width;
    private int height;

    public TakeOutAdp(Context context) {
        super(context);
        int sc_width = MyApplication.getInstance().getScreenWidth();
        width = sc_width / 10 - 10;
        height = width / 4 + 20;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TakeOut takeOut = (TakeOut) getItem(position);
        final TextView textView = new TextView(context);

        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(width, height);
        textView.setPadding(5, 5, 5, 5);
        textView.setLayoutParams(layoutParams);
        textView.setText("¥"+takeOut.waiDai_cost+"/"+takeOut.takeOutStr);
        if (takeOut.waiDai_cost.compareTo(new BigDecimal("0")) == 0)
        {
            textView.setText("免打包费");
        }
        else
        {
            textView.setText("¥"+takeOut.waiDai_cost);
        }

        if(current_select == position)
        {
            ToolsUtils.writeUserOperationRecords("选择了("+textView.getText()+")打包费");
            textView.setBackgroundResource(R.drawable.border_green);
        }
        else
        {
            textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);
        }
//        //原价
//        if(position == 0)
//        {
//
//            textView.setText(discount.getDiscountStr()+"/￥"+new BigDecimal(dish.getDishCost().multiply(new BigDecimal(1+"")).intValue()));
//        }
//        //5折
//         if(position == 1)
//        {
//            textView.setText(discount.getDiscountStr()+"/￥"+new BigDecimal(dish.getDishCost().multiply(new BigDecimal(0.5+"")).intValue()));
//        }
//        //8折
//         if(position == 2)
//        {
//            textView.setText(discount.getDiscountStr()+"/￥"+new BigDecimal(dish.getDishCost().multiply(new BigDecimal(0.8+"")).intValue()));
//        }
        //
        //        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
        //            textView.setText(option.name);
        //        } else {
        //            textView.setText(option.name + " 加" + option.getPrice() + "元");
        //        }
        //        textView.setTextSize(16);
        //        textView.setId(option.getId());
        //        textView.setGravity(Gravity.CENTER);
        //        textView.setTextColor(0xff000000);
        //        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
        //                width, height);
        //        textView.setPadding(5, 5, 5, 5);
        //        textView.setLayoutParams(layoutParams);
        //        textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);
        //
        //        if (option.required) {
        //            option.setSelect(true);
        //            Cart.addOption(option);
        //            textView.setBackgroundResource(R.drawable.border_green);
        //        } else {
        //            option.setSelect(false);
        ////            Cart.deleteOption(option);
        //            textView.setBackgroundResource(R.drawable.border_gray1_xb);
        //        }
        //        if(isSelect)
        //        {
        //            if(optionList != null && optionList.size() >0)
        //            {
        //                for (Option optionSelect :optionList)
        //                {
        //                    if(optionSelect.getId() == option.getId())
        //                    {
        //                        option.setSelect(true);
        //                        Cart.addOption(option);
        //                        textView.setBackgroundResource(R.drawable.border_green);
        //                    }
        //                }
        //            }
        //        }
        //
        //        textView.setOnClickListener(new View.OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                isSelect = false;
        //                if (!option.required) {
        //                    if (option.getSelect()) {
        //                        option.setSelect(false);
        //                        Cart.deleteOption(option);
        //                        textView.setBackgroundResource(R.drawable.border_gray1_xb);
        //                    } else {
        //                        option.setSelect(true);
        //                        Cart.addOption(option);
        //                        textView.setBackgroundResource(R.drawable.border_green);
        //                    }
        //                }
        //            }
        //        });

        return textView;
    }
    private Dish dish;
    private int current_select = 0;

    public void setDish(Dish dish) {
        if(dish!= null)
        {
            this.dish = dish;
        }
    }

    public void setCurrent_select(int current_select) {
        this.current_select = current_select;
    }

    //    private ArrayList<Discount> discountsList = new ArrayList<>();
    //    public void setDisCountList(ArrayList<Discount> discountsList)
    //    {
    //        if(discountsList != null && discountsList.size() >0)
    //        {
    //            this.discountsList = discountsList;
    //        }
    //    }

}
