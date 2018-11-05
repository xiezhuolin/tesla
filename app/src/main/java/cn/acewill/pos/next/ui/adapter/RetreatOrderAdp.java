package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.order.OrderItem;

/**
 * Created by DHH on 2016/6/17.
 */
public class RetreatOrderAdp<T> extends BaseAdapter{
    private List<OrderItem> orderItemList;
    public RetreatOrderAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final OrderItem item = (OrderItem)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_retreat_order, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.img_select = (ImageView) convertView.findViewById(R.id.img_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishName.setText((position+1)+"."+item.getDishName());

        holder.tv_count.setText(item.quantity+"/份");

        String money = String.format("%.2f ", item.getCost());
        holder.tv_money.setText("¥ " + money);

        if(item.subItemList != null && item.subItemList.size() >0 || item.optionList != null && item.optionList.size() >0)
        {
            int count = 1;
            holder.tv_info.setVisibility(View.VISIBLE);
            StringBuffer sb = new StringBuffer();
            if(item.subItemList != null && item.subItemList.size() >0)
            {
                for (Dish.Package packge: item.subItemList)
                {
                    sb.append(count+"."+packge.getDishName()+"\n");
                    count++;
                }
            }
            if(item.optionList != null && item.optionList.size() >0)
            {
                for(Option option:item.optionList)
                {
                    String name = "";
                    double price = option.getPrice().doubleValue();
                    if(price >0)
                    {
                        name = option.name + "(" + option.getPrice() + "元)";
                    }
                    else
                    {
                        name = option.getName();
                    }
                    sb.append(count+"."+name+"\n");
                    count++;
                }
            }
            holder.tv_info.setText(sb.toString());
        }
        else
        {
            holder.tv_info.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setOrderItemList(List<OrderItem> checkDishCount){
        if (checkDishCount != null && checkDishCount.size() > 0){
            if(this.orderItemList != null && this.orderItemList.size() >0)
            {
                this.orderItemList.clear();
            }
            this.orderItemList = checkDishCount;
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_money;
        TextView tv_info;
        Button btn_minus;
        Button btn_add;
        ImageView img_select;
    }

}
