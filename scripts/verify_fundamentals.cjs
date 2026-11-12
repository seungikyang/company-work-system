// 중학생 눈높이: 이 파일은 자동 검사입니다. 2,000문항의 개수·근거 파일·채점·검색 규칙을 확인합니다.
// assert는 예상과 실제가 맞는지 확인하는 도구입니다. 틀리면 그 지점에서 검사 실패를 알려 줍니다.
// 실행: 저장소 루트에서 node scripts/verify_fundamentals.cjs
const assert = require("node:assert/strict");
const fs = require("node:fs");
const crypto = require("node:crypto");
const path = require("node:path");
const { spawnSync } = require("node:child_process");
const root = path.resolve(__dirname, "..");
assert.equal(fs.realpathSync(process.cwd()), fs.realpathSync(root), "저장소 루트에서 실행하세요.");
const quizDirectory = path.join(root, "practice/quiz");
for (const name of fs.readdirSync(quizDirectory).filter(name => name.endsWith(".js"))) {
  const result = spawnSync(process.execPath, ["--check", path.join(quizDirectory, name)], { encoding: "utf8" });
  assert.equal(result.status, 0, `${name}: ${result.stderr}`);
}
for (const range of ["00-09", "10-19", "20-29", "30-39"]) require(path.join(quizDirectory, `content-${range}.js`));
require(path.join(quizDirectory, "quiz-core.js"));
const core = globalThis.Fundamentals;
const bank = core.buildBank(globalThis.FUNDAMENTALS_SOURCES);
const { chapters, questions } = bank;
assert.equal(core.version, 2);
assert.notEqual(core.storageKey, "company-work-system.fundamentals.v1", "기존 문항의 풀이 저장소를 재사용하면 안 됨");
assert.equal(chapters.length, 40);
assert.equal(questions.length, 2000);
assert.equal(new Set(questions.map(q => q.id)).size, 2000, "문항 ID 중복");
assert.equal(new Set(questions.map(q => q.prompt)).size, 2000, "문항 지문 중복");
// 확장 전 문제의 모든 필드가 동일해야 기존 선택 번호와 채점 기록이 유효합니다.
assert.equal(crypto.createHash("sha256").update(JSON.stringify(questions.slice(0, 1000))).digest("hex"),
  "d38901d2103fa704c75c1e4630a80f6e799b13db4a4ff070f759f2cc9a665da3",
  "기존 1,000문항의 ID·번호·보기·정답·해설이 변경됨");
