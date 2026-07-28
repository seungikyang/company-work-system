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
STARTER_ROOT = ROOT / "practice" / "starter"
HARD_ROOT = ROOT / "practice" / "hard"
SERVER_SCRIPT = ROOT / "scripts" / "serve_workbook.py"
OPEN_ORCA_SCRIPT = ROOT / "scripts" / "open_workbook_in_orca.sh"
FAVICON_PREFIX = '<link rel="icon" href="data:image/svg+xml,'
CODE_CHAPTERS = tuple([*range(20), 21, *range(23, 35)])
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


def validate_document_icons() -> None:
    for path in HTML_FILES:
        document = path.read_text(encoding="utf-8")
        require(
            document.count(FAVICON_PREFIX) == 1,
            f"{path.name}: SVG 데이터 URI 파비콘 계약 누락",
        )


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


def validate_program_folders(index_parser: HtmlContractParser) -> tuple[int, int]:
    starter_directories = sorted(path for path in STARTER_ROOT.iterdir() if path.is_dir())
    hard_directories = sorted(path for path in HARD_ROOT.iterdir() if path.is_dir())
    expected_starter = [f"{number:02d}" for number in range(40)]
    expected_hard = [f"{number:02d}" for number in CODE_CHAPTERS]

    def validate_directory_sequence(paths: list[Path], expected: list[str], mode: str) -> None:
        names = [path.name for path in paths]
        for name in names:
            require(
                re.fullmatch(r"\d{2}-[a-z0-9-]+", name) is not None,
                f"{mode}: 프로그램 폴더 이름 형식 오류 {name}",
            )
        require(
            [name[:2] for name in names] == expected,
            f"{mode}: 프로그램 폴더 번호가 예상 범위와 다름",
        )

    validate_directory_sequence(starter_directories, expected_starter, "Starter")
    validate_directory_sequence(hard_directories, expected_hard, "Hard")

    linked_targets = set()
    for raw in index_parser.links:
        if is_external(raw):
            continue
        path_part = urlsplit(raw).path
        if path_part:
            linked_targets.add((ROOT / unquote(path_part)).resolve())

    program_files: list[Path] = []
    for directory in [*starter_directories, *hard_directories]:
        files = sorted(path for path in directory.iterdir() if path.is_file())
        require(bool(files), f"{directory.relative_to(ROOT)}: 학습 파일이 없음")
        program_files.extend(files)

    require(len(program_files) == 77, f"프로그램 학습 파일이 77개가 아님: {len(program_files)}개")
    for path in program_files:
        relative = path.relative_to(ROOT)
        require(path.suffix in {".java", ".fragment", ".md"}, f"{relative}: 지원하지 않는 학습 파일 형식")
        content = path.read_text(encoding="utf-8")
        require(bool(content.strip()), f"{relative}: 빈 학습 파일")
        first_line = content.splitlines()[0].strip()
        require(first_line.startswith(("//", "#")), f"{relative}: 역할을 설명하는 첫 줄이 없음")
        if path.suffix in {".java", ".fragment"}:
            require(
                re.search(r"^(//|#) 목표:", content, flags=re.MULTILINE) is not None,
                f"{relative}: 프로그램 학습 목표 헤더가 없음",
            )
            require(
                "TODO" in content or "____" in content,
                f"{relative}: 직접 작성할 TODO 또는 빈칸이 없음",
            )
        if path.suffix == ".md":
            require("## 챕터 계약" in content, f"{relative}: 챕터 계약이 없음")
            for label in ("학습 목표", "취업 결과물", "완료 검증"):
                require(f"| {label} |" in content, f"{relative}: 챕터 계약의 {label}이 없음")
        require(path.resolve() in linked_targets, f"{relative}: index.html 전수 목차 링크가 없음")

    for hard_file in (path for path in program_files if HARD_ROOT in path.parents):
        starter_pair = STARTER_ROOT / hard_file.relative_to(HARD_ROOT)
        require(
            starter_pair.exists(),
            f"{hard_file.relative_to(ROOT)}: 같은 경로의 Starter 파일이 없음",
        )

    return len(starter_directories) + len(hard_directories), len(program_files)


def extract_single(text: str, tag: str) -> str:
    matches = re.findall(rf"<{tag}(?:\s[^>]*)?>([\s\S]*?)</{tag}>", text, flags=re.IGNORECASE)
    require(len(matches) == 1, f"index.html: 인라인 {tag}가 1개가 아님")
    return matches[0]


