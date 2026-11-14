// 코드 주석의 자동 생성 문항을 웹 풀이로 연결합니다. 원본 파일에는 쓰지 않습니다.
(function () {
  "use strict";
  function extract(source) {
    // 왜 이렇게 찾을까요? 자동 생성 문제는 '시작'과 '끝' 표시 줄 사이에 있습니다. //와 # 주석을 모두 받아
    // Java·YAML·Gradle 파일 어디서든 같은 방법으로 찾습니다. [\s\S]*?는 줄바꿈까지 포함해 가장 짧게 찾는다는 뜻입니다.
    const generated = source.match(/^[ \t]*(?:\/\/|#)\s*4지선다 학습 문제 \(자동 생성 시작\)[\s\S]*?^[ \t]*(?:\/\/|#)\s*4지선다 학습 문제 \(자동 생성 끝\)[^\n]*/m);
    const ids = generated ? [...new Set([...generated[0].matchAll(/\[(chapter-v1-\d{2}-\d{2})\]/g)].map(match => match[1]))] : [];
    // 왜 지울까요? 문서 화면에서는 같은 문제를 웹 퀴즈로 보여 주므로, 주석 속 문제를 또 보이면 중복이 됩니다.
    // 읽어 온 글자에서만 지우고 원본 파일은 건드리지 않습니다. 문제 번호를 하나도 못 찾으면 안전하게 원문 그대로 둡니다.
    return { source: ids.length ? source.replace(generated[0], "") : source, ids };
  }
  globalThis.DocumentQuiz = { extract };
})();
