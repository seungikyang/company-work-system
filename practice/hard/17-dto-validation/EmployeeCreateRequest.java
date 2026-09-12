// 실제 구현 위치 예: dto/EmployeeCreateRequest.java
// 목표: Bean Validation 어노테이션을 거의 백지에서. TRD 3.7.2, PRD 2.6.
// 주의: Spring Boot 3.x → jakarta.validation.constraints.*
// 막히면 starter/17-dto-validation, answers.md 17장 참고.

// 초보자 안내 (17장): DTO와 Bean Validation
// 개념: DTO는 요청·응답 데이터를 운반하는 객체이며 Bean Validation은 필드 제약을 검사하는 체계입니다.
// 사용 이유: 이메일 형식·필수값·길이 같은 입력 검사를 선언으로 모아 반복을 줄입니다.
// 동작 흐름: 요청 본문이 DTO로 변환된 후 @Valid로 필드의 제약을 검사합니다. 부서가 실제 존재하는지 같은 DB 관련 업무 검증은 Service에서 이어집니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateRequest {

    // TODO 01: null/공백/빈문자열 모두 차단 + 이메일 형식.
    @____(message = "이메일은 필수입니다.")
    @____(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    // TODO 02: 길이 8~64.
    @____(min = ____, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    private String password;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotNull(message = "부서를 선택하세요.")
    private Long departmentId;

    // TODO 03: 사번 패턴(EMP-001).
    @NotBlank
    @____(regexp = "^EMP-\\d{3,}$", message = "사번 형식은 EMP-001 처럼 입력하세요.")
    @Size(max = 30)
    private String employeeNumber;

    @Size(max = 50)
    private String position;

    @Size(max = 30)
    private String phone;

    // TODO 04: 입사일은 미래 금지.
    @____
    private LocalDate hireDate;
}

// 학습 질문 (직접 답):
// Q1. @NotBlank, @NotEmpty, @NotNull 의 차이는?
//     A:
// Q2. DTO 검증이 동작하려면 Controller 파라미터에 무엇을 붙이나?
//     A:
// Q3. 요청 DTO 와 응답 DTO 를 같은 클래스로 두면 무슨 문제가?
//     A:

// 자가 채점:
// □ @NotBlank email  □ @Email  □ @Size(min=8,max=64) password  □ @Pattern 사번  □ @PastOrPresent hireDate
