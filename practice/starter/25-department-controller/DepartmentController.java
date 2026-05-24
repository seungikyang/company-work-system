// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/DepartmentController.java
// 목표: 부서 REST API. TRD 3.7.3 참고.

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
