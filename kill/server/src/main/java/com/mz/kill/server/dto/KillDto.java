package com.mz.kill.server.dto;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhi
 *
 **/
@Data
@ToString
@Getter
public class KillDto implements Serializable{

    @NotNull
    private Integer killId;

    private Integer userId;

    public Integer getKillId() {
        return killId;
    }

    public void setKillId(Integer killId) {
        this.killId = killId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}