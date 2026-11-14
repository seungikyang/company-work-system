// 화면과 분리한 학습 범위 계산입니다. 채점 중에는 선택한 목록을 유지합니다.
(function () {
  "use strict";
  function select(questions, answers, filters, requestedId = "", startId = "") {
    const query = filters.query.trim().toLocaleLowerCase();
    const matches = questions.filter(question => {
      if (filters.ids?.length && !filters.ids.includes(question.id)) return false;
      if (filters.chapter && question.chapter !== filters.chapter) return false;
      const record = answers[question.id];
      // 주소로 다시 연 현재 문제는 재풀이·재채점 후에도 복습 화면에 남깁니다.
      if (question.id !== requestedId) {
        if (filters.mode === "wrong" && !(record?.graded && record.selected !== question.answer)) return false;
        if (filters.mode === "correct" && !(record?.graded && record.selected === question.answer)) return false;
        if (filters.mode === "unanswered" && record?.graded) return false;
      }
      return !query || `${question.prompt} ${question.options.join(" ")} ${question.chapterTitle || ""} ${question.technology || ""}`.toLocaleLowerCase().includes(query);
    });
    // 왜? 정해 둔 개수만 받습니다. 주소창에 이상한 값이 들어와도 0(제한 없음)으로 처리해 화면이 망가지지 않게 합니다.
    const limit = ["10", "20", "50"].includes(filters.limit) ? Number(filters.limit) : 0;
    if (!limit || matches.length <= limit) return { questions: matches, total: matches.length };
    let start = matches.findIndex(question => question.id === (startId || requestedId));
    // 왜? 시작 문항이 따로 없으면 아직 채점하지 않은 첫 문제부터 시작해, 이미 푼 문제를 다시 넘기는 수고를 줄입니다.
    if (start < 0) start = Math.max(0, matches.findIndex(question => !answers[question.id]?.graded));
    // 요청 문항이 범위의 끝을 벗어나면 그 문항부터 이어서 보여 줍니다.
    const requested = matches.findIndex(question => question.id === requestedId);
    if (requested >= 0 && (requested < start || requested >= start + limit)) start = requested;
    return { questions: matches.slice(start, start + limit), total: matches.length };
  }
  // 왜 화면과 분리했을까요? 화면 없이 계산만 하는 함수라서 자동 검사(scripts/verify_focus_quiz.cjs)로 쉽게 확인할 수 있습니다.
  globalThis.FocusSession = { select };
})();
