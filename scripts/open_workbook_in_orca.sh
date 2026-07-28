#!/bin/sh
# 고정 폴더 URI의 취업 워크북을 Orca 브라우저 탭으로 여는 실행 스크립트
set -eu

script_dir=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
repo_root=$(git -C "$script_dir/.." rev-parse --show-toplevel)
repo_name=$(basename "$repo_root")
workbook_url="http://127.0.0.1:4174/$repo_name/"
orca_cli="/Applications/Orca.app/Contents/Resources/bin/orca"
worktree_selector="path:$repo_root"

cd "$repo_root"
./scripts/assert_repo_root.sh >/dev/null

if [ ! -x "$orca_cli" ]; then
  echo "Orca 공식 CLI를 찾을 수 없습니다: $orca_cli" >&2
  exit 1
fi

workbook_is_ready() {
  curl --fail --silent --show-error --max-time 2 "$workbook_url" |
    grep --quiet "<title>Company Work System 취업 워크북</title>"
}

"$orca_cli" open --json >/dev/null

if ! workbook_is_ready 2>/dev/null; then
  if lsof -nP -iTCP:4174 -sTCP:LISTEN >/dev/null 2>&1; then
    echo "4174 포트를 다른 서버가 사용 중이라 고정 URI를 시작할 수 없습니다." >&2
    exit 1
  fi

  "$orca_cli" terminal create \
    --worktree "$worktree_selector" \
    --title "취업 워크북 서버 · 4174" \
    --command "python3 scripts/serve_workbook.py" \
    --json >/dev/null

  attempt=0
  while ! workbook_is_ready 2>/dev/null; do
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 25 ]; then
      echo "고정 URI 서버가 제한 시간 안에 준비되지 않았습니다: $workbook_url" >&2
      exit 1
    fi
    sleep 0.2
  done
fi

if "$orca_cli" tab current --worktree "$worktree_selector" --json >/dev/null 2>&1; then
  "$orca_cli" goto \
    --url "$workbook_url" \
    --worktree "$worktree_selector" \
    --json >/dev/null
  "$orca_cli" reload \
    --worktree "$worktree_selector" \
    --json >/dev/null
else
  "$orca_cli" tab create \
    --url "$workbook_url" \
    --worktree "$worktree_selector" \
    --json >/dev/null
fi

printf 'Orca에서 취업 워크북을 열었습니다: %s\n' "$workbook_url"
