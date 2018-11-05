package cn.acewill.pos.next.utils;


import java.io.IOException;


/**
 * 佳博网口标签打印
 * Created by aqw on 2016/12/29.
 */
public class GpEnternetPrintTwo {

    public static final int port = 9100;


    public static void gpPrint(String ip, Integer lableHeight) throws IOException {
        GpPrintCommandTwo gp = new GpPrintCommandTwo();

        gp.openport(ip, port);

        gp.setup(60, lableHeight, 4, 4, 0, 2, 0);
        gp.clearbuffer();
        gp.setDirection(1);
        gp.setReference(0, 0);
        gp.setTear("ON");

        String count = "  1/1";
        gp.printerfont(0, 1, 1, "订单号:0010  " + count);//第一行
        gp.printerfont(1, 1, 1, "时间:");//第二行
        gp.printerfont(2, 1, 2, "测试菜品");//第三行
        gp.printerfont(3, 1, 1, "价格:" + "0.01" + "￥");//第四行
        gp.printerfont(4, 1, 1, "定制项:" + "asdf" + "￥");//第四行

        gp.printlabel(1, 1);//打印出缓冲区的数据,第一个参数是打印的份数，第二个是每份打印的张数
        gp.clearbuffer();
        gp.closeport();
        
        

//        printOrderItem(gp, orderItem);
    }


    //打印标签明细


    //获取定制项或做法或口味

}
