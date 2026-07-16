# 취업 워크북용 사내 업무관리 시스템 PRD / TRD

> 문서 목적: 취업 워크북에서 구현할 **사내 업무관리 시스템(Internal Work Management System)** 의 제품 요구사항(PRD)과 기술 요구사항(TRD)을 정리한다.
>
> 프로그램 유형: Java Spring Boot 취업 포트폴리오 워크북
>
> 구현 대상: Java Spring Boot 기반 사내 업무 시스템
>
> 주요 기능: 직원 관리, 부서 관리, 휴가 신청/승인, 공지사항, 전자결재, 관리자 기능
>
> 작성 기준: 신입/주니어 백엔드·SI 개발자 취업 준비용

---

# 1. 문서 개요

## 1.1 문서명

취업 워크북용 사내 업무관리 시스템 PRD / TRD

## 1.2 프로젝트명

Internal Work Management System

## 1.3 저장소명 예시

```text
company-work-system
internal-work-system
office-management-system
```

## 1.4 문서 작성 목적

이 문서는 취업 워크북의 실습 주제인 사내 업무관리 시스템을 구현하기 전에 필요한 기능 요구사항과 기술 설계 내용을 정리한다.

학습자는 이 문서를 요구사항 기준선으로 사용해 직접 코드를 작성하고, 테스트와 Git 기록으로 구현 근거를 남긴다. 포트폴리오 관점에서는 아래 역량을 보여주는 것을 목표로 한다.

- 업무 시스템 요구사항 분석 능력
- DB 테이블 설계 능력
- Spring Boot 기반 CRUD 구현 능력
- 승인/반려 같은 업무 프로세스 구현 능력
- Controller, Service, Repository 계층 분리 능력
- 관리자/일반 사용자 권한 구분 능력
- API 문서화 및 GitHub README 정리 능력

## 1.5 워크북 프로그램과 구현 대상의 구분

| 구분 | 주 사용자 | 역할 |
|---|---|---|
| 취업 워크북 프로그램 | Java 백엔드 취업 준비생 | 학습 순서, 빈칸 문제, 구현 기준, 정답 해설, 진행 기록, 면접 준비 제공 |
| 사내 업무관리 시스템 | 직원, 관리자, 결재자 | 학습자가 별도 실행 프로젝트에서 구현할 업무 도메인 |

이후 PRD의 사용자와 화면 요구사항은 구현 대상 시스템을 설명한다. 워크북 사용 흐름은 루트 `README.md`와 `practice/job-preparation-workbook.md`를 따른다.

## 1.6 요구사항과 취업 증거 추적

요구사항을 구현했다는 표시는 코드 작성만으로 끝나지 않는다. 채용 공고에서 요구한 역량을 FR ID와 실행 결과에 연결한다.

```text
채용 공고 요구 역량
→ PRD의 FR ID 또는 비기능 요구사항
→ E-{FR ID} 증거 ID
→ 코드
→ 테스트 명령과 결과
→ 커밋·PR
→ 포트폴리오 문서와 면접 답변
```

기능 증거는 `E-FR-LEAVE-005`, 문서·데모는 `E-DOC-README`, 트러블슈팅은 `E-TS-001`, 면접 답변은 `E-INT-001`처럼 기록한다. 세부 인덱스는 `practice/job-preparation-workbook.md`에서 관리한다.

---

# 2. PRD: Product Requirements Document

---

## 2.1 프로젝트 배경

기업 내부에서는 직원 정보, 부서 정보, 휴가 신청, 공지사항, 결재 요청 등을 관리해야 한다.

실제 SI 업무에서는 이런 형태의 사내 업무 시스템, 그룹웨어, 인사관리 시스템, 전자결재 시스템을 자주 개발하거나 유지보수한다.

이 워크북은 신입 개발자가 SI 업무 흐름을 이해하고 실무형 포트폴리오를 만들 수 있도록 사내 업무관리 시스템을 실습 주제로 사용한다.

---

## 2.2 프로젝트 목표

### 2.2.1 기능적 목표

- 직원 정보를 등록, 조회, 수정, 삭제할 수 있다.
- 부서를 등록하고 직원과 연결할 수 있다.
- 직원이 휴가를 신청할 수 있다.
- 관리자는 휴가 신청을 승인 또는 반려할 수 있다.
- 공지사항을 등록하고 조회할 수 있다.
- 직원은 결재 문서를 작성하고 결재 요청을 할 수 있다.
- 관리자는 결재 문서를 승인 또는 반려할 수 있다.
- 사용자 권한에 따라 접근 가능한 기능을 구분한다.

### 2.2.2 포트폴리오 목표

- 단순 게시판보다 실무에 가까운 업무 흐름을 구현한다.
- 직원, 부서, 휴가, 결재, 공지사항의 관계를 DB로 설계한다.
- 승인 상태 변경, 검색, 페이징, 예외처리, 트랜잭션을 보여준다.
- README, ERD, API 명세, 트러블슈팅 문서를 포함한다.

### 2.2.3 워크북 학습 목표

- 각 기능을 FR ID에서 코드와 테스트까지 추적한다.
- 빈칸 풀이 후 같은 기능을 실행 가능한 프로젝트에 직접 구현한다.
- 성공·실패·권한·상태 전이 시나리오를 검증한다.
- 기능별 코드 위치, 검증 명령, 커밋, 설계 이유를 기록한다.
- 구현 경험을 포트폴리오 문서와 30초 면접 답변으로 전환한다.
- 채용 공고 요구 역량과 구현 결과를 공통 증거 ID로 추적한다.

---

## 2.3 구현 대상 시스템 사용자 유형

| 사용자 | 설명 | 주요 기능 |
|---|---|---|
| 일반 직원 | 회사 내부 시스템을 사용하는 직원 | 내 정보 조회, 휴가 신청, 공지 조회, 결재 요청 |
| 관리자 | 인사/총무 담당자 또는 시스템 관리자 | 직원 관리, 부서 관리, 휴가 승인, 공지 등록 |
| 결재자 | 결재 승인 권한이 있는 사용자 | 결재 문서 승인/반려 |
| 시스템 관리자 | 전체 시스템 관리 담당자 | 사용자 권한 관리, 전체 데이터 조회 |

---

## 2.4 핵심 업무 흐름

### 2.4.1 직원 관리 흐름

```text
관리자 로그인
→ 직원 등록
→ 부서 배정
→ 직원 목록 조회
→ 직원 정보 수정
→ 퇴사 또는 비활성 처리
```

