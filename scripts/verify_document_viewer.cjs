// 실제 문서 파서와 렌더러로 코드 블록 보존·목차 연결·DOM ID 중복을 검사합니다.
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");
const vm = require("node:vm");
const root = path.resolve(__dirname, "..");
assert.equal(fs.realpathSync(process.cwd()), fs.realpathSync(root), "저장소 루트에서 실행하세요.");
const html = fs.readFileSync(path.join(root, "workbook-viewer.html"), "utf8");
const functions = html.slice(html.indexOf("    function makeHeadingId("), html.indexOf("    async function loadDocument("));
assert.ok(functions.includes("function splitDocument(") && functions.includes("function renderDocument("));

class Element {
  constructor(tagName) {
    this.tagName = tagName;
    this.children = [];
    this.classList = { toggle() {} };
  }
  append(...children) { this.children.push(...children); }
  replaceChildren(...children) { this.children = children; }
  get childElementCount() { return this.children.length; }
}
const nodes = new Map();
const node = id => {
  if (!nodes.has(id)) nodes.set(id, new Element("div"));
  return nodes.get(id);
};
const context = vm.createContext({
  document: { createElement: tag => new Element(tag), querySelector: node },
  documentTitle: node("title"),
  documentBody: node("body"),
  documentOutline: node("outline"),
  loadStatus: node("status"),
  solvingTab: { getAttribute: () => "false" },
  selectLearningTab() {},
  sourceFile: "README.md",
});
vm.runInContext(fs.readFileSync(path.join(root, "practice/quiz/document-quiz.js"), "utf8"), context);
vm.runInContext(functions, context);
const split = source => JSON.parse(JSON.stringify(context.splitDocument(source)));

// 실제 자료에 있는 셸 주석과 Markdown 양식 제목이 코드 본문에 남아야 합니다.
for (const [file, exampleHeading] of [
  ["README.md", "# SW 기술 4지선다 2,000문항 문제집 열기"],
  ["practice/job-preparation-workbook.md", "### YYYY-MM-DD"],
]) {
  const source = fs.readFileSync(path.join(root, file), "utf8");
  const chunks = split(source);
  assert.ok(chunks.some(chunk => chunk.lines.includes(exampleHeading)), `${file}: 코드 예시 보존`);
  assert.ok(!chunks.some(chunk => chunk.title === exampleHeading.replace(/^#+\s+/, "")), `${file}: 가짜 목차 방지`);
  const examples = source.match(/```[^\n]*\n[\s\S]*?^```[ \t]*$/gm) || [];
  for (const example of examples) {
    assert.ok(chunks.some(chunk => chunk.lines.join("\n").includes(example)), `${file}: 코드 블록이 여러 구획으로 나뉘면 안 됨`);
  }
}

// 더 짧은 닫기·다른 종류의 닫기·닫히지 않은 블록도 본문을 보존합니다.
const fenced = [
  "# 실제 제목", "   ````md", "# 코드 제목", "```", "## 여전히 코드", "~~~~", "   `````",
  "## 실제 구획", "~~~text", "### 예시 제목", "```", "~~~~", "## 마지막 구획",
].join("\r\n");
assert.deepEqual(split(fenced).map(chunk => chunk.title), ["실제 제목", "실제 구획", "마지막 구획"]);
assert.deepEqual(split("# 문서\n```md\n## 코드\n").map(chunk => chunk.title), ["문서"]);
assert.deepEqual(split("# 문서\n```bad`info\n## 제목\n").map(chunk => chunk.title), ["문서", "제목"]);
assert.deepEqual(split("class User {\r\n  String name;\r\n}\r\n")[0].lines, ["class User {", "  String name;", "}", ""]);

const flatten = element => [element, ...element.children.flatMap(flatten)];
for (const source of [
  fs.readFileSync(path.join(root, "README.md"), "utf8"),
  "# 문서\n## 같은 제목\n첫 구획\n## 같은 제목\n둘째 구획\n### 하위 제목\n본문",
]) {
  context.renderDocument(source);
  const body = flatten(node("body"));
  const ids = body.filter(element => element.id).map(element => element.id);
  assert.equal(ids.length, new Set(ids).size, "목차 대상 ID는 문서 안에서 고유해야 합니다.");
  const links = flatten(node("outline")).filter(element => element.tagName === "a");
  assert.ok(links.length > 0, "문서 목차가 있어야 합니다.");
  for (const link of links) {
    const targets = body.filter(element => element.id === link.href.slice(1));
    assert.equal(targets.length, 1, "각 목차 링크는 한 구획으로 연결되어야 합니다.");
    assert.equal(targets[0].tagName, "section", "목차 이동 시 구획의 스크롤 여백을 유지합니다.");
  }
}
console.log("문서 뷰어 검증 통과: 실제 자료 코드 블록 보존·fence 경계·고유 ID·목차 구획 연결");
