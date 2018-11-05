package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.RetreatDishController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.utils.ToolsUtils;


/**
 * Created by DHH on 2016/6/17.
 */
public class RetreatAdp extends BaseAdapter {
    private int position;
    private Order order;
    private Context context;
    public RetreatAdp(Context context) {
        super(context);
        this.context = context;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final OrderItem dishItem = (OrderItem) getItem(position);
        final OrderItem tempItem = RetreatDishController.getTempItemList().get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_retreat, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_money_count = (TextView) convertView.findViewById(R.id.tv_money_count);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_person = (TextView) convertView.findViewById(R.id.tv_person);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishName.setText(dishItem.getDishName());
        holder.tv_time.setText(ToolsUtils.formatTime(order.getCreatedAt()));
        holder.tv_money_count.setText(dishItem.getPrice()+"*"+dishItem.getQuantity());
        holder.tv_person.setText(order.getCreatedBy());
        holder.tv_count.setText(tempItem.rejectedQuantity+"");

        //加
        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectQuantity = ++tempItem.rejectedQuantity;
//                System.out.println(selectQuantity+"=====++++===="+tempItem.quantity);
                if(selectQuantity < tempItem.quantity  || selectQuantity == tempItem.quantity)
                {
                    holder.tv_count.setText(selectQuantity+"");
                    RetreatDishController.addItem(position,selectQuantity);
                }
                else
                {
                    --tempItem.rejectedQuantity;
                    MyApplication.getInstance().ShowToast("超出可选份额!");
                    holder.tv_count.setText(tempItem.rejectedQuantity+"");
                }


            }
        });

        //减
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectQuantity = --tempItem.rejectedQuantity;
//                System.out.println(selectQuantity+"=====----===="+tempItem.quantity);
                if(selectQuantity >0 && selectQuantity != 0)
                {
                    holder.tv_count.setText(selectQuantity+"");
                    RetreatDishController.minusItem(position,selectQuantity);
                }
                else
                {
                    tempItem.rejectedQuantity = 0;
                    holder.tv_count.setText(tempItem.rejectedQuantity+"");
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_money_count;
        TextView tv_time;
        TextView tv_count;
        TextView tv_person;
        Button btn_add;
        Button btn_minus;
    }

    public void setOrder(Order order)
    {
        this.order = order;
    }


}
