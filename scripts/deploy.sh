#!/bin/bash

REPOSITORY="/home/ubuntu/app"
PROJECT_NAME="tenwonmoa"

echo "> Build 파일 복사"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 현재 구동 중인 애플리케이션 pid 확인"

CURRENT_PID=$(pgrep -fl $PROJECT_NAME | grep java | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
  echo "> 현재 구동 중인 애플리케이션 없음."
else
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 실행권한 추가"

sudo chmod +x $JAR_NAME

echo "> Docker 재실행"
sudo service docker restart

echo "> SwaggerUI 컨테이너 실행"
sudo docker-compose -f docker-compose-dev.yml up -d

echo "> $JAR_NAME 실행"

nohup java -jar -Dspring.profiles.active=dev $JAR_NAME --server.port=8080 \
$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &