package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.dish.OptionCategory;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolGridView;

/**
 * Created by DHH on 2016/6/17.
 */
public class OptionCategoryAdp<T> extends BaseAdapter{
    private Dish dish;
    public OptionCategoryAdp(Context context,Dish dish) {
        super(context);
        this.dish = dish;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final OptionCategory optionCategory = (OptionCategory) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_option_category, null);
            holder.tv_menu_name = (TextView) convertView.findViewById(R.id.tv_menu_name);
            holder.gv_cook = (ScrolGridView) convertView.findViewById(R.id.gv_cook);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_menu_name.setText(optionCategory.name);
        if(optionCategory.optionList != null && optionCategory.optionList.size() >0)
        {
            List<OptionCategory> optionCategoryList = ToolsUtils.cloneTo(dataList);
            OptionAdp optionAdp = new OptionAdp(context,dish,optionCategoryList);
            optionAdp.setOptionList(optionList);
            optionAdp.setData(optionCategory.optionList);
            holder.gv_cook.setAdapter(optionAdp);
        }
        return convertView;
    }

    private ArrayList<Option> optionList;
    public void setOptionList(ArrayList<Option> optionList)
    {
        if(optionList != null && optionList.size() >0)
        {
            this.optionList = optionList;
        }
    }

    class ViewHolder {
        TextView tv_menu_name;
        ScrolGridView gv_cook;
    }

}
