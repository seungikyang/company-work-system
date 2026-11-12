// 중학생 눈높이: 자료별 DB 담당자
// 계정·부서·휴가·공지·결재마다 DB에서 자료를 찾는 담당자를 정합니다.
// Optional은 결과가 있을 수도 없을 수도 있다는 표시입니다. 없는 경우를 따로 처리해야 합니다.
// 자료 종류 확인 → 메서드 이름의 검색 조건 읽기 → 결과 종류 확인 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: repository/*
// 목표: 전 도메인 Repository 를 거의 백지에서. TRD 3.5.
// 막히면 starter/31-repositories-all, answers.md 31장 참고.

// 초보자 안내 (31장): Repository 역할 분리
// 개념: 각 Repository는 담당 Entity의 저장·조회 조건을 선언합니다.
// 사용 이유: 업무 절차를 Service에 두고 데이터 접근 코드를 대상별로 찾기 쉽게 합니다.
// 동작 흐름: Service가 필요한 Repository를 호출하면 메서드 이름이나 쿼리 정의에 맞춰 조회합니다. 속성 경로는 DB 컬럼명이 아닌 Java Entity 필드 구조와 맞아야 합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-31
//
// Q1. 파생 Repository 메서드가 길어질 때 @Query로 옮기는 적절한 기준은? [chapter-v1-31-05]
// ① 조건·조인 구조를 이름으로 읽기 어렵고 변경 때 실수하기 쉬워 명시적 쿼리가 더 명확한지 판단한다.
// ② 글자 수가 10자를 넘으면 JPA가 실행을 금지한다.
// ③ @Query를 쓰면 파라미터 바인딩을 하지 않아도 안전하다.
// ④ 쿼리를 옮기면 모든 테스트를 삭제한다.
// 정답: ①
// 해설: 가독성·복잡도·동적 조건의 요구를 기준으로 선택하고 파라미터와 결과 계약을 검증한다.
//
// Q2. Spring Data가 파생 쿼리 메서드를 해석하는 방식으로 알맞은 것은? [chapter-v1-31-06]
// ① find·exists 같은 주제와 By 뒤의 엔티티 속성·연산 키워드·And·Or 등을 파싱한다.
// ② 메서드 이름의 모든 단어를 DB의 실제 컬럼 이름으로만 해석한다.
// ③ 메서드 길이로 SQL 결과 건수를 정한다.
// ④ 메서드 이름은 무시하고 첫 번째 테이블만 조회한다.
// 정답: ①
// 해설: 파생 쿼리는 엔티티의 속성 모델을 바탕으로 해석한다. 중첩 경로·키워드·실제 생성 쿼리를 확인한다.
//
// Q3. DB 컬럼명을 바꾸었을 때 파생 쿼리에 미치는 영향을 설명한 것은? [chapter-v1-31-07]
// ① 물리 컬럼명이 바뀌면 어떤 파생 쿼리든 반드시 같은 이름으로 바꿔야 한다.
// ② 엔티티 속성명을 바꿔도 메서드 파싱에는 영향이 없다.
// ③ @Column이 있으면 DB 마이그레이션도 자동으로 불필요해진다.
// ④ 엔티티 속성명이 유지되고 @Column 매핑이 맞으면 메서드 이름은 유지할 수 있지만 속성명 변경은 메서드도 맞춰야 한다.
// 정답: ④
// 해설: 자바 속성과 물리 컬럼의 매핑을 구분한다. 파생 쿼리·JPQL·native SQL에 대한 영향도 각각 검토한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ findByEmail/existsByEmail  □ countByDepartment_Id  □ findByEmployee_Id
// □ 겹침: startDate<=:end AND endDate>=:start  □ @Modifying, +1  □ findByApproverIdAndStatus
