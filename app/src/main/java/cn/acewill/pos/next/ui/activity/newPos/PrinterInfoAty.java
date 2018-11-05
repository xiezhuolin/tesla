package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.PrinterDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.printer.Alignment;
import cn.acewill.pos.next.printer.PrinterFactory;
import cn.acewill.pos.next.printer.PrinterInterface;
import cn.acewill.pos.next.printer.PrinterLinkType;
import cn.acewill.pos.next.printer.PrinterOutputType;
import cn.acewill.pos.next.printer.PrinterVendor;
import cn.acewill.pos.next.printer.PrinterVendors;
import cn.acewill.pos.next.printer.PrinterWidth;
import cn.acewill.pos.next.printer.Separator;
import cn.acewill.pos.next.printer.TextRow;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.adapter.PrinterVendorAdp;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

import static cn.acewill.pos.R.id.paper_wide;
import static cn.acewill.pos.next.utils.TimeUtil.getTimeStr;

/**
 * 添加删除打印机
 * Created by DHH on 2016/6/12.
 */
public class PrinterInfoAty extends BaseActivity {
    @BindView( R.id.printer_name )
    EditText printerName;
    @BindView( R.id.print_reduce )
    ImageView printReduce;
    @BindView( R.id.print_count )
    TextView printerCount;
    @BindView( R.id.print_plus )
    ImageView printPlus;
    @BindView( R.id.printer_ip )
    EditText printerIp;
    @BindView( R.id.print_test )
    TextView printTest;
    @BindView( R.id.print_lan )
    RadioButton printLan;
    @BindView( R.id.print_bluetooth )
    RadioButton printBluetooth;
    @BindView( R.id.interface_rg )
    RadioGroup interfaceRg;
    @BindView( R.id.print_common )
    RadioButton printCommon;
    @BindView( R.id.print_label )
    RadioButton printLabel;
    @BindView( R.id.style_rg )
    RadioGroup styleRg;
    @BindView( paper_wide )
    EditText paperWide;
    @BindView( R.id.lin_width )
    LinearLayout linWidth;
    @BindView( R.id.paper_heigh )
    EditText paperHeigh;
    @BindView( R.id.lin_height )
    LinearLayout linHeight;
    @BindView( R.id.lv_printerVendor )
    ScrolListView lvPrinterVendor;
    @BindView( R.id.delete_print )
    TextView deletePrint;

