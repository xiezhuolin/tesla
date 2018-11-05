package cn.acewill.pos.next.ui.activity.newPos;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import cn.acewill.pos.next.base.activity.BaseActivity;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.SystemService;
import cn.acewill.pos.next.utils.DialogUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;


/**
 *上传日志
 * Created by aqw on 2016/8/30.
 */
public class UpLoadActivity extends BaseActivity {
    @BindView( R.id.beforday )
    TextView beforday;
    @BindView( R.id.yesterday )
    TextView yesterday;
    @BindView( R.id.today )
    TextView today;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_up_load);
        myApplication.addPage(UpLoadActivity.this);
        ButterKnife.bind(this);
    }

    @OnClick( {R.id.beforday, R.id.yesterday, R.id.today} )
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.beforday://前天日志
                ToolsUtils.writeUserOperationRecords(ToolsUtils.returnXMLStr("up_load_the_day_before_yesterday"));
                upLoadLog(TimeUtil.getAgoDay(-2));
                break;
            case R.id.yesterday://昨天日志
                ToolsUtils.writeUserOperationRecords(ToolsUtils.returnXMLStr("up_load_yesterday"));
                upLoadLog(TimeUtil.getAgoDay(-1));
                break;
            case R.id.today://今天日志
                ToolsUtils.writeUserOperationRecords(ToolsUtils.returnXMLStr("up_load_today"));
                upLoadLog(TimeUtil.getAgoDay(0));
                break;
        }
    }

    /**
     * 上传日志文件
     */
    private  Dialog dialog;
    private void upLoadLog(String logFileName) {
        dialog = DialogUtil.createDialog(context, R.layout.dialog_uplog, 6, LinearLayout.LayoutParams.WRAP_CONTENT);

        String filePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath()
                + File.separator
                + "MyApp"
                + File.separator
                + "System_log";
        filePath += File.separator + logFileName + "_P.log";
        String time = TimeUtil.getStringTime(System.currentTimeMillis());
        time = "_"+time.replace(":","_");
        zipFile(filePath, time,dialog);
    }



    //压缩文件
    public void zipFile(final String url,final String time, final Dialog dialog){

        new Thread() {
            @Override
            public void run() {
                try {
                    String device = Store.getInstance(context).getDeviceName()+time;
                    File oldFile = new File(url);
                    String zipPath = url.replace(".log", ".zip");
                    zipPath = zipPath.replace("_P", "_P_" + device);
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
                    zipOut.putNextEntry(new ZipEntry(oldFile.getName().replace("_P", "_P_" + device)));
                    int temp = 0;
                    byte[] buffer = new byte[2048];
                    while ((temp = input.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, temp);
                    }
                    input.close();
                    zipOut.close();

                    if (zipFile != null) {
                        upLog(zipFile, dialog);
                    } else {
                        showToast(ToolsUtils.returnXMLStr("compressed_file_failed"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showToast(ToolsUtils.returnXMLStr("not_find_file_please_select_log"));
                    dialog.dismiss();
                }
            }
        }.start();
    }

    //调用上传接口
    private void upLog(final File logFile, final Dialog dialog) {
        try {
            SystemService systemService = SystemService.getInstance();
            if (logFile != null) {
                systemService.upLoadLogFile(logFile, new ResultCallback() {
                    @Override
                    public void onResult(Object result) {
                        dialog.dismiss();
                        showToast(ToolsUtils.returnXMLStr("upload_success"));
                        Log.i("日志上传成功:", "success");
                        logFile.delete();
                        finish();
                    }

                    @Override
                    public void onError(PosServiceException e) {
                        dialog.dismiss();
                        showToast(ToolsUtils.returnXMLStr("upload_failure") + e.getMessage());
                        Log.e("日志上传失败:", e.getMessage());
                    }
                });
            }
        } catch (PosServiceException e) {
            e.printStackTrace();
            dialog.dismiss();
            showToast(ToolsUtils.returnXMLStr("upload_failure") + e.getMessage());
            Log.e("日志上传失败:", e.getMessage());
        }
    }

}
