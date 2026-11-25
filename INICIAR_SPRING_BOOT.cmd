@echo off
setlocal
cd /d "%~dp0"

if defined JAVA_HOME (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) else (
    set "JAVA_EXE=java"
)

"%JAVA_EXE%" -version 2>&1 | findstr /C:"17." >nul
if errorlevel 1 (
    echo ERROR: Este proyecto debe ejecutarse con Java 17 (Amazon Corretto 17).
    echo Configura JAVA_HOME para que apunte a tu instalacion de Corretto 17.
    echo En IntelliJ, selecciona tambien Corretto 17 como Project SDK y Maven Runner.
    pause
    exit /b 1
)

if not exist "target\Farmacias_URB_SpringBoot-1.0-SNAPSHOT.jar" (
    echo No se encontro el JAR ejecutable. Abre el proyecto en IntelliJ y ejecuta Maven: clean package.
    pause
    exit /b 1
)

echo Iniciando Farmacias URB con Java 17...
echo La aplicacion necesita que MySQL este iniciado y configurado en application.properties.
echo.
"%JAVA_EXE%" -jar "target\Farmacias_URB_SpringBoot-1.0-SNAPSHOT.jar"

pause
