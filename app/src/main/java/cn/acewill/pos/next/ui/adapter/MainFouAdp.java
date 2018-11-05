package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.MainSelect;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.widget.MainPopWindow;

/**
 * Created by DHH on 2016/6/17.
 */
public class MainFouAdp<T> extends BaseAdapter{
    private MainPopWindow<T> mainPopWindow;
    public MainFouAdp(Context context, MainPopWindow<T> mainPopWindow) {
        super(context);
        this.mainPopWindow = mainPopWindow;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
       final MainSelect mainSelect = (MainSelect)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_area, null);
            holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_context.setText(mainSelect.getSelectName());

        holder.tv_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_MAIN_DROP_LIST,mainSelect.getSelectId()));
                mainPopWindow.dismiss();
            }
        });
        return convertView;
    }
    class ViewHolder {
        TextView tv_context;
    }

}
