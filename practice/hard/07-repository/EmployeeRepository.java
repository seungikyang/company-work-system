// 실제 구현 위치 예: src/main/java/com/example/companywork/repository/EmployeeRepository.java
// 목표: 직원 검색/페이징/부서별 조회 메서드 쿼리를 거의 백지에서. PRD 2.5.2, TRD 3.7.2.
// 막히면 starter/07-repository, answers.md 7장 참고.

public interface EmployeeRepository extends ____<Employee, Long> {

    // TODO 01: 이메일 중복 확인. User.email 로 들어가는 nested property(_).
    boolean ____ByUser_____(String email);

    // TODO 02: 사번 중복 확인.
    boolean ____By____(String employeeNumber);

    // TODO 03: 사번 단건 조회.
    ____<Employee> ____By____(String employeeNumber);

    // TODO 04: 이름 또는 사번에 keyword 포함, 페이징.
    ____<Employee> findByUser_____ContainingOr____Containing(
            String nameKeyword, String employeeNumberKeyword, ____ pageable);

    // TODO 05: 부서별 직원(페이징).
    ____<Employee> findBy____(Long departmentId, Pageable pageable);

    // TODO 06: 상태별(재직 ACTIVE) 페이징.
    Page<Employee> findBy____(EmployeeStatus status, Pageable pageable);
}

// 학습 질문 (직접 답):
// Q1. findByUser_Email 의 밑줄(_)은 무엇을 의미하나?
//     A:
// Q2. existsByX 와 findByX 의 비용 차이는? 중복검사에 exists 가 유리한 이유?
//     A:
// Q3. 메서드 이름이 길어지면 언제 @Query/Querydsl 로 옮기나?
//     A:

// 자가 채점:
// □ JpaRepository 상속  □ existsByUser_Email  □ existsByEmployeeNumber  □ findByEmployeeNumber
// □ findByUser_NameContainingOrEmployeeNumberContaining + Pageable  □ findByDepartment_Id  □ findByStatus
