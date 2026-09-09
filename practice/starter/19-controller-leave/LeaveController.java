// 실제 구현 위치 예:
//   - src/main/java/com/example/companywork/controller/LeaveController.java       (USER)
//   - src/main/java/com/example/companywork/controller/AdminLeaveController.java  (ADMIN)
// 목표: 휴가 신청자와 관리자의 경로/권한을 명확히 분리해 보세요. TRD 3.7.4 참고.

// 초보자 안내 (19장): 사용자·관리자 API 분리
// 개념: API 경로는 어떤 업무를 요청하는지 나타내는 주소입니다.
// 사용 이유: 내 휴가 신청과 관리자의 승인 작업을 구분하고 각 요청의 권한을 검사합니다.
// 동작 흐름: Controller가 세션에서 확인한 행위자와 요청 값을 Service에 전달합니다. 주소에 admin이 들어 있다는 사실만으로 접근이 차단되는 것은 아니므로 보안 설정과 권한 검사를 함께 구현합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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

    // TODO 03: 휴가 상세. 본인 또는 ADMIN 만 조회할 수 있습니다.
    @GetMapping("/{leaveId}")
    public LeaveResponse detail(
            @CurrentEmployee Long currentEmployeeId,
            @CurrentUserRole UserRole currentRole,
            @PathVariable Long leaveId) {
        return leaveService.detail(currentEmployeeId, currentRole, leaveId);
    }

    // TODO 04: 휴가 신청 취소(PENDING 만). 어떤 HTTP 메서드와 경로가 자연스러울까요?
    @____("/{leaveId}/cancel")
    public LeaveResponse cancel(
            @CurrentEmployee Long currentEmployeeId,
            @PathVariable Long leaveId) {
        return leaveService.cancel(currentEmployeeId, leaveId);
    }
}


// ===== 관리자용 =====
@RestController
@RequestMapping("____") // TODO 05: 관리자 휴가 API 의 prefix 를 정해 보세요. 예: /api/admin/leaves
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLeaveController {

    private final LeaveService leaveService;

    // TODO 06: 관리자 휴가 목록 (상태/직원 등 조건).
    @GetMapping
    public Page<LeaveResponse> list(
            @RequestParam(required = false) ApprovalStatus status,
            @RequestParam(required = false) Long employeeId,
            Pageable pageable) {
        return leaveService.findForAdmin(status, employeeId, pageable);
    }

    // TODO 07: 휴가 승인. 상태 변경 동사형 경로(/approve)는 REST 순수주의 관점의 단점이 있지만 의미가 분명합니다.
    @PatchMapping("/{leaveId}/____")
    public LeaveResponse approve(
            @CurrentUser Long approverUserId,
            @PathVariable Long leaveId) {
        return leaveService.approve(approverUserId, leaveId);
    }

    // TODO 08: 휴가 반려. 반려 사유를 body 로 받습니다.
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
//
// 심화 노트 (면접 답변 포인트):
// - /api/leaves(USER) vs /api/admin/leaves(ADMIN) 분리 → 권한 정책이 경로에 드러나 SecurityFilterChain 설정이 단순.
//   단점은 컨트롤러/경로 중복.
// - 인증 사용자 주입 진화: @SessionAttribute/HttpSession → @AuthenticationPrincipal → 직접 만든 @CurrentEmployee.
// - 상태 전이는 PATCH + 동사형 경로(/cancel, /approve, /reject). PUT 은 전체 교체 의미라 부적절.
// - IDOR 방지: detail/cancel 은 "본인 또는 ADMIN" 여부를 Service 에서 소유자 검증(URL 의 id 를 신뢰하지 않는다).
