package cn.acewill.pos.next.service.canxingjian.retrofit;

import java.io.File;

import retrofit2.Response;
import retrofit2.http.GET;
import rx.Observable;

public interface CXJRetrofitDBService {

    /**
     * 是否更新数据库
     */
    @GET("order/api/api.php?do=isSqliteFileUpdated&app=ClientNewAndroidMobile")
    Observable<Response<String>> isUpdataDb();

    /**
     * 下载数据库
     */
    @GET("sync/sqlite/basicMenuData.db")
    Observable<Response<File>> downLoadDbFile();
}
