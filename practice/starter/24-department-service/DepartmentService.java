// 중학생 눈높이: 부서 관리
// 부서를 등록하거나 고치고, 부서와 소속 직원의 정보를 찾습니다.
// 직원이 속한 부서를 지우려면 연결된 직원 정보를 어떻게 처리할지 먼저 정해야 합니다.
// 중복·존재 확인 → 부서 조회 또는 변경 → 직원과의 연결 확인 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/service/DepartmentService.java
// 목표: 부서 CRUD 와 소속 직원 조회를 채우세요. PRD 2.5.3, TRD 3.7.3 참고.

// 초보자 안내 (24장): 삭제 정책과 참조 관계
// 개념: 참조 관계는 직원이 부서 ID를 가리키는 연결입니다.
// 사용 이유: 직원이 속한 부서를 지워 소속 정보가 끊기는 일을 막습니다.
// 동작 흐름: Service가 역할·부서명 중복을 확인해 생성·수정하고 삭제 전 소속 직원 존재 여부를 검사합니다. 동시 요청까지 고려한 최종 무결성은 DB 제약과 함께 다룹니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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

        // TODO 02: 부서명 중복 확인. 15장의 공통 ErrorCode 기준을 사용합니다.
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (departmentRepository.existsByName(name)) {
            // throw는 문제를 알리는 예외를 던집니다. 이 지점 뒤의 정상 작업은 계속 진행하지 않습니다.
            throw new BusinessException(ErrorCode.____, "이미 존재하는 부서명입니다.");
        }

        Department dept = Department.of(name, request.getDescription());
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
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
            throw new BusinessException(ErrorCode.DUPLICATE_DEPARTMENT_NAME, "이미 존재하는 부서명입니다.");
        }

        dept.update(request.getName(), request.getDescription());
        return DepartmentResponse.from(dept);
    }

    @Transactional
    public void delete(UserRole role, Long departmentId) {
        accessGuard.requireAdmin(role);

        // TODO 06: 소속 직원이 한 명이라도 있으면 삭제를 막아야 할까요? 어떤 비즈니스 규칙이 자연스러운가요?
        // 삭제 전에 직원 수를 세는 이유는 직원이 참조 중인 부서를 지우지 않기 위해서입니다.
        // 검사와 삭제 사이의 동시 요청까지 다루려면 DB 외래키 제약도 함께 확인하세요.
        long memberCount = employeeRepository.countByDepartment_Id(departmentId);
        if (memberCount > 0) {
            throw new BusinessException(ErrorCode.____, "소속 직원이 있는 부서는 삭제할 수 없습니다.");
        }

        departmentRepository.deleteById(departmentId);
    }
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-24
//
// Q1. 부서 목록이 커질 때 List와 Page 선택을 재검토하는 기준은? [chapter-v1-24-05]
// ① 예상 데이터 규모·응답 크기·탐색 요구를 보고 커지면 페이징·검색을 적용한다.
// ② 부서라는 이름의 데이터는 언제나 건수가 적다.
// ③ 1,000건이 넘어도 전체 조회 비용은 반드시 일정하다.
// ④ List 반환이 모든 N+1 문제를 자동 제거한다.
// 정답: ①
// 해설: 초기 규모에 맞는 단순 조회도 데이터가 증가하면 재검토한다. 측정한 비용과 사용자 탐색 흐름을 기준으로 선택한다.
//
// Q2. 직원이 속한 부서를 삭제할 때 가능한 정책 조합은? [chapter-v1-24-06]
// ① 정책 없이 직원을 전부 삭제한다.
// ② 삭제 거절·대체 부서로 이동·관계 null 허용을 업무 규칙과 외래키 제약에 맞춰 선택한다.
// ③ 외래키가 있어도 존재하지 않는 부서 ID를 그대로 유지한다.
// ④ null을 허용하지 않는 관계에서 아무 처리 없이 부서만 지운다.
// 정답: ②
// 해설: 각 정책의 인사 이력·관계 제약·원자성 영향을 검토한다. 기본 부서 이동이나 null 허용은 실제 요구·매핑 변경과 함께 적용한다.
//
// Q3. 부서 상세의 직원 정보를 N+1 없이 가져오는 조회 설계는? [chapter-v1-24-07]
// ① LAZY를 EAGER로 바꾸면 모든 경우에 N+1이 사라진다.
// ② 직원마다 별도 쿼리를 실행해야만 N+1이 줄어든다.
// ③ 필요한 관계를 fetch join·EntityGraph 또는 DTO 전용 조회로 가져오고 실제 SQL 수를 확인한다.
// ④ JSON 직렬화 때 모든 관계를 무조건 로딩한다.
// 정답: ③
// 해설: 조회 범위·페이지 처리·컬렉션 중복을 고려해 방식을 선택한다. 어노테이션 이름만으로 성능을 보장하지 않는다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - 부서 목록은 List 전체 조회(부서는 소수). 운영에서 1000개+ 면 페이징 + 본부/팀 그룹화로 확장.
// - 삭제 정책 3안: ① 거부(DEPARTMENT_HAS_EMPLOYEES, 학습 기본) ② 기본 부서로 이동 ③ FK NULL 허용. 비즈니스 규칙으로 명문화.
// - detail 의 members 를 Pageable.unpaged() 로 가져와 직원마다 user 를 접근하면 N+1 → fetch join / @EntityGraph 로 한 번에.
// - 이름 변경 시 중복검사는 "자기 자신 제외"(newName != 현재 이름일 때만 existsByName) — 안 그러면 자기 이름으로도 막힌다.
