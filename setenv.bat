chcp 65001
@REM set JAVA_HOME=C:\jdk\jdk-11

set TODAY=%DATE:~0,4%%DATE:~5,2%%DATE:~8,2%
set TOTIME=%TIME: =0%
set TOTIME=%TOTIME:~0,2%%TIME:~3,2%%TIME:~6,2%

set JAVA_OPTS=-server
set JAVA_OPTS=%JAVA_OPTS% -Xms256m
set JAVA_OPTS=%JAVA_OPTS% -Xmx2g
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Duser.timezone=Asia/Seoul
set JAVA_OPTS=%JAVA_OPTS% -Dspring.profiles.active=local
@REM set JAVA_OPTS=%JAVA_OPTS% -Dspring.config.name=demo
@REM set JAVA_OPTS=%JAVA_OPTS% -Dhazelcast.socket.bind.any=false

set VERSION=1.0.0
