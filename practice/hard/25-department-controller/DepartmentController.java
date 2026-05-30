// 실제 구현 위치 예: controller/DepartmentController.java
// 목표: 부서 REST API + DTO 를 거의 백지에서. TRD 3.7.3.
// 막히면 starter/25-department-controller, answers.md 25장 참고.

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

// 학습 질문 (직접 답):
// Q1. 부서 목록은 List, 직원 목록은 Page 인 차이를 무엇으로 결정하나?
//     A:
// Q2. 부서 수정에 PUT vs PATCH?
//     A:
// Q3. MemberItem 을 nested record 로 둔 이유는?
//     A:

// 자가 채점:
// □ hasRole('ADMIN')  □ CREATED  □ List 반환  □ @PutMapping
