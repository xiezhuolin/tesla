package cn.acewill.pos.next.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import cn.acewill.pos.R;

public class LoadAppDialog extends Dialog {
	
	TextView tipTextView;

	public LoadAppDialog(Context context) {
		super(context, R.style.loading_dialog);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.layout_upapp_dialog);
		setCanceledOnTouchOutside(false);
		// main.xml中的ImageView
		ImageView spaceshipImage = (ImageView) findViewById(R.id.img);
		tipTextView = (TextView) findViewById(R.id.tipTextView);// 提示文字
		// 加载动画
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		// 使用ImageView显示动画
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		
	}
	
	public void setText(String msg){
		tipTextView.setText(msg);// 设置加载信息
		tipTextView.setVisibility(View.VISIBLE);
	}
}
