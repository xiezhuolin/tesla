package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.next.config.MyApplication;

/**
 * Created by DHH on 2016/6/17.
 */
public class RollPagerAdp extends StaticPagerAdapter {
    private Context context;
    private ImageLoader imageLoader;
    private List<String> url_imgs;
    public RollPagerAdp(Context context) {
        this.context = context;
        url_imgs = new ArrayList<>();
        imageLoader = MyApplication.getInstance().initImageLoader(context);
    }


//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        final String imageUrl = url_imgs.get(position);
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_roll_pager, null);
//            holder.img_context = (ImageView) convertView.findViewById(R.id.img_context);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
////        imageLoader.displayImage(dish.getImageName(), holder.iv_dishImagePath);
//
//        return convertView;
//    }

    @Override
    public View getView(ViewGroup container, int position) {
//        ViewHolder holder = null;
//        final String imageUrl = url_imgs.get(position);
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_roll_pager, null);
//            holder.img_context = (ImageView) convertView.findViewById(R.id.img_context);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
        ImageView view = new ImageView(container.getContext());
        imageLoader.displayImage(url_imgs.get(position),view);
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getCount() {
        return url_imgs.size();
    }

    public void setData(List<String> url_imgs) {
        this.url_imgs = url_imgs;
    }

}
