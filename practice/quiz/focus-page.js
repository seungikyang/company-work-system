// 한 문제씩 표시하는 화면입니다. 문항·정답과 저장 키는 기존 문제집과 공유합니다.
(function () {
  "use strict";
  const byId = id => document.getElementById(id);
  const parameters = new URLSearchParams(location.search);
  // 왜 제목을 직접 적었을까요? 코드 연결 문제 자료에는 챕터 번호만 있어서, 화면에 보일 이름을 순서대로 짝지어 둡니다.
  const codeTitles = ["빌드와 애플리케이션 설정", "User 엔티티", "Department 엔티티", "Employee 엔티티", "LeaveRequest 엔티티와 enum", "Notice 엔티티", "ApprovalDocument 엔티티", "Repository 메서드 쿼리", "직원 등록 Service", "직원 검색 + 페이징", "휴가 신청 Service", "휴가 승인/반려 Service", "공지사항 Service", "결재 문서 작성/요청 Service", "결재 승인/반려 Service", "ErrorCode 와 응답 모델", "GlobalExceptionHandler", "DTO 검증", "EmployeeController", "LeaveController", "보안 흐름", "통합 테스트 흐름", "문서화", "인증 — 로그인 / 로그아웃 / 내 정보 / 비밀번호 변경", "부서 Service — CRUD + 삭제 정책", "부서 Controller + DTO", "공지 Controller + DTO", "결재 Controller + DTO — my / pending / submit / approve / reject", "직원 상세 / 수정 / 퇴사 처리", "휴가 — 내 목록 / 상세 / 취소 + 관리자 목록", "결재 — my / pending / detail 권한 검사", "Repository — User / Department / Leave / Notice / Approval", "응답 DTO 매핑 패턴", "핵심 Bean 설정 — JPA Auditing / PasswordEncoder / WebMvcConfig", "세션 인터셉터 + @CurrentUser ArgumentResolver", "Thymeleaf 화면", "패키지 구조와 계층 책임", "비즈니스 규칙 다층 검증 매트릭스", "트러블슈팅 워크북", "면접 카드 + 커밋 + PR 템플릿"];
  try {
    const basics = Fundamentals.buildBank(globalThis.FUNDAMENTALS_SOURCES);
    const basicChapters = new Map(basics.chapters.map(chapter => [chapter.id, chapter]));
    const banks = {
      chapters: { questions: ChapterQuiz.questions.map(question => ({ ...question, chapterTitle: codeTitles[Number(question.chapter)] })), chapters: codeTitles.map((title, i) => ({ id: String(i).padStart(2, "0"), title })), name: "코드 연결" },
      fundamentals: { questions: basics.questions.map(question => ({
        ...question,
        chapterTitle: basicChapters.get(question.chapter).title,
        // 왜? 공통 카드 화면(ChapterQuizUI)은 보기를 글자 목록으로 받으므로, 두 문제집의 모양을 하나로 맞춥니다.
        // 덕분에 카드 그리기·채점 코드를 한 벌만 관리하면 됩니다.
        options: question.options.map(option => option.text),
        optionExplanations: question.options.map((option, i) => {
          const topic = basicChapters.get(question.chapter).topics.find(item => item.name === option.topic);
          return question.kind.startsWith("identify-")
            ? `${option.topic}: ${topic.facets[0]}.`
            : `${option.topic}에 대한 설명입니다.${i !== question.answer ? ` 질문의 ‘${question.topic}’과 구분하세요.` : ""}`;
        }),
      })), chapters: basics.chapters, name: "기술 기본기" },
    };
    const embedded = parameters.get("embed") === "1";
    document.body.classList.toggle("embedded-quiz", embedded);
    if (embedded) {
      // 문서 안에서는 문제와 보기를 먼저 보여 주고, 학습 통계는 풀이 아래에 둡니다.
      byId("results").after(document.querySelector(".session-overview"));
    }
    if (embedded && window.parent !== window) {
      // 문서 화면 안에서도 이 퀴즈를 그대로 사용하고, 해설 길이에 맞춰 높이를 알립니다.
      // 왜 높이를 알릴까요? 문서 안 iframe(화면 속 작은 화면)은 내용 높이를 스스로 모릅니다. 해설을 펼쳐 높아지면 잘리지 않게 부모에게 알려 줍니다.
      // 받는 곳을 location.origin으로 정해 같은 사이트에만 보냅니다. 파일로 직접 열면 origin이 "null"이라 그때만 "*"를 씁니다.
      const reportHeight = () => window.parent.postMessage({ type: "company-work-system:quiz-height", height: Math.ceil(document.body.getBoundingClientRect().height) }, location.origin === "null" ? "*" : location.origin);
      new ResizeObserver(reportHeight).observe(document.body);
    }
    let bankId = parameters.get("bank") === "fundamentals" ? "fundamentals" : "chapters";
    const chapterIds = new Set(banks.chapters.questions.map(question => question.id));
    let documentItems = bankId === "chapters"
      // 왜? 주소창 값은 누구나 바꿀 수 있습니다. 실제 있는 문제 번호만 남기고(filter), 중복을 지우고(Set), 50개로 제한해 안전하게 씁니다.
      ? [...new Set((parameters.get("items") || "").split(",").filter(id => chapterIds.has(id)))].slice(0, 50)
      : [];
    let answers = {};
    // 왜? 저장소를 읽지 못하는 상황에서도 문제집을 바꿨다 돌아오면 방금 푼 답이 화면에 남도록 문제집별로 메모리에 보관합니다.
    const memoryAnswers = { chapters: {}, fundamentals: {} };
    const pendingAnswers = { chapters: new Map(), fundamentals: new Map() };
    let filtered = [];
    let index = 0;
    let activeFilters = { chapter: "", mode: "all", query: "", limit: "all" };

    function element(tag, className, text) {
      const node = document.createElement(tag);
      if (className) node.className = className;
      if (text !== undefined) node.textContent = text;
      return node;
    }
    const storageKey = () => bankId === "chapters" ? StudyRecords.workbookKey : Fundamentals.storageKey;
    function parseStored(raw) {
      let value;
      try { value = JSON.parse(raw || "null"); }
      catch (_) {
        // 읽을 수 없는 JSON은 비운 상태에서 다시 저장할 수 있게 복구합니다.
        value = null;
        byId("storage-status").textContent = "손상된 저장 기록을 읽지 못했습니다. 새로 푼 문제부터 다시 저장합니다.";
      }
      // 왜? 코드 연결 답안은 메인 워크북 기록 안에 함께 저장됩니다. 워크북 전체를 읽고 그 일부만 바꿔야
      // 목표·작성 답안·진척도 같은 다른 칸을 지우지 않습니다. 같은 이유로 백업에도 자동으로 함께 들어갑니다.
      if (bankId === "chapters") return StudyRecords.normalizeWorkbook(value);
      if (value !== null && value.version !== Fundamentals.version) throw new Error("지원하지 않는 풀이 기록 버전");
      return { version: Fundamentals.version, answers: Fundamentals.sanitizeAnswers(value?.answers, banks.fundamentals.questions) };
    }
    function records(stored) {
      return bankId === "chapters" ? stored.workbook.multipleChoice : stored.answers;
    }
    function readAnswers() {
      answers = memoryAnswers[bankId];
      try {
        answers = records(parseStored(localStorage.getItem(storageKey())));
        for (const [id, record] of pendingAnswers[bankId]) {
          if (record) answers[id] = record;
          else delete answers[id];
        }
        memoryAnswers[bankId] = answers;
      }
      catch (_) {
        byId("storage-status").textContent = "이전 기록을 읽지 못했습니다. 지금은 화면에서 풀 수 있으며 다음 입력 때 저장을 다시 시도합니다.";
      }
    }
    function saveAnswer(id, record) {
      pendingAnswers[bankId].set(id, record);
      if (record) answers[id] = record;
      else delete answers[id];
      memoryAnswers[bankId] = answers;
      try {
        // 매번 최신 저장본을 읽어 다른 화면의 목표·답안·진척도를 보존합니다.
        const stored = parseStored(localStorage.getItem(storageKey()));
        const latest = records(stored);
        for (const [changedId, changedRecord] of pendingAnswers[bankId]) {
          if (changedRecord) latest[changedId] = changedRecord;
          else delete latest[changedId];
        }
        localStorage.setItem(storageKey(), JSON.stringify(stored));
        answers = { ...latest };
        memoryAnswers[bankId] = answers;
        pendingAnswers[bankId].clear();
        byId("storage-status").textContent = "✓ 풀이 기록 저장됨 · 기존 문제집과 메인 워크북의 백업에 함께 반영됩니다.";
      } catch (_) {
        byId("storage-status").textContent = "브라우저에 저장하지 못했습니다. 현재 화면에서는 계속 풀 수 있습니다. 다음 입력 때 다시 시도합니다.";
      }
      updateStatistics();
      renderMap();
      updateSource();
    }
    function statistics() {
      let graded = 0;
      let correct = 0;
      filtered.forEach(question => {
        const record = answers[question.id];
        if (record?.graded) {
          graded += 1;
          if (record.selected === question.answer) correct += 1;
        }
      });
      return { total: filtered.length, graded, correct, wrong: graded - correct };
    }
    function updateStatistics() {
      const stats = statistics();
      byId("total-count").textContent = stats.total.toLocaleString("ko-KR");
      byId("correct-count").textContent = stats.correct;
      byId("wrong-count").textContent = stats.wrong;
      byId("remaining-count").textContent = stats.total - stats.graded;
      byId("accuracy").textContent = `정답률 ${stats.graded ? Math.round(stats.correct / stats.graded * 100) + "%" : "—"}`;
      byId("study-progress").max = stats.total || 1;
      byId("study-progress").value = stats.graded;
      byId("progress-text").textContent = `${stats.total ? Math.round(stats.graded / stats.total * 100) : 0}% · ${stats.graded}/${stats.total}`;
    }
    function updateUrl() {
      const next = new URLSearchParams({ bank: bankId });
      for (const key of ["chapter", "mode", "query"]) if (activeFilters[key] && activeFilters[key] !== "all") next.set(key, activeFilters[key]);
      if (activeFilters.limit !== "all") {
        next.set("limit", activeFilters.limit);
        if (filtered[0]) next.set("start", filtered[0].id);
      }
      if (byId("instant-grade").checked) next.set("instant", "1");
      if (embedded) next.set("embed", "1");
      if (documentItems.length) next.set("items", documentItems.join(","));
      if (filtered[index]) next.set("question", filtered[index].id);
      // 파일로 직접 열어도 문제 풀이를 계속할 수 있도록 주소 갱신 실패는 허용합니다.
      // 왜 주소를 바꿀까요? 새로고침하거나 주소를 공유해도 같은 문제·같은 조건으로 다시 열리게 하려는 것입니다.
      // pushState 대신 replaceState를 쓰는 이유: 문제를 넘길 때마다 '뒤로 가기' 기록이 수십 개씩 쌓이지 않게 합니다.
      try { history.replaceState(null, "", `${location.pathname}?${next}`); } catch (_) { /* file:// */ }
    }
    function updateSource() {
      const question = filtered[index];
      if (!question) return;
      const identify = question.kind?.startsWith("identify-");
      byId("question-source").hidden = Boolean(identify && !answers[question.id]?.graded);
      byId("source-evidence").textContent = bankId === "fundamentals"
        ? `${Fundamentals.scopes[question.scope]}\n근거 파일: ${question.source}\n확인할 표현: ${question.evidence}`
        : `코드 연결 ${question.chapter}장 · 기존 문제집에서 힌트와 Starter·Hard 코드를 함께 확인할 수 있습니다.`;
      byId("reading-link").href = bankId === "fundamentals"
        ? `./workbook-viewer.html?file=${encodeURIComponent(question.reading)}`
        : `./problems.html#problem-${question.chapter}`;
    }
    function renderMap() {
      const map = byId("question-map");
      map.replaceChildren();
      // 왜 9개만? 수백 개 번호 버튼을 다 그리면 화면이 복잡하고 느려집니다. 현재 문제를 가운데 두고 앞뒤 4개씩만 보여 줍니다.
      const start = Math.max(0, Math.min(index - 4, filtered.length - 9));
      filtered.slice(start, start + 9).forEach((question, offset) => {
        const position = start + offset;
        const record = answers[question.id];
        const status = record?.graded ? record.selected === question.answer ? "정답" : "오답" : "미풀이";
        const button = element("button", record?.graded ? status === "정답" ? "map-correct" : "map-wrong" : "", position + 1);
        button.type = "button";
        button.setAttribute("aria-label", `${position + 1}번째 문제 · ${status}`);
        if (position === index) button.setAttribute("aria-current", "step");
        button.addEventListener("click", () => { index = position; renderQuestion(true); });
        map.append(button);
      });
    }
    function renderQuestion(focus = false) {
      const question = filtered[index];
      byId("results").hidden = true;
      byId("question-area").hidden = !question;
      byId("empty-state").hidden = Boolean(question);
      updateStatistics();
      updateUrl();
      if (!question) return;
      const chapter = banks[bankId].chapters.find(item => item.id === question.chapter);
      byId("question-position").textContent = `QUESTION ${String(index + 1).padStart(2, "0")} / ${filtered.length}`;
      byId("question-tag").textContent = `${question.chapter} · ${chapter.title}`;
      byId("navigation-position").textContent = `${index + 1} / ${filtered.length}`;
      byId("previous-question").disabled = index === 0;
      byId("next-question").disabled = false;
      byId("next-question").textContent = index === filtered.length - 1 ? "학습 결과 보기 →" : "다음 문제 →";
      ChapterQuizUI.mount(byId("focused-question"), [question], { getAnswers: () => answers, onChange: saveAnswer, headingLevel: 2, showNumber: false, gradeOnSelect: () => byId("instant-grade").checked });
      byId("question-source").open = false;
      updateSource();
      renderMap();
      if (focus) {
        const heading = byId("focused-question").querySelector("h2");
        // 왜? 제목은 원래 키보드로 이동할 수 없는 요소입니다. tabIndex = -1이면 Tab 순서에는 끼지 않으면서 코드로 focus를 줄 수 있습니다.
        // 문제가 바뀐 것을 화면 낭독기 사용자도 바로 알 수 있습니다.
        heading.tabIndex = -1;
        heading.focus();
      }
    }
    function fillChapters() {
      const select = byId("chapter-filter");
      select.replaceChildren(element("option", "", "전체 챕터 / 기술 단원"));
      select.firstChild.value = "";
      banks[byId("bank-filter").value].chapters.forEach(chapter => {
        const option = element("option", "", `${chapter.id} · ${chapter.title}`);
        option.value = chapter.id;
        select.append(option);
      });
    }
    function applyFilters(requestedId = "", focus = true, startId = "") {
      if (bankId !== byId("bank-filter").value) documentItems = [];
      bankId = byId("bank-filter").value;
      readAnswers();
      activeFilters = { chapter: byId("chapter-filter").value, mode: byId("mode-filter").value, query: byId("search-filter").value.trim(), limit: byId("limit-filter").value, ids: documentItems };
      byId("document-scope").hidden = !documentItems.length;
      const session = FocusSession.select(banks[bankId].questions, answers, activeFilters, requestedId, startId);
      filtered = session.questions;
      // 복습 중에는 문제 목록을 유지하여 재풀이를 눌러도 현재 카드가 사라지지 않습니다.
      index = filtered.findIndex(question => question.id === requestedId);
      if (index < 0) index = Math.max(0, filtered.findIndex(question => !answers[question.id]?.graded));
      const chapter = banks[bankId].chapters.find(item => item.id === activeFilters.chapter);
      byId("scope-title").textContent = `${banks[bankId].name} · ${chapter ? chapter.title : "전체 범위"}${activeFilters.limit !== "all" ? ` · ${session.total}문항 중 ${filtered.length}문제` : ""}`;
      byId("list-link").href = bankId === "chapters" ? "./problems.html" : "./fundamentals.html";
      renderQuestion(focus);
    }
    byId("bank-filter").value = bankId;
    fillChapters();
    if (banks[bankId].chapters.some(chapter => chapter.id === parameters.get("chapter"))) byId("chapter-filter").value = parameters.get("chapter");
    if (["all", "wrong", "unanswered", "correct"].includes(parameters.get("mode"))) byId("mode-filter").value = parameters.get("mode");
    if (["10", "20", "50"].includes(parameters.get("limit"))) byId("limit-filter").value = parameters.get("limit");
    byId("instant-grade").checked = parameters.get("instant") === "1";
    function updateInteractionHint() {
      byId("interaction-hint").textContent = byId("instant-grade").checked
        ? "보기를 선택하면 바로 정답과 해설이 열려요."
        : "보기를 고른 뒤 채점하면 해설이 열려요.";
    }
    updateInteractionHint();
    byId("instant-grade").addEventListener("change", () => { updateInteractionHint(); updateUrl(); });
    // 왜? 주소에서 온 검색어는 길이를 제한해, 지나치게 긴 값으로 화면이 깨지거나 느려지지 않게 합니다.
    byId("search-filter").value = (parameters.get("query") || "").slice(0, 100);
    byId("bank-filter").addEventListener("change", fillChapters);
    // 왜? 좁은 휴대폰 화면에서는 필터를 접어 문제를 먼저 보여 주고, 넓은 화면에서는 펼쳐 둡니다.
    // 화면 크기가 바뀌는 순간(change)도 따라가도록 이벤트를 연결합니다.
    const mobileLayout = matchMedia("(max-width: 700px)");
    byId("filter-panel").open = !mobileLayout.matches;
    mobileLayout.addEventListener("change", event => { byId("filter-panel").open = !event.matches; });
    byId("quiz-filters").addEventListener("submit", event => {
      event.preventDefault();
      if (mobileLayout.matches) byId("filter-panel").open = false;
      applyFilters();
    });
    byId("clear-document-scope").addEventListener("click", () => {
      documentItems = [];
      applyFilters();
    });
    document.querySelectorAll("[data-quick-bank]").forEach(button => {
      button.addEventListener("click", () => {
        documentItems = [];
        byId("bank-filter").value = button.dataset.quickBank;
        fillChapters();
        byId("mode-filter").value = "all";
        byId("search-filter").value = "";
        byId("limit-filter").value = "10";
        if (mobileLayout.matches) byId("filter-panel").open = false;
        applyFilters();
      });
    });
    byId("previous-question").addEventListener("click", () => { if (index > 0) { index -= 1; renderQuestion(true); } });
    byId("next-question").addEventListener("click", () => {
      if (index < filtered.length - 1) { index += 1; renderQuestion(true); return; }
      const stats = statistics();
      byId("question-area").hidden = true;
      byId("results").hidden = false;
      byId("results-summary").textContent = `${stats.total}문항 중 ${stats.graded}문항 채점 · 정답 ${stats.correct} · 오답 ${stats.wrong} · 미풀이 ${stats.total - stats.graded}`;
      const accuracy = stats.graded ? Math.round(stats.correct / stats.graded * 100) : 0;
      byId("result-percent").textContent = stats.graded ? `${accuracy}%` : "—";
      byId("result-score").style.setProperty("--score-angle", `${accuracy * 3.6}deg`);
      byId("results-message").textContent = stats.graded < stats.total
        ? "아직 풀지 않은 문제가 있어요. 문제로 돌아가 학습을 이어가세요."
        : stats.wrong ? "틀린 문제의 해설을 한 번 더 읽고, 다시 풀어 보세요." : "모두 정확히 이해했어요. 다음 학습도 이어가 볼까요?";
      byId("review-wrong").disabled = stats.wrong === 0;
      byId("continue-study").hidden = !FocusSession.select(banks[bankId].questions, answers, { ...activeFilters, mode: "unanswered", limit: "all" }).questions.length;
      byId("results-heading").focus();
    });
    byId("return-question").addEventListener("click", () => renderQuestion(true));
    byId("review-wrong").addEventListener("click", () => { byId("bank-filter").value = bankId; fillChapters(); byId("chapter-filter").value = activeFilters.chapter; byId("search-filter").value = activeFilters.query; byId("limit-filter").value = activeFilters.limit; byId("mode-filter").value = "wrong"; applyFilters(); });
    byId("continue-study").addEventListener("click", () => {
      byId("bank-filter").value = bankId;
      fillChapters();
      byId("chapter-filter").value = activeFilters.chapter;
      byId("search-filter").value = activeFilters.query;
      byId("limit-filter").value = activeFilters.limit;
      byId("mode-filter").value = "unanswered";
      applyFilters();
    });
    byId("show-all").addEventListener("click", () => { byId("chapter-filter").value = ""; byId("mode-filter").value = "all"; byId("search-filter").value = ""; applyFilters(); });
    // 다른 탭에서 복원하거나 채점한 기록도 반영합니다. 현재 복습 목록은 유지합니다.
    window.addEventListener("storage", event => {
      if (event.key === storageKey() || event.key === null) { readAnswers(); renderQuestion(); }
    });
    byId("apply-filters").disabled = false;
    applyFilters(parameters.get("question"), false, parameters.get("start"));
  } catch (error) {
    byId("load-error").hidden = false;
    byId("question-area").hidden = true;
    console.error("집중 퀴즈 준비 실패", error);
  }
  // 왜? 위 try/catch로도 잡지 못한 예상 밖의 문제로 준비가 끝나지 않으면(버튼이 여전히 잠겨 있으면)
  // 빈 화면 대신 안내 문구를 보여 주는 마지막 안전장치입니다.
  window.addEventListener("load", () => {
    if (byId("apply-filters").disabled) {
      byId("load-error").hidden = false;
      byId("question-area").hidden = true;
    }
  });
})();
