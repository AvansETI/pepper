package com.pepper.backend.services.database;

import com.pepper.backend.model.*;
import com.pepper.backend.model.database.Response;
import com.pepper.backend.repositories.database.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DatabaseService {

    private final AnswerRepository answerRepository;
    private final FeedbackRepository feedbackRepository;
    private final MealRepository mealRepository;
    private final MealOrderRepository mealOrderRepository;
    private final PatientRepository patientRepository;
    private final QuestionRepository questionRepository;
    private final ReminderRepository reminderRepository;
    private final NextSequenceService nextSequenceService;

    public DatabaseService(
            AnswerRepository answerRepository,
            FeedbackRepository feedbackRepository,
            MealRepository mealRepository, MealOrderRepository mealOrderRepository,
            PatientRepository patientRepository,
            QuestionRepository questionRepository,
            ReminderRepository reminderRepository,
            NextSequenceService nextSequenceService) {
        this.answerRepository = answerRepository;
        this.feedbackRepository = feedbackRepository;
        this.mealRepository = mealRepository;
        this.mealOrderRepository = mealOrderRepository;
        this.patientRepository = patientRepository;
        this.questionRepository = questionRepository;
        this.reminderRepository = reminderRepository;
        this.nextSequenceService = nextSequenceService;
    }

    public Response savePatient(Patient patient) {
        String id;
        boolean isNew = false;

        if (patient.getId().equals("-1")) {
            id = this.nextSequenceService.getNextSequence("patient");
            isNew = true;
        } else {
            id = patient.getId();
        }

        Optional<Patient> founded = this.patientRepository.findById(id);

        if (founded.isEmpty()) {
            patient.setId(id);
            this.patientRepository.save(patient);
        } else {
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

        return new Response(id, isNew);
    }

    public Patient findPatient(String id) {
        return this.patientRepository.findById(id).orElse(null);
    }

    public Set<String> findPatientIds(LocalDate birthdate) {
        Set<String> ids = new HashSet<>();

        for (Patient patient : this.patientRepository.findAll()) {
            if (patient.getBirthdate().equals(birthdate)) {
                ids.add(patient.getId());
            }
        }

        return ids;
    }

    public Set<String> findPatientIds() {
        Set<String> ids = new HashSet<>();

        for (Patient patient : this.patientRepository.findAll()) {
            ids.add(patient.getId());
        }

        return ids;
    }

    public Response saveMeal(Meal meal) {
        String id;
        boolean isNew = false;

        if (meal.getId().equals("-1")) {
            id = this.nextSequenceService.getNextSequence("meal");
            isNew = true;
        } else {
            id = meal.getId();
        }

        Optional<Meal> founded = this.mealRepository.findById(id);

        if (founded.isEmpty()) {
            meal.setId(id);
            this.mealRepository.save(meal);
        } else {
            Meal mealFounded = founded.get();

            if (meal.getName() != null) {
                mealFounded.setName(meal.getName());
            } else if (meal.getDescription() != null) {
                mealFounded.setDescription(meal.getDescription());
            } else if (meal.getCalories() != null) {
                mealFounded.setCalories(meal.getCalories());
            } else if (meal.getAllergies() != null) {
                if (mealFounded.getAllergies() == null) {
                    mealFounded.setAllergies(new HashSet<>());
                }

                mealFounded.addAllergies(meal.getAllergies());
            } else if (meal.getImage() != null) {
                mealFounded.setImage(meal.getImage());
            }

            this.mealRepository.save(mealFounded);
        }

        return new Response(id, isNew);
    }

    public Response saveMealOrder(MealOrder order) {
        String id;
        boolean isNew = false;

        if (order.getId().equals("-1")) {
            id = this.nextSequenceService.getNextSequence("mealOrder");
            isNew = true;
        } else {
            id = order.getId();
        }

        Optional<MealOrder> founded = this.mealOrderRepository.findById(id);

        if (founded.isEmpty()) {
            order.setId(id);
            this.mealOrderRepository.save(order);
        } else {
            MealOrder orderFounded = founded.get();

            if (order.getMealId() != null) {
                orderFounded.setMealId(order.getMealId());
            } else if (order.getTimestamp() != null) {
                orderFounded.setTimestamp(order.getTimestamp());
            }

            this.mealOrderRepository.save(orderFounded);
        }

        return new Response(id, isNew);
    }

    public Response saveFeedback(Feedback feedback) {
        String id;
        boolean isNew = false;

        if (feedback.getId().equals("-1")) {
            id = this.nextSequenceService.getNextSequence("feedback");
            isNew = true;
        } else {
            id = feedback.getId();
        }

        Optional<Feedback> founded = this.feedbackRepository.findById(id);

        if (founded.isEmpty()) {
            feedback.setId(id);
            this.feedbackRepository.save(feedback);
        } else {
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

        return new Response(id, isNew);
    }

    public Response saveAnswer(Answer answer) {
        String id;
        boolean isNew = false;

        if (answer.getId().equals("-1")) {
            id = this.nextSequenceService.getNextSequence("answer");
            isNew = true;
        } else {
            id = answer.getId();
        }

        Optional<Answer> founded = this.answerRepository.findById(id);

        if (founded.isEmpty()) {
            answer.setId(id);
            this.answerRepository.save(answer);
        } else {
            Answer answerFounded = founded.get();

            if (answer.getQuestionId() != null) {
                answerFounded.setQuestionId(answer.getQuestionId());
            } else if (answer.getText() != null) {
                answerFounded.setText(answer.getText());
            } else if (answer.getTimestamp() != null) {
                answerFounded.setTimestamp(answer.getTimestamp());
            }

            this.answerRepository.save(answerFounded);
        }

        return new Response(id, isNew);
    }

    public Response saveQuestion(Question question) {
        String id;
        boolean isNew = false;

        if (question.getId().equals("-1")) {
            id = this.nextSequenceService.getNextSequence("question");
            isNew = true;
        } else {
            id = question.getId();
        }

        Optional<Question> founded = this.questionRepository.findById(id);

        if (founded.isEmpty()) {
            question.setId(id);
            this.questionRepository.save(question);
        } else {
            Question questionFounded = founded.get();

            if (question.getText() != null) {
                questionFounded.setText(question.getText());
            } else if (question.getTimestamp() != null) {
                questionFounded.setTimestamp(question.getTimestamp());
            }

            this.questionRepository.save(questionFounded);
        }

        return new Response(id, isNew);
    }

    public Set<String> findUnansweredQuestionIds(String patientId) {
        Set<String> ids = new HashSet<>();

        for (Question question : this.questionRepository.findAll()) {

            if (!question.getPatientId().equals(patientId)) {
                continue;
            }

            boolean match = false;
            for (Answer answer : this.answerRepository.findAll()) {
                if (answer.getQuestionId().equals(question.getId())) {
                    match = true;
                    break;
                }
            }

            if (!match) {
                ids.add(question.getId());
            }
        }

        return ids;
    }

    public Question findQuestion(String id) {
        return this.questionRepository.findById(id).orElse(null);
    }

}
