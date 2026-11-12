// 중학생 눈높이: 이 파일은 자동 검사입니다. 학습 노트의 백업·복원과 잘못된 기록 처리를 확인합니다.
// assert는 예상과 실제가 맞는지 확인하는 도구입니다. 틀리면 그 지점에서 검사 실패를 알려 줍니다.
// 실행: 저장소 루트에서 node scripts/verify_study_records.cjs
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
require("../practice/chapter-quiz-data.js");
require("../practice/chapter-quiz.js");
const source = fs.readFileSync(path.join(__dirname, "../practice/study-records.js"), "utf8");
new vm.Script(source);
vm.runInThisContext(source);
const core = globalThis.StudyRecords;
const workbook = core.normalizeWorkbook({
  profile: { targetRole: "Java 개발자", targetDeadline: "2026-12-01", dailyMinutes: "60" },
  today: { frId: "FR-LEAVE-001", chapter: "10", goal: "실패 테스트", testCommand: "./gradlew test", testResult: "pass" },
  stages: ["prepare"], chapters: ["00", "10"], session: ["choose", "test"],
  workbook: { answers: { "10": "권한과 중복을 확인합니다." }, reviewed: ["10"] },
});
const quiz = { version: 2, answers: { "tech-v2-39-5-verification": { selected: 3, graded: true } } };
const backup = core.createBackup(workbook, quiz);
assert.deepEqual(core.parseBackup(JSON.stringify(backup)), { workbook, quiz });
const expandedQuiz = { version: 2, answers: { ...quiz.answers } };
for (const kind of ["concept", "situation", "implementation", "risk", "verification"]) {
  expandedQuiz.answers[`tech-v2-39-5-identify-${kind}`] = { selected: 2, graded: true };
}
assert.deepEqual(core.parseBackup(JSON.stringify(core.createBackup(workbook, expandedQuiz))), { workbook, quiz: expandedQuiz });

assert.deepEqual(core.parseBackup(JSON.stringify(core.createBackup(workbook, null))), { workbook, quiz: null });
for (const invalid of [null, [], 42, "broken", { profile: { targetRole: [] }, workbook: { answers: { "10": 10 } } }]) {
  assert.doesNotThrow(() => core.normalizeWorkbook(invalid));
  assert.equal(core.normalizeWorkbook(invalid).profile.targetRole, "");
  assert.throws(() => core.normalizeWorkbook(invalid, true));
}
const old = core.normalizeWorkbook({ profile: { targetRole: "예전 기록" }, chapters: ["00"], today: {} });
assert.equal(old.profile.targetRole, "예전 기록");
assert.deepEqual(old.chapters, ["00"]);
assert.deepEqual(old.workbook, { answers: {}, reviewed: [], multipleChoice: {} });
const dirty = JSON.parse(JSON.stringify(workbook));
dirty.chapters = ["00", "00", "99", null];
dirty.workbook.answers = JSON.parse('{"10":"정상 답안","11":23,"12":"   ","__proto__":"위험","99":"없는 챕터"}');
dirty.workbook.reviewed = ["10", "11", "12"];
const cleaned = core.normalizeWorkbook(dirty);
assert.deepEqual(cleaned.chapters, ["00"]);
assert.deepEqual(cleaned.workbook, { answers: { "10": "정상 답안" }, reviewed: ["10"], multipleChoice: {} });
assert.equal({}.polluted, undefined);
assert.equal(core.normalizeWorkbook(Object.create(workbook)).profile.targetRole, "");
for (const change of [
  value => { value.version = 999; },
  value => { value.format = "other-app"; },
  value => { delete value.workbook; },
  value => { value.workbook.workbook.answers["10"] = {}; },
  value => { value.workbook.today.chapter = "99"; },
  value => { value.workbook.today.testResult = "fake"; },
  value => { value.workbook.profile.targetDeadline = "2026-02-30"; },
  value => { value.workbook.profile.dailyMinutes = "five"; },
  value => { delete value.quiz; },
  value => { value.quiz.version = 1; },
  value => { value.quiz.answers["tech-v2-00-1-identify-unknown"] = { selected: 0, graded: true }; },
  value => { value.quiz.answers["tech-v2-39-5-verification"].selected = 4; },
  value => { value.quiz.answers["tech-v2-99-1-concept"] = { selected: 1, graded: false }; },
]) {
  const value = JSON.parse(JSON.stringify(backup));
  change(value);
  assert.throws(() => core.parseBackup(JSON.stringify(value)));
}
assert.throws(() => core.parseBackup("{broken"));
for (const date of ["invalid", "2026-02-30", "2026-13-01"]) {
  const input = JSON.parse(JSON.stringify(workbook));
  input.profile.targetDeadline = date;
  assert.equal(core.normalizeWorkbook(input).profile.targetDeadline, "");
}
function storage(initial = {}) {
  const data = new Map(Object.entries(initial));
  return {
    getItem: key => data.get(key) ?? null,
    setItem: (key, value) => data.set(key, value),
    removeItem: key => data.delete(key),
  };
}
const store = storage({ unrelated: "preserved" });
assert.throws(() => core.restore(store, { workbook: null, quiz }));
assert.equal(store.getItem(core.workbookKey), null, "잘못된 기록은 저장 전 거절");
core.restore(store, { workbook, quiz });
assert.deepEqual(JSON.parse(store.getItem(core.workbookKey)), workbook);
assert.deepEqual(JSON.parse(store.getItem(core.quizKey)), quiz);
assert.equal(store.getItem("unrelated"), "preserved");
const expandedStore = storage();
core.restore(expandedStore, { workbook, quiz: expandedQuiz });
assert.deepEqual(JSON.parse(expandedStore.getItem(core.quizKey)), expandedQuiz);

