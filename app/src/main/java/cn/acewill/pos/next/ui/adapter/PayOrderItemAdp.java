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
import cn.acewill.pos.next.model.MarketObject;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * Created by aqw on 2016/12/2.
 */
public class PayOrderItemAdp extends BaseAdapter{

    public PayOrderItemAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final OrderItem orderItem = (OrderItem) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_pay_order, null);
            holder.dish_name = (ComTextView)convertView.findViewById(R.id.dish_name);
            holder.dish_count = (ComTextView)convertView.findViewById(R.id.dish_count);
            holder.tv_disCount = (TextView)convertView.findViewById(R.id.tv_disCount);
            holder.dish_price = (ComTextView)convertView.findViewById(R.id.dish_price);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dish_name.setText(orderItem.getDishName());
        holder.dish_count.setText(orderItem.getQuantity()+"");

        String marketListStr = "";
        String tempMarketListStr = "";
        if(orderItem.getMarketList() != null && orderItem.getMarketList().size() >0)
        {
            marketListStr = ToolsUtils.getDisCountStr(orderItem.getMarketList());
        }
        if(orderItem.getTempMarketList() != null && orderItem.getTempMarketList().size() >0)
        {
             tempMarketListStr = getDisCountStr(orderItem.getTempMarketList());
        }
        String newLine = "";
        if(!TextUtils.isEmpty(marketListStr))
        {
            newLine = "\n";
        }
        String marketStr = marketListStr+newLine+tempMarketListStr;
        if(!TextUtils.isEmpty(marketStr))
        {
            holder.tv_disCount.setVisibility(View.VISIBLE);
            holder.tv_disCount.setText(marketStr);
        }
        else{
            holder.tv_disCount.setVisibility(View.GONE);
        }
        BigDecimal tempMarketPrice = new BigDecimal("0.000");
        BigDecimal dishCost = orderItem.getCost().multiply(new BigDecimal(orderItem.getQuantity())).setScale(2,BigDecimal.ROUND_HALF_UP);
        holder.dish_price.setText(dishCost+"￥");
        return convertView;
    }
//    private static BigDecimal reduceCast = new BigDecimal("0.00");
    public static String getDisCountStr(List<MarketObject> marketList)
    {
//        reduceCast = new BigDecimal("0.00");
        StringBuffer sb = new StringBuffer();
        if(marketList != null && marketList.size() >0)
        {
            int size = marketList.size();
            for (int i = 0; i < size; i++) {
                MarketObject market = marketList.get(i);
                sb.append(market.getMarketName() +"-"+market.getReduceCash().toString()+" ¥");
                if(i != size-1)
                {
                    sb.append("\n");
                }
//                reduceCast = market.getReduceCash().add(reduceCast);
            }
        }
        return sb.toString();
    }



    class ViewHolder {
        ComTextView dish_name;
        ComTextView dish_count;
        ComTextView dish_price;
        TextView tv_disCount;
    }
}
