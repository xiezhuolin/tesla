package cn.acewill.pos.next.service.canxingjian;

import android.text.TextUtils;

import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;

/**
 * 保存所有老的餐行健后台的API路径
 * Created by Acewill on 2016/6/3.
 */
public class CXJServerUrl {
    private Store store;
    private static final String URL_WITH_PORT = "%s://%s:%d/";
    private static final String URL = "%s://%s/";
    private String protocol = "https://"; //或者https

    private String host;

    public static final String NEW_SERVICE_URL = "sz.canxingjian.com";//sz.canxingjian.com

    public CXJServerUrl(String protocol, String host, int port) {
        this.protocol = protocol;
        this.host = host;
    }

    public CXJServerUrl() {
    }

    //path 就是下面的常量之一
    public String getBaseUrl() {
        store = Store.getInstance(MyApplication.getInstance());
        String port = store.getStorePort();
        host = store.getServiceAddress();
        if(!TextUtils.isEmpty(port))
        {
            return protocol+host+":"+port+"/";
        }
        else
        {
            return protocol+host+"/";
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    //调用失败
   // public static final String CALLSERVICE = "/order/api/api.php?do=callService&app=ClientAndroidMobile"; // 呼叫接口



    //调用失败
   // public static final String GETRESTAURANTINFO = "/order/api/api.php?do=getRestaurantInfo&app=ClientAndroidMobile"; // 饭店信息
    public static final String GETCARDINFO = "/order/api/api.php?do=getCardInfo&app=ClientAndroidMobile"; // 会员信息


    //应该是getTableOrders
    //public static final String GETTABLEORDER = "/order/api/api.php?do=getTableOrder&app=ClientAndroidMobile"; // 获得桌台的订单信息

    public static final String KITCHENNOTICEMESSAGE = "/order/api/api.php?do=kitchenNoticeMessage&app=clientAndroidMobile"; // 厨房通知
    public static final String GETCURRENTBOOKINFO = "/order/api/api.php?do=getCurrentBookInfo&app=ClientAndroidMobile"; // 得到当前预定信息

    //直接从安卓发指令到打印机，不需要通过后台处理
    // public static final String PRINTPASSNGER = "/order/api/api.php?do=printPassnger&app=clientAndroidMobile"; // 打印接口

    // ************************************************************************************************************************

    /**
     * 获取菜品信息-- 通过getAllDishs
     */
    //public static final String GETMENUDATA = "/order/api/api.php?do=getMenuData&app=clientDigitalMenu"; //
    /**
     * 下载图片
     */
    public static final String GETFILEUPDATE = "/order/api/api.php?do=getfileupdate&app=ClientAndroidMobile"; // 下载图片

    /**
     * 10设置开台信息
     */
    public static final String SET_ORDERINFO = "/order/api/api.php?do=setOrderInfo&app=ClientNewAndroidMobile";


    /**
     * 验证优惠券
     */
    public static final String VERIFY_ORDER_COUPON = "/order/api/api.php?do=verifyOrderCoupon&app=ClientNewAndroidMobile";

    /**
     * 获取订单营销方案
     */
    public static final String GET_ORDER_MARKET ="/order/api/api.php?do=getOrderMarket&app=ClientNewAndroidMobile";
    /**
     * 使用营销方案预结
     */
    public static final String PRECHECK_MAEKET = "/order/api/api.php?do=precheckmarket&app=ClientNewAndroidMobile";

    /**
     * 微信订单
     */
    public static final String GET_NET_ORDER = "/order/api/api.php?do=getnetorder&app=ClientNewAndroidMobile";


    /**
     * apk应该从CDN下载
     * 下载更新APK数据包
     */
    //public static final String UPDATE_APK ="/order/api/api.php?do=getUpdateApk&app=ClientNewAndroidMobile";

}
