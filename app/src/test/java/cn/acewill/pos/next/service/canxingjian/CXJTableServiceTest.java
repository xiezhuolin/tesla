package cn.acewill.pos.next.service.canxingjian;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.acewill.pos.next.exception.PosServiceException;
import cn.acewill.pos.next.model.Restaurant;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.user.User;
import cn.acewill.pos.next.service.ResultCallback;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.AppendToOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.CreateOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.HastenDishRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.MergeOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.RejectDishRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.ServeOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.SplitOrderRequest;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.SwitchTableRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Acewill on 2016/6/3.
 */
@Ignore
public class CXJTableServiceTest {
    CXJTableService service;
    CXJSystemService systemService;

    @Before
    public void setup() {
        service = new CXJTableService();
        systemService = new CXJSystemService();
    }

    private void login(String username, String password) {
        systemService.login(username, password, new ResultCallback<User>() {
            @Override
            public void onResult(User result) {

            }

            @Override
            public void onError(PosServiceException e) {

            }
        });
    }


//    @Test
//    public void getTablesStatus() throws URISyntaxException, IOException {
//        List<Table> tableList = service.getTables();
//
//        assertEquals(2, tableList.size());
//        Table table = tableList.get(0);
//        assertEquals(1, table.getId());
//        assertEquals(TableStatus.UseStatus.EMPTY, table.getStatus());
//        assertEquals(TableStatus.BookingStatus.NOT_BOOKING, table.getBookingStatus());
//
//        table = tableList.get(1);
//        assertEquals(2, table.getId());
//        assertEquals(TableStatus.UseStatus.IN_USE, table.getStatus());
//        assertEquals(TableStatus.BookingStatus.NOT_BOOKING, table.getBookingStatus());
//    }

    @Test
    public void getRestaurantInfo() throws URISyntaxException, IOException {
        Restaurant restaurant  = service.getRestaurantInfo();

        assertEquals("小南国中信泰富店", restaurant.getName());
    }

    @Test
    public void getOrderList() throws URISyntaxException, IOException {
        List<Order> orders  = service.getOrderList();

        assertEquals(3, orders.size());
    }

    @Test
    public void getOrderByTableId() throws URISyntaxException, IOException {
        List<Order> orders  = service.getOrdersByTableId(2);

        assertEquals(1, orders.size());
    }

    @Test
    public void getOrderById() throws URISyntaxException, IOException {
        Order  order = service.getOrderById(31);

        assertEquals(1, order.getCustomerAmount());
    }

    @Test
    public void setTableDirty() throws IOException, PosServiceException {
        login("1","acewill");
        boolean successful = service.setTableDirty(2);
        assertTrue(successful);

        successful = service.setTableDirty(222);
        assertFalse(successful);
    }

    @Test
    public void setTableEmpty() throws IOException, PosServiceException {
        login("1","acewill");
        boolean successful = service.setTableEmpty(2);
        assertTrue(successful);

        successful = service.setTableEmpty(222);
        assertFalse(successful);
    }

    @Test
    public void getServerTimestamp() throws URISyntaxException, IOException {
        Date date = service.getServerTimestamp();

    }


    @Test
    public void createOrder() throws PosServiceException, IOException {
        //需要登录才能创建订单
        login("1","acewill");
        CreateOrderRequest request = new CreateOrderRequest();

      //  {"accessid":"","orderidentity":1465189403,"mid":-1,"username":"1","pwd":"1","people":"7","tid":"2","tids":"2","ctid":"",
         //   * "saleuid":"","bid":"","omids":"","freememo":"","normalitems":[[0,"64","3",1,"26.00","","","","",[],false,1]],"setmeals":[],"freeitems":[]}
        request.setAccessid("");
        request.setOrderidentity(System.currentTimeMillis());
        request.setMid(-1);
        request.setUsername("1");
        request.setPwd("1");
        request.setPeople("7");
        request.setTid("2");
        request.setTids("2");
        request.setCtid("");
        request.setSaleuid("");
        request.setBid("");
        request.setCtid("");
        request.setFreememo("");

        CreateOrderRequest.OrderItemRequest or = new CreateOrderRequest.OrderItemRequest();
        or.setBwait(0);
        or.setDid("64");
        or.setDuid("3");
        or.setAmount(1);
        or.setPrice("26.00");
        or.setAssistduid("");
        or.setAssistamount("");
        or.setOmids("");
        or.setFreememo("");
        or.setBgift(false);
        or.setUnknown(1);

        List<List<Object>> items = new ArrayList<>() ;
        items.add(or.toArray());

        request.setNormalitems(items);

        Order order = service.createOrder(request);
        assertNotNull(order);

    }

