// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/AuthController.java
// 목표: 인증 관련 API 엔드포인트를 채우세요. TRD 3.7.1 참고.

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO 01: 로그인. 자격 증명 검증 후 새 세션 ID 로 인증 상태를 저장하세요.
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {

        LoginResponse response = authService.login(loginRequest);

        HttpSession session = request.getSession(true);
        // TODO 02: 로그인 전 세션 ID 를 그대로 쓰지 않도록 재발급하세요.
        request.____();
        session.setAttribute("USER_ID", response.userId());
        session.setAttribute("USER_ROLE", response.role());

        return response;
    }

    // TODO 03: 로그아웃. 기존 세션이 있을 때만 안전하게 폐기하세요.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.____();
        }
        return ResponseEntity.____().build();
    }
}


// ===== /api/users/me 는 보통 별도 UserController 로 분리 =====
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController {

    private final AuthService authService;

    // TODO 04: 내 정보 조회. 현재 로그인한 사용자 ID 는 어떻게 꺼낼까요?
    //          (학습용 세션: @SessionAttribute("USER_ID"), Security: @AuthenticationPrincipal …)
    @GetMapping("/me")
    public MyInfoResponse me(@SessionAttribute(name = "USER_ID", required = true) Long currentUserId) {
        return authService.me(currentUserId);
    }

    // TODO 05: 비밀번호 변경. 응답 body 가 없으면 어떤 상태 코드?
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @SessionAttribute("USER_ID") Long currentUserId,
            @Valid @RequestBody PasswordChangeRequest request) {
        authService.changePassword(currentUserId, request);
        return ResponseEntity.____Content().build();
    }
}


// ===== DTO =====
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) { }

public record LoginResponse(Long userId, String email, String name, UserRole role) {
    public static LoginResponse from(User u) {
        return new LoginResponse(u.getId(), u.getEmail(), u.getName(), u.getRole());
    }
}

public record PasswordChangeRequest(
        @NotBlank String currentPassword,
        // TODO 06: 새 비밀번호 길이 제약을 채우세요.
        @NotBlank @Size(min = ____, max = 64) String newPassword
) { }

public record MyInfoResponse(
        Long userId, String email, String name, UserRole role,
        Long employeeId, String employeeNumber, String departmentName
) {
    public static MyInfoResponse from(User user, Employee employee) {
        if (employee == null) {
            return new MyInfoResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(),
                    null, null, null);
        }
        return new MyInfoResponse(
                user.getId(), user.getEmail(), user.getName(), user.getRole(),
                employee.getId(), employee.getEmployeeNumber(),
                employee.getDepartment().getName());
    }
}

// 학습 질문:
// Q1. /api/auth/login 은 왜 permitAll() 이어야 하는가? (Spring Security 단계 기준)
//     A:
// Q2. /api/users/me 의 비밀번호 변경을 PATCH 로 둔 이유는?
//     A:
// Q3. LoginResponse 에 사용자 ID 를 노출해도 괜찮은가? (idor 공격 관점)
//     A:
// Q4. 세션 생성/폐기를 Service 가 아니라 Controller 에 둔 이유는?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - /api/auth/login 은 permitAll: 로그인 자체가 "인증을 획득하는" 과정이라 인증을 요구하면 순환에 빠진다.
//   단, 입력 검증 + (가능하면) 로그인 시도 율제한(rate limit)은 필요.
// - 세션은 HTTP 요청 생명주기에 속하므로 Controller 에서 생성/회전/폐기하고, Service 는 자격 증명 검증만 담당한다.
// - 내 정보/비번 변경은 URL 에 userId 를 받지 않고 세션의 USER_ID 를 쓴다 → 남의 id 로 조회하는 IDOR 차단.
// - LoginResponse 에 userId 노출 자체는 취약점이 아니다. 핵심은 "서버가 요청마다 세션/토큰의 id 를 신뢰하고
//   URL 의 id 는 신뢰하지 않는다" 는 원칙.
// - 응답 본문이 없으면 204 No Content(로그아웃/비번변경).
