package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DishCountListener;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.presenter.DishPresenter;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.adapter.DishCountKindsAdp;
import cn.acewill.pos.next.ui.adapter.DishCountPagerAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;

/**
 * 菜品沽清设置
 * Created by DHH on 2016/9/5.
 */
public class DishCountAty extends BaseActivity implements DialogView, DishCountListener {
    @BindView( R.id.gv_dish_kind )
    GridView gvDishKind;
    @BindView( R.id.vp_dish )
    ViewPager vpDish;

    private DishPresenter dishPresenter;
    private ProgressDialogF progressDialogF;
    private DishCountKindsAdp dishCountKindsAdp;
    private List<DishType> dishKind;//菜品分类

    private DishCountPagerAdp dishCountPagerAdp;

    /**
     * 滑动page的下标位置
     */
    private int currentPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.fragment_dish_count);
        myApplication.addPage(DishCountAty.this);
        setTitle(ToolsUtils.returnXMLStr("sell_out"));
        setShowBtnBack(true);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        getDishCounts();
        initData();
        loadData();
        onSetListener();
    }


    private void initData() {
        progressDialogF = new ProgressDialogF(context);
        dishPresenter = new DishPresenter(this);
        dishCountKindsAdp = new DishCountKindsAdp(context);

        vpDish.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                dishCountKindsAdp.setSelect(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        //适配菜品分类数据
        gvDishKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                vpDish.setCurrentItem(currentPosition);
                dishCountKindsAdp.setSelect(currentPosition);
                //                DishAdp dishAdapter = new DishAdp(aty, position);
                //                gvDishItem.setAdapter(dishAdapter);
            }
        });


    }

    private void loadData() {
        if (DishDataController.dishKindList != null && DishDataController.dishKindList.size() > 0) {
            if (DishDataController.menuData != null && DishDataController.menuData.size() > 0) {
                initDishAdapter();
            } else {
                getDishInfo();
            }
        } else {
            dishPresenter.getKindData();
        }
    }

    private void initDishAdapter() {
        dishCountKindsAdp.setData(DishDataController.dishKindList);
        gvDishKind.setAdapter(dishCountKindsAdp);
        dishCountPagerAdp = new DishCountPagerAdp(context, DishCountAty.this);
        vpDish.setAdapter(dishCountPagerAdp);
        vpDish.setCurrentItem(currentPosition);
        dishCountKindsAdp.setSelect(currentPosition);
    }

    private void onSetListener() {

    }

    @Override
    public void showDialog() {
        progressDialogF.showLoading("");
    }

    @Override
    public void dissDialog() {
        progressDialogF.disLoading();
    }

    @Override
    public void showError(PosServiceException e) {
    }

    /**
     * 得到菜品数据  dishList
     */
    private void getDishInfo() {
        progressDialogF.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getDishList(new ResultCallback<List<Menu>>() {
            @Override
            public void onResult(List<Menu> result) {
                DishDataController.setDishData(result);
                initDishAdapter();
                progressDialogF.disLoading();

                //                DishAdp dishAdp = new DishAdp(aty, 0);
                //                gvDishItem.setAdapter(dishAdp);
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialogF.disLoading();
            }
        });
    }

    @Override
    public <T> void callBackData(T t) {
        dishKind = (List<DishType>) t;
        if (!ToolsUtils.isList(dishKind)) {
            DishDataController.dishKindList = dishKind;
            getDishInfo();
        } else {
            myApplication.ShowToast("菜品分类为空!");
        }
    }

//    private void setDishCountInfo(final Dish dish) {
//        DialogUtil.inputDialog(context, "设置菜品沽清", "份数", "设置菜品数量", 0, false, new DialogEtCallback() {
//            @Override
//            public void onConfirm(String sth) {
//                try {
//                    Integer dishCount = Integer.valueOf(sth);
//                    DishService dishService = DishService.getInstance();
//                    dishService.updataDishCount(dish.getDishId(), dishCount, new ResultCallback<Integer>() {
//                        @Override
//                        public void onResult(Integer result) {
//                            showToast("设置菜品沽清成功!");
//                            getDishInfo();
//                        }
//
//                        @Override
//                        public void onError(PosServiceException e) {
//                            showToast("设置菜品沽清失败!");
//                        }
//                    });
//                } catch (PosServiceException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancle() {
//            }
//        });
//
//    }

    @Override
    public void setDishCont(Dish dish) {
//        setDishCountInfo(dish);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDishCounts();
    }

    public  void getDishCounts() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
                @Override
                public void onResult(List<DishCount> result) {
                    if (result != null && result.size() > 0) {
                        dishCountPagerAdp.setDishCount(result);
                    } else {
                        MyApplication.getInstance().ShowToast("获取菜品沽清状态失败!");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    MyApplication.getInstance().ShowToast("获取菜品沽清状态失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            MyApplication.getInstance().ShowToast("获取菜品沽清状态失败!");
        }
    }
}
