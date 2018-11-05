package cn.acewill.pos.next.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.event.PrinterContentEvent;
import cn.acewill.pos.next.model.printer.PrinterStyle;
import cn.acewill.pos.next.utils.Constant;


/**
 * Created by DHH on 2016/6/17.
 */
public class PrinterModeAdp<T> extends BaseAdapter {
    Dialog dialog;
    EditText et;
    /**
     * 取消
     */
    Button positiveButton;
    /**
     * 确定
     */
    Button negativeButton;
    public PrinterModeAdp(Context context) {
        super(context);
        createAddItem();
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size()+1 : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(position==getCount()-1)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_base_content_add_item, null);
            TextView tv_add = (TextView) convertView.findViewById(R.id.tv_base_add_content);
            tv_add.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if(dialog != null)
                    {
                        dialog.show();
                    }
                }
            });
        }
        else
        {
            final PrinterStyle printerContent = (PrinterStyle) getItem(position);
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_base_content_item, null);
            TextView tv_base_content = (TextView) convertView.findViewById(R.id.tv_base_content);

            tv_base_content.setText(printerContent.moduleName);
            if(printerContent.enableStatus == PrinterStyle.EnableStatus.UNSELECTED)
            {
                convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border));
            }
            else
            {
                convertView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_blue));
            }
        }

        return convertView;
    }
    private void createAddItem()
    {
        dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setContentView(R.layout.dialog_add_print_content);
        et = (EditText) dialog.findViewById(R.id.tableall_neworder_peonum);
        positiveButton = (Button) dialog.findViewById(R.id.positiveButton);
        negativeButton = (Button) dialog.findViewById(R.id.negativeButton);

        /**
         * 取消
         */
        positiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.cancel();
            }
        });

        /**
         * 确定
         */
        negativeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog.cancel();
                // 点击确定按钮后得到输入的值，保存
                String content = et.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    MyApplication.getInstance().ShowToast("请输入模块内容!");
                    return;
                } else {
                    EventBus.getDefault().post(new PrinterContentEvent(Constant.PrinterContentStyle.ADD_PRINTER_CONTENT,content));
                }
            }
        });
    }

}
