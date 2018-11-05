package cn.acewill.pos.next.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.adapter.BaseAdapter;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.ui.activity.PackagerAty;
import cn.acewill.pos.next.widget.ScrolListView;

/**
 * Created by DHH on 2016/6/17.
 */
public class SearchDishAdp<T> extends BaseAdapter{
    public SearchDishAdp(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final Dish dish = (Dish)getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.lv_item_order_new, null);
            holder.tv_dishName = (TextView) convertView.findViewById(R.id.tv_dishName);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
            holder.tv_option = (TextView) convertView.findViewById(R.id.tv_option);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_count.setVisibility(View.GONE);
        holder.tv_option.setVisibility(View.GONE);
        holder.tv_dishName.setText((position+1)+"."+dish.getDishName());
        String money = String.format("%.2f ", dish.getPrice());
        holder.tv_money.setText("¥ " + money);


        List<Dish> dishItem =  Cart.getDishItemList();
        for (int i = 0; i < dishItem.size(); i++) {
            Dish cartItem = dishItem.get(i);
            if(cartItem.getDishId() == dish.getDishId())
            {
                if(cartItem.quantity >0)
                {
                    holder.tv_count.setVisibility(View.VISIBLE);
                    holder.tv_count.setText("已点"+cartItem.quantity+"份");
                    holder.tv_count.setTextColor(res.getColor(R.color.red));
                }
                else
                {
                    holder.tv_count.setVisibility(View.GONE);
                }
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDish(dish);
            }
        });

        return convertView;
    }


    class ViewHolder {
        TextView tv_dishName;
        TextView tv_count;
        TextView tv_option;
        TextView tv_package;
        TextView tv_money;
    }

    public void addDish(Dish mDishModel) {
        if (mDishModel.isPackage()) {
            Intent intent = new Intent(context, PackagerAty.class);
            intent.putExtra("dish",(Serializable)mDishModel);
            context.startActivity(intent);
            return;
        }
        Cart.getInstance().addItem(mDishModel);
//        else if (!mDishModel.isOptionRequired()) {
//
//        } else {
//            createDishDialog(mDishModel).show();
//        }
        notifyDataSetChanged();
    }

    private Dialog dialog;
    private ScrolListView lv_option;
    private OptionCategoryAdp optionCategoryAdp;
    private TextView tv_dishName_title;
    private ImageView img_close;
    private EditText ed_note;
    private Button btn_minus, btn_add, btn_cancle, btn_ok;
    private TextView tv_copies;

//    public Dialog createDishDialog(final Dish mDishModel) {
//        dialog = new Dialog(context, R.style.loading_dialog);
//        dialog.setContentView(R.layout.dialog_add_dish);
//
//        Window dialogWindow = dialog.getWindow();
//        WindowManager m = ((Activity) context).getWindowManager();
//        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
//        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//        p.width = (int) (d.getWidth() * 0.7); // 高度设置为屏幕的0.5
//        dialogWindow.setAttributes(p);
//        lv_option = (ScrolListView) dialog.findViewById(R.id.lv_option);
//        ed_note = (EditText) dialog.findViewById(R.id.ed_note);
//        btn_minus = (Button) dialog.findViewById(R.id.btn_minus);
//        btn_add = (Button) dialog.findViewById(R.id.btn_add);
//        btn_cancle = (Button) dialog.findViewById(R.id.btn_cancle);
//        btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
//        tv_copies = (TextView) dialog.findViewById(R.id.tv_copies);
//        tv_dishName_title = (TextView) dialog.findViewById(R.id.tv_dishName_title);
//        img_close = (ImageView) dialog.findViewById(R.id.img_close);
//        if (mDishModel.quantity == 0) {
//            mDishModel.quantity = 1;
//            tv_copies.setText(mDishModel.quantity + "");
//        } else {
//            tv_copies.setText(mDishModel.quantity + "");
//        }
//        if (mDishModel.haveOptionCategory()) {
//            optionCategoryAdp = new OptionCategoryAdp(context);
//            optionCategoryAdp.setData(mDishModel.optionCategoryList);
//            lv_option.setAdapter(optionCategoryAdp);
//        }
//
//        btn_minus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i = --mDishModel.quantity;
//                if (i == 0) {
//                    mDishModel.quantity = 1;
//                }
//                tv_copies.setText(mDishModel.quantity + "");
//            }
//        });
//        btn_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i = ++mDishModel.quantity;
//                if (i > mDishModel.dishCount) {
//                    mDishModel.quantity = mDishModel.dishCount;
//                }
//                tv_copies.setText(mDishModel.quantity + "");
//            }
//        });
//        img_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        tv_dishName_title.setText(mDishModel.getDishName());
//        btn_cancle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//            }
//        });
//
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.cancel();
//                String note = ed_note.getText().toString().trim();
//                if (!TextUtils.isEmpty(note)) {
//                    mDishModel.comment = note;
//                }
//                //                //遍历选中的定制项
//                Map<Integer, Option> optionMap = Cart.getOptionMap();
//                if(!DishDataController.checkDishOptionCount(mDishModel,optionMap))
//                {
//                    return ;
//                }
//                Iterator iter = optionMap.keySet().iterator();
//                while (iter.hasNext()) {
//                    Object key = iter.next();
//                    Cart.optionList.add(optionMap.get(key));
//                }
//                Cart.cleanOptionMap();
//                Cart.getInstance().addItem(mDishModel);
//            }
//        });
//        return dialog;
//    }

}
