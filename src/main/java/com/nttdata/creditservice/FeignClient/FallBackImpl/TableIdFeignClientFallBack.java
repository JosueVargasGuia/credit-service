package com.nttdata.creditservice.FeignClient.FallBackImpl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nttdata.creditservice.FeignClient.TableIdFeignClient;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class TableIdFeignClientFallBack implements TableIdFeignClient {
	@Value("${api.tableId-service.uri}")
	private String tableIdService;

	public Long generateKey(String nameTable) {
		log.info("TableIdFeignClientFallBack[" + tableIdService + "/generateKey/" + nameTable + "]:" + 0);
		return Long.valueOf(0);
	}

}
