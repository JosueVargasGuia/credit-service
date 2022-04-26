package com.nttdata.creditservice.FeignClient.FallBackImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nttdata.creditservice.FeignClient.CustomerFeignClient;
import com.nttdata.creditservice.model.Customer;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class CustomerFeignClientFallBack implements CustomerFeignClient {
	@Value("${api.customer-service.uri}")
	private String customerService;

	public Customer customerfindById(Long id) {
		// TODO Auto-generated method stub
		// https://arnoldgalovics.com/feign-fallback/
		
		log.info("CustomerFeignClientFallBack[" + customerService + "/" + id + "]");
		return null;

	}

}
