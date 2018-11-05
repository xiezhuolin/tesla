package cn.acewill.pos.next.ui.activity.newPos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.RefrushLisener;
import cn.acewill.pos.next.model.StandByCash;
import cn.acewill.pos.next.model.user.Staff;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.ui.adapter.StandByAdapter;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.utils.ViewUtil;
import cn.acewill.pos.next.widget.CommonEditText;
import cn.acewill.pos.next.widget.OnRecyclerItemClickListener;

/**
 * 备用金
 * Created by DHH on 2016/6/12.
 */
public class StandByCashAty extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, RefrushLisener {
    @BindView( R.id.order_lv )
    RecyclerView orderLv;
    @BindView( R.id.order_srl )
    SwipeRefreshLayout orderSrl;
    @BindView( R.id.search_cotent )
    CommonEditText edMemberNumber;
    @BindView( R.id.search_clear )
    LinearLayout searchClear;

    private int page = 0;
    private int limit = 11;

    private StandByAdapter adapter;
    private List<StandByCash> standByCashList = new ArrayList<StandByCash>();
    private int lastVisibleItem = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_standby_cash);
        ButterKnife.bind(this);
        initView();
        ViewUtil.setActivityWindow(context, 8, 8);
    }

    private void loadData() {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        page = 0;
        getStandByCashList();
    }

    private void initView() {
        myApplication.addPage(StandByCashAty.this);
//        setTitle("备用金使用记录");
//        setShowBtnBack(true);
//        setRightImage(R.mipmap.img_base_right_icon);
        orderSrl.setOnRefreshListener(this);
        orderSrl.setColorSchemeResources(R.color.green, R.color.blue, R.color.black);
        adapter = new StandByAdapter(context, this);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        orderLv.setLayoutManager(linearLayoutManager);

        orderLv.setAdapter(adapter);
        orderLv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem + 1 == adapter.getItemCount() && adapter.load_more_status == adapter.LOAD_MORE && dy > 0) {
                    adapter.setLoadType(adapter.UP_LOAD_TYPE);
                    adapter.changeMoreStatus(adapter.LOADING);

                    adapter.changeMoreStatus(adapter.NO_MORE);
                    orderSrl.setRefreshing(false);
                }
            }
        });

        orderLv.addOnItemTouchListener(new OnRecyclerItemClickListener(orderLv) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                StandByCash standByCash = (StandByCash) adapter.getItem(position);
                if (standByCash != null) {
                    Intent intent = new Intent();
                    ToolsUtils.writeUserOperationRecords("跳转到新建备用金界面");
                    intent.setClass(StandByCashAty.this, CreateStandByCashAty.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("StandByCash", standByCash);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLOngClick(RecyclerView.ViewHolder viewHolder) {

            }
        });

        //edtext的控件内容监听
        edMemberNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchClear.setVisibility(View.VISIBLE);
                    List<Staff> staffs = null;
//                    staffs = getUserListForId(userList,s.toString().trim());
//                    adapter.setData(staffs);
                } else {
                    searchClear.setVisibility(View.GONE);
                    adapter.setData(standByCashList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }


    //获取第一页数据
    private void initData() {
        adapter.setLoadType(adapter.DOWN_LOAD_TYPE);
        page = 0;
        getStandByCashList();
    }


    @OnClick( {R.id.search_clear, R.id.tv_login} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                startActivity(new Intent(this, CreateStandByCashAty.class));
                break;
            case R.id.search_clear:
                edMemberNumber.setText("");
                adapter.setData(standByCashList);
                break;
        }
    }

    @Override
    public void onRefresh() {
        initData();
    }

    @Override
    public void refrush() {
        initData();
    }

    private void getStandByCashList() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.getStandByCash(new ResultCallback<List<StandByCash>>() {
                @Override
                public void onResult(List<StandByCash> result) {
                    if (result != null) {
                        standByCashList = result;
                        adapter.setData(result);

                        if (result != null && result.size() > 0) {
                            if (result.size() < limit) {
                                adapter.changeMoreStatus(adapter.NO_MORE);
                            } else {
                                adapter.changeMoreStatus(adapter.LOAD_MORE);
                            }
                        } else {
                            adapter.changeMoreStatus(adapter.NO_MORE);
                        }
                        orderSrl.setRefreshing(false);
                    }
                    else{
                        showToast(ToolsUtils.returnXMLStr("standby_cash_recording_is_null"));
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取备用金使用记录失败,", e.getMessage());
                    showToast(ToolsUtils.returnXMLStr("get_standby_cash_use_recording_is_failure")+"," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


}
