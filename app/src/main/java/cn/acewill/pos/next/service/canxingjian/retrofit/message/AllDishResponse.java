package cn.acewill.pos.next.service.canxingjian.retrofit.message;

/**
 * Created by Acewill on 2016/6/6.
 */

import java.util.List;

//"1","11","1","\u732b\u5c71\u738b\u69b4\u69e4\u6bd4\u8428(9\u5bf8\uff09","mswllbsjc","1","1.00","0","-1","03011",null,"1","0","0","0","0",null,null,"1",null,null,"0"

public class AllDishResponse {
    public boolean success;
    public List<List<Object>> data;

    public static class DishResponse {
        public String dishId;
        public String diskKindId;//
        public String gkId;//财务统计id
        public String name; //菜名
        public String alias; // 拼音首字母
    }
}
