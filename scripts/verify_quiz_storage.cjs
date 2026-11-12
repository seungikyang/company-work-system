// 실제 기본기 화면의 이벤트를 실행하여 여러 화면·백업 복원·저장 실패 뒤에도 답안이 보존되는지 검사합니다.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const root = path.resolve(__dirname, "..");
assert.equal(fs.realpathSync(process.cwd()), fs.realpathSync(root), "저장소 루트에서 실행해야 합니다.");

// 브라우저 저장 동작만 격리하고 실제 문제 자료와 quiz-page.js를 그대로 실행합니다.
class Element {
  constructor(tag = "div") {
    this.tag = tag;
    this.children = [];
    this.listeners = {};
    this.value = "";
    this.classList = { toggle() {} };
  }
  append(...nodes) { this.children.push(...nodes); }
  replaceChildren(...nodes) { this.children = nodes; }
  setAttribute() {}
  addEventListener(type, handler) { this.listeners[type] = handler; }
  focus() {}
}
const files = [
  "practice/quiz/content-00-09.js", "practice/quiz/content-10-19.js",
  "practice/quiz/content-20-29.js", "practice/quiz/content-30-39.js",
  "practice/quiz/quiz-core.js", "practice/quiz/quiz-page.js",
];
const sources = files.map(file => [file, fs.readFileSync(path.join(root, file), "utf8")]);
const storageKey = "company-work-system.fundamentals.technology.v2";
const first = "tech-v2-00-1-concept";
const second = "tech-v2-00-2-concept";
const third = "tech-v2-00-3-concept";
const restored = "tech-v2-39-5-verification";
const graded = { selected: 3, graded: true };

function makeStorage(answers = {}) {
  let raw = JSON.stringify({ version: 2, answers });
  return {
    blocked: false,
    getItem(key) { return key === storageKey ? raw : null; },
    setItem(key, value) {
      assert.equal(key, storageKey);
      if (this.blocked) throw new Error("QuotaExceededError");
      raw = value;
    },
    removeItem(key) { if (key === storageKey) raw = null; },
    read() { return JSON.parse(raw || '{"answers":{}}').answers; },
    replace(answers) { raw = JSON.stringify({ version: 2, answers }); },
  };
}

function openPage(storage) {
  const nodes = new Map();
  const listeners = {};
  const byId = id => {
    if (!nodes.has(id)) nodes.set(id, new Element());
    return nodes.get(id);
  };
  const context = vm.createContext({
    console, URLSearchParams, location: { search: "" }, localStorage: storage,
    document: { getElementById: byId, createElement: tag => new Element(tag) },
    window: { addEventListener: (type, handler) => { listeners[type] = handler; } },
  });
  for (const [file, source] of sources) vm.runInContext(source, context, { filename: file });
  assert.ok(byId("question-list").children.length, "기본기 화면을 정상적으로 준비합니다.");
  function descendants(node) {
    return [node, ...node.children.flatMap(descendants)];
  }
  function card(id) {
    const result = byId("question-list").children.find(node => node.id === id);
    assert.ok(result, `화면에 문항 ${id}가 있습니다.`);
    return result;
  }
  function choices(id) { return descendants(card(id)).filter(node => node.tag === "input"); }
  return {
    select(id, selected) { choices(id)[selected].listeners.change(); },
    grade(id) {
      descendants(card(id)).find(node => node.className === "grade-button").listeners.click();
    },
    selection(id) { return choices(id).findIndex(node => node.checked); },
    graded(id) { return choices(id).every(node => node.disabled); },
    count() { return Number(byId("answered-count").textContent.replaceAll(",", "")); },
    storageEvent(key = storageKey) { listeners.storage({ key }); },
  };
}

// storage 알림보다 클릭이 먼저 처리되어도 다른 탭의 답안을 덮어쓰지 않습니다.
{
  const storage = makeStorage();
  const pageA = openPage(storage);
  const pageB = openPage(storage);
  pageB.select(first, 1);
  pageB.grade(first);
  pageA.select(second, 2);
  assert.deepEqual(storage.read(), {
    [first]: { selected: 1, graded: true },
    [second]: { selected: 2, graded: false },
  });
  assert.equal(pageA.count(), 1, "병합한 다른 화면의 채점 수를 반영합니다.");
  pageB.storageEvent();
  assert.equal(pageB.selection(second), 2, "다른 화면의 선택을 화면에도 반영합니다.");
}

// 오래 열린 화면에서 답안을 고르더라도 백업에서 복원한 답안과 삭제 결과가 보존됩니다.
{
  const storage = makeStorage({ [first]: graded });
  const page = openPage(storage);
  storage.replace({ [restored]: graded });
  page.select(second, 0);
  assert.deepEqual(storage.read(), {
    [restored]: graded, [second]: { selected: 0, graded: false },
  });
  assert.equal(Object.hasOwn(storage.read(), first), false, "복원으로 지운 옛 답안을 되살리지 않습니다.");
}

