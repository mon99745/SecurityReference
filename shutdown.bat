@REM usage: shutdown demo

set demo=%1
set /P PID=<%MODULE_NAME%.pid

taskkill /f /pid %PID%
