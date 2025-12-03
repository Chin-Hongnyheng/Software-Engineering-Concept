package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.Comparator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;

/**
 * UserViewModel class that acts as the ViewModel in the MVVM pattern.
 * This class exposes properties that the View can bind to and handles
 * the presentation logic.
 */
public class UserViewModel {
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty Email = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> Date = new SimpleObjectProperty<>(null);
    private final ObservableList<Student> users = FXCollections.observableArrayList();
    private final FilteredList<Student> filteredUsers = new FilteredList<>(users, p -> true);

    public UserViewModel() {
        users.addAll(
                new Student("Hongnyheng", "Chin", "hongnyheng@gmail.com", LocalDate.of(2005, 12, 29)),
                new Student("Vireak", "Rith", "vireakrith@gmail.com", LocalDate.of(2005, 12, 25)));
    }

    public ObservableList<Student> getFilteredUsers() {
        return filteredUsers;
    }

    // Property accessors for data binding
    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return Email;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return Date;
    }

    public ObservableList<Student> getUsers() {
        return users;
    }

    // Getter and setter methods for convenience
    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setEmail(String Email) {
        this.Email.set(Email);
    }

    public String getEmail() {
        return Email.get();
    }

    public void setDate(LocalDate Date) {
        this.Date.set(Date);
    }

    public LocalDate getDate() {
        return Date.get();
    }

    /**
     * Computed property that returns the full name by combining first and last
     * name.
     * This is called whenever the first or last name changes.
     */
    public String getFullName() {
        String first = firstName.get();
        String last = lastName.get();
        String email = Email.get();
        LocalDate date = Date.get();

        if (first == null)
            first = "";
        if (last == null)
            last = "";

        return (first + " " + last).trim();
    }

    public void applySearch(String query) {
        String lowerQuery = query == null ? "" : query.toLowerCase();
        filteredUsers.setPredicate(student -> {
            if (lowerQuery.isEmpty())
                return true;
            // Match by full name, email, or birthdate
            return student.getStudent().toLowerCase().contains(lowerQuery)
                    || student.getEmail().toLowerCase().contains(lowerQuery)
                    || (student.getDate() != null && student.getDate().toString().contains(lowerQuery));
        });
    }

    /**
     * Adds a new user to the collection based on current first and last name
     * values.
     */
    public void addUser() {
        if (isValidUser()) {
            Student newStudent = new Student(getFirstName(), getLastName(), getEmail(), getDate());
            users.add(newStudent);
            clearForm();
        }
    }

    /**
     * Removes a user from the collection.
     */
    public void removeUser(Student user) {
        if (user != null) {
            users.remove(user);
        }
    }

    /**
     * Clears the form by resetting first and last name properties.
     */
    public void clearForm() {
        setFirstName("");
        setLastName("");
        setEmail("");
        setDate(null);
    }

    /**
     * Validates if the current first and last name form a valid user.
     */
    public boolean isValidUser() {
        String first = getFirstName();
        String last = getLastName();
        String email = getEmail();
        LocalDate date = getDate();
        return first != null && !first.trim().isEmpty() &&
                last != null && !last.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                date != null;
    }

    /**
     * Gets the count of users in the collection.
     */
    public int getUserCount() {
        return users.size();
    }

    public void applySorting(String sortBy, boolean descending) {
        if (users.isEmpty())
            return;

        Comparator<Student> comparator;

        // Choose sorting type
        switch (sortBy) {
            case "Email":
                comparator = Comparator.comparing(Student::getEmail, String.CASE_INSENSITIVE_ORDER);
                break;
            case "Birthdate":
                comparator = Comparator.comparing(Student::getDate);
                break;
            default: // Name
                comparator = Comparator.comparing(Student::getStudent, String.CASE_INSENSITIVE_ORDER);
                break;
        }

        // Descending?
        if (descending) {
            comparator = comparator.reversed();
        }

        FXCollections.sort(users, comparator);
    }

}