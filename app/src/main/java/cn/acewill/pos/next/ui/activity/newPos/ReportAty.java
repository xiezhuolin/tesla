package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.ui.fragment.XttReportFragment;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2017/3/21.
 */

public class ReportAty extends BaseActivity {
    @BindView( R.id.tv_title )
    TextView tvTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_set_table);
        ButterKnife.bind(this);
        tvTitle.setText(ToolsUtils.returnXMLStr("report"));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.content, new XttReportFragment()).commit();
        findViewById(R.id.rel_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToolsUtils.writeUserOperationRecords("退出报表界面");
                finish();
            }
        });
    }
}
