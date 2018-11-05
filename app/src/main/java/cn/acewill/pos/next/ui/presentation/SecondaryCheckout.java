package cn.acewill.pos.next.ui.presentation;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.config.Configure;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.ui.adapter.OrderDishAdapter;
import cn.acewill.pos.next.utils.CreateImage;
import cn.acewill.pos.next.utils.FormatUtils;

/**
 * Created by aqw on 2016/9/8.
 */
public class SecondaryCheckout extends Presentation implements ViewPagerEx.OnPageChangeListener {
    @BindView(R.id.dish_list)
    ListView dishList;
    @BindView(R.id.ad_slider)
    SliderLayout mDemoSlider;
    @BindView(R.id.all_money)
    TextView allMoney;
    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.pay_type)
    TextView payType;
    @BindView(R.id.total_money)
    TextView totalMoney;
    @BindView(R.id.cost_money)
    TextView costMoney;
    @BindView(R.id.change_money)
    TextView changeMoney;
    @BindView(R.id.scan_code_tv)
    TextView scanCodeTv;
    @BindView(R.id.scan_code_iv)
    ImageView scanCodeIv;
    @BindView(R.id.scan_dialog_ll)
    LinearLayout scanDialogLl;
    @BindView(R.id.scan_ll)
    LinearLayout scanLl;

    private HashMap<String, String> url_maps;
    private Random rn = new Random();

    private Order order;
    private OrderDishAdapter orderDishAdapter;
    private Context context;

    public SecondaryCheckout(Context outerContext, Display display, Order order) {
        super(outerContext, display);
        context = outerContext;
        this.order = order;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_secondary_checkout);
        ButterKnife.bind(this);
        initSlider();

        orderDishAdapter = new OrderDishAdapter(context);
        dishList.setAdapter(orderDishAdapter);

        List<OrderItem> orderItems = order.getItemList();

        if (orderItems != null && orderItems.size() > 0) {
            orderDishAdapter.setData(orderItems);
        }

        allMoney.setText("￥" + order.getTotal());
        totalMoney.setText("￥" + order.getTotal());

        scanDialogLl.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(scanDialogLl.getWidth()*2/3,scanDialogLl.getHeight()*2/3);
                scanDialogLl.setLayoutParams(params);
            }
        });

    }

    private void initSlider() {

        if (url_maps == null || url_maps.size() == 0) {
            url_maps = Configure.getHashMap();
        }

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this.getContext());
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int range = SliderLayout.Transformer.values().length;
        int randomNum = rn.nextInt(range);

//        System.out.println("Transformer index; " + randomNum);
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.values()[randomNum]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //设置支付方式
    public void setPayType(String pName) {
        payType.setText(pName);
    }

    //设置实收金额
    public void setCostMoney(double printMoney) {

        try {
            costMoney.setText("￥" + FormatUtils.getDoubleW(printMoney));
            double all_money = Double.parseDouble(order.getTotal());
            if (printMoney > all_money) {
                changeMoney.setText("￥" + FormatUtils.getDoubleW(printMoney - all_money));
            } else {
                changeMoney.setText("￥0.00");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //显示二维码
    public void setScanCode(String code, final int type) {

        scanLl.setVisibility(View.VISIBLE);
        Bitmap bitmap = null;
        Bitmap qrcode = CreateImage.creatQRImage(code, bitmap,
                800, 800);
        scanCodeIv.setImageBitmap(qrcode);

        scanCodeTv.setText(type == 1 ? "请打开支付宝扫一扫" : "请打开微信扫一扫");
    }

    //隐藏二维码
    public void setHiddenCode(){
        scanLl.setVisibility(View.GONE);
    }

}
