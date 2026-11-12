// 중학생 눈높이: 공지 요청 창구
// 공지를 읽는 요청과 관리자가 공지를 쓰는 요청을 받습니다.
// 누구나 읽는 기능과 관리자만 바꾸는 기능은 권한 규칙을 따로 확인합니다.
// 공지 번호·입력 읽기 → 필요한 권한 확인 → Service 호출 → 응답 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/NoticeController.java
// 목표: 공지사항 REST API + DTO. TRD 3.7.5 참고.

// 초보자 안내 (26장): 공지 API와 역할 검사
// 개념: 공지 Controller는 목록·상세·관리 요청의 입구입니다.
// 사용 이유: 조회 기능과 관리자 편집 기능에 다른 접근 기준을 적용합니다.
// 동작 흐름: 경로·본문·현재 사용자 정보를 받아 공지 Service로 넘깁니다. 화면에서 편집 버튼을 숨겨도 직접 요청할 수 있으므로 서버에서 역할을 검사해야 합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // TODO 01: 공지 등록은 ADMIN 만. 응답 상태 코드는?
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

    // TODO 02: 공지 목록 — 로그인 사용자라면 모두 조회 가능. 중요 공지를 위로 끌어올리는 정렬은 Service 가 처리합니다.
    @GetMapping
    public Page<NoticeResponse> list(@PageableDefault(size = 20) ____ pageable) {
        return noticeService.list(pageable);
    }

    // TODO 03: 공지 상세. 조회수가 올라야 한다면 어떤 HTTP 메서드를 사용하는 게 의미상 더 적절한가요?
    //          (GET 이지만 사이드이펙트가 있는 경우의 일반적 합의는?)
    @GetMapping("/{noticeId}")
    // 상세 조회는 Service의 view를 호출하고 그 안에서 조회 수가 바뀝니다.
    // HTTP GET이라는 이름만 보지 말고 이어지는 Service에서 DB 변경 여부를 확인하세요.
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
    public ResponseEntity<Void> delete(
            @CurrentUserRole UserRole role,
            @PathVariable Long noticeId) {
        noticeService.delete(role, noticeId);
        return ResponseEntity.noContent().build();
    }
}


// ===== DTO =====
public record NoticeCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        boolean important
) { }

public record NoticeUpdateRequest(
        @Size(max = 200) String title,
        String content,
        boolean important
) { }

public record NoticeResponse(
        Long id, String title, String content, Long writerId,
        boolean important, long viewCount,
        LocalDateTime createdAt, LocalDateTime updatedAt
) {
    public static NoticeResponse from(Notice n) {
        return new NoticeResponse(
                n.getId(), n.getTitle(), n.getContent(), n.getWriterId(),
                n.isImportant(), n.getViewCount(),
                n.getCreatedAt(), n.getUpdatedAt());
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
//
// 심화 노트 (면접 답변 포인트):
// - 목록 vs 상세 DTO 분리: 목록에 content(TEXT)까지 넣으면 페이로드가 폭증한다 → 목록은 title/important/createdAt 요약 DTO,
//   상세만 풀 DTO. NoticeResponse 를 목록에도 쓰는 현재 코드는 학습용 단순화.
// - @PageableDefault(size=20): 클라이언트가 size 를 빠뜨려도 안전한 기본값. 무제한 size 로 전체를 끌어오는 사고 방지.
// - GET 상세의 조회수 증가는 GET 의 멱등성(부수효과 없음) 원칙과 충돌 → PATCH /{id}/view 분리 또는 중복집계 방지.
// - 공지 수정은 PRD 상 PUT. 작성자 무관하게 ADMIN 권한이 수정의 핵심 기준.
