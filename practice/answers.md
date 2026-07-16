# 정답 방향 (Answers)

빈칸의 키워드 자체보다 **왜 그렇게 채워야 하는가**를 설명합니다. 이 문서는 복사할 완성 코드가 아니라 직접 풀이 후 설계 판단을 교정하고 면접 언어로 바꾸기 위한 해설서입니다.

## 해설 사용 규칙

- 문제를 직접 풀고 막힌 지점을 기록한 뒤 확인합니다.
- 정답 키워드만 옮기지 않고 선택 이유와 대안을 본인 말로 다시 씁니다.
- 해설의 예시를 실행 프로젝트에 적용했다면 관련 테스트를 추가합니다.
- 이해한 내용은 [취업 준비 통합 워크북](./job-preparation-workbook.md)의 기능별 증거나 면접 답변에 연결합니다.

## 해설 반영 기록

해설을 읽은 뒤 정답 문장을 복사하지 말고 같은 증거 ID에 아래 내용을 남깁니다.

| 항목 | 직접 작성 |
|---|---|
| 증거 ID |  |
| 내가 처음 선택한 방식 |  |
| 해설을 보고 바꾼 판단 |  |
| 고려한 대안과 트레이드오프 |  |
| 추가하거나 수정한 테스트 |  |
| 면접에서 설명할 한 문장 |  |

---

## 0. 빌드와 설정

- `spring-boot-starter-web` 은 Tomcat + Spring MVC + Jackson 을 묶어 REST API 와 Thymeleaf 렌더링을 가능하게 한다.
- `spring-boot-starter-data-jpa` 는 EntityManager 와 Hibernate, Repository 인터페이스 자동 구현을 제공한다.
- `spring-boot-starter-validation` 은 `@Valid` 와 Hibernate Validator(jakarta.validation) 를 활성화한다.
- `h2` 는 인메모리/파일 DB 로, 로컬과 테스트에서 별도 설치 없이 빠르게 띄울 수 있다.
- `spring-boot-starter-thymeleaf` 는 서버 렌더링용. 화면 요구사항(2.7) 을 위해 사용한다.
- `ddl-auto: create` 는 시작할 때마다 스키마를 새로 만들기 때문에 로컬 학습용에 가깝다. 운영은 `validate` 또는 `none` 이 안전하다.
- Spring Boot 3.x 부터 패키지가 `javax.*` → `jakarta.*` 로 바뀌었다. 검증 어노테이션도 `jakarta.validation.constraints` 를 쓴다.

**심화·면접 답변:**
- **스타터(starter) vs 라이브러리**: 스타터는 "함께 쓰이는 의존성 묶음 + 자동설정(auto-configuration)" 이다. `starter-web` 하나로 Tomcat·MVC·Jackson 이 따라오고, classpath 에 있으면 Spring Boot 가 알아서 DispatcherServlet 등을 구성한다. 면접에서는 "버전 호환이 검증된 묶음을 자동설정과 함께 받는 것" 이라고 답하면 충분하다.
- **dependency-management 플러그인 / BOM**: Spring Boot BOM 이 각 라이브러리의 호환 버전을 고정해 주므로 `implementation 'org.springframework.boot:spring-boot-starter-web'` 처럼 버전을 생략한다. 버전을 직접 박으면 BOM 이 검증한 조합을 깨서 런타임 `NoSuchMethodError` 같은 사고가 난다.
- **ddl-auto 비교**: `create`(시작 시 drop 후 생성), `create-drop`(종료 시 추가로 drop), `update`(차이만 반영, 운영 금지 — 컬럼 삭제/타입변경을 놓침), `validate`(스키마만 검증, 변경 안 함), `none`(아무것도 안 함). → 로컬 학습은 `create`, 운영은 마이그레이션 도구(Flyway/Liquibase) + `validate`.
- **show-sql vs 로깅**: `show-sql` 은 `System.out` 으로 찍어 파라미터 바인딩이 안 보인다. 운영 진단은 `logging.level.org.hibernate.SQL=debug` + `...type.descriptor.sql.BasicBinder=trace` 가 정석. 운영에서 show-sql 은 성능/로그오염으로 끈다.
- **함정**: H2 콘솔(`enabled: true`)을 운영에 켜두면 보안 구멍이다. 로컬 프로파일에서만 켜라.

## 1. User

- `users` 가 SQL 예약어 충돌을 피하는 가장 흔한 테이블명이다.
- 비밀번호는 `BCryptPasswordEncoder.encode(...)` 로 해시한 결과만 저장한다. 평문 저장은 PRD 비기능 요구사항(2.6 보안) 위반.
- `@Enumerated(EnumType.STRING)` 을 쓰면 DB 에 `USER`, `ADMIN`, `APPROVER` 가 그대로 들어가서 enum 순서가 바뀌어도 데이터가 깨지지 않는다.
- `createdAt`, `updatedAt` 은 `@CreatedDate`, `@LastModifiedDate` + `@EnableJpaAuditing` 으로 자동화하거나, `@PrePersist`/`@PreUpdate` 로 수동 세팅한다.

**심화·면접 답변:**
- **User/Employee 분리 (면접 Q3)**: 로그인 계정(인증)과 인사 정보(업무 데이터)는 라이프사이클·책임이 다르다. 계정은 잠금/비번변경 같은 보안 관심사, 직원은 부서/사번/입사일 같은 인사 관심사. 한 테이블에 섞으면 단일 책임이 깨지고, 권한 컬럼과 인사 컬럼이 한 곳에서 변경되어 변경 영향 범위가 커진다. 1:1 로 나누면 각 테이블이 한 가지 이유로만 변한다.
- **EnumType.STRING 을 쓰는 진짜 이유**: `ORDINAL` 은 enum 의 "순서(0,1,2)" 를 저장한다. 운영 중 `UserRole` 중간에 값을 추가하거나 순서를 바꾸면, 이미 저장된 0,1,2 의 의미가 통째로 어긋나 **조용한 데이터 오염**이 난다. STRING 은 `'ADMIN'` 문자열을 저장하므로 순서 변경에 안전하다. (대가: 약간의 저장공간/인덱스 비용)
- **password 길이 255**: BCrypt 해시는 `$2a$10$...` 형태로 60자다. 지금은 60이면 충분하지만, 알고리즘 교체(Argon2 등)나 prefix 변화에 대비해 255 로 여유를 둔다. **평문 길이가 아니라 해시 결과 길이** 기준으로 잡는다는 점이 핵심.
- **Auditing 동작 조건**: `@CreatedDate`/`@LastModifiedDate` 는 `@EntityListeners(AuditingEntityListener.class)` + 설정 클래스에 `@EnableJpaAuditing` 이 둘 다 있어야 채워진다. 하나라도 빠지면 `createdAt` 이 null 로 들어간다(33장 트러블슈팅과 직결). `updatable=false` 로 생성시각이 수정 때 덮어쓰이지 않게 막는다.

## 2. Department

- 부서명이 중복되면 같은 이름의 두 부서가 생겨 사용자가 혼란스럽다. `@Column(unique = true)` 또는 `@UniqueConstraint` 로 막는다.
- 양방향 매핑은 편하지만 직렬화 무한 루프, 페치 비용 증가, 양쪽 동기화 부담이 생긴다. 학습용으로는 **Employee → Department 단방향**으로 두고, 부서별 직원 목록은 Repository 쿼리로 조회하는 편이 단순하다.

**심화·면접 답변:**
- **exists 검사 + DB unique 다층 방어**: Service 의 `existsByName` 은 "친절한 에러 메시지(`DUPLICATE_DEPARTMENT_NAME`)" 를 주기 위한 1차 방어다. 하지만 두 요청이 동시에 `exists`(둘 다 false) → 둘 다 `insert` 하는 race 가 가능하다. 이때 DB unique 제약이 **최종 방어선**으로 두 번째 insert 를 `DataIntegrityViolationException` 으로 막는다. → "검증은 Service, 보장은 DB" 라고 답한다.
- **trim 을 도메인에 두는 이유**: 정규화(공백 제거)를 엔티티 정적 팩토리에 두면, 어느 호출 경로로 들어와도 같은 규칙이 적용된다. Service 마다 trim 을 반복하면 한 곳을 빠뜨리는 순간 `"개발팀"` 과 `"개발팀 "` 이 다른 부서로 갈린다.
- **양방향 vs 단방향 (트레이드오프)**: 양방향(`@OneToMany mappedBy`)은 `department.getEmployees()` 가 편하지만 → JSON 직렬화 무한루프, 부서 조회 시 직원까지 끌려오는 페치 비용, 양쪽 컬렉션 동기화 부담. 단방향 + 쿼리(`employeeRepository.findByDepartment_Id`)가 학습/유지보수에 단순하다.
- **함정**: `@Column(unique=true)` 만으로도 unique 인덱스가 생기지만, 제약 **이름**을 통제하려면 `@Table(uniqueConstraints=@UniqueConstraint(name=...))` 가 낫다. 에러 로그/마이그레이션에서 제약 이름이 의미를 가진다.

