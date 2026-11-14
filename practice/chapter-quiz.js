// 코드 연결 문제집과 메인 화면이 같은 문제·채점 규칙을 사용합니다.
// 이 파일은 채점 선생님 역할입니다. 문제를 정리하고, 선택한 답과 정답을 비교합니다.
// 화면을 그리는 일은 chapter-quiz-ui.js에 맡깁니다. 덕분에 두 화면이 같은 규칙으로 채점합니다.
(function () {
  "use strict";
  // 긴 문제 글을 줄바꿈마다 잘라 한 줄을 한 문제로 읽습니다. trim은 양 끝의 빈 공간을 지웁니다.
  const rows = globalThis.CHAPTER_QUIZ_ROWS.trim().split("\n");
  // 문항이 추가되어도 기존 문항의 보기 번호가 바뀌지 않도록 ID별로 정합니다.
  function answerPosition(id) {
    // 문제의 고유 번호를 숫자로 바꿉니다. 같은 번호를 넣으면 언제나 같은 결과가 나옵니다.
    // 마지막 % 4는 4로 나눈 나머지이므로, 정답 자리 번호는 0·1·2·3 중 하나입니다.
    // 왜 순서 번호 대신 ID를 쓸까요? 문제를 중간에 추가해도 기존 문제의 ID는 그대로라, 정답 자리도 바뀌지 않습니다.
    // 16777619는 문자열을 숫자로 섞는 FNV 해시에서 쓰는 소수이고, 마지막 seed ^ (seed >>> 16)은
    // 아래쪽 숫자까지 잘 섞어 % 4 결과가 한쪽으로 몰리지 않게 합니다.
    let seed = 20261005;
    for (const character of id) seed = Math.imul(seed ^ character.charCodeAt(0), 16777619);
    return ((seed ^ (seed >>> 16)) >>> 0) % 4;
  }
  const counts = {};
  const questions = rows.map(row => {
    // |는 자료의 칸을 나누는 표시입니다. 질문·정답·오답·해설을 각각 꺼냅니다.
    const fields = row.split("|");
    // 왜 바로 멈출까요? 칸이 하나 빠진 줄을 그대로 쓰면 질문과 해설이 엇갈려 틀린 문제가 화면에 나옵니다. 잘못된 자료는 즉시 알립니다.
    if (fields.length !== 7 || fields.some(field => !field.trim())) throw new Error("객관식 문항 형식 오류");
    const [chapter, prompt, correct, ...rest] = fields;
    const explanation = rest.pop();
    const number = (counts[chapter] || 0) + 1;
    counts[chapter] = number;
    const id = `chapter-v1-${chapter}-${String(number).padStart(2, "0")}`;
    // 사람은 보기를 ①~④로 읽지만 컴퓨터는 첫 번째 자리를 0으로 셉니다.
    const answer = answerPosition(id);
    const options = [...rest];
    options.splice(answer, 0, correct);
    // Set은 같은 값을 한 번만 담습니다. 보기 네 개가 서로 다른지 확인할 때 사용합니다.
    if (new Set(options).size !== 4) throw new Error(`${chapter}장 보기 중복`);
    return { id, chapter, number, prompt, options, answer, explanation };
  });
  const ids = new Set(questions.map(question => question.id));
  function sanitizeAnswers(value, strict = false) {
    // 저장된 답안의 점검표입니다. 실제 문제 번호와 올바른 보기 번호를 가진 기록만 남깁니다.
    // strict가 true이면 잘못된 기록을 발견하는 즉시 멈춥니다. 백업 파일을 가져올 때 이 방식으로 검사합니다.
    const result = {};
    const object = item => item !== null && typeof item === "object" && !Array.isArray(item);
    if (!object(value)) {
      if (strict) throw new Error("챕터 객관식 기록 형식이 올바르지 않습니다.");
      return result;
    }
    for (const [id, record] of Object.entries(value)) {
      if (!ids.has(id) || !object(record) || !Number.isInteger(record.selected)
        || record.selected < 0 || record.selected > 3 || typeof record.graded !== "boolean") {
        if (strict) throw new Error("챕터 객관식 풀이 항목이 올바르지 않습니다.");
        continue;
      }
      result[id] = { selected: record.selected, graded: record.graded };
    }
    return result;
  }
  function statistics(subset, answers) {
    // 채점한 문제만 셉니다. 보기를 고르기만 하고 채점하지 않은 문제는 점수에 넣지 않습니다.
    let graded = 0;
    let correct = 0;
    subset.forEach(question => {
      const record = Object.hasOwn(answers, question.id) ? answers[question.id] : null;
      if (record?.graded) {
        graded += 1;
        if (record.selected === question.answer) correct += 1;
      }
    });
    return { total: subset.length, graded, correct, wrong: graded - correct };
  }
  function completedChapters(answers) {
    // 한 챕터의 모든 문항을 채점했는지 확인합니다. 실제 프로그램 구현 완료와는 별도 기록입니다.
    return Object.keys(counts).filter(chapter => statistics(forChapter(chapter), answers).graded === counts[chapter]);
  }
  // 참고: completedChapters가 위에서 forChapter를 쓰지만 문제없습니다. 함수가 실제로 '호출'될 때는 이 줄이 이미 실행된 뒤이기 때문입니다.
  const forChapter = chapter => questions.filter(question => question.chapter === chapter);
  // globalThis에 붙이면 다른 JavaScript 파일도 ChapterQuiz라는 이름으로 이 기능을 사용할 수 있습니다.
  globalThis.ChapterQuiz = { questions, counts, forChapter, sanitizeAnswers, statistics, completedChapters };
})();