def validate_index_contract(index: str, index_parser: HtmlContractParser) -> None:
    chapters = re.findall(r'data-chapter="(\d{2})"', index)
    chapter_blocks = re.findall(
        r'<li class="chapter"([^>]*)>([\s\S]*?)</li>',
        index,
    )
    stages = re.findall(r'data-stage="([a-z]+)"', index)
    sessions = re.findall(r'data-session-check="([a-z]+)"', index)
    test_results = re.findall(r'<option value="(fail|pass)">', index)

    require(chapters == [f"{number:02d}" for number in range(40)], "00~39 챕터 순서가 깨짐")
    require(len(chapter_blocks) == 40, "챕터 HTML 블록이 40개가 아님")
    for attributes, block in chapter_blocks:
        number_match = re.search(r'data-chapter="(\d{2})"', attributes)
        output_match = re.search(r'data-career-output="([^"]+)"', attributes)
        check_match = re.search(r'data-career-check="([^"]+)"', attributes)
        require(number_match is not None, "챕터 행에 번호가 없음")
        number = number_match.group(1)
        require('class="chapter-links"' in block, f"{number}장: 학습 링크 영역이 없음")
        require("href=" in block, f"{number}장: 연결된 학습 파일이 없음")
        require(output_match is not None, f"{number}장: 취업 결과물 계약이 없음")
        require(check_match is not None, f"{number}장: 완료 검증 계약이 없음")
        for raw in re.findall(r'href="([^"]+)"', block):
            program_match = re.search(r"\./practice/(?:starter|hard)/(\d{2})-", raw)
            if program_match:
                require(
                    program_match.group(1) == number,
                    f"{number}장: 다른 번호의 프로그램 폴더 링크 {raw}",
                )
    require(len(stages) == 5 and len(set(stages)) == 5, "취업 준비 단계가 5개가 아님")
    require(len(sessions) == 5 and len(set(sessions)) == 5, "오늘 학습 체크가 5개가 아님")
    require(test_results == ["fail", "pass"], "검증 결과 실패·통과 선택지가 깨짐")
    require(index.count('class="learning-map-number"') == 6, "번호형 학습 목차가 6단계가 아님")
    require(index.count('<details class="course"') == 5, "전체 교재 코스가 5개가 아님")
    require("company-workbook-state-v1" in index, "브라우저 저장 키가 없음")
    require("renderSelectedChapter" in index, "선택 챕터 렌더링 함수가 없음")
    require("renderVerificationStatus" in index, "검증 결과 렌더링 함수가 없음")
    require("resetTodaySession" in index, "오늘 학습 초기화 함수가 없음")
    require("selectChapterAt" in index, "연속 챕터 탐색 함수가 없음")
    require("renderChapterWorkbookStatus" in index, "문제집 상태 렌더링 함수가 없음")
    require("toggleWorkbookReveal" in index, "힌트·정답 토글 함수가 없음")
    require("syncFieldValues" in index, "상태 필드 값 동기화 함수가 없음")
    require("bindStateFields" in index, "상태 필드 이벤트 연결 함수가 없음")
    require("setNextAction" in index, "다음 행동 공통 갱신 함수가 없음")
    require("setCoursesOpen" in index, "전체 코스 펼치기·접기 공통 함수가 없음")
    require("updateListValue" in index, "체크 목록 공통 갱신 함수가 없음")
    require("setActiveNavigation" in index, "현재 메뉴 표시 함수가 없음")
    require("focusSelectedChapter" in index, "선택 챕터 포커스 이동 함수가 없음")
    require("prefersReducedMotion" in index, "모션 축소 감지 계약이 없음")
    require("window.history.replaceState" in index, "프로그램 화면 전환의 현재 메뉴 동기화가 없음")
    require(index.count('class="skip-link"') == 1, "본문 바로 가기 링크가 1개가 아님")
    require('workbook: { answers: {}, reviewed: [] }' in index, "챕터별 문제집 기본 상태가 없음")
    require("state.workbook.answers[number]" in index, "챕터별 답안 저장 계약이 없음")
    require("state.workbook.reviewed" in index, "정답 비교 완료 저장 계약이 없음")

    required_ids = {
        "learning-map",
        "setup",
        "roadmap",
        "session",
        "curriculum",
        "evidence",
        "today-chapter",
        "today-test-command",
        "today-test-result",
        "selected-chapter-title",
        "selected-chapter-copy",
        "selected-career-contract",
        "selected-career-output",
        "selected-career-check",
        "selected-chapter-loop",
        "selected-hard-link",
        "selected-starter-link",
        "workbook-count",
        "chapter-workbook",
        "chapter-question-title",
        "chapter-question",
        "chapter-answer",
        "chapter-workbook-status",
        "toggle-chapter-hint",
        "toggle-chapter-answer",
        "chapter-hint",
        "chapter-hint-copy",
        "chapter-answer-guide",
        "chapter-answer-responsibility",
        "chapter-answer-output",
        "chapter-answer-verification",
        "chapter-reviewed",
        "chapter-sequence",
        "chapter-position",
        "previous-chapter",
        "next-chapter",
        "open-selected-guide",
        "show-selected-chapter",
        "verification-status",
        "verification-status-title",
        "verification-status-copy",
        "reset-today-session",
    }
    require(required_ids <= index_parser.ids, f"필수 ID 누락 {sorted(required_ids - index_parser.ids)}")

    script = extract_single(index, "script")
    selected_ids = set(re.findall(r'querySelector(?:All)?\("#([A-Za-z0-9_-]+)"\)', script))
    selected_ids.update(re.findall(r'byId\("([A-Za-z0-9_-]+)"\)', script))
    require(selected_ids <= index_parser.ids, f"JavaScript 대상 ID 누락 {sorted(selected_ids - index_parser.ids)}")
    require(
        not re.findall(r'document\.querySelector\("#[A-Za-z0-9_-]+"\)', script),
        "알려진 ID 요소를 querySelector로 중복 조회함",
    )
    for selector in ("[data-stage-check]", "[data-session-check]", ".course"):
        require(
            script.count(f'document.querySelectorAll("{selector}")') == 1,
            f"{selector} 요소 목록을 한 번만 조회해야 함",
        )

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
    require("@media (prefers-reduced-motion: reduce)" in style, "모션 축소 반응형 분기가 없음")
    require("scroll-margin-top" in style, "고정 헤더를 고려한 앵커 여백이 없음")
    require('a[aria-current="location"]' in style, "현재 메뉴 표시 스타일이 없음")
    require("button:focus-visible" in style, "버튼 키보드 포커스 스타일이 없음")
    require(".session-jump" in style, "선택 챕터 문제 이동 버튼 스타일이 없음")
    require(".career-contract" in style, "취업 결과물 계약 반응형 스타일이 없음")
    require(".chapter-workbook" in style, "챕터 문제집 스타일이 없음")
    require(".workbook-reveal[hidden]" in style, "힌트·정답 숨김 스타일이 없음")


