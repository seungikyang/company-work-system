# 저장소 폴더 이름 URI로 취업 워크북만 제공하는 로컬 HTTP 서버
from __future__ import annotations

from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path, PurePosixPath
from urllib.parse import unquote, urlsplit, urlunsplit


REPO_ROOT = Path(__file__).resolve().parent.parent
HOST = "127.0.0.1"
PORT = 4174
URI_PREFIX = f"/{REPO_ROOT.name}"
WORKBOOK_URL = f"http://{HOST}:{PORT}{URI_PREFIX}/"
PUBLIC_ROOT_FILES = {
    "index.html",
    "history.html",
    "README.md",
    "company_work_system_PRD_TRD.md",
}
PUBLIC_DIRECTORIES = {"practice"}


class WorkbookRequestHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args: object, **kwargs: object) -> None:
        super().__init__(*args, directory=str(REPO_ROOT), **kwargs)

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

    def do_GET(self) -> None:
        if self.prepare_workbook_path():
            super().do_GET()

    def do_HEAD(self) -> None:
        if self.prepare_workbook_path():
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
