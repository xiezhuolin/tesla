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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.OptionCategory;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.ui.activity.PackagerAty;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * Created by DHH on 2016/6/17.
 */
public class DishMenuAdp<T> extends BaseAdapter {
    private ImageLoader imageLoader;
    private int widch;
    private int heigh;
    private int screenWidth;//屏幕的宽
    private int screenHeigh;//屏幕的高
    private int showPointType;//显示的模式
    private PosInfo posInfo;
    private String logoPath;

    public DishMenuAdp(Context context) {
        super(context);
        imageLoader = MyApplication.getInstance().initImageLoader(context);
        screenWidth = MyApplication.getInstance().getScreenWidth();
        screenHeigh = MyApplication.getInstance().getScreenHeight();
        posInfo = posInfo.getInstance();
        logoPath = posInfo.getLogoPath();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final DishType dishKind = (DishType)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_dishmenu, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_dishMoney = (TextView) convertView.findViewById(R.id.tv_money);
            holder.iv_dishImagePath = (ImageView) convertView.findViewById(R.id.iv_dishImagePath);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(showPointType == 1 || showPointType == 2)
        {
            heigh = screenHeigh/3;
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heigh);
            convertView.setLayoutParams(params);
        }
        else if(showPointType == 3)
        {
            heigh = screenHeigh/2;
            widch = screenWidth/3;
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(widch, heigh);
            convertView.setLayoutParams(params);
        }
        else if(showPointType == 4)
        {
            heigh = screenHeigh/2;
            widch = screenWidth/4;
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(widch, heigh);
            convertView.setLayoutParams(params);
        }

        holder.tv_dishName.setText(dishKind.getName());

        String image = getKindbgImage(position);
        if (!TextUtils.isEmpty(image)) {
            imageLoader.displayImage(image, holder.iv_dishImagePath);
        } else {
            if (!TextUtils.isEmpty(logoPath)) {
                imageLoader.displayImage(logoPath, holder.iv_dishImagePath);
            } else {
                imageLoader.displayImage(image, holder.iv_dishImagePath);
            }
        }
        return convertView;
    }

    private String getKindbgImage(int position)
    {
        String bgImage = "";
        List<Dish> datas = DishDataController.getdishsForKind(position);
        if(datas != null && datas.size() >0)
        {
            Dish dish = datas.get(0);
            if(dish != null && !TextUtils.isEmpty(dish.getImageName()))
            {
                bgImage = dish.getImageName();
            }
        }
        return bgImage;
    }


    public void setShowType(int showPointType) {
        this.showPointType = showPointType;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView tv_dishName;
        TextView tv_dishMoney;
        ImageView iv_dishImagePath;
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
                createDishDialog(mDishModel).show();
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
