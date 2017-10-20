package com.bstirbat.message.dw.repository;


import com.bstirbat.message.dw.model.Message;

public interface MessageRepository {

    Message findById(String id);
}
