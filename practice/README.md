# Company Work System 취업 포트폴리오 워크북

이 폴더는 [company_work_system_PRD_TRD.md](../company_work_system_PRD_TRD.md)에 정의된 사내 업무관리 시스템의 기술과 개념을 직접 구현하며, Java 백엔드 취업에 사용할 코드·테스트·문서·면접 답변을 만드는 TODO/빈칸 워크북입니다.

완성 코드를 베끼는 대신 핵심 로직을 손으로 채우고, 같은 흐름을 실행 프로젝트에 다시 구현하는 것을 목표로 합니다.

## 처음 시작한다면

[초보자 기술 안내](./beginner-guide.md)에서 워크북과 구현 대상 시스템의 차이, 기술별 사용 이유와 직원 등록 흐름을 먼저 읽습니다. [40챕터 4지선다 289제](../problems.html)의 챕터별 개념 설명과 Starter 파일 상단의 초보자 안내를 읽고 입력 → 검사 → 저장 → 반환 순서로 코드를 따라갑니다.

## 프로그램 구성

| 구성 | 하는 일 | 완료 증거 |
|---|---|---|
| [`../index.html`](../index.html) | 목표와 오늘 할 일, 5단계와 00~39 챕터의 답안·힌트·정답 기준·진행 상태를 작성·체크 | 오늘 선택한 FR ID, 챕터별 선택·채점 기록 |
| `job-preparation-workbook.md` | 목표와 지원 준비 상태 관리 | 목표일, 역량 진단, 기능별 링크 |
| `feature-implementation-workbook.md` | FR ID를 계층과 테스트로 분해 | 기능별 구현·테스트 계획 |
| `problems.md` | 40챕터 4지선다 289문항과 해설 | 선택·채점 결과와 연결된 구현 코드 |
| `starter/`, `hard/` | 73개 폴더의 난이도별 코드 작성 연습 | HTML에 표시된 취업 결과물과 완료 검증 |
| `answers.md` | 선택 이유와 트레이드오프 확인 | 본인 말로 다시 쓴 설명 |
| [`../history.html`](../history.html) | 워크북 프로그램 발전 과정 확인 | Git 기반 변경 타임라인 |

## 사용 방법

기본기를 먼저 연습하려면 [4지선다 2,000제](../fundamentals.html)를 엽니다. 기술 중심 개정판은 HTML·CSS·JavaScript·Python 등 워크북 구현 기술 600문항과 Java·Spring·JPA 등 백엔드 실습/확장 기술 1,400문항입니다. 각 문항의 근거 파일과 코드·주석 표현을 확인하고, 사용 범위·기술 단원·유형·미풀이·오답 필터로 복습합니다. 40개 기술 단원 번호는 아래의 기존 코드 연습 챕터 번호와 다릅니다. 기존 기술 문제 1,000문항과 풀이 기록을 유지하고, 설명에서 기술 이름을 고르는 1,000문항을 추가했습니다. 단원마다 설명 선택 25문항·기술 식별 25문항을 제공합니다. [기술 점검표](./quiz/technology-audit.md)에서 기술별 범위를 확인할 수 있습니다. 코드 연결 객관식 289제와 코드 연습은 아래 순서로 이어갑니다.

