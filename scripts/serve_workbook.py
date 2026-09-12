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
HOST = "127.0.0.1"
PORT = 4174
URI_PREFIX = f"/{REPO_ROOT.name}"
WORKBOOK_URL = f"http://{HOST}:{PORT}{URI_PREFIX}/"
PUBLIC_ROOT_FILES = {
    "index.html",
    "history.html",
    "problems.html",
    "README.md",
    "company_work_system_PRD_TRD.md",
    "workbook-viewer.html",
}
PUBLIC_DIRECTORIES = {"practice"}
TEXT_VIEWER_SUFFIXES = {".fragment", ".java", ".md"}


class WorkbookRequestHandler(SimpleHTTPRequestHandler):
    extensions_map = {
        **SimpleHTTPRequestHandler.extensions_map,
        ".fragment": "text/plain; charset=utf-8",
        ".java": "text/plain; charset=utf-8",
        ".md": "text/plain; charset=utf-8",
    }

    def __init__(self, *args: object, **kwargs: object) -> None:
        super().__init__(*args, directory=str(REPO_ROOT), **kwargs)

    def end_headers(self) -> None:
        self.send_header("Cache-Control", "no-cache")
        super().end_headers()

    # URL의 폴더 접두사와 공개 경로를 검사합니다. 거절할 때 False를 반환하면 호출자가 응답 처리를 중단합니다.
    def prepare_workbook_path(self) -> bool:
        parsed = urlsplit(self.path)
        if parsed.path == URI_PREFIX:
            self.send_response(HTTPStatus.PERMANENT_REDIRECT)
            self.send_header("Location", f"{URI_PREFIX}/")
            self.end_headers()
            return False

        allowed_prefix = f"{URI_PREFIX}/"
        if not parsed.path.startswith(allowed_prefix):
            self.send_error(HTTPStatus.NOT_FOUND)
            return False

        decoded_path = unquote(parsed.path[len(allowed_prefix) :])
        path_parts = PurePosixPath(decoded_path).parts
        if any(part in {".", ".."} or part.startswith(".") for part in path_parts):
            self.send_error(HTTPStatus.NOT_FOUND)
            return False
        if path_parts:
            first_part = path_parts[0]
            is_public = (
                len(path_parts) == 1 and first_part in PUBLIC_ROOT_FILES
            ) or first_part in PUBLIC_DIRECTORIES
            if not is_public:
                self.send_error(HTTPStatus.NOT_FOUND)
                return False

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
        self.send_response(HTTPStatus.FOUND)
        self.send_header("Location", viewer_url)
        self.end_headers()
        return True

    def do_GET(self) -> None:
        if not self.prepare_workbook_path():
            return
        if self.redirect_document_to_viewer():
            return
        super().do_GET()

    # HEAD는 GET처럼 상태·헤더를 확인하되 본문은 받지 않는 요청입니다. 같은 경로 검사를 거칩니다.
    def do_HEAD(self) -> None:
        if not self.prepare_workbook_path():
            return
        if self.redirect_document_to_viewer():
            return
        super().do_HEAD()

    def list_directory(self, path: str) -> None:
        self.send_error(HTTPStatus.NOT_FOUND)
        return None

    def log_message(self, message: str, *args: object) -> None:
        print(f"[workbook] {self.address_string()} {message % args}", flush=True)


def main() -> None:
    with ThreadingHTTPServer((HOST, PORT), WorkbookRequestHandler) as server:
        print(f"취업 워크북 서버 실행: {WORKBOOK_URL}", flush=True)
        server.serve_forever()


if __name__ == "__main__":
    main()
