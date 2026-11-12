// 중학생 눈높이: 이 파일은 10개 기술 단원의 학습 자료입니다. 문제를 만드는 재료라고 생각하세요.
// ## 줄은 단원 정보이고, 그 아래 줄은 주제별 설명입니다. |로 칸을 나눠 quiz-core.js가 읽습니다.
// String.raw는 자료 속 역슬래시를 적은 대로 유지합니다. 괄호 안의 긴 글은 실행 명령이 아닌 학습 자료입니다.
globalThis.FUNDAMENTALS_SOURCES.push(String.raw`
## 30|트랜잭션과 예외 전파|backend|Spring 트랜잭션|practice/starter/08-employee-register/EmployeeService.register.java
Transactional 경계|@Transactional|여러 저장 작업을 함께 확정하거나 취소하는 경계 선언|계정과 직원 저장이 한 기능으로 실행되는 상황|두 저장을 같은 Service 트랜잭션에 참여시킨다|두 번째 저장 실패 후 첫 번째 데이터만 남을 수 있다|두 번째 저장을 실패시킨 뒤 첫 번째 데이터도 남지 않는지 확인한다
RuntimeException 롤백|RuntimeException이 경계 밖으로 전달되면|기본 설정에서 런타임 예외 전파 시 트랜잭션을 되돌리는 규칙|저장 도중 런타임 예외가 발생한 상황|롤백이 필요한 실패를 트랜잭션 경계 밖으로 전달한다|예외를 삼키고 정상 반환하여 변경이 커밋될 수 있다|실패 뒤 별도 조회로 두 저장 결과가 없는지 확인한다
rollbackFor 설정|rollbackFor = Exception.class|기본 롤백 규칙을 예외 타입에 맞게 확장하는 설정|checked 예외도 업무 전체를 취소해야 하는 상황|요구한 예외에 맞춰 rollbackFor를 지정한다|checked 예외가 났으니 항상 자동 롤백된다고 오해한다|지정한 checked 예외를 발생시켜 실제 롤백 여부를 확인한다
Spring 트랜잭션 프록시|Spring 프록시의 트랜잭션 경계|관리 객체를 호출할 때 트랜잭션 처리를 둘러싸는 방식|어노테이션을 붙였는데 경계가 적용되지 않는 상황|프록시를 거치는 호출과 실제 트랜잭션 참여를 확인한다|같은 객체 내부 호출도 새 어노테이션 설정이 적용된다고 오해한다|호출 경로별 트랜잭션 적용 여부와 저장 결과를 비교한다
Optional.orElseThrow|.orElseThrow(() ->|조회 값이 없을 때 제공한 예외를 던지는 연산|부서 조회 결과가 없으면 등록을 멈춰야 하는 상황|Optional에 예외 생성 함수를 전달한다|없는 조회 결과로 후속 객체 생성을 계속 진행한다|부재일 때 예외가 발생하고 이후 저장은 호출되지 않는지 확인한다

## 31|DTO 매핑과 직렬화 경계|backend|DTO·JPA|practice/starter/32-response-dto-mapping/ResponseDtoMapping.java
응답 필드 선택|필드 선택, 직렬화, 버전 관리|외부에 공개할 속성만 응답 모델에 포함하는 설계|계정 정보를 포함하는 직원 응답을 만드는 상황|비밀번호 등 내부 값은 응답 구성 요소에서 제외한다|저장 엔티티의 민감한 필드가 JSON으로 노출된다|직원 JSON 응답에 비밀번호 필드가 없는지 확인한다
from 매핑 메서드|public static EmployeeResponse from(Employee e)|엔티티를 응답 객체로 변환하는 명시적인 진입점|여러 조회에서 같은 응답 형태를 만들어야 하는 상황|필드 변환을 from 같은 공통 메서드에 모은다|동일 응답 타입의 매핑이 호출 위치마다 달라진다|같은 엔티티의 각 조회 경로에서 응답 필드를 비교한다
연관 객체 평탄화|e.getDepartment().getName()|연관 객체의 일부 값을 단순 응답 필드로 옮기는 방식|직원 응답에 부서 전체 대신 이름만 필요한 상황|departmentName에 부서 이름을 매핑한다|불필요하게 중첩된 연관 구조를 응답에 담는다|응답의 부서 정보가 명세한 단순 필드인지 확인한다
지연 로딩과 DTO 변환 시점|지연 로딩 관계를 읽는 시점|연관 데이터에 접근 가능한 범위에서 응답을 만드는 원칙|컨텍스트 종료 후 연관 접근이 실패할 수 있는 상황|필요한 연관을 조회하고 적절한 영속성 범위에서 변환한다|응답 직렬화 단계에서 지연 로딩 오류가 발생할 수 있다|웹 계층의 추가 조회에 기대지 않고 DTO가 완성되는지 확인한다
DTO와 Entity 의존 방향|Entity 가 DTO 를 알면|도메인 모델이 외부 응답 구조에 묶이지 않게 하는 경계|API 응답 형태를 바꾸어도 저장 모델을 유지하려는 상황|응답 변환은 외부에서 수행하고 Entity에 응답 책임을 넣지 않는다|응답 변경 때마다 도메인 모델도 바꿔야 할 수 있다|Entity에서 응답 DTO를 생성하는 의존이 없는지 검토한다

## 32|BCrypt와 PasswordEncoder|backend|비밀번호 해시|practice/starter/23-auth-login/AuthService.login.java
비밀번호 단방향 해시|BCrypt|원문 복원을 목적으로 하지 않는 비밀번호 검증용 변환|계정의 비밀번호를 DB에 보관하는 상황|원문 대신 적절한 비밀번호 해시 결과를 저장한다|DB 유출 시 원문 비밀번호가 바로 드러난다|저장값이 원문과 다르고 검증 함수로만 비교되는지 확인한다
PasswordEncoder.matches|passwordEncoder.matches|원문 비밀번호와 저장된 해시의 일치 여부를 검사하는 API|로그인 시 입력 비밀번호를 검증하는 상황|matches에 원문과 저장 해시를 순서대로 전달한다|두 문자열을 단순 비교해 정상 비밀번호를 거절할 수 있다|맞는 원문은 통과하고 다른 원문은 실패하는지 확인한다
PasswordEncoder.encode|새 비밀번호를 해시해 반영|원문을 저장할 수 있는 비밀번호 해시로 만드는 API|비밀번호 변경에서 새 값을 보관하는 상황|새 비밀번호를 encode한 결과로 기존 해시를 교체한다|변경 경로에서 원문을 그대로 저장할 수 있다|변경 후 새 원문은 검증되고 이전 원문은 거절되는지 확인한다
BCrypt salt|같은 salt 로 해시한 뒤 비교|해시 계산에 부가값을 사용하여 결과의 단순 일치를 피하는 원리|같은 비밀번호의 새 해시와 기존 해시가 다른 상황|재해시 문자열 동등 비교 대신 저장 해시를 이용해 matches한다|같은 원문이면 해시 문자열도 항상 같다고 가정한다|같은 원문의 서로 다른 해시가 모두 matches를 통과하는지 확인한다
인증 실패 메시지 통일|같은 메시지 + 같은 status(401)|없는 계정과 비밀번호 오류를 외부 응답에서 구별하지 않는 처리|로그인 실패로 계정 존재를 추정하려는 상황|두 실패에 같은 상태와 일반 실패 문구를 사용한다|실패 응답만으로 가입한 이메일을 추정할 수 있다|없는 이메일과 틀린 비밀번호 응답을 비교한다

## 33|Servlet 세션 API|backend|Servlet·HttpSession|practice/starter/23-auth-login/AuthController.java
getSession(true)|request.getSession(true)|기존 세션이 없으면 새 세션을 생성하여 얻는 호출|로그인 검증 성공 뒤 상태를 저장하는 상황|성공한 인증 뒤 생성 가능한 방식으로 세션을 구한다|세션이 없는데 있다고 가정해 속성 저장이 실패한다|세션 없는 로그인 성공 시 새 세션이 생기는지 확인한다
getSession(false)|request.getSession(false)|기존 세션만 얻고 없으면 null을 반환하는 호출|로그아웃을 위해 불필요한 새 세션을 만들지 않는 상황|기존 세션만 조회하고 null 여부를 처리한다|로그아웃 요청이 오히려 새 세션을 생성할 수 있다|세션 없는 로그아웃에서 새 세션이 생성되지 않는지 확인한다
세션 setAttribute|session.setAttribute("USER_ID", response.userId())|서버 세션에 이름으로 값을 보관하는 API|후속 요청에 사용할 인증 주체를 기록하는 상황|인증으로 확인한 사용자 ID를 세션 속성에 저장한다|후속 요청에서 로그인 사용자 정보를 찾지 못한다|동일 세션 요청에서 저장한 USER_ID를 읽는지 확인한다
SessionAttribute 바인딩|@SessionAttribute("USER_ID")|세션 속성을 Controller 인자로 받는 MVC 선언|내 비밀번호 변경 주체를 현재 세션에서 얻는 상황|서버 세션의 USER_ID를 메서드 인자로 바인딩한다|클라이언트가 보낸 임의 사용자 ID를 인증 주체로 신뢰한다|요청 ID를 바꾸어도 세션의 사용자 기준으로 처리되는지 확인한다
세션 ID 재발급|로그인 전 세션 ID 를 그대로 쓰지 않도록 재발급|인증 성공 시 기존 세션 식별자를 교체하는 방어|인증 이전부터 세션이 있었던 상황|로그인 성공 후 changeSessionId 등으로 식별자를 변경한다|로그인 이전 식별자가 인증 후에도 유지된다|로그인 전후 세션 ID가 달라지는지 확인한다

## 34|Interceptor와 인자 해석기|backend|Spring MVC 확장|practice/starter/34-current-user-interceptor/CurrentUserAndInterceptor.java
사용자 정의 어노테이션|public @interface CurrentUser|코드 요소에 메타데이터 표식을 선언하는 Java 문법|현재 사용자 주입 대상 인자를 표시하는 상황|CurrentUser를 선언하고 처리 해석기를 함께 구현한다|어노테이션 선언만으로 값이 자동 주입된다고 착각한다|표식과 실제 해석 로직이 연결되는지 확인한다
RetentionPolicy.RUNTIME|@Retention(RetentionPolicy.RUNTIME)|실행 중 어노테이션 정보를 읽을 수 있도록 보존하는 설정|인자 해석기가 요청 처리 시 표식을 확인하는 상황|실행 시 판별할 표식에 RUNTIME 보존 정책을 지정한다|실행 중 검사할 어노테이션 정보가 남지 않을 수 있다|런타임 인자 검사에서 표식이 감지되는지 확인한다
Interceptor preHandle|public boolean preHandle|Controller 실행 전에 공통 요청 처리를 하는 지점|인증 없는 보호 요청을 먼저 거절하는 상황|preHandle에서 기존 세션과 인증 속성을 확인한다|Controller마다 로그인 확인을 반복하거나 빠뜨릴 수 있다|인증 없는 요청에서 Controller 진입이 차단되는지 확인한다
supportsParameter|public boolean supportsParameter|해석기가 처리할 인자 타입과 표식을 판별하는 메서드|CurrentUser가 붙은 Long 인자만 처리하는 상황|어노테이션 유무와 인자 타입을 함께 검사한다|관계없는 인자를 해석하려다가 잘못된 값을 주입한다|표식이나 타입이 다른 인자는 선택되지 않는지 확인한다
resolveArgument|public Object resolveArgument|선택된 Controller 인자의 실제 값을 만들어 반환하는 메서드|세션에서 현재 계정 ID를 꺼내 주입하는 상황|세션 값을 확인하고 기대한 인자 타입으로 반환한다|해석 대상은 선택했지만 인자 값은 만들어지지 않는다|Controller가 받은 ID와 세션에 저장된 ID를 비교한다

## 35|Spring Security 확장 설정|extension|Spring Security|practice/starter/33-config-beans/ConfigBeans.java
SecurityFilterChain|public SecurityFilterChain filterChain|요청 보안 필터의 정책을 구성하는 Spring Security 구성|예제의 2차 보안 단계로 확장하는 상황|관련 의존성과 인증 방식에 맞춰 필터 체인을 실제 구성한다|주석으로 남은 미리보기만으로 보안이 활성화됐다고 오해한다|확장 프로젝트에서 필터 체인 Bean과 보호 요청을 확인한다
메서드 보안 활성화|@EnableMethodSecurity|PreAuthorize 등 메서드 보안 처리를 활성화하는 설정|역할 어노테이션으로 접근을 검사하는 상황|보안 의존성과 EnableMethodSecurity 설정을 함께 준비한다|어노테이션 글자만 있고 실제 권한 검사가 실행되지 않을 수 있다|권한이 다른 사용자로 보호 메서드를 호출해 차이를 확인한다
requestMatchers|.requestMatchers("/api/admin/**")|특정 요청 경로에 보안 정책을 연결하는 설정|관리자 URL 묶음에 같은 권한 기준을 적용하는 상황|경로 매처와 역할 제한을 함께 지정한다|요청 경로와 정책이 어긋나 의도하지 않은 접근을 허용한다|대상 경로와 인접 비대상 경로의 적용 정책을 비교한다
permitAll 접근 정책|.permitAll()|대상 요청에 인증을 요구하지 않는 접근 설정|로그인 진입점에 아직 인증하지 않은 사용자가 접근하는 상황|인증을 획득하는 공개 경로에 permitAll을 지정한다|로그인 경로가 인증을 요구하여 접근 순환이 생긴다|미인증 상태로 로그인 경로에 진입할 수 있는지 확인한다
CSRF 설정 검토|.csrf(csrf -> csrf.disable())|쿠키 기반 위조 변경 요청에 대한 방어 정책 구성|주석의 보안 미리보기를 세션 폼 앱으로 확장하는 상황|disable을 그대로 복사하지 않고 CSRF 토큰 검증을 구성한다|사용자 쿠키를 악용한 외부 변경 요청을 허용할 수 있다|세션 폼에서 토큰 없는 변경 요청은 거절되는지 확인한다

## 36|Thymeleaf 템플릿|backend|Thymeleaf|practice/starter/35-thymeleaf-views/ThymeleafViews.md
서버 Model과 뷰 이름|return "employee/list"|모델 데이터와 템플릿 이름으로 HTML 응답을 만드는 흐름|직원 데이터를 서버에서 화면으로 렌더링하는 상황|Controller가 Model에 값을 넣고 뷰 이름을 반환한다|뷰 이름을 JSON 응답 문자열처럼 취급할 수 있다|렌더링된 HTML에 모델의 직원 데이터가 포함되는지 확인한다
th:text 이스케이프|th:text|값을 HTML 코드로 실행하지 않고 텍스트로 출력하는 속성|사용자 입력이 포함된 이름이나 공지를 표시하는 상황|일반 사용자 문자열에는 th:text를 사용한다|원문 HTML 출력으로 태그나 스크립트가 해석될 수 있다|태그 모양 문자열이 실행되지 않고 문자로 표시되는지 확인한다
Thymeleaf 반복 처리|emp :|목록의 원소마다 템플릿 요소를 반복하는 문법|직원 목록을 표의 여러 행으로 표시하는 상황|th:each에 원소 변수와 목록 표현식을 연결한다|목록 데이터가 한 행으로만 표시될 수 있다|목록 원소 수와 출력된 데이터 행 수를 비교한다
th:field 폼 바인딩|th:field|폼 객체의 필드를 입력 요소와 연결하는 기능|화면 입력과 요청 DTO의 필드명을 맞추는 상황|폼 객체와 해당 입력 필드를 함께 바인딩한다|화면 입력 이름이 DTO와 달라 값 전달이 어긋난다|입력·제출·검증 실패 재표시에서 같은 필드가 유지되는지 확인한다
Thymeleaf fragment|fragments/header :: header|공통 템플릿 조각을 이름으로 재사용하는 방식|여러 화면에서 같은 헤더를 사용하는 상황|헤더 fragment를 정의하고 사용처에서 삽입하거나 교체한다|동일 헤더가 복제되어 화면마다 수정 내용이 달라진다|공통 헤더 수정이 이를 사용하는 화면들에 반영되는지 확인한다

## 37|JUnit과 Spring 테스트 구성|backend|JUnit 5·Spring Test|practice/starter/21-test-flow/LeaveFlowTest.java
JUnit Test 표시|@Test|테스트 실행기가 실행할 메서드를 표시하는 선언|성공·실패 검증을 반복 가능한 테스트로 작성하는 상황|검증 메서드에 Test를 지정한다|작성한 메서드가 테스트로 발견되지 않을 수 있다|실행 결과에 해당 테스트 이름과 결과가 나타나는지 확인한다
JUnit DisplayName|@DisplayName(|테스트 결과에 나타낼 설명 이름을 지정하는 선언|실패한 테스트의 업무 의도를 빠르게 읽는 상황|검증 의도를 DisplayName 문자열로 작성한다|메서드 이름만으로 실패 의미를 파악하기 어려울 수 있다|테스트 리포트의 표시 이름을 확인한다
SpringBootTest 컨텍스트|@SpringBootTest|Spring Boot 앱의 테스트 컨텍스트를 준비하는 구성|Controller·Service·저장을 함께 통합 검증하는 상황|통합 테스트에 SpringBootTest와 필요한 테스트 설정을 둔다|필요 Bean이 없는 범위에서 통합 흐름을 실행하려 한다|테스트에서 핵심 Bean이 주입되고 요청 흐름이 동작하는지 확인한다
AutoConfigureMockMvc|@AutoConfigureMockMvc|테스트 컨텍스트에서 MockMvc 구성을 준비하는 설정|실제 소켓 없이 MVC 요청을 실행하는 상황|통합 테스트에 AutoConfigureMockMvc를 연결한다|MockMvc를 자동 주입받을 구성이 빠질 수 있다|주입한 MockMvc로 매핑된 요청을 수행해 본다
테스트 트랜잭션 롤백|각 메서드 후 자동 롤백|트랜잭션 테스트 변경을 기본적으로 되돌리는 격리 방식|테스트 데이터가 다음 테스트에 영향을 주지 않아야 하는 상황|테스트 트랜잭션의 기본 롤백과 커밋 검증 한계를 구분한다|롤백 테스트만으로 실제 커밋 이후 동작까지 검증했다고 오해한다|종료 후 데이터 정리와 커밋 전용 검증의 필요성을 확인한다

## 38|JPA 감사 필드와 DB 제약|backend|JPA Auditing·DB 제약|practice/starter/02-department-entity/Department.entity.java
CreatedDate 감사 시각|@CreatedDate|최초 저장 시 생성 시각을 채우는 감사 필드 표시|각 부서 데이터의 생성 시점을 기록하는 상황|CreatedDate와 Auditing 활성화·리스너를 연결한다|생성 시각 필드가 자동으로 채워지지 않는다|최초 저장 후 createdAt에 시각이 있는지 확인한다
LastModifiedDate 감사 시각|@LastModifiedDate|엔티티 변경 시 수정 시각을 기록하는 감사 필드 표시|부서 설명의 마지막 변경 시점을 추적하는 상황|수정 시각 필드에 LastModifiedDate를 연결한다|수정해도 마지막 변경 시각을 알 수 없다|변경 전후 updatedAt 값을 비교한다
AuditingEntityListener|@EntityListeners(AuditingEntityListener.class)|엔티티 생명주기의 감사 콜백을 연결하는 리스너|감사 어노테이션이 실제 저장 과정에 반영되어야 하는 상황|엔티티에 감사 리스너와 전역 Auditing 설정을 함께 구성한다|감사 필드 선언만으로 값이 채워진다고 오해한다|저장과 변경에서 감사 콜백 결과를 확인한다
Column updatable=false|updatable = false|JPA의 일반 갱신 SQL에서 해당 컬럼 변경을 제외하는 매핑|생성 시각이 이후 수정으로 바뀌지 않아야 하는 상황|생성 시각 컬럼을 updatable=false로 설정한다|일반 수정 시 생성 시각까지 바뀔 수 있다|다른 필드 수정 뒤 생성 시각이 유지되는지 확인한다
DB unique 제약|@UniqueConstraint|같은 컬럼 값의 중복 저장을 DB에서 막는 제약|동시 등록 요청이 같은 부서명을 저장하려는 상황|매핑과 실제 DB 스키마에 유일성 제약을 유지한다|사전 검사만 통과한 경쟁 요청이 중복 데이터를 저장한다|동일 부서명 두 건을 실제 DB에 넣어 제약 위반을 확인한다

## 39|MockMvc 요청과 JSON 검증|backend|MockMvc·Jackson|practice/starter/21-test-flow/LeaveFlowTest.java
MockMvc perform|mockMvc.perform(|테스트에서 MVC 요청 처리를 실행하는 API|휴가 신청 POST를 테스트 코드에서 호출하는 상황|요청 빌더를 perform에 전달한다|메서드 호출 단위 검사만으로 웹 매핑 오류를 놓친다|요청이 기대한 Controller 경로와 응답으로 이어지는지 확인한다
JSON Content-Type|.contentType(MediaType.APPLICATION_JSON)|요청 본문이 JSON임을 알리는 미디어 타입 설정|JSON 문자열을 DTO로 바인딩하는 요청을 만드는 상황|테스트 요청의 Content-Type을 application/json으로 설정한다|본문 변환기가 입력 형식을 잘못 해석하거나 요청을 거절한다|올바른 타입의 JSON 요청이 DTO로 변환되는지 확인한다
Jackson JSON 직렬화|objectMapper.writeValueAsString(body)|Java 값을 JSON 문자열로 바꾸는 Jackson API|Map으로 만든 테스트 입력을 요청 본문에 넣는 상황|ObjectMapper로 입력 객체를 직렬화한다|수동 문자열 조합의 따옴표나 필드 타입 오류가 생긴다|생성 JSON을 파싱해 원래 필드 값과 비교한다
MockMvc jsonPath 단언|jsonPath("$.status")|JSON 응답의 특정 경로 값을 비교하는 검증|성공 응답의 status 필드까지 확인하는 상황|jsonPath와 기대 값을 연결해 본문을 단언한다|HTTP 성공만 보고 잘못된 응답 데이터를 놓친다|응답 status를 잘못 반환하면 테스트가 실패하는지 확인한다
MockHttpSession 재사용|.session(adminSession)|동일 사용자 상태를 테스트 요청에 연결하는 방식|로그인 이후 관리자 요청을 이어서 보내는 상황|로그인에서 얻은 세션을 후속 요청에 붙인다|로그인 성공과 후속 요청이 서로 다른 인증 상태로 실행된다|세션 연결 요청과 연결하지 않은 요청의 인증 결과를 비교한다
`);
