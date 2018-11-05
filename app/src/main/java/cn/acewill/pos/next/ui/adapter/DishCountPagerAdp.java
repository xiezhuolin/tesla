package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.interfices.DishCountListener;
import cn.acewill.pos.next.model.dish.DishCount;


public class DishCountPagerAdp extends PagerAdapter {

	private Context context;
	private GridView gv_dish;
	private DishCountListener callback;
	private DishCountAdp dishAdapter;
	private SparseArray<GridView> gridViews = new SparseArray<GridView>();

	public DishCountPagerAdp(Context context, DishCountListener callback) {
		this.context = context;
		this.callback = callback;
	}

	@Override
	public int getCount() {
		return DishDataController.dishKindList != null ? DishDataController.dishKindList.size() :0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View item_vp_dish = View.inflate(context, R.layout.item_vp_dish,null);
		gv_dish = (GridView) item_vp_dish.findViewById(R.id.gv_dish_item);
		gv_dish.setNumColumns(5);
		dishAdapter = new DishCountAdp(context,position,callback);
		gv_dish.setAdapter(dishAdapter);
		gridViews.put(position,gv_dish);
		container.addView(item_vp_dish);
		return item_vp_dish;
	}
	
	public void refresh(){
//		for (int i = 0;i<getCount();i++) {
//			GridView gridView = gridViews.get(i);
//			if(gridView!=null){
//				gridView.smoothScrollToPosition(0);
//			}
//		}
	}

	private List<DishCount> dishCountList = new ArrayList<>();;
	public void setDishCount(List<DishCount> dishCountList)
	{
		if(dishCountList != null && dishCountList.size() >0)
		{
			this.dishCountList.clear();
			this.dishCountList = dishCountList;
			dishAdapter.setDishCount(dishCountList);
			gv_dish.setAdapter(dishAdapter);
		}
	}


}
