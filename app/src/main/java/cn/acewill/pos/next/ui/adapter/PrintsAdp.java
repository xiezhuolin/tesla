package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.printer.Printer;

/**
 * Created by DHH on 2016/6/17.
 */
public class PrintsAdp<T> extends BaseAdapter{
    public PrintsAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Printer printer = (Printer) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_device, null);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_ip = (TextView) convertView.findViewById(R.id.tv_ip);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_name.setText(printer.getDescription());
        holder.tv_ip.setText(printer.getIp());
        return convertView;
    }
    class ViewHolder {
        TextView tv_name;
        TextView tv_ip;
    }

}
