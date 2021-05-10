package com.pepper.backend.repositories.database;

import com.pepper.backend.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {

}
