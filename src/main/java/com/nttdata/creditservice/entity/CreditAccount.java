package com.nttdata.creditservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "CreditAccount")
public final class CreditAccount extends Account {
	@Id
	private Long idCreditAccount;
	private Long idProduct;
	private Double amountCreditLimit;
}
