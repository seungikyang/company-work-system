# 기능 구현 TODO/빈칸 워크북

이 문서는 `company_work_system_PRD_TRD.md` 의 모든 기능 요구사항을 구현 단위로 다시 쪼개는 학습용 워크북입니다.

기존 `practice/problems.md` 와 `practice/starter/` 가 코드 조각 중심이라면, 이 문서는 **FR ID → 화면/API → Controller → Service → Repository → Entity/DTO → 테스트** 흐름을 직접 채워보게 합니다.

각 FR ID를 학습한 뒤에는 [취업 준비 통합 워크북](./job-preparation-workbook.md)의 `기능별 구현 증거`에 실제 코드 위치, 테스트 결과, 커밋과 설계 이유를 기록합니다. 빈칸 풀이만 끝낸 기능은 포트폴리오 완료로 표시하지 않습니다.

## 사용 규칙

1. 구현할 FR ID를 고르고 `E-{FR ID}` 형식의 증거 ID를 만든다.
2. 각 기능의 `개념 빈칸`을 먼저 채운다.
3. 이어서 `구현 TODO`의 계층별 빈칸을 채운다.
4. 마지막으로 `테스트 TODO`를 Given/When/Then 으로 한 줄씩 적는다.
5. 막히면 PRD/TRD 절 번호와 `practice/starter` 파일을 다시 본다.
6. 실행 프로젝트 구현과 검증 후 통합 워크북의 같은 증거 ID를 갱신한다.

## 기능 시작 카드

기능을 시작할 때 아래 다섯 칸을 먼저 채웁니다.

| 항목 | 직접 작성 |
|---|---|
| 증거 ID | `E-____` |
| 채용 공고의 요구 역량 |  |
| 이번 세션의 완료 조건 |  |
| 반드시 실패해야 하는 시나리오 |  |
| 검증할 명령 |  |

## 공통 구현 지도

아래 빈칸을 먼저 채우면 모든 기능의 구현 방향이 흔들리지 않습니다.

| 계층 | 책임 | 빈칸 |
|---|---|---|
| Controller | HTTP 요청/응답, 인증 사용자 주입, DTO 검증 | Controller 는 Repository 를 직접 호출하지 않고 ____ 를 호출한다. |
| Service | 비즈니스 규칙, 트랜잭션, 권한/소유자 검증 | 상태 전이는 주로 ____ 계층 또는 도메인 메서드에서 검증한다. |
| Repository | DB 조회/저장, 조건 검색, 페이징 | 복잡한 조건은 메서드 이름이 길어지면 ____ 로 옮긴다. |
| Entity | 식별자, 연관관계, 상태값, 도메인 메서드 | 상태값은 String 대신 ____ 으로 관리한다. |
| DTO | 요청/응답 계약, Validation | Entity 를 그대로 응답하지 않고 ____ DTO 로 변환한다. |
| Exception | 공통 에러 응답 | 예외 응답은 status, code, message, ____ 를 포함한다. |
| Test | 성공/실패/권한/상태 전이 검증 | 승인/반려 기능은 ____ 상태가 아닌 경우를 반드시 테스트한다. |

## 전체 기능 추적표

