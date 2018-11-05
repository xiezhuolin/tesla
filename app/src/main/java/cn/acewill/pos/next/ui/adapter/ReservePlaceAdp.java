package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.newPos.Reserve;

/**
 * Created by DHH on 2016/6/17.
 */
public class ReservePlaceAdp<T> extends BaseAdapter{
    private List<DishCount> checkDishCount;
    public ReservePlaceAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Reserve reserve = (Reserve)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_reserve_place, null);
            holder.tv_customrerName = (TextView) convertView.findViewById(R.id.tv_customrerName);
            holder.tv_repastNumber = (TextView) convertView.findViewById(R.id.tv_repastNumber);
            holder.tv_tableName = (TextView) convertView.findViewById(R.id.tv_tableName);
            holder.tv_dateTime = (TextView) convertView.findViewById(R.id.tv_dateTime);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_customrerName.setText(reserve.customrerName);
        holder.tv_repastNumber.setText(reserve.repastNumber+"人");
        holder.tv_tableName.setText(reserve.tableName+"桌");
        holder.tv_dateTime.setText(reserve.date +"  "+reserve.time);
        return convertView;
    }

    class ViewHolder {
        TextView tv_customrerName;
        TextView tv_repastNumber;
        TextView tv_tableName;
        TextView tv_dateTime;
    }

}
