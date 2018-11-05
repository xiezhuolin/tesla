package cn.acewill.pos.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by DHH on 2016/6/3.
 * 返回所有桌台订单信息
 */
public class InitCurrBean {

    /**
     * success : true
     * data : {"businessrange":"午市","orderstatus":{"bussinessrange":"午市","total":0,"precheck":0,"checkout":0},"section":[{"sid":"1","section":"大厅","seq":"1","dfservicerate":"0.00","pid":null,"sno":"1","status":"1"}],"table":[{"tid":"1","sid":"1","tablename":"1","status":"1","usestatus":"1","maxseat":"10","seq":"1","sno":"1","curseat":null,"dfservicerate":"0.00","pid":"7","curuid":null,"csid":null,"bbook":"no","bookuser":"","expectedtime":"","tablenum":"","tables":"","phone":"","mobile":"","earnest":"","spec":"","newtime":""},{"tid":"2","sid":"1","tablename":"测试桌台","status":"1","usestatus":"1","maxseat":"10","seq":"2","sno":"2","curseat":null,"dfservicerate":"0.00","pid":"7","curuid":null,"csid":null,"bbook":"no","bookuser":"","expectedtime":"","tablenum":"","tables":"","phone":"","mobile":"","earnest":"","spec":"","newtime":""}],"param":[{"name":"DEFAULT_PRECHECK_TYPE","value":1},{"name":"DEFAULT_RATE","value":1},{"name":"DEFAULT_SERVICE_RATE","value":"0"},{"name":"DEFAULT_PRECHECK_MARKETING_ACTIVITY","value":null},{"name":"DEFAULT_PRECHECK_DISCOUNT_SCHEME","value":null},{"name":"B_DEFAULT_PRECHECK_PRINT","value":"1"},{"name":"B_DEFAULT_GUEST_LEAVE","value":"0"},{"name":"B_DEFAULT_INVOICE","value":"0"},{"name":"DF_WIPE_ZERO","value":"0"},{"name":"JOB_CASHIER","value":-1},{"name":"JOB_WAITER","value":-2},{"name":"JOB_SENTER","value":-6},{"name":"iconsize","value":"small"},{"name":"refshtime","value":3000},{"name":"B_TABLE_STATUS_WITH_GUEST_IN","value":"0"},{"name":"B_TABLE_STATUS_WITH_DIRTY","value":"0"},{"name":"IcCard","value":1},{"name":"DOCKING_HOTEL_SOFT","value":0},{"name":"INTERMEDIATE_SOFT_INTERFACE","value":0},{"name":"ICBCMODEL","value":"0"},{"name":"ICBC_RMB_INTEGRAL_PROPORTION","value":1},{"name":"B_USE_PRINT_CODE","value":1},{"name":"B_MUTI_PAY","value":0},{"name":"B_PRINT_CHECKOUTBILL_NUMBER","value":1},{"name":"ARE_MEMBER_INTERFACE","value":2},{"name":"YAZUOUSERNAME","value":""},{"name":"YAZUOPWD","value":""},{"name":"B_USE_MEMBER_PLANTFORM","value":1},{"name":"takerefshtime","value":160000},{"name":"B_PRINT_WEIXIN_QRCODE","value":0},{"name":"B_PRINT_BAIDU_QRCODE","value":0},{"name":"B_PRINT_ZHIFUBAO_QRCODE","value":0},{"name":"DEFAULT_SERVICE_RATE_BYMINUTE","value":0},{"name":"SERVICE_RATE_MINUTE","value":0},{"name":"SERVICE_RATE_MONEY","value":0},{"name":"CURRENT_UID","value":1},{"name":"MEMBER_PLANTFORM_IP","value":"4128171141233445"},{"name":"MEMBER_PLANTFORM_PWDKEY","value":"4128171141233445"},{"name":"NODEJS_URL","value":""},{"name":"B_NET_ORDER","value":0}],"callservice":[],"timestamp":[{"name":"c_job_ts","value":"1451271018"},{"name":"c_pay_ts","value":"1463533896"},{"name":"c_user_ts","value":"1368890249"},{"name":"o_discount_ts","value":"1367583501"},{"name":"o_discounttype_ts","value":"1451271018"},{"name":"o_dish_ts","value":"1464161712"},{"name":"o_dishbill_ts","value":"1451378323"},{"name":"o_dishkind_ts","value":"1334133685"},{"name":"o_dishraw_ts","value":"1451271019"},{"name":"o_dishunit_ts","value":"1451271018"},{"name":"o_financestat_ts","value":"1334133657"},{"name":"o_groupkind_ts","value":"1451271018"},{"name":"o_menudish_ts","value":"1451271019"},{"name":"o_ordermemo_ts","value":"1367582576"},{"name":"o_reason_ts","value":"1451271018"},{"name":"o_sectionprint_ts","value":"1451378367"},{"name":"o_setmeal_ts","value":"1451378713"},{"name":"o_table_ts","value":"1464934952"},{"name":"o_terminal_ts","value":"1368796326"}],"iconsize":"small","hotelinfo":"","sourcepaytype":{"1":"支付宝","5":"百度支付","8":"点评支付","9":"美团支付","3":"微信支付","10":"饿了么支付"}}
     */

