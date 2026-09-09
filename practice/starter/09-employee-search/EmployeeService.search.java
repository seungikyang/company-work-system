// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 목록을 검색 + 페이징으로 반환하세요. PRD 2.5.2 (FR-EMP-006/007) 참고.

// 초보자 안내 (09장): Pageable과 Page
// 개념: Pageable은 페이지 번호·크기·정렬 조건이고 Page는 조회 결과와 전체 건수 등의 페이지 정보입니다.
// 사용 이유: 직원이 많아도 한 화면에서 필요한 분량만 조회합니다.
// 동작 흐름: 검색 조건과 Pageable을 Repository에 전달하고 Page.map으로 각 Employee를 응답 DTO로 바꿉니다. 페이지 정보는 유지되며 전체 건수 계산에 추가 쿼리가 필요할 수 있습니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// TODO 01: 읽기 전용 트랜잭션을 명시하면 성능과 의도를 동시에 챙길 수 있습니다.
@Transactional(readOnly = ____)
public Page<EmployeeResponse> search(String keyword, Long departmentId, Pageable pageable) {

    // 검색 조건이 다양해질 때는 Querydsl/Specification 이 더 깔끔하지만,
    // 학습 단계에서는 if 분기로 충분합니다.

    Page<Employee> page;

    if (keyword != null && !keyword.isBlank() && departmentId != null) {
        // TODO 02: 이름/사번 검색 + 부서 조건이 동시에 들어온 경우.
        //          여기서는 학습용으로 단순하게 두 단계 필터를 거치는 예시를 보여줍니다.
        page = employeeRepository.findByUser_NameContainingOrEmployeeNumberContaining(
            keyword, keyword, pageable);
        // 학습 메모: 위 쿼리는 부서 조건을 반영하지 못하므로,
        //           실제로는 @Query 나 Specification 으로 합쳐서 1회 쿼리에 처리하는 편이 정확합니다.
    } else if (keyword != null && !keyword.isBlank()) {
        page = employeeRepository.____(keyword, keyword, pageable);
    } else if (departmentId != null) {
        page = employeeRepository.findByDepartment_Id(departmentId, pageable);
    } else {
        page = employeeRepository.____(pageable);
    }

    // TODO 03: Page<Entity> 를 Page<DTO> 로 변환하세요. (페이지 메타 정보가 유지됩니다)
    return page.____(EmployeeResponse::from);
}

// 학습 질문:
// Q1. Controller 가 Pageable 을 그대로 받으면 클라이언트는 어떤 쿼리스트링으로 페이지/사이즈/정렬을 보내나?
//     A:
// Q2. List<EmployeeResponse> 가 아니라 Page<EmployeeResponse> 로 반환하는 이유는?
//     A:
// Q3. 정렬 기준을 클라이언트가 자유롭게 지정할 때 발생할 수 있는 위험은?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - @Transactional(readOnly=true) → Hibernate flush 모드 MANUAL, dirty checking 생략 → 성능 + 의도 표현.
// - 이 if 분기는 keyword+departmentId 동시 입력 시 부서조건을 못 건다(학습용 한계).
//   실무는 Querydsl/Specification 으로 동적 조건을 1회 쿼리에 결합.
// - page.map(...) 은 content 만 DTO 로 바꾸고 totalElements/totalPages 등 메타를 유지한다.
//   List 로 바꾸면 프론트 페이지네이션이 깨진다.
