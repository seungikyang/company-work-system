// 중학생 눈높이: 휴가 요청 창구
// 직원의 신청과 관리자의 처리를 각각 알맞은 주소에서 받습니다.
// 로그인 사용자 정보는 서버가 확인합니다. 요청자가 적은 ID만 믿고 다른 사람의 일을 처리하지 않습니다.
// 요청 종류 확인 → 현재 사용자 확인 → 휴가 Service 호출 → 응답 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
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
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-19
//
// Q1. 내 휴가와 관리자 휴가 API를 경로로 분리하는 판단으로 알맞은 것은? [chapter-v1-19-04]
// ① 사용자·관리자 목적과 권한 계약을 명확히 하되 중복 구현을 줄이고 서버 인가를 유지한다.
// ② admin이라는 경로명 자체가 접근 권한을 보장한다.
// ③ 일반 사용자 경로이면 소유권 검사가 필요 없다.
// ④ 경로를 분리하면 Service와 Repository가 없어도 된다.
// 정답: ①
// 해설: 경로 구분은 사용 의도와 API 계약을 표현한다. 실질적 접근 제한은 인증·인가와 소유권 검사로 보장한다.
//
// Q2. 휴가 승인·반려 같은 상태 부분 변경에 자연스러운 HTTP 계약은? [chapter-v1-19-05]
// ① GET으로 상태를 바꾸고 인증을 생략한다.
// ② DELETE가 항상 승인 동작을 뜻한다.
// ③ PUT 이름만 쓰면 상태 검증 없이 모든 전이가 허용된다.
// ④ 상태 변경의 부분 갱신 계약을 가진 PATCH를 사용하고 전이 규칙을 서버에서 검사한다.
// 정답: ④
// 해설: 이 실습의 상태 변경 계약을 따르고 메서드 선택과 별개로 역할·행위자·현재 상태를 검증한다.
//
// Q3. 휴가 API의 다층 방어를 책임에 맞게 연결한 것은? [chapter-v1-19-06]
// ① 화면 버튼 숨김이 인증·인가·소유권을 모두 보장한다.
// ② Repository는 요청 본문의 ADMIN 값을 신뢰해야 한다.
// ③ 필터는 인증, 메서드 인가는 역할, Service·조회 조건은 소유권과 업무 규칙을 확인한다.
// ④ 모든 사용자가 로그인 후 모든 휴가를 승인한다.
// 정답: ③
// 해설: 인증된 주체를 사용하고 역할과 자료 접근 조건을 함께 적용한다. 계층별 실패 테스트로 우회를 검증한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ /api/leaves  □ @CurrentEmployee  □ CREATED  □ @PatchMapping cancel
// □ /api/admin/leaves + hasRole('ADMIN')  □ /approve  □ /reject
