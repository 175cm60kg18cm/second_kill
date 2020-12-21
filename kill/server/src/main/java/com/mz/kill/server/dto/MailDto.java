package com.mz.kill.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhi
 *
 **/
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MailDto implements Serializable{
    //邮件主题
    private String subject;
    //邮件内容
    private String content;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getTos() {
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }

    //接收人
    private String[] tos;
}