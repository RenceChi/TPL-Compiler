package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
    private StringBuilder resultLog;

    public LexicalAnalyzer() {

        this.resultLog = new StringBuilder();
    }
    public List<Token> analyze(String sourceCode) {

        resultLog.setLength(0);
        resultLog.append("--- Lexical Analysis Results ---\n");

        List<Token> tokens = new ArrayList<>();

        // REGEX used to match lexemes (pieces of code)
        // 1. ".*?"    → matches string literals
        // 2. '.*?'    → matches char literals
        // 3. [a-zA-Z_][a-zA-Z0-9_]* → identifiers & keywords
        // 4. -?\\d+(\\.\\d+)? → integer or decimal numbers
        // 5. [=;{}()\\[\\]] → operators & punctuation
        // 6. \\S → any other non-space character
        Pattern pattern = Pattern.compile("\".*?\"|'.*?'|[a-zA-Z_][a-zA-Z0-9_]*|-?\\d+(\\.\\d+)?|[=;{}()\\[\\]]|\\S");

        // Matcher will scan the entire source code
        Matcher matcher = pattern.matcher(sourceCode);
        while (matcher.find()) {
            String lexeme = matcher.group();
            String type = identifyToken(lexeme);
            Token token = new Token(lexeme, type);
            tokens.add(token);
            resultLog.append(token.toString()).append("\n");
        }

        return tokens;
    }

    public String getResultLog() {
        return resultLog.toString();
    }

   //Classifies each lexeme into a token type.
    private String identifyToken(String str) {

        if (str.matches("int|String|double|float|char|boolean"))
            return "DATA_TYPE";

        if (str.matches("package|import|public|private|protected|class|static|void|main|new|return"))
            return "KEYWORD_IGNORE";

        if (str.equals("="))
            return "ASSIGNMENT_OPERATOR";

        if (str.equals(";"))
            return "DELIMITER";

        if (str.matches("[{}()\\[\\]]"))
            return "DELIMITER_BLOCK";

        if (str.matches("\".*\""))
            return "LITERAL_STRING";

        if (str.matches("'.*'"))
            return "LITERAL_CHAR";

        if (str.matches("-?\\d+(\\.\\d+)?"))
            return "LITERAL_NUMBER";

        if (str.matches("true|false"))
            return "LITERAL_BOOLEAN";

        if (str.matches("[a-zA-Z_][a-zA-Z0-9_]*"))
            return "IDENTIFIER";

        return "UNKNOWN";
    }
}