### 2.4.2 휴가 신청 흐름

```text
직원 로그인
→ 휴가 신청 작성
→ 관리자 검토
→ 승인 또는 반려
→ 직원이 결과 확인
```

### 2.4.3 전자결재 흐름

```text
직원 결재 문서 작성
→ 결재 요청
→ 결재자 확인
→ 승인 또는 반려
→ 결재 상태 변경
```

### 2.4.4 공지사항 흐름

```text
관리자 공지사항 작성
→ 직원 공지사항 목록 조회
→ 공지사항 상세 조회
```

---

## 2.5 기능 요구사항

### 2.5.1 회원/로그인 기능

| ID | 기능 | 설명 | 우선순위 |
|---|---|---|---|
| FR-USER-001 | 로그인 | 이메일과 비밀번호로 로그인한다 | 필수 |
| FR-USER-002 | 로그아웃 | 로그인 세션을 종료한다 | 필수 |
| FR-USER-003 | 내 정보 조회 | 로그인한 사용자의 정보를 조회한다 | 필수 |
| FR-USER-004 | 비밀번호 변경 | 사용자가 본인 비밀번호를 변경한다 | 선택 |
| FR-USER-005 | 권한 구분 | USER, ADMIN, APPROVER 권한을 구분한다 | 필수 |

### 2.5.2 직원 관리 기능

| ID | 기능 | 설명 | 우선순위 |
|---|---|---|---|
| FR-EMP-001 | 직원 등록 | 관리자가 직원을 등록한다 | 필수 |
| FR-EMP-002 | 직원 목록 조회 | 직원 목록을 조회한다 | 필수 |
| FR-EMP-003 | 직원 상세 조회 | 특정 직원의 상세 정보를 조회한다 | 필수 |
| FR-EMP-004 | 직원 정보 수정 | 직원 이름, 연락처, 부서 등을 수정한다 | 필수 |
| FR-EMP-005 | 직원 삭제/비활성화 | 직원을 삭제하거나 비활성 처리한다 | 선택 |
| FR-EMP-006 | 직원 검색 | 이름, 이메일, 부서명 기준으로 검색한다 | 필수 |
| FR-EMP-007 | 페이징 | 직원 목록을 페이지 단위로 조회한다 | 필수 |

### 2.5.3 부서 관리 기능

| ID | 기능 | 설명 | 우선순위 |
|---|---|---|---|
| FR-DEPT-001 | 부서 등록 | 관리자가 부서를 등록한다 | 필수 |
| FR-DEPT-002 | 부서 목록 조회 | 전체 부서 목록을 조회한다 | 필수 |
| FR-DEPT-003 | 부서 상세 조회 | 부서 정보와 소속 직원을 조회한다 | 필수 |
| FR-DEPT-004 | 부서 정보 수정 | 부서명, 설명 등을 수정한다 | 필수 |
| FR-DEPT-005 | 부서 삭제 | 사용하지 않는 부서를 삭제한다 | 선택 |

### 2.5.4 휴가 신청 기능

| ID | 기능 | 설명 | 우선순위 |
|---|---|---|---|
| FR-LEAVE-001 | 휴가 신청 | 직원이 휴가를 신청한다 | 필수 |
| FR-LEAVE-002 | 내 휴가 목록 조회 | 직원이 본인의 휴가 신청 목록을 조회한다 | 필수 |
| FR-LEAVE-003 | 휴가 상세 조회 | 휴가 신청 상세 내용을 조회한다 | 필수 |
| FR-LEAVE-004 | 휴가 신청 취소 | 대기 상태의 휴가를 취소한다 | 선택 |
| FR-LEAVE-005 | 휴가 승인 | 관리자가 휴가를 승인한다 | 필수 |
| FR-LEAVE-006 | 휴가 반려 | 관리자가 휴가를 반려한다 | 필수 |
| FR-LEAVE-007 | 휴가 상태 조회 | PENDING, APPROVED, REJECTED 상태를 조회한다 | 필수 |
| FR-LEAVE-008 | 관리자 휴가 목록 조회 | 관리자가 전체 휴가 신청을 상태/직원 조건으로 조회한다 | 필수 |

### 2.5.5 공지사항 기능

| ID | 기능 | 설명 | 우선순위 |
|---|---|---|---|
| FR-NOTICE-001 | 공지 등록 | 관리자가 공지사항을 등록한다 | 필수 |
| FR-NOTICE-002 | 공지 목록 조회 | 사용자가 공지사항 목록을 조회한다 | 필수 |
| FR-NOTICE-003 | 공지 상세 조회 | 공지사항 상세 내용을 조회한다 | 필수 |
| FR-NOTICE-004 | 공지 수정 | 관리자가 공지 내용을 수정한다 | 필수 |
| FR-NOTICE-005 | 공지 삭제 | 관리자가 공지를 삭제한다 | 선택 |
| FR-NOTICE-006 | 중요 공지 표시 | 중요 공지를 상단에 표시한다 | 선택 |

### 2.5.6 전자결재 기능

| ID | 기능 | 설명 | 우선순위 |
|---|---|---|---|
| FR-APPROVAL-001 | 결재 문서 작성 | 직원이 결재 문서를 작성한다 | 필수 |
| FR-APPROVAL-002 | 결재 요청 | 작성한 문서를 결재 요청한다 | 필수 |
| FR-APPROVAL-003 | 내 결재 목록 조회 | 내가 작성한 결재 목록을 조회한다 | 필수 |
| FR-APPROVAL-004 | 결재 상세 조회 | 결재 문서 상세를 조회한다 | 필수 |
| FR-APPROVAL-005 | 결재 승인 | 결재자가 문서를 승인한다 | 필수 |
| FR-APPROVAL-006 | 결재 반려 | 결재자가 문서를 반려한다 | 필수 |
| FR-APPROVAL-007 | 결재 상태 조회 | DRAFT, PENDING, APPROVED, REJECTED 상태를 조회한다 | 필수 |

---

## 2.6 비기능 요구사항

| 구분 | 요구사항 |
|---|---|
| 성능 | 목록 조회는 페이징을 적용한다 |
| 보안 | 비밀번호는 평문 저장하지 않는다 |
| 권한 | 일반 사용자와 관리자의 접근 권한을 구분한다 |
| 유지보수 | Controller, Service, Repository 계층을 분리한다 |
| 검증 | 필수 입력값과 잘못된 요청값을 검증한다 |
| 예외처리 | 공통 예외 응답 형식을 사용한다 |
| 데이터 정합성 | 승인/반려 상태 변경 시 트랜잭션을 적용한다 |
| 문서화 | README, ERD, API 명세를 작성한다 |

