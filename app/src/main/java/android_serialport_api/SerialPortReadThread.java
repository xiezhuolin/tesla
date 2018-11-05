package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Acewill on 2017/1/7.
 */

public abstract class SerialPortReadThread extends Thread {
    private InputStream inputStream;

    public SerialPortReadThread(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        super.run();
        while(!isInterrupted()) {
            int size;
            try {
                byte[] buffer = new byte[64];
                if (inputStream == null) return;
                size = inputStream.read(buffer);
                if (size > 0) {
                    onDataReceived(buffer, size);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    protected abstract void onDataReceived(final byte[] buffer, final int size);
}
