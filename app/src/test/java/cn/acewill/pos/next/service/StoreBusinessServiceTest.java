package cn.acewill.pos.next.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.printer.Printer;
import cn.acewill.pos.next.model.Receipt;
import cn.acewill.pos.next.model.ReceiptType;
import cn.acewill.pos.next.model.StoreBusinessInformation;
import cn.acewill.pos.next.model.WorkShift;
import cn.acewill.pos.next.printer.PrinterWidth;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by Acewill on 2016/8/5.
 */
public class StoreBusinessServiceTest {
    StoreBusinessService storeBusinessService;
    SystemService systemService;

    @Rule
    public RxJavaResetRule rxJavaResetRule = new RxJavaResetRule();

    @Before
    public void setup() throws InterruptedException, PosServiceException {
        // PosInfo.getInstance().setServerUrl("http://sz.canxingjian.com:18080");
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setServerUrl("http://localhost:8080");
        posInfo.setAppId("a123");
        posInfo.setBrandId("2");
        posInfo.setStoreId("2");
        posInfo.setTerminalName("a123");
        posInfo.setTerminalMac("a123");
        posInfo.setReceiveNetOrder(0);
        posInfo.setCurrentVersion("1.2.3");
        posInfo.setVersionId("11");

        storeBusinessService = StoreBusinessService.getInstance();
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
    public void startWorkShift() throws InterruptedException {
        WorkShift workShift = new WorkShift();
        workShift.setDefinitionId(1);
        workShift.setUserId(1);
        workShift.setUserName("app unit test");
        workShift.setSpareCash(new BigDecimal(110));
        workShift.setStartTime(System.currentTimeMillis());
        workShift.setTerminalId(1);

        TestResultCallback<WorkShift> testResultCallback = new TestResultCallback(storeBusinessService);
        storeBusinessService.startWorkShift(workShift,testResultCallback);

        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        assertNotNull(testResultCallback.getResult().getId());
    }

    @Test
    public void endWorkShift() throws InterruptedException {
        WorkShift workShift = new WorkShift();
        workShift.setCashRevenue(new BigDecimal(200000));
        workShift.setEndTime(System.currentTimeMillis());


        TestResultCallback<Integer> testResultCallback = new TestResultCallback(storeBusinessService);
        storeBusinessService.endWorkShift(new Long(1005), workShift,testResultCallback);

        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
    }

    @Test
    public void addPrinter() throws InterruptedException {
        Printer printer = new Printer();
        printer.setDescription("厨房打印机， app测试");
        printer.setIp("1.2.2.3");
        printer.setDeviceName("bm21");
        printer.setWidth(PrinterWidth.WIDTH_56MM);
        List<ReceiptType> typeList = new ArrayList<ReceiptType>();
        typeList.add(ReceiptType.ORDER_RECEIPT);
        typeList.add(ReceiptType.DAY_RECEIPT);
        printer.setReceiptTypeList(typeList);
        printer.setVendor("爱普生");


        TestResultCallback<Printer> testResultCallback = new TestResultCallback(storeBusinessService);
        storeBusinessService.addPrinter(printer, testResultCallback);

        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
    }

    @Test
    public void listPrinters() throws InterruptedException {
        TestResultCallback<List<Printer>> testResultCallback = new TestResultCallback(storeBusinessService);
        try {
            storeBusinessService.listPrinters(testResultCallback);
            testResultCallback.waitForComplete();

            // assertNotNull(testResultCallback.getResult());
            //  assertTrue(testResultCallback.getResult().size() > 0);
            Thread.sleep(10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void deletePrinter() throws InterruptedException {
        TestResultCallback<Integer> testResultCallback = new TestResultCallback(storeBusinessService);
        storeBusinessService.deletePrinter(1006, testResultCallback);

        testResultCallback.waitForComplete();

        assertNull(testResultCallback.getException());
    }

    @Test
    public void updatePrinter() throws InterruptedException {
        TestResultCallback<Printer> testResultCallback = new TestResultCallback(storeBusinessService);
        Printer printer = new Printer();
        printer.setVendor("test");
        printer.setDescription("test");
        printer.setIp("1.2.3.4");
        storeBusinessService.updatePrinter(1004,printer, testResultCallback);

        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
    }

    @Test
    public void listReceipts() throws InterruptedException {
        TestResultCallback<List<Receipt>> testResultCallback = new TestResultCallback(storeBusinessService);
        try {
            storeBusinessService.listReceipts(testResultCallback);
            testResultCallback.waitForComplete();

            assertNotNull(testResultCallback.getResult());
            assertTrue(testResultCallback.getResult().size() > 0);
            Thread.sleep(10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getStoreBusinessInformation() throws InterruptedException {
        TestResultCallback<StoreBusinessInformation> testResultCallback = new TestResultCallback(storeBusinessService);
        try {
            storeBusinessService.getStoreBusinessInformation(testResultCallback);
            testResultCallback.waitForComplete();

            assertNotNull(testResultCallback.getResult());
            Thread.sleep(10 * 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
