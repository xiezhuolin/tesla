package cn.acewill.pos.next.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.ui.adapter.MainFouAdp;

/**
 * Created by DHH on 2016/6/17.
 */
public class MainPopWindow<T> extends PopupWindow{
    private View view;
    private Resources res;
    private Context context;
    private ListView listSection;
    private MainFouAdp mainFouAdp;

    @TargetApi( Build.VERSION_CODES.CUPCAKE)
    public MainPopWindow(final Context context, ArrayList<T> dataList) {
        view = View.inflate(context, R.layout.popup_view_main, null);
        this.setAnimationStyle(R.style.dialog_up_show);
        this.context = context;
        listSection = (ListView) view.findViewById(R.id.list_section);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
//        half_trans_light_gray
        setTouchable(true);
        setOutsideTouchable(false);
        setContentView(view);
        res = context.getResources();

        mainFouAdp = new MainFouAdp(context,MainPopWindow.this);
        mainFouAdp.setData(dataList);

        listSection.setAdapter(mainFouAdp);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                boolean isLocation  = inRangeOfView(listSection,ev);
                if(!isLocation)
                {
                    dismiss();
                }
                return false;
            }
        });

    }

    private boolean inRangeOfView(View view, MotionEvent ev){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if(ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())){
            return false;
        }
        return true;
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
//        showAtLocation(v, Gravity.NO_GRAVITY,0, 0);
        showAsDropDown(v,0, 0);
//        showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1]-height);
    }

}
