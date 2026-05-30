// 실제 구현 위치 예: controller/AuthController.java
// 목표: 인증 API 엔드포인트를 거의 백지에서. TRD 3.7.1.
// 막히면 starter/23-auth-login, answers.md 23장 참고.

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO 01: 세션 기반 로그인 — HttpSession 주입.
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, ____ session) {
        return authService.login(request, session);
    }

    // TODO 02: 로그아웃 — body 없으면 status?
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.____().build();
    }
}

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
class UserController {

    private final AuthService authService;

    // TODO 03: 내 정보 — 현재 로그인 사용자 ID 주입(세션).
    @GetMapping("/me")
    public MyInfoResponse me(@____(name = "USER_ID", required = true) Long currentUserId) {
        return authService.me(currentUserId);
    }

    // TODO 04: 비밀번호 변경 — body 없으면 status?
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(
            @SessionAttribute("USER_ID") Long currentUserId,
            @Valid @RequestBody PasswordChangeRequest request) {
        authService.changePassword(currentUserId, request);
        return ResponseEntity.____Content().build();
    }
}

// ===== DTO =====
public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) { }

public record LoginResponse(Long userId, String email, String name, UserRole role) {
    public static LoginResponse from(User u) {
        return new LoginResponse(u.getId(), u.getEmail(), u.getName(), u.getRole());
    }
}

public record PasswordChangeRequest(
        @NotBlank String currentPassword,
        // TODO 05: 새 비밀번호 길이.
        @NotBlank @Size(min = ____, max = 64) String newPassword
) { }

public record MyInfoResponse(
        Long userId, String email, String name, UserRole role,
        Long employeeId, String employeeNumber, String departmentName
) {
    public static MyInfoResponse from(User user, Employee employee) {
        if (employee == null) {
            return new MyInfoResponse(user.getId(), user.getEmail(), user.getName(), user.getRole(), null, null, null);
        }
        return new MyInfoResponse(
                user.getId(), user.getEmail(), user.getName(), user.getRole(),
                employee.getId(), employee.getEmployeeNumber(), employee.getDepartment().getName());
    }
}

// 학습 질문 (직접 답):
// Q1. /api/auth/login 은 왜 permitAll() 이어야 하나?
//     A:
// Q2. 비밀번호 변경을 PATCH 로 둔 이유는?
//     A:
// Q3. LoginResponse 에 userId 를 노출해도 괜찮은가? (IDOR 관점)
//     A:

// 자가 채점:
// □ HttpSession 주입  □ noContent()  □ @SessionAttribute  □ noContent()  □ @Size(min=8)
