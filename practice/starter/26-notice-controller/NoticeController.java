// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/NoticeController.java
// 목표: 공지사항 REST API + DTO. TRD 3.7.5 참고.

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

// 학습 질문:
// Q1. GET 상세에서 조회수를 올리는 패턴의 문제와 대안은?
//     A:
// Q2. PUT 과 PATCH 중 공지 수정에 어울리는 것은? PRD 기준은?
//     A:
// Q3. 공지 본문(content)을 응답 list 에 그대로 포함하면 어떤 단점이 있을까? (페이로드 크기)
//     A:
