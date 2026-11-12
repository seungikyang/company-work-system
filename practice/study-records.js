// 학습 기록의 자료형을 확인합니다. 손상된 브라우저 기록은 정상 항목만 읽고,
// 가져올 백업은 전체 형식이 맞을 때만 승인합니다.
// 백업은 학습 노트를 파일로 복사해 보관하는 일, 복원은 그 파일의 기록을 다시 불러오는 일입니다.
// 읽는 순서: normalizeWorkbook으로 정리 → createBackup으로 묶기 → parseBackup으로 검사 → restore로 저장.
(function () {
  "use strict";
  const workbookKey = "company-workbook-state-v1";
  // 저장 키는 서랍에 붙인 이름표와 같습니다. 워크북과 기본기 문제집은 서로 다른 서랍에 넣습니다.
  const quizKey = "company-work-system.fundamentals.technology.v2";
  const chapters = Array.from({ length: 40 }, (_, i) => String(i).padStart(2, "0"));
  const object = value => value !== null && typeof value === "object" && !Array.isArray(value);
  const own = (value, key) => object(value) && Object.hasOwn(value, key) ? value[key] : undefined;

  function normalizeWorkbook(value, strict = false) {
    // normalize는 여러 형태의 기록을 우리가 사용하는 한 가지 모양으로 정리한다는 뜻입니다.
    // 브라우저의 옛 기록은 쓸 수 있는 부분을 살리고, 가져올 백업은 strict 검사로 전체 모양을 확인합니다.
    function check(valid) {
      if (strict && !valid) throw new Error("워크북 기록 형식이 올바르지 않습니다.");
      return valid;
    }
    function text(parent, key, allowed) {
      // 숫자나 배열이 글자 칸에 들어오면 빈 글자로 정리합니다. allowed가 있으면 정해진 값인지도 봅니다.
      const input = own(parent, key);
      return check(typeof input === "string" && (!allowed || allowed.includes(input))) ? input : "";
    }
    function list(input, allowed) {
      // 목록에서 허용한 항목만 남기고 중복을 지웁니다. 같은 챕터를 두 번 완료한 것으로 세지 않습니다.
      if (!check(Array.isArray(input))) return [];
      check(input.every(item => allowed.includes(item)));
      return [...new Set(input.filter(item => allowed.includes(item)))];
    }
    check(object(value));
    const profile = own(value, "profile");
    const today = own(value, "today");
    const workbook = own(value, "workbook");
    check(object(profile) && object(today) && object(workbook));
    let deadline = text(profile, "targetDeadline");
    if (deadline) {
      // 날짜처럼 보이는 글자라도 실제 달력에 있어야 합니다. 예를 들어 2월 30일은 허용하지 않습니다.
      const date = new Date(`${deadline}T00:00:00Z`);
      if (!check(/^\d{4}-\d{2}-\d{2}$/.test(deadline)
        && Number.isFinite(date.getTime()) && date.toISOString().slice(0, 10) === deadline)) deadline = "";
    }
    const answers = {};
    const source = own(workbook, "answers");
    if (check(object(source))) {
      for (const [id, answer] of Object.entries(source)) {
        if (check(chapters.includes(id) && typeof answer === "string")) {
          if (answer.trim()) answers[id] = answer;
        }
      }
    }
    return {
      profile: {
        targetRole: text(profile, "targetRole"),
        targetDeadline: deadline,
        dailyMinutes: text(profile, "dailyMinutes", ["", "30", "60", "90", "120"]),
      },
      today: {
        frId: text(today, "frId"), chapter: text(today, "chapter", ["", ...chapters]),
        goal: text(today, "goal"), testCommand: text(today, "testCommand"),
        testResult: text(today, "testResult", ["", "fail", "pass"]),
      },
      stages: list(own(value, "stages"), ["prepare", "design", "build", "verify", "apply"]),
      chapters: list(own(value, "chapters"), chapters),
      session: list(own(value, "session"), ["choose", "solve", "learn", "test", "record"]),
      workbook: {
        answers,
        reviewed: list(own(workbook, "reviewed"), chapters).filter(id => Object.hasOwn(answers, id)),
        multipleChoice: ChapterQuiz.sanitizeAnswers(own(workbook, "multipleChoice") ?? {}, strict),
      },
    };
  }

  function validateQuiz(value) {
    // 기본기 문제집의 답안도 번호·보기·채점 여부를 확인합니다. null은 백업에 그 기록이 없다는 뜻입니다.
    if (value === null) return null;
    if (!object(value) || value.version !== 2 || !object(value.answers)) {
      throw new Error("객관식 기록 버전 또는 형식이 올바르지 않습니다.");
    }
    const answers = {};
    for (const [id, record] of Object.entries(value.answers)) {
      const match = /^tech-v2-(\d{2})-[1-5]-(?:identify-)?(concept|situation|implementation|risk|verification)$/.exec(id);
      if (!match || !chapters.includes(match[1]) || !object(record)
        || !Number.isInteger(record.selected) || record.selected < 0 || record.selected > 3
        || typeof record.graded !== "boolean") {
        throw new Error("객관식 풀이 항목이 올바르지 않습니다.");
      }
      answers[id] = { selected: record.selected, graded: record.graded };
    }
    return { version: 2, answers };
  }

  function createBackup(workbook, quiz) {
    // format은 이 프로그램의 백업이라는 이름표이고, version은 파일을 읽는 규칙의 버전입니다.
    return {
      format: "company-work-system-study-backup", version: 2,
      exportedAt: new Date().toISOString(),
      workbook: normalizeWorkbook(workbook, true), quiz: validateQuiz(quiz),
    };
  }

  function parseBackup(input) {
    // JSON은 글자로 자료를 정리하는 규칙입니다. JSON.parse는 그 글자를 다룰 수 있는 값으로 바꿉니다.
    const value = JSON.parse(input);
    if (!object(value) || value.format !== "company-work-system-study-backup" || ![1, 2].includes(value.version)) {
      throw new Error("지원하는 워크북 백업 파일이 아닙니다.");
    }
    return { workbook: normalizeWorkbook(value.workbook, true), quiz: validateQuiz(value.quiz) };
  }

  // 두 저장 항목 중 하나라도 실패하면 원래 값으로 되돌립니다.
  function restore(storage, backup) {
    // JSON.stringify는 반대로 자료를 글자로 바꿉니다. 브라우저 저장소에는 이 글자를 넣습니다.
    const workbook = JSON.stringify(normalizeWorkbook(backup.workbook, true));
    const quiz = validateQuiz(backup.quiz);
    const before = storage.getItem(workbookKey);
    // 새 기록을 쓰기 전에 원래 기록을 기억해 둡니다. 중간에 저장이 실패하면 되돌리는 데 씁니다.
    const quizBefore = storage.getItem(quizKey);
    try {
      if (quiz !== null) storage.setItem(quizKey, JSON.stringify(quiz));
      storage.setItem(workbookKey, workbook);
    } catch (error) {
      // try 안에서 오류가 나면 catch가 처리합니다. 두 기록 중 하나만 바뀐 상태가 남지 않게 복구합니다.
      try {
        if (storage.getItem(workbookKey) !== before) {
          if (before === null) storage.removeItem(workbookKey);
          else storage.setItem(workbookKey, before);
        }
        if (storage.getItem(quizKey) !== quizBefore) {
          if (quizBefore === null) storage.removeItem(quizKey);
          else storage.setItem(quizKey, quizBefore);
        }
      } catch (_) {
        throw new Error("복원과 원래 기록 복구에 실패했습니다. 백업 파일을 보관하고 브라우저 저장 공간을 확인해 주세요.");
      }
      throw error;
    }
  }

  globalThis.StudyRecords = { workbookKey, quizKey, normalizeWorkbook, createBackup, parseBackup, restore };
})();
