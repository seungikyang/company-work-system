// 중학생 눈높이: 이 파일은 10개 기술 단원의 학습 자료입니다. 문제를 만드는 재료라고 생각하세요.
// ## 줄은 단원 정보이고, 그 아래 줄은 주제별 설명입니다. |로 칸을 나눠 quiz-core.js가 읽습니다.
// String.raw는 자료 속 역슬래시를 적은 대로 유지합니다. 괄호 안의 긴 글은 실행 명령이 아닌 학습 자료입니다.
globalThis.FUNDAMENTALS_SOURCES.push(String.raw`
## 10|URL과 문서 읽기|workbook|URL·문자열 처리|workbook-viewer.html
URLSearchParams|new URLSearchParams|주소의 쿼리 문자열을 이름별로 읽는 API|읽기 화면의 file 파라미터를 확인하는 상황|URLSearchParams로 file 값을 추출한다|쿼리를 단순 자르기로 처리해 인코딩이나 여러 값을 혼동한다|서로 다른 file 값으로 원하는 학습 자료를 선택하는지 확인한다
상대 URL 해석|new URL(|상대 경로를 기준 주소에 대해 해석하는 기능|폴더 접두사가 있는 워크북에서 자료 주소를 만드는 상황|상대 경로와 현재 주소를 URL 생성자에 전달한다|주소를 임의 결합해 폴더 경로가 누락될 수 있다|생성한 URL의 경로에 워크북 접두사가 유지되는지 확인한다
raw 쿼리 파라미터|sourceUrl.searchParams.set("raw", "1")|읽기 화면 대신 자료 원문을 요청하는 이 서버의 약속|Java 파일 내용을 읽기 화면에 표시하는 상황|원문 요청 URL의 raw 값을 1로 지정한다|원문 대신 읽기 화면 HTML을 가져오거나 연결이 반복될 수 있다|네트워크 응답이 리다이렉트 화면이 아닌 원문인지 확인한다
정규식 경로 검사|allowedSource.test(sourceFile)|허용한 자료 경로 형태와 입력을 대조하는 검사|외부 입력으로 받은 file 경로를 확인하는 상황|허용 패턴 검사와 상위 경로 거절을 수행한다|의도하지 않은 자료 경로를 요청할 수 있다|허용 자료와 상위 경로 입력의 처리 결과를 비교한다
pre 원문 표시|document.createElement("pre")|공백과 줄바꿈을 유지하는 HTML 텍스트 영역|코드 들여쓰기를 보존해 읽어야 하는 상황|pre 요소에 원문을 textContent로 넣는다|일반 문단 출력으로 코드의 들여쓰기와 줄 구분이 흐려진다|원문 파일과 표시된 코드의 줄바꿈·들여쓰기를 비교한다

## 11|Python 로컬 HTTP 서버|workbook|Python·HTTP|scripts/serve_workbook.py
루프백 바인딩|HOST = "127.0.0.1"|서버를 같은 컴퓨터에서 접근하는 주소에 연결하는 설정|학습 자료를 로컬 컴퓨터에서만 제공하는 상황|서버의 호스트를 127.0.0.1로 지정한다|모든 네트워크 인터페이스에 바인딩하면 접근 범위가 넓어진다|서버가 듣는 주소가 루프백인지 확인한다
HTTP GET 처리|def do_GET|요청 경로를 검사하고 파일 본문을 응답하는 처리 메서드|브라우저가 HTML이나 학습 파일을 가져오는 상황|do_GET에서 경로 확인 후 파일 응답을 수행한다|경로 확인 없이 파일 제공이 실행될 수 있다|공개 파일 GET은 성공하고 비공개 경로는 거절되는지 확인한다
HTTP HEAD 처리|def do_HEAD|GET과 같은 대상의 헤더를 본문 없이 확인하는 처리|파일 상태와 Content-Type만 점검하는 상황|do_HEAD에서도 GET과 동일한 경로 검사를 적용한다|GET과 HEAD에 서로 다른 접근 정책이 적용될 수 있다|HEAD의 상태·헤더와 본문이 없다는 점을 확인한다
Python pathlib 경로|Path(__file__).resolve().parent.parent|스크립트 파일 위치에서 저장소 루트를 계산하는 경로 처리|파일 제공 기준 디렉터리를 정하는 상황|스크립트의 물리 경로와 부모 디렉터리로 루트를 구한다|실행 위치에 따라 의도와 다른 파일 경로를 참조할 수 있다|계산한 루트가 실제 워크북 루트와 같은지 확인한다
HTTP Location 리다이렉트|self.send_header("Location", viewer_url)|응답 헤더로 클라이언트를 다른 주소로 안내하는 방식|Markdown 직접 요청을 공통 읽기 화면으로 연결하는 상황|리다이렉트 상태와 Location 헤더를 함께 보낸다|브라우저가 문서 읽기 화면으로 이동할 주소를 알지 못한다|원문 옵션 없는 문서 요청의 상태와 Location 값을 확인한다

## 12|Java 필드와 자료형|backend|Java|practice/starter/03-employee-entity/Employee.entity.java
Java Long 필드|private Long id;|정수 값을 객체 타입으로 보관하는 선언|아직 ID가 없는 객체와 저장된 ID를 표현하는 상황|ID 필드에 Long을 사용하고 null 가능성을 구분한다|박싱 타입의 null을 고려하지 않아 언박싱 오류가 날 수 있다|ID가 null인 상태와 값이 있는 상태를 나누어 확인한다
Java String 필드|private String employeeNumber;|문자열을 참조하는 Java 필드 선언|EMP-001 같은 사번 텍스트를 보관하는 상황|문자와 숫자가 섞인 사번을 String으로 선언한다|문자열 사번을 숫자로 해석해 접두사나 형식을 잃는다|접두사와 앞자리 0이 저장·조회 후에도 유지되는지 확인한다
Java LocalDate|private LocalDate hireDate;|시각과 시간대 없이 날짜를 표현하는 Java 타입|직원 입사일의 연월일을 표현하는 상황|입사일 필드에 LocalDate를 사용한다|필요하지 않은 시각 변환을 날짜 처리에 섞을 수 있다|입력한 연월일이 변경 없이 반환되는지 확인한다
Java boolean 반환|public boolean isActive()|조건의 참 또는 거짓을 반환하는 메서드 타입|호출자가 직원 활성 여부에 따라 분기하는 상황|판정 결과를 boolean으로 반환한다|상태 문자열을 직접 해석하는 코드가 호출부마다 반복된다|활성·비활성 상태에 대한 true·false 결과를 확인한다
Java enum 타입|public enum EmployeeStatus|허용된 이름 있는 상수 집합을 표현하는 타입|직원 상태 값의 오타를 줄이는 상황|상태 필드의 타입을 EmployeeStatus로 선언한다|자유 문자열 상태에 정의되지 않은 값이 섞인다|상태 분기에서 선언한 enum 상수만 사용하는지 확인한다

## 13|Java 객체와 메서드|backend|Java|practice/starter/03-employee-entity/Employee.entity.java
Java class|public class Employee|객체의 필드와 동작을 정의하는 틀|여러 직원 객체가 같은 구조를 공유하는 상황|Employee 클래스에 인사 필드와 변경 메서드를 정의한다|값과 동작이 흩어져 직원 객체의 책임이 불명확해진다|각 직원 객체가 같은 구조와 독립된 값을 갖는지 확인한다
Java private|private Department department;|클래스 밖에서 직접 접근할 수 없게 제한하는 접근 제어|부서 필드 변경을 정해진 메서드로 모으는 상황|필드를 private으로 두고 필요한 동작을 공개한다|호출부가 내부 필드를 무제한으로 바꿀 수 있다|외부 코드가 직접 필드 대입을 할 수 없는지 확인한다
Java new 연산|Employee e = new Employee();|클래스로 새 객체를 생성하는 표현|등록할 직원의 메모리 객체를 만드는 상황|new Employee로 객체를 만든 뒤 필요한 값을 채운다|객체 생성과 DB INSERT를 같은 동작으로 오해한다|new 직후 메모리 객체 생성과 실제 저장 호출을 구분한다
Java 정적 팩토리|public static Employee create|클래스 이름으로 호출하여 객체를 만드는 메서드|초기화 과정을 하나의 생성 진입점으로 모으는 상황|static create에서 필수값을 채우고 객체를 반환한다|생성 경로마다 초기화가 달라질 수 있다|팩토리 호출 결과에 전달한 필드들이 채워졌는지 확인한다
Java this 참조|this.department = department;|현재 객체를 가리켜 필드와 매개변수를 구분하는 참조|필드와 인자 이름이 같은 변경 메서드를 작성하는 상황|this.department에 매개변수 department를 대입한다|인자를 자기 자신에 대입해 객체 필드가 바뀌지 않을 수 있다|메서드 호출 후 대상 객체의 부서 참조가 바뀌는지 확인한다

## 14|Java 조건과 문자열|backend|Java|practice/starter/02-department-entity/Department.entity.java
Java null 검사|name != null|참조가 어떤 객체도 가리키지 않는지 확인하는 비교|선택적으로 전달된 부서명을 다루는 상황|문자열 메서드 호출 전에 null 여부를 확인한다|null에 메서드를 호출해 NullPointerException이 발생한다|null 입력에서도 정의한 변경 정책대로 동작하는지 확인한다
Java 논리 AND 단락 평가|name != null && !name.isBlank()|왼쪽 조건이 거짓이면 오른쪽 조건을 평가하지 않는 동작|null 검사와 문자열 공백 검사를 연결하는 상황|null 검사를 왼쪽에 두고 &&로 이어 붙인다|조건 순서를 바꾸면 null에서 문자열 메서드가 실행될 수 있다|null 입력 시 오른쪽 메서드가 호출되지 않는지 확인한다
String.isBlank|!name.isBlank()|문자열이 비었거나 공백 문자만 있는지 확인하는 기능|부서 이름에 공백만 입력되었는지 판단하는 상황|문자열의 isBlank 결과로 공백뿐인 입력을 구분한다|길이만 보고 공백뿐인 문자열을 정상 이름으로 취급한다|빈 값·공백뿐인 값·글자가 있는 값의 결과를 비교한다
String.trim|this.name = name.trim();|문자열 양끝의 지정 범위 공백 문자를 제거하는 연산|이름 앞뒤에 일반 공백이 붙은 입력을 정리하는 상황|trim의 반환 문자열을 필드에 대입한다|반환값을 버려 정리 전 문자열을 계속 사용할 수 있다|일반 앞뒤 공백은 없어지고 가운데 공백은 유지되는지 확인한다
Java 조건 분기 if|if (name != null|조건을 만족할 때만 코드 블록을 실행하는 제어문|이름을 전달한 요청에서만 이름을 변경하는 상황|변경 조건을 if에 넣고 필드 갱신을 블록 안에 둔다|값이 생략된 요청도 기존 값을 덮어쓸 수 있다|조건이 참일 때와 거짓일 때 필드 변경 여부를 비교한다

## 15|Java record와 제네릭|backend|Java|practice/starter/32-response-dto-mapping/ResponseDtoMapping.java
Java record DTO|public record EmployeeResponse(|데이터 구성 요소와 기본 접근자 등을 선언하는 Java 타입|응답 필드를 간결한 데이터 객체로 묶는 상황|record 헤더에 필요한 응답 구성 요소를 선언한다|필드 구조와 반복 접근자 구현이 여러 곳에서 어긋날 수 있다|record의 구성 요소와 생성·접근 코드가 일치하는지 확인한다
Java 메서드 반환 타입|public static EmployeeResponse from|메서드 결과의 타입을 호출자에게 명시하는 선언|엔티티를 응답 객체로 변환하는 상황|from 메서드가 EmployeeResponse를 반환하도록 선언한다|호출부가 반환 결과의 자료형을 잘못 취급한다|변환 결과를 EmployeeResponse 변수로 받을 수 있는지 확인한다
Java 제네릭 타입 매개변수|<E, R>|입력과 결과의 타입을 매개변수로 표현하는 문법|서로 다른 엔티티·DTO의 페이지 변환을 재사용하는 상황|변환 메서드에 입력 E와 출력 R을 선언한다|공통 처리에 Object와 강제 형변환이 늘어난다|서로 다른 입력·출력 타입에서도 타입 검사가 유지되는지 확인한다
Function 함수형 인터페이스|Function<E, R> mapper|E를 받아 R을 반환하는 변환 동작의 타입|페이지 원소를 응답 DTO로 바꾸는 함수를 전달하는 상황|인자에 Function<E,R> 형태의 매핑 함수를 전달한다|전달한 함수의 입력·결과 타입이 페이지와 맞지 않는다|매핑 함수가 각 E를 기대한 R로 변환하는지 확인한다
Java 유틸리티 생성자 숨김|private PageMapper() {}|직접 인스턴스를 만들지 않도록 생성자를 제한하는 방식|정적 메서드만 쓰는 변환 도구를 만드는 상황|유틸리티 클래스 생성자를 private으로 둔다|상태 없는 유틸리티의 불필요한 인스턴스가 생성된다|외부에서 new PageMapper를 호출할 수 없는지 확인한다

## 16|Gradle과 Spring Boot 의존성|backend|Gradle·Spring Boot|practice/starter/00-build-config/build.gradle.fragment
Gradle plugins 블록|plugins {|빌드에 사용할 플러그인을 선언하는 영역|Java와 Spring Boot의 빌드 기능을 준비하는 상황|plugins 안에 필요한 플러그인 ID를 선언한다|플러그인이 제공할 빌드 기능이 활성화되지 않는다|플러그인 선언과 생성된 빌드 작업을 확인한다
Gradle implementation|implementation 'org.springframework.boot:|앱 코드의 컴파일과 실행에 필요한 의존성 구성|MVC나 JPA 라이브러리를 앱에서 사용하는 상황|앱 의존성을 implementation에 선언한다|앱 컴파일에서 필요한 클래스를 찾지 못한다|앱 컴파일과 런타임 클래스 경로에 필요한 의존성이 있는지 확인한다
Gradle runtimeOnly|runtimeOnly '|실행 시 필요하지만 직접 컴파일 참조하지 않는 의존성 구성|예제에서 H2 JDBC 드라이버를 준비하는 상황|실행 전용 드라이버를 runtimeOnly에 선언한다|컴파일은 되어도 실행 시 DB 드라이버를 찾지 못한다|앱 실행에서 H2 연결이 초기화되는지 확인한다
Gradle testImplementation|testImplementation '|테스트 코드에 필요한 의존성을 선언하는 구성|JUnit과 Spring 테스트 도구를 테스트에서 사용하는 상황|테스트 라이브러리를 testImplementation에 둔다|테스트 코드에서 테스트 API를 찾지 못한다|테스트 소스가 필요한 도구를 참조해 컴파일되는지 확인한다
Gradle JUnit Platform 연결|useJUnitPlatform()|Gradle 테스트 작업에 JUnit Platform 사용을 지정하는 설정|JUnit 5 테스트를 빌드 작업으로 실행하는 상황|test 작업에서 useJUnitPlatform을 호출한다|테스트 검색·실행 설정이 사용한 테스트 엔진과 맞지 않을 수 있다|test 작업이 작성한 JUnit 5 테스트를 발견하는지 확인한다

## 17|YAML·H2·JPA 설정|backend|YAML·H2|practice/starter/00-build-config/application.yml.fragment
YAML 계층 들여쓰기|datasource:|키가 어느 상위 설정에 속하는지 나타내는 구조|DB 연결 설정을 spring 아래에 배치하는 상황|같은 단계의 키는 같은 깊이로 들여쓴다|키가 잘못된 계층에 놓여 설정이 적용되지 않는다|spring.datasource 아래 연결 속성이 놓였는지 확인한다
H2 메모리 JDBC URL|jdbc:h2:mem:|H2 데이터베이스를 메모리 모드로 여는 연결 주소|별도 DB 서버 없이 저장 연습을 하는 상황|jdbc:h2:mem 뒤에 학습 DB 이름을 지정한다|메모리 DB를 영구 저장소로 오해해 재시작 때 데이터를 잃는다|프로세스 재시작 전후 데이터 유지 여부를 확인한다
Hibernate ddl-auto|ddl-auto:|JPA 실행 시 스키마 처리 방식을 지정하는 설정|학습용 초기화와 운영용 검증 설정을 구분하는 상황|환경의 데이터 보존 요구에 맞게 create·validate 등을 선택한다|운영 환경에 재생성 설정을 적용해 데이터를 잃을 수 있다|연습 DB에서 시작 시 스키마 처리 결과를 확인한다
JPA SQL 출력 설정|show-sql:|실행되는 SQL을 콘솔에 표시하도록 하는 설정|객체 조회가 어떤 SQL을 만드는지 학습하는 상황|학습 환경에서 show-sql 출력을 설정한다|실제 실행 SQL을 확인하지 않고 조회 동작을 추측한다|조회 요청 뒤 콘솔에 나타난 SQL을 확인한다
환경 변수와 기본값|APP_SESSION_TIMEOUT:1800|외부 설정값이 없을 때 기본값을 쓰는 참조 표현|환경별 사용자 정의 세션 설정값을 읽는 상황|환경 변수 참조를 두고 실제 세션 적용 코드를 별도로 연결한다|app.* 선언만으로 서버 세션 만료가 자동 변경된다고 오해한다|환경 값 해석과 실제 세션 적용을 각각 검증한다

## 18|Spring 객체와 의존성 주입|backend|Spring DI|practice/starter/23-auth-login/AuthService.login.java
Spring Service 등록|@Service|업무 처리 클래스를 컴포넌트 탐색 대상으로 표시하는 선언|인증 Service를 Spring 관리 객체로 사용하는 상황|탐색 범위의 Service 클래스에 @Service를 선언한다|주입받을 Service가 컨테이너에 등록되지 않을 수 있다|앱 컨텍스트에서 AuthService를 주입할 수 있는지 확인한다
생성자 기반 의존성|@RequiredArgsConstructor|필수 의존성을 생성자 인자로 전달받는 구성 방식|UserRepository와 인코더를 Service에 연결하는 상황|필수 필드 생성자를 직접 작성하거나 Lombok으로 생성한다|필수 협력 객체 없이 Service가 만들어질 수 있다|테스트에서 가짜 의존성을 생성자로 전달할 수 있는지 확인한다
final 의존성 필드|private final UserRepository|생성 후 필드 참조를 다시 대입하지 않는 선언|주입받은 Repository 참조를 유지하는 상황|필수 협력 객체 필드를 final로 선언한다|협력 객체 참조가 실행 중 다른 값으로 바뀔 수 있다|생성자에서 필드가 초기화되고 이후 재할당이 없는지 확인한다
인터페이스 타입 의존|private final PasswordEncoder|구체 구현 대신 동작 계약 타입에 의존하는 방식|비밀번호 인코더 구현을 교체하거나 테스트 대역을 쓰는 상황|PasswordEncoder 타입으로 의존성을 선언한다|Service가 특정 구현 생성에 묶여 교체가 어려워진다|같은 인터페이스의 테스트 대역으로 Service를 실행해 본다
Servlet 의존성의 경계|세션 생성은 Controller 책임|인증 검증과 HTTP 세션 처리를 역할에 맞게 나누는 설계|비밀번호 검증을 웹 환경 없이 테스트하는 상황|Service는 검증 결과를 반환하고 Controller가 세션을 처리한다|인증 로직 단위 테스트에 불필요한 Servlet 환경이 필요해진다|HttpSession 없이 로그인 검증 Service를 테스트할 수 있는지 확인한다

## 19|Spring 설정과 Bean 등록|backend|Spring Config|practice/starter/33-config-beans/ConfigBeans.java
Configuration 클래스|@Configuration|Bean과 기능 설정을 모으는 Spring 구성 클래스 표시|공통 인코더와 MVC 확장을 등록하는 상황|설정 클래스에 @Configuration을 선언한다|작성한 구성 메서드가 설정으로 처리되지 않을 수 있다|설정 클래스가 앱 컨텍스트에 포함되는지 확인한다
Bean 팩토리 메서드|@Bean|반환 객체를 Spring 관리 Bean으로 제공하는 메서드 표시|직접 생성한 PasswordEncoder를 주입 가능하게 만드는 상황|인코더 생성 메서드에 @Bean을 붙인다|구성한 인코더를 필요한 Service에 주입할 수 없다|컨텍스트에서 PasswordEncoder Bean을 조회한다
인터셉터 등록|registry.addInterceptor(authCheckInterceptor)|작성한 인터셉터를 MVC 요청 처리에 연결하는 설정|인증 인터셉터가 실제 요청에서 실행되어야 하는 상황|addInterceptors에서 해당 인스턴스를 등록한다|클래스는 있어도 요청을 가로채지 못한다|보호 경로 요청에서 preHandle 호출을 확인한다
인자 해석기 등록|resolvers.add(currentUserArgumentResolver)|사용자 정의 인자 해석기를 MVC에 추가하는 설정|CurrentUser 인자가 Controller에서 채워져야 하는 상황|addArgumentResolvers에서 해석기를 추가한다|어노테이션만 선언하고 실제 인자 값은 만들어지지 않는다|현재 사용자 인자가 요청 정보로 채워지는지 확인한다
인터셉터 경로 제외|.excludePathPatterns(|지정 경로를 인터셉터 실행 대상에서 제외하는 구성|인증 전 로그인 요청은 접근 가능해야 하는 상황|로그인과 필요한 공개 자원을 제외 패턴으로 지정한다|로그인 진입점도 인증을 요구하는 순환이 생긴다|미로그인 상태로 공개 로그인 경로에 접근해 본다
`);
