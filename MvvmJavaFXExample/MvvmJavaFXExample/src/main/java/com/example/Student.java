package com.example;

import java.time.LocalDate;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

/**
 * User model class representing a user entity.
 * This is the Model in the MVVM pattern.
 */
public class Student {
    private String firstName;
    private String lastName;
    private String Email;
    private LocalDate Date;

    public String getStudent() {
        return firstName + " " + lastName + " Email: " + Email + " Date: " + (Date != null ? Date.toString() : "N/A");
    }

    @Override
    public String toString() {
        return getStudent();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Student user = (Student) obj;
        return firstName.equals(user.firstName) && lastName.equals(user.lastName) && Email.equals(user.Email)
                && Date.equals(user.Date);
    }

    @Override
    public int hashCode() {
        return firstName.hashCode() + lastName.hashCode() + Email.hashCode() + Date.hashCode();
    }
}