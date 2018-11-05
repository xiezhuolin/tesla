package cn.acewill.pos.next.ui.presentation;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Random;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;

/**
 * Created by Acewill on 2016/8/17.
 */
public class SecondaryPresentation extends Presentation  implements ViewPagerEx.OnPageChangeListener {
//    private SliderLayout mDemoSlider;
    private ImageView iv_bg;
    private ImageLoader imageLoader;
    private HashMap<String,String> url_maps;

    Random rn = new Random();
    public SecondaryPresentation(Context outerContext, Display display,HashMap<String,String> url_maps) {
        super(outerContext, display);
        this.url_maps = url_maps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_image_slider);
        // setContentView(R.layout.aty_login);
        imageLoader = MyApplication.getInstance() .initImageLoader(MyApplication.getInstance().getContext());
        iv_bg = (ImageView) findViewById(R.id.iv_bg);
        String imageUrl = "http://szfileserver.419174855.mtmssdn.com/common/fileupload/20170713211927_5836.jpg";
        imageLoader.displayImage(imageUrl, iv_bg);
//        initSlider();
    }

//    private void initSlider() {
////        mDemoSlider = (SliderLayout)findViewById(R.id.ad_slider);
//
//
//        if(url_maps==null||url_maps.size()==0){
//            url_maps = Configure.getHashMap();
////            url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
////            url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
////            url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");
////            url_maps.put("Game of Thrones", "http://www.hdwallpapers.in/walls/helix_nebula_5k-wide.jpg");
//        }
//
//        for(String name : url_maps.keySet()){
//            TextSliderView textSliderView = new TextSliderView(this.getContext());
//            textSliderView.description(name).image(url_maps.get(name))
//                    .setScaleType(BaseSliderView.ScaleType.Fit);
//
//            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra",name);
//
//            mDemoSlider.addSlider(textSliderView);
//        }
//
//        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//        mDemoSlider.setDuration(4000);
//        mDemoSlider.addOnPageChangeListener(this);
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int range =  SliderLayout.Transformer.values().length;
        int randomNum =  rn.nextInt(range);

//        System.out.println("Transformer index; " + randomNum);
//        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.values()[randomNum]);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
