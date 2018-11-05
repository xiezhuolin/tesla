package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.model.StandByCash;

/**
 * 选择支付方式
 * Created by aqw on 2016/8/19.
 */
public class StandByCashPayTypeAdp extends RecyclerView.Adapter {

    private int current_select = -10;
    private int current_selectEatStyle = -1;

    private Context context;
    public Resources res;
    private LayoutInflater mInflater;
    private List<StandByCash> mDatas = new ArrayList<>();
    private OnItemClickLitener onItemClickLitener;

    private int[] standByCashTypeBg = {R.drawable.key_number_gray_selector, R.drawable.key_number_green_selector, R.drawable.key_number_red_selector,
            R.drawable.key_number_blue_selector, R.drawable.key_number_orange_selector
    };

    public StandByCashPayTypeAdp(Context context) {
        this.context = context;
        res = context.getResources();
        mInflater = LayoutInflater.from(context);
    }

    public void setData(List<StandByCash> datas){
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
        View view = mInflater.inflate(R.layout.lv_item_standbycash_paytype, parent, false);
        ItemViewHolder viewHolder = new ItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        StandByCash standByCash = mDatas.get(position);

//        if(payMent.getId() == -1 ||payMent.getId() == -2||payMent.getId() == -3||payMent.getId() == -4||payMent.getId() == -5)
//        {
//            itemViewHolder.pay_name.setTextColor(res.getColor(R.color.black));
//            if(payMent.getId() == -5)
//            {
//                itemViewHolder.pay_name.setText(payMent.getName());
//            }
//            else{
//                itemViewHolder.pay_name.setText(payMent.getMoneyStr());
//            }
//        }
//        else
//        {
//            itemViewHolder.pay_name.setTextColor(res.getColor(R.color.btn_blue));
//            itemViewHolder.pay_name.setText(payMent.getName());
//        }
        itemViewHolder.tv_standbyCash.setBackgroundResource(standByCashTypeBg[position%5]);
        itemViewHolder.tv_standbyCash.setText(standByCash.getReasonName());
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

    public StandByCash getItem(int position){
        return mDatas.get(position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout pay_type_rl;
        TextView tv_standbyCash;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pay_type_rl = (RelativeLayout) itemView.findViewById(R.id.pay_type_rl);
            tv_standbyCash = (TextView) itemView.findViewById(R.id.tv_standbyCash);
        }
    }

    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }
}