1. [HTML 취업 워크북](../index.html)에서 목표를 적고 오늘 공부할 FR ID와 챕터를 고른 뒤, 선택 패널의 취업 결과물과 완료 검증을 확인합니다.
2. [job-preparation-workbook.md](./job-preparation-workbook.md) 에 목표 직무, 완료일, 현재 수준을 적습니다.
3. [feature-implementation-workbook.md](./feature-implementation-workbook.md) 에서 기능 요구사항(FR ID)별 구현 흐름을 먼저 훑습니다.
4. 오늘 구현할 FR ID를 고르고 `E-{FR ID}` 형식의 증거 ID를 만듭니다.
5. HTML 선택 패널의 4지선다 문제에서 보기 하나를 선택하고 채점·해설을 확인합니다.
6. [problems.md](./problems.md) 를 읽고 HTML 목차에서 연결된 Starter 또는 Hard 파일의 `TODO`와 `____` 빈칸을 채웁니다.
7. 틀린 문제는 `이 문제 다시 풀기`로 재풀이하고 [answers.md](./answers.md)의 기술 해설과 실제 코드를 비교합니다.
8. 선택 패널에 표시된 취업 결과물을 본인의 실행 가능한 Spring Boot 프로젝트에 다시 구현하고, 표시된 완료 검증을 테스트합니다.
9. 통합 워크북에 증거 ID, 코드 위치, 검증 결과, 커밋, 설계 이유를 기록하고 HTML의 `다음 챕터`로 이동합니다.

이 폴더의 파일은 **컴파일 대상이 아니라 학습용 조각 코드**입니다. 패키지 선언과 import 는 일부러 생략했습니다.

코드·설정 조각은 파일 상단의 `목표`를 먼저 읽고, 객관식 학습 문서은 상단 `챕터 계약`의 학습 목표·취업 결과물·완료 검증을 먼저 확인합니다. HTML 선택 패널의 계약과 같은 기준으로 본인 실행 프로젝트의 결과를 판단합니다.

학습 세션을 끝낼 때는 [취업 준비 통합 워크북](./job-preparation-workbook.md)의 하루 기록과 기능별 구현 증거를 함께 갱신합니다.

## 난이도 모드

| 폴더 | 난이도 | 설명 |
|---|---|---|
| `starter/` | 쉬움(스캐폴드) | 키워드 한두 개를 채우는 빈칸. 골격이 대부분 채워져 있어 구조 확인용으로도 쓴다. |
| `hard/` | 어려움(최대 작성) | 골격(제어구조·호출 형태)만 남기고 토큰 대부분을 비운 버전. "내가 거의 다 쓴다". 코드 챕터(00–19, 21, 23–34) 제공. |
| `answers.md` | 정답·해설 | 각 장의 정답 키워드 + **왜** + 트레이드오프 + 면접/트러블슈팅 연결. |

초보자 학습 루프: **개념·사용 이유·동작 설명 → Starter 골격 읽기와 빈칸 풀이 → 객관식 선택·채점 → 해설과 answers 비교 → 실행 프로젝트 구현·검증**. 익숙해지면 **Hard 직접 작성 → Starter·해설 비교**로 복습합니다. 자세한 규칙은 `hard/README.md` 참고. 문서형 챕터(20, 22, 35–39)는 같은 객관식과 기술 해설을 `starter/`에서 제공합니다.

## 학습 순서

