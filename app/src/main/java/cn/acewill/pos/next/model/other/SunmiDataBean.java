package cn.acewill.pos.next.model.other;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.dish.Cart;
import cn.acewill.pos.next.model.dish.Option;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;

/**
 * 商米副屏数据封装(14寸客显屏)
 * Created by aqw on 2016/12/27.
 */
public class SunmiDataBean {

    public String title;
    public LinkedHashMap head;
    public List<LinkedHashMap> list;
    public List<LinkedHashMap> KVPList;

    /**
     * 封装副屏表格数据
     * @param order
     * @return
     */
    public static String getJson(Order order) {

        String jsonStr = "";

        if (order != null && order.getItemList() != null) {

            List<OrderItem> orderItemList = order.getItemList();

            //标题
            String title = Store.getInstance(MyApplication.getContext()).getStoreName()+"欢迎您";

            //head
            LinkedHashMap headMap = new LinkedHashMap();
            headMap.put("param1", "商品名");
            headMap.put("param2", "单价");
            headMap.put("param3", "数量");
            headMap.put("param4", "小结");

            //菜品项
            List<LinkedHashMap> listItem = new ArrayList<LinkedHashMap>();
            for (int i = 0; i < orderItemList.size(); i++) {
                LinkedHashMap itemMap = new LinkedHashMap();
//                BigDecimal dishOptionCost = getOptionListPrice(orderItemList.get(i).getOptionList());
                StringBuffer sb = new StringBuffer();
                List<Option> optionList = orderItemList.get(i).getOptionList();
                BigDecimal dishOptionCost = new BigDecimal("0.00");
                if(optionList != null && optionList.size() >0)
                {
                    for (Option option : optionList) {
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 1) {
                            dishOptionCost = dishOptionCost.add(option.getPrice());
                        }
                        if (option.getPrice().compareTo(new BigDecimal("0")) == 0) {
                            sb.append(option.name + ",");
                        } else {
                            sb.append(option.name + " (" + option.getPrice() + "￥)" + ",");
                        }
                    }
                }
                BigDecimal money = orderItemList.get(i).getCost();
                itemMap.put("param1", orderItemList.get(i).getDishName()+"  "+sb.toString());
//                itemMap.put("param2", String.format("%.2f ", money)+"");
                itemMap.put("param2", money.setScale(2,BigDecimal.ROUND_DOWN)+"");
                itemMap.put("param3", orderItemList.get(i).getQuantity()+"");
                if(money==null){
                    money = new BigDecimal(0);
                }
//                itemMap.put("param4",String.format("%.2f ", money.multiply(new BigDecimal(orderItemList.get(i).getQuantity()))) +"");
                itemMap.put("param4",money.multiply(new BigDecimal(orderItemList.get(i).getQuantity())).setScale(2,BigDecimal.ROUND_DOWN) +"");
                listItem.add(itemMap);
            }

            //KVP
            List<LinkedHashMap> lisKVP = new ArrayList<LinkedHashMap>();
            LinkedHashMap skMap = new LinkedHashMap();
            skMap.put("name", "应收");
//            skMap.put("value", "￥"+String.format("%.2f ", order.getTotal()));
            skMap.put("value", "￥"+new BigDecimal(order.getTotal()).setScale(2,BigDecimal.ROUND_DOWN));


            BigDecimal money = new BigDecimal(0);
            if(Cart.getInstance().getDishCount()>0){
                money = money.add(new BigDecimal(Cart.getInstance().getCost()).subtract(order.getAvtive_money()));
            }else{
                if(new BigDecimal(order.getCost()).compareTo(BigDecimal.ZERO)==1){
                    money = new BigDecimal(order.getCost()).subtract(order.getAvtive_money());
                }else {
                    money = new BigDecimal(order.getTotal()).subtract(order.getAvtive_money());
                }
            }

            LinkedHashMap yhMap = new LinkedHashMap();
            yhMap.put("name", "优惠");
//            yhMap.put("value", "￥"+order.getAvtive_money().setScale(2,BigDecimal.ROUND_DOWN)+"");
            yhMap.put("value", "￥"+new BigDecimal(order.getTotal()).subtract(money).setScale(2,BigDecimal.ROUND_DOWN)+"");

            LinkedHashMap hjMap = new LinkedHashMap();
            hjMap.put("name", "合计");
            hjMap.put("value", "￥"+money.setScale(2,BigDecimal.ROUND_DOWN));

            lisKVP.add(skMap);
            lisKVP.add(yhMap);
            lisKVP.add(hjMap);

            //封装对象
            SunmiDataBean bean = new SunmiDataBean();
            bean.title = title;
            bean.head = headMap;
            bean.list = listItem;
            bean.KVPList = lisKVP;

            Gson gson = new Gson();
            return gson.toJson(bean);

        }

        return jsonStr;
    }

    private static BigDecimal getOptionListPrice(List<Option> optionList)
    {
        BigDecimal dishOption = new BigDecimal("0.00");
        if(optionList != null && optionList.size() >0)
        {
            for (Option option : optionList) {
                if (option.getPrice().compareTo(new BigDecimal("0")) == 1) {
                    dishOption = dishOption.add(option.getPrice());
                }
            }
        }
        return dishOption;
    }

}
