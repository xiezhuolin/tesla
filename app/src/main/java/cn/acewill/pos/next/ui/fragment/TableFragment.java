package cn.acewill.pos.next.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogEtCallback;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.table.JsTable;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.TableService;
import cn.acewill.pos.next.ui.DialogListView;
import cn.acewill.pos.next.ui.activity.newPos.TableOrderNewAty;
import cn.acewill.pos.next.ui.adapter.TableOrderAdp;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ProgressDialogF;
import cn.acewill.pos.next.widget.ScrolGridView;

/**
 * Created by DHH on 2016/6/14.
 */
public class TableFragment extends BaseFragment implements DialogListView {
    @BindView( R.id.table_wv )
    WebView tableWv;
    @BindView( R.id.myProgressBar )
    ProgressBar myProgressBar;

    private String serverUrl;
    private String table_pie;//桌台html路径
    private String jessionid;
    private String store_url;//appid,brandid,storeid的组合参数
    private String table_manager;//桌台管理的html路径
    private Handler handler = null;

    private ProgressDialogF progressDialog;
    private ProgressDialog progdialog;

    @Nullable
    @Override
    @SuppressLint( "SetJavaScriptEnabled" )
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_table_manager, container, false);
        ButterKnife.bind(this, view);
        initData();
        crateOrderDialog();
        return view;
    }


    private void initData() {
        serverUrl = PosInfo.getInstance().getServerUrl();
        //        serverUrl = "http://192.168.1.149:8080/";
        //        table_pie = "html/temporary.html";
        table_pie = "html/tablesAPP.html";
        store_url = "?appid=" + PosInfo.getInstance().getAppId() + "&brandid=" + PosInfo.getInstance().getBrandId() + "&storeid=" + PosInfo.getInstance().getStoreId();
        table_manager = serverUrl + table_pie + store_url;
        progressDialog = new ProgressDialogF(aty);
        //创建属于主线程的handler
        handler = new Handler();
        //        tableWv.getSettings().setLoadWithOverviewMode(true);
        //        tableWv.getSettings().setUseWideViewPort(true);
        //        tableWv.getSettings().setSupportZoom(false);
        //        tableWv.getSettings().setBuiltInZoomControls(true);
        tableWv.getSettings().setJavaScriptEnabled(true);
        tableWv.getSettings().setLoadWithOverviewMode(true);
        tableWv.getSettings().setDomStorageEnabled(true);
        tableWv.getSettings().setBuiltInZoomControls(true);
        tableWv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        tableWv.getSettings().setSupportZoom(true);
        tableWv.setWebChromeClient(new WebChromeClient());
        tableWv.addJavascriptInterface(new DemoJavaScriptInterface(), "table");

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
                progressDialog.disLoading();
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

        if (!TextUtils.isEmpty(table_manager)) {
            loadTableUrl();
        }
    }

    /**
     * 加载桌台界面Url
     */
    public void loadTableUrl() {
        if (!TextUtils.isEmpty(table_manager) && tableWv != null) {
            System.out.println(table_manager);
            tableWv.loadUrl(table_manager);
        }
    }

    /**
     * 开台
     *
     * @param tableId
     * @param numberOfCustomer
     */
    private void openTable(final long tableId, final int numberOfCustomer) {
        try {
            TableService tableService = TableService.getInstance();
            tableService.openTable(tableId, numberOfCustomer, new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        Integer peopleNums = Integer.valueOf(String.valueOf(numberOfCustomer));
                        Order order = new Order();
                        order.addTableIds(tableId);
                        order.setCustomerAmount(peopleNums);
                        setTableInfo(tableId, order);
                        //                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, tableId));//跳转到下单界面
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("开台失败," + e.getMessage() + "!");
                }
            });
        } catch (PosServiceException e) {
        }
    }

    /**
     * 加台
     *
     * @param oldTableId
     * @param newTableId
     * @param numberOfCustomer
     */
    private void addTable(final long oldTableId, final long newTableId, final Long numberOfCustomer) {
        try {
            TableService tableService = TableService.getInstance();
            tableService.addTable(oldTableId, newTableId, numberOfCustomer, new ResultCallback<List<Order>>() {
                @Override
                public void onResult(List<Order> result) {
                    if (result != null && result.size() > 0) {
                        Order order = result.get(0);
                        if (order != null && order.getId() > 0) {
                            System.out.println(ToolsUtils.getPrinterSth(order) + "<<===加台加菜");
                            order.addTableIds(oldTableId, newTableId);
                            order.setTableId(newTableId);
                            order.setCustomerAmount(Integer.valueOf(String.valueOf(numberOfCustomer)));
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, order));//跳转到下单界面
                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast("加台失败," + e.getMessage() + "!");
                }
            });
        } catch (PosServiceException e) {
        }
    }

    private void setTableInfo(final long tableId, final Order order) {
        Intent tableIntent = new Intent(aty, TableOrderNewAty.class);
        tableIntent.putExtra("tableId", tableId);
        Bundle tableBun = new Bundle();
        order.setTableNames(tableName);
        tableBun.putSerializable("tableOrder", order);
        tableIntent.putExtras(tableBun);
        startActivity(tableIntent);
    }



    /**
     * 获取某个桌台上的订单
     *
     * @param tableId
     */
    private void ordersTable(final int type, final long tableId, final long oldTableId) {
        try {
            showProgress();
            TableService tableService = TableService.getInstance();
            tableService.ordersTable(tableId, new ResultCallback<List<Order>>() {
                @Override
                public void onResult(List<Order> result) {
                    dissmiss();
                    if (result != null) {
                        if (result.size() == 0) {
                            Order order = new Order();
                            order.setTableId(tableId);
                            order.setTableNames(tableName);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, order));//跳转到下单界面
                        } else {
                            if (result.size() == 1) {
                                tableOrder = result.get(0);
                                if (tableOrder != null) {
                                    if (type == Constant.JsToAndroid.JS_A_ADDDISH) {
                                        tableOrder.setTableId(tableId);
                                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, tableOrder));//跳转到下单界面
                                    } else if (type == Constant.JsToAndroid.JS_A_CHECK_TABLE) {
                                        tableOrder.setTableId(tableId);
                                        EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, tableOrder));//跳转到下单界面
                                    } else if (type == Constant.JsToAndroid.JS_A_RETREATDISH) {
                                        //  EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_RETREAT, tableOrder));//跳转到退菜界面
                                    } else if (type == Constant.JsToAndroid.JS_A_CHECKOUT) {
                                        //  EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_CHECK_OUT, tableOrder));//跳转到结账界面
                                    }
                                } else {
                                    Order order = new Order();
                                    order.setTableId(tableId);
                                    order.setTableStyle(Constant.JsToAndroid.JS_A_SPLITTABLE);
                                    EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, order));//跳转到下单界面
                                }
                            } else {
                                dialog_flag = type;//复制给全局flag标识
                                tableOrderAdp.setData(result);
                                selectTableOrderIndex = -1;
                                tableOrderAdp.setPosition(selectTableOrderIndex);
                                dialog_title.setText("选择桌台号( " + tableName + " )订单");
                                dialog.show();
                            }
                        }
                    } else {
                        showToast("获取桌台订单信息失败!");
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    dissmiss();
                    showToast("获取桌台订单信息失败," + e.getMessage() + "!");
                }
            });
        } catch (PosServiceException e) {
            dissmiss();
            e.printStackTrace();
        }
    }


    private Dialog dialog;
    private TextView dialog_title;
    private ScrolGridView gv_definition;
    private LinearLayout lin_select_work;
    private TextView negativeButton, positiveButton;
    private TableOrderAdp tableOrderAdp;
    private Order tableOrder;
    private int dialog_flag = -1;
    private long tableId;
    private long oldTableId;
    private String tableName;
    private int selectTableOrderIndex = -1;//

    private Dialog crateOrderDialog() {
        dialog = DialogUtil.getDialogShow(aty, R.layout.dialog_work_shift, 0.5f, 0.6f, false, false);
        dialog_title = (TextView) dialog.findViewById(R.id.print_title);
        TextView message = (TextView) dialog.findViewById(R.id.message);
        lin_select_work = (LinearLayout) dialog.findViewById(R.id.lin_select_work);
        gv_definition = (ScrolGridView) dialog.findViewById(R.id.gv_definition);
        LinearLayout print_close_ll = (LinearLayout) dialog.findViewById(R.id.print_close_ll);
        LinearLayout lin_money = (LinearLayout) dialog.findViewById(R.id.lin_money);
        negativeButton = (TextView) dialog.findViewById(R.id.print_ok);
        positiveButton = (TextView) dialog.findViewById(R.id.print_cancle);
        message.setText("桌台订单号");
        dialog_title.setText("选择(" + tableName + ")桌台号订单");
        lin_money.setVisibility(View.GONE);
        tableOrderAdp = new TableOrderAdp(aty);
        gv_definition.setAdapter(tableOrderAdp);
        print_close_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /**
         * 取消
         */
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /**
         * 确定
         */
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (selectTableOrderIndex != -1) {
                    if (tableOrder != null) {
                        if (dialog_flag == Constant.JsToAndroid.JS_A_ADDDISH) {
                            tableOrder.setTableId(tableId);
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, tableOrder));//跳转到下单界面
                        } else if (dialog_flag == Constant.JsToAndroid.JS_A_RETREATDISH) {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_RETREAT, tableOrder));//跳转到退菜界面
                        } else if (dialog_flag == Constant.JsToAndroid.JS_A_CHECKOUT) {
                            EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_CHECK_OUT, tableOrder));//跳转到结账界面
                        } else if (dialog_flag == Constant.JsToAndroid.JS_A_ADDTABLE) {
                            DialogUtil.inputDialog(aty, "加台", "加台人数", "请输入加台人数", 0, false,false, new DialogEtCallback() {
                                @Override
                                public void onConfirm(String sth) {
                                    Long numberOfCustomer = Long.valueOf(sth);//人数
                                    addTable(oldTableId, tableId, numberOfCustomer);//开台操作
                                }

                                @Override
                                public void onCancle() {

                                }
                            });
                        } else {
                            setTableInfo(tableId, tableOrder);
                        }

                    }
                } else {
                    showToast("请选择一个桌台订单!");
                }

            }
        });
        gv_definition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTableOrderIndex = position;
                Order order = (Order) tableOrderAdp.getItem(position);
                tableOrderAdp.setPosition(selectTableOrderIndex);
                if (order != null) {
                    tableOrder = order;
                }
            }
        });
        return dialog;
    }


    /**
     * 开台操作
     *
     * @param jstable
     */
    private void openTable(final JsTable jstable) {
        DialogUtil.inputDialog(aty, "开台", "开台人数", "请输入开台人数", 0, false, false,new DialogEtCallback() {
            @Override
            public void onConfirm(String sth) {
                Integer numberOfCustomer = Integer.valueOf(sth);//人数
                openTable(jstable.tableId, numberOfCustomer);//开台操作
            }

            @Override
            public void onCancle() {

            }
        });
    }

    /**
     * 加台操作
     *
     * @param jstable
     */
    private void addTable(final JsTable jstable) {
        DialogUtil.inputDialog(aty, "加台", "加台人数", "请输入加台人数", 0, false, false,new DialogEtCallback() {
            @Override
            public void onConfirm(String sth) {
                Long numberOfCustomer = Long.valueOf(sth);//人数
                addTable(jstable.oldTableId, jstable.tableId, numberOfCustomer);//开台操作
            }

            @Override
            public void onCancle() {

            }
        });
    }

    /**
     * 搭台操作
     *
     * @param jstable
     */
    private void splitTable(final JsTable jstable) {
        DialogUtil.inputDialog(aty, "搭台", "搭台人数", "请输入搭台人数", 0, false, false,new DialogEtCallback() {
            @Override
            public void onConfirm(String sth) {
                Order order = new Order();
                order.setCustomerAmount(Integer.valueOf(sth));//人数
                order.setTableId(jstable.tableId);
                order.setTableStyle(Constant.JsToAndroid.JS_A_SPLITTABLE);
                order.setItemList(null);
                order.addTableIds(jstable.tableId);
                order.setCustomerAmount(Integer.valueOf(sth));
                EventBus.getDefault().post(new PosEvent(Constant.EventState.SELECT_FRAGMTNT_ORDER, order));//跳转到下单界面
            }

            @Override
            public void onCancle() {

            }
        });
    }

    @Override
    public void showDialog() {
        progressDialog.showLoading("");
    }

    @Override
    public void dissDialog() {
        progressDialog.disLoading();
    }

    @Override
    public void showError(PosServiceException e) {
        showToast(e.getMessage());
        Log.i("返回有误", e.getMessage());
    }

    @Override
    public <T> void callBackData(List<T> dataList) {

    }


    /**
     * 接收js回调值
     */
    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface() {

        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * loadUrl on the UI thread.
         */
        //js向android发送消息时的事件接收方法
        @JavascriptInterface
        public void jsToAndroid(int flag, String jsonStr) {/*
            System.out.println(flag + "====" + jsonStr);
            if (flag != 0) {
                Gson gson = new Gson();
                JsTable jstable = null;
                try {
                    jstable = gson.fromJson(jsonStr, JsTable.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    showToast("无效桌台!");
                }
                if (jstable != null) {
                    if (!TextUtils.isEmpty(jstable.tableName)) {
                        tableName = jstable.tableName;
                    }
                    if (jstable.tableId > 0) {
                        final JsTable finalJstable = jstable;
                        if (flag == Constant.JsToAndroid.JS_A_OPENTABLE) {
                            openTable(finalJstable);
                        } else if (flag == Constant.JsToAndroid.JS_A_ADDTABLE) {
                            addTable(finalJstable);
                        } else if (flag == Constant.JsToAndroid.JS_A_SPLITTABLE) {
                            splitTable(finalJstable);
                        } else {
                            tableId = jstable.tableId;
                            oldTableId = jstable.oldTableId;
                            ordersTable(flag, jstable.tableId, jstable.oldTableId);
                        }
                    }
                }
            }
       */ }
    }

}
