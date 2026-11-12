# 4지선다 2,000제 기술 점검표

점검일: 2026-10-09 · 기술 중심 개정판 v2 · 2,000문항 확장

[문제 풀기](../../fundamentals.html) · [기술과 코드 안내](../beginner-guide.md)

## 점검 결과와 수정 내용

기존 문제에는 Java·Spring 관련 내용도 있었지만 업무 규칙, 면접 설명, 포트폴리오 작성 항목이 섞여 있었습니다. 실제 워크북의 HTML·CSS·JavaScript와 로컬 Python 서버를 충분히 다루지 않아, 프로그램 사용 기술 중심으로 40개 기술 단원을 다시 구성했습니다.

이 저장소는 정적 워크북과 백엔드 학습 자료입니다. Java 백엔드가 이 워크북에서 실행되는 것은 아닙니다. 실습 자료에는 TODO 빈칸과 주석 예제가 있으며, Spring Security 설정은 확장 미리보기로 구분합니다.

| 사용 범위 | 단원 | 문항 수 |
| --- | --- | ---: |
| 워크북 실제 구현: HTML·CSS·JavaScript·DOM·저장·HTTP 서버 | 00–11 | 600 |
| 백엔드 실습 조각: Java·Spring·JPA·검증·세션·테스트 등 | 12–34, 36–39 | 1,350 |
| 백엔드 확장 미리보기: Spring Security | 35 | 50 |
| 합계 | 40개 단원 | 2,000 |

## 문항 구성과 출처

200개 기술 항목을 개념·적용 상황·구현 방법·오류 위험·검증 방법의 다섯 관점으로 출제합니다. 각 관점에서 설명 선택과 기술 식별 두 유형을 제공합니다. 각 단원은 5개 항목 × 5개 관점 × 2개 유형 = 50문항입니다. 독립된 기술 항목이 2,000개라는 의미는 아닙니다.

보기는 해당 항목의 설명 하나와 같은 단원의 다른 항목 설명 세 개로 구성합니다. 추가된 기술 식별 문항은 설명을 지문으로 제시하고 같은 단원의 기술 이름 네 개 중 해당하는 하나를 고릅니다. 기술 식별 문항의 근거 코드·표현은 채점 후 표시하여 정답 이름을 미리 드러내지 않습니다. 해설에서 정답과 다른 보기의 개념도 비교할 수 있습니다.

각 문항에는 실제 파일 경로와 그 파일에 존재하는 코드·설정·주석 표현을 표시합니다. 아래 번호는 이 문제집의 기술 단원 번호이며 기존 코드 연습 챕터 번호와 다릅니다. 모든 단원은 50문항입니다.

| 기술 단원 | 내용 | 사용 범위 | 근거 파일 |
| --- | --- | --- | --- |
| 20 | Spring MVC 입력 매핑 | 실습 조각 | `practice/starter/18-controller-employee/EmployeeController.java` |
| 21 | REST 응답과 HTTP 메서드 | 실습 조각 | `practice/starter/26-notice-controller/NoticeController.java` |
| 22 | Bean Validation 제약 | 실습 조각 | `practice/starter/17-dto-validation/EmployeeCreateRequest.java` |
| 23 | Java 예외와 오류 데이터 | 실습 조각 | `practice/starter/15-error-model/ErrorCodeAndResponse.java` |
| 24 | Spring MVC 예외 변환 | 실습 조각 | `practice/starter/16-global-handler/GlobalExceptionHandler.java` |
| 25 | JPA Entity와 컬럼 | 실습 조각 | `practice/starter/01-user-entity/User.entity.java` |
| 26 | JPA 연관관계와 로딩 | 실습 조각 | `practice/starter/03-employee-entity/Employee.entity.java` |
| 27 | Spring Data Repository | 실습 조각 | `practice/starter/07-repository/EmployeeRepository.java` |
| 28 | JPQL과 갱신 쿼리 | 실습 조각 | `practice/starter/31-repositories-all/AllRepositories.java` |
| 29 | 페이지와 함수형 변환 | 실습 조각 | `practice/starter/09-employee-search/EmployeeService.search.java` |
| 10 | URL과 문서 읽기 | 실제 구현 | `workbook-viewer.html` |
| 11 | Python 로컬 HTTP 서버 | 실제 구현 | `scripts/serve_workbook.py` |
| 12 | Java 필드와 자료형 | 실습 조각 | `practice/starter/03-employee-entity/Employee.entity.java` |
| 13 | Java 객체와 메서드 | 실습 조각 | `practice/starter/03-employee-entity/Employee.entity.java` |
| 14 | Java 조건과 문자열 | 실습 조각 | `practice/starter/02-department-entity/Department.entity.java` |
| 15 | Java record와 제네릭 | 실습 조각 | `practice/starter/32-response-dto-mapping/ResponseDtoMapping.java` |
| 16 | Gradle과 Spring Boot 의존성 | 실습 조각 | `practice/starter/00-build-config/build.gradle.fragment` |
| 17 | YAML·H2·JPA 설정 | 실습 조각 | `practice/starter/00-build-config/application.yml.fragment` |
| 18 | Spring 객체와 의존성 주입 | 실습 조각 | `practice/starter/23-auth-login/AuthService.login.java` |
| 19 | Spring 설정과 Bean 등록 | 실습 조각 | `practice/starter/33-config-beans/ConfigBeans.java` |
| 30 | 트랜잭션과 예외 전파 | 실습 조각 | `practice/starter/08-employee-register/EmployeeService.register.java` |
| 31 | DTO 매핑과 직렬화 경계 | 실습 조각 | `practice/starter/32-response-dto-mapping/ResponseDtoMapping.java` |
| 32 | BCrypt와 PasswordEncoder | 실습 조각 | `practice/starter/23-auth-login/AuthService.login.java` |
| 33 | Servlet 세션 API | 실습 조각 | `practice/starter/23-auth-login/AuthController.java` |
| 34 | Interceptor와 인자 해석기 | 실습 조각 | `practice/starter/34-current-user-interceptor/CurrentUserAndInterceptor.java` |
| 35 | Spring Security 확장 설정 | 확장 미리보기 | `practice/starter/33-config-beans/ConfigBeans.java` |
| 36 | Thymeleaf 템플릿 | 실습 조각 | `practice/starter/35-thymeleaf-views/ThymeleafViews.md` |
| 37 | JUnit과 Spring 테스트 구성 | 실습 조각 | `practice/starter/21-test-flow/LeaveFlowTest.java` |
| 38 | JPA 감사 필드와 DB 제약 | 실습 조각 | `practice/starter/02-department-entity/Department.entity.java` |
| 39 | MockMvc 요청과 JSON 검증 | 실습 조각 | `practice/starter/21-test-flow/LeaveFlowTest.java` |
| 00 | HTML 문서 구조 | 실제 구현 | `index.html` |
| 01 | HTML 입력과 접근성 | 실제 구현 | `index.html` |
| 02 | CSS 배치와 크기 | 실제 구현 | `index.html` |
| 03 | CSS 반응형과 상호작용 | 실제 구현 | `index.html` |
| 04 | JavaScript 값과 조건 | 실제 구현 | `index.html` |
| 05 | JavaScript 함수와 배열 | 실제 구현 | `index.html` |
| 06 | DOM 요소 생성과 표시 | 실제 구현 | `problems.html` |
| 07 | 브라우저 이벤트 | 실제 구현 | `index.html` |
| 08 | JSON과 브라우저 저장 | 실제 구현 | `index.html` |
| 09 | fetch와 비동기 처리 | 실제 구현 | `problems.html` |

