package com.pepper.backend.repositories;

import com.pepper.backend.model.staff.StaffMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StaffMessageRepository extends MongoRepository<StaffMessage, String> { }
