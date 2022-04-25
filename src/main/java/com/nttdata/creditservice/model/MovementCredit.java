package com.nttdata.creditservice.model;

import java.util.Date;
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
 
public class MovementCredit {
 
	Long idMovementCredit;
	double amount;
	Date dateMovement;
	TypeMovementCredit typeMovementCredit;
	Long idCredit;
	@Override
	public String toString() {
		return "MovementCredit [idMovementCredit=" + idMovementCredit + ", amount=" + amount + ", dateMovement="
				+ dateMovement + ", typeMovementCredit=" + typeMovementCredit + ", idCredit=" + idCredit + "]";
	}

}
