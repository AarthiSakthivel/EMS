package com.ems2p0.client.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;

import org.codehaus.plexus.component.annotations.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * EMS 2.0 - Configuration file to use all of the request parameters used to
 * invoke the external APIS
 */
@Configuration

public class FeignClientConfig {

	/**
	 * Initiated the form data encoder request form to use in the client to invoke
	 * the APIS
	 * 
	 * @return
	 */
	@Bean
	Encoder feignFormEncoder() {
		return new SpringFormEncoder();
	}
	
	
}
