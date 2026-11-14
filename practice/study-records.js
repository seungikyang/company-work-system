// 학습 기록의 자료형을 확인합니다. 손상된 브라우저 기록은 정상 항목만 읽고,
// 가져올 백업은 전체 형식이 맞을 때만 승인합니다.
// 백업은 학습 노트를 파일로 복사해 보관하는 일, 복원은 그 파일의 기록을 다시 불러오는 일입니다.
// 읽는 순서: normalizeWorkbook으로 정리 → createBackup으로 묶기 → parseBackup으로 검사 → restore로 저장.
// 왜? 파일 전체를 함수로 감싸면 안쪽 변수 이름이 다른 파일과 부딪히지 않습니다.
//      빌드 도구 없이 <script> 태그로 여러 파일을 불러오는 구조라 이 방법으로 칸막이를 칩니다.
(function () {
  // 왜? 엄격 모드는 오타로 생긴 변수 같은 실수를 조용히 넘기지 않고 바로 오류로 알려 줍니다.
  "use strict";
  // 왜? 이름 끝의 v1·v2는 저장 형식의 버전입니다. 형식이 바뀌면 이름을 바꿔 옛 기록과 새 기록이 섞이지 않게 합니다.
  const workbookKey = "company-workbook-state-v1";
  // 저장 키는 서랍에 붙인 이름표와 같습니다. 워크북과 기본기 문제집은 서로 다른 서랍에 넣습니다.
  const quizKey = "company-work-system.fundamentals.technology.v2";
  const chapters = Array.from({ length: 40 }, (_, i) => String(i).padStart(2, "0"));
  // 왜? JavaScript에서는 null과 배열도 typeof 결과가 "object"입니다. 진짜 { } 모양만 고르려고 두 경우를 따로 뺍니다.
  const object = value => value !== null && typeof value === "object" && !Array.isArray(value);
  // 왜? Object.hasOwn은 그 값이 직접 가진 칸만 확인합니다. 백업 파일에 "__proto__" 같은 특수한 이름이 있어도
  //      물려받은 값을 실수로 읽지 않게 하려는 안전장치입니다.
  const own = (value, key) => object(value) && Object.hasOwn(value, key) ? value[key] : undefined;

  // 왜 모드가 두 개일까요?
  // - 브라우저에 이미 쌓인 기록(strict=false): 일부가 망가져도 나머지를 살리는 편이 사용자에게 이득입니다.
  // - 새로 가져올 백업 파일(strict=true): 이상한 곳이 하나라도 있으면 통째로 거절합니다.
  //   반쯤 맞는 파일로 멀쩡한 기존 기록을 덮어쓰는 사고를 막기 위해서입니다.
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
      // 왜? new Date는 2월 30일을 오류 없이 3월 2일로 바꿔 버립니다. 그래서 다시 글자로 바꿔 원래 글자와 같은지 비교합니다.
      //      끝의 Z(세계 표준시)는 사는 지역의 시간대 때문에 날짜가 하루 밀리는 일을 막습니다.
      const date = new Date(`${deadline}T00:00:00Z`);
      if (!check(/^\d{4}-\d{2}-\d{2}$/.test(deadline)
        && Number.isFinite(date.getTime()) && date.toISOString().slice(0, 10) === deadline)) deadline = "";
    }
    const answers = {};
    const source = own(workbook, "answers");
    if (check(object(source))) {
      for (const [id, answer] of Object.entries(source)) {
        if (check(chapters.includes(id) && typeof answer === "string")) {
          // 왜? 공백만 있는 답은 '작성한 답'으로 세지 않으려고 저장하지 않습니다.
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
        // 왜? 답안이 없는 챕터가 '복습 완료'로 남으면 진척도가 실제보다 높아 보이므로 답안이 있는 챕터만 남깁니다.
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
      // 왜? 문제 번호 규칙을 정규식(글자 모양 검사 규칙)으로 확인해, 존재하지 않는 문제 번호가 백업에 섞여 들어오는 것을 막습니다.
      const match = /^tech-v2-(\d{2})-[1-5]-(?:identify-)?(concept|situation|implementation|risk|verification)$/.exec(id);
      if (!match || !chapters.includes(match[1]) || !object(record)
        || !Number.isInteger(record.selected) || record.selected < 0 || record.selected > 3
        || typeof record.graded !== "boolean") {
        throw new Error("객관식 풀이 항목이 올바르지 않습니다.");
      }
      // 왜? 받은 값을 그대로 쓰지 않고 필요한 두 칸만 새로 담습니다. 파일에 덧붙은 쓸모없는 칸이 저장소로 따라 들어오지 않게 합니다.
      answers[id] = { selected: record.selected, graded: record.graded };
    }
    return { version: 2, answers };
  }

  function createBackup(workbook, quiz) {
    // format은 이 프로그램의 백업이라는 이름표이고, version은 파일을 읽는 규칙의 버전입니다.
    return {
      format: "company-work-system-study-backup", version: 2,
      exportedAt: new Date().toISOString(),
      // 왜? 내보낼 때도 엄격하게 검사합니다. 망가진 백업을 만들어 두면 정작 복원이 필요할 때 실패하기 때문입니다.
      workbook: normalizeWorkbook(workbook, true), quiz: validateQuiz(quiz),
    };
  }

  function parseBackup(input) {
    // JSON은 글자로 자료를 정리하는 규칙입니다. JSON.parse는 그 글자를 다룰 수 있는 값으로 바꿉니다.
    const value = JSON.parse(input);
    // 왜? 예전 규칙(버전 1)으로 만든 백업 파일도 계속 복원할 수 있도록 두 버전을 모두 받습니다.
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
        // 왜? 이미 원래 값과 같은 항목은 건드리지 않습니다. 복구 중에 또 저장하다가 실패할 가능성을 줄이려는 것입니다.
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
      // 왜? 복구에 성공해도 오류를 다시 던집니다. 그래야 화면 쪽이 '복원 실패'를 알고 사용자에게 안내할 수 있습니다.
      throw error;
    }
  }

  // 왜? import 없이 <script>로 불러오는 구조라, 다른 파일이 쓸 기능만 전역 이름 하나에 묶어 공개합니다.
  //      나머지 도우미 함수는 바깥에서 볼 수 없어 실수로 바꿀 일이 없습니다.
  globalThis.StudyRecords = { workbookKey, quizKey, normalizeWorkbook, createBackup, parseBackup, restore };
})();
