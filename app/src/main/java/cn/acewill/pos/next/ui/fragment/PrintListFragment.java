package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.interfices.BtnOnClickListener;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.service.PrintManager;
import cn.acewill.pos.next.ui.adapter.PrintListAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 获取打印机列表
 * Created by aqw on 2016/12/12.
 */
public class PrintListFragment extends BaseFragment implements BtnOnClickListener{
    @BindView( R.id.lv_print )
    ListView lvPrint;

    private PrintListAdp printListAdp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_print_list, container, false);
        ButterKnife.bind(this, view);
        loadData();
        return view;
    }

    private void loadData() {
        printListAdp = new PrintListAdp(aty,this);
        if(StoreInfor.printerList != null && StoreInfor.printerList.size() >0)
        {
            printListAdp.setData(StoreInfor.printerList);
            lvPrint.setAdapter(printListAdp);
        }
        else
        {
            showToast(ToolsUtils.returnXMLStr("not_find_printer"));
        }
    }

    @Override
    public void onClick(int type, int position) {
        switch (type) {
            case Constant.DialogStyle.TEST_PRINTER:
                final Printer printerTest = (Printer) printListAdp.getItem(position);
                if (printerTest != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //是正常的ip地址
                            if(ToolsUtils.isIP(printerTest.getIp()))
                            {
                                PrintManager.getInstance().printTextStr(printerTest);
                            }
                            //是串口打印机
                            else
                            {
                                PrintManager.getInstance().printUsbTextStr(printerTest);
                            }
                        }
                    }).start();
                }
                break;
        }
    }
}
