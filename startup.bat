@REM usage: startup demo

call setenv
set WAR_PATH=build/libs/demo-1.0.0.war

@REM JAR 파일이 존재하는지 확인
if not exist %WAR_PATH% (
    echo "Error: WAR 파일이 존재하지 않습니다."
    exit /b 1
)

java demo -jar %WAR_PATH%
