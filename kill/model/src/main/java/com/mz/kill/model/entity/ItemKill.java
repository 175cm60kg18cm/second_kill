package com.mz.kill.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ItemKill {
    private Integer id;

    private Integer itemId;

    private Integer total;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    private Byte isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private String itemName;

    //采用服务器时间控制是否可以进行抢购
    private Integer canKill;

    public Integer getId() {
        return id;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getTotal() {
        return total;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Byte getIsActive() {
        return isActive;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getItemName() {
        return itemName;
    }

    public Integer getCanKill() {
        return canKill;
    }
}