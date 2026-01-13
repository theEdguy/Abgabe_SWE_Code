@echo off
REM Starte das Programm, vorausgesetzt Java ist installiert und im PATH

set JAR=swelab-0.0.1-SNAPSHOT-jar-with-dependencies.jar

if not exist "%JAR%" (
    echo Fehler: %JAR% nicht gefunden!
    echo Bitte stelle sicher, dass die Datei im gleichen Ordner wie dieses Skript liegt.
    pause
    exit /b 1
)

java -jar "%JAR%"
pause
