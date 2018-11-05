package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.table.TableInfor;
import cn.acewill.pos.next.model.table.TableStatus;
import cn.acewill.pos.next.utils.TimeUtil;


/**
 * Created by aqw on 2016/12/19.
 */
public class TableAdapter extends BaseAdapter {
    private int position = -1;
    public TableAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final TableInfor tableInfor = (TableInfor) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table, null);
            holder.table_bg = (LinearLayout)convertView.findViewById(R.id.table_bg);
            holder.time_ll = (LinearLayout)convertView.findViewById(R.id.time_ll);
            holder.table_name = (TextView)convertView.findViewById(R.id.table_name);
            holder.table_people = (TextView)convertView.findViewById(R.id.table_people);
            holder.table_money = (TextView)convertView.findViewById(R.id.table_money);
            holder.table_time = (TextView)convertView.findViewById(R.id.table_time);
            holder.use_iv = (ImageView)convertView.findViewById(R.id.use_iv);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        String tableName = tableInfor.getName();
        if(tableInfor.getStatus() == TableStatus.IN_USE&&!TextUtils.isEmpty(tableInfor.getTemStu())){
            tableName = tableInfor.getTemStu()+tableName;
        }
        holder.table_name.setText(tableName);
        holder.table_people.setText(tableInfor.getRealNumber()+"/"+tableInfor.getCapacity());
        holder.table_money.setText(tableInfor.getMoney()!=null?"￥"+tableInfor.getMoney()+"":"￥0.00");
        holder.table_time.setText(TimeUtil.getHours(tableInfor.getCreated_time()));

        holder.table_name.setTextColor(ContextCompat.getColor(context,R.color.white));
        holder.table_people.setTextColor(ContextCompat.getColor(context,R.color.white));
        holder.table_money.setTextColor(ContextCompat.getColor(context,R.color.white));
        holder.table_time.setTextColor(ContextCompat.getColor(context,R.color.white));

        holder.table_money.setVisibility(View.GONE);
        holder.time_ll.setVisibility(View.GONE);
        holder.use_iv.setVisibility(View.GONE);


        if(tableInfor.getStatus() == TableStatus.EMPTY){//空闲
            holder.table_bg.setBackgroundResource(R.drawable.btn_selector_write);

            holder.table_name.setTextColor(ContextCompat.getColor(context,R.color.gray_search_text));
            holder.table_people.setTextColor(ContextCompat.getColor(context,R.color.gray_search_text));
            holder.table_money.setTextColor(ContextCompat.getColor(context,R.color.gray_search_text));
            holder.table_time.setTextColor(ContextCompat.getColor(context,R.color.gray_search_text));

        }else if(tableInfor.getStatus() == TableStatus.IN_USE){//使用
            holder.table_bg.setBackgroundResource(R.drawable.btn_selector_blue);
            holder.table_money.setVisibility(View.VISIBLE);
            holder.time_ll.setVisibility(View.VISIBLE);
            holder.use_iv.setVisibility(View.VISIBLE);

        }else if(tableInfor.getStatus() == TableStatus.DIRTY){//脏台
            holder.table_bg.setBackgroundResource(R.drawable.btn_gray);
        }
        if(this.position == position)
        {
            holder.table_bg.setBackgroundResource(R.drawable.btn_selector_orange);
        }
        return convertView;
    }

    public void setSelectIndex(int position)
    {
        this.position = position;
        notifyDataSetChanged();
    }

    class ViewHolder {
        LinearLayout table_bg;
        LinearLayout time_ll;
        TextView table_name;
        TextView table_people;
        TextView table_money;
        TextView table_time;
        ImageView use_iv;
    }
}
