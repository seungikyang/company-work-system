// 실제 구현 위치 예: controller/NoticeController.java
// 목표: 공지 REST API + DTO 를 거의 백지에서. TRD 3.7.5.
// 막히면 starter/26-notice-controller, answers.md 26장 참고.

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

// 학습 질문 (직접 답):
// Q1. GET 상세에서 조회수를 올리는 문제와 대안은?
//     A:
// Q2. 공지 수정에 PUT vs PATCH? PRD 기준은?
//     A:
// Q3. 목록 응답에 content 를 그대로 넣으면 단점은?
//     A:

// 자가 채점:
// □ CREATED  □ Pageable  □ 목록 요약 DTO 권장  □ GET 멱등성 vs 조회수