// 다시 풀기는 해당 문항만 삭제합니다.
{
  const storage = makeStorage({ [first]: graded });
  const page = openPage(storage);
  storage.replace({ [first]: graded, [restored]: graded });
  page.grade(first);
  assert.deepEqual(storage.read(), { [restored]: graded });
}

// 쓰기 실패 중에도 미저장 선택·삭제는 화면에 남으며 다음 저장에 함께 재시도합니다.
{
  const storage = makeStorage({ [first]: graded });
  const page = openPage(storage);
  storage.blocked = true;
  page.grade(first);
  page.select(second, 2);
  storage.replace({ [first]: graded, [third]: graded, [restored]: graded });
  page.storageEvent();
  assert.equal(page.selection(first), -1, "실패한 삭제가 다른 화면의 알림으로 취소되지 않습니다.");
  assert.equal(page.selection(second), 2, "미저장 선택을 유지합니다.");
  assert.equal(page.graded(third), true, "다른 화면의 채점도 함께 반영합니다.");
  storage.blocked = false;
  page.grade(second);
  assert.deepEqual(storage.read(), {
    [third]: graded, [restored]: graded,
    [second]: { selected: 2, graded: true },
  });
  storage.replace({ [second]: { selected: 0, graded: false } });
  page.storageEvent();
  assert.equal(page.selection(second), 0, "저장한 변경은 이후 다른 화면의 수정으로 갱신할 수 있습니다.");
}

// 저장소 전체 삭제 알림도 반영하며 미저장 선택은 지우지 않습니다.
{
  const storage = makeStorage({ [first]: graded });
  const page = openPage(storage);
  storage.blocked = true;
  page.select(second, 1);
  storage.removeItem(storageKey);
  page.storageEvent(null);
  assert.equal(page.selection(first), -1);
  assert.equal(page.selection(second), 1);
  assert.equal(page.count(), 0);
  storage.blocked = false;
  page.grade(second);
  assert.deepEqual(storage.read(), { [second]: { selected: 1, graded: true } });
}

// 코드 연결 문제집(problems.html)의 실제 저장 함수도 최신 목표·다른 답안을 보존하는지 검사합니다.
const workbookKey = "company-workbook-state-v1";
const chapterFirst = "chapter-v1-00-01";
const chapterSecond = "chapter-v1-00-02";
const problemScript = fs.readFileSync(path.join(root, "problems.html"), "utf8")
  .match(/<script>\n([\s\S]*?)<\/script>\s*<\/body>/)[1];
const chapterSources = [
  "practice/chapter-quiz-data.js", "practice/chapter-quiz.js", "practice/study-records.js",
].map(file => [file, fs.readFileSync(path.join(root, file), "utf8")]);

function makeWorkbookStorage(value = null) {
  let raw = value === null ? null : JSON.stringify(value);
  return {
    blocked: false,
    getItem(key) {
      if (this.blocked) throw new Error("SecurityError");
      return key === workbookKey ? raw : null;
    },
    setItem(key, next) {
      assert.equal(key, workbookKey);
      if (this.blocked) throw new Error("QuotaExceededError");
      raw = next;
    },
    read() { return JSON.parse(raw); },
  };
}

// 자료를 영원히 불러오는 중인 상태로 열어 storage 알림이 로딩 중에 오는 경우를 만듭니다.
function openProblemPage(storage) {
  const nodes = new Map();
  const listeners = {};
  const context = vm.createContext({
    console, URL, localStorage: storage,
    fetch: () => new Promise(() => {}),
    document: {
      getElementById: id => {
        if (!nodes.has(id)) nodes.set(id, new Element());
        return nodes.get(id);
      },
      createElement: tag => new Element(tag),
    },
    window: {
      location: { href: "http://127.0.0.1:4174/company-work-system/problems.html", origin: "http://127.0.0.1:4174" },
      addEventListener: (type, handler) => { listeners[type] = handler; },
    },
  });
  for (const [file, source] of chapterSources) vm.runInContext(source, context, { filename: file });
  vm.runInContext(problemScript, context, { filename: "problems.html" });
  return {
    answer(id, record) { context.saveQuizAnswer(id, record); },
    storageEvent(key = workbookKey) { listeners.storage({ key }); },
  };
}

const recordsContext = vm.createContext({});
for (const [file, source] of chapterSources) vm.runInContext(source, recordsContext, { filename: file });
function workbookWith(targetRole, multipleChoice = {}) {
  const value = JSON.parse(JSON.stringify(vm.runInContext("StudyRecords.normalizeWorkbook(null)", recordsContext)));
  value.profile.targetRole = targetRole;
  value.workbook.multipleChoice = multipleChoice;
  return value;
}

