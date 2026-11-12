// 이 파일은 문제 카드 조립 담당입니다. 질문, 보기 네 개, 버튼, 해설 칸을 화면에 붙입니다.
// 읽는 순서: mount로 카드 만들기 → 보기 선택 → 채점 버튼 → refresh로 결과 표시.
(function () {
  "use strict";
  function element(tag, className, text) {
    // DOM은 브라우저가 관리하는 화면 부품 목록입니다. createElement는 새 부품을 만듭니다.
    // textContent는 글자를 그대로 보여 줍니다. 글 속에 HTML이 있어도 실행하지 않습니다.
    const node = document.createElement(tag);
    if (className) node.className = className;
    if (text !== undefined) node.textContent = text;
    return node;
  }
  const letters = ["A", "B", "C", "D"];
  function mount(container, questions, { getAnswers, onChange, headingLevel = 5, showNumber = true, gradeOnSelect = () => false }) {
    // container는 카드를 붙일 자리입니다. 챕터가 바뀌면 이전 카드를 지우고 새로 만듭니다.
    // getAnswers는 현재 답안을 읽는 함수, onChange는 바뀐 답안을 저장하도록 알리는 함수입니다.
    container.replaceChildren();
    questions.forEach(question => {
      const card = element("article", "chapter-choice-card");
      card.dataset.questionId = question.id;
      const prefix = showNumber ? `${question.chapter}-${String(question.number).padStart(2, "0")}. ` : "";
      const heading = element(`h${headingLevel}`, "chapter-choice-heading", prefix + question.prompt);
      heading.id = `${container.id}-${question.id}-heading`;
      const fieldset = element("fieldset", "chapter-choices");
      const legend = element("legend", "sr-only", "보기 네 개 중 하나를 선택하세요.");
      fieldset.setAttribute("aria-labelledby", heading.id);
      // aria-labelledby는 화면을 음성으로 읽는 도구에 이 보기 묶음의 질문 제목을 알려 줍니다.
      fieldset.append(legend);
      const feedback = element("div", "chapter-choice-feedback");
      feedback.setAttribute("role", "status");
      const button = element("button", "chapter-grade", "채점하고 해설 보기");
      button.type = "button";
      const choices = question.options.map((option, i) => {
        const label = element("label", "chapter-choice");
        const input = element("input");
        input.type = "radio";
        // 같은 name을 가진 라디오 버튼은 한 묶음입니다. 네 보기 중 하나만 선택할 수 있습니다.
        input.name = `${container.id}-${question.id}`;
        input.value = String(i);
        input.setAttribute("aria-label", `${letters[i]}. ${option}`);
        const marker = element("span", "choice-marker", letters[i]);
        marker.setAttribute("aria-hidden", "true");
        const status = element("span", "choice-status");
        label.append(input, marker, element("span", "choice-text", option), status);
        fieldset.append(label);
        input.addEventListener("change", () => {
          // 이벤트는 클릭이나 입력 같은 사용자 행동입니다. 보기가 바뀌면 이 함수가 실행됩니다.
          // selected는 고른 자리 번호, graded는 채점 여부입니다. 즉시 채점 모드를 켜면 선택할 때 채점합니다.
          onChange(question.id, { selected: i, graded: gradeOnSelect() });
          refresh();
        });
        return { input, label, marker, status };
      });
      function refresh() {
        // 저장 기록을 읽어 체크 표시·보기 색·버튼 글씨를 다시 맞춥니다.
        const record = getAnswers()[question.id];
        const graded = record?.graded === true;
        choices.forEach(({ input, label, marker, status }, i) => {
          input.checked = record?.selected === i;
          input.disabled = graded;
          // 채점 후에는 답을 바꿀 수 없게 합니다. 다시 풀기를 누르면 선택할 수 있게 돌아갑니다.
          label.classList.toggle("is-correct", graded && i === question.answer);
          label.classList.toggle("is-wrong", graded && i === record.selected && i !== question.answer);
          label.classList.toggle("is-selected", !graded && record?.selected === i);
          marker.textContent = graded && i === question.answer ? "✓" : graded && i === record.selected ? "×" : letters[i];
          status.textContent = graded && i === question.answer ? "정답" : graded && i === record.selected ? "내 선택 · 오답" : "";
        });
        card.classList.toggle("is-graded", graded);
        button.textContent = graded ? "이 문제 다시 풀기" : "채점하고 해설 보기";
        feedback.replaceChildren();
        if (graded) {
          const correct = record.selected === question.answer;
          feedback.append(element("p", correct ? "choice-verdict" : "choice-verdict is-wrong", `${correct ? "정답입니다!" : "다시 확인해 보세요."} 내 선택 ${letters[record.selected]} · 정답 ${letters[question.answer]}`));
          const details = element("details", "choice-explanation");
          details.open = true;
          const summary = element("summary", "", "해설 보기");
          const body = element("div", "choice-explanation-body");
          body.append(element("strong", "", `정답 ${letters[question.answer]} · ${question.options[question.answer]}`), element("p", "", question.explanation));
          if (question.optionExplanations) {
            const comparison = element("details", "choice-comparison");
            comparison.append(element("summary", "", "보기별로 이해하기"));
            const list = element("ul");
            question.optionExplanations.forEach((explanation, i) => {
              const item = element("li");
              item.append(element("strong", "", `${letters[i]}${i === question.answer ? " · 정답" : ""}`), document.createTextNode(` ${explanation}`));
              list.append(item);
            });
            comparison.append(list);
            body.append(comparison);
          }
          details.append(summary, body);
          feedback.append(details);
        }
      }
      button.addEventListener("click", () => {
        const record = getAnswers()[question.id];
        if (record?.graded) {
          // null을 전달하면 이 문제의 기록만 지웁니다. 다른 문제의 답안은 그대로 남습니다.
          onChange(question.id, null);
          refresh();
          choices[0].input.focus();
        } else if (!record) {
          // 답을 고르지 않았다면 채점하지 않고 첫 보기로 키보드 이동 위치를 옮깁니다.
          feedback.replaceChildren(element("p", "", "먼저 보기 하나를 선택해 주세요."));
          choices[0].input.focus();
        } else {
          // 선택 기록에 graded: true를 더한 다음, 정답과 해설을 보여 줍니다.
          onChange(question.id, { ...record, graded: true });
          refresh();
        }
      });
      card.append(heading, fieldset, button, feedback);
      container.append(card);
      refresh();
    });
  }
  globalThis.ChapterQuizUI = { mount };
})();
