// 실제 구현 위치 예: service/DepartmentService.java
// 목표: 부서 CRUD + 소속 직원 조회를 거의 백지에서. PRD 2.5.3, TRD 3.7.3.
// 막히면 starter/24-department-service, answers.md 24장 참고.

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AccessGuard accessGuard;

    @Transactional
    public DepartmentResponse create(UserRole role, DepartmentCreateRequest request) {
        // TODO 01: ADMIN 권한.
        accessGuard.____(role);
        String name = request.getName().trim();
        // TODO 02: 부서명 중복.
        if (departmentRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.____, "이미 존재하는 부서명입니다.");
        }
        Department dept = Department.of(name, request.getDescription());
        return DepartmentResponse.from(departmentRepository.save(dept));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> listAll() {
        return departmentRepository.findAll(Sort.by("name"))
            .stream()
            // TODO 03: Entity → DTO.
            .map(DepartmentResponse::____)
            .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentDetailResponse detail(Long departmentId) {
        Department dept = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));
        // TODO 04: 소속 직원 — N+1 주의(@EntityGraph/fetch join).
        List<Employee> members = employeeRepository.findByDepartment_Id(departmentId, Pageable.unpaged()).getContent();
        return DepartmentDetailResponse.of(dept, members);
    }

    @Transactional
    public DepartmentResponse update(UserRole role, Long departmentId, DepartmentUpdateRequest request) {
        accessGuard.requireAdmin(role);
        Department dept = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));
        // TODO 05: 이름 변경 시 "자기 자신 제외" 중복검사.
        if (request.getName() != null
                && !request.getName().trim().____(dept.getName())
                && departmentRepository.existsByName(request.getName().trim())) {
            throw new BusinessException(ErrorCode.DUPLICATE_DEPARTMENT_NAME, "이미 존재하는 부서명입니다.");
        }
        dept.update(request.getName(), request.getDescription());
        return DepartmentResponse.from(dept);
    }

    @Transactional
    public void delete(UserRole role, Long departmentId) {
        accessGuard.requireAdmin(role);
        // TODO 06: 소속 직원이 있으면 삭제 거부.
        long memberCount = employeeRepository.____(departmentId);
        if (memberCount > 0) {
            throw new BusinessException(ErrorCode.____, "소속 직원이 있는 부서는 삭제할 수 없습니다.");
        }
        departmentRepository.deleteById(departmentId);
    }
}

// 학습 질문 (직접 답):
// Q1. 부서 목록을 페이징하지 않은 이유는? 1000개가 넘으면?
//     A:
// Q2. 부서 삭제 시 소속 직원 정책 3가지는?
//     A:
// Q3. 소속 직원까지 N+1 없이 가져오려면?
//     A:

// 자가 채점:
// □ requireAdmin  □ DUPLICATE_DEPARTMENT_NAME  □ DepartmentResponse::from  □ DEPARTMENT_NOT_FOUND
// □ !equals(현재이름)  □ countByDepartment_Id  □ DEPARTMENT_HAS_EMPLOYEES
