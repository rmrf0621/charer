package com.sharer.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ServletComponentScan
@ComponentScan("com.sharer.*")
@MapperScan("com.sharer.*.mapper")
public class SharerApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharerApiApplication.class, args);
	}

}