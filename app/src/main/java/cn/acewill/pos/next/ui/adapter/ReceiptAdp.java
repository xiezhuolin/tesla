package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.Receipt;

/**
 * Created by DHH on 2016/6/17.
 */
public class ReceiptAdp<T> extends BaseAdapter{
    private List<Receipt> receiptList;
    public ReceiptAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Receipt receipt = (Receipt) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_receipt, null);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_context.setText(receipt.getName());

        if(isHaveReceipt(receipt))
        {
            holder.iv_select.setSelected(true);
        }
        else
        {
            holder.iv_select.setSelected(false);
        }

        return convertView;
    }

    public void setReceipt(List<Receipt> receiptList){
        if (receiptList != null){
            this.receiptList = receiptList;
            this.notifyDataSetChanged();
        }
    }

    private boolean isHaveReceipt(Receipt receipt)
    {
        int size = receiptList.size();
        for (int i = 0; i < size; i++) {
            Receipt selectReceipt = receiptList.get(i);
            if(selectReceipt.getId() == receipt.getId())
            {
                return true;
            }
        }
        return false;
    }




    class ViewHolder {
        TextView tv_context;
        ImageView iv_select;
    }

}
