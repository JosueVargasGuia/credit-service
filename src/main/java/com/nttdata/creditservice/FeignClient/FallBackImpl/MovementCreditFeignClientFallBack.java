package com.nttdata.creditservice.FeignClient.FallBackImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nttdata.creditservice.FeignClient.MovementCreditFeignClient;
import com.nttdata.creditservice.model.MovementCredit;

import lombok.extern.log4j.Log4j2;
 

@Log4j2
@Component
public class MovementCreditFeignClientFallBack implements MovementCreditFeignClient {
	
	@Value("${api.movementCredit-service.uri}")
	private String movementCreditService;

	public List<MovementCredit>  findAll() {		
		log.info("MovementCreditFeignClientFallBack[" + movementCreditService + "]:");
		return new ArrayList<MovementCredit>();
	}

	@Override
	public List<MovementCredit> findAllByCustomer(Long idCustomer) {
		log.info("MovementCreditFeignClientFallBack[" + "/findAllByCustomer/" + idCustomer+ "]:");
		return null;
	}

}
