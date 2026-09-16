// 모든 k6 시나리오가 공통으로 사용하는 설정 헬퍼입니다.
// 특정 API를 가정하지 않으므로, 실제 엔드포인트/페이로드는 각 시나리오 파일에서 작성하세요.

// 대상 서버 주소. docker-compose 실행 시 BASE_URL 환경변수로 주입됩니다.
export const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 공통 옵션 기본값을 만들어주는 헬퍼. 시나리오별로 필요한 부분만 덮어써서 사용하세요.
//
// 예시:
//   import { defaultOptions } from '../lib/config.js';
//   export const options = defaultOptions({ stages: [...] });
export function defaultOptions(overrides = {}) {
  return {
    vus: Number(__ENV.VUS) || 10,
    duration: __ENV.DURATION || '30s',
    thresholds: {
      http_req_failed: ['rate<0.01'],
      http_req_duration: ['p(95)<500'],
    },
    ...overrides,
  };
}