| FR ID | 기능 | 구현 핵심 | 관련 starter |
|---|---|---|---|
| FR-USER-001 | 로그인 | 이메일 조회, 비밀번호 해시 비교, 세션 생성 | `23-auth-login/*`, `20-security-session` |
| FR-USER-002 | 로그아웃 | 세션 무효화 | `23-auth-login/*` |
| FR-USER-003 | 내 정보 조회 | current user 조회, 응답 DTO | `23-auth-login/*`, `34-current-user-interceptor` |
| FR-USER-004 | 비밀번호 변경 | 현재 비밀번호 검증, 새 비밀번호 해시 | `23-auth-login/*` |
| FR-USER-005 | 권한 구분 | USER/ADMIN/APPROVER, 접근 제어 | `20-security-session`, `34-current-user-interceptor` |
| FR-EMP-001 | 직원 등록 | User + Employee 원자적 저장 | `08-employee-register`, `17-dto-validation` |
| FR-EMP-002 | 직원 목록 조회 | Page 조회, DTO 변환 | `09-employee-search`, `18-controller-employee` |
| FR-EMP-003 | 직원 상세 조회 | 단건 조회, LAZY 관계 DTO 변환 | `28-employee-detail-update` |
| FR-EMP-004 | 직원 정보 수정 | 부서 존재 확인, 도메인 메서드 | `28-employee-detail-update` |
| FR-EMP-005 | 직원 삭제/비활성화 | soft delete, RESIGNED 상태 | `28-employee-detail-update` |
| FR-EMP-006 | 직원 검색 | 이름/이메일/부서 조건 | `07-repository`, `09-employee-search` |
| FR-EMP-007 | 페이징 | Pageable, Page.map | `09-employee-search`, `32-response-dto-mapping` |
| FR-DEPT-001 | 부서 등록 | 부서명 중복 검증 | `24-department-service`, `25-department-controller` |
| FR-DEPT-002 | 부서 목록 조회 | 전체 또는 페이징 목록 | `24-department-service`, `25-department-controller` |
| FR-DEPT-003 | 부서 상세 조회 | 소속 직원 포함, N+1 주의 | `24-department-service`, `25-department-controller` |
| FR-DEPT-004 | 부서 정보 수정 | PUT/PATCH 정책, unique 유지 | `24-department-service`, `25-department-controller` |
| FR-DEPT-005 | 부서 삭제 | 소속 직원 존재 시 삭제 정책 | `24-department-service` |
| FR-LEAVE-001 | 휴가 신청 | 날짜 검증, PENDING 생성 | `10-leave-request`, `19-controller-leave` |
| FR-LEAVE-002 | 내 휴가 목록 조회 | 소유자 조건, Page 조회 | `29-leave-my-cancel` |
| FR-LEAVE-003 | 휴가 상세 조회 | 본인/관리자 권한 분기 | `29-leave-my-cancel` |
| FR-LEAVE-004 | 휴가 신청 취소 | 본인 + PENDING 상태만 취소 | `29-leave-my-cancel` |
| FR-LEAVE-005 | 휴가 승인 | 관리자, PENDING → APPROVED | `11-leave-approval` |
| FR-LEAVE-006 | 휴가 반려 | 관리자, 반려 사유 필수 | `11-leave-approval` |
| FR-LEAVE-007 | 휴가 상태 조회 | 상태 enum 기반 목록/상세 | `04-leave-entity`, `29-leave-my-cancel` |
| FR-LEAVE-008 | 관리자 휴가 목록 조회 | 상태/직원 조건별 전체 휴가 조회 | `19-controller-leave`, `29-leave-my-cancel` |
| FR-NOTICE-001 | 공지 등록 | ADMIN 만 작성 | `12-notice-service`, `26-notice-controller` |
| FR-NOTICE-002 | 공지 목록 조회 | 중요 공지 우선 정렬 | `12-notice-service`, `26-notice-controller` |
| FR-NOTICE-003 | 공지 상세 조회 | 조회수 증가 정책 | `12-notice-service`, `26-notice-controller` |
| FR-NOTICE-004 | 공지 수정 | 작성 권한, 필수값 검증 | `12-notice-service`, `26-notice-controller` |
| FR-NOTICE-005 | 공지 삭제 | ADMIN 만 삭제 | `12-notice-service`, `26-notice-controller` |
| FR-NOTICE-006 | 중요 공지 표시 | important DESC 정렬 | `05-notice-entity`, `12-notice-service` |
| FR-APPROVAL-001 | 결재 문서 작성 | DRAFT 생성, 작성자/결재자 검증 | `13-approval-document` |
| FR-APPROVAL-002 | 결재 요청 | DRAFT → PENDING | `13-approval-document`, `27-approval-controller` |
| FR-APPROVAL-003 | 내 결재 목록 조회 | writerId 조건 | `30-approval-lists` |
| FR-APPROVAL-004 | 결재 상세 조회 | 작성자/결재자/ADMIN 조회 | `30-approval-lists` |
| FR-APPROVAL-005 | 결재 승인 | approver 검증, approvedAt 기록 | `14-approval-decision` |
| FR-APPROVAL-006 | 결재 반려 | approver 검증, 반려 사유 필수 | `14-approval-decision` |
| FR-APPROVAL-007 | 결재 상태 조회 | DRAFT/PENDING/APPROVED/REJECTED | `06-approval-entity`, `30-approval-lists` |

---

# 1. 회원/인증 기능

## FR-USER-001 로그인

개념 빈칸:

- 인증(Authentication)은 사용자가 ____ 인지 확인하는 과정이다.
- 인가(Authorization)는 사용자가 특정 기능을 ____ 수 있는지 판단하는 과정이다.
- 비밀번호는 평문 비교가 아니라 `passwordEncoder.____(raw, encoded)` 로 검증한다.
- 로그인 실패 메시지를 이메일/비밀번호로 나누지 않는 이유는 ____ 공격을 줄이기 위해서다.

구현 TODO:

- Request DTO: `email`, `password` 에 각각 `@____`, `@____` 를 적용한다.
- Repository: `Optional<User> findBy____(String email)` 을 만든다.
- Service:
  - 이메일을 `trim().toLowerCase()` 로 ____ 한다.
  - 사용자를 찾지 못하면 `ErrorCode.____` 또는 로그인 실패 전용 코드를 던진다.
  - 비밀번호 불일치도 같은 메시지로 응답한다.
  - 성공 시 세션에 `USER_ID`, `USER_ROLE` 을 저장한다.
  - 세션 고정 공격 방지를 위해 `request.____()` 를 호출한다.
