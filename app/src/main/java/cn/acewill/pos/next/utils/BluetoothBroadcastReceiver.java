package cn.acewill.pos.next.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.acewill.pos.next.printer.vendor.BluetoothPrinters;

/**
 * Created by Acewill on 2016/8/22.
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    public BluetoothBroadcastReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothPrinters.addDevice(device.getName(), device);
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED .equals(action)) {

        }
    }
}
