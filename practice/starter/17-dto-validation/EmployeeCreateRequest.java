// 실제 구현 위치 예: src/main/java/com/example/companywork/dto/EmployeeCreateRequest.java
// 목표: Bean Validation 어노테이션을 채우세요. TRD 3.7.2, PRD 2.6 비기능 요구사항 검증 참고.
// 주의: Spring Boot 3.x 이므로 import 는 jakarta.validation.constraints.* 입니다.

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

// 학습 질문:
// Q1. @NotBlank, @NotEmpty, @NotNull 의 차이는?
//     A:
// Q2. DTO 검증이 동작하려면 Controller 의 파라미터에 어떤 어노테이션을 붙여야 하나?
//     A:
// Q3. 응답 DTO 와 요청 DTO 를 같은 클래스로 두면 무슨 문제가 생기나?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - @NotNull(null 만 차단) / @NotEmpty(null+빈 문자열, 공백 " " 은 통과) / @NotBlank(null+빈+공백 모두 차단).
//   문자열 필수값엔 @NotBlank 가 정답.
// - @Valid 는 Controller 파라미터에 붙어야 동작: body 는 @Valid @RequestBody, PathVariable/RequestParam 검증은
//   클래스에 @Validated + ConstraintViolationException 처리.
// - 요청/응답 DTO 분리: 응답 스펙을 바꿔도 요청 계약이 안 깨지고, password 같은 입력 전용 필드가 응답에 새지 않는다.
// - hireDate 미래 금지는 @PastOrPresent. 사번 포맷은 @Pattern(정규식). 형식 검증은 DTO, 비즈니스 규칙은 Service.
