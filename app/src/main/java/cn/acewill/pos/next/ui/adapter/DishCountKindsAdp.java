package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.DishType;


/**
 * Created by DHH on 2016/6/17.
 */
public class DishCountKindsAdp<T> extends BaseAdapter {
    private int position;

    public DishCountKindsAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishType dishType = (DishType) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dish_kind, null);
            holder.tv_dishKinds = (TextView) convertView.findViewById(R.id.tv_dishKinds);
            holder.tv_bottom_line = (TextView) convertView.findViewById(R.id.tv_bottom_line);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishKinds.setText(dishType.getName());

        if (position == this.position) {
            holder.tv_dishKinds.setSelected(true);
            holder.tv_bottom_line.setBackgroundColor(context.getResources().getColor(R.color.app_title_bg));
        } else {
            holder.tv_dishKinds.setSelected(false);
            holder.tv_bottom_line.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_dishKinds;
        TextView tv_bottom_line;

    }

    public void setSelect(int position) {
        if (this.position != position) {
            this.position = position;
            notifyDataSetChanged();
        }
    }

}
