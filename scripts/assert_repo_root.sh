#!/bin/sh
# 저장소 상태 변경 전에 실제 루트와 임시 경로 금지를 검사하는 가드
set -eu

repo_root=$(git rev-parse --show-toplevel)
physical_root=$(cd "$repo_root" && pwd -P)
physical_pwd=$(pwd -P)

if [ "$physical_pwd" != "$physical_root" ]; then
  echo "저장소 루트에서 실행해야 합니다: $physical_root" >&2
  exit 1
fi

case "$physical_root/" in
  /tmp/*|/private/tmp/*)
    echo "임시 경로의 clone이나 worktree에서는 작업할 수 없습니다: $physical_root" >&2
    exit 1
    ;;
esac

if [ -n "${TMPDIR:-}" ]; then
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

codex_worktrees="$HOME/.codex/worktrees"
case "$physical_root/" in
  "$codex_worktrees"/*)
    echo "Codex 임시 worktree에서는 작업할 수 없습니다: $physical_root" >&2
    exit 1
    ;;
esac

printf '저장소 루트 확인: %s\n' "$physical_root"
