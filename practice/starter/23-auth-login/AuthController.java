// 실제 구현 위치 예: src/main/java/com/example/companywork/controller/AuthController.java
// 목표: 인증 관련 API 엔드포인트를 채우세요. PRD/TRD 3.7.1 참고.

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO 01: 로그인. 세션 기반 로그인에서는 HttpSession 을 어떻게 주입받나요?
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            ____ session) {
        return authService.login(request, session);
    }

    // TODO 02: 로그아웃. 응답 body 가 없으면 어떤 상태 코드가 자연스럽나요?
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.____().build();
    }
}


// ===== /api/users/me 는 보통 별도 UserController 로 분리 =====
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController {

    private final AuthService authService;

    // TODO 03: 내 정보 조회. 현재 로그인한 사용자 ID 는 어떻게 꺼낼까요?
    //          (학습용 세션: @SessionAttribute("USER_ID"), Security: @AuthenticationPrincipal …)
    @GetMapping("/me")
    public MyInfoResponse me(@SessionAttribute(name = "USER_ID", required = true) Long currentUserId) {
        return authService.me(currentUserId);
    }

    // TODO 04: 비밀번호 변경. 응답 body 가 없으면 어떤 상태 코드?
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
        // TODO 05: 새 비밀번호 길이 제약을 채우세요.
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
