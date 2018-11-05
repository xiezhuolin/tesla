package cn.acewill.pos.next.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by aqw on 2016/6/20.
 */
public class ScreenUtil {

    /**
     * 获取屏幕高宽
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        int[] screens = new int[2];
        screens[0] = width;
        screens[1] = height;
        return screens;
    }

    /**
     * 设置窗口高宽
     * @param context
     * @param height
     * @param width
     */
    public static void setWindow(Context context, double height, double width){
        WindowManager m = ((Activity)context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = ((Activity)context).getWindow().getAttributes();
        p.height = (int) (d.getHeight() * height); // 高度设置为屏幕的0.8
        p.width = (int) (d.getWidth() * width); // 宽度设置为屏幕的0.7
        ((Activity)context).getWindow().setAttributes(p);
        ((Activity)context).setFinishOnTouchOutside(false);//点击空白不消失
    }

}
