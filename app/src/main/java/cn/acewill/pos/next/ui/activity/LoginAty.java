package cn.acewill.pos.next.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.Configure;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.dao.cache.CachedDao;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.factory.CookieInterceptor;
import cn.acewill.pos.next.model.StoreConfiguration;
import cn.acewill.pos.next.model.TerminalInfo;
import cn.acewill.pos.next.model.UserRet;
import cn.acewill.pos.next.model.user.User;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.presenter.LoginPresenter;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.activity.newPos.DishMenuAty;
import cn.acewill.pos.next.ui.activity.newPos.TableMainAty;
import cn.acewill.pos.next.ui.adapter.UserNameAdp;
import cn.acewill.pos.next.ui.presentation.SecondaryPresentation;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/6/12.
 */
public class LoginAty extends BaseActivity implements DialogView {

    @BindView( R.id.login_user_et )
    EditText loginUserEt;
    @BindView( R.id.login_pw_et )
    EditText loginPwEt;
    @BindView( R.id.login_bind )
    TextView loginBind;
    @BindView( R.id.login_user )
    TextView loginUser;
    @BindView( R.id.login_ip )
    TextView loginIp;
    @BindView( R.id.tv_terminalTitle )
    TextView tvTerMinalTitle;
    @BindView( R.id.tv_remember_pw_hint )
    TextView tvRememberPwHine;
    @BindView( R.id.dish_menu )
    TextView dishMenu;
    @BindView( R.id.store_endTime )
    TextView storeEndTime;
    //    @BindView( R.id.wxlogin_wv )
    //    WebView wxlogin_wv;
    //    @BindView( R.id.login_cb )
    //    ImageView loginCb;
    //    @BindView( R.id.zwf_v )
    //    View zwfV;
    @BindView( R.id.login_version )
    TextView loginVersion;
    //    @BindView( R.id.lin_pos )
    //    LinearLayout linPos;
    @BindView( R.id.login_pos_et )
    EditText loginPosEt;
    @BindView( R.id.view_store_info )
    View viewStoreInfo;
    //    @BindView( R.id.toast_title )
    //    TextView toastTitle;
    //    @BindView( R.id.lin_userName )
    //    RelativeLayout linUserName;
    //    @BindView( R.id.lin_pwd )
    //    LinearLayout linPwd;
    @BindView( R.id.lin_bind )
    LinearLayout linBind;
    @BindView( R.id.lin_login )
    LinearLayout linLogin;
    @BindView( R.id.lin_remember_pw )
    LinearLayout linRememberPw;
    @BindView( R.id.lin_forget_pw )
    LinearLayout linForgetPw;
    @BindView( R.id.ck_select_pw )
    CheckBox ckSelectPw;
    @BindView( R.id.rel_arrow )
    RelativeLayout relArrow;

    private UserData mUserData;

    private int version;
    private String date;
    private LoginPresenter loginPresenter;

    private Store store;


    private String userName = "";
    private String pwd = "";

    private boolean onLoad = true;

    private String redirect = "http://sz.canxingjian.com:18080/html/wxscan.html";

    //    private String redirect = "http://192.168.1.200:8080/html/wxscan.html";

    private String onLineServerAddress = "www.smarant.com";
    private PosInfo posinfo;
    private String terminalMac;
    private CachedDao cacheDao;
    private long oneDayCurrTimeMillis = 86399858;

