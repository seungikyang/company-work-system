// 실제 구현 위치 예: src/main/java/com/example/companywork/service/DepartmentService.java
// 목표: 부서 CRUD 와 소속 직원 조회를 채우세요. PRD/TRD 2.5.3, 3.7.3 참고.

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final AccessGuard accessGuard;

    @Transactional
    public DepartmentResponse create(UserRole role, DepartmentCreateRequest request) {

        // TODO 01: 권한 검사 (ADMIN 만 부서 등록 가능).
        accessGuard.____(role);

        String name = request.getName().trim();

        // TODO 02: 부서명 중복 확인. ErrorCode 는 새로 만들어도 좋습니다.
        if (departmentRepository.existsByName(name)) {
            throw new BusinessException(ErrorCode.____, "이미 존재하는 부서명입니다.");
        }

        Department dept = Department.of(name, request.getDescription());
        return DepartmentResponse.from(departmentRepository.save(dept));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> listAll() {
        // 부서는 보통 양이 적어 페이징보다 전체 조회가 자연스럽습니다.
        return departmentRepository.findAll(Sort.by("name"))
            .stream()
            // TODO 03: Entity → DTO 변환.
            .map(DepartmentResponse::____)
            .toList();
    }

    @Transactional(readOnly = true)
    public DepartmentDetailResponse detail(Long departmentId) {
        Department dept = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));

        // TODO 04: 소속 직원 목록을 함께 반환합니다. N+1 을 피하려면 어떻게 해야 할까요?
        //          힌트: @EntityGraph 또는 Repository 의 JPQL fetch join
        List<Employee> members = employeeRepository.findByDepartment_Id(departmentId,
                Pageable.unpaged()).getContent();

        return DepartmentDetailResponse.of(dept, members);
    }

    @Transactional
    public DepartmentResponse update(UserRole role, Long departmentId, DepartmentUpdateRequest request) {
        accessGuard.requireAdmin(role);

        Department dept = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new BusinessException(ErrorCode.DEPARTMENT_NOT_FOUND));

        // TODO 05: 부서명 변경 시에도 다른 부서와 중복되지 않아야 합니다.
        if (request.getName() != null
                && !request.getName().trim().equals(dept.getName())
                && departmentRepository.existsByName(request.getName().trim())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "이미 존재하는 부서명입니다.");
        }

        dept.update(request.getName(), request.getDescription());
        return DepartmentResponse.from(dept);
    }

    @Transactional
    public void delete(UserRole role, Long departmentId) {
        accessGuard.requireAdmin(role);

        // TODO 06: 소속 직원이 한 명이라도 있으면 삭제를 막아야 할까요? 어떤 비즈니스 규칙이 자연스러운가요?
        long memberCount = employeeRepository.countByDepartment_Id(departmentId);
        if (memberCount > 0) {
            throw new BusinessException(ErrorCode.____, "소속 직원이 있는 부서는 삭제할 수 없습니다.");
        }

        departmentRepository.deleteById(departmentId);
    }
}

// 학습 질문:
// Q1. 부서 목록을 페이징하지 않은 이유는? 운영 단계에서 부서가 1000개가 넘는다면?
//     A:
// Q2. 부서 삭제 시 소속 직원이 있으면 어떤 정책을 둘 수 있을까? (거부 / 기본 부서 이동 / NULL 허용)
//     A:
// Q3. 부서 상세에서 소속 직원까지 N+1 없이 가져오려면 어떤 방법이 있나?
//     A:
