package cn.acewill.pos.next.service;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.Definition;
import cn.acewill.pos.next.model.KitchenPrintMode;
import cn.acewill.pos.next.model.PaymentList;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.model.ReceiptType;
import cn.acewill.pos.next.model.WorkShiftReport;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.payment.Payment;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.printer.Alignment;
import cn.acewill.pos.next.printer.BitmapRow;
import cn.acewill.pos.next.printer.Column;
import cn.acewill.pos.next.printer.PrinterFactory;
import cn.acewill.pos.next.printer.PrinterInterface;
import cn.acewill.pos.next.printer.PrinterLinkType;
import cn.acewill.pos.next.printer.PrinterOutputType;
import cn.acewill.pos.next.printer.PrinterVendor;
import cn.acewill.pos.next.printer.PrinterWidth;
import cn.acewill.pos.next.printer.Separator;
import cn.acewill.pos.next.printer.Table;
import cn.acewill.pos.next.printer.TextRow;
import cn.acewill.pos.next.printer.gpnetwork.GpEnternetPrint;
import cn.acewill.pos.next.printer.usb.GpUsbPrinter;
import cn.acewill.pos.next.printer.vendor.PreviewPrinter;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.CreateImage;
import cn.acewill.pos.next.utils.PrintUtils;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by Acewill on 2016/8/19.
 */
public class PrintManager {
    //小票类型 和 对应的打印机的映射,  一个打印机也可以打印多种小票
    private Map<Integer, Printer> receipt2PrintersMap = new HashMap<Integer, Printer>();
    private static PrintManager instance;

    private Context context;
    private List<Printer> printerList = new CopyOnWriteArrayList<Printer>();
    private static List<Printer> selectPrinter = new ArrayList<Printer>();
    private static Store store;
    private GpUsbPrinter usbPrinter;
    private HashMap<Integer, List<OrderItem>> orderItemPrintMap = new HashMap<>();
    private List<Printer> printKitchenSummaryTicketList = new ArrayList<>();//用来打总单的打印机

    private AlertDialog errDishDialog;

    public Printer getReceiptPrinter() {
        return receiptPrinter;
    }

    public void setReceiptPrinter(Printer receiptPrinter) {
        this.receiptPrinter = receiptPrinter;
    }

    public Printer receiptPrinter;

    private String printName;

    public static PrintManager getInstance() {
        if (instance == null) {
            instance = new PrintManager();
            store = Store.getInstance(MyApplication.getInstance());
        }
        return instance;
    }

    private PrintManager() {
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Printer> getSelectPrinter() {
        return selectPrinter;
    }

    public void addSelectPrinter(Printer selectPrinter) {
        this.selectPrinter.add(selectPrinter);
    }

    public List<Printer> getPrinterList() {
        return printerList;
    }

    public void setPrinterList(List<Printer> printerList) {
        this.printerList = printerList;
    }

    public void addPrinter(Printer printer) {
        receipt2PrintersMap.put(printer.getId(), printer);
    }

    public void addPrinterList(List<Printer> printerList) {
        for (Printer printer : printerList) {
            receipt2PrintersMap.put(printer.getId(), printer);
        }
    }

    public void modifyPrinter(Printer printer) {
        receipt2PrintersMap.put(printer.getId(), printer);
    }

    public void deletePrinter(Integer printerId) {
        Iterator iter = receipt2PrintersMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (receipt2PrintersMap.get(key).getId() == printerId) {
                receipt2PrintersMap.remove(key);
            }
        }
    }

