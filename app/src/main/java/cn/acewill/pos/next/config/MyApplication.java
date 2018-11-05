package cn.acewill.pos.next.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.factory.AppDatabase;
import cn.acewill.pos.next.model.user.User;
import cn.acewill.pos.next.service.LogService;
import cn.acewill.pos.next.service.PrintManager;
import cn.acewill.pos.next.service.UploadOrderThread;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.ToolsUtils;
import woyou.aidlservice.jiuiv5.ICallback;
import woyou.aidlservice.jiuiv5.IWoyouService;

/**
 * Created by Dhh on 2016/1/27. 702 348 281
 */
public class MyApplication extends MultiDexApplication {
    private static Context context;
    public User user;
    private Toast toast;
    private static Resources res;
    private static MyApplication mInstance;

    private Handler handler = new Handler();
    private boolean isWriteLog = true;//是否开启错误日志
    // 关闭或者显示log打印
    private static PackageManager mPackageManager;

    private static PackageInfo mPackageInfo;
    private SharedPreferences spSetting = null;
    private SharedPreferences.Editor editor;
    private LogService logService;
    private boolean isConFirmNetOrder = false;
    public static SoundPool soundPool;
    private static ImageLoader imageLoader;

    public static  boolean isSyncNetOrderInit;
    /**
     * 分割 Dex 支持
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;
        res = getApplicationContext().getResources();
        initImageLoader(getApplicationContext());

        bindCashBoxServer(mInstance);

        //从本地sqlite服务器里获取
        //开始服务器状态线程
        //初始化 DBFlow
        FlowManager.init(new FlowConfig.Builder(getApplicationContext()).build());
        //        //初始化商米副屏
        //        SunmiSecondScreen.getInstance(getApplicationContext());

        DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.NAME);
        AndroidDatabase android = (AndroidDatabase) database.getWritableDatabase();
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_menu('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_all_menu('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_dish_type('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_payment_type('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_order('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_user('id' integer PRIMARY KEY AUTOINCREMENT, 'name' text NOT NULL, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_workshift('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_work_shift_definition('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_storebusiness_information('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_dish_count('id' integer PRIMARY KEY, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_store_configuration('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_bind_terminal_info('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_terminal_info('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_printer_list('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_kds_list('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_kitchenstall_list('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_printer_templates_list('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");
        android.execSQL("CREATE TABLE IF NOT EXISTS cached_screen_info('id' integer PRIMARY KEY AUTOINCREMENT, 'jsonObject' text NOT NULL)");

        if (isWriteLog) {
            // 设置编写错误日志־
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
        mPackageManager = getApplicationContext().getPackageManager();
        try {
            mPackageInfo = mPackageManager.getPackageInfo(
                    this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //心跳线程
//        Thread serverStatusThread = new ServerStatusThread();
//        serverStatusThread.start();

        //后台订单上传接口
        Thread uploadOrderThread = new UploadOrderThread();
        uploadOrderThread.start();

        spSetting = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        editor = spSetting.edit();


        callback = new ICallback.Stub() {
            @Override
            public void onRunResult(boolean isSuccess, int code, String msg) throws RemoteException {

            }
        };

        soundPool= new SoundPool(21, AudioManager.STREAM_SYSTEM,10);
        soundPool.load(this,R.raw.scan_success,1);
    }

    /**
     * 存手机屏幕宽
     *
     * @param screnn_width
     */
    public void setScreenWidth(int screnn_width) {
        editor.putInt("screnn_width", screnn_width);
        editor.commit();
    }

    /**
     * 存手机屏幕高
     *
     * @param screnn_height
     */
    public void setScreenHeight(int screnn_height) {
        editor.putInt("screnn_height", screnn_height);
        editor.commit();
    }


    /**
     * 存台桌展示方式标识
     *
     * @param style
     */
    public void setTableShowStyle(int style) {
        editor.putInt("table_style", style);
        editor.commit();
    }

