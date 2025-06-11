package com.hsbc.trans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hsbc.common, com.hsbc.trans"})
public class TransApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransApplication.class, args);
    }

}
