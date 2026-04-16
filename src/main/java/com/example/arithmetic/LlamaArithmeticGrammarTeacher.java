package com.example.arithmetic;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;

import com.example.arithmetic.antlr.ArithmeticBaseVisitor;
import com.example.arithmetic.antlr.ArithmeticLexer;
import com.example.arithmetic.antlr.ArithmeticParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Teaches arithmetic grammar to Llama-compatible chat API using hybrid approach:
 * 1. Llama converts natural language to formal syntax
 * 2. ANTLR validates the generated expression
 * 3. Feedback loop if validation fails
 *
 * Default endpoint is Ollama local chat API.
 */
public class LlamaArithmeticGrammarTeacher {

    private static final String DEFAULT_LLAMA_API_URL = "http://localhost:11434/api/chat";
    private final String apiUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final int maxRetries;
    private final String grammarContent;
    private final String model;

    public LlamaArithmeticGrammarTeacher(String grammarFilePath) throws Exception {
        this(grammarFilePath, 3, "gemma3.1b", DEFAULT_LLAMA_API_URL);
    }

    public LlamaArithmeticGrammarTeacher(String grammarFilePath, int maxRetries) throws Exception {
        this(grammarFilePath, maxRetries, "gemma3.1b", DEFAULT_LLAMA_API_URL);
    }

    public LlamaArithmeticGrammarTeacher(String grammarFilePath, int maxRetries, String model, String apiUrl) throws Exception {
        this.maxRetries = maxRetries;
        this.model = model;
        this.apiUrl = apiUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.grammarContent = loadGrammarFile(grammarFilePath);
    }

