package com.bstirbat.message.dw.controller;


import co.paralleluniverse.fibers.Suspendable;
import com.bstirbat.message.dw.model.Message;
import com.bstirbat.message.dw.repository.MessageRepository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
public class MessageController {

    private MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GET
    @Path("/{id}")
    @Suspendable
    public Message findMessage(@PathParam("id") String messageId) throws InterruptedException {

        return messageRepository.findById(messageId);
    }

}
