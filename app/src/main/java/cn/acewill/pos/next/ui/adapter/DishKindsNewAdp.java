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
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.BadgeView;


/**
 * Created by DHH on 2016/6/17.
 */
public class DishKindsNewAdp<T> extends BaseAdapter {
    private int position = -1;
    private Map<String, Integer> order_dish_mp = new HashMap<String, Integer>();

    public DishKindsNewAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishType dishType = (DishType) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_text_new, null);
            holder.tv_dishKinds = (TextView) convertView.findViewById(R.id.tv_dishKinds);
//            holder.tv_line = (TextView) convertView.findViewById(R.id.tv_line);
//            holder.tv_bottom_line2 = (TextView) convertView.findViewById(R.id.tv_bottom_line2);

            holder.badeView = new BadgeView(context, (TextView) holder.tv_dishKinds);
            holder.badeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            holder.badeView.setTextColor(Color.WHITE);
            holder.badeView.setBadgeBackgroundColor(res.getColor(R.color.blue_table_nomber_title));
            holder.badeView.setTextSize(14);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if((position+1) % 4 == 0)
        {
//            holder.tv_line.setVisibility(View.GONE);
        }

        holder.tv_dishKinds.setText(dishType.getName());
        if (position == this.position) {
            ToolsUtils.writeUserOperationRecords("选中了("+dishType.getName()+")分类");
            holder.tv_dishKinds.setSelected(true);
//            holder.tv_bottom_line2.setVisibility(View.VISIBLE);
        } else {
            holder.tv_dishKinds.setSelected(false);
//            holder.tv_bottom_line2.setVisibility(View.GONE);
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
//        TextView tv_line;
//        TextView tv_bottom_line2;
        BadgeView badeView;
    }

    public void setSelect(int position) {
        if (this.position != position) {
            this.position = position;
            notifyDataSetChanged();
        }
    }

    public void setMap(Map<String, Integer> order_dish_mp) {
        if(order_dish_mp != null && order_dish_mp.size() >0)
        {
            this.order_dish_mp = order_dish_mp;
        }
        this.notifyDataSetChanged();
    }

}
