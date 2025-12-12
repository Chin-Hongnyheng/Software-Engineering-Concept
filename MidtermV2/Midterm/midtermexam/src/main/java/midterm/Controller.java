package midterm;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

public class Controller {

    @FXML
    private TextField searchFolder;
    @FXML
    private TextField ForbiddenFille;
    @FXML
    private TableView<Result> resultTable;
    @FXML
    private TableColumn<Result, String> FileNameColumn;
    @FXML
    private TableColumn<Result, String> ReplacementColumn;
    @FXML
    private TableColumn<Result, String> OriginalColumn;
    @FXML
    private TableColumn<Result, String> NewColumn;
    @FXML
    private ProgressBar progressBar;

    private Set<String> forbiddenWords = new HashSet<>();
    private volatile boolean paused = false;
    private volatile boolean stopped = false;
    private final Object pauseLock = new Object();
    private List<File> filesToProcess = new ArrayList<>();
    private Map<String, Integer> wordCountMap = new HashMap<>();

    @FXML
    public void initialize() {
        FileNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFileName()));
        ReplacementColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReplacement()));
        OriginalColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getOriginalLocation()));
        NewColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNewLocation()));
    }

    @FXML
    void openFolderOnAction(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        File folder = chooser.showDialog(null);
        if (folder != null)
            searchFolder.setText(folder.getAbsolutePath());
    }

    @FXML
    void OpenFileOnAction(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            ForbiddenFille.setText(file.getAbsolutePath());
            loadForbiddenWords(file);
        }
    }

    private void loadForbiddenWords(File file) {
        forbiddenWords.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty())
                    forbiddenWords.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void StartOnAction(ActionEvent event) {
        File folder = new File(searchFolder.getText());
        if (!folder.exists() || !folder.isDirectory() || forbiddenWords.isEmpty())
            return;

        stopped = false;
        paused = false;
        resultTable.getItems().clear();
        wordCountMap.clear();
        filesToProcess.clear();
        getAllFiles(folder, filesToProcess);

        if (filesToProcess.isEmpty())
            return;

        progressBar.setProgress(0);

        // Run sequential processing in background thread
        new Thread(() -> {
            int total = filesToProcess.size();
            int processed = 0;

            for (File file : filesToProcess) {
                // Stop check
                if (stopped)
                    break;

                // Pause handling
                synchronized (pauseLock) {
                    while (paused) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Process the file
                processFile(file, folder);

                processed++;
                int finalProcessed = processed;
                Platform.runLater(() -> progressBar.setProgress((double) finalProcessed / total));

                // Delay 2 seconds before next file
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Generate report
            generateReport(folder);
        }).start();
    }

    private void getAllFiles(File folder, List<File> files) {
        File[] list = folder.listFiles();
        if (list == null)
            return;
        for (File f : list) {
            if (f.isFile())
                files.add(f);
            else if (f.isDirectory())
                getAllFiles(f, files);
        }
    }

    private void processFile(File file, File baseFolder) {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            boolean containsForbidden = false;
            int replacementCount = 0;
            List<String> newLines = new ArrayList<>();

            for (String line : lines) {
                for (String word : forbiddenWords) {
                    Pattern pattern = Pattern.compile("(?i)" + Pattern.quote(word));
                    if (pattern.matcher(line).find()) {
                        replacementCount++;
                        containsForbidden = true;
                        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                        line = pattern.matcher(line).replaceAll("*******");
                    }
                }
                newLines.add(line);
            }

            if (containsForbidden && !stopped) {
                Path dest = Paths.get(baseFolder.getAbsolutePath(), "ProcessedFiles");
                Files.createDirectories(dest);

                Path originalCopy = dest.resolve(file.getName());
                Files.copy(file.toPath(), originalCopy, StandardCopyOption.REPLACE_EXISTING);

                Path newFile = dest.resolve("MOD_" + file.getName());
                Files.write(newFile, newLines, StandardCharsets.UTF_8);

                int finalReplacementCount = replacementCount;
                Platform.runLater(() -> resultTable.getItems().add(new Result(
                        file.getName(),
                        String.valueOf(finalReplacementCount),
                        file.getAbsolutePath(),
                        newFile.toAbsolutePath().toString())));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void PauseOnAction(ActionEvent event) {
        paused = true;
    }

    @FXML
    void ResumeOnAction(ActionEvent event) {
        paused = false;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    @FXML
    void StopOnAction(ActionEvent event) {
        stopped = true;
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }

    private void generateReport(File baseFolder) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(baseFolder.getAbsolutePath(), "report.txt"),
                StandardCharsets.UTF_8)) {

            writer.write("Report of forbidden words\n\n");

            List<Result> snapshot = new ArrayList<>(resultTable.getItems());
            for (Result r : snapshot) {
                File f = new File(r.getOriginalLocation());
                writer.write("File: " + r.getFileName() + "\n");
                writer.write("Original: " + r.getOriginalLocation() + "\n");
                writer.write("Modified: " + r.getNewLocation() + "\n");
                writer.write("Size: " + f.length() + " bytes\n\n");
            }

            writer.write("Top 10 forbidden words:\n");
            wordCountMap.entrySet().stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .limit(10)
                    .forEach(e -> {
                        try {
                            writer.write(e.getKey() + ": " + e.getValue() + "\n");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Result {
        private final SimpleStringProperty fileName;
        private final SimpleStringProperty replacement;
        private final SimpleStringProperty originalLocation;
        private final SimpleStringProperty newLocation;

        public Result(String fileName, String replacement, String originalLocation, String newLocation) {
            this.fileName = new SimpleStringProperty(fileName);
            this.replacement = new SimpleStringProperty(replacement);
            this.originalLocation = new SimpleStringProperty(originalLocation);
            this.newLocation = new SimpleStringProperty(newLocation);
        }

        public String getFileName() {
            return fileName.get();
        }

        public String getReplacement() {
            return replacement.get();
        }

        public String getOriginalLocation() {
            return originalLocation.get();
        }

        public String getNewLocation() {
            return newLocation.get();
        }
    }
}
