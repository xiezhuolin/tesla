package cn.acewill.pos.next.service.canxingjian.retrofit.message;

import java.util.List;

import cn.acewill.pos.next.model.table.SectionStatus;

/**
 * Created by DHH on 2016/6/16.
 */
public class TableAreaResponse {
    /**
     * result : 0
     * content : [{"id":1,"appId":"1","brandId":1,"storeId":1,"name":"大厅1","status":"OCCUPIED"},{"id":2,"appId":"1","brandId":1,"storeId":1,"name":"包间1","status":"OCCUPIED"},{"id":3,"appId":"1","brandId":1,"storeId":1,"name":"包间2","status":"OCCUPIED"}]
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
         * name : 大厅1
         * status : OCCUPIED
         * totalTables = 3
         * inuseTables = 1
         */

        private long id;
        private String appId;
        private long brandId;
        private long storeId;
        private String name;
        private SectionStatus status;
        private int totalTables;
        private int inuseTables;

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

        public void setName(String name) {
            this.name = name;
        }

        public void setStatus(SectionStatus status) {
            this.status = status;
        }

        public long getId() {
            return id;
        }

        public String getAppId() {
            return appId;
        }

        public long getBrandId() {
            return brandId;
        }

        public long getStoreId() {
            return storeId;
        }

        public String getName() {
            return name;
        }

        public SectionStatus getStatus() {
            return status;
        }

        public int getTotalTables() {
            return totalTables;
        }

        public void setTotalTables(int totalTables) {
            this.totalTables = totalTables;
        }

        public int getInuseTables() {
            return inuseTables;
        }

        public void setInuseTables(int inuseTables) {
            this.inuseTables = inuseTables;
        }
    }
}
