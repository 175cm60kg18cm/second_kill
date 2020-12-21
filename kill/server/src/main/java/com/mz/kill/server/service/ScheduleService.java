package com.mz.kill.server.service;

import com.mz.kill.model.entity.ItemKillSuccess;
import com.mz.kill.model.mapper.ItemKillSuccessMapper;
import com.sun.tools.doclint.Env;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {
    private Logger logger= LoggerFactory.getLogger(ScheduleService.class);
    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    Environment environment;
    @Scheduled(cron = "0/10 * * * * ?")
    public void scheduleExpireOrders(){
        logger.info("trying to get all expired orders");
        List<ItemKillSuccess> expireOrders = itemKillSuccessMapper.selectExpireOrders();
        if(expireOrders!=null&&expireOrders.size()>0)
            expireOrders.forEach(expireOrder->{
                if(expireOrder.getDiffTime().intValue()>Integer.valueOf(environment.getProperty("scheduler.expire.orders.time"))){
                    itemKillSuccessMapper.expireOrder(expireOrder.getCode());
                }
            });
    }

}
