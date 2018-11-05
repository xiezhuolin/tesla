package cn.acewill.pos.next.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.dish.DishCount;
import cn.acewill.pos.next.model.dish.DishType;
import cn.acewill.pos.next.model.dish.Menu;
import cn.acewill.pos.next.model.order.MarketingActivity;
import cn.acewill.pos.next.model.payment.Payment;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by Acewill on 2016/8/2.
 */
public class DishServiceTest {
    DishService dishService;
    SystemService systemService;
    @Rule
    public RxJavaResetRule rxJavaResetRule = new RxJavaResetRule();

    @Before
    public void setup() throws InterruptedException, PosServiceException {
        // PosInfo.getInstance().setServerUrl("http://sz.canxingjian.com:18080");
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setServerUrl("http://localhost:8080");
        posInfo.setAppId("app-unit-test");
        posInfo.setBrandId("1");
        posInfo.setStoreId("1");
        posInfo.setTerminalName("a123");
        posInfo.setTerminalMac("a123");
        posInfo.setReceiveNetOrder(0);
        posInfo.setCurrentVersion("1.2.3");
        posInfo.setVersionId("11");

        dishService = DishService.getInstance();
        systemService = SystemService.getInstance();

        TestResultCallback<Integer> testResultCallback = new TestResultCallback(systemService);

        //先登录终端
        systemService.terminalLogin(testResultCallback);

        testResultCallback.waitForComplete();
        assertNotNull(testResultCallback.getResult());

        TestResultCallback<Integer> testResultCallback2 = new TestResultCallback(systemService);
        //然后登录用户
        systemService.login("1","1",testResultCallback2);
        testResultCallback2.waitForComplete();
        assertNotNull(testResultCallback2.getResult());
    }

    @Test
    public void getKindDataInfo() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<DishType>> testResultCallback = new TestResultCallback(dishService);
        dishService.getKindDataInfo(testResultCallback);
        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        assertEquals(3, testResultCallback.getResult().size());
    }

    @Test
    public void getDishList() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<Menu>> testResultCallback = new TestResultCallback(dishService);
        dishService.getDishList(testResultCallback);
        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        assertEquals(2, testResultCallback.getResult().size());

    }

    @Test
    public void getPaytypeList() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<Payment>> testResultCallback = new TestResultCallback(dishService);
        dishService.getPaytypeList(testResultCallback);
        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        assertEquals(7, testResultCallback.getResult().size());
    }

    @Test
    public void checkDishCount() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<DishCount>> testResultCallback = new TestResultCallback(dishService);
        List<DishCount> dishCountList = new ArrayList<>();

        dishCountList.add(new DishCount(1, 10000));
        dishCountList.add(new DishCount(2, 1));
        dishService.checkDishCount(dishCountList, testResultCallback);
        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        //菜品1 点了10000份，所以一定会不够
        assertEquals(1, testResultCallback.getResult().size());
    }

    @Test
    public void getMarketingActivityList() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<MarketingActivity>> testResultCallback = new TestResultCallback(dishService);

        dishService.getMarketingActivityList(testResultCallback);
        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        //
        assertEquals(2, testResultCallback.getResult().size());
    }
}
