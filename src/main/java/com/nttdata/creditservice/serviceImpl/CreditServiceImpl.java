package com.nttdata.creditservice.serviceImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap; 
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.nttdata.creditservice.FeignClient.CustomerFeignClient;
import com.nttdata.creditservice.FeignClient.MovementCreditFeignClient;
import com.nttdata.creditservice.FeignClient.ProductFeignClient;
import com.nttdata.creditservice.FeignClient.TableIdFeignClient;
import com.nttdata.creditservice.entity.Account;
import com.nttdata.creditservice.entity.CreditAccount;
import com.nttdata.creditservice.model.Customer;
import com.nttdata.creditservice.model.MovementCredit;
import com.nttdata.creditservice.model.Product;
import com.nttdata.creditservice.model.TypeCustomer; 
import com.nttdata.creditservice.model.TypeProduct;
import com.nttdata.creditservice.repository.CreditRepository;
import com.nttdata.creditservice.service.CreditService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CreditServiceImpl implements CreditService {
	Logger log = LoggerFactory.getLogger(CreditServiceImpl.class);

	@Autowired
	CreditRepository creditRepository;
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	CustomerFeignClient customerFeignClient;
	@Autowired
	ProductFeignClient productFeignClient;
	@Autowired
	TableIdFeignClient tableIdFeignClient;
	@Autowired
	MovementCreditFeignClient movementCreditFeignClient;

	@Value("${api.customer-service.uri}")
	private String customerService;

	@Value("${api.product-service.uri}")
	private String productService;

	@Value("${api.movementCredit-service.uri}")
	private String movementCreditService;

	@Value("${api.tableId-service.uri}")
	String tableIdService;

	@Override
	public Flux<CreditAccount> findAll() {
		return creditRepository.findAll()
				.sort((credit1, credit2) -> credit1.getIdCreditAccount().compareTo(credit2.getIdCreditAccount()));
	}

	@Override
	public Mono<CreditAccount> save(CreditAccount creditAccount) {
		Long idCreditAccount = generateKey(CreditAccount.class.getSimpleName());
		if (idCreditAccount >= 1) {
			creditAccount.setIdCreditAccount(idCreditAccount);	
			creditAccount.setCreationDate(Calendar.getInstance().getTime());
		} else {
			return Mono.error(new InterruptedException("Servicio no disponible:" + CreditAccount.class.getSimpleName()));
		}
		
		Long idAccount = generateKey(Account.class.getSimpleName());
		if (idAccount >= 1) {
			creditAccount.setIdAccount(idAccount);			
		} else {
			return Mono.error(new InterruptedException("Servicio no disponible:" + Account.class.getSimpleName()));
		}
		
		log.info("SAVE[product]:" + creditAccount.toString());
		return creditRepository.insert(creditAccount);
	}

	@Override
	public Mono<CreditAccount> findById(Long idCreditAccount) {
		return creditRepository.findById(idCreditAccount);
	}

	@Override
	public Mono<CreditAccount> update(CreditAccount creditAccount) {
		return creditRepository.save(creditAccount);
	}

	@Override
	public Mono<Void> delete(Long idCreditAccount) {
		return creditRepository.deleteById(idCreditAccount);
	}

	/*
	 * <b>Rule 1</b>:Un cliente puede tener un producto de credito sin la obligacion
	 * de tener una cuenta bancaria en la instituci�n<br/>
	 */
	@Override
	public Map<String, Object> registerAccountCredit(CreditAccount creditAccount) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		boolean isValid = true;
		Product product = this.findByIdProduct(creditAccount.getIdProducto());
		if (product != null) {
			if (product.getTypeProduct() == TypeProduct.pasivos) {
				hashMap.put("Product", "El producto no es un activo para registrase como credito.");
				isValid = false;
			}
		} else {
			hashMap.put("Product", "Producto no encontrado.");
			isValid = false;
		}
		Customer customer = this.findByIdCustomer(creditAccount.getIdCustomer());
		if (customer != null) {
			if (customer.getTypeCustomer() == TypeCustomer.company) {
				hashMap.put("Product", "El cliente no puede tener una cuenta de credito.");
				isValid = false;
			}
		} else {
			hashMap.put("Customer", "El Cliente no existe.");
			isValid = false;
		}
		if (isValid) {
			this.save(creditAccount).map(e -> {
				return Mono.just(hashMap);
			}).subscribe();
			hashMap.put("Credit", creditAccount);
			return hashMap;
		}
		log.info(hashMap.toString());
		return hashMap;

	}

	@Override
	public Product findByIdProduct(Long idProducto) {
		/*
		 * ResponseEntity<Product> responseGet = restTemplate.exchange(productService +
		 * "/" + idProducto, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<Product>() { }); if (responseGet.getStatusCode()
		 * == HttpStatus.OK) { return responseGet.getBody(); } else { return null; }
		 */
		return productFeignClient.findById(idProducto);
	}

	@Override
	public Customer findByIdCustomer(Long idCustomer) {
		log.info(customerService + "/" + idCustomer);
		/*
		 * ResponseEntity<Customer> responseGet = restTemplate.exchange(customerService
		 * + "/" + idCustomer, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<Customer>() { }); if (responseGet.getStatusCode()
		 * == HttpStatus.OK) { return responseGet.getBody(); } else { return null; }
		 */

		return customerFeignClient.customerfindById(idCustomer);

	}

	/*
	 * @Override public Flux<MovementCredit> consultMovements(Long idCredit) {
	 * Map<String, Object> hashMap = new HashMap<String, Object>(); if
	 * (this.findById(idCredit) != null) { hashMap.put("Movements",
	 * this.findAllMovements(idCredit)); return Flux.empty(); } else {
	 * hashMap.put("Credit", "La cuenta de credito no exite."); return Flux.empty();
	 * } }
	 */
	@Override
	public Flux<MovementCredit> consultMovements(Long idCreditAccount) {
		log.info(movementCreditService);
		/*
		 * ResponseEntity<List<MovementCredit>> responseGet =
		 * restTemplate.exchange(movementCreditService, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<List<MovementCredit>>() { }); List<MovementCredit>
		 * list; if (responseGet.getStatusCode() == HttpStatus.OK) { list =
		 * responseGet.getBody(); return Flux.fromIterable(list).filter(movementCredit
		 * -> movementCredit.getIdCredit() == idCredit); } else { return Flux.empty(); }
		 */
		return Flux.fromIterable(movementCreditFeignClient.findAll())
				.filter(movementCredit -> movementCredit.getIdCreditAccount() == idCreditAccount).switchIfEmpty(Flux.empty());
	}

	@Override
	public Long generateKey(String nameTable) {
		log.info(tableIdService + "/generateKey/" + nameTable);
		/*
		 * ResponseEntity<Long> responseGet = restTemplate.exchange(tableIdService +
		 * "/generateKey/" + nameTable, HttpMethod.GET, null, new
		 * ParameterizedTypeReference<Long>() { }); if (responseGet.getStatusCode() ==
		 * HttpStatus.OK) { log.info("Body:" + responseGet.getBody()); return
		 * responseGet.getBody(); } else { return Long.valueOf(0); }-
		 */
		return tableIdFeignClient.generateKey(nameTable);
	}
}