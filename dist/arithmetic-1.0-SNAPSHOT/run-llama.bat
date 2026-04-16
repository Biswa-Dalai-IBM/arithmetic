@echo off
REM Llama Arithmetic Grammar Teacher Launcher - Windows
REM Usage: run-llama.bat "<expression>" [model] [apiUrl] [grammarFile]

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "JAR_FILE=%SCRIPT_DIR%arithmetic-1.0-SNAPSHOT.jar"
set "LIB_DIR=%SCRIPT_DIR%lib"
set "GRAMMAR_FILE=%SCRIPT_DIR%grammar\com\example\arithmetic\antlr\Arithmetic.g4"

if "%~1"=="" (
    echo Usage: %~nx0 ^"<expression^>" [model] [apiUrl] [grammarFile]
    echo.
    echo Arguments:
    echo   expression    Natural language arithmetic request
    echo   model         Optional Llama/Ollama model, default is llama3.1
    echo   apiUrl        Optional Llama/Ollama API URL, default is http://localhost:11434/api/chat
    echo   grammarFile   Optional path to Arithmetic.g4
    echo.
    echo Examples:
    echo   %~nx0 "add five and three"
    echo   %~nx0 "multiply seven by four, then divide by two" llama3.1
    echo   %~nx0 "subtract three from ten and multiply by two" llama3.1 http://localhost:11434/api/chat "C:\MyDevelopment\ANTLR\arithmetic\src\main\antlr4\com\example\arithmetic\antlr\Arithmetic.g4"
    exit /b 1
)

if not exist "%JAR_FILE%" (
    echo Error: JAR file not found: %JAR_FILE%
    echo Please build the distribution first.
    exit /b 1
)

if not exist "%GRAMMAR_FILE%" (
    echo Error: Grammar file not found: %GRAMMAR_FILE%
    exit /b 1
)

set "JAVA_CMD=java"
if defined JAVA_HOME (
    set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
)

"%JAVA_CMD%" -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not accessible
    exit /b 1
)

set "CLASSPATH=%JAR_FILE%"
for %%f in ("%LIB_DIR%\*.jar") do (
    set "CLASSPATH=!CLASSPATH!;%%f"
)

if not "%~4"=="" (
    set "GRAMMAR_FILE=%~4"
)

if not exist "%GRAMMAR_FILE%" (
    echo Error: Grammar file not found: %GRAMMAR_FILE%
    exit /b 1
)

if "%~2"=="" (
    "%JAVA_CMD%" -cp "%CLASSPATH%" com.example.arithmetic.LlamaArithmeticGrammarTeacher "%GRAMMAR_FILE%" %1
) else if "%~3"=="" (
    "%JAVA_CMD%" -cp "%CLASSPATH%" com.example.arithmetic.LlamaArithmeticGrammarTeacher "%GRAMMAR_FILE%" %1 %2
) else (
    "%JAVA_CMD%" -cp "%CLASSPATH%" com.example.arithmetic.LlamaArithmeticGrammarTeacher "%GRAMMAR_FILE%" %1 %2 %3
)

endlocal

@REM Made with Bob
