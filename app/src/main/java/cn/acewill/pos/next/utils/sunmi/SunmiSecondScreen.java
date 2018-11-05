package cn.acewill.pos.next.utils.sunmi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.other.SunmiDataBean;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.retrofit.response.ScreenResponse;
import cn.acewill.pos.next.utils.ToolsUtils;
import sunmi.ds.DSKernel;
import sunmi.ds.callback.IConnectionCallback;
import sunmi.ds.callback.IReceiveCallback;
import sunmi.ds.callback.ISendCallback;
import sunmi.ds.callback.ISendFilesCallback;
import sunmi.ds.data.DSData;
import sunmi.ds.data.DSFile;
import sunmi.ds.data.DSFiles;
import sunmi.ds.data.DataPacket;

/**
 * 商米第二屏
 * Created by aqw on 2016/12/4.
 */
public class SunmiSecondScreen {

    private static final String WELCOME_IMG_ID = "WELCOME_IMG_ID";
    private static SunmiSecondScreen sunmiSecondScreen;
    private static DSKernel mDSKernel;
    private static Context context;
    private static long taskId;
    private static DataPacket dsPacket;
    public static final int SCRENN_14 = 1;
    public static final int SCRENN_7 = 2;
    public static final int SCRENN_NULL = 3;

    public static synchronized SunmiSecondScreen getInstance(Context context) {
        if (sunmiSecondScreen == null) {
            sunmiSecondScreen = new SunmiSecondScreen(context);
        }
        return sunmiSecondScreen;
    }

    private SunmiSecondScreen(Context context) {
        this.context = context;
        initSDK(context);
        posInfo = PosInfo.getInstance();
        logoPath = posInfo.getLogoPath();
    }

    IConnectionCallback mConnCallback = new IConnectionCallback() {// SDK链接状态回调
        @Override
        public void onDisConnect() {

        }

        @Override
        public void onConnected(final ConnState state) {
            switch (state) {
                case AIDL_CONN:
                    //与本地service的连接畅通
                    Log.e("SUnmi", "与本地service的连接畅通");
                    break;
                case VICE_SERVICE_CONN:
                    //与副屏service连接畅通aqa1
                    Log.e("SUnmi", "与副屏service连接畅通");
                    break;
                case VICE_APP_CONN:
                    //与副屏app连接畅通
                    Log.e("SUnmi", "与副屏app连接畅通");
                    break;

                default:
                    break;
            }

        }
    };

    IReceiveCallback mReceiveCallback = new IReceiveCallback() {// 接收副屏数据的回调

        @Override
        public void onReceiveFile(DSFile arg0) {
            Log.e("Sunmi", arg0.path);
        }

        @Override
        public void onReceiveFiles(DSFiles dsFiles) {
            Log.e("Sunmi", "onReceiveFiles");
        }

        @Override
        public void onReceiveData(DSData data) {
            Log.e("Sunmi", "onReceiveData");
        }

        @Override
        public void onReceiveCMD(DSData arg0) {
            Log.e("Sunmi", "onReceiveCMD");
        }
    };

    //初始化副屏
    public void initSDK(Context context) {
        mDSKernel = DSKernel.newInstance();
        mDSKernel.init(context, mConnCallback);
        mDSKernel.addReceiveCallback(mReceiveCallback);
    }

    /*****************************
     * 7寸副屏
     *************************/

