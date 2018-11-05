package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
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
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishTime;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Unit;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * 添加菜品
 * Created by aqw on 2016/12/8.
 */
public class AddDishAty extends BaseActivity {
    @BindView( R.id.tv_dishCategory )
    TextView tvDishCategory;
    @BindView( R.id.ed_dishName )
    EditText edDishName;
    @BindView( R.id.ed_dishPrice )
    CommonEditText edDishPrice;
    @BindView( R.id.ed_dishCount )
    CommonEditText edDishCount;
    @BindView( R.id.save_btn )
    TextView saveBtn;
    @BindView( R.id.tv_dishUnit )
    TextView tvDishUnit;
    @BindView( R.id.tv_dishTime )
    TextView tvDishTime;

    private OptionPicker optionPicker;
    private OptionPicker dishUnitPicker;
    private OptionPicker dishTimePicker;
    private String selectOption = "";
    private String selectUnit = "";
    private String selectDishTime = "";
    private ArrayList<String> dishDishCategoryList = new ArrayList<String>();
    private ArrayList<String> dishUnitsList = new ArrayList<String>();
    private ArrayList<String> dishTimesList = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_add_dish);
        myApplication.addPage(AddDishAty.this);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        setShowBtnBack(true);
        setTitle(ToolsUtils.returnXMLStr("add_dish"));
        initData();
    }
    private void setDishKinds(boolean isShow)
    {
        if(dishDishCategoryList.size() == 0)
        {
            if(DishDataController.dishKindList != null && DishDataController.dishKindList.size() >0)
            {
                for (DishType dishType : DishDataController.dishKindList) {
                    dishDishCategoryList.add(dishType.getName());
                }
            }
        }
        if (dishDishCategoryList != null && dishDishCategoryList.size() > 0) {
            optionPicker = new OptionPicker(AddDishAty.this, dishDishCategoryList);
            MyApplication application = MyApplication.getInstance();
            optionPicker.setHeight(application.getScreenHeight() / 3);
            optionPicker.setCancelTextColor(application.getResources().getColor(R.color.red));
            optionPicker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
            optionPicker.setCancelTextSize(28);
            optionPicker.setSubmitTextSize(28);
            optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int position, String option) {
                    selectOption = option;
                    tvDishCategory.setText(selectOption);
                }
            });
        }
        if(isShow)
        {
            if(optionPicker != null )
            {
                optionPicker.show();
            }
        }
    }

    private void setDishUnit(boolean isShow)
    {
        if(dishUnitsList.size() == 0)
        {
            if(DishDataController.dishUnitList != null && DishDataController.dishUnitList.size() >0)
            {
                for (Unit unit : DishDataController.dishUnitList) {
                    dishUnitsList.add(unit.getUnitName());
                }
            }
        }
        if (dishUnitsList != null && dishUnitsList.size() > 0) {
            dishUnitPicker = new OptionPicker(AddDishAty.this, dishUnitsList);
            MyApplication application = MyApplication.getInstance();
            dishUnitPicker.setHeight(application.getScreenHeight() / 3);
            dishUnitPicker.setCancelTextColor(application.getResources().getColor(R.color.red));
            dishUnitPicker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
            dishUnitPicker.setCancelTextSize(28);
            dishUnitPicker.setSubmitTextSize(28);
            dishUnitPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int position, String option) {
                    selectUnit = option;
                    tvDishUnit.setText(selectUnit);
                }
            });
        }
        if(isShow)
        {
            if(dishUnitPicker != null )
            {
                dishUnitPicker.show();
            }
        }
    }

    private void setDishTime(boolean isShow)
    {
        if(dishTimesList.size() == 0)
        {
            if(DishDataController.dishTimeList != null && DishDataController.dishTimeList.size() >0)
            {
                for (DishTime dishTime : DishDataController.dishTimeList) {
                    dishTimesList.add(dishTime.getTimeName());
                }
            }
        }

        if (dishTimesList != null && dishTimesList.size() > 0) {
            dishTimePicker = new OptionPicker(AddDishAty.this, dishTimesList);
            MyApplication application = MyApplication.getInstance();
            dishTimePicker.setHeight(application.getScreenHeight() / 3);
            dishTimePicker.setCancelTextColor(application.getResources().getColor(R.color.red));
            dishTimePicker.setSubmitTextColor(application.getResources().getColor(R.color.blue_table_nomber_title));
            dishTimePicker.setCancelTextSize(28);
            dishTimePicker.setSubmitTextSize(28);
            dishTimePicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int position, String option) {
                    selectDishTime = option;
                    tvDishTime.setText(selectDishTime);
                }
            });
        }
        if(isShow)
        {
            if(dishTimePicker != null )
            {
                dishTimePicker.show();
            }
        }

    }

    private void initData() {
        if(DishDataController.dishKindList == null || DishDataController.dishKindList.size() == 0)
        {
            getKindInfo(false);
        }
        else
        {
            setDishKinds(false);
        }
        if(DishDataController.dishUnitList == null || DishDataController.dishUnitList.size() == 0)
        {
            getDishUnit(false);
        }
        if(DishDataController.dishTimeList == null ||  DishDataController.dishTimeList.size() == 0)
        {
            getDishTime(false);
        }
    }

    private String dishKindId;
    private int dishUnitId;
    private long dishTimeId;
    private void addDish() {
        for (DishType dishType : DishDataController.dishKindList) {
            if (dishType.getName().equals(selectOption)) {
                dishKindId = String.valueOf(dishType.getId());
            }
        }
        for (Unit unit : DishDataController.dishUnitList) {
            if (unit.getUnitName().equals(selectUnit)) {
                dishUnitId = Integer.valueOf(String.valueOf(unit.getUnitid()));
            }
        }
        for (DishTime dishTime : DishDataController.dishTimeList) {
            if (dishTime.getTimeName().equals(selectDishTime)) {
                dishTimeId = dishTime.getTimeid();
            }
        }
        String dishCategory = tvDishCategory.getText().toString().trim();
        String dishUnit = tvDishUnit.getText().toString().trim();
        String dishTime = tvDishTime.getText().toString().trim();
        String dishName = edDishName.getText().toString().trim();
        String dishPrice = edDishPrice.getText().toString().trim();
        String dishCount = edDishCount.getText().toString().trim();
        if (TextUtils.isEmpty(dishCategory)) {
            showToast("请选择菜品分类");
            return;
        }
        if (TextUtils.isEmpty(dishUnit)) {
            showToast("请选择菜品单位");
            return;
        }
        if (TextUtils.isEmpty(dishTime)) {
            showToast("请选择菜品时段");
            return;
        }
        if (TextUtils.isEmpty(dishName)) {
            showToast("请输入菜品名");
            return;
        }
        if (TextUtils.isEmpty(dishPrice)) {
            showToast("请输入菜品价格");
            return;
        }
        if (TextUtils.isEmpty(dishCount)) {
            dishCount = "99999";
        }
        PosInfo posInfo = PosInfo.getInstance();
        Dish newDish = new Dish();
        newDish.setAppid(posInfo.getAppId());
        newDish.setBrandid(Long.valueOf(posInfo.getBrandId()));
        newDish.setStoreid(Long.valueOf(posInfo.getStoreId()));
        newDish.setPrice(new BigDecimal(dishPrice).setScale(2, BigDecimal.ROUND_DOWN));
        newDish.setDishCount(Integer.valueOf(dishCount));
        newDish.setDishName(dishName);
        newDish.setDishKind(dishKindId);
        newDish.setDishUnitID(dishUnitId);
        newDish.setDishTimeID(dishTimeId);
        try {
            DishService dishService = DishService.getInstance();
            dishService.addDish(newDish, new ResultCallback<PosResponse>() {
                @Override
                public void onResult(PosResponse result) {
                    if (result != null && result.isSuccessful()) {
                        showToast("添加菜品成功!");
                        DialogUtil.ordinaryDialog(context, ToolsUtils.returnXMLStr("add_dish"), "添加菜品成功!是否要退出重启APP.[按下(确定)重新启动APP,按下(取消)即可继续添加.]", new DialogCallback() {
                            @Override
                            public void onConfirm() {
                                myApplication.clean();
                                Intent orderIntent = new Intent(AddDishAty.this, LoginAty.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(orderIntent);
                            }

                            @Override
                            public void onCancle() {
                                selectOption = "";
                                tvDishCategory.setText("");
                                tvDishUnit.setText("");
                                tvDishTime.setText("");
                                edDishName.setText("");
                                edDishPrice.setText("");
                                edDishCount.setText("");
                            }
                        });
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("添加菜品失败", e.getMessage());
                    showToast("添加菜品失败!" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("添加菜品失败", e.getMessage());
            showToast("添加菜品失败!" + e.getMessage());
        }
    }

    private void getKindInfo(final boolean isShow) {
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
                if (result != null && result.size() > 0) {
                    DishDataController.dishKindList = result;
                    setDishKinds(isShow);
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                }
            }

            @Override
            public void onError(PosServiceException e) {
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_error")+"," + e.getMessage());
            }
        });
    }

    private void getDishUnit(final boolean isShow)
    {
        try {
            DishService dishService = DishService.getInstance();
            dishService.getDishUnit(new ResultCallback<List<Unit>>() {
                @Override
                public void onResult(List<Unit> result) {
                    if (result != null && result.size() >0) {
                        DishDataController.dishUnitList = result;
                        setDishUnit(isShow);
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取菜品单位失败", e.getMessage());
                    showToast("获取菜品单位失败!" + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("获取菜品单位失败", e.getMessage());
            showToast("获取菜品单位失败!" + e.getMessage());
        }
    }

    private void getDishTime(final boolean isShow)
    {
        try {
            DishService dishService = DishService.getInstance();
            dishService.getDishTime(new ResultCallback<List<DishTime>>() {
                @Override
                public void onResult(List<DishTime> result) {
                    if (result != null && result.size() >0) {
                        DishDataController.dishTimeList = result;
                        setDishTime(isShow);
                    }
                }

                @Override
                public void onError(PosServiceException e) {

                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
            Log.i("获取菜品时段失败", e.getMessage());
            showToast("获取菜品时段失败!" + e.getMessage());
        }
    }

    @OnClick( {R.id.save_btn, R.id.tv_dishCategory,R.id.tv_dishUnit,R.id.tv_dishTime} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_btn:
                ToolsUtils.writeUserOperationRecords("添加菜品按钮");
                addDish();
                break;
            case R.id.tv_dishCategory:
                ToolsUtils.writeUserOperationRecords("选择菜品分类按钮");
                setDishInfo(1);
                break;
            case R.id.tv_dishUnit:
                ToolsUtils.writeUserOperationRecords("选择菜品单位按钮");
                setDishInfo(2);
                break;
            case R.id.tv_dishTime:
                ToolsUtils.writeUserOperationRecords("选择菜品时段按钮");
                setDishInfo(3);
                break;
        }
    }

    private void setDishInfo(int type)
    {
        if(type == 1)
        {
            if(DishDataController.dishKindList == null || DishDataController.dishKindList.size() == 0)
            {
                getKindInfo(true);
            }
            else
            {
                setDishKinds(true);
            }
        }
        else if(type == 2)
        {
            if(DishDataController.dishUnitList == null || DishDataController.dishUnitList.size() == 0)
            {
                getDishUnit(true);
            }
            else
            {
                setDishUnit(true);
            }
        }
        else if(type == 3)
        {
            if(DishDataController.dishTimeList == null || DishDataController.dishTimeList.size() == 0)
            {
                getDishTime(true);
            }
            else
            {
                setDishTime(true);
            }
        }
    }
}
