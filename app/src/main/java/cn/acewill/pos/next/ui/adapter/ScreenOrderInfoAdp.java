package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Dish;

/**
 * 订单详情
 * Created by aqw on 2016/9/7.
 */
public class ScreenOrderInfoAdp extends BaseAdapter {
    public ScreenOrderInfoAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final Dish orderItem = (Dish) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_screen_order_info, null);
            holder.dish_name = (TextView)convertView.findViewById(R.id.dish_name);
            holder.dish_count = (TextView)convertView.findViewById(R.id.dish_count);
            holder.dish_price = (TextView)convertView.findViewById(R.id.dish_price);
            holder.dish_sub = (TextView)convertView.findViewById(R.id.dish_sub);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        BigDecimal price = (orderItem.getDishRealCost()).multiply(new BigDecimal(orderItem.quantity)).setScale(3, BigDecimal.ROUND_DOWN);
        String money = String.format("%.2f ", price);
        holder.dish_price.setText("¥ " + money);
        holder.dish_name.setText(orderItem.getDishName());
        holder.dish_count.setText(orderItem.getQuantity()+"");

        String itemStr = orderItem.getSubItemSth();
        if(!TextUtils.isEmpty(itemStr))
        {
            holder.dish_sub.setVisibility(View.VISIBLE);
            holder.dish_sub.setText(itemStr);
        }
        else{
            holder.dish_sub.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView dish_name;
        TextView dish_count;
        TextView dish_price;
        TextView dish_sub;
    }
}
