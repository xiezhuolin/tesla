package cn.acewill.pos.next.base.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import cn.acewill.pos.R;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.widget.CatLoadingView;

/**
 * BaseFragment
 * Created by DHH on 2016/1/28.
 */
public  class BaseFragment extends Fragment {
    public Context mContext;
    public Activity aty;
    public Resources resources;
    public MyApplication myApplication;
    protected boolean isVisible;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.resources = getResources();
        this.aty = getActivity();
        this.myApplication  = MyApplication.getInstance();
    }

    public String getStringById(int resId){
        String str = "";
        if (resources != null){
           str = resources.getString(resId);
        }
        return str;
    }

    public void showToast(String sth)
    {
        MyApplication.getInstance().ShowToast(sth);
    }

    CatLoadingView mView;
    public void showProgress()
    {
        try {
            mView = new CatLoadingView();
            mView.show(getActivity().getSupportFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showProgress(String str)
    {
        try {
            mView = new CatLoadingView();
            if(!TextUtils.isEmpty(str))
            {
                mView.setText(str);
            }
            mView.show(getActivity().getSupportFragmentManager(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dissmiss() {
        try {
            if(mView != null)
            {
                mView.setText(resources.getString(R.string.sth_loading));
                mView.dismiss();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


}
