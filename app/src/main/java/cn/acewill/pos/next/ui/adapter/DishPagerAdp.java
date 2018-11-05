package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.model.dish.Dish;


public class DishPagerAdp extends PagerAdapter {

	private Context context;
	private GridView gv_dish;
	private View item_vp_dish;
	private DishAdp dishAdapter;
	private List<Dish> dataList;
	private int type = 0;
	private SparseArray<GridView> gridViews = new SparseArray<GridView>();
//	private SparseArray<DishAdp> dishApds = new SparseArray<DishAdp>();

	public DishPagerAdp(Context context) {
		this.context = context;
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
		item_vp_dish = View.inflate(context, R.layout.item_vp_dish,null);
		gv_dish = (GridView) item_vp_dish.findViewById(R.id.gv_dish_item);
		gv_dish.setNumColumns(3);
		dishAdapter = new DishAdp(context, position);
		if(type == 0)
		{
			dishAdapter.setDataInfo(DishDataController.getdishsForKind(position));
		}
		else
		{
			if(dataList != null && dataList.size() >0)
			{
				dishAdapter.setDataInfo(dataList);
				type = 0;
			}
		}
		gv_dish.setAdapter(dishAdapter);
		gridViews.put(position,gv_dish);
//		dishApds.put(position,dishAdapter);
		container.addView(item_vp_dish);

		return item_vp_dish;
	}

	public void setSearchList(List<Dish> dataList,int type){
		this.type = type;
		if (dataList != null){
			this.dataList = dataList;
			this.notifyDataSetChanged();
		}
	}

	
	public void refresh(){
		for (int i = 0;i<getCount();i++) {
			GridView gridView = gridViews.get(i);
//			DishAdp adp = dishApds.get(i);
			if(gridView!=null){
				gridView.smoothScrollToPosition(0);
			}
//			if(adp != null)
//			{
//				adp.notifyDataSetChanged();
//			}
		}
	}

}
