package cn.acewill.pos.next.model.booking;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ModelContainer;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.List;

import cn.acewill.pos.next.factory.AppDatabase;

/**
 * Created by Acewill on 2016/6/2.
 */
//预定

@com.raizlabs.android.dbflow.annotation.Table(name="booking",database = AppDatabase.class)
@ModelContainer
public class Booking extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    private long id;
    @Column
    private int bookingNumber; // 预订号
    @Column
    private int salesmanId; // 营销用户ID（拉来客户的营销经理）
    @Column
    private String customerName; // 顾客姓名
    @Column
    private String customerTelephone; // 顾客电话
    @Column
    private String expectedTime; // 预期到达时间（HH-mm-dd）
    @Column
    private String expiredTime; // 预订过期时间（HH-mm-dd）
    @Column
    private int amount; // 人数
    @Column
    private int tableAmount; // 桌台数
    @Column
    private int backupTableAmount; // 备用桌数



    @Column
    private String comment; // 备注

    //以下字段不保存到数据库
    private List<Long> tableIdList; // 桌台ID列表

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(int bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public int getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(int salesmanId) {
        this.salesmanId = salesmanId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerTelephone() {
        return customerTelephone;
    }

    public void setCustomerTelephone(String customerTelephone) {
        this.customerTelephone = customerTelephone;
    }

    public String getExpectedTime() {
        return expectedTime;
    }

    public void setExpectedTime(String expectedTime) {
        this.expectedTime = expectedTime;
    }

    public String getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(String expiredTime) {
        this.expiredTime = expiredTime;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTableAmount() {
        return tableAmount;
    }

    public void setTableAmount(int tableAmount) {
        this.tableAmount = tableAmount;
    }

    public int getBackupTableAmount() {
        return backupTableAmount;
    }

    public void setBackupTableAmount(int backupTableAmount) {
        this.backupTableAmount = backupTableAmount;
    }

    public List<Long> getTableIdList() {
        return tableIdList;
    }

    public void setTableIdList(List<Long> tableIdList) {
        this.tableIdList = tableIdList;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
