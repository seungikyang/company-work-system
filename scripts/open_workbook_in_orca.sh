#!/bin/sh
# 중학생 눈높이: 서버가 준비되었는지 확인한 다음 Orca에서 워크북을 여는 순서표입니다.
# 셸 스크립트는 터미널 명령을 위에서 아래로 실행합니다. 실패하면 멈추고 이유를 알려 줍니다.
# 고정 폴더 URI의 취업 워크북을 Orca 브라우저 탭으로 여는 실행 스크립트
# 초보자 안내: 서버 확인과 브라우저 열기를 순서대로 연결한 셸 스크립트입니다.
# curl로 워크북 응답 확인 → 필요하면 서버 시작 → 준비 확인 → 기존 탭 재사용 또는 새 탭 생성 순으로 읽습니다.
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

# curl로 화면을 요청하고 제목을 확인합니다. 포트가 열렸다는 사실뿐 아니라 우리 워크북인지도 확인합니다.
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
  # while은 조건이 맞는 동안 반복합니다. 서버 준비를 짧게 기다리되 정해진 횟수를 넘으면 중단합니다.
  while ! workbook_is_ready 2>/dev/null; do
    attempt=$((attempt + 1))
    if [ "$attempt" -ge 25 ]; then
      echo "고정 URI 서버가 제한 시간 안에 준비되지 않았습니다: $workbook_url" >&2
      exit 1
    fi
    sleep 0.2
  done
fi

# 이미 열린 탭이 있으면 다시 사용하고, 없으면 새 탭을 만듭니다. 같은 화면을 계속 늘리지 않습니다.
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
