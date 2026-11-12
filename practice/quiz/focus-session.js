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
    const limit = ["10", "20", "50"].includes(filters.limit) ? Number(filters.limit) : 0;
    if (!limit || matches.length <= limit) return { questions: matches, total: matches.length };
    let start = matches.findIndex(question => question.id === (startId || requestedId));
    if (start < 0) start = Math.max(0, matches.findIndex(question => !answers[question.id]?.graded));
    // 요청 문항이 범위의 끝을 벗어나면 그 문항부터 이어서 보여 줍니다.
    const requested = matches.findIndex(question => question.id === requestedId);
    if (requested >= 0 && (requested < start || requested >= start + limit)) start = requested;
    return { questions: matches.slice(start, start + limit), total: matches.length };
  }
  globalThis.FocusSession = { select };
})();