    private boolean success;
    /**
     * businessrange : 午市
     * orderstatus : {"bussinessrange":"午市","total":0,"precheck":0,"checkout":0}
     * section : [{"sid":"1","section":"大厅","seq":"1","dfservicerate":"0.00","pid":null,"sno":"1","status":"1"}]
     * table : [{"tid":"1","sid":"1","tablename":"1","status":"1","usestatus":"1","maxseat":"10","seq":"1","sno":"1","curseat":null,"dfservicerate":"0.00","pid":"7","curuid":null,"csid":null,"bbook":"no","bookuser":"","expectedtime":"","tablenum":"","tables":"","phone":"","mobile":"","earnest":"","spec":"","newtime":""},{"tid":"2","sid":"1","tablename":"测试桌台","status":"1","usestatus":"1","maxseat":"10","seq":"2","sno":"2","curseat":null,"dfservicerate":"0.00","pid":"7","curuid":null,"csid":null,"bbook":"no","bookuser":"","expectedtime":"","tablenum":"","tables":"","phone":"","mobile":"","earnest":"","spec":"","newtime":""}]
     * param : [{"name":"DEFAULT_PRECHECK_TYPE","value":1},{"name":"DEFAULT_RATE","value":1},{"name":"DEFAULT_SERVICE_RATE","value":"0"},{"name":"DEFAULT_PRECHECK_MARKETING_ACTIVITY","value":null},{"name":"DEFAULT_PRECHECK_DISCOUNT_SCHEME","value":null},{"name":"B_DEFAULT_PRECHECK_PRINT","value":"1"},{"name":"B_DEFAULT_GUEST_LEAVE","value":"0"},{"name":"B_DEFAULT_INVOICE","value":"0"},{"name":"DF_WIPE_ZERO","value":"0"},{"name":"JOB_CASHIER","value":-1},{"name":"JOB_WAITER","value":-2},{"name":"JOB_SENTER","value":-6},{"name":"iconsize","value":"small"},{"name":"refshtime","value":3000},{"name":"B_TABLE_STATUS_WITH_GUEST_IN","value":"0"},{"name":"B_TABLE_STATUS_WITH_DIRTY","value":"0"},{"name":"IcCard","value":1},{"name":"DOCKING_HOTEL_SOFT","value":0},{"name":"INTERMEDIATE_SOFT_INTERFACE","value":0},{"name":"ICBCMODEL","value":"0"},{"name":"ICBC_RMB_INTEGRAL_PROPORTION","value":1},{"name":"B_USE_PRINT_CODE","value":1},{"name":"B_MUTI_PAY","value":0},{"name":"B_PRINT_CHECKOUTBILL_NUMBER","value":1},{"name":"ARE_MEMBER_INTERFACE","value":2},{"name":"YAZUOUSERNAME","value":""},{"name":"YAZUOPWD","value":""},{"name":"B_USE_MEMBER_PLANTFORM","value":1},{"name":"takerefshtime","value":160000},{"name":"B_PRINT_WEIXIN_QRCODE","value":0},{"name":"B_PRINT_BAIDU_QRCODE","value":0},{"name":"B_PRINT_ZHIFUBAO_QRCODE","value":0},{"name":"DEFAULT_SERVICE_RATE_BYMINUTE","value":0},{"name":"SERVICE_RATE_MINUTE","value":0},{"name":"SERVICE_RATE_MONEY","value":0},{"name":"CURRENT_UID","value":1},{"name":"MEMBER_PLANTFORM_IP","value":"4128171141233445"},{"name":"MEMBER_PLANTFORM_PWDKEY","value":"4128171141233445"},{"name":"NODEJS_URL","value":""},{"name":"B_NET_ORDER","value":0}]
     * callservice : []
     * timestamp : [{"name":"c_job_ts","value":"1451271018"},{"name":"c_pay_ts","value":"1463533896"},{"name":"c_user_ts","value":"1368890249"},{"name":"o_discount_ts","value":"1367583501"},{"name":"o_discounttype_ts","value":"1451271018"},{"name":"o_dish_ts","value":"1464161712"},{"name":"o_dishbill_ts","value":"1451378323"},{"name":"o_dishkind_ts","value":"1334133685"},{"name":"o_dishraw_ts","value":"1451271019"},{"name":"o_dishunit_ts","value":"1451271018"},{"name":"o_financestat_ts","value":"1334133657"},{"name":"o_groupkind_ts","value":"1451271018"},{"name":"o_menudish_ts","value":"1451271019"},{"name":"o_ordermemo_ts","value":"1367582576"},{"name":"o_reason_ts","value":"1451271018"},{"name":"o_sectionprint_ts","value":"1451378367"},{"name":"o_setmeal_ts","value":"1451378713"},{"name":"o_table_ts","value":"1464934952"},{"name":"o_terminal_ts","value":"1368796326"}]
     * iconsize : small
     * hotelinfo :
     * sourcepaytype : {"1":"支付宝","5":"百度支付","8":"点评支付","9":"美团支付","3":"微信支付","10":"饿了么支付"}
     */

    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String businessrange;
        /**
         * bussinessrange : 午市
         * total : 0
         * precheck : 0
         * checkout : 0
         */

