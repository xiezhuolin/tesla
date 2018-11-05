package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.BadgeView;
import cn.acewill.pos.next.widget.MarqueeTextView;

/**
 * Created by DHH on 2017/2/16.
 */

public class DishKidsCountAdp<T> extends BaseAdapter{
    private Context context;
    public List<T> dataList;
    private static int positions = 0;//初始化点击分类下标
//    private static int last = -1;
    private int positionPage = 0;
    private int VIEW_COUNT = 8;
    public Resources res;
    private Map<String, Integer> order_dish_mp = new HashMap<String, Integer>();
    private ArrayList<HashMap<String, DishType>> listItem = new ArrayList<>();
    private int isSelect = -1;
    private int isClick = -1;
    private boolean isInit = true;
    private int widch;

    public DishKidsCountAdp(Context context)
    {
        this.context = context;
        res = context.getResources();
        int screenWidth = MyApplication.getInstance().getScreenWidth();
        widch = (screenWidth / 6);
    }
    @Override
    public int getCount() {
        // ori表示到目前为止的前几页的总共的个数。
        int ori = VIEW_COUNT * positionPage;

        // 值的总个数-前几页的个数就是这一页要显示的个数，如果比默认的值小，说明这是最后一页，只需显示这么多就可以了
        if (listItem.size() - ori < VIEW_COUNT) {
            return listItem.size() - ori;
        }
        // 如果比默认的值还要大，说明一页显示不完，还要用换一页显示，这一页用默认的值显示满就可以了。
        else {
            return VIEW_COUNT;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setNotifyDataChange(int id) {
        isSelect = id;
        super.notifyDataSetChanged();
    }

    public void setNotifyItemSelected(int id) {
        positions = id;
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishType dishType = listItem.get(position + positionPage * VIEW_COUNT).get("kindsItem");
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_text_new, null);
            holder.tv_dishKinds = (MarqueeTextView) convertView.findViewById(R.id.tv_dishKinds);
            holder.rel_count = (RelativeLayout) convertView.findViewById(R.id.rel_count);
//            holder.tv_line = (TextView) convertView.findViewById(R.id.tv_line);
//            holder.tv_bottom_line2 = (TextView) convertView.findViewById(R.id.tv_bottom_line2);

            holder.badeView = new BadgeView(context, (TextView) holder.tv_dishKinds);
            holder.badeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            holder.badeView.setTextColor(Color.WHITE);
            holder.badeView.setBadgeBackgroundColor(res.getColor(R.color.red));
            holder.badeView.setTextSize(14);

//            AbsListView.LayoutParams params = new AbsListView.LayoutParams(widch, 60);
//            convertView.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_dishKinds.setText(dishType.getName());
        if(positions == position)
        {
            holder.tv_dishKinds.setTextColor(res.getColor(R.color.white));
            holder.tv_dishKinds.setTextSize(22);
            holder.rel_count.setBackgroundColor(ToolsUtils.getStrColor2Int(dishType.getKindColor()));
        }
        else{
            holder.tv_dishKinds.setTextColor(res.getColor(R.color.black));
            holder.tv_dishKinds.setTextSize(18);
            holder.rel_count.setBackgroundColor(ToolsUtils.getStrColor2Int("ffffff"));
        }

//        if((position+1) % 4 == 0)
//        {
//            holder.tv_line.setVisibility(View.GONE);
//        }
//        if (position == positions) {
//            showOnFocusAnimation(convertView);
//            ToolsUtils.writeUserOperationRecords("选中了("+dishType.getName()+")分类");
//            holder.tv_dishKinds.setSelected(true);
//            holder.tv_bottom_line2.setVisibility(View.GONE);
//            holder.rel_count.startAnimation(magnifyAnim);
//        }
//        else {
//            holder.tv_dishKinds.setSelected(false);
//            holder.tv_bottom_line2.setVisibility(View.GONE);
//        }
        if (order_dish_mp != null && order_dish_mp.size() > 0)
        {
            if (order_dish_mp.get(String.valueOf(dishType.getId())) != null)
            {
                int conunt = order_dish_mp.get(String.valueOf(dishType.getId()));
                if (conunt > 0)
                {
                    holder.badeView.setText(conunt + "");
                    holder.badeView.show();
                }
                else
                {
                    holder.badeView.hide();
                }
            }
            else
            {
                holder.badeView.hide();
            }
        }
        else
        {
            holder.badeView.hide();
        }

        return convertView;
    }

    class ViewHolder {
        MarqueeTextView tv_dishKinds;
//        TextView tv_line;
//        TextView tv_bottom_line2;
        RelativeLayout rel_count;
        BadgeView badeView;
    }

    public void setSelect(int position) {
        if (this.positions != position) {
            this.positions = position;
            notifyDataSetChanged();
        }
    }
    public void setSelectPage(int positionPage) {
        if (this.positionPage != positionPage) {
            this.positionPage = positionPage;
            notifyDataSetChanged();
        }
    }

    public void setData(List<T> dataList){
        if (dataList != null){
            this.dataList = dataList;
            this.notifyDataSetChanged();
        }
    }

    public void setMap(Map<String, Integer> order_dish_mp) {
        this.order_dish_mp = order_dish_mp;
        this.notifyDataSetChanged();
    }

    public void setKidsMapList(ArrayList<HashMap<String, DishType>> listItem) {
        if(listItem != null && listItem.size() >0)
        {
            if(this.listItem.size()  >0)
            {
                this.listItem.clear();
            }
            this.listItem.addAll(listItem);
        }
        this.notifyDataSetChanged();
    }

    private AnimationSet manimationSet;
    ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    ScaleAnimation scaleAnimation1 = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    /**
     * 播放动画
     *
     * @param view
     */
    private void startAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        if (manimationSet != null && manimationSet != animationSet) {

            scaleAnimation.setDuration(500);
            manimationSet.addAnimation(scaleAnimation);
            manimationSet.setFillAfter(false);
            view.startAnimation(manimationSet);
        }
        scaleAnimation1.setDuration(500);
        animationSet.addAnimation(scaleAnimation1);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);
        manimationSet = animationSet;
    }
}