assert.equal(core.kinds.length, 10);
assert.ok(questions.slice(1000).every(q => q.kind.startsWith("identify-")));
assert.equal(new Set(chapters.flatMap(chapter => chapter.topics.map(topic => topic.name))).size, 200, "학습 항목 중복");
assert.deepEqual(core.buildBank(globalThis.FUNDAMENTALS_SOURCES), bank, "새로고침 시 보기 순서가 바뀜");
const distribution = [0, 0, 0, 0];
for (const chapter of chapters) {
  const subset = questions.filter(q => q.chapter === chapter.id);
  assert.equal(subset.length, 50);
  assert.ok(fs.existsSync(path.join(root, chapter.source)), `없는 학습 자료: ${chapter.source}`);
  assert.ok(fs.existsSync(path.join(root, chapter.reading)), `없는 읽기 자료: ${chapter.reading}`);
  assert.ok(chapter.reading.startsWith("practice/"), "읽기 화면은 공개 학습 자료로만 연결");
  assert.ok(!chapter.source.includes("..") && !path.isAbsolute(chapter.source));
  const source = fs.readFileSync(path.join(root, chapter.source), "utf8");
  for (const topic of chapter.topics) {
    assert.ok(source.includes(topic.evidence), `${chapter.id} ${topic.name}: 근거 표현이 파일에 없음: ${topic.evidence}`);
  }
  if (chapter.scope === "workbook") {
    assert.ok(["index.html", "problems.html", "workbook-viewer.html", "scripts/serve_workbook.py"].includes(chapter.source));
  } else {
    assert.ok(chapter.source.startsWith("practice/starter/"));
  }
  for (const kind of core.kinds) assert.equal(subset.filter(q => q.kind === kind.id).length, 5);
}
questions.forEach((q, i) => {
  assert.equal(q.number, i + 1);
  assert.equal(q.options.length, 4);
  assert.equal(new Set(q.options.map(option => option.text)).size, 4);
  assert.ok(q.answer >= 0 && q.answer < 4);
  assert.equal(q.options.filter(option => option.topic === q.topic).length, 1);
  assert.equal(q.options[q.answer].topic, q.topic);
  const chapter = chapters[Number(q.chapter)];
  assert.equal(q.scope, chapter.scope);
  assert.equal(q.technology, chapter.technology);
  assert.equal(q.source, chapter.source);
  assert.equal(q.reading, chapter.reading);
  const kind = core.kinds.find(kind => kind.id === q.kind);
  const facetIndex = kind.facetIndex ?? core.kinds.indexOf(kind);
  const topic = chapter.topics.find(topic => topic.name === q.topic);
  assert.equal(q.evidence, topic.evidence);
  assert.equal(q.options[q.answer].text, kind.identify ? topic.name : topic.facets[facetIndex]);
  for (const option of q.options) {
    assert.equal(option.text, kind.identify ? option.topic : chapter.topics.find(topic => topic.name === option.topic).facets[facetIndex]);
  }
  assert.equal(q.prompt, kind.identify ? `${kind.prompt}\n「${topic.facets[facetIndex]}」` : `「${topic.name}」${kind.prompt}`);
  assert.ok(q.explanation.includes(topic.facets[0]) && q.explanation.includes(topic.facets[2]));
  distribution[q.answer] += 1;
});
assert.deepEqual(distribution, [500, 500, 500, 500], "정답 위치 분포 불균형");
assert.equal(questions.filter(q => q.scope === "workbook").length, 600);
assert.equal(questions.filter(q => q.scope === "backend").length, 1350);
assert.equal(questions.filter(q => q.scope === "extension").length, 50);
assert.equal(chapters.find(chapter => chapter.technology === "Spring Security").scope, "extension");
assert.ok(questions.every(q => !/30초|포트폴리오|면접|Pull Request/.test(q.topic)), "기술 외 주제가 문항에 섞임");

const [first, second, third] = questions;
const records = {
  [first.id]: { selected: first.answer, graded: true },
  [second.id]: { selected: (second.answer + 1) % 4, graded: true },
  [third.id]: { selected: third.answer, graded: false },
};
assert.deepEqual(core.statistics(questions, records), { total: 2000, answered: 2, correct: 1, wrong: 1, remaining: 1998 });
assert.deepEqual(core.filterQuestions(questions, { mode: "wrong" }, records), [second]);
assert.deepEqual(core.filterQuestions(questions, { mode: "correct" }, records), [first]);
assert.equal(core.filterQuestions(questions, { mode: "unanswered" }, records).length, 1998);
assert.equal(core.filterQuestions(questions, { chapter: "39" }, records).length, 50);
assert.equal(core.filterQuestions(questions, { chapter: "39", kind: "verification" }, records).length, 5);
assert.equal(core.filterQuestions(questions, { chapter: "39", mode: "wrong" }, records).length, 0);
assert.equal(core.filterQuestions(questions, { scope: "workbook" }, records).length, 600);
assert.equal(core.filterQuestions(questions, { scope: "backend" }, records).length, 1400);
assert.equal(core.filterQuestions(questions, { scope: "workbook", chapter: "39" }, records).length, 0);
assert.equal(core.filterQuestions(questions, { scope: "backend", chapter: "35" }, records).length, 50);
assert.equal(core.filterQuestions(questions, { query: "  PYTHON  " }, records).length, 50);
assert.equal(core.filterQuestions(questions, { query: "scripts/serve_workbook.py" }, records).length, 50);
assert.equal(core.filterQuestions(questions, { query: "  2000  " }, records)[0].number, 2000);
assert.equal(core.filterQuestions(questions, { query: "없는개념xyz" }, records).length, 0);
assert.deepEqual(core.sanitizeAnswers(JSON.parse(JSON.stringify(records)), questions), records);
const expandedRecords = { ...records, [questions[1000].id]: { selected: questions[1000].answer, graded: true } };
assert.deepEqual(core.sanitizeAnswers(expandedRecords, questions), expandedRecords);
assert.deepEqual(core.statistics(questions, expandedRecords), { total: 2000, answered: 3, correct: 2, wrong: 1, remaining: 1997 });
assert.equal(core.filterQuestions(questions, { kind: "identify-concept" }, {}).length, 200);
assert.equal(core.filterQuestions(questions, { chapter: "39", kind: "identify-verification" }, {}).length, 5);

