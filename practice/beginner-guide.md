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


## 7. 파일을 열었을 때 어디부터 읽나요?

코드 전체를 위에서부터 외우기보다 아래 순서로 필요한 파일을 찾아갑니다. Starter에는 중요한 문장 바로 앞에 설명을 넣었고, Hard에도 같은 기술의 개념·목적·흐름을 넣었습니다. Hard에서는 Starter를 덮고 조건과 호출 순서를 스스로 재구성합니다.

| 알고 싶은 동작 | 실제 파일 | 먼저 찾을 이름 | 이 기술이 필요한 이유 |
|---|---|---|---|
| 답안을 적고 다시 열기 | index.html | loadState, chapterAnswer의 input 이벤트, saveState | 브라우저 입력·메모리 상태·저장값을 연결 |
| 선택 챕터 안내 바꾸기 | index.html | renderSelectedChapter | 목차의 같은 번호에서 제목·링크·완료 기준을 함께 가져오기 |
| 문제와 정답 함께 읽기 | problems.html | loadProblems, parseWorkbookSections, renderProblems | 기존 문제·해설 파일을 번호로 조합해 중복 작성 줄이기 |
| Java·Markdown 원문 읽기 | workbook-viewer.html | loadDocument, renderDocument | 파일을 내려받지 않고 제목·목차·본문으로 읽기 |
| 브라우저에 자료 보내기 | scripts/serve_workbook.py | main, do_GET, prepare_workbook_path | HTTP 요청을 허용된 학습 파일의 응답으로 바꾸기 |
| 깨진 링크 찾기 | scripts/verify_workbook.py | main, require | 여러 문서를 수정한 뒤 같은 기준으로 오류 확인 |
| 실행 위치 확인하기 | scripts/assert_repo_root.sh | physical_pwd, physical_root | 다른 복사본에서 수정하는 실수 방지 |

이 표의 HTML·scripts 경로는 편집기에서 여는 저장소 상대 경로입니다. 로컬 학습 서버는 scripts 폴더를 공개하지 않으므로 해당 파일은 브라우저 문서 읽기 화면에서 열리지 않습니다.

## 8. 답안 한 줄이 저장되는 과정

이 예시는 현재 워크북에 실제 구현된 동작입니다. 내 프로젝트의 직원 정보가 저장되는 DB 흐름과 구분해서 따라갑니다.

1. 메인 화면에서 08장을 선택하고 답안 입력창에 문장을 적습니다.
2. 브라우저가 input 이벤트를 발생시킵니다. index.html의 chapterAnswer.addEventListener 안에서 event.target.value가 지금 입력한 문장을 읽습니다.
3. state.workbook.answers[number]에 문장을 넣습니다. number가 "08"이면 08장 답안만 바뀝니다. state는 실행 중인 페이지 메모리에 있는 객체입니다.
4. saveState가 JSON.stringify(state)로 문자열을 만들고 localStorage.setItem에 전달합니다. 이 경로에는 HTTP 요청이 없습니다.
5. renderChapterWorkbookStatus와 updateDashboard가 입력 상태·완료 개수를 화면에 반영합니다. 저장과 화면 갱신은 서로 다른 작업입니다.
6. 새로고침하면 페이지 메모리는 새로 만들어집니다. loadState가 남아 있는 저장 문자열을 읽고 renderState가 입력창과 선택값을 복원합니다.

직접 확인: 시험용 답안을 입력한 뒤 09장으로 이동했다가 08장으로 돌아옵니다. 이어서 새로고침하고 같은 답안이 남는지 확인합니다. 답안을 모두 지우면 그 챕터의 비교 완료 표시도 해제됩니다. 기존 학습 기록이 있으면 다른 브라우저 프로필에서 연습하세요.

저장이 막히면 catch가 오류를 받아 안내합니다. try/catch는 실패할 수 있는 작업을 시도하고 실패했을 때 대신 할 처리를 정하는 문법입니다. 화면에 문장이 보인다는 사실만으로 다음 실행에도 보존됐다고 판단하지 말고 새로고침 후 결과까지 확인합니다.

## 9. 전체 문제를 열면 파일 세 개를 읽는 이유

문제 본문은 practice/problems.md, 해설은 practice/answers.md, 실제 코드 경로는 index.html 목차에 있습니다. 같은 정보를 여러 파일에 새로 복사하면 한쪽만 수정했을 때 내용이 어긋나므로 기존 자료를 합쳐 보여줍니다.

1. loadProblems가 Promise.all로 세 파일의 요청을 시작합니다. Promise는 나중에 성공 값이나 실패 이유를 받는 비동기 작업을 표현합니다.
2. fetchText가 URL을 만들고 Markdown에는 raw=1을 붙입니다. raw가 없으면 서버는 문서 읽기 화면으로 이동시키므로 카드에 필요한 원문을 받으려면 이 구분이 필요합니다.
3. await는 응답 준비 후 해당 함수의 다음 처리를 이어갑니다. 기다리는 동안 브라우저 전체를 멈추는 명령은 아닙니다.
4. 응답 상태가 성공인지 response.ok로 검사하고 본문을 문자열로 읽습니다. fetch는 404 응답만으로 실패 예외를 발생시키지 않으므로 명시적인 상태 검사가 필요합니다. [MDN fetch 설명](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API/Using_Fetch)
5. parseWorkbookSections가 번호 있는 제목으로 문제와 해설을 나눕니다. parseCurriculum은 목차의 data-chapter 등 속성을 읽습니다.
6. renderProblems가 번호로 세 자료를 맞추고 40개 카드와 실제 파일 링크를 만듭니다. 정답 details는 처음부터 펼쳐져 있으며 버튼으로 접거나 펼칠 수 있습니다.
7. 요청이나 자료 대응 검사에 실패하면 catch에서 실패 메시지와 복귀 안내를 표시합니다.

