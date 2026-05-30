// 실제 구현 위치 예: repository/*
// 목표: 전 도메인 Repository 를 거의 백지에서. TRD 3.5.
// 막히면 starter/31-repositories-all, answers.md 31장 참고.

public interface UserRepository extends JpaRepository<User, Long> {
    // TODO 01: 로그인용 이메일 조회.
    ____<User> findBy____(String email);
    // TODO 02: 중복 검사.
    boolean existsBy____(String email);
}

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
}

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByUser_Email(String email);
    boolean existsByEmployeeNumber(String employeeNumber);
    Optional<Employee> findByUser_Id(Long userId);
    // TODO 03: 부서별 직원 수(삭제 검증).
    long ____By____(Long departmentId);
    Page<Employee> findByDepartment_Id(Long departmentId, Pageable pageable);
    Page<Employee> findByUser_NameContainingOrEmployeeNumberContaining(String n, String e, Pageable pageable);
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
}

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    // TODO 04: 내 휴가.
    Page<LeaveRequest> findBy____(Long employeeId, Pageable pageable);
    long countByEmployee_Id(Long employeeId);
    Page<LeaveRequest> findByStatus(ApprovalStatus status, Pageable pageable);
    Page<LeaveRequest> findByStatusAndEmployee_Id(ApprovalStatus status, Long employeeId, Pageable pageable);

    // TODO 05: 기간 겹침 — start <= :endDate AND end >= :startDate.
    @Query("""
        SELECT COUNT(l) > 0 FROM LeaveRequest l
        WHERE l.employee.id = :employeeId
          AND l.status IN (:statuses)
          AND l.startDate ____ :endDate
          AND l.endDate   ____ :startDate
        """)
    boolean existsOverlapping(@Param("employeeId") Long employeeId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("statuses") List<ApprovalStatus> statuses);
}

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // TODO 06: 조회수 원자적 증가.
    @____
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount ____ 1 WHERE n.id = :id")
    int increaseViewCount(@Param("id") Long id);
}

public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Long> {
    Page<ApprovalDocument> findByWriterId(Long writerId, Pageable pageable);
    Page<ApprovalDocument> findByWriterIdAndStatus(Long writerId, ApprovalStatus status, Pageable pageable);
    // TODO 07: 결재자 + 상태.
    Page<ApprovalDocument> findBy____And____(Long approverId, ApprovalStatus status, Pageable pageable);
}

// 학습 질문 (직접 답):
// Q1. 메서드 이름이 너무 길면 언제 @Query 로?
//     A:
// Q2. Spring Data 가 메서드 이름을 파싱하는 규칙 한 줄.
//     A:
// Q3. 컬럼명이 바뀌면 메서드 이름 쿼리는 어떻게 깨지나? (필드명 vs 컬럼명)
//     A:

// 자가 채점:
// □ findByEmail/existsByEmail  □ countByDepartment_Id  □ findByEmployee_Id
// □ 겹침: startDate<=:end AND endDate>=:start  □ @Modifying, +1  □ findByApproverIdAndStatus
