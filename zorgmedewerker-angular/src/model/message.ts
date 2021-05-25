export enum Sender {
    STAFF,
    PLATFORM
}

export enum Person {
    PATIENT,
    ALL
}

export enum Task {
    QUESTION,

    PATIENT,
    PATIENT_ID,
    PATIENT_NAME,
    PATIENT_BIRTHDATE,
    PATIENT_ALLERGY,
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
