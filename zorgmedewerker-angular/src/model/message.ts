export enum Sender {
    STAFF,
    PLATFORM
}

export enum Person {
    PATIENT,
    NONE
}

export enum Task {
    QUESTION_ID,
    QUESTION_TEXT,
    QUESTION_TIMESTAMP,

    MEAL,
    MEAL_ID,
    MEAL_NAME,
    MEAL_DESCRIPTION,
    MEAL_CALORIES,
    MEAL_ALLERGIES,
    MEAL_IMAGE,

    PATIENT,
    PATIENT_ID,
    PATIENT_NAME,
    PATIENT_BIRTHDATE,
    PATIENT_ALLERGIES,
}

export interface Message {
    sender: Sender;
    senderId: string;
    person: Person;
    personId: string;
    task: Task,
    taskId: string;
    data: string;
}
