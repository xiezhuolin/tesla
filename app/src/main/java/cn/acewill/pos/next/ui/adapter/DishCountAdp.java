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

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.interfices.DishCountListener;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.service.PosInfo;

/**
 * Created by DHH on 2016/6/17.
 */
public class DishCountAdp<T> extends BaseAdapter {
    private DishCountListener callback;
    private List<Dish> datas;
    private ImageLoader imageLoader;
    private int widch;
    private PosInfo posInfo;
    private String logoPath;

    public DishCountAdp(Context context, int position, DishCountListener callback) {
        super(context);
        this.callback = callback;
        datas = DishDataController.getdishsForKind(position);
        imageLoader = MyApplication.getInstance().initImageLoader(context);
        int screenWidth = MyApplication.getInstance().getScreenWidth();
        widch = (screenWidth / 6);
        posInfo = posInfo.getInstance();
        logoPath = posInfo.getLogoPath();
    }

    @Override
    public int getCount() {
        return datas != null ? datas.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Dish dish = datas.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dish, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_dishMoney = (TextView) convertView.findViewById(R.id.tv_money);
            holder.btn_add = (TextView) convertView.findViewById(R.id.btn_add);
            holder.iv_sale_out = (ImageView) convertView.findViewById(R.id.iv_sale_out);
            holder.iv_dishImagePath = (ImageView) convertView.findViewById(R.id.iv_dishImagePath);

            AbsListView.LayoutParams params = new AbsListView.LayoutParams(widch - 10, widch);
            convertView.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        holder.tv_dishName.setText(dish.getDishName());
        if (dish.dishCount <= 0) {
            holder.tv_dishMoney.setText(dish.getDishCount() + "");
        } else {
            if (dishCountList != null && dishCountList.size() > 0) {
                for (DishCount dishCount : dishCountList) {
                    if (dishCount.dishid == dish.getDishId()) {
                        holder.tv_dishMoney.setText(dishCount.count + "");
                        break;
                    }
                }
            } else {
                holder.tv_dishMoney.setText(dish.getDishCount() + "");
            }
        }

        if (!TextUtils.isEmpty(dish.getImageName()))

        {
            imageLoader.displayImage(dish.getImageName(), holder.iv_dishImagePath);
        } else {
            if (!TextUtils.isEmpty(logoPath)) {
                imageLoader.displayImage(logoPath, holder.iv_dishImagePath);
            } else {
                imageLoader.displayImage(dish.getImageName(), holder.iv_dishImagePath);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener()
                                       {
                                           @Override
                                           public void onClick(View v) {
                                               callback.setDishCont(dish);
                                           }
                                       }

        );
        return convertView;
    }

    private List<DishCount> dishCountList = new ArrayList<>();

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList.clear();
            this.dishCountList = dishCountList;
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_dishMoney;
        TextView btn_add;
        ImageView iv_dishImagePath;
        ImageView iv_sale_out;
    }
}