---

## 2.7 화면 요구사항

### 2.7.1 공통 화면

| 화면 | 설명 |
|---|---|
| 로그인 화면 | 이메일, 비밀번호 입력 |
| 메인 대시보드 | 직원 수, 휴가 신청 수, 공지사항 요약 |
| 내 정보 화면 | 로그인 사용자 정보 확인 |

### 2.7.2 직원 관리 화면

| 화면 | 설명 |
|---|---|
| 직원 목록 화면 | 직원 목록, 검색, 페이징 |
| 직원 등록 화면 | 신규 직원 등록 |
| 직원 상세 화면 | 직원 상세 정보 확인 |
| 직원 수정 화면 | 직원 정보 수정 |

### 2.7.3 부서 관리 화면

| 화면 | 설명 |
|---|---|
| 부서 목록 화면 | 전체 부서 목록 조회 |
| 부서 등록 화면 | 신규 부서 등록 |
| 부서 상세 화면 | 부서 정보와 소속 직원 조회 |

### 2.7.4 휴가 관리 화면

| 화면 | 설명 |
|---|---|
| 휴가 신청 화면 | 휴가 종류, 기간, 사유 입력 |
| 내 휴가 목록 화면 | 본인 휴가 신청 내역 |
| 휴가 승인 관리 화면 | 관리자가 휴가 승인/반려 처리 |

### 2.7.5 공지사항 화면

| 화면 | 설명 |
|---|---|
| 공지 목록 화면 | 공지사항 목록 조회 |
| 공지 상세 화면 | 공지사항 상세 조회 |
| 공지 등록 화면 | 관리자가 공지 등록 |

### 2.7.6 전자결재 화면

| 화면 | 설명 |
|---|---|
| 결재 작성 화면 | 결재 제목, 내용, 결재자 입력 |
| 내 결재 목록 화면 | 내가 작성한 결재 문서 목록 |
| 결재 승인 화면 | 결재자가 승인/반려 처리 |

---

## 2.8 MVP 범위

### 2.8.1 1차 MVP 필수 기능

```text
1. 로그인
2. 직원 등록/조회/수정
3. 부서 등록/조회
4. 휴가 신청
5. 휴가 승인/반려
6. 공지사항 CRUD
7. 관리자/일반 사용자 권한 구분
```

### 2.8.2 2차 확장 기능

```text
1. 전자결재
2. 검색 조건 고도화
3. 첨부파일
4. 댓글
5. 통계 대시보드
6. Docker 배포
7. Spring Security / JWT 적용
```

---

# 3. TRD: Technical Requirements Document

---

## 3.1 기술 스택

### 3.1.1 기본 추천 스택

| 구분 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| View | Thymeleaf |
| Database | H2, MySQL |
| ORM | Spring Data JPA |
| Security | 1차 `spring-security-crypto` + 세션, 2차 Spring Security, 3차 JWT |
| Build | Gradle |
| Test | JUnit 5 |
| Version Control | Git / GitHub |
| API Test | Postman, curl |
| Deploy | Render, Railway, AWS, Docker 중 선택 |

### 3.1.2 SI 친화 스택

| 구분 | 기술 |
|---|---|
| Language | Java |
| Framework | Spring Boot, Spring MVC |
| View | JSP 또는 Thymeleaf |
| Database | Oracle 또는 MySQL |
| Persistence | MyBatis 또는 JPA |
| Build | Maven 또는 Gradle |
| Server | Tomcat |
| OS | Linux |

포트폴리오 초보자에게는 **Spring Boot + Thymeleaf + JPA + H2/MySQL** 조합을 추천한다.

---

## 3.2 시스템 아키텍처

### 3.2.1 계층 구조

```text
Browser
  ↓
Controller
  ↓
Service
  ↓
Repository
  ↓
Database
```

### 3.2.2 패키지 구조

```text
company-work-system
└── src
    └── main
        ├── java
        │   └── com.example.companywork
        │       ├── controller
        │       ├── service
        │       ├── repository
        │       ├── domain
        │       ├── dto
        │       ├── config
        │       └── exception
        └── resources
            ├── templates
            ├── static
            └── application.yml
```

---

## 3.3 주요 도메인 설계

### 3.3.1 User

시스템 로그인 계정 정보를 관리한다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 사용자 PK |
| email | String | 로그인 이메일 |
| password | String | 비밀번호 |
| name | String | 사용자 이름 |
| role | UserRole | USER, ADMIN, APPROVER |
| createdAt | LocalDateTime | 생성일 |
| updatedAt | LocalDateTime | 수정일 |

### 3.3.2 Employee

직원 인사 정보를 관리한다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 직원 PK |
| userId | Long | 사용자 FK |
| departmentId | Long | 부서 FK |
| employeeNumber | String | 사번 |
| position | String | 직급 |
| phone | String | 연락처 |
| hireDate | LocalDate | 입사일 |
| status | EmployeeStatus | ACTIVE, INACTIVE, RESIGNED |

### 3.3.3 Department

부서 정보를 관리한다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 부서 PK |
| name | String | 부서명 |
| description | String | 부서 설명 |
| createdAt | LocalDateTime | 생성일 |
| updatedAt | LocalDateTime | 수정일 |

### 3.3.4 LeaveRequest

휴가 신청 정보를 관리한다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 휴가 신청 PK |
| employeeId | Long | 직원 FK |
| leaveType | LeaveType | ANNUAL, HALF_DAY, SICK, OFFICIAL |
| startDate | LocalDate | 휴가 시작일 |
| endDate | LocalDate | 휴가 종료일 |
| reason | String | 휴가 사유 |
| status | ApprovalStatus | PENDING, APPROVED, REJECTED |
| approverId | Long | 승인자 FK |
| rejectReason | String | 반려 사유 |
| createdAt | LocalDateTime | 신청일 |

### 3.3.5 Notice

공지사항 정보를 관리한다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 공지사항 PK |
| title | String | 제목 |
| content | Text | 내용 |
| writerId | Long | 작성자 FK |
| important | Boolean | 중요 공지 여부 |
| viewCount | Long | 조회수 |
| createdAt | LocalDateTime | 작성일 |
| updatedAt | LocalDateTime | 수정일 |

