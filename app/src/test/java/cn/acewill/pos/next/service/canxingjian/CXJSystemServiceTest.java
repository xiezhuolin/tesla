package cn.acewill.pos.next.service.canxingjian;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.user.User;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.canxingjian.CXJSystemService;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Acewill on 2016/6/12.
 */
@Ignore
public class CXJSystemServiceTest {
    CXJSystemService service;
    @Before
    public void setup() {
        service = new CXJSystemService();
    }

    @Test
    public void loginSuccess() throws PosServiceException, IOException {
        service.login("1", "acewill", new ResultCallback<User>() {
            @Override
            public void onResult(User user) {
                //登录成功
                assertEquals("1",user.getAccount());
                assertNotNull(user.getSessionId());
            }

            @Override
            public void onError(PosServiceException e) {
                fail("update password failed");
            }
        });

    }

    @Test(expected=PosServiceException.class)
    public void loginFailed() throws PosServiceException, IOException {
        service.login("1", "acewill2", new ResultCallback<User>() {
            @Override
            public void onResult(User user) {
                assertNull(user);
            }

            @Override
            public void onError(PosServiceException e) {

            }
        });
    }

    @Test
    public void updatePasswordSuccess() throws PosServiceException, IOException {
        service.updatePassword("1","acewill1", "acewill2", new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                //修改成功
                assertTrue(result);
            }

            @Override
            public void onError(PosServiceException e) {
                fail("update password failed");
            }
        });

    }

    @Test(expected=PosServiceException.class)
    public void updatePasswordFailed() throws PosServiceException, IOException {
        service.updatePassword("2","xxxxxx","acewill2", new ResultCallback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                //修改成功
                assertTrue(result);
            }

            @Override
            public void onError(PosServiceException e) {

            }
        });
    }
}
