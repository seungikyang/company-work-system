# 초보자를 위한 기술과 코드 안내

처음에는 기술 이름을 모두 외우지 않아도 됩니다. 이 안내에서 전체 흐름을 보고, [40개 문제](../problems.html)에서 오늘 배울 챕터의 ‘어떤 기술인가요? → 왜 쓰나요? → 어떻게 동작하나요?’를 읽은 다음 Starter의 빈칸 하나를 채워 보세요.

## 1. 지금 사용하는 프로그램과 만들 프로그램

이 저장소에서 실행하는 프로그램은 HTML 학습 워크북입니다. 목표와 답안을 저장하고 Markdown 문서와 코드 조각을 읽는 화면을 제공합니다. 직원 등록이나 휴가 승인 API가 실행되는 Spring Boot 애플리케이션은 아직 아닙니다.

학습자가 만들 프로그램은 Java·Spring Boot 기반 사내 업무관리 시스템입니다. practice/starter와 practice/hard는 그 프로젝트에 옮겨 구현할 연습 조각입니다. TODO·____, 생략된 import와 클래스, 여러 public 타입이 한 파일에 들어 있는 예제가 있으므로 이 폴더 자체를 컴파일해서 실행할 수는 없습니다.

- Java를 처음 접하면 변수(값을 담는 이름), 조건문(if), 메서드(이름 붙인 작업), 클래스(객체의 구조), 객체(클래스로 만든 실제 값), 인터페이스(호출할 기능의 약속)를 먼저 익힙니다.
- 기초 문법을 알아도 업무 시스템이 처음이면 Starter의 설명과 골격부터 읽습니다. Hard는 같은 개념을 설명하고 구현할 수 있을 때 복습용으로 사용합니다.
- 한 번에 모든 기능을 붙이기보다 ‘직원 등록 하나가 요청부터 저장까지 어떻게 흐르는가’를 먼저 따라갑니다.

## 2. 워크북 자체에는 어떤 기술이 들어 있나요?

아래 기술은 지금 읽는 학습 화면에 실제로 사용됩니다. Java 백엔드 실습 기술과 별개입니다.

### HTML·CSS·JavaScript: 내용, 모양, 행동

HTML은 제목·입력창·버튼의 구조를 만들고, CSS는 배치·색상·화면 크기에 따른 모양을 정합니다. JavaScript는 사용자가 입력하거나 챕터를 선택했을 때 화면을 바꿉니다. 이 정도의 학습 화면을 별도 UI 라이브러리 없이 브라우저 기능으로 구현해 파일에서 구조와 동작을 함께 읽을 수 있습니다.

index.html에서 HTML의 id는 특정 요소에 붙인 이름입니다. JavaScript의 byId가 그 요소를 찾고 addEventListener가 입력·클릭에 반응할 작업을 연결합니다. renderSelectedChapter는 선택한 챕터 안내를 화면에 반영합니다. 이처럼 화면 요소를 프로그램에서 다루는 구조를 DOM이라고 부릅니다.

직접 확인: index.html에서 today-chapter를 검색하고, 선택값이 상태에 반영되는 코드와 renderSelectedChapter를 차례로 읽습니다. 챕터를 바꾸면 제목과 학습 링크가 함께 바뀌는지 봅니다.

### localStorage·JSON: 새로고침 후에도 답안 기억하기

localStorage는 브라우저 안에 문자열을 보관하는 저장 공간입니다. 워크북은 입력값을 state라는 JavaScript 객체에 모으고 JSON.stringify로 문자열로 바꿔 company-workbook-state-v1이라는 키에 저장합니다. 다시 열 때 JSON.parse로 객체를 복원합니다. 로그인 서버나 DB 없이 학습 기록을 유지하려고 사용합니다.

이 기록은 서버 계정의 데이터가 아닙니다. 브라우저·프로필·접속 주소의 출처(origin: 프로토콜, 호스트, 포트)가 다르면 저장 공간도 다릅니다. 브라우저 데이터를 지우면 기록을 잃을 수 있고, 저장이 차단되면 화면에 안내가 표시됩니다. localStorage와 서버 로그인 세션은 서로 다른 기능입니다.

직접 확인: 목표에 시험용 문장을 적고 새로고침해 남는지 봅니다. 제출할 코드와 검증 증거는 본인 프로젝트에도 기록합니다.

### Python HTTP 서버·fetch: 학습 파일 읽어 오기

scripts/serve_workbook.py는 허용한 학습 파일을 HTTP로 제공하는 작은 로컬 서버입니다. fetch는 브라우저가 그 서버에 파일 내용을 요청하는 기능입니다. problems.html은 문제·정답 Markdown과 index.html의 챕터 정보를 읽어 카드로 조합합니다. workbook-viewer.html은 문서나 코드를 읽어 제목·목차·원문을 표시합니다.

원문 요청에는 raw=1을 붙입니다. 그렇지 않은 학습 문서 요청은 서버가 공통 읽기 화면으로 이동시킵니다. 읽기 화면은 원문을 textContent로 넣어 문서 속 코드를 실행하지 않고 글자로 보여줍니다.

HTML 파일을 직접 여는 방식은 브라우저의 파일 접근 정책 때문에 문서 읽기나 전체 문제의 fetch가 실패할 수 있습니다. 이 기능까지 사용할 때는 저장소 루트에서 다음 순서로 실행합니다.

1. ./scripts/assert_repo_root.sh 로 작업 위치를 확인합니다.
2. python3 scripts/serve_workbook.py 로 서버를 시작합니다.
3. 터미널에 출력된 ‘취업 워크북 서버 실행’ 주소를 브라우저에서 엽니다. 주소 끝의 폴더 이름은 실제 저장소 폴더명에 따라 달라집니다.
4. 사용을 마치면 서버를 실행한 터미널에서 Ctrl+C로 종료합니다.

이미 실행 중인 서버 때문에 포트 충돌이 나면 기존 워크북 주소를 먼저 확인합니다. Orca에서 여는 방법은 [루트 README](../README.md)에 있습니다.

### 검증 스크립트: 화면과 자료의 연결 확인

scripts/verify_workbook.py는 HTML 태그·링크·챕터 구성과 JavaScript 구문 등을 검사합니다. 문서를 추가하다 링크를 깨뜨리거나 코드를 연결하지 않는 실수를 찾으려고 사용합니다. Python 3와 Node.js를 준비한 뒤 저장소 루트에서 python3 scripts/verify_workbook.py를 실행합니다.

이 검사 통과가 사내 업무 시스템의 API 테스트 통과를 뜻하지는 않습니다. 학습자가 만드는 Spring Boot 프로젝트의 동작은 별도로 검증해야 합니다.

## 3. 실습할 백엔드 기술은 왜 필요한가요?

이 워크북의 기준은 PRD/TRD와 00장 예제의 Java 17·Spring Boot 3.x입니다. 기술의 이름만 모으지 말고 아래 역할을 실제 코드와 연결하세요.

### Java·Gradle·Spring Boot: 업무 코드와 실행 준비

Java는 ‘퇴사한 직원은 휴가를 신청할 수 없다’ 같은 업무 규칙을 쓰는 언어입니다. Gradle은 필요한 라이브러리를 내려받고 컴파일·테스트·빌드를 수행합니다. 라이브러리는 미리 구현된 재사용 코드이며, 프로젝트가 필요로 하는 라이브러리를 의존성이라고 부릅니다.

Spring Boot는 Spring 기반 애플리케이션의 실행과 공통 설정을 돕습니다. Starter 의존성은 웹이나 JPA 같은 용도에 필요한 라이브러리를 묶어 제공합니다. spring-boot-starter-web에는 요청을 받을 내장 Tomcat, 요청을 메서드로 연결하는 Spring MVC, JSON과 Java 객체 사이 변환에 쓰는 Jackson 등이 연결됩니다.

00장의 build.gradle.fragment는 도구와 의존성 목록, application.yml.fragment는 실행 설정입니다. 의존성을 추가하는 일과 실제 설정·업무 코드를 작성하는 일은 다릅니다. 예를 들어 app.security.session-timeout은 예제의 사용자 정의 설정값이므로 그 값만 적는다고 서버 세션 만료 시간이 자동으로 바뀌지는 않습니다.

### Spring Bean·의존성 주입: 필요한 객체 연결하기

직원 Controller는 직원 Service가 필요하고 Service는 Repository와 PasswordEncoder가 필요합니다. 각 클래스가 직접 모든 객체를 만들면 교체와 테스트가 어려워집니다. Spring이 객체를 생성·관리하고 생성자 등으로 필요한 객체를 전달하는 방식을 의존성 주입(DI)이라고 합니다. Spring이 관리하는 객체를 Bean이라고 부릅니다.

