package com.pepper.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "patient")
public class Patient {

    @Id
    private String id;

    private String name;

    private LocalDate birthdate;

    private Set<Allergy> allergies;

    public void addAllergies(Set<Allergy> allergies) {
        this.allergies.addAll(allergies);
    }

}
