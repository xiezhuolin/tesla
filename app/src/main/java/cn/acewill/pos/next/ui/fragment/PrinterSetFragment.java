package cn.acewill.pos.next.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.ReceiptsDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.model.Receipt;
import cn.acewill.pos.next.model.ReceiptType;
import cn.acewill.pos.next.printer.Alignment;
import cn.acewill.pos.next.printer.PrinterFactory;
import cn.acewill.pos.next.printer.PrinterInterface;
import cn.acewill.pos.next.printer.PrinterVendor;
import cn.acewill.pos.next.printer.PrinterWidth;
import cn.acewill.pos.next.printer.Separator;
import cn.acewill.pos.next.printer.TextRow;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.PrintManager;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.activity.ReceiptsTypeAty;
import cn.acewill.pos.next.ui.adapter.PrintDeviceAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.OnRecyclerItemClickListener;
import cn.acewill.pos.next.widget.ProgressDialogF;

/**
 * 打印机设置
 * <p/>
 * Created by aqw on 2016/8/20.
 */
public class PrinterSetFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, PrintDeviceAdp.RefrushLisener {
    //    @BindView( R.id.printList )
    //    ListView printList;
    @BindView( R.id.print_add )
    TextView printAdd;
    @BindView( R.id.print_lv)
    RecyclerView printLv;
    @BindView( R.id.print_srl)
    SwipeRefreshLayout printSrl;

    private Dialog receiptDialog;
    private ProgressDialogF progressDialogF;
    private PrintDeviceAdp printDeviceAdp;
    List<Printer> printerList = new ArrayList<Printer>();

    private PrintRunnable printRunnable;

