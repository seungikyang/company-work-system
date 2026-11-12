// 중학생 눈높이: 로그인과 세션
// 로그인은 입력한 계정이 본인 것인지 확인하는 과정입니다.
// 세션은 서버가 기억하는 로그인 기록입니다. 비밀번호 해시는 원래 비밀번호 대신 비교에 쓰는 값입니다.
// 입력 받기 → 계정·비밀번호 확인 → 로그인 결과 또는 세션 처리 순서로 봅니다.
// Hard는 힌트가 적은 복습용입니다. 같은 챕터의 Starter 설명을 읽은 뒤 빈칸을 채워 보세요.
// ____는 직접 채울 빈칸입니다. 주석은 사람이 읽는 설명이므로 프로그램이 실행하지 않습니다.
// 실제 구현 위치 예: controller/AuthController.java
// 목표: 인증 API 엔드포인트를 거의 백지에서. TRD 3.7.1.
// 막히면 starter/23-auth-login, answers.md 23장 참고.

// 초보자 안내 (23장): BCrypt와 로그인 세션
// 개념: BCrypt는 비밀번호를 단방향 해시로 저장하는 방법이고 세션은 서버가 로그인 상태를 기억하는 공간입니다.
// 사용 이유: 평문 비밀번호 저장을 피하고 매 요청마다 비밀번호를 다시 보내지 않게 합니다.
// 동작 흐름: 가입은 encode, 로그인은 matches로 비교합니다. 성공 후 Controller가 세션 ID를 바꾸고 USER_ID·USER_ROLE을 저장하며 로그아웃은 세션을 폐기합니다.
// 읽는 순서: 먼저 같은 챕터 Starter의 코드 옆 설명을 읽고, 여기서 검사·호출 순서를 직접 구성하세요.
// 전체 기술 안내: practice/beginner-guide.md (저장소 루트 기준). 이 파일은 학습용 조각입니다.

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // TODO 01: 인증 후 세션 생성 + Session Fixation 방어 + ID/Role 저장.
    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            ____ request) {
        LoginResponse response = authService.login(loginRequest);
        HttpSession session = request.getSession(____);
        request.____();
        session.setAttribute("USER_ID", response.____());
        session.setAttribute("USER_ROLE", response.____());
        // return은 결과를 호출한 곳에 돌려주고 이 메서드를 끝냅니다. 값이 없는 return은 종료만 합니다.
        return response;
    }

    // TODO 02: 로그아웃 — 세션이 있을 때만 폐기, body 없으면 status?
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(____);
        // if는 조건이 맞을 때만 안쪽 작업을 실행합니다. 조건을 확인하는 갈림길입니다.
        if (session != null) {
            session.____();
        }
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

// 4지선다 학습 문제 (자동 생성 시작)
// 선택·채점 화면 (저장소 루트 기준): problems.html#problem-23
//
// Q1. Security 적용 단계에서 로그인 경로에 permitAll을 설정하는 이유는? [chapter-v1-23-07]
// ① 로그인 성공 여부와 관계없이 관리자 역할을 주기 위해서다.
// ② permitAll이면 모든 필터와 CSRF 정책이 자동 해제되기 때문이다.
// ③ 아직 인증되지 않은 사용자도 자격 증명으로 로그인할 수 있어야 하기 때문이다.
// ④ 로그인 요청을 DB에 저장하지 못하게 하기 위해서다.
// 정답: ③
// 해설: 로그인 전 접근을 허용하되 자격 증명 검증과 적용 중인 다른 보안 정책은 유지한다.
//
// Q2. 내 비밀번호 변경을 PATCH 계약으로 나타내는 이유는? [chapter-v1-23-08]
// ① 비밀번호 변경은 읽기 전용 GET 요청이기 때문이다.
// ② PATCH는 비밀번호를 평문 저장하도록 강제하기 때문이다.
// ③ PATCH 요청이면 현재 비밀번호·인증 확인을 생략하기 때문이다.
// ④ 현재 사용자 자원의 일부인 비밀번호를 지정된 검증 절차에 따라 변경하기 위해서다.
// 정답: ④
// 해설: 부분 변경 계약과 본인 확인·새 비밀번호 정책·세션 처리 규칙을 함께 설계한다.
//
// Q3. 로그인 응답의 사용자 ID와 IDOR 방어의 관계로 알맞은 것은? [chapter-v1-23-09]
// ① ID 노출 여부와 별개로 서버가 인증된 사용자와 대상 자료의 접근 권한을 검사해야 한다.
// ② ID를 숨기면 소유권 검사를 모두 생략해도 된다.
// ③ ID가 숫자이면 자동으로 IDOR 공격이 막힌다.
// ④ 로그인 응답에 ID가 있으면 모든 API가 무조건 취약하다.
// 정답: ①
// 해설: 식별자는 권한 증명이 아니다. 요청의 대상 ID를 신뢰하지 않고 인증 주체의 권한·소유권을 확인한다.
//
// Q4. HttpSession을 Service에 직접 넘기지 않는 설계의 이점은? [chapter-v1-23-10]
// ① Service가 로그인 검증을 할 수 없어진다.
// ② Controller가 모든 DB 트랜잭션을 직접 구현해야 한다.
// ③ 세션을 넘기지 않으면 인증 사용자 ID도 전달할 수 없다.
// ④ HTTP 세션 관리를 웹 계층에 두고 업무 Service의 Servlet 결합과 테스트 부담을 줄인다.
// 정답: ④
// 해설: 웹 상태 관리와 업무 처리를 구분한다. Service에는 검증된 행위자·입력·결과 같은 업무 자료를 전달한다.
//
// 4지선다 학습 문제 (자동 생성 끝)

// 자가 채점:
// □ HttpServletRequest  □ getSession(true)  □ changeSessionId()  □ response.userId()/role()
// □ getSession(false)+invalidate  □ noContent()  □ @SessionAttribute  □ @Size(min=8)
