package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.payment.Payment;

/**
 * 选择支付方式
 * Created by aqw on 2016/8/19.
 */
public class SelectPayTypeAdp extends RecyclerView.Adapter {

    private int current_select = 0;

    private Context context;
    private LayoutInflater mInflater;
    private List<Payment> mDatas = new ArrayList<>();
    private OnItemClickLitener onItemClickLitener;

    public SelectPayTypeAdp(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Payment> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickLitener onItemClickLitener){
        this.onItemClickLitener = onItemClickLitener;
    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder = null;
//        final Payment payment = (Payment) getItem(position);
//        if (convertView == null) {
//            holder = new ViewHolder();
//            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_select_paytype, null);
//            holder.pay_type_rl = (RelativeLayout)convertView.findViewById(R.id.pay_type_rl);
//            holder.pay_name = (TextView)convertView.findViewById(R.id.pay_name);
//            holder.pay_select_icon = (ImageView)convertView.findViewById(R.id.pay_select_icon);
//
//            convertView.setTag(holder);
//        }
//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        if(current_select==position){
//            holder.pay_name.setSelected(true);
//            holder.pay_select_icon.setSelected(true);
//        }else {
//            holder.pay_name.setSelected(false);
//            holder.pay_select_icon.setSelected(false);
//        }
//        holder.pay_name.setText(payment.getName());
//
//
//        return convertView;
//    }
//
//    class ViewHolder {
//        RelativeLayout pay_type_rl;
//        TextView pay_name;
//        ImageView pay_select_icon;
//    }

    public void setCurrent_select(int positiion) {
        if (current_select == positiion) {
            return;
        }

        this.current_select = positiion;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.lv_item_select_paytype, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        if (current_select == position) {
            itemViewHolder.pay_name.setSelected(true);
            itemViewHolder.pay_select_icon.setSelected(true);
        } else {
            itemViewHolder.pay_name.setSelected(false);
            itemViewHolder.pay_select_icon.setSelected(false);
        }
        itemViewHolder.pay_name.setText(mDatas.get(position).getName());

        if(onItemClickLitener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLitener.onItemClick(holder.itemView,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mDatas!=null?mDatas.size():0;
    }

    public Payment getItem(int position){
        return mDatas.get(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout pay_type_rl;
        TextView pay_name;
        ImageView pay_select_icon;

        public ItemViewHolder(View itemView) {
            super(itemView);

            pay_type_rl = (RelativeLayout) itemView.findViewById(R.id.pay_type_rl);
            pay_name = (TextView) itemView.findViewById(R.id.pay_name);
            pay_select_icon = (ImageView) itemView.findViewById(R.id.pay_select_icon);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }
}
