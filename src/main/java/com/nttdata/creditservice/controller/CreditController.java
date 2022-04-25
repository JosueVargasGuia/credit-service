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

import com.nttdata.creditservice.entity.Credit;
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
	public Flux<Credit> findAll() {
		return creditService.findAll();
	}

	@PostMapping
	public Mono<ResponseEntity<Credit>> save(@RequestBody Credit credit) {
		return creditService.save(credit).map(_credit -> ResponseEntity.ok().body(_credit)).onErrorResume(e -> {
			log.info("Error:" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		});
	}

	@GetMapping("/{idCredit}")
	public Mono<ResponseEntity<Credit>> findById(@PathVariable(name = "idCredit") long idCredit) {
		return creditService.findById(idCredit).map(configuration -> ResponseEntity.ok().body(configuration))
				.onErrorResume(e -> {
					log.info(e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@PutMapping
	public Mono<ResponseEntity<Credit>> update(@RequestBody Credit credit) {

		Mono<Credit> mono = creditService.findById(credit.getIdCredit()).flatMap(objCredit -> {
			log.info("Update:[new]" + credit + " [Old]:" + objCredit);
			return creditService.update(credit);
		});

		return mono.map(_credit -> {
			log.info("Status:" + HttpStatus.OK);
			return ResponseEntity.ok().body(_credit);
		}).onErrorResume(e -> {
			log.info("Status:" + HttpStatus.BAD_REQUEST + " menssage" + e.getMessage());
			return Mono.just(ResponseEntity.badRequest().build());
		}).defaultIfEmpty(ResponseEntity.noContent().build());

	}

	@DeleteMapping("/{idCredit}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable(name = "idCredit") long idCredit) {
		return creditService.findById(idCredit).flatMap(credit -> {
			return creditService.delete(credit.getIdCredit()).then(Mono.just(ResponseEntity.ok().build()));
		});
	}

	@PostMapping("/registerAccountCredit")
	public Mono<ResponseEntity<Map<String, Object>>> registerAccountCredit(@RequestBody Credit credit) {
		return Mono.just(creditService.registerAccountCredit(credit)).map(_object -> ResponseEntity.ok().body(_object))
				.onErrorResume(e -> {
					log.info("Error:" + e.getMessage());
					return Mono.just(ResponseEntity.badRequest().build());
				}).defaultIfEmpty(ResponseEntity.noContent().build());
	}

	@GetMapping("/consultMovements/{idCredit}")
	public Flux<MovementCredit> consultMovements(@PathVariable(name = "idCredit") Long idCredit) {
		return creditService.consultMovements(idCredit);

	}
}
