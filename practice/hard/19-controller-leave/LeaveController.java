// 실제 구현 위치 예: controller/LeaveController.java (USER) + AdminLeaveController.java (ADMIN)
// 목표: 휴가 신청자/관리자 경로·권한 분리를 거의 백지에서. TRD 3.7.4.
// 막히면 starter/19-controller-leave, answers.md 19장 참고.

// ===== 일반 사용자용 =====
@RestController
@RequestMapping("____") // TODO 01: 일반 사용자 휴가 prefix
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // TODO 02: 휴가 신청. 로그인 직원 ID 주입(세션/Security/JWT 단계별).
    @PostMapping
    public ResponseEntity<LeaveResponse> request(
            @____ Long currentEmployeeId,
            @Valid @RequestBody LeaveCreateRequest request) {
        LeaveResponse response = leaveService.request(currentEmployeeId, request);
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    @GetMapping("/my")
    public Page<LeaveResponse> my(@CurrentEmployee Long currentEmployeeId, Pageable pageable) {
        return leaveService.findMyLeaves(currentEmployeeId, pageable);
    }

    @GetMapping("/{leaveId}")
    public LeaveResponse detail(
            @CurrentEmployee Long currentEmployeeId,
            @CurrentUserRole UserRole currentRole,
            @PathVariable Long leaveId) {
        return leaveService.detail(currentEmployeeId, currentRole, leaveId);
    }

    // TODO 03: 신청 취소(PENDING 만) — HTTP 메서드?
    @____("/{leaveId}/cancel")
    public LeaveResponse cancel(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long leaveId) {
        return leaveService.cancel(currentEmployeeId, leaveId);
    }
}

// ===== 관리자용 =====
@RestController
@RequestMapping("____") // TODO 04: 관리자 휴가 prefix
@PreAuthorize("hasRole('____')")
@RequiredArgsConstructor
public class AdminLeaveController {

    private final LeaveService leaveService;

    @GetMapping
    public Page<LeaveResponse> list(
            @RequestParam(required = false) ApprovalStatus status,
            @RequestParam(required = false) Long employeeId,
            Pageable pageable) {
        return leaveService.findForAdmin(status, employeeId, pageable);
    }

    // TODO 05: 승인 — 동사형 경로.
    @PatchMapping("/{leaveId}/____")
    public LeaveResponse approve(@CurrentUser Long approverUserId, @PathVariable Long leaveId) {
        return leaveService.approve(approverUserId, leaveId);
    }

    // TODO 06: 반려 — 사유 body.
    @PatchMapping("/{leaveId}/____")
    public LeaveResponse reject(
            @CurrentUser Long approverUserId,
            @PathVariable Long leaveId,
            @Valid @RequestBody LeaveRejectRequest request) {
        return leaveService.reject(approverUserId, leaveId, request);
    }
}

// 학습 질문 (직접 답):
// Q1. /api/leaves 와 /api/admin/leaves 분리의 장단점은?
//     A:
// Q2. 상태 전이에 PATCH 와 PUT 중 어울리는 것은?
//     A:
// Q3. 권한 체크 다층 방어 권장 패턴은?
//     A:

// 자가 채점:
// □ /api/leaves  □ @CurrentEmployee  □ CREATED  □ @PatchMapping cancel
// □ /api/admin/leaves + hasRole('ADMIN')  □ /approve  □ /reject