코드 링크를 클릭한 다음에는 workbook-viewer.html의 loadDocument를 따라갑니다. 이 화면은 원문을 textContent로 넣어 문자열 속 HTML을 실행하지 않습니다. pre 요소는 공백·줄바꿈을 보존합니다. Markdown 제목으로 구획과 목차를 만들지만 본문은 원문으로 표시하므로 모든 Markdown 문법을 꾸며 주는 편집기로 생각하지 않아도 됩니다.

## 10. Java 문법을 실제 업무 코드로 읽기

| 표기 | 쉬운 뜻 | 이 저장소에서 읽을 예 |
|---|---|---|
| private final EmployeeService | 이 객체가 사용할 Service 참조를 보관하고 다시 대입하지 않음 | 18장 Controller의 생성자 주입 |
| new User() | 메모리에 User 객체 하나 만들기 | 01장 create; 그 자체로 DB 저장은 아님 |
| this.status | 현재 객체의 status 필드 | 04·06장의 상태 변경 |
| return | 결과를 호출한 쪽에 돌려주고 메서드를 끝내기 | Service에서 응답 DTO 반환 |
| throw | 현재 처리를 중단하고 예외를 호출한 쪽으로 전달하기 | 부서 없음·권한 없음·상태 오류 |
| Optional, orElseThrow | 없을 수도 있는 조회값과 없을 때의 처리 | 08장 부서 조회 |
| () -> ... | 필요할 때 실행할 함수를 값처럼 전달하기 | 조회 실패 시 만들 예외 지정 |
| EmployeeResponse::from | from 메서드를 변환 함수로 전달하기 | 09장 조회 결과의 각 직원을 응답 DTO로 변환 |
| !, &&, 논리 OR 연산자 | 부정, 둘 다 참, 하나 이상 참 | 29장 본인·관리자 접근 검사 |
| record | 전달할 데이터와 접근자를 간결하게 선언하기 | 23·25장의 요청·응답 타입 |

어노테이션을 읽을 때는 이름 옆에 ‘누가 이 표시를 처리하는가’를 적어 보세요. Lombok은 컴파일 때 생성자·접근자를 만들고, Spring MVC는 요청 매핑을 읽고, Validation은 입력 제약을 검사하며, JPA는 객체와 DB 매핑을 처리합니다. 서로 다른 도구가 담당하므로 어노테이션 하나로 모든 단계가 실행되지는 않습니다.

### 조각들을 연결할 때 꼭 대조할 네 가지

- DTO 접근자: 일반 클래스의 Lombok getter는 getName(), record의 접근자는 name()입니다. 예를 들어 23장 AuthService의 getEmail()과 AuthController에 선언된 LoginRequest record는 그대로 연결되지 않습니다. 실행 프로젝트에서 DTO 형식을 선택하고 사용처를 통일합니다.
- 식별자: User의 ID는 로그인 계정, Employee의 ID는 인사 정보 식별자입니다. 34장의 CurrentUser 해석기는 계정 ID를 읽으며 CurrentEmployee·CurrentUserRole은 선언만 존재합니다. 필요한 해석기와 33장의 MVC 등록까지 구현해야 합니다.
- 권한 이름: 01장의 역할과 27장의 APPROVER 표기는 실행 프로젝트에서 권한 모델을 맞춰야 하는 지점입니다. PRD/TRD를 기준으로 역할 이름·인증 정보·검사 표현식을 통일하고 역할 권한과 문서에 지정된 결재자 여부를 각각 확인합니다.
- 메서드와 조회 조건: 18·28장의 삭제 인자 개수, 09장의 이름+부서 복합 검색처럼 연결하거나 완성해야 하는 부분이 있습니다. 메서드 이름이 같다는 사실만으로 입력과 업무 규칙이 모두 맞지는 않습니다.

### 실패했을 때 어느 파일로 돌아가나요?

- 필수값·문자열 형식이 잘못되면 요청 DTO와 Controller의 검증 연결을 봅니다. MVC의 검증 예외는 검증 방식에 따라 다를 수 있습니다. Spring Framework 6.1의 메서드 검증은 HandlerMethodValidationException을 사용할 수 있으므로 실행 버전과 검증 경로에 맞게 핸들러를 구성합니다. [Spring MVC 검증 설명](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)
- 부서가 없거나 중복이면 Service와 Repository 조건을 봅니다. throw 뒤에는 정상 저장·반환이 이어지지 않는지 확인합니다.
- 저장 도중 실패하면 트랜잭션 경계를 봅니다. 기본 프록시 방식의 외부 호출과 롤백 규칙을 확인해야 하며 같은 메서드 안에 저장 코드를 나열한 것만으로 원자성이 생기지는 않습니다. [Spring 트랜잭션 설명](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)
- 성공 응답에 내부 정보가 섞이면 32장의 DTO 변환에서 복사하는 필드를 봅니다. 응답 변환은 조회·저장과 구분해서 점검합니다.