    /**
     * 取台桌展示方式标识
     *
     * @return style
     */
    public int getTableShowStyle() {
        return spSetting.getInt("table_style", Constant.ShowTableStyle.TABLE_LIST);
    }

    /**
     * 存输入菜品名历史记录
     */
    public void setOrderDishHistory(String order_dish) {
        String old_text = getOrderDishHistory();

        // 利用StringBuilder.append新增内容，逗号便于读取内容时用逗号拆分开
        StringBuilder builder = new StringBuilder(old_text);
        builder.append(order_dish + ",");
        // 判断搜索内容是否已经存在于历史文件，已存在则不重复添加
        if (!ToolsUtils.isNull(order_dish) && !old_text.contains(order_dish + ",")) {
            editor.putString("order_dish", builder.toString());
            editor.commit();
        } else {
            //			ShowToast("已存在");
        }
    }

    public void cleanOrderDishHistory() {
        editor.putString("order_dish", "");
        editor.commit();
    }


    /**
     * 取输入菜品名历史记录
     */
    public String getOrderDishHistory() {
        return spSetting.getString("order_dish", "");
    }

    /**
     * 设置是否支持退菜功能
     *
     * @param tuicai
     */
    public void setIsTuicai(String tuicai) {
        editor.putString("dc_istuicai", tuicai).commit();
    }

    /**
     * 返回是否支持退菜
     *
     * @return
     */
    public String getIsTuicai() {
        return spSetting.getString("dc_istuicai", "是");
    }


    /**
     * @return 返回手机屏幕宽
     */
    public int getScreenWidth() {
        return spSetting.getInt("screnn_width", 0);
    }

    /**
     * @return 返回手机屏幕高
     */
    public int getScreenHeight() {
        return spSetting.getInt("screnn_height", 0);
    }

