package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.ui.fragment.OtherFragment;
import cn.acewill.pos.next.ui.fragment.PrintFragment;
import cn.acewill.pos.next.ui.fragment.PrintListFragment;
import cn.acewill.pos.next.ui.fragment.ScanFragment;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * 关于POS
 * Created by aqw on 2016/12/8.
 */
public class AboutAty extends BaseActivity {

    @BindView( R.id.back_ll )
    LinearLayout backLl;
    @BindView( R.id.scan_set )
    ComTextView scanSet;
    @BindView( R.id.sale_set )
    ComTextView saleSet;
    @BindView( R.id.up_print )
    ComTextView upPrint;
    @BindView( R.id.print_list )
    ComTextView printList;

    private ScanFragment scanFragment;
    private OtherFragment otherFragment;
    private PrintFragment printFragment;
    private PrintListFragment printListFragment;

    private ArrayList<ComTextView> comTextViewList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_about);
        myApplication.addPage(AboutAty.this);
        //初始化 BufferKnife
        ButterKnife.bind(this);
        changeTab(0);
    }

    private void changeTab(int tab) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        comTextViewList.add(scanSet);
        comTextViewList.add(saleSet);
        comTextViewList.add(upPrint);
        comTextViewList.add(printList);

        switch (tab) {
            case 0:
                setSelect(0);
                if (scanFragment == null) {
                    scanFragment = new ScanFragment();
                }
                ft.replace(R.id.set_content, scanFragment);
                break;
            case 1:
                setSelect(1);
                if (otherFragment == null) {
                    otherFragment = new OtherFragment();
                }
                ft.replace(R.id.set_content, otherFragment);
                break;
            case 2:
                setSelect(2);
                if (printFragment == null) {
                    printFragment = new PrintFragment();
                }
                ft.replace(R.id.set_content, printFragment);
                break;
            case 3:
                setSelect(3);
                if (printListFragment == null) {
                    printListFragment = new PrintListFragment();
                }
                ft.replace(R.id.set_content, printListFragment);
                break;
        }
        ft.commit();
    }

    private int tempIndex = 0;
    private void setViewState(int selectIndex)
    {
        int size = comTextViewList.size();
        for (int i = 0; i < size; i++) {
            comTextViewList.get(tempIndex).setBackgroundColor(resources.getColor(R.color.white));
            comTextViewList.get(selectIndex).setBackgroundColor(resources.getColor(R.color.bbutton_info));
            tempIndex = selectIndex;
        }
    }

    private void setSelect(int position) {
        switch (position) {
            case 0:
                setViewState(0);
                break;
            case 1:
                setViewState(1);
                break;
            case 2:
                setViewState(2);
                break;
            case 3:
                setViewState(3);
                break;
        }
    }

    @OnClick( {R.id.back_ll, R.id.scan_set, R.id.sale_set, R.id.up_print,R.id.print_list} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ll:
                ToolsUtils.writeUserOperationRecords("退出更多界面");
                finish();
                break;
            case R.id.scan_set://正反扫设置
                ToolsUtils.writeUserOperationRecords("切换至正反扫设置界面");
                changeTab(0);
                break;
            case R.id.sale_set://配送费设置
                ToolsUtils.writeUserOperationRecords("切换至配送费设置界面");
                changeTab(1);
                break;
            case R.id.up_print://标签打印机设置
                ToolsUtils.writeUserOperationRecords("切换至标签打印机设置界面");
                changeTab(2);
                break;
            case R.id.print_list://获取打印机列表
                ToolsUtils.writeUserOperationRecords("切换至打印机列表界面");
                changeTab(3);
                break;
        }
    }
}
