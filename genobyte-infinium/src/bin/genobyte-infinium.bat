@ECHO OFF
cd ..

set JARS=
for %%d in (. lib .. ..\lib) do for %%i in (%%d\*.jar) do call bin\cp-append.bat %%i
set CLASSPATH=%LIBS%;%JARS%

java -Xmx1800M -cp %CLASSPATH% org.obiba.illumina.bitwise.InfiniumApp
