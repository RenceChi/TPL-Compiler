package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompilerController {

    @FXML private Button btnOpen;
    @FXML private Button btnLexical;
    @FXML private Button btnSyntax;
    @FXML private Button btnSemantic;
    @FXML private Button btnClear;
    @FXML private TextArea txtResult;
    @FXML private TextArea txtCode;

    // Logic Classes (Assumed to exist in your project)
    private FileManager fileManager;
    private LexicalAnalyzer lexicalAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;
    private SemanticAnalyzer semanticAnalyzer;

    // Data State
    private List<Token> tokenList = new ArrayList<>();

    @FXML
    public void initialize() {
        fileManager = new FileManager();
        lexicalAnalyzer = new LexicalAnalyzer();
        syntaxAnalyzer = new SyntaxAnalyzer();
        semanticAnalyzer = new SemanticAnalyzer();

        // 1. Force the Code Area to be Typable
        txtCode.setEditable(true);
        resetButtonState();

        // 2. Add Listener: Detect Manual Typing
        // If user types, we must reset the pipeline because previous tokens are invalid
        txtCode.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals(oldVal)) {
                btnLexical.setDisable(newVal.trim().isEmpty());
                btnSyntax.setDisable(true);
                btnSemantic.setDisable(true);
            }
        });
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java & Text Files", "*.java", "*.txt"));

        Stage stage = (Stage) btnOpen.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                String fileContent = fileManager.readFile(selectedFile);
                txtCode.setText(fileContent);
                txtResult.setText("File loaded: " + selectedFile.getName());

                // Reset buttons for new content
                btnLexical.setDisable(false);
                btnSyntax.setDisable(true);
                btnSemantic.setDisable(true);
            } catch (IOException ex) {
                txtResult.setText("Error reading file: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleLexical() {
        // CRITICAL FIX: Read exactly what is on the screen right now
        String currentSourceCode = txtCode.getText();

        if (currentSourceCode.trim().isEmpty()) {
            txtResult.setText("Source code is empty.");
            return;
        }

        tokenList = lexicalAnalyzer.analyze(currentSourceCode);
        txtResult.setText(lexicalAnalyzer.getResultLog());

        btnLexical.setDisable(true);
        if (!tokenList.isEmpty()) {
            btnSyntax.setDisable(false);
        } else {
            txtResult.appendText("\nLexical Analysis failed or produced no tokens.");
        }
    }

    @FXML
    private void handleSyntax() {
        boolean isValid = syntaxAnalyzer.analyze(tokenList);
        txtResult.setText(syntaxAnalyzer.getResultLog());

        btnSyntax.setDisable(true);
        if (isValid) {
            btnSemantic.setDisable(false);
        }
    }

    @FXML
    private void handleSemantic() {
        semanticAnalyzer.analyze(tokenList);
        txtResult.setText(semanticAnalyzer.getResultLog());
        btnSemantic.setDisable(true);
    }

    @FXML
    private void handleClear() {
        txtResult.clear();
        txtCode.clear();
        tokenList.clear();
        resetButtonState();
    }

    private void resetButtonState() {
        btnOpen.setDisable(false);
        btnLexical.setDisable(true);
        btnSyntax.setDisable(true);
        btnSemantic.setDisable(true);
        btnClear.setDisable(false);
    }
}