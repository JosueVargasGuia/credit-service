package com.nttdata.creditservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
 
import com.nttdata.creditservice.entity.CreditAccount;

@Repository
public interface CreditRepository extends ReactiveMongoRepository<CreditAccount,Long>{

}
