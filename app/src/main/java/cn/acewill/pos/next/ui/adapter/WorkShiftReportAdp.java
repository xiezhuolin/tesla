package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.WorkShiftNewReport;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * Created by DHH on 2016/6/17.
 */
public class WorkShiftReportAdp<T> extends BaseAdapter{
    public WorkShiftReportAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final WorkShiftNewReport.WorkShiftCategoryDataList itemCategorySalesDataList = (WorkShiftNewReport.WorkShiftCategoryDataList) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_work_shift_report, null);
            holder.tv_categorySales_title = (TextView) convertView.findViewById(R.id.tv_categorySales_title);
            holder.lv_categorySales = (ScrolListView) convertView.findViewById(R.id.lv_categorySales);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_categorySales_title.setText(itemCategorySalesDataList.getName());
        CategorySalesAdp categorySalesAdp = new CategorySalesAdp(context);
        categorySalesAdp.setData(itemCategorySalesDataList.getWorkShiftItemDatas());
        holder.lv_categorySales.setAdapter(categorySalesAdp);
        return convertView;
    }
    class ViewHolder {
        TextView tv_categorySales_title;
        ScrolListView lv_categorySales;
    }

}
