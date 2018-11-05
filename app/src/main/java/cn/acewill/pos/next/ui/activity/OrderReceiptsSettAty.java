package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.model.event.PrinterContentEvent;
import cn.acewill.pos.next.model.printer.ModleStyle;
import cn.acewill.pos.next.model.printer.PrinterStyle;
import cn.acewill.pos.next.ui.adapter.DragListAdapter;
import cn.acewill.pos.next.ui.adapter.PrinterModeAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.DragListView;
import cn.acewill.pos.next.widget.SegmentedRadioGroup;

/**
 * Created by DHH on 2016/6/12.
 */
public class OrderReceiptsSettAty extends BaseActivity implements OnCheckedChangeListener {
    @BindView( R.id.grid_item_content )
    GridView gridItemContent;
    @BindView( R.id.button_left )
    RadioButton buttonLeft;
    @BindView( R.id.button_center )
    RadioButton buttonCenter;
    @BindView( R.id.button_right )
    RadioButton buttonRight;
    @BindView( R.id.segment_show_location )
    SegmentedRadioGroup segmentShowLocation;
    @BindView( R.id.button_one )
    RadioButton buttonOne;
    @BindView( R.id.button_two )
    RadioButton buttonTwo;
    @BindView( R.id.button_there )
    RadioButton buttonThere;
    @BindView( R.id.segment_text_size )
    SegmentedRadioGroup segmentTextSize;
    @BindView( R.id.button_nomer )
    RadioButton buttonNomer;
    @BindView( R.id.button_bold )
    RadioButton buttonBold;
    @BindView( R.id.segment_text_style )
    SegmentedRadioGroup segmentTextStyle;
    @BindView( R.id.lin_left )
    LinearLayout linLeft;
    @BindView( R.id.other_drag_list )
    DragListView otherDragList;
    @BindView( R.id.tv_testPrint )
    Button tvTestPrint;

    private String IP_ADDRESS = "192.168.1.186";

    private String[] moduleNameArr = {"订单号", "时间", "菜品内容", "小计", "电话", "地址"};
    private int[] modeStyleArr = {ModleStyle.MODE_STORE_NAME, ModleStyle.MODE_DATA_TIME, ModleStyle.MODE_ORDER_DISH, ModleStyle.MODE_TOTAL_SUM, ModleStyle.MODE_PHONE_NUMBER, ModleStyle.MODE_ADDRESS};
    private List<PrinterStyle> printerModeList = new ArrayList<PrinterStyle>();
    private List<PrinterStyle> modifyPrinterModeList = new ArrayList<PrinterStyle>();
    private PrinterModeAdp printerModeAdp;
    private DragListAdapter dragListAdapter;
    private PrinterStyle printStyle;
    private int selectPosition = -1;

