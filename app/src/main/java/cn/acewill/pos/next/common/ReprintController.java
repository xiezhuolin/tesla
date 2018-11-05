package cn.acewill.pos.next.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.next.printer.Printer;

/**
 * Created by DHH on 2017/1/8.
 */

public class ReprintController {
    /**
     * 补打列表
     */
    public static List<Printer> rePrinterList = new CopyOnWriteArrayList<>();

    public static List<Printer> getRePrinterList() {
        return rePrinterList;
    }

    public static void setRePrinterList(List<Printer> rePrinterList) {
        ReprintController.rePrinterList = rePrinterList;
    }
}