def validate_history_contract(history: str) -> int:
    item_count = len(re.findall(r'<li class="history-item(?: latest)?">', history))
    commit_count = history.count('class="commit"')
    latest_count = history.count('<li class="history-item latest">')
    dates = re.findall(r'<time class="history-date" datetime="(\d{4}-\d{2}-\d{2})">', history)
    summary_match = re.search(
        r'<section class="summary-grid"[\s\S]*?<article class="summary-card">\s*'
        r"<strong>(\d+)</strong>",
        history,
    )
    updated_match = re.search(
        r'마지막 문서 갱신일\s*<time datetime="(\d{4}-\d{2}-\d{2})">',
        history,
    )

    require(item_count > 0, "history.html: 대표 이력이 없음")
    require(commit_count == item_count, "history.html: 커밋 링크 수와 대표 이력 수가 다름")
    require(latest_count == 1, "history.html: latest 대표 이력이 1개가 아님")
    require(len(dates) == item_count, "history.html: 날짜 수와 대표 이력 수가 다름")
    require(dates[0] == max(dates), "history.html: 최신 날짜가 타임라인 첫 항목이 아님")
    require(summary_match is not None, "history.html: 대표 이력 요약 수가 없음")
    require(
        int(summary_match.group(1)) == item_count,
        "history.html: 요약 수와 대표 이력 수가 다름",
    )
    require(
        f"대표 배포 확인 이력 {item_count}개" in history,
        "history.html: 타임라인 설명의 대표 이력 수가 다름",
    )
    require(updated_match is not None, "history.html: 마지막 문서 갱신일이 없음")
    require(
        updated_match.group(1) == dates[0],
        "history.html: 마지막 문서 갱신일과 최신 이력 날짜가 다름",
    )
    return item_count


