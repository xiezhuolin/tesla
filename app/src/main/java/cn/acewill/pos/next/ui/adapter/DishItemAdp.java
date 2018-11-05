package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.DishItem;

/**
 * Created by DHH on 2016/6/17.
 */
public class DishItemAdp<T> extends BaseAdapter{
    public DishItemAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishItem dishItem = (DishItem)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dish_menu, null);
            holder.tv_dishMenu = (TextView) convertView.findViewById(R.id.tv_dishMenu);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_dishMenu.setText(dishItem.getItemStr());
        return convertView;
    }

    class ViewHolder {
        TextView tv_dishMenu;
    }

}
