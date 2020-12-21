package com.mz.kill.server.service;

import com.mz.api.Main;
import com.mz.kill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    Environment environment;
    @Async
    public void sendSimpleMail(final MailDto mailDto){
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(environment.getProperty("spring.mail.username"));
            simpleMailMessage.setTo(mailDto.getTos());
            simpleMailMessage.setSubject(mailDto.getSubject());
            simpleMailMessage.setText(mailDto.getContent());
            mailSender.send(simpleMailMessage);
            logger.info("发送邮件成功。。。");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("发送邮件失败....");
        }
    }
}
