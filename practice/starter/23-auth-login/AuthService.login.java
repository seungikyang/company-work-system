// 실제 구현 위치 예: src/main/java/com/example/companywork/service/AuthService.java
// 목표: 로그인 / 로그아웃 / 내 정보 조회 / 비밀번호 변경. PRD 2.5.1, TRD 3.11 참고.

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== 로그인 =====
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        String email = request.getEmail().trim().toLowerCase();

        // TODO 01: 이메일 사용자가 존재하는지 확인. 존재 여부와 비번 불일치를
        //          같은 메시지로 응답하는 이유는?
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.____, "이메일 또는 비밀번호가 올바르지 않습니다."));

        // TODO 02: 비밀번호 검증. 평문이 아닌 해시 비교를 위한 메서드는?
        if (!passwordEncoder.____(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTHENTICATION_REQUIRED, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 세션 생성과 ID 재발급은 웹 기술을 아는 Controller 에서 처리합니다.
        // Service 는 자격 증명 검증과 응답 생성에만 집중합니다.
        return LoginResponse.from(user);
    }

    // ===== 내 정보 조회 =====
    @Transactional(readOnly = true)
    public MyInfoResponse me(Long currentUserId) {
        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.____));

        // 직원 정보가 함께 필요하면 Employee 도 조회
        Employee employee = employeeRepository.findByUser_Id(currentUserId).orElse(null);

        return MyInfoResponse.from(user, employee);
    }

    // ===== 비밀번호 변경 =====
    @Transactional
    public void changePassword(Long currentUserId, PasswordChangeRequest request) {

        User user = userRepository.findById(currentUserId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // TODO 03: 현재 비밀번호 확인. 본인이 맞는지 한 번 더 검증해야 합니다.
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.____, "현재 비밀번호가 일치하지 않습니다.");
        }

        // TODO 04: 새 비밀번호를 해시해 반영하세요.
        user.changePassword(passwordEncoder.____(request.getNewPassword()));
    }
}

// 학습 질문 (한 줄 답을 적어 보세요):
// Q1. 로그인 실패 시 “존재하지 않는 이메일” / “비밀번호 틀림” 을 구분해서 응답하면 안 되는 이유는?
//     A:
// Q2. 세션 ID 를 로그인 직후에 재발급하는 이유 (Session Fixation) 는?
//     A:
// Q3. 비밀번호 변경 후 사용자의 기존 세션/토큰을 어떻게 처리하는 것이 안전한가?
//     A:
// Q4. login() 에 @Transactional(readOnly=true) 를 단 이유와 changePassword() 에는 빼고 그냥 @Transactional 을 단 이유는?
//     A:
// Q5. AuthService 가 HttpSession/HttpServletRequest 를 직접 다루지 않게 한 이유는?
//     A:
//
// 심화 노트 (면접 답변 포인트):
// - 사용자 열거(enumeration) 방지: "없는 이메일" 과 "비번 틀림" 을 같은 메시지 + 같은 status(401)로 응답.
//   여유가 있으면 응답 시간도 비슷하게(timing attack 방지) — 존재하지 않아도 dummy 해시 비교.
// - matches(raw, encoded): BCrypt 는 encoded 안에 박힌 salt 를 꺼내 raw 를 같은 salt 로 해시한 뒤 비교한다.
//   그래서 평문 == 비교가 불가능하고, DB 가 털려도 원문 복원이 어렵다.
// - Session Fixation 방어와 세션 저장은 AuthController 책임. Service 에 Servlet API 를 넣지 않아 단위 테스트를 단순하게 유지한다.
// - login()/me() 는 조회라 readOnly=true, changePassword() 는 쓰기라 일반 @Transactional.