### 3.3.6 ApprovalDocument

전자결재 문서를 관리한다.

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Long | 결재 문서 PK |
| writerId | Long | 작성자 FK |
| approverId | Long | 결재자 FK |
| title | String | 제목 |
| content | Text | 내용 |
| status | ApprovalStatus | DRAFT, PENDING, APPROVED, REJECTED |
| rejectReason | String | 반려 사유 |
| createdAt | LocalDateTime | 작성일 |
| approvedAt | LocalDateTime | 승인일 |

---

## 3.4 ERD 초안

```text
User 1 : 1 Employee
Department 1 : N Employee
Employee 1 : N LeaveRequest
Employee 1 : N ApprovalDocument(writer)
Employee 1 : N ApprovalDocument(approver)
User 1 : N Notice
```

### 3.4.1 테이블 관계 설명

- User는 로그인 계정이다.
- Employee는 실제 직원 인사 정보다.
- Department는 여러 명의 Employee를 가질 수 있다.
- Employee는 여러 개의 휴가 신청을 할 수 있다.
- Employee는 여러 개의 결재 문서를 작성할 수 있다.
- Employee는 결재자로서 여러 문서를 승인할 수 있다.
- Notice는 User가 작성한다.

---

## 3.5 DB 테이블 설계

### 3.5.1 users

| 컬럼 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 사용자 ID |
| email | VARCHAR(100) | UNIQUE, NOT NULL | 이메일 |
| password | VARCHAR(255) | NOT NULL | 비밀번호 |
| name | VARCHAR(50) | NOT NULL | 이름 |
| role | VARCHAR(20) | NOT NULL | 권한 |
| created_at | DATETIME | NOT NULL | 생성일 |
| updated_at | DATETIME | NULL | 수정일 |

### 3.5.2 departments

| 컬럼 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 부서 ID |
| name | VARCHAR(100) | UNIQUE, NOT NULL | 부서명 |
| description | VARCHAR(255) | NULL | 설명 |
| created_at | DATETIME | NOT NULL | 생성일 |
| updated_at | DATETIME | NULL | 수정일 |

### 3.5.3 employees

| 컬럼 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 직원 ID |
| user_id | BIGINT | FK, NOT NULL | 사용자 ID |
| department_id | BIGINT | FK, NOT NULL | 부서 ID |
| employee_number | VARCHAR(30) | UNIQUE, NOT NULL | 사번 |
| position | VARCHAR(50) | NULL | 직급 |
| phone | VARCHAR(30) | NULL | 연락처 |
| hire_date | DATE | NULL | 입사일 |
| status | VARCHAR(20) | NOT NULL | 재직 상태 |

### 3.5.4 leave_requests

| 컬럼 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 휴가 신청 ID |
| employee_id | BIGINT | FK, NOT NULL | 신청 직원 |
| leave_type | VARCHAR(30) | NOT NULL | 휴가 종류 |
| start_date | DATE | NOT NULL | 시작일 |
| end_date | DATE | NOT NULL | 종료일 |
| reason | VARCHAR(500) | NULL | 사유 |
| status | VARCHAR(30) | NOT NULL | 상태 |
| approver_id | BIGINT | FK | 승인자 |
| reject_reason | VARCHAR(500) | NULL | 반려 사유 |
| created_at | DATETIME | NOT NULL | 신청일 |
| updated_at | DATETIME | NULL | 수정일 |

### 3.5.5 notices

| 컬럼 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 공지 ID |
| title | VARCHAR(200) | NOT NULL | 제목 |
| content | TEXT | NOT NULL | 내용 |
| writer_id | BIGINT | FK, NOT NULL | 작성자 |
| important | BOOLEAN | NOT NULL | 중요 여부 |
| view_count | BIGINT | NOT NULL | 조회수 |
| created_at | DATETIME | NOT NULL | 작성일 |
| updated_at | DATETIME | NULL | 수정일 |

### 3.5.6 approval_documents

| 컬럼 | 타입 | 제약조건 | 설명 |
|---|---|---|---|
| id | BIGINT | PK | 결재 문서 ID |
| writer_id | BIGINT | FK, NOT NULL | 작성자 |
| approver_id | BIGINT | FK, NOT NULL | 결재자 |
| title | VARCHAR(200) | NOT NULL | 제목 |
| content | TEXT | NOT NULL | 내용 |
| status | VARCHAR(30) | NOT NULL | 상태 |
| reject_reason | VARCHAR(500) | NULL | 반려 사유 |
| created_at | DATETIME | NOT NULL | 작성일 |
| updated_at | DATETIME | NULL | 수정일 |
| approved_at | DATETIME | NULL | 승인일 |

---

## 3.6 Enum 설계

### 3.6.1 UserRole

```java
public enum UserRole {
    USER,
    ADMIN,
    APPROVER
}
```

### 3.6.2 EmployeeStatus

```java
public enum EmployeeStatus {
    ACTIVE,
    INACTIVE,
    RESIGNED
}
```

### 3.6.3 LeaveType

```java
public enum LeaveType {
    ANNUAL,
    HALF_DAY,
    SICK,
    OFFICIAL
}
```

### 3.6.4 ApprovalStatus

```java
public enum ApprovalStatus {
    DRAFT,
    PENDING,
    APPROVED,
    REJECTED
}
```

---

## 3.7 API 설계

### 3.7.1 사용자 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | /api/auth/login | 로그인 | 전체 |
| POST | /api/auth/logout | 로그아웃 | 로그인 사용자 |
| GET | /api/users/me | 내 정보 조회 | 로그인 사용자 |
| PATCH | /api/users/me/password | 비밀번호 변경 | 로그인 사용자 |

### 3.7.2 직원 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | /api/employees | 직원 등록 | ADMIN |
| GET | /api/employees | 직원 목록 조회 | ADMIN |
| GET | /api/employees/{employeeId} | 직원 상세 조회 | ADMIN |
| PUT | /api/employees/{employeeId} | 직원 수정 | ADMIN |
| DELETE | /api/employees/{employeeId} | 직원 삭제/비활성 | ADMIN |

#### 직원 등록 Request 예시

```json
{
  "email": "user@test.com",
  "password": "1234",
  "name": "홍길동",
  "departmentId": 1,
  "employeeNumber": "EMP-001",
  "position": "사원",
  "phone": "010-1234-5678",
  "hireDate": "2026-01-01"
}
```

#### 직원 등록 Response 예시

