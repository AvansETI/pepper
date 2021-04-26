package com.pepper.backend.services.database;

import com.pepper.backend.model.bot.BotMessage;
import com.pepper.backend.model.staff.StaffMessage;
import com.pepper.backend.repositories.BotMessageRepository;
import com.pepper.backend.repositories.StaffMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseService {

    private final NextSequenceService nextSequenceService;
    private final BotMessageRepository botMessageRepository;
    private final StaffMessageRepository staffMessageRepository;

    public DatabaseService(NextSequenceService nextSequenceService, BotMessageRepository botMessageRepository, StaffMessageRepository staffMessageRepository) {
        this.nextSequenceService = nextSequenceService;
        this.botMessageRepository = botMessageRepository;
        this.staffMessageRepository = staffMessageRepository;
    }

    public void writeBotMessage(BotMessage message) {
        message.setId(this.nextSequenceService.getNextSequence("botMessageSequence"));
        this.botMessageRepository.save(message);
    }

    public List<BotMessage> readAllBotMessages() {
        return this.botMessageRepository.findAll();
    }

    public void writeStaffMessage(StaffMessage message) {
        message.setId(this.nextSequenceService.getNextSequence("staffMessageSequence"));
        this.staffMessageRepository.save(message);
    }

    public List<StaffMessage> readAllStaffMessages() {
        return this.staffMessageRepository.findAll();
    }

}
