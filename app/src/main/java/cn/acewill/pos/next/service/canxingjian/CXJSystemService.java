package cn.acewill.pos.next.service.canxingjian;

import android.util.Log;

import java.io.IOException;

import cn.acewill.pos.next.exception.ErrorCode;
import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.factory.RetrofitFactory;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.ResultSubscriber;
import cn.acewill.pos.next.service.canxingjian.retrofit.CXJRetrofitSystemService;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.OperationResponse;
import cn.acewill.pos.next.service.retrofit.response.PosResponse;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Acewill on 2016/6/12.
 */
public class CXJSystemService {
    CXJRetrofitSystemService internalSystemService;

    public CXJSystemService() {
        CXJServerUrl cxjServerUrl = new CXJServerUrl();
        internalSystemService = RetrofitFactory.buildService(cxjServerUrl.getBaseUrl(), CXJRetrofitSystemService.class);
    }

    /**
     * 登录，
     *
     * @param username
     * @param password
     * @return 成功返回用户对象，否则返回null
     * @throws IOException
     */
    public void login(String username, String password, final ResultCallback resultCallback) {
        internalSystemService.login(username, password).map(new Func1<PosResponse, Integer>() {
            @Override
            public Integer call(PosResponse response) {
                if (response.getResult() == 0) {
                    return 0;
                }

                //                String cookies = response.headers().get("Set-Cookie");
                //                    LoginResponse loginResponse = response.body();
                //                    User u = null;
                //                    if (loginResponse.getSuccess() > 0) {
                //                        //设置cookie，后续的有些操作需要cookie才能调用
                //                        CookieInterceptor.addCookie(cookies);
                //
                //                        u = new User();
                //                        u.setName(loginResponse.getRname());
                //                        u.setAccount(loginResponse.getUsername());
                //                        u.setId(Integer.parseInt(loginResponse.getUid()));
                //                        u.setSessionId(cookies);
                PosServiceException exception = new PosServiceException(ErrorCode.INVALID_USERNAME_PASSWORD, response.getErrmsg());
                throw Exceptions.propagate(exception);
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ResultSubscriber<Integer>(resultCallback));
    }

    /**
     * 修改密码
     * username:1
     * oldpwd:acewill
     * newpwd:acewill1
     */
    public void updatePassword(String username, String oldPassword, String newPassword, ResultCallback resultCallback) throws IOException, PosServiceException {
        internalSystemService.updatePassword(username, oldPassword, newPassword).map(new Func1<OperationResponse, Boolean>() {
            @Override
            public Boolean call(OperationResponse response) {
                if (response.isSuccess()) {
                    return true;
                }

                throw Exceptions.propagate(new PosServiceException(ErrorCode.INVALID_USERNAME_PASSWORD));
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new ResultSubscriber<Boolean>(resultCallback)
        );
    }
}
