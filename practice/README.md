# Company Work System 취업 포트폴리오 워크북

이 폴더는 [company_work_system_PRD_TRD.md](../company_work_system_PRD_TRD.md)에 정의된 사내 업무관리 시스템의 기술과 개념을 직접 구현하며, Java 백엔드 취업에 사용할 코드·테스트·문서·면접 답변을 만드는 TODO/빈칸 워크북입니다.

완성 코드를 베끼는 대신 핵심 로직을 손으로 채우고, 같은 흐름을 실행 프로젝트에 다시 구현하는 것을 목표로 합니다.

## 프로그램 구성

| 구성 | 하는 일 | 완료 증거 |
|---|---|---|
| [`../index.html`](../index.html) | 목표와 오늘 할 일, 5단계와 00~39 챕터의 답안·힌트·정답 기준·진행 상태를 작성·체크 | 오늘 선택한 FR ID, 챕터별 답안과 비교 완료 기록 |
| `job-preparation-workbook.md` | 목표와 지원 준비 상태 관리 | 목표일, 역량 진단, 기능별 링크 |
| `feature-implementation-workbook.md` | FR ID를 계층과 테스트로 분해 | 기능별 구현·테스트 계획 |
| `problems.md` | 챕터별 기술 과제 선택 | 직접 작성한 답과 코드 |
| `starter/`, `hard/` | 73개 폴더의 난이도별 코드 작성 연습 | HTML에 표시된 취업 결과물과 완료 검증 |
| `answers.md` | 선택 이유와 트레이드오프 확인 | 본인 말로 다시 쓴 설명 |
| [`../history.html`](../history.html) | 워크북 프로그램 발전 과정 확인 | Git 기반 변경 타임라인 |

## 사용 방법

1. [HTML 취업 워크북](../index.html)에서 목표를 적고 오늘 공부할 FR ID와 챕터를 고른 뒤, 선택 패널의 취업 결과물과 완료 검증을 확인합니다.
2. [job-preparation-workbook.md](./job-preparation-workbook.md) 에 목표 직무, 완료일, 현재 수준을 적습니다.
3. [feature-implementation-workbook.md](./feature-implementation-workbook.md) 에서 기능 요구사항(FR ID)별 구현 흐름을 먼저 훑습니다.
4. 오늘 구현할 FR ID를 고르고 `E-{FR ID}` 형식의 증거 ID를 만듭니다.
5. HTML 선택 패널의 취업형 문제에 핵심 책임·실패 조건·검증 방법을 먼저 적고, 막히는 만큼만 힌트를 확인합니다.
6. [problems.md](./problems.md) 를 읽고 HTML 목차에서 연결된 Starter 또는 Hard 파일의 `TODO`와 `____` 빈칸을 채웁니다.
7. `정답 기준`과 [answers.md](./answers.md)를 비교해 내 답을 보완하고 비교 완료를 체크합니다.
8. 선택 패널에 표시된 취업 결과물을 본인의 실행 가능한 Spring Boot 프로젝트에 다시 구현하고, 표시된 완료 검증을 테스트합니다.
9. 통합 워크북에 증거 ID, 코드 위치, 검증 결과, 커밋, 설계 이유를 기록하고 HTML의 `다음 챕터`로 이동합니다.

이 폴더의 파일은 **컴파일 대상이 아니라 학습용 조각 코드**입니다. 패키지 선언과 import 는 일부러 생략했습니다.

코드·설정 조각은 파일 상단의 `목표`를 먼저 읽고, 서술형 Markdown은 상단 `챕터 계약`의 학습 목표·취업 결과물·완료 검증을 먼저 확인합니다. HTML 선택 패널의 계약과 같은 기준으로 본인 실행 프로젝트의 결과를 판단합니다.

학습 세션을 끝낼 때는 [취업 준비 통합 워크북](./job-preparation-workbook.md)의 하루 기록과 기능별 구현 증거를 함께 갱신합니다.

## 난이도 모드

| 폴더 | 난이도 | 설명 |
|---|---|---|
| `starter/` | 쉬움(스캐폴드) | 키워드 한두 개를 채우는 빈칸. 골격이 대부분 채워져 있어 구조 확인용으로도 쓴다. |
| `hard/` | 어려움(최대 작성) | 골격(제어구조·호출 형태)만 남기고 토큰 대부분을 비운 버전. "내가 거의 다 쓴다". 코드 챕터(00–19, 21, 23–34) 제공. |
| `answers.md` | 정답·해설 | 각 장의 정답 키워드 + **왜** + 트레이드오프 + 면접/트러블슈팅 연결. |

권장 학습 루프: **HTML 문제에 내 답 작성 → 필요한 만큼 힌트 확인 → hard 에서 백지에 가깝게 작성 → 같은 번호 starter 로 구조 확인 → 정답 기준과 answers 로 "왜" 확인**. 자세한 규칙은 `hard/README.md` 참고. 워크시트형 Markdown 챕터(20, 22, 35–39)는 원래 서술 작성량이 많아 `starter/` 하나로 충분합니다.

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

## 개념 자가 점검

직접 풀고 나면 다음 질문에 짧게 답해 봅니다.

- 이 코드에서 트랜잭션이 필요한 이유는?
- 이 검증을 Controller 가 아닌 Service 에서 한 이유는?
- 이 응답을 Entity 그대로 두지 않고 DTO 로 감싼 이유는?
- 이 상태값을 String 이 아닌 enum 으로 둔 이유는?

한 줄 답변은 개념 이해 확인입니다. 챕터를 완료하려면 HTML 선택 패널의 취업 결과물을 실행 프로젝트에 구현하고, 표시된 완료 검증과 관련 회귀 테스트를 통과한 뒤 검증 명령 기록, 커밋 또는 PR 링크까지 남겨야 합니다.