    private Runnable sendable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_order_receipts);
        myApplication.addPage(OrderReceiptsSettAty.this);
        EventBus.getDefault().register(this);
        setTitle(ToolsUtils.returnXMLStr("ticket_format"));
        ButterKnife.bind(this);
        loadData();

        sendable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                print();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void print() {
//        PrintUtil.printerTest(PrinterVendor.EPSON, IP_ADDRESS, modifyPrinterModeList,PrinterWidth.WIDTH_80MM);
    }

    @OnClick( {R.id.tv_testPrint} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_testPrint:
                new Thread(sendable).start();
                break;

        }
    }

    private PrinterStyle addPrinterMode(PrinterStyle printMode, String modeName, int modeStyle) {
        printMode.moduleName = modeName;
        printMode.modeStyle = modeStyle;
        printMode.enableStatus = PrinterStyle.EnableStatus.SELECTED;
        printMode.showLocation = PrinterStyle.ShowLocation.Left;
        printMode.textStyle = PrinterStyle.TextStyle.Normal;
        printMode.textSize = PrinterStyle.TextSize.NormalSize;
        return printMode;
    }

    private List<PrinterStyle> addStoreInfo(List<PrinterStyle> printerList) {
        ArrayList<PrinterStyle> PrinterStyleList = new ArrayList<PrinterStyle>();
        for (int i = 0; i < printerList.size(); i++) {
            PrinterStyle printerStyle = printerList.get(i);
            if (printerStyle != null) {
                if (printerStyle.enableStatus == PrinterStyle.EnableStatus.SELECTED) {
                    PrinterStyleList.add(printerStyle);
                }
            }
        }
        return PrinterStyleList;
    }

    private void loadData() {
        segmentShowLocation.setOnCheckedChangeListener(this);
        segmentTextSize.setOnCheckedChangeListener(this);
        segmentTextStyle.setOnCheckedChangeListener(this);

        for (int i = 0; i < moduleNameArr.length; i++) {
            PrinterStyle printMode = new PrinterStyle();
            printerModeList.add(addPrinterMode(printMode, moduleNameArr[i], modeStyleArr[i]));
        }
        printerModeAdp = new PrinterModeAdp(this);
        printerModeAdp.setData(printerModeList);
        gridItemContent.setAdapter(printerModeAdp);

        gridItemContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (printerModeList != null && printerModeList.size() > 0) {
                    PrinterStyle content = printerModeList.get(position);
                    if (content != null) {
                        if (content.enableStatus == PrinterStyle.EnableStatus.UNSELECTED) {
                            content.enableStatus = PrinterStyle.EnableStatus.SELECTED;
                        } else {
                            content.enableStatus = PrinterStyle.EnableStatus.UNSELECTED;
                        }
                    }
                    modifyPrinterModeList = addStoreInfo(printerModeList);
                    dragListAdapter = new DragListAdapter(OrderReceiptsSettAty.this, modifyPrinterModeList);
                    otherDragList.setAdapter(dragListAdapter);

                    printerModeAdp.notifyDataSetChanged();
                }
            }
        });
        modifyPrinterModeList = addStoreInfo(printerModeList);
        dragListAdapter = new DragListAdapter(this, modifyPrinterModeList);
        otherDragList.setAdapter(dragListAdapter);

        otherDragList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                printStyle = (PrinterStyle) dragListAdapter.getItem(position);
                selectPosition = position;
                dragListAdapter.setSelectPosition(selectPosition);
                if (printStyle != null) {
                    //设置对齐方式
                    if (printStyle.showLocation == PrinterStyle.ShowLocation.Left) {
                        buttonLeft.setChecked(true);
                    } else if (printStyle.showLocation == PrinterStyle.ShowLocation.Center) {
                        buttonCenter.setChecked(true);
                    } else if (printStyle.showLocation == PrinterStyle.ShowLocation.Right) {
                        buttonRight.setChecked(true);
                    }
                    //设置字体大小
                    if (printStyle.textSize == PrinterStyle.TextSize.NormalSize) {
                        buttonOne.setChecked(true);
                    } else if (printStyle.textSize == PrinterStyle.TextSize.TwoXSize) {
                        buttonTwo.setChecked(true);
                    } else if (printStyle.textSize == PrinterStyle.TextSize.ThereXSize) {
                        buttonThere.setChecked(true);
                    }
                    //设置字体样式
                    if (printStyle.textStyle == PrinterStyle.TextStyle.Normal) {
                        buttonNomer.setChecked(true);
                    } else if (printStyle.textStyle == PrinterStyle.TextStyle.Bold) {
                        buttonBold.setChecked(true);
                    }
                }
                dragListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe
    private void AddPrinterContent(PrinterContentEvent event) {
        switch (event.getAction()) {
            case Constant.PrinterContentStyle.ADD_PRINTER_CONTENT:
                printerModeList.add(addPrinterMode(new PrinterStyle(), event.getContent(), ModleStyle.MODE_MYSELF_ADD));
                printerModeAdp.setData(printerModeList);

                modifyPrinterModeList = addStoreInfo(printerModeList);
                dragListAdapter = new DragListAdapter(OrderReceiptsSettAty.this, modifyPrinterModeList);
                otherDragList.setAdapter(dragListAdapter);

                printerModeAdp.notifyDataSetChanged();
                dragListAdapter.notifyDataSetChanged();
                break;
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        // TODO Auto-generated method stub
        //对齐方式radio监听
        if (group == segmentShowLocation) {
            if (selectPosition == -1) {
                System.out.println("请先选择一项");
                return;
            } else {
                PrinterStyle contentStyle = ((PrinterStyle) dragListAdapter.getItem(selectPosition));
                if (contentStyle != null) {
                    if (checkedId == R.id.button_left) {
                        contentStyle.showLocation = PrinterStyle.ShowLocation.Left;
                    } else if (checkedId == R.id.button_center) {
                        contentStyle.showLocation = PrinterStyle.ShowLocation.Center;
                    } else if (checkedId == R.id.button_right) {
                        contentStyle.showLocation = PrinterStyle.ShowLocation.Right;
                    }
                    dragListAdapter.notifyDataSetChanged();
                }
            }
        }
        //字体大小radio监听
        if (group == segmentTextSize) {
            if (selectPosition == -1) {
                System.out.println("请先选择一项");
                return;
            } else {
                PrinterStyle contentStyle = ((PrinterStyle) dragListAdapter.getItem(selectPosition));
                if (contentStyle != null) {
                    if (checkedId == R.id.button_one) {
                        contentStyle.textSize = PrinterStyle.TextSize.NormalSize;
                    } else if (checkedId == R.id.button_two) {
                        contentStyle.textSize = PrinterStyle.TextSize.TwoXSize;
                    } else if (checkedId == R.id.button_there) {
                        contentStyle.textSize = PrinterStyle.TextSize.ThereXSize;
                    }
                    dragListAdapter.notifyDataSetChanged();
                }
            }
        }
        //字体样式radio监听
        if (group == segmentTextStyle) {
            if (selectPosition == -1) {
                System.out.println("请先选择一项");
                return;
            } else {
                PrinterStyle contentStyle = ((PrinterStyle) dragListAdapter.getItem(selectPosition));
                if (contentStyle != null) {
                    if (checkedId == R.id.button_nomer) {
                        contentStyle.textStyle = PrinterStyle.TextStyle.Normal;
                    } else if (checkedId == R.id.button_bold) {
                        contentStyle.textStyle = PrinterStyle.TextStyle.Bold;
                    }
                    dragListAdapter.notifyDataSetChanged();
                }
            }
        }

    }

}
