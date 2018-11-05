package cn.acewill.pos.next.ui.activity.newPos;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.interfices.DialogTCallback;
import cn.acewill.pos.next.model.user.Staff;
import cn.acewill.pos.next.model.user.StaffStatus;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.ToolsUtils;
import cn.acewill.pos.next.widget.CommonEditText;

/**
 * 员工档案
 * Created by DHH on 2016/6/12.
 */
public class StaffInfoAty extends BaseActivity {
    @BindView( R.id.tv_nickName )
    CommonEditText tvNickName;
    @BindView( R.id.tv_userName )
    CommonEditText tvUserName;
    @BindView( R.id.tv_phoneNumber )
    CommonEditText tvPhoneNumber;
    @BindView( R.id.tv_discount )
    CommonEditText tvDiscount;
    @BindView( R.id.tv_quartersName )
    TextView tvQuartersName;
    @BindView( R.id.lin_bottom )
    LinearLayout linBottom;
    @BindView( R.id.ck_user_status )
    CheckBox ckUserStatus;

    private Staff staff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentXml(R.layout.aty_staff_info);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    private void loadData() {
    }

    private void initView() {
        myApplication.addPage(StaffInfoAty.this);
        setTitle(ToolsUtils.returnXMLStr("staff_list"));
        setShowBtnBack(true);
        setRightText("保存");
        staff = (Staff) getIntent().getSerializableExtra("staff");
        if (staff != null) {
            linBottom.setVisibility(View.VISIBLE);
            tvUserName.setText(staff.getUsername());
            tvQuartersName.setText(staff.getQuartersName());
            tvDiscount.setText(staff.getDiscount() + "/折");
            quartersid = staff.getQuartersid();
            //禁用
            if(staff.getStatus() == StaffStatus.DISABLED)
            {
                ckUserStatus.setChecked(false);
                status = StaffStatus.DISABLED;
            }
            //启用
            if(staff.getStatus() == StaffStatus.ENABLED)
            {
                ckUserStatus.setChecked(true);
                status = StaffStatus.ENABLED;
            }
        }
        ckUserStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    status = StaffStatus.ENABLED;
                }
                else{
                    status = StaffStatus.DISABLED;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getQuarters();
    }

    /**
     * 岗位列表
     */
    private List<Staff> staffList = new ArrayList<>();
    private void getQuarters() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.getQuarters(new ResultCallback<List<Staff>>() {
                @Override
                public void onResult(List<Staff> result) {
                    if (result != null) {
                        staffList = result;
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("获取岗位列表失败,", e.getMessage());
                    showToast("获取岗位列表失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    private String nickName = "";
    private String userName = "";
    private String phoneNumber = "";
    private Integer disCount;
    private StaffStatus status;
    private void saveUserInfo() {
        nickName = tvNickName.getText().toString().trim();
        userName = tvUserName.getText().toString().trim();
        phoneNumber = tvPhoneNumber.getText().toString().trim();
        disCount = Integer.valueOf(tvDiscount.getText().toString().trim());

        if (TextUtils.isEmpty(nickName)) {
            showToast("请输入名称");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("请输入电话");
            return;
        }
        if (disCount <= 0) {
            showToast("请输入员工折扣");
            return;
        }
        if(quartersid <= 0)
        {
            showToast("请选取员工岗位");
            return;
        }
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.addUser(nickName,disCount,quartersid,userName, "",new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("添加成功!");
                        StaffInfoAty.this.finish();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("添加新员工失败,", e.getMessage());
                    showToast("添加新员工失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改员工信息
     */
    private void modifyUserInfo() {
        nickName = tvNickName.getText().toString().trim();
        userName = tvUserName.getText().toString().trim();
        phoneNumber = tvPhoneNumber.getText().toString().trim();
        disCount = Integer.valueOf(tvDiscount.getText().toString().trim());

        if (TextUtils.isEmpty(nickName)) {
            showToast("请输入名称");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            showToast("请输入电话");
            return;
        }
        if (disCount <= 0) {
            showToast("请输入员工折扣");
            return;
        }
        if(quartersid <= 0)
        {
            showToast("请选取员工岗位");
            return;
        }
        if(status.getStatus() < 0)
        {
            showToast("请选取员工状态");
            return;
        }
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.modifyStaff(nickName,status.getStatus(),disCount,quartersid,userName, "",new ResultCallback() {
                @Override
                public void onResult(Object result) {
                    if ((int) result == 0) {
                        showToast("修改成功!");
                        StaffInfoAty.this.finish();
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    Log.i("修改员工信息失败,", e.getMessage());
                    showToast("修改员工信息失败," + e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    private int quartersid;
    @OnClick( {R.id.tv_login,R.id.tv_quartersName} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_quartersName:
                if(staffList != null && staffList.size() >0)
                {
                    DialogUtil.showQuarters(context, staffList, new DialogTCallback() {
                        @Override
                        public void onConfirm(Object o) {
                            Staff staff = (Staff)o;
                            if(staff != null)
                            {
                                quartersid = staff.getQuartersid();
                                tvQuartersName.setText(staff.getQuartersName()+"＞");
                            }
                        }

                        @Override
                        public void onCancle() {

                        }
                    });
                }
                else{
                    showToast("员工岗位列表为空!");
                }

                break;
            case R.id.tv_login:
                if(staff == null)
                {
                    saveUserInfo();
                }
                else{;
                    modifyUserInfo();
                }
                break;
        }
    }
}
