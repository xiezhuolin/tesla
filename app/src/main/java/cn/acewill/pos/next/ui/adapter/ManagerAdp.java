package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.config.MyApplication;

/**
 * Created by DHH on 2016/6/17.
 */
public class ManagerAdp<T> extends BaseAdapter{
    private int widch;
    public ManagerAdp(Context context) {
        super(context);
        int screenWidth = MyApplication.getInstance().getScreenWidth();
        widch = (screenWidth/6);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        String managerStr = (String)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_managere_item, null);
            holder.tv_manager = (TextView) convertView.findViewById(R.id.tv_manager);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(widch,widch);
            convertView.setLayoutParams(params);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_manager.setText(managerStr);
        return convertView;
    }
    class ViewHolder {
        TextView tv_manager;
    }

}