    private int getKitModeCount(OrderItem oi, boolean isRefundDish) {
        int modeCount = 0;
        if (ToolsUtils.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {//多份一单
            modeCount = 1;
        } else //多份多单
        {
            if (isRefundDish) {
                modeCount = oi.getRejectedQuantity();
            } else {
                modeCount = oi.getQuantity();
            }
        }
        return modeCount;
    }

    public void printKitChenOrder(final Order order) {
        orderItemPrintMap.clear();
        printKitchenSummaryTicketList.clear();
        Log.i("==========", "printKitChenOrder, orderid" + order.getId());
        Log.i("==========", "printer length: " + printerList.size());
        if (printerList != null && printerList.size() > 0) {
            for (Printer printer : printerList) {
                if (printer == null) {
                    Log.e("PrintManager", "failed to find printer for receipt");
                    return;
                }
                if (printer.getSummaryReceiptCounts() != null && (int) printer.getSummaryReceiptCounts() > 0) {
                    printKitchenSummaryTicketList.add(printer);
                }
                for (final OrderItem oi : order.getItemList()) {
                    oi.setOrderId(order.getId());
//                    Log.i("==========", "printer str: " + oi.getPrinterStr() + "  dishName: " + oi.getDishName());
                    printOrderItemDish(order, oi, printer);
                }
            }
        }
    }

    private String[] dishPrinter2Arr(String dishPrinter) {
        String[] dishPrintersArr = null;
        if (!TextUtils.isEmpty(dishPrinter)) {
            dishPrintersArr = dishPrinter.split(",");
        }
        return dishPrintersArr;
    }

    private void printOrderItemDish(final Order order, final OrderItem oi, final Printer printer) {
        //套餐菜品把子项发送到不同打印机
        try {
//            String[] printerStrArr = dishPrinter2Arr(oi.getPrinterStr());
            String[] printerStrArr = dishPrinter2Arr("11");
            if (printerStrArr != null && printerStrArr.length > 0) {
                int size = printerStrArr.length;
                for (int r = 0; r < size; r++) {
                    String printStr = printerStrArr[r];
                    if (!TextUtils.isEmpty(printStr) && printStr.contains(printer.getId() + "") && oi.getPrintState() == false) {
                        //判断是标签纸还是普通纸  并且不是退菜单
                        if (printer.getOutputType() == PrinterOutputType.LABEL && !isLogicRefundDish(order)) {
                            if (printer.getLinkType() == PrinterLinkType.USB) {
                                if (printer.getDeviceName() != null && !printer.getDeviceName().isEmpty() && usbPrinter != null) {
                                    usbPrinter.printItem(order, oi);
                                }
                            }
                            //标签一份打印一张
                            else if (printer.getLinkType() == PrinterLinkType.NETWORK) {
                                for (int i = 0; i < oi.getQuantity(); i++) {
                                    Log.i("标签打印:", oi.getDishName());
                                    GpEnternetPrint.gpPrint(oi, printer, printer.getLabelHeight(),i,0,order);
                                }
                            }
                        } else {
                            if (printer.getDishReceiptCounts() != null && (int) printer.getDishReceiptCounts() > 0) {
                                printName = printer.getDeviceName();
                                Log.i("厨房打印", "打印机" + printName + "," + printer.getIp());
                                int modeCount = getKitModeCount(oi, false);
                                for (int i = 0; i < modeCount; i++) {
                                    PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), PrinterWidth.WIDTH_76MM);
                                    printKitchenReceipt(printerInterface, order, oi, printer.getDeviceName());
                                }
                            }
                            if (printer.getSummaryReceiptCounts() != null && (int) printer.getSummaryReceiptCounts() > 0) {
                                savePrintForDish(oi, printer);
                            }
                        }
                        if (r == size - 1)//通过该菜品设置了多少个打印机,判断打多少次,当打到最后一次的时间将该菜品的打印状态设置为已打印
                        {
                            oi.setPrintState(true);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, oi, printer));
        }
    }


    private void savePrintForDish(OrderItem oi, Printer printer) {
//        if (!TextUtils.isEmpty(oi.getPrinterStr())) {
        if (!TextUtils.isEmpty("")) {
            List<OrderItem> orderItemList = orderItemPrintMap.get(printer.getId());
            if (orderItemList == null) {
                orderItemList = new ArrayList<>();
                orderItemPrintMap.put(printer.getId(), orderItemList);
            }
            orderItemList.add(oi);
        }
    }

    public void printOrderKitSummaryTicket(Order order) {
        if (printKitchenSummaryTicketList != null && printKitchenSummaryTicketList.size() > 0) {
            for (Printer printer : printKitchenSummaryTicketList) {
                if (!TextUtils.isEmpty(printer.getIp())) {
                    PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), PrinterWidth.WIDTH_76MM);
                    printOrderDishKitSummaryTicket(printerInterface, printer, order);
                }
            }
        }
    }

    public void printOrderDishKitSummaryTicket(PrinterInterface printerInterface, Printer printer, Order order) {
        List<OrderItem> orderItemList = null;
        if (orderItemPrintMap != null && orderItemPrintMap.size() > 0) {
            orderItemList = orderItemPrintMap.get(printer.getId());
        }
        if (orderItemList != null && orderItemList.size() > 0) {
            try {
                printerInterface.init();
                for (OrderItem orderItem : orderItemList) {
                    orderItem.setPackName(orderItem.getTempPackName());
                }
                String orderType = order.getOrderType();
                if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
                    orderType = "厨房(退菜单)总单";
                } else if (order.getTableStyle() == Constant.EventState.PRINTER_RUSH_DISH) {
                    orderType = "厨房(催菜单)总单";
                } else if (order.getTableStyle() == Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT) {
                    orderType = "补打厨房单";
                } else {
                    orderType = orderType.equals("EAT_IN") ? "厨房(堂食单)总单" : "厨房(外带单)总单";
                }
                TextRow row = createRow(false, 2, orderType);
                row.setAlign(Alignment.CENTER);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator(" "));
                String tableName = order.getTableNames();
                String comment = "";
                comment = order.getComment();
                if (ToolsUtils.logicIsTable()) {
                    if (!TextUtils.isEmpty(tableName)) {
                        TextRow rowNum = createRow(true, 2, "桌台号:( " + (TextUtils.isEmpty(tableName) ? "0" : tableName) + " ) ");
                        rowNum.setAlign(Alignment.CENTER);
                        printerInterface.printRow(rowNum);
                    }
                } else {
                    String cardNumberType = "";
                    String callNumber = "";
                    if (StoreInfor.cardNumberMode) {
                        cardNumberType = "餐牌号:";
                        callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
                    } else {
                        cardNumberType = "取餐号:";
                        callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
                    }

                    row = createRow(false, 2, cardNumberType + "(" + callNumber + ")");
                    row.setAlign(Alignment.CENTER);
                    row.setScaleHeight(2);
                    printerInterface.printRow(row);
                }

                printerInterface.printRow(new Separator(" "));

                printerInterface.printRow(createRow(false, 1, "单号: " + order.getId()));
                printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

                printerInterface.printRow(new Separator(" "));
                String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
                printerInterface.printRow(createRow(false, 1, orderTitle));
                printerInterface.printRow(new Separator("-"));

                String previousPackageName = "";
                for (OrderItem orderItem : orderItemList) {
                    orderItem.setTempPackName(orderItem.getPackName());
                    if (previousPackageName.equals(orderItem.getPackName())) {
                        orderItem.setPackName("");
                    } else {
                        previousPackageName = orderItem.getPackName();
                    }
                }
                for (OrderItem orderItem : orderItemList) {
                    String dishItem = "";
                    String dishQuantity = "";
                    if (ToolsUtils.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
                        //退菜
                        if (isLogicRefundDish(order)) {
                            dishQuantity = orderItem.getRejectedQuantity() + "";
                            dishQuantity = getDishRejectedQuantity(dishQuantity);
                        }
                        //正常下单打菜
                        else {
                            dishQuantity = orderItem.getQuantity() + "";
                        }
                    } else {
                        dishQuantity = "1";
                    }
                    if (!TextUtils.isEmpty(orderItem.getPackName())) {
                        TextRow rowNum = createRow(true, 2, "(" + orderItem.getPackName()+isShowDishPackageMoney(orderItem) + ")");
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }
                    if(isLogicRefundDish(order))
                    {
                        dishItem = PrintUtils.getStr("[退]"+orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr("-"+dishQuantity + "份" + isShowDishMoney(orderItem), 5, PrintUtils.TYPE_TOP);
                    }
                    else{
                        dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr(dishQuantity + "份"+isShowDishMoney(orderItem),5, PrintUtils.TYPE_TOP);
                    }

                    printerInterface.printRow(createRow(false, 2, dishItem));

                    printDishOption(true,printerInterface,orderItem.optionList);
                    if (!TextUtils.isEmpty(comment)) {
                        TextRow rowNum = createRow(true, 2, "备注:( " + (TextUtils.isEmpty(comment) ? "" : comment) + " ) ");
                        rowNum.setAlign(Alignment.LEFT);
                        printerInterface.printRow(rowNum);
                    }
                }
                printerInterface.close();
                PosInfo posinfo = PosInfo.getInstance();
                String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                        "单号: " + order.getId() + "/r/n" +
                        "操作人: " + posinfo.getRealname() + "/r/n";
                ToolsUtils.writePrinterReceiptInfo(countInfo);
            } catch (Exception e) {
                e.printStackTrace();
                EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, orderItemList, printer));
            }
        }
    }

    public void printKitChenRefundOrder(final Order order) {
        if (printerList != null && printerList.size() > 0) {
            for (Printer printer : printerList) {
                if (printer == null) {
                    Log.e("PrintManager", "failed to find printer for receipt");
                    return;
                }
                for (final OrderItem oi : order.getItemList()) {
                    oi.setOrderId(order.getId());
//                    Log.i("==========", "printer str: " + oi.getPrinterStr() + "  dishName: " + oi.getDishName());
                    try {
                        String[] printerStrArr = dishPrinter2Arr("");
//                        String[] printerStrArr = dishPrinter2Arr(oi.getPrinterStr());
                        if (printerStrArr != null && printerStrArr.length > 0) {
                            int size = printerStrArr.length;
                            for (int r = 0; r < size; r++) {
                                String printStr = printerStrArr[r];
                                if (!TextUtils.isEmpty(printStr) && printStr.contains(printer.getId() + "") && oi.getPrintState() == false) {
                                    if (printer.getOutputType() == PrinterOutputType.LABEL) {//判断是标签纸还是普通纸
                                        //
                                    } else {
                                        printName = printer.getDeviceName();
                                        Log.i("厨房打印", "打印机" + printName + "," + printer.getIp());

                                        int modeCount = getKitModeCount(oi, true);
                                        for (int i = 0; i < modeCount; i++) {
                                            PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), PrinterWidth.WIDTH_76MM);
                                            printKitchenRefund(printerInterface, order, oi, printer.getDeviceName());
                                        }
                                    }
                                    if (r == size - 1)//通过该菜品设置了多少个打印机,判断打多少次,当打到最后一次的时间将该菜品的打印状态设置为已打印
                                    {
                                        oi.setPrintState(true);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, oi, printer));
                    }
                }
            }
        }
    }

    public void printKitChenALLDish(final Order order) {
        if (printerList != null && printerList.size() > 0) {
            for (Printer printer : printerList) {
                if (printer == null) {
                    Log.e("PrintManager", "failed to find printer for receipt");
                    return;
                }
                for (final OrderItem oi : order.getItemList()) {
                    oi.setOrderId(order.getId());
//                    Log.i("==========", "printer str: " + oi.getPrinterStr() + "  dishName: " + oi.getDishName());
                    //套餐菜品把子项发送到不同打印机
                    if (oi.getSubItemList() != null && oi.getSubItemList().size() > 0) {
                        Dish.Package subDishTemp = null;
                        try {
                            List<Dish.Package> subDishs = oi.getSubItemList();
                            for (Dish.Package subDish : subDishs) {
                                subDishTemp = subDish;
                                OrderItem subOi = new OrderItem();
                                subOi.setOrderId(order.getId());
                                subOi.setDishName(subDish.getDishName());
                                subOi.setQuantity(subDish.quantity * oi.getQuantity());
//                                subOi.setPrinterStr(subDish.getPrinterStr());
                                subOi.setDishKind(subDish.dishKind);
//                                String[] printerStrArr = dishPrinter2Arr(subDish.getPrinterStr());
                                String[] printerStrArr = dishPrinter2Arr("");
                                if (printerStrArr != null && printerStrArr.length > 0) {
                                    int size = printerStrArr.length;
                                    for (int r = 0; r < size; r++) {
                                        String printStr = printerStrArr[r];
                                        if (!TextUtils.isEmpty(printStr) && printStr.contains(printer.getId() + "") && subDish.isPrint == false) {
                                            if (printer.getOutputType() == PrinterOutputType.LABEL) {//判断是标签纸还是普通纸
                                                //
                                            } else {
                                                printName = printer.getDeviceName();
                                                Log.i("厨房打印", "打印机" + printName + "," + printer.getIp());
                                                int modeCount = getKitModeCount(subOi, false);
                                                for (int i = 0; i < modeCount; i++) {
                                                    PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), PrinterWidth.WIDTH_76MM);
                                                    printKitchenALLReceipt(printerInterface, order, subOi, printer.getDeviceName());
                                                }
                                            }
                                            if (r == size - 1)//通过该菜品设置了多少个打印机,判断打多少次,当打到最后一次的时间将该菜品的打印状态设置为已打印
                                            {
                                                subDish.isPrint = true;
                                            }
                                        }

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, oi, subDishTemp, printer));
                        }
                    } else {
                        try {
//                            String[] printerStrArr = dishPrinter2Arr(oi.getPrinterStr());
                            String[] printerStrArr = dishPrinter2Arr("");
                            if (printerStrArr != null && printerStrArr.length > 0) {
                                int size = printerStrArr.length;
                                for (int r = 0; r < size; r++) {
                                    String printStr = printerStrArr[r];
                                    if (!TextUtils.isEmpty(printStr) && printStr.contains(printer.getId() + "") && oi.getPrintState() == false) {
                                        if (printer.getOutputType() == PrinterOutputType.LABEL) {//判断是标签纸还是普通纸
                                            //
                                        } else {
                                            printName = printer.getDeviceName();
                                            Log.i("厨房打印", "打印机" + printName + "," + printer.getIp());

                                            int modeCount = getKitModeCount(oi, false);
                                            for (int i = 0; i < modeCount; i++) {
                                                PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), PrinterWidth.WIDTH_76MM);
                                                printKitchenALLReceipt(printerInterface, order, oi, printer.getDeviceName());
                                            }
                                        }
                                        if (r == size - 1)//通过该菜品设置了多少个打印机,判断打多少次,当打到最后一次的时间将该菜品的打印状态设置为已打印
                                        {
                                            oi.setPrintState(true);
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_KITCHEN_ORDER, order, oi, printer));
                        }
                    }
                }
            }
        }
    }

    //    public void printWorkShiftReport(WorkShiftReport workShiftReport) {
    //        if (printerList != null && printerList.size() > 0) {
    //            int size = printerList.size();
    //            for (int i = 0; i < size; i++) {
    //                if (receiptPrinter.getIp().equals(printerList.get(i).getIp())) {
    //                    Printer printer = printerList.get(i);
    //                    if (printer == null) {
    //                        Log.e("PrintManager", "failed to find printer for receipt");
    //                        return;
    //                    }
    //                    PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
    //                    PosInfo posInfo = PosInfo.getInstance();
    //                    printWorkShiftReceipt(printerInterface, workShiftReport, posInfo.getDefinition());
    //                }
    //            }
    //        }
    //    }


    public void printOrder(PrinterInterface printerInterface, Order order, String deviceName) throws Exception {
        printerInterface.init();
        String orderTitle = "";
        if (order.getPrinterType() == Constant.JsToAndroid.JS_A_ADDDISH) {
            orderTitle = "桌台加菜单";
        } else if (order.getOrderPrintType() == Constant.EventState.PRINTER_EXTRA_RECEIPT) {
            orderTitle = "补打结账单";
        } else {
            orderTitle = "结账单";
        }
        TextRow row = createRow(false, 2, orderTitle);
        row.setAlign(Alignment.CENTER);
        printerInterface.printRow(row);

        printerInterface.printRow(new Separator(" "));
        String tableName = order.getTableNames();
        if (!TextUtils.isEmpty(tableName)) {
            row = createRow(false, 2, "桌台号: (" + tableName + ")");
            row.setAlign(Alignment.CENTER);
            row.setScaleHeight(2);
            row.setScaleWidth(1);
            printerInterface.printRow(row);

            printerInterface.printRow(new Separator(" "));
        }

        String orderType = order.getOrderType();
        printerInterface.printTable(createRow(false, 1, "单号: " + order.getId(), orderType.equals("EAT_IN") ? "堂食" : "外带"));

        printerInterface.printTable(createRow(false, 1, "客人数: " + order.getCustomerAmount(), "取餐号: " + order.getCallNumber()));
        printerInterface.printRow(createRow(false, 1, "时间: " + getTimeStr(order.getCreatedAt())));
        printerInterface.printRow(new Separator("-"));

        printerInterface.printTable(createDishTable(order, 2));

        printerInterface.printRow(new Separator(" "));
        if (!TextUtils.isEmpty(store.getStorePhone())) {
            printerInterface.printRow(createRow(false, 1, "电话号码: " + store.getStorePhone()));
        }
        if (!TextUtils.isEmpty(store.getStoreAddress())) {
            printerInterface.printRow(createRow(false, 1, "地    址: " + store.getStoreAddress()));
        }
        printerInterface.close();
    }


    /**
     * 退菜 厨房
     *
     * @param printerInterface
     * @param order
     * @param orderItem
     * @param deviceName
     * @throws Exception
     */
    public void printKitchenRefund(PrinterInterface printerInterface, Order order, OrderItem orderItem, String deviceName) throws Exception {
        printerInterface.init();
        String orderType = "厨房(退菜单)";
        TextRow row = createRow(false, 2, orderType);
        row.setAlign(Alignment.CENTER);
        printerInterface.printRow(row);

        printerInterface.printRow(new Separator(" "));
        String tableName = order.getTableNames();
        String comment = "";
        comment = order.getComment();

        if (ToolsUtils.logicIsTable()) {
            if (!TextUtils.isEmpty(tableName)) {
                TextRow rowNum = createRow(true, 2, "桌台号:( " + (TextUtils.isEmpty(tableName) ? "0" : tableName) + " ) ");
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);

                //                String customer = order.getCustomerAmount() + "";
                //                printerInterface.printRow(createRow(false, 1, "客人数:" + (TextUtils.isEmpty(customer) ? "0" : customer)));
            }
        } else {
            String cardNumberType = "";
            String callNumber = "";
            if (StoreInfor.cardNumberMode) {
                cardNumberType = "餐牌号:";
                callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
            } else {
                cardNumberType = "取餐号:";
                callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
            }

            row = createRow(false, 2, cardNumberType + "(" + callNumber + ")");
            row.setAlign(Alignment.CENTER);
            row.setScaleHeight(2);
            printerInterface.printRow(row);
        }

        printerInterface.printRow(createRow(false, 1, "单号: " + order.getId()));
        printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

        printerInterface.printRow(new Separator(" "));
        String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
        printerInterface.printRow(createRow(false, 1, orderTitle));
        printerInterface.printRow(new Separator("-"));

        String dishRejectedQuantity = "";
        if (ToolsUtils.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
            dishRejectedQuantity = orderItem.getRejectedQuantity() + "";
            dishRejectedQuantity = getDishRejectedQuantity(dishRejectedQuantity);
        } else {
            dishRejectedQuantity = "1";
        }
        String dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + "-" + PrintUtils.getStr(dishRejectedQuantity + "份", 5, PrintUtils.TYPE_TOP);
        //厨房单里的菜品需要比较大的字体
        printerInterface.printRow(createRow(false, 2, dishItem));

        if (orderItem.optionList != null && orderItem.optionList.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (Option option : orderItem.optionList) {
                if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                    sb.append(option.name + "、");
                } else {
                    sb.append(option.name + "(" + option.getPrice() + "元)、");
                }
            }
            TextRow oiRow = createRow(false, 2, "    " + sb.toString());
            oiRow.setScaleWidth(1);
            oiRow.setScaleHeight(1);
            printerInterface.printRow(oiRow);
        }
        if (!TextUtils.isEmpty(comment)) {
            TextRow rowNum = createRow(true, 2, "备注:( " + (TextUtils.isEmpty(comment) ? "" : comment) + " ) ");
            rowNum.setAlign(Alignment.LEFT);
            printerInterface.printRow(rowNum);
        }

        printerInterface.close();
        PosInfo posinfo = PosInfo.getInstance();
        String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                "单号: " + order.getId() + "/r/n" +
                "操作人: " + posinfo.getRealname() + "/r/n";
        ToolsUtils.writePrinterReceiptInfo(countInfo);
    }


    public void printKitchenALLReceipt(PrinterInterface printerInterface, Order order, OrderItem orderItem, String deviceName) throws Exception {
        printerInterface.init();
        String orderType = "";
        orderType = "厨房(退菜单)";
        TextRow row = createRow(false, 2, orderType);
        row.setAlign(Alignment.CENTER);
        printerInterface.printRow(row);

        printerInterface.printRow(new Separator(" "));
        String tableName = order.getTableNames();
        String comment = "";
        comment = order.getComment();
        if (ToolsUtils.logicIsTable()) {
            if (!TextUtils.isEmpty(tableName)) {
                TextRow rowNum = createRow(true, 2, "桌台号:( " + (TextUtils.isEmpty(tableName) ? "0" : tableName) + " ) ");
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
            }
        } else {
            String cardNumberType = "";
            String callNumber = "";
            if (StoreInfor.cardNumberMode) {
                cardNumberType = "餐牌号:";
                callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
            } else {
                cardNumberType = "取餐号:";
                callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
            }

            row = createRow(false, 2, cardNumberType + "(" + callNumber + ")");
            row.setAlign(Alignment.CENTER);
            row.setScaleHeight(2);
            printerInterface.printRow(row);
        }

        printerInterface.printRow(new Separator(" "));

        printerInterface.printRow(createRow(false, 1, "单号: " + order.getId()));
        printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

        printerInterface.printRow(new Separator(" "));
        String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
        printerInterface.printRow(createRow(false, 1, orderTitle));
        printerInterface.printRow(new Separator("-"));

        String dishItem = "";
        String dishQuantity = "";
        if (ToolsUtils.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
            dishQuantity = orderItem.getQuantity() + "";
        } else {
            dishQuantity = "1";
        }
        dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + "-" + PrintUtils.getStr(dishQuantity + "份", 5, PrintUtils.TYPE_TOP);
        //厨房单里的菜品需要比较大的字体
        printerInterface.printRow(createRow(false, 2, dishItem));

        if (!TextUtils.isEmpty(comment)) {
            TextRow rowNum = createRow(true, 2, "备注:( " + (TextUtils.isEmpty(comment) ? "" : comment) + " ) ");
            rowNum.setAlign(Alignment.LEFT);
            printerInterface.printRow(rowNum);
        }

        printerInterface.close();
        PosInfo posinfo = PosInfo.getInstance();
        String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                "单号: " + order.getId() + "/r/n" +
                "操作人: " + posinfo.getRealname() + "/r/n";
        ToolsUtils.writePrinterReceiptInfo(countInfo);
    }


    public void printKitchenReceipt(PrinterInterface printerInterface, Order order, OrderItem orderItem, String deviceName) throws Exception {
        printerInterface.init();
        String orderType = order.getOrderType();
        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
            orderType = "厨房(退菜单)";
        } else if (order.getTableStyle() == Constant.EventState.PRINTER_RUSH_DISH) {
            orderType = "厨房(催菜单)";
        } else if (order.getTableStyle() == Constant.EventState.PRINTER_EXTRA_KITCHEN_RECEIPT) {
            orderType = "补打厨房单";
        } else if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
            orderType = "厨房(退菜单)";
        } else {
            orderType = orderType.equals("EAT_IN") ? "厨房(堂食单)分单" : "厨房(外带单)分单";
        }
        TextRow row = createRow(false, 2, orderType);
        row.setAlign(Alignment.CENTER);
        printerInterface.printRow(row);

        printerInterface.printRow(new Separator(" "));
        String tableName = order.getTableNames();
        String comment = "";
        comment = order.getComment();
        if (ToolsUtils.logicIsTable()) {
            if (!TextUtils.isEmpty(tableName)) {
                TextRow rowNum = createRow(true, 2, "桌台号:( " + (TextUtils.isEmpty(tableName) ? "0" : tableName) + " ) ");
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
            }
        } else {
            String cardNumberType = "";
            String callNumber = "";
            if (StoreInfor.cardNumberMode) {
                cardNumberType = "餐牌号:";
                callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
            } else {
                cardNumberType = "取餐号:";
                callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
            }

            row = createRow(false, 2, cardNumberType + "(" + callNumber + ")");
            row.setAlign(Alignment.CENTER);
            row.setScaleHeight(2);
            printerInterface.printRow(row);
        }

        printerInterface.printRow(new Separator(" "));

        printerInterface.printRow(createRow(false, 1, "单号: " + order.getId()));
        printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

        printerInterface.printRow(new Separator(" "));
        String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
        printerInterface.printRow(createRow(false, 1, orderTitle));
        printerInterface.printRow(new Separator("-"));

        String dishItem = "";
        String dishQuantity = "";
        if (ToolsUtils.getKitchenPrintMode() == KitchenPrintMode.PER_DISH) {
            //退菜
            if (isLogicRefundDish(order)) {
                dishQuantity = orderItem.getRejectedQuantity() + "";
                dishQuantity = getDishRejectedQuantity(dishQuantity);
            }
            //正常下单打菜
            else {
                dishQuantity = orderItem.getQuantity() + "";
            }
        } else {
            dishQuantity = "1";
        }

        if (!TextUtils.isEmpty(orderItem.getPackName())) {
            printerInterface.printRow(createRow(false, 2, "(" + orderItem.getPackName()+isShowDishPackageMoney(orderItem)+ ")"));
        }
        if (isLogicRefundDish(order)) {
            dishItem = PrintUtils.getStr("[退]"+orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr("-"+dishQuantity + "份" +isShowDishMoney(orderItem), 7, PrintUtils.TYPE_TOP);
        }
        else{
            dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr(dishQuantity + "份"+isShowDishMoney(orderItem), 7, PrintUtils.TYPE_TOP);
        }
        //厨房单里的菜品需要比较大的字体
        printerInterface.printRow(createRow(false, 2, dishItem));

        printDishOption(true,printerInterface,orderItem.optionList);
        if (!TextUtils.isEmpty(comment)) {
            TextRow rowNum = createRow(true, 2, "备注:( " + (TextUtils.isEmpty(comment) ? "" : comment) + " ) ");
            rowNum.setAlign(Alignment.LEFT);
            printerInterface.printRow(rowNum);
        }
        printerInterface.close();
        PosInfo posinfo = PosInfo.getInstance();
        String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                "单号: " + order.getId() + "/r/n" +
                "操作人: " + posinfo.getRealname() + "/r/n";
        ToolsUtils.writePrinterReceiptInfo(countInfo);
    }

    public void printWorkShiftReceipt(PrinterInterface printerInterface, WorkShiftReport workShiftReport, Definition definition) {
        try {
            PosInfo posInfo = PosInfo.getInstance();
            printerInterface.init();
            TextRow row = createRow(false, 2, "交接班打印单");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 2, "交接人 : " + posInfo.getRealname());
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 1, "交接时间 : " + getTimeStr(System.currentTimeMillis()));
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);

            createWorkShiftItem(printerInterface, workShiftReport);
            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.getInstance().ShowToast("打印小票失败,请检查打印机设置!");
        }
    }

    public void RetreatPrintOrder(PrinterInterface printerInterface, Order order, String deviceName) {
        try {
            printerInterface.init();
            TextRow row = createRow(false, 2, "退菜单");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));
            String tableName = order.getTableNames();
            if (!TextUtils.isEmpty(tableName)) {
                row = createRow(false, 2, "桌台号: (" + tableName + ")");
                row.setAlign(Alignment.CENTER);
                row.setScaleHeight(2);
                row.setScaleWidth(1);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator(" "));
            }

            String orderType = order.getOrderType();
            printerInterface.printTable(createRow(false, 1, "单号: " + order.getId(), orderType.equals("EAT_IN") ? "堂食" : "外带"));

            printerInterface.printTable(createRow(false, 1, "客人数: " + order.getCustomerAmount(), "取餐号: " + order.getCallNumber()));
            printerInterface.printRow(createRow(false, 1, "时间: " + getTimeStr(System.currentTimeMillis())));
            printerInterface.printRow(new Separator("-"));

            printerInterface.printTable(createRetreatDishTable(order, 1));

            printerInterface.printRow(new Separator(" "));
            if (!TextUtils.isEmpty(store.getStorePhone())) {
                printerInterface.printRow(createRow(false, 1, "电话号码: " + store.getStorePhone()));
            }
            if (!TextUtils.isEmpty(store.getStoreAddress())) {
                printerInterface.printRow(createRow(false, 1, "地    址: " + store.getStoreAddress()));
            }

            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.getInstance().ShowToast(deviceName + "打印机打印小票失败,请检查打印机设置!");
        }
    }

    /**
     * 桌台退菜厨房打印单
     *
     * @param printerInterface
     * @param order
     * @param orderItem
     * @param deviceName
     */
    public void printKitchenRetreatReceipt(PrinterInterface printerInterface, Order order, OrderItem orderItem, String deviceName) {
        try {
            PosInfo posInfo = PosInfo.getInstance();
            printerInterface.init();
            TextRow row = createRow(false, 2, "厨房退菜单");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);

            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 2, "操作人: " + posInfo.getRealname());
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);

            printerInterface.printRow(new Separator(" "));
            String tableName = order.getTableNames();
            if (!TextUtils.isEmpty(tableName)) {
                row = createRow(false, 2, "桌台号: (" + tableName + ")");
                row.setAlign(Alignment.CENTER);
                row.setScaleHeight(2);
                row.setScaleWidth(1);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator(" "));
            }

            printerInterface.printRow(new Separator(" "));

            printerInterface.printRow(createRow(false, 1, "单号: " + order.getId()));
            printerInterface.printRow(createRow(false, 1, "下单时间: " + getTimeStr(order.getCreatedAt())));

            printerInterface.printRow(new Separator(" "));
            String orderTitle = PrintUtils.getStr("菜品", 21, PrintUtils.TYPE_BACK) + PrintUtils.getStr("数量", 21, PrintUtils.TYPE_TOP);
            printerInterface.printRow(createRow(false, 1, orderTitle));
            printerInterface.printRow(new Separator("-"));
            String dishItem = PrintUtils.getStr(orderItem.getDishName() + ToolsUtils.isWaiDai(order.getOrderType()), 16, PrintUtils.TYPE_BACK) + PrintUtils.getStr(orderItem.getRejectedQuantity() + "份", 5, PrintUtils.TYPE_TOP);
            //厨房单里的菜品需要比较大的字体
            printerInterface.printRow(createRow(false, 2, dishItem));

            printerInterface.close();
            PosInfo posinfo = PosInfo.getInstance();
            String countInfo = "时间: " + getTimeStr(order.getCreatedAt()) + "/r/n" +
                    "单号: " + order.getId() + "/r/n" +
                    "操作人: " + posinfo.getRealname() + "/r/n";
            ToolsUtils.writePrinterReceiptInfo(countInfo);
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.getInstance().ShowToast(deviceName + "打印机打印小票失败,请检查打印机设置!");
        }
    }

    private TextRow createRow(boolean bold, int size, String content) {
        TextRow title = new TextRow(content);
        title.setScaleWidth(size);
        title.setScaleHeight(size);
        title.setBoldFont(bold);
        return title;
    }

    //创建一行， 内容为 左对齐 和 有对齐 的2列
    private Table createRow(boolean bold, int size, String left, String right) {
        Table table = new Table(2);
        TextRow row = new TextRow();

        row.setScaleHeight(size);
        row.setScaleWidth(size);
        row.setBoldFont(bold);
        row.addColumn(new Column(left, Alignment.LEFT));
        row.addColumn(new Column(right, Alignment.RIGHT));

        table.addRow(row);
        return table;
    }

    private Table createDishTable(Order order, int scaleWidth) {
        Table table = new Table(3);

        TextRow title = new TextRow();
        title.setBoldFont(true);
        title.setScaleWidth(scaleWidth);
        title.addColumn(new Column("名称", Alignment.LEFT));
        title.addColumn(new Column("数量", Alignment.LEFT));
        title.addColumn(new Column("价格", Alignment.RIGHT));
        table.setTitle(title);
        table.addRow(new Separator("-"));

        int totalQuantiity = 0;
        //        double totalCost = 0;
        BigDecimal totalCost = new BigDecimal("0.00");
        for (OrderItem oi : order.getItemList()) {
            title = new TextRow();
            title.setScaleWidth(scaleWidth);
            title.addColumn(new Column(oi.getDishName(), Alignment.LEFT));
            title.addColumn(new Column(String.valueOf(oi.getQuantity()), Alignment.LEFT));
            BigDecimal totalItemCost = new BigDecimal("0.00");
            totalItemCost = oi.getCost().multiply(new BigDecimal(oi.getQuantity() + ""));
            title.addColumn(new Column(String.valueOf(totalItemCost), Alignment.RIGHT));
            table.addRow(title);
            totalQuantiity += oi.getQuantity();
            BigDecimal costItem = new BigDecimal(totalItemCost + "");
            totalCost = totalCost.add(costItem);
            //            totalCost += oi.getCost();
        }
        //统计行
        table.addRow(new Separator("-"));
        title = new TextRow();
        title.setScaleWidth(scaleWidth);
        title.addColumn(new Column("共计", Alignment.LEFT));
        title.addColumn(new Column(String.valueOf(totalQuantiity), Alignment.LEFT));
        title.addColumn(new Column(String.valueOf(totalCost), Alignment.RIGHT));
        table.addRow(title);
        table.addRow(new Separator("-"));
        return table;
    }

    private Table createRetreatDishTable(Order order, int scaleWidth) {
        Table table = new Table(3);

        TextRow title = new TextRow();
        title.setBoldFont(true);
        title.setScaleWidth(scaleWidth);
        title.addColumn(new Column("名称", Alignment.LEFT));
        title.addColumn(new Column("数量", Alignment.LEFT));
        title.addColumn(new Column("价格", Alignment.RIGHT));
        table.setTitle(title);
        table.addRow(new Separator("-"));

        int totalQuantiity = 0;
        //        double totalCost = 0;
        BigDecimal totalCost = new BigDecimal("0.00");
        for (OrderItem oi : order.getItemList()) {
            if (oi.rejectedQuantity > 0) {
                title = new TextRow();
                title.setScaleWidth(scaleWidth);
                title.addColumn(new Column(oi.getDishName(), Alignment.LEFT));
                title.addColumn(new Column(String.valueOf(oi.getRejectedQuantity()), Alignment.LEFT));
                BigDecimal totalItemCost = new BigDecimal("0.00");
                totalItemCost = oi.getCost().multiply(new BigDecimal(oi.getRejectedQuantity() + ""));
                title.addColumn(new Column(String.valueOf(totalItemCost), Alignment.RIGHT));
                table.addRow(title);
                totalQuantiity += oi.getRejectedQuantity();
                BigDecimal costItem = new BigDecimal(totalItemCost + "");
                totalCost = totalCost.add(costItem);
            }
            //            totalCost += oi.getCost();
        }
        //统计行
        table.addRow(new Separator("-"));
        title = new TextRow();
        title.setScaleWidth(scaleWidth);
        title.addColumn(new Column("共计", Alignment.LEFT));
        title.addColumn(new Column(String.valueOf(totalQuantiity), Alignment.LEFT));
        title.addColumn(new Column(String.valueOf(totalCost), Alignment.RIGHT));
        table.addRow(title);
        table.addRow(new Separator("-"));
        return table;
    }

    private void createWorkShiftItem(PrinterInterface printerInterface, WorkShiftReport workShiftReport) {
        try {
            for (WorkShiftReport.ItemCategorySalesDataList itemCategorySalesDataList : workShiftReport.getItemCategorySalesDataList()) {
                printerInterface.printRow(new Separator(" "));
                printerInterface.printRow(new Separator("*"));
                printerInterface.printRow(new Separator(" "));

                TextRow row = createRow(false, 2, itemCategorySalesDataList.getName());
                row.setAlign(Alignment.CENTER);
                printerInterface.printRow(row);

                printerInterface.printRow(new Separator(" "));
                printerInterface.printRow(new Separator("*"));
                printerInterface.printRow(new Separator(" "));

                Table table = new Table(3);

                TextRow title = new TextRow();
                title.setBoldFont(true);
                title.setScaleWidth(1);
                title.addColumn(new Column("名称", Alignment.LEFT));
                title.addColumn(new Column("数量", Alignment.LEFT));
                title.addColumn(new Column("金额", Alignment.RIGHT));
                table.setTitle(title);
                table.addRow(new Separator("-"));

                for (WorkShiftReport.ItemCategorySalesDataList.ItemSalesDataList itemSalesDataList : itemCategorySalesDataList.getItemSalesDataList()) {
                    title = new TextRow();
                    title.setScaleWidth(1);
                    title.addColumn(new Column(itemSalesDataList.getName(), Alignment.LEFT));
                    title.addColumn(new Column(String.valueOf(itemSalesDataList.getItemCounts()), Alignment.LEFT));
                    title.addColumn(new Column(String.valueOf(itemSalesDataList.getTotal()), Alignment.RIGHT));
                    table.addRow(new Separator(" "));
                    table.addRow(title);
                }
                //统计行
                printerInterface.printTable(table);
            }

            printerInterface.printRow(new Separator(" "));
            printerInterface.printRow(new Separator("*"));
            printerInterface.printRow(new Separator(" "));

            TextRow row = createRow(false, 2, "客单价统计");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);

            printerInterface.printRow(new Separator(" "));
            printerInterface.printRow(new Separator("*"));
            printerInterface.printRow(new Separator(" "));

            WorkShiftReport.PctData pctData = workShiftReport.getPctData();
            printerInterface.printTable(createRow(false, 1, "订单总数", String.valueOf(pctData.getOrderCounts() + " /条")));
            printerInterface.printRow(new Separator(" "));
            printerInterface.printTable(createRow(false, 1, "客人总数", String.valueOf(pctData.getCustomerCounts() + " /人")));
            printerInterface.printRow(new Separator(" "));
            printerInterface.printTable(createRow(false, 1, "平均订单金额", " ￥ " + String.valueOf(pctData.getPricePerOrder() + " / 元")));
            printerInterface.printRow(new Separator(" "));
            printerInterface.printTable(createRow(false, 1, "客单价", " ￥ " + String.valueOf(pctData.getPricePerCustomer() + " / 元")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimeStr(long time) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(new Date(time));
    }

    private Order getTemplateOrder() {
        PosInfo.getInstance().setPhone("123456677");
        PosInfo.getInstance().setAddress("西丽皮革厂平山大道20号");

        Order order = new Order();
        order.setId(12233);
        order.setOrderType("堂食");
        order.setCustomerAmount(2);
        order.setCallNumber("A12345");
        order.setCreatedAt(System.currentTimeMillis());


        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem oi = new OrderItem();
        oi.setDishName("酸辣牛肉");
        oi.setQuantity(2);
        oi.setCost(new BigDecimal(200));
        orderItemList.add(oi);

        //        oi = new OrderItem();
        //        oi.setDishName("红烧猪肉面");
        //        oi.setQuantity(2);
        //        oi.setCost(new BigDecimal(200));
        //        orderItemList.add(oi);
        //
        //        oi = new OrderItem();
        //        oi.setDishName("茄子豆角");
        //        oi.setQuantity(2);
        //        oi.setCost(new BigDecimal(200));
        //        orderItemList.add(oi);

        order.setItemList(orderItemList);
        return order;
    }

    private OrderItem getTemplateOrderItem() {
        OrderItem oi = new OrderItem();
        oi.setDishName("酸辣牛肉");
        oi.setQuantity(2);
        oi.setCost(new BigDecimal(200));
        return oi;
    }

    //把订单模板显示到textview上
    public void printTemplateOrder(TextView textView, int width, String deviceName) {
        PreviewPrinter previewPrinter = new PreviewPrinter(textView, width);
        try {
            printOrder(previewPrinter, getTemplateOrder(), deviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //廚房單
    public void printTemplateKitchenReceipt(TextView textView, int width, String deviceName) {
        PreviewPrinter previewPrinter = new PreviewPrinter(textView, width);
        try {
            printKitchenReceipt(previewPrinter, getTemplateOrder(), getTemplateOrderItem(), deviceName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打开钱箱
    public void openCashBox() {
        ToolsUtils.writeUserOperationRecords("打开钱箱");
        PrinterInterface printerInterface = null;
        if (receiptPrinter != null) {
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(receiptPrinter.getVendor()), receiptPrinter.getIp(), receiptPrinter.getWidth());
        } else {
            printerInterface = PrinterFactory.createBluetoothPrinter(PrinterWidth.WIDTH_80MM);
        }

        if (printerInterface == null) {
            Log.i("钱箱未连接", "钱箱未连接,请打开蓝牙");
            MyApplication.getInstance().ShowToast("未配置钱箱!");
            return;
        }
        try {
            printerInterface.init();
            if (receiptPrinter != null) {
                printerInterface.openCachBox();
            } else {
                MyApplication.getInstance().openSunMicashBox();
//                printerInterface.openSumMiCachBox();
            }
//            printerInterface.closeNoCutPaper();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //打印结账单
    public void printCheckOut(Order order) {
        Log.i("打印结账单", "<====printCheckOut");
        Log.i("订单Id==orderId==", order.getId() + "");
        PrinterInterface printerInterface = logicInitPrint();
        if (printerInterface == null) {
            return;
        }
        try {
            printerInterface.init();
            //            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
            //            printerInterface.printRow(new BitmapRow(bitmap));
            String type = order.getOrderType();
            String orderType = "堂食";
            if (type.equals("EAT_IN")) {
                orderType = "堂食";
            } else if (type.equals("SALE_OUT")) {
                orderType = "外卖";
            } else if (type.equals("TAKE_OUT")) {
                orderType = "外带";
            }
            String storeName = "";
            if (!TextUtils.isEmpty(StoreInfor.storeName)) {
                storeName = StoreInfor.storeName;
            }

            TextRow rowStore = createRow(false, 2, "欢迎光临 " + storeName);
            PosInfo posInfo = PosInfo.getInstance();
            rowStore.setScaleHeight(2);
            rowStore.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowStore);
            printerInterface.printRow(new Separator(" "));
            String checkOutTitle = "";
            if (order.getPrinterType() == Constant.EventState.ORDER_TYPE_ADVANCE) {
                checkOutTitle = "预结单";
            } else {
                checkOutTitle = "结账单";
            }
            TextRow rowNumTitle = createRow(true, 2, checkOutTitle);
            rowNumTitle.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowNumTitle);
            printerInterface.printRow(new Separator(" "));

            if (ToolsUtils.logicIsTable()) {
                String tableName = order.getTableNames();
                TextRow rowNum = createRow(true, 2, "桌台号:( " + (TextUtils.isEmpty(tableName) ? "0" : tableName) + " ) ");
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));

                String customer = order.getCustomerAmount() + "";
                printerInterface.printRow(createRow(false, 1, "客人数:" + (TextUtils.isEmpty(customer) ? "0" : customer)));
            } else {
                String cardNumberType = "";
                String callNumber = "";
                if (StoreInfor.cardNumberMode) {
                    cardNumberType = "餐牌号:";
                    callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
                } else {
                    cardNumberType = "取餐号:";
                    callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
                }
                TextRow rowNum = createRow(true, 2, ToSBC(cardNumberType + callNumber));
                rowNum.setScaleHeight(4);
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));
                rowNum = createRow(true, 2, "就餐方式:" + orderType);
                rowNum.setScaleHeight(4);
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));

                if (ToolsUtils.getCustomerInfoForWaimai() && type.equals("SALE_OUT")) {
                    rowNum = createRow(true, 2, "顾客姓名:" + order.getCustomerName());
                    rowNum.setScaleWidth(1);
                    rowNum.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowNum);

                    rowNum = createRow(true, 2, "顾客电话:" + order.getCustomerPhoneNumber());
                    rowNum.setScaleWidth(1);
                    rowNum.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowNum);

                    rowNum = createRow(true, 2, "顾客地址:" + order.getCustomerAddress());
                    rowNum.setScaleWidth(1);
                    rowNum.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowNum);

                    rowNum = createRow(true, 2, "外卖配送费: ￥" + getShippingFee(order.getShippingFee()).toString());
                    rowNum.setScaleWidth(1);
                    rowNum.setAlign(Alignment.LEFT);
                    printerInterface.printRow(rowNum);

                }
            }

            printerInterface.printRow(createRow(false, 1, "时间: " + getTimeStr(order.getCreatedAt())));
            if (ToolsUtils.getCustomerInfoForWaimai() && type.equals("SALE_OUT")) {
                printerInterface.printRow(createRow(false, 1, "外卖订单号: " + order.getOuterOrderid()));
            } else {
                printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
            }
            printerInterface.printRow(createRow(false, 1, "收银员: " + UserData.getInstance(context).getRealName()));

            printerInterface.printRow(new Separator("-"));

            String dishTitle = PrintUtils.getStr("菜品", "数量", "金额", 50);
            printerInterface.printRow(createRow(false, 1, dishTitle));
            printerInterface.printRow(new Separator("-"));

            List<OrderItem> itemList = order.getItemList();
            for (int i = 0; i < itemList.size(); i++) {
                OrderItem orderItem = itemList.get(i);
                if (ToolsUtils.getIsPackage(orderItem)) {// 套餐菜品
                    BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN);

                    String oi = PrintUtils.getStr("※" + orderItem.getDishName(), orderItem.quantity + "", money.toString(), 50);
                    TextRow oiRow = createRow(false, 2, oi);
                    oiRow.setScaleWidth(1);
                    oiRow.setScaleHeight(2);
                    printerInterface.printRow(oiRow);

                    // 套餐子项
                    List<Dish.Package> subItemList = orderItem.getSubItemList();
                    for (int a = 0; a < subItemList.size(); a++) {
                        String oiSub = PrintUtils.getStr("  " + subItemList.get(a).getDishName(), subItemList.get(a).quantity * orderItem.quantity + "", "0.0", 50);

                        TextRow oiSubRow = createRow(false, 1, oiSub);
                        printerInterface.printRow(oiSubRow);

                        printDishPackageOption(false,printerInterface,subItemList.get(a));
                    }

                } else {
                    BigDecimal price = orderItem.getCost();
                    price = price == null ? new BigDecimal(0) : price;
                    BigDecimal money = price.multiply(new BigDecimal(orderItem.getQuantity())).setScale(2, BigDecimal.ROUND_DOWN);

                    String oi = PrintUtils.getStr(orderItem.getDishName(), orderItem.getQuantity() + "", money.toString(), 50);
                    TextRow oiRow = createRow(false, 2, oi);
                    oiRow.setScaleWidth(1);
                    oiRow.setScaleHeight(2);
                    printerInterface.printRow(oiRow);

                    printDishOption(false,printerInterface,orderItem.optionList);
                }

            }

            printerInterface.printRow(new Separator("-"));

            //            String ticktTotal = PrintUtils.getStr("小计", "", order.getTotal() , 50);
            //            printerInterface.printRow(createRow(false, 1, ticktTotal));


            if (!ToolsUtils.logicIsTable()) {
                String serviceMoney = PrintUtils.getStr(ToolsUtils.returnXMLStr("service_charge"), "", order.getServiceMoney().toString(), 50);
                printerInterface.printRow(createRow(false, 1, serviceMoney));
            }

            if (type.equals("TAKE_OUT") || type.equals("SALE_OUT")) {
                String takeMoney = PrintUtils.getStr("打包费", "", order.getTake_money().toString(), 50);
                printerInterface.printRow(createRow(false, 1, takeMoney));
            }
            if (type.equals("SALE_OUT")) {
                String saleMoney = PrintUtils.getStr("配送费", "", getShippingFee(order.getShippingFee()).toString() + "", 50);
                printerInterface.printRow(createRow(false, 1, saleMoney));
            }

            String activeMoney = PrintUtils.getStr("优惠", "", order.getAvtive_money().toString(), 50);
            printerInterface.printRow(createRow(false, 1, activeMoney));
            if (!TextUtils.isEmpty(order.getAvtiveName())) {
                printerInterface.printRow(createRow(false, 1, "营销活动: " + order.getAvtiveName()));
            }

            BigDecimal countMoney = new BigDecimal(order.getCost()).add(getShippingFee(order.getShippingFee()));
            String totalMoney = PrintUtils.getStr("合计", "", countMoney.toString(), 50);
            TextRow mRow = createRow(false, 1, totalMoney);
            mRow.setScaleWidth(1);
            mRow.setScaleHeight(1);
            printerInterface.printRow(mRow);
            printerInterface.printRow(new Separator("-"));

            if (order.getPrinterType() != Constant.EventState.ORDER_TYPE_ADVANCE) {
                //            if(order.getPay_money().compareTo(BigDecimal.ZERO)==1){
                String payMoney = PrintUtils.getStr("实收", "", order.getPay_money().setScale(2, BigDecimal.ROUND_DOWN).toString(), 50);
                printerInterface.printRow(createRow(false, 1, payMoney));
                //            }

                //            if(order.getGive_money().compareTo(BigDecimal.ZERO)!=0){
                String giveMoney = PrintUtils.getStr("找零", "", order.getGive_money().setScale(2, BigDecimal.ROUND_DOWN).toString(), 50);
                printerInterface.printRow(createRow(false, 1, giveMoney));
                //            }

                printerInterface.printRow(new Separator("-"));

                if (order.getPaymentList() != null && order.getPaymentList().size() > 0) {
                    printerInterface.printRow(createRow(false, 1, "支付方式"));
                    printerInterface.printRow(new Separator("-"));

                    for (PaymentList paymentList : order.getPaymentList()) {
                        int paymentTypeId = paymentList.getPaymentTypeId();
                        Payment payment = StoreInfor.getPaymentById(paymentTypeId);
                        if(payment != null)
                        {
                            String payType = PrintUtils.getStr(payment.getName(), "", paymentList.getValue().setScale(2, BigDecimal.ROUND_DOWN).toString(), 50);
                            printerInterface.printRow(createRow(false, 1, payType));
                        }
                    }
                    printerInterface.printRow(new Separator(" "));
                }

                if (!TextUtils.isEmpty(order.getPaymentNo())) {
                    String paymentNo = PrintUtils.getStr("电子流水号", "", order.getPaymentNo(), 50);
                    printerInterface.printRow(createRow(false, 1, paymentNo));
                    printerInterface.printRow(new Separator(" "));
                }
            }

            if (order.getPrinterType() != Constant.EventState.ORDER_TYPE_ADVANCE && StoreInfor.printInvoiceBarcode) {
                TextRow rowNum = createRow(true, 1, "扫描下面二维码获取电子发票");
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));

                Bitmap bitmap = null;
                Bitmap qrcode = CreateImage.creatQRImage(getBarcodeUrl(order.getId() + ""), bitmap,250, 250);
                boolean isEnter = true;//是否打印回车
                if (receiptPrinter != null) {
                    isEnter = true;
                } else {
                    isEnter = false;
                }
                printerInterface.printBmp(new BitmapRow(qrcode), isEnter);
            }

            printerInterface.printRow(new Separator(" "));
            printerInterface.printRow(createRow(false, 1, "谢谢惠顾！"));
            printerInterface.printRow(createRow(false, 1, "请仔细核对收银票据，找零及随时物品，离柜概不负责。"));
            String address = StoreInfor.address;
            if (!TextUtils.isEmpty(address)) {
                printerInterface.printRow(createRow(false, 1, "门店地址:" + address));
            }

            printerInterface.printRow(new Separator(" "));
            printerInterface.close();
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_ORDER, order, receiptPrinter));
        }
    }

    //打印退菜单
    public void printRefundDish(Order order) {
        if (receiptPrinter == null) {
            MyApplication.getInstance().ShowToast("后台未配置小票打印机");
            return;
        }
        PrinterInterface printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(receiptPrinter.getVendor()), receiptPrinter.getIp(), receiptPrinter.getWidth());
        if (printerInterface == null) {
            //            MyApplication.getInstance().ShowToast("请打开蓝牙");
            return;
        }
        try {

            printerInterface.init();

            //            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
            //            printerInterface.printRow(new BitmapRow(bitmap));
            String storeName = "";
            if (!TextUtils.isEmpty(StoreInfor.storeName)) {
                storeName = StoreInfor.storeName;
            }

            TextRow rowStore = createRow(false, 2, "欢迎光临 " + storeName);
            rowStore.setScaleHeight(1);
            rowStore.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowStore);
            TextRow rowNum = createRow(true, 2, "退菜单");
            rowNum.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowNum);
            printerInterface.printRow(createRow(false, 1, "时间: " + getTimeStr(order.getCreatedAt())));
            printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
            printerInterface.printRow(createRow(false, 1, "收银员: " + UserData.getInstance(context).getRealName()));

            printerInterface.printRow(new Separator("-"));

            String dishTitle = PrintUtils.getStr("菜品", "数量", "金额", 50);
            printerInterface.printRow(createRow(false, 1, dishTitle));
            printerInterface.printRow(new Separator("-"));

            List<OrderItem> itemList = order.getItemList();
            BigDecimal dishCost = new BigDecimal("0.00");
            for (int i = 0; i < itemList.size(); i++) {
                OrderItem orderItem = itemList.get(i);
                if (ToolsUtils.getIsPackage(orderItem)) {// 套餐菜品
                    if (orderItem.getRejectedQuantity() > 0) {
                        BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN);
                        dishCost = dishCost.add(money);

                        String oi = PrintUtils.getStr("※" + orderItem.getDishName(), "-" + orderItem.getRejectedQuantity() + "", money.toString() + "", 50);
                        TextRow oiRow = createRow(false, 1, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(1);
                        printerInterface.printRow(oiRow);

                        // 套餐子项
                        List<Dish.Package> subItemList = orderItem.getSubItemList();
                        for (int a = 0; a < subItemList.size(); a++) {
                            String oiSub = PrintUtils.getStr("  " + subItemList.get(a).getDishName(), "-" + subItemList.get(a).quantity * orderItem.getRejectedQuantity() + "", "0.0", 50);

                            TextRow oiSubRow = createRow(false, 1, oiSub);
                            printerInterface.printRow(oiSubRow);
                        }
                    }

                } else {
                    if (orderItem.getRejectedQuantity() > 0) {
                        BigDecimal price = orderItem.getPrice();
                        price = price == null ? new BigDecimal(0) : price;
                        BigDecimal money = price.multiply(new BigDecimal(orderItem.getRejectedQuantity())).setScale(2, BigDecimal.ROUND_DOWN);

                        String oi = PrintUtils.getStr(orderItem.getDishName(), "-" + orderItem.getRejectedQuantity() + "", money.toString() + "", 50);
                        dishCost = dishCost.add(money);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(1);
                        printerInterface.printRow(oiRow);
                    }
                }

            }

            printerInterface.printRow(new Separator("-"));

            String ticktTotal = PrintUtils.getStr("小计", "", dishCost + "", 50);

            printerInterface.printRow(createRow(false, 1, ticktTotal));

            if (!ToolsUtils.logicIsTable()) {
                String serviceMoney = PrintUtils.getStr("服务费", "", order.getServiceMoney().toString(), 50);
                printerInterface.printRow(createRow(false, 1, serviceMoney));
            }

            String activeMoney = PrintUtils.getStr("优惠", "", order.getAvtive_money().toString(), 50);
            printerInterface.printRow(createRow(false, 1, activeMoney));

            String totalMoney = PrintUtils.getStr("合计", "", order.getCost() + "", 50);
            TextRow mRow = createRow(false, 1, totalMoney);
            mRow.setScaleWidth(1);
            mRow.setScaleHeight(1);
            printerInterface.printRow(mRow);

            printerInterface.printRow(new Separator("-"));

            printerInterface.printRow(new Separator(" "));
            printerInterface.printRow(createRow(false, 1, "谢谢惠顾！"));
            printerInterface.printRow(createRow(false, 1, "请仔细核对收银票据，找零及随时物品，离柜概不负责。"));
            printerInterface.printRow(new Separator(" "));

            printerInterface.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //打印退单
    public void printRefundOrder(Order order) {
        Log.i("打印退单", "<<======printRefunOrder");
        Log.i("订单Id==orderId==", order.getId() + "");
        PrinterInterface printerInterface = logicInitPrint();
        if (printerInterface == null) {
            return;
        }
        try {

            printerInterface.init();
            //            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.logo);
            //            printerInterface.printRow(new BitmapRow(bitmap));
            String storeName = "";
            if (!TextUtils.isEmpty(StoreInfor.storeName)) {
                storeName = StoreInfor.storeName;
            }
            TextRow rowStore = createRow(false, 2, "欢迎光临 " + storeName);
            rowStore.setScaleHeight(2);
            rowStore.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowStore);
            TextRow rowNum = createRow(true, 2, "退单");
            rowNum.setAlign(Alignment.CENTER);
            printerInterface.printRow(rowNum);


            printerInterface.printRow(createRow(false, 1, "时间: " + getTimeStr(order.getCreatedAt())));
            printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
            printerInterface.printRow(createRow(false, 1, "收银员: " + UserData.getInstance(context).getRealName()));

            printerInterface.printRow(new Separator("-"));

            String dishTitle = PrintUtils.getStr("菜品", "数量", "金额", 50);
            printerInterface.printRow(createRow(false, 1, dishTitle));
            printerInterface.printRow(new Separator("-"));

            List<OrderItem> itemList = order.getItemList();
            BigDecimal dishCost = new BigDecimal("0.00");
            for (int i = 0; i < itemList.size(); i++) {
                OrderItem orderItem = itemList.get(i);
                if (ToolsUtils.getIsPackage(orderItem)) {// 套餐菜品
                    BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN);
                    //                    dishCost = dishCost.add(money);

                    String oi = PrintUtils.getStr("※" + orderItem.getDishName(), "-" + orderItem.getQuantity() + "", money.toString() + "", 50);
                    TextRow oiRow = createRow(false, 1, oi);
                    oiRow.setScaleWidth(1);
                    oiRow.setScaleHeight(1);
                    printerInterface.printRow(oiRow);

                    // 套餐子项
                    List<Dish.Package> subItemList = orderItem.getSubItemList();
                    for (int a = 0; a < subItemList.size(); a++) {
                        String oiSub = PrintUtils.getStr("  " + subItemList.get(a).getDishName(), "-" + (subItemList.get(a).quantity * orderItem.getQuantity()) + "", "0.0", 50);

                        TextRow oiSubRow = createRow(false, 1, oiSub);
                        printerInterface.printRow(oiSubRow);

                        printDishPackageOption(false,printerInterface,subItemList.get(a));
                    }

                } else {
                    BigDecimal price = orderItem.getPrice();
                    price = price == null ? new BigDecimal(0) : price;
                    BigDecimal money = price.multiply(new BigDecimal(orderItem.getQuantity())).setScale(2, BigDecimal.ROUND_DOWN);

                    String oi = PrintUtils.getStr(orderItem.getDishName(), "-" + orderItem.getQuantity() + "", money.toString() + "", 50);
                    //                    dishCost = dishCost.add(money);
                    TextRow oiRow = createRow(false, 1, oi);
                    oiRow.setScaleWidth(1);
                    oiRow.setScaleHeight(1);
                    printerInterface.printRow(oiRow);

                    printDishOption(false,printerInterface,orderItem.optionList);
                }

            }

            printerInterface.printRow(new Separator("-"));

            if (!TextUtils.isEmpty(order.getCost()) && new BigDecimal(order.getCost()).compareTo(BigDecimal.ZERO) == 1) {
                dishCost = new BigDecimal(order.getCost());
            } else {
                if (!TextUtils.isEmpty(order.getTotal())) {
                    dishCost = new BigDecimal(order.getTotal());
                }
            }
            String totalMoney = PrintUtils.getStr("合计", "", dishCost.toString() + "", 50);
            TextRow mRow = createRow(false, 1, totalMoney);
            mRow.setScaleWidth(1);
            mRow.setScaleHeight(1);
            printerInterface.printRow(mRow);

            printerInterface.printRow(new Separator("-"));

            printerInterface.printRow(new Separator(" "));
            printerInterface.printRow(createRow(false, 1, "谢谢惠顾！"));
            printerInterface.printRow(createRow(false, 1, "请仔细核对收银票据，找零及随时物品，离柜概不负责。"));
            printerInterface.printRow(new Separator(" "));

            printerInterface.close();
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_REFUND_ORDER, order, receiptPrinter));
        }
    }

    //打印交接班小票
    public void printWorkShift(WorkShiftReport workShiftReport) {
        Log.i("打印交接班小票", "<<======printWorkShift");
        PrinterInterface printerInterface = logicInitPrint();
        if (printerInterface == null) {
            return;
        }
        PosInfo posInfo = PosInfo.getInstance();
        printWorkShiftReceipt(printerInterface, workShiftReport, posInfo.getDefinition());
    }


    public static void main(String[] args) {
        PrintManager pm = new PrintManager();
        Printer printer = new Printer();
        ArrayList<ReceiptType> receipTypeList = new ArrayList<ReceiptType>();
        receipTypeList.add(ReceiptType.ORDER_RECEIPT);
        printer.setReceiptTypeList(receipTypeList);
        printer.setVendor(PrinterVendor.EPSON.getName());
        printer.setWidth(PrinterWidth.WIDTH_80MM);
        printer.setIp("192.168.1.114");

        pm.addPrinter(printer);

        //        pm.printOrder(pm.getTemplateOrder(), PrintManager.getInstance().getPrinterList());
    }

    //////////////////////////////////////////////////////////////////
    //打印客用单
    public void printGuestOrder(Order order) {
        Log.i("打印客用单", "<<======printGuestOrder");
        Log.i("订单Id==orderId==", order.getId() + "");
        PrinterInterface printerInterface = logicInitPrint();
        if (printerInterface == null) {
            return;
        }
        try {
            printerInterface.init();
            String type = order.getOrderType();
            String orderType = "堂食";
            if (type.equals("EAT_IN")) {
                orderType = "堂食";
            } else if (type.equals("SALE_OUT")) {
                orderType = "外卖";
            } else if (type.equals("TAKE_OUT")) {
                orderType = "外带";
            }
            String storeName = "";
            if (!TextUtils.isEmpty(StoreInfor.storeName)) {
                storeName = StoreInfor.storeName;
            }
            PosInfo posInfo = PosInfo.getInstance();
            TextRow rowStore = null;
            if (order.getPrinterType() != Constant.JsToAndroid.JS_A_ADDDISH)//加菜标识
            {
                rowStore = createRow(false, 2, "欢迎光临 " + storeName);
                rowStore.setScaleHeight(2);
                rowStore.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowStore);
                printerInterface.printRow(new Separator(" "));
            }
            if (order.getPrinterType() == Constant.JsToAndroid.JS_A_ADDDISH)//加菜标识
            {
                rowStore = createRow(false, 2, "加菜单");
                rowStore.setScaleHeight(2);
                rowStore.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowStore);
                printerInterface.printRow(new Separator(" "));
            }
            if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST)//退菜标识
            {
                rowStore = createRow(false, 2, "退菜单");
                rowStore.setScaleHeight(2);
                rowStore.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowStore);
                printerInterface.printRow(new Separator(" "));
            }
            if (!TextUtils.isEmpty(order.getSource())) {
                TextRow rowNum = createRow(true, 2, order.getSource());
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));
            }
            if (ToolsUtils.logicIsTable()) {
                String tableName = order.getTableNames();
                if (!TextUtils.isEmpty(tableName)) {
                    TextRow rowNum = createRow(true, 2, "桌台号:( " + (TextUtils.isEmpty(tableName) ? "0" : tableName) + " ) ");
                    rowNum.setAlign(Alignment.CENTER);
                    printerInterface.printRow(rowNum);
                    printerInterface.printRow(new Separator(" "));
                }

                if (order.getTableStyle() != Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
                    String customer = order.getCustomerAmount() + "";
                    printerInterface.printRow(createRow(false, 1, "客人数:" + (TextUtils.isEmpty(customer) ? "0" : customer)));
                }
            } else {
                String cardNumberType = "";
                String callNumber = "";
                if (StoreInfor.cardNumberMode) {
                    cardNumberType = "餐牌号:";
                    callNumber = (TextUtils.isEmpty(order.getTableNames()) ? "0" : order.getTableNames());
                } else {
                    cardNumberType = "取餐号:";
                    callNumber = (TextUtils.isEmpty(order.getCallNumber()) ? "0" : order.getCallNumber());
                }
                TextRow rowNum = createRow(true, 2, ToSBC(cardNumberType + callNumber));
                rowNum.setScaleHeight(4);
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));


                rowNum = createRow(true, 2, "就餐方式:" + orderType);
                rowNum.setScaleHeight(4);
                rowNum.setAlign(Alignment.CENTER);
                printerInterface.printRow(rowNum);
                printerInterface.printRow(new Separator(" "));
                //                ToolsUtils.getCustomerInfoForWaimai() &&
            }
            if (type.equals("SALE_OUT")) {
                TextRow rowNum = createRow(true, 2, "顾客姓名:" + order.getCustomerName());
                rowNum.setAlign(Alignment.LEFT);
                rowNum.setScaleWidth(1);
                printerInterface.printRow(rowNum);

                rowNum = createRow(true, 2, "顾客电话:" + order.getCustomerPhoneNumber());
                rowNum.setScaleWidth(1);
                rowNum.setAlign(Alignment.LEFT);
                printerInterface.printRow(rowNum);

                rowNum = createRow(true, 2, "顾客地址:" + order.getCustomerAddress());
                rowNum.setScaleWidth(1);
                rowNum.setAlign(Alignment.LEFT);
                printerInterface.printRow(rowNum);

                rowNum = createRow(true, 2, "外卖配送费: ￥" + getShippingFee(order.getShippingFee()));
                rowNum.setScaleWidth(1);
                rowNum.setAlign(Alignment.LEFT);
                printerInterface.printRow(rowNum);
            }
            printerInterface.printRow(createRow(false, 1, "时间: " + getTimeStr(order.getCreatedAt())));
            if (ToolsUtils.getCustomerInfoForWaimai() && type.equals("SALE_OUT")) {
                printerInterface.printRow(createRow(false, 1, "外卖订单号: " + order.getOuterOrderid()));
            } else {
                printerInterface.printRow(createRow(false, 1, "订单号: " + order.getId()));
            }

            String dishTitle = PrintUtils.getStr("菜品", "数量", "金额", 50);
            printerInterface.printRow(createRow(false, 1, dishTitle));
            printerInterface.printRow(new Separator("-"));

            List<OrderItem> itemList = order.getItemList();
            BigDecimal dishCount = new BigDecimal("0.00");
            for (int i = 0; i < itemList.size(); i++) {
                OrderItem orderItem = itemList.get(i);
                if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH_GUEST) {
                    if (ToolsUtils.getIsPackage(orderItem)) {// 套餐菜品
                        BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN);
                        String dishRejectedQuantity = getDishRejectedQuantity(orderItem.getRejectedQuantity() + "");
                        money = money.multiply(new BigDecimal(dishRejectedQuantity)).setScale(2, BigDecimal.ROUND_DOWN);
                        dishCount = dishCount.add(money);
                        String zc = "";
                        if (money.compareTo(BigDecimal.ZERO) != 1) {
                            zc = "(赠)";
                        }
                        String oi = PrintUtils.getStr("※" + orderItem.getDishName() + zc, "-" + dishRejectedQuantity + "", money.toString(), 50);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(2);
                        printerInterface.printRow(oiRow);

                        // 套餐子项
                        List<Dish.Package> subItemList = orderItem.getSubItemList();
                        for (int a = 0; a < subItemList.size(); a++) {
                            String oiSub = PrintUtils.getStr("  " + subItemList.get(a).getDishName(), "-" + subItemList.get(a).quantity * orderItem.getRejectedQuantity() + "", "0.0", 50);

                            TextRow oiSubRow = createRow(false, 1, oiSub);
                            printerInterface.printRow(oiSubRow);

                            dishCount = dishCount.add(printDishPackageOption(false,printerInterface,subItemList.get(a)));
                        }

                    } else {
                        BigDecimal price = orderItem.getCost();
                        price = price == null ? new BigDecimal(0) : price;
                        String dishRejectedQuantity = getDishRejectedQuantity(orderItem.getRejectedQuantity() + "");
                        BigDecimal money = price.multiply(new BigDecimal(dishRejectedQuantity)).setScale(2, BigDecimal.ROUND_DOWN);
                        dishCount = dishCount.add(money);
                        String zc = "";
                        if (money.compareTo(BigDecimal.ZERO) != 1 && !orderItem.getDishName().contains("套")) {
                            zc = "(赠)";
                        }
                        String oi = PrintUtils.getStr(orderItem.getDishName() + zc, "-" + dishRejectedQuantity + "", money.toString(), 50);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(2);
                        printerInterface.printRow(oiRow);

                        dishCount = dishCount.add(printDishOption(false,printerInterface,orderItem.optionList));
                    }
                } else {
                    // 套餐菜品
                    if (ToolsUtils.getIsPackage(orderItem)) {
//                        System.out.println(ToolsUtils.getPrinterSth(orderItem));
                        BigDecimal money = orderItem.getCost().setScale(2, BigDecimal.ROUND_DOWN);
                        dishCount = dishCount.add(money);
                        String zc = "";
                        if (money.compareTo(BigDecimal.ZERO) != 1) {
                            zc = "(赠)";
                        }
                        String oi = PrintUtils.getStr("※" + orderItem.getDishName() + zc, orderItem.quantity + "", money.toString(), 50);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(2);
                        printerInterface.printRow(oiRow);

                        // 套餐子项
                        List<Dish.Package> subItemList = orderItem.getSubItemList();
                        for (int a = 0; a < subItemList.size(); a++) {
                            String oiSub = PrintUtils.getStr("  " + subItemList.get(a).getDishName(), subItemList.get(a).quantity * orderItem.quantity + "", "0.0", 50);
                            TextRow oiSubRow = createRow(false, 1, oiSub);
                            printerInterface.printRow(oiSubRow);

                            dishCount = dishCount.add(printDishPackageOption(false,printerInterface,subItemList.get(a)));
                        }

                    } else {
                        BigDecimal price = orderItem.getCost();
                        price = price == null ? new BigDecimal(0) : price;
                        BigDecimal money = price.multiply(new BigDecimal(orderItem.getQuantity())).setScale(2, BigDecimal.ROUND_DOWN);
                        dishCount = dishCount.add(money);
                        String zc = "";
                        if (money.compareTo(BigDecimal.ZERO) != 1) {
                            zc = "(赠)";
                        }
                        String oi = PrintUtils.getStr(orderItem.getDishName() + zc, orderItem.getQuantity() + "", money.toString(), 50);
                        TextRow oiRow = createRow(false, 2, oi);
                        oiRow.setScaleWidth(1);
                        oiRow.setScaleHeight(2);
                        printerInterface.printRow(oiRow);

                        dishCount = dishCount.add(printDishOption(false,printerInterface,orderItem.optionList));
                    }
                }
            }
            if (order.getShippingFee() != null && order.getShippingFee().compareTo(new BigDecimal("0")) == 1) {
                String oi = PrintUtils.getStr("外卖配送费", "", "" + order.getShippingFee(), 50);
                TextRow oiRow = createRow(false, 2, oi);
                oiRow.setScaleWidth(1);
                oiRow.setScaleHeight(2);
                printerInterface.printRow(oiRow);
            }
            BigDecimal countMoney = dishCount;
            if (order.getShippingFee() != null) {
                countMoney = dishCount.add(order.getShippingFee());
            }
            String totalMoney = PrintUtils.getStr("合计", "", countMoney.toString(), 50);
            TextRow mRow = createRow(false, 1, totalMoney);
            mRow.setScaleWidth(1);
            mRow.setScaleHeight(1);
            printerInterface.printRow(mRow);

            printerInterface.printRow(new Separator("-"));
            if (order.getPrinterType() != Constant.JsToAndroid.JS_A_ADDDISH)//加菜标识
            {
                String address = StoreInfor.address;
                if (!TextUtils.isEmpty(address)) {
                    printerInterface.printRow(createRow(false, 1, "门店地址:" + address));
                }
            }
            printerInterface.printRow(new Separator(" "));

            printerInterface.close();
        } catch (IOException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new PosEvent(Constant.EventState.ERR_PRINT_GUEST_ORDER, order, receiptPrinter));
        }
    }

    /**
     * 打印普通菜品中的定制项
     * @param printerInterface
     * @param optionList
     */
    private BigDecimal printDishOption(boolean isKitReceipt,PrinterInterface printerInterface,List<Option> optionList)
    {
        BigDecimal dishOption = new BigDecimal("0.00");
        try {
            if(printerInterface != null && optionList != null && optionList.size() >0)
            {
                if (optionList != null && optionList.size() > 0) {
                    StringBuffer sb = new StringBuffer();
                    for (Option option : optionList) {
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                            sb.append(option.name + "、");
                        } else {
                            dishOption = dishOption.add(option.getPrice());
                            sb.append(option.name + "(" + option.getPrice() + "元)、");
                        }
                    }
                    TextRow oiRow = createRow(false, 1, "    " + sb.toString());
                    if(isKitReceipt)
                    {
                        oiRow.setScaleWidth(2);
                        oiRow.setScaleHeight(2);
                    }
                    printerInterface.printRow(oiRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dishOption;
    }

    /**
     * 打印套餐项中的定制项
     */
    private BigDecimal printDishPackageOption(boolean isKitReceipt,PrinterInterface printerInterface,Dish.Package subitem)
    {
        BigDecimal dishOption = new BigDecimal("0.00");
        if(subitem != null && subitem.optionList != null && subitem.optionList.size() >0)
        {
            dishOption = dishOption.add(printDishOption(isKitReceipt,printerInterface,subitem.optionList));
        }
        return dishOption;
    }

    public GpUsbPrinter getUsbPrinter() {
        return usbPrinter;
    }

    public void setUsbPrinter(GpUsbPrinter usbPrinter) {
        this.usbPrinter = usbPrinter;
    }

    /**
     * @return
     */
    private PrinterInterface logicInitPrint() {
        PrinterInterface printerInterface = null;
        if (receiptPrinter != null) {
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(receiptPrinter.getVendor()), receiptPrinter.getIp(), receiptPrinter.getWidth());
            Log.i("打印机id===" + receiptPrinter.getId(), receiptPrinter.getDescription() + "====" + receiptPrinter.getIp());
        } else {
            printerInterface = PrinterFactory.createBluetoothPrinter(PrinterWidth.WIDTH_80MM);
            Log.i("蓝牙打印机===", "");
        }
        if (printerInterface == null) {
            if (receiptPrinter == null) {
                MyApplication.getInstance().ShowToast("小票打印机打印失败,请打开蓝牙!");
            } else {
                MyApplication.getInstance().ShowToast("未配置小票打印机");
            }
            return null;
        }
        return printerInterface;
    }

    public void printTextStr(Printer printer) {
        PrinterInterface printerInterface = null;
        if (printer != null) {
            printerInterface = PrinterFactory.createPrinter(PrinterVendor.fromName(printer.getVendor()), printer.getIp(), printer.getWidth());
        }
        if (printerInterface == null) {
            MyApplication.getInstance().ShowToast("打印机配置有误!");
            return;
        }
        try {
            printerInterface.init();
            printerInterface.printRow(new Separator("-"));
            TextRow row = createRow(false, 2, "打印机测试");
            row.setAlign(Alignment.CENTER);
            printerInterface.printRow(row);
            printerInterface.printRow(new Separator(" "));

            row = createRow(false, 1, "打印机名称 : " + printer.getDeviceName());
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);

            row = createRow(false, 1, "打印机IP : " + printer.getIp());
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);

            row = createRow(false, 1, "测试日期 : " + getTimeStr(System.currentTimeMillis()));
            row.setAlign(Alignment.LEFT);
            printerInterface.printRow(row);
            printerInterface.close();
        } catch (Exception e) {
            e.printStackTrace();
            MyApplication.getInstance().ShowToast("打印机连接超时,请检查打印机网络或重启打印机!");
        }
    }

    public void printUsbTextStr(Printer printer) {
        if (printer != null) {
            try {
                usbPrinter.setLabelHeight(printer.getLabelHeight());
                usbPrinter.printTestOrder();
            } catch (Exception e) {
                e.printStackTrace();
                MyApplication.getInstance().ShowToast("打印机连接超时,请检查打印机网络或重启打印机!");
            }
        } else {
            MyApplication.getInstance().ShowToast("打印机配置有误!");
        }
    }

    private String getBarcodeUrl(String orderId) {
        PosInfo posInfo = PosInfo.getInstance();
        String mob = "mobilereport/invoice.html?";
        String storeInfo = "appId=" + posInfo.getAppId() + "&brandId=" + posInfo.getBrandId() + "&storeId=" + posInfo.getStoreId() + "&orderId=" + orderId;
        String url = posInfo.getServerUrl() + mob + storeInfo;
        return url;
    }

    private BigDecimal getShippingFee(BigDecimal shippingFee) {
        if (shippingFee != null) {
            return shippingFee;
        }
        return new BigDecimal("0");
    }

    private String getDishRejectedQuantity(String dishRejectedQuantity) {
        if (TextUtils.isEmpty(dishRejectedQuantity) || "0".equals(dishRejectedQuantity)) {
            dishRejectedQuantity = "1";
        }
        return dishRejectedQuantity;
    }

    /**
     * 这个订单是不是退菜模式
     */
    private boolean isLogicRefundDish(Order order) {
        if (order.getTableStyle() == Constant.EventState.PRINTER_RETREAT_DISH) {
            return true;
        }
        return false;
    }

    /**
     * 厨房单是否要显示菜品金额  套餐项
     * @param oi
     * @return
     */
    private String isShowDishPackageMoney(OrderItem oi)
    {
        if(store.getKitMoneyStyle())
        {
            return "   ￥"+oi.getKitDishMoney().toString();
        }
        return "";
    }

    /**
     * 厨房单是否要显示菜品金额  普通菜品
     * @param oi
     * @return
     */
    private String isShowDishMoney(OrderItem oi)
    {
        if(store.getKitMoneyStyle() && !oi.isPackageItem())
        {
            return "   ￥"+oi.getKitDishMoney().toString();
        }
        return "";
    }

    public static String ToSBC(String input)
    {
        //半角转全角：
        char[] c=input.toCharArray();
        for (int i = 0; i < c.length; i++)
        {
            if (c[i]==32)
            {
                c[i]=(char)12288;
                continue;
            }
            if (c[i]<127)
                c[i]=(char)(c[i]+65248);
        }
        return new String(c);
    }

}
