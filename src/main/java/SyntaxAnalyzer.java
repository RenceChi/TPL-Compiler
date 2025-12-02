import java.util.List;

public class SyntaxAnalyzer {

    private StringBuilder resultLog;

    public SyntaxAnalyzer() {
        this.resultLog = new StringBuilder();
    }

    public boolean analyze(List<Token> tokens) {
        resultLog.setLength(0);
        resultLog.append("--- Syntax Analysis Results ---\n");

        boolean atLeastOneValid = false;
        boolean hasErrors = false;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            // 1. Found Start of a potential declaration
            if (t.getType().equals("KEYWORD_TYPE")) {

                // 2. SAFETY CHECK: Ignore types inside method signatures (like 'String[] args')
                // We search for a semicolon, but if we hit '{', '}', or ')' first, we abort.
                int semiColonIndex = -1;
                for (int j = i + 1; j < tokens.size(); j++) {
                    String val = tokens.get(j).getValue();
                    if (val.equals(";")) {
                        semiColonIndex = j;
                        break;
                    }
                    // Stop if we hit a method boundary or parameter list end
                    if (val.equals("{") || val.equals("}") || val.equals(")")) {
                        break;
                    }
                }

                if (semiColonIndex == -1) {
                    // This was likely 'public static void main...' or a parameter. Ignore it.
                    continue;
                }

                // 3. Determine if it's an Array Declaration (e.g., int[] x)
                // If next token is '[' and token after is ']', we skip them.
                int nameIndex = i + 1;
                boolean isArray = false;
                if (nameIndex < tokens.size() && tokens.get(nameIndex).getValue().equals("[")) {
                    if (nameIndex + 1 < tokens.size() && tokens.get(nameIndex + 1).getValue().equals("]")) {
                        nameIndex += 2; // Move identifier expectation forward
                        isArray = true;
                    }
                }

                // 4. Check Declaration Logic
                // We pass 'nameIndex' so the checker knows where the Variable Name is supposed to be
                if (checkStatement(tokens, i, nameIndex, semiColonIndex, isArray)) {
                    atLeastOneValid = true;
                } else {
                    hasErrors = true;
                }

                // 5. Skip ahead
                i = semiColonIndex;
            }
        }

        if (!atLeastOneValid && !hasErrors) {
            resultLog.append("No variable declarations found.\n");
            return false;
        } else if (atLeastOneValid && !hasErrors) {
            resultLog.append("\nSyntax Analysis Passed.");
            return true;
        }

        return atLeastOneValid && !hasErrors;
    }

    private boolean checkStatement(List<Token> tokens, int typeIndex, int nameIndex, int semiColonIndex, boolean isArray) {
        // A. Check Identifier Presence
        if (nameIndex >= semiColonIndex) {
            resultLog.append("Syntax Error: Expected Identifier after type '").append(tokens.get(typeIndex).getValue()).append("'\n");
            return false;
        }

        Token tName = tokens.get(nameIndex);
        if (!tName.getType().equals("IDENTIFIER")) {
            resultLog.append("Syntax Error: Expected Identifier, found '").append(tName.getValue()).append("'\n");
            return false;
        }

        // B. Check what comes after Identifier
        // If Identifier is the last thing before semicolon -> Simple Declaration (int x;)
        if (nameIndex + 1 == semiColonIndex) {
            resultLog.append("Valid Declaration: ").append(tokens.get(typeIndex).getValue())
                    .append(isArray ? "[] " : " ").append(tName.getValue()).append("\n");
            return true;
        }

        // C. If not simple, it must be Initialization (int x = 10;)
        // The token after Identifier MUST be '='
        Token tEq = tokens.get(nameIndex + 1);
        if (!tEq.getType().equals("OPERATOR_ASSIGN")) {
            resultLog.append("Syntax Error: Expected '=' after variable name '").append(tName.getValue()).append("'\n");
            return false;
        }

        // D. Check if there is a value after '='
        if (nameIndex + 2 >= semiColonIndex) {
            resultLog.append("Syntax Error: No value found after '='\n");
            return false;
        }

        resultLog.append("Valid Initialization: ").append(tokens.get(typeIndex).getValue())
                .append(isArray ? "[] " : " ").append(tName.getValue()).append("\n");
        return true;
    }

    public String getResultLog() {
        return resultLog.toString();
    }
}