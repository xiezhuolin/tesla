package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.table.Area;
import cn.acewill.pos.next.widget.AreaPopWindow;

/**
 * Created by DHH on 2016/6/17.
 */
public class AreaAdp<T> extends BaseAdapter{
    private AreaPopWindow<T> tAreaPopWindow;
    public AreaAdp(Context context, AreaPopWindow<T> tAreaPopWindow) {
        super(context);
        this.tAreaPopWindow = tAreaPopWindow;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Area area = (Area) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_area, null);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_context.setText(area.getName()+" ("+area.getTotalTables()+"/"+area.getInuseTables()+") ");

        holder.tv_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_AREA,area.getId()));
//                tAreaPopWindow.dismiss();
            }
        });
        return convertView;
    }
    class ViewHolder {
        TextView tv_context;
    }

}
