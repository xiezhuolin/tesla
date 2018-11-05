package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.BigDecimal;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.PaymentList;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * Created by aqw on 2016/11/29.
 */
public class PayTypeSelectedAdp extends BaseAdapter {

    public PayTypeSelectedAdp(Context context){
        super(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final PaymentList paymentList = (PaymentList) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.item_selected_pay, null);
            holder.pay_name = (ComTextView)convertView.findViewById(R.id.pay_name);
            holder.pay_money = (ComTextView)convertView.findViewById(R.id.pay_money);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.pay_name.setText(paymentList.getPayName());
        holder.pay_money.setText(paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN)+"￥");

        return convertView;
    }

    class ViewHolder {
        ComTextView pay_name;
        ComTextView pay_money;
    }

}
