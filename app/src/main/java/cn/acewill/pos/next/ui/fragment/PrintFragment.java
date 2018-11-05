package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.printer.usb.GpUsbPrinter;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.widget.CommonEditText;

/**
 * 其他配置
 * Created by aqw on 2016/12/12.
 */
public class PrintFragment extends BaseFragment {

    @BindView( R.id.sale_money )
    CommonEditText saleMoney;
    @BindView( R.id.save_btn )
    TextView saveBtn;
    @BindView( R.id.print_height )
    CommonEditText printHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tsc_print, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        //        float money = Store.getInstance(getActivity()).getSaleMoney();
        //        saleMoney.setText(money+"");
        List<String> printStrList = GpUsbPrinter.listGpUsbPrinterList(aty);
        StringBuffer sb = new StringBuffer();
        for (String str : printStrList) {
            sb.append(str + ",");
        }
        saleMoney.setText(sb.toString());

    }

    @OnClick( R.id.save_btn )
    public void onClick() {
        String height = printHeight.getText().toString().trim();
        if(!TextUtils.isEmpty(height))
        {
            PosInfo posInfo = PosInfo.getInstance();
            posInfo.setLabelPrinterHeight(Integer.valueOf(height));
            showToast("保存成功");
        }
        //        String money = saleMoney.getText().toString().trim();
        //        if(TextUtils.isEmpty(money)){
        //            showToast("请输入有效金额");
        //            return;
        //        }
        //        Store.getInstance(getActivity()).setSaleMoney(Float.parseFloat(money));
        //        showToast("保存成功");
    }
}