## 3. Employee

- User 와 Employee 를 분리한 이유: 로그인 계정과 인사 정보의 라이프사이클이 다르고, 권한과 인사 데이터를 같은 테이블에 두면 책임이 섞인다(면접 Q3).
- `fetch = LAZY` 가 기본인 이유: 직원 목록을 조회할 때마다 매번 부서까지 join 으로 끌어오면 N+1 또는 불필요한 부하가 생긴다.
- `RESIGNED` 상태로 두는 이유: 휴가/결재/공지 등 과거 데이터에 남은 직원 참조를 깨지 않기 위해 soft delete 가 안전하다.

**심화·면접 답변:**
- **LAZY 와 N+1**: `@ManyToOne` 기본값은 EAGER 다. 직원 목록 100건을 EAGER 로 조회하면, 직원 1번 쿼리 + 부서를 채우려는 N번 쿼리 = **N+1 문제**가 터진다. LAZY 로 두면 부서는 실제로 접근할 때만 로딩된다. 목록 화면에서 부서명이 꼭 필요하면 `fetch join` 또는 `@EntityGraph` 로 한 번에 가져온다.
- **soft delete(RESIGNED) vs hard delete**: 퇴사 직원이 과거에 신청한 휴가/작성한 결재 문서가 그 직원을 FK 로 참조한다. hard delete 하면 그 참조가 깨지거나 FK 제약으로 삭제 자체가 막힌다. `RESIGNED` 상태로 남기면 이력이 보존되고, `isActive()` 로 신규 신청만 차단할 수 있다.
- **도메인 메서드 vs setter**: `resign()`, `changeDepartment()` 처럼 의미 있는 메서드를 두면 "상태를 바꾸는 규칙" 이 엔티티 안에 모인다. Service 가 `setStatus(RESIGNED)` 를 아무 데서나 부를 수 있게 두면, 검증 없이 상태가 바뀌는 경로가 생겨 불변식이 깨진다. 면접에서는 "setter 를 막고 의도가 드러나는 도메인 메서드를 노출한다" 고 답한다.
- **1:1 매핑 주의**: `@OneToOne` + `@JoinColumn(unique=true)` 로 한 User 가 한 Employee 만 갖게 강제한다. 1:1 도 LAZY 가 항상 동작하지 않을 수 있는 점(역방향 1:1 프록시 한계)은 심화 주제로 기억해 두면 좋다.

## 4. LeaveRequest

- 휴가 기간은 시간 정보가 필요 없으므로 `LocalDate` 가 자연스럽다.
- 정적 팩토리(`LeaveRequest.create(...)`)에서 `status = ApprovalStatus.PENDING` 으로 시작하도록 강제하면 호출부에서 상태 초기화를 잊을 일이 없다.
- `rejectReason` 은 `APPROVED` 일 때 null, `REJECTED` 일 때 필수다. → Service 검증에서 강제한다.
- 시작일 > 종료일 검증은 Service 의 `validateDateRange` 에서 처리하고 `INVALID_DATE_RANGE` 에러로 변환한다.

**심화·면접 답변:**
- **상태 검증을 도메인 메서드에 두는 이유 (트러블슈팅 3.16.1 직결)**: `approve()`/`reject()` 안에서 `status != PENDING` 을 검증하면, 어떤 호출 경로로 들어와도 이미 승인된 휴가를 다시 승인하는 사고를 막는다. Service 의 if 문에만 두면, 다른 Service 메서드가 그 검증을 빠뜨리고 엔티티를 건드리는 순간 **중복 승인 버그**가 가능해진다. → "검증을 데이터(엔티티) 옆에 두어야 우회가 불가능하다."
- **enum 공유 + DRAFT 위험**: 휴가와 결재가 `ApprovalStatus` 를 공유하면 코드/상태표가 일관되어 좋지만, 휴가에는 의미 없는 `DRAFT` 가 타입상 노출된다. → 휴가 생성은 항상 `PENDING` 으로 시작하는 정적 팩토리로 강제해, DRAFT 가 휴가에 들어갈 경로 자체를 없앤다.
- **동시 승인 (동시성)**: 두 관리자가 같은 PENDING 휴가를 동시에 읽고 둘 다 approve 하면, 상태 검증을 둘 다 통과해 마지막 쓰기가 이긴다. 막는 법: **낙관적 락** `@Version`(충돌 시 `OptimisticLockException`) 또는 비관적 락 `SELECT ... FOR UPDATE`. 학습 포트폴리오에선 "낙관적 락으로 막을 수 있다" 정도로 답하면 충분.
- **취소 상태 설계**: PENDING 취소를 `REJECTED` 로 재사용하면 "관리자 반려" 와 "본인 취소" 가 통계에서 구분되지 않는다. 정확하게 가려면 별도 `CANCELED` 를 추가한다 — 단, enum 확장 시 기존 화면/필터 영향을 함께 본다.

## 5. Notice

- 공지 본문은 길어질 수 있으니 `VARCHAR` 가 아니라 `TEXT`/`@Lob` 으로 둔다. `columnDefinition = "TEXT"` 가 가장 명시적이다.
- 중요 공지를 위로 올리려면 `ORDER BY important DESC, createdAt DESC` 같은 복합 정렬을 쓴다.
- `viewCount` 를 단순히 `+1` 하면 동시에 두 요청이 같은 값을 읽고 같은 값을 쓰는 경합이 생긴다. JPQL `UPDATE notice SET view_count = view_count + 1 WHERE id = :id` 같은 원자적 증가 쿼리를 쓰는 편이 안전하다.

**심화·면접 답변:**
- **@Lob / TEXT 기준**: `VARCHAR(255)` 를 넘길 가능성이 있는 본문은 `TEXT`(`columnDefinition = "TEXT"`)나 `@Lob` 으로 둔다. 길이를 짧게 잡으면 긴 공지 저장 시 `value too long` 예외가 난다. `columnDefinition="TEXT"` 가 DB 의도가 가장 명시적.
- **조회수 비원자적 증가가 누락되는 과정**: 요청 A 가 `view_count=10` 을 읽음 → 요청 B 도 `10` 을 읽음 → A 가 `11` 저장 → B 도 `11` 저장. 두 번 조회됐는데 11. JPQL `UPDATE ... SET view_count = view_count + 1` 은 DB 가 현재값 기준으로 원자적으로 더하므로 12 가 된다. (31장 `@Modifying @Query` 와 연결)
- **조회수 증가 위치**: GET 상세 조회 안에서 올리면 검색봇/링크 미리보기/새로고침으로 부풀려진다. 대안: 별도 `PATCH /notices/{id}/view`, 또는 동일 사용자·세션의 중복 집계 방지. 면접에서는 "GET 은 멱등(idempotent)해야 하는데 조회수 증가는 부수효과라 설계가 어긋난다" 가 좋은 답.
- **중요 공지 정렬**: `important` 가 boolean 이라도 true 를 위로 올리려면 `ORDER BY important DESC, created_at DESC`. `Sort.by(DESC, "important").and(Sort.by(DESC, "createdAt"))`.

## 6. ApprovalDocument

- 상태 전이 그래프:
  - `DRAFT` → `PENDING` (결재 요청)
  - `PENDING` → `APPROVED` (승인)
  - `PENDING` → `REJECTED` (반려)
  - 그 외 전이는 모두 차단.
- 작성자=결재자 차단은 Service 의 `validateApprover` 에서 한다. DB 제약으로는 표현이 까다롭다.
- `approvedAt` 은 승인 시점에만 채워지므로 nullable 이다.

**심화·면접 답변:**
- **허용되지 않는 전이 예 + 방어 위치**: `APPROVED → PENDING`(승인 후 재요청), `REJECTED → APPROVED`(반려 후 승인), `DRAFT → APPROVED`(요청 없이 승인) 등은 모두 차단. 가장 견고한 위치는 **Entity 도메인 메서드**(`submit()`/`approve()`/`reject()` 가 진입 상태를 검증). Service 는 권한·소유자 검증을 더하고, DB 는 표현이 어렵다.
- **작성자=결재자 금지 위치**: `createDraft()` 에서 `writerId.equals(approverId)` 로 막으면 어느 경로든 동일하게 적용된다. DB 제약(CHECK)으로도 가능하지만 JPA/H2 호환·메시지 품질 때문에 도메인 검증이 실무적으로 낫다.
- **approvedAt nullable**: 승인 전에는 "승인 시각" 이 존재하지 않는다. NOT NULL + 기본값(now)으로 두면 "아직 승인 안 된 문서도 승인시각이 있는" 모순이 생긴다. null = "아직 승인 안 됨" 이라는 도메인 의미를 그대로 표현.
- **DRAFT/PENDING 분리 효용**: 임시저장(작성 중) 과 결재 진행(상신됨) 을 구분해 "작성하다 만 문서" 를 자연스럽게 표현한다.
- **Long 비교 함정 (== vs equals)**: `writerId == approverId` 는 객체 참조 비교다. `Long` 은 -128~127 만 캐시되어 그 범위 밖 값은 같은 숫자라도 == 가 false 다. 반드시 `.equals()` 또는 `longValue()` 비교. 면접 단골 질문.

