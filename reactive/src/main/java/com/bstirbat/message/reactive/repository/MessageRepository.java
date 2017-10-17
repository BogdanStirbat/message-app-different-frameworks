package com.bstirbat.message.reactive.repository;


import com.bstirbat.message.reactive.model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends ReactiveMongoRepository <Message, String> {

}