33장의 @Bean 메서드는 사용할 비밀번호 인코더를 등록합니다. @Service·@Component 등은 탐색 대상 클래스임을 표시합니다. 이런 @표시는 어노테이션이며 관련 도구가 읽을 메타데이터입니다. 글자만 붙이면 모든 기능이 켜지는 것은 아니고 의존성·설정·호출 방식이 맞아야 합니다.

Starter의 @Getter·@RequiredArgsConstructor는 Lombok이 컴파일 때 getter나 필요한 생성자를 만들어 주는 표기입니다. 00장 의존성 조각에는 Lombok 설정이 포함되어 있지 않으므로 실행 프로젝트에서는 해당 설정을 추가하거나 getter·생성자를 직접 작성해야 합니다. 처음에는 직접 작성해 주입 흐름을 확인해도 됩니다.

### Spring MVC·DTO·Validation: 요청을 안전한 입력으로 바꾸기

HTTP는 클라이언트와 서버가 요청·응답을 주고받는 통신 방식입니다. GET은 조회, POST는 생성이나 처리 요청 등에 씁니다. API는 어떤 주소에 어떤 입력을 보내면 어떤 결과를 받는지 정한 약속입니다. JSON은 이때 사용할 수 있는 데이터 표현 형식입니다.

18장의 Controller는 주소와 HTTP 메서드에 맞는 Java 메서드를 연결합니다. @RequestBody로 JSON을 요청 DTO로 바꾸고 @Valid로 17장의 필드 제약을 검사합니다. DTO(Data Transfer Object)는 전달할 값만 모은 객체입니다. 입력 DTO와 응답 DTO를 나누면 클라이언트가 수정할 수 있는 값과 볼 수 있는 값을 각각 정할 수 있습니다.

Validation은 빈 이메일 같은 입력 형태를 검사합니다. 중복 이메일이나 부서 존재 여부는 DB를 확인해야 하므로 Service가 검사합니다. Controller는 통신을 담당하고 Service는 업무 순서를 담당합니다.

### JPA·Hibernate·Spring Data JPA·DB: 객체를 데이터로 보관하기

DB는 데이터를 테이블에 저장하고 SQL은 데이터를 조회·변경하는 언어입니다. Entity는 저장할 업무 대상을 표현하는 객체입니다. PK는 행을 구분하는 기본키, FK는 다른 행을 가리키는 외래키입니다.

JPA는 Java 객체와 관계형 DB를 연결하는 표준이고, Hibernate는 이 표준을 구현해 SQL 실행과 객체 관리를 맡는 도구입니다. Spring Data JPA는 그 위에서 Repository 구현과 메서드 이름 기반 조회 등을 제공합니다. ORM은 이런 객체와 테이블 사이의 연결 방식입니다. 이 도구를 사용해도 SQL과 테이블 관계를 이해해야 합니다.

07장의 EmployeeRepository extends JpaRepository<Employee, Long>에서 Employee는 대상 Entity, Long은 ID 타입입니다. findByUser_Email은 Employee의 user 속성에서 email로 이어지는 조건을 해석합니다. 직접 작성한 구현 클래스가 없는 이유는 Spring Data JPA가 구현을 제공하기 때문입니다. [공식 쿼리 메서드 설명](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html)

H2는 별도 DB 설치 부담을 줄이는 학습용 선택입니다. 예제의 jdbc:h2:mem 설정은 메모리를 쓰므로 애플리케이션이 끝난 뒤에도 데이터가 남는 저장소로 생각하면 안 됩니다. MySQL은 별도 DB 환경에서 저장과 운영을 연습할 때 사용할 수 있습니다. H2에서 통과한 SQL·제약·테스트가 MySQL에서도 같은지는 실제 대상 DB에서 확인합니다.

### 트랜잭션과 변경 감지: 저장을 한 덩어리로 다루기

08장 직원 등록은 User와 Employee를 함께 만듭니다. 둘 중 하나만 저장되면 계정과 직원 정보가 어긋납니다. 트랜잭션은 여러 변경을 함께 확정(commit)하거나 되돌리는(rollback) 단위입니다.

기본 프록시 방식에서 Spring이 관리하는 Service를 외부에서 호출하면 @Transactional을 처리하는 대리 객체가 트랜잭션 경계를 만듭니다. 기본 설정은 밖으로 전달된 RuntimeException과 Error에 롤백하며, checked exception은 별도 롤백 규칙이 필요할 수 있습니다. 같은 객체 내부에서 메서드를 호출하면 해당 메서드의 새 트랜잭션 설정이 적용되지 않을 수 있으므로 ‘어노테이션을 붙였다’만으로 판단하지 않습니다. [공식 @Transactional 설명](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)

