# 정답 방향 (Answers)

빈칸의 키워드 자체보다 **왜 그렇게 채워야 하는가**를 설명합니다. PRD/TRD 의 해당 절을 다시 펴 보며 한 줄로 자기 답을 만들어 보세요.

---

## 0. 빌드와 설정

- `spring-boot-starter-web` 은 Tomcat + Spring MVC + Jackson 을 묶어 REST API 와 Thymeleaf 렌더링을 가능하게 한다.
- `spring-boot-starter-data-jpa` 는 EntityManager 와 Hibernate, Repository 인터페이스 자동 구현을 제공한다.
- `spring-boot-starter-validation` 은 `@Valid` 와 Hibernate Validator(jakarta.validation) 를 활성화한다.
- `h2` 는 인메모리/파일 DB 로, 로컬과 테스트에서 별도 설치 없이 빠르게 띄울 수 있다.
- `spring-boot-starter-thymeleaf` 는 서버 렌더링용. 화면 요구사항(2.7) 을 위해 사용한다.
- `ddl-auto: create` 는 시작할 때마다 스키마를 새로 만들기 때문에 로컬 학습용에 가깝다. 운영은 `validate` 또는 `none` 이 안전하다.
- Spring Boot 3.x 부터 패키지가 `javax.*` → `jakarta.*` 로 바뀌었다. 검증 어노테이션도 `jakarta.validation.constraints` 를 쓴다.

## 1. User

- `users` 가 SQL 예약어 충돌을 피하는 가장 흔한 테이블명이다.
- 비밀번호는 `BCryptPasswordEncoder.encode(...)` 로 해시한 결과만 저장한다. 평문 저장은 PRD 비기능 요구사항(2.6 보안) 위반.
- `@Enumerated(EnumType.STRING)` 을 쓰면 DB 에 `USER`, `ADMIN`, `APPROVER` 가 그대로 들어가서 enum 순서가 바뀌어도 데이터가 깨지지 않는다.
- `createdAt`, `updatedAt` 은 `@CreatedDate`, `@LastModifiedDate` + `@EnableJpaAuditing` 으로 자동화하거나, `@PrePersist`/`@PreUpdate` 로 수동 세팅한다.

## 2. Department

- 부서명이 중복되면 같은 이름의 두 부서가 생겨 사용자가 혼란스럽다. `@Column(unique = true)` 또는 `@UniqueConstraint` 로 막는다.
- 양방향 매핑은 편하지만 직렬화 무한 루프, 페치 비용 증가, 양쪽 동기화 부담이 생긴다. 학습용으로는 **Employee → Department 단방향**으로 두고, 부서별 직원 목록은 Repository 쿼리로 조회하는 편이 단순하다.

## 3. Employee

- User 와 Employee 를 분리한 이유: 로그인 계정과 인사 정보의 라이프사이클이 다르고, 권한과 인사 데이터를 같은 테이블에 두면 책임이 섞인다(면접 Q3).
- `fetch = LAZY` 가 기본인 이유: 직원 목록을 조회할 때마다 매번 부서까지 join 으로 끌어오면 N+1 또는 불필요한 부하가 생긴다.
- `RESIGNED` 상태로 두는 이유: 휴가/결재/공지 등 과거 데이터에 남은 직원 참조를 깨지 않기 위해 soft delete 가 안전하다.

## 4. LeaveRequest

- 휴가 기간은 시간 정보가 필요 없으므로 `LocalDate` 가 자연스럽다.
- 정적 팩토리(`LeaveRequest.create(...)`)에서 `status = ApprovalStatus.PENDING` 으로 시작하도록 강제하면 호출부에서 상태 초기화를 잊을 일이 없다.
- `rejectReason` 은 `APPROVED` 일 때 null, `REJECTED` 일 때 필수다. → Service 검증에서 강제한다.
- 시작일 > 종료일 검증은 Service 의 `validateDateRange` 에서 처리하고 `INVALID_DATE_RANGE` 에러로 변환한다.

## 5. Notice

