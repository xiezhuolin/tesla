package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.math.BigDecimal;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.report.DishReport;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * Created by aqw on 2016/12/15.
 */
public class ReportDishAdapter extends BaseAdapter {

    public ReportDishAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        DishReport dishReport = (DishReport) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_report_dish, null);
            holder.bg_ll =  (LinearLayout)convertView.findViewById(R.id.bg_ll);
            holder.dish_name = (ComTextView) convertView.findViewById(R.id.dish_name);
            holder.dish_num = (ComTextView) convertView.findViewById(R.id.dish_num);
            holder.dish_money = (ComTextView) convertView.findViewById(R.id.dish_money);
            holder.dish_pro = (ComTextView) convertView.findViewById(R.id.dish_pro);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.dish_name.setText(dishReport.getName());
        holder.dish_num.setText(dishReport.getSalesAmount()+"");
        holder.dish_money.setText("￥"+dishReport.getTotal().setScale(2, BigDecimal.ROUND_DOWN));
        holder.dish_pro.setText(dishReport.getProportion().setScale(1,BigDecimal.ROUND_DOWN)+"%");

        if(position%2==0){
            holder.bg_ll.setBackgroundResource(R.color.layout_gray);
        }else {
            holder.bg_ll.setBackgroundResource(R.color.transform);
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout bg_ll;
        ComTextView dish_name;
        ComTextView dish_num;
        ComTextView dish_money;
        ComTextView dish_pro;
    }
}
