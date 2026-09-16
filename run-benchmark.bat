@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo [1/3] Building FastAIMatcher library...
call mvn clean install -DskipTests -q
if %errorlevel% neq 0 ( echo [ERROR] FastAIMatcher build failed! & pause & exit /b 1 )

echo [2/3] Building Benchmark Uber-JAR...
cd examples\Benchmark
call mvn clean package -DskipTests -q
if %errorlevel% neq 0 ( echo [ERROR] Benchmark packaging failed! & pause & exit /b 1 )

echo [3/3] Running JMH Benchmarks...
powershell -Command "Unblock-File -Path target\benchmarks.jar" 2>nul
java -jar target\benchmarks.jar -f 1 -wi 2 -i 3 -tu ms -bm thrpt

cd ..\..
pause
