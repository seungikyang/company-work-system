// 중학생 눈높이: 공지 요청 창구
// 공지를 읽는 요청과 관리자가 공지를 쓰는 요청을 받습니다.
// 누구나 읽는 기능과 관리자만 바꾸는 기능은 권한 규칙을 따로 확인합니다.
// 공지 번호·입력 읽기 → 필요한 권한 확인 → Service 호출 → 응답 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: controller/NoticeController.java
// 목표: 공지 REST API + DTO 를 거의 백지에서. TRD 3.7.5.
// 막히면 starter/26-notice-controller, answers.md 26장 참고.

// 초보자 안내 (26장): 공지 API와 역할 검사
// 개념: 공지 Controller는 목록·상세·관리 요청의 입구입니다.
// 사용 이유: 조회 기능과 관리자 편집 기능에 다른 접근 기준을 적용합니다.
// 동작 흐름: 경로·본문·현재 사용자 정보를 받아 공지 Service로 넘깁니다. 화면에서 편집 버튼을 숨겨도 직접 요청할 수 있으므로 서버에서 역할을 검사해야 합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // TODO 01: 등록 ADMIN, 생성 status.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NoticeResponse> create(
            @CurrentUser Long currentUserId,
            @CurrentUserRole UserRole role,
            @Valid @RequestBody NoticeCreateRequest request) {
        NoticeResponse response = noticeService.create(currentUserId, role, request);
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 02: 목록 — 기본 페이지 크기.
    @GetMapping
    public Page<NoticeResponse> list(@PageableDefault(size = 20) ____ pageable) {
        return noticeService.list(pageable);
    }

    @GetMapping("/{noticeId}")
    public NoticeResponse detail(@PathVariable Long noticeId) {
        return noticeService.view(noticeId);
    }

    @PutMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public NoticeResponse update(
            @CurrentUserRole UserRole role,
            @PathVariable Long noticeId,
            @Valid @RequestBody NoticeUpdateRequest request) {
        return noticeService.update(role, noticeId, request);
    }

    @DeleteMapping("/{noticeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@CurrentUserRole UserRole role, @PathVariable Long noticeId) {
        noticeService.delete(role, noticeId);
        return ResponseEntity.noContent().build();
    }
}

// ===== DTO =====
public record NoticeCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        boolean important) { }

public record NoticeUpdateRequest(@Size(max = 200) String title, String content, boolean important) { }

public record NoticeResponse(
        Long id, String title, String content, Long writerId,
        boolean important, long viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static NoticeResponse from(Notice n) {
        return new NoticeResponse(
                n.getId(), n.getTitle(), n.getContent(), n.getWriterId(),
                n.isImportant(), n.getViewCount(), n.getCreatedAt(), n.getUpdatedAt());
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-26
//
// Q1. 공지 GET 상세의 조회수 증가 부작용을 검토한 설명으로 알맞은 것은? [chapter-v1-26-04]
// ① GET 요청은 사람이 버튼을 누를 때만 발생한다.
// ② GET 이름이면 DB의 증가 SQL이 읽기 전용으로 바뀐다.
// ③ 봇·미리보기·재시도로 증가할 수 있어 조회수 기록과 중복 집계 정책을 분리할 수 있다.
// ④ 요청 메서드만 바꾸면 봇 탐지가 자동 완성된다.
// 정답: ③
// 해설: HTTP 조회 의도와 집계 부작용을 구분한다. 이 실습의 구현과 개선 대안을 함께 이해한다.
//
// Q2. 이 실습의 공지 수정 메서드와 부분 변경의 관계를 올바르게 설명한 것은? [chapter-v1-26-05]
// ① 부분 수정 코드를 썼으므로 @PutMapping도 자동으로 PATCH가 된다.
// ② 현재 @PutMapping 계약은 확인하고 부분 변경으로 설계할 경우 PATCH와 필드 의미를 함께 조정한다.
// ③ GET으로 본문을 변경하는 것이 이 실습의 수정 계약이다.
// ④ PUT이면 기존 본문과 생략 필드의 의미를 확인하지 않아도 된다.
// 정답: ②
// 해설: 현재 PRD·Controller의 PUT 계약과 부분 변경 설계의 대안을 구분한다. 계약 변경 시 구현·문서·테스트를 맞춘다.
//
// Q3. 공지 목록 응답에 모든 content를 넣으면 발생할 수 있는 비용은? [chapter-v1-26-06]
// ① 각 본문 길이만큼 응답량·전송·렌더링 비용이 커져 요약 목록과 상세 응답을 분리할 수 있다.
// ② 본문을 넣으면 모든 조회가 반드시 한 SQL로 줄어든다.
// ③ content가 길어도 JSON 응답 크기는 항상 같다.
// ④ 본문을 생략하면 권한 검사가 자동 완료된다.
// 정답: ①
// 해설: 목록에 필요한 공개 필드만 전달하고 본문은 상세 조회로 제공하는 방식을 규모에 맞춰 검토한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ CREATED  □ Pageable  □ 목록 요약 DTO 권장  □ GET 멱등성 vs 조회수