## 7. Repository

- 메서드 이름 쿼리는 Spring Data JPA 가 메서드 이름의 키워드(`existsBy`, `findBy`, `Containing`, `OrderBy` 등)를 보고 JPQL 을 자동 생성한다.
- 검색 쿼리는 `findByNameContainingOrEmailContainingOrDepartment_NameContaining(...)` 처럼 길어질 수 있다. 조건이 동적이면 Querydsl/JPA Criteria 로 옮긴다.
- 페이징은 `Page<Employee> findAll(Pageable pageable)` 처럼 시그니처만 맞추면 정렬과 페이지 메타 정보를 자동으로 채워 준다.
- `findByDepartmentId(Long departmentId, Pageable pageable)` 처럼 FK 컬럼명 그대로 쓰면 된다.

**심화·면접 답변:**
- **nested property(밑줄 _)**: `findByUser_Email` 의 `_` 는 "Employee 의 user 의 email" 처럼 연관 엔티티 속성을 타고 들어가라는 표시다. `_` 가 없으면 Spring Data 가 `userEmail` 이라는 단일 속성을 먼저 찾으려다 모호해질 수 있어, 경계를 명시할 때 `_` 를 쓴다.
- **exists vs find 비용**: `existsByX` 는 `SELECT 1 ... LIMIT 1` 에 가깝게 "있는지" 만 확인하고 엔티티를 적재하지 않는다. 중복 검사처럼 존재 여부만 필요할 때 `findByX().isPresent()` 보다 가볍다.
- **메서드 이름 → @Query 전환 기준**: 조건이 3개를 넘거나, OR/그룹핑이 섞이거나, 동적(있을 수도 없을 수도)이면 메서드 이름이 폭발한다. 이때 `@Query`(정적·복잡 고정 쿼리) 또는 Querydsl/Specification(동적 조건)으로 옮긴다. → "가독성이 깨지고 동적 조건이 필요해지는 지점" 이 전환선.
- **Page<Entity> 직접 반환 금지**: 응답으로 엔티티를 그대로 내보내면 password 등 내부 필드 노출 + LAZY 연관을 직렬화하다 `LazyInitializationException`/N+1 이 난다. `page.map(EmployeeResponse::from)` 으로 DTO 페이지로 변환한다(메타정보 유지). (32장과 연결)
- **Sort 보안**: 클라이언트가 `sort=` 로 임의 컬럼을 지정하게 두면 인덱스 없는 컬럼 정렬로 느려지거나 내부 컬럼이 노출된다. 허용 컬럼 화이트리스트를 둔다.

## 8. 직원 등록

- `@Transactional` 은 Service 메서드에 단다. Controller 에 달면 메서드 진입 시점에 트랜잭션이 열려서 비즈니스 검증 전에 자원을 잡는다.
- 이메일/사번 중복 검사는 Repository 호출 횟수가 적은 쪽이 효율적이지만, 동시 가입 race 가 있으면 DB unique 제약이 최종 방어선이다.
- User 만 저장되고 Employee 가 실패하면 “계정은 있는데 직원 정보가 없는” 상태가 생긴다 → 트랜잭션으로 묶어서 둘 다 살거나 둘 다 죽도록 한다.
- 트랜잭션 안에서 `RuntimeException` 이 던져지면 기본 rollback. checked exception 은 기본적으로 rollback 되지 않으므로 `rollbackFor = Exception.class` 가 필요한 경우가 있다.

**심화·면접 답변:**
- **왜 둘 다 롤백되나**: `register()` 한 메서드가 하나의 물리 트랜잭션/커넥션을 공유하므로, `User.save` 가 끝났어도 아직 commit 전이다. 이어진 `Employee.save` 에서 RuntimeException 이 나면 메서드를 빠져나가며 rollback 되어 User insert 까지 함께 취소된다. → 트러블슈팅 3.16.2 의 "부분 저장" 이 정확히 이 케이스.
- **save 순서**: `User` 를 먼저 영속화해야 `Employee` 의 FK(`user_id`)가 결정된다. (cascade 로 묶을 수도 있지만 명시적 순서가 학습엔 명확)
- **정규화 위치**: `email.trim().toLowerCase()` 를 **중복검사 전에 1회** 수행 → `User@x.com` 과 `user@x.com` 이 다른 계정으로 갈리는 사고를 막는다.
- **면접 포인트**: "이메일 중복을 Service 에서 검사하는데 unique 제약이 왜 또 필요한가?" → 동시 가입 race 에서 두 요청이 모두 exists=false 를 통과할 수 있고, 그때 DB unique 가 최종 방어선(`DataIntegrityViolationException` → `DUPLICATE_EMAIL` 로 변환).

## 9. 검색 + 페이징

- Controller 에서 `Pageable pageable` 을 그대로 받으면 `?page=0&size=20&sort=name,asc` 가 자동 매핑된다.
- `Page<Employee> page = repo.findByXxx(...)` → `page.map(EmployeeResponse::from)` 으로 변환하면 페이지 메타 정보가 유지된다.
- 정렬을 클라이언트에 전부 맡기면 인덱스 없는 컬럼으로 정렬당할 수 있다. 허용 컬럼 목록을 두는 편이 안전하다.

**심화·면접 답변:**
- **readOnly=true 효과**: Hibernate flush 모드를 `MANUAL` 로 바꿔 dirty checking/flush 를 건너뛴다 → 불필요한 UPDATE 방지·약간의 성능 이득 + "이 메서드는 쓰지 않는다" 는 의도 표현. 조회 전용 메서드엔 습관처럼 붙인다.
- **학습용 if 분기의 한계**: `keyword` 와 `departmentId` 가 **동시에** 들어오면 현재 코드는 부서 조건을 반영하지 못한다(이름/사번 OR 검색만 수행). 실무에서는 Querydsl/Specification 으로 조건을 1회 쿼리에 결합한다. → 면접에서 "동적 조건이 늘면 메서드 쿼리의 한계가 온다" 로 연결.
- **Page.map 의 의미**: `content` 만 DTO 로 바꾸고 `totalElements/totalPages/number/size` 같은 메타는 그대로 유지한다. `List` 로 바꾸면 이 메타가 사라져 프론트 페이지네이션 UI 가 깨진다.

## 10. 휴가 신청

- 시작일 > 종료일 → `INVALID_DATE_RANGE` (400).
- 퇴사 직원은 `EmployeeStatus.RESIGNED` 검사 후 `ACCESS_DENIED` 또는 별도 도메인 에러로 막는다.
- 초기 상태는 Entity 의 정적 팩토리에서 `PENDING` 으로 고정한다. Controller 에서 값을 받지 않는다.

**심화·면접 답변:**
- **날짜 검증의 다층 방어**: DTO `@AssertTrue`(즉시 400, 빠른 실패) → Entity `create`(도메인 불변식, 어느 경로든) → Service(`INVALID_DATE_RANGE` 로 일관 변환). 같은 규칙을 세 계층에 두는 이유는 "각 계층이 자기 책임 안에서 독립적으로 안전" 하기 위해서다(37장 다층 검증 매트릭스).
- **기간 겹침(확장 규칙)**: 같은 직원이 이미 `PENDING/APPROVED` 인 기간에 또 신청하면 막는다. 조건식 `startDate <= :reqEnd AND endDate >= :reqStart` (31장 JPQL).
- **알림 타이밍**: 신청 완료 알림은 **커밋 후**(`@TransactionalEventListener(phase = AFTER_COMMIT)`)에 보낸다. 트랜잭션 안에서 보내면 이후 롤백돼도 알림이 이미 나가 "신청됐다는데 DB엔 없는" 모순이 생긴다.
- **퇴사 직원 차단**: `employee.isActive()` 가 false 면 `ACCESS_DENIED`(또는 도메인 전용 에러). 도메인 메서드로 판단을 캡슐화.

## 11. 휴가 승인/반려

- “PENDING 만 처리 가능” 검증은 도메인 메서드(`leave.approve(approver)`) 안에 두면 호출부가 잊어도 안전하다.
- 반려 사유는 빈 문자열도 NG. `@NotBlank` 검증 + Service 에서 한 번 더 확인.
- 동시 승인 문제는 PESSIMISTIC_WRITE 락 또는 버전 컬럼(`@Version` 으로 낙관적 락) 으로 막는다. 학습용에서는 “문제 인식”까지 적어두는 것만으로 충분하다.

