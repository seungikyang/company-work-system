# HTML 취업 워크북의 구조와 학습 링크 계약을 반복 검증하는 스크립트
from __future__ import annotations

from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import unquote, urlsplit
import re
import subprocess
import sys


ROOT = Path(__file__).resolve().parent.parent
HTML_FILES = (ROOT / "index.html", ROOT / "history.html")
VOID_TAGS = {
    "area",
    "base",
    "br",
    "col",
    "embed",
    "hr",
    "img",
    "input",
    "link",
    "meta",
    "param",
    "source",
    "track",
    "wbr",
}


class HtmlContractParser(HTMLParser):
    def __init__(self, path: Path) -> None:
        super().__init__(convert_charrefs=True)
        self.path = path
        self.stack: list[str] = []
        self.ids: set[str] = set()
        self.links: list[str] = []

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        attributes = dict(attrs)
        element_id = attributes.get("id")
        if element_id:
            require(element_id not in self.ids, f"{self.path.name}: 중복 ID #{element_id}")
            self.ids.add(element_id)

        for attribute in ("href", "src"):
            value = attributes.get(attribute)
            if value:
                self.links.append(value)

        if tag not in VOID_TAGS:
            self.stack.append(tag)

    def handle_startendtag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        self.handle_starttag(tag, attrs)
        if tag not in VOID_TAGS:
            self.stack.pop()

    def handle_endtag(self, tag: str) -> None:
        if tag in VOID_TAGS:
            return
        require(bool(self.stack), f"{self.path.name}: 여는 태그가 없는 </{tag}>")
        opened = self.stack.pop()
        require(opened == tag, f"{self.path.name}: <{opened}> 다음에 </{tag}>가 닫힘")


def require(condition: bool, message: str) -> None:
    if not condition:
        raise AssertionError(message)


def parse_html(path: Path) -> HtmlContractParser:
    parser = HtmlContractParser(path)
    parser.feed(path.read_text(encoding="utf-8"))
    parser.close()
    require(not parser.stack, f"{path.name}: 닫히지 않은 태그 {parser.stack}")
    return parser


def is_external(raw: str) -> bool:
    parsed = urlsplit(raw)
    return bool(parsed.scheme or parsed.netloc or raw.startswith(("mailto:", "tel:", "data:")))


def validate_html_links(parsers: dict[Path, HtmlContractParser]) -> int:
    checked = 0
    for source, parser in parsers.items():
        for raw in parser.links:
            if is_external(raw):
                continue
            parsed = urlsplit(raw)
            target = source if not parsed.path else (source.parent / unquote(parsed.path)).resolve()
            require(target.exists(), f"{source.relative_to(ROOT)}: 없는 로컬 링크 {raw}")
            if parsed.fragment and target.suffix == ".html":
                target_parser = parsers.get(target) or parse_html(target)
                require(
                    parsed.fragment in target_parser.ids,
                    f"{source.relative_to(ROOT)}: 없는 fragment {raw}",
                )
            checked += 1
    return checked


def validate_markdown_links() -> int:
    checked = 0
    link_pattern = re.compile(r"(?<!!)\[[^\]]+\]\(([^)]+)\)")
    for source in ROOT.rglob("*.md"):
        for raw in link_pattern.findall(source.read_text(encoding="utf-8")):
            raw = raw.strip().strip("<>")
            if not raw or is_external(raw) or raw.startswith("#"):
                continue
            path_part = urlsplit(raw).path
            target = (source.parent / unquote(path_part)).resolve()
            require(target.exists(), f"{source.relative_to(ROOT)}: 없는 Markdown 링크 {raw}")
            checked += 1
    return checked


def extract_single(text: str, tag: str) -> str:
    matches = re.findall(rf"<{tag}(?:\s[^>]*)?>([\s\S]*?)</{tag}>", text, flags=re.IGNORECASE)
    require(len(matches) == 1, f"index.html: 인라인 {tag}가 1개가 아님")
    return matches[0]


def validate_index_contract(index: str, index_parser: HtmlContractParser) -> None:
    chapters = re.findall(r'data-chapter="(\d{2})"', index)
    chapter_blocks = re.findall(
        r'<li class="chapter" data-chapter="(\d{2})">([\s\S]*?)</li>',
        index,
    )
    stages = re.findall(r'data-stage="([a-z]+)"', index)
    sessions = re.findall(r'data-session-check="([a-z]+)"', index)

    require(chapters == [f"{number:02d}" for number in range(40)], "00~39 챕터 순서가 깨짐")
    require(len(chapter_blocks) == 40, "챕터 HTML 블록이 40개가 아님")
    for number, block in chapter_blocks:
        require('class="chapter-links"' in block, f"{number}장: 학습 링크 영역이 없음")
        require("href=" in block, f"{number}장: 연결된 학습 파일이 없음")
    require(len(stages) == 5 and len(set(stages)) == 5, "취업 준비 단계가 5개가 아님")
    require(len(sessions) == 5 and len(set(sessions)) == 5, "오늘 학습 체크가 5개가 아님")
    require(index.count('class="learning-map-number"') == 6, "번호형 학습 목차가 6단계가 아님")
    require(index.count('<details class="course"') == 5, "전체 교재 코스가 5개가 아님")
    require("company-workbook-state-v1" in index, "브라우저 저장 키가 없음")
    require("renderSelectedChapter" in index, "선택 챕터 렌더링 함수가 없음")

    required_ids = {
        "learning-map",
        "setup",
        "roadmap",
        "session",
        "curriculum",
        "evidence",
        "today-chapter",
        "selected-chapter-title",
        "selected-chapter-copy",
        "selected-chapter-loop",
        "selected-hard-link",
        "selected-starter-link",
        "show-selected-chapter",
    }
    require(required_ids <= index_parser.ids, f"필수 ID 누락 {sorted(required_ids - index_parser.ids)}")

    script = extract_single(index, "script")
    selected_ids = set(re.findall(r'querySelector(?:All)?\("#([A-Za-z0-9_-]+)"\)', script))
    require(selected_ids <= index_parser.ids, f"JavaScript 대상 ID 누락 {sorted(selected_ids - index_parser.ids)}")

    result = subprocess.run(
        ["node", "--check", "-"],
        input=script,
        text=True,
        capture_output=True,
        check=False,
    )
    require(result.returncode == 0, f"JavaScript 구문 오류\n{result.stderr.strip()}")

    style = extract_single(index, "style")
    require(style.count("{") == style.count("}"), "CSS 중괄호 수가 맞지 않음")
    require("@media (max-width: 920px)" in style, "태블릿 반응형 분기가 없음")
    require("@media (max-width: 680px)" in style, "모바일 반응형 분기가 없음")


def main() -> int:
    require(Path.cwd().resolve() == ROOT, "저장소 루트에서 실행해야 합니다")
    parsers = {path: parse_html(path) for path in HTML_FILES}
    html_links = validate_html_links(parsers)
    markdown_links = validate_markdown_links()
    index = (ROOT / "index.html").read_text(encoding="utf-8")
    validate_index_contract(index, parsers[ROOT / "index.html"])

    print("워크북 검증 통과")
    print(f"- HTML 2개와 로컬 링크 {html_links}개")
    print(f"- Markdown 상대 링크 {markdown_links}개")
    print("- 학습 목차 6단계, 취업 로드맵 5단계, 링크가 있는 챕터 40개, 세션 체크 5개")
    print("- JavaScript 구문·DOM ID 계약과 920px·680px 반응형 분기")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (AssertionError, OSError) as error:
        print(f"워크북 검증 실패: {error}", file=sys.stderr)
        raise SystemExit(1)
