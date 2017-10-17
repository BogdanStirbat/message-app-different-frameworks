package com.bstirbat.message.servlet.controller;

import com.bstirbat.message.servlet.model.Message;
import com.bstirbat.message.servlet.repository.MessageRepository;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class MessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/messages")
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @PostMapping("/messages")
    public Message createMessage(@Valid @RequestBody Message message) {
        return messageRepository.save(message);
    }

    @GetMapping("/messages/{id}")
    public Optional<Message> getMessageById(@PathVariable(value = "id") String messageId) {
        return messageRepository.findById(messageId);
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable(value = "id") String messageId,
                                                 @Valid @RequestBody Message message) {
        return messageRepository.findById(messageId)
                .map(existingMessage -> {
                    existingMessage.setText(message.getText());
                    return messageRepository.save(message);
                })
                .map(updatedMessage -> new ResponseEntity<Message>(updatedMessage, HttpStatus.OK))
                .orElse(new ResponseEntity<Message>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable(value = "id") String messageId) {
        return messageRepository.findById(messageId)
                .map(existingMessage -> {
                    messageRepository.delete(existingMessage);
                    return new ResponseEntity<Void>(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}
