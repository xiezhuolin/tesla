package cn.acewill.pos.next.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by DHH on 2017/5/17.
 */

public class ZoneGridView extends GridView {

    private int position = 0;

    public ZoneGridView(Context context) {
        super(context);
    }

    public ZoneGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    public void setCurrentPosition(int pos) {
        // 刷新adapter前，在activity中调用这句传入当前选中的item在屏幕中的次序
        this.position = pos;
    }

    @SuppressLint("NewApi")
    @Override
    protected void setChildrenDrawingOrderEnabled(boolean enabled) {
        // TODO Auto-generated method stub
        super.setChildrenDrawingOrderEnabled(enabled);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if (i == childCount - 1) {// 这是最后一个需要刷新的item
            return position;
        }
        if (i == position) {// 这是原本要在最后一个刷新的item
            return childCount - 1;
        }
        return i;// 正常次序的item
    }
}
