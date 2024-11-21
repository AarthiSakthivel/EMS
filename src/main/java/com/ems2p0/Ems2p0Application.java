package com.ems2p0;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@SpringBootApplication
@EnableScheduling
public class Ems2p0Application {


	public static void main(String[] args) {
		SpringApplication.run(Ems2p0Application.class, args);
//		Ems2p0Application job=new Ems2p0Application();
//		job.currrentTime();
         
	}
}
