package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.model.dish.DishType;

public class OrderDishKidsAdp extends BaseAdapter {
	private Context context = null;
	private int positionLocaion;
	private HashMap<String, Integer> order_dish_mp = new HashMap<String, Integer>();
	private List<DishType> mDishKinds;
	private Resources res;

	public OrderDishKidsAdp(Context context) {
		this.context = context;
		res = context.getResources();
	}

	public void setData(List<DishType> mDishKinds) {
		if (mDishKinds != null && mDishKinds.size() >0) {
			this.mDishKinds = mDishKinds;
			this.notifyDataSetChanged();
		}
	}
	
	public void setPosition(int position)
	{
		this.positionLocaion = position;
		this.notifyDataSetChanged();
	}
	
	public void setMap(HashMap<String, Integer> order_dish_mp)
	{
		this.order_dish_mp = order_dish_mp;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDishKinds != null ? mDishKinds.size() : null;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDishKinds != null ? mDishKinds.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		DishType dishkids = mDishKinds.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.adp_lv_order_dish_kids,null);
			holder.tv_kids = (TextView) convertView.findViewById(R.id.tv_kids);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if(positionLocaion == position)
		{
			holder.tv_kids.setBackgroundColor(res.getColor(R.color.white));
			holder.tv_kids.setTextColor(res.getColor(R.color.green));
		}
		else
		{
			holder.tv_kids.setBackgroundColor(res.getColor(R.color.black));
			holder.tv_kids.setTextColor(res.getColor(R.color.white));
		}
		holder.tv_kids.setText(dishkids.getName());
		return convertView;
	}
	
	class ViewHolder {
		TextView tv_kids;
	}
	
}
