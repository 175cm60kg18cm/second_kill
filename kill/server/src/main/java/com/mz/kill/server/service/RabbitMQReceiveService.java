package com.mz.kill.server.service;

import com.mz.kill.model.dto.KillSuccessUserInfo;
import com.mz.kill.model.entity.ItemKillSuccess;
import com.mz.kill.model.mapper.ItemKillSuccessMapper;
import com.mz.kill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQReceiveService {
    Logger logger = LoggerFactory.getLogger(RabbitMQReceiveService.class);
    @Autowired
    MailService mailService;
    @Autowired
    ItemKillSuccessMapper itemKillSuccessMapper;
    
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumerMessage(KillSuccessUserInfo info){
        logger.info("msg receive....");
        try{
            MailDto mailDto = new MailDto();
            mailDto.setContent("dd");
            mailDto.setSubject("success ordered");
            mailDto.setTos(new String[]{info.getEmail()});
            mailService.sendSimpleMail(mailDto);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("send mail failed");
        }
    }
    /**
     * 用户秒杀成功但未支付的消息处理
     * @param info
     * */
    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"},containerFactory = "singleListenerContainer")
    public void consumeExpireMsg(KillSuccessUserInfo info){
        logger.info("expire msg receive....");
        try{
           if(info!=null){
               ItemKillSuccess itemKillSuccess = itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
               if(itemKillSuccess!=null&&itemKillSuccess.getStatus()==0){
                    itemKillSuccessMapper.expireOrder(info.getCode());
                    logger.info("not pay money on time...");
               }
           }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("expire msg is invalid");
        }
    }
}
