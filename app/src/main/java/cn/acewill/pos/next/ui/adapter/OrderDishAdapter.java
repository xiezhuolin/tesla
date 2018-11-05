package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.CookingMethod;
import cn.acewill.pos.next.model.dish.Flavor;
import cn.acewill.pos.next.model.order.OrderItem;

/**
 * Created by aqw on 2016/8/18.
 */
public class OrderDishAdapter extends BaseAdapter {

    public OrderDishAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final OrderItem orderItem = (OrderItem) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_order_dish, null);
            holder.dish_name = (TextView)convertView.findViewById(R.id.dish_name);
            holder.dish_count = (TextView)convertView.findViewById(R.id.dish_count);
            holder.dish_price = (TextView)convertView.findViewById(R.id.dish_price);
            holder.dish_gift = (TextView)convertView.findViewById(R.id.dish_gift);
            holder.dish_bz = (TextView)convertView.findViewById(R.id.dish_bz);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String comm = getCookieAndFlavor(orderItem);

        holder.dish_name.setText((position+1)+"."+orderItem.getDishName());
        holder.dish_count.setText(orderItem.getQuantity()+"");
        holder.dish_price.setText("￥"+orderItem.getPrice()+"/份");
        holder.dish_gift.setText(orderItem.isGift()?"是":"否");
        holder.dish_bz.setText(comm.length()>0?comm:"无");

        return convertView;
    }

    //获取做法与口味
    private String getCookieAndFlavor(OrderItem orderItem){
        String commnt = "";
        if(orderItem.getCookList()!=null&&orderItem.getCookList().size()>0){
            for (CookingMethod cookingMethod : orderItem.getCookList()) {
                float price = cookingMethod.getPrice();
                commnt+=cookingMethod.cookName+(price>0?"(+"+price+")":"")+",";
            }
        }
        if(orderItem.getTasteList()!=null&&orderItem.getTasteList().size()>0){
            for (Flavor flavor : orderItem.getTasteList()) {
                commnt+=flavor.tasteName+(flavor.tasteExtraCost>0?"(+"+flavor.tasteExtraCost+")":"")+",";
            }
        }
        if(commnt.length()>0){
            commnt = commnt.substring(0,commnt.length()-1);
        }
        return commnt;
    }

    class ViewHolder {
        TextView dish_name;
        TextView dish_count;
        TextView dish_price;
        TextView dish_gift;
        TextView dish_bz;
    }
}
