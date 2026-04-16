@echo off
REM Claude Arithmetic Grammar Teacher Launcher - Windows
REM Usage: run-claude.bat <API_KEY> "<expression>" [grammarFile]

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"
set "JAR_FILE=%SCRIPT_DIR%arithmetic-1.0-SNAPSHOT.jar"
set "LIB_DIR=%SCRIPT_DIR%lib"
set "GRAMMAR_FILE=%SCRIPT_DIR%grammar\com\example\arithmetic\antlr\Arithmetic.g4"

if "%~1"=="" (
    echo Usage: %~nx0 ^<API_KEY^> ^"<expression^>" [grammarFile]
    echo.
    echo Arguments:
    echo   API_KEY       Claude API key
    echo   expression    Natural language arithmetic request
    echo   grammarFile   Optional path to Arithmetic.g4
    echo.
    echo Examples:
    echo   %~nx0 sk-ant-xxx "add five and three"
    echo   %~nx0 sk-ant-xxx "multiply twelve by four, then subtract two" "C:\MyDevelopment\ANTLR\arithmetic\src\main\antlr4\com\example\arithmetic\antlr\Arithmetic.g4"
    exit /b 1
)

if "%~2"=="" (
    echo Usage: %~nx0 ^<API_KEY^> ^"<expression^>" [grammarFile]
    echo.
    echo Arguments:
    echo   API_KEY       Claude API key
    echo   expression    Natural language arithmetic request
    echo   grammarFile   Optional path to Arithmetic.g4
    echo.
    echo Examples:
    echo   %~nx0 sk-ant-xxx "add five and three"
    echo   %~nx0 sk-ant-xxx "multiply twelve by four, then subtract two" "C:\MyDevelopment\ANTLR\arithmetic\src\main\antlr4\com\example\arithmetic\antlr\Arithmetic.g4"
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

if not "%~3"=="" (
    set "GRAMMAR_FILE=%~3"
)

if not exist "%GRAMMAR_FILE%" (
    echo Error: Grammar file not found: %GRAMMAR_FILE%
    exit /b 1
)

"%JAVA_CMD%" -cp "%CLASSPATH%" com.example.arithmetic.ArithmeticGrammarTeacher %1 "%GRAMMAR_FILE%" %2

endlocal

@REM Made with Bob
