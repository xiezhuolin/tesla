package cn.acewill.pos.next.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.ui.fragment.TableFragment;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by hzc on 2017/1/9.
 */

public class SetTableActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_set_table);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content,new TableFragment()).commit();
        findViewById(R.id.rel_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolsUtils.writeUserOperationRecords("退出设置桌台界面");
                finish();
            }
        });
    }



}
