package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.service.PosInfo;

/**
 * Created by DHH on 2016/6/17.
 */
public class DishMenuItemAdp<T> extends BaseAdapter {
    private ImageLoader imageLoader;
    private int widch;
    private int heigh;
    private int screenWidth;//屏幕的宽
    private int screenHeigh;//屏幕的高
    private int showPointType;//显示的模式
    private PosInfo posInfo;
    private String logoPath;

    public DishMenuItemAdp(Context context) {
        super(context);
        imageLoader = MyApplication.getInstance().initImageLoader(context);
        screenWidth = MyApplication.getInstance().getScreenWidth();
        screenHeigh = MyApplication.getInstance().getScreenHeight();
        posInfo = posInfo.getInstance();
        logoPath = posInfo.getLogoPath();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Dish dish = (Dish)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dish_menu_item, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_dishMoney = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_option = (TextView) convertView.findViewById(R.id.tv_option);
            holder.iv_dishImagePath = (ImageView) convertView.findViewById(R.id.iv_dishImagePath);

            heigh = screenHeigh/6;
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heigh);
            convertView.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tv_dishName.setText(dish.getDishName());
        String money = String.format("%.2f ", dish.getPrice());
        holder.tv_dishMoney.setText("¥ " + money);

        String image = dish.getImageName();
        if (!TextUtils.isEmpty(image)) {
            imageLoader.displayImage(image, holder.iv_dishImagePath);
        } else {
            if (!TextUtils.isEmpty(logoPath)) {
                imageLoader.displayImage(logoPath, holder.iv_dishImagePath);
            } else {
                imageLoader.displayImage(image, holder.iv_dishImagePath);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_dishMoney;
        TextView tv_option;
        ImageView iv_dishImagePath;
    }

}
