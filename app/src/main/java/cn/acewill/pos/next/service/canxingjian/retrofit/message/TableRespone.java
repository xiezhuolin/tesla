package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import java.util.List;

/**
 * Created by DHH on 2016/6/15.
 */
public class TableRespone {

    /**
     * result : 0
     * content : [{"id":1,"appId":"1","brandId":1,"storeId":1,"sectionId":1,"serialNumber":"1","name":"桌台1","capacity":10,"status":"IN_USE","bookingStatus":"WAITING_CUSTOMER"},{"id":2,"appId":"1","brandId":1,"storeId":1,"sectionId":1,"serialNumber":"2","name":"桌台2","capacity":10,"status":"IN_USE","bookingStatus":"WAITING_CUSTOMER"},{"id":3,"appId":"1","brandId":1,"storeId":1,"sectionId":1,"serialNumber":"3","name":"桌台3","capacity":10,"status":"IN_USE","bookingStatus":"WAITING_CUSTOMER"},{"id":4,"appId":"1","brandId":1,"storeId":1,"sectionId":1,"serialNumber":"4","name":"桌台4","capacity":10,"status":"IN_USE","bookingStatus":"WAITING_CUSTOMER"}]
     * errmsg :
     */

    private int result;
    private String errmsg;
    private List<ContentBean> content;

    public void setResult(int result) {
        this.result = result;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public int getResult() {
        return result;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public static class ContentBean {
        /**
         * id : 1
         * appId : 1
         * brandId : 1
         * storeId : 1
         * sectionId : 1
         * serialNumber : 1
         * name : 桌台1
         * capacity : 10
         * status : IN_USE
         * bookingStatus : WAITING_CUSTOMER
         */

        private int id;
        private String appId;
        private int brandId;
        private int storeId;
        private int sectionId;
        private String serialNumber;
        private String name;
        private int capacity;
//        private TableStatus.UseStatus status;
//        private TableStatus.BookingStatus bookingStatus;

        public void setId(int id) {
            this.id = id;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public void setBrandId(int brandId) {
            this.brandId = brandId;
        }

        public void setStoreId(int storeId) {
            this.storeId = storeId;
        }

        public void setSectionId(int sectionId) {
            this.sectionId = sectionId;
        }

        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setCapacity(int capacity) {
            this.capacity = capacity;
        }

        public int getId() {
            return id;
        }

        public String getAppId() {
            return appId;
        }

        public int getBrandId() {
            return brandId;
        }

        public int getStoreId() {
            return storeId;
        }

        public int getSectionId() {
            return sectionId;
        }

        public String getSerialNumber() {
            return serialNumber;
        }

        public String getName() {
            return name;
        }

        public int getCapacity() {
            return capacity;
        }

//        public TableStatus.UseStatus getStatus() {
//            return status;
//        }
//
//        public void setStatus(TableStatus.UseStatus status) {
//            this.status = status;
//        }
//
//        public TableStatus.BookingStatus getBookingStatus() {
//            return bookingStatus;
//        }
//
//        public void setBookingStatus(TableStatus.BookingStatus bookingStatus) {
//            this.bookingStatus = bookingStatus;
//        }
    }
}
