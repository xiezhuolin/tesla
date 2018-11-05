package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2017/3/21.
 */

public class CallGoodsAty extends BaseActivity {
    @BindView( R.id.tv_title )
    TextView tvTitle;
    @BindView( R.id.table_wv )
    WebView tableWv;
    @BindView( R.id.rel_back )
    RelativeLayout relBack;
    @BindView( R.id.myProgressBar )
    ProgressBar myProgressBar;

    private PosInfo posInfo;
    private String serverUrl;
    private String callGoodsUrl;
    private String store_url;//appid,brandid,storeid的组合参数
    private String callGoods_manager;//桌台管理的html路径
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_call_goods);
        ButterKnife.bind(this);
        tvTitle.setText(ToolsUtils.returnXMLStr("store_order"));
        initData();
    }

    private void initData() {
        posInfo = PosInfo.getInstance();
        serverUrl = posInfo.getServerUrl();
        callGoodsUrl = "CallOrderManage.html";
        store_url = "?appid=" + posInfo.getAppId() + "&brandid=" + posInfo.getBrandId() + "&storeid=" + posInfo.getStoreId()+"&username="+posInfo.getUsername();
        callGoods_manager = serverUrl+callGoodsUrl+store_url;

        tableWv.getSettings().setJavaScriptEnabled(true);
        tableWv.getSettings().setLoadWithOverviewMode(true);
        tableWv.getSettings().setDomStorageEnabled(true);
        tableWv.getSettings().setBuiltInZoomControls(true);
        tableWv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        tableWv.getSettings().setSupportZoom(true);

        tableWv.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageCommitVisible(WebView view, String url) {
                //                Log.e("onPageCommitVisible", url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });

        tableWv.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == myProgressBar.getVisibility()) {
                        myProgressBar.setVisibility(View.VISIBLE);
                    }
                    myProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        if (!TextUtils.isEmpty(callGoods_manager)) {
            loadCallGoodsUrl();
        }


        relBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 加载桌台界面Url
     */
    public void loadCallGoodsUrl() {
        if (!TextUtils.isEmpty(callGoods_manager) && tableWv != null) {
            tableWv.loadUrl(callGoods_manager);
        }
    }
}