    private String loadGrammarFile(String grammarFilePath) throws Exception {
        Path path = Paths.get(grammarFilePath);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Grammar file not found: " + grammarFilePath);
        }
        return Files.readString(path);
    }

    public ParseResult teachGrammarToLLM(String userInput) throws Exception {
        return teachGrammarToLLM(userInput, 0);
    }

    private ParseResult teachGrammarToLLM(String userInput, int attemptCount) throws Exception {
        if (attemptCount >= maxRetries) {
            return new ParseResult(false, null, null,
                "Max retries exceeded. Could not generate valid expression.");
        }

        String llmOutput = generateFormalSyntax(userInput, attemptCount);

        try {
            ParseTree parseTree = parseExpression(llmOutput);
            return new ParseResult(true, llmOutput, parseTree, null);
        } catch (Exception e) {
            System.out.println("Attempt " + (attemptCount + 1) + " failed: " + e.getMessage());
            String correctionPrompt = buildCorrectionPrompt(llmOutput, e.getMessage());
            return teachGrammarToLLM(correctionPrompt, attemptCount + 1);
        }
    }

    private String generateFormalSyntax(String userInput, int attemptCount) throws Exception {
        String systemPrompt = buildSystemPrompt();
        String userPrompt = attemptCount == 0
            ? buildInitialUserPrompt(userInput)
            : userInput;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("stream", false);
        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", systemPrompt),
            Map.of("role", "user", "content", userPrompt)
        });

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiUrl))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
            .build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Llama API error: " + response.body());
        }

        JsonNode jsonResponse = objectMapper.readTree(response.body());
        JsonNode message = jsonResponse.get("message");
        if (message == null || message.get("content") == null) {
            throw new RuntimeException("Llama response missing message content: " + response.body());
        }

        String content = message.get("content").asText();
        return extractExpression(content);
    }

    private String buildSystemPrompt() {
        return String.format("""
            You are an arithmetic expression converter. Convert natural language to formal arithmetic syntax.
            
            You MUST follow this ANTLR4 grammar exactly:
            
            ```
            %s
            ```
            
            KEY RULES FROM GRAMMAR:
            - Use keywords: plus, subtract, multiply, divide (NOT symbols like +, -, *, /)
            - Numbers: integers or decimals matching [0-9]+ ('.' [0-9]+)?
            - Parentheses: Use () for grouping expressions
            - Operator precedence: multiply/divide have higher precedence than plus/subtract
            - Whitespace: Flexible spacing between tokens
            
            VALID EXAMPLES:
            - "5 plus 3"
            - "10 multiply 2 subtract 4"
            - "(8 plus 2) divide 5"
            - "3.14 multiply 2.5"
            
            INVALID EXAMPLES:
            - "5 + 3" (use "plus" not "+")
            - "5 * 3" (use "multiply" not "*")
            - "5plus3" (need spaces)
            - ".5 plus 3" (numbers must start with digit)
            
            OUTPUT: Only the formal expression that matches the grammar, nothing else.
            """, grammarContent);
    }

    private String buildInitialUserPrompt(String userInput) {
        return String.format("""
            Convert this to formal arithmetic syntax using keywords (plus, subtract, multiply, divide):
            
            Input: %s
            
            Output only the formal expression:
            """, userInput);
    }

    private String buildCorrectionPrompt(String invalidExpression, String errorMessage) {
        return String.format("""
            The expression you generated is INVALID:
            Expression: %s
            Error: %s
            
            Fix it according to Arithmetic.g4 grammar rules:
            - Use keywords: plus, subtract, multiply, divide (NOT symbols)
            - Numbers must be valid (digits with optional decimal)
            - Use spaces between tokens
            - Respect operator precedence
            
            Output only the corrected expression:
            """, invalidExpression, errorMessage);
    }

    private String extractExpression(String llmResponse) {
        return llmResponse.trim()
            .replaceAll("(?i)^(output:|expression:|result:)\\s*", "")
            .replaceAll("(?i)^(the formal expression is:?)\\s*", "")
            .split("\n")[0]
            .trim();
    }

    private ParseTree parseExpression(String expression) throws Exception {
        CharStream input = CharStreams.fromString(expression);
        ArithmeticLexer lexer = new ArithmeticLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticParser parser = new ArithmeticParser(tokens);

        parser.removeErrorListeners();
        ThrowingErrorListener errorListener = new ThrowingErrorListener();
        parser.addErrorListener(errorListener);

        return parser.prog();
    }

    public double evaluate(ParseTree tree) {
        EvalVisitor visitor = new EvalVisitor();
        return visitor.visit(tree);
    }

    private static class ThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                              int line, int charPositionInLine, String msg,
                              RecognitionException e) {
            throw new RuntimeException("Syntax error at line " + line + ":" +
                charPositionInLine + " - " + msg);
        }
    }

    private static class EvalVisitor extends ArithmeticBaseVisitor<Double> {
        @Override
        public Double visitProg(ArithmeticParser.ProgContext ctx) {
            return visit(ctx.expr());
        }

        @Override
        public Double visitNumber(ArithmeticParser.NumberContext ctx) {
            return Double.parseDouble(ctx.NUMBER().getText());
        }

        @Override
        public Double visitMulDiv(ArithmeticParser.MulDivContext ctx) {
            double left = visit(ctx.expr(0));
            double right = visit(ctx.expr(1));
            String op = ctx.op.getText();

            if (op.equals("multiply")) {
                return left * right;
            } else {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return left / right;
            }
        }

        @Override
        public Double visitAddSub(ArithmeticParser.AddSubContext ctx) {
            double left = visit(ctx.expr(0));
            double right = visit(ctx.expr(1));
            String op = ctx.op.getText();

            if (op.equals("plus")) {
                return left + right;
            } else {
                return left - right;
            }
        }

        @Override
        public Double visitParens(ArithmeticParser.ParensContext ctx) {
            return visit(ctx.expr());
        }
    }

    public static class ParseResult {
        private final boolean valid;
        private final String expression;
        private final ParseTree parseTree;
        private final String error;

        public ParseResult(boolean valid, String expression, ParseTree parseTree, String error) {
            this.valid = valid;
            this.expression = expression;
            this.parseTree = parseTree;
            this.error = error;
        }

        public boolean isValid() { return valid; }
        public String getExpression() { return expression; }
        public ParseTree getParseTree() { return parseTree; }
        public String getError() { return error; }

        @Override
        public String toString() {
            if (valid) {
                return String.format("Valid: %s\nParse Tree: %s", expression, parseTree.toStringTree());
            } else {
                return String.format("Invalid: %s", error);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java LlamaArithmeticGrammarTeacher <GRAMMAR_FILE> <expression> [model] [apiUrl]");
            System.out.println("Example: java LlamaArithmeticGrammarTeacher Arithmetic.g4 \"add five and three\" llama3.1 http://localhost:11434/api/chat");
            return;
        }

        String grammarFile = args[0];
        String userInput = args[1];
        String model = args.length >= 3 ? args[2] : "llama3.1";
        String apiUrl = args.length >= 4 ? args[3] : DEFAULT_LLAMA_API_URL;

        try {
            LlamaArithmeticGrammarTeacher teacher =
                new LlamaArithmeticGrammarTeacher(grammarFile, 3, model, apiUrl);

            System.out.println("Input: " + userInput);
            System.out.println("Processing with Llama...\n");

            ParseResult result = teacher.teachGrammarToLLM(userInput);

            if (result.isValid()) {
                System.out.println("✓ Success!");
                System.out.println("Formal Expression: " + result.getExpression());
                System.out.println("Parse Tree: " + result.getParseTree().toStringTree(new ArithmeticParser(null)));

                double value = teacher.evaluate(result.getParseTree());
                System.out.println("Result: " + value);
            } else {
                System.out.println("✗ Failed!");
                System.out.println("Error: " + result.getError());
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

// Made with Bob
