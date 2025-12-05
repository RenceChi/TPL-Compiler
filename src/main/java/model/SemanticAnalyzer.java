package model;
import java.util.List;

public class SemanticAnalyzer {

    private StringBuilder resultLog;

    public SemanticAnalyzer() {
        this.resultLog = new StringBuilder();
    }

    public boolean analyze(List<Token> tokens) {

        // Reset logs
        resultLog.setLength(0);
        resultLog.append("--- Semantic Analysis Results ---\n");

        boolean isSemanticValid = true;
        boolean foundVariables = false;

        for (int i = 0; i < tokens.size(); i++) {

            Token t = tokens.get(i);

            if (t.getType().equals("DATA_TYPE")) {

                // Find semicolon marking end of declaration
                int semiColonIndex = -1;

                for (int j = i + 1; j < tokens.size(); j++) {
                    if (tokens.get(j).getValue().equals(";")) {
                        semiColonIndex = j;
                        break;
                    }
                }

                if (semiColonIndex == -1)
                    continue;

                foundVariables = true;

                // Find '=' inside declaration (if any)
                int assignIndex = -1;

                for (int k = i + 1; k < semiColonIndex; k++) {
                    if (tokens.get(k).getType().equals("ASSIGNMENT_OPERATOR")) {
                        assignIndex = k;
                        break;
                    }
                }

                String declType = t.getValue();
                String varName = tokens.get(i + 1).getValue();

                // CASE A — Declaration WITH initialization (int x = ...)
                if (assignIndex != -1) {

                    // Ensure there is a value after '='
                    if (assignIndex + 1 < semiColonIndex) {

                        Token valToken = tokens.get(assignIndex + 1);
                        String valueType = valToken.getType();
                        String valueVal = valToken.getValue();

                        boolean mismatch = false;

                        // Apply type rules
                        if (declType.equals("int") &&
                                (valueVal.contains(".") || !valueType.equals("LITERAL_NUMBER")))
                            mismatch = true;

                        else if (declType.equals("double") &&
                                !valueType.equals("LITERAL_NUMBER"))
                            mismatch = true;

                        else if (declType.equals("String") &&
                                !valueType.equals("LITERAL_STRING"))
                            mismatch = true;

                        else if (declType.equals("boolean") &&
                                !valueType.equals("LITERAL_BOOLEAN"))
                            mismatch = true;

                        else if (declType.equals("char") &&
                                !valueType.equals("LITERAL_CHAR"))
                            mismatch = true;

                        // Report mismatch
                        if (mismatch) {
                            resultLog.append("Semantic Error: Type mismatch for '")
                                    .append(varName)
                                    .append("'. Cannot assign ")
                                    .append(valueType)
                                    .append(" to ").append(declType)
                                    .append(".\n");
                            isSemanticValid = false;
                        }
                        else {
                            resultLog.append("Variable '")
                                    .append(varName)
                                    .append("' is semantically valid.\n");
                        }

                    } else {

                        // No value after '=' → semantic error
                        resultLog.append("Semantic Error: No value found after '=' for '")
                                .append(varName).append("'.\n");
                        isSemanticValid = false;
                    }

                } else {

                    // CASE B — Declaration WITHOUT initialization
                    resultLog.append("Variable '").append(varName)
                            .append("' declared (no value to check).\n");
                }

                // Skip to end of statement
                i = semiColonIndex;
            }
        }

        // If no variables at all
        if (!foundVariables) {
            resultLog.append("No variables to analyze.");
        }
        // If everything passed
        else if (isSemanticValid) {
            resultLog.append("\nSemantic Analysis Passed. Code is executable.");
        }

        return isSemanticValid;
    }

    // Allow controller to retrieve logs
    public String getResultLog() {
        return resultLog.toString();
    }
}
