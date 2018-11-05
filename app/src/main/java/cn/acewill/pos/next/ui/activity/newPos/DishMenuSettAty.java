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
import cn.acewill.pos.next.ui.fragment.AboutFragment;
import cn.acewill.pos.next.ui.fragment.MyStoreFragment;
import cn.acewill.pos.next.ui.fragment.PrintKDSFragment;
import cn.acewill.pos.next.ui.fragment.ScanFragment;
import cn.acewill.pos.next.ui.fragment.SeniorFragment;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.ComTextView;

/**
 * 关于POS
 * Created by aqw on 2016/12/8.
 */
public class DishMenuSettAty extends BaseActivity {
    @BindView( R.id.back_ll )
    LinearLayout backLl;
    @BindView( R.id.about_set )
    ComTextView aboutSet;
    @BindView( R.id.my_store )
    ComTextView myStore;
//    @BindView( R.id.table_set )
//    ComTextView tableSet;
    @BindView( R.id.print_kds )
    ComTextView printKds;
    @BindView( R.id.senior_set )
    ComTextView seniorSet;
    @BindView( R.id.scan_set )
    ComTextView scanSet;
//    @BindView( R.id.help_set )
//    ComTextView helpSet;

    private AboutFragment aboutFragment;
    private MyStoreFragment myStoreFragment;
//    private TableFragment tableFragment;
    private PrintKDSFragment printKDSFragment;
    private SeniorFragment seniorFragment;
    private ScanFragment scanFragment;
//    private HelpFragment helpFragment;

    private ArrayList<ComTextView> comTextViewList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_dish_menu_set);
        myApplication.addPage(DishMenuSettAty.this);
        //初始化 BufferKnife
        ButterKnife.bind(this);
//        changeTab(0);
    }

    private void changeTab(int tab) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        comTextViewList.add(aboutSet);
        comTextViewList.add(myStore);
//        comTextViewList.add(tableSet);
        comTextViewList.add(printKds);
        comTextViewList.add(seniorSet);
        comTextViewList.add(scanSet);
//        comTextViewList.add(helpSet);
        switch (tab) {
            case 0:
                setSelect(0);
                if (aboutFragment == null) {
                    aboutFragment = new AboutFragment();
                }
                ft.replace(R.id.set_content, aboutFragment);
                break;
            case 1:
                setSelect(1);
                if (myStoreFragment == null) {
                    myStoreFragment = new MyStoreFragment();
                }
                ft.replace(R.id.set_content, myStoreFragment);
                break;
//            case 2:
//                setSelect(2);
//                if (tableFragment == null) {
//                    tableFragment = new TableFragment();
//                }
//                ft.replace(R.id.set_content, tableFragment);
//                break;
            case 2:
                setSelect(2);
                if (printKDSFragment == null) {
                    printKDSFragment = new PrintKDSFragment();
                }
                ft.replace(R.id.set_content, printKDSFragment);
                break;
            case 3:
                setSelect(3);
                if (seniorFragment == null) {
                    seniorFragment = new SeniorFragment();
                }
                ft.replace(R.id.set_content, seniorFragment);
                break;
            case 4:
                setSelect(4);
                if (scanFragment == null) {
                    scanFragment = new ScanFragment();
                }
                ft.replace(R.id.set_content, scanFragment);
                break;
//            case 6:
//                setSelect(6);
//                if (helpFragment == null) {
//                    helpFragment = new HelpFragment();
//                }
//                ft.replace(R.id.set_content, helpFragment);
//                break;
        }
        ft.commit();
    }

    private int tempIndex = 0;

    private void setViewState(int selectIndex) {
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
            case 4:
                setViewState(4);
                break;
//            case 5:
//                setViewState(5);
//                break;
//            case 6:
//                setViewState(6);
//                break;
        }
    }

    @OnClick( {R.id.back_ll, R.id.about_set, R.id.my_store, R.id.print_kds, R.id.senior_set, R.id.scan_set/*, R.id.table_set,R.id.help_set*/} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ll:
                ToolsUtils.writeUserOperationRecords("退出更多界面");
                finish();
                break;
            case R.id.about_set:
                ToolsUtils.writeUserOperationRecords("切换至关于云POS界面");
                changeTab(0);
                break;
            case R.id.my_store:
                ToolsUtils.writeUserOperationRecords("切换至我的餐厅界面");
                changeTab(1);
                break;
//            case R.id.table_set:
//                ToolsUtils.writeUserOperationRecords("切换至桌台界面");
//                changeTab(2);
//                break;
            case R.id.print_kds:
                ToolsUtils.writeUserOperationRecords("切换至打印机KDS界面");
                changeTab(2);
                break;
            case R.id.senior_set:
                ToolsUtils.writeUserOperationRecords("切换至高级设置界面");
                changeTab(3);
                break;
            case R.id.scan_set:
                ToolsUtils.writeUserOperationRecords("切换至正反扫界面");
                changeTab(4);
                break;
//            case R.id.help_set:
//                ToolsUtils.writeUserOperationRecords("切换至帮助界面");
//                changeTab(6);
//                break;
        }
    }
}
