package com.pepper.backend.repositories.database;

import com.pepper.backend.model.Meal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MealRepository extends MongoRepository<Meal, String> {
}
