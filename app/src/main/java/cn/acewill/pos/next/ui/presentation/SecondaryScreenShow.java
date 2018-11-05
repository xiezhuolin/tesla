package cn.acewill.pos.next.ui.presentation;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jude.rollviewpager.RollPagerView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.SecondScreen;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.wsh.Account;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.retrofit.response.ScreenResponse;
import cn.acewill.pos.next.ui.adapter.RollPagerAdp;
import cn.acewill.pos.next.ui.adapter.ScreenOrderInfoAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * Created by Acewill on 2016/8/17.
 */
public class SecondaryScreenShow extends Presentation  {
//    private SliderLayout mDemoSlider;
    private RollPagerView mRollViewPager;
    private ImageView iv_bg;
    private Context context;
    private TextView tv_main_title;
    private TextView tv_pos_level;
    private TextView tv_pos_tips;
    private TextView dish_sum;
    private TextView dish_price;
    private TextView dish_active;
    private TextView dish_total;
    private TextView member_info;
    private ImageView iv_dishImagePath;
    private ImageLoader imageLoader;
    private ScreenResponse screenResponse;
    private String defaultImg;
    private String tipsSth;
    private List<String> url_imgs;
    private String storeName;
    private PosInfo posInfo;
    private String logoPath;
    private RelativeLayout relLeft;
    private RelativeLayout relRight;
    private ScrolListView dish_list;
    private Cart cart;
    private ScreenOrderInfoAdp screenOrderInfoAdp;
    private RollPagerAdp rollPagerAdp;
    private static boolean flag = false;

    Random rn = new Random();

