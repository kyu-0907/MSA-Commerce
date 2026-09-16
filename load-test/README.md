# 로컬 부하테스트 인프라

k6 + Prometheus + Grafana로 구성된 로컬 부하테스트 인프라입니다.
아직 테스트할 API가 확정되지 않았기 때문에 특정 API에 종속된 시나리오는 포함하지 않았고,
`k6/scenarios/` 아래에 시나리오만 추가하면 바로 쓸 수 있는 구조로 구성했습니다.

## 전체 워크플로우 (API 개발 → 컨테이너 기동 → 부하테스트)

API를 새로 개발하고, 마이크로서비스 컨테이너를 띄운 뒤, 부하테스트까지 진행하려면 아래 순서를 따르세요.

### 1. API 개발
해당 서버 모듈(`product-server`, `order-server` 등)에서 평소처럼 컨트롤러/서비스를 작성합니다.

### 2. 마이크로서비스 컨테이너 기동 (루트 `docker-compose.yml`)
루트의 `docker-compose.yml`이 MySQL/Redis/Kafka/Elasticsearch 같은 인프라와 9개 서버를 전부 정의하고 있습니다.

```bash
# 프로젝트 루트에서
docker compose up -d --build          # 전체 기동
docker compose up -d --build product-server   # 특정 서비스만 재빌드/재기동
```

기동 후 헬스체크:
```bash
docker compose ps
curl http://localhost:8080/actuator/health   # gateway 예시
```

### 3. 부하테스트 대상 URL 확정
게이트웨이(`8080`)를 거칠지, 개별 서버(예: product-server `8085`)를 직접 찌를지 정하고 `load-test/.env`의 `BASE_URL`에 반영합니다.

```bash
cd load-test
cp .env.example .env   # 최초 1회
# .env 수정
# BASE_URL=http://host.docker.internal:8080   (게이트웨이 경유)
# BASE_URL=http://host.docker.internal:8085   (product-server 직접)
```

k6 컨테이너는 별도 compose 네트워크에 있지만, 앱 서버들이 이미 호스트 포트로 노출돼 있어
`host.docker.internal`로 바로 도달 가능합니다. 두 compose 네트워크를 합칠 필요는 없습니다.

### 4. 부하테스트 인프라 기동
```bash
cd load-test
docker compose up -d prometheus grafana
```

### 5. k6 시나리오 작성
`load-test/k6/scenarios/`에 이번에 개발한 API를 호출하는 스크립트를 새로 추가합니다.

```js
// load-test/k6/scenarios/product-get.js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, defaultOptions } from '../lib/config.js';

export const options = defaultOptions();

export default function () {
  const res = http.get(`${BASE_URL}/api/products/1`);
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(1);
}
```

### 6. 부하테스트 실행
```bash
cd load-test
docker compose run --rm k6 run scenarios/product-get.js
# 옵션 오버라이드 예시
docker compose run --rm -e VUS=50 -e DURATION=1m k6 run scenarios/product-get.js
```

### 7. 결과 확인
- 실행 직후 터미널에 k6 요약 출력
- `http://localhost:3000` (admin/admin) → **Dashboards → k6 → k6 Load Test Overview**에서 실시간/직전 결과 그래프 확인

### 8. 종료
```bash
cd load-test
docker compose down          # 모니터링 인프라 종료 (데이터는 볼륨에 유지)
# docker compose down -v     # Prometheus/Grafana 데이터까지 초기화하고 싶을 때

cd ..
docker compose down          # 마이크로서비스 종료 (필요할 때만)
```

정리하면 **① 마이크로서비스 기동 → ② BASE_URL 설정 → ③ 모니터링 인프라 기동 → ④ 시나리오 작성 →
⑤ k6 실행 → ⑥ Grafana 확인 → ⑦ 종료** 순서이고, 새 API가 나올 때마다 ②④⑤⑥만 반복하면 됩니다.

## 구성

| 구성요소 | 역할 | 포트 |
|---|---|---|
| k6 | 부하테스트 실행 (필요할 때만 실행되는 1회성 컨테이너) | - |
| Prometheus | k6가 Remote Write로 보내는 메트릭 수집/저장 | 9090 |
| Grafana | Prometheus 데이터 시각화 | 3000 |

k6 → Prometheus 연동은 k6의 실험적 출력 모듈인 `experimental-prometheus-rw`(Prometheus Remote Write)를
사용합니다. 이 방식은 별도 exporter 없이 k6가 메트릭을 Prometheus에 직접 push하는 구조입니다.

## 디렉토리 구조

```
load-test/
├── docker-compose.yml        # k6 / Prometheus / Grafana 전체 구성
├── .env.example               # BASE_URL 등 환경변수 예시
├── prometheus/
│   └── prometheus.yml         # Prometheus 설정 (remote-write 수신 활성화는 compose command로 지정)
├── grafana/
│   ├── provisioning/
│   │   ├── datasources/       # Prometheus 데이터소스 자동 등록
│   │   └── dashboards/        # 대시보드 프로비저닝 설정
│   └── dashboards/
│       └── k6-overview.json   # k6 결과를 보여주는 기본 대시보드
└── k6/
    ├── lib/
    │   └── config.js           # BASE_URL, 공통 옵션 등 시나리오 공통 헬퍼 (API 비종속)
    ├── scenarios/               # 실제 부하테스트 시나리오 (.js)를 추가하는 곳
    └── results/                 # k6 결과 파일(summary export 등)을 저장하는 곳 (git 미포함)
```

테스트 시나리오(`k6/`)와 모니터링 인프라(`prometheus/`, `grafana/`, `docker-compose.yml`)는
디렉토리로 분리되어 있어, 시나리오가 늘어나도 인프라 설정을 건드릴 필요가 없습니다.

## 사전 준비

```bash
cd load-test
cp .env.example .env
# .env를 열어 BASE_URL을 실제 테스트 대상 서버 주소로 수정하세요.
```

- 도커 컨테이너(k6)에서 **호스트에 떠 있는 서버**를 호출하려면 `http://host.docker.internal:<port>`를 사용하세요.
- 이 프로젝트의 `docker-compose.yml`(루트)로 띄운 컨테이너를 직접 호출하려면, 해당 compose 네트워크에
  이 스택을 연결하거나 노출된 호스트 포트(예: gateway의 `8080`)를 사용하면 됩니다.

## 실행 방법

모니터링 인프라(Prometheus + Grafana) 기동:

```bash
cd load-test
docker compose up -d prometheus grafana
```

- Grafana: http://localhost:3000 (계정: `admin` / `admin`)
  - Prometheus 데이터소스와 "k6 Load Test Overview" 대시보드가 자동으로 등록되어 있습니다.
- Prometheus: http://localhost:9090

k6 시나리오 실행 (시나리오는 `k6/scenarios/`에 추가한 뒤 실행):

```bash
docker compose run --rm k6 run scenarios/<파일명>.js
```

VUs, 실행 시간, 대상 서버 등은 환경변수로 덮어쓸 수 있습니다.

```bash
docker compose run --rm -e VUS=50 -e DURATION=1m -e BASE_URL=http://host.docker.internal:8080 \
  k6 run scenarios/<파일명>.js
```

실행이 끝나면 Grafana 대시보드에서 실시간/직전 결과를 바로 확인할 수 있습니다.

시나리오 작성법은 `k6/scenarios/README.md`를 참고하세요.

## 종료 방법

```bash
cd load-test
docker compose down
```

Prometheus/Grafana에 쌓인 데이터까지 완전히 초기화하려면:

```bash
docker compose down -v
```

## 검증 내역

이 인프라는 실제로 아래 과정을 로컬에서 직접 실행해 정상 동작을 확인했습니다.

1. `docker compose up -d prometheus grafana`로 기동
2. Prometheus `--web.enable-remote-write-receiver` 활성화 확인
3. Grafana에 Prometheus 데이터소스 및 대시보드 자동 프로비저닝 확인
4. k6 컨테이너에서 임시 스크립트를 실행해 `k6_*` 메트릭이 Prometheus로 정상 수집되는 것을 확인
   (`k6_vus`, `k6_http_reqs_total`, `k6_http_req_duration_p99`, `k6_http_req_failed_rate`,
   `k6_checks_rate`, `k6_iterations_total`, `k6_data_sent_total`, `k6_data_received_total` 등)
5. 대시보드에서 사용하는 PromQL 쿼리들이 실제 값을 반환하는 것을 Prometheus API로 확인

검증에 사용한 임시 스크립트는 실제 프로젝트 API를 가정하지 않도록 Prometheus 자체 헬스체크
엔드포인트(`/-/healthy`)를 호출하는 방식이었고, 검증 후 삭제했습니다. 따라서 `k6/scenarios/`에는
API 종속적인 코드가 없습니다.

## 참고

- k6가 사용하는 `experimental-prometheus-rw` 출력은 k6 버전에 따라 옵션명이 바뀔 수 있습니다.
  (`docker-compose.yml`은 `grafana/k6:0.54.0`으로 버전을 고정해두었습니다.)
- 대시보드 패널에서 사용하는 메트릭 이름/레이블은 k6 버전 및 트렌드 통계 설정(`K6_PROMETHEUS_RW_TREND_STATS`)에
  따라 달라질 수 있습니다. 필요하면 Prometheus의 Graph 화면(`/graph`)에서 `k6_`로 시작하는 메트릭을
  직접 조회해 대시보드 쿼리를 조정하세요.
