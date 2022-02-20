## 소개

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
version: "3.7"

services:
  coinautotrading:
    image: dingdongdeng/coinautotrading
    ports:
      - 8080:8080
    environment:
      UPBIT_ACCESS_KEY: {액스세키}
      UPBIT_SECRET_KEY: {시크릿키}
    cpus: ".30"  # CPU 프로세스 사용률 30%로 제한
    mem_limit: "1g"  # 1GB로 메모리 제한
```

---
### 사용법
사용자 콘솔 화면 준비 중 입니다.

  