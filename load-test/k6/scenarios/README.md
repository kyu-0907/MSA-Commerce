# 시나리오 작성 가이드

이 디렉토리에 실제 부하테스트 대상 API가 정해지면 시나리오 스크립트(`.js`)를 추가하세요.
아직 확정된 API가 없으므로 예시 시나리오는 포함되어 있지 않습니다.

## 기본 템플릿

```js
import http from 'k6/http';
import { check, sleep } from 'k6';
import { BASE_URL, defaultOptions } from '../lib/config.js';

export const options = defaultOptions({
  // 필요하면 이 시나리오만의 vus/duration/thresholds로 덮어쓰세요.
});

export default function () {
  const res = http.get(`${BASE_URL}/원하는-경로`);
  check(res, {
    'status is 200': (r) => r.status === 200,
  });
  sleep(1);
}
```

## 실행 방법

`load-test/` 디렉토리에서:

```bash
docker compose run --rm k6 run scenarios/파일명.js
```

- `BASE_URL`, `VUS`, `DURATION`은 `.env` 또는 `-e` 옵션으로 덮어쓸 수 있습니다.
  ```bash
  docker compose run --rm -e VUS=50 -e DURATION=1m k6 run scenarios/파일명.js
  ```
- 결과를 파일로 남기고 싶으면 `--summary-export`를 사용하세요 (`results/`는 호스트와 공유됩니다).
  ```bash
  docker compose run --rm k6 run --summary-export=results/summary.json scenarios/파일명.js
  ```