**심화·면접 답변:**
- **도메인 메서드 캡슐화**: `leave.approve(approverId)` 안에 (상태 검증 + 승인자 세팅 + 상태 전이)를 모으면 Service 는 "조회 → 도메인 메서드 호출" 만 한다. Service 마다 검증을 복붙하지 않아도 되고, 검증을 빠뜨릴 경로가 사라진다.
- **낙관적 vs 비관적 락 선택 기준**: 충돌이 드물면 낙관적(`@Version`, 충돌 시 재시도) 이 가볍다. 충돌이 잦고 정합성이 치명적이면 비관적(`SELECT ... FOR UPDATE`). 휴가 승인은 충돌이 드물어 낙관적이 적합 — 면접에서 "둘의 선택 기준은 충돌 빈도" 라고 답한다.
- **반려 사유 이중 방어**: DTO `@NotBlank`(1차, 400 즉시) + 도메인 `reject()` 내부 검증(2차, 서비스 직접 호출 우회 방지).

## 12. 공지사항

- “관리자만 등록” 은 Controller 의 권한 어노테이션/필터로 1차, Service 에서 2차 검증하는 것이 안전하다.
- `ORDER BY important DESC, createdAt DESC` 로 중요 공지를 항상 상단에 둔다.
- 조회수 증가를 상세 조회와 같이 두면 검색 봇 등 의도치 않은 트래픽으로 카운트가 오른다. 별도 PATCH 로 분리하는 편이 깔끔하다.

**심화·면접 답변:**
- **권한 다층 방어**: Controller `@PreAuthorize("hasRole('ADMIN')")` 는 HTTP 경계의 1차 차단. Service `accessGuard.requireAdmin(role)` 은 다른 Service·배치·내부 호출이 우회하지 못하게 하는 2차. Controller 에만 두면 Service 재사용 경로가 무방비가 된다(면접 단골).
- **정렬 인덱스**: `(important DESC, created_at DESC)` 복합 인덱스를 두면 중요공지 우선 정렬의 비용이 낮아진다. boolean 정렬도 인덱스로 커버 가능.
- **조회수 분리 전략**: ① 별도 `@Transactional(REQUIRES_NEW)` 로 본 조회와 독립 커밋, ② 원자적 `UPDATE ... view_count+1`(경합 안전), ③ GET 멱등성을 지키려 `PATCH /notices/{id}/view` 로 분리. 셋의 트레이드오프를 말할 수 있으면 좋다.

## 13. 결재 작성/요청

- 작성과 요청을 분리(`POST /approvals` + `PATCH /approvals/{id}/submit`)하면 “임시 저장” 개념이 자연스럽다. 합치면 API 가 단순하지만 DRAFT 가 의미를 잃는다.
- DRAFT → PENDING 전이 시 제목/내용/결재자 ID 가 모두 채워졌는지 확인한다.
- 작성자=결재자 검증은 Service 에서 `Objects.equals(writerId, approverId)` 로 막는다.

**심화·면접 답변:**
- **이중 방어(작성자=결재자)**: Service 는 `Objects.equals(...)` 로 친절한 에러(`INVALID_INPUT`)를, Entity `createDraft()` 는 불변식으로 어느 경로든 차단. (`Objects.equals` 는 null 안전 + Long 박싱 함정 회피)
- **결재자 ACTIVE 검증**: 결재자가 `RESIGNED` 면 상신 후 영원히 대기하는 문서가 생긴다 → 작성 단계에서 `approver.isActive()` 로 막는다.
- **DRAFT/PENDING 분리 효용**: 임시저장 UX 를 표현하고, `submit()` 시점에 제목/내용/결재자 같은 필수필드를 다시 검증하는 자연스러운 게이트를 만든다.

## 14. 결재 승인/반려

- 본인이 결재자인지 확인하지 않으면, URL 만 알면 누구나 승인할 수 있는 권한 누락 버그가 된다.
- 승인 시 `approvedAt = LocalDateTime.now()` 를 채운다.
- 반려 사유는 `@NotBlank` + Service 검증의 이중 방어선을 둔다.

**심화·면접 답변:**
- **도메인 메서드만으로 부족한 부분**: 권한(`APPROVER/ADMIN`), 트랜잭션 경계, 외부 의존(알림)은 엔티티가 알면 안 되는 관심사라 여전히 Service 책임이다. 엔티티는 "자기 상태 전이 규칙" 만 담는다. → "도메인 메서드로 옮긴다고 Service 가 사라지지 않는다" 가 정확한 답.
- **결재자 본인 검증 위치**: `approve()` 안 `approverId.equals(current)`. 이게 빠지면 결재자가 아닌 APPROVER 도 URL 만 알면 승인하는 권한 누락(IDOR) 버그가 된다.
- **Clock 주입(가산점)**: `LocalDateTime.now()` 를 직접 부르면 테스트에서 `approvedAt` 을 단정하기 어렵다. `Clock` 을 주입하면 테스트에서 시간을 고정해 결정적으로 검증할 수 있다.

## 15. ErrorCode / ErrorResponse

- HTTP status 는 클라이언트/프록시/모니터링 도구가 보는 1차 신호.
- 비즈니스 에러 코드(`EMPLOYEE_NOT_FOUND`)는 같은 status(404) 안에서도 원인을 구분하기 위한 2차 신호.
- `ErrorResponse(status, code, message, timestamp, errors?)` 형태가 무난하다.

**심화·면접 답변:**
- **status vs code 책임 분리**: 404 라는 HTTP status 는 "없음" 만 말한다. `EMPLOYEE_NOT_FOUND` vs `DEPARTMENT_NOT_FOUND` 는 같은 404 안에서 원인을 구분해 클라이언트가 분기/메시지 표시를 할 수 있게 한다. status 는 인프라용, code 는 애플리케이션용.
- **RuntimeException 상속 이유**: 트랜잭션은 기본적으로 `RuntimeException` 에서만 롤백한다. `BusinessException` 을 unchecked 로 두면 Service 어디서 던져도 자동 롤백되고, 호출부가 try-catch 로 더럽혀지지 않는다. Checked 로 만들면 모든 호출부가 catch/throws 를 강제당하고 기본 커밋이라 롤백 누락 위험.
- **stack trace 비노출**: 응답에 stack trace 가 실리면 내부 패키지/쿼리/버전이 새 보안 위협이 된다. 로그(`log.error`)로만 남기고 응답엔 `INTERNAL_ERROR`.
- **스펙 정합**: 4필드(status, code, message, timestamp)는 TRD 3.9 고정. `errors` 는 검증 실패 시에만 추가되는 확장.

## 16. GlobalExceptionHandler

- `@RestControllerAdvice` 가 모든 Controller 의 예외를 가로채서 일관된 응답으로 변환한다.
- `MethodArgumentNotValidException` 의 `BindingResult` 에서 `FieldError` 를 모아 `errors` 리스트로 만든다.
- 마지막 `Exception.class` 핸들러는 스택트레이스를 응답에 노출하지 말고 로그로만 남긴다.

**심화·면접 답변:**
- **@RestControllerAdvice = @ControllerAdvice + @ResponseBody**: 변환 결과를 뷰가 아니라 JSON 으로 응답한다.
- **핸들러 우선순위**: 더 구체적인 예외 타입이 먼저 매칭된다. `BusinessException` → `MethodArgumentNotValidException` → `ConstraintViolationException` → `Exception`(최종 안전망) 순으로 좁은 것부터.
- **검증 실패 수집**: `MethodArgumentNotValidException.getBindingResult().getFieldErrors()` 에서 `FieldError` 를 모아 `(field, message)` 리스트로 변환 → `errors` 에 담는다.
- **Security 예외는 여기서 못 잡을 수 있다**: 인증/인가 실패는 `ExceptionTranslationFilter`(필터 단계)에서 발생해 `@RestControllerAdvice`(서블릿/컨트롤러 단계)보다 앞선다. `AuthenticationEntryPoint`(401)·`AccessDeniedHandler`(403)에서 같은 `ErrorResponse` 포맷으로 변환해야 응답 일관성이 유지된다.

## 17. DTO 검증

- `@NotBlank` 는 null/빈 문자열/공백 모두 거른다. `@NotNull` 보다 문자열에 적합하다.
- `@Email` 은 형식 검사. `@Size(min, max)` 와 함께 쓴다.
- `@Pattern(regexp = ...)` 는 사번 포맷처럼 형식이 고정된 값에 쓴다.
- 요청 DTO 와 응답 DTO 를 분리하면, 응답 필드를 바꿔도 요청 스펙이 깨지지 않고 그 반대도 마찬가지다.

**심화·면접 답변:**
- **세 어노테이션 정확 비교**: `@NotNull`(null 만 차단, 빈 문자열·공백 통과) / `@NotEmpty`(null+빈 문자열 차단, 공백 `" "` 통과) / `@NotBlank`(null+빈+공백 모두 차단). 문자열 필수값엔 `@NotBlank`.
- **@Valid 가 도는 위치**: Controller 파라미터. body 는 `@Valid @RequestBody`, PathVariable/RequestParam 검증은 클래스에 `@Validated` + `ConstraintViolationException` 처리. 즉 검증은 "웹 경계(Controller)" 에서 발동한다.
- **요청/응답 DTO 분리 이유**: ① 응답 스펙 변경이 요청 계약을 깨지 않음 ② `password` 같은 입력 전용 필드가 응답에 새지 않음 ③ 검증 규칙이 요청에만 붙어 응답이 가벼움.
- **형식 vs 비즈니스**: `@Email`/`@Pattern`/`@PastOrPresent` 같은 **형식** 검증은 DTO, "이메일 중복" 같은 **비즈니스** 검증은 Service. 계층 책임이 다르다.

