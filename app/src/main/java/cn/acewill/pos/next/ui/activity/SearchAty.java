package cn.acewill.pos.next.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.swipemenulistview.SwipeMenu;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuCreator;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuItem;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuListView;
import cn.acewill.pos.next.ui.adapter.SearchDishAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;

/**
 * Created by DHH on 2016/6/12.
 */
public class SearchAty extends BaseActivity {
    @BindView( R.id.rel_back )
    RelativeLayout relBack;
    @BindView( R.id.search_cotent )
    CommonEditText searchCotent;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;
    @BindView( R.id.lv_order )
    SwipeMenuListView lvOrder;


    public SearchDishAdp searchDishAdp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_search);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        myApplication.addPage(SearchAty.this);

        searchDishAdp = new SearchDishAdp(this);
        lvOrder.setAdapter(searchDishAdp);

        //左滑删除
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // Create different menus depending on the view type
                createMenu1(menu);
            }

            private void createMenu1(SwipeMenu menu) {
                SwipeMenuItem item1 = new SwipeMenuItem(context);
                item1.setBackground(new ColorDrawable(Color.rgb(0xfe, 0x00, 0x00)));
                item1.setWidth(dp2px(90));
                item1.setTitle(ToolsUtils.returnXMLStr("delete"));
                item1.setTitleSize(18);
                item1.setTitleColor(resources.getColor(R.color.white));
                menu.addMenuItem(item1);
            }
        };

        //edtext的控件内容监听
        searchCotent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                    List<Dish> dishs = null;
                    if(ToolsUtils.isNumeric(s.toString()))
                    {
                        lvOrder.setVisibility(View.VISIBLE);
                        dishs = DishDataController.sortMarkDish(s.toString());
                        searchDishAdp.setData(dishs);
                    }
                    else if(ToolsUtils.isLetter(s.toString()))
                    {
                        lvOrder.setVisibility(View.VISIBLE);
                        dishs = DishDataController.searchDish(s.toString());
                        searchDishAdp.setData(dishs);
                    }
                    else
                    {
                        lvOrder.setVisibility(View.GONE);
                    }

                } else {
                    lvOrder.setVisibility(View.VISIBLE);
                    searchClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //设置键盘的回车按钮监听
        searchCotent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    ToolsUtils.hideInputManager(context,searchCotent);
                    return true;
                }
                return false;
            }
        });

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @OnClick( {R.id.rel_back,R.id.search_clear} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_back:
                SearchAty.this.finish();
                break;
            case R.id.search_clear:
                searchCotent.setText("");
                break;
        }
    }
}