- 공지 본문은 길어질 수 있으니 `VARCHAR` 가 아니라 `TEXT`/`@Lob` 으로 둔다. `columnDefinition = "TEXT"` 가 가장 명시적이다.
- 중요 공지를 위로 올리려면 `ORDER BY important DESC, createdAt DESC` 같은 복합 정렬을 쓴다.
- `viewCount` 를 단순히 `+1` 하면 동시에 두 요청이 같은 값을 읽고 같은 값을 쓰는 경합이 생긴다. JPQL `UPDATE notice SET view_count = view_count + 1 WHERE id = :id` 같은 원자적 증가 쿼리를 쓰는 편이 안전하다.

## 6. ApprovalDocument

- 상태 전이 그래프:
  - `DRAFT` → `PENDING` (결재 요청)
  - `PENDING` → `APPROVED` (승인)
  - `PENDING` → `REJECTED` (반려)
  - 그 외 전이는 모두 차단.
- 작성자=결재자 차단은 Service 의 `validateApprover` 에서 한다. DB 제약으로는 표현이 까다롭다.
- `approvedAt` 은 승인 시점에만 채워지므로 nullable 이다.

## 7. Repository

- 메서드 이름 쿼리는 Spring Data JPA 가 메서드 이름의 키워드(`existsBy`, `findBy`, `Containing`, `OrderBy` 등)를 보고 JPQL 을 자동 생성한다.
- 검색 쿼리는 `findByNameContainingOrEmailContainingOrDepartment_NameContaining(...)` 처럼 길어질 수 있다. 조건이 동적이면 Querydsl/JPA Criteria 로 옮긴다.
- 페이징은 `Page<Employee> findAll(Pageable pageable)` 처럼 시그니처만 맞추면 정렬과 페이지 메타 정보를 자동으로 채워 준다.
- `findByDepartmentId(Long departmentId, Pageable pageable)` 처럼 FK 컬럼명 그대로 쓰면 된다.

## 8. 직원 등록

- `@Transactional` 은 Service 메서드에 단다. Controller 에 달면 메서드 진입 시점에 트랜잭션이 열려서 비즈니스 검증 전에 자원을 잡는다.
- 이메일/사번 중복 검사는 Repository 호출 횟수가 적은 쪽이 효율적이지만, 동시 가입 race 가 있으면 DB unique 제약이 최종 방어선이다.
- User 만 저장되고 Employee 가 실패하면 “계정은 있는데 직원 정보가 없는” 상태가 생긴다 → 트랜잭션으로 묶어서 둘 다 살거나 둘 다 죽도록 한다.
- 트랜잭션 안에서 `RuntimeException` 이 던져지면 기본 rollback. checked exception 은 기본적으로 rollback 되지 않으므로 `rollbackFor = Exception.class` 가 필요한 경우가 있다.

## 9. 검색 + 페이징

- Controller 에서 `Pageable pageable` 을 그대로 받으면 `?page=0&size=20&sort=name,asc` 가 자동 매핑된다.
- `Page<Employee> page = repo.findByXxx(...)` → `page.map(EmployeeResponse::from)` 으로 변환하면 페이지 메타 정보가 유지된다.
- 정렬을 클라이언트에 전부 맡기면 인덱스 없는 컬럼으로 정렬당할 수 있다. 허용 컬럼 목록을 두는 편이 안전하다.

## 10. 휴가 신청

- 시작일 > 종료일 → `INVALID_DATE_RANGE` (400).
- 퇴사 직원은 `EmployeeStatus.RESIGNED` 검사 후 `ACCESS_DENIED` 또는 별도 도메인 에러로 막는다.
- 초기 상태는 Entity 의 정적 팩토리에서 `PENDING` 으로 고정한다. Controller 에서 값을 받지 않는다.

## 11. 휴가 승인/반려

- “PENDING 만 처리 가능” 검증은 도메인 메서드(`leave.approve(approver)`) 안에 두면 호출부가 잊어도 안전하다.
- 반려 사유는 빈 문자열도 NG. `@NotBlank` 검증 + Service 에서 한 번 더 확인.
- 동시 승인 문제는 PESSIMISTIC_WRITE 락 또는 버전 컬럼(`@Version` 으로 낙관적 락) 으로 막는다. 학습용에서는 “문제 인식”까지 적어두는 것만으로 충분하다.

