import { Allergy } from './patient'

export interface Meal {
    id: string,
    name: string,
    description: string,
    calories: string,
    allergies: Set<Allergy>,
    image: string
}
