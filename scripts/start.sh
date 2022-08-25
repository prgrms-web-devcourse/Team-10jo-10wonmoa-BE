#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY="/home/ubuntu/app"
PROJECT_NAME="tenwonmoa"

echo "> Build 파일 복사"

cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 실행권한 추가"

sudo chmod +x $JAR_NAME

echo "> Docker 재실행"
sudo service docker restart

echo "> SwaggerUI 컨테이너 실행"
sudo docker-compose -f $REPOSITORY/zip/docker-compose-dev.yml up -d

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 을 profile=$IDLE_PROFILE 로 실행."

nohup java -jar -Dspring.profiles.active=${IDLE_PROFILE} \
$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