- Controller:
  - `POST /api/auth/____`
  - 성공 응답에는 비밀번호를 절대 포함하지 않는다.

테스트 TODO:

- Given 등록된 사용자, When 올바른 비밀번호로 로그인, Then 세션에 ____ 가 저장된다.
- Given 등록된 사용자, When 틀린 비밀번호로 로그인, Then HTTP ____ 와 공통 에러 응답을 받는다.

## FR-USER-002 로그아웃

개념 빈칸:

- 세션 로그아웃은 서버가 보관하던 인증 상태를 ____ 하는 것이다.
- JWT 로그아웃은 서버가 토큰 상태를 보관하지 않으면 즉시 강제 로그아웃이 ____.

구현 TODO:

- Service: `session.____()` 로 기존 세션을 폐기한다.
- Controller: `POST /api/auth/____`
- 응답: 본문 없이 `204 No Content` 또는 메시지 포함 `200 OK` 중 팀 컨벤션을 정한다.

테스트 TODO:

- Given 로그인된 세션, When 로그아웃, Then 이후 보호 API 호출은 HTTP ____ 이다.

## FR-USER-003 내 정보 조회

개념 빈칸:

- 내 정보 조회는 URL 에 userId 를 받기보다 현재 인증 사용자에서 ____ 를 꺼내는 편이 안전하다.
- 응답 DTO 에 password, internal token, salt 같은 값은 ____.

구현 TODO:

- 인증 사용자 주입:
  - 1차 세션 방식: `HttpSession.getAttribute("____")`
  - 개선 방식: `@____ Long currentUserId`
  - Spring Security 방식: `@____`
- Service: `userRepository.findById(currentUserId)` 로 조회한다.
- Response DTO: `userId`, `email`, `name`, `role`, `employeeId`, `departmentName` 정도만 포함한다.
- Controller: `GET /api/users/____`

테스트 TODO:

- Given 로그인하지 않은 사용자, When 내 정보 조회, Then HTTP ____.
- Given 로그인한 사용자, When 내 정보 조회, Then 응답에 ____ 필드가 없다.

## FR-USER-004 비밀번호 변경

개념 빈칸:

- 비밀번호 변경 전 현재 비밀번호를 다시 확인하는 이유는 ____ 된 세션 피해를 줄이기 위해서다.
- 새 비밀번호는 저장 전 반드시 ____ 해야 한다.

구현 TODO:

- Request DTO:
  - `currentPassword`: `@NotBlank`
  - `newPassword`: `@Size(min = ____, max = ____)`
- Service:
  - 현재 사용자 조회
  - `passwordEncoder.matches(currentPassword, user.getPassword())`
  - 새 비밀번호를 `passwordEncoder.____(...)`
  - `user.changePassword(encodedPassword)`
- Controller: `PATCH /api/users/me/password`

테스트 TODO:

- 현재 비밀번호가 틀리면 HTTP ____.
- 성공 후 기존 raw password 로는 `matches` 가 ____ 이어야 한다.

## FR-USER-005 권한 구분

개념 빈칸:

- USER, ADMIN, APPROVER 는 `UserRole` ____ 으로 정의한다.
- `@Enumerated(EnumType.____)` 를 사용하는 이유는 enum 순서 변경에 안전하기 때문이다.
- ADMIN 권한 검사는 Controller 에서 1차, ____ 에서 2차로 할 수 있다.

구현 TODO:

- `UserRole`: `USER`, `ADMIN`, `APPROVER`
- 세션 방식:
  - 로그인 시 `USER_ROLE` 저장
  - Interceptor 또는 Service 가 `role == UserRole.____` 검사
- Spring Security 방식:
  - `@PreAuthorize("hasRole('____')")`
  - `@PreAuthorize("hasAnyRole('APPROVER','____')")`

테스트 TODO:

- USER 가 직원 등록 API 를 호출하면 HTTP ____.
- APPROVER 가 결재 승인 API 를 호출하면 HTTP ____.

---

# 2. 직원 관리 기능

## FR-EMP-001 직원 등록

개념 빈칸:

- 직원 등록은 로그인 계정인 ____ 와 인사 정보인 ____ 를 함께 만든다.
- 두 객체 중 하나만 저장되는 문제를 막기 위해 Service 에 `@____` 을 적용한다.
- 이메일/사번 중복은 Service 검증과 DB ____ 제약으로 이중 방어한다.

구현 TODO:

- Request DTO: `email`, `password`, `name`, `departmentId`, `employeeNumber`, `position`, `phone`, `hireDate`
- Repository:
  - `userRepository.existsBy____(email)`
  - `employeeRepository.existsBy____(employeeNumber)`
  - `departmentRepository.findById(departmentId)`