for (const invalid of [null, [], "broken", 123]) assert.deepEqual(core.sanitizeAnswers(invalid, questions), {});
for (const invalid of [null, { selected: -1, graded: true }, { selected: 4, graded: true }, { selected: "1", graded: true }, { selected: 0, graded: "yes" }]) {
  assert.deepEqual(core.sanitizeAnswers({ [first.id]: invalid }, questions), {});
}
assert.deepEqual(core.sanitizeAnswers(Object.create(records), questions), {}, "상속된 저장 기록을 읽으면 안 됨");
assert.deepEqual(core.sanitizeAnswers({ "basic-00-1-concept": { selected: 0, graded: true } }, questions), {}, "기존 문항 점수를 개정판으로 가져오면 안 됨");
assert.deepEqual(core.sanitizeAnswers({ ...records, unknown: { selected: 0, graded: true } }, questions), records);
assert.throws(() => core.buildBank([]));
const broken = [...globalThis.FUNDAMENTALS_SOURCES];
broken[0] = broken[0].replace("## 00|", "## 99|");
assert.throws(() => core.buildBank(broken));

const html = fs.readFileSync(path.join(root, "fundamentals.html"), "utf8");
for (const match of html.matchAll(/<script>([\s\S]*?)<\/script>/g)) {
  const result = spawnSync(process.execPath, ["--check", "-"], { input: match[1], encoding: "utf8" });
  assert.equal(result.status, 0, `로드 실패 안내 스크립트: ${result.stderr}`);
}
assert.ok(html.includes('max="2000"'));
assert.ok(html.includes('채점 완료 / 2,000'));
assert.ok(html.includes('워크북 실제 구현 · 600문항'));
assert.ok(html.includes('백엔드 실습·확장 · 1,400문항'));
const pageScript = fs.readFileSync(path.join(quizDirectory, "quiz-page.js"), "utf8");
const ids = new Set([...html.matchAll(/\bid="([^"]+)"/g)].map(match => match[1]));
for (const match of pageScript.matchAll(/byId\("([^"]+)"\)/g)) assert.ok(ids.has(match[1]), `없는 DOM ID ${match[1]}`);
for (const side of ["top", "bottom"]) for (const prefix of ["previous", "next", "page-status"]) assert.ok(ids.has(`${prefix}-${side}`));
for (const name of ["answered", "correct", "wrong", "remaining"]) assert.ok(ids.has(`${name}-count`));
assert.ok(!pageScript.includes("innerHTML"), "학습 문자열은 textContent로 표시해야 함");
const scripts = [...html.matchAll(/<script defer src="\.\/([^\"]+)"/g)].map(match => match[1]);
assert.deepEqual(scripts, [
  "practice/quiz/content-00-09.js", "practice/quiz/content-10-19.js",
  "practice/quiz/content-20-29.js", "practice/quiz/content-30-39.js",
  "practice/quiz/quiz-core.js", "practice/quiz/quiz-page.js",
]);
console.log("기술 기본기 2,000제 검증 통과: 40단원 × 50문항, 워크북 기술 600문항·백엔드 실습/확장 1,400문항");
console.log("200개 기술 항목의 파일·근거 표현 존재, 보기·정답·지문 중복·채점·필터·개정판 저장 분리·DOM 연결 검사 통과");
