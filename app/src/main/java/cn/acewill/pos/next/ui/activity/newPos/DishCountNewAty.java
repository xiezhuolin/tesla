package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.adapter.DishKindsNewAdp;
import cn.acewill.pos.next.ui.adapter.SearchCountAdp;
import cn.acewill.pos.next.ui.adapter.SearchCountDoAdp;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DishCountUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ScrolGridView;

import static cn.acewill.pos.next.common.DishDataController.dishKindList;

/**
 * 菜品沽清设置
 * Created by DHH on 2016/9/5.
 */
public class DishCountNewAty extends BaseActivity {
    @BindView( R.id.btn_all_clean )
    TextView btnAllClean;
    @BindView( R.id.btn_all_add )
    TextView btnAllAdd;
    @BindView( R.id.btn_all_minus )
    TextView btnAllMinus;
    @BindView( R.id.lv_disCount )
    ListView lvDisCount;
    @BindView( R.id.ed_search )
    CommonEditText edSearch;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;
    @BindView( R.id.gv_search )
    ScrolGridView gvSearch;
    @BindView( R.id.gv_dish_kind )
    GridView gvDishKind;

    private DishCountUtil dishCountUtil;
    private ProgressDialogF progressDialogF;
    private SearchCountAdp searchCountAdp;
    private SearchCountDoAdp searchCountDoAdp;
    private DishKindsNewAdp dishKindsAdp;

    private List<DishType> dishKind;//菜品分类
    private int currentPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_dish_count_new);
        myApplication.addPage(DishCountNewAty.this);
        setTitle(ToolsUtils.returnXMLStr("sell_out"));
        setShowBtnBack(true);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        initData();
        loadData();
        getDishCounts();
    }

    private void initDishAdapter() {
        if (dishKindsAdp != null && gvDishKind != null) {
            dishKindsAdp.setData(dishKindList);
            gvDishKind.setAdapter(dishKindsAdp);
        }
        //适配菜品分类数据
        gvDishKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = position;
                dishKindsAdp.setSelect(position);
                dishCountUtil.dishItemList = DishDataController.getdishsForKind(position);
                searchCountDoAdp.setData(dishCountUtil.dishItemList);
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
            getKindInfo();
        }
    }

    /**
     * 获取菜品分类数据
     */
    private void getKindInfo() {
        progressDialogF.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getKindDataInfo(new ResultCallback<List<DishType>>() {
            @Override
            public void onResult(List<DishType> result) {
                progressDialogF.disLoading();
                if (result != null && result.size() > 0) {
                    dishKind = result;
                    dishKindList = dishKind;
                    getDishInfo();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialogF.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_error")+","+ e.getMessage());
            }
        });
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
                if (result != null && result.size() > 0) {
                    DishDataController.setDishData(result);
                    progressDialogF.disLoading();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialogF.disLoading();
                showToast(e.getMessage());
            }
        });
    }

    private void initData() {
        progressDialogF = new ProgressDialogF(context);
        dishCountUtil = DishCountUtil.getInstance();
        searchCountAdp = new SearchCountAdp(context);
        searchCountDoAdp = new SearchCountDoAdp(context);
        gvSearch.setAdapter(searchCountAdp);
        searchCountDoAdp.setData(dishCountUtil.dishItemList);
        lvDisCount.setAdapter(searchCountDoAdp);
        dishKindsAdp = new DishKindsNewAdp(context);

        //edtext的控件内容监听
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                    List<Dish> dishs = null;
                    if (ToolsUtils.isNumeric(s.toString())) {
                        gvSearch.setVisibility(View.VISIBLE);
                        dishs = DishDataController.sortMarkDish(s.toString());
                        searchCountAdp.setData(dishs);
                    } else if (ToolsUtils.isLetter(s.toString())) {
                        gvSearch.setVisibility(View.VISIBLE);
                        dishs = DishDataController.searchDish(s.toString());
                        searchCountAdp.setData(dishs);
                    } else {
                        gvSearch.setVisibility(View.GONE);
                    }

                } else {
                    gvSearch.setVisibility(View.VISIBLE);
                    searchClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //从选中菜品列表中直接沽清单个菜品
        lvDisCount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dish dish = (Dish) searchCountDoAdp.getItem(position);
                if (dish != null) {
                    List<Dish> dishCount = new ArrayList<Dish>();
                    dishCount.add(dish);
                    setDishCount(dishCount, 1);
                }
            }
        });

        //从搜索结果中直接沽清单个菜品
        gvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dish dish = (Dish) searchCountAdp.getItem(position);
                if (dish != null) {
                    List<Dish> dishCount = new ArrayList<Dish>();
                    dishCount.add(dish);
                    setDishCount(dishCount, 1);
                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void getDishCounts() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
                @Override
                public void onResult(List<DishCount> result) {
                    if (result != null && result.size() > 0) {
                        if (searchCountDoAdp != null) {
                            searchCountDoAdp.setDishCount(result);
                        }
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

    private List<Integer> setDishList(List<Dish> dishList) {
        List<Integer> dishIdList = new ArrayList<Integer>();
        if (dishList != null && dishList.size() > 0) {
            for (Dish dish : dishList) {
                dishIdList.add(dish.getDishId());
            }
        }
        return dishIdList;
    }

    private void setDishCount(List<Dish> dishList, int dishCount) {
        if (dishList != null && dishList.size() > 0) {
            final List<Integer> dishIdList = setDishList(dishList);
            if (dishCount == 0) {
                updataDishCount(dishIdList, dishCount);
            } else {
                DialogUtil.inputDialog(context, "设置菜品沽清份数", "份数", "设置菜品数量", 0, false, true,new DialogEtCallback() {
                    @Override
                    public void onConfirm(String sth) {
                        int acount = Integer.valueOf(sth);
                        updataDishCount(dishIdList, acount);
                    }

                    @Override
                    public void onCancle() {
                    }
                });
            }
        }
    }

    private void updataDishCount(List<Integer> dishList, int dishCount) {
        try {
            DishService dishService = DishService.getInstance();
            dishService.updataDishCount(dishList, dishCount, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    showToast("设置菜品沽清成功!");
                    getDishCounts();
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("设置菜品沽清失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast("设置菜品沽清失败," + e.getMessage());
        }
    }


    @OnClick( {R.id.search_clear, R.id.btn_all_clean, R.id.btn_all_add, R.id.btn_all_minus} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_clear:
                ToolsUtils.writeUserOperationRecords("清空搜索内容按钮");
                edSearch.setText("");
                break;
            case R.id.btn_all_clean:
                ToolsUtils.writeUserOperationRecords("全部沽清按钮");
                if(dishCountUtil.dishItemList != null && dishCountUtil.dishItemList.size() >0)
                {
                    setDishCount(dishCountUtil.dishItemList, 0);
                }
                break;
            case R.id.btn_all_add:
                ToolsUtils.writeUserOperationRecords("沽清份数全部增加按钮");
                if(dishCountUtil.dishItemList != null && dishCountUtil.dishItemList.size() >0)
                {
                    setDishCount(dishCountUtil.dishItemList, 1);
                }
                break;
            case R.id.btn_all_minus:
                ToolsUtils.writeUserOperationRecords("沽清份数全部减少按钮");
                if(dishCountUtil.dishItemList != null && dishCountUtil.dishItemList.size() >0)
                {
                    setDishCount(dishCountUtil.dishItemList, 1);
                }
              break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolsUtils.writeUserOperationRecords("退出设置沽清界面");
    }
}