## 18. EmployeeController

- 자원 표현은 명사(`/employees`), 동작은 HTTP 메서드(POST/GET/PUT/DELETE)로 표현한다.
- `@PathVariable` 은 URL 의 식별자, `@RequestParam` 은 쿼리스트링, `@RequestBody` 는 JSON 본문이다.
- 권한 검사는 1차로 Security 필터/`@PreAuthorize`, 2차로 Service 의 `requireAdmin(currentUser)` 같은 가드.

**심화·면접 답변:**
- **@PathVariable vs @RequestParam vs @RequestBody**: 각각 URL 식별자(`/employees/{id}`), 쿼리스트링(`?keyword=`), JSON 본문. "자원 식별은 path, 필터/검색은 query, 생성/수정 데이터는 body" 로 기억.
- **201 + Location**: 생성 응답은 `201 Created` + `Location` 헤더에 새 자원 URI. `ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")...` 로 만든다.
- **PUT vs PATCH**: PUT 은 전체 교체(보낸 표현으로 자원을 통째로 대체, 누락 필드는 비워짐), PATCH 는 부분 갱신. 직원 수정이 "보낸 필드만 변경" 의도면 PATCH 가 정확.
- **204 No Content**: 삭제/비활성은 돌려줄 본문이 없으므로 204. 면접: "왜 200 이 아니라 204?" → 응답 바디가 없음을 명시.

## 19. LeaveController

- 같은 자원(휴가)이라도 일반 사용자/관리자 관점이 다르면 경로를 분리하는 편이 보안 정책 관리가 쉽다.
- `PATCH /leaves/{id}/approve` 는 REST 순수주의 관점에서 동사를 URL 에 넣는 점이 아쉽지만, 상태 변경이 명확해서 실무에서 자주 쓴다.
- 로그인 사용자는 학습 단계에서는 `HttpSession`, Spring Security 단계에서는 `@AuthenticationPrincipal`, JWT 단계에서는 직접 만든 `@CurrentUser` 같은 어노테이션을 사용한다.

**심화·면접 답변:**
- **경로 분리(/api/leaves vs /api/admin/leaves)의 트레이드오프**: 장점은 권한 정책이 경로에 드러나 `SecurityFilterChain` 의 `requestMatchers("/api/admin/**").hasRole("ADMIN")` 설정이 단순해진다는 것. 단점은 컨트롤러/경로가 도메인마다 둘로 늘어난다는 것.
- **동사형 경로 + PATCH**: `/leaves/{id}/approve` 는 REST 순수주의(자원=명사) 관점에선 아쉽지만, 상태 전이가 명확하고 부분 변경이라 `PATCH` 와 잘 맞는다. 실무에서 흔히 채택.
- **IDOR 방어**: 휴가 상세/취소는 URL 의 `leaveId` 를 신뢰하지 말고, Service 에서 "현재 사용자가 소유자이거나 ADMIN" 인지 검증한다. 안 하면 ID 만 바꿔 남의 휴가를 보는 취약점.
- **인증 사용자 주입 진화**: 세션(`@SessionAttribute`) → Security(`@AuthenticationPrincipal`) → JWT(커스텀 `@CurrentEmployee` + ArgumentResolver). 34장과 연결.

## 20. 보안 흐름

- 1차: 로그인 성공 → `session.setAttribute("userId", user.getId())` → 보호 자원은 `HandlerInterceptor` 에서 세션 검사.
- 2차: `SecurityFilterChain` 에 `formLogin` + `BCryptPasswordEncoder` + 권한 매핑(`hasRole("ADMIN")`).
- 3차: JWT 발급 → Authorization 헤더 검사 → `UsernamePasswordAuthenticationToken` 으로 SecurityContext 구성. 서버는 무상태로 유지된다.
- 세션 → JWT 진화의 핵심은 **상태를 어디에 둘 것인가**(서버 메모리 vs 클라이언트 토큰).

**심화·면접 답변:**
- **BCrypt > SHA-256**: BCrypt 는 salt 자동 + work factor 로 의도적으로 느려 무차별 대입을 막는다. SHA-256 은 빠르고 salt 가 없어(같은 비밀번호=같은 해시) 레인보우 테이블에 취약. "비밀번호 해시는 느려야 한다" 가 면접 포인트.
- **세션 vs JWT (상태 위치)**: 세션=서버 메모리 → 스케일아웃 시 Redis 세션 공유/sticky session 필요, 대신 강제 로그아웃이 세션 삭제로 즉시 가능. JWT=클라이언트 토큰 → 무상태라 수평 확장 쉬움, 대신 강제 로그아웃이 어려워 짧은 만료 + RefreshToken + 블랙리스트로 보완.
- **CSRF disable 조건**: 쿠키 기반(세션)은 브라우저 자동 전송 때문에 CSRF 토큰 필요. JWT 를 `Authorization` 헤더로 보내면 자동 전송이 아니라 disable 가능. (H2 콘솔용 disable 과는 이유가 다름)
- **hasRole vs hasAuthority**: `hasRole("ADMIN")` 은 `ROLE_ADMIN` 을 찾는다(접두사 자동). 저장한 권한 문자열 규칙과 맞춰야 한다.

## 21. 통합 테스트

- `@SpringBootTest` + `@AutoConfigureMockMvc` 로 컨텍스트를 띄우고, `MockMvc.perform(...)` 으로 요청을 흘려보낸다.
- 응답 JSON 에서 다음 요청에 쓸 값은 `andReturn().getResponse().getContentAsString()` → ObjectMapper 로 파싱한다.
- 권한 실패는 `andExpect(status().isForbidden())` 또는 `.is(403)` 로 검증한다.

**심화·면접 답변:**
- **@SpringBootTest vs @WebMvcTest**: 전자는 전체 컨텍스트를 띄우는 진짜 통합 테스트(느리지만 실제 흐름 검증), 후자는 웹 레이어만 슬라이스로 띄우고 Service 는 `@MockBean`(빠르고 Controller 단위 검증). "무엇을 보장할 것인가" 로 고른다.
- **@Transactional 테스트의 양면**: 각 테스트 후 자동 롤백되어 DB 가 깨끗이 유지(격리). 단점은 실제 커밋 시점 동작(`AFTER_COMMIT` 이벤트, DB 트리거)을 검증하지 못한다는 것 — 이땐 `@Transactional` 을 빼거나 `TestTransaction` 으로 제어.
- **401 vs 403**: 인증 안 됨(누구인지 모름)=401, 인증은 됐지만 권한 없음=403. 테스트에서 둘을 구분해 단정하면 권한 설계 이해도를 보여준다.
- **비결정성 제어**: `LocalDateTime.now()` 는 `Clock` 주입으로 고정, 랜덤은 시드 고정. 응답 JSON 에서 `leaveId` 를 파싱해 다음 요청에 체이닝하는 패턴이 통합 테스트의 기본기.

## 22. 문서화

- README 8섹션 예시: 프로젝트 소개 / 개발 목적 / 사용 기술 / 주요 기능 / 시스템 구조 / ERD / API 명세 / 실행 방법.
- ERD 표기: 학습 단계에서는 텍스트 트리(`User 1:1 Employee`)로 충분. 면접용 자료는 dbdiagram.io 로 시각화하면 인상이 좋다.
- 트러블슈팅 4단계 템플릿: 문제(증상) → 원인(왜) → 해결(코드/명령) → 배운 점(다음에 같은 실수를 피하는 방법).
- API 명세는 Markdown 으로 시작해서, 익숙해지면 springdoc-openapi 로 자동 생성하는 단계로 넘어간다.

**심화·면접 답변:**
- **README 의 기준은 "클론 후 5분 실행"**: 실행 방법(`./gradlew bootRun`)·기본 포트·H2 콘솔 경로·시드 계정(admin/employee)을 명시한다. 면접관이 가장 먼저 보는 문서라 여기서 막히면 코드도 안 본다.
- **트러블슈팅이 곧 면접 무기**: 문제→원인→해결→배운 점 4단 + 코드 diff. 이 프로젝트의 대표 3건은 휴가 중복 승인(상태 검증 위치), 직원 부분 저장(@Transactional), LazyInitializationException(DTO 변환 시점)이다 — TRD 3.19 Q12("가장 어려웠던 문제")의 답이 그대로 나온다.
- **API 명세 진화**: 손으로 적은 Markdown(필드 의미를 직접 사고) → `springdoc-openapi` 자동 생성(`/swagger-ui.html`). 어느 쪽이든 인증 헤더 형식(`Authorization: Bearer ...`)을 반드시 적는다.
- **커밋/PR 도 포트폴리오**: "왜" 를 적은 커밋과 4섹션 PR 이 협업 역량을 보여준다(39장).

