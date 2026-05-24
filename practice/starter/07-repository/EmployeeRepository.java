// 실제 구현 위치 예: src/main/java/com/example/companywork/repository/EmployeeRepository.java
// 목표: 직원 검색 / 페이징 / 부서별 조회를 위한 메서드 쿼리를 채우세요.
//       PRD/TRD 2.5.2, 3.7.2 참고.

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // TODO 01: 이메일 중복 여부를 빠르게 확인하는 메서드 이름은?
    //          User 의 email 에 접근해야 하므로 _ 또는 _ 표기를 이용한 nested property 를 떠올려 보세요.
    boolean ____ByUser_Email(String email);

    // TODO 02: 사번으로 직원이 이미 있는지 확인.
    boolean existsBy____(String employeeNumber);

    // TODO 03: 사번으로 직원 단건 조회.
    Optional<Employee> findBy____(String employeeNumber);

    // TODO 04: 이름에 keyword 가 포함되거나, 사번에 keyword 가 포함되는 직원을 페이징 조회.
    //          (LIKE %keyword% 와 동일)
    Page<Employee> findByUser_NameContainingOrEmployeeNumberContaining(
            String nameKeyword, String employeeNumberKeyword, ____ pageable);

    // TODO 05: 부서별 직원 목록 (페이징).
    Page<Employee> findByDepartment_Id(Long departmentId, Pageable pageable);

    // TODO 06: 재직 중인 직원만 페이징 조회.
    //          EmployeeStatus.ACTIVE 만 보여주려면?
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    // 학습 메모:
    // - 검색 조건이 동적이면 Querydsl 또는 JPA Criteria, Specification 으로 옮기는 편이 안전합니다.
    // - 메서드 이름이 너무 길어지면 @Query("SELECT e FROM Employee e ...") 로 풀어쓰는 것도 방법입니다.
    // - Sort 기준을 클라이언트가 자유롭게 지정하게 두면, 인덱스 없는 컬럼으로 정렬당해 느려질 수 있습니다.
    //   허용 컬럼 목록을 두는 패턴을 검토하세요.
}
