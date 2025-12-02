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

        // REGEX EXPLANATION:
        // 1. \".*?\"                    -> Matches Strings like "Hello World"
        // 2. \'.*?\'                    -> Matches Chars like 'a'
        // 3. [a-zA-Z_][a-zA-Z0-9_]* -> Matches Identifiers (x, myVar)
        // 4. -?\d+(\.\d+)?              -> Matches Numbers (10, -5, 3.14)
        // 5. [=;{}\\(\\)]               -> Matches Symbols (= ; { } ( ))
        // 6. \S                         -> Matches any other non-space char
        Pattern pattern = Pattern.compile("\".*?\"|'.*?'|[a-zA-Z_][a-zA-Z0-9_]*|-?\\d+(\\.\\d+)?|[=;{}\\(\\)]|\\S");
        Matcher matcher = pattern.matcher(sourceCode);

        while (matcher.find()) {
            String part = matcher.group().trim();
            if (part.isEmpty()) continue;

            String tokenType = identifyToken(part);
            Token token = new Token(part, tokenType);
            tokens.add(token);

            resultLog.append(token.toString()).append("\n");
        }
        return tokens;
    }

    public String getResultLog() {
        return resultLog.toString();
    }

    private String identifyToken(String str) {
        if (str.matches("int|String|double|float|char|boolean")) return "KEYWORD_TYPE";
        if (str.matches("package|import|public|private|protected|class|static|void|main|new|return")) return "KEYWORD_IGNORE";
        if (str.equals("=")) return "OPERATOR_ASSIGN";
        if (str.equals(";")) return "DELIMITER";
        if (str.matches("[\\{\\}\\(\\)\\[\\]]")) return "DELIMITER_BLOCK";
        if (str.matches("\".*\"")) return "LITERAL_STRING";
        if (str.matches("'.*'")) return "LITERAL_CHAR";
        if (str.matches("-?\\d+(\\.\\d+)?")) return "LITERAL_NUMBER";
        if (str.matches("true|false")) return "LITERAL_BOOLEAN";
        if (str.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return "IDENTIFIER";
        return "UNKNOWN";
    }
}