- Service:
  - ADMIN 권한 확인
  - 이메일 중복 확인
  - 사번 중복 확인
  - 부서 존재 확인
  - 비밀번호 `____`
  - `User.create(...)`
  - `Employee.create(user, department, ...)`
- Controller: `POST /api/____`
- Response: `employeeId`, `name`, `email`, `departmentName`, `employeeNumber`, `status`

테스트 TODO:

- 직원 등록 성공 시 users 와 employees 가 모두 ____ 된다.
- Employee 저장 실패를 강제로 발생시키면 User 저장도 ____ 된다.
- 중복 이메일은 HTTP ____ 와 `DUPLICATE_EMAIL`.

## FR-EMP-002 직원 목록 조회

개념 빈칸:

- 목록 조회는 데이터가 많아질 수 있으므로 ____ 를 적용한다.
- Entity 목록을 그대로 응답하지 않고 `Page<EmployeeResponse>` 로 ____ 한다.

구현 TODO:

- Controller: `GET /api/employees?page=0&size=20&sort=name,asc`
- Service:
  - ADMIN 권한 확인
  - `employeeRepository.findAll(pageable)`
  - `page.____(EmployeeResponse::from)`
- Response DTO 에는 user.password 가 포함되면 안 된다.

테스트 TODO:

- size=2 로 조회하면 응답 content 크기는 최대 ____ 이다.
- USER 권한 조회는 HTTP ____.

## FR-EMP-003 직원 상세 조회

개념 빈칸:

- 상세 조회는 PathVariable 로 ____ 를 받는다.
- LAZY 관계는 트랜잭션 밖에서 접근하면 ____ 예외가 날 수 있다.

구현 TODO:

- Repository:
  - `findById(employeeId)` 또는 상세 전용 fetch join
- Service:
  - ADMIN 권한 확인
  - 직원 없으면 `ErrorCode.____`
  - Service 안에서 DTO 로 변환
- Controller: `GET /api/employees/{____}`

테스트 TODO:

- 없는 직원 ID 조회 시 HTTP ____.
- 응답에 부서명과 사번이 포함된다.

## FR-EMP-004 직원 정보 수정

개념 빈칸:

- 수정 시 Service 가 setter 를 직접 여러 번 부르기보다 Entity 의 ____ 메서드를 쓰면 규칙이 모인다.
- 부서 변경은 새 departmentId 가 실제 존재하는지 먼저 ____ 해야 한다.

구현 TODO:

- Request DTO: `name`, `departmentId`, `position`, `phone`, `status`
- Service:
  - 직원 조회
  - 부서 변경 요청이 있으면 부서 조회
  - `employee.updateProfile(...)`
  - `employee.changeDepartment(department)`
- Controller: `PUT /api/employees/{employeeId}`

테스트 TODO:

- 존재하지 않는 부서로 변경하면 HTTP ____.
- 수정 후 상세 조회에서 변경값이 ____ 된다.

## FR-EMP-005 직원 삭제/비활성화

개념 빈칸:

- 실무 인사 시스템에서는 과거 휴가/결재 기록 때문에 hard delete 보다 ____ delete 가 안전하다.
- 퇴사 상태 직원은 휴가 신청과 결재 요청을 할 수 ____.

구현 TODO:

- Entity: `EmployeeStatus.RESIGNED`
- 도메인 메서드: `employee.____()`
- Service:
  - 관련 기록이 있으면 삭제 대신 상태 변경
  - 실제 DELETE 를 쓸 경우 FK 제약 검토
- Controller: `DELETE /api/employees/{employeeId}` 또는 `PATCH /api/employees/{employeeId}/resign`

테스트 TODO:

- 퇴사 처리 후 직원 상태는 ____.
- 퇴사 직원이 휴가 신청하면 HTTP ____.

## FR-EMP-006 직원 검색

개념 빈칸:

- 검색 조건이 이름/이메일/부서명으로 늘어나면 단순 메서드 이름 쿼리가 ____ 질 수 있다.
- 검색 결과도 목록이므로 ____ 를 유지한다.

구현 TODO:

- Controller: `GET /api/employees?keyword=kim&departmentId=1`
- Repository:
  - 단순 버전: `findByUser_NameContainingOrUser_EmailContaining(...)`
  - 확장 버전: `@Query` 또는 Specification/Querydsl
- Service:
  - keyword 가 blank 면 전체 조회
  - keyword 를 trim 한다.

테스트 TODO:

- 이름 일부로 검색하면 해당 직원만 나온다.
- 부서 조건과 keyword 조건을 함께 주면 두 조건을 ____ 한다.

## FR-EMP-007 페이징

개념 빈칸:

- Spring Data 의 `Page` 는 content 뿐 아니라 totalElements, totalPages 같은 ____ 정보를 가진다.
- `Page.map` 을 쓰면 페이징 메타 정보가 ____ 된다.

