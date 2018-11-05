package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.swipemenulistview.SwipeMenuListView;
import cn.acewill.pos.next.ui.adapter.DishKidsCountAdp;
import cn.acewill.pos.next.ui.adapter.ModifyDishCountAdp;
import cn.acewill.pos.next.ui.adapter.SearchCountDoSettAdp;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ZoneGridView;

import static cn.acewill.pos.next.common.DishDataController.dishKindList;

/**
 * Created by DHH on 2017/7/13.
 */

public class DishCountSettAty extends BaseActivity {
    @BindView( R.id.btn_clean )
    TextView btnClean;
    @BindView( R.id.lin_title )
    LinearLayout linTitle;
    @BindView( R.id.lv_repertory )
    SwipeMenuListView lvRepertory;
    @BindView( R.id.ed_search )
    CommonEditText edSearch;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;
    @BindView( R.id.rel_top )
    RelativeLayout relTop;
    @BindView( R.id.btn_pre )
    TextView btnPre;
    @BindView( R.id.btn_next )
    TextView btnNext;
    @BindView( R.id.lin_do )
    LinearLayout linDo;
    @BindView( R.id.gv_dish_kind )
    ZoneGridView gvDishKind;
    @BindView( R.id.btn_all_clean )
    TextView btnAllClean;
    @BindView( R.id.btn_all_add )
    TextView btnAllAdd;
    @BindView( R.id.btn_all_minus )
    TextView btnAllMinus;
    @BindView( R.id.lin_doing )
    LinearLayout linDoing;
    @BindView( R.id.lv_order )
    ListView lvOrder;

    // 用于显示每列4个Item项。
    int VIEW_COUNT = 8;

    // 用于显示页号的索引
    int index = 0;
    /**
     * 滑动page的下标位置
     */
    private int currentPosition = 0;
    private List<DishType> dishKind;//菜品分类
    private List<Dish> modifyDishList = new CopyOnWriteArrayList<>();//这是修改过的菜品的历史记录
    private List<Dish> currentDishList = new CopyOnWriteArrayList<>();//这是当前选中的菜品列表

