package com.pepper.backend.repositories.database;

import com.pepper.backend.model.Answer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnswerRepository extends MongoRepository<Answer, String> {

}
