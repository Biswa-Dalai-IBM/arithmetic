#!/bin/bash
# Claude Arithmetic Grammar Teacher Launcher
# Usage: ./run-claude.sh <API_KEY> "<expression>" [grammarFile]

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$SCRIPT_DIR/arithmetic-1.0-SNAPSHOT.jar"
LIB_DIR="$SCRIPT_DIR/lib"
GRAMMAR_FILE="$SCRIPT_DIR/grammar/com/example/arithmetic/antlr/Arithmetic.g4"

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Usage: $0 <API_KEY> \"<expression>\" [grammarFile]"
    echo ""
    echo "Arguments:"
    echo "  API_KEY       Claude API key"
    echo "  expression    Natural language arithmetic request"
    echo "  grammarFile   Optional path to Arithmetic.g4"
    echo ""
    echo "Examples:"
    echo "  $0 sk-ant-xxx \"add five and three\""
    echo "  $0 sk-ant-xxx \"multiply twelve by four, then subtract two\" \"C:/MyDevelopment/ANTLR/arithmetic/src/main/antlr4/com/example/arithmetic/antlr/Arithmetic.g4\""
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

if [ -n "$3" ]; then
    GRAMMAR_FILE="$3"
fi

if [ ! -f "$GRAMMAR_FILE" ]; then
    echo "Error: Grammar file not found: $GRAMMAR_FILE"
    exit 1
fi

java -cp "$CLASSPATH" com.example.arithmetic.ArithmeticGrammarTeacher "$1" "$GRAMMAR_FILE" "$2"

# Made with Bob
