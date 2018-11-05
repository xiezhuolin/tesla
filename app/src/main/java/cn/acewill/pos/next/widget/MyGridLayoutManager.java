package cn.acewill.pos.next.widget;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.acewill.pos.next.utils.ScreenUtil;

/**
 * Created by aqw on 2016/9/5.
 */
public class MyGridLayoutManager extends GridLayoutManager {

    private Context context;


    public MyGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        this.context = context;
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {

        int itemCount = getItemCount();
        if(itemCount == 0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec);
            return ;
        }
        View view = recycler.getViewForPosition(0);
        if(view != null){
            measureChild(view, widthSpec, heightSpec);
            int measuredWidth = View.MeasureSpec.getSize(widthSpec);
            int measuredHeight = view.getMeasuredHeight();
            setMeasuredDimension(measuredWidth, measuredHeight);
        }
    }
}
