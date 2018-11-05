package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class OrderDayNewAdapter extends RecyclerView.Adapter {

    public Context context;
    public List<Order> dataList = new ArrayList<>();
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


    public OrderDayNewAdapter(Context context, List<Order> dataList, RefrushLisener refrushLisener){
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
    }

    public Order getItem(int position) {
        if(dataList != null && dataList.size() >0)
        {
            if(dataList.get(position) != null)
            {
                return dataList.get(position);
            }
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.lv_item_order_day_new,parent,false);
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
            Order order = dataList.get(position);
            itemViewHolder.tv_time.setText(TimeUtil.getStringTimeLong(order.getCreatedAt()));
            String type = order.getOrderType();
            String orderType = "堂食";
            if(type.equals("EAT_IN"))
            {
                orderType = "堂食";
            }
            else if(type.equals("SALE_OUT"))
            {
                orderType = "外卖";
            }
            else if(type.equals("TAKE_OUT"))
            {
                orderType = "外带";
            }
            itemViewHolder.tv_customer.setText(orderType);
            itemViewHolder.tv_orderId.setText(order.getId()+"");
            itemViewHolder.tv_money.setText("￥"+order.getTotal());
            if(ToolsUtils.logicIsTable())
            {
                itemViewHolder.tv_operator.setText(!TextUtils.isEmpty(order.getTableNames())?order.getTableNames():"0");
            }
            else
            {
                String callNumber = "";
                if (StoreInfor.cardNumberMode) {
                    callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
                } else {
                    callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
                }
                itemViewHolder.tv_operator.setText(callNumber);
            }
            String payMentStatus = order.getPaymentStatus().toString();
//            if(order.getStatus() == OrderStatus.PENDING)
//            {
//                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.common_orange));
//            }

            if ("NOT_PAYED".equals(payMentStatus)) {//未支付：显示修改与支付
                itemViewHolder.tv_isPay.setText(ToolsUtils.returnXMLStr("unpaid2"));
            }
           else if("PAYED".equals(payMentStatus))
            {
                itemViewHolder.tv_isPay.setText("已支付");
            }
            else if("REFUND".equals(payMentStatus))
            {
                itemViewHolder.tv_isPay.setText("已退款");
            }
            else if("FAILED_TO_QUERY_STATUS".equals(payMentStatus))
            {
                itemViewHolder.tv_isPay.setText("未知订单状态");
            }
            else if("CANCELED".equals(payMentStatus))
            {
                itemViewHolder.tv_isPay.setText("已取消");
            }
            else if("DUPLICATED".equals(payMentStatus))
            {
                itemViewHolder.tv_isPay.setText("重复单");
            }

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
        private TextView tv_time;
        private TextView tv_customer;
        private TextView tv_money;
        private TextView tv_operator;
        private TextView tv_isPay;
        private TextView tv_orderId;

        public ItemViewHolder(View view){
            super(view);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            tv_customer = (TextView) view.findViewById(R.id.tv_customer);
            tv_money = (TextView) view.findViewById(R.id.tv_money);
            tv_operator = (TextView) view.findViewById(R.id.tv_operator);
            tv_isPay = (TextView)view.findViewById(R.id.tv_isPay);
            tv_orderId = (TextView)view.findViewById(R.id.tv_orderId);
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
    public void setData(List<Order> orders){
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

    public interface RefrushLisener{
        public void refrush();
    }


}
