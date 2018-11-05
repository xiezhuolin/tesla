package cn.acewill.pos.next.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cn.acewill.pos.R;
import cn.acewill.pos.next.service.DialogCallback;
import cn.acewill.pos.next.widget.CircleProgressView;



public class DownUtlis {

    private Context context;
    private File downFile = null;
    // apk文件名
    private String UPDATE_SERVERAPK = "pos.apk";
    private Dialog loading_dialog;

    private static DownUtlis downUtlis;
    private CircleProgressView progressView;

    public DownUtlis(Context context) {
        this.context = context;
        this.loading_dialog = DialogUtil.createDialog(context, R.layout.dialog_up_data,3, LinearLayout.LayoutParams.WRAP_CONTENT,false);
        progressView = (CircleProgressView)loading_dialog.findViewById(R.id.progressView);
    }

    public static DownUtlis getInstance(Context context){
        if(downUtlis==null){
            downUtlis = new DownUtlis(context);
        }
        return downUtlis;
    }

    /**
     * 有新版本时弹出的对话框
     *
     * @param //      更新日志
     * @param fileurl apk下载链接
     * @author sxf
     * @date 2014-9-30 上午10:23:13
     */
    public void upDataDialog(final String description,final String fileurl) {
        DialogUtil.ordinaryDialog(context, context.getResources().getString(R.string.text_app_version), context.getResources().getString(R.string.text_app_version_hine)+"\n 更新内容 : "+description, new DialogCallback() {
            @Override
            public void onConfirm() {
                loading_dialog.show();
                downFile(fileurl);
            }

            @Override
            public void onCancle() {
            }
        });
    }

    /**
     * 下载apk
     */
    public void downFile(final String url) {
        // pd.show();
        if (!isNetworkConnected()) {
            ShowToast(R.string.err_network);
            return;
        }
        final String finalPath = url;
        new Thread() {
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(finalPath);
                    connection = (HttpURLConnection) url.openConnection();
                    // 设置请求方法，默认是GET
                    connection.setRequestMethod("GET");
                    // 设置字符集
                    connection.setRequestProperty("Charset", "UTF-8");
                    // 设置文件类型
                    connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                    if (connection.getResponseCode() == 200) {
                        long length = connection.getContentLength();
                        InputStream is = connection.getInputStream();
                        FileOutputStream fileOutputStream = null;
                        if (is != null) {
                            downFile = new File(Environment.getExternalStorageDirectory(), UPDATE_SERVERAPK);
                            fileOutputStream = new FileOutputStream(downFile);
                            byte[] b = new byte[1024];
                            int charb = -1;
                            int count = 0;
                            while ((charb = is.read(b)) != -1) {
                                fileOutputStream.write(b, 0, charb);
                                count += charb;
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = length;
                                msg.arg2 = count;
                                downloadHandle.sendMessage(msg);
                            }
                        }
                        fileOutputStream.flush();
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        update();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 下载更新进度条
     */
    @SuppressLint( "HandlerLeak" )
    private Handler downloadHandle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    long total = (Long) msg.obj;
                    long progress = (long) msg.arg2;
                    float p = ((float) progress / (float) total);
                    BigDecimal b = new BigDecimal(p);
                    float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
                    if (!loading_dialog.isShowing()) {
                        loading_dialog.show();
                    }
                    int pro = (int) (f1 * 100);
                    progressView.setProgressNotInUiThread(pro);
                    if (pro == 100) {
                        loading_dialog.dismiss();
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };

    /**
     * 安装应用
     */
    public void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), UPDATE_SERVERAPK)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 判断网络是否可用
     *
     * @return true为可用 false为不可用
     */
    @SuppressWarnings( "static-access" )
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            return network.isAvailable();
        } else {
            ShowToast(R.string.err_network);
        }
        return false;
    }

    private void ShowToast(int strID) {
        Toast.makeText(context, context.getResources().getString(strID),
                Toast.LENGTH_SHORT).show();
    }

}
