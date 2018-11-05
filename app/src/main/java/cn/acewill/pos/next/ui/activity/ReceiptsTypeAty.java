package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.ReceiptsDataController;
import cn.acewill.pos.next.model.Receipt;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.ui.adapter.ReceiptAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/8/23.
 */
public class ReceiptsTypeAty extends BaseActivity {
    @BindView( R.id.lv_receipts )
    ListView lvReceipts;
    @BindView( R.id.print_cancle )
    TextView printCancle;
    @BindView( R.id.print_ok )
    TextView printOk;

    private List<Receipt> receiptList;
    private List<Receipt> selectReceiptList = new ArrayList<Receipt>();
    private HashMap<Integer, Receipt> selectReceiptMap;
    private ReceiptAdp receiptAdp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_receipts);
        ButterKnife.bind(this);
        receiptList = ReceiptsDataController.receiptList;
        selectReceiptMap = ReceiptsDataController.selectReceiptMap;
        Iterator iter = selectReceiptMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            selectReceiptList.add(selectReceiptMap.get(key));
        }
        receiptAdp = new ReceiptAdp(context);
        receiptAdp.setData(receiptList);
        receiptAdp.setReceipt(selectReceiptList);
        lvReceipts.setAdapter(receiptAdp);
        lvReceipts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                              @Override
                                              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                  Receipt receipt = (Receipt) receiptAdp.getItem(position);
                                                  if (receipt != null) {
                                                      if (selectReceiptList.size() == 0)
                                                      {
                                                          selectReceiptList.add(receipt);
                                                      }
                                                      else
                                                      {
                                                          if(isHaveReceipt(receipt))
                                                          {
                                                              selectReceiptList.remove(receipt);
                                                          }
                                                          else
                                                          {
                                                              selectReceiptList.add(receipt);
                                                          }
                                                      }
                                                      receiptAdp.setReceipt(selectReceiptList);
                                                      receiptAdp.notifyDataSetChanged();
//                                                      if (selectReceiptMap.size() == 0) {
//                                                          selectReceiptMap.put(receipt.getId(), receipt);
//                                                      } else {
//                                                          if (ReceiptsDataController.isHaveReceipt(receipt)) {
//                                                              selectReceiptMap.remove(receipt.getId());
//                                                          } else {
//                                                              selectReceiptMap.put(receipt.getId(), receipt);
//                                                          }
//                                                          receiptAdp.notifyDataSetChanged();
//                                                      }
//                                                      ReceiptsDataController.setSelectReceiptMap(selectReceiptMap);
//                                                      EventBus.getDefault().post(new PosEvent(Constant.EventState.DIALOG_RECEIPT_CALLBACK));
//                                                      finish();
                                                  }
                                              }
                                          }
        );
    }

    @OnClick( {R.id.print_cancle, R.id.print_ok} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.print_cancle:
                finish();
                break;
            case R.id.print_ok:
                setReceipt();
                finish();
                break;
        }
    }

    private void setReceipt()
    {
        if(!ToolsUtils.isList(selectReceiptList))
        {
            int size = selectReceiptList.size();
            for (int i = 0; i < size; i++) {
                Receipt receipt = selectReceiptList.get(i);
                selectReceiptMap.put(receipt.getId(), receipt);
            }
            ReceiptsDataController.setSelectReceiptMap(selectReceiptMap);
            EventBus.getDefault().post(new PosEvent(Constant.EventState.DIALOG_RECEIPT_CALLBACK));
        }


    }


    private boolean isHaveReceipt(Receipt receipt)
    {
        int size = selectReceiptList.size();
        for (int i = 0; i < size; i++) {
            Receipt selectReceipt = selectReceiptList.get(i);
            if(selectReceipt.getId() == receipt.getId())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(selectReceiptList != null && selectReceiptList.size() >0)
        {
            selectReceiptList.clear();;
        }
    }
}