    public static MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }


    public void ShowToast(String msg) {
        handler.post(new ToastHandler(msg));
    }

    /**
     * 无网络连接时提示
     */
    public void ShowNetWorkError() {
        handler.post(new ToastHandler(getResources().getString(R.string.text_network_error)));
    }

    public void ShowToast(int msgResId) {
        handler.post(new ToastHandler(getResources().getString(msgResId)));
    }


    private class ToastHandler implements Runnable {
        private String msg;

        private ToastHandler(String msg) {
            this.msg = msg;
        }

        public void run() {
            if (toast == null) {
                toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
            } else {
                toast.setText(msg);
            }

            toast.show();
        }
    }

    /**
     * 判断网络是否可用
     *
     * @return true为可用 false为不可用
     */
    public static boolean isNetConnected(Context mContext) {
        if (mContext == null)
            return false;
        // 获取网络连接管理器
        ConnectivityManager connect = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 需要权限 android.permission.ACCESS_NETWORK_STATE
        NetworkInfo mNetInfo = connect.getActiveNetworkInfo();
        if (mNetInfo == null || !mNetInfo.isAvailable()) {
            return false;
        }
        // 获取网络连接类型： mNetworkInfo.getType()
        return true;
    }

    /**
     * 获得当前运行app的版本号
     *
     * @return
     */
    public static String getVersionName() {
        return "V " + mPackageInfo.versionName;
    }

    /**
     * 获得当前运行app的版本号
     *
     * @return
     */
    public static int getVersionCode() {
        return mPackageInfo.versionCode;
    }

    public LogService getLogService() {
        return logService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }


    /**
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * activity跳转
     *
     * @param clas activity类
     */
    public void startActivity(Class<? extends BaseActivity> clas) {
        if (clas == null)
            return;
        Intent intent = new Intent(this, clas);
        startActivity(intent);
    }

    /**
     * 跳转Activity
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
    }

    private List<Activity> activitys;

    /**
     * 将Activity实例添加进Activity栈中管理以便于回收
     *
     * @param activity
     */
    public void addPage(Activity activity) {
        if (activity == null)
            return;
        if (activitys == null)
            activitys = new ArrayList<Activity>();
        activitys.add(activity);
    }

    /**
     * 根据类名移除Activity
     *
     * @param clazz
     */
    public void popClass(Class clazz) {
        for (Activity activity : activitys) {
            if (activity.getClass().equals(clazz)) {
                popActivity(activity);
                break;
            }
        }
    }

    /**
     * 退出栈顶Activity
     *
     * @param activity
     */
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activitys.remove(activity);
            activity = null;
        }
    }

    /**
     * 获取栈顶Activity
     *
     * @return
     */
    public Activity getPopActivity() {
        if (activitys != null && activitys.size() > 0) {
            return activitys.get(0);
        }
        return null;
    }

    /**
     * 关闭栈中所有Activity实例
     */
    public void exit() {
        if (activitys != null)
            for (Activity activity : activitys) {
                if (!activity.isFinishing())
                    activity.finish();
            }
        soundPool.stop(1);
        unbindCashBoxServer();
        mInstance = null;
        System.exit(0);
    }

    public void playSound()
    {
        soundPool.play(1,1, 1, 0, 0,  1f);
    }

    public void clean() {
        if (activitys != null)
            for (Activity activity : activitys) {
                if (!activity.isFinishing())
                    activity.finish();
            }
    }

    public void setUser(User user) {
        this.user = user;
    }

    // 初始化ImageLoader的对象，默认 加在图
    @SuppressWarnings( {"static-access", "deprecation"} )
    public static ImageLoader initImageLoader(Context context) {
        imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.img_dish_default_bg)
                .showImageForEmptyUri(R.mipmap.img_dish_default_bg)
                .showImageOnFail(R.mipmap.img_dish_default_bg).cacheInMemory()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .displayer(new SimpleBitmapDisplayer()).cacheOnDisc()
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        File cacheDir = StorageUtils.getOwnCacheDirectory(context,
                "AcewillPos/Cache");// 缓存地址
        ImageLoaderConfiguration config = (new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 1)
                .threadPoolSize(4).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(1 * 1024 * 100)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCache(new UnlimitedDiscCache(cacheDir))
                .tasksProcessingOrder(QueueProcessingType.LIFO))
                .defaultDisplayImageOptions(options).build();
        imageLoader.getInstance().init(config);
        return imageLoader;
    }

    public static ImageLoader getImageLoader()
    {
        return imageLoader;
    }

    private boolean isShowTokenDia = false;

    public boolean isShowTokenDia() {
        return isShowTokenDia;
    }

    public void setShowTokenDia(boolean showTokenDia) {
        isShowTokenDia = showTokenDia;
    }

    /**
     * 是否是第一次进入选择套餐界面
     */
    public static boolean isFirstOrderMeal = false;

    public static boolean isFirstOrderMeal() {
        return isFirstOrderMeal;
    }

    public static void setIsFirstOrderMeal(boolean isFirstOrderMeal) {
        MyApplication.isFirstOrderMeal = isFirstOrderMeal;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context conText) {
        context = conText;
        PrintManager.getInstance().setContext(context);
        Store.getInstance(context);
    }

    public boolean isConFirmNetOrder() {
        return isConFirmNetOrder;
    }

    public void setConFirmNetOrder(boolean conFirmNetOrder) {
        isConFirmNetOrder = conFirmNetOrder;
    }

    private ICallback callback = null;
    private IWoyouService woyouService;
    private ServiceConnection connService = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            woyouService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            woyouService = IWoyouService.Stub.asInterface(service);
        }
    };

    public void bindCashBoxServer(Context context) {
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        startService(intent);
        bindService(intent, connService, Context.BIND_AUTO_CREATE);
    }

    public void openSunMicashBox() {
        try {
            if(woyouService != null)
            {
                woyouService.openDrawer(callback);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unbindCashBoxServer() {
        try {
            if (connService != null) {
                unbindService(connService);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("MyApplication","unbindService Error");
        }
    }
}
