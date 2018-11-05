package cn.acewill.pos.bean;

import java.util.List;

/**
 * Created by DHH on 2016/6/3.
 */
public class AllTabStaOrdBean {

    /**
     * success : 1
     * orders : [{"orderinfo":{"oid":"29","people":"1","tids":"1","ctid":null,"newuid":"6","newtime":"2016-06-03 16:14:16","memo":null,"cost":536,"orderstatus":"2","total":536,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":536,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["173","2","1","0","6","2016-06-03 16:14:16","3",268,"1",1,null,null,[],"",null,"",268,268,0,null,"收银店员"],["174","2","1","0","6","2016-06-03 16:14:16","3",268,"1",1,null,null,[],"",null,"",268,268,0,null,"收银店员"]]}]
     */

    private GetAllTableOrderDetailBean getAllTableOrderDetail;
    /**
     * getTablesStatus : [["1","3","-1","2","0",536],["2","1","-1",-1,0,""]]
     * getAllTableOrderDetail : {"success":1,"orders":[{"orderinfo":{"oid":"29","people":"1","tids":"1","ctid":null,"newuid":"6","newtime":"2016-06-03 16:14:16","memo":null,"cost":536,"orderstatus":"2","total":536,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":536,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null},"orderitems":[["173","2","1","0","6","2016-06-03 16:14:16","3",268,"1",1,null,null,[],"",null,"",268,268,0,null,"收银店员"],["174","2","1","0","6","2016-06-03 16:14:16","3",268,"1",1,null,null,[],"",null,"",268,268,0,null,"收银店员"]]}]}
     */

    private List<List<String>> getTablesStatus;

    public GetAllTableOrderDetailBean getGetAllTableOrderDetail() {
        return getAllTableOrderDetail;
    }

    public void setGetAllTableOrderDetail(GetAllTableOrderDetailBean getAllTableOrderDetail) {
        this.getAllTableOrderDetail = getAllTableOrderDetail;
    }

    public List<List<String>> getGetTablesStatus() {
        return getTablesStatus;
    }

    public void setGetTablesStatus(List<List<String>> getTablesStatus) {
        this.getTablesStatus = getTablesStatus;
    }

    public static class GetAllTableOrderDetailBean {
        private int success;
        /**
         * orderinfo : {"oid":"29","people":"1","tids":"1","ctid":null,"newuid":"6","newtime":"2016-06-03 16:14:16","memo":null,"cost":536,"orderstatus":"2","total":536,"costservice":0,"extradiscount":0,"block":"0","maid":"","ordermarket":"","totaldish":536,"cardcode":"","discountmoney":0,"maling":0,"bookmoney":0,"paytype":null}
         * orderitems : [["173","2","1","0","6","2016-06-03 16:14:16","3",268,"1",1,null,null,[],"",null,"",268,268,0,null,"收银店员"],["174","2","1","0","6","2016-06-03 16:14:16","3",268,"1",1,null,null,[],"",null,"",268,268,0,null,"收银店员"]]
         */

        private List<OrdersBean> orders;

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public List<OrdersBean> getOrders() {
            return orders;
        }

        public void setOrders(List<OrdersBean> orders) {
            this.orders = orders;
        }

        public static class OrdersBean {
            /**
             * oid : 29
             * people : 1
             * tids : 1
             * ctid : null
             * newuid : 6
             * newtime : 2016-06-03 16:14:16
             * memo : null
             * cost : 536
             * orderstatus : 2
             * total : 536
             * costservice : 0
             * extradiscount : 0
             * block : 0
             * maid :
             * ordermarket :
             * totaldish : 536
             * cardcode :
             * discountmoney : 0
             * maling : 0
             * bookmoney : 0
             * paytype : null
             */

            private OrderinfoBean orderinfo;
            private List<List<String>> orderitems;

            public OrderinfoBean getOrderinfo() {
                return orderinfo;
            }

            public void setOrderinfo(OrderinfoBean orderinfo) {
                this.orderinfo = orderinfo;
            }

            public List<List<String>> getOrderitems() {
                return orderitems;
            }

            public void setOrderitems(List<List<String>> orderitems) {
                this.orderitems = orderitems;
            }

            public static class OrderinfoBean {
                private String oid;
                private String people;
                private String tids;
                private Object ctid;
                private String newuid;
                private String newtime;
                private Object memo;
                private int cost;
                private String orderstatus;
                private int total;
                private int costservice;
                private int extradiscount;
                private String block;
                private String maid;
                private String ordermarket;
                private int totaldish;
                private String cardcode;
                private int discountmoney;
                private int maling;
                private int bookmoney;
                private Object paytype;

                public String getOid() {
                    return oid;
                }

                public void setOid(String oid) {
                    this.oid = oid;
                }

                public String getPeople() {
                    return people;
                }

                public void setPeople(String people) {
                    this.people = people;
                }

                public String getTids() {
                    return tids;
                }

                public void setTids(String tids) {
                    this.tids = tids;
                }

                public Object getCtid() {
                    return ctid;
                }

                public void setCtid(Object ctid) {
                    this.ctid = ctid;
                }

                public String getNewuid() {
                    return newuid;
                }

                public void setNewuid(String newuid) {
                    this.newuid = newuid;
                }

                public String getNewtime() {
                    return newtime;
                }

                public void setNewtime(String newtime) {
                    this.newtime = newtime;
                }

                public Object getMemo() {
                    return memo;
                }

                public void setMemo(Object memo) {
                    this.memo = memo;
                }

                public int getCost() {
                    return cost;
                }

                public void setCost(int cost) {
                    this.cost = cost;
                }

                public String getOrderstatus() {
                    return orderstatus;
                }

                public void setOrderstatus(String orderstatus) {
                    this.orderstatus = orderstatus;
                }

                public int getTotal() {
                    return total;
                }

                public void setTotal(int total) {
                    this.total = total;
                }

                public int getCostservice() {
                    return costservice;
                }

                public void setCostservice(int costservice) {
                    this.costservice = costservice;
                }

                public int getExtradiscount() {
                    return extradiscount;
                }

                public void setExtradiscount(int extradiscount) {
                    this.extradiscount = extradiscount;
                }

                public String getBlock() {
                    return block;
                }

                public void setBlock(String block) {
                    this.block = block;
                }

                public String getMaid() {
                    return maid;
                }

                public void setMaid(String maid) {
                    this.maid = maid;
                }

                public String getOrdermarket() {
                    return ordermarket;
                }

                public void setOrdermarket(String ordermarket) {
                    this.ordermarket = ordermarket;
                }

                public int getTotaldish() {
                    return totaldish;
                }

                public void setTotaldish(int totaldish) {
                    this.totaldish = totaldish;
                }

                public String getCardcode() {
                    return cardcode;
                }

                public void setCardcode(String cardcode) {
                    this.cardcode = cardcode;
                }

                public int getDiscountmoney() {
                    return discountmoney;
                }

                public void setDiscountmoney(int discountmoney) {
                    this.discountmoney = discountmoney;
                }

                public int getMaling() {
                    return maling;
                }

                public void setMaling(int maling) {
                    this.maling = maling;
                }

                public int getBookmoney() {
                    return bookmoney;
                }

                public void setBookmoney(int bookmoney) {
                    this.bookmoney = bookmoney;
                }

                public Object getPaytype() {
                    return paytype;
                }

                public void setPaytype(Object paytype) {
                    this.paytype = paytype;
                }
            }
        }
    }
}
