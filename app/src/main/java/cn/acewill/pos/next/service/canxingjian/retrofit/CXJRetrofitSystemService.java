package cn.acewill.pos.next.service.canxingjian.retrofit;

import cn.acewill.pos.next.service.canxingjian.retrofit.message.OperationResponse;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Acewill on 2016/6/6.
 */
public interface CXJRetrofitSystemService {
    /**
     * {"success":1,"funcodes":[100,101,102],"username":"1","rname":"\u7ba1\u7406\u5458","uid":"1"}
     * 或者{"success":0,"msg":"用户名与密码不匹配"}
     * 登陆接口
     */
    @FormUrlEncoded
    @POST("management/user/login")
//    Observable<Response<LoginResponse>> login(@Field("username") String username, @Field("pwd") String password);
    Observable<PosResponse> login(@Field("username") String username, @Field("pwd") String password);

    /**
     * 输入username:1
     * oldpwd:acewill
     * newpwd:acewill1
     *
     * {"success":1}
     * 或者{"success":0,"msg":"用户名与密码不匹配"}
     * 登陆接口
     */
    @FormUrlEncoded
    @POST("order/api/api.php?app=clientTouch&do=updateLoginPwd")
    Observable<OperationResponse> updatePassword(@Field("username") String username, @Field("oldpwd") String oldPassword, @Field("newpwd") String newPassword);

}
