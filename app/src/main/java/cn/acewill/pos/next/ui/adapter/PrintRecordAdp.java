package cn.acewill.pos.next.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.model.KitchenStall;
import cn.acewill.pos.next.printer.PrintRecord;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.TimeUtil;

/**
 * Created by DHH on 2016/6/17.
 */
public class PrintRecordAdp<T> extends BaseAdapter{
    private List<PrintRecord> printRecordList;
    private PosInfo posInfo;
    public PrintRecordAdp(Context context){
        super(context);
        printRecordList = PrinterDataController.getPrintRecordList();
        posInfo = PosInfo.getInstance();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final PrintRecord printRecord = (PrintRecord) getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_device_record, null);
            holder.tv_orderId = (TextView) convertView.findViewById(R.id.tv_orderId);
            holder.tv_receiptsType = (TextView) convertView.findViewById(R.id.tv_receiptsType);
            holder.tv_wating = (TextView) convertView.findViewById(R.id.tv_wating);
            holder.tv_associatedInfo = (TextView) convertView.findViewById(R.id.tv_associatedInfo);
            holder.tv_print_time = (TextView) convertView.findViewById(R.id.tv_print_time);
            holder.img_state = (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String orderId = "";
        if(printRecord.getOrder() != null && printRecord.getOrder().getId() >0)
        {
            orderId = printRecord.getOrder().getId()+"";
        }
        else{
            orderId = printRecord.getOrderId()+"";
        }
        String orderTypeStr = "";
        if(printRecord.getOrderPrintType() == Constant.EventState.PRINTER_ORDER)
        {
            orderTypeStr = "客用结账小票";
        }
        else if(printRecord.getOrderPrintType() == Constant.EventState.PRINT_CHECKOUT)
        {
            orderTypeStr = "结账小票";
        }
        else if(printRecord.getOrderPrintType() == Constant.EventState.PRINTER_KITCHEN_ORDER)
        {
            orderTypeStr = "厨房分单小票";
        }
        else if(printRecord.getOrderPrintType() == Constant.EventState.PRINTER_KITCHEN_SUMMARY_ORDER)
        {
            orderTypeStr = "厨房总单小票";
        }

        holder.tv_orderId.setText("  "+orderId);
        holder.tv_receiptsType.setText(orderTypeStr);
        holder.tv_print_time.setText(TimeUtil.getStringTimeLong(printRecord.getPrintTime()));
        boolean isState = false;
        //客用单
        if(printRecord.getOrderPrintType() == Constant.EventState.PRINTER_ORDER)
        {
            if(posInfo.getGuestReceiptCounts() == printRecord.getGuestReceiptNeedCounts())
            {
                isState = true;
                printRecord.setPrint(isState);
            }
        }
        //结账单
        else if(printRecord.getOrderPrintType() == Constant.EventState.PRINT_CHECKOUT)
        {
            if(posInfo.getCheckoutReceiptCounts() == printRecord.getCheckoutReceiptNeedCounts())
            {
                isState = true;
                printRecord.setPrint(isState);
            }
        }
        if(isState)
        {
            holder.img_state.setVisibility(View.VISIBLE);
            holder.tv_wating.setVisibility(View.GONE);
            holder.img_state.setImageResource(R.mipmap.img_state_ok);
        }
        else if(!isState){
            holder.img_state.setVisibility(View.VISIBLE);
            holder.tv_wating.setVisibility(View.GONE);
            holder.img_state.setImageResource(R.mipmap.img_state_err);
        }
        return convertView;
    }

    private String getPrintForKitStall(List<KitchenStall> kitchenStallList)
    {
        StringBuffer sb = new StringBuffer();
        if(kitchenStallList != null && kitchenStallList.size() >0)
        {
            for (KitchenStall kitchenStall:kitchenStallList)
            {
                sb.append(kitchenStall.getStallsName()+";");
            }
        }
        return sb.toString();
    }
    class ViewHolder {
        TextView tv_orderId;
        TextView tv_receiptsType;
        TextView tv_wating;
        TextView tv_print_time;
        ImageView img_state;
        TextView tv_associatedInfo;
    }

}
