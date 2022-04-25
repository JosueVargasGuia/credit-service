package com.nttdata.creditservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "credit")
public class Credit {
	@Id
	Long idCredit;
	Long idCustomer;
	Long idProducto;
	Double amountCreditLimit;
}
