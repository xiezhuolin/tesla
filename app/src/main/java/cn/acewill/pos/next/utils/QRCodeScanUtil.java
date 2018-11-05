package cn.acewill.pos.next.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.io.UnsupportedEncodingException;

import cn.acewill.pos.next.service.LogService;

public class QRCodeScanUtil {
	private static D2xxManager ftdid2xx;
	private static FT_Device ftDev;
	private static final int REV_DATA = 0;
	private static int baud = 115200;// 9600;// /* baud rate */
	private static byte stopBits = 1; /* 1:1stop bits, 2:2 stop bits */
	private static byte dataBits = 8; /* 8:8bit, 7: 7bit */
	private static byte parity = 0; /*
									 * 0: none, 1: odd, 2: even, 3: mark, 4:
									 * space
									 */
	private static byte flowControl = 0; /* 0:none, 1: flow control(CTS,RTS) */
	private static boolean isRead;
	public static QRCodeScanUtil instance;
	private Context context;
	private int DevCount = -1;
	private int currentIndex = -1;
	private boolean bReadThreadGoing = false;
	private int openIndex = 0;// 1st usb port is assigned to QRScanner
	private boolean isEnableRead = false;

	private boolean isReady = false;

	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// String TAG = "FragL";
			String action = intent.getAction();
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equalsIgnoreCase(action)) {
				disconnectFunction();

				Toast.makeText(context, "USB_DEVICE_DETACHED in dev",
						Toast.LENGTH_SHORT).show();
				Log.i(LogService.SCAN, "USB_DEVICE_DETACHED in dev");
			} else if (UsbManager.ACTION_USB_DEVICE_ATTACHED
					.equalsIgnoreCase(action)) {

				Toast.makeText(context, "USB_DEVICE_ATTACHED in dev",
						Toast.LENGTH_SHORT).show();
				Log.i(LogService.SCAN, "USB_DEVICE_ATTACHED in dev");
				connectFunction();
			}else{
				Log.i("NOUSB","in dev");
			}

		}
	};

	private D2xxListner d2xxListener = null;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case REV_DATA:
				// Toast.makeText(context, (String)msg.obj, 0).show();
				synchronized (QRCodeScanUtil.this) {
					if (d2xxListener != null) {
						d2xxListener.d2xxListener((String) msg.obj);
					}
				}
				break;

			}
			super.handleMessage(msg);
		}
	};
	private ReadThread readThread;

	public static QRCodeScanUtil getInstance(Context context) {
		if (instance == null) {
			instance = new QRCodeScanUtil(context);
		}
		return instance;
	}

	public boolean startWithRetries(D2xxListner d2xxListener) {
		boolean bRes = false;
		for (int i = 0; i < 3; i++) {
			bRes = start(d2xxListener);
			if (bRes) {
				break;
			} else {
//				try {
//					Thread.currentThread().sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
		}

		return bRes;
	}

	public void stop() {
		setD2xxListener(null);

		try {
			context.unregisterReceiver(mUsbReceiver);
		} catch (Exception ex) {

		}

		if (ftDev != null) {
			disconnectFunction();
		}

		isReady = false;
	}

	public void resetListener() {
		this.setD2xxListener(null);
	}

	private QRCodeScanUtil(Context context) {
		this.context = context;
		d2xxListener = null;

		connectFunction();
	}

	private boolean start(D2xxListner d2xxListener) {
		if (!isReady) {
			stop();
			connectFunction();
		}

		if (isReady) {
			this.setD2xxListener(d2xxListener);
			startThread();
		}

		return isReady;
	}

	private boolean createDeviceList() {
		if (context == null)
			return false;

		int tempDevCount = ftdid2xx.createDeviceInfoList(context);

		if (tempDevCount > 0) {
			if (DevCount != tempDevCount) {
				DevCount = tempDevCount;
			}
		} else {
			DevCount = -1;
			currentIndex = -1;
		}

		return tempDevCount > 0;
	}

	private void disconnectFunction() {
		Log.i(LogService.SCAN, " disconnect");
		DevCount = -1;
		currentIndex = -1;
		this.stopThread();

		if (ftDev != null) {
			isReady = false;
			synchronized (ftDev) {
				if (true == ftDev.isOpen()) {
					ftDev.close();
				}

				ftDev = null;
			}
		}
	}

	private void EnableRead(boolean isEnable) {
		if (ftDev == null)
			return;

		isEnableRead = isEnable;
		if (isEnable) {
			ftDev.purge((byte) (D2xxManager.FT_PURGE_TX));
			ftDev.restartInTask();

		} else {
			ftDev.stopInTask();
		}
	}

	private boolean connectFunction() {
		if(isReady){
			return isReady;
		}
		Log.i(LogService.SCAN, " connect start");
		try {
			ftdid2xx = D2xxManager.getInstance(context);
		} catch (D2xxManager.D2xxException ex) {
			ftdid2xx = null;
			Toast.makeText(context, "扫码枪异常", Toast.LENGTH_SHORT).show();
			System.err.println("扫码枪异常:D2xxManager.getInstance");
			ex.printStackTrace();
			Log.i(LogService.SCAN, " connect getInstance false");
			return false;
		}

		if (ftdid2xx == null || !ftdid2xx.setVIDPID(0x0403, 0xada1)) {
			Toast.makeText(context, "扫码枪异常", Toast.LENGTH_SHORT).show();
			Log.i("ftd2xx-java", "setVIDPID Error");

			System.err.println("扫码枪异常, setVIDPID(0x0403, 0xada1)");
			Log.i(LogService.SCAN, " connect setVIDPID false");
			return false;
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.setPriority(500);
		context.registerReceiver(mUsbReceiver, filter);

		int count = ftdid2xx.createDeviceInfoList(context);
		if (count > 0) {
			// Toast.makeText(context, "dev count :"+count, 0).show();
			ftDev = ftdid2xx.openByIndex(context, openIndex);
			Log.i(LogService.SCAN, "open dev");
		}

		if (ftDev != null && ftDev.isOpen()) {
			EnableRead(true);
		}

		if (ftDev != null) {
			SetConfig(ftDev, baud, dataBits, stopBits, parity, flowControl,
					context);
			Log.i(LogService.SCAN, "open success");
//			this.startThread();

			isReady = true;

			return true;
		} else {
			System.err.println("扫码枪打开端口异常！！！");
			Log.i(LogService.SCAN, "open false");
		}

		return false;
	}

	private void SetConfig(FT_Device ftDev, int baud, byte dataBits,
			byte stopBits, byte parity, byte flowControl, Context context) {
		if (ftDev == null) {
			System.out.println("in SetConfig, ftdev is null");
			return;
		}

		if (ftDev.isOpen() == false) {
			Log.e("j2xx", "SetConfig: device not open");
			return;
		}

		// configure our port
		// reset to UART mode for 232 devices
		ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);

		ftDev.setBaudRate(baud);

		switch (dataBits) {
		case 7:
			dataBits = D2xxManager.FT_DATA_BITS_7;
			break;
		case 8:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		default:
			dataBits = D2xxManager.FT_DATA_BITS_8;
			break;
		}

		switch (stopBits) {
		case 1:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		case 2:
			stopBits = D2xxManager.FT_STOP_BITS_2;
			break;
		default:
			stopBits = D2xxManager.FT_STOP_BITS_1;
			break;
		}

		switch (parity) {
		case 0:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		case 1:
			parity = D2xxManager.FT_PARITY_ODD;
			break;
		case 2:
			parity = D2xxManager.FT_PARITY_EVEN;
			break;
		case 3:
			parity = D2xxManager.FT_PARITY_MARK;
			break;
		case 4:
			parity = D2xxManager.FT_PARITY_SPACE;
			break;
		default:
			parity = D2xxManager.FT_PARITY_NONE;
			break;
		}

		ftDev.setDataCharacteristics(dataBits, stopBits, parity);

		short flowCtrlSetting;
		switch (flowControl) {
		case 0:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		case 1:
			flowCtrlSetting = D2xxManager.FT_FLOW_RTS_CTS;
			break;
		case 2:
			flowCtrlSetting = D2xxManager.FT_FLOW_DTR_DSR;
			break;
		case 3:
			flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
			break;
		default:
			flowCtrlSetting = D2xxManager.FT_FLOW_NONE;
			break;
		}

		// TODO : flow ctrl: XOFF/XOM
		// TODO : flow ctrl: XOFF/XOM
		ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);

		// uart_configured = true;
		// Toast.makeText(context, "Config done", Toast.LENGTH_SHORT).show();
	}

	private void startThread() {
		if (readThread == null || !readThread.isAlive()) {
			readThread = new ReadThread();
			readThread.start();
			bReadThreadGoing = true;
		}

	}

	public void stopThread() {
		bReadThreadGoing = false;
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private class ReadThread extends Thread {
		public ReadThread() {
			this.setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		public void run() {
			int iavailable;
			int readLength = 1000;
			byte[] readBuffer = new byte[readLength + 4];
			// char[] textBuffer = new char[readLength + 4];
			if (ftDev != null && ftDev.isOpen()) {
				// testSend = true;
				// sendString(testCmd);
				int read = ftDev.read(readBuffer, ftDev.getQueueStatus());
				System.out.println("read:"+read);
			}

			Log.i(LogService.SCAN, "start thread");
			while (true == bReadThreadGoing) {
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}

				if (ftDev != null) {
					synchronized (ftDev) {
						iavailable = ftDev.getQueueStatus();
						if (iavailable > 0) {
							if (iavailable > 18) {
								iavailable = 18;
							}

							int read = ftDev.read(readBuffer, iavailable);

							readBuffer[read] = 0;
							if (read > 0) {
								byte[] out = new byte[read];
								System.arraycopy(readBuffer, 0, out, 0, read);

								try {
									String v = new String(out, "GBK");

									if (!v.endsWith("\r\n")) {
										v += "\r\n";
									}
									handler.sendMessage(handler.obtainMessage(
											REV_DATA, v));

									System.out.println("the scanned data is "
											+ v);
									break;
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}

							}
						}
					}
				}
			}
			Log.i("dofun", "stop thread");
		}

	}

	private void setD2xxListener(D2xxListner d2xxListener) {

		synchronized (this) {
			this.d2xxListener = d2xxListener;
		}
	}

	public interface D2xxListner {
		void d2xxListener(String code);
	}
}
