import { Allergy } from './allergy'

export interface Patient {
    id: string,
    name: string,
    birthdate: Date,
    allergies: Set<Allergy>
}
