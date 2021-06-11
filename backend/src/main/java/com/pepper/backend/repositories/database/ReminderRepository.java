package com.pepper.backend.repositories.database;

import com.pepper.backend.model.Reminder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReminderRepository extends MongoRepository<Reminder, String> {

}
