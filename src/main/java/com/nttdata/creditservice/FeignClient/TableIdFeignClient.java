package com.nttdata.creditservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nttdata.creditservice.FeignClient.FallBackImpl.TableIdFeignClientFallBack;

@FeignClient(name = "${api.tableId-service.uri}", fallback = TableIdFeignClientFallBack.class)
public interface TableIdFeignClient {

	@GetMapping("/generateKey/{nameTable}")
	public Long generateKey(@PathVariable(name = "nameTable") String nameTable);
}