## 23. 인증 — 로그인 / 로그아웃 / 내 정보 / 비밀번호 변경

- 로그인 실패 메시지에서 “이메일” / “비번” 구분을 안 하는 이유: 공격자가 이메일 존재 여부를 사용자 열거에 이용할 수 있다. → 동일 메시지로 응답.
- `passwordEncoder.matches(raw, encoded)` 는 같은 salt 로 다시 해시한 결과를 비교하므로 평문 비교가 불가능하다.
- 로그인 직후 `request.changeSessionId()` 로 세션 ID 를 새로 발급해야 Session Fixation 공격을 막을 수 있다.
- `logout()` 은 `session.invalidate()` 로 세션 자체를 폐기한다.
- 비밀번호 변경은 “현재 비번 확인” + “새 비번 해시 저장” 두 단계. 변경 후 기존 세션을 만료시키는 정책도 검토.

**심화·면접 답변:**
- **사용자 열거 방지의 두 축**: 메시지 통일(“이메일 또는 비밀번호가 올바르지 않습니다”) + 응답 시간 통일. 존재하지 않는 이메일이면 더미 해시를 한 번 비교해 timing 차이를 없앤다. 상태코드는 401.
- **matches 동작 원리**: BCrypt 해시 문자열(`$2a$10$salt+hash`)에서 salt 를 꺼내 raw 를 같은 salt·cost 로 해시한 뒤 비교한다. 그래서 같은 비밀번호도 매번 다른 해시가 저장되지만 검증은 가능하다.
- **Session Fixation 시나리오**: 공격자가 자기 세션 ID 를 피해자 브라우저에 심음 → 피해자가 그 세션으로 로그인 → 공격자가 같은 세션 ID 로 접근해 탈취. `changeSessionId()` 로 로그인 직후 ID 를 바꾸면 차단.
- **readOnly 구분 이유**: `login()`/`me()` 는 조회만 하므로 `readOnly=true`(flush 생략), `changePassword()` 는 비밀번호를 바꾸는 쓰기라 일반 `@Transactional`.

## 24. 부서 Service

- 부서명 중복은 `existsByName` + DB unique 두 단계. 동시 등록 race 까지 DB 가 마지막 방어선.
- 부서 삭제 시 소속 직원이 있으면 일반적으로는 거부. 운영 정책에 따라 “기본 부서로 이동” 또는 “부서 NULL 허용” 도 선택지.
- 부서 상세에서 직원까지 N+1 없이 가져오려면 `@EntityGraph` 또는 fetch join 으로 한 번에 끌어온다.
- 부서가 1000개를 넘으면 페이징 + 부서 그룹화(본부/팀)를 도입하는 것이 자연스럽다.

**심화·면접 답변:**
- **이름 변경 시 "자기 자신 제외" 중복검사**: `!newName.equals(currentName) && existsByName(newName)` 처럼 변경이 실제로 일어날 때만 중복을 본다. 이 가드가 없으면 같은 이름으로 저장(설명만 수정)하는 요청이 `DUPLICATE_DEPARTMENT_NAME` 으로 오탐된다.
- **N+1 구체 시나리오**: `detail` 에서 `members` 를 가져온 뒤 응답 변환 시 직원마다 `e.getUser().getName()` 을 접근하면, 직원 N명에 대해 user 조회 N번이 추가로 나간다. `@EntityGraph(attributePaths="user")` 또는 fetch join 으로 1쿼리로 해결.
- **삭제 정책의 명문화**: "소속 직원이 있으면 거부" 를 코드 주석이 아니라 비즈니스 규칙 문서(37장)에 적어둔다. 정책이 코드에만 있으면 화면/기획과 어긋난다.

## 25. 부서 Controller

- 권한 검사는 `@PreAuthorize` (Security 단) + Service 의 `requireAdmin(role)` (도메인 단) 다층 방어.
- 부분 수정이라면 PATCH 가 의미상 정확하지만, PRD 처럼 PUT 으로 두는 경우도 흔하다. 팀 컨벤션에 맞춘다.
- `DepartmentDetailResponse` 는 부서 정보 + 소속 직원 nested record 로 두면 응답 모양이 명확하다.

**심화·면접 답변:**
- **List vs Page 선택 근거**: 부서는 소수라 `List` 전체 조회가 자연스럽고, 직원은 다수라 `Page`. "데이터 규모와 증가 가능성" 이 컬렉션 타입을 결정한다 — 면접에서 자주 묻는다.
- **nested record(MemberItem)의 가치**: 부서 상세에 `Employee` 엔티티를 그대로 넣으면 `user.password`·LAZY 연관까지 직렬화 위험. 필요한 필드(이름/사번/직급)만 담은 요약 record 로 모양을 통제한다.
- **권한 다층**: Controller `@PreAuthorize` + Service `requireAdmin`. 둘 다 두는 이유는 Service 가 다른 경로(배치/내부 호출)에서도 재사용되기 때문.

## 26. 공지 Controller

- GET 안에서 조회수를 올리면 검색 봇/프리뷰 요청으로 카운트가 부풀려진다. 별도 PATCH `/{id}/view` 로 분리하는 패턴이 안전.
- 목록 응답에 `content` 까지 그대로 넣으면 페이로드가 커진다. 목록은 요약 DTO, 상세는 풀 DTO 로 분리하는 편이 깔끔.
- `@PageableDefault(size = 20)` 처럼 기본 페이지 크기를 두면 클라이언트가 size 를 누락해도 안전.

**심화·면접 답변:**
- **목록 요약 DTO vs 상세 풀 DTO**: 목록 응답에 `content`(TEXT)까지 넣으면 한 페이지에 큰 본문 20개가 실려 페이로드가 폭증한다. 목록은 `title/important/createdAt` 요약 DTO, 상세에서만 `content`. (현재 코드가 같은 DTO 를 쓰는 건 학습용 단순화)
- **@PageableDefault 의 안전망**: 기본 size 가 없으면 `size` 누락 시 구현/버전에 따라 전체를 끌어오는 사고가 날 수 있다. 상한도 함께 두면 더 안전.
- **GET 멱등성 vs 조회수**: 조회수 증가는 부수효과라 GET 의 멱등 원칙과 충돌한다. `PATCH /{id}/view` 분리 또는 동일 사용자 중복 집계 방지로 절충.

## 27. 결재 Controller

- `/my` 는 작성자가 보는 화면, `/pending` 은 결재자가 보는 화면. 권한이 다르므로 경로 분리.
- 상태 전이를 동사형(`/submit`, `/approve`, `/reject`) 으로 표현하면 의미가 분명하지만 REST 순수주의에서는 비선호. 실무에서는 의미가 우선.
- `hasAnyRole('APPROVER','ADMIN')` 으로 두 역할을 모두 허용. role 문자열에 자동으로 `ROLE_` 접두사가 붙는다는 점 주의.

**심화·면접 답변:**
- **역할 검사 ≠ 소유/담당 검사**: `@PreAuthorize("hasAnyRole('APPROVER','ADMIN')")` 는 "결재할 자격이 있는 역할인가" 만 본다. "이 문서의 결재자 본인인가" 는 Service 에서 `approverId.equals(current)` 로 다시 본다. 둘을 합치면 다른 결재자의 문서를 승인하는 IDOR 가 뚫린다.
- **/pending 을 데이터로 좁히기**: `findByApproverIdAndStatus(current, PENDING)` 로 "내가 결재할, 대기 중인" 문서만 반환 → 권한 어노테이션이 못 거르는 소유 범위를 쿼리가 보강.
- **작성과 submit 분리**: 임시저장(DRAFT) UX + submit 시 필수필드 재검증. 작성 201, 전이는 PATCH + 동사형(`/submit`).

## 28. 직원 상세 / 수정 / 퇴사

- LAZY 관계가 응답 변환 시점에 풀리려면 트랜잭션이 살아있어야 한다(`open-in-view: true` 의 함정). Service 안에서 DTO 까지 변환해 반환하는 것이 가장 안전.
- 부서 변경은 도메인 메서드(`changeDepartment(newDept)`)로 캡슐화. Service 가 직접 setter 를 호출하면 다음 호출자가 검증을 잊는다.
- 퇴사 처리는 hard delete 가 아니라 `RESIGNED` 상태로 두어 휴가/결재/공지에 남은 참조가 깨지지 않도록 한다.

**심화·면접 답변:**
- **open-in-view 의 함정**: 기본값 true 면 영속성 컨텍스트가 뷰 렌더까지 열려 있어 Controller/뷰에서 LAZY 접근이 "되긴 된다". 하지만 DB 커넥션을 요청 끝까지 잡아 커넥션 풀 고갈 위험. 끄면(false) 트랜잭션 밖 LAZY 접근에서 `LazyInitializationException` → **Service 안에서 DTO 까지 변환**해 반환하는 것이 정석.
- **부수효과 추적**: `employee.getUser().changeName()` 은 Employee 를 통해 User 를 바꾼다. 같은 트랜잭션이라 dirty checking 으로 함께 commit 되지만, "직원 수정이 계정 정보까지 건드린다" 는 영향 범위를 의식해야 한다.
- **부분 갱신 가드**: 부서 변경은 `departmentId` 가 현재와 다를 때만 조회·검증. 매 요청마다 부서를 다시 조회하면 불필요한 쿼리.

