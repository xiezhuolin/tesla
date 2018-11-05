package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DishCountListener;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.presenter.DishPresenter;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.ui.DialogView;
import cn.acewill.pos.next.ui.adapter.DishCountKindsAdp;
import cn.acewill.pos.next.ui.adapter.DishCountPagerAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;

/**
 * 菜品沽清设置
 * Created by DHH on 2016/9/5.
 */
public class DishCountFragment extends BaseFragment implements DialogView, DishCountListener {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dish_count, container, false);
        ButterKnife.bind(this, view);
        initData();
        loadData();
        onSetListener();
        return view;
    }

    private void initData() {
        progressDialogF = new ProgressDialogF(aty);
        dishPresenter = new DishPresenter(this);
        dishCountKindsAdp = new DishCountKindsAdp(aty);

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
        if(vpDish.getAdapter() == null)
        {
            dishPresenter.getKindData();
        }
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

                dishCountKindsAdp.setData(dishKind);
                gvDishKind.setAdapter(dishCountKindsAdp);

                dishCountPagerAdp = new DishCountPagerAdp(aty, DishCountFragment.this);
                vpDish.setAdapter(dishCountPagerAdp);
                vpDish.setCurrentItem(currentPosition);
                dishCountKindsAdp.setSelect(currentPosition);
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
//        DialogUtil.inputDialog(aty, "设置菜品沽清", "份数", "设置菜品数量",0,false, new DialogEtCallback() {
//            @Override
//            public void onConfirm(String sth) {
//                try {
//                    int dishCount = Integer.valueOf(sth);
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
}
