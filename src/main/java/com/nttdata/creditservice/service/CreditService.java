package com.nttdata.creditservice.service;

import java.util.Map;
 
import com.nttdata.creditservice.entity.CreditAccount;
import com.nttdata.creditservice.model.Customer;
import com.nttdata.creditservice.model.MovementCredit;
import com.nttdata.creditservice.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditService {

	Flux<CreditAccount> findAll();

	Mono<CreditAccount> findById(Long idCreditAccount);

	Mono<CreditAccount> save(CreditAccount creditAccount);

	Mono<CreditAccount> update(CreditAccount creditAccount);

	Mono<Void> delete(Long idCreditAccount);

	Map<String, Object> registerAccountCredit(CreditAccount creditAccount);

	Product findByIdProduct(Long idProducto);

	Customer findByIdCustomer(Long idCustomer);

	// Mono<Map<String, Object>> consultMovements(Long idCredit);

	Flux<MovementCredit> consultMovements(Long idCreditAccount);

	Long generateKey(String nameTable);

}
