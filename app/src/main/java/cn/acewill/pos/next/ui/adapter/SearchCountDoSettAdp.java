package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;

/**
 * Created by DHH on 2016/6/17.
 */
public class SearchCountDoSettAdp<T> extends BaseAdapter {
    public SearchCountDoSettAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Dish dish = (Dish) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_search_count_sett, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_danger = (TextView) convertView.findViewById(R.id.tv_danger);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_dishName.setText((position+1)+"."+dish.getDishName());
        holder.tv_danger.setText("10份");

        if (dishCountList != null && dishCountList.size() > 0) {
            for (DishCount dishCount : dishCountList) {
                if (dishCount.dishid == dish.getDishId()) {
                    if (dishCount.count <= 10) {
                        holder.tv_count.setText(dishCount.count + "/份");
                        holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.red));
                    } else {
                        holder.tv_count.setText(dishCount.count + "/份");
                        holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.green_press));

                    }
                    break;
                }
            }
        } else {
            if (dish.dishCount <= 0) {
                holder.tv_count.setText(dish.dishCount + "/份");
                holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
            else
            {
                holder.tv_count.setText(dish.dishCount + "/份");
                holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.green_press));
            }
        }
        return convertView;
    }

    private List<DishCount> dishCountList = new ArrayList<>();

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList.clear();
            this.dishCountList = dishCountList;
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_danger;
    }
}
