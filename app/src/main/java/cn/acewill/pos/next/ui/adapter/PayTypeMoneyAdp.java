package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.interfices.BtnOnClickListener;
import cn.acewill.pos.next.model.payment.PayType;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.utils.FormatUtils;

/**
 * 结账时每种支付方式，支付金额
 * Created by aqw on 2016/8/19.
 */
public class PayTypeMoneyAdp extends BaseAdapter{

    private BtnOnClickListener btnOnClickListener;

    public PayTypeMoneyAdp(Context context, BtnOnClickListener btnOnClickListener) {
        super(context);
        this.btnOnClickListener = btnOnClickListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final PayType payType = (PayType) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.iv_item_paytyep_money,null);
            holder.payTypeName = (TextView)convertView.findViewById(R.id.payTypeName);
            holder.payTypeMoney = (TextView)convertView.findViewById(R.id.payTypeMoney);
            holder.payTypeReturn = (TextView)convertView.findViewById(R.id.payTypeReturn);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.payTypeName.setText(payType.getName());
        holder.payTypeMoney.setText(FormatUtils.getDoubleW(payType.getMoney()));
        holder.payTypeReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btnOnClickListener.onClick(payType.getId(),position);
            }
        });

        return convertView;
    }
    class ViewHolder {
        TextView payTypeName;
        TextView payTypeMoney;
        TextView payTypeReturn;
    }

}
