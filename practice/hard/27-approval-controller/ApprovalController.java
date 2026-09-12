// 실제 구현 위치 예: controller/ApprovalController.java
// 목표: 전자결재 REST API + DTO 를 거의 백지에서. TRD 3.7.6.
// 막히면 starter/27-approval-controller, answers.md 27장 참고.

// 초보자 안내 (27장): 결재 API와 명시적 동작
// 개념: 결재 API는 작성·제출·승인·반려를 각각 업무 동작으로 표현합니다.
// 사용 이유: 클라이언트가 status 값을 임의로 지정해 검증을 건너뛰지 못하게 합니다.
// 동작 흐름: Controller가 요청을 해당 Service 메서드로 전달하고 Service·Entity가 행위자와 상태 전이를 검사합니다. my와 pending 목록은 현재 직원 기준으로 조회합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // TODO 01: 작성(DRAFT) — 누구나, 생성 status.
    @PostMapping
    public ResponseEntity<ApprovalResponse> createDraft(
            @CurrentEmployee Long currentEmployeeId,
            @Valid @RequestBody ApprovalCreateRequest request) {
        ApprovalResponse response = approvalService.createDraft(currentEmployeeId, request);
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 02: 결재 요청(DRAFT→PENDING) — 동사형 경로.
    @PatchMapping("/{approvalId}/____")
    public ApprovalResponse submit(@CurrentEmployee Long currentEmployeeId, @PathVariable Long approvalId) {
        return approvalService.submit(currentEmployeeId, approvalId);
    }

    @GetMapping("/my")
    public Page<ApprovalResponse> my(
            @CurrentEmployee Long currentEmployeeId,
            @RequestParam(required = false) ApprovalStatus status,
            Pageable pageable) {
        return approvalService.findMyDocuments(currentEmployeeId, status, pageable);
    }

    // TODO 03: 결재 대기 — 권한 두 역할.
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('____', '____')")
    public Page<ApprovalResponse> pending(@CurrentEmployee Long currentEmployeeId, Pageable pageable) {
        return approvalService.findPendingForApprover(currentEmployeeId, pageable);
    }

    @GetMapping("/{approvalId}")
    public ApprovalResponse detail(
            @CurrentEmployee Long currentEmployeeId,
            @CurrentUserRole UserRole currentRole,
            @PathVariable Long approvalId) {
        return approvalService.detail(currentEmployeeId, currentRole, approvalId);
    }

    @PatchMapping("/{approvalId}/approve")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    public ApprovalResponse approve(@CurrentEmployee Long currentEmployeeId, @PathVariable Long approvalId) {
        return approvalService.approve(currentEmployeeId, approvalId);
    }

    @PatchMapping("/{approvalId}/reject")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    public ApprovalResponse reject(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long approvalId,
            @Valid @RequestBody ApprovalRejectRequest request) {
        return approvalService.reject(currentEmployeeId, approvalId, request);
    }
}

// ===== DTO =====
public record ApprovalCreateRequest(
        @NotNull Long approverId,
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content) { }

public record ApprovalRejectRequest(
        // TODO 04: 반려 사유 필수.
        @____ @Size(max = 500) String rejectReason) { }

public record ApprovalResponse(
        Long id, Long writerId, Long approverId, String title, String content,
        ApprovalStatus status, String rejectReason, LocalDateTime createdAt, LocalDateTime approvedAt) {
    public static ApprovalResponse from(ApprovalDocument d) {
        return new ApprovalResponse(
                d.getId(), d.getWriterId(), d.getApproverId(), d.getTitle(), d.getContent(),
                d.getStatus(), d.getRejectReason(), d.getCreatedAt(), d.getApprovedAt());
    }
}

// 학습 질문 (직접 답):
// Q1. /my 와 /pending 의 권한 차이는?
//     A:
// Q2. 작성 후 자동 submit 하지 않고 별도 호출로 둔 이유는?
//     A:
// Q3. 결재자 본인이 아닌 사람이 /pending 을 호출하면 어떻게 막히나?
//     A:

// 자가 채점:
// □ CREATED  □ /submit  □ hasAnyRole('APPROVER','ADMIN')  □ @NotBlank rejectReason