## 29. 휴가 — 내 목록 / 상세 / 취소

- “내 휴가만” 검증을 Service 가 한 뒤에도, Repository 쿼리 자체에 `employeeId` 조건을 두는 편이 IDOR 공격에 안전하다. 상세 조회는 본인 또는 ADMIN 만 허용한다.
- 본인 PENDING 휴가만 취소 가능. 도메인 메서드(`cancelByOwner`) 안에서 상태 검증을 캡슐화.
- 관리자 목록(FR-LEAVE-008)은 `/api/admin/leaves` 에서 status, employeeId 같은 조건을 받는다. 동적 조건은 학습 단계에서는 if 분기로 충분하고, 조건이 4개 이상으로 늘면 Querydsl/Specification 을 검토한다.
- `CANCELED` 를 별도 상태로 두면 “직원이 직접 취소한 것” 과 “관리자가 반려한 것” 을 통계에서 구분할 수 있다.

**심화·면접 답변:**
- **IDOR 이중 방어 구체화**: ① Service 가 `isOwner || isAdmin` 검증 ② Repository 가 `findByEmployee_Id(current, ...)` 로 애초에 내 것만 조회. 목록은 ②로, 단건 상세는 ①로 막는다. URL 의 id 는 인증 주체로 쓰지 않는다.
- **동적 조건 확장 경로**: `findForAdmin` 의 status/employeeId if 분기는 조합이 2×2=4. 기간(from/to)이 추가되면 8가지로 폭발 → Querydsl `BooleanBuilder` 나 Specification 으로 "있는 조건만 AND" 하는 단일 쿼리가 정답.
- **취소 상태 설계**: `REJECTED` 재사용은 단순하지만 "본인 취소 vs 관리자 반려" 가 통계에서 뭉개진다. 이력 정확성이 필요하면 `CANCELED` 추가 — 단 enum 확장 시 기존 필터/화면 영향 점검.

## 30. 결재 — my / pending / detail 권한

- 상세 조회는 작성자, 결재자 또는 ADMIN 만 허용. 셋 다 아니면 `ACCESS_DENIED(403)`.
- 결재자가 변경 가능한 모델이라면 `pending` 쿼리는 현재 `approverId` 기준이므로 위임 직후부터 새 결재자에게 노출된다.
- 관리자에게 모든 문서를 보여주려면 Service 시그니처에 `UserRole role` 을 추가해 관리자 허용 분기를 명시하는 편이 명확.

**심화·면접 답변:**
- **/my 와 /pending 을 분리하는 진짜 이유**: 조회 조건이 다르다. `/my` 는 `writerId` 기준, `/pending` 은 `approverId + status=PENDING` 기준. 한 메서드에서 분기하면 조건이 뒤엉켜 권한 실수가 나기 쉽다.
- **역할 vs 소유 재확인**: detail 에서 `hasAnyRole` 을 통과한 APPROVER 라도 그 문서의 writer/approver 가 아니면 `ACCESS_DENIED`. 면접: "권한 어노테이션만으로 충분한가?" → "역할은 거르지만 소유/담당은 Service 가 본다."
- **권한 위임의 파급**: `approverId` 변경을 허용하면 `pending` 은 현재 approverId 기준이라 위임 즉시 목록이 옮겨간다. 위임 이력(누가 언제)을 별도로 남길지도 설계 포인트.

## 31. 모든 Repository

- `findByUser_Email` 같은 nested property 는 `_` 로 연관 경로를 표시.
- 휴가 기간 겹침은 `start <= :endDate AND end >= :startDate` 가 “구간이 겹친다” 의 표준 조건.
- 조회수 증가는 `@Modifying @Query("UPDATE ... SET view_count = view_count + 1")` 로 원자적으로 처리해야 동시성 손실이 없다.
- 메서드 이름이 3 단어를 넘으면 보통 `@Query` 로 옮기는 편이 가독성에 좋다.

**심화·면접 답변:**
- **메서드 이름 쿼리의 약점**: 필드명 기반 문자열이라 컴파일타임 검증이 약하다. 필드명을 바꾸거나 오타가 나면 런타임(앱 기동/첫 호출)에야 깨진다. 그래서 복잡·고정 쿼리는 `@Query`(JPQL 검증 일부 가능), 동적 조건은 Querydsl(타입 안전 + IDE 리팩터링 추종)로 옮긴다.
- **@Modifying 벌크 연산 함정**: `UPDATE ... view_count+1` 같은 벌크는 영속성 컨텍스트를 우회한다. 같은 트랜잭션에서 이미 로딩한 Notice 엔티티의 viewCount 와 DB 값이 어긋날 수 있어 `@Modifying(clearAutomatically = true)` 로 1차 캐시를 비운다.
- **겹침 검사의 statuses 파라미터**: `existsOverlapping` 에 `PENDING, APPROVED` 만 넘기는 이유 — 이미 반려/취소된 휴가는 기간이 겹쳐도 충돌이 아니다. 어떤 상태를 "유효한 점유" 로 볼지 명시한다.

## 32. 응답 DTO 매핑 패턴

- `Page<Entity>.map(Response::from)` 으로 페이지 메타(totalElements, totalPages) 가 자연스럽게 유지된다.
- `record` 는 불변 + equals/hashCode 자동 생성으로 응답 DTO 에 적합.
- DTO 가 Entity 의 setter 를 호출하면 양방향 의존이 생기고, DTO 가 도메인 규칙을 우회할 수 있게 된다.
- MapStruct 는 DTO 가 20개를 넘어가고, 변환 코드가 단순 반복이 될 때 도입.

**심화·면접 답변:**
- **단방향 의존이 주는 안정성**: DTO 는 Entity 를 알아도(`from(entity)`) Entity 는 DTO 를 모른다. 그래서 응답 모양이 자주 바뀌어도 도메인은 흔들리지 않는다 — 변경의 파급을 한 방향으로 가둔다.
- **record 의 불변성 이점**: 응답 DTO 가 생성 후 바뀌지 않으므로 예측 가능하고 스레드 안전하다. equals/hashCode/toString 자동 생성으로 테스트 단정도 쉽다.
- **세 안티패턴의 공통 뿌리**: ① Entity 직접 반환 ② Entity 에 `@JsonIgnore` 떡칠 ③ 요청·응답 DTO 공용 — 모두 "Entity 가 응답(표현) 책임까지 떠안을 때" 생긴다. 응답 책임을 DTO 로 분리하면 한 번에 사라진다.

## 33. Bean 설정

- `@EnableJpaAuditing` 이 없으면 AuditingEntityListener 가 작동하지 않아 `createdAt` 이 null 로 들어온다.
- BCrypt 가 SHA-256 보다 안전한 이유: (1) salt 가 매 해시마다 다르고 결과에 포함됨 (2) work factor(cost) 로 해시 비용을 늘려 brute force 를 어렵게 함.
- `WebMvcConfigurer` 의 `addInterceptors` 는 진입 전 후 가로채기, `addArgumentResolvers` 는 메서드 인자 주입.
- 1차(세션) 와 2차(Security) 를 동시에 켜면 인증 책임이 두 곳에 흩어져 디버깅이 어렵다.

**심화·면접 답변:**
- **Auditing 의 두 조건**: `@EnableJpaAuditing`(설정에서 기능 켜기) + 엔티티의 `@EntityListeners(AuditingEntityListener.class)`(콜백 받기). 둘 중 하나라도 없으면 `createdAt` 이 null. 면접에서 "createdAt 이 안 들어와요" 의 단골 원인.
- **PasswordEncoder 를 직접 빈으로 두는 이유**: 어떤 해시 알고리즘을 쓸지는 보안 정책이라 Spring 이 임의로 고르지 않는다. 실무에서는 `DelegatingPasswordEncoder`(`{bcrypt}...`)로 두면 나중에 알고리즘을 교체·마이그레이션하기 쉽다.
- **1차/2차 공존 금지 이유 구체화**: 세션 인터셉터와 SecurityFilterChain 이 둘 다 인증을 검사하면, 어디서 401/403 이 났는지 추적이 두 배로 어렵고 정책이 어긋날 수 있다. 단계 전환 시 1차를 끄고 2차로 옮긴다.

## 34. @CurrentUser + Interceptor

- ArgumentResolver 가 없다면 Controller 마다 `session.getAttribute("USER_ID")` + null 체크 + 캐스팅이 반복된다.
- Filter 가 더 먼저 실행되고, Interceptor 는 DispatcherServlet 이후. ArgumentResolver 는 Controller 메서드 진입 직전.
- JWT 단계에서는 ArgumentResolver 가 `SecurityContext.getAuthentication()` 에서 사용자 정보를 꺼내도록 바뀐다.

