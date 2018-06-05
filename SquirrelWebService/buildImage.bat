@echo off

echo Please don't forget to execute "mvn clean package", if you made some changes!

cd %~dp0
cd target

if EXIST "original-*.jar" (
    del /F SquirrelWebService*.jar
)

copy /B /Y *.jar start.jar
move start.jar ../
cd ..

docker build -t squirrel/webimage .

del start.jar

cd ..