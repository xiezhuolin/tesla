package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.ui.activity.newPos.OrderAmountAty;
import cn.acewill.pos.next.utils.ToolsUtils;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class FragmentOrderNewAdp extends RecyclerView.Adapter {
    public Context context;
    public List<Order> dataList = new CopyOnWriteArrayList<>();
    public Cart cart;
    public Resources res;
    public MyApplication myApplication;
    public LayoutInflater inflater;
    private RefrushLisener refrushLisener;
    private int selectPosition = -1;


    public static final int UP_LOAD_TYPE = 0;//上拉加载
    public static final int DOWN_LOAD_TYPE = 1;//下拉刷新
    public int load_type = 0;//加载类型

    public static final int LOAD_MORE = 0;//加载更多
    public static final int LOADING = 1;//正在加载
    public static final int NO_MORE = 2;//没有数据了
    public int load_more_status = 0;
    private int mLastPosition = -1;

    private static final int TYPE_ITEM = 0;//普通Item
    private static final int TYPE_FOOTER = 1;//底部footview


    public FragmentOrderNewAdp(Context context, RefrushLisener refrushLisener) {
        this.context = context;
        this.refrushLisener = refrushLisener;
        this.cart = Cart.getInstance();
        res = context.getResources();
        inflater = LayoutInflater.from(context);
        myApplication = MyApplication.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.item_lv_net_order, parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        } else if (viewType == TYPE_FOOTER) {
            View foot_view = inflater.inflate(R.layout.foot_view, parent, false);
            FootViewHolder footViewHolder = new FootViewHolder(foot_view);
            return footViewHolder;
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final Order orderInfo = dataList.get(position);
            if(position == selectPosition)
            {
                itemViewHolder.lin_count.setBackgroundColor(res.getColor(R.color.bbutton_info_order_blue));
            }
            else{
                itemViewHolder.lin_count.setBackgroundColor(res.getColor(R.color.white));
            }
            itemViewHolder.order_num.setText(ToolsUtils.returnXMLStr("order_id_xx")+orderInfo.getId() + "");
            String numberType = "";
            String callNumber = "";
            if (ToolsUtils.logicIsTable()) {
                numberType = ToolsUtils.returnXMLStr("table_number");
                callNumber = (TextUtils.isEmpty(orderInfo.getTableNames()) ? "0" : orderInfo.getTableNames());
            } else {
                if (StoreInfor.cardNumberMode) {
                    numberType = ToolsUtils.returnXMLStr("card_number");
                    callNumber = (TextUtils.isEmpty(orderInfo.getTableNames()) ? "0" : orderInfo.getTableNames());
                } else {
                    numberType = ToolsUtils.returnXMLStr("call_number");
                    callNumber = (TextUtils.isEmpty(orderInfo.getCallNumber()) ? "0" : orderInfo.getCallNumber());
                }
            }
            itemViewHolder.order_tableName.setText(numberType+callNumber);
            String source = orderInfo.getSource();
            if (!TextUtils.isEmpty(source)) {
                if (source.equals("2")) {
                    source = "微信点餐";
                }
            } else {
                source = "未知来源";
            }
            itemViewHolder.order_source.setText(source);
            String type = orderInfo.getOrderType();
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
            itemViewHolder.order_eatType.setText(orderType+"  >");
            String payMentStatus = orderInfo.getPaymentStatus().toString();
            String payType = "";
            if ("NOT_PAYED".equals(payMentStatus)) {//未支付：显示修改与支付
                payType = ToolsUtils.returnXMLStr("unpaid2");
            }
            else if("PAYED".equals(payMentStatus))
            {
                payType = "已支付";
            }
            else if("REFUND".equals(payMentStatus))
            {
                payType = "已退款";
            }
            else if("FAILED_TO_QUERY_STATUS".equals(payMentStatus))
            {
                payType = "未知订单状态";
            }
            else if("CANCELED".equals(payMentStatus))
            {
                payType = "已取消";
            }
            else if("DUPLICATED".equals(payMentStatus))
            {
                payType = "重复单";
            }
            itemViewHolder.order_paystate.setText(payType);

            holder.itemView.setTag(position);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;

            switch (load_more_status) {
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
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView order_num;
        private TextView order_tableName;
        private TextView order_source;
        private TextView order_eatType;
        private TextView order_paystate;
        private LinearLayout lin_count;

        public ItemViewHolder(View view) {
            super(view);
            order_num = (TextView) view.findViewById(R.id.order_num);
            order_tableName = (TextView) view.findViewById(R.id.order_tableName);
            order_source = (TextView) view.findViewById(R.id.order_source);
            order_eatType = (TextView) view.findViewById(R.id.order_eatType);
            order_paystate = (TextView) view.findViewById(R.id.order_paystate);
            lin_count = (LinearLayout) view.findViewById(R.id.lin_count);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView load_more_tv;
        private ProgressBar load_icon;

        public FootViewHolder(View itemView) {
            super(itemView);
            load_more_tv = (TextView) itemView.findViewById(R.id.load_more_tv);
            load_icon = (ProgressBar) itemView.findViewById(R.id.load_icon);
        }
    }

    /**
     * 更新数据
     *
     * @param orders
     */
    public void setData(List<Order> orders) {
        if (orders != null && orders.size() > 0) {
            switch (load_type) {
                case UP_LOAD_TYPE://上拉加载
                    this.dataList.addAll(orders);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = orders;
                    break;
            }
        } else {
            this.dataList = new ArrayList<>();
        }
        this.notifyDataSetChanged();
    }

    /**
     * status
     * 0:加载更多；1:加载中；2:没有数据了；3:上拉刷新
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        load_more_status = status;
        this.notifyDataSetChanged();
    }

    public void setLoadType(int type) {
        load_type = type;
    }

    public interface RefrushLisener {
        public void refrush();
    }

    public Order getItem(int position) {
        try {
            if(dataList != null && dataList.size() >0)
            {
                Order order = dataList.get(position);
                if(order != null)
                {
                    return order;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSelectPosition(int position)
    {
        this.selectPosition = position;
        notifyDataSetChanged();
    }

    private void show() {
        ((OrderAmountAty)context).showProgress();
    }

    private void dissmiss() {
        ((OrderAmountAty) context).dissmiss();
    }

    /**
     * 休息两秒
     */
    private void sleep() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
