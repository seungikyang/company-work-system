// 중학생 눈높이: 직원 등록 입력표
// DTO는 필요한 정보만 담아 다른 부분에 전달하는 작은 메모입니다.
// 이 입력표에는 이메일·비밀번호·부서 같은 등록 정보를 담고, 형식이 맞는지 규칙을 붙입니다.
// 받을 항목 읽기 → 빈 값·길이·형식 검사 규칙 보기 순서로 봅니다.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: src/main/java/com/example/companywork/dto/EmployeeCreateRequest.java
// 목표: Bean Validation 어노테이션을 채우세요. TRD 3.7.2, PRD 2.6 비기능 요구사항 검증 참고.
// 주의: Spring Boot 3.x 이므로 import 는 jakarta.validation.constraints.* 입니다.

// 초보자 안내 (17장): DTO와 Bean Validation
// 개념: DTO는 요청·응답 데이터를 운반하는 객체이며 Bean Validation은 필드 제약을 검사하는 체계입니다.
// 사용 이유: 이메일 형식·필수값·길이 같은 입력 검사를 선언으로 모아 반복을 줄입니다.
// 동작 흐름: 요청 본문이 DTO로 변환된 후 @Valid로 필드의 제약을 검사합니다. 부서가 실제 존재하는지 같은 DB 관련 업무 검증은 Service에서 이어집니다.
// 읽는 순서: 아래 입력값 → 조건 검사 → 호출·저장 → 반환값을 따라가며 TODO를 채우세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateRequest {

    // TODO 01: null/공백/빈문자열 모두 차단.
    @____(message = "이메일은 필수입니다.")
    // TODO 02: 이메일 형식 검사.
    @____(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    // TODO 03: 비밀번호 길이는 최소 8자, 최대 64자로 두고 싶다면?
    @____(min = ____, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    // 이 검사는 부서 ID가 입력되었는지만 확인합니다. 999가 실제 부서인지는 Repository 조회가 필요합니다.
    // 어노테이션은 메타데이터이므로 new로 객체를 만드는 것만으로 모든 제약이 자동 실행되지는 않습니다.
    @NotNull(message = "부서를 선택하세요.")
    private Long departmentId;

    // TODO 04: 사번 패턴 검사 (예: EMP-001 형태).
    @NotBlank
    @____(regexp = "^EMP-\\d{3,}$", message = "사번 형식은 EMP-001 처럼 입력하세요.")
    @Size(max = 30)
    private String employeeNumber;

    @Size(max = 50)
    private String position;

    // TODO 05: 휴대폰 번호는 자유롭게 받되, 길이만 제한.
    @Size(max = 30)
    private String phone;

    // TODO 06: 입사일이 미래여서는 안 됩니다.
    @____
    private LocalDate hireDate;
}

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-17
//
// Q1. NotNull·NotEmpty·NotBlank를 올바르게 비교한 것은? [chapter-v1-17-04]
// ① NotNull은 빈 문자열과 공백을 모두 금지한다.
// ② NotNull은 null 금지, NotEmpty는 null·빈 컨테이너 금지, NotBlank는 null·공백뿐인 문자열 금지다.
// ③ NotEmpty는 공백 문자열도 반드시 금지한다.
// ④ NotBlank는 어떤 숫자 자료형에도 문자열처럼 적용한다.
// 정답: ②
// 해설: 제약마다 적용 자료형과 허용 값이 다르다. null·빈 값·공백 입력을 구분해 테스트한다.
//
// Q2. 요청 DTO 제약을 MVC에서 검사하려는 파라미터의 일반적인 구성은? [chapter-v1-17-05]
// ① @RequestBody만 붙이면 모든 제약이 자동 검사된다.
// ② @Entity만 붙이면 요청 DTO가 검증된다.
// ③ @Valid @RequestBody로 DTO를 받고 검증 오류를 공통 응답으로 처리한다.
// ④ @Transactional만 붙이면 문자열 길이가 검사된다.
// 정답: ③
// 해설: 본문 바인딩과 제약 검증은 다른 단계다. 검증 실행 설정과 실패 응답 계약을 함께 확인한다.
//
// Q3. 요청 DTO와 응답 DTO를 분리하는 이유로 알맞은 것은? [chapter-v1-17-06]
// ① 분리하면 모든 필드가 자동으로 암호화된다.
// ② 응답의 민감 정보를 요청에도 모두 허용하기 위해서다.
// ③ 분리하면 API 변경 검토가 필요 없어진다.
// ④ 입력 허용 필드·검증 규칙과 공개 응답 필드가 달라 과입력·정보 노출·계약 결합을 줄이기 위해서다.
// 정답: ④
// 해설: 입력과 출력의 목적별 계약을 설계한다. 특히 비밀번호·역할·서버 관리 값의 입력과 공개 범위를 명시한다.
//
// 4지선다 학습 문제 (자동 생성 끝)
//
// 심화 노트 (면접 답변 포인트):
// - @NotNull(null 만 차단) / @NotEmpty(null+빈 문자열, 공백 " " 은 통과) / @NotBlank(null+빈+공백 모두 차단).
//   문자열 필수값엔 @NotBlank 가 정답.
// - 이 예제의 body 검증은 @Valid @RequestBody로 연결한다. 메서드 검증은 적용 방식·버전에 따라 예외가 다르다.
//   클래스 @Validated 방식과 Spring MVC 6.1 내장 검증을 구분하고 입문 안내 10절을 참고한다.
// - 요청/응답 DTO 분리: 응답 스펙을 바꿔도 요청 계약이 안 깨지고, password 같은 입력 전용 필드가 응답에 새지 않는다.
// - hireDate 미래 금지는 @PastOrPresent. 사번 포맷은 @Pattern(정규식). 형식 검증은 DTO, 비즈니스 규칙은 Service.
