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
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Specification;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/6/17.
 */
public class SpecificationsAdp<T> extends BaseAdapter {
    private int width;
    private int height;

    public SpecificationsAdp(Context context) {
        super(context);
        int sc_width = MyApplication.getInstance().getScreenWidth();
        width = sc_width / 7 - 10;
        height = width / 4 + 20;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Specification specification = (Specification) getItem(position);
        final TextView textView = new TextView(context);

        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(width, height);
        textView.setPadding(5, 5, 5, 5);
        textView.setLayoutParams(layoutParams);
        BigDecimal discountPrice = new BigDecimal(specification.getPrice()+"").setScale(2,BigDecimal.ROUND_DOWN);
        textView.setText(discountPrice+"￥/"+specification.getName());

        if(current_select == position)
        {
            ToolsUtils.writeUserOperationRecords("选择了("+discountPrice+"￥/"+specification.getName()+")菜品规格");
            textView.setBackgroundResource(R.drawable.border_green);
        }
        else
        {
            textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);
        }
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

}