    @Test
    public void hastenDish() throws PosServiceException, IOException {
        login("1","acewill");
        //{"oid":"31","accessid":"","border":false,"dkids":"","amount":["192_2","186_1"],"oiid":["192","186"],"username":"1","pwd":"acewill","operationId":1465260820}
        HastenDishRequest request = new HastenDishRequest();
        request.setAccessid("");
        List<String> amount = new ArrayList<>();
        amount.add("192_2");
        amount.add("186_1");
        request.setAmount(amount);
        request.setBorder(false);
        request.setDkids("");
        request.setUsername("1");
        request.setPwd("acewill");
        request.setOid("31");

        List<String> oiid = new ArrayList<>();
        oiid.add("192");
        oiid.add("186");
        request.setOiid(oiid);
        request.setOperationId(System.currentTimeMillis());

        Order order = service.hastenDish(request);
        assertNotNull(order);
        assertEquals(31,order.getId());
    }

    @Test
    public void rejectDish() throws PosServiceException, IOException {
        login("1","acewill");

        RejectDishRequest request = new RejectDishRequest();

        //amount:2, rsid:8 ,oiid:194 ,oid:31 ,accessid:
        request.setAmount(2);
        request.setRsid(8);
        request.setOiid(194);
        request.setOid(31);
        request.setAccessid("");

        Order order = service.rejectDish(request);
        assertNotNull(order);
        assertEquals(31,order.getId());
    }

    @Test
    public void serveOrder() throws PosServiceException, IOException {
        login("1","acewill");

        ServeOrderRequest request = new ServeOrderRequest();

        request.setOid("31");
        request.setAccessid("");

        Order order = service.serveOrder(request);
        assertNotNull(order);
        assertEquals(31,order.getId());
    }

    @Test
    public void switchTable() throws IOException, PosServiceException {
        login("1","acewill");

        SwitchTableRequest request = new SwitchTableRequest();

        request.setTidFrom("2");
        request.setTidTo("1");
        request.setOid("34");
        request.setAccessid("");

        Order order = service.switchTable(request);
        assertNotNull(order);
        assertEquals(31,order.getId());
    }

    @Test
    public void mergeOrder() throws IOException, PosServiceException {
        login("1","acewill");

        MergeOrderRequest request = new MergeOrderRequest();

        request.setFromoid("39");
        request.setTooid("34");
        request.setBswitchtable("1");

        assertTrue(service.mergeOrder(request));
    }

    @Test
    public void splitOrder() throws IOException, PosServiceException {
        login("1","acewill");

        SplitOrderRequest request = new SplitOrderRequest();

        //{"oid":"34","tid":"2","newoid":"40","items":[{"oiid":"189","amount":"1"}]}
        request.setOid("34");
        request.setTid("2");
        request.setNewoid("40");

        List<SplitOrderRequest.Item> itemList = new ArrayList<>();
        SplitOrderRequest.Item item = new SplitOrderRequest.Item();
        item.setOiid("189");
        item.setAmount("1");
        itemList.add(item);

        request.setItems(itemList);

        assertTrue(service.splitOrder(request));
    }

    @Test
    public void appendToOrder() throws PosServiceException, IOException {
        login("1","acewill");
        AppendToOrderRequest request = new AppendToOrderRequest();

        //  {"accessid":"","orderidentity":1465189403,"mid":-1,"username":"1","pwd":"1","people":"7","tid":"2","tids":"2","ctid":"",
        //   * "saleuid":"","bid":"","omids":"","freememo":"","normalitems":[[0,"64","3",1,"26.00","","","","",[],false,1]],"setmeals":[],"freeitems":[]}
        request.setOid("40");
        request.setAccessid("");
        request.setOrderidentity(System.currentTimeMillis());
        request.setMid(-1);
        request.setUsername("1");
        request.setPwd("1");
        request.setPeople("7");
        request.setTid("2");
        request.setTids("2");
        request.setCtid("");
        request.setSaleuid("");
        request.setBid("");
        request.setCtid("");
        request.setFreememo("");

        CreateOrderRequest.OrderItemRequest or = new CreateOrderRequest.OrderItemRequest();
        or.setBwait(0);
        or.setDid("64");
        or.setDuid("3");
        or.setAmount(1);
        or.setPrice("26.00");
        or.setAssistduid("");
        or.setAssistamount("");
        or.setOmids("");
        or.setFreememo("");
        or.setBgift(false);
        or.setUnknown(1);

        List<List<Object>> items = new ArrayList<>() ;
        items.add(or.toArray());

        request.setNormalitems(items);

        Order order = service.appendToOrder(request);
        assertNotNull(order);
    }
}