```json
{
  "employeeId": 1,
  "name": "홍길동",
  "email": "user@test.com",
  "departmentName": "개발팀",
  "employeeNumber": "EMP-001",
  "status": "ACTIVE"
}
```

### 3.7.3 부서 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | /api/departments | 부서 등록 | ADMIN |
| GET | /api/departments | 부서 목록 조회 | 로그인 사용자 |
| GET | /api/departments/{departmentId} | 부서 상세 조회 | 로그인 사용자 |
| PUT | /api/departments/{departmentId} | 부서 수정 | ADMIN |
| DELETE | /api/departments/{departmentId} | 부서 삭제 | ADMIN |

### 3.7.4 휴가 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | /api/leaves | 휴가 신청 | 로그인 사용자 |
| GET | /api/leaves/my | 내 휴가 목록 | 로그인 사용자 |
| GET | /api/leaves/{leaveId} | 휴가 상세 조회 | 본인 또는 ADMIN |
| PATCH | /api/leaves/{leaveId}/cancel | 휴가 신청 취소 | 본인 |
| GET | /api/admin/leaves | 관리자 휴가 목록 | ADMIN |
| PATCH | /api/admin/leaves/{leaveId}/approve | 휴가 승인 | ADMIN |
| PATCH | /api/admin/leaves/{leaveId}/reject | 휴가 반려 | ADMIN |

#### 휴가 신청 Request 예시

```json
{
  "leaveType": "ANNUAL",
  "startDate": "2026-06-01",
  "endDate": "2026-06-03",
  "reason": "개인 사유"
}
```

#### 휴가 승인 Response 예시

```json
{
  "leaveId": 1,
  "status": "APPROVED",
  "message": "휴가 신청이 승인되었습니다."
}
```

### 3.7.5 공지사항 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | /api/notices | 공지 등록 | ADMIN |
| GET | /api/notices | 공지 목록 조회 | 로그인 사용자 |
| GET | /api/notices/{noticeId} | 공지 상세 조회 | 로그인 사용자 |
| PUT | /api/notices/{noticeId} | 공지 수정 | ADMIN |
| DELETE | /api/notices/{noticeId} | 공지 삭제 | ADMIN |

### 3.7.6 전자결재 API

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | /api/approvals | 결재 문서 작성 | 로그인 사용자 |
| PATCH | /api/approvals/{approvalId}/submit | 결재 요청 | 작성자 |
| GET | /api/approvals/my | 내 결재 문서 목록 | 로그인 사용자 |
| GET | /api/approvals/pending | 승인 대기 문서 목록 | APPROVER, ADMIN |
| GET | /api/approvals/{approvalId} | 결재 상세 조회 | 작성자, 결재자, ADMIN |
| PATCH | /api/approvals/{approvalId}/approve | 결재 승인 | APPROVER, ADMIN |
| PATCH | /api/approvals/{approvalId}/reject | 결재 반려 | APPROVER, ADMIN |

---

## 3.8 비즈니스 규칙

### 3.8.1 직원 관리 규칙

- 이메일은 중복될 수 없다.
- 사번은 중복될 수 없다.
- 직원은 반드시 하나의 부서에 속해야 한다.
- 퇴사 상태 직원은 휴가 신청과 결재 요청을 할 수 없다.

### 3.8.2 휴가 신청 규칙

- 휴가 시작일은 종료일보다 늦을 수 없다.
- 이미 승인된 휴가는 일반 사용자가 취소할 수 없다.
- PENDING 상태의 휴가만 승인 또는 반려할 수 있다.
- 승인된 휴가는 다시 반려할 수 없다.
- 반려 시 반려 사유를 입력해야 한다.

### 3.8.3 공지사항 규칙

- 공지 제목과 내용은 필수다.
- 공지는 관리자만 등록, 수정, 삭제할 수 있다.
- 일반 사용자는 공지 목록과 상세만 조회할 수 있다.

### 3.8.4 전자결재 규칙

- DRAFT 상태 문서만 결재 요청할 수 있다.
- PENDING 상태 문서만 승인 또는 반려할 수 있다.
- 작성자와 결재자가 같을 수 없도록 제한한다.
- 반려 시 반려 사유를 입력해야 한다.

---

## 3.9 예외 처리 설계

### 3.9.1 공통 에러 응답 형식

```json
{
  "status": 400,
  "code": "INVALID_REQUEST",
  "message": "잘못된 요청입니다.",
  "timestamp": "2026-05-24T20:00:00"
}
```

### 3.9.2 주요 예외 목록

| 예외 코드 | HTTP Status | 설명 |
|---|---|---|
| INVALID_INPUT | 400 | 입력값 검증 실패 |
| AUTHENTICATION_REQUIRED | 401 | 로그인이 필요함 |
| USER_NOT_FOUND | 404 | 사용자를 찾을 수 없음 |
| EMPLOYEE_NOT_FOUND | 404 | 직원을 찾을 수 없음 |
| DEPARTMENT_NOT_FOUND | 404 | 부서를 찾을 수 없음 |
| LEAVE_NOT_FOUND | 404 | 휴가 신청을 찾을 수 없음 |
| APPROVAL_NOT_FOUND | 404 | 결재 문서를 찾을 수 없음 |
| NOTICE_NOT_FOUND | 404 | 공지사항을 찾을 수 없음 |
| DUPLICATE_EMAIL | 400 | 이메일 중복 |
| DUPLICATE_EMPLOYEE_NUMBER | 400 | 사번 중복 |
| DUPLICATE_DEPARTMENT_NAME | 400 | 부서명 중복 |
| DEPARTMENT_HAS_EMPLOYEES | 400 | 소속 직원이 있는 부서 삭제 시도 |
| INVALID_DATE_RANGE | 400 | 잘못된 날짜 범위 |
| INVALID_STATUS | 400 | 처리할 수 없는 상태 |
| ACCESS_DENIED | 403 | 접근 권한 없음 |
| INTERNAL_ERROR | 500 | 서버 내부 오류 |

---

## 3.10 트랜잭션 설계

### 3.10.1 직원 등록

직원 등록 시 User와 Employee가 함께 생성되어야 한다.

```text
User 생성
→ Employee 생성
→ 둘 중 하나라도 실패하면 rollback
```

Service 메서드에 `@Transactional`을 적용한다.

### 3.10.2 휴가 승인

휴가 승인 상태 변경은 하나의 트랜잭션으로 처리한다.

