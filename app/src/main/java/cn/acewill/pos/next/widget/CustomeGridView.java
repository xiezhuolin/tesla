package cn.acewill.pos.next.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

@SuppressLint("NewApi")
public class CustomeGridView extends GridView {

	public CustomeGridView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public CustomeGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomeGridView(Context context) {
		super(context);
	}

//	/**
//	 * 设置上下不滚动
//	 */
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if(ev.getAction() == MotionEvent.ACTION_MOVE){
//			return true;//true:禁止滚动
//		}
//		return super.dispatchTouchEvent(ev);
//	}

}