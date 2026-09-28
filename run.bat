@echo off
cd /d "%~dp0"
echo ========================================
echo   Starting World of Wonder (MVC Game)
echo ========================================

if not exist bin mkdir bin

echo Compiling Java source files with UTF-8 encoding...
javac -encoding UTF-8 -d bin src\com\worldofwonder\*.java src\com\worldofwonder\controller\*.java src\com\worldofwonder\model\*.java src\com\worldofwonder\util\*.java src\com\worldofwonder\view\*.java src\com\worldofwonder\test\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo Launching World of Wonder GUI...
java -cp bin com.worldofwonder.Main
pause
