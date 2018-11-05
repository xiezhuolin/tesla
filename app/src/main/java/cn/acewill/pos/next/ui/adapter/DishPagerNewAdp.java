package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.utils.ToolsUtils;


public class DishPagerNewAdp extends PagerAdapter {

    private Context context;
    private GridView lv_dish;
    private GridView gv_dish;
    private View item_vp_dish;
    private DishNewAdp dishAdapter;
    private DishAdp dishAdp;
    private List<Dish> dataList;
    private int type = 0;
    private SparseArray<GridView> listViews = new SparseArray<GridView>();
    private SparseArray<GridView> gridViews = new SparseArray<GridView>();
    private List<DishCount> dishCountList = new CopyOnWriteArrayList<>();
    private Store store;
    private boolean isShowImg = false;

    public DishPagerNewAdp(Context context) {
        this.context = context;
        store = Store.getInstance(context);
    }

    @Override
    public int getCount() {
        return DishDataController.dishKindList != null ? DishDataController.dishKindList.size() : 0;
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
        //图文
        if (isShowImg) {
            item_vp_dish = View.inflate(context, R.layout.item_vp_dish, null);
            gv_dish = (GridView) item_vp_dish.findViewById(R.id.gv_dish_item);
            dishAdp = new DishAdp(context, position);
            if (type == 0) {
                dishAdp.setDataInfo(DishDataController.getdishsForKind(position));
            } else {
                if (dataList != null && dataList.size() > 0) {
                    dishAdp.setDataInfo(dataList);
                    type = 0;
                }
            }
            if(!ToolsUtils.isList(dishCountList))
            {
                dishAdp.setDishCount(dishCountList);
            }
            gv_dish.setAdapter(dishAdp);
            gridViews.put(position, gv_dish);
            //		dishApds.put(position,dishAdapter);
            container.addView(item_vp_dish);
        }
        //纯文字
        else {
            item_vp_dish = View.inflate(context, R.layout.item_vp_dish_new, null);
            lv_dish = (GridView) item_vp_dish.findViewById(R.id.lv_dish_item);
            dishAdapter = new DishNewAdp(context, position, lv_dish);
            if (type == 0) {
                dishAdapter.setDataInfo(DishDataController.getdishsForKind(position));
            } else {
                if (dataList != null && dataList.size() > 0) {
                    dishAdapter.setDataInfo(dataList);
                    type = 0;
                }
            }
            if(!ToolsUtils.isList(dishCountList))
            {
                dishAdapter.setDishCount(dishCountList);
            }
            lv_dish.setAdapter(dishAdapter);
            listViews.put(position, lv_dish);

            container.addView(item_vp_dish);
        }
        return item_vp_dish;
    }

    public void refresh() {
    }

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList = dishCountList;
        }
    }

    public void setDishShowStyle(boolean isShowImg)
    {
        this.isShowImg = isShowImg;
    }

}
