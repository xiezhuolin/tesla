package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.table.Sections;
import cn.acewill.pos.next.widget.AutoFitTextView;


/**
 * Created by aqw on 2016/12/21.
 */
public class EatTypeAdapter extends BaseAdapter {
    private int selectPosition = 0;
    public EatTypeAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Sections sections = (Sections)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_section_eat_type, null);
            holder.tv_eat_type = (AutoFitTextView) convertView.findViewById(R.id.tv_eat_type);
            holder.section_line = (View) convertView.findViewById(R.id.section_line);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_eat_type.setText(sections.getName());
        if (position == getCount() - 1) {
            holder.section_line.setVisibility(View.GONE);
        } else {
            holder.section_line.setVisibility(View.VISIBLE);
        }

        if(selectPosition  == position)
        {
            holder.tv_eat_type.setBackgroundColor(res.getColor(R.color.bbutton_info));
        }
        else{
            holder.tv_eat_type.setBackgroundColor(res.getColor(R.color.login_gray));
        }


        return convertView;
    }


    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    class ViewHolder {
        AutoFitTextView tv_eat_type;
        View section_line;
    }
}
