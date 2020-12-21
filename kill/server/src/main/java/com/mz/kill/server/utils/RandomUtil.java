package com.mz.kill.server.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    private static final SimpleDateFormat simpleDateformat =new SimpleDateFormat("yyyyMMddHHmmssSS");
    private static final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
    public static String generateOrderCode(){
        return simpleDateformat.format(DateTime.now().toDate())+generateNumber(6);
    }
    public static String generateNumber(final int num){
        StringBuffer stringBuffer=new StringBuffer();
        for (int i=0;i<num;i++){
            stringBuffer.append(threadLocalRandom.nextInt(9));
        }
        return stringBuffer.toString();
    }
}
