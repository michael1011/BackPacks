@echo off

:loop
java -Xmx1G -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-1.8.8.jar
PAUSE
goto loop