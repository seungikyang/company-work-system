// 중학생 눈높이: 이 파일은 자동 검사입니다. 289문항의 보기·정답·저장·문서 일치를 확인합니다.
// assert는 예상과 실제가 맞는지 확인하는 도구입니다. 틀리면 그 지점에서 검사 실패를 알려 줍니다.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const crypto = require("node:crypto");
const { spawnSync } = require("node:child_process");
const root = path.resolve(__dirname, "..");
assert.equal(fs.realpathSync(process.cwd()), fs.realpathSync(root));
require("../practice/chapter-quiz-data.js");
require("../practice/chapter-quiz.js");
require("../practice/study-records.js");
const core = ChapterQuiz;
const metadata = JSON.parse(fs.readFileSync(path.join(root, "practice/chapter-quiz-metadata.json"), "utf8"));
const codeMetadata = JSON.parse(fs.readFileSync(path.join(root, "practice/code-question-metadata.json"), "utf8"));
const codeRows = fs.readFileSync(path.join(root, "practice/code-question-data.txt"), "utf8").trim().split("\n");
const codeKeys = codeRows.map(row => row.split("|", 1)[0]);
assert.equal(codeRows.length, 121);
assert.equal(new Set(codeKeys).size, 121);
assert.equal(codeMetadata.length, 70);
assert.equal(codeMetadata.reduce((sum, entry) => sum + entry.originals.length, 0), 230);
assert.deepEqual(new Set(codeMetadata.flatMap(entry => entry.keys)), new Set(codeKeys), "코드 질문 전수 대응");
assert.equal(metadata.length, 40);
assert.equal(metadata.reduce((sum, chapter) => sum + chapter.originals.length, 0), 148);
assert.equal(core.questions.length, 289);
assert.equal(new Set(core.questions.map(q => q.id)).size, 289);
assert.equal(new Set(core.questions.map(q => q.prompt)).size, 289);
assert.equal(crypto.createHash("sha256").update(JSON.stringify(core.questions.slice(0, 168))).digest("hex"),
  "0cfbaa25b1486702006c5a32408c8fd71b657a1e4a31661c6671cab18448d4a3", "기존 문항·보기·정답·해설 변경");
