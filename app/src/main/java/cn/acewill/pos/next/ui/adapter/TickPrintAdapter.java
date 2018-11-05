package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.printer.Printer;

/**
 * 打印机列表-结账选择小票打印时
 * Created by aqw on 2016/11/25.
 */
public class TickPrintAdapter extends BaseAdapter{

    private static int[] selectP;

    public TickPrintAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final Printer printer = (Printer)getItem(position);
        if (convertView == null) {
            if(selectP==null){
                selectP = new int[getCount()];
                for (int i : selectP) {
                    selectP[i] = 0;
                }
            }

            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_active, null);
            holder.type_ll = (LinearLayout)convertView.findViewById(R.id.type_ll);
            holder.type_name = (TextView)convertView.findViewById(R.id.type_name);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(selectP[position]==1){
            holder.type_ll.setSelected(true);
        }else {
            holder.type_ll.setSelected(false);
        }

        holder.type_name.setText(printer.getDeviceName());

        holder.type_ll.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(holder.type_ll.isSelected()){
                    holder.type_ll.setSelected(false);
                    selectP[position] = 0;

                }else {
                    holder.type_ll.setSelected(true);
                    selectP[position] = 1;
                }
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


    class ViewHolder {
        LinearLayout type_ll;
        TextView type_name;
    }

    //获取选择的打印机
    public List<Printer> getSelectPrints(){
        List<Printer> printerList = new ArrayList<>();
        for (int i = 0; i < selectP.length; i++) {
            Log.e("selectP"+i+":",selectP[i]+"");
            if (selectP[i]==1){
                Printer printer = (Printer)getItem(i);
                printerList.add(printer);
            }
        }
        return printerList;
    }

}
