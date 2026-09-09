// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/ApprovalController.java
// 목표: 전자결재 REST API + DTO. TRD 3.7.6 참고.

// 초보자 안내 (27장): 결재 API와 명시적 동작
// 개념: 결재 API는 작성·제출·승인·반려를 각각 업무 동작으로 표현합니다.
// 사용 이유: 클라이언트가 status 값을 임의로 지정해 검증을 건너뛰지 못하게 합니다.
// 동작 흐름: Controller가 요청을 해당 Service 메서드로 전달하고 Service·Entity가 행위자와 상태 전이를 검사합니다. my와 pending 목록은 현재 직원 기준으로 조회합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
            @CurrentUserRole UserRole currentRole,
            @PathVariable Long approvalId) {
        return approvalService.detail(currentEmployeeId, currentRole, approvalId);
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
//
// 심화 노트 (면접 답변 포인트):
// - /my(작성자가 보는 내 문서, 로그인 누구나) vs /pending(결재자가 처리할 문서, APPROVER/ADMIN) — 관점·권한이 다르다.
// - @PreAuthorize 는 "역할" 만 거른다. "이 문서의 결재자 본인인가" 는 Service 의 approverId 검증으로 — 역할 통과해도 남의 문서는 못 봄(IDOR).
// - findPendingForApprover 는 currentEmployeeId 를 approverId 조건으로 써서 "내가 결재할 것" 만 반환 → 권한 어노테이션만으로 부족한 부분을 데이터로 보강.
// - 작성과 submit 을 분리해 임시저장 표현. createDraft 는 201, 상태 전이는 PATCH + 동사형 경로.
