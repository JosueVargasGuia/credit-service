package com.nttdata.creditservice.controller;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nttdata.creditservice.entity.CreditAccount;
import com.nttdata.creditservice.model.MovementCredit;
import com.nttdata.creditservice.service.CreditService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/credit")
public class CreditController {

	Logger log = LoggerFactory.getLogger(CreditController.class);

	@Autowired
	CreditService creditService;

	@GetMapping
	public Flux<CreditAccount> findAll() {
		return creditService.findAll();
	}

	@PostMapping
	public Mono<ResponseEntity<CreditAccount>> save(@RequestBody CreditAccount creditAccount) {
		return creditService.save(creditAccount).map(_credit -> ResponseEntity.ok().body(_credit)).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		});
	}

	@GetMapping("/{idCreditAccount}")
	public Mono<ResponseEntity<CreditAccount>> findById(@PathVariable(name = "idCreditAccount") long idCreditAccount) {
		return creditService.findById(idCreditAccount).map(creditAccount -> ResponseEntity.ok().body(creditAccount))
				.onErrorResume(e -> {
					log.info(e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@PutMapping
	public Mono<ResponseEntity<CreditAccount>> update(@RequestBody CreditAccount creditAccount) {

		Mono<CreditAccount> mono = creditService.findById(creditAccount.getIdCreditAccount()).flatMap(objCredit -> {
			log.info("Update:[new]" + creditAccount + " [Old]:" + objCredit);
			return creditService.update(creditAccount);
		});

		return mono.map(_credit -> {
			log.info("Status:" + HttpStatus.OK);
			return ResponseEntity.ok().body(_credit);
		}).onErrorResume(e -> {
			log.info("Status:" + HttpStatus.BAD_REQUEST + " menssage" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		}).defaultIfEmpty(ResponseEntity.noContent().build());

	}

	@DeleteMapping("/{idCreditAccount}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable(name = "idCreditAccount") long idCreditAccount) {
		return creditService.findById(idCreditAccount).flatMap(credit -> {
			return creditService.delete(credit.getIdCreditAccount()).then(Mono.just(ResponseEntity.ok().build()));
		});
	}

	@PostMapping("/registerAccountCredit")
	public Mono<ResponseEntity<Map<String, Object>>> registerAccountCredit(@RequestBody CreditAccount creditAccount) {
		return Mono.just(creditService.registerAccountCredit(creditAccount))
				.map(_object -> ResponseEntity.ok().body(_object)).onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@GetMapping("/consultMovements/{idCreditAccount}")
	public Flux<MovementCredit> consultMovements(@PathVariable(name = "idCreditAccount") Long idCreditAccount) {
		return creditService.consultMovements(idCreditAccount);

	}
}
