// 중학생 눈높이: 이 파일은 기본기 문제집의 화면 담당입니다.
// 문제 준비 → 이전 답안 읽기 → 카드 표시 → 사용자 선택·채점 → 저장 순서로 움직입니다.
(function () {
  "use strict";
  const byId = id => document.getElementById(id);
  // 한 번에 25문항만 보여 줍니다. 2,000개의 카드를 모두 붙이는 것보다 읽기 쉽고 화면 작업도 줄어듭니다.
  const PAGE_SIZE = 25;
  const letters = ["①", "②", "③", "④"];
  const core = globalThis.Fundamentals;

  // 화면 부품을 만드는 공통 도구입니다. textContent로 넣은 글자는 HTML 코드로 실행되지 않습니다.
  function element(tag, className, text) {
    const node = document.createElement(tag);
    if (className) node.className = className;
    if (text !== undefined) node.textContent = text;
    return node;
  }

  try {
    const { chapters, questions } = core.buildBank(globalThis.FUNDAMENTALS_SOURCES);
    // answers는 문제 번호별 풀이 노트이고, filtered는 현재 검색 조건에 맞는 문제 목록입니다.
    // page는 현재 페이지 번호입니다. 컴퓨터는 첫 페이지를 0으로, 화면은 1로 표시합니다.
    let answers = {};
    // 저장하지 못한 변경만 따로 기억합니다. 다른 화면의 새 답안 위에 이 변경만 합칩니다.
    const pendingAnswers = new Map();
    let filtered = questions;
    let page = 0;
    function readStoredAnswers() {
      // 보관함의 글자를 JSON.parse로 다시 자료로 바꿉니다. 기록이 없으면 처음부터 시작합니다.
      const raw = localStorage.getItem(core.storageKey);
      let stored;
      try { stored = JSON.parse(raw || "null"); }
      catch (_) {
        byId("storage-status").textContent = "손상된 저장 기록을 읽지 못했습니다. 새로 푼 문제부터 다시 저장합니다.";
        return {};
      }
      if (stored === null) return {};
      if (stored.version !== core.version || !stored.answers || typeof stored.answers !== "object") throw new Error("저장 형식 오류");
      const cleaned = core.sanitizeAnswers(stored.answers, questions);
      if (Object.keys(cleaned).length !== Object.keys(stored.answers).length) {
        byId("storage-status").textContent = "형식이 맞는 풀이 기록만 복원했습니다. 일부 손상된 항목은 다시 풀어 주세요.";
      }
      return cleaned;
    }
    function mergePending(latest) {
      for (const [id, record] of pendingAnswers) {
        if (record) latest[id] = record;
        else delete latest[id];
      }
      return latest;
    }
    function readAnswers() {
      try {
        answers = mergePending(readStoredAnswers());
        return true;
      } catch (_) {
        byId("storage-status").textContent = "저장 기록을 읽을 수 없습니다. 현재 화면에서는 계속 풀 수 있으며, 다음 선택부터 저장을 다시 시도합니다.";
        return false;
      }
    }
    readAnswers();

    // 전체 옛 노트를 덮어쓰지 않고 방금 바꾼 문항과 미저장 변경을 최신 기록에 합칩니다.
    function save(id, record) {
      pendingAnswers.set(id, record);
      if (record) answers[id] = record;
      else delete answers[id];
      try {
        const latest = mergePending(readStoredAnswers());
        localStorage.setItem(core.storageKey, JSON.stringify({ version: core.version, answers: latest }));
        answers = latest;
        pendingAnswers.clear();
        byId("storage-status").textContent = "풀이 기록을 이 브라우저에 저장했습니다. 기존 워크북의 답안과 완료 기록은 별도로 유지됩니다.";
      } catch (_) {
        byId("storage-status").textContent = "브라우저에 저장할 수 없습니다. 현재 화면에서는 계속 풀 수 있지만 새로고침하면 이번 변경이 사라질 수 있습니다.";
      }
      updateStatistics();
    }

    // 채점·정답·오답·남은 수를 계산해서 화면의 숫자와 진행 막대를 맞춥니다.
    function updateStatistics() {
      const stats = core.statistics(questions, answers);
      byId("study-progress").max = stats.total;
      for (const name of ["answered", "correct", "wrong", "remaining"]) {
        byId(`${name}-count`).textContent = stats[name].toLocaleString("ko-KR");
      }
      byId("study-progress").value = stats.answered;
    }

    // 한 문제를 질문·보기 네 개·채점 버튼·해설로 조립합니다. 문제마다 자기 기록을 읽습니다.
    function makeCard(question) {
      const card = element("article", "question-card");
      card.id = question.id;
      const chapter = chapters[Number(question.chapter)];
      const kind = core.kinds.find(item => item.id === question.kind);
      card.append(element("p", "question-meta", `문제 ${String(question.number).padStart(4, "0")} · 기술 ${chapter.id} ${chapter.title} · ${kind.label}`));
      card.append(element("p", "scope-label", `${core.scopes[question.scope]} · ${question.technology}`));
      const heading = element("h3", "", question.prompt);
      heading.id = `${question.id}-title`;
      const evidence = element("div", "source-evidence");
      evidence.append(element("p", "", `근거 파일: ${question.source}`));
      evidence.append(element("p", "", question.scope === "workbook"
        ? "파일에서 확인할 코드·설정 표현"
        : "실습 자료에서 확인할 코드·주석 표현 (TODO 빈칸·확장 예제 포함)"));
      evidence.append(element("code", "", question.evidence));
      const group = element("fieldset");
      group.setAttribute("aria-labelledby", heading.id);
      group.append(element("legend", "sr-only", "보기 네 개 중 하나를 선택하세요."));
      const choices = question.options.map((option, i) => {
        const label = element("label", "choice");
        const input = element("input");
        input.type = "radio";
        // 같은 name을 쓰는 라디오 버튼끼리는 한 묶음이므로 네 보기 중 하나만 선택할 수 있습니다.
        input.name = question.id;
        input.value = String(i);
        input.addEventListener("change", () => {
          save(question.id, { selected: i, graded: false });
          feedback.replaceChildren();
        });
        label.append(input, element("span", "", `${letters[i]} ${option.text}`));
        group.append(label);
        return { label, input };
      });
      const actions = element("div", "question-actions");
      const grade = element("button", "grade-button", "채점하고 해설 보기");
      grade.type = "button";
      const source = element("a", "", "연결된 기술 설명 →");
      source.href = `./workbook-viewer.html?file=${encodeURIComponent(question.reading)}`;
      actions.append(grade, source);
      const feedback = element("div", "feedback");
      feedback.setAttribute("aria-live", "polite");
      feedback.setAttribute("aria-atomic", "true");

      // 저장된 선택을 체크하고 채점했다면 보기 색과 정답·해설을 보여 줍니다.
      function refresh() {
        const record = answers[question.id];
        const graded = record?.graded === true;
        choices.forEach(({ label, input }, i) => {
          input.checked = record?.selected === i;
          input.disabled = graded;
          label.classList.toggle("correct", graded && i === question.answer);
          label.classList.toggle("incorrect", graded && record.selected === i && i !== question.answer);
        });
        grade.textContent = graded ? "이 문제 다시 풀기" : "채점하고 해설 보기";
        feedback.replaceChildren();
        if (graded) {
          const correct = record.selected === question.answer;
          feedback.append(element("p", correct ? "verdict" : "verdict wrong-verdict",
            `${correct ? "정답입니다." : "오답입니다."} 내 선택 ${letters[record.selected]} · 정답 ${letters[question.answer]}`));
          const explanation = element("div", "explanation");
          explanation.append(element("p", "", `정답: ${question.options[question.answer].text}`), element("p", "", question.explanation));
          const details = element("details");
          details.append(element("summary", "", kind.identify ? "다른 기술 항목과 비교하기" : "다른 보기는 어떤 항목을 설명하나요?"));
          const list = element("ul");
          question.options.forEach((option, i) => {
            if (i !== question.answer) {
              const topic = chapter.topics.find(item => item.name === option.topic);
              list.append(element("li", "", kind.identify
                ? `${letters[i]} ${option.topic}: ${topic.facets[0]}.`
                : `${letters[i]} ${option.topic}에 대한 설명입니다. 질문의 ‘${question.topic}’과 구분하세요.`));
            }
          });
          details.append(list);
          explanation.append(details);
          // 기술 식별 문항은 코드 표현이 정답 이름의 힌트가 되므로 채점한 뒤 근거를 표시합니다.
          if (kind.identify) explanation.append(evidence);
          feedback.append(explanation);
        }
      }

      // 클릭 이벤트는 버튼을 눌렀다는 알림입니다. 다시 풀기·미선택·채점을 각각 다른 경우로 처리합니다.
      grade.addEventListener("click", () => {
        const record = answers[question.id];
        if (record?.graded) {
          save(question.id, null);
          refresh();
          choices[0].input.focus();
          return;
        }
        if (!record) {
          feedback.replaceChildren(element("p", "", "먼저 보기 하나를 선택해 주세요."));
          choices[0].input.focus();
          return;
        }
        save(question.id, { ...record, graded: true });
        refresh();
      });
      card.append(heading);
      if (!kind.identify) card.append(evidence);
      card.append(group, actions, feedback);
      refresh();
      return card;
    }

    // 현재 페이지에 해당하는 부분만 잘라 카드로 보여 줍니다. Math.ceil은 남은 일부 문제도 한 페이지로 셉니다.
    function render(focus = false) {
      const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
      page = Math.max(0, Math.min(page, totalPages - 1));
      const start = page * PAGE_SIZE;
      const shown = filtered.slice(start, start + PAGE_SIZE);
      const list = byId("question-list");
      list.replaceChildren(...shown.map(makeCard));
      if (!shown.length) list.append(element("p", "empty", "조건에 맞는 문제가 없습니다. 사용 범위·기술 단원·유형·풀이 상태·검색어를 바꾸고 필터를 적용해 보세요."));
      byId("result-summary").textContent = shown.length
        ? `${filtered.length.toLocaleString("ko-KR")}문항 중 ${start + 1}–${start + shown.length}번째 표시`
        : "조건에 맞는 문제 0문항";
      for (const side of ["top", "bottom"]) {
        byId(`page-status-${side}`).textContent = `${page + 1} / ${totalPages} 페이지`;
        byId(`previous-${side}`).disabled = page === 0;
        byId(`next-${side}`).disabled = page >= totalPages - 1;
      }
      if (focus) byId("questions-heading").focus();
    }

    // 화면에서 고른 검색 조건으로 문제를 다시 고르고 첫 페이지로 돌아갑니다.
    function applyFilters(focus = true) {
      filtered = core.filterQuestions(questions, {
        chapter: byId("chapter-filter").value,
        scope: byId("scope-filter").value,
        kind: byId("kind-filter").value,
        mode: byId("mode-filter").value,
        query: byId("search-filter").value,
      }, answers);
      page = 0;
      render(focus);
    }

    for (const chapter of chapters) {
      const count = questions.filter(question => question.chapter === chapter.id).length;
      const option = element("option", "", `${chapter.id}. ${chapter.title} · ${count}문항`);
      option.value = chapter.id;
      byId("chapter-filter").append(option);
    }
    for (const kind of core.kinds) {
      const option = element("option", "", kind.label);
      option.value = kind.id;
      byId("kind-filter").append(option);
    }
    const requestedChapter = new URLSearchParams(location.search).get("chapter");
    if (chapters.some(chapter => chapter.id === requestedChapter)) byId("chapter-filter").value = requestedChapter;
    // preventDefault는 폼 제출로 페이지가 새로 열리는 것을 막습니다. 지금 화면에서 검색 결과만 바꿉니다.
    byId("filters").addEventListener("submit", event => { event.preventDefault(); applyFilters(); });
    for (const side of ["top", "bottom"]) {
      byId(`previous-${side}`).addEventListener("click", () => { page -= 1; render(true); });
      byId(`next-${side}`).addEventListener("click", () => { page += 1; render(true); });
    }
    // 복원·채점·삭제를 다른 화면에서 했어도 새 기록을 보여 주며, 이 화면의 미저장 변경은 유지합니다.
    window.addEventListener("storage", event => {
      if (event.key !== core.storageKey && event.key !== null) return;
      if (readAnswers()) {
        updateStatistics();
        render();
      }
    });
    byId("apply-filters").disabled = false;
    updateStatistics();
    applyFilters(false);
  } catch (error) {
    byId("load-error").hidden = false;
    byId("result-summary").textContent = "문제 준비에 실패했습니다.";
    console.error("기본기 문제집 로드 실패", error);
  }
})();
