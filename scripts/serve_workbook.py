# 중학생 눈높이: 서버는 브라우저의 파일 요청에 답하는 배달 담당입니다.
# 이 서버는 HTML·코드·학습 문서를 전달합니다. 풀이 노트는 브라우저가 별도로 보관합니다.
# 저장소 폴더 이름 URI로 취업 워크북만 제공하는 로컬 HTTP 서버
# 초보자 안내: Python 표준 라이브러리로 학습 파일을 HTTP 응답으로 제공하는 서버입니다.
# 브라우저 fetch가 파일을 읽을 수 있도록 사용하며 목표·답안을 DB에 저장하는 백엔드는 아닙니다.
# 읽는 순서: main → do_GET → 경로 검사 → 읽기 화면 리다이렉트 또는 원문 응답.
from __future__ import annotations

from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path, PurePosixPath
from urllib.parse import parse_qs, quote, unquote, urlsplit, urlunsplit


# __file__은 현재 스크립트의 경로입니다. 두 단계 위를 기준으로 파일을 제공하고 127.0.0.1에서 로컬 요청을 받습니다.
REPO_ROOT = Path(__file__).resolve().parent.parent
# 127.0.0.1은 지금 사용하는 컴퓨터의 주소입니다. PORT는 여러 프로그램을 구별하는 문 번호입니다.
# 왜 0.0.0.0이 아닐까요? 0.0.0.0이면 같은 와이파이의 다른 기기도 접속할 수 있습니다. 개인 학습 자료라 내 컴퓨터에서만 열리게 합니다.
HOST = "127.0.0.1"
# 왜 4174처럼 흔하지 않은 번호일까요? 3000·8080 같은 번호는 다른 개발 서버가 자주 쓰므로 부딪히지 않게 골랐습니다.
# 번호를 고정해 두면 주소가 늘 같아서 브라우저 저장 기록(localStorage)도 같은 칸에 계속 쌓입니다.
PORT = 4174
# 왜 주소 앞에 폴더 이름을 붙일까요? 주소만 보고도 어느 저장소의 워크북인지 알 수 있고,
# 이 이름표로 시작하지 않는 요청은 아래에서 모두 거절해 열어 둘 범위를 좁힐 수 있습니다.
URI_PREFIX = f"/{REPO_ROOT.name}"
WORKBOOK_URL = f"http://{HOST}:{PORT}{URI_PREFIX}/"
# 전달할 수 있는 파일의 목록입니다. 프로그램 내부 폴더까지 전부 공개하지 않습니다.
PUBLIC_ROOT_FILES = {
    "index.html",
    "history.html",
    "problems.html",
    "fundamentals.html",
    "quiz.html",
    "README.md",
    "company_work_system_PRD_TRD.md",
    "workbook-viewer.html",
}
PUBLIC_DIRECTORIES = {"practice"}
TEXT_VIEWER_SUFFIXES = {".fragment", ".java", ".md"}


