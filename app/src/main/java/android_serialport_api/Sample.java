package android_serialport_api;

import java.io.File;
import java.io.OutputStream;

import cn.acewill.pos.next.printer.leddisplay.LedDisplayCommand;

/**
 * Created by Acewill on 2017/1/7.
 */

public class Sample {
    public static void show() {
        SerialPortFinder finder = new SerialPortFinder();
        String[] devices = finder.getAllDevicesPath();
        for (final String devicePath : devices) {
            try {
                SerialPort serialPort = new SerialPort(new File(devicePath), 2400, 0);
                OutputStream outputStream = serialPort.getOutputStream();
                SerialPortReadThread thread = new SerialPortReadThread(serialPort.getInputStream()) {
                    @Override
                    protected void onDataReceived(byte[] buffer, int size) {
                        System.out.println("received data from serial port: " + devicePath + " data: " + new String(buffer));
                    }
                };
                thread.start();
                outputStream.write(LedDisplayCommand.SHOW_INTEGER_NUMBER(3));
                outputStream.flush();
                // serialPort.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
