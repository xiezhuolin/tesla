package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.BigDecimal;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * Created by DHH on 2016/6/17.
 */
public class OrderConfirmAdp<T> extends BaseAdapter{
    private int mLastPosition = -1;
    public OrderConfirmAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order_confirm, null);
            holder.dishName = (TextView) convertView
                    .findViewById(R.id.dishName);
            holder.dishCount = (TextView) convertView.findViewById(R.id.dishCount);
            holder.dishPrice = (TextView) convertView.findViewById(R.id.dishPrice);

            holder.dish_edit_ll = (LinearLayout)convertView.findViewById(R.id.dish_edit_ll);
            holder.dish_show_ll = (LinearLayout)convertView.findViewById(R.id.dish_show_ll);
            holder.dish_reduce = (ImageView)convertView.findViewById(R.id.dish_reduce);
            holder.dish_count = (TextView)convertView.findViewById(R.id.dish_count);
            holder.dish_add = (ImageView)convertView.findViewById(R.id.dish_add);
            holder.dish_delete = (ImageView)convertView.findViewById(R.id.dish_delete);
            holder.isout = (TextView)convertView.findViewById(R.id.isout);
            holder.num_tv = (TextView)convertView.findViewById(R.id.num_tv);
            holder.suborder_lv = (ScrolListView)convertView.findViewById(R.id.suborder_lv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Dish dish = (Dish) getItem(position);
        if (dish != null) {
            // 菜名
            String dishName = (position + 1) + "." + dish.getDishName();
            holder.dishName.setText(dishName);
            BigDecimal price = dish.getPrice();
            final int did = dish.getDishId();
            int count = dish.selectDishCount;
            holder.dishCount.setText("X  " + count);
            holder.dish_count.setText(count+"");
            holder.dishPrice.setText("￥" + price);
            final double leftamount = Double.valueOf(dish.dishCount);

            if(0<leftamount&&leftamount<10)//当小于10份时显示剩余提示文本
            {
                holder.num_tv.setText("剩余"+leftamount+"份");
            }
            else
            {
                holder.num_tv.setText("");
            }
            holder.isout.setText("");
            holder.dish_add.setVisibility(View.VISIBLE);
            holder.dish_reduce.setVisibility(View.VISIBLE);
            holder.dish_count.setVisibility(View.VISIBLE);
            holder.dish_show_ll.setBackgroundColor(context.getResources().getColor(R.color.white));

            if (position == mLastPosition) {
                holder.dish_edit_ll.setVisibility(View.VISIBLE);
            }else{
                holder.dish_edit_ll.setVisibility(View.GONE);
            }

            holder.dish_show_ll.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    changeItemVisibility(position);
                }
            });

            // 加号
            holder.dish_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int dishId = dish.getDishId();
                    Dish goodsModel = Cart.getItemByDid( dishId );
                    goodsModel.selectDishCount++;
                    Cart.notifyContentChange();

                    holder.dish_count.setText(goodsModel.selectDishCount+"");
                    holder.dishCount.setText("X" + goodsModel.selectDishCount);
                    notifyDataSetChanged();
                }
            });
//            // 减号
//            holder.dish_reduce.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int dishId = dish.dishID;
//                    Dish goodsModel = Cart.getItemByDid(dishId);
//                    goodsModel.selectDishCount--;
//                    if (goodsModel.selectDishCount <= 0) {
//                        Cart.removeItem(goodsModel);
//                    } else {
//                        Cart.notifyContentChange();
//                    }
//
//                    holder.dish_count.setText(goodsModel.selectDishCount+"");
//                    holder.dishCount.setText("X" + goodsModel.selectDishCount);
//                    notifyDataSetChanged();
//                }
//            });
//
//            // 删除
//            holder.dish_delete.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    int dishId = dish.dishID;
//                    Dish goodsModel = Cart.getItemByDid(dishId);
//                    Cart.removeItem(goodsModel);
//
//                    notifyDataSetChanged();
//                    Cart.notifyContentChange();
//                }
//            });

        }


        return convertView;
    }

    private void changeItemVisibility(int position) {
        if (position == mLastPosition) {
            mLastPosition = -1;
        } else {
            mLastPosition = position;
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView dishName;
        TextView dishCount;
        TextView dishPrice;
        LinearLayout dish_edit_ll;
        LinearLayout dish_show_ll;
        ImageView dish_reduce;
        TextView dish_count;
        ImageView dish_add;
        ImageView dish_delete;
        TextView isout;
        TextView num_tv;
        ScrolListView suborder_lv;
    }

}