        private OrderstatusBean orderstatus;
        private String iconsize;
        private String hotelinfo;
        /**
         * 1 : 支付宝
         * 5 : 百度支付
         * 8 : 点评支付
         * 9 : 美团支付
         * 3 : 微信支付
         * 10 : 饿了么支付
         */

        private SourcepaytypeBean sourcepaytype;
        /**
         * sid : 1
         * section : 大厅
         * seq : 1
         * dfservicerate : 0.00
         * pid : null
         * sno : 1
         * status : 1
         */

        private List<SectionBean> section;
        /**
         * tid : 1
         * sid : 1
         * status : 1
         * usestatus : 1
         * maxseat : 10
         * seq : 1
         * sno : 1
         * curseat : null
         * dfservicerate : 0.00
         * pid : 7
         * curuid : null
         * csid : null
         * bbook : no
         * bookuser :
         * expectedtime :
         * tablenum :
         * tables :
         * phone :
         * mobile :
         * earnest :
         * spec :
         * newtime :
         */

        private List<TableBean> table;
        /**
         * name : DEFAULT_PRECHECK_TYPE
         * value : 1
         */

        private List<ParamBean> param;
        private List<?> callservice;
        /**
         * name : c_job_ts
         * value : 1451271018
         */

        private List<TimestampBean> timestamp;

        public String getBusinessrange() {
            return businessrange;
        }

        public void setBusinessrange(String businessrange) {
            this.businessrange = businessrange;
        }

        public OrderstatusBean getOrderstatus() {
            return orderstatus;
        }

        public void setOrderstatus(OrderstatusBean orderstatus) {
            this.orderstatus = orderstatus;
        }

        public String getIconsize() {
            return iconsize;
        }

        public void setIconsize(String iconsize) {
            this.iconsize = iconsize;
        }

        public String getHotelinfo() {
            return hotelinfo;
        }

        public void setHotelinfo(String hotelinfo) {
            this.hotelinfo = hotelinfo;
        }

        public SourcepaytypeBean getSourcepaytype() {
            return sourcepaytype;
        }

        public void setSourcepaytype(SourcepaytypeBean sourcepaytype) {
            this.sourcepaytype = sourcepaytype;
        }

        public List<SectionBean> getSection() {
            return section;
        }

        public void setSection(List<SectionBean> section) {
            this.section = section;
        }

        public List<TableBean> getTable() {
            return table;
        }

        public void setTable(List<TableBean> table) {
            this.table = table;
        }

        public List<ParamBean> getParam() {
            return param;
        }

        public void setParam(List<ParamBean> param) {
            this.param = param;
        }

        public List<?> getCallservice() {
            return callservice;
        }

        public void setCallservice(List<?> callservice) {
            this.callservice = callservice;
        }

        public List<TimestampBean> getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(List<TimestampBean> timestamp) {
            this.timestamp = timestamp;
        }

        public static class OrderstatusBean {
            private String bussinessrange;
            private int total;
            private int precheck;
            private int checkout;

            public String getBussinessrange() {
                return bussinessrange;
            }

            public void setBussinessrange(String bussinessrange) {
                this.bussinessrange = bussinessrange;
            }

            public int getTotal() {
                return total;
            }

            public void setTotal(int total) {
                this.total = total;
            }

            public int getPrecheck() {
                return precheck;
            }

            public void setPrecheck(int precheck) {
                this.precheck = precheck;
            }

            public int getCheckout() {
                return checkout;
            }

            public void setCheckout(int checkout) {
                this.checkout = checkout;
            }
        }

        public static class SourcepaytypeBean {
            @SerializedName("1")
            private String value1;
            @SerializedName("5")
            private String value5;
            @SerializedName("8")
            private String value8;
            @SerializedName("9")
            private String value9;
            @SerializedName("3")
            private String value3;
            @SerializedName("10")
            private String value10;

