package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;

/**
 * Created by DHH on 2016/6/17.
 */
public class OrderAdp<T> extends BaseAdapter{
    private List<DishCount> checkDishCount;
    public OrderAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final int selectIndex = position;
        final Dish dish = Cart.getDishItemList().get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_order, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.btn_minus = (Button) convertView.findViewById(R.id.btn_minus);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishName.setText((position+1)+"."+dish.getDishName());
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.getInstance().reduceItem(position);
            }
        });

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart.getInstance().addItem(position);
            }
        });
        holder.tv_count.setText(dish.quantity+"");

        if(checkDishCount != null && checkDishCount.size()>0)
        {
            DishCount dishCount =  checkDishCount.get(position);
            if(dishCount != null)
            {
                if(dishCount.getDishid() == dish.getDishId())
                {
                    convertView.setBackgroundColor(MyApplication.getInstance().getResources().getColor(R.color.common_yellow));
                }
            }
        }

        return convertView;
    }

    public void setCheckDishCount(List<DishCount> checkDishCount){
        if (checkDishCount != null){
            if(this.checkDishCount != null && this.checkDishCount.size() >0)
            {
                this.checkDishCount.clear();
            }
            this.checkDishCount = checkDishCount;
            this.notifyDataSetChanged();
        }
    }
    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        Button btn_minus;
        Button btn_add;
    }

}