    private ProgressDialogF progressDialog;
    private DishKidsCountAdp dishKidsTopAdp;
    private SearchCountDoSettAdp searchCountDoSettAdp;
    private ModifyDishCountAdp modifyDishCountAdp;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolsUtils.writeUserOperationRecords("退出设置沽清界面");
        if (currentDishList != null && currentDishList.size() > 0) {
            currentDishList.clear();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_dish_count_sett);
        myApplication.addPage(DishCountSettAty.this);
        setTitle(ToolsUtils.returnXMLStr("sell_out"));
        setShowBtnBack(true);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        initData();
        loadData();
        getDishCounts();
    }

    public void getDishCounts() {
        try {
            StoreBusinessService storeBusinessService = StoreBusinessService.getInstance();
            storeBusinessService.getDishCounts(new ResultCallback<List<DishCount>>() {
                @Override
                public void onResult(List<DishCount> result) {
                    if (result != null && result.size() > 0) {
                        if (searchCountDoSettAdp != null) {
                            searchCountDoSettAdp.setDishCount(result);
                        }
                    } else {
                        showToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err")+"," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(ToolsUtils.returnXMLStr("get_dish_sell_out_state_err"));
        }
    }

    private void loadData() {
        if (DishDataController.dishKindList != null && DishDataController.dishKindList.size() > 0) {
            if (DishDataController.menuAllData != null && DishDataController.menuAllData.size() > 0) {
                setKidsMap(dishKindList);
                initDishAdapter();
            } else {
//                getDishInfo();
                getAllDishList();
            }

        } else {
            getKindInfo();
        }
    }

    /**
     * 获取菜品分类数据
     */
    private void getKindInfo() {
        progressDialog.showLoading("");
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
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    dishKind = result;
                    dishKindList = dishKind;
                    setKidsMap(dishKindList);
//                    getDishInfo();
                    getAllDishList();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                    Log.i("获取菜品分类为空", "");
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_error")+"," + e.getMessage());
                Log.i("获取菜品分类为空", e.getMessage());
            }
        });
    }

    /**
     * 得到菜品数据  dishList
     */
    private void getAllDishList() {
        progressDialog.showLoading("");
        DishService dishService = null;
        try {
            dishService = DishService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
            return;
        }
        dishService.getAllDishList(new ResultCallback<List<Menu>>() {
            @Override
            public void onResult(List<Menu> result) {
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    DishDataController.setDishAllData(result);
                    initDishAdapter();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(e.getMessage());
                Log.i("获取菜品为空", e.getMessage());
            }
        });
    }

    private void initDishAdapter() {
        progressDialog.disLoading();
        if (dishKidsTopAdp != null && gvDishKind != null) {
            setKidsMap(dishKindList);
            gvDishKind.setAdapter(dishKidsTopAdp);
            searchCountDoSettAdp = new SearchCountDoSettAdp(this);
            currentDishList = ToolsUtils.cloneTo(DishDataController.getAlldishsForKind(currentPosition));
            searchCountDoSettAdp.setData(currentDishList);
            lvOrder.setAdapter(searchCountDoSettAdp);
            dishKidsTopAdp.setSelect(currentPosition);

        }
    }


    private ArrayList<HashMap<String, DishType>> listItem = new ArrayList<>();

    private void setKidsMap(List<DishType> dishKindList) {
        if (listItem != null && listItem.size() > 0) {
            listItem.clear();
        }
        if (dishKindList != null && dishKindList.size() > 0) {
            int size = dishKindList.size();
            for (int i = 0; i < size; i++) {
                HashMap<String, DishType> map = new HashMap<String, DishType>();
                map.put("kindsItem", dishKindList.get(i));
                listItem.add(map);
            }
            checkButton();
            dishKidsTopAdp.setKidsMapList(listItem);
        }
    }

    public void checkButton() {
        // 索引值小于等于0，表示不能向前翻页了，以经到了第一页了。
        // 将向前翻页的按钮设为不可用。
        if (index <= 0) {
            btnPre.setEnabled(false);
            btnPre.setTextColor(resources.getColor(R.color.gray_check_sth));
        } else {
            btnPre.setEnabled(true);
            btnPre.setTextColor(resources.getColor(R.color.black));
        }
        // 值的长度减去前几页的长度，剩下的就是这一页的长度，如果这一页的长度比View_Count小，表示这是最后的一页了，后面在没有了。
        // 将向后翻页的按钮设为不可用。
        if (listItem.size() - index * VIEW_COUNT <= VIEW_COUNT) {
            btnNext.setEnabled(false);
            btnNext.setTextColor(resources.getColor(R.color.gray_check_sth));
        }
        // 否则将2个按钮都设为可用的。
        else {
            btnNext.setEnabled(true);
            btnNext.setTextColor(resources.getColor(R.color.black));
        }
    }

    // 点击左边的Button，表示向前翻页，索引值要减1.
    public void preView(boolean isSwitchData) {
        index--;

        dishKidsTopAdp.setSelectPage(index);
        if (isSwitchData) {
            dishKidsTopAdp.setSelect(VIEW_COUNT - 1);//切换到最后一项
            currentPosition = index * VIEW_COUNT + (VIEW_COUNT - 1);
            currentDishList = ToolsUtils.cloneTo(DishDataController.getAlldishsForKind(currentPosition));
            searchCountDoSettAdp.setData(currentDishList);
            //            vpDish.setCurrentItem(currentPosition);
        }
        // 刷新ListView里面的数值。
        dishKidsTopAdp.notifyDataSetChanged();

        // 检查Button是否可用。
        checkButton();
    }

    // 点击右边的Button，表示向后翻页，索引值要加1.
    public void nextView(boolean isSwitchData) {
        index++;

        dishKidsTopAdp.setSelectPage(index);
        if (isSwitchData) {
            dishKidsTopAdp.setSelect(0);//切换到第一项
            currentPosition = index * VIEW_COUNT;
            currentDishList = ToolsUtils.cloneTo(DishDataController.getAlldishsForKind(currentPosition));
            searchCountDoSettAdp.setData(currentDishList);
            //            vpDish.setCurrentItem(currentPosition);
        }
        // 刷新ListView里面的数值。
        dishKidsTopAdp.notifyDataSetChanged();

        // 检查Button是否可用。
        checkButton();
    }


    private void initData() {
        dishKidsTopAdp = new DishKidsCountAdp(this);
        progressDialog = new ProgressDialogF(this);
        searchCountDoSettAdp = new SearchCountDoSettAdp(this);
        modifyDishCountAdp = new ModifyDishCountAdp(this);
        lvRepertory.setAdapter(modifyDishCountAdp);

        if (currentDishList != null && currentDishList.size() > 0) {
            currentDishList.clear();
        }

        //适配菜品分类数据
        gvDishKind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentPosition = VIEW_COUNT * index + position;
                currentDishList = ToolsUtils.cloneTo(DishDataController.getAlldishsForKind(currentPosition));
                searchCountDoSettAdp.setData(currentDishList);
                dishKidsTopAdp.setSelect(currentPosition % VIEW_COUNT);
            }
        });

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
                        dishs = DishDataController.sortAllMarkDish(s.toString());
                        searchCountDoSettAdp.setData(dishs);
                    } else if (ToolsUtils.isLetter(s.toString())) {
                        dishs = DishDataController.searchAllDish(s.toString());
                        searchCountDoSettAdp.setData(dishs);
                    } else {
                        searchCountDoSettAdp.setData(currentDishList);
                    }

                } else {
                    searchCountDoSettAdp.setData(currentDishList);
                    searchClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //从选中菜品列表中直接沽清单个菜品
        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dish dish = (Dish) searchCountDoSettAdp.getItem(position);
                if (dish != null) {
                    List<Dish> dishCount = new ArrayList<Dish>();
                    dishCount.add(dish);
                    setDishCount(dishCount, 1);
                }
            }
        });
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

    private void setDishCount(final List<Dish> dishList, int dishCount) {
        if (dishList != null && dishList.size() > 0) {
            final List<Integer> dishIdList = setDishList(dishList);
            if (dishCount == 0) {
                updataDishCount(dishIdList, dishCount, dishList);
            } else {
                DialogUtil.inputDialog(context, ToolsUtils.returnXMLStr("set_sell_out_copies"), ToolsUtils.returnXMLStr("sell_out_copies"), ToolsUtils.returnXMLStr("set_dish_counts"), 0, false, true, new DialogEtCallback() {
                    @Override
                    public void onConfirm(String sth) {
                        int acount = Integer.valueOf(sth);
                        updataDishCount(dishIdList, acount, dishList);
                    }

                    @Override
                    public void onCancle() {
                    }
                });
            }
        }
    }

    private void updataDishCount(final List<Integer> dishList, final int dishCount, final List<Dish> modifyDishs) {
        try {
            DishService dishService = DishService.getInstance();
            dishService.updataDishCount(dishList, dishCount, new ResultCallback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    showToast(ToolsUtils.returnXMLStr("sell_out_is_success"));
                    getDishCounts();
                    addDishCountHistory(modifyDishs, dishCount, dishList);
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(ToolsUtils.returnXMLStr("sell_out_is_error")+","+ e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            showToast(ToolsUtils.returnXMLStr("sell_out_is_error")+"," + e.getMessage());
        }
    }

    private void cleanDishCountDetection() {
        if (!ToolsUtils.isList(modifyDishList)) {
            for (Dish dish : modifyDishList) {
                dish.setDishCountDetection(false);
            }
        }
    }

    private void addDishCountHistory(List<Dish> modifyDishs, int dishCount, final List<Integer> dishList) {
        if (modifyDishs != null && modifyDishs.size() > 0) {
            if (modifyDishList.size() == 0) {
                int size = modifyDishs.size();
                for (int i = 0; i < size; i++) {
                    Dish dish = modifyDishs.get(i);
                    if (dish != null) {
                        dish.setDishModifyCount(dishCount - dish.getDishCount());
                        dish.setDishCount(dishCount);
                        dish.setDishCountModify(true);
                        modifyDishList.add(dish);
                    }
                }
            } else {
                int size = modifyDishs.size();
                for (int i = 0; i < size; i++) {
                    Dish dish = modifyDishs.get(i);
                    if (dish != null) {
                        Dish dishTemp = ToolsUtils.cloneTo(dish);
                        dishTemp.setDishModifyCount(dishCount - dishTemp.getDishCount());
                        dishTemp.setDishCount(dishCount);
                        modifyDishList.add(dishTemp);
                    }
                }
            }
        }
        modifyDishList = ToolsUtils.cloneTo(modifyDishList);
        modifyDishCountAdp.setData(modifyDishList);
    }



    @OnClick( {R.id.btn_clean,R.id.search_clear, R.id.btn_all_clean, R.id.btn_all_add, R.id.btn_all_minus, R.id.btn_pre, R.id.btn_next} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_clean:
                if(!ToolsUtils.isList(modifyDishList))
                {
                    modifyDishList.clear();
                }
                modifyDishCountAdp.setData(modifyDishList);
                break;
            case R.id.search_clear:
                ToolsUtils.writeUserOperationRecords("清空搜索内容按钮");
                edSearch.setText("");
                break;
            case R.id.btn_all_clean:
                ToolsUtils.writeUserOperationRecords("全部沽清按钮");
                if (!ToolsUtils.isList(currentDishList)) {
                    setDishCount(currentDishList, 0);
                }
                break;
            case R.id.btn_all_add:
                ToolsUtils.writeUserOperationRecords("沽清份数全部增加按钮");
                if (!ToolsUtils.isList(currentDishList)) {
                    setDishCount(currentDishList, 1);
                }
                break;
            case R.id.btn_all_minus:
                ToolsUtils.writeUserOperationRecords("沽清份数全部减少按钮");
                if (!ToolsUtils.isList(currentDishList)) {
                    setDishCount(currentDishList, 1);
                }
                break;
            case R.id.btn_pre:
                preView(true);
                break;
            case R.id.btn_next:
                nextView(true);
                break;
        }
    }
}