            public String getValue1() {
                return value1;
            }

            public void setValue1(String value1) {
                this.value1 = value1;
            }

            public String getValue5() {
                return value5;
            }

            public void setValue5(String value5) {
                this.value5 = value5;
            }

            public String getValue8() {
                return value8;
            }

            public void setValue8(String value8) {
                this.value8 = value8;
            }

            public String getValue9() {
                return value9;
            }

            public void setValue9(String value9) {
                this.value9 = value9;
            }

            public String getValue3() {
                return value3;
            }

            public void setValue3(String value3) {
                this.value3 = value3;
            }

            public String getValue10() {
                return value10;
            }

            public void setValue10(String value10) {
                this.value10 = value10;
            }
        }

        public static class SectionBean {
            private String sid;
            private String section;
            private String seq;
            private String dfservicerate;
            private Object pid;
            private String sno;
            private String status;

            public String getSid() {
                return sid;
            }

            public void setSid(String sid) {
                this.sid = sid;
            }

            public String getSection() {
                return section;
            }

            public void setSection(String section) {
                this.section = section;
            }

            public String getSeq() {
                return seq;
            }

            public void setSeq(String seq) {
                this.seq = seq;
            }

            public String getDfservicerate() {
                return dfservicerate;
            }

            public void setDfservicerate(String dfservicerate) {
                this.dfservicerate = dfservicerate;
            }

            public Object getPid() {
                return pid;
            }

            public void setPid(Object pid) {
                this.pid = pid;
            }

            public String getSno() {
                return sno;
            }

            public void setSno(String sno) {
                this.sno = sno;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }

        public static class TableBean {
            private String tid;
            private String sid;
            private String tablename;
            private String status;
            private String usestatus;
            private String maxseat;
            private String seq;
            private String sno;
            private Object curseat;
            private String dfservicerate;
            private String pid;
            private Object curuid;
            private Object csid;
            private String bbook;
            private String bookuser;
            private String expectedtime;
            private String tablenum;
            private String tables;
            private String phone;
            private String mobile;
            private String earnest;
            private String spec;
            private String newtime;

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public String getSid() {
                return sid;
            }

            public void setSid(String sid) {
                this.sid = sid;
            }

            public String getTablename() {
                return tablename;
            }

            public void setTablename(String tablename) {
                this.tablename = tablename;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getUsestatus() {
                return usestatus;
            }

            public void setUsestatus(String usestatus) {
                this.usestatus = usestatus;
            }

            public String getMaxseat() {
                return maxseat;
            }

            public void setMaxseat(String maxseat) {
                this.maxseat = maxseat;
            }

            public String getSeq() {
                return seq;
            }

            public void setSeq(String seq) {
                this.seq = seq;
            }

            public String getSno() {
                return sno;
            }

            public void setSno(String sno) {
                this.sno = sno;
            }

            public Object getCurseat() {
                return curseat;
            }

            public void setCurseat(Object curseat) {
                this.curseat = curseat;
            }

            public String getDfservicerate() {
                return dfservicerate;
            }

            public void setDfservicerate(String dfservicerate) {
                this.dfservicerate = dfservicerate;
            }

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public Object getCuruid() {
                return curuid;
            }

            public void setCuruid(Object curuid) {
                this.curuid = curuid;
            }

            public Object getCsid() {
                return csid;
            }

            public void setCsid(Object csid) {
                this.csid = csid;
            }

            public String getBbook() {
                return bbook;
            }

            public void setBbook(String bbook) {
                this.bbook = bbook;
            }

            public String getBookuser() {
                return bookuser;
            }

            public void setBookuser(String bookuser) {
                this.bookuser = bookuser;
            }

            public String getExpectedtime() {
                return expectedtime;
            }

            public void setExpectedtime(String expectedtime) {
                this.expectedtime = expectedtime;
            }

            public String getTablenum() {
                return tablenum;
            }

            public void setTablenum(String tablenum) {
                this.tablenum = tablenum;
            }

            public String getTables() {
                return tables;
            }

            public void setTables(String tables) {
                this.tables = tables;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public String getEarnest() {
                return earnest;
            }

            public void setEarnest(String earnest) {
                this.earnest = earnest;
            }

            public String getSpec() {
                return spec;
            }

            public void setSpec(String spec) {
                this.spec = spec;
            }

            public String getNewtime() {
                return newtime;
            }

            public void setNewtime(String newtime) {
                this.newtime = newtime;
            }
        }

        public static class ParamBean {
            private String name;
            private int value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }

        public static class TimestampBean {
            private String name;
            private String value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
