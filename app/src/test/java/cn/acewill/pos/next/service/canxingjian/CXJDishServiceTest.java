package cn.acewill.pos.next.service.canxingjian;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import cn.acewill.pos.next.factory.CookieInterceptor;
import cn.acewill.pos.next.model.dish.Dish;

import static org.junit.Assert.assertEquals;

/**
 * Created by Acewill on 2016/6/3.
 */
@Ignore
public class CXJDishServiceTest {

    CXJDishService service;
    @Before
    public void setup() {
        service = new CXJDishService();
    }

    @Test
    public void getSoldoutDishIdList() throws URISyntaxException, IOException {
        List<Long> tableList = service.getSoldOutDishIds();

        assertEquals(0, tableList.size());
    }

    @Test
    public void getAllDishs() throws URISyntaxException, IOException {

        CookieInterceptor.addCookie("q3cs7kt5bqtjie0ekqqre5eev6");
        List<Dish> dishList = service.getAllDishs();

        assertEquals(114,dishList.size());
    }


}
