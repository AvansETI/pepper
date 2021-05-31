package com.pepper.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document("meal")
public class Meal {

    @Id
    private String id;

    private String name;

    private String description;

    private String calories;

    private Set<Allergy> allergies;

    private String image;

    public void addAllergies(Set<Allergy> allergies) {
        this.allergies.addAll(allergies);
    }
}
