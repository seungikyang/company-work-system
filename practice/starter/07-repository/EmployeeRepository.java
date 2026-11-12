// 중학생 눈높이: 직원 찾기
// Repository는 DB에서 자료를 찾아오거나 저장하는 담당자입니다.
// 메서드는 이름이 있는 작업입니다. 검색 메서드의 이름에는 무엇을 찾을지 조건이 담깁니다.
// 찾을 조건 읽기 → 필요한 입력값 보기 → 돌려받는 결과의 종류 확인 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/repository/EmployeeRepository.java
// 목표: 직원 검색 / 페이징 / 부서별 조회를 위한 메서드 쿼리를 채우세요.
//       PRD 2.5.2, TRD 3.7.2 참고.

// 초보자 안내 (07장): Spring Data JPA Repository
// 개념: Repository는 저장·조회 작업을 호출하는 창구입니다. Spring Data JPA는 인터페이스의 구현을 만들어 줍니다.
// 사용 이유: 기본 CRUD와 단순 검색에 반복되는 DB 접근 코드를 줄입니다.
// 동작 흐름: findByUser_Email의 User_Email은 Employee → User → email 속성 경로입니다. 메서드 이름에서 조건을 해석하고 실제 SQL 처리는 JPA 구현체가 맡습니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

// interface는 호출할 기능의 약속입니다. JpaRepository의 두 타입은 저장할 객체와 식별자 타입입니다.
// 메서드 이름의 속성 경로를 Spring Data JPA가 해석하므로 Entity의 실제 필드 이름과 맞아야 합니다.
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

    // 4지선다 학습 문제 (자동 생성 시작)
    // 선택·채점 화면 (저장소 루트 기준): problems.html#problem-07
    //
    // Q1. findByUser_Email의 밑줄이 의미하는 것은? [chapter-v1-07-05]
    // ① DB 컬럼명에 반드시 밑줄을 추가한다.
    // ② email 값의 공백을 자동 제거한다.
    // ③ Employee의 user 관계를 거쳐 email 속성으로 탐색하는 경계를 명시한다.
    // ④ Repository 메서드를 비동기로 실행한다.
    // 정답: ③
    // 해설: 파생 쿼리는 엔티티 속성 경로를 파싱한다. 밑줄은 중첩 속성의 탐색 경계를 명확히 하는 데 사용한다.
    //
    // Q2. existsByX와 findByX 중 중복 여부만 필요한 경우 exists 조회가 적절한 이유는? [chapter-v1-07-06]
    // ① exists는 어떤 DB에서도 항상 같은 SQL과 같은 실행 비용을 보장한다.
    // ② exists 조회 자체가 동시 등록의 unique를 대체한다.
    // ③ find 조회는 존재하지 않는 자료에서도 반드시 객체를 반환한다.
    // ④ 전체 엔티티 대신 존재 여부를 반환해 불필요한 데이터 로딩을 줄일 수 있다.
    // 정답: ④
    // 해설: 반환 목적에 맞는 조회를 선택하고 실제 SQL·실행 계획을 확인한다. 중복 무결성은 DB 제약으로 보장한다.
    //
    // Q3. 긴 파생 쿼리를 @Query나 Querydsl로 옮길 판단 기준은? [chapter-v1-07-07]
    // ① 복잡한 조건·조인·동적 검색 때문에 메서드 이름의 가독성과 유지보수가 떨어지는지 확인한다.
    // ② 메서드 이름이 정확히 20자를 넘으면 무조건 Querydsl로 바꾼다.
    // ③ @Query로 옮기면 쿼리 검증이 필요 없어지는지 확인한다.
    // ④ Repository 없이 Controller에서 문자열 SQL만 연결한다.
    // 정답: ①
    // 해설: 조건 복잡도와 변경 빈도에 따라 명시적 쿼리나 동적 쿼리를 선택하며 결과·권한·성능을 검증한다.
    //
    // Q4. Page<Employee>를 API 응답에 직접 노출하는 대신 해야 할 일은? [chapter-v1-07-08]
    // ① 모든 관계와 비밀번호를 그대로 직렬화한다.
    // ② 조회·매핑 경계를 정하고 공개 필드만 가진 Page<EmployeeResponse>로 변환한다.
    // ③ Page이면 Entity의 민감 정보가 자동 제거된다고 믿는다.
    // ④ LAZY 관계를 아무 시점에서나 조회하도록 둔다.
    // 정답: ②
    // 해설: 페이지 메타정보는 유지하면서 Entity의 공개 범위·지연 로딩·API 계약을 DTO로 관리한다.
    //
    // 4지선다 학습 문제 (자동 생성 끝)
}
