package com.nttdata.creditservice.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.nttdata.creditservice.entity.Credit;
import com.nttdata.creditservice.model.Customer;
import com.nttdata.creditservice.model.MovementCredit;
import com.nttdata.creditservice.model.Product;
import com.nttdata.creditservice.model.TypeCustomer;
import com.nttdata.creditservice.model.TypeDocumento;
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

	@Value("${api.customer-service.uri}")
	private String customerService;

	@Value("${api.product-service.uri}")
	private String productService;

	@Value("${api.movementCredit-service.uri}")
	private String movementCreditService;

	@Value("${api.tableId-service.uri}")
	String tableIdService;

	@Override
	public Flux<Credit> findAll() {
		return creditRepository.findAll()
				.sort((credit1, credit2) -> credit1.getIdCredit().compareTo(credit2.getIdCredit()));
	}

	@Override
	public Mono<Credit> save(Credit credit) {
		Long key = generateKey(Credit.class.getSimpleName());
		if (key >= 1) {
			credit.setIdCredit(key);
			log.info("SAVE[product]:" + credit.toString());
		}
		return creditRepository.insert(credit);
	}

	@Override
	public Mono<Credit> findById(Long idCredit) {
		return creditRepository.findById(idCredit);
	}

	@Override
	public Mono<Credit> update(Credit credit) {
		return creditRepository.save(credit);
	}

	@Override
	public Mono<Void> delete(Long idCredit) {
		return creditRepository.deleteById(idCredit);
	}

	/*
	 * <b>Rule 1</b>:Un cliente puede tener un producto de credito sin la obligacion
	 * de tener una cuenta bancaria en la instituci�n<br/>
	 */
	@Override
	public Map<String, Object> registerAccountCredit(Credit credit) {
		Map<String, Object> hashMap = new HashMap<String, Object>();
		boolean isValid = true;
		Product product = null;
		if (this.findByIdProduct(credit.getIdProducto()) != null) {
			product = this.findByIdProduct(credit.getIdProducto());
			if (product.getTypeProduct() == TypeProduct.pasivos) {
				hashMap.put("Product", "El producto no es un activo para registrase como credito.");
				isValid = false;
			}
		} else {
			hashMap.put("Product", "Producto no encontrado.");
			isValid = false;
		}
		Customer customer = this.findByIdCustomer(credit.getIdCustomer());
		if (customer != null) {
// customer = this.findByIdCustomer(credit.getIdCustomer());
			if (customer.getTypeCustomer() == TypeCustomer.company) {
				hashMap.put("Product", "El cliente no puede tener una cuenta de credito.");
				isValid = false;
			}
		} else {
			hashMap.put("Customer", "El Cliente no existe.");
			isValid = false;
		}
		if (isValid) {
//Mono.fromRunnable(() -> ).subscribe(e -> log.info("fromRunnable:" + e.toString()));
			this.save(credit).map(e -> {

				return Mono.just(hashMap);
			}).subscribe();
			hashMap.put("Credit", credit);
			return hashMap;
		}
		log.info(hashMap.toString());
		return hashMap;

	}

	@Override
	public Product findByIdProduct(Long idProducto) {
		ResponseEntity<Product> responseGet = restTemplate.exchange(productService + "/" + idProducto, HttpMethod.GET,
				null, new ParameterizedTypeReference<Product>() {
				});
		if (responseGet.getStatusCode() == HttpStatus.OK) {
			return responseGet.getBody();
		} else {
			return null;
		}

	}

	@Override
	public Customer findByIdCustomer(Long idCustomer) {
		log.info(customerService + "/" + idCustomer);
		ResponseEntity<Customer> responseGet = restTemplate.exchange(customerService + "/" + idCustomer, HttpMethod.GET,
				null, new ParameterizedTypeReference<Customer>() {
				});
		if (responseGet.getStatusCode() == HttpStatus.OK) {
			return responseGet.getBody();
		} else {
			return null;
		}

		/*
		 * Long idCustomer; TypeCustomer typeCustomer; String firstName; String
		 * lastName; String emailAddress; String phoneNumber; String homeAddress; String
		 * document; TypeDocumento typeDocumento;
		 */
		/*
		 * if (idCustomer == 1) { return new Customer(idCustomer, TypeCustomer.personal,
		 * "Josue", "Vargas Guia", "josue@nttdata.com", "941451121", "jr.- calle",
		 * "45519040", TypeDocumento.dni); } else if (idCustomer == 2) { return new
		 * Customer(idCustomer, TypeCustomer.empresarial, "Josue", "Vargas Guia",
		 * "josue@nttdata.com", "941451121", "jr.- calle", "45519040",
		 * TypeDocumento.dni); } else { return null; }
		 */

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
	public Flux<MovementCredit> consultMovements(Long idCredit) {
		log.info(movementCreditService);
		ResponseEntity<List<MovementCredit>> responseGet = restTemplate.exchange(movementCreditService, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<MovementCredit>>() {
				});
		List<MovementCredit> list;
		if (responseGet.getStatusCode() == HttpStatus.OK) {
			list = responseGet.getBody();
			return Flux.fromIterable(list).filter(movementCredit -> movementCredit.getIdCredit() == idCredit);
		} else {
			return Flux.empty();
		}

	}

	@Override
	public Long generateKey(String nameTable) {
		log.info(tableIdService + "/generateKey/" + nameTable);
		ResponseEntity<Long> responseGet = restTemplate.exchange(tableIdService + "/generateKey/" + nameTable,
				HttpMethod.GET, null, new ParameterizedTypeReference<Long>() {
				});
		if (responseGet.getStatusCode() == HttpStatus.OK) {
			log.info("Body:" + responseGet.getBody());
			return responseGet.getBody();
		} else {
			return Long.valueOf(0);
		}
	}
}