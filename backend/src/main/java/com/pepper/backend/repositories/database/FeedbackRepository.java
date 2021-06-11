package com.pepper.backend.repositories.database;

import com.pepper.backend.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {

}