## 12. 공지사항

- “관리자만 등록” 은 Controller 의 권한 어노테이션/필터로 1차, Service 에서 2차 검증하는 것이 안전하다.
- `ORDER BY important DESC, createdAt DESC` 로 중요 공지를 항상 상단에 둔다.
- 조회수 증가를 상세 조회와 같이 두면 검색 봇 등 의도치 않은 트래픽으로 카운트가 오른다. 별도 PATCH 로 분리하는 편이 깔끔하다.

## 13. 결재 작성/요청

- 작성과 요청을 분리(`POST /approvals` + `PATCH /approvals/{id}/submit`)하면 “임시 저장” 개념이 자연스럽다. 합치면 API 가 단순하지만 DRAFT 가 의미를 잃는다.
- DRAFT → PENDING 전이 시 제목/내용/결재자 ID 가 모두 채워졌는지 확인한다.
- 작성자=결재자 검증은 Service 에서 `Objects.equals(writerId, approverId)` 로 막는다.

## 14. 결재 승인/반려

- 본인이 결재자인지 확인하지 않으면, URL 만 알면 누구나 승인할 수 있는 권한 누락 버그가 된다.
- 승인 시 `approvedAt = LocalDateTime.now()` 를 채운다.
- 반려 사유는 `@NotBlank` + Service 검증의 이중 방어선을 둔다.

## 15. ErrorCode / ErrorResponse

- HTTP status 는 클라이언트/프록시/모니터링 도구가 보는 1차 신호.
- 비즈니스 에러 코드(`EMPLOYEE_NOT_FOUND`)는 같은 status(404) 안에서도 원인을 구분하기 위한 2차 신호.
- `ErrorResponse(status, code, message, timestamp, errors?)` 형태가 무난하다.

## 16. GlobalExceptionHandler

- `@RestControllerAdvice` 가 모든 Controller 의 예외를 가로채서 일관된 응답으로 변환한다.
- `MethodArgumentNotValidException` 의 `BindingResult` 에서 `FieldError` 를 모아 `errors` 리스트로 만든다.
- 마지막 `Exception.class` 핸들러는 스택트레이스를 응답에 노출하지 말고 로그로만 남긴다.

## 17. DTO 검증

- `@NotBlank` 는 null/빈 문자열/공백 모두 거른다. `@NotNull` 보다 문자열에 적합하다.
- `@Email` 은 형식 검사. `@Size(min, max)` 와 함께 쓴다.
- `@Pattern(regexp = ...)` 는 사번 포맷처럼 형식이 고정된 값에 쓴다.
- 요청 DTO 와 응답 DTO 를 분리하면, 응답 필드를 바꿔도 요청 스펙이 깨지지 않고 그 반대도 마찬가지다.

## 18. EmployeeController

- 자원 표현은 명사(`/employees`), 동작은 HTTP 메서드(POST/GET/PUT/DELETE)로 표현한다.
- `@PathVariable` 은 URL 의 식별자, `@RequestParam` 은 쿼리스트링, `@RequestBody` 는 JSON 본문이다.
- 권한 검사는 1차로 Security 필터/`@PreAuthorize`, 2차로 Service 의 `requireAdmin(currentUser)` 같은 가드.

## 19. LeaveController

- 같은 자원(휴가)이라도 일반 사용자/관리자 관점이 다르면 경로를 분리하는 편이 보안 정책 관리가 쉽다.
- `PATCH /leaves/{id}/approve` 는 REST 순수주의 관점에서 동사를 URL 에 넣는 점이 아쉽지만, 상태 변경이 명확해서 실무에서 자주 쓴다.
- 로그인 사용자는 학습 단계에서는 `HttpSession`, Spring Security 단계에서는 `@AuthenticationPrincipal`, JWT 단계에서는 직접 만든 `@CurrentUser` 같은 어노테이션을 사용한다.

## 20. 보안 흐름

