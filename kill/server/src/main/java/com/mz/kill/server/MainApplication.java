package com.mz.kill.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.ErrorPageFilter;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
@ImportResource(value = "classpath:spring/spring-jdbc.xml")
@MapperScan(basePackages = {"com.mz.kill.model.mapper"})
@EnableScheduling
public class MainApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class,args);
    }

    public MainApplication() {
        super();
        setRegisterErrorPageFilter(false);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MainApplication.class);
    }
    //解决ErrorPageFilter错误
//    @Bean
//    public ErrorPageFilter errorPageFilter(){
//        return new ErrorPageFilter();
//    }
//    @Bean
//    public FilterRegistrationBean disableSpringBootErrorFilter(ErrorPageFilter filter) {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(filter);
//        filterRegistrationBean.setEnabled(false);
//        return filterRegistrationBean;
//    }

}
