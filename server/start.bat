@echo off

:loop
java -Xmx1G -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-1.9.jar
PAUSE
goto loop