- 1차: 로그인 성공 → `session.setAttribute("userId", user.getId())` → 보호 자원은 `HandlerInterceptor` 에서 세션 검사.
- 2차: `SecurityFilterChain` 에 `formLogin` + `BCryptPasswordEncoder` + 권한 매핑(`hasRole("ADMIN")`).
- 3차: JWT 발급 → Authorization 헤더 검사 → `UsernamePasswordAuthenticationToken` 으로 SecurityContext 구성. 서버는 무상태로 유지된다.
- 세션 → JWT 진화의 핵심은 **상태를 어디에 둘 것인가**(서버 메모리 vs 클라이언트 토큰).

## 21. 통합 테스트

- `@SpringBootTest` + `@AutoConfigureMockMvc` 로 컨텍스트를 띄우고, `MockMvc.perform(...)` 으로 요청을 흘려보낸다.
- 응답 JSON 에서 다음 요청에 쓸 값은 `andReturn().getResponse().getContentAsString()` → ObjectMapper 로 파싱한다.
- 권한 실패는 `andExpect(status().isForbidden())` 또는 `.is(403)` 로 검증한다.

## 22. 문서화

- README 8섹션 예시: 프로젝트 소개 / 개발 목적 / 사용 기술 / 주요 기능 / 시스템 구조 / ERD / API 명세 / 실행 방법.
- ERD 표기: 학습 단계에서는 텍스트 트리(`User 1:1 Employee`)로 충분. 면접용 자료는 dbdiagram.io 로 시각화하면 인상이 좋다.
- 트러블슈팅 4단계 템플릿: 문제(증상) → 원인(왜) → 해결(코드/명령) → 배운 점(다음에 같은 실수를 피하는 방법).
- API 명세는 Markdown 으로 시작해서, 익숙해지면 springdoc-openapi 로 자동 생성하는 단계로 넘어간다.

## 23. 인증 — 로그인 / 로그아웃 / 내 정보 / 비밀번호 변경

- 로그인 실패 메시지에서 “이메일” / “비번” 구분을 안 하는 이유: 공격자가 이메일 존재 여부를 사용자 열거에 이용할 수 있다. → 동일 메시지로 응답.
- `passwordEncoder.matches(raw, encoded)` 는 같은 salt 로 다시 해시한 결과를 비교하므로 평문 비교가 불가능하다.
- 로그인 직후 `request.changeSessionId()` 로 세션 ID 를 새로 발급해야 Session Fixation 공격을 막을 수 있다.
- `logout()` 은 `session.invalidate()` 로 세션 자체를 폐기한다.
- 비밀번호 변경은 “현재 비번 확인” + “새 비번 해시 저장” 두 단계. 변경 후 기존 세션을 만료시키는 정책도 검토.

## 24. 부서 Service

- 부서명 중복은 `existsByName` + DB unique 두 단계. 동시 등록 race 까지 DB 가 마지막 방어선.
- 부서 삭제 시 소속 직원이 있으면 일반적으로는 거부. 운영 정책에 따라 “기본 부서로 이동” 또는 “부서 NULL 허용” 도 선택지.
- 부서 상세에서 직원까지 N+1 없이 가져오려면 `@EntityGraph` 또는 fetch join 으로 한 번에 끌어온다.
- 부서가 1000개를 넘으면 페이징 + 부서 그룹화(본부/팀)를 도입하는 것이 자연스럽다.

## 25. 부서 Controller

- 권한 검사는 `@PreAuthorize` (Security 단) + Service 의 `requireAdmin(role)` (도메인 단) 다층 방어.
- 부분 수정이라면 PATCH 가 의미상 정확하지만, PRD 처럼 PUT 으로 두는 경우도 흔하다. 팀 컨벤션에 맞춘다.
- `DepartmentDetailResponse` 는 부서 정보 + 소속 직원 nested record 로 두면 응답 모양이 명확하다.

## 26. 공지 Controller

- GET 안에서 조회수를 올리면 검색 봇/프리뷰 요청으로 카운트가 부풀려진다. 별도 PATCH `/{id}/view` 로 분리하는 패턴이 안전.
- 목록 응답에 `content` 까지 그대로 넣으면 페이로드가 커진다. 목록은 요약 DTO, 상세는 풀 DTO 로 분리하는 편이 깔끔.
- `@PageableDefault(size = 20)` 처럼 기본 페이지 크기를 두면 클라이언트가 size 를 누락해도 안전.