    public SecondaryScreenShow(Context outerContext, Display display, ScreenResponse screenResponse) {
        super(outerContext, display);
        this.context = outerContext;
        this.screenResponse = screenResponse;
        if (screenResponse != null) {
            if (screenResponse.getScreenImgs() != null && screenResponse.getScreenImgs().size() > 0) {
                this.url_imgs = screenResponse.getScreenImgs();
            }
            if (!TextUtils.isEmpty(screenResponse.getDefaultImg())) {
                defaultImg = screenResponse.getDefaultImg();
            }
            if (!TextUtils.isEmpty(screenResponse.getSubtitle())) {
                tipsSth = screenResponse.getSubtitle();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_secondary_screen);
        posInfo = PosInfo.getInstance();
        cart = Cart.getInstance();
        logoPath = posInfo.getLogoPath();
        imageLoader = MyApplication.getInstance().initImageLoader(MyApplication.getInstance().getContext());
        //        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        //        imageLoader.displayImage(imageUrl, iv_bg);
        if (TextUtils.isEmpty(defaultImg)) {
            defaultImg = "http://szfileserver.419174855.mtmssdn.com/common/fileupload/20170713211927_5836.jpg";
        }
        initSlider();
//        myThread = new MyThread();
//        myThread.start();
    }

//    private MyThread myThread;

    private void initSlider() {
        //        mDemoSlider = (SliderLayout) findViewById(R.id.ad_slider);
        mRollViewPager = (RollPagerView) findViewById(R.id.roll_pagerView);
        tv_main_title = (TextView) findViewById(R.id.tv_main_title);
        tv_pos_level = (TextView) findViewById(R.id.tv_pos_level);
        tv_pos_tips = (TextView) findViewById(R.id.tv_pos_tips);
        dish_list = (ScrolListView) findViewById(R.id.dish_list);
        relLeft = (RelativeLayout) findViewById(R.id.rel_left);
        relRight = (RelativeLayout) findViewById(R.id.rel_right);
        iv_dishImagePath = (ImageView) findViewById(R.id.iv_dishImagePath);

        dish_sum = (TextView) findViewById(R.id.dish_sum);
        dish_price = (TextView) findViewById(R.id.dish_price);
        dish_active = (TextView) findViewById(R.id.dish_active);
        dish_total = (TextView) findViewById(R.id.dish_total);
        member_info = (TextView) findViewById(R.id.member_info);

        screenOrderInfoAdp = new ScreenOrderInfoAdp(context);
        dish_list.setAdapter(screenOrderInfoAdp);
        tv_pos_level.setText(String.format(getResources().getString(R.string.company_name3), ToolsUtils.getVersionName(context)));
        storeName = posInfo.getBrandName();
        if (!TextUtils.isEmpty(storeName)) {
            tv_main_title.setText(storeName + " 欢迎您!");
        }
        if (!TextUtils.isEmpty(tipsSth)) {
            tv_pos_tips.setText(tipsSth);
        } else {
            if (TextUtils.isEmpty(storeName)) {
                tv_pos_tips.setText("欢迎光临!");
            } else {
                tv_pos_tips.setText("欢迎光临" + storeName + "!");
            }
        }

        if (url_imgs == null) {
            url_imgs = new ArrayList<String>();
            url_imgs.add(defaultImg);
        }

        rollPagerAdp = new RollPagerAdp(context);
        //设置播放时间间隔
        mRollViewPager.setPlayDelay(10000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        rollPagerAdp.setData(url_imgs);
        //设置适配器
        mRollViewPager.setAdapter(rollPagerAdp);
    }


    public void setDishItemInfo(List<Dish> marketActList, BigDecimal price, int dishItemCount) {
//        if (myThread != null) {
//            myThread.setScreenInfo(marketActList, price, dishItemCount);
//        }
    }


    static final int HANDLER_TEST = 1;
    class MyThread extends Thread {
        private volatile boolean pause = false;
        private List<Dish> marketActList;
        private BigDecimal price;
        int dishItemCount;

        public void setScreenInfo(List<Dish> marketActList, BigDecimal price, int dishItemCount) {
            this.marketActList = ToolsUtils.cloneTo(marketActList);
            this.price = price;
            this.dishItemCount = dishItemCount;
            pause = true;
        }

        public void run() {
            while (true) {
                if (pause) {
                    Message msg = new Message();
                    msg.what = HANDLER_TEST;
                    SecondScreen secondScreen = new SecondScreen();
//                    if (marketActList != null && marketActList.size() > 0) {
//                        int size = marketActList.size();
//                        for (int i = 0; i < size; i++) {
//                            Dish orderItem = marketActList.get(i);
//                            if (orderItem.subItemList != null && orderItem.subItemList.size() > 0)// 套餐菜品
//                            {
//                                List<Dish.Package> subItemList = orderItem.getSubItemList();
//                                StringBuffer sb = new StringBuffer();
//                                String space = "";
//                                for (int a = 0; a < subItemList.size(); a++) {
//                                    if (a != subItemList.size() - 1) {
//                                        space = "\n";
//                                    }
//                                    String skuStrs = "";
//                                    skuStrs = printDishOption(subItemList.get(a).optionList);
//                                    sb.append((a + 1) + "." + subItemList.get(a).getDishName() + "     " + subItemList.get(a).quantity + "/份 " + skuStrs + space);
//                                }
//                                orderItem.setSubItemSth(sb.toString());
//                            } else {
//                                String skuStrs = printDishOption(orderItem.optionList);
//                                if (!TextUtils.isEmpty(skuStrs)) {
//                                    orderItem.setSubItemSth(skuStrs);
//                                }
//                            }
//                        }
//                    }

                    secondScreen.setMarketActList(marketActList);
                    secondScreen.setPrice(price);
                    secondScreen.setDishItemCount(dishItemCount);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("secondScreen", secondScreen);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                    pause = false;
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_TEST:
                    Bundle bundle = msg.getData();
                    final SecondScreen secondScreen = (SecondScreen) bundle.getSerializable("secondScreen");
                    if (secondScreen != null) {
                        List<Dish> marketActLists = secondScreen.getMarketActList();
                        BigDecimal price = secondScreen.getPrice();
                        int dishItemCount = secondScreen.getDishItemCount();
                        if (marketActLists != null && marketActLists.size() > 0) {
                            int size = marketActLists.size();
                            showDishItemInfo(true);
                            BigDecimal totalMoney = new BigDecimal(cart.getPriceSum()).setScale(3, BigDecimal.ROUND_HALF_UP);
                            BigDecimal priceMoney = price.setScale(3, BigDecimal.ROUND_HALF_UP);
                            BigDecimal activeMoney = totalMoney.subtract(priceMoney);

                            dish_sum.setText("数量:" + dishItemCount);
                            dish_price.setText("原价:" + String.format("%.2f ", totalMoney) + " ¥");
                            dish_total.setText("实收:" + String.format("%.2f ", priceMoney) + " ¥");
                            dish_active.setText("优惠:" + String.format("%.2f ", activeMoney) + " ¥");

//                            showDishItemInfo(true);
//                            Dish dish = marketActLists.get(size - 1);
                            //                            if (dish != null) {
                            //                                setSecondScreenImBg(true, dish.getImageName());
                            //                            }
                            screenOrderInfoAdp.setData(marketActLists);
                        } else {
                            showDishItemInfo(false);
                        }
                    }
                    break;
            }
        }
    };

    public void setMemberInfo() {
        if (posInfo.getAccountMember() != null) {
            member_info.setVisibility(View.VISIBLE);
            Account account = posInfo.getAccountMember();
            BigDecimal userStored = new BigDecimal(account.getBalance() / 100.0).setScale(2, BigDecimal.ROUND_DOWN);
            String memberUno = "会员卡号:" + account.getUno() + "、   ";
            String memberName = "会员姓名:" + ToolsUtils.getStarString2(account.getName(), 1, 0) + "、   ";
            String memberGrade = "卡  等  级:" + account.getGradeName() + "、   ";

            String memberIntegral = "剩余积分:" + account.getCredit();
            String memberBalce = "剩余储值:" + userStored + "(元)" + "、";
            int couponsCount = 0;
            if (account.getCoupons() != null && account.getCoupons().size() > 0) {
                couponsCount = account.getCoupons().size();
            }
            String memberCouponsCount = "剩余代金券张数:" + couponsCount + "(张)" + "、";

            member_info.setText(memberUno + memberIntegral + "\n" + memberName + memberBalce + "\n" + memberGrade + memberCouponsCount);
        }
    }

    private void showDishItemInfo(boolean isShow) {
        if (isShow) {
            relLeft.setVisibility(View.VISIBLE);
            mRollViewPager.pause();
            mRollViewPager.setVisibility(View.GONE);
            relRight.setVisibility(View.GONE);
        } else {
            relLeft.setVisibility(View.GONE);
            mRollViewPager.resume();
            mRollViewPager.setVisibility(View.VISIBLE);
            relRight.setVisibility(View.VISIBLE);
//            setSecondScreenImBg(false, "");
        }
    }


    private void setSecondScreenImBg(boolean isShowBg, String bgUrl) {
        if (isShowBg) {
            iv_dishImagePath.setVisibility(View.VISIBLE);
//            mDemoSlider.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(bgUrl)) {
                imageLoader.displayImage(bgUrl, iv_dishImagePath);
            } else {
                if (!TextUtils.isEmpty(logoPath)) {
                    imageLoader.displayImage(logoPath, iv_dishImagePath);
                } else {
                    imageLoader.displayImage(bgUrl, iv_dishImagePath);
                }
            }
        } else {
            iv_dishImagePath.setVisibility(View.GONE);
//            mDemoSlider.setVisibility(View.VISIBLE);
        }
    }

    private String printDishOption(List<Option> optionList) {
        StringBuffer sb = new StringBuffer();
        if (optionList != null && optionList.size() > 0) {
            if (optionList != null && optionList.size() > 0) {
                for (Option option : optionList) {
                    if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                        sb.append(option.name + "、");
                    } else {
                        sb.append(option.name + "(" + option.getPrice() + "元)、");
                    }
                }
            }
        }
        return sb.toString();
    }
}
