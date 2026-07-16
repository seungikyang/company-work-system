// 실제 구현 위치 예: service/AuthService.java
// 목표: 로그인/로그아웃/내 정보/비밀번호 변경을 거의 백지에서. PRD 2.5.1, TRD 3.11.
// 막히면 starter/23-auth-login, answers.md 23장 참고.

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = ____)
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().____().____();

        // TODO 01: 사용자 없음/비번 불일치를 같은 메시지로.
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.____, "이메일 또는 비밀번호가 올바르지 않습니다."));

        // TODO 02: 해시 비교.
        if (!passwordEncoder.____(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 세션 생성/회전/저장은 Controller 책임.
        return LoginResponse.from(user);
    }

    @Transactional(readOnly = true)
    public MyInfoResponse me(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));
        Employee employee = employeeRepository.findByUser_Id(currentUserId).orElse(null);
        return MyInfoResponse.from(user, employee);
    }

    @Transactional
    public void changePassword(Long currentUserId, PasswordChangeRequest request) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        // TODO 03: 현재 비번 확인.
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.____, "현재 비밀번호가 일치하지 않습니다.");
        }
        // TODO 04: 새 비번 해시 후 반영.
        user.changePassword(passwordEncoder.____(request.getNewPassword()));
    }
}

// 학습 질문 (직접 답):
// Q1. 로그인 실패 시 "이메일 없음/비번 틀림" 을 구분하면 안 되는 이유는?
//     A:
// Q2. 로그인 직후 세션 ID 재발급(Session Fixation)의 이유는?
//     A:
// Q3. 비밀번호 변경 후 기존 세션/토큰은 어떻게 처리?
//     A:
// Q4. Service 에서 Servlet API 를 제거하면 테스트와 계층 책임에 어떤 장점이 있나?
//     A:

// 자가 채점:
// □ login readOnly=true  □ AUTHENTICATION_REQUIRED  □ passwordEncoder.matches
// □ Service 에 HttpSession 없음  □ USER_NOT_FOUND  □ passwordEncoder.encode
