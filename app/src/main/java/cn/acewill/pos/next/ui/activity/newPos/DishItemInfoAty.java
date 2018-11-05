package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.adapter.DishMenuItemAdp;
import cn.acewill.pos.next.widget.ProgressDialogF;

/**
 * 菜品详细信息
 * Created by DHH on 2016/12/23.
 */

public class DishItemInfoAty extends BaseActivity {
    @BindView( R.id.img_back )
    ImageButton imgBack;
    @BindView( R.id.tv_menu_title )
    TextView tvMenuTitle;
    @BindView( R.id.tv_dishName )
    TextView tvDishName;
    @BindView( R.id.tv_money )
    TextView tvMoney;
    @BindView( R.id.iv_dishImagePath )
    ImageView ivDishImagePath;
    @BindView( R.id.img_cart )
    ImageButton imgCart;
    @BindView( R.id.img_search )
    ImageButton imgSearch;

    private ProgressDialogF progressDialog;

    private DishMenuItemAdp dishMenuItemAdp;
    private ImageLoader imageLoader;
    private String logoPath;
    private PosInfo posInfo;
    private Dish dish;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_dish_menu_item_info);
        ButterKnife.bind(this);
        myApplication.addPage(DishItemInfoAty.this);
        loadData();
    }

    private void loadData() {
        posInfo = posInfo.getInstance();
        logoPath = posInfo.getLogoPath();
        progressDialog = new ProgressDialogF(context);
        imageLoader = MyApplication.getInstance().initImageLoader(context);
        dish = (Dish)getIntent().getSerializableExtra("dish");
        if(dish != null)
        {
            tvMenuTitle.setText(dish.getDishName());
            tvDishName.setText(dish.getDishName());
            String money = String.format("%.2f ", dish.getPrice());
            tvMoney.setText("¥ " + money);

            String image = dish.getImageName();
            if (!TextUtils.isEmpty(image)) {
                imageLoader.displayImage(image, ivDishImagePath);
            } else {
                if (!TextUtils.isEmpty(logoPath)) {
                    imageLoader.displayImage(logoPath, ivDishImagePath);
                } else {
                    imageLoader.displayImage(image, ivDishImagePath);
                }
            }
        }
    }



    @OnClick( {R.id.img_back, R.id.tv_menu_title} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                DishItemInfoAty.this.finish();
                break;
            case R.id.img_cart:

                break;
            case R.id.img_search:
                break;
        }
    }
}
