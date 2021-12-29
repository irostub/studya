# 스터디야 프로젝트
내가 참가하고 싶은 동네 주변 스터디를 한번에!

## 목표

+ 사용자가 빠르고 쉽게 접근할 수 있는 주변 스터디 찾기 서비스를 제공합니다.
+ 사용자가 관심있는 주제에 대한 스터디 모임을 추천합니다.
+ 사용자가 관심있는 지역에 대한 스터디 모임을 추천합니다.

## 프리뷰
### login 이전 index 페이지
![index_before](./readme/img/index(before%20login).png)
  
### login 이후 index 페이지
![index_after](./readme/img/index(after%20login).png)

## 빌드
### window
    ./gradlew.bat build
### mac
    ./gradlew build
## 실행
    psql create database studya_test;
    docker run -it -p 8080:8080 irostub/studya:1.0.0
## 설계
이미지 삽입 예정

## 기술
+ Spring Web MVC
+ Spring Security
+ JPA
+ QueryDSL
+ Thymeleaf
+ Docker
+ Nginx
## 배포
+ 사설 서버 배포 예정

## 기타
네임드 쿼리, Spring Data JPA 통합 필요  
지역 우선 순위 지정 기능 구현 필요  
docker-compose 필요  
postgresql 도 docker 로 띄운 뒤 compose 로 컨테이너간 통신 설정  
