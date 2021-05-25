export enum Allergy {
    GLUTEN,
    DIABETES,
    LACTOSE,
    EGGS,
    CELERY,
    NUTS,
    SOY,
    WHEAT,
    FISH,
    SHELLFISH
}

export interface Patient {
    id: string,
    name: string,
    birthdate: Date,
    allergies: Set<Allergy>
}
