package com.nttdata.creditservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nttdata.creditservice.FeignClient.FallBackImpl.CustomerFeignClientFallBack;
import com.nttdata.creditservice.model.Customer;

@FeignClient(name = "${api.customer-service.uri}", fallback = CustomerFeignClientFallBack.class)
public interface CustomerFeignClient {

	@GetMapping("/{id}")
	Customer customerfindById(@PathVariable(name = "id") Long id);

}
