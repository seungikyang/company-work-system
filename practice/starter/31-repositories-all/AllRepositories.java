// 중학생 눈높이: 자료별 DB 담당자
// 계정·부서·휴가·공지·결재마다 DB에서 자료를 찾는 담당자를 정합니다.
// Optional은 결과가 있을 수도 없을 수도 있다는 표시입니다. 없는 경우를 따로 처리해야 합니다.
// 자료 종류 확인 → 메서드 이름의 검색 조건 읽기 → 결과 종류 확인 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/repository/*
// 목표: PRD/TRD 의 모든 도메인 Repository 를 메서드 이름 쿼리로 정리하세요.
//       07-repository 의 EmployeeRepository 와 짝이 되는 다섯 가지 Repository 입니다.

// 초보자 안내 (31장): Repository 역할 분리
// 개념: 각 Repository는 담당 Entity의 저장·조회 조건을 선언합니다.
// 사용 이유: 업무 절차를 Service에 두고 데이터 접근 코드를 대상별로 찾기 쉽게 합니다.
// 동작 흐름: Service가 필요한 Repository를 호출하면 메서드 이름이나 쿼리 정의에 맞춰 조회합니다. 속성 경로는 DB 컬럼명이 아닌 Java Entity 필드 구조와 맞아야 합니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

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
    // 이 JPQL은 테이블명이 아니라 Entity 이름과 속성을 사용합니다. :employeeId 같은 이름은 @Param에 연결됩니다.
    // 기간 겹침은 기존 시작일 ≤ 새 종료일 AND 기존 종료일 ≥ 새 시작일 조건으로 판단합니다.
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
