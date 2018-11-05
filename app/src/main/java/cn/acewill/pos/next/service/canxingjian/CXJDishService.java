package cn.acewill.pos.next.service.canxingjian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.acewill.pos.next.factory.RetrofitFactory;
import cn.acewill.pos.next.model.dish.Dish;
import cn.acewill.pos.next.service.canxingjian.retrofit.CXJRetrofitDishService;
import cn.acewill.pos.next.service.canxingjian.retrofit.message.AllDishResponse;

/**
 * Created by Acewill on 2016/6/3.
 */
public class CXJDishService {
    private CXJServerUrl cxjServerUrl;
    CXJRetrofitDishService internalService;

    public CXJDishService() {
        cxjServerUrl = new CXJServerUrl();
        internalService = RetrofitFactory.buildService(cxjServerUrl.getBaseUrl(), CXJRetrofitDishService.class);
    }

    public List<Long> getSoldOutDishIds() throws IOException {
        List<Long> soldOutDishIdList = new ArrayList<>();

        CXJRetrofitDishService.SoldOutDishResponse response = internalService.getSoldOutDishIds().execute().body();
        for (String id: response.dids.split(",")) {
            if (id.length() > 0) {
                soldOutDishIdList.add(Long.parseLong(id));
            }

        }
        return soldOutDishIdList;
    }


    public List<Dish> getAllDishs() throws IOException {
        AllDishResponse response = internalService.getAllDishs().execute().body();

        List<Dish> dishList = new ArrayList<>();

        if (response.success) {
            for (List<Object>  d : response.data) {
                Dish dish = new Dish();
//                dish.setId(Long.parseLong(d.get(0).toString()));

//                 dish.setSummary(summary);
                dishList.add(dish);
            }
        }

        return dishList;

    }
}
