package com.pepper.backend.services.database;

import com.pepper.backend.model.Feedback;
import com.pepper.backend.model.MealOrder;
import com.pepper.backend.model.Patient;
import com.pepper.backend.repositories.database.AnswerRepository;
import com.pepper.backend.repositories.database.FeedbackRepository;
import com.pepper.backend.repositories.database.MealOrderRepository;
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
    private final MealOrderRepository mealOrderRepository;
    private final PatientRepository patientRepository;
    private final QuestionRepository questionRepository;
    private final ReminderRepository reminderRepository;

    public DatabaseService(
            AnswerRepository answerRepository,
            FeedbackRepository feedbackRepository,
            MealOrderRepository mealOrderRepository,
            PatientRepository patientRepository,
            QuestionRepository questionRepository,
            ReminderRepository reminderRepository
    ) {
        this.answerRepository = answerRepository;
        this.feedbackRepository = feedbackRepository;
        this.mealOrderRepository = mealOrderRepository;
        this.patientRepository = patientRepository;
        this.questionRepository = questionRepository;
        this.reminderRepository = reminderRepository;
    }

    public void savePatient(Patient patient) {
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

    public void saveMealOrder(MealOrder order) {
        Optional<MealOrder> founded = this.mealOrderRepository.findById(order.getId());

        if (founded.isEmpty()) {
            this.mealOrderRepository.save(order);
            return;
        }

        MealOrder orderFounded = founded.get();

        if (order.getMeal() != null) {
            orderFounded.setMeal(order.getMeal());
        } else if (order.getTimestamp() != null) {
            orderFounded.setTimestamp(order.getTimestamp());
        }

        this.mealOrderRepository.save(orderFounded);
    }

    public void saveFeedback(Feedback feedback) {
        Optional<Feedback> founded = this.feedbackRepository.findById(feedback.getId());

        if (founded.isEmpty()) {
            this.feedbackRepository.save(feedback);
            return;
        }

        Feedback feedbackFounded = founded.get();

        if (feedback.getStatus() != null) {
            feedbackFounded.setStatus(feedback.getStatus());
        } else if (feedback.getExplanation() != null) {
            feedbackFounded.setExplanation(feedback.getExplanation());
        } else if (feedback.getTimestamp() != null) {
            feedbackFounded.setTimestamp(feedback.getTimestamp());
        }

        this.feedbackRepository.save(feedbackFounded);
    }

}
