package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.interfices.RefrushLisener;
import cn.acewill.pos.next.model.user.Staff;
import cn.acewill.pos.next.utils.ToolsUtils;


/**
 * 员工列表
 * Created by aqw on 2016/8/16.
 */
public class StaffAdapter extends RecyclerView.Adapter {

    public Context context;
    public List<Staff> dataList = new ArrayList<>();
    public LayoutInflater inflater;
    private RefrushLisener refrushLisener;


    public static final int UP_LOAD_TYPE = 0;//上拉加载
    public static final int DOWN_LOAD_TYPE = 1;//下拉刷新
    public int load_type = 0;//加载类型

    public static final int LOAD_MORE = 0;//加载更多
    public static final int LOADING = 1;//正在加载
    public static final int NO_MORE = 2;//没有数据了
    public int load_more_status = 0;

    private static final int TYPE_ITEM = 0;//普通Item
    private static final int TYPE_FOOTER = 1;//底部footview


    public StaffAdapter(Context context, RefrushLisener refrushLisener){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
    }

    public Staff getItem(int position) {
        return dataList != null ? dataList.get(position) != null ?dataList.get(position):null :null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.lv_item_staff,parent,false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }else if(viewType == TYPE_FOOTER){
            View foot_view = inflater.inflate(R.layout.foot_view,parent,false);
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            Staff staff = dataList.get(position);
            itemViewHolder.staff_name.setText(staff.getRealname());
            itemViewHolder.staff_number.setText(staff.getQuartersid()+"");
            itemViewHolder.staff_role.setText(staff.getQuartersName());

            holder.itemView.setTag(position);
        }else if(holder instanceof FootViewHolder){
            FootViewHolder footViewHolder = (FootViewHolder)holder;

            switch (load_more_status){
                case LOAD_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText(ToolsUtils.returnXMLStr("pull_up_to_load_more"));
                    break;
                case LOADING:
                    footViewHolder.load_icon.setVisibility(View.VISIBLE);
                    footViewHolder.load_more_tv.setText(ToolsUtils.returnXMLStr("loading"));
                    break;
                case NO_MORE:
                    footViewHolder.load_icon.setVisibility(View.GONE);
                    footViewHolder.load_more_tv.setText("");
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position + 1 == getItemCount()){
            return TYPE_FOOTER;
        }else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout order_ll;
        private TextView staff_name;
        private TextView staff_number;
        private TextView staff_role;

        public ItemViewHolder(View view){
            super(view);
            order_ll = (LinearLayout)view.findViewById(R.id.order_ll);
            staff_name = (TextView) view.findViewById(R.id.staff_name);
            staff_number = (TextView) view.findViewById(R.id.staff_number);
            staff_role = (TextView)view.findViewById(R.id.staff_role);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder{
        private TextView load_more_tv;
        private ProgressBar load_icon;

        public FootViewHolder(View itemView) {
            super(itemView);
            load_more_tv = (TextView)itemView.findViewById(R.id.load_more_tv);
            load_icon = (ProgressBar)itemView.findViewById(R.id.load_icon);
        }
    }

    /**
     * 更新数据
     * @param orders
     */
    public void setData(List<Staff> orders){
        if(orders!=null&&orders.size()>0){
            switch (load_type){
                case UP_LOAD_TYPE://上拉加载
                    dataList.addAll(orders);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = orders;
                    break;
            }
        }
        else
        {
            this.dataList.clear();
        }
        this.notifyDataSetChanged();
    }

    /**
     * status
     * 0:加载更多；1:加载中；2:没有数据了；3:上拉刷新
     * @param status
     */
    public void changeMoreStatus(int status){
        load_more_status = status;
        this.notifyDataSetChanged();
    }

    public void setLoadType(int type){
        load_type = type;
    }

}
