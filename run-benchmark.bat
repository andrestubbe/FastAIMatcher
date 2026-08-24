@echo off
setlocal
cd /d "%~dp0"
echo ===================================================
echo  Building FastAIMatcher JMH Benchmarks Uber-Jar
echo ===================================================

call "C:\Users\andre\tools\apache-maven-3.9.9\bin\mvn.cmd" install -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo FastAIMatcher install failed!
    exit /b %ERRORLEVEL%
)

cd examples\Benchmark
call "C:\Users\andre\tools\apache-maven-3.9.9\bin\mvn.cmd" clean package
if %ERRORLEVEL% NEQ 0 (
    echo Benchmark package failed!
    exit /b %ERRORLEVEL%
)

echo ===================================================
echo  Running JMH Benchmarks
echo ===================================================
java -jar target\benchmarks.jar -f 1 -wi 2 -i 3 -tu ms -bm thrpt
pause
