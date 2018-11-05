package cn.acewill.pos.next.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.acewill.pos.next.utils.ScreenUtil;

/**
 * Created by aqw on 2016/12/13.
 */
public class ComLinearLayout extends LinearLayout {

    private Context context;

    public ComLinearLayout(Context context) {
        super(context);
        this.context = context;
    }

    public ComLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
//        int screenHeight = ScreenUtil.getScreenSize(context)[1];
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,screenHeight/2);
//        setLayoutParams(params);
    }
}