**심화·면접 답변:**
- **실행 순서와 각 단계 책임**: Servlet Filter(인증/로깅, 가장 바깥) → DispatcherServlet → Interceptor.preHandle(컨트롤러 진입 전 세션 검사) → ArgumentResolver(메서드 인자 주입) → Controller. "Filter 는 서블릿 레벨, Interceptor 는 스프링 MVC 레벨" 이 핵심 구분.
- **@AuthenticationPrincipal vs @CurrentUser**: 전자는 Spring Security 표준으로 `SecurityContext` 의 principal 을 주입, 후자는 직접 만든 세션 기반 어노테이션. 목적(현재 사용자 주입)은 같고 출처(SecurityContext vs HttpSession)가 다르다.
- **ArgumentResolver 의 본질**: "모든 Controller 가 반복하는 횡단 관심사(인증 사용자 추출)" 를 한 곳에 모으는 패턴. 빠지면 메서드마다 세션 조회·null 체크·캐스팅이 복붙된다.

## 35. Thymeleaf

- `th:text` 는 escape, `th:utext` 는 raw HTML 을 그대로 출력 → XSS 위험. 신뢰된 데이터에만 사용.
- `th:href="@{/employees/{id}(id=${emp.employeeId})}"` 형태로 동적 URL 을 만든다.
- 폼에 `${_csrf.parameterName}` / `${_csrf.token}` 으로 CSRF 토큰을 자동 삽입.
- Fragment 재사용: `th:fragment="header"` 선언 → `th:replace="~{fragments/header :: header}"` 로 삽입.
- `@RestController` 는 JSON, `@Controller` 는 view 이름을 반환.

**심화·면접 답변:**
- **th:utext 의 구체적 위험**: 공지 본문 같은 사용자 입력을 `th:utext` 로 출력하면 `<script>alert(document.cookie)</script>` 가 그대로 실행되는 저장형 XSS 가 된다. `th:text` 는 escape 하므로 안전. utext 는 관리자가 만든 신뢰된 HTML 에만.
- **SSR vs SPA 트레이드오프**: Thymeleaf(SSR)는 초기 로딩·SEO·단순 배포가 강점, React(SPA)는 상호작용·프론트백 분리가 강점이지만 빌드·인증 토큰·CORS 복잡도가 는다. 백엔드 포트폴리오 데모로는 SSR 이 가볍다.
- **화면도 서버에서 보호**: `/leaves/admin` 같은 화면은 JSON API 와 똑같이 인터셉터/Security 로 컨트롤러 진입 전에 막는다. "화면이라 그냥 렌더" 가 가장 흔한 인가 누락.

## 36. 패키지 구조

- 의존 방향: Controller → Service → Repository → Domain. Domain 은 거꾸로 의존하지 않는다.
- DTO 는 Entity 를 import 할 수 있지만, Entity 가 DTO 를 import 하면 도메인 책임이 흐려진다.
- 계층 우선 → 도메인이 6개 이상이 되면 도메인 우선으로 자연스럽게 전환.
- 안티 패턴 중 가장 빠지기 쉬운 것: Controller 가 Repository 직접 호출. Service 가 한 줄짜리 wrap 만 한다는 핑계로 자주 발생.

**심화·면접 답변:**
- **의존 방향 한 줄 정리**: 화살표는 항상 상위→하위(Controller→Service→Repository→Domain), 역방향 금지. Domain 은 아무것도 import 하지 않는 가장 안정적인 핵이라 어느 계층에서도 안전하게 재사용된다.
- **Controller→Repository 직접 호출이 위험한 이유**: 트랜잭션 경계와 비즈니스 검증을 둘 곳(Service)이 사라져 로직이 Controller 로 샌다. 처음엔 "한 줄 wrap" 이지만 곧 검증·트랜잭션이 필요해지면서 Controller 가 비대해진다.
- **Service 가 HttpSession 을 직접 다루지 않기**: 웹 기술이 Service 로 새면 단위 테스트가 불가능해진다. Controller 가 인증 사용자를 꺼내 `Long currentUserId` 순수 값으로 넘긴다(34장).
- **트랜잭션이 Service 인 이유**: "비즈니스 작업 1개 = 트랜잭션 1개" 라 비즈니스 경계인 Service 가 트랜잭션 경계와 일치한다.

## 37. 비즈니스 규칙 매트릭스

- 같은 검증을 여러 계층에 두는 이유: 다층 방어. 한 곳을 우회해도 다른 곳에서 잡힌다.
- DB unique 가 있는데도 Service 에서 `exists` 를 부르는 이유: 사용자 친화적인 에러 메시지를 빨리 돌려주기 위해. unique 위반의 DB 예외는 직접 보여주기 어렵다.
- 도메인 메서드의 `IllegalStateException` 을 그대로 두면 GlobalExceptionHandler 에서 일관된 ErrorCode 매핑이 어려워진다. 대신 BusinessException(ErrorCode.INVALID_STATUS) 를 던지는 편이 깔끔.

**심화·면접 답변:**
- **각 층이 막는 실패 모드**: D(DTO)=형식 오류를 빠르게 400, S(Service)=상태/권한/관계 흐름, E(Entity)=어느 호출 경로로 와도 깨지지 않는 불변식, DB=동시성 race. 같은 규칙을 여러 층에 두는 건 중복이 아니라 "서로 다른 실패를 막는" 다층 방어다.
- **도메인 예외 변환의 일관성**: 엔티티가 `IllegalStateException` 을 던지면 의미는 분명하지만 핸들러가 ErrorCode 로 매핑하기 애매하다. 도메인 경계나 Service 에서 `BusinessException(INVALID_STATUS)` 로 바꿔 응답 코드를 일관되게 한다.
- **규칙 증가 시 리팩터링**: Service 가 if 로 비대해지면 정책 객체(`LeavePolicy`)·도메인 서비스·Specification 으로 분리한다.

## 38. 트러블슈팅

- 휴가 승인 중복: 도메인 메서드 안에 상태 검증을 두면 호출 경로가 늘어나도 깨지지 않는다.
- 직원 등록 부분 저장: `@Transactional` 로 원자성 보장.
- Entity 직접 반환: DTO 분리 + Entity 에 직렬화 어노테이션 금지.
- LazyInitializationException: Service 안에서 DTO 까지 변환해 반환하거나, fetch join 으로 미리 로드.
- 이메일 동시 가입: 애플리케이션 검증 + DB unique 다층 방어. DataIntegrityViolationException 을 ErrorCode 로 매핑.

**심화·면접 답변:**
- **5건을 관통하는 한 원리**: "검증·원자성·책임을 데이터 옆에 둔다." 상태 검증→Entity(중복 승인), 원자성→@Transactional(부분 저장), 응답 책임→DTO(Entity 노출), 로딩 시점→명시(LazyInitializationException), 무결성→DB 제약(동시 가입). 면접에서 "공통 교훈은?" 에 이 한 줄.
- **30초 압축 공식**: 증상 → 근본 원인 → 해결 → 일반화된 교훈, 각 한 문장. 외운 티가 아니라 "구조화된 사고" 로 들린다.
- **회귀 테스트와 짝**: 각 해결에 "이 버그를 재현하는 테스트를 추가" 를 붙이면 같은 버그가 다시 못 들어온다는 설득력이 생긴다(21장).

## 39. 면접 카드 + 커밋

- 30초 면접 답변의 공식: (1) 어떤 문제를 풀었나 → (2) 어떻게 풀었나 → (3) 무엇을 배웠나.
- 커밋 메시지 “왜” 만 적기 — “무엇” 은 diff 가 보여준다. 한 커밋 = 한 의도.
- PR 4섹션: 무엇 / 왜 / 어떻게 테스트했나 / 영향 범위. 면접에서 PR 링크를 보여줄 때 이 4섹션이 채워져 있으면 인상이 좋다.

**심화·면접 답변:**
- **답변은 STAR + 트레이드오프**: 상황–과제–행동–결과에 더해 "왜 그 선택을 했고 대안은 무엇이었나" 를 한 줄 넣는다. 예) "@Transactional 을 Service 에 뒀습니다 — Controller 에 달면 검증 전부터 커넥션을 잡고, Repository 는 비즈니스 단위가 아니기 때문입니다."
- **8대 질문 한 줄 키워드**: 동기(SI 업무) / User·Employee 분리 / 부서 N:1·LAZY / enum 상태머신 / @Transactional 위치 / DTO 분리 / 권한 다층 / 트러블슈팅 1건. 각 키워드에 "왜" 를 붙여 답한다.
- **이 워크북이 곧 산출물**: 채운 면접 답변·자기소개·커밋 컨벤션을 그대로 README 와 `docs/`, 면접 노트로 옮기면 포트폴리오 문서 한 벌이 완성된다(22장).
- **커밋·PR 도 평가 대상**: "왜" 를 적은 커밋과 4섹션 PR 은 "혼자 짠 코드" 가 아니라 "리뷰 가능한 결과물" 이라는 협업 신호다.
