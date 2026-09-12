// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/DepartmentController.java
// 목표: 부서 REST API. TRD 3.7.3 참고.

// 초보자 안내 (25장): 부서 API와 요청 DTO
// 개념: 부서 Controller는 부서 관리 HTTP 요청을 Service 호출로 바꿉니다.
// 사용 이유: 주소 처리·입력 형식 검사와 부서 중복·삭제 정책을 분리합니다.
// 동작 흐름: 경로의 부서 ID와 검증한 요청 DTO를 받고 Service를 호출합니다. 조회는 결과를 보내고 삭제는 본문 없는 응답으로 완료를 알립니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    // TODO 01: 부서 등록은 어떤 역할만 호출 가능한가요?
    @PostMapping
    @PreAuthorize("hasRole('____')")
    public ResponseEntity<DepartmentResponse> create(
            @CurrentUserRole UserRole role,
            @Valid @RequestBody DepartmentCreateRequest request) {

        DepartmentResponse response = departmentService.create(role, request);
        // TODO 02: 신규 자원 생성 상태 코드는?
        return ResponseEntity.status(HttpStatus.____).body(response);
    }

    // TODO 03: 부서 목록 — 로그인 사용자라면 모두 조회 가능.
    @GetMapping
    public List<DepartmentResponse> list() {
        return departmentService.listAll();
    }

    // TODO 04: 부서 상세 (소속 직원 포함).
    @GetMapping("/{departmentId}")
    public DepartmentDetailResponse detail(@PathVariable Long departmentId) {
        return departmentService.detail(departmentId);
    }

    // TODO 05: 전체 갱신과 부분 갱신 중 어떤 HTTP 메서드가 자연스러운가요?
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
    public ResponseEntity<Void> delete(
            @CurrentUserRole UserRole role,
            @PathVariable Long departmentId) {
        departmentService.delete(role, departmentId);
        return ResponseEntity.noContent().build();
    }
}


// ===== DTO =====
// 요청 record는 전송 필드와 접근자를 제공합니다. name()으로 읽으므로 24장의 getName() 호출과
// 그대로 조립할 수는 없습니다. 실행 프로젝트에서 DTO 형식과 호출부를 통일하세요.
public record DepartmentCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description
) { }

public record DepartmentUpdateRequest(
        // TODO 06: 부분 수정에서는 name 이 null 인 경우도 허용할까요?
        @Size(max = 100) String name,
        @Size(max = 255) String description
) { }

public record DepartmentResponse(Long id, String name, String description) {
    public static DepartmentResponse from(Department d) {
        return new DepartmentResponse(d.getId(), d.getName(), d.getDescription());
    }
}

public record DepartmentDetailResponse(
        Long id, String name, String description, List<MemberItem> members
) {
    public static DepartmentDetailResponse of(Department d, List<Employee> members) {
        return new DepartmentDetailResponse(
                d.getId(), d.getName(), d.getDescription(),
                members.stream().map(MemberItem::from).toList());
    }

    public record MemberItem(Long employeeId, String name, String employeeNumber, String position) {
        public static MemberItem from(Employee e) {
            return new MemberItem(e.getId(), e.getUser().getName(),
                    e.getEmployeeNumber(), e.getPosition());
        }
    }
}

// 학습 질문 (면접 대비):
// Q1. 부서 목록은 List 인데 직원 목록은 Page 입니다. 무엇으로 이 차이를 결정했나요?
//     A:
// Q2. 부서 수정에 PUT 과 PATCH 중 무엇을 골랐고, 그 근거는?
//     A:
// Q3. DepartmentDetailResponse 안에 MemberItem 을 nested record 로 둔 이유는? (Employee 를 그대로 안 쓰는 이유)
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - 권한 다층: @PreAuthorize("hasRole('ADMIN')")(1차) + Service accessGuard.requireAdmin(2차).
// - List(부서=소수) vs Page(직원=다수) — 데이터 규모로 컬렉션 타입을 정한다.
// - nested record(MemberItem)는 응답 모양을 명확히 하고, 직원 엔티티의 user.password 같은 필드 노출을 막는다(요약 필드만).
// - 부서 등록은 201 Created, 삭제는 204 No Content.
