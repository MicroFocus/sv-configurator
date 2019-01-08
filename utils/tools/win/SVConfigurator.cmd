@ECHO OFF

SET "_COMMAND=%~dp0SVConfigurator.jar"
SET "_JAVA_COMMAND="
SET "_JAVA_REL_PATH=bin\java.exe"
SET "_HP_JAVA_64_COMMAND=%~dp0..\..\..\jre_x64\%_JAVA_REL_PATH%"

SET "SVCONF_WAIT_MS=30000"

IF EXIST "%_HP_JAVA_64_COMMAND%" (
    SET "_JAVA_COMMAND=%_HP_JAVA_64_COMMAND%"
    goto runJava
)

java -version >nul 2>&1 && (
    SET "_JAVA_COMMAND=java"
    goto runJava
)

SET "_JAVA_HOME_COMMAND=%JAVA_HOME%\%_JAVA_REL_PATH%"
IF EXIST "%_JAVA_HOME_COMMAND%" (
    SET "_JAVA_COMMAND=%_JAVA_HOME_COMMAND%"
    goto runJava
)

goto noJava





:runJava
    IF EXIST "%_COMMAND%" (
        "%_JAVA_COMMAND%" -Dfile.encoding=UTF-8 -Dsvconf_wait_ms="%SVCONF_WAIT_MS%" -jar "%_COMMAND%" %*
        goto end
    ) ELSE (
        echo Could not find "SVConfigurator.jar". Is it in the same location as this .cmd file?
        goto end
    )

:noJava
    echo Could not find Java. Please, add the bin directory of Java installation into PATH environment variable or set the JAVA_HOME environment variable.
    goto end

:end