HTML·Python 근거 파일은 저장소에서 직접 확인합니다. 문항의 ‘연결된 기술 설명’은 워크북 기술의 경우 초보자 안내로, 백엔드 기술의 경우 해당 실습 자료로 연결됩니다. 로컬 서버의 비공개 경로를 문서 뷰어에 노출하지 않습니다.

## 풀이 기록 분리

이전 문제집의 기록과 메인 워크북의 기록은 삭제하지 않습니다. 문제 내용이 바뀌었으므로 이전 선택을 새 문제의 답으로 옮기지 않습니다. 이번 확장은 기술 개정판의 기존 1–1,000번 문항 ID·번호·보기·정답을 그대로 유지하고 1,001–2,000번을 추가하므로 기존 v2 풀이와 백업을 계속 사용할 수 있습니다.

- 개정판 저장 키: `company-work-system.fundamentals.technology.v2`
- 개정판 문항 ID: `tech-v2-{기술 단원}-{항목 번호}-{유형}`
- 개정판 저장 형식 버전: `2`

기록은 브라우저와 접속 주소별로 유지됩니다. 기기 간 자동 동기화는 지원하지 않습니다.

## 검증과 한계

저장소 루트에서 다음 명령으로 재검사합니다.

```sh
./scripts/assert_repo_root.sh
node scripts/verify_fundamentals.cjs
python3 scripts/verify_workbook.py
```

문항 검사는 총 2,000문항, 단원·유형별 개수, 네 보기와 정답 위치 분포, 지문·ID 중복, 출처 파일과 200개 근거 표현의 존재, 필터·채점·저장 분리·DOM 연결을 확인합니다. 출처 문자열이 존재한다는 검사는 구조적 검증이며, 모든 설명의 의미적 정확성이나 백엔드 실행 성공을 증명하는 것은 아닙니다. 백엔드 조각은 완성된 앱이 아니므로 컴파일·통합 테스트 성공을 주장하지 않습니다.

2026-10-09 확장 검증 결과: 기본기·학습 기록·워크북 자동 검사와 `git diff --check`가 통과했습니다. 확장 전 1,000문항 전체의 SHA-256을 비교하여 ID·번호·보기·정답·해설 보존을 확인했습니다. 추가 문항의 다섯 유형 모두 백업·복원 대상에 포함됩니다.

Playwright 로컬 HTTP 검사에서는 2,000문항·80페이지, 단원별 50문항, 워크북 600문항·백엔드 1,400문항 필터, 기술 식별 다섯 유형의 채점·근거 표시·재풀이, 기존·추가 문항 풀이의 새로고침 복원, 2,000번 문항 검색과 390px 모바일 가로 넘침 없음을 확인했습니다.

2026-09-29 검증 결과: 위 자동 검사와 `git diff --check`가 통과했습니다. Playwright로 로컬 HTTP 페이지에서 다음 항목을 확인했습니다.

- 첫 화면 25문항과 각 문항의 네 보기, HTML 근거 표현의 안전한 텍스트 출력
- 정답·오답 채점, 새로고침 후 개정판 풀이 복원, 이전 저장 키의 값 보존
- 실제 구현 300문항·백엔드 700문항 필터, 확장 단원 25문항의 구분 표시
- 범위·단원 불일치의 빈 결과와 Python 검색 25문항
- 390px 모바일 화면에서 긴 근거 경로를 포함한 가로 넘침 없음 및 문제 카드 시각 확인
- 기술 점검표 문서 뷰어 연결

브라우저 검증은 로컬 HTTP 접속 기준이며, `file:` 직접 열기나 완성된 Java 서버 실행을 검증한 것은 아닙니다.
