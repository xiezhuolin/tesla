package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.interfices.BtnOnClickListener;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.utils.Constant;

/**
 * Created by DHH on 2016/6/17.
 */
public class PrintListAdp<T> extends BaseAdapter{
    private List<DishCount> checkDishCount;
    private BtnOnClickListener callBack;
    public PrintListAdp(Context context,BtnOnClickListener callBack) {
        super(context);
        this.callBack = callBack;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Printer printer = (Printer)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_prints, null);
            holder.print_brand = (TextView) convertView.findViewById(R.id.print_brand);
            holder.print_ip = (TextView) convertView.findViewById(R.id.print_ip);
            holder.print_des = (TextView) convertView.findViewById(R.id.print_des);
            holder.print_type = (TextView) convertView.findViewById(R.id.print_type);
            holder.print_delete = (TextView) convertView.findViewById(R.id.print_delete);
            holder.edit = (TextView) convertView.findViewById(R.id.edit);
            holder.test = (TextView) convertView.findViewById(R.id.test);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.print_brand.setText(printer.getVendor());
        holder.print_ip.setText(printer.getIp());
        holder.print_des.setText(printer.getDescription());
        holder.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.onClick(Constant.DialogStyle.TEST_PRINTER,position);
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView print_brand;
        TextView print_ip;
        TextView print_des;
        TextView print_type;
        TextView print_delete;
        TextView edit;
        TextView test;
    }

}