구현 TODO:

- Controller 파라미터: `Pageable pageable`
- 기본값: `@PageableDefault(size = ____, sort = "id")`
- 정렬 허용 컬럼을 제한하는 정책을 적는다: ____.

테스트 TODO:

- page=1, size=10 요청 시 두 번째 페이지가 조회된다.
- 허용하지 않는 sort 컬럼은 ____.

---

# 3. 부서 관리 기능

## FR-DEPT-001 부서 등록

개념 빈칸:

- 부서명은 사용자 식별성이 높으므로 DB 에 ____ 제약을 둔다.
- 같은 이름의 부서 동시 등록은 Service 의 exists 검사를 통과해도 DB 에서 ____ 될 수 있다.

구현 TODO:

- Request DTO: `name`, `description`
- Service:
  - ADMIN 권한 확인
  - `departmentRepository.existsBy____(name)`
  - `Department.create(name, description)`
- Controller: `POST /api/____`

테스트 TODO:

- 중복 부서명은 `DUPLICATE_DEPARTMENT_NAME` 으로 응답한다.

## FR-DEPT-002 부서 목록 조회

개념 빈칸:

- 부서는 직원보다 수가 적어 MVP 에서는 전체 목록 조회가 가능하지만, 1000개 이상이면 ____ 를 고려한다.

구현 TODO:

- Controller: `GET /api/departments`
- Service: `departmentRepository.findAll(Sort.by("name").____())`
- Response: `departmentId`, `name`, `description`

테스트 TODO:

- 로그인 사용자는 USER/ADMIN/APPROVER 모두 조회 가능하다.

## FR-DEPT-003 부서 상세 조회

개념 빈칸:

- 부서 상세에 소속 직원 목록을 포함하면 N+1 문제를 피하기 위해 ____ 또는 EntityGraph 를 사용할 수 있다.

구현 TODO:

- Repository:
  - `findById(departmentId)`
  - 소속 직원: `employeeRepository.findByDepartment_Id(departmentId, pageable)`
- Response: 부서 정보 + 소속 직원 요약 DTO
- Controller: `GET /api/departments/{departmentId}`

테스트 TODO:

- 없는 부서 ID 는 HTTP ____.
- 상세 응답에 employee summary 목록이 포함된다.

## FR-DEPT-004 부서 정보 수정

개념 빈칸:

- 전체 갱신은 ____, 부분 갱신은 ____ HTTP 메서드가 자연스럽다.
- 이름 변경 시에도 중복 검증은 ____.

구현 TODO:

- Controller: `PUT /api/departments/{departmentId}` 또는 `PATCH /api/departments/{departmentId}`
- Service:
  - ADMIN 권한 확인
  - 부서 조회
  - 새 이름이 기존 이름과 다르면 중복 확인
  - `department.update(name, description)`

테스트 TODO:

- 다른 부서가 이미 쓰는 이름으로 변경하면 HTTP ____.

## FR-DEPT-005 부서 삭제

개념 빈칸:

- 소속 직원이 있는 부서를 삭제하면 직원의 FK 가 ____ 될 수 있다.
- 삭제 정책 세 가지: 삭제 거부, 기본 부서 이동, ____ 허용.

구현 TODO:

- Repository: `employeeRepository.countBy____(departmentId)`
- Service:
  - count > 0 이면 `DEPARTMENT_HAS_EMPLOYEES`
  - count == 0 이면 delete
- Controller: `DELETE /api/departments/{departmentId}`

테스트 TODO:

- 직원이 있는 부서 삭제 시 HTTP ____.
- 직원이 없는 부서는 삭제 후 상세 조회가 ____.

---

# 4. 휴가 신청 기능

## FR-LEAVE-001 휴가 신청

개념 빈칸:

- 휴가 기간은 시간보다 날짜가 중요하므로 `____` 타입이 자연스럽다.
- 시작일이 종료일보다 늦으면 `ErrorCode.____`.
- 신규 휴가 상태는 `ApprovalStatus.____`.

구현 TODO:

- Request DTO: `leaveType`, `startDate`, `endDate`, `reason`
- Service:
  - currentUserId 로 Employee 조회
  - 퇴사 상태인지 확인
  - 날짜 범위 검증
  - 기간 겹침 검증 선택: `startDate <= requestedEnd AND endDate >= requestedStart`
  - `LeaveRequest.create(employee, leaveType, startDate, endDate, reason)`
- Controller: `POST /api/____`

테스트 TODO:

- 시작일 > 종료일은 HTTP ____.
- 성공 시 상태는 ____.

## FR-LEAVE-002 내 휴가 목록 조회

개념 빈칸:

- IDOR 공격을 막으려면 URL 의 employeeId 보다 인증 사용자 기반 ____ 조건을 쓴다.

