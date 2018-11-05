package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.StoreInfor;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.TerminalInfo;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.model.user.UserData;
import cn.acewill.pos.next.service.DishService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.ui.activity.LoginAty;
import cn.acewill.pos.next.ui.adapter.DishMenuAdp;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;

import static cn.acewill.pos.next.common.DishDataController.dishKindList;

/**
 * 菜单
 * Created by DHH on 2016/12/23.
 */

public class DishMenuAty extends BaseActivity {
    @BindView( R.id.img_user )
    ImageButton imgUser;
    @BindView( R.id.tv_menu_title )
    TextView tvMenuTitle;
    @BindView( R.id.img_cart )
    ImageButton imgCart;
    @BindView( R.id.img_search )
    ImageButton imgSearch;
    @BindView( R.id.iv_point1 )
    ImageView ivPoint1;
    @BindView( R.id.iv_point2 )
    ImageView ivPoint2;
    @BindView( R.id.iv_point3 )
    ImageView ivPoint3;
    @BindView( R.id.iv_point4 )
    ImageView ivPoint4;
    @BindView( R.id.gv_dish_item )
    GridView gvDishItem;
    @BindView( R.id.lin_points )
    LinearLayout linPoints;

    private ProgressDialogF progressDialog;

    private DishMenuAdp dishMenuAdp;

    int showPointType = 1;
    private List<ImageView> ivPointList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_dish_menu);
        ButterKnife.bind(this);
        myApplication.addPage(DishMenuAty.this);
        loadData();
    }

    private void loadData() {
        progressDialog = new ProgressDialogF(context);
        if (StoreInfor.terminalInfo != null) {
            TerminalInfo terminalInfo = StoreInfor.terminalInfo;
            String brandName = TextUtils.isEmpty(terminalInfo.brandName) ? ToolsUtils.returnXMLStr("wisdom_cash_register") : terminalInfo.brandName;
            String storeName = TextUtils.isEmpty(terminalInfo.sname) ? ToolsUtils.returnXMLStr("acewill_cloud_pos") : terminalInfo.sname;
            if (TextUtils.isEmpty(terminalInfo.sname)) {
                tvMenuTitle.setText(brandName);
            }
            tvMenuTitle.setText(brandName + "-" + storeName);
        }

        if (DishDataController.getDishKindList() != null && DishDataController.getDishKindList().size() > 0) {
            if (DishDataController.menuData != null && DishDataController.menuData.size() > 0) {
                initKindInfo();
            } else {
                getDishInfo();
            }
        } else {
            getKindInfo();
        }

        gvDishItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DishType dishKind = (DishType)dishMenuAdp.getItem(position);
                if(dishKind != null)
                {
                    List<Dish> datas = DishDataController.getdishsForKind(position);
                    if(datas != null && datas.size() >0)
                    {
                        Intent intent = new Intent(context, DishItemAty.class);
                        intent.putExtra("dishMenuName",dishKind.getName());
                        intent.putParcelableArrayListExtra("dishMenu",(ArrayList)datas);
                        startActivity(intent);
                    }
                    else{
                        MyApplication.getInstance().ShowToast(ToolsUtils.returnXMLStr("current_dish_get_is_null"));
                    }
                }
            }
        });
    }

    private void initKindInfo() {
        if (DishDataController.getDishKindList() != null && DishDataController.getDishKindList().size() > 0) {
            dishMenuAdp = new DishMenuAdp(context);
            ivPointList.add(ivPoint1);
            ivPointList.add(ivPoint2);
            ivPointList.add(ivPoint3);
            ivPointList.add(ivPoint4);

            dishMenuAdp.setData(DishDataController.getDishKindList());
            gvDishItem.setAdapter(dishMenuAdp);
            setPointType(showPointType);
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
                    dishKindList = result;
                    getDishInfo();
                } else {
                    showToast(ToolsUtils.returnXMLStr("get_dish_kind_is_null"));
                    DishMenuAty.this.finish();
                    Log.i("获取菜品分类为空", "");
                }
            }

            @Override
            public void onError(PosServiceException e) {
                progressDialog.disLoading();
                showToast(ToolsUtils.returnXMLStr("get_dish_kind_error")+"," + e.getMessage());
                DishMenuAty.this.finish();
                Log.i("获取菜品分类为空", e.getMessage());
            }
        });
    }

    /**
     * 得到菜品数据  dishList
     */
    private void getDishInfo() {
        progressDialog.showLoading("");
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
                progressDialog.disLoading();
                if (result != null && result.size() > 0) {
                    DishDataController.setDishData(result);
                }
                initKindInfo();
            }

            @Override
            public void onError(PosServiceException e) {
                initKindInfo();
                progressDialog.disLoading();
                showToast(e.getMessage());
                Log.i("获取菜品为空", e.getMessage());
            }
        });
    }


    private void setPointType(int showPointType) {
        int size = ivPointList.size();
        for (int i = 0; i < size; i++) {
            ImageView iv = ivPointList.get(i);
            if (i < showPointType) {
                iv.setVisibility(View.VISIBLE);
            } else {
                iv.setVisibility(View.GONE);
            }
        }
        gvDishItem.setNumColumns(showPointType);
        dishMenuAdp.setShowType(showPointType);
    }


    @OnClick( {R.id.img_user, R.id.tv_menu_title, R.id.lin_points} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_user:
                jumpLogin();
                break;
            case R.id.img_cart:

                break;
            case R.id.img_search:
                break;
            case R.id.lin_points:
                ++showPointType;
                if (showPointType > 4 && showPointType != 4) {
                    showPointType = 1;
                }
                setPointType(showPointType);
                break;
        }
    }

    /**
     * 跳转到Login界面
     */
    private void jumpLogin() {
        ToolsUtils.writeUserOperationRecords("跳转到login界面");
        UserData mUserData = UserData.getInstance(context);
        mUserData.setUserName("");
        mUserData.setPwd("");
        mUserData.setSaveStated(false);
        errorReturnLogin();
    }
    /**
     * 当出错时,退回到LoginAty重新登录
     */
    private void errorReturnLogin() {
        myApplication.clean();
        myApplication.unbindCashBoxServer();
        myApplication.soundPool.stop(1);
        Intent orderIntent = new Intent(DishMenuAty.this, LoginAty.class);
        orderIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(orderIntent);
    }
}