    private int page = 0;
    private int limit = 10;
    private int lastVisibleItem = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_printer, container, false);
        ButterKnife.bind(this, view);
        initView();
        initData();
        return view;
    }

    private void initView() {
        progressDialogF = new ProgressDialogF(aty);
        printSrl.setOnRefreshListener(this);
        printSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        printDeviceAdp = new PrintDeviceAdp(getContext(), this);
        final LinearLayoutManager linearLayoutManager =  new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        printLv.setLayoutManager(linearLayoutManager);

        printLv.setAdapter(printDeviceAdp);

        printLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == printDeviceAdp.getItemCount() && printDeviceAdp.load_more_status == printDeviceAdp.LOAD_MORE && dy > 0) {
                    printDeviceAdp.setLoadType(printDeviceAdp.UP_LOAD_TYPE);
                    printDeviceAdp.changeMoreStatus(printDeviceAdp.LOADING);
                }
            }
        });

        printLv.addOnItemTouchListener(new OnRecyclerItemClickListener(printLv) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {

            }

            @Override
            public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                Printer printer = (Printer) printDeviceAdp.getItem(position);
                if (printer != null) {
                    printerSettDialog(printer);
                }
            }
        });
    }

    private void initData() {
        printRunnable = new  PrintRunnable();
        if(ReceiptsDataController.receiptList.size() < 0)
        {
            getListReceipts();
        }
        getPrinterList();
    }

    /**
     * 操作打印机方法  添加  修改
     *
     * @param type
     * @param printer
     */
    private void operatePrinter(final int type, final Printer printer) {
        printerDialog(getContext(), type, printer, new DialogCallback() {
            @Override
            public void onConfirm() {
                String ip = print_ip.getText().toString().trim();
                String brand = print_brand.getText().toString().trim();
                String des = print_des.getText().toString().trim();
                String name = print_name.getText().toString().trim();
                int id = 0;
                if (printer != null) {
                    id = printer.getId();
                }
                List<ReceiptType> receiptTypeList = ReceiptsDataController.selectMap2ReceiptTypeList();
                if (ToolsUtils.isNull(ip)) {
                    showToast("打印机IP地址不能为空!");
                    return;
                }
                else if (ToolsUtils.isNull(name)) {
                    showToast("打印机名称不能为空!");
                    return;
                }
                else if (ToolsUtils.isNull(brand)) {
                    showToast("打印机品牌不能为空!");
                    return;
                }
//                else if (ToolsUtils.isList(receiptTypeList)) {
//                    showToast("请选择打印类型!");
//                    return;
//                }
                if(!ToolsUtils.isIP(ip))
                {
                    showToast("请填写正确的打印机IP地址!");
                    return;
                }
                try {
                    StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
                    Printer printer = new Printer();
//                    printer.setVendor(brand);
                    printer.setIp(ip);
                    if (id != 0) {
                        printer.setId(id);
                    }
                    printer.setDeviceName(name);
                    printer.setDescription(des);
//                    printer.setReceiptTypeList(receiptTypeList);
                    if (type == Constant.DialogStyle.ADD_PRINTER) {
                        storeBusinessService.addPrinter(printer, new ResultCallback<Printer>() {
                            @Override
                            public void onResult(Printer result) {
                                showToast("添加成功!");
                                PrintManager.getInstance().addPrinter(result);
                                getPrinterList();
                                ReceiptsDataController.cleanSelectReceiptMap();
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(PosServiceException e) {
                                dialog.dismiss();
                                showToast("添加失败!"+e.getMessage());
                            }
                        });
                    } else if (type == Constant.DialogStyle.MODIFY_PRINTER) {
                        storeBusinessService.updatePrinter(printer.getId(), printer, new ResultCallback<Printer>() {
                            @Override
                            public void onResult(Printer result) {
                                PrintManager.getInstance().modifyPrinter(result);
                                getPrinterList();
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(PosServiceException e) {
                                showToast("更新失败!"+e.getMessage());
                                dialog.dismiss();
                            }
                        });
                    }

                } catch (PosServiceException e) {
                    e.printStackTrace();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancle() {

            }
        });
    }

    /**
     * 删除打印机
     *
     * @param printerId
     */
    private void deletePrinter(final Integer printerId) {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.deletePrinter(printerId, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    PrintManager.getInstance().deletePrinter(printerId);
                    getPrinterList();
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("删除失败!" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    /**
     * 列出这个门店的小票类型
     */
    private void getListReceipts() {
        try {
            progressDialogF.showLoading("");
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.listReceipts(new ResultCallback<List<Receipt>>() {
                @Override
                public void onResult(List<Receipt> result) {
                    progressDialogF.disLoading();
                    ReceiptsDataController.receiptList = result;
                    ReceiptsDataController.receiptList2Map(result);
                }

                @Override
                public void onError(PosServiceException e) {
                    progressDialogF.disLoading();
                    showToast("获取小票类型失败!"+e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取打印机列表
     */
    private void getPrinterList() {
        try {
            progressDialogF.showLoading("");
            printDeviceAdp.setLoadType(printDeviceAdp.DOWN_LOAD_TYPE);
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.listPrinters(new ResultCallback<List<Printer>>() {
                @Override
                public void onResult(List<Printer> result) {
                    progressDialogF.disLoading();
                    printerList = result;
                    if (ToolsUtils.isList(printerList)) {
                        showToast("请添加一台打印机!");
                    }
                    if (printerList != null && printerList.size() > 0) {
                        printDeviceAdp.setData(printerList);
                        if (printerList.size() < limit) {
                            printDeviceAdp.changeMoreStatus(printDeviceAdp.NO_MORE);
                        } else {
                            printDeviceAdp.changeMoreStatus(printDeviceAdp.LOAD_MORE);
                        }
                    } else {
                        printDeviceAdp.setData(printerList);
                        printDeviceAdp.changeMoreStatus(printDeviceAdp.NO_MORE);
                    }
                    printSrl.setRefreshing(false);
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("获取打印机列表失败!"+e.getMessage());
                    progressDialogF.disLoading();
                    printSrl.setRefreshing(false);
                }
            });

        } catch (PosServiceException e) {
            e.printStackTrace();
            printSrl.setRefreshing(false);
        }
    }

    /**
     * 打印机弹出框
     *
     * @param context
     * @param source  0:添加打印机；1:修改打印机
     * @param printer  修改时打印机参数
     */
    private Dialog dialog;
    private TextView print_type;
    private EditText print_ip;
    private EditText print_brand;
    private EditText print_des;
    private EditText print_name;

    public Dialog printerDialog(final Context context, final int source, final Printer printer, final DialogCallback dialogCallback) {
        dialog = DialogUtil.getDialog(context, R.layout.dialog_printer, 0.7f, 0.65f);

        TextView print_title = (TextView) dialog.findViewById(R.id.print_title);
        LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
        print_ip = (EditText) dialog.findViewById(R.id.print_ip);
        print_brand = (EditText) dialog.findViewById(R.id.print_brand);
        print_type = (TextView) dialog.findViewById(R.id.print_type);
        print_des = (EditText) dialog.findViewById(R.id.print_des);
        print_name = (EditText) dialog.findViewById(R.id.print_name);
        TextView print_cancle = (TextView) dialog.findViewById(R.id.print_cancle);
        TextView print_ok = (TextView) dialog.findViewById(R.id.print_ok);

        print_close_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCallback.onCancle();
            }
        });
        print_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogCallback.onCancle();
            }
        });
        print_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                dialog.dismiss();
                dialogCallback.onConfirm();
            }
        });
        print_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ToolsUtils.isList(ReceiptsDataController.receiptList)) {
                    Intent intent = new Intent();
                    intent.setClass(context, ReceiptsTypeAty.class);
                    context.startActivity(intent);
                }
            }
        });

        if (source == Constant.DialogStyle.ADD_PRINTER) {
            print_title.setText("添加打印机");

        } else if (source == Constant.DialogStyle.MODIFY_PRINTER) {
            print_title.setText("修改打印机");
            if (printer != null) {
                print_ip.setText(printer.getIp());
                print_name.setText(printer.getDeviceName());
                print_brand.setText(printer.getVendor());
                print_type.setText(ReceiptsDataController.receiptType2sth(printer));
                if (!ToolsUtils.isNull(printer.getDescription())) {
                    print_des.setText(print_des.getText());
                }
            }
        }
        return dialog;
    }

    public void modifyDialog() {
        print_type.setText(ReceiptsDataController.selectMap2Sth());
    }

    @OnClick( R.id.print_add )
    public void onClick() {
        ReceiptsDataController.cleanSelectReceiptMap();
        operatePrinter(Constant.DialogStyle.ADD_PRINTER, null);
    }

    @Override
    public void onClick(int type, final int position) {
        switch (type) {
            case Constant.DialogStyle.DELETE_PRINTER:
                DialogUtil.ordinaryDialog(aty, "删除打印机", "是否删除打印机?", new DialogCallback() {
                    @Override
                    public void onConfirm() {
                        Printer printer = (Printer) printDeviceAdp.getItem(position);
                        if (printer != null) {
                            deletePrinter(printer.getId());
                        }
                    }

                    @Override
                    public void onCancle() {
                    }
                });
                break;
            case Constant.DialogStyle.MODIFY_PRINTER:
                Printer printer = (Printer) printDeviceAdp.getItem(position);
                if (printer != null) {

                    operatePrinter(Constant.DialogStyle.MODIFY_PRINTER, printer);
                }
                break;
            case Constant.DialogStyle.TEST_PRINTER:
                Printer printerTest = (Printer) printDeviceAdp.getItem(position);
                if (printerTest != null) {
                    printRunnable.setPrinter(printerTest);
                    new Thread(printRunnable).start();
                }
                break;
        }
    }

    class  PrintRunnable implements Runnable {
        private Printer printerTest;
        public void setPrinter(Printer printerTest)
        {
            this.printerTest =printerTest;
        }
        @Override
        public void run() {
            testPrintReceipts(printerTest);
        }
    }


    @Override
    public void onRefresh() {
        getPrinterList();
    }

    @Override
    public void refrush() {
        getPrinterList();
    }

    /**
     * 测试打印机
     * @param printerTest
     */
    private void testPrintReceipts(Printer printerTest)
    {
        try {
            PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.EPSON,printerTest.getIp(), PrinterWidth.WIDTH_80MM);
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

    private String getTimeStr(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(new Date(time));
    }

    private Dialog printerSettDialog(final Printer printer)
    {
        LayoutInflater inflater = LayoutInflater.from(aty);
        View view = inflater.inflate(R.layout.dialog_printer_setting, null);
        final Dialog dialog = new Dialog(aty);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setCanceledOnTouchOutside(true);

        TextView print_title = (TextView) dialog.findViewById(R.id.print_title);
        TextView tv_modify = (TextView) dialog.findViewById(R.id.tv_modify);
        TextView tv_delete = (TextView) dialog.findViewById(R.id.tv_delete);
        TextView tv_state = (TextView) dialog.findViewById(R.id.tv_state);
        LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);

        print_title.setText(printer.getVendor());
        print_close_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //修改打印机
        tv_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (printer != null) {
                    operatePrinter(Constant.DialogStyle.MODIFY_PRINTER, printer);
                }
            }
        });
        //删除打印机
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DialogUtil.ordinaryDialog(aty, "删除打印机", "是否删除打印机?", new DialogCallback() {
                    @Override
                    public void onConfirm() {
                        if (printer != null) {
                            deletePrinter(printer.getId());
                        }
                    }

                    @Override
                    public void onCancle() {
                    }
                });
            }
        });
        //启用/禁用打印机
        tv_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showToast("正在开发!");
            }
        });
        dialog.show();
        return dialog;
    }
}
