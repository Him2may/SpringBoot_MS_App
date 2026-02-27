@echo off
setlocal

REM === VERIFY MAVEN ===
mvn -v >nul 2>&1
if errorlevel 1 (
    echo ERROR: Maven is not available in PATH
    pause
    exit /b 1
)
REM === CHANGE THESE PATHS ===
set CONFIG=E:\Projects\Spring Projects\workspace-spring\Spring Boot MS\com.config-service.app
set EMP=E:\Projects\Spring Projects\workspace-spring\Spring Boot MS\com.employee-service.app
set DEPART=E:\Projects\Spring Projects\workspace-spring\Spring Boot MS\com.department-service.app

REM === BUILD CONFIG SERVICE ===
echo.
echo ===== Building CONFIG SERVICE =====
cd /d "%CONFIG%" || goto :error
call mvn clean package -DskipTests || goto :error

REM === BUILD EMP SERVICE ===
echo.
echo ===== Building EMP SERVICE =====
cd /d "%EMP%" || goto :error
call mvn clean package -DskipTests || goto :error

REM === BUILD DEPART SERVICE ===
echo.
echo ===== Building DEPART SERVICE =====
cd /d "%DEPART%" || goto :error
call mvn clean package -DskipTests || goto :error

echo.
echo ✅ All services built successfully!
pause
exit /b 0

:error
echo.
echo ❌ Build failed!
pause
exit /b 1