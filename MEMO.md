### 개선 예정 목록
- 백테스팅 구현하기
- 모델 매핑 라이브러리를 사용하기
    - https://cchoimin.tistory.com/entry/model-mapping-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC-Mapstruct
- 도커 이용한 실행 사용성 검토
- logback 이슈 검토
- 버저닝 자동화 (github tag, build.gradle version, image tag)

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





