package cn.acewill.pos.printer;

import java.io.IOException;

import cn.acewill.pos.next.printer.Alignment;
import cn.acewill.pos.next.printer.Column;
import cn.acewill.pos.next.printer.PrinterFactory;
import cn.acewill.pos.next.printer.PrinterInterface;
import cn.acewill.pos.next.printer.PrinterVendor;
import cn.acewill.pos.next.printer.PrinterWidth;
import cn.acewill.pos.next.printer.Separator;
import cn.acewill.pos.next.printer.Table;
import cn.acewill.pos.next.printer.TextRow;

/**
 * Created by Acewill on 2016/8/17.
 */
public class PrinterTest {


    private static void  printTable(PrinterInterface printer) throws IOException {
        Column section = new Column("表格1");
        TextRow row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setAlign(Alignment.LEFT);
        printer.printRow(row);

        //3列的表格
        Table table = new Table(3);
        TextRow title = new TextRow();
        title.setScaleWidth(2);
        title.setBoldFont(true);
        title.addColumn(new Column("标题1"));
        title.addColumn(new Column("标题2"));
        title.addColumn(new Column("标题3", Alignment.RIGHT));
       // table.setTitle(title);

        row = new TextRow();
        row.addColumn(new Column("左对齐1"));
        row.addColumn(new Column("左对齐2"));
        row.addColumn(new Column("右对齐3",Alignment.RIGHT));
     //   table.addRow(row);

        row = new TextRow();
        row.addColumn(new Column("左对齐1"));
        row.addColumn(new Column("左对齐2"));
        row.addColumn(new Column("右对齐3",Alignment.RIGHT));
    //    table.addRow(row);

     //   table.addRow(new Separator("-"));
    //    table.addRow(new Separator(" "));

        row = new TextRow();
        row.addColumn(new Column("总计:"));
        row.addColumn(new Column("8"));
        row.addColumn(new Column("40.00",Alignment.RIGHT));
        table.addRow(row);

        printer.printTable(table);


        section = new Column("表格2 - 只有一行");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setAlign(Alignment.LEFT);
        printer.printRow(row);

        table = new Table(3);
        row = new TextRow();
        row.addColumn(new Column("总计:"));
        row.addColumn(new Column("8"));
        row.addColumn(new Column("40.00",Alignment.RIGHT));
        table.addRow(row);
        printer.printTable(table);
    }

    public static void main(String[] args) throws IOException {
        PrinterInterface printer = PrinterFactory.createPrinter(PrinterVendor.EPSON,"192.168.1.114", PrinterWidth.WIDTH_80MM);
        printer.init();

       /* Column section = new Column("左对齐");
        TextRow row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setAlign(Alignment.LEFT);
        printer.printRow(row);

        section = new Column("中间对齐");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        printer.printRow(row);

        section = new Column("右对齐");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setAlign(Alignment.RIGHT);
        printer.printRow(row);


        section = new Column("普通大小");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        printer.printRow(row);

        section = new Column("普通大小的2倍");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(2);
        row.setScaleWidth(2);
        printer.printRow(row);

        section = new Column("普通大小的3倍");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(3);
        row.setScaleWidth(4);
        printer.printRow(row);

        section = new Column("普通大小的4倍");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(4);
        row.setScaleWidth(4);
        printer.printRow(row);

        section = new Column("普通大小的5倍");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(5);
        row.setScaleWidth(6);
        printer.printRow(row);

        section = new Column("普通大小的6倍");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(6);
        row.setScaleWidth(6);
        printer.printRow(row);

        section = new Column("粗体");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setBoldFont(true);
        printer.printRow(row);

        section = new Column("不加粗");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setBoldFont(false);
        printer.printRow(row);

        section = new Column("反显打印");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(2);
        row.setScaleWidth(2);
        row.setRevertMode(true);
        printer.printRow(row);

        section = new Column("underline");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setScaleHeight(2);
        row.setScaleWidth(2);
        row.setShowUnderline(true);
        printer.printRow(row);*/

        //   printer.printBarcode("123456789123");
        // printer.printBarcode("*123456*");
        // printer.printBarcode("*123456*");

        /*section = new Column("12345678901234567890123456789012345678901234567890");
        row = new TextRow(section);
        row.setHeight((byte)0x3c);
        row.setAlign(Alignment.LEFT);
        printer.printRow(row);*/



      /*  row = new TextRow();

        row.addColumn(new Column("右对齐1", Alignment.RIGHT));
        row.addColumn(new Column("右对齐2", Alignment.RIGHT));
        row.addColumn(new Column("右对齐3", Alignment.RIGHT));
        table.addRow(row);*/


     //   printTable(printer);

        //打印分隔符
        TextRow row = new TextRow();
        row.setScaleWidth(3);
        row.addColumn(new Column("hahahha"));
        printer.printRow(row);

        byte[] bt = "测试".getBytes("GB2312");

        printer.close();


    }
}
