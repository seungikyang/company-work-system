// 중학생 눈높이: 문제 원본으로 여러 Markdown 학습 문서를 만드는 복사 담당입니다.
// 질문을 한곳에서 고친 뒤 이 도구로 갱신하면 화면과 문서의 보기·정답·해설이 서로 맞습니다.
// 문제의 단일 원본(JS)에서 Markdown 문제·정답 자료를 갱신합니다.
const fs = require("node:fs");
const path = require("node:path");
const root = path.resolve(__dirname, "..");
if (fs.realpathSync(process.cwd()) !== fs.realpathSync(root)) throw new Error("저장소 루트에서 실행하세요.");
// 코드·설정 파일에 남은 질문도 같은 채점 원본에 붙입니다. 기존 168문항은 그대로 유지합니다.
const codeRows = fs.readFileSync(path.join(root, "practice/code-question-data.txt"), "utf8").trim().split("\n");
const codeMetadata = JSON.parse(fs.readFileSync(path.join(root, "practice/code-question-metadata.json"), "utf8"));
const dataFile = path.join(root, "practice/chapter-quiz-data.js");
const marker = "// 코드·설정 서술형 질문의 4지선다 변환 (자동 생성)";
const originalData = fs.readFileSync(dataFile, "utf8").split(marker)[0].trimEnd();
const addedRows = codeRows.map(row => {
  const [key, ...fields] = row.split("|");
  if (fields.length !== 6 || fields.some(field => !field.trim())) throw new Error(`코드 문항 형식 오류: ${key}`);
  return `${key.slice(0, 2)}|${fields.join("|")}`;
});
write(dataFile, `${originalData}\n\n${marker}\nglobalThis.CHAPTER_QUIZ_ROWS = globalThis.CHAPTER_QUIZ_ROWS.trimEnd() + "\\n" + [\n${addedRows.map(row => "  " + JSON.stringify(row)).join(",\n")}\n].join("\\n");\n`);
require("../practice/chapter-quiz-data.js");
require("../practice/chapter-quiz.js");
const codeQuestions = new Map(codeRows.map((row, index) => [row.split("|", 1)[0], ChapterQuiz.questions[168 + index]]));
// metadata는 챕터 제목·설명·실습 파일 같은 문제 주변의 안내 정보입니다.
const metadata = JSON.parse(fs.readFileSync(path.join(root, "practice/chapter-quiz-metadata.json"), "utf8"));
const letters = ["①", "②", "③", "④"];
// 질문 목록을 문서 글자로 바꿉니다. map은 각 질문을 변환하고 join은 변환한 글을 이어 붙입니다.
function renderQuestions(questions) {
  return questions.map(question => `### Q${question.chapter}-${String(question.number).padStart(2, "0")}. ${question.prompt}\n\n${question.options.map((option, i) => `- ${letters[i]} ${option}`).join("\n")}\n\n**정답:** ${letters[question.answer]}\n\n**해설:** ${question.explanation}\n`).join("\n");
}
// 시작·끝 표시 사이의 질문만 바꿔 코드·TODO·자가 채점·심화 노트를 보존합니다.
for (const entry of codeMetadata) {
  const target = path.join(root, entry.file);
  const original = fs.readFileSync(target, "utf8");
  const startMarker = `${entry.indent}${entry.comment} 4지선다 학습 문제 (자동 생성 시작)`;
  const endMarker = `${entry.indent}${entry.comment} 4지선다 학습 문제 (자동 생성 끝)`;
  let begin = original.indexOf(startMarker);
  let finish;
  if (begin >= 0) {
    const end = original.indexOf(endMarker, begin);
    if (end < 0) throw new Error(`코드 문항 끝 표시 누락: ${entry.file}`);
    finish = end + endMarker.length;
  } else {
    const heading = original.match(/^[ \t]*(?:\/\/|#)[^\n]*학습 질문[^\n]*\n/m);
    if (!heading) throw new Error(`학습 질문 구획 누락: ${entry.file}`);
    begin = heading.index;
    const answers = [...original.slice(begin).matchAll(/^[ \t]*(?:\/\/|#)[ \t]*A\d*:[ \t]*$/gm)];
    if (!answers.length) throw new Error(`학습 답안 구획 누락: ${entry.file}`);
    finish = begin + answers.at(-1).index + answers.at(-1)[0].length;
  }
  const lines = [startMarker, `선택·채점 화면 (저장소 루트 기준): problems.html#problem-${entry.keys[0].slice(0, 2)}`, ""];
  entry.keys.forEach((key, index) => {
    const question = codeQuestions.get(key);
    if (!question) throw new Error(`코드 문항 대응 누락: ${key}`);
    lines.push(`Q${index + 1}. ${question.prompt} [${question.id}]`,
      ...question.options.map((option, i) => `${letters[i]} ${option}`),
      `정답: ${letters[question.answer]}`, `해설: ${question.explanation}`, "");
  });
  const commentLines = lines.map((line, index) => index === 0 ? line : `${entry.indent}${entry.comment}${line ? " " + line : ""}`);
  commentLines.push(endMarker);
  write(target, original.slice(0, begin) + commentLines.join("\n") + original.slice(finish));
}
let document = `# 코드 연결 4지선다 문제집\n\n40개 챕터의 기존 학습 항목 148개·PRD 면접 질문 12개·공통 검증 질문 8개에 코드·설정 학습 질문을 변환한 ${codeRows.length}문항을 추가했습니다. Starter·Hard 70개 파일의 서술형 질문 230개도 같은 보기·정답·해설로 제공합니다. 총 ${ChapterQuiz.questions.length}문항을 [메인 워크북](../index.html) 또는 [객관식 문제 화면](../problems.html)에서 선택·채점·재풀이할 수 있습니다.\n\n## 사용 방법\n\n1. 보기 네 개 중 가장 적절한 답 하나를 선택합니다.\n2. 화면에서는 채점 버튼을, 이 문서에서는 정답·해설을 확인합니다.\n3. 연결된 Starter·Hard·문서와 실제 코드·테스트를 함께 확인합니다.\n4. 목표·FR ID·검증 명령과 증거는 [통합 워크북](./job-preparation-workbook.md)에 기록합니다.\n\n`;
// 코드 대신 문서로 학습하는 챕터 번호입니다. 이 문서들도 같은 문제 원본을 사용합니다.
const worksheetChapters = new Set([20, 22, 35, 36, 37, 38, 39]);
for (const chapter of metadata) {
  const id = String(chapter.number).padStart(2, "0");
  const questions = ChapterQuiz.forChapter(id);
  document += `## ${chapter.heading}\n\n${chapter.intro}\n\n${chapter.files.join("\n")}\n\n**4지선다 문제**\n\n${renderQuestions(questions)}\n`;
  if (!worksheetChapters.has(chapter.number)) continue;
  const sourceMatch = chapter.files.join(" ").match(/starter\/[^`\s]+\.md/);
  const file = path.join(root, "practice", sourceMatch[0]);
  const original = fs.readFileSync(file, "utf8");
  // 문제 본문만 갱신하고 기존 기술 해설·코드 예제·실제 결과물 계약은 보존합니다.
  const prefix = original.split("\n---", 1)[0].replace(/\n<!-- 객관식 시작 -->[\s\S]*$/, "");
  const notes = original.match(/^## (?:\d+\. )?심화 노트[^\n]*\n[\s\S]*/m)?.[0] || "";
  const title = prefix.split("\n")[0];
  const contract = prefix.slice(prefix.indexOf("## 챕터 계약")).trim();
  let text = `${title}\n\n이 챕터의 개념과 설계 판단을 4지선다로 확인합니다. [선택·채점 화면](../../../problems.html#problem-${id})에서 풀고 실제 구현·검증 결과를 [통합 워크북](../../job-preparation-workbook.md)에 남깁니다.\n\n${contract}\n\n<!-- 객관식 시작 -->\n## 4지선다 문제\n\n${renderQuestions(questions)}\n`;
  if (chapter.number === 35) {
    text += '## 렌더링 코드 참고\n\n```java\nreturn "employee/list";\n```\n\n```html\n<p th:text="${employee.name}"></p>\n<tr th:each="emp : ${employees}"></tr>\n<input th:field="*{name}">\n<header th:replace="~{fragments/header :: header}"></header>\n```\n\n';
  }
  text += notes;
  write(file, text.trim() + "\n");
}
write(path.join(root, "practice/problems.md"), document.trim() + "\n");
// 기존 문서 전체를 덮어쓰지 않고 시작·끝 제목 사이의 해당 구획만 갱신합니다.
function section(file, start, end, text) {
  const target = path.join(root, file);
  const original = fs.readFileSync(target, "utf8");
  const begin = original.indexOf(start);
  const finish = end ? original.indexOf(end, begin) : original.length;
  if (begin < 0 || finish < begin) throw new Error(`문서 구획 누락: ${file}`);
  write(target, original.slice(0, begin) + text.trim() + (end ? "\n\n" : "\n") + original.slice(finish));
}
section("company_work_system_PRD_TRD.md", "## 3.19 면접 대비 질문", "## 3.20", `## 3.19 면접 대비 질문 · 4지선다\n\n기존 면접 질문 12개를 객관식으로 확인합니다. [선택·채점 화면](./problems.html#problem-39)에서 풀고 구현 근거를 연결합니다.\n\n${renderQuestions(ChapterQuiz.forChapter("39").slice(4))}\n---`);
section("practice/feature-implementation-workbook.md", "# 7. 기능별 공통 테스트 체크리스트", "## 마무리 구현 증거 기록", `# 7. 기능별 공통 테스트 체크리스트 · 4지선다\n\n성공·입력 검증·역할·소유권·상태·트랜잭션·DTO·동시성의 검증 기준을 객관식으로 확인합니다. [21장 선택·채점 화면](../problems.html#problem-21)과 실제 기능별 테스트를 함께 사용합니다.\n\n${renderQuestions(ChapterQuiz.forChapter("21").slice(4, 12))}`);
section("practice/README.md", "## 개념 자가 점검", null, `## 개념 자가 점검 · 4지선다\n\n다음 객관식 문항으로 개념을 확인합니다. [선택·채점 화면](../problems.html)에서 풀 수 있습니다.\n\n${renderQuestions(["08-01", "37-01", "32-01", "01-03"].map(id => ChapterQuiz.questions.find(q => q.id === "chapter-v1-" + id)))}\n객관식 이해 확인 뒤에는 실행 프로젝트에 구현하고 실제 검증 명령·테스트 결과·커밋 근거를 남깁니다.`);
// --check가 있으면 파일을 고치지 않고 생성 결과와 같은지 비교합니다. 없으면 새 내용으로 저장합니다.
function write(file, text) {
  if (process.argv.includes("--check")) {
    if (fs.readFileSync(file, "utf8") !== text) throw new Error(`생성 자료 불일치: ${path.relative(root, file)}`);
  } else fs.writeFileSync(file, text);
}
console.log(`객관식 자료 ${ChapterQuiz.questions.length}문항 ${process.argv.includes("--check") ? "일치 확인" : "갱신"}`);
