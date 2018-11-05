package cn.acewill.pos.next.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderSingleReason;
import cn.acewill.pos.next.model.order.PaymentStatus;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.TradeService;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.ui.activity.CheckOutAty;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;


/**
 * 當日訂單
 * Created by aqw on 2016/8/16.
 */
public class OrderDayAdapter extends RecyclerView.Adapter {

    public Context context;
    public List<Order> dataList ;
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


    public OrderDayAdapter(Context context,List<Order> dataList,RefrushLisener refrushLisener){
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.lv_item_order_day,parent,false);
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
            String tableName = dataList.get(position).getTableNames();
            if(!TextUtils.isEmpty(tableName))
            {
                itemViewHolder.order_tableName.setText(dataList.get(position).getTableNames()+"号桌");
            }
            else
            {
                itemViewHolder.order_tableName.setText("");
            }
            itemViewHolder.order_num.setText(dataList.get(position).getId()+"");
            itemViewHolder.take_num.setText(dataList.get(position).getCallNumber());
            itemViewHolder.order_source.setText(dataList.get(position).getSource());
            itemViewHolder.order_total.setText(dataList.get(position).getTotal());
            itemViewHolder.creat_time.setText(TimeUtil.getStringTime(dataList.get(position).getCreatedAt()));
            PaymentStatus paymentStatus =  dataList.get(position).getPaymentStatus();
            final String paymentstate;
            if(paymentStatus != null) {
                paymentstate = paymentStatus.toString();
                if ("NOT_PAYED".equals(paymentstate)) {//未支付：显示修改与支付
                    itemViewHolder.edit_btn.setText("修改");
                    itemViewHolder.pay_btn.setText("结账");
                    itemViewHolder.order_cost.setText("0.00");
                    itemViewHolder.pay_btn.setBackgroundResource(R.drawable.btn_selector_red);
                } else {//已支付:显示反结账与退款
                    itemViewHolder.order_cost.setText(dataList.get(position).getCost());
                    itemViewHolder.edit_btn.setText("反结账");
                    if ("REFUND".equals(paymentstate)) {//已退款
                        itemViewHolder.pay_btn.setText("已退款");
                        itemViewHolder.pay_btn.setBackgroundResource(R.drawable.btn_gray);
                    } else {
                        itemViewHolder.pay_btn.setText("退款");
                        itemViewHolder.pay_btn.setBackgroundResource(R.drawable.btn_selector_blue);
                    }
                }

                itemViewHolder.edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("NOT_PAYED".equals(paymentstate)) {//修改

                        } else if ("PAYED".equals(paymentstate)) {//反结账

                        }
                    }
                });
                itemViewHolder.pay_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("NOT_PAYED".equals(paymentstate)) {//支付
                            Intent intent = new Intent(context, CheckOutAty.class);
                            intent.putExtra("orderId", dataList.get(position).getId() + "");
                            intent.putExtra("source", Constant.EventState.SOURCE_ORDER_DAY);

                            context.startActivity(intent);
                        } else if ("PAYED".equals(paymentstate)) {//退款
                            getSingleReason(dataList.get(position).getId() + "");
                        }
                    }
                });
            }
            else
            {
                itemViewHolder.order_cost.setText("0.00");
                itemViewHolder.pay_btn.setText("错误订单状态");
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
        private TextView order_num;
        private TextView take_num;
        private TextView order_source;
        private TextView order_tableName;
        private TextView order_total;
        private TextView order_cost;
        private TextView creat_time;
        private TextView edit_btn;
        private TextView pay_btn;

        public ItemViewHolder(View view){
            super(view);
            order_num = (TextView) view.findViewById(R.id.order_num);
            take_num = (TextView) view.findViewById(R.id.take_num);
            order_source = (TextView) view.findViewById(R.id.order_source);
            order_total = (TextView) view.findViewById(R.id.order_total);
            order_cost = (TextView)view.findViewById(R.id.order_cost);
            creat_time = (TextView)view.findViewById(R.id.creat_time);
            edit_btn = (TextView)view.findViewById(R.id.edit_btn);
            pay_btn = (TextView)view.findViewById(R.id.pay_btn);
            order_tableName = (TextView)view.findViewById(R.id.order_tableName);

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
            this.notifyDataSetChanged();
        }else{
            this.dataList = new ArrayList<>();
            this.notifyDataSetChanged();
        }
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

    /**
     * 获取退款原因列表
     */
    private void getSingleReason(final String orderId){
        try{
            final ProgressDialogF dialogF = new ProgressDialogF(context);
            dialogF.showLoading("");
            TradeService tradeService = TradeService.getInstance();
            tradeService.getSingleReason(new ResultCallback<List<OrderSingleReason>>() {
                @Override
                public void onResult(List<OrderSingleReason> result) {
                    dialogF.disLoading();
                    if(result!=null&&result.size()>0){

                        final Dialog dialog = DialogUtil.getDialog(context,R.layout.dialog_reason,0.5f,0.5f);
                        Window dialogWindow = dialog.getWindow();
                        WindowManager m = ((Activity) context).getWindowManager();
                        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                        p.width = (int) (d.getWidth() * 0.3f); // 高度设置为屏幕的0.5
                        p.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        dialogWindow.setAttributes(p);

                        ListView reasonList = (ListView)dialog.findViewById(R.id.reason_list);
                        TextView cancle = (TextView) dialog.findViewById(R.id.reason_cancle);
                        TextView ok = (TextView) dialog.findViewById(R.id.reason_ok);

                        final OrderSingleReasonAdp adp = new OrderSingleReasonAdp(context);
                        reasonList.setAdapter(adp);
                        adp.setData(result);
                        adp.setCurrent_select(0);

                        cancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                refund(orderId,adp.getSelectId());
                            }
                        });

                        reasonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                adp.setCurrent_select(position);
                            }
                        });

                        dialog.show();

                    }else {
                        MyApplication.getInstance().ShowToast("获取退单原因失败");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    dialogF.disLoading();
                }
            });


        }catch (PosServiceException e) {
            e.printStackTrace();
        }

    }

    /**
     * 退款
     * @param orderId 订单id
     * @param reasonId 退单原因id
     */
    private void refund(String orderId,int reasonId){
        try {
            final ProgressDialogF dialogF = new ProgressDialogF(context);
            dialogF.showLoading("");
            UserData userData = UserData.getInstance(context);
            TradeService tradeService = TradeService.getInstance();
            Order order = new Order();
            order.setId(Long.valueOf(orderId));
            tradeService.refund(order,reasonId, userData.getRealName(),new ResultCallback<PosResponse>() {
                @Override
                public void onResult(PosResponse result) {
                    dialogF.disLoading();
                    if(result.isSuccessful()){//退款成功
                        MyApplication.getInstance().ShowToast("退款成功");
                        refrushLisener.refrush();
                    }else {//退款失败
                        MyApplication.getInstance().ShowToast(result.getErrmsg());
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    dialogF.disLoading();
                }
            });
        }catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    public interface RefrushLisener{
        public void refrush();
    }


}
