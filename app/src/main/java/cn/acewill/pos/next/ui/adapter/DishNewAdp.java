package cn.acewill.pos.next.ui.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.model.DishWeight;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.OptionCategory;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.activity.PackagerAty;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * Created by DHH on 2016/6/17.
 */
public class DishNewAdp<T> extends BaseAdapter {
    private List<Dish> datas;
    private int widch;
    private PosInfo posInfo;
    private String logoPath;
    private GridView gv;
    private MyApplication myApplication;

    public DishNewAdp(Context context, int position, GridView gv) {
        super(context);
        this.gv = gv;
        posInfo = posInfo.getInstance();
        logoPath = posInfo.getLogoPath();
        myApplication = MyApplication.getInstance();
        float screenWidth = MyApplication.getInstance().getScreenWidth();
        widch = (int)(screenWidth / 8);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dish_new, null);
            holder.dish_bg = (LinearLayout) convertView.findViewById(R.id.dish_bg);
            holder.lin_bg = (LinearLayout) convertView.findViewById(R.id.lin_bg);
            holder.lin_money = (LinearLayout) convertView.findViewById(R.id.lin_money);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_dishMoney = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_saleOut = (TextView) convertView.findViewById(R.id.tv_saleOut);

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
        holder.tv_dishMoney.setText(money+"/"+dishUnit);

        // 绑定tag
        holder.tv_dishName.setTag(position);
        //  绑定当前的item，也就是convertview
        holder.tv_dishMoney.setTag(convertView);


        holder.lin_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //菜品是称重属性
                if(dish.isWeighted())
                {
                    DialogUtil.dishWeight(context, dish, new DialogTCallback() {
                        @Override
                        public void onConfirm(Object o) {
                            DishWeight dishWeight = (DishWeight)o;
                            if(dishWeight != null)
                            {
                                Integer dishOptionId = TimeUtil.getStringData();
                                float dishWeightNum = dishWeight.getDishWeight();
                                Dish newDish = new Dish(dish);
                                newDish.setDishWeightPrice(dishWeightNum);
                                newDish.setDishOptionId(dishOptionId);
                                Cart.getInstance().addItem(newDish);
                            }
                        }

                        @Override
                        public void onCancle() {

                        }
                    });
                }
                else{
                    addDish(dish);
                }
            }
        });

        if (dishCountList != null && dishCountList.size() > 0) {
            for (DishCount dishCount : dishCountList) {
                if (dishCount.dishid == dish.getDishId()) {
                    if (dishCount.count <= 0) {
                        holder.lin_bg.setEnabled(false);
                        holder.lin_bg.setBackgroundResource(R.drawable.border_gray1_xb);
                        holder.tv_count.setText("已估清");
                        holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.bbutton_danger_disabled_edge));
                        holder.tv_saleOut.setVisibility(View.VISIBLE);
                    } else {
                        holder.lin_bg.setEnabled(true);
                        holder.lin_bg.setBackgroundResource(R.drawable.btn_selector_gray);
                        holder.tv_count.setText("剩余:" + dishCount.count);
                        holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
//                        holder.dish_bg.setBackgroundColor(ToolsUtils.getStrColor2Int(dish.getDishKindColor()));
                        holder.lin_bg.setBackgroundColor(ToolsUtils.getStrColor2Int(dish.getDishKindColor()));
                        holder.tv_saleOut.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        } else {
            if (dish.dishCount <= 0) {
                holder.lin_bg.setEnabled(false);
                holder.lin_bg.setBackgroundResource(R.drawable.border_gray1_xb);
                holder.tv_count.setText("已估清");
                holder.tv_saleOut.setVisibility(View.VISIBLE);
                holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.bbutton_danger_disabled_edge));
            } else {
                holder.lin_bg.setEnabled(true);
                holder.lin_bg.setBackgroundResource(R.drawable.btn_selector_gray);
                holder.tv_count.setText("剩余:" + dish.dishCount);
                holder.tv_saleOut.setVisibility(View.GONE);
                holder.tv_count.setTextColor(ContextCompat.getColor(context, R.color.login_gray));
//                holder.dish_bg.setBackgroundColor(ToolsUtils.getStrColor2Int(dish.getDishKindColor()));
                holder.lin_bg.setBackgroundColor(ToolsUtils.getStrColor2Int(dish.getDishKindColor()));
            }
        }
        return convertView;
    }

    private List<DishCount> dishCountList = new ArrayList<>();

    public void setDishCount(List<DishCount> dishCountList) {
        if (dishCountList != null && dishCountList.size() > 0) {
            this.dishCountList = dishCountList;
            this.notifyDataSetChanged();
        }
    }


    class ViewHolder {
        LinearLayout dish_bg;
        LinearLayout lin_bg;
        LinearLayout lin_money;
        TextView tv_dishName;
        TextView tv_dishMoney;
        TextView tv_count;
        TextView tv_saleOut;
    }

    public void addDish(Dish mDishModel) {
        ToolsUtils.writeUserOperationRecords("点击了(" + mDishModel.getDishName() + ")菜品");
        Integer dishOptionId = TimeUtil.getStringData();
        mDishModel.setDishOptionId(dishOptionId);
        if (mDishModel.isPackage()) {
            if(mDishModel.isShowPackageItemsFlag())
            {
                Intent intent = new Intent(context, PackagerAty.class);
                intent.putExtra("dish",(Serializable)mDishModel);
                context.startActivity(intent);
                return;
            }
            else{
                Cart.getInstance().addItem(mDishModel);
            }
        }
        else if (!mDishModel.isOptionRequired()) {
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
                //遍历选中的定制项
                Cart.getInstance().addItem(mDishModel);
                dialog.cancel();
            }
        });
        return dialog;
    }
}
