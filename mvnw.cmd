@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper Startup Batch script for Windows
@REM ----------------------------------------------------------------------------
@IF "%DEBUG%"=="" @ECHO OFF
@SETLOCAL

SET "ERROR_CODE=0"
SET "DIRNAME=%~dp0"
IF "%DIRNAME%"=="" SET "DIRNAME=."

SET "WRAPPER_JAR=%DIRNAME%\.mvn\wrapper\maven-wrapper.jar"

where mvn >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
  mvn %*
  EXIT /B %ERRORLEVEL%
)

IF NOT EXIST "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper...
  powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar', '%WRAPPER_JAR%')"
)

IF EXIST "%WRAPPER_JAR%" (
  java -Dmaven.multiModuleProjectDirectory="%DIRNAME%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
) ELSE (
  echo Error: Could not download maven-wrapper.jar. Please install Maven or check internet connection.
  EXIT /B 1
)
