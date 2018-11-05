package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.ui.adapter.DishMenuItemAdp;
import cn.acewill.pos.next.widget.ProgressDialogF;

import static cn.acewill.pos.R.id.gv_dish_item;

/**
 * 菜单子项
 * Created by DHH on 2016/12/23.
 */

public class DishItemAty extends BaseActivity {
    @BindView( R.id.img_back )
    ImageButton imgBack;
    @BindView( R.id.tv_menu_title )
    TextView tvMenuTitle;
    @BindView( R.id.img_cart )
    ImageButton imgCart;
    @BindView( R.id.img_search )
    ImageButton imgSearch;
    @BindView( gv_dish_item )
    GridView gvDishItem;

    private ProgressDialogF progressDialog;

    private DishMenuItemAdp dishMenuItemAdp;

    private List<Dish> dataList = new ArrayList<>();
    private String titleMenuName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_dish_menu_item);
        ButterKnife.bind(this);
        myApplication.addPage(DishItemAty.this);
        loadData();
    }

    private void loadData() {
        progressDialog = new ProgressDialogF(context);
        titleMenuName = getIntent().getStringExtra("dishMenuName");
        dataList = (ArrayList)getIntent().getParcelableArrayListExtra("dishMenu");
        tvMenuTitle.setText(titleMenuName);
        if(dataList != null && dataList.size() >0)
        {
            dishMenuItemAdp = new DishMenuItemAdp(context);
            dishMenuItemAdp.setData(dataList);
            gvDishItem.setAdapter(dishMenuItemAdp);

            gvDishItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Dish dish = (Dish)dishMenuItemAdp.getItem(position);
                    if(dish != null)
                    {
                        Intent intent = new Intent(context, DishItemInfoAty.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("dish",dish);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
            });
        }
    }



    @OnClick( {R.id.img_back, R.id.tv_menu_title} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                DishItemAty.this.finish();
                break;
            case R.id.img_cart:

                break;
            case R.id.img_search:
                break;
        }
    }
}
