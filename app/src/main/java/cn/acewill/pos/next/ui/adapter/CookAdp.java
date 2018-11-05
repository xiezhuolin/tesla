package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.CookingMethod;

/**
 * Created by DHH on 2016/6/17.
 */
public class CookAdp<T> extends BaseAdapter{
    private int width;
    private int height;
    public CookAdp(Context context) {
        super(context);
        int sc_width = MyApplication.getInstance().getScreenWidth();
        width = sc_width/7-10;
        height = width/3+20;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CookingMethod cook = (CookingMethod)getItem(position);
        final TextView textView = new TextView(context);
        if (cook.getPrice() == 0) {
            textView.setText(cook.cookName);
        } else {
            textView.setText(cook.cookName + " 加" + cook.getPrice() + "元");
        }
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(0xff000000);
        android.widget.AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(
                width, height);
        textView.setPadding(5,5,5,5);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundResource(R.drawable.selector_item_cooker_xb);
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (textView.isSelected()) {
                    textView.setSelected(false);
                } else {
                    textView.setSelected(true);
                }
            }
        });

        return textView;
    }

}
