package com.nttdata.creditservice.service;

import java.util.Map;

import com.nttdata.creditservice.entity.Credit;
import com.nttdata.creditservice.model.Customer;
import com.nttdata.creditservice.model.MovementCredit;
import com.nttdata.creditservice.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditService {

	Flux<Credit> findAll();

	Mono<Credit> findById(Long idCredit);

	Mono<Credit> save(Credit credit);

	Mono<Credit> update(Credit credit);

	Mono<Void> delete(Long idCredit);

	Map<String, Object> registerAccountCredit(Credit credit);

	Product findByIdProduct(Long idProducto);

	Customer findByIdCustomer(Long idCustomer);

	// Mono<Map<String, Object>> consultMovements(Long idCredit);

	Flux<MovementCredit> consultMovements(Long idCredit);

	Long generateKey(String nameTable);

}
