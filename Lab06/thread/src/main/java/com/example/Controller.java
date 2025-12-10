package com.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {

    private ViewModel viewModel = new ViewModel();

    @FXML
    private Text FibonacciInBound;

    @FXML
    private TextField FibonacciInBoundInput;

    @FXML
    private TextField FibonacciOutBoundInput;

    @FXML
    private TextField PrimeOutBoundInput;

    @FXML
    private TextArea PrimeDisplay;

    @FXML
    private Text PrimeInBound;

    @FXML
    private TextField PrimeInBoundInput;

    @FXML
    private Text PrimeOutBound;

    @FXML
    private TextArea FibonacciDisplay;

    @FXML
    private ProgressBar FibonacciProgress;

    @FXML
    private ProgressBar PrimeProgress;

    @FXML
    void GenerateFibonacciOnAction(ActionEvent event) {
        FibonacciDisplay.clear();
        FibonacciProgress.setProgress(0);

        int startA = FibonacciInBoundInput.getText().isEmpty() ? 0 : Integer.parseInt(FibonacciInBoundInput.getText());
        int startB = 1;
        Integer end = FibonacciOutBoundInput.getText().isEmpty() ? null
                : Integer.parseInt(FibonacciOutBoundInput.getText());

        if (end == null) {
            FibonacciProgress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }

        viewModel.startGeneratingFibonacci(startA, startB, end,
                text -> FibonacciDisplay.appendText(text + " "),
                progress -> FibonacciProgress.setProgress(progress));
    }

    @FXML
    void GeneratePrimeOnAction(ActionEvent event) {
        PrimeDisplay.clear();
        PrimeProgress.setProgress(0);

        int start = PrimeInBoundInput.getText().isEmpty() ? 2 : Integer.parseInt(PrimeInBoundInput.getText());
        Integer end = PrimeOutBoundInput.getText().isEmpty() ? null : Integer.parseInt(PrimeOutBoundInput.getText());

        // Set indeterminate if no upper bound
        if (end == null) {
            PrimeProgress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }

        viewModel.startGeneratingPrime(start, end,
                text -> PrimeDisplay.appendText(text + " "),
                progress -> PrimeProgress.setProgress(progress));
    }

    @FXML
    void PauseFibonacciOnAction(ActionEvent event) {
        viewModel.FibonacciPause();
    }

    @FXML
    void PausePrimeOnAction(ActionEvent event) {
        viewModel.PrimePause();
    }

    @FXML
    void RestartFibonacciOnAction(ActionEvent event) {
        FibonacciDisplay.clear();
        FibonacciProgress.setProgress(0);

        int startA = FibonacciInBoundInput.getText().isEmpty()
                ? 0
                : Integer.parseInt(FibonacciInBoundInput.getText());
        int startB = 1;

        Integer end = FibonacciOutBoundInput.getText().isEmpty()
                ? null
                : Integer.parseInt(FibonacciOutBoundInput.getText());

        if (end == null) {
            FibonacciProgress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }

        viewModel.restartFibonacci(startA, startB, end,
                text -> FibonacciDisplay.appendText(text + " "),
                progress -> FibonacciProgress.setProgress(progress));
    }

    @FXML
    void RestartPrimeOnAction(ActionEvent event) {
        PrimeDisplay.clear();
        PrimeProgress.setProgress(0);

        int start = PrimeInBoundInput.getText().isEmpty()
                ? 2
                : Integer.parseInt(PrimeInBoundInput.getText());
        Integer end = PrimeOutBoundInput.getText().isEmpty()
                ? null
                : Integer.parseInt(PrimeOutBoundInput.getText());

        if (end == null) {
            PrimeProgress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }

        viewModel.restartPrime(start, end,
                text -> PrimeDisplay.appendText(text + " "),
                progress -> PrimeProgress.setProgress(progress));
    }

    @FXML
    void ResumeFibonacciOnAction(ActionEvent event) {
        viewModel.FibonacciResume();
    }

    @FXML
    void ResumePrimeOnAction(ActionEvent event) {
        viewModel.PrimeResume();
    }

    @FXML
    void StopFibonacciOnAction(ActionEvent event) {
        viewModel.FibonacciStop();
    }

    @FXML
    void StopPrimeOnAction(ActionEvent event) {
        viewModel.PrimeStop();
    }

}
