package cn.acewill.pos.next.ui.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.OtherFile;
import cn.acewill.pos.next.utils.ScreenUtil;

/**
 * Created by Administrator on 2016/9/3.
 */
public class ScreenAdapter extends RecyclerView.Adapter {

    public Context context;
    public List<OtherFile> dataList;
    public LayoutInflater inflater;
    private ImageLoader imageLoader;
    private List<Integer> selectItems = new ArrayList<Integer>();
    private List<String> ids;

    public ScreenAdapter(Context context, List<OtherFile> dataList) {
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
        imageLoader = MyApplication.getInstance().initImageLoader(context);

        //获取之前选中的图片id
        String[] ss = Store.getInstance(context).getSelectIds().split(",");
        ids = Arrays.asList(ss);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_screen_file, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtil.getScreenSize(context)[0]/5);
        params.setMargins(5,5,5,5);
        view.setLayoutParams(params);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        OtherFile otherFile = dataList.get(position);

        selectItems.add(position, 0);

        itemViewHolder.title.setText(otherFile.getFiletypeStr());

        if (otherFile.getFiletype() == 0) {
            imageLoader.displayImage(otherFile.getFilename(), itemViewHolder.img_src);
        }

        if(ids.contains(otherFile.getFileid())){
            itemViewHolder.ad_rl.setSelected(true);
        }else {
            itemViewHolder.ad_rl.setSelected(false);
        }


        //勾选图片
        itemViewHolder.ad_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemViewHolder.ad_rl.isSelected()) {
                    itemViewHolder.ad_rl.setSelected(false);
                    selectItems.set(position, 0);
                } else {
                    itemViewHolder.ad_rl.setSelected(true);
                    selectItems.set(position, 1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_src;
        private TextView title;
        private RelativeLayout ad_rl;

        public ItemViewHolder(View itemView) {
            super(itemView);

            img_src = (ImageView) itemView.findViewById(R.id.img_src);
            title = (TextView) itemView.findViewById(R.id.file_title);
            ad_rl = (RelativeLayout) itemView.findViewById(R.id.ad_rl);
        }
    }

    //获取选中的图片id
    public HashMap<String,String> getSelectId() {
        HashMap<String,String> url_maps = new HashMap<>();

        StringBuffer ids = new StringBuffer();
        for (int i=0;i< selectItems.size();i++) {
            if (selectItems.get(i)==1) {
                ids.append(dataList.get(i).getFileid()+",");
                url_maps.put(dataList.get(i).getFiletypeStr()+i,dataList.get(i).getFilename());
            }
        }

        //保存选中的id
        Store.getInstance(context).setSelectIds(ids.toString());

        return url_maps;
    }
}
