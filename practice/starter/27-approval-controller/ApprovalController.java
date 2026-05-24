// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/ApprovalController.java
// 목표: 전자결재 REST API + DTO. PRD/TRD 3.7.6 참고.

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;

    // TODO 01: 결재 문서 작성 (DRAFT). 일반 사용자도 가능합니다.
    @PostMapping
    public ResponseEntity<ApprovalResponse> createDraft(
            @CurrentEmployee Long currentEmployeeId,
            @Valid @RequestBody ApprovalCreateRequest request) {

        ApprovalResponse response = approvalService.createDraft(currentEmployeeId, request);
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 02: 결재 요청 (DRAFT → PENDING). 상태 전이를 동사형 경로로 표현합니다.
    @PatchMapping("/{approvalId}/____")
    public ApprovalResponse submit(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long approvalId) {
        return approvalService.submit(currentEmployeeId, approvalId);
    }

    // TODO 03: 내가 작성한 결재 문서 목록.
    @GetMapping("/my")
    public Page<ApprovalResponse> my(
            @CurrentEmployee Long currentEmployeeId,
            @RequestParam(required = false) ApprovalStatus status,
            Pageable pageable) {
        return approvalService.findMyDocuments(currentEmployeeId, status, pageable);
    }

    // TODO 04: 내가 결재해야 할 대기 문서 목록. 권한은 APPROVER 또는 ADMIN.
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('____', '____')")
    public Page<ApprovalResponse> pending(
            @CurrentEmployee Long currentEmployeeId,
            Pageable pageable) {
        return approvalService.findPendingForApprover(currentEmployeeId, pageable);
    }

    @GetMapping("/{approvalId}")
    public ApprovalResponse detail(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long approvalId) {
        return approvalService.detail(currentEmployeeId, approvalId);
    }

    @PatchMapping("/{approvalId}/approve")
    @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
    public ApprovalResponse approve(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long approvalId) {
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
        @NotBlank String content
) { }

public record ApprovalRejectRequest(
        // TODO 05: 반려 사유는 비어 있으면 안 되는 이유는?
        @NotBlank @Size(max = 500) String rejectReason
) { }

public record ApprovalResponse(
        Long id,
        Long writerId,
        Long approverId,
        String title,
        String content,
        ApprovalStatus status,
        String rejectReason,
        LocalDateTime createdAt,
        LocalDateTime approvedAt
) {
    public static ApprovalResponse from(ApprovalDocument d) {
        return new ApprovalResponse(
                d.getId(), d.getWriterId(), d.getApproverId(),
                d.getTitle(), d.getContent(), d.getStatus(),
                d.getRejectReason(), d.getCreatedAt(), d.getApprovedAt());
    }
}

// 학습 질문:
// Q1. /my 와 /pending 의 권한 차이를 정리해 보세요.
//     A:
// Q2. 결재 작성 후 자동으로 submit 까지 하지 않고 별도 호출로 둔 이유는?
//     A:
// Q3. 결재자 본인이 아닌 사람이 /pending 을 호출하면 어떻게 막혀야 하는가?
//     A:
