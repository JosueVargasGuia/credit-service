package com.nttdata.creditservice.FeignClient;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.nttdata.creditservice.FeignClient.FallBackImpl.MovementCreditFeignClientFallBack;
import com.nttdata.creditservice.model.MovementCredit;

@FeignClient(name = "${api.movementCredit-service.uri}", fallback = MovementCreditFeignClientFallBack.class)
public interface MovementCreditFeignClient {
	@GetMapping
	public List<MovementCredit> findAll();
}