11장에서는 트랜잭션 안에서 조회한 휴가 객체를 변경합니다. JPA가 관리하는 객체의 변경을 감지해 flush 시 SQL로 반영하고 커밋하면 확정됩니다. 이를 변경 감지(dirty checking)라고 부릅니다. save 호출과 커밋은 같은 말이 아니며, 트랜잭션을 붙이는 것만으로 동시 승인 충돌까지 해결되지는 않습니다.

### BCrypt·세션·Spring Security: 로그인과 권한 지키기

BCrypt는 비밀번호를 복원할 수 없는 해시로 저장하는 방법입니다. 가입 시 PasswordEncoder.encode로 저장용 값을 만들고 로그인 시 matches로 입력값을 검증합니다. 매번 소금값(salt)이 달라질 수 있으므로 새로 encode한 문자열이 DB 문자열과 같은지 비교하지 않습니다. [공식 비밀번호 저장 설명](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)

23장 로그인 성공 후 서버 세션에는 USER_ID·USER_ROLE을 넣습니다. 브라우저는 보통 세션 ID 쿠키를 다음 요청에 보내고 서버는 해당 세션에서 로그인 정보를 찾습니다. 34장의 인터셉터는 로그인 유무를 검사하고 인자 해석기는 사용자 ID를 Controller에 넘깁니다. 로그인한 사람이 이 휴가를 승인해도 되는지는 별도의 권한·소유자 검사입니다.

학습 단계는 1차 spring-security-crypto + 직접 구현한 세션 처리, 2차 Spring Security, 3차 JWT 확장입니다. crypto만 추가하면 로그인 차단이나 @PreAuthorize가 자동 활성화되지 않습니다. Spring Security 단계에서는 필터와 메서드 보안 설정, 인증 정보 저장 방식을 함께 구현해야 합니다.

33장의 SecurityFilterChain은 주석 처리된 미리보기입니다. 23장의 JSON 로그인과 formLogin 처리를 같은 주소에 그대로 겹쳐 켜는 완성 설정이 아닙니다. 35장의 브라우저 세션 폼에서는 CSRF(브라우저의 인증 정보를 악용하는 위조 요청)를 막는 토큰 처리까지 구현하며, 미리보기의 csrf.disable()을 완성 설정으로 복사하지 않습니다.

### Thymeleaf·JUnit·Git: 화면, 검증, 변경 기록

Thymeleaf는 서버에서 Model의 데이터와 HTML 템플릿을 합쳐 화면을 만듭니다. 예를 들어 직원 목록을 반복 출력하는 템플릿에 실제 직원 이름을 넣어 브라우저로 보냅니다. JSON 응답용 Controller와 화면 이름을 반환하는 Controller의 역할을 나눕니다. 현재 워크북 HTML은 Thymeleaf로 생성되는 화면이 아닙니다.

JUnit 5는 테스트 코드를 실행합니다. MockMvc는 실제 네트워크 없이 MVC 요청 흐름을 검증합니다. 21장에서는 입력·행동·예상 결과를 준비하고, 신청 성공뿐 아니라 권한 부족·중복 승인 같은 실패도 확인합니다. Git 커밋에는 검증한 변경과 이유를 남겨 나중에 되짚습니다.

## 4. 직원 등록 요청 하나를 끝까지 따라가기

아래는 실습 프로젝트에 구현할 흐름입니다. 현재 워크북에서 이 API가 실행되는 것은 아닙니다.

브라우저 또는 API 도구 → 인증·권한 검사 → EmployeeController → EmployeeService → Repository → DB

DB 조회·변경 결과 → Service의 응답 DTO → Controller의 HTTP 응답 → 브라우저 또는 API 도구

