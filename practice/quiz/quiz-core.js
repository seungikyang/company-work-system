// 중학생 눈높이: 이 파일은 기본기 2,000제의 문제를 준비하고 채점·검색 규칙을 정합니다.
// 40단원 × 단원마다 5개 주제 × 5가지 관점 × 설명 선택·기술 식별 = 2,000문항입니다.
// 화면 표시 없이 계산할 수 있어 같은 규칙을 자동 검사에도 사용합니다.
// DOM 없이도 문제 수·채점·저장 데이터·필터를 검증할 수 있는 공통 로직입니다.
(function () {
  "use strict";
  const version = 2;
  // localStorage는 이 브라우저의 작은 보관함입니다. 이 이름표 아래 기본기 답안만 넣습니다.
  const storageKey = "company-work-system.fundamentals.technology.v2";
  const scopes = {
    workbook: "워크북 실제 구현",
    backend: "백엔드 실습 조각",
    extension: "백엔드 확장 미리보기",
  };
  const kinds = [
    { id: "concept", label: "개념", prompt: "의 개념을 가장 잘 설명한 것은?" },
    { id: "situation", label: "적용 상황", prompt: "의 필요성이 가장 직접적으로 드러나는 상황은?" },
    { id: "implementation", label: "구현 방법", prompt: "의 구현 방법으로 알맞은 것은?" },
    { id: "risk", label: "오류 위험", prompt: "의 규칙이나 처리를 놓쳤을 때 가장 직접적인 문제는?" },
    { id: "verification", label: "검증 방법", prompt: "의 동작이나 규칙을 직접 확인하는 검증 방법은?" },
  ];
  // 설명을 고르는 기존 문제와 반대로, 설명에서 기술 이름을 찾아보는 다섯 유형을 추가합니다.
  const identificationPrompts = [
    "다음 설명에 해당하는 기술 항목은?",
    "다음 상황에서 가장 직접적으로 필요한 기술 항목은?",
    "다음 구현 방법에 해당하는 기술 항목은?",
    "다음 문제를 직접 예방하기 위해 확인할 기술 항목은?",
    "다음 검증 방법으로 직접 확인하는 기술 항목은?",
  ];
  kinds.push(...kinds.map((kind, facetIndex) => ({
    id: `identify-${kind.id}`, label: `${kind.label} → 기술 식별`,
    prompt: identificationPrompts[facetIndex], facetIndex, identify: true,
  })));

  // 고정 시드로 보기를 섞어 새로고침 후에도 저장한 선택지 번호의 의미가 유지됩니다.
  // seed는 숫자 뽑기의 시작값입니다. 같은 시작값이면 같은 순서가 나와 저장한 보기 번호가 유지됩니다.
  // 보안 비밀을 만드는 함수가 아니라 연습 문제의 보기 순서를 정하는 함수입니다.
  function random(seed) {
    return function () {
      seed = (Math.imul(seed, 1664525) + 1013904223) >>> 0;
      return seed / 4294967296;
    };
  }
  // 카드 섞기처럼 목록의 순서를 바꿉니다. ...items로 복사해서 원래 목록은 바꾸지 않습니다.
  function shuffle(items, next) {
    const result = [...items];
    for (let i = result.length - 1; i > 0; i -= 1) {
      const j = Math.floor(next() * (i + 1));
      [result[i], result[j]] = [result[j], result[i]];
    }
    return result;
  }

  // 네 자료 묶음을 단원별로 읽은 뒤 문제를 만듭니다. 형식이나 단원 수가 틀리면 오류로 알려 줍니다.
  function buildBank(sources) {
    if (!Array.isArray(sources) || sources.length !== 4) throw new Error("학습 자료 네 묶음이 모두 필요합니다.");
    const chapters = sources.flatMap(source => source.trim().split(/\n\s*\n/)).map(block => {
      const [heading, ...lines] = block.trim().split("\n");
      const [id, title, scope, technology, source] = heading.replace(/^## /, "").split("|");
      const topics = lines.map(line => {
        const [name, evidence, ...facets] = line.split("|").map(value => value.trim());
        if (!name || !evidence || facets.length !== 5 || facets.some(value => !value)) {
          throw new Error(`${id}장 학습 항목 형식이 올바르지 않습니다.`);
        }
        return { name, evidence, facets };
      });
      if (!title || !source || !scopes[scope] || !technology || topics.length !== 5) throw new Error(`${id}단원 구성 오류`);
      const reading = scope === "workbook" ? "practice/beginner-guide.md" : source;
      return { id, title, scope, technology, source, reading, topics };
    });
    if (chapters.length !== 40 || chapters.some((chapter, i) => chapter.id !== String(i).padStart(2, "0"))) {
      throw new Error("00~39장의 순서와 개수를 확인하세요.");
    }
    // 기존 1,000문항의 보기·정답 순서를 유지해야 저장된 선택 번호도 그대로 유효합니다.
    const originalCount = chapters.reduce((count, chapter) => count + chapter.topics.length * 5, 0);
    const positions = [
      ...shuffle(Array.from({ length: originalCount }, (_, i) => i % 4), random(20260922)),
      ...shuffle(Array.from({ length: originalCount }, (_, i) => i % 4), random(20261009)),
    ];
    const questions = [];
    // 기존 문제를 먼저 모두 만들고 새 문제를 뒤에 붙여 기존 ID·번호·보기를 보존합니다.
    for (const identify of [false, true]) {
      for (const chapter of chapters) {
        kinds.filter(kind => Boolean(kind.identify) === identify).forEach((kind, index) => {
          const facetIndex = kind.facetIndex ?? index;
          chapter.topics.forEach((topic, topicIndex) => {
            const number = questions.length + 1;
            const next = random(number * 7919);
            // 같은 단원의 다른 주제 설명 세 개를 오답 보기로 고릅니다. 질문의 주제를 구별하는 연습입니다.
            const others = shuffle(chapter.topics.filter(other => other !== topic), next).slice(0, 3);
            const optionFor = item => ({ text: identify ? item.name : item.facets[facetIndex], topic: item.name });
            const options = others.map(optionFor);
            const answer = positions[number - 1];
            options.splice(answer, 0, optionFor(topic));
            if (new Set(options.map(option => option.text)).size !== 4) throw new Error(`${number}번 보기 중복`);
            questions.push({
              id: `tech-v2-${chapter.id}-${topicIndex + 1}-${kind.id}`,
              number, chapter: chapter.id, kind: kind.id, topic: topic.name,
              scope: chapter.scope, technology: chapter.technology, evidence: topic.evidence,
              prompt: identify ? `${kind.prompt}\n「${topic.facets[facetIndex]}」` : `「${topic.name}」${kind.prompt}`,
              options, answer, source: chapter.source, reading: chapter.reading,
              explanation: `${topic.name}: ${topic.facets[0]}. 적용 방법: ${topic.facets[2]}. 확인 방법: ${topic.facets[4]}.`,
            });
          });
        });
      }
    }
    return { chapters, questions };
  }

  // 저장 기록이 망가져도 알려진 문제와 정상 보기 번호를 가진 답안만 골라 사용할 수 있게 정리합니다.
  function sanitizeAnswers(value, questions) {
    const result = {};
    if (!value || typeof value !== "object" || Array.isArray(value)) return result;
    for (const question of questions) {
      const record = Object.prototype.hasOwnProperty.call(value, question.id) ? value[question.id] : null;
      if (record && Number.isInteger(record.selected) && record.selected >= 0 && record.selected < 4 && typeof record.graded === "boolean") {
        result[question.id] = { selected: record.selected, graded: record.graded };
      }
    }
    return result;
  }

  // 도서관에서 조건에 맞는 책만 찾듯이 단원·유형·풀이 상태·검색어에 맞는 문제만 남깁니다.
  function filterQuestions(questions, filters, answers) {
    const query = String(filters.query || "").trim().toLocaleLowerCase();
    return questions.filter(question => {
      const record = answers[question.id];
      if (filters.chapter && question.chapter !== filters.chapter) return false;
      if (filters.scope === "workbook" && question.scope !== "workbook") return false;
      if (filters.scope === "backend" && question.scope === "workbook") return false;
      if (filters.kind && question.kind !== filters.kind) return false;
      if (filters.mode === "unanswered" && record?.graded) return false;
      if (filters.mode === "wrong" && (!record?.graded || record.selected === question.answer)) return false;
      if (filters.mode === "correct" && (!record?.graded || record.selected !== question.answer)) return false;
      return !query || `${question.number} ${question.technology} ${question.topic} ${question.prompt} ${question.evidence} ${question.source}`.toLocaleLowerCase().includes(query);
    });
  }

  // 보기를 선택한 것과 채점한 것은 다릅니다. graded가 true인 기록만 채점 수와 점수에 넣습니다.
  function statistics(questions, answers) {
    let answered = 0;
    let correct = 0;
    for (const question of questions) {
      const record = answers[question.id];
      if (record?.graded) {
        answered += 1;
        if (record.selected === question.answer) correct += 1;
      }
    }
    return { total: questions.length, answered, correct, wrong: answered - correct, remaining: questions.length - answered };
  }

  globalThis.Fundamentals = { version, storageKey, scopes, kinds, buildBank, sanitizeAnswers, filterQuestions, statistics };
})();
