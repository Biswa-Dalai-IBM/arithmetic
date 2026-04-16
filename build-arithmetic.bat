@echo off
REM Build Arithmetic ANTLR project with Maven
REM Usage: build-arithmetic.bat

setlocal

cd /d "%~dp0"

echo Generating parser and compiling project for:
echo   %CD%\src\main\antlr4\com\example\arithmetic\antlr\Arithmetic.g4
echo.

mvn clean generate-sources compile

if errorlevel 1 (
    echo.
    echo Build failed.
    exit /b 1
)

echo.
echo Build successful.
echo Generated sources are under target\generated-sources\antlr4
echo.

endlocal

@REM Made with Bob
