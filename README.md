## 소개
사용자의 컴퓨터에서 간단한 명령어들을 통해 자동매매를 이용하기 위한 프로젝트입니다.  
거래소, 코인, 매매전략을 선택하여, 효과적인 매매가 가능하게 됩니다.  
---
### 사전준비
자동매매를 지원하는 거래소들 중, 원하는 거래소의 액세스키, 시크릿키를 생성해주세요.(각 거래소 홈페이지 참조)  

---
### 실행방법
#### git 이용해 프로젝트 다운 및 실행하기(추천)
```
### 아래 명령어들을 순서대로 입력해주세요.
커맨드창을 켜주세요.(윈도우는 ctr + r, cmd 입력 후 엔터)
cd ~  
git clone https://github.com/DingDongDeng/coinautotrading.git
~/coinautotrading/gradlew clean bootRun --args='--UPBIT_SECRET_KEY=시크릿키 --UPBIT_ACCESS_KEY=액세스키' 
~/coinautotrading/gradlew.bat clean bootRun --args='--UPBIT_SECRET_KEY=시크릿키 --UPBIT_ACCESS_KEY=액세스키'  (윈도우)

### 프로젝트 최신버전으로 업데이트
cd ~/coinautotrading
git pull  
```
#### zip 파일 형식으로 다운받아 실행하기
```
https://github.com/DingDongDeng/coinautotrading/releases 링크에서 최신버전의 zip파일을 다운 받아주세요.

### 아래 명령어들을 순서대로 입력해주세요.
커맨드창을 켜주세요.(윈도우는 ctr + r, cmd 입력 후 엔터)
cd "프로젝트 다운받은 경로"/"프로젝트명"
gradlew clean bootRun --args='--UPBIT_SECRET_KEY=시크릿키 --UPBIT_ACCESS_KEY=액세스키'
```

---
### 사용법
프로젝트 다운로드 및 실행이 완료되면 브라우저에 아래 링크 접속하여 사용법을 확인해주세요.  
```
### 아래 링크 접속!!
http://localhost:8080/docs/index.html

위 링크에 기술 되어있는curl 명령어를 커맨드창에 입력하여 자동매매 시작, 정지가 가능합니다.
```
  
---

### 개선 사항 목록
- 백테스팅 구현하기
- 모델 매핑 라이브러리를 사용하기
    - https://cchoimin.tistory.com/entry/model-mapping-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-Mapstruct
- 도커 이용한 실행 사용성 검토

### sample
- gradle asciidoctor bootRun -PUPBIT_SECRET_KEY={시크릿키} -PUPBIT_ACCESS_KEY={액세스키}
- gradle --tests "com.dingdongdeng.coinautotrading.exchange.client.UpbitClientTest.주문과_조회와_취소_테스트"  --project-prop UPBIT_SECRET_KEY={시크릿키} --project-prop UPBIT_ACCESS_KEY={액세스키}