    private SecondaryPresentation secondaryPresentation = null;//副屏上切换的轮播图
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (secondaryPresentation != null && secondaryPresentation.isShowing()) {
            secondaryPresentation.dismiss();
        }
    }

    public void switchLanguage(Locale locale) {
        Configuration config = getResources().getConfiguration();// 获得设置对象
        Resources resources = getResources();// 获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 简体中文
        resources.updateConfiguration(config, dm);
    }

    private void settLanguage() {
        String language = store.getLanguageSett();
        if (language.equals(Constant.LANGUAGE_CHINESE))//中文
        {
            switchLanguage(Locale.getDefault());
        } else if (language.equals(Constant.LANGUAGE_ENGLISH))//英文
        {
            switchLanguage(Locale.ENGLISH);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = Store.getInstance(context);
        settLanguage();
        setContentView(R.layout.aty_login);
        myApplication.addPage(LoginAty.this);
        ButterKnife.bind(this);
        initView();
        loadData();
        loginPresenter = new LoginPresenter(this);
        showListPopulWindow();

        requestPermisions();//申请权限
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //初始化副屏幕
    private void initSecondaryScreen() {
        DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
        if (dm != null) {
            Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

            //            myApplication.ShowToast("secondary screen found: " + displays.length);

            for (Display display : displays) {
                if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                    secondaryPresentation = new SecondaryPresentation(this, display, null);
                    secondaryPresentation.show();
                }
            }
        }
    }

    private void initView() {
        loginVersion.setText(String.format(getResources().getString(R.string.company_name), ToolsUtils.getVersionName(this)));
        //        wxlogin_wv.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //        wxlogin_wv.setInitialScale(getResources().getInteger(R.integer.wx_scan_size));
        //        wxlogin_wv.getSettings().setJavaScriptEnabled(true);
        //        wxlogin_wv.addJavascriptInterface(this, "wxLogin");
        //        wxlogin_wv.loadUrl(redirect);
        //        wxlogin_wv.setWebViewClient(new WebViewClient() {
        //            @Override
        //            public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //                view.loadUrl(url);
        //                return true;
        //            }
        //
        //            @Override
        //            public void onPageFinished(WebView view, String url) {
        //                //                Log.e("onPageFinished", url);
        //                if (url.contains("wxscan.html")) {
        //                    sendToJs();
        //                }
        //            }
        //
        //            @Override
        //            public void onPageCommitVisible(WebView view, String url) {
        //                //                Log.e("onPageCommitVisible", url);
        //            }
        //        });
        //        wxlogin_wv.setWebChromeClient(new WebChromeClient());
        //
        //        wxlogin_wv.setOnTouchListener(new View.OnTouchListener() {
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                return true;
        //            }
        //        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        logicBindOrLogin(null);
        if (!TextUtils.isEmpty(terminalMac)) {
            getTerminInfo();
        } else {
            saveTerminalInfo(null);
            tvTerMinalTitle.setText(resources.getString(R.string.terminal_title));
        }
        MyApplication.setContext(context);
        initSecondaryScreen();
    }


    /**
     * 判断绑定还是登录
     */
    private void logicBindOrLogin(TerminalInfo result) {
        if (!TextUtils.isEmpty(store.getTerminalMac()) && result != null && result.isActive()) {
            linBind.setVisibility(View.GONE);
            linLogin.setVisibility(View.VISIBLE);
        } else {
            linBind.setVisibility(View.VISIBLE);
            linLogin.setVisibility(View.GONE);
        }
    }

    public void getTerminInfo() {
        onClickCount = 0;
        saveTerminalInfo(null);
        getStoreInfo(context, !TextUtils.isEmpty(terminalMac) ? terminalMac : store.getTerminalMac());
    }

    /**
     * 保存获取的门店终端信息
     *
     * @param result
     */
    private void saveTerminalInfo(TerminalInfo result) {
        if (TextUtils.isEmpty(store.getServiceAddress())) {
            store.setServiceAddress(onLineServerAddress);
            store.setStorePort("8080");
        } else {
            store.setServiceAddress(store.getServiceAddress());
            if (store.getServiceAddress().equals(onLineServerAddress)) {
                store.setStorePort("8080");
            } else {
                if (TextUtils.isEmpty(store.getStorePort())) {
                    store.setStorePort("18080");
                } else {
                    store.setStorePort(store.getStorePort());
                }
            }
        }
        if (result != null) {
            store.setStoreAppId(result.appid);
            store.setBrandId(result.brandid);
            store.setStoreId(result.storeid);
            store.setStoreName(result.sname);
            store.setDeviceName(result.tname);
            store.setTerminalTypeStr(result.terminalTypeStr);
            System.out.println(System.currentTimeMillis());
            store.setStoreEndTime(TimeUtil.times(result.getStoreEndTime() + ""));
            if ((result.getStoreEndTime() - System.currentTimeMillis()) < (oneDayCurrTimeMillis * 30)) {
                storeEndTime.setVisibility(View.VISIBLE);
                if ((result.getStoreEndTime() - System.currentTimeMillis()) < 0) {
                    storeEndTime.setText("注意:本店服务已到期,请联系服务商续费,避免影响门店正常运营.");
                } else {
                    storeEndTime.setText("注意:本店将在" + TimeUtil.times2(result.getStoreEndTime() + "") + "终止服务,请及时联系服务商续费,避免影响门店正常运营.");
                }
            }

            store.setSaveState(true);
            saveSetting();
        }
        String serviceAddress = store.getServiceAddress();
        String servicePort = store.getStorePort();
        if (!TextUtils.isEmpty(servicePort)) {
            posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + ":" + servicePort + "/");
        } else {
            posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + "/");
        }
    }

    private void getStoreInfo(Context context, String macAddress) {
        try {
            String serviceAddress = store.getServiceAddress();
            String servicePort = store.getStorePort();
            posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + ":" + servicePort + "/");
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getTerminalInfo(context, macAddress, new ResultCallback<TerminalInfo>() {
                @Override
                public void onResult(TerminalInfo result) {
                    if (result != null && result.isActive()) {//有绑定信息走登录流程 并且为激活状态
                        StoreInfor.terminalInfo = result;
                        saveTerminalInfo(result);
                        logicBindOrLogin(result);
                        saveCashPrinter(result);
                        tvTerMinalTitle.setText(resources.getString(R.string.account_login));
                        //                        dishMenu.setVisibility(View.GONE);
                    } else {//无绑定信息走绑定流程
                        saveTerminalInfo(null);
                        logicBindOrLogin(result);
                        terminalMac = store.getTerminalMac();
                        if (!TextUtils.isEmpty(terminalMac)) {
                            loginPosEt.setText(terminalMac);
                        }
                        loginUserEt.setText("");
                        loginPwEt.setText("");
                        tvTerMinalTitle.setText(resources.getString(R.string.terminal_title));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(resources.getString(R.string.get_store_info_failure) + "," + e.getMessage());
                    Log.i("获取门店信息失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(resources.getString(R.string.get_store_info_failure) + "," + e.getMessage());
            Log.i("获取门店信息失败", e.getMessage());
        }
    }

    /**
     * 找出收银打印机
     *
     * @param result
     */
    private void saveCashPrinter(TerminalInfo result) {
        //        if (result != null && result.getPrinterid() > 0) {
        store.setCashPrinterId(result.getPrinterid());
        store.setCashKdsId(result.getKdsid());
        store.setSecondaryPrinterId(result.getSecondaryPrinterId());
        store.setTakeoutPrinterId(result.getTakeoutPrinterid());
        //        }
    }

    private String getStringIdToStr(int stringId) {
        return resources.getString(stringId);
    }

    private void loadData() {
        Configure.init(LoginAty.this);
        cacheDao = new CachedDao();
        myApplication.setScreenHeight(Configure.screenHeight);
        myApplication.setScreenWidth(Configure.screenWidth);
        posinfo = PosInfo.getInstance();
        terminalMac = store.getTerminalMac();
        mUserData = UserData.getInstance(context);
        posinfo.setTerminalIp(ToolsUtils.getIPAddress(context));
        loginIp.setText("IP:" + ToolsUtils.getIPAddress(context));
        ckSelectPw.setChecked(store.isRememberPw());
        if (!ckSelectPw.isChecked()) {
            loginPwEt.setText("");
            tvRememberPwHine.setTextColor(resources.getColor(R.color.font_gray));
        } else {
            tvRememberPwHine.setSelected(true);
            tvRememberPwHine.setTextColor(resources.getColor(R.color.white));
        }
        ckSelectPw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tvRememberPwHine.setTextColor(resources.getColor(R.color.white));
                } else {
                    tvRememberPwHine.setTextColor(resources.getColor(R.color.font_gray));
                }
                store.setRememberPw(isChecked);
            }
        });
        boolean saveState = mUserData.getSaveState();
        if (saveState) {
            String userName = mUserData.getRealName();
            String pwd = mUserData.getPwd();
            //            loginUserEt.setText(userName);
            //            loginPwEt.setText(pwd);
            loginUserEt.setText("");
            loginPwEt.setText("");
        }
    }

    int onClickCount = 0;//已点击的次数
    int logicClick = 4;//需要点击的次数

    @OnClick( {R.id.login_user, R.id.view_store_info, R.id.rel_arrow, R.id.login_bind, R.id.lin_remember_pw, R.id.lin_forget_pw, R.id.dish_menu} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lin_forget_pw:
                //                ToolsUtils.writeUserOperationRecords("重置密码");
                //                DialogUtil.ordinaryDialog(context, "重置密码", "是否确定重置密码?", new DialogCallback() {
                //                    @Override
                //                    public void onConfirm() {
                //                        checkoutForgetPwd();
                //                    }
                //
                //                    @Override
                //                    public void onCancle() {
                //
                //                    }
                //                });
                break;
            case R.id.view_store_info:
                ++onClickCount;
                if (onClickCount >= logicClick) {
                    DialogUtil.loginSetDialog(this);
                }
                break;
            case R.id.lin_remember_pw:
                if (ckSelectPw.isChecked()) {
                    store.setRememberPw(false);
                    tvRememberPwHine.setTextColor(resources.getColor(R.color.font_gray));
                } else {
                    store.setRememberPw(true);
                    tvRememberPwHine.setTextColor(resources.getColor(R.color.white));
                }
                ckSelectPw.setChecked(store.isRememberPw());
                break;
            case R.id.login_bind:
            case R.id.login_user:
                //                xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
                Login();
                //                new Thread(new Runnable()
                //                {
                //                    @Override
                //                    public void run()
                //                    {
                //                        try {
                //                            GpEnternetPrintTwo.gpPrint("192.168.1.122", 30);
                //                        } catch (IOException e) {
                //                            // TODO Auto-generated catch block
                //                            e.printStackTrace();
                //                        }
                //                    }
                //                }).start();
                break;
            case R.id.rel_arrow:
                if (!ToolsUtils.isList(userList) && listPopupWindow != null) {
                    listPopupWindow.show();
                } else {
                    showToast(resources.getString(R.string.user_login_recording_is_null));
                }
                break;
            case R.id.dish_menu:
                ToolsUtils.writeUserOperationRecords("点击进入菜单按钮");
                terminLogin();
                break;

        }
    }

    private void terminLogin() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.terminalLogin(new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    int code = (Integer) result;
                    if (code == 0) {
                        startActivity(new Intent(LoginAty.this, DishMenuAty.class));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(resources.getString(R.string.terminal_login_failure) + "," + e.getMessage());
                    Log.i("终端登录失败，", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    ListPopupWindow listPopupWindow;
    UserNameAdp userNameAdp;
    List<User> userList = new ArrayList<>();

    private void showListPopulWindow() {
        userList.clear();
        userList = cacheDao.getAllUser();
        listPopupWindow = new ListPopupWindow(this);
        userNameAdp = new UserNameAdp(context);
        listPopupWindow.setAdapter(userNameAdp);
        userNameAdp.setData(userList);
        listPopupWindow.setAnchorView(loginUserEt);
        listPopupWindow.setModal(true);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User user = (User) userNameAdp.getItem(i);
                if (user != null) {
                    loginUserEt.setText(user.getName());
                    loginPwEt.setText("");
                    listPopupWindow.dismiss();
                }
            }
        });
    }

    @JavascriptInterface
    public void toActivity(String jsessionid, String name, String msg, String token, String terminalid) {
        if (TextUtils.isEmpty(msg)) {
            System.out.println("JSESSIONID=" + jsessionid);
            CookieInterceptor.addCookie("JSESSIONID=" + jsessionid);
            CookieInterceptor.setToken(token);
            saveInfo();
            PosInfo posInfo = PosInfo.getInstance();
            posInfo.setToken(token);
            posInfo.setTerminalId(terminalid);
            startActivity(TableMainAty.class);
            finish();
        } else {
            showToast(msg);
            onLoad = true;
            //            wxlogin_wv.postDelayed(new Runnable() {
            //                @Override
            //                public void run() {
            //                    wxlogin_wv.loadUrl(redirect);
            //                }
            //            }, 500);
        }
        //        Log.e("Tag", "返回:" + jsessionid);
        //        Log.e("Tag", "返回msg:" + msg);
    }

    //调用页面js
    public void sendToJs() {
        if (onLoad) {
            onLoad = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    Map map = new HashMap();

                    map.put("appid", store.getStoreAppId());
                    map.put("brandid", store.getBrandId());
                    map.put("storeid", store.getStoreId());
                    map.put("tname", store.getDeviceName());
                    map.put("versionid", ToolsUtils.getVersionCode(context) + "");
                    map.put("receiveNetOrder", "1");
                    map.put("terminalmac", TextUtils.isEmpty(terminalMac) ? "" : store.getTerminalMac());
                    map.put("currentVersion", ToolsUtils.getVersionName(context));
                    String state = gson.toJson(map);
                    //                    Log.e("ss", state);
                    //                    wxlogin_wv.loadUrl("javascript:setState('" + state + "')");
                }
            });
        }
    }

    /**
     * ****登录
     */
    private void Login() {
        userName = loginUserEt.getText().toString().trim();
        pwd = loginPwEt.getText().toString().trim();
        terminalMac = loginPosEt.getText().toString().trim();
        if (linBind.getVisibility() == View.VISIBLE)//绑定
        {
            if (TextUtils.isEmpty(terminalMac)) {
                showToast(resources.getString(R.string.terminal_auth_code_is_not_null));
                return;
            }
            ToolsUtils.writeUserOperationRecords("绑定设备");
            store.setDeviceName(terminalMac);
            bindStore(terminalMac);
        } else//登录
        {
            if (TextUtils.isEmpty(userName)) {
                showToast(resources.getString(R.string.terminal_auth_code_is_not_null));
                return;
            } else if (TextUtils.isEmpty(pwd)) {
                showToast(resources.getString(R.string.input_password));
                return;
            }
            if (!saveInfo()) {
                return;
            }
            ToolsUtils.writeUserOperationRecords("登录设备");
            loginPresenter.getLoginWork(userName, pwd);
        }
    }


    /**
     * 绑定门店
     *
     * @param terminalMac
     */
    private void bindStore(final String terminalMac) {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.bindStore(terminalMac, new ResultCallback<TerminalInfo>() {
                @Override
                public void onResult(TerminalInfo result) {
                    if (result != null)//绑定成功
                    {
                        store.setTerminalMac(terminalMac);//绑定成功后将机器识别码写入到文件中
                        store.setBindUUID(result.getBindUUID());
                        tvTerMinalTitle.setText(resources.getString(R.string.account_login));
                        showToast(resources.getString(R.string.auth_success_now_login));
                        logicBindOrLogin(result);
                        StoreInfor.terminalInfo = result;
                        saveTerminalInfo(result);
                        saveCashPrinter(result);
                        saveInfo();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(resources.getString(R.string.bind_store_failure) + "," + e.getMessage());
                    Log.i("绑定门店失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(resources.getString(R.string.bind_store_failure) + "," + e.getMessage());
            Log.i("绑定门店失败", e.getMessage());
        }
    }


    @Override
    public void showDialog() {
        loginUser.setEnabled(false);
        showProgress(resources.getString(R.string.loging));
    }

    @Override
    public void dissDialog() {
        loginUser.setEnabled(true);
        dissmiss();
    }

    @Override
    public void showError(PosServiceException e) {
        loginUser.setEnabled(true);
        showToast(resources.getString(R.string.login_failure) + "," + e.getMessage());
    }

    @Override
    public <T> void callBackData(T t) {
        User user = (User) t;
        UserRet userInfo = user.getUserRet();
        //        if(store.isRememberPw())
        //        {
        mUserData.setUserName(userName);
        mUserData.setPwd(pwd);
        mUserData.setRealName(userInfo.getRealname());
        //            mUserData.setSaveStated(true);
        //        }

        PosInfo posinfo = PosInfo.getInstance();
        posinfo.setUsername(userName);
        posinfo.setUserId(user.getId());
        posinfo.setRealname(userInfo.getRealname());
        if (userInfo.getSupportCallMaterial() != 0) {
            store.setSupportCallGoods(true);
        }
        getStoreConfiguration();
        //        getStoreBusinessInfor();
    }

    private void jumpTableMain() {
        //        showToast("登录成功");
        //        //        Intent orderIntent = new Intent(LoginAty.this, OrderMainAty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        //        Intent orderIntent = new Intent(LoginAty.this, TableMainAty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        //        Intent orderIntent = new Intent(LoginAty.this, OrderDishAty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        //        Intent orderIntent = new Intent(LoginAty.this, WelcomeAty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        Intent orderIntent = new Intent(LoginAty.this, TableMainAty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        //        Intent orderIntent = new Intent(LoginAty.this, MemberAty.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //        myApplication.clean();
        //        LoginAty.this.finish();
        //        startActivity(orderIntent);

        Intent orderIntent = new Intent(LoginAty.this, OrderDishMainAty.class);
        myApplication.clean();
        LoginAty.this.finish();
        startActivity(orderIntent);
    }

    /**
     * 保存设置的配置信息
     */
    private boolean saveInfo() {
        String storeMerchantsId = store.getStoreAppId();
        String storeBrandId = store.getBrandId();
        String storeStoreId = store.getStoreId();

        String storeDeviceName = store.getDeviceName();
        String serviceAddress = store.getServiceAddress();
        String servicePort = store.getStorePort();
        String macAddress = store.getTerminalMac();

        if (TextUtils.isEmpty(serviceAddress)) {
            showToast(resources.getString(R.string.server_address_is_not_null));
            return false;
        }
        if (TextUtils.isEmpty(servicePort)) {
            showToast(resources.getString(R.string.port_is_not_null));
            return false;
        }

        PosInfo posinfo = PosInfo.getInstance();
        posinfo.setAppId(storeMerchantsId);
        posinfo.setBrandId(storeBrandId);
        posinfo.setStoreId(storeStoreId);
        posinfo.setTerminalName(storeDeviceName);
        posinfo.setTerminalMac(macAddress);
        posinfo.setReceiveNetOrder(1);
        posinfo.setVersionId(ToolsUtils.getVersionCode(this) + "");
        posinfo.setCurrentVersion(ToolsUtils.getVersionName(this));

        if (!TextUtils.isEmpty(servicePort)) {
            posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + ":" + servicePort + "/");
        } else {
            posinfo.setServerUrl(Constant.SERVER_ADDRESS_URL + serviceAddress + "/");
        }
        return true;
    }

    public void saveSetting() {
        if (saveInfo()) {
            onLoad = true;
            sendToJs();
        }
    }

    /**
     * 获取门店基本信息(正反扫、先后支付、小票)
     */
    //    private void getStoreBusinessInfor() {
    //        try {
    //            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
    //            storeBusinessService.getStoreBusinessInformation(new ResultCallback<StoreBusinessInformation>() {
    //                @Override
    //                public void onResult(StoreBusinessInformation result) {
    //                    if (result != null) {
    //                        StoreInfor.printInvoiceBarcode = result.isPrintInvoiceBarcode();
    //                        StoreInfor.customerInfoForWaimai = result.isCustomerInfoForWaimai();
    //                        StoreInfor.cardNumberMode = result.isCardNumberMode();
    //                        StoreInfor.createDishFromPOS = result.isCreateDishFromPOS();
    //                        StoreInfor.printConfiguration = result.getPrintConfiguration();
    //                        StoreInfor.storeMode = result.getStoreMode();
    //                        StoreInfor.paymentFlow = result.getPaymentFlow();
    //                        StoreInfor.scanType = result.getScanType();
    //                        StoreInfor.storeName = result.getStoreName();
    //                        StoreInfor.address = result.getAddress();
    //                        StoreInfor.phoneNumber = result.getPhoneNumber();
    //                        jumpTableMain(result);
    //                        Store.getInstance(context).setStoreName(result.getStoreName());
    //                        //                        StoreInfor.receiptList = result.getReceiptList();
    //                        List<Printer> printerList = result.getPrinterList();
    //                        StoreInfor.printerList = printerList;
    //                        setSelectPrinter(printerList);
    //                        PrintManager.getInstance().setPrinterList(printerList);
    //                        PrintManager.getInstance().addPrinterList(printerList);
    //
    //                        PrintManager.getInstance().receiptPrinter = result.getReceiptPrinter();
    //                        PrinterDataController.getInstance().setReceiptPrinter(result.getReceiptPrinter());
    //                    }
    //                }
    //
    //                @Override
    //                public void onError(PosServiceException e) {
    //                    //                    showToast("---" + e.getMessage());
    //                }
    //            });
    //        } catch (PosServiceException e) {
    //            e.printStackTrace();
    //        }
    //    }


    /**
     * 查询门店设置信息
     */
    private void getStoreConfiguration() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getStoreConfiguration(new ResultCallback<StoreConfiguration>() {
                @Override
                public void onResult(StoreConfiguration result) {
                    if (result != null) {
                        if (result.isTableServer()) {
                            StoreInfor.storeMode = "TABLE";
                        } else {
                            StoreInfor.storeMode = "NOTABLE";
                        }
                        StoreInfor.printConfiguration = result.getPrintConfiguration();
                        StoreInfor.cardNumberMode = result.isCardNumberMode();
                        StoreInfor.setRepastPopulation(result.isRetreatCheckAuthority());
                        StoreInfor.wipeZero = result.getWipeZero();

                        jumpTableMain();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("门店配置信息获取失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private void checkoutForgetPwd() {
        String userName = loginUserEt.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            showToast("用户名不能为空!");
            return;
        }
        if (!ToolsUtils.isNumeric(userName) && !ToolsUtils.isEmail(userName)) {
            showToast("手机或邮箱格式不正确");
            return;
        }
        if (ToolsUtils.isNumeric(userName) && !ToolsUtils.isMobileNO(userName)) {
            showToast("手机或邮箱格式不正确");
            return;
        }
        forgetPwd(userName);
    }

    private void forgetPwd(String userName) {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.forgetPwd(userName, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast(resources.getString(R.string.reset_success));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(resources.getString(R.string.reset_failure) + "," + e.getMessage());
                    Log.i("重置失败", e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(resources.getString(R.string.reset_failure) + "," + e.getMessage());
            Log.i("重置失败", e.getMessage());
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("LoginAty Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
