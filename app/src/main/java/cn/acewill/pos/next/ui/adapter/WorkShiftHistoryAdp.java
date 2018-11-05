package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.WorkShift;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.utils.Constant;

/**
 * Created by DHH on 2016/6/17.
 */
public class WorkShiftHistoryAdp<T> extends BaseAdapter{
    public WorkShiftHistoryAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final WorkShift workShift = (WorkShift) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_work_shift, null);
            holder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_worketName = (TextView) convertView.findViewById(R.id.tv_worketName);
            holder.tv_terminalName = (TextView) convertView.findViewById(R.id.tv_terminalName);
            holder.tv_startTime = (TextView) convertView.findViewById(R.id.tv_startTime);
            holder.tv_endTime = (TextView) convertView.findViewById(R.id.tv_endTime);
            holder.print_btn = (TextView) convertView.findViewById(R.id.print_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_id.setText(workShift.getId()+"");
        holder.tv_name.setText(workShift.getUserName());
        holder.tv_worketName.setText(workShift.getDefinitionName());
        holder.tv_terminalName.setText(workShift.getTerminalName());
        holder.tv_startTime.setText(workShift.getStartTimeStr());
        if(TextUtils.isEmpty(workShift.getEndTimeStr()))
        {
            holder.tv_endTime.setText("未交班");
        }
        else{
            holder.tv_endTime.setText(workShift.getEndTimeStr());
        }

        holder.print_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.PRINT_WORKE_SHIFT_HISTORY,Integer.valueOf(workShift.getId()+"")));
            }
        });

        return convertView;
    }
    class ViewHolder {
        TextView tv_id;
        TextView tv_name;
        TextView tv_worketName;
        TextView tv_terminalName;
        TextView tv_startTime;
        TextView tv_endTime;
        TextView print_btn;
    }

}
