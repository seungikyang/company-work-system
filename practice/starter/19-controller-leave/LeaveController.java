// 실제 구현 위치 예:
//   - src/main/java/com/example/companywork/controller/LeaveController.java       (USER)
//   - src/main/java/com/example/companywork/controller/AdminLeaveController.java  (ADMIN)
// 목표: 휴가 신청자와 관리자의 경로/권한을 명확히 분리해 보세요. PRD/TRD 3.7.4 참고.

// ===== 일반 사용자용 =====
@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // TODO 01: 휴가 신청. 로그인한 직원의 ID 는 어떻게 꺼낼까요?
    //          - 학습용 세션 단계: @SessionAttribute / HttpSession
    //          - Spring Security 단계: @AuthenticationPrincipal
    //          - JWT 단계: 직접 만든 @CurrentEmployee 어노테이션
    @PostMapping
    public ResponseEntity<LeaveResponse> request(
            @____ Long currentEmployeeId,
            @Valid @RequestBody LeaveCreateRequest request) {

        LeaveResponse response = leaveService.request(currentEmployeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // TODO 02: 내가 신청한 휴가 목록.
    @GetMapping("/my")
    public Page<LeaveResponse> my(
            @CurrentEmployee Long currentEmployeeId,
            Pageable pageable) {
        return leaveService.findMyLeaves(currentEmployeeId, pageable);
    }

    // TODO 03: 휴가 신청 취소(PENDING 만). 어떤 HTTP 메서드와 경로가 자연스러울까요?
    @____("/{leaveId}/cancel")
    public LeaveResponse cancel(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long leaveId) {
        return leaveService.cancel(currentEmployeeId, leaveId);
    }
}


// ===== 관리자용 =====
@RestController
@RequestMapping("____") // TODO 04: 관리자 휴가 API 의 prefix 를 정해 보세요. 예: /api/admin/leaves
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLeaveController {

    private final LeaveService leaveService;

    // TODO 05: 관리자 휴가 목록 (상태/직원 등 조건).
    @GetMapping
    public Page<LeaveResponse> list(
            @RequestParam(required = false) ApprovalStatus status,
            @RequestParam(required = false) Long employeeId,
            Pageable pageable) {
        return leaveService.findForAdmin(status, employeeId, pageable);
    }

    // TODO 06: 휴가 승인. 상태 변경 동사형 경로(/approve)는 REST 순수주의 관점의 단점이 있지만 의미가 분명합니다.
    @PatchMapping("/{leaveId}/____")
    public LeaveResponse approve(
            @CurrentUser Long approverUserId,
            @PathVariable Long leaveId) {
        return leaveService.approve(approverUserId, leaveId);
    }

    // TODO 07: 휴가 반려. 반려 사유를 body 로 받습니다.
    @PatchMapping("/{leaveId}/____")
    public LeaveResponse reject(
            @CurrentUser Long approverUserId,
            @PathVariable Long leaveId,
            @Valid @RequestBody LeaveRejectRequest request) {
        return leaveService.reject(approverUserId, leaveId, request);
    }
}

// 학습 질문:
// Q1. /api/leaves 와 /api/admin/leaves 로 분리하는 장점/단점은?
//     A:
// Q2. PATCH 와 PUT 중 상태 전이에 어울리는 것은?
//     A:
// Q3. 권한 체크를 어디(필터/AOP/Service)에서 할지, 다층 방어를 위한 권장 패턴은?
//     A:
