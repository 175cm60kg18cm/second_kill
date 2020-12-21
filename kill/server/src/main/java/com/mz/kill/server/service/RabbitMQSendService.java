package com.mz.kill.server.service;

import com.mz.kill.model.dto.KillSuccessUserInfo;
import com.mz.kill.model.mapper.ItemKillSuccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RabbitMQSendService {
    private Logger logger = LoggerFactory.getLogger(RabbitMQSendService.class);
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    Environment environment;
    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;
    /**
    * 秒杀成功发送邮件
    */
    public void sendSecondKillSuccessEmailMsg(String orderNum){
        System.out.println("order num...");
        if(!StringUtils.isEmpty(orderNum)){
            KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNum);
            System.out.println("i       "+orderNum);
            if(info!=null){
                try{
                    logger.info("prepare to send msg");
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(environment.getProperty("mq.kill.item.success.email.exchange"));
                    rabbitTemplate.setRoutingKey(environment.getProperty("mq.kill.item.success.email.routing.key"));
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties = message.getMessageProperties();
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            messageProperties.setHeader(
                                    AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,
                                    KillSuccessUserInfo.class);
                            return message;
                        }
                    });
                    logger.info("sended  msg...... ");
                }catch (Exception e){
                    e.printStackTrace();
                    logger.error("send error ....");
                }
            }
        }

    }
    /**
     * 用户抢购成功，将消息发送到死信队列中，等待一定时间失效超时的订单
    * @param  orderNum
     * */
    public void killSuccessOrderExpireMsg(String orderNum){
        try{
            if(orderNum!=null){
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNum);
                if(info!=null){
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(environment.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));
                    rabbitTemplate.setRoutingKey(environment.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties = message.getMessageProperties();
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            messageProperties.setHeader(
                                    AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME,
                                    KillSuccessUserInfo.class);
                            messageProperties.setExpiration(environment.getProperty("mq.kill.item.success.kill.expire"));
                            return message;
                        }
                    });
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("error on sending msg to dead queue");
        }

    }
}
