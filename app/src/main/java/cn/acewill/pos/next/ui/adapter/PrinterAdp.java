package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.printer.Printer;

/**
 * Created by DHH on 2016/6/17.
 */
public class PrinterAdp<T> extends BaseAdapter{
    private ArrayList<String> printerList;
    public PrinterAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Printer printer = (Printer) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_receipt, null);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_context.setText(printer.getDeviceName());

        if(isHavePrinter(String.valueOf(printer.getId())))
        {
            holder.iv_select.setSelected(true);
        }
        else
        {
            holder.iv_select.setSelected(false);
        }

        return convertView;
    }

    public void setStringArr(ArrayList<String> printerList){
        if (printerList != null){
            this.printerList = printerList;
            this.notifyDataSetChanged();
        }
    }

    private boolean isHavePrinter(String printerId)
    {
        int size = printerList.size();
        for (int i = 0; i < size; i++) {
            String printerSth = printerList.get(i);
            if(printerSth.equals(printerId))
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
