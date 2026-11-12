// 실제 문항으로 범위 제한·검색·주소 복원·오답 재풀이의 경계 조건을 확인합니다.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const root = path.resolve(__dirname, "..");
assert.equal(fs.realpathSync(process.cwd()), fs.realpathSync(root), "저장소 루트에서 실행해야 합니다.");
const context = vm.createContext({});
for (const file of ["practice/quiz/focus-page.js", "practice/chapter-quiz-ui.js"]) {
  new vm.Script(fs.readFileSync(path.join(root, file), "utf8"), { filename: file });
}
for (const file of ["practice/chapter-quiz-data.js", "practice/chapter-quiz.js", "practice/quiz/focus-session.js", "practice/quiz/document-quiz.js"]) {
  vm.runInContext(fs.readFileSync(path.join(root, file), "utf8"), context, { filename: file });
}
const questions = context.ChapterQuiz.questions;
const select = context.FocusSession.select;
const all = { chapter: "", mode: "all", query: "", limit: "all" };
assert.equal(select(questions, {}, all).questions.length, 289);
assert.equal(select(questions, {}, { ...all, limit: "10" }).questions.length, 10);
assert.equal(select(questions, {}, { ...all, limit: "20" }).questions.length, 20);
assert.equal(select(questions, {}, { ...all, limit: "50" }).questions.length, 50);
assert.equal(select(questions, {}, { ...all, limit: "-1" }).questions.length, 289);

const answers = Object.fromEntries(questions.slice(0, 10).map(q => [q.id, { selected: q.answer, graded: true }]));
const next = select(questions, answers, { ...all, limit: "10" });
assert.equal(next.questions[0].id, questions[10].id, "완료한 첫 묶음 뒤에서 학습을 이어갑니다.");
const resumed = select(questions, answers, { ...all, limit: "10" }, questions[8].id, questions[0].id);
assert.equal(resumed.questions[0].id, questions[0].id, "새로고침 후에도 학습 묶음의 시작이 유지됩니다.");
assert.equal(resumed.questions[8].id, questions[8].id);
const outside = select(questions, {}, { ...all, limit: "10" }, questions[15].id, questions[0].id);
assert.equal(outside.questions[0].id, questions[15].id, "범위 밖 주소도 요청한 문항에서 시작합니다.");
assert.equal(select(questions, {}, { ...all, limit: "10" }, questions.at(-1).id).questions.length, 1);

const wrong = questions[2];
answers[wrong.id] = { selected: (wrong.answer + 1) % 4, graded: true };
assert.equal(select(questions, answers, { ...all, mode: "wrong" }).questions[0].id, wrong.id);
delete answers[wrong.id];
assert.equal(select(questions, answers, { ...all, mode: "wrong" }, wrong.id).questions[0].id, wrong.id, "오답을 다시 풀다가 새로고침해도 현재 문항을 유지합니다.");
assert.equal(select(questions, answers, { ...all, mode: "wrong" }).questions.length, 0, "필터를 새로 적용하면 최신 오답만 남습니다.");
assert.equal(select(questions, answers, { ...all, mode: "correct" }).questions.length, 9);
assert.equal(select(questions, answers, { ...all, mode: "unanswered" }).questions.length, 280);
assert.ok(select(questions, {}, { ...all, chapter: "01" }).questions.every(q => q.chapter === "01"));
assert.equal(select(questions, {}, { ...all, chapter: "01" }, questions[0].id).questions.some(q => q.id === questions[0].id), false, "주소가 다른 챕터 제한을 우회하지 않습니다.");
assert.equal(select(questions, {}, { ...all, query: "없는검색어_2026" }).questions.length, 0);
assert.ok(select(questions, {}, { ...all, query: "spring" }).questions.length > 0);
const fileQuestions = [questions[0].id, questions[2].id, questions[15].id];
assert.equal(select(questions, {}, { ...all, ids: fileQuestions }).questions.length, 3);
assert.equal(select(questions, {}, { ...all, ids: fileQuestions }, questions[1].id).questions.some(q => q.id === questions[1].id), false, "현재 문항 주소도 파일의 문제 범위를 우회하지 않습니다.");
const metadata = JSON.parse(fs.readFileSync(path.join(root, "practice/code-question-metadata.json"), "utf8"));
for (const item of metadata) {
  const original = fs.readFileSync(path.join(root, item.file), "utf8");
  const extracted = context.DocumentQuiz.extract(original);
  assert.equal(extracted.ids.length, item.keys.length, `${item.file}: 파일에 실린 문항 수`);
  assert.ok(!extracted.source.includes("4지선다 학습 문제 (자동 생성 시작)"), `${item.file}: 문서에서 정답 주석 노출 방지`);
  assert.ok(extracted.ids.every(id => questions.some(q => q.id === id)), `${item.file}: 실제 문제 은행과 연결`);
  const subset = select(questions, {}, { ...all, ids: extracted.ids });
  assert.equal(subset.questions.length, extracted.ids.length, `${item.file}: 파일에 실린 문항만 웹 풀이`);
}
const plain = "// 기술 설명\nclass User {}\n";
assert.equal(context.DocumentQuiz.extract(plain).source, plain, "문항이 없는 코드의 본문 보존");
const invalidBlock = "// 4지선다 학습 문제 (자동 생성 시작)\n// 문항 ID가 없는 설명\n// 4지선다 학습 문제 (자동 생성 끝)";
assert.equal(context.DocumentQuiz.extract(invalidBlock).source, invalidBlock, "연결할 문항이 없으면 본문 보존");
console.log(`집중 퀴즈 검증 통과: 289문항·10/20/50문제·학습 이어가기·주소 복원·오답 재풀이·문서 ${metadata.length}개의 실제 문항 연결`);