1. 클라이언트가 POST /api/employees에 이메일·비밀번호·사번·부서 ID 등을 보냅니다. 요청 DTO의 필드부터 확인합니다.
2. 선택한 보안 단계에서 관리자 요청인지 검사합니다. 18장의 @PreAuthorize를 쓰려면 Spring Security와 메서드 보안 설정이 필요합니다.
3. 18장의 register가 JSON을 17장의 EmployeeCreateRequest로 받고 입력 제약을 검사합니다. 이메일이 비어 있으면 이 경로는 Service 호출 전에 실패합니다.
4. 08장의 register가 이메일·사번 중복과 부서 존재를 검사합니다. 07장의 Repository는 그 조회에 필요한 창구입니다.
5. PasswordEncoder로 비밀번호를 해시하고 User를 저장합니다. 저장한 User와 Department를 연결해 Employee를 저장합니다.
6. 트랜잭션이 정상 완료되면 두 저장을 확정합니다. 두 번째 저장에서 런타임 예외가 밖으로 전달되면 기본 롤백 규칙으로 두 변경을 되돌립니다. DB의 unique 제약은 동시 요청으로 중복 사전 검사를 함께 통과하는 경우에도 필요합니다.
7. EmployeeResponse.from이 공개할 값만 옮기고 Controller가 201 Created로 반환합니다. Entity 전체나 비밀번호 해시는 응답에 넣지 않습니다.
8. 입력 실패·부서 없음·중복 등은 15·16장의 오류 모델과 전역 예외 처리로 상태와 메시지를 맞춥니다. DB 제약 위반도 일관된 응답으로 변환하도록 실행 프로젝트에서 구현합니다.

직접 확인할 코드: [요청 DTO](./starter/17-dto-validation/EmployeeCreateRequest.java), [Controller](./starter/18-controller-employee/EmployeeController.java), [Service](./starter/08-employee-register/EmployeeService.register.java), [Repository](./starter/07-repository/EmployeeRepository.java), [응답 매핑](./starter/32-response-dto-mapping/ResponseDtoMapping.java).

성공 사례는 ‘두 행이 생기고 201 응답에 비밀번호가 없다’로 확인합니다. 실패 사례는 ‘존재하지 않는 부서로 요청하면 직원과 계정이 추가되지 않는다’로 확인합니다. 롤백 자체를 시험하려면 User 저장 이후 Employee 저장 단계에서 실패하는 사례를 따로 재현해야 합니다.

## 5. 조각 코드를 읽고 실행 프로젝트로 옮기는 순서

1. 파일 상단의 목표·초보자 안내를 읽고 입력 → 조건 검사 → 호출·저장 → 반환 순서를 말로 적습니다.
2. TODO 01처럼 번호가 붙은 빈칸을 하나씩 채웁니다. @표시, 메서드 호출, return이 맡은 역할을 구분합니다.
3. from은 이 예제에서 만든 변환 메서드 이름입니다. Java의 예약어가 아닙니다. Optional은 조회 결과가 없을 수 있음을 표현하고 orElseThrow는 없을 때 던질 예외를 정합니다. A::from은 from 메서드를 변환 함수로 전달하는 표기입니다.
4. 본인 프로젝트에서는 실제 패키지·import·의존성·생성자를 갖추고 파일명과 타입을 맞춥니다. 00장 설정만 복사했다고 모든 조각이 바로 연결되는 것은 아닙니다.
5. Controller 호출과 Service 인자·DTO가 서로 맞는지 확인합니다. 예를 들어 18장의 delete(employeeId)와 28장의 delete(employeeId, hardDelete)는 연습 관점이 다르므로 실제 삭제 정책에 맞춰 연결해야 합니다.
6. User의 ID와 Employee의 ID를 구분합니다. 34장의 @CurrentEmployee 선언만으로 직원 ID가 주입되지 않습니다. 로그인 계정으로 직원 정보를 조회하는 해석기 등 필요한 연결을 구현합니다.
7. 본인 프로젝트에서 테스트를 실행하고 실패 원인을 찾습니다. 답과 비교한 뒤 왜 그 기술을 쓰는지 한 문장으로 설명합니다.

빈칸을 채운 즉시 이해되지 않아도 됩니다. ‘입력 하나가 어디로 이동하는지’와 ‘어느 조건에서 멈추는지’를 먼저 찾으면 다음 코드의 역할이 보입니다.

## 6. 오늘 학습의 이해 확인

- 어떤 기술인가: 오늘 사용한 기술을 처음 듣는 사람에게 한 문장으로 설명할 수 있나요?
- 왜 필요한가: 이 기술이나 검사를 빼면 어떤 구체적인 문제가 생기나요?
- 어떻게 동작하는가: 요청이 통과하는 메서드와 저장·반환 순서를 가리킬 수 있나요?
- 무엇으로 확인했나: 성공 입력 하나와 실패 입력 하나, 실제 실행한 테스트 결과가 있나요?

이 질문에 답한 뒤 [학습 워크북 안내](./README.md)의 챕터 완료 기록으로 이어갑니다. 더 자세한 설계 선택은 [정답과 해설](./answers.md), 구현 범위는 [PRD/TRD](../company_work_system_PRD_TRD.md)와 대조합니다.
