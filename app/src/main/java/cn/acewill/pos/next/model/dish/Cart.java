package cn.acewill.pos.next.model.dish;

import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.acewill.pos.next.common.DishDataController;
import cn.acewill.pos.next.common.DishOptionController;
import cn.acewill.pos.next.common.MarketDataController;
import cn.acewill.pos.next.config.MyApplication;
import cn.acewill.pos.next.config.Store;
import cn.acewill.pos.next.model.FetchOrder;
import cn.acewill.pos.next.model.MarketObject;
import cn.acewill.pos.next.model.MarketType;
import cn.acewill.pos.next.model.OrderStatus;
import cn.acewill.pos.next.model.event.PosEvent;
import cn.acewill.pos.next.model.order.Order;
import cn.acewill.pos.next.model.order.OrderItem;
import cn.acewill.pos.next.model.order.PaymentStatus;
import cn.acewill.pos.next.service.PosInfo;
import cn.acewill.pos.next.utils.Constant;
import cn.acewill.pos.next.utils.PriceUtil;
import cn.acewill.pos.next.utils.TimeUtil;
import cn.acewill.pos.next.utils.ToolsUtils;

/**
 * Created by DHH on 2016/7/21.
 */
public class Cart {
    private static Cart cart = new Cart();

    public static Cart getInstance() {
        return cart;
    }

    private Cart() {
    }

    public static String tid = "";
    public static float alreadyPaidMoney = (float) 0.0;// 已经通过团购券，或微信支付的钱
    public static float dmValue = (float) 0.0;// 所使用的代金券金额
    public static String subtraction = "0";
    public static String discount = "1";
    public static List<DishCount> dishCountsList = new ArrayList<DishCount>();
    private static List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    public static List<Dish> getDishItemList() {
        return dishItemList;
    }

    public static List<Dish> getDishItemMarketActList() {
        dishItemList = MarketDataController.adapterMarket(dishItemList, getPriceSum());
        return dishItemList;
    }

    /**
     * 切换到外卖时  菜品price要取菜品外卖价格 waimaiPrice
     */
    public static void switchWaiMaiPrice() {
        if (!ToolsUtils.isList(dishItemList)) {
            int size = dishItemList.size();
            for (int i = 0; i < size; i++) {
                Dish dish = dishItemList.get(i);
                dish.setPrice(dish.getWaimaiTempPrice());
                dish.setCost(dish.getWaimaiTempPrice());
            }
        }
        notifyContentChange();
    }

    /**
     * 切换到堂食时  菜品price要切换回菜品的原价 price
     */
    public static void switchEATINPrice() {
        if (!ToolsUtils.isList(dishItemList)) {
            int size = dishItemList.size();
            for (int i = 0; i < size; i++) {
                Dish dish = dishItemList.get(i);
                dish.setPrice(dish.getTemporaryPrice());
                dish.setCost(dish.getTemporaryPrice());
            }
        }
        notifyContentChange();
    }

    //    public static void setDishItemTempMarketActList() {
    //        MarketDataController.adapterTempMarket(dishItemList);
    //        notifyContentChange();
    //    }

    public static void setDishItemList(List<Dish> dishItemLists) {
        dishItemList = dishItemLists;
        if (!ToolsUtils.isList(dishItemList)) {
            for (Dish dish : dishItemList) {
                dish.setTempQuantity(0);
                cleanAllOrderMarketState();
                DishDataController.addDishMark(dish.dishKind, dish);
            }
        }
    }

    /**
     * 已点的菜品
     */
    public static List<Dish> dishItemList = new CopyOnWriteArrayList<>();

    //挂单中的订单
    public static List<FetchOrder> handDishItemList = new CopyOnWriteArrayList<>();

