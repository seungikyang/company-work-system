// 중학생 눈높이: 이 파일은 10개 기술 단원의 학습 자료입니다. 문제를 만드는 재료라고 생각하세요.
// ## 줄은 단원 정보이고, 그 아래 줄은 주제별 설명입니다. |로 칸을 나눠 quiz-core.js가 읽습니다.
// String.raw는 자료 속 역슬래시를 적은 대로 유지합니다. 괄호 안의 긴 글은 실행 명령이 아닌 학습 자료입니다.
globalThis.FUNDAMENTALS_SOURCES.push(String.raw`
## 20|Spring MVC 입력 매핑|backend|Spring MVC|practice/starter/18-controller-employee/EmployeeController.java
RequestMapping 공통 경로|@RequestMapping(|Controller의 여러 메서드에 공통 URL 경로를 지정하는 선언|직원 API의 공통 접두사를 묶는 상황|클래스의 RequestMapping과 메서드 경로를 함께 설계한다|공통 경로와 개별 경로를 혼동해 다른 URL로 매핑한다|최종 요청 URL이 의도한 Controller 메서드로 연결되는지 확인한다
RequestBody 역직렬화|@RequestBody EmployeeCreateRequest|HTTP 본문을 요청 객체로 변환해 받는 선언|JSON 직원 등록 내용을 Java DTO로 읽는 상황|DTO 인자에 RequestBody를 연결한다|본문 JSON이 기대한 입력 객체로 바인딩되지 않는다|JSON 필드 값이 Service에 전달되는 DTO와 같은지 확인한다
RequestParam 쿼리 값|@RequestParam(required = false)|URL 쿼리 파라미터를 메서드 인자로 받는 선언|keyword가 있을 수도 없을 수도 있는 목록 요청 상황|선택 파라미터에 required=false를 지정한다|선택 검색값을 생략한 요청도 필수 입력 오류가 날 수 있다|파라미터 있는 요청과 없는 요청을 각각 보낸다
PathVariable 식별자|@PathVariable Long employeeId|URL 경로 일부를 인자로 바인딩하는 선언|/employees/7 경로의 7을 조회 ID로 쓰는 상황|경로 변수 이름과 PathVariable 인자를 맞춘다|요청 경로의 ID가 올바른 인자로 전달되지 않는다|다른 경로 ID를 보내 대응 직원이 조회되는지 확인한다
Valid 요청 검증 연결|@Valid @RequestBody|바인딩된 요청 객체의 Bean Validation을 실행하게 하는 선언|DTO 필드 제약을 웹 요청에서 적용하는 상황|검증 대상 본문 DTO 인자에 Valid를 붙인다|필드 제약이 있어도 웹 경계에서 검증되지 않는다|잘못된 요청이 Service 동작 전에 검증 오류로 처리되는지 확인한다

## 21|REST 응답과 HTTP 메서드|backend|HTTP·Spring MVC|practice/starter/26-notice-controller/NoticeController.java
RestController 응답 본문|@RestController|반환 객체를 응답 본문으로 처리하는 Controller 선언|공지 DTO를 JSON 응답으로 전송하는 상황|JSON API Controller에 RestController를 사용한다|객체 응답과 화면 뷰 이름 반환을 혼동할 수 있다|응답 Content-Type과 JSON 본문을 확인한다
PostMapping 생성 요청|@PostMapping|POST 요청을 지정 메서드에 연결하는 매핑|새 공지 생성 요청을 받는 상황|생성 처리 메서드를 PostMapping으로 연결한다|생성 URL의 POST가 의도한 메서드를 찾지 못한다|정상 POST가 생성 처리에 도달하는지 확인한다
GetMapping 조회 요청|@GetMapping|GET 요청을 지정 메서드에 연결하는 매핑|공지 목록 데이터를 요청하는 상황|조회 진입점에 GetMapping을 지정한다|목록 요청을 잘못된 HTTP 메서드에 연결할 수 있다|GET 요청의 응답 데이터와 매핑 메서드를 확인한다
PutMapping 수정 요청|@PutMapping("/{noticeId}")|PUT 요청을 지정 자원의 수정 처리로 연결하는 매핑|명세의 공지 수정 URL을 구현하는 상황|PutMapping 경로에 noticeId를 포함한다|대상 ID와 수정 요청의 연결이 어긋난다|동일 ID에 수정 요청 후 변경된 내용이 조회되는지 확인한다
ResponseEntity의 204|ResponseEntity.noContent().build()|본문 없이 성공 상태를 반환하는 응답 구성|삭제 성공 뒤 보낼 응답 본문이 없는 상황|noContent로 204 응답을 구성한다|본문 없는 성공과 JSON 응답 형식을 혼동한다|응답 상태가 204이고 본문이 비어 있는지 확인한다

## 22|Bean Validation 제약|backend|Jakarta Validation|practice/starter/17-dto-validation/EmployeeCreateRequest.java
NotBlank 문자열 제약|@NotBlank|문자열의 null·빈 값·공백뿐인 값을 거부하는 제약|직원 이름이 의미 있는 문자열이어야 하는 상황|필수 문자열에 NotBlank를 선언한다|공백뿐인 이름이 정상 입력으로 취급된다|null·빈 문자열·공백뿐인 이름의 실패를 확인한다
NotNull 참조 제약|@NotNull|참조 값이 null인 경우를 거부하는 제약|필수 부서 ID가 요청에 포함되어야 하는 상황|부서 ID에 NotNull을 적용한다|입력되지 않은 ID가 다음 처리까지 전달된다|ID 누락은 실패하되 실제 부서 존재는 별도 조회로 확인한다
Size 길이 제약|@Size(max = 50)|문자열이나 컬렉션의 크기 범위를 제한하는 제약|이름 길이를 정해진 최대값 안으로 제한하는 상황|문자열 필드에 최대 길이 Size를 적용한다|과도하게 긴 입력이 저장 단계까지 전달된다|허용 최대 길이와 그보다 한 글자 긴 값을 비교한다
Pattern 정규식 제약|@Pattern|문자열이 지정한 정규 표현식과 맞는지 검사하는 제약|EMP-001 형식의 사번을 검사하는 상황|Pattern의 regexp에 요구한 형식을 선언한다|길이가 같아도 형식이 다른 사번이 통과한다|접두사·숫자 자리 수가 다른 입력의 검증 결과를 비교한다
PastOrPresent 날짜 제약|@PastOrPresent|날짜가 현재 또는 과거인지 검사하는 제약|입사일의 미래 날짜를 제한하는 상황|해당 날짜 필드에 PastOrPresent를 적용한다|미래 날짜가 입력 검증을 통과한다|어제·오늘·내일 날짜로 경계 결과를 확인한다

## 23|Java 예외와 오류 데이터|backend|Java 예외·enum|practice/starter/15-error-model/ErrorCodeAndResponse.java
RuntimeException 계열|RuntimeException|호출부에 catch나 throws 선언을 강제하지 않는 예외 계열|업무 실패를 상위 처리 경계에 전파하는 상황|BusinessException을 의도한 런타임 예외 계열로 정의한다|checked 예외와 런타임 예외의 전파·롤백 정책을 혼동한다|예외 타입과 실제 전파 경로를 테스트로 확인한다
Java 생성자 위임|this(errorCode, errorCode.getDefaultMessage())|같은 클래스의 다른 생성자를 호출하는 문법|오류 코드만 받는 생성자에서 기본 메시지를 재사용하는 상황|생성자의 첫 문장에서 this로 다른 생성자에 위임한다|초기화 코드가 중복되어 기본 메시지 규칙이 달라질 수 있다|코드만 넣은 생성 결과가 기본 메시지를 사용하는지 확인한다
Java super 생성자 호출|super(message)|상위 클래스 생성자에 인자를 전달하는 표현|예외 객체의 상위 메시지 초기화를 수행하는 상황|생성자에서 메시지를 상위 예외 생성자에 전달한다|상위 예외가 보관하는 메시지가 의도와 달라질 수 있다|예외 메시지가 전달한 문자열과 일치하는지 확인한다
enum의 name 메서드|ErrorCode.INVALID_INPUT.name()|enum 상수의 선언 이름을 문자열로 얻는 메서드|기계가 판별할 안정된 오류 식별자를 응답에 넣는 상황|오류 enum의 name 결과를 code 필드에 사용한다|사용자 설명 문구를 코드로 써 문구 변경에 분기가 깨진다|메시지가 달라도 code가 기대한 상수 이름인지 확인한다
LocalDateTime 현재 시각|LocalDateTime.now()|현재 날짜와 시각을 읽어 값으로 만드는 호출|오류 응답의 생성 시각을 기록하는 상황|응답 생성 시 timestamp에 읽은 시각을 넣는다|응답 시점 정보가 없어 오류 발생 순서를 파악하기 어렵다|응답 timestamp가 생성 시점 범위에 속하는지 확인한다

## 24|Spring MVC 예외 변환|backend|Spring MVC 예외 처리|practice/starter/16-global-handler/GlobalExceptionHandler.java
RestControllerAdvice|@RestControllerAdvice|MVC 예외 처리 결과를 응답 본문으로 제공하는 공통 구성|Controller마다 오류 변환을 반복하는 상황|공통 처리 클래스를 RestControllerAdvice로 구성한다|같은 오류가 Controller마다 다른 모양으로 반환된다|여러 Controller의 동일 예외 응답 구조를 비교한다
ExceptionHandler 타입 연결|@ExceptionHandler(Exception.class)|처리할 예외 타입과 메서드를 연결하는 선언|특정 업무 예외와 일반 예외를 다르게 처리하는 상황|메서드마다 대응하는 예외 타입을 명시한다|파일에 나열한 순서가 처리 순서라고 오해한다|실제 예외 타입별로 의도한 처리기가 선택되는지 확인한다
BindingResult 필드 오류|getBindingResult().getFieldErrors()|입력 검증 실패에서 필드별 오류 정보를 얻는 기능|여러 입력 필드가 동시에 잘못된 상황|BindingResult의 FieldError 목록을 응답으로 변환한다|어떤 필드를 고쳐야 하는지 응답에서 알 수 없다|실패한 필드 이름과 검증 메시지가 응답에 포함되는지 확인한다
Stream map 변환|.map(fe ->|각 원소를 다른 값으로 변환하는 스트림 중간 연산|FieldError 목록을 응답용 FieldErrorItem으로 바꾸는 상황|map에서 원소 변환 함수를 적용하고 결과를 수집한다|내부 검증 객체가 응답 모델로 변환되지 않는다|변환한 각 원소의 필드명과 메시지를 비교한다
오류 로그와 응답 분리|log.error("Unexpected exception", e)|상세 진단을 서버에 기록하고 외부 응답은 제한하는 처리|예상 밖 예외의 원인을 남기는 상황|로그에 예외를 기록하고 응답에는 일반 오류 모델을 사용한다|스택·내부 경로가 사용자 응답에 노출될 수 있다|서버 로그에는 원인이 있고 응답에는 스택이 없는지 확인한다

## 25|JPA Entity와 컬럼|backend|JPA 매핑|practice/starter/01-user-entity/User.entity.java
JPA Entity 대상|JPA Entity|Java 클래스를 JPA 영속성 관리 대상으로 선언하는 개념|계정 객체와 DB 테이블을 연결하는 상황|학습 빈칸을 완성하며 Entity 어노테이션을 지정한다|일반 객체가 자동으로 영속 대상이라고 착각한다|완성한 매핑에서 계정 저장과 재조회가 되는지 확인한다
JPA Table 이름|@Table(name =|엔티티가 대응할 테이블 이름을 지정하는 선언|클래스 이름과 DB 테이블명을 명시적으로 연결하는 상황|Table의 name을 스키마 테이블명에 맞춘다|기본 추론 이름과 실제 테이블 이름이 달라질 수 있다|생성되거나 사용되는 SQL의 테이블명을 확인한다
JPA 기본키 식별|PK 와 자동 증가|행을 식별하는 속성과 ID 생성 전략을 정의하는 매핑|새 User를 저장하고 고유 ID를 얻는 상황|Id와 DB에 맞는 GeneratedValue 전략을 설정한다|엔티티 식별이나 ID 생성 방식이 스키마와 맞지 않는다|서로 다른 두 저장 결과의 ID가 구분되는지 확인한다
Column null 허용 여부|@Column(nullable = false|컬럼의 null 허용 여부를 매핑에 표현하는 속성|이메일 같은 필수 저장 값을 정의하는 상황|필수 컬럼에 nullable=false와 실제 DB 제약을 맞춘다|null 값이 저장될 수 있는 구조로 남는다|실제 DB에서 null 저장이 제약에 의해 거절되는지 확인한다
EnumType.STRING 저장|@Enumerated(EnumType.|enum 이름을 문자열로 저장하는 JPA 방식|역할 enum의 선언 순서 변경 영향을 줄이는 상황|Enumerated를 STRING 방식으로 완성한다|순번 저장에서 상수 순서 변경이 값의 의미를 바꿀 수 있다|DB 역할 값이 숫자 순번이 아닌 이름인지 확인한다

## 26|JPA 연관관계와 로딩|backend|JPA 연관관계|practice/starter/03-employee-entity/Employee.entity.java
JPA 일대일 관계|User 와 1:1|한 객체가 상대 객체 하나와 연결되는 관계 매핑|직원과 로그인 계정을 연결하는 상황|OneToOne과 외래키 유일성 제약을 맞춘다|같은 계정이 여러 직원 정보와 연결될 수 있다|같은 user_id의 중복 직원 저장이 제약으로 막히는지 확인한다
JPA 다대일 관계|여러 직원이 한 부서에 속한다|여러 엔티티가 하나의 관련 엔티티를 참조하는 관계|여러 직원이 동일 부서를 공유하는 상황|직원에서 부서로 ManyToOne을 매핑한다|다대일 관계를 잘못 모델링해 부서를 불필요하게 중복한다|두 직원이 같은 부서 ID를 참조할 수 있는지 확인한다
JoinColumn 외래키|@JoinColumn(name = "department_id"|연관관계에 사용하는 외래키 컬럼을 지정하는 선언|직원 부서 참조와 department_id를 연결하는 상황|JoinColumn 이름을 실제 외래키 컬럼과 맞춘다|연관관계가 잘못된 컬럼을 읽거나 저장한다|직원 저장 SQL의 부서 외래키 값을 확인한다
LAZY 지연 로딩|LAZY는 관련 객체의 조회를 필요할 때까지 미루는 설정|연관 객체 조회를 접근 시점까지 지연하는 로딩 전략|직원만 필요하고 부서 상세는 아직 필요 없는 상황|연관 매핑의 fetch 전략을 LAZY로 검토한다|필요하지 않은 연관 조회를 일찍 수행할 수 있다|연관 접근 전후 SQL을 비교해 실제 로딩 시점을 확인한다
JPA N+1 조회 현상|LAZY만으로 N+1 문제가 해결되지는 않습니다|목록 조회 뒤 연관 접근마다 추가 조회가 생기는 현상|직원마다 부서나 계정 정보를 읽는 목록 상황|SQL 횟수를 관찰하고 필요한 연관 조회 전략을 설계한다|LAZY만 지정하면 추가 조회 문제가 사라진다고 오해한다|목록 크기를 늘려 추가 SELECT 수가 함께 늘어나는지 확인한다

## 27|Spring Data Repository|backend|Spring Data JPA|practice/starter/07-repository/EmployeeRepository.java
JpaRepository 제네릭|JpaRepository<Employee, Long>|대상 엔티티와 ID 타입을 지정하는 Repository 인터페이스|Employee 기본 저장·조회를 재사용하는 상황|JpaRepository의 타입 인자를 엔티티와 식별자에 맞춘다|다른 엔티티나 ID 타입으로 메서드를 잘못 사용한다|ID 인자와 조회 결과의 컴파일 타입을 확인한다
existsBy 반환 의미|existsByX|조건에 맞는 데이터 존재 여부를 반환하는 파생 쿼리|중복 여부만 판단하면 되는 상황|existsBy 계열을 사용해 boolean 결과를 받는다|불필요하게 전체 엔티티를 읽어 존재 여부를 판단한다|값이 있을 때 true이고 없을 때 false인지 확인한다
Optional 조회 결과|Optional<Employee>|없을 수도 있는 단일 조회 결과를 표현하는 타입|사번 조회에서 직원이 없을 가능성을 다루는 상황|Optional의 존재·부재를 명시적으로 처리한다|결과가 항상 있다고 가정해 부재 시 실패한다|존재하는 사번과 없는 사번의 처리 경로를 비교한다
파생 쿼리의 속성 경로|findByDepartment_Id|연관된 객체의 필드 경로로 조건을 만드는 규칙|직원과 연결된 부서 ID로 검색하는 상황|메서드 이름을 Entity의 department.id 경로에 맞춘다|DB 컬럼명과 Java 속성 경로를 혼동해 쿼리 구성이 실패한다|서로 다른 부서 직원 중 지정 ID만 조회되는지 확인한다
Containing 문자열 조건|findByUser_NameContainingOrEmployeeNumberContaining|문자열 일부 포함 조건을 나타내는 파생 쿼리 키워드|이름 일부로 직원 목록을 찾는 상황|문자열 속성 조건에 Containing을 사용한다|완전 일치만 적용되어 부분 검색 결과가 누락된다|이름 가운데 문자열로 검색해 해당 직원이 포함되는지 확인한다

## 28|JPQL과 갱신 쿼리|backend|JPQL·DB|practice/starter/31-repositories-all/AllRepositories.java
JPQL 엔티티 이름|FROM LeaveRequest l|DB 테이블 대신 엔티티와 속성으로 질의하는 JPA 언어|LeaveRequest의 직원과 날짜 조건을 조회하는 상황|JPQL에서 엔티티명과 Java 속성명을 사용한다|테이블·컬럼 이름을 그대로 사용해 해석이 실패한다|완성한 쿼리의 초기화와 실행 결과를 확인한다
Query 명시 선언|@Query(|Repository 메서드의 질의를 직접 지정하는 선언|메서드 이름만으로 읽기 어려운 조건을 작성하는 상황|Query에 필요한 JPQL 조건을 명시한다|메서드 이름과 별도 질의의 책임을 혼동한다|메서드를 호출해 선언한 조건대로 조회되는지 확인한다
Param 이름 바인딩|@Param("employeeId")|메서드 인자를 쿼리의 이름 있는 파라미터에 연결하는 선언|JPQL의 :employeeId에 호출 인자를 넣는 상황|Param 이름을 쿼리 파라미터와 일치시킨다|이름 불일치로 값 바인딩이 실패한다|서로 다른 인자를 넣어 그 값에 맞는 결과가 나오는지 확인한다
Modifying 변경 선언|@Modifying|선언한 쿼리가 조회가 아닌 변경 작업임을 알리는 설정|UPDATE JPQL을 Repository에서 실행하는 상황|변경 Query에 Modifying을 붙이고 쓰기 트랜잭션으로 실행한다|조회 실행 방식으로 처리되어 변경 쿼리가 실패할 수 있다|수행 건수와 새로 조회한 DB 값을 함께 확인한다
조회수 원자적 UPDATE|n.viewCount = n.viewCount + 1|DB 현재값을 기준으로 한 문장에서 증가시키는 연산|동시 요청이 같은 조회수를 각각 읽고 쓰는 상황|DB UPDATE에서 현재 조회수에 1을 더한다|읽기 후 대입 방식에서 다른 요청의 증가분이 덮일 수 있다|동시 증가 후 기대한 총 증가량이 반영되는지 확인한다

## 29|페이지와 함수형 변환|backend|Pageable·Page|practice/starter/09-employee-search/EmployeeService.search.java
Pageable 조회 조건|Pageable pageable|페이지 번호·크기·정렬을 전달하는 요청 조건 객체|한 화면에 필요한 직원 분량만 조회하는 상황|Repository에 Pageable을 전달한다|전체 데이터를 한 번에 조회해 응답이 커질 수 있다|페이지와 크기를 달리하여 반환 범위를 비교한다
Page 결과 구조|Page<Employee> page|조회 항목과 페이지 메타정보를 함께 담는 결과 타입|목록과 전체 건수를 화면에 전달하는 상황|페이지 결과를 Page로 다루며 메타정보를 보존한다|내용 목록만 전달해 전체 건수 정보를 잃는다|content와 totalElements 등 페이지 정보를 함께 확인한다
Page.map|page.map(...)|페이지 메타정보를 유지하며 각 원소를 변환하는 기능|Entity 페이지를 DTO 페이지로 바꾸는 상황|Page.map에 엔티티→DTO 변환 함수를 넘긴다|원소만 별도 목록으로 옮겨 페이지 정보가 사라진다|변환 전후 전체 건수와 페이지 번호가 같은지 확인한다
Java 메서드 참조|EmployeeResponse::from|메서드를 함수 인자로 전달하는 Java 문법|페이지 원소마다 같은 DTO 변환을 적용하는 상황|변환 콜백에 EmployeeResponse::from을 전달한다|반환 객체와 변환 함수 자체를 혼동한다|각 Employee에 from이 적용된 응답 원소를 확인한다
동적 조회 조건 결합|@Query 나 Specification|있는 조건만 조합하여 조회를 구성하는 접근|키워드와 부서 조건을 동시에 적용하는 상황|복합 조건을 하나의 올바른 질의로 결합한다|예제의 단순 분기처럼 전달받은 조건 하나를 무시할 수 있다|두 조건 동시 입력에서 양쪽을 만족한 데이터만 반환되는지 확인한다
`);
