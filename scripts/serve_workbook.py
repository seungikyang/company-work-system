# 저장소 폴더 이름 URI로 취업 워크북만 제공하는 로컬 HTTP 서버
from __future__ import annotations

from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path, PurePosixPath
from urllib.parse import parse_qs, quote, unquote, urlsplit, urlunsplit


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
