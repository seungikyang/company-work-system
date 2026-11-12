// 코드 주석의 자동 생성 문항을 웹 풀이로 연결합니다. 원본 파일에는 쓰지 않습니다.
(function () {
  "use strict";
  function extract(source) {
    const generated = source.match(/^[ \t]*(?:\/\/|#)\s*4지선다 학습 문제 \(자동 생성 시작\)[\s\S]*?^[ \t]*(?:\/\/|#)\s*4지선다 학습 문제 \(자동 생성 끝\)[^\n]*/m);
    const ids = generated ? [...new Set([...generated[0].matchAll(/\[(chapter-v1-\d{2}-\d{2})\]/g)].map(match => match[1]))] : [];
    return { source: ids.length ? source.replace(generated[0], "") : source, ids };
  }
  globalThis.DocumentQuiz = { extract };
})();
