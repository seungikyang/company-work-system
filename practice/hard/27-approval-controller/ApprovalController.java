// 중학생 눈높이: 결재 요청 창구
// 문서 작성·제출·승인·반려를 각각 정해진 요청으로 받습니다.
// 요청 주소는 하고 싶은 작업을 구별합니다. 주소만 만들었다고 권한 검사가 자동 완성되지는 않습니다.
// 요청 작업 읽기 → 현재 사용자 확인 → 결재 Service 호출 → 응답 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-27
//
// Q1. 결재 /my와 /pending 조회의 권한 범위를 짝지은 것은? [chapter-v1-27-04]
// ① my는 모든 직원의 문서, pending은 로그인 없이 전체 문서다.
// ② 요청의 writerId만 신뢰해 다른 사람의 my 문서를 보여준다.
// ③ ADMIN 문자열이 본문에 있으면 모든 pending 문서를 보여준다.
// ④ my는 인증된 작성자의 문서, pending은 역할 검사를 통과한 지정 결재자의 대기 문서다.
// 정답: ④
// 해설: 인증 주체를 기준으로 작성자·결재자 조건을 쿼리에 적용한다. 역할과 상태 조건도 함께 검증한다.
//
// Q2. 작성 직후 자동 제출하지 않고 별도 submit API를 두는 이유는? [chapter-v1-27-05]
// ① 임시저장·수정·검토 후 사용자가 결재 요청을 확정하는 시점을 구분하기 위해서다.
// ② 임시저장 상태에서도 승인 완료로 처리하기 위해서다.
// ③ 결재자 검사를 생략하기 위해서다.
// ④ submit이 자료 소유권을 자동으로 알아서 검사하기 때문이다.
// 정답: ①
// 해설: DRAFT와 PENDING의 의미에 맞게 요청 흐름과 상태 전이를 분리하며 제출 권한도 검사한다.
//
// Q3. 지정 결재자가 아닌 사용자의 pending 접근을 막는 방법은? [chapter-v1-27-06]
// ① 클라이언트가 보낸 approverId가 자신의 것이라고 믿는다.
// ② 인증된 직원 ID와 역할을 검사하고 approverId 조건을 조회에 적용한다.
// ③ 화면에서 메뉴만 숨긴다.
// ④ pending이면 어떤 로그인 사용자에게든 모든 문서를 보여준다.
// 정답: ②
// 해설: 타인의 대기 목록을 ID 교체로 조회하지 못하도록 서버·쿼리 조건으로 범위를 제한한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ CREATED  □ /submit  □ hasAnyRole('APPROVER','ADMIN')  □ @NotBlank rejectReason
