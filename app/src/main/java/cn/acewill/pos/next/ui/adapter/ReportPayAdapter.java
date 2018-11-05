package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.math.BigDecimal;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.OrderItemReportData;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * 支付方式报表
 * Created by aqw on 2016/12/13.
 */
public class ReportPayAdapter extends BaseAdapter{

    public ReportPayAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        OrderItemReportData.ItemSalesData itemSalesData = (OrderItemReportData.ItemSalesData) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_report_pay, null);
            holder.bg_ll = (LinearLayout)convertView.findViewById(R.id.bg_ll);
            holder.pay_name = (ComTextView) convertView.findViewById(R.id.pay_name);
            holder.num = (ComTextView) convertView.findViewById(R.id.num);
            holder.money = (ComTextView) convertView.findViewById(R.id.money);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pay_name.setText(itemSalesData.name);
        holder.num.setText(itemSalesData.itemCounts+"");
        holder.money.setText("￥"+itemSalesData.total.setScale(2, BigDecimal.ROUND_DOWN));

        if(position%2==0){
            holder.bg_ll.setBackgroundResource(R.color.layout_gray);
        }else {
            holder.bg_ll.setBackgroundResource(R.color.transform);
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout bg_ll;
        ComTextView pay_name;
        ComTextView num;
        ComTextView money;
    }
}