def validate_orca_launch_contract() -> None:
    require(SERVER_SCRIPT.exists(), "폴더 이름 URI 서버 스크립트가 없음")
    require(OPEN_ORCA_SCRIPT.exists(), "Orca 워크북 실행 스크립트가 없음")
    require(OPEN_ORCA_SCRIPT.stat().st_mode & 0o111 != 0, "Orca 워크북 실행 스크립트가 실행 가능하지 않음")

    server = SERVER_SCRIPT.read_text(encoding="utf-8")
    launcher = OPEN_ORCA_SCRIPT.read_text(encoding="utf-8")
    readme = (ROOT / "README.md").read_text(encoding="utf-8")
    compile(server, str(SERVER_SCRIPT), "exec")

    for contract in (
        'HOST = "127.0.0.1"',
        "PORT = 4174",
        'URI_PREFIX = f"/{REPO_ROOT.name}"',
        'PUBLIC_DIRECTORIES = {"practice"}',
        "if not parsed.path.startswith(allowed_prefix)",
        'part.startswith(".")',
        "HTTPStatus.NOT_FOUND",
        "def list_directory",
    ):
        require(contract in server, f"폴더 이름 URI 서버 계약 누락: {contract}")

    for contract in (
        'workbook_url="http://127.0.0.1:4174/$repo_name/"',
        "/Applications/Orca.app/Contents/Resources/bin/orca",
        "python3 scripts/serve_workbook.py",
        "terminal create",
        "tab create",
        '--worktree "path:$repo_root"',
        "<title>Company Work System 취업 워크북</title>",
    ):
        require(contract in launcher, f"Orca 실행 계약 누락: {contract}")

    result = subprocess.run(
        ["sh", "-n", str(OPEN_ORCA_SCRIPT)],
        text=True,
        capture_output=True,
        check=False,
    )
    require(result.returncode == 0, f"Orca 실행 스크립트 구문 오류\n{result.stderr.strip()}")
    for contract in (
        "./scripts/open_workbook_in_orca.sh",
        "http://127.0.0.1:4174/company-work-system/",
    ):
        require(contract in readme, f"README Orca 실행 안내 누락: {contract}")


def main() -> int:
    require(Path.cwd().resolve() == ROOT, "저장소 루트에서 실행해야 합니다")
    parsers = {path: parse_html(path) for path in HTML_FILES}
    validate_document_icons()
    html_links = validate_html_links(parsers)
    markdown_links = validate_markdown_links()
    index = (ROOT / "index.html").read_text(encoding="utf-8")
    validate_index_contract(index, parsers[ROOT / "index.html"])
    program_folders, program_files = validate_program_folders(parsers[ROOT / "index.html"])
    history = (ROOT / "history.html").read_text(encoding="utf-8")
    history_items = validate_history_contract(history)
    validate_orca_launch_contract()

    print("워크북 검증 통과")
    print(f"- HTML 2개와 로컬 링크 {html_links}개")
    print(f"- Markdown 상대 링크 {markdown_links}개")
    print(f"- 내부 목표·계약과 HTML 연결이 있는 프로그램 폴더 {program_folders}개·학습 파일 {program_files}개")
    print("- 학습 목차 6단계, 취업 로드맵 5단계, 취업 결과물 계약이 있는 챕터 40개, 세션 체크 5개")
    print("- 선택 챕터별 답안 저장, 힌트·정답 기준 토글과 40개 문제 비교 완료 기록")
    print("- 00~39 이전·다음 연속 탐색과 챕터 번호별 프로그램 폴더 대응")
    print(f"- 실제 Git 커밋에 연결된 대표 변경 이력 {history_items}개")
    print("- JavaScript 구문·DOM ID 계약과 920px·680px 반응형 분기")
    print("- 127.0.0.1:4174 폴더 이름 URI 서버와 Orca 실행 계약")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (AssertionError, OSError) as error:
        print(f"워크북 검증 실패: {error}", file=sys.stderr)
        raise SystemExit(1)
