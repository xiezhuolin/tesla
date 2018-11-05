package cn.acewill.pos.next.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.OptionCategory;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.activity.PackagerAty;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

import static cn.acewill.pos.R.id.lin_sole_out;

/**
 * Created by DHH on 2016/6/17.
 */
public class DishAdp<T> extends BaseAdapter {
    private List<Dish> datas;
    private ImageLoader imageLoader;
    private int widch;
    private PosInfo posInfo;
    private String logoPath;

    public DishAdp(Context context, int position) {
        super(context);
        //        datas = DishDataController.getdishsForKind(position);
        imageLoader = MyApplication.getInstance().initImageLoader(context);
        int screenWidth = MyApplication.getInstance().getScreenWidth();
        widch = (screenWidth / 8);
        posInfo = posInfo.getInstance();
        logoPath = posInfo.getLogoPath();
    }

    @Override
    public int getCount() {
        if (datas == null) {
            EventBus.getDefault().post(new PosEvent(Constant.EventState.CURRENT_TIME_DISH_NULL));
        }
        return datas != null ? datas.size() : 0;
    }

    public void setDataInfo(List<Dish> dataList) {
        if (dataList != null) {
            this.datas = dataList;
            this.notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Dish dish = datas.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dish_main, null);
            holder.lin_sole_out = (LinearLayout) convertView.findViewById(lin_sole_out);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_dishMoney = (TextView) convertView.findViewById(R.id.tv_money);
            holder.btn_add = (TextView) convertView.findViewById(R.id.btn_add);
            holder.iv_dishImagePath = (ImageView) convertView.findViewById(R.id.iv_dishImagePath);

            //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widch,(widch*2)/3);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(widch, (int)(widch*8.4)/9);
            convertView.setLayoutParams(params);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        holder.tv_dishName.setText(dish.getDishName());

        String money = String.format("%.2f ", dish.getPrice());
        String dishUnit = dish.getDishUnit();
        dishUnit = TextUtils.isEmpty(dishUnit)?"份":dishUnit;
        holder.tv_dishMoney.setText("¥ " + money+"/"+dishUnit);

        if (!TextUtils.isEmpty(dish.getImageName())) {
            imageLoader.displayImage(dish.getImageName(), holder.iv_dishImagePath);
        } else {
            if (!TextUtils.isEmpty(logoPath)) {
                imageLoader.displayImage(logoPath, holder.iv_dishImagePath);
            } else {
                imageLoader.displayImage(dish.getImageName(), holder.iv_dishImagePath);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDish(dish);
            }
        });

        if (dishCountList != null && dishCountList.size() >= 0) {
            for (DishCount dishCount : dishCountList) {
                if (dishCount.dishid == dish.getDishId()) {
                    if (dishCount.count <= 0) {
//                        isShowSelectDish(false, holder);
                        holder.lin_sole_out.setVisibility(View.VISIBLE);
                        convertView.setClickable(false);
                        break;
                    }
                    else{
                        holder.lin_sole_out.setVisibility(View.INVISIBLE);
                    }
                }
            }
//            if (holder.iv_sale_out.getVisibility() == View.VISIBLE) {
//                for (Dish dishItem : Cart.getDishItemList()) {
//                    if (dishItem.dishId == dish.dishId) {
//                        isShowSelectDish(true, holder);
//                        convertView.setClickable(true);
//                    }
//                    break;
//                }
//            }
        } else {
            if (dish.dishCount <= 0) {
//                isShowSelectDish(false, holder);
                holder.lin_sole_out.setVisibility(View.VISIBLE);
                convertView.setClickable(false);
            }
            else{
                holder.lin_sole_out.setVisibility(View.INVISIBLE);
            }
//            else {
//                for (Dish dishItem : Cart.getDishItemList()) {
//                    if (dishItem.dishId == dish.dishId) {
//                        isShowSelectDish(true, holder);
//                        convertView.setClickable(true);
//                    }
//                    break;
//                }
//            }
        }
        return convertView;
    }

