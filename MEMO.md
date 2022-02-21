### 개선 예정 목록
- 백테스팅 구현하기
- 모델 매핑 라이브러리를 사용하기
    - https://cchoimin.tistory.com/entry/model-mapping-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-Mapstruct
- 도커 이용한 실행 사용성 검토
- logback 이슈 검토
- 버저닝 자동화 (github tag, build.gradle version, image tag)
- 프로젝트를 멀티모듈로 재구성하여, 모니터링 모듈, 문서 모듈 등을 구분하여 사용
- test 시에 거래소 api를 실제로 찌르지 않도록 수정 필요(결국 단순 동작 테스트에서 실제 로직 테스트하도록 테스트코드 강화해야함)
- RsiStrategy 설정 값들 동적으로 받을 수 있도록 수정
- admin 패키지를 user 패키지로 변경하면서 사용자 인터페이스를 위한 패키징 구조 재설계 필요

### command sample
- gradle asciidoctor bootRun -PUPBIT_SECRET_KEY={시크릿키} -PUPBIT_ACCESS_KEY={액세스키}
- gradle jib -PUPBIT_SECRET_KEY={시크릿키} -PUPBIT_ACCESS_KEY={액세스키}
- gradle --tests "com.dingdongdeng.coinautotrading.exchange.client.UpbitClientTest.주문과_조회와_취소_테스트"  --project-prop UPBIT_SECRET_KEY={시크릿키} --project-prop UPBIT_ACCESS_KEY={액세스키}
- ./gradlew jib -x test   
    -Djib.to.auth.username={USERNAME}  
    -Djib.to.auth.password={TOKEN}                 
    -Djib.to.image={IMAGE}              
    -Djib.to.tags=latest    
    -Djib.container.creationTime=USE_CURRENT_TIMESTAMP               
    -Djib.container.jvmFlags=-XX:MaxRAMPercentage=60.0,-XX:MinRAMPercentage=60.0   





