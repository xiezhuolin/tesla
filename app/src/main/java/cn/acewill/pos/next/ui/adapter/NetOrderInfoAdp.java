package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.RetreatDishController;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.utils.ToolsUtils;

import static cn.acewill.pos.R.id.dish_price;


/**
 * 订单详情
 * Created by aqw on 2016/9/7.
 */
public class NetOrderInfoAdp extends BaseAdapter {
    public NetOrderInfoAdp(Context context) {
        super(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder ;
        final OrderItem orderItem = (OrderItem) getItem(position);
        final OrderItem tempItem = RetreatDishController.getTempItemList().get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_net_order_info, null);
            holder.dish_name = (TextView)convertView.findViewById(R.id.dish_name);
            holder.dish_count = (TextView)convertView.findViewById(R.id.dish_count);
            holder.dish_price = (TextView)convertView.findViewById(dish_price);
            holder.dish_sub = (TextView)convertView.findViewById(R.id.dish_sub);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(orderItem.subItemList != null && orderItem.subItemList.size() >0)// 套餐菜品
        {
            holder.dish_sub.setVisibility(View.VISIBLE);
            BigDecimal money = orderItem.getCost();
            holder.dish_name.setText(orderItem.getDishName());
            holder.dish_count.setText(money.toString()+"*"+orderItem.getQuantity());
            BigDecimal b2 = new BigDecimal(orderItem.getQuantity()+"");
            BigDecimal subPrice = b2.multiply(orderItem.getCost()==null?new BigDecimal(0):orderItem.getCost()).setScale(2, BigDecimal.ROUND_DOWN);
            holder.dish_price.setText(subPrice+"");

            List<Dish.Package> subItemList = orderItem.getSubItemList();
            StringBuffer sb = new StringBuffer();
            String space = "";
            for (int a = 0; a < subItemList.size(); a++) {
                if(a != subItemList.size()-1)
                {
                    space = "\n";
                }
                String skuStrs = "";
                skuStrs = printDishOption(subItemList.get(a).optionList);
                sb.append((a+1)+"."+subItemList.get(a).getDishName()+"     "+subItemList.get(a).quantity+"/ "+ ToolsUtils.returnXMLStr("copies")+ skuStrs+space);
            }
            holder.dish_sub.setText(sb.toString());
        }
        else
        {
            holder.dish_sub.setVisibility(View.GONE);
            holder.dish_name.setText(orderItem.getDishName());
            holder.dish_count.setText(orderItem.getPrice()+"*"+orderItem.getQuantity());
            BigDecimal b1 = new BigDecimal(orderItem.getQuantity()+"");
            BigDecimal dishPrice = b1.multiply(orderItem.getCost()==null?new BigDecimal(0):orderItem.getCost()).setScale(2, BigDecimal.ROUND_DOWN);
            holder.dish_price.setText(dishPrice+"");

            String skuStrs = printDishOption(orderItem.optionList);
            if(!TextUtils.isEmpty(skuStrs))
            {
                holder.dish_sub.setVisibility(View.VISIBLE);
                holder.dish_sub.setText(skuStrs);
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView dish_name;
        TextView dish_count;
        TextView dish_price;
        TextView dish_sub;
    }
    private boolean isRetreat = true;
    public void isRetreatDish(boolean isRetreat)
    {
        this.isRetreat = isRetreat;
    }

    public interface Retreat{
        //isOneItem为true时，说明此订单只有一个菜品并且数量为1，这时候调用退菜就会调用退单接口
        public void onRetreat(OrderItem orderItem, boolean isOneItem);
    }

    private String printDishOption(List<Option> optionList) {
        StringBuffer sb = new StringBuffer();
        if (optionList != null && optionList.size() > 0) {
                if (optionList != null && optionList.size() > 0) {
                    for (Option option : optionList) {
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                            sb.append(option.name + "、");
                        } else {
                            sb.append(option.name + "(" + option.getPrice() + "￥)、");
                        }
                    }
                }
            }
        return sb.toString();
    }
}