## 27. 결재 Controller

- `/my` 는 작성자가 보는 화면, `/pending` 은 결재자가 보는 화면. 권한이 다르므로 경로 분리.
- 상태 전이를 동사형(`/submit`, `/approve`, `/reject`) 으로 표현하면 의미가 분명하지만 REST 순수주의에서는 비선호. 실무에서는 의미가 우선.
- `hasAnyRole('APPROVER','ADMIN')` 으로 두 역할을 모두 허용. role 문자열에 자동으로 `ROLE_` 접두사가 붙는다는 점 주의.

## 28. 직원 상세 / 수정 / 퇴사

- LAZY 관계가 응답 변환 시점에 풀리려면 트랜잭션이 살아있어야 한다(`open-in-view: true` 의 함정). Service 안에서 DTO 까지 변환해 반환하는 것이 가장 안전.
- 부서 변경은 도메인 메서드(`changeDepartment(newDept)`)로 캡슐화. Service 가 직접 setter 를 호출하면 다음 호출자가 검증을 잊는다.
- 퇴사 처리는 hard delete 가 아니라 `RESIGNED` 상태로 두어 휴가/결재/공지에 남은 참조가 깨지지 않도록 한다.

## 29. 휴가 — 내 목록 / 상세 / 취소

- “내 휴가만” 검증을 Service 가 한 뒤에도, Repository 쿼리 자체에 `employeeId` 조건을 두는 편이 IDOR 공격에 안전하다.
- 본인 PENDING 휴가만 취소 가능. 도메인 메서드(`cancelByOwner`) 안에서 상태 검증을 캡슐화.
- 관리자 목록의 동적 조건은 학습 단계에서는 if 분기로 충분. 조건이 4개 이상으로 늘면 Querydsl/Specification.
- `CANCELED` 를 별도 상태로 두면 “직원이 직접 취소한 것” 과 “관리자가 반려한 것” 을 통계에서 구분할 수 있다.

## 30. 결재 — my / pending / detail 권한

- 상세 조회는 작성자 또는 결재자만 허용. 둘 다 아니면 `ACCESS_DENIED(403)`.
- 결재자가 변경 가능한 모델이라면 `pending` 쿼리는 현재 `approverId` 기준이므로 위임 직후부터 새 결재자에게 노출된다.
- 관리자에게 모든 문서를 보여주려면 Service 시그니처에 `UserRole role` 을 추가해 관리자 우회 분기를 두는 편이 명확.

## 31. 모든 Repository

- `findByUser_Email` 같은 nested property 는 `_` 로 연관 경로를 표시.
- 휴가 기간 겹침은 `start <= :endDate AND end >= :startDate` 가 “구간이 겹친다” 의 표준 조건.
- 조회수 증가는 `@Modifying @Query("UPDATE ... SET view_count = view_count + 1")` 로 원자적으로 처리해야 동시성 손실이 없다.
- 메서드 이름이 3 단어를 넘으면 보통 `@Query` 로 옮기는 편이 가독성에 좋다.

## 32. 응답 DTO 매핑 패턴

- `Page<Entity>.map(Response::from)` 으로 페이지 메타(totalElements, totalPages) 가 자연스럽게 유지된다.
- `record` 는 불변 + equals/hashCode 자동 생성으로 응답 DTO 에 적합.
- DTO 가 Entity 의 setter 를 호출하면 양방향 의존이 생기고, DTO 가 도메인 규칙을 우회할 수 있게 된다.
- MapStruct 는 DTO 가 20개를 넘어가고, 변환 코드가 단순 반복이 될 때 도입.

## 33. Bean 설정

