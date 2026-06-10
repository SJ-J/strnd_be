package com.strnd.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class StrndApplication {

	public static void main(String[] args) {
		// JVM 기본 타임존을 KST로 고정
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(StrndApplication.class, args);
	}

}