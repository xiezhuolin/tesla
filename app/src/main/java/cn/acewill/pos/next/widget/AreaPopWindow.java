package cn.acewill.pos.next.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.ui.adapter.AreaAdp;

/**
 * Created by DHH on 2016/6/17.
 */
public class AreaPopWindow<T> extends PopupWindow{
    private View view;
    private Resources res;
    private ListView listSection;
    private AreaAdp areaAdp;

    public AreaPopWindow(final Context context, ArrayList<T> dataList) {
        view = View.inflate(context, R.layout.pop_area, null);
        this.setAnimationStyle(R.style.dialog_bottom_up_show);
        listSection = (ListView) view.findViewById(R.id.list_section);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchable(true);
        setOutsideTouchable(true);
        setContentView(view);
        res = context.getResources();

        areaAdp = new AreaAdp(context,AreaPopWindow.this);
        areaAdp.setData(dataList);
        listSection.setAdapter(areaAdp);

//        tv_all_table.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_AREA,0));
//                AreaPopWindow.this.dismiss();
//            }
//        });
    }
    public void Show(View v)
    {
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-height);
    }
}