// 다른 화면이 저장한 최신 목표·답안을 오래된 화면의 답안 저장이 덮어쓰지 않습니다.
{
  const storage = makeWorkbookStorage(workbookWith("old"));
  const page = openProblemPage(storage);
  storage.setItem(workbookKey, JSON.stringify(workbookWith("new", { [chapterFirst]: graded })));
  page.answer(chapterSecond, { selected: 1, graded: false });
  assert.equal(storage.read().profile.targetRole, "new", "다른 화면에서 저장한 목표를 보존합니다.");
  assert.deepEqual(storage.read().workbook.multipleChoice, {
    [chapterFirst]: graded, [chapterSecond]: { selected: 1, graded: false },
  });
}

// 처음 읽기에 실패한 뒤 저장이 다시 가능해져도 기존 기록을 비운 상태로 덮어쓰지 않습니다.
{
  const storage = makeWorkbookStorage(workbookWith("kept", { [chapterFirst]: graded }));
  storage.blocked = true;
  const page = openProblemPage(storage);
  page.answer(chapterSecond, { selected: 2, graded: false });
  storage.blocked = false;
  page.answer(chapterSecond, { selected: 2, graded: true });
  assert.equal(storage.read().profile.targetRole, "kept");
  assert.deepEqual(storage.read().workbook.multipleChoice, {
    [chapterFirst]: graded, [chapterSecond]: { selected: 2, graded: true },
  });
}

// 문제집을 불러오는 중에 온 storage 알림과 저장 실패 중의 미저장 선택이 모두 반영됩니다.
{
  const storage = makeWorkbookStorage(workbookWith("old"));
  const page = openProblemPage(storage);
  storage.blocked = true;
  page.answer(chapterFirst, { selected: 0, graded: false });
  storage.blocked = false;
  storage.setItem(workbookKey, JSON.stringify(workbookWith("loading-time")));
  page.storageEvent();
  page.answer(chapterSecond, { selected: 3, graded: false });
  assert.equal(storage.read().profile.targetRole, "loading-time");
  assert.deepEqual(storage.read().workbook.multipleChoice, {
    [chapterFirst]: { selected: 0, graded: false }, [chapterSecond]: { selected: 3, graded: false },
  });
}

// 메인 화면(index.html)의 저장 병합 함수도 이 화면에서 바꾼 칸만 최신 노트에 덮어쓰는지 검사합니다.
{
  const indexSource = fs.readFileSync(path.join(root, "index.html"), "utf8");
  const pick = name => indexSource.match(new RegExp(`const ${name} = [\\s\\S]*?\\n      };?\\n`))[0];
  const context = vm.createContext({});
  vm.runInContext([
    indexSource.match(/const same = .*\n/)[0],
    pick("mergeChanges"),
    "globalThis.mergeChanges = mergeChanges;",
  ].join("\n"), context, { filename: "index.html" });
  const base = {
    profile: { targetRole: "old" }, stages: ["prepare"],
    workbook: { multipleChoice: { [chapterFirst]: graded } },
  };
  const local = JSON.parse(JSON.stringify(base));
  local.profile.targetRole = "mine";
  local.stages = [];
  const latest = {
    profile: { targetRole: "old" }, stages: ["prepare", "design"],
    workbook: { multipleChoice: { [chapterFirst]: graded, [chapterSecond]: { selected: 1, graded: false } } },
  };
  const merged = JSON.parse(JSON.stringify(context.mergeChanges(base, local, latest)));
  assert.deepEqual(merged, {
    profile: { targetRole: "mine" }, stages: ["design"],
    workbook: { multipleChoice: { [chapterFirst]: graded, [chapterSecond]: { selected: 1, graded: false } } },
  }, "이 화면의 목표·목록 변경만 반영하고 다른 화면의 목록·답안을 보존합니다.");
  const removed = JSON.parse(JSON.stringify(base));
  delete removed.workbook.multipleChoice[chapterFirst];
  assert.deepEqual(
    JSON.parse(JSON.stringify(context.mergeChanges(base, removed, latest))).workbook.multipleChoice,
    { [chapterSecond]: { selected: 1, graded: false } },
    "이 화면에서 지운 답안만 지웁니다.",
  );
  assert.equal(context.mergeChanges(base, JSON.parse(JSON.stringify(base)), latest), latest, "바뀐 것이 없으면 최신 노트를 그대로 씁니다.");
}

console.log("기본기 저장 검증 통과: 다중 화면 병합·복원 보존·문항 삭제·저장 실패 재시도·전체 삭제 알림·코드 연결 문제집·메인 화면 목표 보존");