    private Printer printer;
    private int printCount;
    private PrinterVendorAdp printerVendorAdp;
    private PrinterVendors selectPrinterVendors = null;
    private PrinterLinkType linkType = PrinterLinkType.NETWORK;//连接类型， 网络，蓝牙，或者USB
    private PrinterOutputType outputType = PrinterOutputType.REGULAR;//打印的是标签， 还是普通的纸， 标签和普通打印的指令完全不一样
    String printName = "";
    String printCountStr = "";
    String printIp = "";
    String width = "";
    String height = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_print_info);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrinterVendor();
    }

    private void initData() {
        printerVendorAdp = new PrinterVendorAdp(context);
        lvPrinterVendor.setAdapter(printerVendorAdp);
        lvPrinterVendor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PrinterVendors printerVendors = (PrinterVendors) printerVendorAdp.getItem(position);
                if (printerVendors != null) {
                    printerVendorAdp.setCurrent_select(position);
                    selectPrinterVendors = printerVendors;
                }
            }
        });

        //打印机接口类型  默认-网口    1-蓝牙
        interfaceRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == printLan.getId())
                {
                    linkType = PrinterLinkType.NETWORK;
                }
                else if(checkedId == printBluetooth.getId())
                {
                    linkType = PrinterLinkType.BLUETOOTH;
                }
            }
        });
        //打印机类型
        styleRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == printCommon.getId())
                {
                    outputType = PrinterOutputType.REGULAR;
                    linWidth.setVisibility(View.VISIBLE);
                    linHeight.setVisibility(View.GONE);
                }
                else if(checkedId == printLabel.getId())
                {
                    outputType = PrinterOutputType.LABEL;
                    linWidth.setVisibility(View.GONE);
                    linHeight.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        myApplication.addPage(PrinterInfoAty.this);
        setTitle(ToolsUtils.returnXMLStr("printer"));
        setShowBtnBack(true);
        setRightText(ToolsUtils.returnXMLStr("setting_save"));
        printer = (Printer) getIntent().getSerializableExtra("printer");
        if (printer == null) {
            deletePrint.setText(ToolsUtils.returnXMLStr("setting_save"));
        } else {
            deletePrint.setText(ToolsUtils.returnXMLStr("delete"));
            printerName.setText(printer.getDescription());
            printerIp.setText(printer.getIp());
            if(PrinterLinkType.NETWORK == printer.getLinkType())
            {
                linkType = PrinterLinkType.NETWORK;
                printLan.setChecked(true);
            }
            else {
                linkType = PrinterLinkType.BLUETOOTH;
                printBluetooth.setChecked(true);
            }
            if(PrinterOutputType.REGULAR == printer.getOutputType())
            {
                outputType = PrinterOutputType.REGULAR;
                printLan.setChecked(true);
                linWidth.setVisibility(View.VISIBLE);
                linHeight.setVisibility(View.GONE);
//                paperWide.setText(printer.getWidth().getDotNumber()+"");
            }
            else {
                outputType = PrinterOutputType.LABEL;
                printBluetooth.setChecked(true);
                linWidth.setVisibility(View.GONE);
                linHeight.setVisibility(View.VISIBLE);
                paperHeigh.setText(printer.getLabelHeight()+"");
            }
        }
    }

    private void setPrinterVendorList() {
        printerVendorAdp.setData(PrinterDataController.getPrinterVendorsList());
    }


    private void getPrinterVendor() {
        if (PrinterDataController.getPrinterVendorsList() != null && PrinterDataController.getPrinterVendorsList().size() > 0) {
            setPrinterVendorList();
        } else {
            try {
                SystemService systemService = SystemService.getInstance();
                systemService.getPrinterVendors(new ResultCallback<List<PrinterVendors>>() {
                    @Override
                    public void onResult(List<PrinterVendors> result) {
                        if (result != null && result.size() > 0) {
                            PrinterDataController.setPrinterVendorsList(result);
                            setPrinterVendorList();
                        }
                        Log.i("打印机列表", ToolsUtils.getPrinterSth(result));
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        Log.i("打印机列表获取失败,", e.getMessage());
                    }
                });
            } catch (PosServiceException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick( {R.id.tv_login, R.id.print_reduce, R.id.print_plus,R.id.delete_print,R.id.print_test} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                savePrinterInfo();
                break;
            case R.id.print_reduce:
                --printCount;
                if(printCount <= 0)
                {
                    printCount = 1;
                }
                printerCount.setText(printCount+"");
                break;
            case R.id.print_plus:
                printerCount.setText(++printCount+"");
                break;
            case R.id.delete_print:
                if(deletePrint.getText().equals("保存"))
                {
                    savePrinterInfo();
                }
                else{
                    if(printer != null)
                    {
                        DialogUtil.ordinaryDialog(context, "删除打印机", "是否要删除"+printer.getDescription()+"打印机？", new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                deletePrinter(printer);
                            }

                            @Override
                            public void onCancle() {

                            }
                        });
                    }
                }
                break;
            case R.id.print_test:
                String printIp = printerIp.getText().toString().trim();
                if(!TextUtils.isEmpty(printIp))
                {
                    Printer testPrinter = new Printer();
                    testPrinter.setVendor("unknown");
                    testPrinter.setIp(printIp);
                    testPrinter.setWidth(PrinterWidth.WIDTH_80MM);
                    testPrinter.setDeviceName("测试打印机");
                    testPrintReceipts(testPrinter);
                }
                else{
                    showToast("请输入打印机IP");
                }
                break;
        }
    }

    private void savePrinterInfo() {
        printName = printerName.getText().toString().trim();
        printCountStr = printerCount.getText().toString().trim();
        printIp = printerIp.getText().toString().trim();
        width = "";
        height = "";
        if(TextUtils.isEmpty(printName))
        {
            showToast("请输入打印机名称");
            return ;
        }
        if(TextUtils.isEmpty(printIp))
        {
            showToast("请输入打印机IP地址");
            return ;
        }
        if(!ToolsUtils.isIP(printIp))
        {
            showToast("请输入正确的打印机IP地址");
            return ;
        }
        if(selectPrinterVendors  == null)
        {
            showToast("请选择打印机厂商");
            return ;
        }
        if(linWidth.getVisibility() == View.VISIBLE)
        {
            width = paperWide.getText().toString().trim();
            height = "0";
            if(TextUtils.isEmpty(width))
            {
                showToast("请输入打印机纸张宽度");
                return ;
            }
        }
        else if(linHeight.getVisibility() == View.VISIBLE)
        {
            width = "";
            height = paperHeigh.getText().toString().trim();
            if(TextUtils.isEmpty(height))
            {
                showToast("请输入标签纸高度");
                return ;
            }
        }
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.addPrinter(selectPrinterVendors.getValue(), printIp, printName, linkType.getType()+"", outputType.getType()+"", width, height, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if((int)result == 0)
                    {
                        showToast("添加成功!");
                        PrinterInfoAty.this.finish();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("添加打印机失败,", e.getMessage());
                    showToast("添加打印机失败,"+e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void deletePrinter(final Printer printer)
    {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.deletePrinter(printer, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if((int)result == 0)
                    {
                        showToast("删除成功!");
                        PrinterDataController.removePrinter(printer);
                        PrinterInfoAty.this.finish();

                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("删除打印机失败,", e.getMessage());
                    showToast("删除打印机失败,"+e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试打印机
     * @param printerTest
     */
    private void testPrintReceipts(Printer printerTest)
    {
        try {
            PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()),printerTest.getIp(), printerTest.getWidth());
            printerInterface.init();

            printerInterface.printRow(new Separator("-"));
            TextRow row = createRow(false, 2, "打印机测试");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 1, "打印机名称 : "+printerTest.getDeviceName());
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);

            row = createRow(false, 1, "打印机IP : "+printerTest.getIp());
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);

            row = createRow(false, 1, "测试日期 : "+getTimeStr(System.currentTimeMillis()));
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);
            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.getInstance().ShowToast("打印测试小票失败,请检查打印机设置!");
        }
    }

    private TextRow createRow(boolean bold, int size, String content) {
        TextRow title = new TextRow(content);
        title.setScaleWidth(size);
        title.setScaleHeight(size);
        title.setBoldFont(bold);
        return title;
    }

}