- `@EnableJpaAuditing` 이 없으면 AuditingEntityListener 가 작동하지 않아 `createdAt` 이 null 로 들어온다.
- BCrypt 가 SHA-256 보다 안전한 이유: (1) salt 가 매 해시마다 다르고 결과에 포함됨 (2) work factor(cost) 로 해시 비용을 늘려 brute force 를 어렵게 함.
- `WebMvcConfigurer` 의 `addInterceptors` 는 진입 전 후 가로채기, `addArgumentResolvers` 는 메서드 인자 주입.
- 1차(세션) 와 2차(Security) 를 동시에 켜면 인증 책임이 두 곳에 흩어져 디버깅이 어렵다.

## 34. @CurrentUser + Interceptor

- ArgumentResolver 가 없다면 Controller 마다 `session.getAttribute("USER_ID")` + null 체크 + 캐스팅이 반복된다.
- Filter 가 더 먼저 실행되고, Interceptor 는 DispatcherServlet 이후. ArgumentResolver 는 Controller 메서드 진입 직전.
- JWT 단계에서는 ArgumentResolver 가 `SecurityContext.getAuthentication()` 에서 사용자 정보를 꺼내도록 바뀐다.

## 35. Thymeleaf

- `th:text` 는 escape, `th:utext` 는 raw HTML 을 그대로 출력 → XSS 위험. 신뢰된 데이터에만 사용.
- `th:href="@{/employees/{id}(id=${emp.employeeId})}"` 형태로 동적 URL 을 만든다.
- 폼에 `${_csrf.parameterName}` / `${_csrf.token}` 으로 CSRF 토큰을 자동 삽입.
- Fragment 재사용: `th:fragment="header"` 선언 → `th:replace="~{fragments/header :: header}"` 로 삽입.
- `@RestController` 는 JSON, `@Controller` 는 view 이름을 반환.

## 36. 패키지 구조

- 의존 방향: Controller → Service → Repository → Domain. Domain 은 거꾸로 의존하지 않는다.
- DTO 는 Entity 를 import 할 수 있지만, Entity 가 DTO 를 import 하면 도메인 책임이 흐려진다.
- 계층 우선 → 도메인이 6개 이상이 되면 도메인 우선으로 자연스럽게 전환.
- 안티 패턴 중 가장 빠지기 쉬운 것: Controller 가 Repository 직접 호출. Service 가 한 줄짜리 wrap 만 한다는 핑계로 자주 발생.

## 37. 비즈니스 규칙 매트릭스

- 같은 검증을 여러 계층에 두는 이유: 다층 방어. 한 곳을 우회해도 다른 곳에서 잡힌다.
- DB unique 가 있는데도 Service 에서 `exists` 를 부르는 이유: 사용자 친화적인 에러 메시지를 빨리 돌려주기 위해. unique 위반의 DB 예외는 직접 보여주기 어렵다.
- 도메인 메서드의 `IllegalStateException` 을 그대로 두면 GlobalExceptionHandler 에서 일관된 ErrorCode 매핑이 어려워진다. 대신 BusinessException(ErrorCode.INVALID_STATUS) 를 던지는 편이 깔끔.

## 38. 트러블슈팅

- 휴가 승인 중복: 도메인 메서드 안에 상태 검증을 두면 호출 경로가 늘어나도 깨지지 않는다.
- 직원 등록 부분 저장: `@Transactional` 로 원자성 보장.
- Entity 직접 반환: DTO 분리 + Entity 에 직렬화 어노테이션 금지.
- LazyInitializationException: Service 안에서 DTO 까지 변환해 반환하거나, fetch join 으로 미리 로드.
- 이메일 동시 가입: 애플리케이션 검증 + DB unique 다층 방어. DataIntegrityViolationException 을 ErrorCode 로 매핑.

## 39. 면접 카드 + 커밋

- 30초 면접 답변의 공식: (1) 어떤 문제를 풀었나 → (2) 어떻게 풀었나 → (3) 무엇을 배웠나.
- 커밋 메시지 “왜” 만 적기 — “무엇” 은 diff 가 보여준다. 한 커밋 = 한 의도.
- PR 4섹션: 무엇 / 왜 / 어떻게 테스트했나 / 영향 범위. 면접에서 PR 링크를 보여줄 때 이 4섹션이 채워져 있으면 인상이 좋다.
