// 실제 구현 위치 예: controller/LeaveController.java (USER) + AdminLeaveController.java (ADMIN)
// 목표: 휴가 신청자/관리자 경로·권한 분리를 거의 백지에서. TRD 3.7.4.
// 막히면 starter/19-controller-leave, answers.md 19장 참고.

// 초보자 안내 (19장): 사용자·관리자 API 분리
// 개념: API 경로는 어떤 업무를 요청하는지 나타내는 주소입니다.
// 사용 이유: 내 휴가 신청과 관리자의 승인 작업을 구분하고 각 요청의 권한을 검사합니다.
// 동작 흐름: Controller가 세션에서 확인한 행위자와 요청 값을 Service에 전달합니다. 주소에 admin이 들어 있다는 사실만으로 접근이 차단되는 것은 아니므로 보안 설정과 권한 검사를 함께 구현합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
