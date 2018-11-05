package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.widget.BadgeView;


/**
 * Created by DHH on 2016/6/17.
 */
public class DishKindsAdp<T> extends BaseAdapter {
    private int position;
    private Map<String, Integer> order_dish_mp = new HashMap<String, Integer>();

    public DishKindsAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishType dishType = (DishType) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_text, null);
            holder.tv_dishKinds = (TextView) convertView.findViewById(R.id.tv_dishKinds);
            holder.tv_line = (TextView) convertView.findViewById(R.id.tv_line);

            holder.badeView = new BadgeView(context, (TextView) holder.tv_dishKinds);
            holder.badeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            holder.badeView.setTextColor(Color.WHITE);
            holder.badeView.setBadgeBackgroundColor(res.getColor(R.color.btn_blue_pressed));
            holder.badeView.setTextSize(14);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishKinds.setText(dishType.getName());

        if (position == this.position) {
            holder.tv_dishKinds.setSelected(true);
        } else {
            holder.tv_dishKinds.setSelected(false);
        }
        if (order_dish_mp != null && order_dish_mp.size() > 0)
        {
            if (order_dish_mp.get(String.valueOf(dishType.getId())) != null)
            {
                int conunt = order_dish_mp.get(String.valueOf(dishType.getId()));
                if (conunt > 0)
                {
                    holder.badeView.setText(conunt + "");
                    holder.badeView.show();
                }
                else
                {
                    holder.badeView.hide();
                }
            }
            else
            {
                holder.badeView.hide();
            }
        }
        else
        {
            holder.badeView.hide();
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_dishKinds;
        TextView tv_line;
        BadgeView badeView;
    }

    public void setSelect(int position) {
        if (this.position != position) {
            this.position = position;
            notifyDataSetChanged();
        }
    }

    public void setMap(Map<String, Integer> order_dish_mp) {
        this.order_dish_mp = order_dish_mp;
        this.notifyDataSetChanged();
    }

}