    //保存图片到SD卡
    public static String saveBitmapToSD() {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/welcom.png";

        File file = new File(filePath);

        try {
            if (!file.exists()) {
                file.createNewFile();
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.show);
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return filePath;
    }

    //保存微信和支付宝生成的支付二维码，用于展示在副屏时使用
    public static void saveWxOrAliQcode(Bitmap bitmap, String title, String content) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/wxalicode.png";
        File file = new File(filePath);
        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

            buildPayMeg(title, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showWelcomImg() {
        taskId = SharedPreferencesUtil.getLong(context, WELCOME_IMG_ID);
        if (taskId != -1L) {
            showImg(taskId);
            return;
        }
        String filePath = saveBitmapToSD();

        mDSKernel.sendFile(DSKernel.getDSDPackageName(), filePath, new ISendCallback() {
            @Override
            public void onSendSuccess(long taskId) {
                Log.e("showImg", "发送");
                showImg(taskId);
                SharedPreferencesUtil.put(context, WELCOME_IMG_ID, taskId);
            }

            @Override
            public void onSendFail(int errorId, String errorInfo) {
                cleanDSD();//显示默认欢迎界面
                Log.e("showImg", errorInfo);
            }

            @Override
            public void onSendProcess(long totle, long sended) {
                Log.e("sendImg", totle + "");
            }
        });
    }

    static void showImg(long fileId) {
        String json = UPacketFactory.createJson(DataModel.SHOW_IMG_WELCOME, "");
        mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);//该命令会让副屏显示图片
    }

    /**
     * 清除副屏数据：显示默认欢迎界面
     */
    public static void cleanDSD() {
        dsPacket = UPacketFactory.buildOpenApp("sunmi.dsd", new ISendCallback() {
            @Override
            public void onSendSuccess(long l) {

            }

            @Override
            public void onSendFail(int i, String s) {

            }

            @Override
            public void onSendProcess(long l, long l1) {

            }
        });
        mDSKernel.sendData(dsPacket);
    }

    /**
     * 向副屏发送数据：标题和内容
     */
    public static void sendData4DSD(String title, String money) {
        String jsonStr = buildData(title, "￥" + money);
        dsPacket = UPacketFactory.buildShowText(
                DSKernel.getDSDPackageName(), jsonStr, new ISendCallback() {
                    @Override
                    public void onSendSuccess(long l) {
                        Log.e("sendData4DSD", "成功了" + l);
                    }

                    @Override
                    public void onSendFail(int i, String s) {
                        Log.e("sendData4DSD", "失败了" + s);
                    }

                    @Override
                    public void onSendProcess(long l, long l1) {
                    }
                });
        mDSKernel.sendData(dsPacket);
    }

    /**
     * 向副屏发送消息
     */
    public static void sendMsg4DSD() {
        String jsonStr = buildData("", "哈哈哈");
        dsPacket = UPacketFactory.buildShowSingleText(
                DSKernel.getDSDPackageName(), jsonStr, new ISendCallback() {
                    @Override
                    public void onSendSuccess(long l) {
                    }

                    @Override
                    public void onSendFail(int i, String s) {
                    }

                    @Override
                    public void onSendProcess(long l, long l1) {
                    }
                });
        mDSKernel.sendData(dsPacket);
    }

    private static String buildData(String title, String content) {
        JSONObject json = new JSONObject();
        try {
            json.put("title", title);
            json.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 向副屏发送图片和文字内容
     *
     * @param title
     * @param content
     */
    public static void buildPayMeg(String title, String content) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/wxalicode.png";
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e("wx_ali_qcode", "文件不存在");
            return;
        }
        mDSKernel.sendFile(DSKernel.getDSDPackageName(), buildData(title, content), filePath, new ISendCallback() {
            @Override
            public void onSendSuccess(long l) {
                showQRCode(l);
            }

            @Override
            public void onSendFail(int i, String s) {

            }

            @Override
            public void onSendProcess(long l, long l1) {

            }
        });
    }

    private static void showQRCode(long taskId) {
        String json = UPacketFactory.createJson(DataModel.QRCODE, "");
        mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, taskId, null);
    }


    /*********************************
     * 14寸副屏
     *******************/

    //全屏只显示复杂的表格字符数据(14寸屏)
    public static void showExcelData(Order order) {

        DataPacket pack = UPacketFactory.buildPack(mDSKernel.getDSDPackageName(), DSData.DataType.DATA, DataModel.TEXT, SunmiDataBean.getJson(order), new ISendCallback() {
            @Override
            public void onSendSuccess(long l) {
                Log.i("14屏表格", "成功了" + l);
            }

            @Override
            public void onSendFail(int i, String s) {
                Log.i("14屏表格", "失败了" + s);
            }

            @Override
            public void onSendProcess(long l, long l1) {
                Log.i("14屏表格", "发送进度" + l1);
            }
        });
        mDSKernel.sendData(pack);
    }