    /**
     * 将需要挂单的菜品列表加入到map中
     *
     * @param dishList
     */
    public static void handDishList(List<Dish> dishList) {
        if (dishList != null && dishList.size() > 0) {
            FetchOrder fetchOrder = new FetchOrder();
            fetchOrder.setCreateOrderTime(TimeUtil.getTimeStr(System.currentTimeMillis()));
            fetchOrder.setFetchDishList(ToolsUtils.cloneTo(dishList));
            handDishItemList.add(fetchOrder);
            dishItemList.clear();
            EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));//清空点菜列表数据
        }
    }

    /**
     * 清空挂单列表
     */
    public static void cleanHandDishList() {
        if (handDishItemList != null && handDishItemList.size() > 0) {
            handDishItemList.clear();
            EventBus.getDefault().post(new PosEvent(Constant.EventState.CLEAN_CART));//清空点菜列表数据
        }
    }


    public interface ChangeListener {
        public void contentChanged();
    }

    @SuppressWarnings( "unchecked" )
    public String getItemNameByDids(ArrayList<DishCount> dids, List<Dish> dishItemList) {
        ArrayList<DishCount> clone = (ArrayList<DishCount>) dids.clone();
        StringBuilder sb = new StringBuilder();
        for (DishCount item : clone) {
            for (Dish dish : dishItemList) {
                if (dish.isPackage()) {
                    for (Dish.Package aPackage : dish.subItemList) {
                        if (item.dishid == aPackage.dishId) {
                            sb.append(aPackage.dishName + " /剩 " + item.getCount() + " 份 ,");
                            DishDataController.getDish(aPackage.dishId, -1).dishCount = item.count;
                        }
                    }
                } else {
                    if (item.dishid == dish.dishId) {
                        sb.append(dish.dishName + " /剩 " + item.getCount() + " 份 ,");
                        DishDataController.getDish(dish.dishId, -1).dishCount = item.count;
                        break;
                    }
                }

            }
        }
        return sb.substring(0, sb.length());
    }

    /**
     * 通过dishId返回菜品
     *
     * @param dishid 菜品Id
     * @return 菜品
     */
    public static Dish getItemByDid(int dishid) {
        for (Dish dish : dishItemList) {
            if (dish.getDishId() == dishid) {
                return dish;
            }
        }
        return null;
    }

    public void selectDishPackage(int position, Dish dish) {
        if (dish != null && dish.subItemList != null && dish.subItemList.size() > 0) {
            Dish.Package dishPackage = dish.subItemList.get(position);
            if (dishPackage.optionCategoryList != null && dishPackage.optionCategoryList.size() > 0) {
                dishPackage.optionList = (ArrayList) DishOptionController.getDishOption(dish, dishPackage);
                //                if (getOptionPackageMap() != null && getOptionPackageMap().size() > 0) {
                //                    ArrayList<Option> options = new ArrayList<Option>();
                //                    int size = optionPackageList.size();
                //                    for (int i = 0; i < size; i++) {
                //                        options.add(Cart.optionPackageList.get(i));
                //                    }
                //                    dishPackage.optionList = options;
                //                }
            }
        }
        //        DishDataController.reduceDishMark(dish.dishKind, dish);
        notifyContentChange();
    }

    public void selectCount(int position, int count, int takeOutCurrent, int current, int current_sp) {
        Dish dish = dishItemList.get(position);
        dish.setTempQuantity(dish.getQuantity());
        dish.quantity = count;
        if (dish.haveOptionCategory()) {
            dish.optionList = (ArrayList) DishOptionController.getDishOption(dish, null);
            //            if (getOptionMap() != null && getOptionMap().size() > 0) {
            //                ArrayList<Option> options = new ArrayList<Option>();
            //                int size = optionList.size();
            //                for (int i = 0; i < size; i++) {
            //                    options.add(Cart.optionList.get(i));
            //                }
            //                dish.optionList = options;
            //            }
        }
        if (current_sp >= 0) {
            if (dish.getSpecificationList() != null && dish.getSpecificationList().size() > 0) {
                dish.current_selectSpecifications = current_sp;
                BigDecimal price = dish.getSpecificationList().get(current_sp).getPrice();
                String name = dish.getSpecificationList().get(current_sp).getName();
                dish.setPrice(price);
                dish.setTempPrice(price);
                dish.cost = price;
                dish.setDishName(dish.getDishName() + "( " + name + " )");
            }
        }
        if (takeOutCurrent >= 0) {
            if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                dish.current_selectTakeOut = takeOutCurrent;
            }
        }
        if (current >= 0) {
            if (dish.dishDiscount != null && dish.dishDiscount.size() > 0) {
                BigDecimal cost = dish.getOrderDishCost().subtract(dish.getAllOrderDisCountSubtractPrice());
                if (current == dish.dishDiscount.size()) {
                    dish.current_select = current;
                    dish.cost = cost;
                    dish.setTempPrice(cost);
                } else {
                    dish.current_select = current;
                    DishDiscount discount = dish.dishDiscount.get(current);
                    BigDecimal discountCost = new BigDecimal(discount.discountPrice);
                    dish.setOnlyCost(discountCost);
                    dish.setTempPrice(discountCost);
                    if (dish.getMarketList() == null) {
                        dish.marketList = new ArrayList<>();
                    } else {
                        dish.setSingleOrderDisCountSubtracePrice(new BigDecimal("0.00"));
                        ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.MANUAL);//如果菜品中已经选择了手动打折
                    }
                    //                    if(dish.getDishPrice().subtract(discountCost).compareTo(BigDecimal.ZERO) > 0){
                    BigDecimal reduceCash = (cost.subtract(discountCost)).multiply(new BigDecimal(dish.getQuantity())).setScale(3, BigDecimal.ROUND_HALF_UP);
                    if(reduceCash.compareTo(BigDecimal.ZERO) == 1)
                    {
                        dish.setSingleOrderDisCountSubtracePrice(cost.subtract(discountCost));
                        MarketObject marketObject = new MarketObject("手动" + discount.getName(), (cost.subtract(discountCost)).multiply(new BigDecimal(dish.getQuantity())).setScale(3, BigDecimal.ROUND_HALF_UP), MarketType.MANUAL);
                        dish.marketList.add(marketObject);
                    }
                    //                    }
                }
            }
        }
        DishDataController.reduceDishMark(dish.dishKind, dish);
        notifyContentChange();
    }

    public int addItem(Dish dish) {
        if (dish.isPackage()) {
        } else if (dish.haveOptionCategory()) {
            // TODO 含有定制项
        } else {
            for (Dish item : dishItemList) {
                if (item.getDishId() == dish.getDishId()) {
                    item.setTempQuantity(item.getQuantity());
                    cleanAllOrderMarketState();
                    item.quantity++;
                    DishDataController.addDishMark(item.dishKind, item);
                    this.notifyContentChange();
                    return item.quantity;
                }
            }
        }
        Dish dishModel = new Dish(dish);
        //dishModel.setTempQuantity(dish.getQuantity());
        dishModel.setTempQuantity(0);
        cleanAllOrderMarketState();
        dishItemList.add(dishModel);
        DishDataController.addDishMark(dishModel.dishKind, dishModel);
        ToolsUtils.writeUserOperationRecords("TIME===>菜品选中时间==" + TimeUtil.getStringTimeLong(System.currentTimeMillis()) + "===选中了(" + dishModel.getDishName() + ")菜品,并将其加入购物车" + "==>>" + ToolsUtils.getPrinterSth(dishModel));
        this.notifyContentChange();
        return 1;
    }

    public int addItem(Dish dish, boolean isNotify) {
        if (dish.isPackage()) {
        } else if (dish.haveOptionCategory()) {
            // TODO 含有定制项
        } else {
            for (Dish item : dishItemList) {
                if (item.getDishId() == dish.getDishId()) {
                    item.setTempQuantity(item.getQuantity());
                    cleanAllOrderMarketState();
                    item.quantity = item.quantity + dish.quantity;
                    DishDataController.addDishMark(item.dishKind, item);
                    ToolsUtils.writeUserOperationRecords("菜品加一份时间:" + TimeUtil.getStringTimeLong(System.currentTimeMillis()) + "增加了(" + item.getDishName() + ")菜品");
                    this.notifyContentChange();
                    return item.quantity;
                }
            }
        }
        Dish dishModel = new Dish(dish);
        //        dishModel.setTempQuantity(dish.getQuantity());
        dishModel.setTempQuantity(0);
        cleanAllOrderMarketState();
        dishItemList.add(dishModel);
        DishDataController.addDishMark(dishModel.dishKind, dishModel);
        ToolsUtils.writeUserOperationRecords("菜品选中时间==" + TimeUtil.getStringTimeLong(System.currentTimeMillis()) + "===选中了(" + dishModel.getDishName() + ")菜品,并将其加入购物车" + "==>>" + ToolsUtils.getPrinterSth(dishModel));
        if (isNotify) {
            this.notifyContentChange();
        }
        return 1;
    }


    /**
     * 加一份
     *
     * @param position
     * @return
     */
    public int addItem(int position) {
        Dish dish = dishItemList.get(position);
        int i = ++dish.quantity;

        DishDataController.addDishMark(dish.dishKind, dish);
        this.notifyContentChange();
        return i;
    }

    public int reduceItem(int position) {
        Dish dish = dishItemList.get(position);
        dish.setTempQuantity(dish.getQuantity());
        cleanAllOrderMarketState();
        int i = --dish.quantity;
        if (i == 0) {
            dishItemList.remove(position);
        }
        DishDataController.reduceDishMark(dish.dishKind, dish);
        notifyContentChange();
        return i;
    }

    /**
     * 减一份
     *
     * @param dish
     * @return 剩余数量
     */
    public int reduceItem(Dish dish) {
        for (Dish item : dishItemList) {
            if (item.getDishId() == dish.getDishId()) {
                if (item.quantity > 0) {
                    item.quantity--;
                }
                if (item.quantity == 0) {
                    dishItemList.remove(item);
                }
                this.notifyContentChange();
                return item.quantity;
            }
        }
        this.notifyContentChange();
        return 0;
    }

    /**
     * 移除菜品
     *
     * @param dish
     */
    public static void removeItem(Dish dish) {
        for (Dish item : dishItemList) {
            cleanAllOrderMarketState();
            if (item.getDishId() == dish.getDishId()) {
                ToolsUtils.writeUserOperationRecords("移除了(" + dish.getDishName() + ")菜品");
                dishItemList.remove(item);
                DishDataController.removeDishMark(dish.dishKind, dish);
                break;
            }
        }
        notifyContentChange();
    }

    public static void removeItem(Dish dish, int position) {
        cleanAllOrderMarketState();
        ToolsUtils.writeUserOperationRecords("移除了(" + dish.getDishName() + ")菜品");
        dishItemList.remove(position);
        DishDataController.removeDishMark(dish.dishKind, dish);
        notifyContentChange();
    }

    public static void removeItem2(Dish dish) {
        for (Dish item : dishItemList) {
            if (item.getDishId() == dish.getDishId() && item.isGiftDish) {
                ToolsUtils.writeUserOperationRecords("移除了(" + dish.getDishName() + ")菜品");
                dishItemList.remove(item);
                //                DishDataController.removeDishMark(dish.dishKind, dish);
                break;
            }
        }
    }


    public static void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public static void notifyContentChange() {
        for (ChangeListener listener : listeners) {
            listener.contentChanged();
        }
    }

    /**
     * 获取总份数
     *
     * @return
     */
    public int getQuantity() {
        int count = 0;
        for (int i = 0; i < dishItemList.size(); i++) {
            count += dishItemList.get(i).quantity;
        }
        return count;
    }

    /**
     * 获取已点菜品个数
     *
     * @return
     */
    public int getDishCount() {
        return dishItemList.size();
    }

    public void remove(Dish dishModel) {
        dishItemList.remove(dishModel);
        notifyContentChange();
    }

    public int add(Dish mDishModel) {
        notifyContentChange();
        return addItem(mDishModel);
    }

    public void deleteItem(int position) {
        dishItemList.remove(position);
        notifyContentChange();
    }

    /**
     * 获取已点菜品项列表
     *
     * @return
     */
    public static List<Dish> getAllOrderDish() {
        return dishItemList;
    }

    /**
     * 清空购物车中的数据
     */
    public static void cleanDishList() {
        if (dishItemList != null) {
            dishItemList.clear();
        }
    }

    //    /**
    //     * @return 总价
    //     */
    //    public String getTotalPrice() {
    //        BigDecimal bigDecimal = new BigDecimal("0.00");
    //        for (Dish goodsModel : dishItemList) {
    ////            BigDecimal bigDecimal2 = new BigDecimal(PriceUtil.formatPrice(goodsModel.getCost()) + "");
    //            BigDecimal bigDecimal2 = new BigDecimal(goodsModel.getCost()+"");
    //            BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
    //            bigDecimal = bigDecimal.add(bigDecimal2.multiply(bigDecimal3));
    //        }
    //        return bigDecimal.toString();
    //    }

    /**
     * 总价(应付)
     *
     * @return
     */
    public static float getPriceSum() {
        BigDecimal bigDecimal = new BigDecimal("0.000");
        for (Dish goodsModel : dishItemList) {
            BigDecimal bigDecimal2 = new BigDecimal(PriceUtil.formatPrice(goodsModel.getTotalPrice()) + "");
            BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
            bigDecimal = bigDecimal.add(bigDecimal2.multiply(bigDecimal3));

            //计算外带打包费用
            if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                if (goodsModel.getWaiDai_cost() != null) {
                    PosInfo.getInstance().setAddWaiMaiMoney(true);
                    bigDecimal = bigDecimal.add(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
                }
            } else {
                if (goodsModel.getWaiDai_cost() != null && PosInfo.getInstance().isAddWaiMaiMoney()) {
                    bigDecimal = bigDecimal.subtract(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
                }
            }
        }
        if (bigDecimal.floatValue() == 0) {
            return 0;
        }

        if ("SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
            bigDecimal = bigDecimal.add(new BigDecimal(Store.getInstance(MyApplication.getContext()).getSaleMoney()));
        }
        float discountPrice = bigDecimal.floatValue();
//        float discountPrice = DishDataController.getDiscountPrice(bigDecimal.floatValue());
        return discountPrice > 0 ? discountPrice : 0;
    }

    /**
     * 总价(应付)
     *
     * @return
     */
    public static float getPriceSumXtt() {
        BigDecimal bigDecimal = new BigDecimal("0.000");
        for (Dish goodsModel : dishItemList) {
            BigDecimal bigDecimal2 = new BigDecimal(PriceUtil.formatPrice(goodsModel.getTotalPriceXtt()) + "");
            BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
            bigDecimal = bigDecimal.add(bigDecimal2.multiply(bigDecimal3));
        }
        if (bigDecimal.floatValue() == 0) {
            return 0;
        }

        float discountPrice = DishDataController.getDiscountPrice(bigDecimal.floatValue());
        return discountPrice > 0 ? discountPrice : 0;
    }

    /**
     * 实付
     *
     * @return
     */
    public static float getCost() {
        BigDecimal bigDecimal = new BigDecimal("0.000");
        BigDecimal allOrderDisCountSubtractPrice = new BigDecimal("0.000");//通过全单打折每份菜品最后需要减掉的钱
        BigDecimal singleOrderDisCountSubtractPrice = new BigDecimal("0.000");//通过单品打折每份菜品最后需要减掉的钱
        for (Dish goodsModel : dishItemList) {
            BigDecimal dishCost = new BigDecimal("0.00");
            allOrderDisCountSubtractPrice = allOrderDisCountSubtractPrice.add(goodsModel.getAllOrderDisCountSubtractPrice().multiply(new BigDecimal(goodsModel.getQuantity())));
            singleOrderDisCountSubtractPrice = singleOrderDisCountSubtractPrice.add(goodsModel.getSingleOrderDisCountSubtracePrice().multiply(new BigDecimal(goodsModel.getQuantity())));
            dishCost = goodsModel.getOrderDishCost();
            BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
            bigDecimal = bigDecimal.add(dishCost.multiply(bigDecimal3));
            //计算外带打包费用
            if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                if (goodsModel.getWaiDai_cost() != null) {
                    bigDecimal = bigDecimal.add(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
                }
            }
            //            else {
            //                if (goodsModel.getWaiDai_cost() != null && PosInfo.getInstance().isAddWaiMaiMoney()) {
            ////                    if(goodsModel.isAddIngWaiMaiCost())
            ////                    {
            ////                        bigDecimal = bigDecimal.subtract(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
            ////                        goodsModel.setAddIngWaiMaiCost(false);
            ////                    }
            //                }
            //            }
        }
        if (bigDecimal.floatValue() == 0) {
            return 0;
        }

        if ("SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
            bigDecimal = bigDecimal.add(new BigDecimal(Store.getInstance(MyApplication.getContext()).getSaleMoney()));
        }
        bigDecimal = bigDecimal.subtract(allOrderDisCountSubtractPrice).subtract(singleOrderDisCountSubtractPrice);
        float discountPrice = bigDecimal.floatValue();
        //        float discountPrice = DishDataController.getDiscountPrice(bigDecimal.floatValue());
        //        int price = (int)discountPrice;
        //        discountPrice = price;
        return discountPrice > 0 ? discountPrice : 0;
    }

    /**
     * 获取打包费
     *
     * @return
     */
    public static BigDecimal getTakeMoney() {
        BigDecimal bigDecimal = new BigDecimal("0.000");
        for (Dish goodsModel : dishItemList) {
            BigDecimal bigDecimal3 = new BigDecimal(goodsModel.quantity);
            //计算外带打包费用
            if ("TAKE_OUT".equals(PosInfo.getInstance().getOrderType()) || "SALE_OUT".equals(PosInfo.getInstance().getOrderType())) {
                if (goodsModel.getWaiDai_cost() != null) {
                    bigDecimal = bigDecimal.add(bigDecimal3.multiply(goodsModel.getWaiDai_cost()));
                }
            }
        }
        return bigDecimal;
    }

    public void clear() {
        // for (DishModel iterable_element : items) {
        // iterable_element.count = 0;
        // if(iterable_element.tastes!=null){
        // iterable_element.tastes.clear();
        // }
        // iterable_element.remarks = "";
        // iterable_element.setmealIndex = -1;
        // }
        if(dishItemList != null)
        {
            dishItemList.clear();
        }
        // orderNo = "";
        tid = "";
        //        this.paymentInfo = null;

        alreadyPaidMoney = 0;
        dmValue = 0;
        discount = "1";
        subtraction = "0";

        this.notifyContentChange();
    }

    public Order getOrderItem(List<Dish> mDishs, Order tableOrder, boolean isTableOrder, OrderStatus orderStatus) {
        Order order = new Order();
        PosInfo posInfo = PosInfo.getInstance();
        if (mDishs != null) {
            if (orderStatus != null && orderStatus == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.PENDING);
            }
            order.setTotal(getPriceSum() + "");
            order.setCost("0");
            order.setCreatedAt(System.currentTimeMillis());
            order.setSource(posInfo.getTerminalName());
            order.setDiscount(1);
            order.setCreatedBy(posInfo.getRealname());
            order.setSubtraction(0);
            order.setComment("");
            order.setOrderType(posInfo.getOrderType());
            if (posInfo.getOrderType() == "TAKE_OUT")//如果订单是外卖
            {
                order.setWaimaiType(posInfo.getWaimaiType());
            }
            order.setMenuName(getMenuName());//获取当前菜品餐段名称
            order.setPaymentStatus(PaymentStatus.NOT_PAYED);
            if (tableOrder != null) {
                order.setTableIds(tableOrder.getTableIds());
                order.setTableNames(tableOrder.getTableNames());
                order.setCustomerAmount(Integer.valueOf(tableOrder.getCustomerAmount()));
            }
            int dishSize = mDishs.size();
            List<OrderItem> itemList = new ArrayList<>();
            for (int i = 0; i < dishSize; i++) {
                OrderItem item = new OrderItem();
                Dish dish = mDishs.get(i);
                item.setId(TimeUtil.getOrderItemId(i));
                item.setOptionList(dish.optionList);
                item.setDishId(dish.getDishId());
                item.setDishName(dish.getDishName());
                item.setPrice(dish.getPrice());
                item.setDishUnit(dish.getDishUnit());
                item.setImageName(dish.getImageName());
                item.setWaiDai_cost(dish.getWaiDai_cost());
                //                item.setCost(dish.getPrice()*order.getDiscount());
                //                BigDecimal b1 = new BigDecimal(dish.getTotalPrice()+"");
                //                BigDecimal b2 = new BigDecimal(order.getDiscount());
                item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
                item.setQuantity(dish.quantity);
                item.setComment(dish.comment);
                item.setCookList(dish.cookList);
                item.setTasteList(dish.tasteList);
                item.setMenuName(getMenuName());//获取当前菜品餐段名称
                item.setDishUnitID(dish.getDishUnitID());
                item.setSubItemList(dish.subItemList);
                item.setGift(false);
                item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
                item.setDishKind(dish.dishKind);
                item.setMarketList(dish.getMarketList());
                item.setTempMarketList(dish.getTempMarketList());
                item.setDiscounted(dish.getDiscounted());
                itemList.add(item);
            }
            order.setItemList(itemList);
        }
        return order;
    }

    public Order getOrderItem(Order tableOrder, List<Dish> mDishs) {
        Order order = new Order();
        PosInfo posInfo = PosInfo.getInstance();
        if (mDishs != null) {
            order.setTotal(getPriceSum() + "");
            order.setCost("0");
            order.setCreatedAt(System.currentTimeMillis());
            order.setSource(posInfo.getTerminalName());
            order.setDiscount(1);
            order.setCreatedBy(posInfo.getRealname());
            if (tableOrder != null) {
                order.setTableNames(tableOrder.getTableNames());
                order.setTableIds(tableOrder.getTableIds());
                order.setCustomerAmount(Integer.valueOf(tableOrder.getCustomerAmount()));
            }
            if (posInfo.getOrderType() == "TAKE_OUT")//如果订单是外卖
            {
                order.setWaimaiType(posInfo.getWaimaiType());
            }
            order.setMenuName(getMenuName());//获取当前菜品餐段名称
            order.setSubtraction(0);
            order.setComment("");
            order.setOrderType(posInfo.getOrderType());
            order.setPaymentStatus(PaymentStatus.NOT_PAYED);
            int dishSize = mDishs.size();
            List<OrderItem> itemList = new ArrayList<>();
            for (int i = 0; i < dishSize; i++) {
                OrderItem item = new OrderItem();
                Dish dish = mDishs.get(i);
                item.setId(TimeUtil.getOrderItemId(i));
                item.setOptionList(dish.optionList);
                item.setDishId(dish.getDishId());
                item.setDishName(dish.getDishName());
                item.setPrice(dish.getPrice());
                item.setDishUnit(dish.getDishUnit());
                item.setImageName(dish.getImageName());
                item.setWaiDai_cost(dish.getWaiDai_cost());
                //                item.setCost(dish.getPrice()*order.getDiscount());
                //                BigDecimal b1 = new BigDecimal(dish.getTotalPrice()+"");
                //                BigDecimal b2 = new BigDecimal(order.getDiscount());
                item.setMenuName(getMenuName());//获取当前菜品餐段名称
                item.setQuantity(dish.quantity);
                item.setComment(dish.comment);
                item.setCookList(dish.cookList);
                item.setTasteList(dish.tasteList);
                item.setDishUnitID(dish.getDishUnitID());
                item.setSubItemList(dish.subItemList);
                item.setIsPackage(dish.getIsPackage());
                item.setGift(false);
                item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
                item.setDishKind(dish.dishKind);
                item.setMarketList(dish.getMarketList());
                item.setTempMarketList(dish.getTempMarketList());
                item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
                item.setDiscounted(dish.getDiscounted());
                itemList.add(item);
            }
            order.setItemList(itemList);
        }
        return order;
    }

    public Order getNetOrderItem(List<Dish> mDishs, Order tableOrder) {
        Order order = new Order();
        PosInfo posInfo = PosInfo.getInstance();
        if (mDishs != null) {
            order.setTotal(tableOrder.getTotal());
            order.setCost(tableOrder.getCost());
            order.setCreatedAt(System.currentTimeMillis());
            order.setSource(tableOrder.getSource());
            order.setNetOrderid(tableOrder.getNetOrderid());
            order.setDiscount(tableOrder.getDiscount());
            order.setCreatedBy(posInfo.getRealname());
            order.setSubtraction(tableOrder.getSubtraction());
            order.setComment(tableOrder.getComment());
            order.setOrderType(tableOrder.getOrderType());
            if (posInfo.getOrderType() == "TAKE_OUT")//如果订单是外卖
            {
                order.setWaimaiType(posInfo.getWaimaiType());
            }
            order.setMenuName(getMenuName());//获取当前菜品餐段名称
            order.setPaymentStatus(tableOrder.getPaymentStatus());
            order.setCustomerName(tableOrder.getCustomerName());
            order.setCustomerPhoneNumber(tableOrder.getCustomerPhoneNumber());
            order.setCustomerAddress(tableOrder.getCustomerAddress());
            if (tableOrder != null) {
                order.setTableIds(tableOrder.getTableIds());
                order.setTableNames(tableOrder.getTableNames());
                order.setCustomerAmount(Integer.valueOf(tableOrder.getCustomerAmount()));
            }
            int dishSize = mDishs.size();
            List<OrderItem> itemList = new ArrayList<>();
            for (int i = 0; i < dishSize; i++) {
                OrderItem item = new OrderItem();
                Dish dish = mDishs.get(i);
                item.setId(TimeUtil.getOrderItemId(i));
                item.setOptionList(dish.optionList);
                item.setDishId(dish.getDishId());
                item.setDishName(dish.getDishName());
                item.setPrice(dish.getPrice());
                item.setDishUnit(dish.getDishUnit());
                item.setImageName(dish.getImageName());
                item.setWaiDai_cost(dish.getWaiDai_cost());
                //                item.setCost(dish.getPrice()*order.getDiscount());
                //                BigDecimal b1 = new BigDecimal(dish.getTotalPrice()+"");
                //                BigDecimal b2 = new BigDecimal(order.getDiscount());
                item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
                item.setQuantity(dish.quantity);
                item.setComment(dish.comment);
                item.setCookList(dish.cookList);
                item.setTasteList(dish.tasteList);
                item.setMenuName(getMenuName());//获取当前菜品餐段名称
                item.setDishUnitID(dish.getDishUnitID());
                item.setSubItemList(dish.subItemList);
                item.setGift(false);
                item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
                item.setDishKind(dish.dishKind);
                item.setMarketList(dish.getMarketList());
                item.setTempMarketList(dish.getTempMarketList());
                item.setDiscounted(dish.getDiscounted());
                itemList.add(item);
            }
            order.setItemList(itemList);
        }
        return order;
    }

    public List<OrderItem> getTableOrderItem(List<Dish> mDishs) {
        List<OrderItem> itemList = new ArrayList<>();
        if (mDishs != null) {
            int dishSize = mDishs.size();
            for (int i = 0; i < dishSize; i++) {
                OrderItem item = new OrderItem();
                Dish dish = mDishs.get(i);
                item.setId(TimeUtil.getOrderItemId(i));
                item.setOptionList(dish.optionList);
                item.setDishId(dish.getDishId());
                item.setDishName(dish.getDishName());
                item.setPrice(dish.getPrice());
                item.setDishUnit(dish.getDishUnit());
                item.setImageName(dish.getImageName());
                item.setCreatedAt(String.valueOf(System.currentTimeMillis()));
                item.setWaiDai_cost(dish.getWaiDai_cost());

                item.setCost((dish.getDishRealCost()).setScale(2, BigDecimal.ROUND_DOWN));
                item.setQuantity(dish.quantity);
                item.setComment(dish.comment);
                item.setCookList(dish.cookList);
                item.setTasteList(dish.tasteList);
                item.setDishUnitID(dish.getDishUnitID());
                item.setSubItemList(dish.subItemList);
                item.setIsPackage(dish.getIsPackage());
                item.setGift(false);
                item.setDishKindStr(DishDataController.getKindNameById(dish.dishKind));
                item.setDishKind(dish.dishKind);
                item.setMarketList(dish.getMarketList());
                item.setTempMarketList(dish.getTempMarketList());
                item.setDiscounted(dish.getDiscounted());
                itemList.add(item);
            }
        }
        return itemList;
    }

    public static List<OrderItem> getRetreatItem(OrderItem orderItem) {
        List<OrderItem> itemList = new ArrayList<>();
        itemList.add(orderItem);
        return itemList;
    }

    /**
     * 检测购物车中是否有菜品记录
     *
     * @return
     */
    public static boolean isCartDishNull() {
        if (dishItemList != null && dishItemList.size() > 0) {
            return false;
        }
        return true;
    }

    public static List<FetchOrder> getHandDishItemList() {
        return handDishItemList;
    }

    public static void setHandDishItemList(List<FetchOrder> handDishItemList) {
        Cart.handDishItemList = handDishItemList;
    }

    public static void removeHandDishList(int position) {
        if (!isCartDishNull() && handDishItemList.get(position) != null) {
            handDishItemList.remove(position);
            notifyContentChange();
        }
    }

    /**
     * 清除全单打折状态  清除单个菜打折状态
     */
    public static void cleanAllOrderMarketState() {
        List<Dish> dishItemList = getDishItemList();
        if (dishItemList != null && dishItemList.size() > 0) {
            for (Dish dish : dishItemList) {
                dish.setAllOrderDisCountSubtractPrice(new BigDecimal("0.00"));
                ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.DISCOUNT);

                dish.setSingleOrderDisCountSubtracePrice(new BigDecimal("0.00"));
                ToolsUtils.removeItemForMarkType(dish.getMarketList(),MarketType.MANUAL);
                dish.current_select = 0;
            }
        }
    }

    private String getMenuName() {
        Menu menu = DishDataController.getDishs(DishDataController.DISH_TYPE);
        if (menu != null && !TextUtils.isEmpty(menu.getTimeName())) {
            return menu.getTimeName();
        }
        return "未知餐段";
    }


}
