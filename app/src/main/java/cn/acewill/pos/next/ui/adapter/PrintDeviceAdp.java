package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.common.ReceiptsDataController;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/9/1.
 */
public class PrintDeviceAdp extends RecyclerView.Adapter {

    public Context context;
    public List<Printer> dataList ;
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

    public PrintDeviceAdp(Context context, RefrushLisener refrushLisener)
    {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.refrushLisener = refrushLisener;
    }


    public Printer getItem(int position) {
        return dataList != null ? dataList.get(position) !=null? dataList.get(position):null: null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View view = inflater.inflate(R.layout.item_prints,parent,false);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            Printer printer = dataList.get(position);
            itemViewHolder.print_brand.setText(printer.getVendor());
            itemViewHolder.print_ip.setText(printer.getIp());
            itemViewHolder.print_des.setText(printer.getDescription());
            itemViewHolder.print_type.setText(ReceiptsDataController.receiptType2sth(printer));

            itemViewHolder.print_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refrushLisener.onClick(Constant.DialogStyle.DELETE_PRINTER,position);
                    //                MyApplication.getInstance().ShowToast("删除");
                }
            });

            itemViewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refrushLisener.onClick(Constant.DialogStyle.MODIFY_PRINTER,position);
                    //                DialogUtil.printerDialog(context,1,printer);
                }
            });

            itemViewHolder.test.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refrushLisener.onClick(Constant.DialogStyle.TEST_PRINTER,position);
                    //                DialogUtil.printerDialog(context,1,printer);
                }
            });

            holder.itemView.setTag(position);
        }
        else if(holder instanceof FootViewHolder){
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
//        return dataList.size()+1;
        return dataList != null ? dataList.size()+1:0;
    }

    /**
     * 更新数据
     * @param printer
     */
    public void setData(List<Printer> printer){
        if(printer!=null&&printer.size()>0){
            switch (load_type){
                case UP_LOAD_TYPE://上拉加载
                    dataList.addAll(printer);
                    break;
                case DOWN_LOAD_TYPE://下拉更新
                    this.dataList = printer;
                    break;
            }
            this.notifyDataSetChanged();
        }
        else
        {
            this.dataList = new ArrayList<>();
            this.notifyDataSetChanged();
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        TextView print_brand;
        TextView print_ip;
        TextView print_des;
        TextView print_type;
        TextView print_delete;
        TextView edit;
        TextView test;


        public ItemViewHolder(View view){
            super(view);
            print_brand = (TextView) view.findViewById(R.id.print_brand);
            print_ip = (TextView) view.findViewById(R.id.print_ip);
            print_des = (TextView) view.findViewById(R.id.print_des);
            print_type = (TextView) view.findViewById(R.id.print_type);
            print_delete = (TextView)view.findViewById(R.id.print_delete);
            edit = (TextView)view.findViewById(R.id.edit);
            test = (TextView)view.findViewById(R.id.test);

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
        void refrush();
        void onClick(int type, int position);
    }
}
