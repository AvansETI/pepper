package com.pepper.backend.repositories.database;

import com.pepper.backend.model.MealOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealOrderRepository extends MongoRepository<MealOrder, String> {

}
