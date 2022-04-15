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



### referrence
- gitlab actions에 대해 aws 보안 설정
    - https://rusyasoft.github.io/github,%20actions,%20aws/2020/12/14/allow-github-actions-ip-addresses-at-aws/
    - https://kamrul.dev/dynamically-add-github-actions-ip-to-aws-security-group/
- aws 보안 설정
    - https://perfectacle.github.io/2018/08/30/aws-security-group-reference-another-security-group/