```text
휴가 신청 조회
→ 상태 검증
→ 승인자 설정
→ 상태 APPROVED 변경
→ 저장
```

### 3.10.3 결재 승인

결재 승인 상태 변경도 하나의 트랜잭션으로 처리한다.

```text
결재 문서 조회
→ 상태 검증
→ 승인자 검증
→ 상태 APPROVED 변경
→ 승인일시 저장
```

---

## 3.11 보안 설계

### 3.11.1 1차 구현

포트폴리오 초기 버전에서는 BCrypt 비밀번호 검증과 `HttpSession`을 사용하는 세션 로그인으로 구현한다. Service는 자격 증명을 검증하고, Controller는 HTTP 세션의 생성·회전·폐기를 담당한다.

```text
AuthService에서 이메일/비밀번호 검증 성공
→ AuthController에서 세션 ID 재발급
→ Session에 userId와 role 저장
→ 요청 시 Session에서 사용자 확인
```

### 3.11.2 확장 구현

나중에 Spring Security 또는 JWT를 적용한다.

```text
Spring Security
BCryptPasswordEncoder
Role 기반 접근 제어
JWT Access Token
```

### 3.11.3 권한 정책

| 기능 | USER | ADMIN | APPROVER |
|---|---|---|---|
| 내 정보 조회 | 가능 | 가능 | 가능 |
| 직원 관리 | 불가 | 가능 | 불가 |
| 부서 관리 | 불가 | 가능 | 불가 |
| 휴가 신청 | 가능 | 가능 | 가능 |
| 휴가 승인 | 불가 | 가능 | 불가 |
| 공지 조회 | 가능 | 가능 | 가능 |
| 공지 등록 | 불가 | 가능 | 불가 |
| 결재 작성 | 가능 | 가능 | 가능 |
| 결재 승인 | 불가 | 가능 | 가능 |

---

## 3.12 테스트 계획

### 3.12.1 단위 테스트

| 테스트 대상 | 테스트 내용 |
|---|---|
| EmployeeService | 직원 등록, 중복 이메일 검증 |
| DepartmentService | 부서 등록, 중복 부서명 검증 |
| LeaveService | 휴가 신청, 날짜 검증, 승인/반려 |
| NoticeService | 공지 등록, 수정, 삭제 |
| ApprovalService | 결재 작성, 요청, 승인/반려 |

### 3.12.2 통합 테스트

| 테스트 | 설명 |
|---|---|
| 직원 등록 API 테스트 | 직원 등록 요청부터 DB 저장까지 확인 |
| 휴가 신청 API 테스트 | 휴가 신청 요청과 응답 확인 |
| 휴가 승인 API 테스트 | 승인 후 상태 변경 확인 |
| 공지사항 API 테스트 | 공지 CRUD 확인 |
| 세션 인증 흐름 테스트 | 로그인 세션을 재사용해 보호 API 호출 확인 |
| 권한 테스트 | 일반 사용자가 관리자 API 접근 시 실패 확인 |

### 3.12.3 수동 테스트 시나리오

```text
1. 관리자 계정으로 로그인한다.
2. 개발팀 부서를 등록한다.
3. 신규 직원을 등록한다.
4. 직원 계정으로 로그인한다.
5. 휴가 신청을 등록한다.
6. 관리자 계정으로 휴가 신청을 승인한다.
7. 직원 계정으로 승인 결과를 확인한다.
8. 관리자가 공지사항을 등록한다.
9. 직원이 공지사항을 조회한다.
10. 직원이 결재 문서를 작성하고 결재 요청한다.
11. 결재자가 승인 또는 반려한다.
```

---

## 3.13 개발 단계

### 3.13.1 1단계: 기본 프로젝트 생성

```text
Spring Boot 프로젝트 생성
GitHub 저장소 연결
README 초안 작성
패키지 구조 생성
```

### 3.13.2 2단계: 기본 도메인 구현

```text
User
Employee
Department
```

### 3.13.3 3단계: 직원/부서 CRUD

```text
직원 등록
직원 목록 조회
직원 상세 조회
직원 수정
직원 검색/페이징
직원 비활성/퇴사 처리
부서 등록
부서 목록 조회
부서 상세 조회
부서 수정
부서 삭제 정책
```

### 3.13.4 4단계: 휴가 신청/승인

```text
휴가 신청
내 휴가 목록 조회
휴가 상세 조회
휴가 신청 취소
관리자 휴가 목록 조회
휴가 승인
휴가 반려
휴가 상태 조회
```

### 3.13.5 5단계: 공지사항

```text
공지 등록
공지 목록 조회
공지 상세 조회
공지 수정
공지 삭제
```

### 3.13.6 6단계: 전자결재

```text
결재 문서 작성
결재 요청
내 결재 목록
승인 대기 목록
결재 상세 조회
결재 승인
결재 반려
결재 상태 조회
```

### 3.13.7 7단계: 문서화

```text
README.md
docs/PRD.md
docs/TRD.md
docs/ERD.md
docs/API_SPEC.md
docs/TROUBLESHOOTING.md
```

---

## 3.14 Codex 개발 프롬프트 예시

### 3.14.1 프로젝트 구조 생성

```text
Spring Boot 기반 사내 업무관리 시스템을 만들려고 합니다.

패키지 구조를 아래와 같이 생성해주세요.

com.example.companywork
- controller
- service
- repository
- domain
- dto
- exception
- config

아직 기능 구현은 하지 말고 기본 README.md와 docs 폴더 구조도 함께 만들어주세요.
```

### 3.14.2 직원/부서 도메인 생성

```text
사내 업무관리 시스템의 직원과 부서 도메인을 구현해주세요.

요구사항:
- User 엔티티
- Employee 엔티티
- Department 엔티티
- UserRole enum
- EmployeeStatus enum
- Repository 생성
- 기본 생성자, 연관관계, createdAt, updatedAt 포함

직원은 하나의 부서에 속하고, 부서는 여러 직원을 가질 수 있도록 설계해주세요.
```

### 3.14.3 휴가 신청 기능 구현

```text
휴가 신청 기능을 구현해주세요.

요구사항:
- LeaveRequest 엔티티
- LeaveType enum
- ApprovalStatus enum
- 휴가 신청 API: POST /api/leaves
- 내 휴가 목록 API: GET /api/leaves/my
- 휴가 상세 API: GET /api/leaves/{leaveId}
- 휴가 신청 취소 API: PATCH /api/leaves/{leaveId}/cancel
- 관리자 휴가 목록 API: GET /api/admin/leaves
- 휴가 승인 API: PATCH /api/admin/leaves/{leaveId}/approve
- 휴가 반려 API: PATCH /api/admin/leaves/{leaveId}/reject

휴가 시작일은 종료일보다 늦을 수 없고, PENDING 상태만 승인/반려 가능하도록 검증해주세요.
```

