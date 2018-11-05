package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;

import butterknife.ButterKnife;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 消息
 * Created by DHH on 2016/6/12.
 */
public class MessAgeAty extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_message);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        myApplication.addPage(MessAgeAty.this);
        setTitle(ToolsUtils.returnXMLStr("message"));
        setShowBtnBack(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
