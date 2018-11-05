package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.order.OrderItem;

/**
 * Created by DHH on 2016/6/17.
 */
public class NetOrderItemAdp<T> extends BaseAdapter{
    public NetOrderItemAdp(Context context) {
        super(context);
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_net_order_info, null);
            holder.dishName = (TextView) convertView.findViewById(R.id.dishName);
            holder.dishCount = (TextView) convertView.findViewById(R.id.dishCount);
            holder.dishPrice = (TextView) convertView.findViewById(R.id.dishPrice);
            holder.view_line = (View) convertView.findViewById(R.id.view_line);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final OrderItem item = (OrderItem)dataList.get(position);
        if(item != null)
        {
            // 菜名
            String dishName = (position + 1) + "." + item.getDishName();
            holder.dishName.setText(dishName);
            holder.dishCount.setText("X" + item.getQuantity());
            holder.dishPrice.setText("￥" + item.getPrice().toString());
        }
        if (position == dataList.size() - 1) {
            holder.view_line.setVisibility(View.GONE);
        } else {
            holder.view_line.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
    class ViewHolder {
        TextView dishName;
        TextView dishCount;
        TextView dishPrice;
        View view_line;
    }
}
