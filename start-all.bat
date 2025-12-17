@echo off
chcp 65001 >nul
echo Starting full-stack application...

REM Check if required directories exist
if not exist "redis" (
    echo Error: redis directory not found
    pause
    exit /b 1
)

if not exist "xiangmu" (
    echo Error: xiangmu directory not found
    pause
    exit /b 1
)

if not exist "manger" (
    echo Error: manger directory not found
    pause
    exit /b 1
)

REM 1. Start Redis
echo Starting Redis...
cd redis
start "Redis Server" redis-server.exe
cd ..

REM Wait for Redis to start
timeout /t 3 /nobreak >nul

REM 2. Start Frontend
echo Installing frontend dependencies and starting dev server...
cd xiangmu
start "Frontend" cmd /k "npm install && npm run dev"
cd ..

REM Wait for frontend to start installing
timeout /t 10 /nobreak >nul

REM 3. Start Backend
echo Installing backend dependencies and starting Spring Boot...
cd manger
start "Backend" cmd /k "mvn dependency:resolve && mvn spring-boot:run"
cd ..

echo All services started successfully!
echo.
echo Service Information:
echo   - Redis: running in redis directory
echo   - Frontend: running in new command window
echo   - Backend: running in new command window
echo.
echo Please visit: http://localhost:3000
echo.
echo Press any key to close this script...
pause >nul