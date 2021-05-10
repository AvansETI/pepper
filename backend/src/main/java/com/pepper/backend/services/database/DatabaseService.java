package com.pepper.backend.services.database;

import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    private final NextSequenceService nextSequenceService;

    public DatabaseService(NextSequenceService nextSequenceService) {
        this.nextSequenceService = nextSequenceService;
    }

}
