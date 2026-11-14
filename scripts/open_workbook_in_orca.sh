#!/bin/sh
# 중학생 눈높이: 서버가 준비되었는지 확인한 다음 Orca에서 워크북을 여는 순서표입니다.
# 셸 스크립트는 터미널 명령을 위에서 아래로 실행합니다. 실패하면 멈추고 이유를 알려 줍니다.
# 고정 폴더 URI의 취업 워크북을 Orca 브라우저 탭으로 여는 실행 스크립트
# 초보자 안내: 서버 확인과 브라우저 열기를 순서대로 연결한 셸 스크립트입니다.
# curl로 워크북 응답 확인 → 필요하면 서버 시작 → 준비 확인 → 기존 탭 재사용 또는 새 탭 생성 순으로 읽습니다.
set -eu

# 이 스크립트 파일이 있는 폴더를 찾습니다. 왜? 어느 폴더에서 실행하든 저장소를 정확히 찾기 위해서입니다.
# CDPATH=를 비우는 이유: 사용자가 CDPATH를 설정해 두면 cd가 다른 폴더로 가거나 경로를 출력해 결과가 오염될 수 있습니다.
# --는 '여기부터는 옵션이 아니라 경로'라는 표시로, 폴더 이름이 -로 시작해도 안전합니다.
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
# --fail은 404 같은 오류 응답을 실패로 처리하고, --max-time 2는 2초 안에 답이 없으면 포기해 스크립트가 멈춰 있지 않게 합니다.
workbook_is_ready() {
  curl --fail --silent --show-error --max-time 2 "$workbook_url" |
    grep --quiet "<title>Company Work System 취업 워크북</title>"
}

"$orca_cli" open --json >/dev/null

if ! workbook_is_ready 2>/dev/null; then
  # 워크북은 아닌데 4174 문이 이미 열려 있다면 다른 프로그램이 쓰고 있는 것입니다.
  # 왜 다른 번호로 바꿔 띄우지 않을까요? 주소가 바뀌면 브라우저 저장 기록(localStorage)이 다른 칸으로 가서 지금까지의 풀이가 안 보입니다.
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
  # 왜 바로 탭을 열지 않을까요? 서버가 켜지는 데 잠깐 걸려서, 너무 빨리 열면 '연결할 수 없음' 화면이 먼저 뜹니다.
  # 0.2초씩 최대 25번(약 5초+확인 시간)만 기다립니다. 끝없이 기다리면 서버가 고장 났을 때 스크립트가 영영 끝나지 않습니다.
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
  # 왜 이동한 뒤 새로고침까지 할까요? 이미 같은 주소였다면 이동만으로는 화면이 바뀌지 않아, 고친 파일이 반영되지 않기 때문입니다.
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
