package cn.acewill.pos.next.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Acewill on 2016/6/17.
 */
public class OrderServiceTest {
    OrderService service;
    SystemService systemService;

    @Rule
    public RxJavaResetRule rxJavaResetRule = new RxJavaResetRule();

    @Before
    public void setup() throws InterruptedException, PosServiceException {
       // PosInfo.getInstance().setServerUrl("http://sz.canxingjian.com:18080");
        PosInfo posInfo = PosInfo.getInstance();
        posInfo.setServerUrl("http://sz.canxingjian.com:18080");
        posInfo.setAppId("app-unit-test");
        posInfo.setBrandId("1");
        posInfo.setStoreId("1");
        posInfo.setTerminalName("a123");
        posInfo.setTerminalMac("a123");
        posInfo.setReceiveNetOrder(0);
        posInfo.setCurrentVersion("1.2.3");
        posInfo.setVersionId("11");

        service = OrderService.getInstance();
        systemService = SystemService.getInstance();

        TestResultCallback<Integer> testResultCallback = new TestResultCallback(service);


        systemService.terminalLogin(testResultCallback);
        testResultCallback.waitForComplete();
    }

    @Test
    public void getOrdersSuccess() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<Order>> testResultCallback = new TestResultCallback(service);

        service.getAllOrders("","1", "1", "1",0,10,-1,-1, testResultCallback);
        testResultCallback.waitForComplete();

        //验证结果 - 门店1有4个订单
        List<Order> orderList = testResultCallback.getResult();
        assertEquals(4, orderList.size());
        assertNull(testResultCallback.getException());
    }

    @Test
    public void getOrdersFail() throws URISyntaxException, IOException, InterruptedException {
        TestResultCallback<List<Order>> testResultCallback = new TestResultCallback(service);

        service.getAllOrders("","1", "1", "3",0,10,-1,-1, testResultCallback);
        testResultCallback.waitForComplete();

        //验证结果 -- 门店3没有订单
        List<Order> orderList = testResultCallback.getResult();
        assertEquals(0, orderList.size());
        assertNull(testResultCallback.getException());
    }

    @Test
    public void getOrderById() throws URISyntaxException, IOException, InterruptedException{
        TestResultCallback<Order> testResultCallback = new TestResultCallback(service);

        service.getOrderById(1, testResultCallback);
        testResultCallback.waitForComplete();

        //验证结果 - 订单下有3个菜品
        Order order = testResultCallback.getResult();
        assertEquals(3, order.getItemList().size());
        assertNull(testResultCallback.getException());
    }

    @Test
    public void getOrdersByTableId() throws URISyntaxException, IOException, InterruptedException{
        TestResultCallback<List<Order>> testResultCallback = new TestResultCallback(service);

        service.getOrdersByTableId(1,testResultCallback);
        testResultCallback.waitForComplete();
        //验证结果--订单数量为1
        List<Order> orderList = testResultCallback.getResult();
        assertEquals(0, orderList.size());
        assertNull(testResultCallback.getException());
    }

    @Test
    public void createOrder() throws URISyntaxException, IOException, InterruptedException{
        TestResultCallback<Order> testResultCallback = new TestResultCallback(service);

        Order order = new Order();
        List<OrderItem> itemList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setDishId(2);
        orderItem.setDishName("麻辣牛肉粉");
        orderItem.setPrice(BigDecimal.valueOf(2.2));
        orderItem.setCost(BigDecimal.valueOf(2));
        orderItem.setQuantity(3);
        orderItem.setComment("不放辣椒");
        itemList.add(orderItem);


        order.setTotal("6.2");
        order.setCost("3.6");
        order.setSource("测试pos");
        order.setDiscount(4);
        order.setComment("下单2");
        order.setCreatedAt(System.currentTimeMillis());
        order.setItemList(itemList);


        service.createOrder(order,testResultCallback);
        testResultCallback.waitForComplete();

        assertNotNull(testResultCallback.getResult());
        assertNull(testResultCallback.getException());
    }



    @Test
    public void appendOrder()throws URISyntaxException, IOException, InterruptedException{
     /*   TestResultCallback<Integer> testResultCallback = new TestResultCallback(service);
        CreateOrder order = new CreateOrder();
        List<CreateOrder.MyOrderItme> itemList = new ArrayList<CreateOrder.MyOrderItme>();
        CreateOrder.MyOrderItme orderItem = order.new MyOrderItme();
        orderItem.setDishId(6);
        orderItem.setPrice(3.3);
        orderItem.setCost(2);
        orderItem.setQuantity(2);
        orderItem.setComment("放卤蛋");
        itemList.add(orderItem);

        service.appendOrder(1,itemList,testResultCallback);
        testResultCallback.waitForComplete();
        Integer result = testResultCallback.getResult();
        assertEquals("1", result);
        assertNull(testResultCallback.getException());*/
    }

    @Test
    public void margeOrder()throws URISyntaxException, IOException, InterruptedException{
        TestResultCallback<Integer> testResultCallback = new TestResultCallback(service);
        service.mergeOrder("1", "1", "1",11,12,testResultCallback);
        testResultCallback.waitForComplete();
        Integer result = testResultCallback.getResult();
        assertEquals("1", result);
        assertNull(testResultCallback.getException());
    }
}
