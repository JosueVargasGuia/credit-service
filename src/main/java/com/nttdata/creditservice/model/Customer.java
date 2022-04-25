package com.nttdata.creditservice.model;
 
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	private Long id;
	private String firstname;
	private String lastname;
	private String documentNumber;
	private TypeDocument typeDocument;
	private TypeCustomer typeCustomer;
	private String emailAddress;
	private String phoneNumber;
	private String homeAddress;
}