구현 TODO:

- Repository: `findByEmployee_Id(currentEmployeeId, pageable)`
- Service: current employee 의 휴가만 조회
- Controller: `GET /api/leaves/____`

테스트 TODO:

- A 사용자가 B 사용자의 휴가를 목록에서 볼 수 ____.

## FR-LEAVE-003 휴가 상세 조회

개념 빈칸:

- 상세 조회는 본인 또는 ADMIN 만 가능하도록 ____ 검증을 한다.

구현 TODO:

- Repository: `findById(leaveId)`
- Service:
  - 휴가 없으면 `LEAVE_NOT_FOUND`
  - 본인 여부 또는 ADMIN 여부 확인
- Controller: `GET /api/leaves/{leaveId}`

테스트 TODO:

- 작성자가 아닌 USER 는 HTTP ____.
- ADMIN 은 다른 직원의 휴가도 조회 가능하다.

## FR-LEAVE-004 휴가 신청 취소

개념 빈칸:

- 취소는 본인의 ____ 상태 휴가만 허용한다.
- 취소 상태를 별도로 둘 경우 enum 에 ____ 를 추가한다.

구현 TODO:

- Entity: `leave.cancelByOwner(currentEmployeeId)`
- Service:
  - 본인 여부 확인
  - 상태 검증
- Controller: `PATCH /api/leaves/{leaveId}/cancel`

테스트 TODO:

- APPROVED 휴가는 취소할 수 ____.

## FR-LEAVE-005 휴가 승인

개념 빈칸:

- 승인/반려는 상태 전이이므로 같은 요청을 두 번 보내도 데이터가 이상해지지 않도록 ____ 을 검증한다.
- 동시에 두 관리자가 승인하면 ____ 락 또는 ____ 락을 고려한다.

구현 TODO:

- Service:
  - ADMIN 권한 확인
  - 휴가 조회
  - `leave.approve(approverEmployeeId)`
  - 승인자 ID 저장
  - 상태 `____`
- Controller: `PATCH /api/admin/leaves/{leaveId}/approve`

테스트 TODO:

- PENDING 휴가 승인 성공.
- 이미 승인된 휴가 재승인은 HTTP ____.

## FR-LEAVE-006 휴가 반려

개념 빈칸:

- 반려 사유는 빈 문자열이 아니어야 하므로 `@____` 를 적용한다.

구현 TODO:

- Request DTO: `rejectReason`
- Service:
  - ADMIN 권한 확인
  - 휴가 조회
  - `leave.reject(approverEmployeeId, rejectReason)`
  - 상태 `____`
- Controller: `PATCH /api/admin/leaves/{leaveId}/reject`

테스트 TODO:

- 반려 사유가 blank 면 HTTP ____.
- PENDING 이 아닌 휴가는 반려할 수 없다.

## FR-LEAVE-007 휴가 상태 조회

개념 빈칸:

- 상태값은 `PENDING`, `APPROVED`, `REJECTED` 를 ____ 으로 관리한다.
- 목록에서 status 조건을 받으면 통계/관리 화면에서 ____ 필터가 가능하다.

구현 TODO:

- Query param: `status=PENDING`
- Repository: `findByStatus(status, pageable)` 또는 관리자 조건별 메서드
- Response: 상태 enum 문자열 포함

테스트 TODO:

- status=PENDING 요청 시 승인/반려 건은 제외된다.

## FR-LEAVE-008 관리자 휴가 목록 조회

개념 빈칸:

- 관리자 휴가 목록은 개인의 `/api/leaves/my` 와 달리 전체 직원의 휴가를 ____ 조건으로 조회한다.
- 일반 사용자가 전체 휴가 목록을 볼 수 있으면 다른 직원의 사유/기간이 노출되는 ____ 문제가 생긴다.

구현 TODO:

- Controller: `GET /api/admin/leaves`
- Query param: `status`, `employeeId`, `from`, `to`
- 권한: `@PreAuthorize("hasRole('____')")`
- Service:
  - ADMIN 권한 확인
  - status 와 employeeId 가 모두 있으면 `findByStatusAndEmployee_Id`
  - status 만 있으면 `findByStatus`
  - employeeId 만 있으면 `findByEmployee_Id`
  - 조건이 없으면 `findAll(pageable)`
- Response: `Page<LeaveResponse>`

테스트 TODO:

- ADMIN 은 전체 휴가 목록을 조회할 수 있다.
- USER 가 `/api/admin/leaves` 를 호출하면 HTTP ____.
- status=PENDING 조건을 주면 대기 중인 휴가만 반환된다.

---

# 5. 공지사항 기능

## FR-NOTICE-001 공지 등록

개념 빈칸:

- 공지 등록은 ____ 권한만 가능하다.
- 제목과 내용은 `@____` 로 필수 검증한다.

