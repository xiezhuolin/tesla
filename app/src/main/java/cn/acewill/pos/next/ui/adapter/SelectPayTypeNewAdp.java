package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.widget.AutoFitTextView;

/**
 * 选择支付方式
 * Created by aqw on 2016/8/19.
 */
public class SelectPayTypeNewAdp extends RecyclerView.Adapter {

    private int current_select = -10;
    private int current_selectEatStyle = -1;

    private Context context;
    public Resources res;
    private LayoutInflater mInflater;
    private List<Payment> mDatas = new ArrayList<>();
    private OnItemClickLitener onItemClickLitener;

    public SelectPayTypeNewAdp(Context context) {
        this.context = context;
        res = context.getResources();
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<Payment> datas){
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickLitener onItemClickLitener){
        this.onItemClickLitener = onItemClickLitener;
    }

    public void setCurrent_select(int positiion) {
        if (current_select == positiion) {
            return;
        }

        this.current_select = positiion;
        this.notifyDataSetChanged();
    }

    public void setCurrent_selectEatStyle(int positiion) {
        if (current_selectEatStyle == positiion) {
            return;
        }

        this.current_selectEatStyle = positiion;
        this.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.lv_item_select_paytype_new, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        Payment payMent = mDatas.get(position);
        if(payMent.getId() == -1 ||payMent.getId() == -2||payMent.getId() == -3||payMent.getId() == -4||payMent.getId() == -5)
        {
//            itemViewHolder.pay_name.setTextColor(res.getColor(R.color.black));
            if(payMent.getId() == -5)
            {
                itemViewHolder.pay_name.setText(payMent.getName());
            }
            else{
                itemViewHolder.pay_name.setText(payMent.getMoneyStr());
            }
        }
        else
        {
//            itemViewHolder.pay_name.setTextColor(res.getColor(R.color.btn_blue));
            itemViewHolder.pay_name.setText(payMent.getName());
        }

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
        AutoFitTextView pay_name;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pay_type_rl = (RelativeLayout) itemView.findViewById(R.id.pay_type_rl);
            pay_name = (AutoFitTextView) itemView.findViewById(R.id.pay_name);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }
}