| 순서 | 영역 | starter 파일 | 핵심 개념 |
|---|---|---|---|
| 00 | 빌드/설정 | `00-build-config/*` | Spring Boot 의존성, JPA 설정, 단계별 보안 의존성 |
| 01 | 도메인 | `01-user-entity/User.entity.java` | User 엔티티와 UserRole enum |
| 02 | 도메인 | `02-department-entity/Department.entity.java` | Department 엔티티, 부서명 unique |
| 03 | 도메인 | `03-employee-entity/Employee.entity.java` | Employee 엔티티, FK, EmployeeStatus |
| 04 | 도메인 | `04-leave-entity/LeaveRequest.entity.java` | LeaveRequest, LeaveType, ApprovalStatus |
| 05 | 도메인 | `05-notice-entity/Notice.entity.java` | Notice 엔티티, 중요 공지/조회수 |
| 06 | 도메인 | `06-approval-entity/ApprovalDocument.entity.java` | 결재 문서, DRAFT/PENDING/APPROVED/REJECTED |
| 07 | Repository | `07-repository/EmployeeRepository.java` | 메서드 쿼리, 검색, 페이징 |
| 08 | Service | `08-employee-register/EmployeeService.register.java` | User+Employee 트랜잭션 등록 |
| 09 | Service | `09-employee-search/EmployeeService.search.java` | 검색 + 페이징 |
| 10 | Service | `10-leave-request/LeaveService.request.java` | 휴가 신청, 날짜 검증 |
| 11 | Service | `11-leave-approval/LeaveService.approval.java` | 휴가 승인/반려 상태 검증 |
| 12 | Service | `12-notice-service/NoticeService.java` | 공지 CRUD, important 정렬 |
| 13 | Service | `13-approval-document/ApprovalService.document.java` | 결재 작성/요청, DRAFT → PENDING |
| 14 | Service | `14-approval-decision/ApprovalService.decision.java` | 결재 승인/반려, 작성자≠결재자 |
| 15 | 예외 | `15-error-model/ErrorCodeAndResponse.java` | ErrorCode, BusinessException, 응답 포맷 |
| 16 | 예외 | `16-global-handler/GlobalExceptionHandler.java` | @RestControllerAdvice |
| 17 | DTO | `17-dto-validation/EmployeeCreateRequest.java` | Bean Validation 어노테이션 |
| 18 | Controller | `18-controller-employee/EmployeeController.java` | REST 매핑, 권한 검사 |
| 19 | Controller | `19-controller-leave/LeaveController.java` | 일반/관리자 경로 분리 |
| 20 | 보안 | `20-security-session/SecurityFlow.md` | 세션 → Spring Security 진화 |
| 21 | 테스트 | `21-test-flow/LeaveFlowTest.java` | MockMvc 통합 흐름 |
| 22 | 문서화 | `22-documentation/PortfolioDocs.md` | README/ERD/API/트러블슈팅 |
| 23 | 인증 | `23-auth-login/*` | 로그인/로그아웃/내 정보/비밀번호 변경 |
| 24 | Service | `24-department-service/DepartmentService.java` | 부서 CRUD + 삭제 정책 |
| 25 | Controller | `25-department-controller/DepartmentController.java` | 부서 REST API + DTO |
| 26 | Controller | `26-notice-controller/NoticeController.java` | 공지 REST API + DTO |
| 27 | Controller | `27-approval-controller/ApprovalController.java` | 결재 REST API + my/pending |
| 28 | Service | `28-employee-detail-update/EmployeeService.detail.java` | 직원 상세/수정/퇴사 처리 |
| 29 | Service | `29-leave-my-cancel/LeaveService.my.java` | 휴가 내 목록/상세/취소 + 관리자 목록 |
| 30 | Service | `30-approval-lists/ApprovalService.lists.java` | 결재 my/pending/detail 권한 검사 |
| 31 | Repository | `31-repositories-all/AllRepositories.java` | User/Department/Leave/Notice/Approval Repository |
| 32 | DTO | `32-response-dto-mapping/ResponseDtoMapping.java` | Entity↔DTO 변환 패턴 |
| 33 | Config | `33-config-beans/ConfigBeans.java` | JPA Auditing + PasswordEncoder + WebMvc |
| 34 | 보안 | `34-current-user-interceptor/CurrentUserAndInterceptor.java` | HandlerInterceptor + @CurrentUser ArgumentResolver |
| 35 | View | `35-thymeleaf-views/ThymeleafViews.md` | Thymeleaf 템플릿 / 폼 / CSRF |
| 36 | 구조 | `36-package-architecture/PackageArchitecture.md` | 패키지 구조와 계층 책임 |
| 37 | 규칙 | `37-business-rules/BusinessRulesChecklist.md` | 비즈니스 규칙 다층 검증 매트릭스 |
| 38 | 운영 | `38-troubleshooting/Troubleshooting.md` | 5건 트러블슈팅 워크북 |
| 39 | 면접 | `39-interview-and-commit/InterviewAndCommit.md` | 면접 카드 + 커밋 컨벤션 + PR 템플릿 |

## 추천 학습 호흡

