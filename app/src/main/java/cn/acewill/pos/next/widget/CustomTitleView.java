package cn.acewill.pos.next.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.acewill.pos.R;
import cn.acewill.pos.next.utils.ScreenUtil;

/**
 * TODO: 自定义标题栏
 */
public class CustomTitleView extends RelativeLayout {

    private RelativeLayout title_group;
    private TextView title_left;
    private TextView title_center;
    private TextView title_right_r;
    private TextView title_right_l;

    public CustomTitleView(Context context) {
        super(context,null);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.title_view,this);

        title_group = (RelativeLayout)findViewById(R.id.title_group);
        title_left = (TextView)findViewById(R.id.title_left);
        title_center = (TextView)findViewById(R.id.title_center);
        title_right_r = (TextView)findViewById(R.id.title_right_r);
        title_right_l = (TextView)findViewById(R.id.title_right_l);

        final TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomTitleView);

        String leftText = typedArray.getString(R.styleable.CustomTitleView_leftText);
        boolean leftTextVisible = typedArray.getBoolean(R.styleable.CustomTitleView_leftTextVisible,true);
        float leftTextSize = typedArray.getDimension(R.styleable.CustomTitleView_leftTextSize,15);
        int leftTextColor = typedArray.getColor(R.styleable.CustomTitleView_leftTextColor,getResources().getColor(R.color.black));

        String titleText = typedArray.getString(R.styleable.CustomTitleView_titleText);
        float titleSize = typedArray.getDimension(R.styleable.CustomTitleView_titleSize,17);
        int titleColor = typedArray.getColor(R.styleable.CustomTitleView_titleColor,getResources().getColor(R.color.black));

        String rightLText = typedArray.getString(R.styleable.CustomTitleView_rightLText);
        float rightLTextSize = typedArray.getDimension(R.styleable.CustomTitleView_rightLTextSize,15);
        int rightLTextColor = typedArray.getColor(R.styleable.CustomTitleView_rightLTextColor,getResources().getColor(R.color.black));
        boolean rightLTextVisible = typedArray.getBoolean(R.styleable.CustomTitleView_rightLTextVisible,false);

        String rightText = typedArray.getString(R.styleable.CustomTitleView_rightText);
        float rightTextSize = typedArray.getDimension(R.styleable.CustomTitleView_rightTextSize,15);
        int rightTextColor = typedArray.getColor(R.styleable.CustomTitleView_rightTextColor,getResources().getColor(R.color.black));
        boolean rightTextVisible = typedArray.getBoolean(R.styleable.CustomTitleView_rightTextVisible,true);

        typedArray.recycle();

        int titleHeight = ScreenUtil.getScreenSize(context)[1];
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,titleHeight/9);
        title_group.setLayoutParams(params);

        title_left.setText(TextUtils.isEmpty(leftText)?"返回":leftText);
        title_left.setVisibility(leftTextVisible?View.VISIBLE:View.GONE);
        title_left.setTextSize(leftTextSize);
        title_left.setTextColor(leftTextColor);

        title_center.setText(titleText);
        title_center.setTextSize(titleSize);
        title_center.setTextColor(titleColor);

        title_right_l.setText(rightLText);
        title_right_l.setTextSize(rightLTextSize);
        title_right_l.setTextColor(rightLTextColor);
        title_right_l.setVisibility(rightLTextVisible?View.VISIBLE:View.GONE);

        title_right_r.setText(rightText);
        title_right_r.setTextSize(rightTextSize);
        title_right_r.setTextColor(rightTextColor);
        title_right_r.setVisibility(rightTextVisible?View.VISIBLE:View.GONE);


    }


}
