package com.nttdata.creditservice.FeignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nttdata.creditservice.FeignClient.FallBackImpl.ProductFeignClientFallBack;
import com.nttdata.creditservice.model.Product;

@FeignClient(name = "${api.product-service.uri}", fallback = ProductFeignClientFallBack.class)
public interface ProductFeignClient {

	@GetMapping("/{idProducto}")
	Product findById(@PathVariable(name = "idProducto") Long id);
}
