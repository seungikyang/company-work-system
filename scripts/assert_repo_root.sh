#!/bin/sh
# 중학생 눈높이: 파일을 고치기 전에 올바른 작업 폴더에 있는지 확인하는 문지기입니다.
# $변수는 기억해 둔 값을 꺼내는 표시이고, $(명령)은 그 명령의 결과를 변수에 넣는 표현입니다.
# 저장소 상태 변경 전에 실제 루트와 임시 경로 금지를 검사하는 가드
# 초보자 안내: 셸은 명령을 순서대로 실행합니다. -e는 처리되지 않은 명령 실패 시, -u는 미정의 변수 사용 시 중단합니다.
# pwd -P는 실제 경로를 확인합니다. 다른 복사본을 수정하지 않도록 Git 루트와 비교하고 금지된 임시 경로를 거절합니다.
set -eu

repo_root=$(git rev-parse --show-toplevel)
# 왜 pwd -P로 한 번 더 바꿀까요? 바로가기(심볼릭 링크) 경로로 들어오면 같은 폴더인데도 글자가 달라 비교가 틀어집니다.
# -P는 바로가기를 풀어 '진짜 위치'를 알려 주므로 두 경로를 공정하게 비교할 수 있습니다.
physical_root=$(cd "$repo_root" && pwd -P)
physical_pwd=$(pwd -P)

# 현재 폴더와 실제 저장소 루트가 같아야 통과합니다. 다른 복사본을 실수로 고치는 일을 막습니다.
if [ "$physical_pwd" != "$physical_root" ]; then
  echo "저장소 루트에서 실행해야 합니다: $physical_root" >&2
  exit 1
fi

# case는 값이 어떤 모양에 맞는지 비교합니다. 임시 폴더 아래의 복사본에서는 작업을 막습니다.
# 왜 임시 폴더를 막을까요? 임시 폴더는 재부팅·정리 때 통째로 지워질 수 있어, 거기서 한 작업은 예고 없이 사라집니다.
# 끝에 /를 붙여 비교하는 이유: '/tmpfoo' 같은 비슷한 이름의 폴더가 '/tmp'로 잘못 걸리지 않게 하려는 것입니다.
# macOS에서는 /tmp가 사실 /private/tmp의 바로가기라 두 경로를 모두 적었습니다.
case "$physical_root/" in
  /tmp/*|/private/tmp/*)
    echo "임시 경로의 clone이나 worktree에서는 작업할 수 없습니다: $physical_root" >&2
    exit 1
    ;;
esac

# ${TMPDIR:-}는 TMPDIR가 없으면 빈 글자로 바꿔 줍니다. 왜? 위의 set -u 때문에 없는 변수를 그냥 쓰면 스크립트가 멈추기 때문입니다.
if [ -n "${TMPDIR:-}" ]; then
  # || true는 TMPDIR 폴더로 이동하지 못해도 여기서 멈추지 않게 합니다. 그 경우 이 검사만 건너뜁니다.
  physical_tmp=$(cd "$TMPDIR" 2>/dev/null && pwd -P || true)
  if [ -n "$physical_tmp" ]; then
    case "$physical_root/" in
      "$physical_tmp"/*)
        echo "TMPDIR 아래의 clone이나 worktree에서는 작업할 수 없습니다: $physical_root" >&2
        exit 1
        ;;
    esac
  fi
fi

# Codex 같은 도구가 자동으로 만든 작업 복사본도 막습니다. 왜? 그곳에서 고친 내용은 진짜 저장소에 반영되지 않아 작업이 둘로 갈라지기 때문입니다.
codex_worktrees="$HOME/.codex/worktrees"
case "$physical_root/" in
  "$codex_worktrees"/*)
    echo "Codex 임시 worktree에서는 작업할 수 없습니다: $physical_root" >&2
    exit 1
    ;;
esac

printf '저장소 루트 확인: %s\n' "$physical_root"