### 3.14.4 공지사항 기능 구현

```text
공지사항 기능을 구현해주세요.

요구사항:
- Notice 엔티티
- 공지 등록 API: POST /api/notices
- 공지 목록 API: GET /api/notices
- 공지 상세 API: GET /api/notices/{noticeId}
- 공지 수정 API: PUT /api/notices/{noticeId}
- 공지 삭제 API: DELETE /api/notices/{noticeId}

목록 조회는 페이징을 적용하고, important=true인 공지는 상단 노출이 가능하도록 정렬 기준을 고려해주세요.
```

### 3.14.5 전자결재 기능 구현

```text
전자결재 기능을 구현해주세요.

요구사항:
- ApprovalDocument 엔티티
- 결재 문서 작성 API: POST /api/approvals
- 결재 요청 API: PATCH /api/approvals/{approvalId}/submit
- 내 결재 목록 API: GET /api/approvals/my
- 승인 대기 목록 API: GET /api/approvals/pending
- 결재 상세 API: GET /api/approvals/{approvalId}
- 결재 승인 API: PATCH /api/approvals/{approvalId}/approve
- 결재 반려 API: PATCH /api/approvals/{approvalId}/reject

DRAFT 상태만 결재 요청 가능하고, PENDING 상태만 승인/반려 가능하도록 구현해주세요.
작성자와 결재자가 동일하면 예외가 발생하도록 해주세요.
```

---

## 3.15 GitHub README 구성

아래 내용은 워크북 저장소의 README가 아니라 학습자가 완성한 실행 프로젝트에 사용할 포트폴리오 README 예시다.

```md
# Company Work Management System

Java Spring Boot 기반 사내 업무관리 시스템입니다.
직원 관리, 부서 관리, 휴가 신청/승인, 공지사항, 전자결재 기능을 구현했습니다.

## 1. 프로젝트 소개

SI 업무 시스템에서 자주 등장하는 사내 관리 기능을 구현한 포트폴리오 프로젝트입니다.

## 2. 개발 목적

- Spring Boot 기반 업무 시스템 개발 학습
- 직원/부서/휴가/결재 도메인 설계
- CRUD, 검색, 페이징 구현
- 승인/반려 업무 프로세스 구현
- 관리자/사용자 권한 구분
- SI 취업용 포트폴리오 완성

## 3. 사용 기술

| 구분 | 기술 |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| DB | H2 / MySQL |
| ORM | Spring Data JPA |
| View | Thymeleaf |
| Build | Gradle |
| Test | JUnit 5 |
| Version Control | Git / GitHub |

## 4. 주요 기능

- 로그인
- 직원 관리
- 부서 관리
- 휴가 신청/승인
- 공지사항 관리
- 전자결재
- 관리자 기능

## 5. 시스템 구조

Controller → Service → Repository → Database

## 6. ERD

User 1 : 1 Employee
Department 1 : N Employee
Employee 1 : N LeaveRequest
Employee 1 : N ApprovalDocument
User 1 : N Notice

## 7. API 명세

자세한 API 명세는 `docs/API_SPEC.md`를 참고하세요.

## 8. 실행 방법

```bash
git clone https://github.com/사용자명/company-work-system.git
cd company-work-system
./gradlew bootRun
```

## 9. 트러블슈팅

자세한 내용은 `docs/TROUBLESHOOTING.md`를 참고하세요.

## 10. 배운 점

- 업무 시스템의 기본 구조
- 승인/반려 프로세스 설계
- Entity와 DTO 분리
- 계층형 아키텍처 적용
- 트랜잭션과 예외 처리의 중요성
```

---

## 3.16 트러블슈팅 예시

### 3.16.1 휴가 승인 상태 중복 처리 문제

#### 문제

이미 승인된 휴가 신청을 다시 승인하거나 반려할 수 있는 문제가 있었다.

#### 원인

휴가 승인/반려 처리 전에 현재 상태가 PENDING인지 검증하지 않았다.

#### 해결

Service 계층에서 상태 검증 로직을 추가했다.

```java
if (leaveRequest.getStatus() != ApprovalStatus.PENDING) {
    throw new BusinessException("대기 상태의 휴가 신청만 처리할 수 있습니다.");
}
```

#### 배운 점

승인/반려 같은 업무 프로세스에서는 현재 상태를 반드시 검증해야 한다.

### 3.16.2 직원 등록 중 일부 데이터만 저장되는 문제

#### 문제

직원 등록 시 User는 생성되었지만 Employee 저장 중 오류가 발생하면 User 데이터만 남을 수 있었다.

#### 원인

User 생성과 Employee 생성이 하나의 트랜잭션으로 묶여 있지 않았다.

#### 해결

직원 등록 Service 메서드에 `@Transactional`을 적용했다.

```java
@Transactional
public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
    // User 생성
    // Employee 생성
}
```

#### 배운 점

서로 연관된 데이터를 함께 저장할 때는 트랜잭션 처리가 필요하다.

### 3.16.3 Entity 직접 반환 문제

#### 문제

Controller에서 Entity를 그대로 반환하면 비밀번호나 내부 관리 필드가 노출될 위험이 있었다.

#### 해결

응답 DTO를 별도로 만들어 필요한 필드만 반환했다.

#### 배운 점

Entity와 DTO를 분리하면 보안과 유지보수 측면에서 유리하다.

---

## 3.17 개발 체크리스트

### 3.17.1 기능 체크리스트

