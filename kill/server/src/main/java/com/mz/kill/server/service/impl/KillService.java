package com.mz.kill.server.service.impl;

import com.mz.kill.model.entity.ItemKill;
import com.mz.kill.model.entity.ItemKillSuccess;
import com.mz.kill.model.mapper.ItemKillMapper;
import com.mz.kill.model.mapper.ItemKillSuccessMapper;
import com.mz.kill.server.enums.SysConstant;
import com.mz.kill.server.service.IKillService;
import com.mz.kill.server.service.RabbitMQSendService;
import com.mz.kill.server.utils.RandomUtil;
import com.mz.kill.server.utils.SnowFlake;
import jdk.management.resource.internal.inst.FileOutputStreamRMHooks;
import org.joda.time.DateTime;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class KillService implements IKillService {
    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;
    @Autowired
    ItemKillMapper itemKillMapper;
    @Autowired
    RabbitMQSendService rabbitMQSendService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;
    private SnowFlake snowFlake=new SnowFlake(2,3);
    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        if(itemKillSuccessMapper.countByKillUserId(killId, userId)<=0){
            ItemKill itemKill=itemKillMapper.selectById(killId);
            if (itemKill!=null && 1==itemKill.getCanKill()){

                //TODO:扣减库存-减1
                int res=itemKillMapper.updateKillItem(killId);
                if (res>0){
                    //TODO:判断是否扣减成功了?是-生成秒杀成功的订单、同时通知用户秒杀已经成功（在一个通用的方法里面实现）
                    this.commonRecordKillSuccessInfo(itemKill,userId);

                    result=true;
                }
            }
        }
        else {
            throw new Exception("该用户已经抢购过该产品");
        }
        return result;
    }
/**
 * 基于redis优化秒杀逻辑
 * @author zhi
 */
    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        if(itemKillSuccessMapper.countByKillUserId(killId, userId)<=0){
            String key=new StringBuilder().append(killId).append(":").append(userId).append("forkill").toString();
            String value=RandomUtil.generateOrderCode();
            ValueOperations<String, String> valueOperations = stringRedisTemplate.opsForValue();
            Boolean ifAbsent = valueOperations.setIfAbsent(key, value);
            if(ifAbsent){
                stringRedisTemplate.expire(key,30, TimeUnit.SECONDS);
                try {
                    ItemKill itemKill=itemKillMapper.selectById(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill()){

                        int res=itemKillMapper.updateKillItem(killId);
                        if (res>0){
                            this.commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(value.equals(valueOperations.get(key).toString())){
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        }
        else {
            throw new Exception("该用户已经抢购过该产品");
        }
        return result;
    }

    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        String key=new StringBuilder().append(killId).append(":").append(userId).append("redissionkill").toString();
        RLock rLock = redissonClient.getLock(key);
        try{
            //此方法执行时线程会在30个时间单位内不断尝试获取锁，
            rLock.tryLock(30,10,TimeUnit.SECONDS);
            if(itemKillSuccessMapper.countByKillUserId(killId, userId)<=0){
                ItemKill itemKill=itemKillMapper.selectById(killId);
                if (itemKill!=null && 1==itemKill.getCanKill()){
                    int res=itemKillMapper.updateKillItem(killId);
                    if (res>0){
                        //TODO:判断是否扣减成功了?是-生成秒杀成功的订单、同时通知用户秒杀已经成功（在一个通用的方法里面实现）
                        this.commonRecordKillSuccessInfo(itemKill,userId);
                        result=true;
                    }
                }
            }
            else {
                throw new Exception("该用户已经抢购过该产品");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return result;

    }

    private void commonRecordKillSuccessInfo(ItemKill kill, Integer userId) throws Exception{
            //TODO:记录抢购成功后生成的秒杀订单记录

            ItemKillSuccess entity=new ItemKillSuccess();
            String orderNo=String.valueOf(snowFlake.nextId());

            entity.setCode(RandomUtil.generateOrderCode());   //传统时间戳+N位随机数
            entity.setCode(orderNo); //雪花算法
            entity.setItemId(kill.getItemId());
            entity.setKillId(kill.getId());
            entity.setUserId(userId.toString());
            entity.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
            entity.setCreateTime(DateTime.now().toDate());
            if(itemKillSuccessMapper.countByKillUserId(kill.getId(), userId)<=0){
            int insert = itemKillSuccessMapper.insert(entity);
            if(insert>0){
                System.out.println("insert..............");
                rabbitMQSendService.sendSecondKillSuccessEmailMsg(orderNo);
                rabbitMQSendService.killSuccessOrderExpireMsg(orderNo);
                ArrayList<int[]> list = new ArrayList<>();

                }
            }
        }
}