//    private void isShowSelectDish(boolean isSealect, ViewHolder holder) {
//        if (isSealect) {
//            holder.iv_sale_dish.setVisibility(View.VISIBLE);
//            holder.iv_sale_out.setVisibility(View.GONE);
//        } else {
//            holder.iv_sale_dish.setVisibility(View.GONE);
//            holder.iv_sale_out.setVisibility(View.VISIBLE);
//        }
//    }

    private List<DishCount> dishCountList = new ArrayList<>();

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList = dishCountList;
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_dishMoney;
        TextView btn_add;
        ImageView iv_dishImagePath;
        LinearLayout lin_sole_out;
    }

    public void addDish(Dish mDishModel) {
        ToolsUtils.writeUserOperationRecords("点击了("+mDishModel.getDishName()+")菜品");
        Integer dishOptionId = TimeUtil.getStringData();
        mDishModel.setDishOptionId(dishOptionId);
        if (mDishModel.isPackage()) {
            Intent intent = new Intent(context, PackagerAty.class);
            intent.putExtra("dish",(Serializable)mDishModel);
            context.startActivity(intent);
            return;
        } else if (!mDishModel.isOptionRequired()) {
            Cart.getInstance().addItem(mDishModel);
        } else {
            List<OptionCategory> optionCategoryList = mDishModel.getOptionCategoryList();
            int count = 0;
            for (OptionCategory optionCategory : optionCategoryList) {
                count += optionCategory.getMinimalOptions();
            }

            if (count == 0) {
                Cart.getInstance().addItem(mDishModel);
            } else {
                Dialog dishDialog = createDishDialog(mDishModel);
                if(dishDialog != null && !dishDialog.isShowing())
                {
                    dishDialog.show();
                }
            }
        }

    }

    private Dialog dialog;
    private ScrolListView lv_option;
    private OptionCategoryAdp optionCategoryAdp;
    private TextView tv_dishName_title;
    private ImageView img_close;
    private EditText ed_note;
    private Button btn_minus, btn_add, btn_cancle, btn_ok;
    private TextView tv_copies;

    public Dialog createDishDialog(final Dish mDishModel) {
        if(dialog != null && dialog.isShowing())
        {
            return null;
        }
        dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setContentView(R.layout.dialog_add_dish);
        Window dialogWindow = dialog.getWindow();

        WindowManager m = ((Activity) context).getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.7); // 高度设置为屏幕的0.5
        dialogWindow.setAttributes(p);
        lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);
        ed_note = (EditText) dialog.findViewById(R.id.ed_note);
        btn_minus = (Button) dialog.findViewById(R.id.btn_minus);
        btn_add = (Button) dialog.findViewById(R.id.btn_add);
        btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
        btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
        tv_copies = (TextView) dialog.findViewById(R.id.tv_copies);
        tv_dishName_title = (TextView) dialog.findViewById(R.id.tv_dishName_title);
        img_close = (ImageView) dialog.findViewById(R.id.img_close);
        if (mDishModel.quantity == 0) {
            mDishModel.quantity = 1;
            tv_copies.setText(mDishModel.quantity + "");
        } else {
            tv_copies.setText(mDishModel.quantity + "");
        }
        if (mDishModel.haveOptionCategory()) {
            optionCategoryAdp = new OptionCategoryAdp(context,mDishModel);
            optionCategoryAdp.setData(mDishModel.optionCategoryList);
            lv_option.setAdapter(optionCategoryAdp);
        }

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = --mDishModel.quantity;
                if (i == 0) {
                    mDishModel.quantity = 1;
                }
                tv_copies.setText(mDishModel.quantity + "");
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = ++mDishModel.quantity;
                if (i > mDishModel.dishCount) {
                    mDishModel.quantity = mDishModel.dishCount;
                }
                tv_copies.setText(mDishModel.quantity + "");
            }
        });
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_dishName_title.setText(mDishModel.getDishName());
        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolsUtils.writeUserOperationRecords("取消了("+mDishModel.getDishName()+")的菜品选择");
                dialog.cancel();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = ed_note.getText().toString().trim();
                if (!TextUtils.isEmpty(note)) {
                    mDishModel.comment = note;
                }
                //遍历选中的定制项
                if(!DishOptionController.checkSelectOption(mDishModel,null))
                {
                    return;
                }
                mDishModel.optionList = DishOptionController.getDishOption(mDishModel,null);
                Cart.getInstance().addItem(mDishModel);
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