- [ ] 로그인
- [ ] 로그아웃
- [ ] 내 정보 조회
- [ ] 비밀번호 변경
- [ ] 권한 구분
- [ ] 직원 등록
- [ ] 직원 목록 조회
- [ ] 직원 상세 조회
- [ ] 직원 수정
- [ ] 직원 삭제/비활성화
- [ ] 직원 검색
- [ ] 직원 페이징
- [ ] 부서 등록
- [ ] 부서 목록 조회
- [ ] 부서 상세 조회
- [ ] 부서 수정
- [ ] 부서 삭제
- [ ] 휴가 신청
- [ ] 내 휴가 목록 조회
- [ ] 휴가 상세 조회
- [ ] 휴가 신청 취소
- [ ] 관리자 휴가 목록 조회
- [ ] 휴가 승인
- [ ] 휴가 반려
- [ ] 휴가 상태 조회
- [ ] 공지사항 등록
- [ ] 공지사항 목록 조회
- [ ] 공지사항 상세 조회
- [ ] 공지사항 수정
- [ ] 공지사항 삭제
- [ ] 중요 공지 표시
- [ ] 결재 문서 작성
- [ ] 결재 요청
- [ ] 내 결재 목록 조회
- [ ] 결재 상세 조회
- [ ] 결재 승인
- [ ] 결재 반려
- [ ] 결재 상태 조회

### 3.17.2 기술 체크리스트

- [ ] Controller, Service, Repository 분리
- [ ] Entity와 DTO 분리
- [ ] Enum 사용
- [ ] 공통 예외 처리
- [ ] Validation 적용
- [ ] 검색 기능 구현
- [ ] 페이징 구현
- [ ] @Transactional 적용
- [ ] 테스트 코드 작성
- [ ] README 작성
- [ ] ERD 작성
- [ ] API 명세 작성
- [ ] 트러블슈팅 작성
- [ ] 화면 캡처 정리

### 3.17.3 취업 증거 체크리스트

- [ ] 핵심 기능에 `E-{FR ID}` 증거 ID를 부여했다.
- [ ] 각 핵심 기능의 코드 위치를 바로 열 수 있다.
- [ ] 실제 실행한 테스트 또는 빌드 명령을 기록했다.
- [ ] 기능별 커밋이나 Pull Request가 한 가지 의도를 가진다.
- [ ] 설계 선택의 이유와 고려한 대안을 설명할 수 있다.
- [ ] 실제로 해결한 트러블슈팅을 회귀 테스트와 연결했다.
- [ ] README, ERD, API 명세와 현재 코드가 일치한다.
- [ ] 프로젝트를 30초와 3분 분량으로 각각 설명할 수 있다.
- [ ] 기능·문서·트러블슈팅·면접 증거가 채용 공고 요구 역량과 연결된다.

### 3.17.4 단계별 통과 기준

| 단계 | PRD/TRD 관점의 최소 산출물 |
|---|---|
| 준비 | 목표 직무, 공고 3개, 증명할 역량 3개 |
| 설계 | MVP 범위, ERD, API, 상태 전이, 권한 정책 |
| 구현 | 필수 FR ID의 실행 코드와 기능별 커밋 |
| 검증 | 성공·실패·권한·상태 전이 테스트와 실제 통과 명령 |
| 지원 | README, 데모, 트러블슈팅, 면접 답변, 공개 링크 |

---

## 3.18 커밋 메시지 예시

```text
feat: 직원 등록 기능 구현
feat: 부서 관리 기능 구현
feat: 휴가 신청 기능 구현
feat: 휴가 승인 및 반려 기능 구현
feat: 공지사항 CRUD 기능 구현
feat: 전자결재 문서 작성 기능 구현
fix: 휴가 승인 상태 검증 오류 수정
refactor: EmployeeService 비즈니스 로직 분리
docs: PRD/TRD 문서 추가
docs: API 명세서 작성
test: 휴가 승인 서비스 테스트 추가
```

---

## 3.19 면접 대비 질문

```text
Q1. 이 프로젝트를 왜 만들었나요?

Q2. 사내 업무관리 시스템에서 가장 중요한 기능은 무엇이라고 생각했나요?

Q3. User와 Employee를 왜 분리했나요?

Q4. 직원과 부서의 관계는 어떻게 설계했나요?

Q5. 휴가 신청 상태값은 어떻게 관리했나요?

Q6. 승인/반려 기능에서 가장 중요한 검증은 무엇인가요?

Q7. @Transactional은 어디에 적용했고, 왜 적용했나요?

Q8. Entity를 직접 응답하지 않고 DTO를 사용한 이유는 무엇인가요?

Q9. 관리자와 일반 사용자의 권한은 어떻게 구분했나요?

Q10. 공지사항 목록에서 페이징이 필요한 이유는 무엇인가요?

Q11. 전자결재 기능을 확장한다면 어떻게 개선하고 싶나요?

Q12. 이 프로젝트에서 가장 어려웠던 문제와 해결 방법은 무엇인가요?
```

---

## 3.20 포트폴리오 설명 문장 예시

```text
사내 업무관리 시스템은 SI 업무에서 자주 등장하는 직원 관리, 부서 관리, 휴가 신청/승인, 공지사항, 전자결재 기능을 구현한 포트폴리오 프로젝트입니다.

직원과 부서의 관계를 설계하고, 휴가 신청과 결재 문서의 승인/반려 상태를 Enum으로 관리했습니다.

특히 직원 등록 시 User와 Employee가 함께 생성되어야 하므로 @Transactional을 적용하여 데이터 정합성을 보장했습니다.

또한 Controller, Service, Repository 계층을 분리하고 Entity와 DTO를 분리하여 유지보수성과 보안성을 고려했습니다.
```

---

# 4. 최종 정리

이 워크북 프로그램은 사내 업무관리 시스템을 실습 주제로 사용해 SI·백엔드 취업 포트폴리오를 단계적으로 완성하도록 돕는다.

이유는 다음과 같다.

1. 실제 SI 업무 시스템과 유사하다.
2. CRUD, 검색, 페이징, 승인/반려 흐름을 모두 보여줄 수 있다.
3. 직원, 부서, 휴가, 공지사항, 결재 등 도메인 관계를 설계할 수 있다.
4. 관리자와 일반 사용자 권한 구분을 설명할 수 있다.
5. 트랜잭션, 예외처리, DTO 분리 같은 실무 개념을 포함할 수 있다.

최소 완성 목표는 아래와 같다.

```text
직원 관리
부서 관리
휴가 신청/승인
공지사항 CRUD
공통 예외 처리
README
ERD
API 명세
트러블슈팅
```

확장 목표는 아래와 같다.

```text
전자결재
Spring Security
JWT
첨부파일
대시보드
Docker 배포
AWS 배포
```

이 문서를 기준으로 구현하고 워크북에 코드·테스트·Git·문서 증거를 공통 증거 ID로 연결하면 단순 예제가 아니라 SI·백엔드 취업 면접에서 설명 가능한 업무 시스템 포트폴리오로 발전시킬 수 있다.
