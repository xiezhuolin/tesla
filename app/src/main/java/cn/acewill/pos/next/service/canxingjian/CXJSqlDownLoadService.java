package cn.acewill.pos.next.service.canxingjian;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.acewill.pos.next.exception.ErrorCode;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.factory.RetrofitFactory;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.ResultSubscriber;
import cn.acewill.pos.next.service.canxingjian.retrofit.CXJRetrofitDBService;
import cn.acewill.pos.next.utils.Constant;
import retrofit2.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class CXJSqlDownLoadService {
    private CXJServerUrl cxjServerUrl;
    private String DB_NAME = "cxjDish.db";
    CXJRetrofitDBService internalService;

    public CXJSqlDownLoadService() {
        cxjServerUrl = new CXJServerUrl();
        internalService = RetrofitFactory.buildService(cxjServerUrl.getBaseUrl(), CXJRetrofitDBService.class);
    }

    /**
     * 是否更新数据库  true/false
     * @param resultCallback
     */
    public void IsSqlFileUpdata(final ResultCallback<String> resultCallback)
    {
        internalService.isUpdataDb().map(new Func1<Response<String>, String>() {
            @Override
            public String call(Response<String> response) {
                String isUpdata = response.body();
                if(!TextUtils.isEmpty(isUpdata))
                {
                    return isUpdata;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, response.message());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<String>(resultCallback));
    }

    /**
     *
     * @return file
     * @throws IOException
     */
    public void downLoadDB(final ResultCallback<File> resultCallback){
        System.out.println("downLoadDB=======");
        internalService.downLoadDbFile().map(new Func1<Response<File>, File>() {
            @Override
            public File call(Response<File> response) {
                System.out.println("call=======");
                File dbFile = response.body();
                System.out.println(dbFile.length()+"<<==========");
                String address = null;
                address = Constant.DATABASE_PATH;;
                int BUFFER = 1024;
                try {
                    FileInputStream fin = new FileInputStream(dbFile);
                    File filedir = new File(address + "/");
                    if (!filedir.exists())
                        filedir.mkdirs();
                    File file = new File(address + "/" + Constant.DATABASE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    byte[] b = new byte[BUFFER];
                    int len = 0;
                    while ((len = fin.read(b)) != -1) {
                        out.write(b, 0, len);
                    }
                    fin.close();
                    out.flush();
                    out.close();
                    if(dbFile.length() > 0)
                    {
                        return dbFile;
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                    return null;
                }
                PosServiceException exception = new PosServiceException(ErrorCode.NETWORK_ERROR, Constant.FILE_DOWNLOAD_ERROR);
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<File>(resultCallback));
    }

}
