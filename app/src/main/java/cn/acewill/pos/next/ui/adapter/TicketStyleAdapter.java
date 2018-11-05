package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;

/**小票样式
 * Created by aqw on 2016/8/22.
 */
public class TicketStyleAdapter extends BaseAdapter{

    private int current_select = 0;

    public TicketStyleAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final String styleName = (String) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_ticket_font, null);
            holder.ticket_name = (TextView)convertView.findViewById(R.id.ticket_name);
            holder.pay_select_icon = (ImageView)convertView.findViewById(R.id.pay_select_icon);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(current_select==position){
            holder.ticket_name.setSelected(true);
            holder.pay_select_icon.setSelected(true);
        }else {
            holder.ticket_name.setSelected(false);
            holder.pay_select_icon.setSelected(false);
        }
        holder.ticket_name.setText(styleName);


        return convertView;
    }

    class ViewHolder {
        TextView ticket_name;
        ImageView pay_select_icon;
    }

    public void setCurrent_select(int positiion){
        if(current_select == positiion){
            return;
        }

        this.current_select = positiion;
        this.notifyDataSetChanged();
    }
}
