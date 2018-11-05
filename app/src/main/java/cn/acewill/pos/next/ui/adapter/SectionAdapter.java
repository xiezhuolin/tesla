package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.table.Sections;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.widget.BadgeView;


/**
 * Created by aqw on 2016/12/21.
 */
public class SectionAdapter extends BaseAdapter {

    public SectionAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Sections sections = (Sections)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_section_order_menu, null);
            holder.section_name = (TextView) convertView.findViewById(R.id.section_name);
            holder.section_line = (View) convertView.findViewById(R.id.section_line);

            holder.badeView = new BadgeView(context, (TextView) holder.section_name);
            holder.badeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            holder.badeView.setTextColor(Color.WHITE);
            holder.badeView.setBadgeMargin(10,0);
            holder.badeView.setBadgeBackgroundColor(res.getColor(R.color.red));
            holder.badeView.setTextSize(14);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.section_name.setText(sections.getName());
        if (position == getCount() - 1) {
            holder.section_line.setVisibility(View.GONE);
        } else {
            holder.section_line.setVisibility(View.VISIBLE);
        }

        //如果是取单这一项
        if(sections.getSectionsStyle() == Constant.ORDER_GET)
        {
            if( Cart.getHandDishItemList()!= null && Cart.getHandDishItemList().size() >0)
            {
                int size = Cart.getHandDishItemList().size();
                holder.badeView.setText(size + "");
                holder.badeView.show();
            }
            else
            {
                holder.badeView.hide();
            }
        }
        else
        {
            holder.badeView.hide();
        }

        return convertView;
    }

    class ViewHolder {
        TextView section_name;
        View section_line;
        BadgeView badeView;
    }
}
