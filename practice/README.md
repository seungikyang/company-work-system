# Company Work System 학습 워크북

이 폴더는 [company_work_system_PRD_TRD.md](../company_work_system_PRD_TRD.md)에 정의된 사내 업무관리 시스템의 기술/개념을 직접 채워보며 공부하는 TODO/빈칸 워크북입니다.

`bank-system/practice` 의 학습 방식을 참고하여, 완성 코드를 베끼는 대신 핵심 로직을 손으로 채워보는 것을 목표로 합니다.

## 사용 방법

1. [problems.md](./problems.md) 를 열어 오늘 풀 문제를 고릅니다.
2. `practice/starter/` 아래의 해당 파일에서 `TODO`와 `____` 빈칸을 채웁니다.
3. 막히면 [company_work_system_PRD_TRD.md](../company_work_system_PRD_TRD.md) 의 관련 절(3.x)을 다시 읽습니다.
4. 채운 다음 [answers.md](./answers.md) 로 의도를 확인합니다.
5. 같은 흐름을 한 번 더 백지에서 작성해 봅니다.

이 폴더의 파일은 **컴파일 대상이 아니라 학습용 조각 코드**입니다. 패키지 선언과 import 는 일부러 생략했습니다.

## 학습 순서

| 순서 | 영역 | starter 파일 | 핵심 개념 |
|---|---|---|---|
| 00 | 빌드/설정 | `00-build-config/*` | Spring Boot 의존성, JPA 설정, 환경 변수 |
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
| 29 | Service | `29-leave-my-cancel/LeaveService.my.java` | 휴가 내 목록/상세/취소 |
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

1주차: 00 ~ 07 (빌드 + 도메인 모델링 + Repository)
2주차: 08 ~ 14, 23 ~ 30 (Service 비즈니스 로직 — 등록/검색/휴가/공지/결재/인증/부서/상세·취소·리스트)
3주차: 15 ~ 19, 25 ~ 27, 31 ~ 34 (예외/DTO/Controller/Repository/Config/Interceptor)
4주차: 20 ~ 22, 35 ~ 39 (보안/테스트/문서/뷰/구조/규칙/트러블슈팅/면접)

## 진행도 자가 점검

직접 풀고 나면 다음 질문에 짧게 답해 봅니다.

- 이 코드에서 트랜잭션이 필요한 이유는?
- 이 검증을 Controller 가 아닌 Service 에서 한 이유는?
- 이 응답을 Entity 그대로 두지 않고 DTO 로 감싼 이유는?
- 이 상태값을 String 이 아닌 enum 으로 둔 이유는?

답을 한 줄로 적을 수 있으면 그 문제는 졸업입니다.
