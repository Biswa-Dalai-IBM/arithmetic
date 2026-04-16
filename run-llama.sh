#!/bin/bash
# Llama Arithmetic Grammar Teacher Launcher
# Usage: ./run-llama.sh "<expression>" [model] [apiUrl] [grammarFile]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$SCRIPT_DIR/arithmetic-1.0-SNAPSHOT.jar"
LIB_DIR="$SCRIPT_DIR/lib"
GRAMMAR_FILE="$SCRIPT_DIR/grammar/com/example/arithmetic/antlr/Arithmetic.g4"

if [ -z "$1" ]; then
    echo "Usage: $0 \"<expression>\" [model] [apiUrl] [grammarFile]"
    echo ""
    echo "Arguments:"
    echo "  expression    Natural language arithmetic request"
    echo "  model         Optional Llama/Ollama model, default is llama3.1"
    echo "  apiUrl        Optional Llama/Ollama API URL, default is http://localhost:11434/api/chat"
    echo "  grammarFile   Optional path to Arithmetic.g4"
    echo ""
    echo "Examples:"
    echo "  $0 \"add five and three\""
    echo "  $0 \"multiply seven by four, then divide by two\" llama3.1"
    echo "  $0 \"subtract three from ten and multiply by two\" llama3.1 http://localhost:11434/api/chat \"C:/MyDevelopment/ANTLR/arithmetic/src/main/antlr4/com/example/arithmetic/antlr/Arithmetic.g4\""
    exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found: $JAR_FILE"
    echo "Please build the distribution first."
    exit 1
fi

if [ ! -f "$GRAMMAR_FILE" ]; then
    echo "Error: Grammar file not found: $GRAMMAR_FILE"
    exit 1
fi

CLASSPATH="$JAR_FILE"
for jar in "$LIB_DIR"/*.jar; do
    if [ -f "$jar" ]; then
        CLASSPATH="$CLASSPATH:$jar"
    fi
done

if [ -n "$4" ]; then
    GRAMMAR_FILE="$4"
fi

if [ ! -f "$GRAMMAR_FILE" ]; then
    echo "Error: Grammar file not found: $GRAMMAR_FILE"
    exit 1
fi

java -cp "$CLASSPATH" com.example.arithmetic.LlamaArithmeticGrammarTeacher "$GRAMMAR_FILE" "$1" "${2:-llama3.1}" "${3:-http://localhost:11434/api/chat}"

# Made with Bob
