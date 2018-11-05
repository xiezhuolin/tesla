package cn.acewill.pos.next.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.model.printer.PrinterStyle;


/***
 * 自定义适配器
 *
 * @author zhangjia
 *
 */
public class DragListAdapter extends BaseAdapter {
	private static final String TAG = "DragListAdapter";
	private List<PrinterStyle> printerModeList;
	//	private ArrayList<Integer> arrayDrawables;
	private Context context;
	public boolean isHidden;
	private int selectPosition = -1;
	private int textSize;

	public DragListAdapter(Context context, List<PrinterStyle> printerModeList/*,
			ArrayList<Integer> arrayDrawables*/) {
		this.context = context;
		this.printerModeList = printerModeList;
		//		this.arrayDrawables = arrayDrawables;
	}

	public void showDropItem(boolean showItem){
		this.ShowItem = showItem;
	}

	public void setInvisiblePosition(int position){
		invisilePosition = position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/***
		 * 在这里尽可能每次都进行实例化新的，这样在拖拽ListView的时候不会出现错乱.
		 * 具体原因不明，不过这样经过测试，目前没有发现错乱。虽说效率不高，但是做拖拽LisView足够了。
		 */
		PrinterStyle PrinterStyle = (PrinterStyle)getItem(position);
		convertView = LayoutInflater.from(context).inflate(R.layout.list_print_item,null);
		ImageView drag_imageView = (ImageView) convertView.findViewById(R.id.drag_list_item_image);
		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		tv_name.setText(PrinterStyle.moduleName);

		if(selectPosition == position)
		{
			convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_right_angle_selected));
		}

		if(PrinterStyle != null)
		{
			//设置对齐方式
			if(PrinterStyle.showLocation == PrinterStyle.showLocation.Left)
			{
				tv_name.setGravity(Gravity.LEFT);
			}
			else if(PrinterStyle.showLocation == PrinterStyle.showLocation.Center)
			{
				tv_name.setGravity(Gravity.CENTER);
			}
			else if(PrinterStyle.showLocation == PrinterStyle.showLocation.Right)
			{
				tv_name.setGravity(Gravity.RIGHT);
			}

			//设置字体大小
			if(PrinterStyle.textSize == PrinterStyle.textSize.NormalSize)
			{
				textSize = PrinterStyle.baseTextSize;
			}
			else if(PrinterStyle.textSize == PrinterStyle.textSize.TwoXSize)
			{
				textSize = PrinterStyle.baseTextSize*2;
			}
			else if(PrinterStyle.textSize == PrinterStyle.textSize.ThereXSize)
			{
				textSize = PrinterStyle.baseTextSize*3;
			}
			tv_name.setTextSize(textSize);
			//设置字体样式
			if(PrinterStyle.textStyle == PrinterStyle.textStyle.Normal)
			{
				tv_name.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));//常体
			}
			else if(PrinterStyle.textStyle == PrinterStyle.textStyle.Bold)
			{
				tv_name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
			}
		}


		if (isChanged){
			if (position == invisilePosition){
				if(!ShowItem){
					drag_imageView.setVisibility(View.INVISIBLE);
					tv_name.setVisibility(View.INVISIBLE);
				}
			}
			if(lastFlag != -1){
				if(lastFlag == 1){
					if(position > invisilePosition){
						Animation animation;
						animation = getFromSelfAnimation(0, -height);
						convertView.startAnimation(animation);
					}
				}else if(lastFlag == 0){
					if(position < invisilePosition){
						Animation animation;
						animation = getFromSelfAnimation(0, height);
						convertView.startAnimation(animation);
					}
				}
			}
		}
		return convertView;
	}

	/***
	 * 动态修改ListVIiw的方位.
	 *
	 * @param start
	 *            点击移动的position
	 * @param down
	 *            松开时候的position
	 */
	private int invisilePosition = -1;
	private boolean isChanged = true;
	private boolean ShowItem = false;

	public void exchange(int startPosition, int endPosition) {
		//		holdPosition = endPosition;
		Object startObject = getItem(startPosition);
		if(startPosition < endPosition){
			printerModeList.add(endPosition + 1, (PrinterStyle) startObject);
			printerModeList.remove(startPosition);
		}else{
			printerModeList.add(endPosition,(PrinterStyle) startObject);
			printerModeList.remove(startPosition + 1);
		}
		isChanged = true;
		//		notifyDataSetChanged();
	}

	public void exchangeCopy(int startPosition, int endPosition) {
//		System.out.println(startPosition + "--" + endPosition);
		//		holdPosition = endPosition;
		Object startObject = getCopyItem(startPosition);
		if(startPosition < endPosition){
			mCopyList.add(endPosition + 1, (PrinterStyle) startObject);
			mCopyList.remove(startPosition);
		}else{
			mCopyList.add(endPosition,(PrinterStyle)startObject);
			mCopyList.remove(startPosition + 1);
		}
		isChanged = true;
		//		notifyDataSetChanged();
	}


	public Object getCopyItem(int position) {
		return mCopyList.get(position);
	}

	@Override
	public int getCount() {
		return printerModeList.size();
	}

	@Override
	public Object getItem(int position) {
		return printerModeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void addDragItem(int start, Object obj){
		Log.i(TAG,"start" + start);
		PrinterStyle title = printerModeList.get(start);
		printerModeList.remove(start);// 删除该项
		printerModeList.add(start, (PrinterStyle) obj);// 添加删除项
	}

	private ArrayList<PrinterStyle> mCopyList = new ArrayList<PrinterStyle>();

	public void copyList(){
		mCopyList.clear();
		for (PrinterStyle str : printerModeList) {
			mCopyList.add(str);
		}
	}

	public void pastList(){
		printerModeList.clear();
		for (PrinterStyle str : mCopyList) {
			printerModeList.add(str);
		}
	}

	private boolean isSameDragDirection = true;
	private int lastFlag = -1;
	private int height;
	private int dragPosition = -1;

	public void setIsSameDragDirection(boolean value){
		isSameDragDirection = value;
	}

	public void setLastFlag(int flag){
		lastFlag = flag;
	}

	public void setHeight(int value){
		height = value;
	}

	public void setCurrentDragPosition(int position){
		dragPosition = position;
	}

	public Animation getFromSelfAnimation(int x,int y){
		TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
				Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
		go.setInterpolator(new AccelerateDecelerateInterpolator());
		go.setFillAfter(true);
		go.setDuration(100);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}

	public Animation getToSelfAnimation(int x,int y){
		TranslateAnimation go = new TranslateAnimation(
				Animation.ABSOLUTE, x, Animation.RELATIVE_TO_SELF, 0,
				Animation.ABSOLUTE, y, Animation.RELATIVE_TO_SELF, 0);
		go.setInterpolator(new AccelerateDecelerateInterpolator());
		go.setFillAfter(true);
		go.setDuration(100);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}

	public void setSelectPosition(int selectPosition) {
		// TODO Auto-generated method stub
		this.selectPosition = selectPosition;
	}
}