# SimpleHTTPRequestHandler는 파이썬이 기본으로 주는 '폴더 속 파일을 그대로 보내는 서버'입니다.
# 왜 처음부터 만들지 않고 물려받을까요? 파일 읽기·응답 형식 같은 어려운 부분은 검증된 기본 기능에 맡기고,
# 우리는 '어떤 파일을 보여 줄지' 검사하는 부분만 덧붙이면 되기 때문입니다.
class WorkbookRequestHandler(SimpleHTTPRequestHandler):
    # 왜 .java·.md를 text/plain으로 알릴까요? 파이썬이 모르는 확장자는 '알 수 없는 파일'로 보내, 브라우저가 내려받기 창을 띄웁니다.
    # charset=utf-8을 함께 적어야 한글 주석이 깨지지 않습니다.
    extensions_map = {
        **SimpleHTTPRequestHandler.extensions_map,
        ".fragment": "text/plain; charset=utf-8",
        ".java": "text/plain; charset=utf-8",
        ".md": "text/plain; charset=utf-8",
    }

    # 왜 directory를 직접 줄까요? 주지 않으면 '명령을 실행한 폴더'를 기준으로 파일을 찾아, 다른 폴더에서 실행하면 엉뚱한 파일이 보입니다.
    def __init__(self, *args: object, **kwargs: object) -> None:
        super().__init__(*args, directory=str(REPO_ROOT), **kwargs)

    # 응답 헤더는 본문을 읽는 방법 같은 안내표입니다. no-cache는 다시 쓰기 전에 서버에 확인하라는 뜻입니다.
    # 왜 필요할까요? 학습 파일을 고친 뒤 새로고침해도 브라우저가 예전 사본을 보여 주면, 고친 내용이 반영 안 된 줄 알고 헷갈립니다.
    def end_headers(self) -> None:
        self.send_header("Cache-Control", "no-cache")
        super().end_headers()

    # URL의 폴더 접두사와 공개 경로를 검사합니다. 거절할 때 False를 반환하면 호출자가 응답 처리를 중단합니다.
    def prepare_workbook_path(self) -> bool:
        # 브라우저가 보낸 주소를 경로와 물음표 뒤의 요청 조건으로 나누어 확인합니다.
        parsed = urlsplit(self.path)
        # 왜 끝에 /를 붙여 다시 보낼까요? '/company-work-system'처럼 /가 없으면 브라우저가 './practice/...' 같은 상대 주소를
        # 한 단계 위 폴더 기준으로 계산해 CSS·스크립트를 못 찾습니다.
        if parsed.path == URI_PREFIX:
            self.send_response(HTTPStatus.PERMANENT_REDIRECT)
            self.send_header("Location", f"{URI_PREFIX}/")
            self.end_headers()
            return False

        allowed_prefix = f"{URI_PREFIX}/"
        if not parsed.path.startswith(allowed_prefix):
            self.send_error(HTTPStatus.NOT_FOUND)
            return False

        # 주소의 특수 문자 표시를 원래 글자로 바꾼 뒤 숨김 폴더나 상위 경로 요청인지 검사합니다.
        # 왜 원래 글자로 바꾼 뒤 검사할까요? '..'을 '%2e%2e'처럼 숨겨 보내면 바꾸기 전 글자만 보고는 알아챌 수 없기 때문입니다.
        decoded_path = unquote(parsed.path[len(allowed_prefix) :])
        path_parts = PurePosixPath(decoded_path).parts
        # '..'은 위 폴더로 올라가 공개하지 않은 파일에 닿을 수 있고, '.'으로 시작하는 .git·.env 같은 폴더에는 비밀 정보가 있을 수 있어 모두 막습니다.
        # 왜 403(금지)이 아니라 404(없음)일까요? '그 파일이 있긴 하다'는 힌트조차 주지 않으려는 것입니다.
        if any(part in {".", ".."} or part.startswith(".") for part in path_parts):
            self.send_error(HTTPStatus.NOT_FOUND)
            return False
        if path_parts:
            first_part = path_parts[0]
            # 왜 '허용 목록'일까요? '막을 목록'은 새로 생긴 비밀 파일을 깜빡 빠뜨릴 수 있지만, 허용 목록은 적어 둔 것만 열어 더 안전합니다.
            # 루트 파일은 이름이 목록에 있을 때만, practice 폴더는 그 안 전체를 엽니다.
            is_public = (
                len(path_parts) == 1 and first_part in PUBLIC_ROOT_FILES
            ) or first_part in PUBLIC_DIRECTORIES
            if not is_public:
                self.send_error(HTTPStatus.NOT_FOUND)
                return False

        # 검사를 마치면 앞의 '/company-work-system'을 떼어 냅니다. 기본 서버는 저장소 폴더를 '/'로 보므로 이 이름표를 모르기 때문입니다.
        relative_path = parsed.path[len(URI_PREFIX) :] or "/"
        self.path = urlunsplit(("", "", relative_path, parsed.query, ""))
        return True

    # 리다이렉트는 브라우저에 다른 주소로 이동하라고 응답하는 것입니다. raw=1 원문 요청에는 적용하지 않아
    # 읽기 화면이 다시 자기 자신을 요청하는 순환을 피합니다.
    def redirect_document_to_viewer(self) -> bool:
        parsed = urlsplit(self.path)
        if Path(parsed.path).suffix.lower() not in TEXT_VIEWER_SUFFIXES:
            return False
        if parse_qs(parsed.query).get("raw") == ["1"]:
            return False

        source_file = parsed.path.lstrip("/")
        if source_file == "practice/problems.md":
            viewer_url = f"{URI_PREFIX}/problems.html"
        else:
            viewer_url = (
                f"{URI_PREFIX}/workbook-viewer.html"
                f"?file={quote(source_file, safe='/')}"
            )
        # 왜 302(FOUND, 임시 이동)일까요? 308(영구 이동)이면 브라우저가 그 이동을 기억해 버려,
        # 나중에 원문 주소 동작을 바꿔도 예전 이동이 계속 적용될 수 있습니다.
        self.send_response(HTTPStatus.FOUND)
        self.send_header("Location", viewer_url)
        self.end_headers()
        return True

    # GET은 파일 내용을 보내 달라는 요청입니다. 주소를 확인하고 필요하면 읽기 화면으로 안내합니다.
    def do_GET(self) -> None:
        if not self.prepare_workbook_path():
            return
        if self.redirect_document_to_viewer():
            return
        super().do_GET()

    # HEAD는 GET처럼 상태·헤더를 확인하되 본문은 받지 않는 요청입니다. 같은 경로 검사를 거칩니다.
    # 왜 HEAD에도 검사가 필요할까요? 검사를 빼먹으면 '이 비공개 파일이 있는지·크기가 얼마인지'를 HEAD로 알아낼 수 있기 때문입니다.
    def do_HEAD(self) -> None:
        if not self.prepare_workbook_path():
            return
        if self.redirect_document_to_viewer():
            return
        super().do_HEAD()

    # 폴더만 요청했을 때 전체 파일 목록을 보여 주지 않습니다. 공개한 자료를 정확한 주소로 읽게 합니다.
    def list_directory(self, path: str) -> None:
        self.send_error(HTTPStatus.NOT_FOUND)
        return None

    # 요청 기록 앞에 [workbook]을 붙여 다른 터미널 출력과 구분합니다.
    # flush=True는 출력을 모아 두지 않고 바로 보여 주라는 뜻입니다. 그래야 Orca 터미널에서 요청이 실시간으로 보입니다.
    def log_message(self, message: str, *args: object) -> None:
        print(f"[workbook] {self.address_string()} {message % args}", flush=True)


# 왜 Threading(여러 일꾼) 서버일까요? 기본 서버는 요청을 한 번에 하나씩만 처리해서, 퀴즈 화면처럼 파일 10여 개를 한꺼번에 요청하면
# 앞 요청이 끝날 때까지 나머지가 줄을 서느라 화면이 느리게 뜹니다.
class WorkbookHTTPServer(ThreadingHTTPServer):
    # 객관식 화면의 CSS·문항 묶음·스크립트가 동시에 연결되어도 수용합니다.
    # 왜 32일까요? 기본값(5)보다 크면 한꺼번에 몰린 요청이 '연결 거절'되지 않습니다. 개인용이라 이 정도면 충분합니다.
    request_queue_size = 32


# main은 서버 시작 지점입니다. 정해진 주소와 포트에서 요청을 기다리고, Ctrl+C로 종료할 수 있습니다.
def main() -> None:
    with WorkbookHTTPServer((HOST, PORT), WorkbookRequestHandler) as server:
        print(f"취업 워크북 서버 실행: {WORKBOOK_URL}", flush=True)
        server.serve_forever()


if __name__ == "__main__":
    main()
