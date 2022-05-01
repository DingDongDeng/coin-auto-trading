### 소개

자동매매를 위한 웹 서비스 프로젝트

- 기능
  - 거래소 키 관리
  - 자동매매 관리
  - 백테스팅 ★

---

### 프로젝트

#### 스펙

```
jdk 11 
spring boot 2.6.2
gradle 7.3.2
```

#### 인프라

```
AWS 서비스
 - Route53
 - ELB
 - EC2
```

#### CI/CD

```
Docker Hub
Github Actions
```

#### 서버 디렉토리 구조

```
~/app/
├── docker-compose.yml
├── logs
│   ├── app.2022-04-17.0.log.gz
│   └── app.log
└── monitoring
    ├── diskusage.sh
    └── healthcheck.sh
```

---

### 참고

```
### docker-compose.yml

version: "3.7"
services:
  coinautotrading:
    container_name: coinautotrading
    image: {{docker hub image url}}
    ports:
      - 80:8080
    deploy:
      resources:
        limits:
          cpus: '0.30'
          memory: 700m
    logging:
      driver: "json-file"
      options:
        max-file: "3"
        max-size: "10m"
    volumes:
      - ~/app/logs:/logs
```

```
### disuage.sh

TARGET_FILE_SYSTEM_NAME=/dev/root

df -H | grep -vE '^Filesystem|tmpfs|cdrom' | grep $TARGET_FILE_SYSTEM_NAME | awk '{ print $5 " " $1 }' | while read output;
do
  echo $output
  usep=$(echo $output | awk '{ print $1}' | cut -d'%' -f1  )
  partition=$(echo $output | awk '{ print $2 }' )
  if [ $usep -ge 85 ]; then
    echo "Running out of space \"$partition ($usep%)\" on $(hostname) as on $(date)"
    curl \
	    -X POST {{Slack Web Hook Url}} \
	    -H "Content-Type: application/json" \
	    -d '{"username":"diskusage", "text":"disk usage  is over 85%"}'
  fi
done
```

```
### healthcheck.sh

if curl -I "http://localhost:80/actuator/health" 2>&1 | grep -w "200\|301" ; then
    echo "service is healthy"
else
    echo "service is unhealthy"
    curl \
	    -X POST {{Slack Web Hook Url}} \
	    -H "Content-Type: application/json" \
	    -d '{"username":"healthcheck", "text":"service is unhealthy"}'
fi
```
