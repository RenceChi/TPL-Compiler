import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CompilerController {

    // Link elements from FXML using their fx:id
    @FXML private Button btnOpen;
    @FXML private Button btnLexical;
    @FXML private Button btnSyntax;
    @FXML private Button btnSemantic;
    @FXML private Button btnClear;
    @FXML private TextArea txtResult;
    @FXML private TextArea txtCode;

    // Logic Classes
    private FileManager fileManager;
    private LexicalAnalyzer lexicalAnalyzer;
    private SyntaxAnalyzer syntaxAnalyzer;
    private SemanticAnalyzer semanticAnalyzer;

    // Data State
    private String sourceCode = "";
    private List<Token> tokenList = new ArrayList<>();

    // This method runs automatically when the FXML is loaded
    @FXML
    public void initialize() {
        fileManager = new FileManager();
        lexicalAnalyzer = new LexicalAnalyzer();
        syntaxAnalyzer = new SyntaxAnalyzer();
        semanticAnalyzer = new SemanticAnalyzer();
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java & Text Files", "*.java", "*.txt"));

        // Get the stage from one of the buttons to show the dialog
        Stage stage = (Stage) btnOpen.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                sourceCode = fileManager.readFile(selectedFile);
                txtCode.setText(sourceCode);
                txtResult.setText("File loaded successfully.");

                // Update UI State
                btnLexical.setDisable(false);
                btnClear.setDisable(false);
                btnOpen.setDisable(false);
                btnSyntax.setDisable(true);
                btnSemantic.setDisable(true);
            } catch (IOException ex) {
                txtResult.setText("Error reading file: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void handleLexical() {
        tokenList = lexicalAnalyzer.analyze(sourceCode);
        txtResult.setText(lexicalAnalyzer.getResultLog());

        btnLexical.setDisable(true);
        if (!tokenList.isEmpty()) {
            btnSyntax.setDisable(false);
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
        sourceCode = "";
        tokenList.clear();

        btnOpen.setDisable(false);
        btnLexical.setDisable(true);
        btnSyntax.setDisable(true);
        btnSemantic.setDisable(true);
        btnClear.setDisable(false);
    }
}