// 실제 구현 위치 예: src/main/java/com/example/companywork/service/EmployeeService.java
// 목표: 직원 검색 + 페이징을 거의 백지에서. PRD 2.5.2 (FR-EMP-006/007).
// 막히면 starter/09-employee-search, answers.md 9장 참고.

// TODO 01: 읽기 전용 트랜잭션.
@Transactional(readOnly = ____)
public Page<EmployeeResponse> search(String keyword, Long departmentId, Pageable pageable) {

    Page<Employee> page;

    if (keyword != null && !keyword.isBlank() && departmentId != null) {
        // 학습용 단순화: 부서조건 결합은 Querydsl/Specification 으로(메모만)
        page = employeeRepository.findByUser_NameContainingOrEmployeeNumberContaining(
            keyword, keyword, pageable);
    } else if (keyword != null && !keyword.isBlank()) {
        // TODO 02: 이름/사번 검색.
        page = employeeRepository.____(keyword, keyword, pageable);
    } else if (departmentId != null) {
        page = employeeRepository.findByDepartment_Id(departmentId, pageable);
    } else {
        // TODO 03: 전체 조회.
        page = employeeRepository.____(pageable);
    }

    // TODO 04: Page<Entity> → Page<DTO> (메타 유지).
    return page.____(EmployeeResponse::from);
}

// 학습 질문 (직접 답):
// Q1. Controller 가 Pageable 을 그대로 받으면 클라이언트는 어떤 쿼리스트링을 보내나?
//     A:
// Q2. List 가 아니라 Page 로 반환하는 이유는?
//     A:
// Q3. 정렬을 클라이언트가 자유 지정할 때의 위험은?
//     A:

// 자가 채점:
// □ readOnly=true  □ findByUser_NameContainingOrEmployeeNumberContaining  □ findAll(pageable)  □ page.map(...)
