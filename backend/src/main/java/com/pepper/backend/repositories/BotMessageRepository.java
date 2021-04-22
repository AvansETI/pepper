package com.pepper.backend.repositories;

import com.pepper.backend.model.bot.BotMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BotMessageRepository extends MongoRepository<BotMessage, String> { }
