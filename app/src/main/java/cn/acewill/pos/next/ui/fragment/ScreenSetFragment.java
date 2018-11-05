package cn.acewill.pos.next.ui.fragment;

import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.config.Configure;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.OtherFile;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.StoreBusinessService;
import cn.acewill.pos.next.ui.adapter.ScreenAdapter;
import cn.acewill.pos.next.ui.presentation.SecondaryPresentation;
import cn.acewill.pos.next.widget.MyGridLayoutManager;

/**
 * 客户端屏幕设置
 * Created by aqw on 2016/8/20.
 */
public class ScreenSetFragment extends BaseFragment {

    @BindView(R.id.listFile)
    RecyclerView listFile;
    @BindView(R.id.btn_save)
    TextView btnSave;

    private StoreBusinessService storeBusinessService;
    private ScreenAdapter adapter;
    public List<OtherFile> dataList = new ArrayList<OtherFile>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen_set, container, false);
        ButterKnife.bind(this, view);
        initView();
        getFiles();
        return view;
    }

    private void initView(){
        StaggeredGridLayoutManager slManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
//        MyGridLayoutManager myGridLayoutManager = new MyGridLayoutManager(getActivity(),4);
//        myGridLayoutManager.setAutoMeasureEnabled(false);
//        listFile.setHasFixedSize(false);

        adapter = new ScreenAdapter(getActivity(),dataList);
        listFile.setLayoutManager(slManager);

        listFile.setAdapter(adapter);

        try {
            storeBusinessService = StoreBusinessService.getInstance();
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_save)
    public void onClick() {
        showToast("保存成功");
        Configure.setHashMap(adapter.getSelectId());
        initSecondaryScreen(adapter.getSelectId());
    }

    //初始化副屏幕
    private void initSecondaryScreen(HashMap<String,String> map) {
        DisplayManager dm = (DisplayManager) getContext().getSystemService(getContext().DISPLAY_SERVICE);
        if (dm != null) {
            Display[] displays = dm.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);

            for (Display display : displays) {
                if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                    SecondaryPresentation secondaryPresentation = new SecondaryPresentation(getContext(), display,map);
                    secondaryPresentation.show();
                }
            }
        }
    }

    /**
     * 获取服务器文件
     */
    private void getFiles(){
        storeBusinessService.getOtherFile(new ResultCallback<List<OtherFile>>() {
            @Override
            public void onResult(List<OtherFile> result) {
                if(result!=null&&result.size()>0){
                    List<OtherFile> itemList = new ArrayList<OtherFile>();
                    for (OtherFile otherFile : result) {
                        if(otherFile.getFiletype()==0){
                            itemList.add(otherFile);
                        }
                    }
                    dataList.clear();
                    dataList.addAll(itemList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(PosServiceException e) {
                showToast(e.getMessage());
            }
        });
    }
}
