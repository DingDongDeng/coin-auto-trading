### 소개

사용자의 컴퓨터에서 간단한 명령어들을 통해 자동매매를 이용하기 위한 프로젝트입니다.  
거래소, 코인, 매매전략을 선택하여, 효과적인 매매가 가능하게 됩니다.  
---
### 사전준비

자동매매를 지원하는 거래소들 중, 원하는 거래소의 액세스키, 시크릿키를 생성해주세요.(각 거래소 홈페이지 참조) 
도커 실행을 위한 준비를 해주세요.   
 - Mac, Win 사용자는 Docker Desktop을 설치해주세요.
 - Linux 계열 사용자는 docker, docker-compose를 설치해주세요.

---
### 실행방법
```
### docker-compose.yml 파일을 생성해주세요.
version: "3.7"
services:
  coinautotrading:
    name: coinautotrading
    image: dingdongdeng/coinautotrading:latest
    ports:
      - 8080:8080
    environment:
      UPBIT_ACCESS_KEY: {액세스_키} 
      UPBIT_SECRET_KEY: {시크릿_키}
    deploy:
      resources:
        limits:
          cpus: '0.30'
          memory: 1G
    logging:
      driver: "json-file"
      options:
        max-file: "3"
        max-size: "10m"
```
```
### 파일이 생성되었다면 아래 명령어로 실행해주세요.
docker-compose -f docker-compose.yml up -d
```
```
브라우저로 localhost:8080/user/console 접속해주세요.
```
