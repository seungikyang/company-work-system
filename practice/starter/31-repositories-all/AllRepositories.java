// 실제 구현 위치 예: src/main/java/com/example/companywork/repository/*
// 목표: PRD/TRD 의 모든 도메인 Repository 를 메서드 이름 쿼리로 정리하세요.
//       07-repository 의 EmployeeRepository 와 짝이 되는 다섯 가지 Repository 입니다.

// =====================================================================
// 1. UserRepository
// =====================================================================
public interface UserRepository extends JpaRepository<User, Long> {

    // TODO 01: 로그인용 이메일 조회.
    Optional<User> findBy____(String email);

    // TODO 02: 회원가입 시 중복 검사.
    boolean existsBy____(String email);
}

// =====================================================================
// 2. DepartmentRepository
// =====================================================================
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // TODO 03: 부서명 중복 검사.
    boolean existsByName(String name);

    // TODO 04: 부서명으로 조회. (관리자 화면에서 자동완성 등에 사용)
    Optional<Department> findByName(String name);
}

// =====================================================================
// 3. EmployeeRepository (07 에서 다룬 메서드와 일관성 맞추기)
// =====================================================================
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByUser_Email(String email);
    boolean existsByEmployeeNumber(String employeeNumber);
    Optional<Employee> findByUser_Id(Long userId);

    // TODO 05: 부서별 직원 수 — 부서 삭제 검증에 사용.
    long countByDepartment_Id(Long departmentId);

    Page<Employee> findByDepartment_Id(Long departmentId, Pageable pageable);
    Page<Employee> findByUser_NameContainingOrEmployeeNumberContaining(
            String nameKeyword, String employeeNumberKeyword, Pageable pageable);
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);
}

// =====================================================================
// 4. LeaveRequestRepository
// =====================================================================
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // TODO 06: 내 휴가 목록 (페이징).
    Page<LeaveRequest> findByEmployee_Id(Long employeeId, Pageable pageable);

    // TODO 07: 직원이 삭제 가능한지 검사할 때 사용 — 휴가 기록 수.
    long countByEmployee_Id(Long employeeId);

    // TODO 08: 관리자용 — 상태별 / 직원별 / (둘 다) 조회.
    Page<LeaveRequest> findByStatus(ApprovalStatus status, Pageable pageable);
    Page<LeaveRequest> findByStatusAndEmployee_Id(ApprovalStatus status, Long employeeId, Pageable pageable);

    // TODO 09: 기간 겹침 검사 — 같은 직원이 이미 휴가 신청한 기간과 겹치는지.
    //          start_date <= :endDate AND end_date >= :startDate 조건.
    @Query("""
        SELECT COUNT(l) > 0 FROM LeaveRequest l
        WHERE l.employee.id = :employeeId
          AND l.status IN (:statuses)
          AND l.startDate <= :endDate
          AND l.endDate   >= :startDate
        """)
    boolean existsOverlapping(@Param("employeeId") Long employeeId,
                              @Param("startDate") LocalDate startDate,
                              @Param("endDate") LocalDate endDate,
                              @Param("statuses") List<ApprovalStatus> statuses);
}

// =====================================================================
// 5. NoticeRepository
// =====================================================================
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // TODO 10: 중요 공지를 위로 끌어올리려면 보통 어디서(Service 정렬 / Repository 쿼리) 해결할까요?
    //          단순 정렬은 Pageable + Sort 로 충분합니다.

    // TODO 11: 조회수 원자적 증가 — 동시성 충돌 회피.
    @Modifying
    @Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.id = :id")
    int increaseViewCount(@Param("id") Long id);
}

// =====================================================================
// 6. ApprovalDocumentRepository
// =====================================================================
public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Long> {

    // TODO 12: 내가 작성한 문서 목록.
    Page<ApprovalDocument> findByWriterId(Long writerId, Pageable pageable);
    Page<ApprovalDocument> findByWriterIdAndStatus(Long writerId, ApprovalStatus status, Pageable pageable);

    // TODO 13: 내가 결재할 문서 — 결재자 + 상태 조건.
    Page<ApprovalDocument> findByApproverIdAndStatus(Long approverId, ApprovalStatus status, Pageable pageable);

    // 학습 질문 (한 줄 답):
    // Q1. 메서드 이름이 너무 길어지면 어떤 시점에 @Query 로 옮길까?
    //     A:
    // Q2. Spring Data JPA 가 메서드 이름을 어떤 규칙으로 파싱하는지 한 줄로 적어 보세요.
    //     A:
    // Q3. 중간에 컬럼명이 바뀌었을 때, 메서드 이름 쿼리는 어떻게 깨지는가? (Property → Column 매핑 관점)
    //     A:
    //
    // 심화 노트 (면접 답변 포인트):
    // - 메서드 이름 파싱: 키워드(findBy/existsBy/countBy + And/Or/Containing/OrderBy)와 엔티티 "속성명"으로 JPQL 생성.
    //   DB 컬럼명(view_count)이 아니라 필드명(viewCount) 기준 → @Column 매핑이 컬럼 변경을 흡수한다.
    //   하지만 필드명을 바꾸면 메서드 이름도 함께 깨진다(컴파일타임 검증이 약함).
    // - @Modifying 벌크 UPDATE(increaseViewCount)는 영속성 컨텍스트를 우회한다 → 1차 캐시와 불일치 가능.
    //   필요하면 @Modifying(clearAutomatically = true).
    // - existsOverlapping 의 (start <= :end AND end >= :start) 가 "두 구간이 겹친다"의 표준 조건.
    // - 조건이 동적(있을 수도 없을 수도)이면 메서드 이름 폭발 → Querydsl(타입 안전) 로 옮기는 게 정석.
}
