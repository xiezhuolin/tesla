package cn.acewill.pos.next.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.acewill.pos.R;
import cn.acewill.pos.next.base.fragment.BaseFragment;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.TerminalVersion;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.service.LogService;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.DownUtlis;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * 软件更新
 * Created by aqw on 2016/8/20.
 */
public class SoftUpdateFragment extends BaseFragment {

    @BindView( R.id.update_tip )
    TextView updateTip;
    @BindView( R.id.update_version )
    TextView updateVersion;
    @BindView( R.id.update_btn )
    TextView updateBtn;
    @BindView( R.id.update_info )
    TextView updateInfo;
    @BindView( R.id.upload_log )
    TextView uploadLog;

    private TerminalVersion result;
    private boolean isCheck;
    private DownUtlis downUtlis;
    Store store;
    private LogService logService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soft_update, container, false);
        ButterKnife.bind(this, view);
        store = Store.getInstance(aty);
        isCheck = false;
        String versionCode = store.getVersionCode();
        if (!TextUtils.isEmpty(versionCode)) {
            updateVersion.setText(" V " + versionCode + ".0");
        } else {
            updateVersion.setText(" V " + MyApplication.getVersionCode() + ".0");
        }
        //下载工具类
        downUtlis = new DownUtlis(aty);
        return view;
    }

    @OnClick( {R.id.update_btn, R.id.upload_log} )
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update_btn:
                if (isCheck) {
                    //下载文件
                    downUtlis.upDataDialog(result.getDescription(),result.getFilename());
                } else {
                    checkVersion();
                }
                break;
            case R.id.upload_log:
                DialogUtil.ordinaryDialog(aty, "上传日志", "是否要是传日志文件?", new DialogCallback() {
                    @Override
                    public void onConfirm() {
                        logService = myApplication.getLogService();
                        if (logService != null) {
//                            System.out.println(logService.getLogPath() + "<<==========LOGPATH");
//                            upLoadLog(logService.getLogPath());
                            zipFile(logService.getLogPath());
                        }
                    }
                    @Override
                    public void onCancle() {
                    }
                });
                break;
        }
    }

    /**
     * 检测新版本
     */
    private void checkVersion() {
        try {
            SystemService systemService = SystemService.getInstance();
            systemService.getTerminalVersions(new ResultCallback<TerminalVersion>() {
                @Override
                public void onResult(TerminalVersion result) {
                    if (result != null) {
                        updateVersion.setText(" V " + result.getVersion() + ".0");
                        updateInfo.setText(result.getDescription());
                        store.setVersionCode(result.getVersion());
                        if (Integer.valueOf(result.getVersion()) > MyApplication.getInstance().getVersionCode()) {
                            SoftUpdateFragment.this.result = result;
                            isCheck = true;
                            updateTip.setText("发现新版本!");
                            updateBtn.setText("立即更新");
                        } else {
                            updateTip.setText("已是最新版本!");
                            showToast("已是最新版本!");
                        }
                    }
                }

                @Override
                public void onError(PosServiceException e) {
                    showToast(e.getMessage());
                }
            });
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传日志文件
     */
    private void upLoadLog(File file) {
        //        String path = "/mnt/internal_sd/MyApp/log/2016-09-08 133303.log";
//        File logFile = zipFile(logPath);
        if(file==null){
            showToast("生成文件失败");
            return;
        }
        try {
            SystemService systemService = SystemService.getInstance();
            if (file != null) {
                systemService.upLoadLogFile(file, new ResultCallback() {
                    @Override
                    public void onResult(Object result) {
                        showToast("上传成功!");
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        showToast("上传失败!" + e.getMessage());
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
        }
    }


    //压缩文件
    public void zipFile(final String url) {

        new Thread() {
            @Override
            public void run() {
                try {
                    showToast("正在压缩日志文件...");
                    String device = Store.getInstance(aty).getDeviceName();
                    File oldFile = new File(url);
                    String zipPath = url.replace(".log", ".zip");
                    zipPath = zipPath.replace("_P","_P_"+device);
                    File zipFile = new File(zipPath);
                    if (zipFile.exists()) {
                        if (zipFile.delete()) {
                            zipFile.createNewFile();
                        }

                    } else {
                        zipFile.createNewFile();
                    }

                    InputStream input = new FileInputStream(oldFile);
                    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
                    zipOut.putNextEntry(new ZipEntry(oldFile.getName().replace("_P","_P_"+device)));
                    int temp = 0;
                    byte[] buffer = new byte[2048];
                    while ((temp = input.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, temp);
                    }
                    input.close();
                    zipOut.close();

                    if (zipFile != null) {
                        showToast("压缩文件成功!");
                        upLoadLog(zipFile);
                    }else {
                        showToast(ToolsUtils.returnXMLStr("compressed_file_failed"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
