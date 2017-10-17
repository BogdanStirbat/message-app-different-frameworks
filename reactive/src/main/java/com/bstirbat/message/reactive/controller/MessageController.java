package com.bstirbat.message.reactive.controller;

import com.bstirbat.message.reactive.model.Message;
import com.bstirbat.message.reactive.repository.MessageRepository;
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
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/messages")
    public Flux<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @PostMapping("/messages")
    public Mono<Message> createMessage(@Valid @RequestBody Message message) {
        return messageRepository.save(message);
    }

    @GetMapping("/messages/{id}")
    public Mono<Message> getMessageById(@PathVariable(value = "id") String messageId) {
        return messageRepository.findById(messageId);
    }

    @PutMapping("/messages/{id}")
    public Mono<ResponseEntity<Message>> updateMessage(@PathVariable(value = "id") String messageId,
                                                       @Valid @RequestBody Message message) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> {
                    existingMessage.setText(message.getText());
                    return messageRepository.save(existingMessage);
                })
                .map(updatedMessage -> new ResponseEntity<Message>(updatedMessage, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<Message>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/messages/{id}")
    public Mono<ResponseEntity<Void>> deleteMessage(@PathVariable(value = "id") String messageId) {
        return messageRepository.findById(messageId)
                .flatMap(existingMessage -> messageRepository.delete(existingMessage)
                        .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK))))
                .defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
