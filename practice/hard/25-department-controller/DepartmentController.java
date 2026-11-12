// 중학생 눈높이: 부서 요청 창구
// 부서 목록·상세·등록·수정·삭제 요청을 받는 창구입니다.
// PUT과 PATCH는 수정 요청에 쓰는 방식입니다. 바꿀 범위는 API의 약속에 맞춥니다.
// 요청 주소 읽기 → 입력·권한 확인 → 부서 Service 호출 → 응답 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: controller/DepartmentController.java
// 목표: 부서 REST API + DTO 를 거의 백지에서. TRD 3.7.3.
// 막히면 starter/25-department-controller, answers.md 25장 참고.

// 초보자 안내 (25장): 부서 API와 요청 DTO
// 개념: 부서 Controller는 부서 관리 HTTP 요청을 Service 호출로 바꿉니다.
// 사용 이유: 주소 처리·입력 형식 검사와 부서 중복·삭제 정책을 분리합니다.
// 동작 흐름: 경로의 부서 ID와 검증한 요청 DTO를 받고 Service를 호출합니다. 조회는 결과를 보내고 삭제는 본문 없는 응답으로 완료를 알립니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    // TODO 01: 부서 등록 권한.
    @PostMapping
    @PreAuthorize("hasRole('____')")
    public ResponseEntity<DepartmentResponse> create(
            @CurrentUserRole UserRole role,
            @Valid @RequestBody DepartmentCreateRequest request) {
        DepartmentResponse response = departmentService.create(role, request);
        // TODO 02: 생성 status.
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 03: 목록 — 로그인 사용자 누구나.
    @GetMapping
    public ____<DepartmentResponse> list() {
        return departmentService.listAll();
    }

    @GetMapping("/{departmentId}")
    public DepartmentDetailResponse detail(@PathVariable Long departmentId) {
        return departmentService.detail(departmentId);
    }

    // TODO 04: 수정 — HTTP 메서드?
    @____("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentResponse update(
            @CurrentUserRole UserRole role,
            @PathVariable Long departmentId,
            @Valid @RequestBody DepartmentUpdateRequest request) {
        return departmentService.update(role, departmentId, request);
    }

    @DeleteMapping("/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@CurrentUserRole UserRole role, @PathVariable Long departmentId) {
        departmentService.delete(role, departmentId);
        return ResponseEntity.noContent().build();
    }
}

// ===== DTO =====
public record DepartmentCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description) { }

public record DepartmentUpdateRequest(
        @Size(max = 100) String name,
        @Size(max = 255) String description) { }

public record DepartmentResponse(Long id, String name, String description) {
    public static DepartmentResponse from(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getDescription());
    }
}

public record DepartmentDetailResponse(
        Long id, String name, String description, List<MemberItem> members) {
    public static DepartmentDetailResponse of(Department d, List<Employee> members) {
        return new DepartmentDetailResponse(
                d.getId(), d.getName(), d.getDescription(),
                members.stream().map(MemberItem::from).toList());
    }
    // TODO 05: 직원 엔티티를 그대로 노출하지 않는 요약 record.
    public record MemberItem(Long employeeId, String name, String employeeNumber, String position) {
        public static MemberItem from(Employee e) {
            return new MemberItem(e.getId(), e.getUser().getName(), e.getEmployeeNumber(), e.getPosition());
        }
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-25
//
// Q1. 부서는 List, 직원은 Page로 조회하는 선택 기준은? [chapter-v1-25-04]
// ① 자료 규모와 화면의 탐색·검색·응답량 요구가 서로 다르기 때문이다.
// ② List는 관리자, Page는 일반 사용자 역할을 뜻한다.
// ③ JPA는 부서에 Page 사용을 금지한다.
// ④ 직원은 작은 목록이어도 모든 요구에서 Page만 허용된다.
// 정답: ①
// 해설: 자원 이름보다 실제 규모와 사용자 흐름을 기준으로 응답 형태를 선택하며 커질 때 재검토한다.
//
// Q2. 부서 수정에서 PUT·PATCH를 선택할 때 적절한 판단은? [chapter-v1-25-05]
// ① 둘 중 최신 철자의 메서드를 선택한다.
// ② PUT과 PATCH의 생략 필드 처리는 항상 같다.
// ③ 메서드 선택으로 권한 검사가 자동 사라진다.
// ④ 전체 교체와 일부 필드 변경 중 어떤 요청 계약인지 정하고 생략 필드의 의미를 문서·코드와 맞춘다.
// 정답: ④
// 해설: 부분 갱신 의도에는 PATCH가 자연스럽다. 이미 정의된 API 계약을 바꾸는 경우 클라이언트와 검증도 함께 갱신한다.
//
// Q3. 부서 응답에서 Employee 대신 MemberItem nested record를 두는 이유는? [chapter-v1-25-06]
// ① nested record가 모든 JPA 관계를 자동 삭제하기 때문이다.
// ② record는 비밀번호를 자동 마스킹하기 때문이다.
// ③ 상세 화면에 필요한 직원 필드만 공개하고 응답 전용 구조를 한곳에 모으기 위해서다.
// ④ Employee의 모든 필드와 관계를 그대로 응답하기 위해서다.
// 정답: ③
// 해설: DTO 공개 범위를 명시한다. 내부 record의 위치는 응답 구조 관리 선택이며 자동 보안 기능이 아니다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ hasRole('ADMIN')  □ CREATED  □ List 반환  □ @PutMapping