for (const chapter of metadata) {
  const id = String(chapter.number).padStart(2, "0");
  const questions = core.forChapter(id);
  const added = codeRows.filter(row => row.startsWith(id + "-")).length;
  assert.equal(questions.length, chapter.originals.length + (id === "39" ? 12 : id === "21" ? 8 : 0) + added, `${id}장 항목 누락`);
  questions.forEach(q => {
    assert.equal(q.options.length, 4);
    assert.equal(new Set(q.options).size, 4);
    assert.ok(Number.isInteger(q.answer) && q.answer >= 0 && q.answer < 4);
    assert.ok(q.prompt.endsWith("?") || q.prompt.endsWith("가?"));
    assert.ok(q.explanation.length > 15);
  });
}
const distribution = [0, 0, 0, 0];
core.questions.forEach(q => { distribution[q.answer] += 1; });
assert.ok(distribution.every(count => count >= 45 && count <= 100), "정답 위치가 과도하게 편중됨");
const augmented = { CHAPTER_QUIZ_ROWS: globalThis.CHAPTER_QUIZ_ROWS.trimEnd() + "\n39|추가 검증용 문항?|정답|오답 A|오답 B|오답 C|추가 검증용 해설입니다." };
vm.runInNewContext(fs.readFileSync(path.join(root, "practice/chapter-quiz.js"), "utf8"), augmented);
core.questions.forEach((question, i) => {
  assert.equal(JSON.stringify(augmented.ChapterQuiz.questions[i].options), JSON.stringify(question.options), "문항 추가 후 기존 선택지 이동");
});
const first = core.questions[0];
const second = core.questions[1];
const records = {
  [first.id]: { selected: first.answer, graded: true },
  [second.id]: { selected: (second.answer + 1) % 4, graded: true },
};
assert.deepEqual(core.statistics(core.questions, records), { total: 289, graded: 2, correct: 1, wrong: 1 });
for (const value of [null, [], "broken", 12]) {
  assert.deepEqual(core.sanitizeAnswers(value), {});
  assert.throws(() => core.sanitizeAnswers(value, true));
}
for (const record of [null, { selected: 4, graded: true }, { selected: "1", graded: true }, { selected: 0, graded: "true" }]) {
  assert.deepEqual(core.sanitizeAnswers({ [first.id]: record }), {});
  assert.throws(() => core.sanitizeAnswers({ [first.id]: record }, true));
}
assert.deepEqual(core.sanitizeAnswers(Object.create(records)), {});
assert.throws(() => core.sanitizeAnswers({ "chapter-v1-99-01": { selected: 1, graded: true } }, true));
const complete = Object.fromEntries(core.forChapter("00").map(q => [q.id, { selected: q.answer, graded: true }]));
assert.deepEqual(core.completedChapters(complete), ["00"]);
delete complete[first.id];
assert.deepEqual(core.completedChapters(complete), []);
const saved = StudyRecords.normalizeWorkbook({ workbook: { answers: { "00": "이전 답안" }, multipleChoice: records } });
assert.equal(saved.workbook.answers["00"], "이전 답안");
const backup = StudyRecords.createBackup(saved, null);
assert.equal(backup.version, 2);
assert.deepEqual(StudyRecords.parseBackup(JSON.stringify(backup)).workbook, saved);
const addedQuestion = core.questions[168];
const expanded = StudyRecords.normalizeWorkbook({ workbook: { multipleChoice: {
  ...records, [addedQuestion.id]: { selected: addedQuestion.answer, graded: true },
} } });
assert.deepEqual(StudyRecords.parseBackup(JSON.stringify(StudyRecords.createBackup(expanded, null))).workbook, expanded);
const legacy = JSON.parse(JSON.stringify(backup));
legacy.version = 1;
delete legacy.workbook.workbook.multipleChoice;
assert.deepEqual(StudyRecords.parseBackup(JSON.stringify(legacy)).workbook.workbook.multipleChoice, {});
const broken = JSON.parse(JSON.stringify(backup));
broken.workbook.workbook.multipleChoice[first.id].selected = 99;
assert.throws(() => StudyRecords.parseBackup(JSON.stringify(broken)));
const generation = spawnSync(process.execPath, ["scripts/generate_chapter_quiz.cjs", "--check"], { cwd: root, encoding: "utf8" });
assert.equal(generation.status, 0, generation.stderr);
for (const name of ["chapter-quiz-data.js", "chapter-quiz.js", "chapter-quiz-ui.js", "study-records.js"]) {
  const result = spawnSync(process.execPath, ["--check", path.join(root, "practice", name)], { encoding: "utf8" });
  assert.equal(result.status, 0, result.stderr);
}
const document = fs.readFileSync(path.join(root, "practice/problems.md"), "utf8");
assert.equal([...document.matchAll(/^### Q\d{2}-\d{2}\./gm)].length, 289);
assert.equal([...document.matchAll(/^\*\*정답:\*\* [①②③④]$/gm)].length, 289);
assert.equal([...document.matchAll(/^- [①②③④] /gm)].length, 1156);
for (const entry of codeMetadata) {
  assert.equal(entry.originals.length, entry.keys.length, entry.file);
  const text = fs.readFileSync(path.join(root, entry.file), "utf8");
  assert.ok(!/^[ \t]*(?:\/\/|#)[ \t]*A\d*:[ \t]*$/m.test(text), `서술형 답안칸: ${entry.file}`);
  assert.ok(!text.includes("학습 질문"), `서술형 제목: ${entry.file}`);
  assert.equal([...text.matchAll(/^[ \t]*(?:\/\/|#) Q\d+\./gm)].length, entry.keys.length);
  assert.equal([...text.matchAll(/^[ \t]*(?:\/\/|#) [①②③④] /gm)].length, entry.keys.length * 4);
  assert.equal([...text.matchAll(/^[ \t]*(?:\/\/|#) 정답: [①②③④]$/gm)].length, entry.keys.length);
  const executable = text.split("\n").filter(line => line.trim() && !/^\s*(?:\/\/|#)/.test(line)).join("\n");
  assert.equal(crypto.createHash("sha256").update(executable).digest("hex"), entry.executableHash, `실습 코드 변경: ${entry.file}`);
}
const index = fs.readFileSync(path.join(root, "index.html"), "utf8");
assert.ok(!index.includes('id="chapter-answer"'));
assert.ok(!index.includes('id="chapter-reviewed"'));
console.log("코드 연결 객관식 289제: 기존 168문항 보존·코드 질문 230개/70파일 전수 변환·121개 공통 문항·보기·채점·백업·코드 보존 검사 통과");
