import java.util.List;

public class SemanticAnalyzer {

    private StringBuilder resultLog;

    public SemanticAnalyzer() {
        this.resultLog = new StringBuilder();
    }

    public boolean analyze(List<Token> tokens) {
        resultLog.setLength(0);
        resultLog.append("--- Semantic Analysis Results ---\n");

        boolean isSemanticValid = true;
        boolean foundVariables = false;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            //Found a Variable Declaration start
            // UPDATED: checking for DATA_TYPE
            if (t.getType().equals("DATA_TYPE")) {

                //Find the end of this statement (the ';')
                int semiColonIndex = -1;
                for (int j = i + 1; j < tokens.size(); j++) {
                    if (tokens.get(j).getValue().equals(";")) {
                        semiColonIndex = j;
                        break;
                    }
                }

                if (semiColonIndex != -1) {
                    foundVariables = true;

                    //Look for an '=' sign inside this statement
                    int assignIndex = -1;
                    for (int k = i + 1; k < semiColonIndex; k++) {
                        // UPDATED: checking for ASSIGNMENT_OPERATOR
                        if (tokens.get(k).getType().equals("ASSIGNMENT_OPERATOR")) {
                            assignIndex = k;
                            break;
                        }
                    }

                    //Handle Logic
                    String declType = t.getValue(); // e.g., "int"
                    String varName = tokens.get(i + 1).getValue(); // e.g., "x"

                    if (assignIndex != -1) {
                        // --- Case A: Initialization (int x = 10;) ---

                        if (assignIndex + 1 < semiColonIndex) {
                            Token valToken = tokens.get(assignIndex + 1);
                            String valueType = valToken.getType();
                            String valueVal = valToken.getValue();

                            boolean mismatch = false;

                            // Check rules
                            if (declType.equals("int") && (valueVal.contains(".") || !valueType.equals("LITERAL_NUMBER"))) mismatch = true;
                            else if (declType.equals("double") && !valueType.equals("LITERAL_NUMBER")) mismatch = true;
                            else if (declType.equals("String") && !valueType.equals("LITERAL_STRING")) mismatch = true;
                            else if (declType.equals("boolean") && !valueType.equals("LITERAL_BOOLEAN")) mismatch = true;
                            else if (declType.equals("char") && !valueType.equals("LITERAL_CHAR")) mismatch = true;
                            else if (declType.equals("float") && !valueType.equals("LITERAL_NUMBER")) mismatch = true;


                            if (mismatch) {
                                resultLog.append("Semantic Error: Type mismatch for '").append(varName)
                                        .append("'. Cannot assign ").append(valueType)
                                        .append(" to ").append(declType).append(".\n");
                                isSemanticValid = false;
                            } else {
                                resultLog.append("Variable '").append(varName).append("' is semantically valid.\n");
                            }
                        } else {
                            resultLog.append("Semantic Error: No value found after '=' for '").append(varName).append("'.\n");
                            isSemanticValid = false;
                        }

                    } else {
                        // --- Case B: Declaration Only (int x;) ---
                        resultLog.append("Variable '").append(varName).append("' declared (no value to check).\n");
                    }

                    // Move the main loop to the end of this statement
                    i = semiColonIndex;
                }
            }
        }

        if (!foundVariables) {
            resultLog.append("No variables to analyze.");
        } else if (isSemanticValid) {
            resultLog.append("\nSemantic Analysis Passed. Code is executable.");
        }

        return isSemanticValid;
    }

    public String getResultLog() {
        return resultLog.toString();
    }
}