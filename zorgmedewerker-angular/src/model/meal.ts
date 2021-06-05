import { Allergy } from './allergy'

export interface Meal {
    id: string,
    name: string,
    description: string,
    calories: string,
    allergies: Set<Allergy>,
    image: string
}