0일차: `feature-implementation-workbook.md` 로 전체 FR ID 와 구현 계층 매핑 훑기
1주차: 00 ~ 07 (빌드 + 도메인 모델링 + Repository)
2주차: 08 ~ 14, 23 ~ 30 (Service 비즈니스 로직 — 등록/검색/휴가/공지/결재/인증/부서/상세·취소·리스트)
3주차: 15 ~ 19, 25 ~ 27, 31 ~ 34 (예외/DTO/Controller/Repository/Config/Interceptor)
4주차: 20 ~ 22, 35 ~ 39 (보안/테스트/문서/뷰/구조/규칙/트러블슈팅/면접)

각 주차의 완료 여부는 읽은 챕터 수가 아니라 [취업 준비 통합 워크북](./job-preparation-workbook.md)에 남긴 실행 코드와 테스트 증거로 판단합니다.

## 챕터 완료 기록

각 챕터를 끝낼 때 아래 정보를 통합 워크북의 같은 증거 ID에 연결합니다.

| 기록 | 작성 내용 |
|---|---|
| 채용 역량 | 이 챕터가 증명하는 공고 요구사항 |
| 구현 | 실행 프로젝트의 파일 또는 코드 링크 |
| 검증 | 실제 실행한 명령, 통과 결과, 확인일 |
| Git | 한 가지 의도의 커밋 또는 PR 링크 |
| 설명 | 선택 이유, 대안, 면접에서 말할 한 문장 |

## 개념 자가 점검 · 4지선다

다음 객관식 문항으로 개념을 확인합니다. [선택·채점 화면](../problems.html)에서 풀 수 있습니다.

### Q08-01. 직원 등록에서 @Transactional을 두는 주된 경계는?

- ① HTML 제출 버튼의 클릭 이벤트다.
- ② User와 Employee 저장을 조율하는 Service 메서드다.
- ③ 응답 DTO의 toString 메서드다.
- ④ 로그 메시지 한 줄의 출력 시점이다.

**정답:** ②

**해설:** 여러 저장이 함께 성공하거나 실패하도록 업무 단위에 트랜잭션을 적용한다.

### Q37-01. 입력 형식·업무 조건·상태 전이·DB 무결성의 알맞은 검증 위치는?

- ① DTO·Service·Entity·DB에 각각 맞는 책임을 둔다.
- ② 모든 검증을 CSS에 둔다.
- ③ 모든 검증은 클라이언트가 했다고 믿는다.
- ④ DB는 모든 업무 권한을 자동 판단한다.

**정답:** ①

**해설:** 형식 검사와 업무·상태·최종 저장 제약은 서로 다른 실패를 막는다.

### Q32-01. EmployeeResponse.from(employee)의 역할은?

- ① Employee에 관리자 권한을 부여한다.
- ② Employee의 비밀번호 원문을 복원한다.
- ③ Entity에서 공개할 필드를 골라 응답 DTO를 만든다.
- ④ 모든 Entity 필드를 반드시 JSON으로 노출한다.

**정답:** ③

**해설:** 정적 매핑 팩토리는 응답 계약을 명시하지만 관계 접근의 조회 비용도 검토해야 한다.

### Q01-03. UserRole을 EnumType.STRING으로 저장하는 주된 이유는?

- ① enum 선언 순서가 바뀌어도 기존 저장값의 의미가 유지된다.
- ② enum 이름을 바꿔도 DB 마이그레이션이 항상 필요 없다.
- ③ 모든 역할에 자동으로 관리자 권한을 부여한다.
- ④ enum을 DB에 저장하지 않도록 한다.

**정답:** ①

**해설:** STRING은 이름을 저장하므로 순서 변경에 안전하다. 이름을 변경하는 경우에는 기존 데이터 이관을 검토한다.

객관식 이해 확인 뒤에는 실행 프로젝트에 구현하고 실제 검증 명령·테스트 결과·커밋 근거를 남깁니다.
