package com.pepper.backend.services.messaging;

import com.pepper.backend.controllers.StaffCommunicationController;
import com.pepper.backend.model.*;
import com.pepper.backend.model.database.Response;
import com.pepper.backend.model.messaging.Message;
import com.pepper.backend.model.messaging.Person;
import com.pepper.backend.model.messaging.Sender;
import com.pepper.backend.model.messaging.Task;
import com.pepper.backend.services.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class StaffMessageHandlerService {

    private static final Logger LOG = LoggerFactory.getLogger(StaffMessageHandlerService.class);

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${encryption.password}")
    private String encryptionPassword;

    private final StaffCommunicationController staffCommunicationController;
    private final MessageParserService messageParser;
    private final MessageEncryptorService messageEncryptor;
    private final DatabaseService databaseService;

    public StaffMessageHandlerService(StaffCommunicationController staffCommunicationController, MessageParserService messageParser, MessageEncryptorService messageEncryptor, DatabaseService databaseService) {
        this.staffCommunicationController = staffCommunicationController;
        this.messageParser = messageParser;
        this.messageEncryptor = messageEncryptor;
        this.databaseService = databaseService;
    }

    public void send(Person person, String personId, Task task, String taskId, String data) {
        String message = this.messageParser.stringify(Sender.PLATFORM, "1", person, personId, task, taskId, data);

        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.encrypt(message, this.encryptionPassword);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                return;
            }
        }

        if (this.staffCommunicationController.send(message)) {
            LOG.info("Send message to staff: " + message);
        } else {
            LOG.error("Failed to send message to staff");
        }
    }

    public void handle(String message) {
        if (this.encryptionEnabled) {
            try {
                message = this.messageEncryptor.decrypt(message, this.encryptionPassword);
            } catch (Exception e) {
                LOG.error("Failed to decrypt message: " + message);
                return;
            }
        }

        Message staffMessage;
        try {
            staffMessage = this.messageParser.parse(message);
        } catch (Exception e) {
            LOG.error("Failed to parse message: " + message);
            return;
        }

        if (staffMessage.getSender() != Sender.STAFF) {
            return;
        }

        this.handleStaffMessage(staffMessage);
    }

    public void handleStaffMessage(Message message) {

        switch (message.getTask()) {
            case USER -> {
                LOG.info("New user login");

                String[] credentials = message.getData().split("%");

                boolean authorized = this.databaseService.isAuthorized(
                        User.builder()
                            .username(credentials[0])
                            .password(credentials[1])
                            .build()
                );

                this.send(Person.NONE, "-1", Task.USER, message.getTaskId(), String.valueOf(authorized));
            }
            case PATIENT -> {
                LOG.info("Get patient request");

                if (message.getPerson() != Person.PATIENT) {
                    break;
                }

                if (message.getPersonId().equals("-1")) {
                    break;
                }

                this.sendPatient(this.databaseService.findPatient(message.getPersonId()), message.getTaskId());
            }
            case PATIENT_ID -> {
                if (message.getPerson() == Person.PATIENT) {
                    LOG.info("Get patient ids request");

                    Set<String> ids = this.databaseService.findPatientIds();
                    this.sendIds(Person.PATIENT, "-1", Task.PATIENT_ID, message.getTaskId(), ids);
                }
            }
            case PATIENT_NAME -> {
                LOG.info("New patient name: " + message.getData());
                Response response = this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .name(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, response.getId(), Task.PATIENT_ID, message.getTaskId(), response.getId());
                }
            }
            case PATIENT_BIRTHDATE -> {
                LOG.info("New patient birthdate: " + message.getData());
                Response response = this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .birthdate(LocalDate.ofEpochDay(Long.parseLong(message.getData())))
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, response.getId(), Task.PATIENT_ID, message.getTaskId(), response.getId());
                }
            }
            case PATIENT_ALLERGIES -> {
                LOG.info("New patient allergies: " + message.getData());

                String[] allergiesString = message.getData().substring(1, message.getData().length() - 1).replace(" ", "").split(",");
                Set<Allergy> allergies = new HashSet<>();

                for (String allergyString : allergiesString) {
                    if (allergyString.equals("")) {
                        continue;
                    }
                    allergies.add(Allergy.valueOf(allergyString));
                }

                Response response = this.databaseService.savePatient(Patient.builder()
                        .id(message.getPersonId())
                        .allergies(allergies)
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, response.getId(), Task.PATIENT_ID, message.getTaskId(), response.getId());
                }
            }
            case FEEDBACK -> {
                LOG.info("New feedback request");

                if (message.getTaskId().equals("-1")) {
                    break;
                }

                this.sendFeedback(this.databaseService.findFeedback(message.getTaskId()));
            }
            case FEEDBACK_ID -> {
                LOG.info("New feedback id request");

                if (message.getPersonId().equals("-1")) {
                    break;
                }

                Set<String> ids = this.databaseService.findFeedbackIds(message.getPersonId());
                this.sendIds(Person.PATIENT, message.getPersonId(), Task.FEEDBACK_ID, message.getTaskId(), ids);
            }
            case MEAL_ORDER -> {
                LOG.info("New meal order request");

                if (message.getTaskId().equals("-1")) {
                    break;
                }

                this.sendMealOrder(this.databaseService.findMealOrder(message.getTaskId()));
            }
            case MEAL_ORDER_ID -> {
                LOG.info("New meal order id request");

                // TODO: patient id?
                Set<String> ids = this.databaseService.findMealOrderTodayIds();
                this.sendIds(Person.NONE, "-1", Task.MEAL_ORDER_ID, message.getTaskId(), ids);
            }
            case MEAL -> {
                LOG.info("New meal request");

                if (message.getTaskId().equals("-1")) {
                    break;
                }

                this.sendMeal(this.databaseService.findMeal(message.getTaskId()));
            }
            case MEAL_ID -> {
                LOG.info("New meal id request");

                Set<String> ids = this.databaseService.findMealIds();
                this.sendIds(Person.NONE, "-1", Task.MEAL_ID, message.getTaskId(), ids);
            }
            case MEAL_NAME -> {
                LOG.info("New meal name");

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .name(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.NONE, "-1", Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_DESCRIPTION -> {
                LOG.info("New meal description: " + message.getData());

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .description(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.NONE, "-1", Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_CALORIES -> {
                LOG.info("New meal calories: " + message.getData());

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .calories(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.NONE, "-1", Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_ALLERGIES -> {
                LOG.info("New meal allergies: " + message.getData());

                String[] allergiesString = message.getData().substring(1, message.getData().length() - 1).replace(" ", "").split(",");
                Set<Allergy> allergies = new HashSet<>();

                for (String allergyString : allergiesString) {
                    if (allergyString.equals("")) {
                        continue;
                    }
                    allergies.add(Allergy.valueOf(allergyString));
                }

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .allergies(allergies)
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.NONE, "-1", Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case MEAL_IMAGE -> {
                LOG.info("New meal image: " + message.getData());

                Response response = this.databaseService.saveMeal(Meal.builder()
                        .id(message.getTaskId())
                        .image(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.NONE, "-1", Task.MEAL_ID, response.getId(), response.getId());
                }
            }
            case ANSWER -> {
                LOG.info("New answer request");

                if (message.getTaskId().equals("-1")) {
                    break;
                }

                this.sendAnswer(this.databaseService.findAnswer(message.getTaskId()));
            }
            case ANSWER_ID -> {
                LOG.info("New answer id request");

                if (message.getPersonId().equals("-1")) {
                    break;
                }

                Set<String> ids = this.databaseService.findAnswerIds(message.getPersonId());
                this.sendIds(Person.PATIENT, message.getPersonId(), Task.ANSWER_ID, message.getTaskId(), ids);
            }
            case QUESTION -> {
                LOG.info("New question request: " + message.getData());

                if (message.getTaskId().equals("-1")) {
                    break;
                }

                this.sendQuestion(this.databaseService.findQuestion(message.getTaskId()));
            }
            case QUESTION_ID -> {
                LOG.info("New question ids request: " + message.getData());

                if (message.getPersonId().equals("-1")) {
                    break;
                }

                Set<String> ids = this.databaseService.findQuestionIds(message.getPersonId());
                this.sendIds(Person.PATIENT, message.getPersonId(), Task.QUESTION_ID, message.getTaskId(), ids);
            }
            case QUESTION_TEXT -> {
                if (message.getPerson() == Person.PATIENT) {
                    LOG.info("Received new question for patient " + message.getPersonId() + ": " + message.getData());

                    Response response = this.databaseService.saveQuestion(Question.builder()
                            .id(message.getTaskId())
                            .patientId(message.getPersonId())
                            .text(message.getData())
                            .build());

                    if (response.isNew()) {
                        this.sendId(Person.PATIENT, message.getPersonId(), Task.QUESTION_ID, response.getId(), response.getId());
                    }
                }
            }
            case QUESTION_TIMESTAMP -> {
                if (message.getPerson() == Person.PATIENT) {
                    LOG.info("Received new question timestamp for patient " + message.getPersonId() + ": " + message.getData());

                    Response response = this.databaseService.saveQuestion(Question.builder()
                            .id(message.getTaskId())
                            .patientId(message.getPersonId())
                            .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                            .build());

                    if (response.isNew()) {
                        this.sendId(Person.PATIENT, message.getPersonId(), Task.QUESTION_ID, response.getId(), response.getId());
                    }
                }
            }
            case REMINDER -> {
                LOG.info("New reminder request");

                if (message.getTaskId().equals("-1")) {
                    break;
                }

                this.sendReminder(this.databaseService.findReminder(message.getTaskId()));
            }
            case REMINDER_ID -> {
                LOG.info("New reminder id request");

                if (message.getPersonId().equals("-1")) {
                    break;
                }

                Set<String> ids = this.databaseService.findReminderTodayIds(message.getPersonId());
                this.sendIds(Person.PATIENT, message.getPersonId(), Task.REMINDER_ID, message.getTaskId(), ids);
            }
            case REMINDER_THING -> {
                LOG.info("New reminder thing");

                Response response = this.databaseService.saveReminder(Reminder.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .thing(message.getData())
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.REMINDER_ID, response.getId(), response.getId());
                }
            }
            case REMINDER_TIMESTAMP -> {
                LOG.info("New reminder timestamp");

                Response response = this.databaseService.saveReminder(Reminder.builder()
                        .id(message.getTaskId())
                        .patientId(message.getPersonId())
                        .timestamp(LocalDateTime.ofEpochSecond(Long.parseLong(message.getData()), 0, ZoneOffset.UTC))
                        .build());

                if (response.isNew()) {
                    this.sendId(Person.PATIENT, message.getPersonId(), Task.REMINDER_ID, response.getId(), response.getId());
                }
            }
            default -> {
                LOG.error("Unknown command: " + message.getTask());
            }
        }
    }

    public void sendReminder(Reminder reminder) {
        if (reminder == null) {
            return;
        }

        this.send(Person.PATIENT, reminder.getPatientId(), Task.REMINDER_THING, reminder.getId(), reminder.getThing());
        this.send(Person.PATIENT, reminder.getPatientId(), Task.REMINDER_TIMESTAMP, reminder.getId(), String.valueOf(reminder.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
    }

    public void sendMeal(Meal meal) {
        if (meal == null) {
            return;
        }

        this.send(Person.NONE, "-1", Task.MEAL_NAME, meal.getId(), meal.getName());
        this.send(Person.NONE, "-1", Task.MEAL_DESCRIPTION, meal.getId(), meal.getDescription());
        this.send(Person.NONE, "-1", Task.MEAL_CALORIES, meal.getId(), meal.getCalories());
        this.send(Person.NONE, "-1", Task.MEAL_ALLERGIES, meal.getId(), String.valueOf(meal.getAllergies() == null ? new HashSet<>() : meal.getAllergies()));
        this.send(Person.NONE, "-1", Task.MEAL_IMAGE, meal.getId(), meal.getImage());
    }

    public void sendMealOrder(MealOrder order) {
        if (order == null) {
            return;
        }

        this.send(Person.PATIENT, order.getPatientId(), Task.MEAL_ORDER_MEAL_ID, order.getId(), order.getMealId());
        this.send(Person.PATIENT, order.getPatientId(), Task.MEAL_ORDER_TIMESTAMP, order.getId(), String.valueOf(order.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
    }

    public void sendFeedback(Feedback feedback) {
        if (feedback == null) {
            return;
        }

        this.send(Person.PATIENT, feedback.getPatientId(), Task.FEEDBACK_STATUS, feedback.getId(), feedback.getStatus());
        this.send(Person.PATIENT, feedback.getPatientId(), Task.FEEDBACK_EXPLANATION, feedback.getId(), feedback.getExplanation());
        this.send(Person.PATIENT, feedback.getPatientId(), Task.FEEDBACK_TIMESTAMP, feedback.getId(), String.valueOf(feedback.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
    }

    public void sendPatient(Patient patient, String taskId) {
        if (patient == null) {
            return;
        }

        this.send(Person.PATIENT, patient.getId(), Task.PATIENT_NAME, taskId, patient.getName());
        this.send(Person.PATIENT, patient.getId(), Task.PATIENT_BIRTHDATE, taskId, String.valueOf(patient.getBirthdate().toEpochDay()));
        this.send(Person.PATIENT, patient.getId(), Task.PATIENT_ALLERGIES, taskId, String.valueOf(patient.getAllergies() == null ? new HashSet<>() : patient.getAllergies()));
    }

    public void sendQuestion(Question question) {
        if (question == null) {
            return;
        }

        this.send(Person.PATIENT, question.getPatientId(), Task.QUESTION_TEXT, question.getId(), question.getText());
        this.send(Person.PATIENT, question.getPatientId(), Task.QUESTION_TIMESTAMP, question.getId(), String.valueOf(question.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
    }

    public void sendAnswer(Answer answer) {
        if (answer == null) {
            return;
        }

        this.send(Person.PATIENT, answer.getPatientId(), Task.ANSWER_QUESTION_ID, answer.getId(), answer.getQuestionId());
        this.send(Person.PATIENT, answer.getPatientId(), Task.ANSWER_TEXT, answer.getId(), answer.getText());
        this.send(Person.PATIENT, answer.getPatientId(), Task.ANSWER_TIMESTAMP, answer.getId(), String.valueOf(answer.getTimestamp().toEpochSecond(ZoneOffset.UTC)));
    }

    public void sendIds(Person person, String personId, Task task, String taskId, Set<String> ids) {
        this.send(person, personId, task, taskId, String.valueOf(ids));
    }

    public void sendId(Person person, String personId, Task task, String taskId, String id) {
        this.send(person, personId, task, taskId, id);
    }


}