    //屏幕左边显示菜品图片，右边显示复杂的表格数据(14寸屏)
    public static void showDishImgExcel(final Order order) {
        List<OrderItem> marketActLists = order.getItemList();
        if (marketActLists != null && marketActLists.size() > 0) {
            int size = marketActLists.size();
            final OrderItem dish = marketActLists.get(size - 1);
            if (dish != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String imageUrl = "";
                            if(TextUtils.isEmpty(dish.getImageName()))
                            {
                                imageUrl = defaultImg;
                            }
                            else{
                                imageUrl = dish.getImageName();
                            }
                            Bitmap image = getBitmap(imageUrl);
                            String dishImage = saveImage(image, "dishImage");
                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), dishImage, new ISendCallback() {
                                public void onSendSuccess(long fileId) {
                                    Log.i("14屏图", "发送图片成功");
                                    show(fileId, order);//图片发送成功，显示文字内容
                                }

                                public void onSendFail(int errorId, String errorInfo) {
                                }

                                public void onSendProcess(long total, long sended) {
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Bitmap image = getBitmap(defaultImg);
                            String dishImage = saveImage(image, "dishImage");
                            mDSKernel.sendFile(DSKernel.getDSDPackageName(), dishImage, new ISendCallback() {
                                public void onSendSuccess(long fileId) {
                                    Log.i("14屏图", "发送图片成功");
                                    show(fileId, order);//图片发送成功，显示文字内容
                                }

                                public void onSendFail(int errorId, String errorInfo) {
                                }

                                public void onSendProcess(long total, long sended) {
                                }
                            });
                        }
                    }
                }).start();//启动打印线程
            } else {
                showImageListScreen();
            }
        } else {
            showImageListScreen();
        }
    }

    //保存微信和支付宝生成的支付二维码，用于展示在副屏时使用
    public static String saveWxOrAliQcode(Bitmap bitmap) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/wxalicode.png";
        File file = new File(filePath);

        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }

    //屏幕左边显示图片，右边显示复杂的表格数据(14寸屏)
    public static void showImgExcel(Bitmap bitmap, final Order order) {
        mDSKernel.sendFile(DSKernel.getDSDPackageName(), saveWxOrAliQcode(bitmap), new ISendCallback() {
            public void onSendSuccess(long fileId) {
                Log.i("14屏图", "发送图片成功");
                show(fileId, order);//图片发送成功，显示文字内容
            }

            public void onSendFail(int errorId, String errorInfo) {
            }

            public void onSendProcess(long total, long sended) {
            }
        });
    }

    //显示上面图片和表格
    private static void show(long fileId, Order order) {
        String jsonStr = UPacketFactory.createJson(DataModel.SHOW_IMG_LIST, SunmiDataBean.getJson(order));
        //第一个参数DataModel.SHOW_IMG_LIST为显示布局模式，jsonStr为要显示的内容字符
        mDSKernel.sendCMD(mDSKernel.getDSDPackageName(), jsonStr, fileId, null);
    }


    public static String saveImage(Bitmap bitmap, String imgName) {
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + imgName + ".png";
        File file = new File(filePath);
        FileOutputStream fos = null;
        try {
            if (file.exists()) {
                file.delete();
            }

            file.createNewFile();
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            if(fos != null)
            {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            showImageListScreen();
        }
        return filePath;
    }

    private PosInfo posInfo;
    private static int IO_BUFFER_SIZE = 2 * 1024;
    private static String logoPath;
    private static List<String> url_imgs;
    private static List pathList = new ArrayList<>();
    private static String defaultImg;
    private static ScreenResponse screenResponse;

    //显示副屏幕轮播图
    public static void showImageListScreen() {
        if (SunmiSecondScreen.getDeviceType() == SunmiSecondScreen.SCRENN_7) {
            cleanDSD();
        } else {
            if (screenResponse != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (pathList == null || pathList.size() <= 0) {
                            if (screenResponse.getScreenImgs() != null && screenResponse.getScreenImgs().size() > 0) {
                                url_imgs = screenResponse.getScreenImgs();
                            }
                            if (!TextUtils.isEmpty(screenResponse.getDefaultImg())) {
                                defaultImg = screenResponse.getDefaultImg();
                            }
                            else{
                                defaultImg = logoPath;
                            }
                            if (url_imgs == null) {
                                url_imgs = new ArrayList<String>();
                                url_imgs.add(defaultImg);
                            }
                            int size = url_imgs.size();
                            for (int i = 0; i < size; i++) {
                                String imageUrl = url_imgs.get(i);
                                Bitmap image = getBitmap(imageUrl);
                                String welcomeScrImage = saveImage(image, "welcomeScrImage" + i);
                                pathList.add(welcomeScrImage);
                            }
                        }
                        try {
                            JSONObject json = new JSONObject();
                            json.put("rotation_time", 5000); //幻灯片的切换时间，用毫秒计算，如果不传默认是10000毫秒
                            mDSKernel.sendFiles(DSKernel.getDSDPackageName(), json.toString(), pathList, new ISendFilesCallback() {
                                public void onAllSendSuccess(long fileId) {
                                    show(fileId);
                                }

                                public void onSendSuccess(final String s, final long l) {
                                }

                                public void onSendFaile(int errorId, String errorInfo) {
                                }

                                public void onSendFileFaile(String path, int errorId, String errorInfo) {
                                }

                                public void onSendProcess(String path, long total, long sended) {
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();//启动打印线程
            } else {
                cleanDSD();
            }
        }
    }

    public static void setSunMiScreenResponses(ScreenResponse screenResponses) {
        if (screenResponse == null) {
            screenResponse = ToolsUtils.cloneTo(screenResponses);
        }
    }

    private static void show(long fileId) {
        String json = UPacketFactory.createJson(DataModel.IMAGES, "");
        mDSKernel.sendCMD(DSKernel.getDSDPackageName(), json, fileId, null);
    }


    /**
     * 获取副屏类型（14寸、7寸）
     */
    public static int getDeviceType() {
        try {
            Class c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String sn = get.invoke(c, "ro.serialno") + "";
            String sn_pre = sn.substring(0, 4);
            Log.i("sunmi", "the sn:" + sn);
            Log.i("sunmi", "First four characters:" + sn_pre);

            if (sn_pre.equals("T103") || sn_pre.equals("T104")|| sn_pre.equals("T110")) {
                return SCRENN_14;
            }

            if (sn_pre.equals("T105") || sn_pre.equals("T106")|| sn_pre.equals("T109")) {
                return SCRENN_7;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SCRENN_NULL;
    }


    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL iconUrl = new URL(url);
            URLConnection conn = iconUrl.openConnection();
            HttpURLConnection http = (HttpURLConnection) conn;

            int length = http.getContentLength();

            conn.connect();
            // 获得图像的字符流
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is, length);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

}
