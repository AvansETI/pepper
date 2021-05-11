package com.pepper.backend.services.database;

import com.pepper.backend.model.Patient;
import com.pepper.backend.repositories.database.AnswerRepository;
import com.pepper.backend.repositories.database.FeedbackRepository;
import com.pepper.backend.repositories.database.OrderRepository;
import com.pepper.backend.repositories.database.PatientRepository;
import com.pepper.backend.repositories.database.QuestionRepository;
import com.pepper.backend.repositories.database.ReminderRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class DatabaseService {

    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final OrderRepository orderRepository;
    private final PatientRepository patientRepository;
    private final QuestionRepository questionRepository;
    private final ReminderRepository reminderRepository;
    private final NextSequenceService nextSequenceService;

    public DatabaseService(
            AnswerRepository answerRepository,
            FeedbackRepository feedbackRepository,
            OrderRepository orderRepository,
            PatientRepository patientRepository,
            QuestionRepository questionRepository,
            ReminderRepository reminderRepository,
            NextSequenceService nextSequenceService
    ) {
        this.answerRepository = answerRepository;
        this.feedbackRepository = feedbackRepository;
        this.orderRepository = orderRepository;
        this.patientRepository = patientRepository;
        this.questionRepository = questionRepository;
        this.reminderRepository = reminderRepository;
        this.nextSequenceService = nextSequenceService;
    }

    public void savePatientData(Patient patient) {
        Optional<Patient> founded = this.patientRepository.findById(patient.getId());

        if (founded.isEmpty()) {
            this.patientRepository.save(patient);
            return;
        }

        Patient patientFounded = founded.get();

        if (patient.getName() != null) {
            patientFounded.setName(patient.getName());
        } else if (patient.getBirthdate() != null) {
            patientFounded.setBirthdate(patient.getBirthdate());
        } else if (patient.getAllergies() != null) {
            if (patientFounded.getAllergies() == null) {
                patientFounded.setAllergies(new HashSet<>());
            }

            patientFounded.addAllergies(patient.getAllergies());
        }

        this.patientRepository.save(patientFounded);
    }

    

}