구현 TODO:

- Request DTO: `title`, `content`, `important`
- Service:
  - ADMIN 권한 확인
  - writerId 저장
  - `viewCount = ____`
- Controller: `POST /api/notices`

테스트 TODO:

- USER 가 공지 등록하면 HTTP ____.

## FR-NOTICE-002 공지 목록 조회

개념 빈칸:

- 공지 목록은 로그인 사용자 모두가 볼 수 있지만, 등록/수정/삭제는 ____ 만 가능하다.
- 중요 공지 우선 정렬은 `important ____ , createdAt ____`.

구현 TODO:

- Repository: `findAll(pageable)` with Sort
- Service: 기본 정렬을 important desc + createdAt desc 로 구성
- Controller: `GET /api/notices`
- Response: 목록 DTO 는 긴 `content` 대신 ____ 를 둘 수 있다.

테스트 TODO:

- important=true 공지가 일반 공지보다 먼저 나온다.

## FR-NOTICE-003 공지 상세 조회

개념 빈칸:

- GET 상세 조회에서 조회수를 증가시키면 검색 봇/미리보기 요청으로 조회수가 ____ 수 있다.
- 원자적 조회수 증가는 `UPDATE notice SET view_count = view_count + ____` 형태가 안전하다.

구현 TODO:

- Service:
  - 공지 조회
  - 조회수 증가 정책 선택: 상세 조회 안에서 증가 또는 별도 `PATCH /view`
  - 상세 DTO 반환
- Repository:
  - `@Modifying @Query` 로 조회수 증가
- Controller: `GET /api/notices/{noticeId}`

테스트 TODO:

- 상세 조회 후 viewCount 가 1 증가한다.

## FR-NOTICE-004 공지 수정

개념 빈칸:

- 수정 권한은 작성자 여부보다 PRD 기준 ____ 권한이 핵심이다.

구현 TODO:

- Request DTO: `title`, `content`, `important`
- Service:
  - ADMIN 권한 확인
  - 공지 조회
  - `notice.update(...)`
- Controller: `PUT /api/notices/{noticeId}`

테스트 TODO:

- USER 수정 시도는 HTTP ____.
- 제목 blank 는 HTTP ____.

## FR-NOTICE-005 공지 삭제

개념 빈칸:

- 공지 삭제는 실제 삭제 또는 `deleted` 플래그를 두는 ____ delete 중 선택할 수 있다.

구현 TODO:

- Service:
  - ADMIN 권한 확인
  - 공지 조회
  - delete 또는 soft delete
- Controller: `DELETE /api/notices/{noticeId}`

테스트 TODO:

- 삭제 후 목록에 노출되지 않는다.

## FR-NOTICE-006 중요 공지 표시

개념 빈칸:

- `important` 는 Boolean 이지만 정렬에서는 true 를 먼저 두기 위해 ____ 정렬을 사용한다.

구현 TODO:

- Entity: `Boolean important`
- Repository/Service sort:
  - `Sort.by(Sort.Direction.____, "important").and(Sort.by(Sort.Direction.____, "createdAt"))`
- 화면: 중요 공지는 badge 또는 상단 고정 영역으로 표시할 수 있다.

테스트 TODO:

- important=false 최신글보다 important=true 이전글이 먼저 나오는지 확인한다.

---

# 6. 전자결재 기능

## FR-APPROVAL-001 결재 문서 작성

개념 빈칸:

- 결재 문서는 작성 직후 바로 승인 대기가 아니라 ____ 상태로 시작한다.
- 작성자와 결재자가 같으면 ____ 위반이다.

구현 TODO:

- Request DTO: `title`, `content`, `approverId`
- Service:
  - writer employee 조회
  - approver employee 조회
  - writerId != approverId 검증
  - `ApprovalDocument.create(writerId, approverId, title, content)`
- Controller: `POST /api/approvals`
- Response: `approvalId`, `status`

테스트 TODO:

- 작성 성공 시 status 는 ____.
- 작성자와 결재자가 같으면 HTTP ____.

## FR-APPROVAL-002 결재 요청

개념 빈칸:

- DRAFT 와 PENDING 을 분리하면 ____ 저장 기능을 자연스럽게 표현할 수 있다.
- 결재 요청은 `DRAFT → ____` 전이다.

구현 TODO:

- Service:
  - 작성자 본인인지 확인
  - 현재 상태가 DRAFT 인지 확인
  - 필수 필드가 비어 있지 않은지 확인
  - `document.submit()`
- Controller: `PATCH /api/approvals/{approvalId}/submit`

테스트 TODO:

- DRAFT 문서는 submit 성공.
- 이미 PENDING 문서를 다시 submit 하면 HTTP ____.

## FR-APPROVAL-003 내 결재 목록 조회

개념 빈칸:

- "내 결재 목록"은 내가 ____ 한 문서 목록이다.
- 결재자로서 처리할 목록은 별도 `pending` API 로 분리한다.

구현 TODO:

- Repository: `findBy____(currentEmployeeId, pageable)`
- Service: current employee 를 writerId 로 사용
- Controller: `GET /api/approvals/____`

테스트 TODO:

- A 작성 문서 목록에 B 작성 문서는 보이지 않는다.

## FR-APPROVAL-004 결재 상세 조회

개념 빈칸:

- 상세 조회는 작성자, 결재자 또는 ____ 만 허용한다.
- ADMIN 전체 조회 허용은 Service 에서 role 기반 예외로 ____ 해야 한다.

구현 TODO:

- Service:
  - 문서 조회
  - currentEmployeeId 가 writerId 또는 approverId 인지 확인
  - currentRole 이 ADMIN 이면 전체 조회 허용
  - 아니면 `ErrorCode.____`
- Controller: `GET /api/approvals/{approvalId}`

테스트 TODO:

- 작성자도 결재자도 아니고 ADMIN 도 아닌 사용자 접근은 HTTP ____.

## FR-APPROVAL-005 결재 승인

개념 빈칸:

- 결재 승인은 APPROVER 또는 ____ 권한이 가능하다.
- 승인 시 `approvedAt` 에 ____ 를 저장한다.

구현 TODO:

- Service:
  - 권한 확인
  - 문서 조회
  - currentEmployeeId 가 approverId 와 같은지 확인
  - 상태가 PENDING 인지 확인
  - `document.approve(currentEmployeeId)`
- Controller: `PATCH /api/approvals/{approvalId}/approve`

테스트 TODO:

- 결재자가 아닌 APPROVER 가 승인하면 HTTP ____.
- 승인 성공 시 status 는 APPROVED, approvedAt 은 null 이 ____.

## FR-APPROVAL-006 결재 반려

개념 빈칸:

- 반려는 승인과 같은 권한/상태 검증을 거치고, 추가로 ____ 가 필수다.

구현 TODO:

- Request DTO: `rejectReason`
- Service:
  - 권한 확인
  - 결재자 본인 확인
  - 상태 PENDING 확인
  - `document.reject(currentEmployeeId, rejectReason)`
- Controller: `PATCH /api/approvals/{approvalId}/reject`

테스트 TODO:

- 반려 사유 blank 는 HTTP ____.
- 반려 성공 시 status 는 ____.

## FR-APPROVAL-007 결재 상태 조회

개념 빈칸:

- 결재 상태 enum 은 `DRAFT`, `PENDING`, `APPROVED`, `____`.
- 허용되지 않는 전이는 `ErrorCode.____` 로 막는다.

구현 TODO:

- 목록 필터: `status=PENDING`
- Repository:
  - `findByWriterIdAndStatus(...)`
  - `findByApproverIdAndStatus(...)`
- Response 에 상태와 상태 변경 일시를 포함한다.

테스트 TODO:

- status=APPROVED 필터는 승인된 문서만 반환한다.

---

# 7. 기능별 공통 테스트 체크리스트

각 기능을 구현할 때 아래 체크리스트를 최소 1개 이상 채우세요.

| 분류 | 질문 | 내 답 |
|---|---|---|
| 성공 | 정상 요청의 HTTP status 는? | ____ |
| 검증 | 필수값 누락 시 어떤 ErrorCode 인가? | ____ |
| 권한 | USER/ADMIN/APPROVER 중 누가 가능한가? | ____ |
| 소유자 | 본인 데이터만 봐야 하는가? | ____ |
| 상태 | 허용되는 상태 전이는? | ____ |
| 트랜잭션 | 실패 시 함께 rollback 되어야 하는 데이터는? | ____ |
| DTO | 응답에서 숨겨야 할 필드는? | ____ |
| 동시성 | 중복 승인/중복 등록 같은 race 가 있는가? | ____ |

## 마무리 자가 점검

- 이 기능은 PRD 의 어떤 FR ID 를 만족하는가? ____
- 이 기능의 핵심 비즈니스 규칙은 무엇인가? ____
- 이 기능에서 Controller 가 하면 안 되는 일은 무엇인가? ____
- 이 기능에서 Service 가 반드시 검증해야 하는 것은 무엇인가? ____
- 이 기능의 실패 케이스 테스트 2개는 무엇인가? ____
- 실행 프로젝트의 코드 위치는 어디인가? ____
- 실제로 통과한 검증 명령은 무엇인가? ____
- 이 기능을 설명하는 커밋 또는 PR은 무엇인가? ____
- 면접에서 30초로 설명할 핵심 선택은 무엇인가? ____

위 구현 증거를 같은 증거 ID로 [취업 준비 통합 워크북](./job-preparation-workbook.md)에 옮겨야 해당 기능이 완료됩니다.
