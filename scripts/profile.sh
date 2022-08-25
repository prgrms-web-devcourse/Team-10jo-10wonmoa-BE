#!/usr/bin/env bash

# 쉬고있는 profile 찾기
function find_idle_profile()
{
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/profile)

  if [ ${RESPONSE_CODE} -ge 400 ] # 400보다 크면
  then
    CURRENT_PROFILE=dev2
  else
    CURRENT_PROFILE=$(curl -s http://localhost:8080/profile)
  fi

  if [ ${CURRENT_PROFILE} == dev ]
  then
    IDLE_PROFILE=dev2
  else
    IDLE_PROFILE=dev
  fi

  echo "${IDLE_PROFILE}"
}

# 쉬고 있는 profile 의 port 찾기
function find_idle_port()
{
  IDLE_PROFILE=$(find_idle_profile)

  if [ ${IDLE_PROFILE} == dev ]
  then
    echo "8080"
  else
    echo "8081"
  fi
}