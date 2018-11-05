package cn.acewill.pos.next.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.table.Table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Acewill on 2016/6/3.
 */
public class TableServiceTest {
    TableService service;
    @Rule
    public RxJavaResetRule rxJavaResetRule = new RxJavaResetRule();
    @Before
    public void setup() throws PosServiceException {
        PosInfo.getInstance().setServerUrl("http://sz.canxingjian.com:18080");
        service = TableService.getInstance();
    }

    @Test
    public void getTablesStatus() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<Table>> testResultCallback = new TestResultCallback(service);

        service.getTables("1", "1", "1",1, testResultCallback);
        testResultCallback.waitForComplete();

        //验证结果 - 门店1有5个桌台
        List<Table> orderList = testResultCallback.getResult();
        assertEquals(5, orderList.size());
        assertNull(testResultCallback.getException());
    }
}