core.restore(store, { workbook: old, quiz: null });
assert.deepEqual(JSON.parse(store.getItem(core.quizKey)), quiz, "미포함 객관식 기록은 유지");
const blocked = storage({ [core.workbookKey]: "original" });
blocked.setItem = () => { throw new Error("storage blocked"); };
assert.throws(() => core.restore(blocked, { workbook, quiz }), /storage blocked/);
assert.equal(blocked.getItem(core.workbookKey), "original");
for (const existing of [false, true]) {
  const initial = existing ? { [core.workbookKey]: "old workbook", [core.quizKey]: "old quiz" } : {};
  const store = storage(initial);
  const setItem = store.setItem;
  let calls = 0;
  store.setItem = (key, value) => {
    calls += 1;
    if (calls === 2) throw new Error("QuotaExceededError");
    setItem(key, value);
  };
  assert.throws(() => core.restore(store, { workbook, quiz }));
  assert.equal(store.getItem(core.workbookKey), initial[core.workbookKey] ?? null);
  assert.equal(store.getItem(core.quizKey), initial[core.quizKey] ?? null);
}
// 실제 파일 선택 핸들러로 내보내기 → 가져오기까지 확인합니다.
// 한글은 UTF-8에서 한 글자가 여러 바이트이므로 정상 백업도 5MiB를 넘을 수 있습니다.
async function verifyBackupFileImport() {
  const html = fs.readFileSync(path.join(__dirname, "../index.html"), "utf8");
  const start = html.indexOf('backupFile.addEventListener("change", async () => {');
  const end = html.indexOf('\n      byId("cancel-study-restore")', start);
  assert.ok(start >= 0 && end > start);
  let importFile;
  const fileInput = { files: [], value: "", addEventListener: (_, callback) => { importFile = callback; } };
  const preview = { hidden: true };
  const status = { textContent: "" };
  const summary = { textContent: "" };
  const context = vm.createContext({
    backupFile: fileInput, backupPreview: preview, backupStatus: status,
    byId: () => summary, StudyRecords: core,
    pendingBackup: null, fileSelection: 0,
  });
  vm.runInContext(html.slice(start, end), context);
  const largeWorkbook = core.normalizeWorkbook(workbook);
  largeWorkbook.today.goal = "가".repeat(1_800_000);
  const exported = JSON.stringify(core.createBackup(largeWorkbook, quiz), null, 2);
  const file = new Blob([exported], { type: "application/json" });
  assert.ok(file.size > 5 * 1024 * 1024);
  fileInput.files = [file];
  await importFile();
  assert.equal(preview.hidden, false, "프로그램이 내보낸 큰 한글 백업도 복원 미리보기에 표시");
  assert.deepEqual(context.pendingBackup, { workbook: largeWorkbook, quiz });

  fileInput.files = [new Blob(["invalid json"])];
  await importFile();
  assert.equal(preview.hidden, true);
  assert.equal(context.pendingBackup, null, "잘못된 형식은 크기에 관계없이 복원 전 거절");
}
verifyBackupFileImport().then(() => {
  console.log("학습 기록 백업·복원과 큰 한글 파일·손상 데이터·저장 실패 복구 검사 통과");
}).catch(error => {
  console.error(error);
  process.exitCode = 1;
});
