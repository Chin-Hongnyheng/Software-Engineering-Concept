package com.example;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

/**
 * UserController class that acts as the Controller in the MVVM pattern.
 * This class handles the interaction between the View (FXML) and the ViewModel.
 */
public class UserController implements Initializable {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button clearButton;

    @FXML
    private ListView<Student> userListView;

    @FXML
    private Button deleteButton;

    @FXML
    private Label userCountLabel;

    private UserViewModel viewModel;

    @FXML
    private TextField emailField;

    @FXML
    private DatePicker dateField;

    @FXML
    private CheckBox Descending;

    @FXML
    private ComboBox<String> Sorting;

    @FXML
    private TextField searchField;

    @FXML
    void onSearch() {
        viewModel.applySearch(searchField.getText());
        viewModel.applySorting(Sorting.getValue(), Descending.isSelected()); // keep sorting
    }

    @FXML
    void descending(ActionEvent event) {
        viewModel.applySorting(Sorting.getValue(), Descending.isSelected());
    }

    @FXML
    void sorting(ActionEvent event) {
        viewModel.applySorting(Sorting.getValue(), Descending.isSelected());
    }

    public UserController() {
        this.viewModel = new UserViewModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDataBinding();
        setupEventHandlers();
        setupListView();
    }

    /**
     * Sets up the data binding between View controls and ViewModel properties.
     * This is the key aspect of the MVVM pattern in JavaFX.
     */
    private void setupDataBinding() {
        // Bidirectional binding for text fields
        firstNameField.textProperty().bindBidirectional(viewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(viewModel.lastNameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        dateField.valueProperty().bindBidirectional(viewModel.dateProperty());

        // Bind full name label to computed property from ViewModel
        fullNameLabel.textProperty().bind(Bindings.createStringBinding(
                () -> {
                    String fullName = viewModel.getFullName();
                    return fullName.isEmpty() ? "Full Name: " : "Full Name: " + fullName;
                },
                viewModel.firstNameProperty(),
                viewModel.lastNameProperty(),
                viewModel.emailProperty(),
                viewModel.dateProperty()));

        // Bind save button enabled state to form validation
        saveButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> !viewModel.isValidUser(),
                viewModel.firstNameProperty(),
                viewModel.lastNameProperty(),
                viewModel.emailProperty(),
                viewModel.dateProperty()));

        // Bind delete button enabled state to list selection
        deleteButton.disableProperty().bind(
                userListView.getSelectionModel().selectedItemProperty().isNull());

        // Bind user count label
        userCountLabel.textProperty().bind(Bindings.createStringBinding(
                () -> "Total Users: " + viewModel.getUserCount(),
                viewModel.getUsers()));

        // Populate sorting combo box and set default
        Sorting.getItems().addAll("Name", "Email", "Birthdate");
        Sorting.setValue("Name"); // default
    }

    /**
     * Sets up event handlers for buttons and other controls.
     */
    private void setupEventHandlers() {
        // Save button handler
        saveButton.setOnAction(event -> handleSaveUser());

        // Clear button handler
        clearButton.setOnAction(event -> handleClearForm());

        // Delete button handler
        deleteButton.setOnAction(event -> handleDeleteUser());

        // Double-click on list item to edit
        userListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Student selectedUser = userListView.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    handleEditUser(selectedUser);
                }
            }
        });

        // Listen to search field changes and update filter in real-time
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.applySearch(newValue);
        });

    }

    /**
     * Sets up the ListView to display users from the ViewModel.
     */
    private void setupListView() {
        userListView.setItems(viewModel.getFilteredUsers());

        // Add listener to update UI when list changes
        viewModel.getUsers().addListener((ListChangeListener<Student>) change -> {
            // This will trigger the binding update for user count
        });
    }

    /**
     * Handles the Save User button action.
     * This method is referenced in the FXML file.
     */
    @FXML
    private void handleSaveUser() {
        if (viewModel.isValidUser()) {
            viewModel.addUser();
            viewModel.applySorting(Sorting.getValue(), Descending.isSelected());
            System.out.println("User Saved: " + viewModel.getFullName());

            // Optional: Show success message or feedback to user
            showSuccessMessage("User saved successfully!");
        }
    }

    /**
     * Handles the Clear Form button action.
     */
    @FXML
    private void handleClearForm() {
        viewModel.clearForm();
        userListView.getSelectionModel().clearSelection();
        System.out.println("Form cleared");
    }

    /**
     * Handles the Delete User button action.
     */
    @FXML
    private void handleDeleteUser() {
        Student selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            viewModel.removeUser(selectedUser);
            viewModel.applySorting(Sorting.getValue(), Descending.isSelected());
            System.out.println("User deleted: " + selectedUser.getStudent());
            showSuccessMessage("User deleted successfully!");
        }
    }

    /**
     * Handles editing a user by populating the form with their data.
     */
    private void handleEditUser(Student user) {
        if (user != null) {
            viewModel.setFirstName(user.getFirstName());
            viewModel.setLastName(user.getLastName());
            viewModel.setEmail(user.getEmail());
            viewModel.setDate(user.getDate());

            // Remove the user from the list so it can be re-added when saved
            viewModel.removeUser(user);

            System.out.println("Editing user: " + user.getStudent());
        }
    }

    /**
     * Shows a success message (in a real application, this might show a toast or
     * status bar message).
     */
    private void showSuccessMessage(String message) {
        // In a real application, you might show this in a status bar, toast, or dialog
        System.out.println("Success: " + message);
    }

    /**
     * Gets the ViewModel instance (useful for testing or external access).
     */
    public UserViewModel getViewModel() {
        return viewModel;
    }

    /**
     * Sets a custom ViewModel (useful for testing or dependency injection).
     */
}
