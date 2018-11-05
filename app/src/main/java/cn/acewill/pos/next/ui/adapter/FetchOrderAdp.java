package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.interfices.FetchOrderCallBack;
import cn.acewill.pos.next.model.FetchOrder;
import cn.acewill.pos.next.model.dish.Dish;

/**
 * Created by DHH on 2017/3/22.
 */

public class FetchOrderAdp extends BaseAdapter {
    public Resources res;
    public FetchOrderCallBack callBack;
    public FetchOrderAdp(Context context, FetchOrderCallBack callBack) {
        super(context);
        this.callBack = callBack;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final FetchOrder fetchOrder =  (FetchOrder)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_fetch_order, null);
            holder.tv_order_info = (TextView) convertView.findViewById(R.id.tv_order_info);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_getOrder = (TextView) convertView.findViewById(R.id.tv_getOrder);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        String dishStr = getDishListStr(fetchOrder.getFetchDishList());
        holder.tv_time.setText("下单时间:"+fetchOrder.getCreateOrderTime());
        holder.tv_order_info.setText(dishStr);
        holder.tv_getOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onOk(fetchOrder,position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_order_info;
        TextView tv_time;
        TextView tv_getOrder;
    }

    /**
     * @param dishList
     */
    private String getDishListStr(List<Dish> dishList) {
        StringBuffer sb = new StringBuffer();
        if (dishList != null && dishList.size() > 0) {
            int size = dishList.size();
            for (int i = 0; i < size; i++) {
                Dish dish = dishList.get(i);
                String space = "";
                sb.append((i+1)+"."+dish.getDishName()+"          "+dish.getQuantity()+"/份"+space);
                if(dish.subItemList != null && dish.subItemList.size() >0)
                {
                    StringBuffer sbb = new StringBuffer();
                    sbb.append("\n");
                    int packageSize = dish.subItemList.size();
                    for (int j = 0; j < packageSize; j++) {
                        Dish.Package packageItem = dish.subItemList.get(j);
                        sbb.append("      "+(j+1)+"."+packageItem.getDishName()+"          "+(packageItem.getQuantity()*dish.quantity)+"/份"+space);
                        if(j != packageSize-1)
                        {
                            sbb.append("\n");
                        }
                    }
                    sb.append(sbb.toString());
                }
                if(i != size-1)
                {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}
